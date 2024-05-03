package net.minecraft.component;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

public final class ComponentChanges {
	public static final ComponentChanges EMPTY = new ComponentChanges(Reference2ObjectMaps.emptyMap());
	public static final Codec<ComponentChanges> CODEC = Codec.dispatchedMap(ComponentChanges.Type.CODEC, ComponentChanges.Type::getValueCodec).xmap(changes -> {
		if (changes.isEmpty()) {
			return EMPTY;
		} else {
			Reference2ObjectMap<ComponentType<?>, Optional<?>> reference2ObjectMap = new Reference2ObjectArrayMap<>(changes.size());

			for (Entry<ComponentChanges.Type, ?> entry : changes.entrySet()) {
				ComponentChanges.Type type = (ComponentChanges.Type)entry.getKey();
				if (type.removed()) {
					reference2ObjectMap.put(type.type(), Optional.empty());
				} else {
					reference2ObjectMap.put(type.type(), Optional.of(entry.getValue()));
				}
			}

			return new ComponentChanges(reference2ObjectMap);
		}
	}, changes -> {
		Reference2ObjectMap<ComponentChanges.Type, Object> reference2ObjectMap = new Reference2ObjectArrayMap<>(changes.changedComponents.size());

		for (Entry<ComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(changes.changedComponents)) {
			ComponentType<?> componentType = (ComponentType<?>)entry.getKey();
			if (!componentType.shouldSkipSerialization()) {
				Optional<?> optional = (Optional<?>)entry.getValue();
				if (optional.isPresent()) {
					reference2ObjectMap.put(new ComponentChanges.Type(componentType, false), optional.get());
				} else {
					reference2ObjectMap.put(new ComponentChanges.Type(componentType, true), Unit.INSTANCE);
				}
			}
		}

		return reference2ObjectMap;
	});
	public static final PacketCodec<RegistryByteBuf, ComponentChanges> PACKET_CODEC = new PacketCodec<RegistryByteBuf, ComponentChanges>() {
		public ComponentChanges decode(RegistryByteBuf registryByteBuf) {
			int i = registryByteBuf.readVarInt();
			int j = registryByteBuf.readVarInt();
			if (i == 0 && j == 0) {
				return ComponentChanges.EMPTY;
			} else {
				Reference2ObjectMap<ComponentType<?>, Optional<?>> reference2ObjectMap = new Reference2ObjectArrayMap<>(i + j);

				for (int k = 0; k < i; k++) {
					ComponentType<?> componentType = ComponentType.PACKET_CODEC.decode(registryByteBuf);
					Object object = componentType.getPacketCodec().decode(registryByteBuf);
					reference2ObjectMap.put(componentType, Optional.of(object));
				}

				for (int k = 0; k < j; k++) {
					ComponentType<?> componentType = ComponentType.PACKET_CODEC.decode(registryByteBuf);
					reference2ObjectMap.put(componentType, Optional.empty());
				}

				return new ComponentChanges(reference2ObjectMap);
			}
		}

		public void encode(RegistryByteBuf registryByteBuf, ComponentChanges componentChanges) {
			if (componentChanges.isEmpty()) {
				registryByteBuf.writeVarInt(0);
				registryByteBuf.writeVarInt(0);
			} else {
				int i = 0;
				int j = 0;

				for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<ComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(
					componentChanges.changedComponents
				)) {
					if (((Optional)entry.getValue()).isPresent()) {
						i++;
					} else {
						j++;
					}
				}

				registryByteBuf.writeVarInt(i);
				registryByteBuf.writeVarInt(j);

				for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<ComponentType<?>, Optional<?>> entryx : Reference2ObjectMaps.fastIterable(
					componentChanges.changedComponents
				)) {
					Optional<?> optional = (Optional<?>)entryx.getValue();
					if (optional.isPresent()) {
						ComponentType<?> componentType = (ComponentType<?>)entryx.getKey();
						ComponentType.PACKET_CODEC.encode(registryByteBuf, componentType);
						encode(registryByteBuf, componentType, optional.get());
					}
				}

				for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<ComponentType<?>, Optional<?>> entryxx : Reference2ObjectMaps.fastIterable(
					componentChanges.changedComponents
				)) {
					if (((Optional)entryxx.getValue()).isEmpty()) {
						ComponentType<?> componentType2 = (ComponentType<?>)entryxx.getKey();
						ComponentType.PACKET_CODEC.encode(registryByteBuf, componentType2);
					}
				}
			}
		}

		private static <T> void encode(RegistryByteBuf buf, ComponentType<T> type, Object value) {
			type.getPacketCodec().encode(buf, (T)value);
		}
	};
	private static final String REMOVE_PREFIX = "!";
	final Reference2ObjectMap<ComponentType<?>, Optional<?>> changedComponents;

	ComponentChanges(Reference2ObjectMap<ComponentType<?>, Optional<?>> changedComponents) {
		this.changedComponents = changedComponents;
	}

	public static ComponentChanges.Builder builder() {
		return new ComponentChanges.Builder();
	}

	@Nullable
	public <T> Optional<? extends T> get(ComponentType<? extends T> type) {
		return (Optional<? extends T>)this.changedComponents.get(type);
	}

	public Set<Entry<ComponentType<?>, Optional<?>>> entrySet() {
		return this.changedComponents.entrySet();
	}

	public int size() {
		return this.changedComponents.size();
	}

	public ComponentChanges withRemovedIf(Predicate<ComponentType<?>> removedTypePredicate) {
		if (this.isEmpty()) {
			return EMPTY;
		} else {
			Reference2ObjectMap<ComponentType<?>, Optional<?>> reference2ObjectMap = new Reference2ObjectArrayMap<>(this.changedComponents);
			reference2ObjectMap.keySet().removeIf(removedTypePredicate);
			return reference2ObjectMap.isEmpty() ? EMPTY : new ComponentChanges(reference2ObjectMap);
		}
	}

	public boolean isEmpty() {
		return this.changedComponents.isEmpty();
	}

	public ComponentChanges.AddedRemovedPair toAddedRemovedPair() {
		if (this.isEmpty()) {
			return ComponentChanges.AddedRemovedPair.EMPTY;
		} else {
			ComponentMap.Builder builder = ComponentMap.builder();
			Set<ComponentType<?>> set = Sets.newIdentityHashSet();
			this.changedComponents.forEach((type, value) -> {
				if (value.isPresent()) {
					builder.put(type, value.get());
				} else {
					set.add(type);
				}
			});
			return new ComponentChanges.AddedRemovedPair(builder.build(), set);
		}
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof ComponentChanges componentChanges && this.changedComponents.equals(componentChanges.changedComponents)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.changedComponents.hashCode();
	}

	public String toString() {
		return toString(this.changedComponents);
	}

	static String toString(Reference2ObjectMap<ComponentType<?>, Optional<?>> changes) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append('{');
		boolean bl = true;

		for (Entry<ComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(changes)) {
			if (bl) {
				bl = false;
			} else {
				stringBuilder.append(", ");
			}

			Optional<?> optional = (Optional<?>)entry.getValue();
			if (optional.isPresent()) {
				stringBuilder.append(entry.getKey());
				stringBuilder.append("=>");
				stringBuilder.append(optional.get());
			} else {
				stringBuilder.append("!");
				stringBuilder.append(entry.getKey());
			}
		}

		stringBuilder.append('}');
		return stringBuilder.toString();
	}

	public static record AddedRemovedPair(ComponentMap added, Set<ComponentType<?>> removed) {
		public static final ComponentChanges.AddedRemovedPair EMPTY = new ComponentChanges.AddedRemovedPair(ComponentMap.EMPTY, Set.of());
	}

	public static class Builder {
		private final Reference2ObjectMap<ComponentType<?>, Optional<?>> changes = new Reference2ObjectArrayMap<>();

		Builder() {
		}

		public <T> ComponentChanges.Builder add(ComponentType<T> type, T value) {
			this.changes.put(type, Optional.of(value));
			return this;
		}

		public <T> ComponentChanges.Builder remove(ComponentType<T> type) {
			this.changes.put(type, Optional.empty());
			return this;
		}

		public <T> ComponentChanges.Builder add(Component<T> component) {
			return this.add(component.type(), component.value());
		}

		public ComponentChanges build() {
			return this.changes.isEmpty() ? ComponentChanges.EMPTY : new ComponentChanges(this.changes);
		}
	}

	static record Type(ComponentType<?> type, boolean removed) {
		public static final Codec<ComponentChanges.Type> CODEC = Codec.STRING
			.flatXmap(
				id -> {
					boolean bl = id.startsWith("!");
					if (bl) {
						id = id.substring("!".length());
					}

					Identifier identifier = Identifier.tryParse(id);
					ComponentType<?> componentType = Registries.DATA_COMPONENT_TYPE.get(identifier);
					if (componentType == null) {
						return DataResult.error(() -> "No component with type: '" + identifier + "'");
					} else {
						return componentType.shouldSkipSerialization()
							? DataResult.error(() -> "'" + identifier + "' is not a persistent component")
							: DataResult.success(new ComponentChanges.Type(componentType, bl));
					}
				},
				type -> {
					ComponentType<?> componentType = type.type();
					Identifier identifier = Registries.DATA_COMPONENT_TYPE.getId(componentType);
					return identifier == null
						? DataResult.error(() -> "Unregistered component: " + componentType)
						: DataResult.success(type.removed() ? "!" + identifier : identifier.toString());
				}
			);

		public Codec<?> getValueCodec() {
			return this.removed ? Codec.EMPTY.codec() : this.type.getCodecOrThrow();
		}
	}
}
