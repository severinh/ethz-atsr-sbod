import java.util.List;

import org.apache.log4j.Logger;

import soot.ArrayType;
import soot.IntType;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.BinopExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.DivExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.MulExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
import soot.jimple.UshrExpr;
import soot.jimple.XorExpr;
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

	void run() {
		doAnalysis();
	}

	/**
	 * Returns the first potentially unsafe statement in the method, or
	 * <code>null</code> if there is no unsafe statement.
	 * 
	 * @param context
	 * @return the first unsafe statement or <code>null</code> if there is none
	 */
	Stmt getFirstUnsafeStatement(IntervalPerVar context) {
		for (Unit unit : graph) {
			Stmt stmt = (Stmt) unit;
			if (stmt instanceof DefinitionStmt) {
				DefinitionStmt defStmt = (DefinitionStmt) stmt;
				Value left = defStmt.getLeftOp();
				Value right = defStmt.getRightOp();
				IntervalPerVar intervalPerVar = getFlowBefore(unit);
				IntervalPerVar merged = new IntervalPerVar();
				merged.mergeFrom(intervalPerVar, context);
				if (!merged.isSafe(left) || !merged.isSafe(right)) {
					return stmt;
				}
			}
		}
		return null;
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
					Interval interval = current.tryGetIntervalForValue(right);
					fallState.putIntervalForVar(varName, interval);
				} else if (right instanceof BinopExpr) {
					Value firstValue = ((BinopExpr) right).getOp1();
					Value secondValue = ((BinopExpr) right).getOp2();

					Interval first = current.tryGetIntervalForValue(firstValue);
					Interval second = current
							.tryGetIntervalForValue(secondValue);

					if (first != null && second != null) {
						Interval newInterval = null;
						if (right instanceof AddExpr) {
							newInterval = Interval.plus(first, second);
						} else if (right instanceof SubExpr) {
							newInterval = Interval.sub(first, second);
						} else if (right instanceof MulExpr) {
							newInterval = Interval.mul(first, second);
						} else if (right instanceof ShlExpr) {
							newInterval = Interval.shl(first, second);
						} else if (right instanceof ShrExpr) {
							newInterval = Interval.shr(first, second);
						} else if (right instanceof UshrExpr) {
							newInterval = Interval.ushr(first, second);
						} else if (right instanceof AndExpr) {
							newInterval = Interval.and(first, second);
						} else if (right instanceof OrExpr) {
							newInterval = Interval.or(first, second);
						} else if (right instanceof XorExpr) {
							newInterval = Interval.xor(first, second);
						} else if (right instanceof RemExpr) {
							unhandled("modulo expression");
						} else if (right instanceof DivExpr) {
							unhandled("division expression");
						}
						fallState.putIntervalForVar(varName, newInterval);
					}
				} else if (right instanceof NegExpr) {
					NegExpr negExpr = (NegExpr) right;
					Value value = negExpr.getOp();
					Interval interval = current.tryGetIntervalForValue(value);
					Interval newInterval = Interval.neg(interval);
					fallState.putIntervalForVar(varName, newInterval);
				} else if (right instanceof NewArrayExpr) {
					JNewArrayExpr newArrayExpr = (JNewArrayExpr) right;
					Value size = newArrayExpr.getSize();
					Interval interval = current.tryGetIntervalForValue(size);
					fallState.putIntervalForVar(varName, interval);
				} else if (right instanceof StaticFieldRef) {
					// Do nothing
				} else if (right instanceof JArrayRef) {
					// Do nothing
				} else if (right instanceof NewExpr) {
					// Do nothing
				} else if (right instanceof InvokeExpr) {
					// When calling a method that returns an integer, assume
					// that the return value could be anything
					InvokeExpr invokeExpr = (InvokeExpr) right;
					SootMethod method = invokeExpr.getMethod();
					if (method.getReturnType() instanceof IntType) {
						fallState.putIntervalForVar(varName, Interval.TOP);
					}
				} else if (right instanceof ParameterRef) {
					ParameterRef parameterRef = (ParameterRef) right;
					Type parameterType = parameterRef.getType();
					if (parameterType instanceof ArrayType) {
						// Do nothing
					} else {
						unhandled("right-hand side of assignment");
					}
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
				ConditionExprEnum conditionExprEnum = ConditionExprEnum
						.fromConditionExprClass(conditionExpr.getClass());

				Value left = conditionExpr.getOp1();
				Value right = conditionExpr.getOp2();
				Interval leftInterval = current.tryGetIntervalForValue(left);
				Interval rightInterval = current.tryGetIntervalForValue(right);

				Interval leftBranchInterval = Interval.cond(leftInterval,
						rightInterval, conditionExprEnum);
				Interval leftFallInterval = Interval.cond(leftInterval,
						rightInterval, conditionExprEnum.getNegation());
				Interval rightBranchInterval = Interval.cond(rightInterval,
						leftInterval, conditionExprEnum.getSwapped());
				Interval rightFallInterval = Interval.cond(rightInterval,
						leftInterval, conditionExprEnum.getSwapped()
								.getNegation());

				if (leftBranchInterval.isBottom()
						&& rightBranchInterval.isBottom()) {
					// If no values of the left and right intervals can satisfy
					// the condition, mark the branch state as dead
					branchState.setInDeadCode();
				} else if (leftFallInterval.isBottom()
						&& rightFallInterval.isBottom()) {
					// If no values of the left and right intervals can satisfy
					// the negation of the condition, mark the fall through
					// state as dead
					fallState.setInDeadCode();
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
