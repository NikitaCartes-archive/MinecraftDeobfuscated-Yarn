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

    protected EnumProperty(String string, Class<T> class_, Collection<T> collection) {
        super(string, class_);
        this.values = ImmutableSet.copyOf(collection);
        for (Enum enum_ : collection) {
            String string2 = ((StringIdentifiable)((Object)enum_)).asString();
            if (this.byName.containsKey(string2)) {
                throw new IllegalArgumentException("Multiple values have the same name '" + string2 + "'");
            }
            this.byName.put(string2, enum_);
        }
    }

    @Override
    public Collection<T> getValues() {
        return this.values;
    }

    @Override
    public Optional<T> parse(String string) {
        return Optional.ofNullable(this.byName.get(string));
    }

    public String method_11846(T enum_) {
        return ((StringIdentifiable)enum_).asString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof EnumProperty && super.equals(object)) {
            EnumProperty enumProperty = (EnumProperty)object;
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

    public static <T extends Enum<T>> EnumProperty<T> of(String string, Class<T> class_) {
        return EnumProperty.of(string, class_, Predicates.alwaysTrue());
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String string, Class<T> class_, Predicate<T> predicate) {
        return EnumProperty.of(string, class_, Arrays.stream(class_.getEnumConstants()).filter(predicate).collect(Collectors.toList()));
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String string, Class<T> class_, T ... enums) {
        return EnumProperty.of(string, class_, Lists.newArrayList(enums));
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String string, Class<T> class_, Collection<T> collection) {
        return new EnumProperty<T>(string, class_, collection);
    }
}

