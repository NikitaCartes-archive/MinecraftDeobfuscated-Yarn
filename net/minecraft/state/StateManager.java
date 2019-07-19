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
import net.minecraft.util.MapUtil;
import org.jetbrains.annotations.Nullable;

public class StateManager<O, S extends State<S>> {
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> properties;
    private final ImmutableList<S> states;

    protected <A extends AbstractState<O, S>> StateManager(O object, Factory<O, S, A> factory, Map<String, Property<?>> map) {
        this.owner = object;
        this.properties = ImmutableSortedMap.copyOf(map);
        LinkedHashMap map2 = Maps.newLinkedHashMap();
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
            Object abstractState = factory.create(object, ImmutableMap.copyOf(map2));
            map2.put(map2, abstractState);
            list3.add(abstractState);
        });
        for (AbstractState abstractState : list3) {
            abstractState.createWithTable(map2);
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
    public Property<?> getProperty(String string) {
        return this.properties.get(string);
    }

    public static class Builder<O, S extends State<S>> {
        private final O baseObject;
        private final Map<String, Property<?>> propertyMap = Maps.newHashMap();

        public Builder(O object) {
            this.baseObject = object;
        }

        public Builder<O, S> add(Property<?> ... propertys) {
            for (Property<?> property : propertys) {
                this.validate(property);
                this.propertyMap.put(property.getName(), property);
            }
            return this;
        }

        private <T extends Comparable<T>> void validate(Property<T> property) {
            String string = property.getName();
            if (!VALID_NAME_PATTERN.matcher(string).matches()) {
                throw new IllegalArgumentException(this.baseObject + " has invalidly named property: " + string);
            }
            Collection<T> collection = property.getValues();
            if (collection.size() <= 1) {
                throw new IllegalArgumentException(this.baseObject + " attempted use property " + string + " with <= 1 possible values");
            }
            for (Comparable comparable : collection) {
                String string2 = property.name(comparable);
                if (VALID_NAME_PATTERN.matcher(string2).matches()) continue;
                throw new IllegalArgumentException(this.baseObject + " has property: " + string + " with invalidly named value: " + string2);
            }
            if (this.propertyMap.containsKey(string)) {
                throw new IllegalArgumentException(this.baseObject + " has duplicate property: " + string);
            }
        }

        public <A extends AbstractState<O, S>> StateManager<O, S> build(Factory<O, S, A> factory) {
            return new StateManager<O, S>(this.baseObject, factory, this.propertyMap);
        }
    }

    public static interface Factory<O, S extends State<S>, A extends AbstractState<O, S>> {
        public A create(O var1, ImmutableMap<Property<?>, Comparable<?>> var2);
    }
}

