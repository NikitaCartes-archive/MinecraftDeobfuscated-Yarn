package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
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
	private Table<Property<?>, Comparable<?>, S> withTable;
	protected final MapCodec<S> codec;

	protected State(O owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<S> codec) {
		this.owner = owner;
		this.propertyMap = propertyMap;
		this.codec = codec;
	}

	public <T extends Comparable<T>> S cycle(Property<T> property) {
		return this.with(property, getNext(property.getValues(), this.get(property)));
	}

	protected static <T> T getNext(Collection<T> values, T value) {
		Iterator<T> iterator = values.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().equals(value)) {
				if (iterator.hasNext()) {
					return (T)iterator.next();
				}

				return (T)values.iterator().next();
			}
		}

		return (T)iterator.next();
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
		Comparable<?> comparable = this.propertyMap.get(property);
		return comparable == null ? Optional.empty() : Optional.of((Comparable)property.getType().cast(comparable));
	}

	public <T extends Comparable<T>, V extends T> S with(Property<T> property, V value) {
		Comparable<?> comparable = this.propertyMap.get(property);
		if (comparable == null) {
			throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.owner);
		} else if (comparable.equals(value)) {
			return (S)this;
		} else {
			S object = this.withTable.get(property, value);
			if (object == null) {
				throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on " + this.owner + ", it is not an allowed value");
			} else {
				return object;
			}
		}
	}

	public <T extends Comparable<T>, V extends T> S withIfExists(Property<T> property, V value) {
		Comparable<?> comparable = this.propertyMap.get(property);
		if (comparable != null && !comparable.equals(value)) {
			S object = this.withTable.get(property, value);
			if (object == null) {
				throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on " + this.owner + ", it is not an allowed value");
			} else {
				return object;
			}
		} else {
			return (S)this;
		}
	}

	public void createWithTable(Map<Map<Property<?>, Comparable<?>>, S> states) {
		if (this.withTable != null) {
			throw new IllegalStateException();
		} else {
			Table<Property<?>, Comparable<?>, S> table = HashBasedTable.create();

			for (Entry<Property<?>, Comparable<?>> entry : this.propertyMap.entrySet()) {
				Property<?> property = (Property<?>)entry.getKey();

				for (Comparable<?> comparable : property.getValues()) {
					if (!comparable.equals(entry.getValue())) {
						table.put(property, comparable, (S)states.get(this.toMapWith(property, comparable)));
					}
				}
			}

			this.withTable = (Table<Property<?>, Comparable<?>, S>)(table.isEmpty() ? table : ArrayTable.create(table));
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
					: state.codec.codec().lenientOptionalFieldOf("Properties").xmap(optional -> (State)optional.orElse(state), Optional::of);
			}
		);
	}
}
