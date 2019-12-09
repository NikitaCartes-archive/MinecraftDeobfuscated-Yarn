/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.state.property.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface State<C> {
    public static final Logger LOGGER = LogManager.getLogger();

    public <T extends Comparable<T>> T get(Property<T> var1);

    public <T extends Comparable<T>, V extends T> C with(Property<T> var1, V var2);

    public ImmutableMap<Property<?>, Comparable<?>> getEntries();

    public static <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
        return property.name(value);
    }

    public static <S extends State<S>, T extends Comparable<T>> S tryRead(S state, Property<T> property, String propertyName, String input, String valueName) {
        Optional<T> optional = property.parse(valueName);
        if (optional.isPresent()) {
            return (S)((State)state.with(property, (Comparable)((Comparable)optional.get())));
        }
        LOGGER.warn("Unable to read property: {} with value: {} for input: {}", (Object)propertyName, (Object)valueName, (Object)input);
        return state;
    }
}

