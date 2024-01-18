package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;

public class PacketCodecDispatcher<B extends ByteBuf, V, T> implements PacketCodec<B, V> {
	private static final int UNKNOWN_PACKET_INDEX = -1;
	private final Function<V, ? extends T> packetIdGetter;
	private final List<PacketCodecDispatcher.PacketType<B, V, T>> packetTypes;
	private final Object2IntMap<T> typeToIndex;

	PacketCodecDispatcher(Function<V, ? extends T> packetIdGetter, List<PacketCodecDispatcher.PacketType<B, V, T>> packetTypes, Object2IntMap<T> typeToIndex) {
		this.packetIdGetter = packetIdGetter;
		this.packetTypes = packetTypes;
		this.typeToIndex = typeToIndex;
	}

	public V decode(B byteBuf) {
		int i = VarInts.read(byteBuf);
		if (i >= 0 && i < this.packetTypes.size()) {
			PacketCodecDispatcher.PacketType<B, V, T> packetType = (PacketCodecDispatcher.PacketType<B, V, T>)this.packetTypes.get(i);

			try {
				return (V)packetType.codec.decode(byteBuf);
			} catch (Exception var5) {
				throw new DecoderException("Failed to decode packet '" + packetType.id + "'", var5);
			}
		} else {
			throw new DecoderException("Received unknown packet id " + i);
		}
	}

	public void encode(B byteBuf, V object) {
		T object2 = (T)this.packetIdGetter.apply(object);
		int i = this.typeToIndex.getOrDefault(object2, -1);
		if (i == -1) {
			throw new EncoderException("Sending unknown packet '" + object2 + "'");
		} else {
			VarInts.write(byteBuf, i);
			PacketCodecDispatcher.PacketType<B, V, T> packetType = (PacketCodecDispatcher.PacketType<B, V, T>)this.packetTypes.get(i);

			try {
				PacketCodec<? super B, V> packetCodec = (PacketCodec<? super B, V>)packetType.codec;
				packetCodec.encode(byteBuf, object);
			} catch (Exception var7) {
				throw new EncoderException("Failed to encode packet '" + object2 + "'", var7);
			}
		}
	}

	public static <B extends ByteBuf, V, T> PacketCodecDispatcher.Builder<B, V, T> builder(Function<V, ? extends T> packetIdGetter) {
		return new PacketCodecDispatcher.Builder<>(packetIdGetter);
	}

	public static class Builder<B extends ByteBuf, V, T> {
		private final List<PacketCodecDispatcher.PacketType<B, V, T>> packetTypes = new ArrayList();
		private final Function<V, ? extends T> packetIdGetter;

		Builder(Function<V, ? extends T> packetIdGetter) {
			this.packetIdGetter = packetIdGetter;
		}

		public PacketCodecDispatcher.Builder<B, V, T> add(T id, PacketCodec<? super B, ? extends V> codec) {
			this.packetTypes.add(new PacketCodecDispatcher.PacketType<>(codec, id));
			return this;
		}

		public PacketCodecDispatcher<B, V, T> build() {
			Object2IntOpenHashMap<T> object2IntOpenHashMap = new Object2IntOpenHashMap<>();
			object2IntOpenHashMap.defaultReturnValue(-2);

			for (PacketCodecDispatcher.PacketType<B, V, T> packetType : this.packetTypes) {
				int i = object2IntOpenHashMap.size();
				int j = object2IntOpenHashMap.putIfAbsent(packetType.id, i);
				if (j != -2) {
					throw new IllegalStateException("Duplicate registration for type " + packetType.id);
				}
			}

			return new PacketCodecDispatcher<>(this.packetIdGetter, List.copyOf(this.packetTypes), object2IntOpenHashMap);
		}
	}

	static record PacketType<B, V, T>(PacketCodec<? super B, ? extends V> codec, T id) {
	}
}
