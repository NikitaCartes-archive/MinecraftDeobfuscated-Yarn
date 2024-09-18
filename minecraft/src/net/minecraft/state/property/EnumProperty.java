package net.minecraft.state.property;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;

/**
 * Represents a property that has enum values.
 * 
 * <p id="notes-on-enum">Notes on the enum class:
 * <ul>
 *   <li>The enum class is required to have 2 or more values.
 *   <li>The enum class is required to provide a name for each value by
 * overriding {@link StringIdentifiable#asString()}.
 *   <li>The names of the values are required to match the {@linkplain
 * net.minecraft.state.StateManager#VALID_NAME_PATTERN valid name pattern}.
 * Otherwise, {@link IllegalArgumentException} will be thrown during the
 * {@linkplain net.minecraft.state.StateManager.Builder#validate(Property)
 * validation of a property}.
 * </ul>
 * 
 * <p>See {@link net.minecraft.state.property.Properties} for example
 * usages.
 */
public final class EnumProperty<T extends Enum<T> & StringIdentifiable> extends Property<T> {
	private final List<T> values;
	private final Map<String, T> byName;
	private final int[] enumOrdinalToPropertyOrdinal;

	private EnumProperty(String name, Class<T> type, List<T> values) {
		super(name, type);
		if (values.isEmpty()) {
			throw new IllegalArgumentException("Trying to make empty EnumProperty '" + name + "'");
		} else {
			this.values = List.copyOf(values);
			T[] enums = (T[])type.getEnumConstants();
			this.enumOrdinalToPropertyOrdinal = new int[enums.length];

			for (T enum_ : enums) {
				this.enumOrdinalToPropertyOrdinal[enum_.ordinal()] = values.indexOf(enum_);
			}

			Builder<String, T> builder = ImmutableMap.builder();

			for (T enum2 : values) {
				String string = enum2.asString();
				builder.put(string, enum2);
			}

			this.byName = builder.buildOrThrow();
		}
	}

	@Override
	public List<T> getValues() {
		return this.values;
	}

	@Override
	public Optional<T> parse(String name) {
		return Optional.ofNullable((Enum)this.byName.get(name));
	}

	public String name(T enum_) {
		return enum_.asString();
	}

	public int ordinal(T enum_) {
		return this.enumOrdinalToPropertyOrdinal[enum_.ordinal()];
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof EnumProperty<?> enumProperty && super.equals(object)) {
				return this.values.equals(enumProperty.values);
			}

			return false;
		}
	}

	@Override
	public int computeHashCode() {
		int i = super.computeHashCode();
		return 31 * i + this.values.hashCode();
	}

	/**
	 * Creates an enum property with all values of the given enum class.
	 * 
	 * <p>See <a href="#notes-on-enum">notes on the enum class</a>.
	 * 
	 * @throws IllegalArgumentException if multiple values have the same name
	 * 
	 * @param type the type of the values the property contains
	 * @param name the name of the property; see {@linkplain Property#name the note on the
	 * name}
	 */
	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type) {
		return of(name, type, (Predicate<T>)(enum_ -> true));
	}

	/**
	 * Creates an enum property with the values allowed by the given filter.
	 * 
	 * <p>See <a href="#notes-on-enum">notes on the enum class</a>.
	 * 
	 * @throws IllegalArgumentException if multiple values have the same name
	 * 
	 * @see #of(String, Class)
	 * 
	 * @param name the name of the property; see {@linkplain Property#name the note on the
	 * name}
	 * @param type the type of the values the property contains
	 * @param filter the filter which specifies if a value is allowed; required to allow 2
	 * or more values
	 */
	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, Predicate<T> filter) {
		return of(name, type, (List<T>)Arrays.stream((Enum[])type.getEnumConstants()).filter(filter).collect(Collectors.toList()));
	}

	/**
	 * Creates an enum property with the given values.
	 * 
	 * <p>See <a href="#notes-on-enum">notes on the enum class</a>.
	 * 
	 * @throws IllegalArgumentException if multiple values have the same name
	 * 
	 * @see #of(String, Class)
	 * 
	 * @param name the name of the property; see {@linkplain Property#name the note on the
	 * name}
	 * @param type the type of the values the property contains
	 * @param values the values the property contains; required to have 2 or more values
	 */
	@SafeVarargs
	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, T... values) {
		return of(name, type, List.of(values));
	}

	/**
	 * Creates an enum property with the given values.
	 * 
	 * <p>See <a href="#notes-on-enum">notes on the enum class</a>.
	 * 
	 * @throws IllegalArgumentException if multiple values have the same name
	 * 
	 * @see #of(String, Class)
	 * 
	 * @param type the type of the values the property contains
	 * @param name the name of the property; see {@linkplain Property#name the note on the
	 * name}
	 */
	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, List<T> values) {
		return new EnumProperty<>(name, type, values);
	}
}
