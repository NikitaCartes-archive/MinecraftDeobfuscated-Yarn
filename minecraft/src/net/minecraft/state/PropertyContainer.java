package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.state.property.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface PropertyContainer<C> {
	Logger LOGGER = LogManager.getLogger();

	<T extends Comparable<T>> T method_11654(Property<T> property);

	<T extends Comparable<T>, V extends T> C method_11657(Property<T> property, V comparable);

	ImmutableMap<Property<?>, Comparable<?>> getEntries();

	static <T extends Comparable<T>> String method_16551(Property<T> property, Comparable<?> comparable) {
		return property.getValueAsString((T)comparable);
	}

	static <S extends PropertyContainer<S>, T extends Comparable<T>> S method_11655(
		S propertyContainer, Property<T> property, String string, String string2, String string3
	) {
		Optional<T> optional = property.getValue(string3);
		if (optional.isPresent()) {
			return propertyContainer.method_11657(property, (Comparable)optional.get());
		} else {
			LOGGER.warn("Unable to read property: {} with value: {} for input: {}", string, string3, string2);
			return propertyContainer;
		}
	}
}
