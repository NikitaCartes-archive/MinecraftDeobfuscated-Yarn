package net.minecraft;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class class_2754<T extends Enum<T> & class_3542> extends class_2733<T> {
	private final ImmutableSet<T> field_12595;
	private final Map<String, T> field_12596 = Maps.<String, T>newHashMap();

	protected class_2754(String string, Class<T> class_, Collection<T> collection) {
		super(string, class_);
		this.field_12595 = ImmutableSet.copyOf(collection);

		for (T enum_ : collection) {
			String string2 = enum_.method_15434();
			if (this.field_12596.containsKey(string2)) {
				throw new IllegalArgumentException("Multiple values have the same name '" + string2 + "'");
			}

			this.field_12596.put(string2, enum_);
		}
	}

	@Override
	public Collection<T> method_11898() {
		return this.field_12595;
	}

	@Override
	public Optional<T> method_11900(String string) {
		return Optional.ofNullable(this.field_12596.get(string));
	}

	public String method_11846(T enum_) {
		return enum_.method_15434();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof class_2754 && super.equals(object)) {
			class_2754<?> lv = (class_2754<?>)object;
			return this.field_12595.equals(lv.field_12595) && this.field_12596.equals(lv.field_12596);
		} else {
			return false;
		}
	}

	@Override
	public int method_11799() {
		int i = super.method_11799();
		i = 31 * i + this.field_12595.hashCode();
		return 31 * i + this.field_12596.hashCode();
	}

	public static <T extends Enum<T> & class_3542> class_2754<T> method_11850(String string, Class<T> class_) {
		return method_11848(string, class_, Predicates.alwaysTrue());
	}

	public static <T extends Enum<T> & class_3542> class_2754<T> method_11848(String string, Class<T> class_, Predicate<T> predicate) {
		return method_11847(string, class_, (Collection<T>)Arrays.stream(class_.getEnumConstants()).filter(predicate).collect(Collectors.toList()));
	}

	public static <T extends Enum<T> & class_3542> class_2754<T> method_11849(String string, Class<T> class_, T... enums) {
		return method_11847(string, class_, Lists.<T>newArrayList(enums));
	}

	public static <T extends Enum<T> & class_3542> class_2754<T> method_11847(String string, Class<T> class_, Collection<T> collection) {
		return new class_2754<>(string, class_, collection);
	}
}
