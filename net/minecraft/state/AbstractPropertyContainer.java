/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPropertyContainer<O, S>
implements PropertyContainer<S> {
    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_MAP_PRINTER = new Function<Map.Entry<Property<?>, Comparable<?>>, String>(){

        public String method_11576(@Nullable Map.Entry<Property<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            }
            Property<?> property = entry.getKey();
            return property.getName() + "=" + this.valueToString(property, entry.getValue());
        }

        private <T extends Comparable<T>> String valueToString(Property<T> property, Comparable<?> comparable) {
            return property.getName(comparable);
        }

        @Override
        public /* synthetic */ Object apply(@Nullable Object object) {
            return this.method_11576((Map.Entry)object);
        }
    };
    protected final O owner;
    private final ImmutableMap<Property<?>, Comparable<?>> entries;
    private final int hashCode;
    private Table<Property<?>, Comparable<?>, S> withTable;

    protected AbstractPropertyContainer(O object, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
        this.owner = object;
        this.entries = immutableMap;
        this.hashCode = immutableMap.hashCode();
    }

    public <T extends Comparable<T>> S cycle(Property<T> property) {
        return this.with(property, (Comparable)AbstractPropertyContainer.getNext(property.getValues(), this.get(property)));
    }

    protected static <T> T getNext(Collection<T> collection, T object) {
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().equals(object)) continue;
            if (iterator.hasNext()) {
                return iterator.next();
            }
            return collection.iterator().next();
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

    @Override
    public <T extends Comparable<T>> T get(Property<T> property) {
        Comparable<?> comparable = this.entries.get(property);
        if (comparable == null) {
            throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.owner);
        }
        return (T)((Comparable)property.getValueType().cast(comparable));
    }

    @Override
    public <T extends Comparable<T>, V extends T> S with(Property<T> property, V comparable) {
        Comparable<?> comparable2 = this.entries.get(property);
        if (comparable2 == null) {
            throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.owner);
        }
        if (comparable2 == comparable) {
            return (S)this;
        }
        S object = this.withTable.get(property, comparable);
        if (object == null) {
            throw new IllegalArgumentException("Cannot set property " + property + " to " + comparable + " on " + this.owner + ", it is not an allowed value");
        }
        return object;
    }

    public void createWithTable(Map<Map<Property<?>, Comparable<?>>, S> map) {
        if (this.withTable != null) {
            throw new IllegalStateException();
        }
        HashBasedTable<Property, Comparable, S> table = HashBasedTable.create();
        for (Map.Entry entry : this.entries.entrySet()) {
            Property property = (Property)entry.getKey();
            for (Comparable comparable : property.getValues()) {
                if (comparable == entry.getValue()) continue;
                table.put(property, comparable, map.get(this.toMapWith(property, comparable)));
            }
        }
        this.withTable = table.isEmpty() ? table : ArrayTable.create(table);
    }

    private Map<Property<?>, Comparable<?>> toMapWith(Property<?> property, Comparable<?> comparable) {
        HashMap<Property<?>, Comparable<?>> map = Maps.newHashMap(this.entries);
        map.put(property, comparable);
        return map;
    }

    @Override
    public ImmutableMap<Property<?>, Comparable<?>> getEntries() {
        return this.entries;
    }

    public boolean equals(Object object) {
        return this == object;
    }

    public int hashCode() {
        return this.hashCode;
    }
}

