/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.state.property;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.state.property.Property;

/**
 * Represents a property that has boolean values.
 * 
 * <p>See {@link net.minecraft.state.property.Properties} for example
 * usages.
 */
public class BooleanProperty
extends Property<Boolean> {
    private final ImmutableSet<Boolean> values = ImmutableSet.of(Boolean.valueOf(true), Boolean.valueOf(false));

    protected BooleanProperty(String name) {
        super(name, Boolean.class);
    }

    @Override
    public Collection<Boolean> getValues() {
        return this.values;
    }

    /**
     * Creates a boolean property.
     * 
     * @param name the name of the property; see {@linkplain Property#name the note on the
     * name}
     */
    public static BooleanProperty of(String name) {
        return new BooleanProperty(name);
    }

    @Override
    public Optional<Boolean> parse(String name) {
        if ("true".equals(name) || "false".equals(name)) {
            return Optional.of(Boolean.valueOf(name));
        }
        return Optional.empty();
    }

    @Override
    public String name(Boolean boolean_) {
        return boolean_.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BooleanProperty && super.equals(object)) {
            BooleanProperty booleanProperty = (BooleanProperty)object;
            return this.values.equals(booleanProperty.values);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return 31 * super.computeHashCode() + this.values.hashCode();
    }
}

