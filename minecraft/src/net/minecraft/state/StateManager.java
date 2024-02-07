package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.state.property.Property;

public class StateManager<O, S extends State<O, S>> {
	static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
	private final O owner;
	private final ImmutableSortedMap<String, Property<?>> properties;
	private final ImmutableList<S> states;

	protected StateManager(Function<O, S> defaultStateGetter, O owner, StateManager.Factory<O, S> factory, Map<String, Property<?>> propertiesMap) {
		this.owner = owner;
		this.properties = ImmutableSortedMap.copyOf(propertiesMap);
		Supplier<S> supplier = () -> (State)defaultStateGetter.apply(owner);
		MapCodec<S> mapCodec = MapCodec.of(Encoder.empty(), Decoder.unit(supplier));

		for (Entry<String, Property<?>> entry : this.properties.entrySet()) {
			mapCodec = addFieldToMapCodec(mapCodec, supplier, (String)entry.getKey(), (Property)entry.getValue());
		}

		MapCodec<S> mapCodec2 = mapCodec;
		Map<Map<Property<?>, Comparable<?>>, S> map = Maps.<Map<Property<?>, Comparable<?>>, S>newLinkedHashMap();
		List<S> list = Lists.<S>newArrayList();
		Stream<List<Pair<Property<?>, Comparable<?>>>> stream = Stream.of(Collections.emptyList());

		for (Property<?> property : this.properties.values()) {
			stream = stream.flatMap(listx -> property.getValues().stream().map(comparable -> {
					List<Pair<Property<?>, Comparable<?>>> list2 = Lists.<Pair<Property<?>, Comparable<?>>>newArrayList(listx);
					list2.add(Pair.of(property, comparable));
					return list2;
				}));
		}

		stream.forEach(list2 -> {
			Reference2ObjectArrayMap<Property<?>, Comparable<?>> reference2ObjectArrayMap = new Reference2ObjectArrayMap<>(list2.size());

			for (Pair<Property<?>, Comparable<?>> pair : list2) {
				reference2ObjectArrayMap.put(pair.getFirst(), pair.getSecond());
			}

			S statex = factory.create(owner, reference2ObjectArrayMap, mapCodec2);
			map.put(reference2ObjectArrayMap, statex);
			list.add(statex);
		});

		for (S state : list) {
			state.createWithTable(map);
		}

		this.states = ImmutableList.copyOf(list);
	}

	private static <S extends State<?, S>, T extends Comparable<T>> MapCodec<S> addFieldToMapCodec(
		MapCodec<S> mapCodec, Supplier<S> defaultStateGetter, String key, Property<T> property
	) {
		return Codec.mapPair(mapCodec, property.getValueCodec().fieldOf(key).orElseGet((Consumer<String>)(string -> {
			}), () -> property.createValue((State<?, ?>)defaultStateGetter.get())))
			.xmap(
				pair -> (State)((State)pair.getFirst()).with(property, ((Property.Value)pair.getSecond()).value()), state -> Pair.of(state, property.createValue(state))
			);
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
	public Property<?> getProperty(String name) {
		return this.properties.get(name);
	}

	public static class Builder<O, S extends State<O, S>> {
		private final O owner;
		private final Map<String, Property<?>> namedProperties = Maps.<String, Property<?>>newHashMap();

		public Builder(O owner) {
			this.owner = owner;
		}

		public StateManager.Builder<O, S> add(Property<?>... properties) {
			for (Property<?> property : properties) {
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

		public StateManager<O, S> build(Function<O, S> defaultStateGetter, StateManager.Factory<O, S> factory) {
			return new StateManager<>(defaultStateGetter, this.owner, factory, this.namedProperties);
		}
	}

	public interface Factory<O, S> {
		S create(O owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<S> codec);
	}
}
