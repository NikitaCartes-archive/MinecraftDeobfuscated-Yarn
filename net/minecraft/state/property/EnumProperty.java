/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.state.property.AbstractProperty;
import net.minecraft.util.StringIdentifiable;

public class EnumProperty<T extends Enum<T>>
extends AbstractProperty<T> {
    private final ImmutableSet<T> values;
    private final Map<String, T> byName = Maps.newHashMap();

    protected EnumProperty(String name, Class<T> type, Collection<T> values) {
        super(name, type);
        this.values = ImmutableSet.copyOf(values);
        for (Enum enum_ : values) {
            String string = ((StringIdentifiable)((Object)enum_)).asString();
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
        return Optional.ofNullable(this.byName.get(name));
    }

    @Override
    public String name(T enum_) {
        return ((StringIdentifiable)enum_).asString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EnumProperty && super.equals(o)) {
            EnumProperty enumProperty = (EnumProperty)o;
            return this.values.equals(enumProperty.values) && this.byName.equals(enumProperty.byName);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int i = super.computeHashCode();
        i = 31 * i + this.values.hashCode();
        i = 31 * i + this.byName.hashCode();
        return i;
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String name, Class<T> type) {
        return EnumProperty.of(name, type, Predicates.alwaysTrue());
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String name, Class<T> type, Predicate<T> filter) {
        return EnumProperty.of(name, type, Arrays.stream(type.getEnumConstants()).filter(filter).collect(Collectors.toList()));
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String name, Class<T> type, T ... values) {
        return EnumProperty.of(name, type, Lists.newArrayList(values));
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String name, Class<T> type, Collection<T> values) {
        return new EnumProperty<T>(name, type, values);
    }
}

