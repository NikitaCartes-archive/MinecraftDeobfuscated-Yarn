/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.state.property;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import net.minecraft.state.property.Property;

/**
 * Represents a property that has integer values.
 * 
 * <p>See {@link net.minecraft.state.property.Properties} for example
 * usages.
 */
public class IntProperty
extends Property<Integer> {
    private final ImmutableSet<Integer> values;

    protected IntProperty(String name, int min, int max) {
        super(name, Integer.class);
        if (min < 0) {
            throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
        }
        if (max <= min) {
            throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
        }
        HashSet<Integer> set = Sets.newHashSet();
        for (int i = min; i <= max; ++i) {
            set.add(i);
        }
        this.values = ImmutableSet.copyOf(set);
    }

    @Override
    public Collection<Integer> getValues() {
        return this.values;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof IntProperty && super.equals(object)) {
            IntProperty intProperty = (IntProperty)object;
            return this.values.equals(intProperty.values);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return 31 * super.computeHashCode() + this.values.hashCode();
    }

    /**
     * Creates an integer property.
     * 
     * <p>Note that this method computes all possible values.
     * 
     * @throws IllegalArgumentException if {@code 0 <= min < max} is not
     * satisfied
     * 
     * @param max the maximum value the property contains
     * @param name the name of the property; see {@linkplain Property#name the note on the
     * name}
     * @param min the minimum value the property contains
     */
    public static IntProperty of(String name, int min, int max) {
        return new IntProperty(name, min, max);
    }

    @Override
    public Optional<Integer> parse(String name) {
        try {
            Integer integer = Integer.valueOf(name);
            return this.values.contains(integer) ? Optional.of(integer) : Optional.empty();
        } catch (NumberFormatException numberFormatException) {
            return Optional.empty();
        }
    }

    @Override
    public String name(Integer integer) {
        return integer.toString();
    }
}

