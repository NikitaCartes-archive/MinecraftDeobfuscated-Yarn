/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.state.property.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface PropertyContainer<C> {
    public static final Logger LOGGER = LogManager.getLogger();

    public <T extends Comparable<T>> T get(Property<T> var1);

    public <T extends Comparable<T>, V extends T> C with(Property<T> var1, V var2);

    public ImmutableMap<Property<?>, Comparable<?>> getEntries();

    public static <T extends Comparable<T>> String getValueAsString(Property<T> property, Comparable<?> comparable) {
        return property.getValueAsString(comparable);
    }

    public static <S extends PropertyContainer<S>, T extends Comparable<T>> S deserialize(S propertyContainer, Property<T> property, String string, String string2, String string3) {
        Optional<T> optional = property.getValue(string3);
        if (optional.isPresent()) {
            return (S)((PropertyContainer)propertyContainer.with(property, (Comparable)((Comparable)optional.get())));
        }
        LOGGER.warn("Unable to read property: {} with value: {} for input: {}", (Object)string, (Object)string3, (Object)string2);
        return propertyContainer;
    }
}

