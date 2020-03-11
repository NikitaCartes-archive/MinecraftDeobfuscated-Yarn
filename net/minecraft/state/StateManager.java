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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.state.AbstractState;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.MapUtil;
import org.jetbrains.annotations.Nullable;

public class StateManager<O, S extends State<S>> {
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> properties;
    private final ImmutableList<S> states;

    protected <A extends AbstractState<O, S>> StateManager(O owner, Factory<O, S, A> factory, Map<String, Property<?>> namedProperties) {
        this.owner = owner;
        this.properties = ImmutableSortedMap.copyOf(namedProperties);
        LinkedHashMap map = Maps.newLinkedHashMap();
        ArrayList<AbstractState> list3 = Lists.newArrayList();
        Stream<List<List<Object>>> stream = Stream.of(Collections.emptyList());
        for (Property property : this.properties.values()) {
            stream = stream.flatMap(list -> property.getValues().stream().map(comparable -> {
                ArrayList<Comparable> list2 = Lists.newArrayList(list);
                list2.add((Comparable)comparable);
                return list2;
            }));
        }
        stream.forEach(list2 -> {
            Map map2 = MapUtil.createMap(this.properties.values(), list2);
            Object abstractState = factory.create(owner, ImmutableMap.copyOf(map2));
            map.put(map2, abstractState);
            list3.add(abstractState);
        });
        for (AbstractState abstractState : list3) {
            abstractState.createWithTable(map);
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

    public static class Builder<O, S extends State<S>> {
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

        public <A extends AbstractState<O, S>> StateManager<O, S> build(Factory<O, S, A> factory) {
            return new StateManager<O, S>(this.owner, factory, this.namedProperties);
        }
    }

    public static interface Factory<O, S extends State<S>, A extends AbstractState<O, S>> {
        public A create(O var1, ImmutableMap<Property<?>, Comparable<?>> var2);
    }
}

