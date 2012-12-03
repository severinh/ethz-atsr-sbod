import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.LeExpr;
import soot.jimple.LtExpr;
import soot.jimple.NeExpr;

public enum ConditionExprEnum {

	LT, LE, EQ, NE, GE, GT;

	static {
		LT.negation = GE;
		LT.swapped = GT;

		LE.negation = GT;
		LE.swapped = GE;

		EQ.negation = NE;
		EQ.swapped = EQ;

		NE.negation = EQ;
		NE.swapped = NE;

		GE.negation = LT;
		GE.swapped = LE;

		GT.negation = LE;
		GT.swapped = LT;
	}

	private ConditionExprEnum negation;
	private ConditionExprEnum swapped;

	public ConditionExprEnum getNegation() {
		return negation;
	}

	public ConditionExprEnum getSwapped() {
		return swapped;
	}

	public static ConditionExprEnum fromConditionExprClass(
			Class<? extends ConditionExpr> conditionExprClass) {
		if (LtExpr.class.isAssignableFrom(conditionExprClass)) {
			return LT;
		} else if (LeExpr.class.isAssignableFrom(conditionExprClass)) {
			return LE;
		} else if (EqExpr.class.isAssignableFrom(conditionExprClass)) {
			return EQ;
		} else if (NeExpr.class.isAssignableFrom(conditionExprClass)) {
			return NE;
		} else if (GeExpr.class.isAssignableFrom(conditionExprClass)) {
			return GE;
		} else if (GtExpr.class.isAssignableFrom(conditionExprClass)) {
			return GT;
		} else {
			throw new IllegalStateException(
					"cannot handle condition expression class "
							+ conditionExprClass);
		}
	}
}
