import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.LengthExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JimpleLocal;

public class IntervalPerVar {

	private final static Logger LOG = Logger.getLogger(IntervalPerVar.class);

	private final HashMap<String, Interval> values;
	private boolean isInDeadCode;

	public IntervalPerVar() {
		values = new HashMap<String, Interval>();
		isInDeadCode = false;
	}

	/**
	 * Returns whether the current state applies to code that will not be
	 * executed given the current information (i.e. it is dead).
	 * 
	 * @return whether the associated code is dead
	 */
	public boolean isInDeadCode() {
		return isInDeadCode;
	}

	/**
	 * Marks the current state as being associated with dead code.
	 */
	public void setInDeadCode() {
		isInDeadCode = true;
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
		if (isInDeadCode()) {
			builder.append(" (dead)");
		}
		return builder.toString();
	}

	/**
	 * Replaces all existing {@link Interval}s with the ones in the
	 * {@link IntervalPerVar} passed as a parameter.
	 * 
	 * @param other
	 *            the new intervals
	 */
	public void copyFrom(IntervalPerVar other) {
		values.clear();
		values.putAll(other.values);
		isInDeadCode = other.isInDeadCode;
	}

	/**
	 * Replaces all existing {@link Interval}s with the ones in the
	 * {@link IntervalPerVar} passed as a parameter.
	 * 
	 * Compared to {@link #copyFrom(IntervalPerVar)}, this method widens all new
	 * intervals if they differ from the existing ones stored in this
	 * {@link IntervalPerVar}.
	 * 
	 * @param newIntervalPerVar
	 *            the new intervals
	 */
	public void copyAndWidenFrom(IntervalPerVar newIntervalPerVar) {
		IntervalPerVar widenedIntervalPerVar = new IntervalPerVar();
		if (newIntervalPerVar.isInDeadCode()) {
			widenedIntervalPerVar.setInDeadCode();
		}
		LOG.debug("Widening " + newIntervalPerVar + "...");
		for (Entry<String, Interval> otherEntry : newIntervalPerVar.values
				.entrySet()) {
			String varName = otherEntry.getKey();
			Interval newInterval = otherEntry.getValue();
			Interval oldInterval = values.get(varName);
			Interval widenedInterval = newInterval;
			if (oldInterval != null && !oldInterval.equals(Interval.BOTTOM)
					&& !oldInterval.equals(newInterval)) {
				int lower = newInterval.getLower();
				int upper = newInterval.getUpper();
				boolean hasWidened = false;
				if (lower < oldInterval.getLower()) {
					lower = Integer.MIN_VALUE;
					hasWidened = true;
				}
				if (upper > oldInterval.getUpper()) {
					upper = Integer.MAX_VALUE;
					hasWidened = true;
				}
				widenedInterval = Interval.of(lower, upper);
				if (hasWidened) {
					LOG.debug(varName + ": " + newInterval + " -> "
							+ widenedInterval);
				}
			}
			widenedIntervalPerVar.putIntervalForVar(varName, widenedInterval);
		}
		copyFrom(widenedIntervalPerVar);
	}

	public void mergeFrom(IntervalPerVar first, IntervalPerVar second) {
		values.clear();
		// If both source states are associated with dead code, so is the result
		if (first.isInDeadCode && second.isInDeadCode) {
			isInDeadCode = true;
		} else {
			isInDeadCode = false;
			// If one of the states is associated with dead code, ignore it
			if (first.isInDeadCode) {
				values.putAll(second.values);
			} else if (second.isInDeadCode) {
				values.putAll(first.values);
			} else {
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
		}
	}

	void putIntervalForVar(String varName, Interval interval) {
		values.put(varName, interval);
	}

	/**
	 * Returns the interval associated with a particular variable, or
	 * {@link Interval#BOTTOM} if there is no record of the variable yet.
	 * 
	 * @param variableName
	 *            the name of the variable
	 * @return the interval
	 */
	Interval getIntervalForVar(String variableName) {
		Interval interval = values.get(variableName);
		if (interval == null) {
			return Interval.BOTTOM;
		} else {
			return interval;
		}
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
			if (arraySizeInterval == null) {
				throw new IllegalStateException(
						"Unknown array size interval for array access");
			}
			Interval indexInterval = tryGetIntervalForValue(arrayIndex);
			if (indexInterval == null) {
				throw new IllegalStateException(
						"Unknown index interval for array access");
			}
			return 0 <= indexInterval.getLower()
					&& indexInterval.getUpper() < arraySizeInterval.getLower();
		} else {
			throw new IllegalStateException(
					"Cannot handle non-local array reference");
		}
	}

	protected boolean isSafe(Value value) {
		if (value instanceof JArrayRef) {
			JArrayRef arrayRef = (JArrayRef) value;
			return isSafeArrayRef(arrayRef);
		}
		return true;
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
