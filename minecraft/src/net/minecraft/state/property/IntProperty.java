package net.minecraft.state.property;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class IntProperty extends AbstractProperty<Integer> {
	private final ImmutableSet<Integer> values;

	protected IntProperty(String name, int min, int max) {
		super(name, Integer.class);
		if (min < 0) {
			throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
		} else if (max <= min) {
			throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
		} else {
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof IntProperty && super.equals(o)) {
			IntProperty intProperty = (IntProperty)o;
			return this.values.equals(intProperty.values);
		} else {
			return false;
		}
	}

	@Override
	public int computeHashCode() {
		return 31 * super.computeHashCode() + this.values.hashCode();
	}

	public static IntProperty of(String name, int min, int max) {
		return new IntProperty(name, min, max);
	}

	@Override
	public Optional<Integer> parse(String name) {
		try {
			Integer integer = Integer.valueOf(name);
			return this.values.contains(integer) ? Optional.of(integer) : Optional.empty();
		} catch (NumberFormatException var3) {
			return Optional.empty();
		}
	}

	public String name(Integer integer) {
		return integer.toString();
	}
}
