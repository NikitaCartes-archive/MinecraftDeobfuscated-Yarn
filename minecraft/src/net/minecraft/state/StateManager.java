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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.state.property.Property;
import org.apache.commons.lang3.mutable.MutableObject;

public class StateManager<O, S extends State<O, S>> {
	private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
	private final O owner;
	private final ImmutableSortedMap<String, Property<?>> properties;
	private final ImmutableList<S> states;

	protected StateManager(Function<O, S> ownerToStateFunction, O owner, StateManager.Factory<O, S> factory, Map<String, Property<?>> propertiesMap) {
		this.owner = owner;
		this.properties = ImmutableSortedMap.copyOf(propertiesMap);
		MapCodec<S> mapCodec = new StateManager.PropertiesCodec<>(this.properties, () -> (State)ownerToStateFunction.apply(owner));
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

		stream.forEach(
			list2 -> {
				ImmutableMap<Property<?>, Comparable<?>> immutableMap = (ImmutableMap<Property<?>, Comparable<?>>)list2.stream()
					.collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
				S statex = factory.create(owner, immutableMap, mapCodec);
				map.put(immutableMap, statex);
				list.add(statex);
			}
		);

		for (S state : list) {
			state.createWithTable(map);
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

		public StateManager<O, S> build(Function<O, S> ownerToStateFunction, StateManager.Factory<O, S> factory) {
			return new StateManager<>(ownerToStateFunction, this.owner, factory, this.namedProperties);
		}
	}

	public interface Factory<O, S> {
		S create(O owner, ImmutableMap<Property<?>, Comparable<?>> entries, MapCodec<S> mapCodec);
	}

	static class PropertiesCodec<S extends State<?, S>> extends MapCodec<S> {
		private final Map<String, Property<?>> field_24735;
		private final Supplier<S> field_24736;

		public PropertiesCodec(Map<String, Property<?>> map, Supplier<S> supplier) {
			this.field_24735 = map;
			this.field_24736 = supplier;
		}

		public <T> RecordBuilder<T> encode(S state, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
			state.getEntries().forEach((property, comparable) -> recordBuilder.add(property.getName(), dynamicOps.createString(method_28487(property, comparable))));
			return recordBuilder;
		}

		@Override
		public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
			return this.field_24735.keySet().stream().map(dynamicOps::createString);
		}

		@Override
		public <T> DataResult<S> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
			MutableObject<DataResult<S>> mutableObject = new MutableObject<>(DataResult.success((S)this.field_24736.get()));
			mapLike.entries().forEach(pair -> {
				DataResult<Property<?>> dataResult = dynamicOps.getStringValue((T)pair.getFirst()).map(this.field_24735::get);
				T object = (T)pair.getSecond();
				mutableObject.setValue(mutableObject.getValue().flatMap(state -> dataResult.flatMap(property -> property.method_28503(dynamicOps, (S)state, object))));
			});
			return mutableObject.getValue();
		}

		private static <T extends Comparable<T>> String method_28487(Property<T> property, Comparable<?> comparable) {
			return property.name((T)comparable);
		}

		public String toString() {
			return "PropertiesCodec";
		}
	}
}
