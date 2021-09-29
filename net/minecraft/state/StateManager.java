/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

public class StateManager<O, S extends State<O, S>> {
    static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> properties;
    private final ImmutableList<S> states;

    protected StateManager(Function<O, S> defaultStateGetter, O owner, Factory<O, S> factory, Map<String, Property<?>> propertiesMap) {
        this.owner = owner;
        this.properties = ImmutableSortedMap.copyOf(propertiesMap);
        Supplier<State> supplier = () -> (State)defaultStateGetter.apply(owner);
        MapCodec<State> mapCodec = MapCodec.of(Encoder.empty(), Decoder.unit(supplier));
        for (Map.Entry entry : this.properties.entrySet()) {
            mapCodec = StateManager.addFieldToMapCodec(mapCodec, supplier, (String)entry.getKey(), (Property)entry.getValue());
        }
        MapCodec<State> mapCodec2 = mapCodec;
        LinkedHashMap map = Maps.newLinkedHashMap();
        ArrayList<State> list3 = Lists.newArrayList();
        Stream<List<List<Object>>> stream = Stream.of(Collections.emptyList());
        for (Property property : this.properties.values()) {
            stream = stream.flatMap(list -> property.getValues().stream().map(comparable -> {
                ArrayList<Pair<Property, Comparable>> list2 = Lists.newArrayList(list);
                list2.add(Pair.of(property, comparable));
                return list2;
            }));
        }
        stream.forEach(list2 -> {
            ImmutableMap<Property<?>, Comparable<?>> immutableMap = list2.stream().collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
            State state = (State)factory.create(owner, immutableMap, mapCodec2);
            map.put(immutableMap, state);
            list3.add(state);
        });
        for (State state : list3) {
            state.createWithTable(map);
        }
        this.states = ImmutableList.copyOf(list3);
    }

    private static <S extends State<?, S>, T extends Comparable<T>> MapCodec<S> addFieldToMapCodec(MapCodec<S> mapCodec, Supplier<S> defaultStateGetter, String key, Property<T> property) {
        return Codec.mapPair(mapCodec, ((MapCodec)property.getValueCodec().fieldOf(key)).orElseGet(string -> {}, () -> property.createValue((State)defaultStateGetter.get()))).xmap(pair -> (State)((State)pair.getFirst()).with(property, ((Property.Value)pair.getSecond()).value()), state -> Pair.of(state, property.createValue((State<?, ?>)state)));
    }

    public ImmutableList<S> getStates() {
        return this.states;
    }

    public S getDefaultState() {
        return (S)((State)this.states.get(0));
    }

    public O getOwner() {
        return this.owner;
    }

    public Collection<Property<?>> getProperties() {
        return this.properties.values();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.properties.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
    }

    @Nullable
    public Property<?> getProperty(String name) {
        return this.properties.get(name);
    }

    public static interface Factory<O, S> {
        public S create(O var1, ImmutableMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3);
    }

    public static class Builder<O, S extends State<O, S>> {
        private final O owner;
        private final Map<String, Property<?>> namedProperties = Maps.newHashMap();

        public Builder(O owner) {
            this.owner = owner;
        }

        public Builder<O, S> add(Property<?> ... properties) {
            for (Property<?> property : properties) {
                this.validate(property);
                this.namedProperties.put(property.getName(), property);
            }
            return this;
        }

        private <T extends Comparable<T>> void validate(Property<T> property) {
            String string = property.getName();
            if (!VALID_NAME_PATTERN.matcher(string).matches()) {
                throw new IllegalArgumentException(this.owner + " has invalidly named property: " + string);
            }
            Collection<T> collection = property.getValues();
            if (collection.size() <= 1) {
                throw new IllegalArgumentException(this.owner + " attempted use property " + string + " with <= 1 possible values");
            }
            for (Comparable comparable : collection) {
                String string2 = property.name(comparable);
                if (VALID_NAME_PATTERN.matcher(string2).matches()) continue;
                throw new IllegalArgumentException(this.owner + " has property: " + string + " with invalidly named value: " + string2);
            }
            if (this.namedProperties.containsKey(string)) {
                throw new IllegalArgumentException(this.owner + " has duplicate property: " + string);
            }
        }

        public StateManager<O, S> build(Function<O, S> defaultStateGetter, Factory<O, S> factory) {
            return new StateManager<O, S>(defaultStateGetter, this.owner, factory, this.namedProperties);
        }
    }
}

