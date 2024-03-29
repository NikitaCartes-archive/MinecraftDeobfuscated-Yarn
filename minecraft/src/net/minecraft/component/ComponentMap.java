package net.minecraft.component;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public interface ComponentMap extends Iterable<Component<?>> {
	ComponentMap EMPTY = new ComponentMap() {
		@Nullable
		@Override
		public <T> T get(DataComponentType<? extends T> type) {
			return null;
		}

		@Override
		public Set<DataComponentType<?>> getTypes() {
			return Set.of();
		}

		@Override
		public Iterator<Component<?>> iterator() {
			return Collections.emptyIterator();
		}
	};

	static ComponentMap.Builder builder() {
		return new ComponentMap.Builder();
	}

	@Nullable
	<T> T get(DataComponentType<? extends T> type);

	Set<DataComponentType<?>> getTypes();

	default boolean contains(DataComponentType<?> type) {
		return this.get(type) != null;
	}

	default <T> T getOrDefault(DataComponentType<? extends T> type, T fallback) {
		T object = this.get(type);
		return (T)(object != null ? object : fallback);
	}

	@Nullable
	default <T> Component<T> copy(DataComponentType<T> type) {
		T object = this.get(type);
		return object != null ? new Component<>(type, object) : null;
	}

	default Iterator<Component<?>> iterator() {
		return Iterators.transform(this.getTypes().iterator(), type -> (Component)Objects.requireNonNull(this.copy(type)));
	}

	default Stream<Component<?>> stream() {
		return StreamSupport.stream(Spliterators.spliterator(this.iterator(), (long)this.size(), 1345), false);
	}

	default int size() {
		return this.getTypes().size();
	}

	default boolean isEmpty() {
		return this.size() == 0;
	}

	default ComponentMap filtered(Predicate<DataComponentType<?>> predicate) {
		return new ComponentMap() {
			@Nullable
			@Override
			public <T> T get(DataComponentType<? extends T> type) {
				return predicate.test(type) ? ComponentMap.this.get(type) : null;
			}

			@Override
			public Set<DataComponentType<?>> getTypes() {
				return Sets.filter(ComponentMap.this.getTypes(), predicate::test);
			}
		};
	}

	public static class Builder {
		private final Reference2ObjectMap<DataComponentType<?>, Object> components = new Reference2ObjectArrayMap<>();

		Builder() {
		}

		public <T> ComponentMap.Builder add(DataComponentType<T> type, @Nullable T component) {
			if (component != null) {
				this.components.put(type, component);
			} else {
				this.components.remove(type);
			}

			return this;
		}

		public ComponentMap.Builder addAll(ComponentMap componentSet) {
			for(Component<?> component : componentSet) {
				this.components.put(component.type(), component.value());
			}

			return this;
		}

		public ComponentMap build() {
			if (this.components.isEmpty()) {
				return ComponentMap.EMPTY;
			} else {
				return this.components.size() < 8
					? new ComponentMap.Builder.SimpleComponentMap(new Reference2ObjectArrayMap<>(this.components))
					: new ComponentMap.Builder.SimpleComponentMap(new Reference2ObjectOpenHashMap<>(this.components));
			}
		}

		static record SimpleComponentMap(Reference2ObjectMap<DataComponentType<?>, Object> map) implements ComponentMap {
			@Nullable
			@Override
			public <T> T get(DataComponentType<? extends T> type) {
				return (T)this.map.get(type);
			}

			@Override
			public boolean contains(DataComponentType<?> type) {
				return this.map.containsKey(type);
			}

			@Override
			public Set<DataComponentType<?>> getTypes() {
				return this.map.keySet();
			}

			@Override
			public Iterator<Component<?>> iterator() {
				return Iterators.transform(Reference2ObjectMaps.fastIterator(this.map), Component::of);
			}

			@Override
			public int size() {
				return this.map.size();
			}

			public String toString() {
				return this.map.toString();
			}
		}
	}
}
