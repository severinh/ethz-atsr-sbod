import java.util.List;

import org.apache.log4j.Logger;

import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.BinopExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;

public class Analysis extends ForwardBranchedFlowAnalysis<IntervalPerVar> {

	private static final Logger LOG = Logger
			.getLogger(Analysis.class.getName());

	public Analysis(UnitGraph graph) {
		super(graph);
		LOG.debug(graph.toString());
	}

	boolean run() {
		doAnalysis();

		IntervalPerVar arraySizeIntervals = getArraySizeIntervals();
		boolean isSafe = true;

		for (Unit unit : graph) {
			Stmt stmt = (Stmt) unit;
			if (stmt instanceof DefinitionStmt) {
				DefinitionStmt defStmt = (DefinitionStmt) stmt;
				Value left = defStmt.getLeftOp();
				Value right = defStmt.getRightOp();

				if (left instanceof JArrayRef) {
					JArrayRef arrayRef = (JArrayRef) left;
					if (!isSafeArrayRef(arrayRef, arraySizeIntervals)) {
						isSafe = false;
					}
				}

				if (right instanceof JArrayRef) {
					JArrayRef arrayRef = (JArrayRef) right;
					if (!isSafeArrayRef(arrayRef, arraySizeIntervals)) {
						isSafe = false;
					}
				}
			}
		}

		return isSafe;
	}

	protected IntervalPerVar getArraySizeIntervals() {
		IntervalPerVar result = new IntervalPerVar();
		for (Unit unit : graph) {
			Stmt stmt = (Stmt) unit;
			if (stmt instanceof DefinitionStmt) {
				DefinitionStmt defStmt = (DefinitionStmt) stmt;
				Value left = defStmt.getLeftOp();
				Value right = defStmt.getRightOp();

				if (left instanceof JimpleLocal
						&& right instanceof JNewArrayExpr) {
					JimpleLocal leftLocal = (JimpleLocal) left;
					String varName = leftLocal.getName();
					JNewArrayExpr newArrayExpr = (JNewArrayExpr) right;
					Value size = newArrayExpr.getSize();
					if (size instanceof IntConstant) {
						IntConstant constantSize = (IntConstant) size;
						result.putIntervalForVar(varName, new Interval(
								constantSize.value, constantSize.value));
					} else {
						unhandled("non-constant size of new array expression");
					}
				}
			}
		}
		return result;
	}

	protected boolean isSafeArrayRef(JArrayRef arrayRef,
			IntervalPerVar arraySizeIntervals) {
		Value arrayBase = arrayRef.getBase();
		Value arrayIndex = arrayRef.getIndex();
		if (arrayBase instanceof JimpleLocal) {
			JimpleLocal localArrayBase = (JimpleLocal) arrayBase;
			String arrayVarName = localArrayBase.getName();
			Interval arraySizeInterval = arraySizeIntervals
					.getIntervalForVar(arrayVarName);
			if (arrayIndex instanceof IntConstant) {
				int arrayIndexConstant = ((IntConstant) arrayIndex).value;
				if (arrayIndexConstant < 0
						|| arrayIndexConstant >= arraySizeInterval.lower) {
					return false;
				}
			}
		} else {
			unhandled("non-local array reference");
		}
		return true;
	}

	static void unhandled(String what) {
		LOG.error("Can't handle " + what);
		System.exit(1);
	}

