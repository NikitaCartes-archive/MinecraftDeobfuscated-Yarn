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

public interface DataComponentType<T> {
	Codec<DataComponentType<?>> CODEC = Codec.lazyInitialized(() -> Registries.DATA_COMPONENT_TYPE.getCodec());
	PacketCodec<RegistryByteBuf, DataComponentType<?>> PACKET_CODEC = PacketCodec.recursive(
		packetCodec -> PacketCodecs.registryValue(RegistryKeys.DATA_COMPONENT_TYPE)
	);
	Codec<DataComponentType<?>> PERSISTENT_CODEC = CODEC.validate(
		componentType -> componentType.shouldSkipSerialization()
				? DataResult.error(() -> "Encountered transient component " + Registries.DATA_COMPONENT_TYPE.getId(componentType))
				: DataResult.success(componentType)
	);
	Codec<Map<DataComponentType<?>, Object>> TYPE_TO_VALUE_MAP_CODEC = Codec.dispatchedMap(PERSISTENT_CODEC, DataComponentType::getCodecOrThrow);

	static <T> DataComponentType.Builder<T> builder() {
		return new DataComponentType.Builder<>();
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

		public DataComponentType.Builder<T> codec(Codec<T> codec) {
			this.codec = codec;
			return this;
		}

		public DataComponentType.Builder<T> packetCodec(PacketCodec<? super RegistryByteBuf, T> packetCodec) {
			this.packetCodec = packetCodec;
			return this;
		}

		public DataComponentType<T> build() {
			PacketCodec<? super RegistryByteBuf, T> packetCodec = (PacketCodec<? super RegistryByteBuf, T>)Objects.requireNonNullElseGet(
				this.packetCodec, () -> PacketCodecs.registryCodec((Codec<T>)Objects.requireNonNull(this.codec, "Missing Codec for component"))
			);
			return new DataComponentType.Builder.SimpleDataComponentType<>(this.codec, packetCodec);
		}

		static class SimpleDataComponentType<T> implements DataComponentType<T> {
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
				return Util.registryValueToString((Registry<DataComponentType.Builder.SimpleDataComponentType<T>>)Registries.DATA_COMPONENT_TYPE, this);
			}
		}
	}
}
