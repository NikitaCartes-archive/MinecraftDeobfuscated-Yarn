package net.minecraft.state.property;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class IntegerProperty extends AbstractProperty<Integer> {
	private final ImmutableSet<Integer> validValues;

	protected IntegerProperty(String string, int i, int j) {
		super(string, Integer.class);
		if (i < 0) {
			throw new IllegalArgumentException("Min value of " + string + " must be 0 or greater");
		} else if (j <= i) {
			throw new IllegalArgumentException("Max value of " + string + " must be greater than min (" + i + ")");
		} else {
			Set<Integer> set = Sets.<Integer>newHashSet();

			for (int k = i; k <= j; k++) {
				set.add(k);
			}

			this.validValues = ImmutableSet.copyOf(set);
		}
	}

	@Override
	public Collection<Integer> getValues() {
		return this.validValues;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof IntegerProperty && super.equals(object)) {
			IntegerProperty integerProperty = (IntegerProperty)object;
			return this.validValues.equals(integerProperty.validValues);
		} else {
			return false;
		}
	}

	@Override
	public int computeHashCode() {
		return 31 * super.computeHashCode() + this.validValues.hashCode();
	}

	public static IntegerProperty create(String string, int i, int j) {
		return new IntegerProperty(string, i, j);
	}

	@Override
	public Optional<Integer> getValue(String string) {
		try {
			Integer integer = Integer.valueOf(string);
			return this.validValues.contains(integer) ? Optional.of(integer) : Optional.empty();
		} catch (NumberFormatException var3) {
			return Optional.empty();
		}
	}

	public String getValueAsString(Integer integer) {
		return integer.toString();
	}
}
