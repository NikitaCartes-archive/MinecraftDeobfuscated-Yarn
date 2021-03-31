/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

public abstract class State<O, S> {
    public static final String NAME = "Name";
    public static final String PROPERTIES = "Properties";
    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_MAP_PRINTER = new Function<Map.Entry<Property<?>, Comparable<?>>, String>(){

        @Override
        public String apply(@Nullable Map.Entry<Property<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            }
            Property<?> property = entry.getKey();
            return property.getName() + "=" + this.nameValue(property, entry.getValue());
        }

        private <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
            return property.name(value);
        }

        @Override
        public /* synthetic */ Object apply(@Nullable Object object) {
            return this.apply((Map.Entry)object);
        }
    };
    protected final O owner;
    private final ImmutableMap<Property<?>, Comparable<?>> entries;
    private Table<Property<?>, Comparable<?>, S> withTable;
    protected final MapCodec<S> codec;

    protected State(O owner, ImmutableMap<Property<?>, Comparable<?>> entries, MapCodec<S> codec) {
        this.owner = owner;
        this.entries = entries;
        this.codec = codec;
    }

    public <T extends Comparable<T>> S cycle(Property<T> property) {
        return this.with(property, (Comparable)State.getNext(property.getValues(), this.get(property)));
    }

    protected static <T> T getNext(Collection<T> values, T value) {
        Iterator<T> iterator = values.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().equals(value)) continue;
            if (iterator.hasNext()) {
                return iterator.next();
            }
            return values.iterator().next();
        }
        return iterator.next();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.owner);
        if (!this.getEntries().isEmpty()) {
            stringBuilder.append('[');
            stringBuilder.append(this.getEntries().entrySet().stream().map(PROPERTY_MAP_PRINTER).collect(Collectors.joining(",")));
            stringBuilder.append(']');
        }
        return stringBuilder.toString();
    }

    public Collection<Property<?>> getProperties() {
        return Collections.unmodifiableCollection(this.entries.keySet());
    }

    public <T extends Comparable<T>> boolean contains(Property<T> property) {
        return this.entries.containsKey(property);
    }

    public <T extends Comparable<T>> T get(Property<T> property) {
        Comparable<?> comparable = this.entries.get(property);
        if (comparable == null) {
            throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.owner);
        }
        return (T)((Comparable)property.getType().cast(comparable));
    }

    public <T extends Comparable<T>> Optional<T> getOrEmpty(Property<T> property) {
        Comparable<?> comparable = this.entries.get(property);
        if (comparable == null) {
            return Optional.empty();
        }
        return Optional.of(property.getType().cast(comparable));
    }

    public <T extends Comparable<T>, V extends T> S with(Property<T> property, V value) {
        Comparable<?> comparable = this.entries.get(property);
        if (comparable == null) {
            throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.owner);
        }
        if (comparable == value) {
            return (S)this;
        }
        S object = this.withTable.get(property, value);
        if (object == null) {
            throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on " + this.owner + ", it is not an allowed value");
        }
        return object;
    }

    public void createWithTable(Map<Map<Property<?>, Comparable<?>>, S> states) {
        if (this.withTable != null) {
            throw new IllegalStateException();
        }
        HashBasedTable<Property, Comparable, S> table = HashBasedTable.create();
        for (Map.Entry entry : this.entries.entrySet()) {
            Property property = (Property)entry.getKey();
            for (Comparable comparable : property.getValues()) {
                if (comparable == entry.getValue()) continue;
                table.put(property, comparable, states.get(this.toMapWith(property, comparable)));
            }
        }
        this.withTable = table.isEmpty() ? table : ArrayTable.create(table);
    }

    private Map<Property<?>, Comparable<?>> toMapWith(Property<?> property, Comparable<?> value) {
        HashMap<Property<?>, Comparable<?>> map = Maps.newHashMap(this.entries);
        map.put(property, value);
        return map;
    }

    public ImmutableMap<Property<?>, Comparable<?>> getEntries() {
        return this.entries;
    }

    protected static <O, S extends State<O, S>> Codec<S> createCodec(Codec<O> codec, Function<O, S> ownerToStateFunction) {
        return codec.dispatch(NAME, state -> state.owner, object -> {
            State state = (State)ownerToStateFunction.apply(object);
            if (state.getEntries().isEmpty()) {
                return Codec.unit(state);
            }
            return state.codec.fieldOf(PROPERTIES).codec();
        });
    }
}

