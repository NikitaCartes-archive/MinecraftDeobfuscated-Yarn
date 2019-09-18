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

public class StateManager<O, S extends State<S>> {
	private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
	private final O owner;
	private final ImmutableSortedMap<String, Property<?>> properties;
	private final ImmutableList<S> states;

	protected <A extends AbstractState<O, S>> StateManager(O object, StateManager.Factory<O, S, A> factory, Map<String, Property<?>> map) {
		this.owner = object;
		this.properties = ImmutableSortedMap.copyOf(map);
		Map<Map<Property<?>, Comparable<?>>, A> map2 = Maps.<Map<Property<?>, Comparable<?>>, A>newLinkedHashMap();
		List<A> list = Lists.<A>newArrayList();
		Stream<List<Comparable<?>>> stream = Stream.of(Collections.emptyList());

		for (Property<?> property : this.properties.values()) {
			stream = stream.flatMap(listx -> property.getValues().stream().map(comparable -> {
					List<Comparable<?>> list2 = Lists.<Comparable<?>>newArrayList(listx);
					list2.add(comparable);
					return list2;
				}));
		}

		stream.forEach(list2 -> {
			Map<Property<?>, Comparable<?>> map2x = MapUtil.createMap(this.properties.values(), list2);
			A abstractStatex = factory.create(object, ImmutableMap.copyOf(map2x));
			map2.put(map2x, abstractStatex);
			list.add(abstractStatex);
		});

		for (A abstractState : list) {
			abstractState.createWithTable(map2);
		}

		this.states = ImmutableList.copyOf(list);
	}

	public ImmutableList<S> getStates() {
		return this.states;
	}

	public S getDefaultState() {
		return (S)this.states.get(0);
	}

	public O getOwner() {
		return this.owner;
	}

	public Collection<Property<?>> getProperties() {
		return this.properties.values();
	}

	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("block", this.owner)
			.add("properties", this.properties.values().stream().map(Property::getName).collect(Collectors.toList()))
			.toString();
	}

	@Nullable
	public Property<?> getProperty(String string) {
		return this.properties.get(string);
	}

	public static class Builder<O, S extends State<S>> {
		private final O owner;
		private final Map<String, Property<?>> namedProperties = Maps.<String, Property<?>>newHashMap();

		public Builder(O object) {
			this.owner = object;
		}

		public StateManager.Builder<O, S> add(Property<?>... propertys) {
			for (Property<?> property : propertys) {
				this.validate(property);
				this.namedProperties.put(property.getName(), property);
			}

			return this;
		}

		private <T extends Comparable<T>> void validate(Property<T> property) {
			String string = property.getName();
			if (!StateManager.VALID_NAME_PATTERN.matcher(string).matches()) {
				throw new IllegalArgumentException(this.owner + " has invalidly named property: " + string);
			} else {
				Collection<T> collection = property.getValues();
				if (collection.size() <= 1) {
					throw new IllegalArgumentException(this.owner + " attempted use property " + string + " with <= 1 possible values");
				} else {
					for (T comparable : collection) {
						String string2 = property.name(comparable);
						if (!StateManager.VALID_NAME_PATTERN.matcher(string2).matches()) {
							throw new IllegalArgumentException(this.owner + " has property: " + string + " with invalidly named value: " + string2);
						}
					}

					if (this.namedProperties.containsKey(string)) {
						throw new IllegalArgumentException(this.owner + " has duplicate property: " + string);
					}
				}
			}
		}

		public <A extends AbstractState<O, S>> StateManager<O, S> build(StateManager.Factory<O, S, A> factory) {
			return new StateManager<>(this.owner, factory, this.namedProperties);
		}
	}

	public interface Factory<O, S extends State<S>, A extends AbstractState<O, S>> {
		A create(O object, ImmutableMap<Property<?>, Comparable<?>> immutableMap);
	}
}
