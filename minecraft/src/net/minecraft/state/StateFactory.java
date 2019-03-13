package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.state.property.Property;
import net.minecraft.util.MapUtil;

public class StateFactory<O, S extends PropertyContainer<S>> {
	private static final Pattern NAME_MATCHER = Pattern.compile("^[a-z0-9_]+$");
	private final O baseObject;
	private final ImmutableSortedMap<String, Property<?>> propertyMap;
	private final ImmutableList<S> states;

	protected <A extends AbstractPropertyContainer<O, S>> StateFactory(O object, StateFactory.Factory<O, S, A> factory, Map<String, Property<?>> map) {
		this.baseObject = object;
		this.propertyMap = ImmutableSortedMap.copyOf(map);
		Map<Map<Property<?>, Comparable<?>>, A> map2 = Maps.<Map<Property<?>, Comparable<?>>, A>newLinkedHashMap();
		List<A> list = Lists.<A>newArrayList();
		Stream<List<Comparable<?>>> stream = Stream.of(Collections.emptyList());

		for (Property<?> property : this.propertyMap.values()) {
			stream = stream.flatMap(listx -> property.getValues().stream().map(comparable -> {
					List<Comparable<?>> list2 = Lists.<Comparable<?>>newArrayList(listx);
					list2.add(comparable);
					return list2;
				}));
		}

		stream.forEach(list2 -> {
			Map<Property<?>, Comparable<?>> map2x = MapUtil.createMap(this.propertyMap.values(), list2);
			A abstractPropertyContainerx = factory.create(object, ImmutableMap.copyOf(map2x));
			map2.put(map2x, abstractPropertyContainerx);
			list.add(abstractPropertyContainerx);
		});

		for (A abstractPropertyContainer : list) {
			abstractPropertyContainer.method_11571(map2);
		}

		this.states = ImmutableList.copyOf(list);
	}

	public ImmutableList<S> getStates() {
		return this.states;
	}

	public S method_11664() {
		return (S)this.states.get(0);
	}

	public O getBaseObject() {
		return this.baseObject;
	}

	public Collection<Property<?>> getProperties() {
		return this.propertyMap.values();
	}

	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("block", this.baseObject)
			.add("properties", this.propertyMap.values().stream().map(Property::getName).collect(Collectors.toList()))
			.toString();
	}

	@Nullable
	public Property<?> method_11663(String string) {
		return this.propertyMap.get(string);
	}

	public static class Builder<O, S extends PropertyContainer<S>> {
		private final O baseObject;
		private final Map<String, Property<?>> propertyMap = Maps.<String, Property<?>>newHashMap();

		public Builder(O object) {
			this.baseObject = object;
		}

		public StateFactory.Builder<O, S> method_11667(Property<?>... propertys) {
			for (Property<?> property : propertys) {
				this.method_11669(property);
				this.propertyMap.put(property.getName(), property);
			}

			return this;
		}

		private <T extends Comparable<T>> void method_11669(Property<T> property) {
			String string = property.getName();
			if (!StateFactory.NAME_MATCHER.matcher(string).matches()) {
				throw new IllegalArgumentException(this.baseObject + " has invalidly named property: " + string);
			} else {
				Collection<T> collection = property.getValues();
				if (collection.size() <= 1) {
					throw new IllegalArgumentException(this.baseObject + " attempted use property " + string + " with <= 1 possible values");
				} else {
					for (T comparable : collection) {
						String string2 = property.getValueAsString(comparable);
						if (!StateFactory.NAME_MATCHER.matcher(string2).matches()) {
							throw new IllegalArgumentException(this.baseObject + " has property: " + string + " with invalidly named value: " + string2);
						}
					}

					if (this.propertyMap.containsKey(string)) {
						throw new IllegalArgumentException(this.baseObject + " has duplicate property: " + string);
					}
				}
			}
		}

		public <A extends AbstractPropertyContainer<O, S>> StateFactory<O, S> method_11668(StateFactory.Factory<O, S, A> factory) {
			return new StateFactory<>(this.baseObject, factory, this.propertyMap);
		}
	}

	public interface Factory<O, S extends PropertyContainer<S>, A extends AbstractPropertyContainer<O, S>> {
		A create(O object, ImmutableMap<Property<?>, Comparable<?>> immutableMap);
	}
}
