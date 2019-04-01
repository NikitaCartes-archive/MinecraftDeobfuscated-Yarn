package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class class_2758 extends class_2733<Integer> {
	private final ImmutableSet<Integer> field_12614;

	protected class_2758(String string, int i, int j) {
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

			this.field_12614 = ImmutableSet.copyOf(set);
		}
	}

	@Override
	public Collection<Integer> method_11898() {
		return this.field_12614;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof class_2758 && super.equals(object)) {
			class_2758 lv = (class_2758)object;
			return this.field_12614.equals(lv.field_12614);
		} else {
			return false;
		}
	}

	@Override
	public int method_11799() {
		return 31 * super.method_11799() + this.field_12614.hashCode();
	}

	public static class_2758 method_11867(String string, int i, int j) {
		return new class_2758(string, i, j);
	}

	@Override
	public Optional<Integer> method_11900(String string) {
		try {
			Integer integer = Integer.valueOf(string);
			return this.field_12614.contains(integer) ? Optional.of(integer) : Optional.empty();
		} catch (NumberFormatException var3) {
			return Optional.empty();
		}
	}

	public String method_11868(Integer integer) {
		return integer.toString();
	}
}
