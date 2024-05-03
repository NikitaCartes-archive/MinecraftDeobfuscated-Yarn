package net.minecraft.component;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
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
		public <T> T get(ComponentType<? extends T> type) {
			return null;
		}

		@Override
		public Set<ComponentType<?>> getTypes() {
			return Set.of();
		}

		@Override
		public Iterator<Component<?>> iterator() {
			return Collections.emptyIterator();
		}
	};
	Codec<ComponentMap> CODEC = createCodecFromValueMap(ComponentType.TYPE_TO_VALUE_MAP_CODEC);

	static Codec<ComponentMap> createCodec(Codec<ComponentType<?>> componentTypeCodec) {
		return createCodecFromValueMap(Codec.dispatchedMap(componentTypeCodec, ComponentType::getCodecOrThrow));
	}

	static Codec<ComponentMap> createCodecFromValueMap(Codec<Map<ComponentType<?>, Object>> typeToValueMapCodec) {
		return typeToValueMapCodec.flatComapMap(ComponentMap.Builder::build, componentMap -> {
			int i = componentMap.size();
			if (i == 0) {
				return DataResult.success(Reference2ObjectMaps.emptyMap());
			} else {
				Reference2ObjectMap<ComponentType<?>, Object> reference2ObjectMap = new Reference2ObjectArrayMap<>(i);

				for (Component<?> component : componentMap) {
					if (!component.type().shouldSkipSerialization()) {
						reference2ObjectMap.put(component.type(), component.value());
					}
				}

				return DataResult.success(reference2ObjectMap);
			}
		});
	}

	static ComponentMap of(ComponentMap base, ComponentMap overrides) {
		return new ComponentMap() {
			@Nullable
			@Override
			public <T> T get(ComponentType<? extends T> type) {
				T object = overrides.get(type);
				return object != null ? object : base.get(type);
			}

			@Override
			public Set<ComponentType<?>> getTypes() {
				return Sets.<ComponentType<?>>union(base.getTypes(), overrides.getTypes());
			}
		};
	}

	static ComponentMap.Builder builder() {
		return new ComponentMap.Builder();
	}

	@Nullable
	<T> T get(ComponentType<? extends T> type);

	Set<ComponentType<?>> getTypes();

	default boolean contains(ComponentType<?> type) {
		return this.get(type) != null;
	}

	default <T> T getOrDefault(ComponentType<? extends T> type, T fallback) {
		T object = this.get(type);
		return object != null ? object : fallback;
	}

	@Nullable
	default <T> Component<T> copy(ComponentType<T> type) {
		T object = this.get(type);
		return object != null ? new Component<>(type, object) : null;
	}

	default Iterator<Component<?>> iterator() {
		return Iterators.transform(this.getTypes().iterator(), type -> (Component<?>)Objects.requireNonNull(this.copy(type)));
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

	default ComponentMap filtered(Predicate<ComponentType<?>> predicate) {
		return new ComponentMap() {
			@Nullable
			@Override
			public <T> T get(ComponentType<? extends T> type) {
				return predicate.test(type) ? ComponentMap.this.get(type) : null;
			}

			@Override
			public Set<ComponentType<?>> getTypes() {
				return Sets.filter(ComponentMap.this.getTypes(), predicate::test);
			}
		};
	}

	public static class Builder {
		private final Reference2ObjectMap<ComponentType<?>, Object> components = new Reference2ObjectArrayMap<>();

		Builder() {
		}

		public <T> ComponentMap.Builder add(ComponentType<T> type, @Nullable T value) {
			this.put(type, value);
			return this;
		}

		<T> void put(ComponentType<T> type, @Nullable Object value) {
			if (value != null) {
				this.components.put(type, value);
			} else {
				this.components.remove(type);
			}
		}

		public ComponentMap.Builder addAll(ComponentMap componentSet) {
			for (Component<?> component : componentSet) {
				this.components.put(component.type(), component.value());
			}

			return this;
		}

		public ComponentMap build() {
			return build(this.components);
		}

		private static ComponentMap build(Map<ComponentType<?>, Object> components) {
			if (components.isEmpty()) {
				return ComponentMap.EMPTY;
			} else {
				return components.size() < 8
					? new ComponentMap.Builder.SimpleComponentMap(new Reference2ObjectArrayMap<>(components))
					: new ComponentMap.Builder.SimpleComponentMap(new Reference2ObjectOpenHashMap<>(components));
			}
		}

		static record SimpleComponentMap(Reference2ObjectMap<ComponentType<?>, Object> map) implements ComponentMap {
			@Nullable
			@Override
			public <T> T get(ComponentType<? extends T> type) {
				return (T)this.map.get(type);
			}

			@Override
			public boolean contains(ComponentType<?> type) {
				return this.map.containsKey(type);
			}

			@Override
			public Set<ComponentType<?>> getTypes() {
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
