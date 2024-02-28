package net.minecraft.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map.Entry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record Component<T>(DataComponentType<T> type, T value) {
	public static final PacketCodec<RegistryByteBuf, Component<?>> PACKET_CODEC = new PacketCodec<RegistryByteBuf, Component<?>>() {
		public Component<?> decode(RegistryByteBuf registryByteBuf) {
			DataComponentType<?> dataComponentType = DataComponentType.PACKET_CODEC.decode(registryByteBuf);
			return read(registryByteBuf, (DataComponentType<T>)dataComponentType);
		}

		private static <T> Component<T> read(RegistryByteBuf buf, DataComponentType<T> type) {
			return new Component<>(type, type.getPacketCodec().decode(buf));
		}

		public void encode(RegistryByteBuf registryByteBuf, Component<?> component) {
			write(registryByteBuf, (Component<T>)component);
		}

		private static <T> void write(RegistryByteBuf buf, Component<T> component) {
			DataComponentType.PACKET_CODEC.encode(buf, component.type());
			component.type().getPacketCodec().encode(buf, component.value());
		}
	};

	static Component<?> of(Entry<DataComponentType<?>, Object> entry) {
		return of((DataComponentType<T>)entry.getKey(), entry.getValue());
	}

	static <T> Component<T> of(DataComponentType<T> type, Object value) {
		return new Component<>(type, (T)value);
	}

	public void apply(ComponentMapImpl components) {
		components.set(this.type, this.value);
	}

	public <D> DataResult<D> encode(DynamicOps<D> ops) {
		Codec<T> codec = this.type.getCodec();
		return codec == null ? DataResult.error(() -> "Component of type " + this.type + " is not encodable") : codec.encodeStart(ops, this.value);
	}

	public String toString() {
		return this.type + "=>" + this.value;
	}
}
