import java.util.List;

import org.apache.log4j.Logger;

import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.BinopExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;

// Implement your numerical analysis here.
public class Analysis extends ForwardBranchedFlowAnalysis<IntervalPerVar> {

	private static final Logger LOG = Logger
			.getLogger(Analysis.class.getName());

	public Analysis(UnitGraph g) {
		super(g);
		LOG.debug(g.toString());
	}

	boolean run() {
		doAnalysis();
		// TODO: Always reports the method as potentially unsafe
		return false;
	}

	static void unhandled(String what) {
		LOG.error("Can't handle " + what);
		System.exit(1);
	}

	@Override
	protected void flowThrough(IntervalPerVar current, Unit op,
			List<IntervalPerVar> fallOut, List<IntervalPerVar> branchOuts) {
		// TODO: This can be optimized.
		LOG.debug("Operation: " + op + "   - " + op.getClass().getName()
				+ "\n      state: " + current);

		Stmt s = (Stmt) op;
		IntervalPerVar fallState = new IntervalPerVar();
		fallState.copyFrom(current);
		IntervalPerVar branchState = new IntervalPerVar();
		branchState.copyFrom(current);

		if (s instanceof DefinitionStmt) {
			DefinitionStmt sd = (DefinitionStmt) s;
			Value left = sd.getLeftOp();
			Value right = sd.getRightOp();
			LOG.debug(left.getClass().getName() + " "
					+ right.getClass().getName());

			// You do not need to handle these cases:
			if ((!(left instanceof StaticFieldRef))
					&& (!(left instanceof JimpleLocal))
					&& (!(left instanceof JArrayRef))
					&& (!(left instanceof JInstanceFieldRef)))
				unhandled("1: Assignment to non-variables is not handled.");
			else if ((left instanceof JArrayRef)
					&& (!((((JArrayRef) left).getBase()) instanceof JimpleLocal)))
				unhandled("2: Assignment to a non-local array variable is not handled.");

			// TODO: Handle other cases. For example:

			if (left instanceof JimpleLocal) {
				String varName = ((JimpleLocal) left).getName();

				if (right instanceof IntConstant) {
					IntConstant c = ((IntConstant) right);
					fallState.putIntervalForVar(varName, new Interval(c.value,
							c.value));
				} else if (right instanceof JimpleLocal) {
					JimpleLocal l = ((JimpleLocal) right);
					fallState.putIntervalForVar(varName,
							current.getIntervalForVar(l.getName()));
				} else if (right instanceof BinopExpr) {
					Value r1 = ((BinopExpr) right).getOp1();
					Value r2 = ((BinopExpr) right).getOp2();

					Interval i1 = tryGetIntervalForValue(current, r1);
					Interval i2 = tryGetIntervalForValue(current, r2);

					if (i1 != null && i2 != null) {
						// Implement transformers.
						if (right instanceof AddExpr) {
							fallState.putIntervalForVar(varName,
									Interval.plus(i1, i2));
						}
					}
				}

				// ...
			}
			// ...
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
