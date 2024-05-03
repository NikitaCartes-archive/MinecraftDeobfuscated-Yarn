package net.minecraft.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Util;

public interface ComponentType<T> {
	Codec<ComponentType<?>> CODEC = Codec.lazyInitialized(() -> Registries.DATA_COMPONENT_TYPE.getCodec());
	PacketCodec<RegistryByteBuf, ComponentType<?>> PACKET_CODEC = PacketCodec.recursive(
		packetCodec -> PacketCodecs.registryValue(RegistryKeys.DATA_COMPONENT_TYPE)
	);
	Codec<ComponentType<?>> PERSISTENT_CODEC = CODEC.validate(
		componentType -> componentType.shouldSkipSerialization()
				? DataResult.error(() -> "Encountered transient component " + Registries.DATA_COMPONENT_TYPE.getId(componentType))
				: DataResult.success(componentType)
	);
	Codec<Map<ComponentType<?>, Object>> TYPE_TO_VALUE_MAP_CODEC = Codec.dispatchedMap(PERSISTENT_CODEC, ComponentType::getCodecOrThrow);

	static <T> ComponentType.Builder<T> builder() {
		return new ComponentType.Builder<>();
	}

	@Nullable
	Codec<T> getCodec();

	default Codec<T> getCodecOrThrow() {
		Codec<T> codec = this.getCodec();
		if (codec == null) {
			throw new IllegalStateException(this + " is not a persistent component");
		} else {
			return codec;
		}
	}

	default boolean shouldSkipSerialization() {
		return this.getCodec() == null;
	}

	PacketCodec<? super RegistryByteBuf, T> getPacketCodec();

	public static class Builder<T> {
		@Nullable
		private Codec<T> codec;
		@Nullable
		private PacketCodec<? super RegistryByteBuf, T> packetCodec;
		private boolean cache;

		public ComponentType.Builder<T> codec(Codec<T> codec) {
			this.codec = codec;
			return this;
		}

		public ComponentType.Builder<T> packetCodec(PacketCodec<? super RegistryByteBuf, T> packetCodec) {
			this.packetCodec = packetCodec;
			return this;
		}

		public ComponentType.Builder<T> cache() {
			this.cache = true;
			return this;
		}

		public ComponentType<T> build() {
			PacketCodec<? super RegistryByteBuf, T> packetCodec = (PacketCodec<? super RegistryByteBuf, T>)Objects.requireNonNullElseGet(
				this.packetCodec, () -> PacketCodecs.registryCodec((Codec<T>)Objects.requireNonNull(this.codec, "Missing Codec for component"))
			);
			Codec<T> codec = this.cache && this.codec != null ? DataComponentTypes.CACHE.wrap(this.codec) : this.codec;
			return new ComponentType.Builder.SimpleDataComponentType<>(codec, packetCodec);
		}

		static class SimpleDataComponentType<T> implements ComponentType<T> {
			@Nullable
			private final Codec<T> codec;
			private final PacketCodec<? super RegistryByteBuf, T> packetCodec;

			SimpleDataComponentType(@Nullable Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec) {
				this.codec = codec;
				this.packetCodec = packetCodec;
			}

			@Nullable
			@Override
			public Codec<T> getCodec() {
				return this.codec;
			}

			@Override
			public PacketCodec<? super RegistryByteBuf, T> getPacketCodec() {
				return this.packetCodec;
			}

			public String toString() {
				return Util.registryValueToString((Registry<ComponentType.Builder.SimpleDataComponentType<T>>)Registries.DATA_COMPONENT_TYPE, this);
			}
		}
	}
}