	@Override
	protected void flowThrough(IntervalPerVar current, Unit op,
			List<IntervalPerVar> fallOut, List<IntervalPerVar> branchOuts) {
		// TODO: This can be optimized.
		LOG.debug(op.getClass().getName() + ": " + op);

		Stmt stmt = (Stmt) op;
		IntervalPerVar fallState = new IntervalPerVar();
		fallState.copyFrom(current);
		IntervalPerVar branchState = new IntervalPerVar();
		branchState.copyFrom(current);

		if (stmt instanceof DefinitionStmt) {
			DefinitionStmt defStmt = (DefinitionStmt) stmt;
			Value left = defStmt.getLeftOp();
			Value right = defStmt.getRightOp();
			LOG.debug("\tLeft: " + left.getClass().getName());
			LOG.debug("\tRight: " + right.getClass().getName());

			// You do not need to handle these cases:
			if ((!(left instanceof StaticFieldRef))
					&& (!(left instanceof JimpleLocal))
					&& (!(left instanceof JArrayRef))
					&& (!(left instanceof JInstanceFieldRef))) {
				unhandled("assignment to non-variables is not handled.");
			} else if ((left instanceof JArrayRef)
					&& (!((((JArrayRef) left).getBase()) instanceof JimpleLocal))) {
				unhandled("assignment to a non-local array variable is not handled.");
			}

			// TODO: Handle other cases. For example:

			if (left instanceof JimpleLocal) {
				String varName = ((JimpleLocal) left).getName();

				if (right instanceof IntConstant) {
					IntConstant intConstant = ((IntConstant) right);
					fallState.putIntervalForVar(varName, new Interval(
							intConstant.value, intConstant.value));
				} else if (right instanceof JimpleLocal) {
					JimpleLocal local = ((JimpleLocal) right);
					fallState.putIntervalForVar(varName,
							current.getIntervalForVar(local.getName()));
				} else if (right instanceof BinopExpr) {
					Value firstValue = ((BinopExpr) right).getOp1();
					Value secondValue = ((BinopExpr) right).getOp2();

					Interval firstInterval = tryGetIntervalForValue(current,
							firstValue);
					Interval secondInterval = tryGetIntervalForValue(current,
							secondValue);

					if (firstInterval != null && secondInterval != null) {
						// Implement transformers.
						if (right instanceof AddExpr) {
							fallState.putIntervalForVar(varName, Interval.plus(
									firstInterval, secondInterval));
						}
					}
				} else if (right instanceof NewArrayExpr) {
					// Do nothing
					// The array size is relevant at the end of the analysis
				} else if (right instanceof StaticFieldRef) {
					// Do nothing
				} else if (right instanceof JArrayRef) {
					// Do nothing
				} else {
					unhandled("unexpected right-hand side of assignment");
				}
			} else if (left instanceof JArrayRef) {
				// Do nothing
				// The array access is relevant at the end of the analysis
			} else {
				unhandled("unexpected left-hand side of assignment");
			}
		}

		// TODO: Maybe avoid copying objects too much. Feel free to optimize.
		for (IntervalPerVar fnext : fallOut) {
			if (fallState != null) {
				fnext.copyFrom(fallState);
			}
		}
		for (IntervalPerVar fnext : branchOuts) {
			if (branchState != null) {
				fnext.copyFrom(branchState);
			}
		}
	}

	Interval tryGetIntervalForValue(IntervalPerVar currentState, Value value) {
		if (value instanceof IntConstant) {
			IntConstant constant = ((IntConstant) value);
			return new Interval(constant.value, constant.value);
		} else if (value instanceof JimpleLocal) {
			JimpleLocal local = ((JimpleLocal) value);
			return currentState.getIntervalForVar(local.getName());
		}
		return null;
	}

	@Override
	protected void copy(IntervalPerVar source, IntervalPerVar dest) {
		dest.copyFrom(source);
	}

	@Override
	protected IntervalPerVar entryInitialFlow() {
		// TODO: How do you model the entry point?
		return new IntervalPerVar();
	}

	@Override
	protected void merge(IntervalPerVar src1, IntervalPerVar src2,
			IntervalPerVar trg) {
		// TODO: Fix this:
		trg.copyFrom(src1);
		LOG.debug(String.format(
				"Merge:\n    %s\n    %s\n    ============\n    %s\n",
				src1.toString(), src2.toString(), trg.toString()));
	}

	@Override
	protected IntervalPerVar newInitialFlow() {
		return new IntervalPerVar();
	}

}
