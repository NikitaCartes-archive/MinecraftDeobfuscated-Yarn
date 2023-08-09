package net.minecraft.state.property;

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
public class EnumProperty<T extends Enum<T> & StringIdentifiable> extends Property<T> {
	private final ImmutableSet<T> values;
	private final Map<String, T> byName = Maps.<String, T>newHashMap();

	protected EnumProperty(String name, Class<T> type, Collection<T> values) {
		super(name, type);
		this.values = ImmutableSet.copyOf(values);

		for (T enum_ : values) {
			String string = enum_.asString();
			if (this.byName.containsKey(string)) {
				throw new IllegalArgumentException("Multiple values have the same name '" + string + "'");
			}

			this.byName.put(string, enum_);
		}
	}

	@Override
	public Collection<T> getValues() {
		return this.values;
	}

	@Override
	public Optional<T> parse(String name) {
		return Optional.ofNullable((Enum)this.byName.get(name));
	}

	public String name(T enum_) {
		return enum_.asString();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof EnumProperty<?> enumProperty && super.equals(object)) {
				return this.values.equals(enumProperty.values) && this.byName.equals(enumProperty.byName);
			}

			return false;
		}
	}

	@Override
	public int computeHashCode() {
		int i = super.computeHashCode();
		i = 31 * i + this.values.hashCode();
		return 31 * i + this.byName.hashCode();
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
		return of(name, type, (Collection<T>)Arrays.stream((Enum[])type.getEnumConstants()).filter(filter).collect(Collectors.toList()));
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
	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, T... values) {
		return of(name, type, Lists.<T>newArrayList(values));
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
	 * @param values the values the property contains; required to have 2 or more values
	 */
	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, Collection<T> values) {
		return new EnumProperty<>(name, type, values);
	}
}
