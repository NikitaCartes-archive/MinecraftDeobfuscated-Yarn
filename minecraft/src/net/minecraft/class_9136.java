package net.minecraft;

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

public class class_9136<B extends ByteBuf, V, T> implements PacketCodec<B, V> {
	private static final int field_48576 = -1;
	private final Function<V, ? extends T> field_48577;
	private final List<class_9136.class_9138<B, V, T>> field_48578;
	private final Object2IntMap<T> field_48579;

	class_9136(Function<V, ? extends T> function, List<class_9136.class_9138<B, V, T>> list, Object2IntMap<T> object2IntMap) {
		this.field_48577 = function;
		this.field_48578 = list;
		this.field_48579 = object2IntMap;
	}

	public V decode(B byteBuf) {
		int i = VarInts.read(byteBuf);
		if (i >= 0 && i < this.field_48578.size()) {
			class_9136.class_9138<B, V, T> lv = (class_9136.class_9138<B, V, T>)this.field_48578.get(i);

			try {
				return (V)lv.serializer.decode(byteBuf);
			} catch (Exception var5) {
				throw new DecoderException("Failed to decode packet '" + lv.type + "'", var5);
			}
		} else {
			throw new DecoderException("Received unknown packet id " + i);
		}
	}

	public void encode(B byteBuf, V object) {
		T object2 = (T)this.field_48577.apply(object);
		int i = this.field_48579.getOrDefault(object2, -1);
		if (i == -1) {
			throw new EncoderException("Sending unknown packet '" + object2 + "'");
		} else {
			VarInts.write(byteBuf, i);
			class_9136.class_9138<B, V, T> lv = (class_9136.class_9138<B, V, T>)this.field_48578.get(i);

			try {
				PacketCodec<? super B, V> packetCodec = (PacketCodec<? super B, V>)lv.serializer;
				packetCodec.encode(byteBuf, object);
			} catch (Exception var7) {
				throw new EncoderException("Failed to encode packet '" + object2 + "'", var7);
			}
		}
	}

	public static <B extends ByteBuf, V, T> class_9136.class_9137<B, V, T> method_56427(Function<V, ? extends T> function) {
		return new class_9136.class_9137<>(function);
	}

	public static class class_9137<B extends ByteBuf, V, T> {
		private final List<class_9136.class_9138<B, V, T>> field_48580 = new ArrayList();
		private final Function<V, ? extends T> field_48581;

		class_9137(Function<V, ? extends T> function) {
			this.field_48581 = function;
		}

		public class_9136.class_9137<B, V, T> method_56429(T object, PacketCodec<? super B, ? extends V> packetCodec) {
			this.field_48580.add(new class_9136.class_9138<>(packetCodec, object));
			return this;
		}

		public class_9136<B, V, T> method_56428() {
			Object2IntOpenHashMap<T> object2IntOpenHashMap = new Object2IntOpenHashMap<>();
			object2IntOpenHashMap.defaultReturnValue(-2);

			for (class_9136.class_9138<B, V, T> lv : this.field_48580) {
				int i = object2IntOpenHashMap.size();
				int j = object2IntOpenHashMap.putIfAbsent(lv.type, i);
				if (j != -2) {
					throw new IllegalStateException("Duplicate registration for type " + lv.type);
				}
			}

			return new class_9136<>(this.field_48581, List.copyOf(this.field_48580), object2IntOpenHashMap);
		}
	}

	static record class_9138<B, V, T>(PacketCodec<? super B, ? extends V> serializer, T type) {
	}
}
