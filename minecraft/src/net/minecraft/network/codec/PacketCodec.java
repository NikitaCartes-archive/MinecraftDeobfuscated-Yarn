package net.minecraft.network.codec;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import io.netty.buffer.ByteBuf;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A codec that is used for serializing a packet.
 * 
 * <p>Packet codecs serialize to, and deserialize from, {@link net.minecraft.network.PacketByteBuf},
 * which is a stream of data. To integrate the classic {@link net.minecraft.network.PacketByteBuf}-based
 * code, use {@link #of(ValueFirstEncoder, PacketDecoder)}
 * like this:
 * 
 * <pre>{@code
 * public static final PacketCodec<PacketByteBuf, MyPacket> CODEC = PacketCodec.of(MyPacket::write, MyPacket::new);
 * 
 * private MyPacket(PacketByteBuf buf) {
 * 	this.text = buf.readString();
 * }
 * 
 * private void write(PacketByteBuf buf) {
 * 	buf.writeString(this.text);
 * }
 * }</pre>
 * 
 * <p>While this serves similar functions as codecs in the DataFixerUpper library,
 * the two are wholly separate and DataFixerUpper methods cannot be used with this.
 * However, a packet codec may reference a regular codec by using {@link
 * PacketCodecs#codec}, which serializes the data to NBT.
 * 
 * <p>See {@link PacketCodecs} for codecs to serialize various objects.
 * 
 * @param <B> the type of the buffer; {@link net.minecraft.network.RegistryByteBuf}
 * for play-phase packets, {@link net.minecraft.network.PacketByteBuf} for other
 * phases (like configuration)
 * @param <V> the type of the value to be encoded/decoded
 */
public interface PacketCodec<B, V> extends PacketDecoder<B, V>, PacketEncoder<B, V> {
	/**
	 * {@return a packet codec from the {@code encoder} and {@code decoder}}
	 * 
	 * @apiNote This is useful for integrating with code that uses static methods for
	 * packet writing, where the buffer is the first argument, like
	 * {@code static void write(PacketByteBuf buf, Data data)}.
	 * For code that uses instance methods like {@code void write(PacketByteBuf buf)},
	 * use {@link #of(ValueFirstEncoder, PacketDecoder)}.
	 */
	static <B, V> PacketCodec<B, V> ofStatic(PacketEncoder<B, V> encoder, PacketDecoder<B, V> decoder) {
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

	/**
	 * {@return a packet codec from the {@code encoder} and {@code decoder}}
	 * 
	 * @apiNote This is useful for integrating with code that uses instance methods for
	 * packet writing, like {@code void write(PacketByteBuf buf)}.
	 * For code that uses static methods like {@code static void write(PacketByteBuf buf, Data data)},
	 * where the buffer is the first argument, use {@link #ofStatic(PacketEncoder, PacketDecoder)}.
	 */
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

	/**
	 * {@return a codec that always returns {@code value}}
	 * 
	 * <p>This does not encode anything. Instead, it throws {@link
	 * IllegalStateException} when the value does not
	 * equal {@code value}. This comparison is made with {@code equals()}, not
	 * reference equality ({@code ==}).
	 */
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

	/**
	 * {@return the result mapped with {@code function}}
	 * 
	 * <p>For example, passing {@code PacketCodecs::optional} makes the value
	 * optional. Additionally, this method can be used like Stream {@link
	 * java.util.stream.Collectors} - hence its name. For example, to make a codec
	 * for a list of something, write {@code parentCodec.collect(PacketCodecs.toList())}.
	 * 
	 * @see PacketCodecs#optional
	 * @see PacketCodecs#toCollection
	 * @see PacketCodecs#toList
	 */
	default <O> PacketCodec<B, O> collect(PacketCodec.ResultFunction<B, V, O> function) {
		return function.apply(this);
	}

	/**
	 * {@return a codec that maps its encode input and decode output with {@code from}
	 * and {@code to}, respectively}
	 * 
	 * <p>This can be used to transform a codec for a simple value (like a string)
	 * into a corresponding, more complex value (like an identifier). An example:
	 * 
	 * <pre>{@code
	 * public static final PacketCodec<ByteBuf, Identifier> PACKET_CODEC = PacketCodecs.STRING.xmap(Identifier::new, Identifier::toString);
	 * }</pre>
	 */
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

	/**
	 * {@return a codec that dispatches one of the sub-codecs based on the type}
	 * 
	 * <p>For example, subtypes of {@link net.minecraft.stat.Stat} requires different values
	 * to be serialized, yet it makes sense to use the same codec for all stats.
	 * This method should be called on the codec for the "type" - like {@link
	 * net.minecraft.stat.StatType}. An example:
	 * 
	 * <pre>{@code
	 * public static final PacketCodec<RegistryByteBuf, Thing<?>> PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.THING_TYPE).dispatch(Thing::getType, ThingType::getPacketCodec);
	 * }</pre>
	 * 
	 * @param codec a function that, given a "type", returns the codec for encoding/decoding the value
	 * @param type a function that, given a value, returns its "type"
	 */
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

	/**
	 * {@return a codec for encoding one value}
	 */
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

	/**
	 * {@return a codec for encoding two values}
	 */
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

	/**
	 * {@return a codec for encoding three values}
	 */
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

	/**
	 * {@return a codec for encoding four values}
	 */
	static <B, C, T1, T2, T3, T4> PacketCodec<B, C> tuple(
		PacketCodec<? super B, T1> codec1,
		Function<C, T1> from1,
		PacketCodec<? super B, T2> codec2,
		Function<C, T2> from2,
		PacketCodec<? super B, T3> codec3,
		Function<C, T3> from3,
		PacketCodec<? super B, T4> codec4,
		Function<C, T4> from4,
		Function4<T1, T2, T3, T4, C> to
	) {
		return new PacketCodec<B, C>() {
			@Override
			public C decode(B object) {
				T1 object2 = codec1.decode(object);
				T2 object3 = codec2.decode(object);
				T3 object4 = codec3.decode(object);
				T4 object5 = codec4.decode(object);
				return to.apply(object2, object3, object4, object5);
			}

			@Override
			public void encode(B object, C object2) {
				codec1.encode(object, (T1)from1.apply(object2));
				codec2.encode(object, (T2)from2.apply(object2));
				codec3.encode(object, (T3)from3.apply(object2));
				codec4.encode(object, (T4)from4.apply(object2));
			}
		};
	}

	/**
	 * {@return a codec for encoding five values}
	 */
	static <B, C, T1, T2, T3, T4, T5> PacketCodec<B, C> tuple(
		PacketCodec<? super B, T1> codec1,
		Function<C, T1> from1,
		PacketCodec<? super B, T2> codec2,
		Function<C, T2> from2,
		PacketCodec<? super B, T3> codec3,
		Function<C, T3> from3,
		PacketCodec<? super B, T4> codec4,
		Function<C, T4> from4,
		PacketCodec<? super B, T5> codec5,
		Function<C, T5> from5,
		Function5<T1, T2, T3, T4, T5, C> to
	) {
		return new PacketCodec<B, C>() {
			@Override
			public C decode(B object) {
				T1 object2 = codec1.decode(object);
				T2 object3 = codec2.decode(object);
				T3 object4 = codec3.decode(object);
				T4 object5 = codec4.decode(object);
				T5 object6 = codec5.decode(object);
				return to.apply(object2, object3, object4, object5, object6);
			}

			@Override
			public void encode(B object, C object2) {
				codec1.encode(object, (T1)from1.apply(object2));
				codec2.encode(object, (T2)from2.apply(object2));
				codec3.encode(object, (T3)from3.apply(object2));
				codec4.encode(object, (T4)from4.apply(object2));
				codec5.encode(object, (T5)from5.apply(object2));
			}
		};
	}

	/**
	 * {@return a codec for encoding six values}
	 */
	static <B, C, T1, T2, T3, T4, T5, T6> PacketCodec<B, C> tuple(
		PacketCodec<? super B, T1> codec1,
		Function<C, T1> from1,
		PacketCodec<? super B, T2> codec2,
		Function<C, T2> from2,
		PacketCodec<? super B, T3> codec3,
		Function<C, T3> from3,
		PacketCodec<? super B, T4> codec4,
		Function<C, T4> from4,
		PacketCodec<? super B, T5> codec5,
		Function<C, T5> from5,
		PacketCodec<? super B, T6> codec6,
		Function<C, T6> from6,
		Function6<T1, T2, T3, T4, T5, T6, C> to
	) {
		return new PacketCodec<B, C>() {
			@Override
			public C decode(B object) {
				T1 object2 = codec1.decode(object);
				T2 object3 = codec2.decode(object);
				T3 object4 = codec3.decode(object);
				T4 object5 = codec4.decode(object);
				T5 object6 = codec5.decode(object);
				T6 object7 = codec6.decode(object);
				return to.apply(object2, object3, object4, object5, object6, object7);
			}

			@Override
			public void encode(B object, C object2) {
				codec1.encode(object, (T1)from1.apply(object2));
				codec2.encode(object, (T2)from2.apply(object2));
				codec3.encode(object, (T3)from3.apply(object2));
				codec4.encode(object, (T4)from4.apply(object2));
				codec5.encode(object, (T5)from5.apply(object2));
				codec6.encode(object, (T6)from6.apply(object2));
			}
		};
	}

	static <B, T> PacketCodec<B, T> recursive(UnaryOperator<PacketCodec<B, T>> codecGetter) {
		return new PacketCodec<B, T>() {
			private final Supplier<PacketCodec<B, T>> codecSupplier = Suppliers.memoize(() -> (PacketCodec<B, T>)codecGetter.apply(this));

			@Override
			public T decode(B object) {
				return (T)((PacketCodec)this.codecSupplier.get()).decode(object);
			}

			@Override
			public void encode(B object, T object2) {
				((PacketCodec)this.codecSupplier.get()).encode(object, (V)object2);
			}
		};
	}

	/**
	 * {@return the same codec, casted to work with buffers of type {@code S}}
	 * 
	 * @apiNote For example, {@link net.minecraft.util.math.BlockPos#PACKET_CODEC}
	 * is defined as {@code PacketCodec<ByteBuf, BlockPos>}. To use this codec
	 * where {@link net.minecraft.network.PacketByteBuf} is expected, you can call
	 * this method for easy casting, like: {@code PACKET_CODEC.cast()}.
	 * Doing this is generally safe and will not result in exceptions.
	 */
	default <S extends B> PacketCodec<S, V> cast() {
		return this;
	}

	@FunctionalInterface
	public interface ResultFunction<B, S, T> {
		PacketCodec<B, T> apply(PacketCodec<B, S> codec);
	}
}
