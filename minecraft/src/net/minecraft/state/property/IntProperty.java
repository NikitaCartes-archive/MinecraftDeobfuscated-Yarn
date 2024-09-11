package net.minecraft.state.property;

import it.unimi.dsi.fastutil.ints.IntImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Represents a property that has integer values.
 * 
 * <p>See {@link net.minecraft.state.property.Properties} for example
 * usages.
 */
public class IntProperty extends Property<Integer> {
	private final IntImmutableList values;
	private final int min;
	private final int max;

	protected IntProperty(String name, int min, int max) {
		super(name, Integer.class);
		if (min < 0) {
			throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
		} else if (max <= min) {
			throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
		} else {
			this.min = min;
			this.max = max;
			this.values = IntImmutableList.toList(IntStream.range(min, max + 1));
		}
	}

	@Override
	public List<Integer> getValues() {
		return this.values;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof IntProperty intProperty && super.equals(object)) {
				return this.values.equals(intProperty.values);
			}

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
	 * @param max the maximum value the property contains
	 * @param name the name of the property; see {@linkplain Property#name the note on the
	 * name}
	 * @param min the minimum value the property contains
	 */
	public static IntProperty of(String name, int min, int max) {
		return new IntProperty(name, min, max);
	}

	@Override
	public Optional<Integer> parse(String name) {
		try {
			Integer integer = Integer.valueOf(name);
			return integer >= this.min && integer <= this.max ? Optional.of(integer) : Optional.empty();
		} catch (NumberFormatException var3) {
			return Optional.empty();
		}
	}

	public String name(Integer integer) {
		return integer.toString();
	}

	public int ordinal(Integer integer) {
		return integer <= this.max ? integer - this.min : -1;
	}
}
