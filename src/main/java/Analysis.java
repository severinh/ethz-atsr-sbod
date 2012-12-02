import java.util.List;

import org.apache.log4j.Logger;

import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.BinopExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
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

		boolean isSafe = true;

		for (Unit unit : graph) {
			Stmt stmt = (Stmt) unit;
			if (stmt instanceof DefinitionStmt) {
				DefinitionStmt defStmt = (DefinitionStmt) stmt;
				Value left = defStmt.getLeftOp();
				Value right = defStmt.getRightOp();
				IntervalPerVar intervalPerVar = getFlowBefore(unit);

				if (left instanceof JArrayRef) {
					JArrayRef arrayRef = (JArrayRef) left;
					if (!isSafeArrayRef(arrayRef, intervalPerVar)) {
						isSafe = false;
					}
				}

				if (right instanceof JArrayRef) {
					JArrayRef arrayRef = (JArrayRef) right;
					if (!isSafeArrayRef(arrayRef, intervalPerVar)) {
						isSafe = false;
					}
				}
			}
		}

		return isSafe;
	}

	protected boolean isSafeArrayRef(JArrayRef arrayRef,
			IntervalPerVar intervalPerVar) {
		Value arrayBase = arrayRef.getBase();
		Value arrayIndex = arrayRef.getIndex();
		if (arrayBase instanceof JimpleLocal) {
			JimpleLocal localArrayBase = (JimpleLocal) arrayBase;
			Interval arraySizeInterval = tryGetIntervalForValue(intervalPerVar,
					localArrayBase);
			Interval indexInterval = tryGetIntervalForValue(intervalPerVar,
					arrayIndex);
			if (indexInterval.getLower() < 0
					|| indexInterval.getUpper() >= arraySizeInterval.getLower()) {
				return false;
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

			if (left instanceof JimpleLocal) {
				String varName = ((JimpleLocal) left).getName();

				if (right instanceof IntConstant
						|| right instanceof JimpleLocal
						|| right instanceof LengthExpr) {
					Interval interval = tryGetIntervalForValue(current, right);
					fallState.putIntervalForVar(varName, interval);
				} else if (right instanceof BinopExpr) {
					Value firstValue = ((BinopExpr) right).getOp1();
					Value secondValue = ((BinopExpr) right).getOp2();

					Interval firstInterval = tryGetIntervalForValue(current,
							firstValue);
					Interval secondInterval = tryGetIntervalForValue(current,
							secondValue);

					if (firstInterval != null && secondInterval != null) {
						if (right instanceof AddExpr) {
							fallState.putIntervalForVar(varName, Interval.plus(
									firstInterval, secondInterval));
						} else if (right instanceof SubExpr) {
							fallState
									.putIntervalForVar(varName, Interval.sub(
											firstInterval, secondInterval));
						} else if (right instanceof MulExpr) {
							fallState
									.putIntervalForVar(varName, Interval.mul(
											firstInterval, secondInterval));
						}
					}
				} else if (right instanceof NegExpr) {
					NegExpr negExpr = (NegExpr) right;
					Value value = negExpr.getOp();
					Interval interval = tryGetIntervalForValue(current, value);
					Interval newInterval = Interval.neg(interval);
					fallState.putIntervalForVar(varName, newInterval);
				} else if (right instanceof NewArrayExpr) {
					JNewArrayExpr newArrayExpr = (JNewArrayExpr) right;
					Value size = newArrayExpr.getSize();
					Interval interval = tryGetIntervalForValue(current, size);
					fallState.putIntervalForVar(varName, interval);
				} else if (right instanceof StaticFieldRef) {
					// Do nothing
				} else if (right instanceof JArrayRef) {
					// Do nothing
				} else {
					unhandled("right-hand side of assignment");
				}
			} else if (left instanceof JArrayRef) {
				// Do nothing
				// The array access is relevant at the end of the analysis
			} else {
				unhandled("left-hand side of assignment");
			}
		} else if (op instanceof IfStmt) {
			IfStmt ifStmt = (IfStmt) op;
			Value condition = ifStmt.getCondition();
			LOG.debug("\tCondition: " + condition.getClass().getName());

			if (condition instanceof ConditionExpr) {
				ConditionExpr conditionExpr = (ConditionExpr) condition;
				Value left = conditionExpr.getOp1();
				Value right = conditionExpr.getOp2();
				Interval leftInterval = tryGetIntervalForValue(current, left);
				Interval rightInterval = tryGetIntervalForValue(current, right);

				Interval leftBranchInterval = null;
				Interval leftFallInterval = null;
				Interval rightBranchInterval = null;
				Interval rightFallInterval = null;

				if (condition instanceof LtExpr) {
					leftBranchInterval = Interval.lt(leftInterval,
							rightInterval);
					leftFallInterval = Interval.ge(leftInterval, rightInterval);
					rightBranchInterval = Interval.gt(rightInterval,
							leftInterval);
					rightFallInterval = Interval
							.le(rightInterval, leftInterval);
				} else if (condition instanceof LeExpr) {
					leftBranchInterval = Interval.le(leftInterval,
							rightInterval);
					leftFallInterval = Interval.gt(leftInterval, rightInterval);
					rightBranchInterval = Interval.ge(rightInterval,
							leftInterval);
					rightFallInterval = Interval
							.lt(rightInterval, leftInterval);
				} else {
					unhandled("condition");
				}

				if (left instanceof JimpleLocal) {
					JimpleLocal leftLocal = (JimpleLocal) left;
					String varName = leftLocal.getName();
					branchState.putIntervalForVar(varName, leftBranchInterval);
					fallState.putIntervalForVar(varName, leftFallInterval);
				}

				if (right instanceof JimpleLocal) {
					JimpleLocal rightLocal = (JimpleLocal) right;
					String varName = rightLocal.getName();
					branchState.putIntervalForVar(varName, rightBranchInterval);
					fallState.putIntervalForVar(varName, rightFallInterval);
				}
			} else {
				unhandled("condition");
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
		} else if (value instanceof LengthExpr) {
			LengthExpr lengthExpr = (LengthExpr) value;
			Value array = lengthExpr.getOp();
			return tryGetIntervalForValue(currentState, array);
		} else {
			unhandled("value " + value.getClass().getName());
		}
		return null;
	}

	@Override
	protected void copy(IntervalPerVar source, IntervalPerVar target) {
		target.copyFrom(source);
	}

	@Override
	protected IntervalPerVar entryInitialFlow() {
		// TODO: How do you model the entry point?
		return new IntervalPerVar();
	}

	@Override
	protected void merge(IntervalPerVar firstSource,
			IntervalPerVar secondSource, IntervalPerVar target) {
		target.mergeFrom(firstSource, secondSource);
		LOG.debug("Merge:");
		LOG.debug("\tSource: " + firstSource);
		LOG.debug("\tSource: " + secondSource);
		LOG.debug("\tResult: " + target);
	}

	@Override
	protected IntervalPerVar newInitialFlow() {
		return new IntervalPerVar();
	}

}
