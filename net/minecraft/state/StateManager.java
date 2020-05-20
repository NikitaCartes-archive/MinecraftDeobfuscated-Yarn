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
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
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
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class StateManager<O, S extends State<O, S>> {
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> properties;
    private final ImmutableList<S> states;

    protected StateManager(Function<O, S> function, O object, Factory<O, S> factory, Map<String, Property<?>> map) {
        this.owner = object;
        this.properties = ImmutableSortedMap.copyOf(map);
        class_5306<State> mapCodec = new class_5306<State>(this.properties, () -> (State)function.apply(object));
        LinkedHashMap map2 = Maps.newLinkedHashMap();
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
            State state = (State)factory.create(object, immutableMap, mapCodec);
            map2.put(immutableMap, state);
            list3.add(state);
        });
        for (State state : list3) {
            state.createWithTable(map2);
        }
        this.states = ImmutableList.copyOf(list3);
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

    static class class_5306<S extends State<?, S>>
    extends MapCodec<S> {
        private final Map<String, Property<?>> field_24735;
        private final Supplier<S> field_24736;

        public class_5306(Map<String, Property<?>> map, Supplier<S> supplier) {
            this.field_24735 = map;
            this.field_24736 = supplier;
        }

        @Override
        public <T> RecordBuilder<T> encode(S state, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
            ((State)state).getEntries().forEach((property, comparable) -> recordBuilder.add(property.getName(), dynamicOps.createString(class_5306.method_28487(property, comparable))));
            return recordBuilder;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
            return this.field_24735.keySet().stream().map(dynamicOps::createString);
        }

        @Override
        public <T> DataResult<S> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
            MutableObject<DataResult<S>> mutableObject = new MutableObject<DataResult<S>>(DataResult.success(this.field_24736.get()));
            mapLike.entries().forEach(pair -> {
                DataResult<Property> dataResult = dynamicOps.getStringValue(pair.getFirst()).map(this.field_24735::get);
                Object object = pair.getSecond();
                mutableObject.setValue(((DataResult)mutableObject.getValue()).flatMap((? super R state) -> dataResult.flatMap((? super R property) -> property.method_28503(dynamicOps, state, object))));
            });
            return mutableObject.getValue();
        }

        private static <T extends Comparable<T>> String method_28487(Property<T> property, Comparable<?> comparable) {
            return property.name(comparable);
        }

        public String toString() {
            return "PropertiesCodec";
        }

        @Override
        public /* synthetic */ RecordBuilder encode(Object object, DynamicOps dynamicOps, RecordBuilder recordBuilder) {
            return this.encode((S)((State)object), (DynamicOps<T>)dynamicOps, (RecordBuilder<T>)recordBuilder);
        }
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

        public StateManager<O, S> build(Function<O, S> function, Factory<O, S> factory) {
            return new StateManager<O, S>(function, this.owner, factory, this.namedProperties);
        }
    }

    public static interface Factory<O, S> {
        public S create(O var1, ImmutableMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3);
    }
}

