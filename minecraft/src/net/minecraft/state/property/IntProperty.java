package net.minecraft.state.property;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a property that has integer values.
 * 
 * <p>See {@link net.minecraft.state.property.Properties} for example
 * usages.
 */
public class IntProperty extends Property<Integer> {
	private final ImmutableSet<Integer> values;
	private final int field_36888;
	private final int field_36889;

	protected IntProperty(String name, int min, int max) {
		super(name, Integer.class);
		if (min < 0) {
			throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
		} else if (max <= min) {
			throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
		} else {
			this.field_36888 = min;
			this.field_36889 = max;
			Set<Integer> set = Sets.<Integer>newHashSet();

			for (int i = min; i <= max; i++) {
				set.add(i);
			}

			this.values = ImmutableSet.copyOf(set);
		}
	}

	@Override
	public Collection<Integer> getValues() {
		return this.values;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof IntProperty && super.equals(object)) {
			IntProperty intProperty = (IntProperty)object;
			return this.values.equals(intProperty.values);
		} else {
			return false;
		}
	}

	@Override
	public int computeHashCode() {
		return 31 * super.computeHashCode() + this.values.hashCode();
	}

	/**
	 * Creates an integer property.
	 * 
	 * <p>Note that this method computes all possible values.
	 * 
	 * @throws IllegalArgumentException if {@code 0 <= min < max} is not
	 * satisfied
	 * 
	 * @param name the name of the property; see {@linkplain Property#name the note on the
	 * name}
	 * @param min the minimum value the property contains
	 * @param max the maximum value the property contains
	 */
	public static IntProperty of(String name, int min, int max) {
		return new IntProperty(name, min, max);
	}

	@Override
	public Optional<Integer> parse(String name) {
		try {
			Integer integer = Integer.valueOf(name);
			return integer >= this.field_36888 && integer <= this.field_36889 ? Optional.of(integer) : Optional.empty();
		} catch (NumberFormatException var3) {
			return Optional.empty();
		}
	}

	public String name(Integer integer) {
		return integer.toString();
	}
}
