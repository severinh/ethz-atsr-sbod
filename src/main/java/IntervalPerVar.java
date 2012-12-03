import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.LengthExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JimpleLocal;

public class IntervalPerVar {

	private final HashMap<String, Interval> values;

	public IntervalPerVar() {
		values = new HashMap<String, Interval>();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, Interval> entry : values.entrySet()) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(entry.getKey());
			builder.append("=");
			builder.append(entry.getValue().toString());
		}
		return builder.toString();
	}

	// This does deep copy of values as opposed to shallow copy, but feel free
	// to optimize.
	public void copyFrom(IntervalPerVar other) {
		values.clear();
		values.putAll(other.values);
	}

	public void mergeFrom(IntervalPerVar first, IntervalPerVar second) {
		values.clear();

		Set<String> varNames = new HashSet<String>();
		varNames.addAll(first.values.keySet());
		varNames.addAll(second.values.keySet());

		for (String varName : varNames) {
			Interval firstInterval = first.values.get(varName);
			Interval secondInterval = second.values.get(varName);
			if (firstInterval == null) {
				values.put(varName, secondInterval);
			} else if (secondInterval == null) {
				values.put(varName, firstInterval);
			} else {
				Interval mergedInterval = Interval.meet(secondInterval,
						firstInterval);
				values.put(varName, mergedInterval);
			}
		}
	}

	void putIntervalForVar(String varName, Interval interval) {
		values.put(varName, interval);
	}

	Interval getIntervalForVar(String var) {
		return values.get(var);
	}

	Interval tryGetIntervalForValue(Value value) {
		if (value instanceof IntConstant) {
			IntConstant constant = ((IntConstant) value);
			return Interval.of(constant.value);
		} else if (value instanceof JimpleLocal) {
			JimpleLocal local = ((JimpleLocal) value);
			return getIntervalForVar(local.getName());
		} else if (value instanceof LengthExpr) {
			LengthExpr lengthExpr = (LengthExpr) value;
			Value array = lengthExpr.getOp();
			return tryGetIntervalForValue(array);
		} else {
			throw new IllegalStateException("Cannot handle value of type "
					+ value.getClass().getName());
		}
	}

	protected boolean isSafeArrayRef(JArrayRef arrayRef) {
		Value arrayBase = arrayRef.getBase();
		Value arrayIndex = arrayRef.getIndex();
		if (arrayBase instanceof JimpleLocal) {
			JimpleLocal localArrayBase = (JimpleLocal) arrayBase;
			Interval arraySizeInterval = tryGetIntervalForValue(localArrayBase);
			Interval indexInterval = tryGetIntervalForValue(arrayIndex);
			return 0 <= indexInterval.getLower()
					&& indexInterval.getUpper() < arraySizeInterval.getLower();
		} else {
			throw new IllegalStateException(
					"Cannot handle non-local array reference");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IntervalPerVar other = (IntervalPerVar) obj;
		if (values == null) {
			if (other.values != null) {
				return false;
			}
		} else if (!values.equals(other.values)) {
			return false;
		}
		return true;
	}

}
