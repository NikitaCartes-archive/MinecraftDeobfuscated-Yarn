package net.minecraft.data.client.model;

import java.util.stream.Stream;
import net.minecraft.state.property.Property;

/**
 * Represents a property to value pair in block state definition.
 * 
 * <p>This object is immutable.
 */
public final class PropertiesEntry<T extends Comparable<T>> {
	private final Property<T> property;
	private final T value;

	public PropertiesEntry(Property<T> property, T value) {
		if (!property.getValues().contains(value)) {
			throw new IllegalArgumentException("Value " + value + " does not belong to property " + property);
		} else {
			this.property = property;
			this.value = value;
		}
	}

	public Property<T> getProperty() {
		return this.property;
	}

	public String toString() {
		return this.property.getName() + "=" + this.property.name(this.value);
	}

	/**
	 * Returns a stream of all possible property to value pairs for a specific
	 * property.
	 */
	public static <T extends Comparable<T>> Stream<PropertiesEntry<T>> streamAllFor(Property<T> property) {
		return property.getValues().stream().map(comparable -> new PropertiesEntry<>(property, (T)comparable));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof PropertiesEntry)) {
			return false;
		} else {
			PropertiesEntry<?> propertiesEntry = (PropertiesEntry<?>)object;
			return this.property == propertiesEntry.property && this.value.equals(propertiesEntry.value);
		}
	}

	public int hashCode() {
		int i = this.property.hashCode();
		return 31 * i + this.value.hashCode();
	}
}
