package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.state.property.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface State<C> {
	Logger LOGGER = LogManager.getLogger();

	<T extends Comparable<T>> T get(Property<T> property);

	<T extends Comparable<T>, V extends T> C with(Property<T> property, V value);

	ImmutableMap<Property<?>, Comparable<?>> getEntries();

	static <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
		return property.name((T)value);
	}

	static <S extends State<S>, T extends Comparable<T>> S tryRead(S state, Property<T> property, String propertyName, String input, String valueName) {
		Optional<T> optional = property.parse(valueName);
		if (optional.isPresent()) {
			return state.with(property, (Comparable)optional.get());
		} else {
			LOGGER.warn("Unable to read property: {} with value: {} for input: {}", propertyName, valueName, input);
			return state;
		}
	}
}
