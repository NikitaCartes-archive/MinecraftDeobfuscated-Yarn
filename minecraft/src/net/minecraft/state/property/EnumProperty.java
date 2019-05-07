package net.minecraft.state.property;

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
import net.minecraft.util.StringIdentifiable;

public class EnumProperty<T extends Enum<T> & StringIdentifiable> extends AbstractProperty<T> {
	private final ImmutableSet<T> values;
	private final Map<String, T> valuesByName = Maps.<String, T>newHashMap();

	protected EnumProperty(String string, Class<T> class_, Collection<T> collection) {
		super(string, class_);
		this.values = ImmutableSet.copyOf(collection);

		for (T enum_ : collection) {
			String string2 = enum_.asString();
			if (this.valuesByName.containsKey(string2)) {
				throw new IllegalArgumentException("Multiple values have the same name '" + string2 + "'");
			}

			this.valuesByName.put(string2, enum_);
		}
	}

	@Override
	public Collection<T> getValues() {
		return this.values;
	}

	@Override
	public Optional<T> getValue(String string) {
		return Optional.ofNullable(this.valuesByName.get(string));
	}

	public String method_11846(T enum_) {
		return enum_.asString();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof EnumProperty && super.equals(object)) {
			EnumProperty<?> enumProperty = (EnumProperty<?>)object;
			return this.values.equals(enumProperty.values) && this.valuesByName.equals(enumProperty.valuesByName);
		} else {
			return false;
		}
	}

	@Override
	public int computeHashCode() {
		int i = super.computeHashCode();
		i = 31 * i + this.values.hashCode();
		return 31 * i + this.valuesByName.hashCode();
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> create(String string, Class<T> class_) {
		return create(string, class_, Predicates.alwaysTrue());
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> create(String string, Class<T> class_, Predicate<T> predicate) {
		return create(string, class_, (Collection<T>)Arrays.stream(class_.getEnumConstants()).filter(predicate).collect(Collectors.toList()));
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> create(String string, Class<T> class_, T... enums) {
		return create(string, class_, Lists.<T>newArrayList(enums));
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> create(String string, Class<T> class_, Collection<T> collection) {
		return new EnumProperty<>(string, class_, collection);
	}
}
