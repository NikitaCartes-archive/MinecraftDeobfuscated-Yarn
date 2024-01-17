package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function3;
import io.netty.buffer.ByteBuf;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface PacketCodec<B, V> extends PacketDecoder<B, V>, PacketEncoder<B, V> {
	static <B, V> PacketCodec<B, V> of(PacketEncoder<B, V> encoder, PacketDecoder<B, V> decoder) {
		return new PacketCodec<B, V>() {
			@Override
			public V decode(B object) {
				return decoder.decode(object);
			}

			@Override
			public void encode(B object, V object2) {
				encoder.encode(object, object2);
			}
		};
	}

	static <B, V> PacketCodec<B, V> of(ValueFirstEncoder<B, V> encoder, PacketDecoder<B, V> decoder) {
		return new PacketCodec<B, V>() {
			@Override
			public V decode(B object) {
				return decoder.decode(object);
			}

			@Override
			public void encode(B object, V object2) {
				encoder.encode(object2, object);
			}
		};
	}

	static <B, V> PacketCodec<B, V> unit(V value) {
		return new PacketCodec<B, V>() {
			@Override
			public V decode(B object) {
				return value;
			}

			@Override
			public void encode(B object, V object2) {
				if (!object2.equals(value)) {
					throw new IllegalStateException("Can't encode '" + object2 + "', expected '" + value + "'");
				}
			}
		};
	}

	default <O> PacketCodec<B, O> mapResult(PacketCodec.ResultFunction<B, V, O> function) {
		return function.apply(this);
	}

	default <O> PacketCodec<B, O> xmap(Function<? super V, ? extends O> to, Function<? super O, ? extends V> from) {
		return new PacketCodec<B, O>() {
			@Override
			public O decode(B object) {
				return (O)to.apply(PacketCodec.this.decode(object));
			}

			@Override
			public void encode(B object, O object2) {
				PacketCodec.this.encode(object, (V)from.apply(object2));
			}
		};
	}

	default <O extends ByteBuf> PacketCodec<O, V> mapBuf(Function<O, ? extends B> function) {
		return new PacketCodec<O, V>() {
			public V decode(O byteBuf) {
				B object = (B)function.apply(byteBuf);
				return PacketCodec.this.decode(object);
			}

			public void encode(O byteBuf, V object) {
				B object2 = (B)function.apply(byteBuf);
				PacketCodec.this.encode(object2, object);
			}
		};
	}

	default <U> PacketCodec<B, U> dispatch(Function<? super U, ? extends V> type, Function<? super V, ? extends PacketCodec<? super B, ? extends U>> codec) {
		return new PacketCodec<B, U>() {
			@Override
			public U decode(B object) {
				V object2 = PacketCodec.this.decode(object);
				PacketCodec<? super B, ? extends U> packetCodec = (PacketCodec<? super B, ? extends U>)codec.apply(object2);
				return (U)packetCodec.decode(object);
			}

			@Override
			public void encode(B object, U object2) {
				V object3 = (V)type.apply(object2);
				PacketCodec<B, U> packetCodec = (PacketCodec<B, U>)codec.apply(object3);
				PacketCodec.this.encode(object, object3);
				packetCodec.encode(object, object2);
			}
		};
	}

	static <B, C, T1> PacketCodec<B, C> tuple(PacketCodec<? super B, T1> codec, Function<C, T1> from, Function<T1, C> to) {
		return new PacketCodec<B, C>() {
			@Override
			public C decode(B object) {
				T1 object2 = codec.decode(object);
				return (C)to.apply(object2);
			}

			@Override
			public void encode(B object, C object2) {
				codec.encode(object, (T1)from.apply(object2));
			}
		};
	}

	static <B, C, T1, T2> PacketCodec<B, C> tuple(
		PacketCodec<? super B, T1> codec1, Function<C, T1> from1, PacketCodec<? super B, T2> codec2, Function<C, T2> from2, BiFunction<T1, T2, C> to
	) {
		return new PacketCodec<B, C>() {
			@Override
			public C decode(B object) {
				T1 object2 = codec1.decode(object);
				T2 object3 = codec2.decode(object);
				return (C)to.apply(object2, object3);
			}

			@Override
			public void encode(B object, C object2) {
				codec1.encode(object, (T1)from1.apply(object2));
				codec2.encode(object, (T2)from2.apply(object2));
			}
		};
	}

	static <B, C, T1, T2, T3> PacketCodec<B, C> tuple(
		PacketCodec<? super B, T1> codec1,
		Function<C, T1> from1,
		PacketCodec<? super B, T2> codec2,
		Function<C, T2> from2,
		PacketCodec<? super B, T3> codec3,
		Function<C, T3> from3,
		Function3<T1, T2, T3, C> to
	) {
		return new PacketCodec<B, C>() {
			@Override
			public C decode(B object) {
				T1 object2 = codec1.decode(object);
				T2 object3 = codec2.decode(object);
				T3 object4 = codec3.decode(object);
				return to.apply(object2, object3, object4);
			}

			@Override
			public void encode(B object, C object2) {
				codec1.encode(object, (T1)from1.apply(object2));
				codec2.encode(object, (T2)from2.apply(object2));
				codec3.encode(object, (T3)from3.apply(object2));
			}
		};
	}

	default <S extends B> PacketCodec<S, V> cast() {
		return this;
	}

	@FunctionalInterface
	public interface ResultFunction<B, S, T> {
		PacketCodec<B, T> apply(PacketCodec<B, S> codec);
	}
}
