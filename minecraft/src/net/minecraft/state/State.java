package net.minecraft.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.state.property.Property;

public abstract class State<O, S> {
	public static final String NAME = "Name";
	public static final String PROPERTIES = "Properties";
	private static final Function<Entry<Property<?>, Comparable<?>>, String> PROPERTY_MAP_PRINTER = new Function<Entry<Property<?>, Comparable<?>>, String>() {
		public String apply(@Nullable Entry<Property<?>, Comparable<?>> entry) {
			if (entry == null) {
				return "<NULL>";
			} else {
				Property<?> property = (Property<?>)entry.getKey();
				return property.getName() + "=" + this.nameValue(property, (Comparable<?>)entry.getValue());
			}
		}

		private <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
			return property.name((T)value);
		}
	};
	protected final O owner;
	private final Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap;
	private Map<Property<?>, S[]> withMap;
	protected final MapCodec<S> codec;

	protected State(O owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<S> codec) {
		this.owner = owner;
		this.propertyMap = propertyMap;
		this.codec = codec;
	}

	public <T extends Comparable<T>> S cycle(Property<T> property) {
		return this.with(property, getNext(property.getValues(), this.get(property)));
	}

	protected static <T> T getNext(List<T> values, T value) {
		int i = values.indexOf(value) + 1;
		return (T)(i == values.size() ? values.getFirst() : values.get(i));
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.owner);
		if (!this.getEntries().isEmpty()) {
			stringBuilder.append('[');
			stringBuilder.append((String)this.getEntries().entrySet().stream().map(PROPERTY_MAP_PRINTER).collect(Collectors.joining(",")));
			stringBuilder.append(']');
		}

		return stringBuilder.toString();
	}

	public Collection<Property<?>> getProperties() {
		return Collections.unmodifiableCollection(this.propertyMap.keySet());
	}

	public <T extends Comparable<T>> boolean contains(Property<T> property) {
		return this.propertyMap.containsKey(property);
	}

	public <T extends Comparable<T>> T get(Property<T> property) {
		Comparable<?> comparable = this.propertyMap.get(property);
		if (comparable == null) {
			throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.owner);
		} else {
			return (T)property.getType().cast(comparable);
		}
	}

	public <T extends Comparable<T>> Optional<T> getOrEmpty(Property<T> property) {
		return Optional.ofNullable(this.getNullable(property));
	}

	public <T extends Comparable<T>> T get(Property<T> property, T fallback) {
		return (T)Objects.requireNonNullElse(this.getNullable(property), fallback);
	}

	@Nullable
	public <T extends Comparable<T>> T getNullable(Property<T> property) {
		Comparable<?> comparable = this.propertyMap.get(property);
		return (T)(comparable == null ? null : property.getType().cast(comparable));
	}

	public <T extends Comparable<T>, V extends T> S with(Property<T> property, V value) {
		Comparable<?> comparable = this.propertyMap.get(property);
		if (comparable == null) {
			throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.owner);
		} else {
			return this.with(property, value, comparable);
		}
	}

	public <T extends Comparable<T>, V extends T> S withIfExists(Property<T> property, V value) {
		Comparable<?> comparable = this.propertyMap.get(property);
		return (S)(comparable == null ? this : this.with(property, value, comparable));
	}

	private <T extends Comparable<T>, V extends T> S with(Property<T> property, V newValue, Comparable<?> oldValue) {
		if (oldValue.equals(newValue)) {
			return (S)this;
		} else {
			int i = property.ordinal((T)newValue);
			if (i < 0) {
				throw new IllegalArgumentException("Cannot set property " + property + " to " + newValue + " on " + this.owner + ", it is not an allowed value");
			} else {
				return (S)this.withMap.get(property)[i];
			}
		}
	}

	public void createWithMap(Map<Map<Property<?>, Comparable<?>>, S> states) {
		if (this.withMap != null) {
			throw new IllegalStateException();
		} else {
			Map<Property<?>, S[]> map = new Reference2ObjectArrayMap<>(this.propertyMap.size());

			for (Entry<Property<?>, Comparable<?>> entry : this.propertyMap.entrySet()) {
				Property<?> property = (Property<?>)entry.getKey();
				map.put(property, property.getValues().stream().map(value -> states.get(this.toMapWith(property, value))).toArray());
			}

			this.withMap = map;
		}
	}

	private Map<Property<?>, Comparable<?>> toMapWith(Property<?> property, Comparable<?> value) {
		Map<Property<?>, Comparable<?>> map = new Reference2ObjectArrayMap<>(this.propertyMap);
		map.put(property, value);
		return map;
	}

	public Map<Property<?>, Comparable<?>> getEntries() {
		return this.propertyMap;
	}

	protected static <O, S extends State<O, S>> Codec<S> createCodec(Codec<O> codec, Function<O, S> ownerToStateFunction) {
		return codec.dispatch(
			"Name",
			state -> state.owner,
			owner -> {
				S state = (S)ownerToStateFunction.apply(owner);
				return state.getEntries().isEmpty()
					? MapCodec.unit(state)
					: state.codec.codec().lenientOptionalFieldOf("Properties").xmap(statex -> (State)statex.orElse(state), Optional::of);
			}
		);
	}
}
