package net.minecraft.network.codec;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.encoding.VarLongs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A set of pre-defined packet codecs.
 * 
 * @see PacketCodec
 */
public interface PacketCodecs {
	/**
	 * A codec for a boolean value.
	 * 
	 * @see io.netty.buffer.ByteBuf#readBoolean
	 * @see io.netty.buffer.ByteBuf#writeBoolean
	 */
	PacketCodec<ByteBuf, Boolean> BOOL = new PacketCodec<ByteBuf, Boolean>() {
		public Boolean decode(ByteBuf byteBuf) {
			return byteBuf.readBoolean();
		}

		public void encode(ByteBuf byteBuf, Boolean boolean_) {
			byteBuf.writeBoolean(boolean_);
		}
	};
	/**
	 * A codec for a byte value.
	 * 
	 * @see io.netty.buffer.ByteBuf#readByte
	 * @see io.netty.buffer.ByteBuf#writeByte
	 */
	PacketCodec<ByteBuf, Byte> BYTE = new PacketCodec<ByteBuf, Byte>() {
		public Byte decode(ByteBuf byteBuf) {
			return byteBuf.readByte();
		}

		public void encode(ByteBuf byteBuf, Byte byte_) {
			byteBuf.writeByte(byte_);
		}
	};
	/**
	 * A codec for a short value.
	 * 
	 * @see io.netty.buffer.ByteBuf#readShort
	 * @see io.netty.buffer.ByteBuf#writeShort
	 */
	PacketCodec<ByteBuf, Short> SHORT = new PacketCodec<ByteBuf, Short>() {
		public Short decode(ByteBuf byteBuf) {
			return byteBuf.readShort();
		}

		public void encode(ByteBuf byteBuf, Short short_) {
			byteBuf.writeShort(short_);
		}
	};
	/**
	 * A codec for a variable-length integer (var int) value.
	 * 
	 * @see net.minecraft.network.PacketByteBuf#readVarInt
	 * @see net.minecraft.network.PacketByteBuf#writeVarInt
	 */
	PacketCodec<ByteBuf, Integer> VAR_INT = new PacketCodec<ByteBuf, Integer>() {
		public Integer decode(ByteBuf byteBuf) {
			return VarInts.read(byteBuf);
		}

		public void encode(ByteBuf byteBuf, Integer integer) {
			VarInts.write(byteBuf, integer);
		}
	};
	/**
	 * A codec for a variable-length long (var long) value.
	 * 
	 * @see net.minecraft.network.PacketByteBuf#readVarLong
	 * @see net.minecraft.network.PacketByteBuf#writeVarLong
	 */
	PacketCodec<ByteBuf, Long> VAR_LONG = new PacketCodec<ByteBuf, Long>() {
		public Long decode(ByteBuf byteBuf) {
			return VarLongs.read(byteBuf);
		}

		public void encode(ByteBuf byteBuf, Long long_) {
			VarLongs.write(byteBuf, long_);
		}
	};
	/**
	 * A codec for a float value.
	 * 
	 * @see io.netty.buffer.ByteBuf#readFloat
	 * @see io.netty.buffer.ByteBuf#writeFloat
	 */
	PacketCodec<ByteBuf, Float> FLOAT = new PacketCodec<ByteBuf, Float>() {
		public Float decode(ByteBuf byteBuf) {
			return byteBuf.readFloat();
		}

		public void encode(ByteBuf byteBuf, Float float_) {
			byteBuf.writeFloat(float_);
		}
	};
	/**
	 * A codec for a double value.
	 * 
	 * @see io.netty.buffer.ByteBuf#readDouble
	 * @see io.netty.buffer.ByteBuf#writeDouble
	 */
	PacketCodec<ByteBuf, Double> DOUBLE = new PacketCodec<ByteBuf, Double>() {
		public Double decode(ByteBuf byteBuf) {
			return byteBuf.readDouble();
		}

		public void encode(ByteBuf byteBuf, Double double_) {
			byteBuf.writeDouble(double_);
		}
	};
	/**
	 * A codec for a byte array.
	 * 
	 * @see net.minecraft.network.PacketByteBuf#readByteArray()
	 * @see net.minecraft.network.PacketByteBuf#writeByteArray(byte[])
	 */
	PacketCodec<ByteBuf, byte[]> BYTE_ARRAY = new PacketCodec<ByteBuf, byte[]>() {
		public byte[] read(ByteBuf buf) {
			return PacketByteBuf.readByteArray(buf);
		}

		public void write(ByteBuf buf, byte[] value) {
			PacketByteBuf.writeByteArray(buf, value);
		}
	};
	/**
	 * A codec for a string value with maximum length {@value Short#MAX_VALUE}.
	 * 
	 * @see #string
	 * @see net.minecraft.network.PacketByteBuf#readString()
	 * @see net.minecraft.network.PacketByteBuf#writeString(String)
	 */
	PacketCodec<ByteBuf, String> STRING = string(32767);
	/**
	 * A codec for an NBT element of unlimited size.
	 * 
	 * @see #nbt
	 * @see net.minecraft.network.PacketByteBuf#readNbt(NbtSizeTracker)
	 * @see net.minecraft.network.PacketByteBuf#writeNbt(NbtElement)
	 */
	PacketCodec<ByteBuf, NbtElement> NBT_ELEMENT = nbt(NbtSizeTracker::ofUnlimitedBytes);
	/**
	 * A codec for an NBT compound of unlimited size.
	 * 
	 * @see #nbt
	 * @see net.minecraft.network.PacketByteBuf#readNbt(NbtSizeTracker)
	 * @see net.minecraft.network.PacketByteBuf#writeNbt(NbtElement)
	 */
	PacketCodec<ByteBuf, NbtCompound> NBT_COMPOUND = nbt(NbtSizeTracker::ofUnlimitedBytes).xmap(nbt -> {
		if (nbt instanceof NbtCompound) {
			return (NbtCompound)nbt;
		} else {
			throw new DecoderException("Not a compound tag: " + nbt);
		}
	}, nbt -> nbt);
	/**
	 * A codec for an optional NBT compound of up to {@value
	 * net.minecraft.network.PacketByteBuf#MAX_READ_NBT_SIZE} bytes.
	 * 
	 * @see #nbt
	 * @see net.minecraft.network.PacketByteBuf#readNbt(PacketByteBuf)
	 * @see net.minecraft.network.PacketByteBuf#writeNbt(io.netty.buffer.ByteBuf, NbtElement)
	 */
	PacketCodec<ByteBuf, Optional<NbtCompound>> OPTIONAL_NBT = new PacketCodec<ByteBuf, Optional<NbtCompound>>() {
		public Optional<NbtCompound> decode(ByteBuf byteBuf) {
			return Optional.ofNullable(PacketByteBuf.readNbt(byteBuf));
		}

		public void encode(ByteBuf byteBuf, Optional<NbtCompound> optional) {
			PacketByteBuf.writeNbt(byteBuf, (NbtElement)optional.orElse(null));
		}
	};
	/**
	 * A codec for a {@link org.joml.Vector3f}.
	 * 
	 * @see net.minecraft.network.PacketByteBuf#readVector3f()
	 * @see net.minecraft.network.PacketByteBuf#writeVector3f(Vector3f)
	 */
	PacketCodec<ByteBuf, Vector3f> VECTOR3F = new PacketCodec<ByteBuf, Vector3f>() {
		public Vector3f decode(ByteBuf byteBuf) {
			return PacketByteBuf.readVector3f(byteBuf);
		}

		public void encode(ByteBuf byteBuf, Vector3f vector3f) {
			PacketByteBuf.writeVector3f(byteBuf, vector3f);
		}
	};
	/**
	 * A codec for a {@link org.joml.Quaternionf}.
	 * 
	 * @see net.minecraft.network.PacketByteBuf#readQuaternionf()
	 * @see net.minecraft.network.PacketByteBuf#writeQuaternionf(Quaternionf)
	 */
	PacketCodec<ByteBuf, Quaternionf> QUATERNIONF = new PacketCodec<ByteBuf, Quaternionf>() {
		public Quaternionf decode(ByteBuf byteBuf) {
			return PacketByteBuf.readQuaternionf(byteBuf);
		}

		public void encode(ByteBuf byteBuf, Quaternionf quaternionf) {
			PacketByteBuf.writeQuaternionf(byteBuf, quaternionf);
		}
	};

	/**
	 * {@return a codec for a byte array with maximum length {@code maxLength}}
	 * 
	 * @see #BYTE_ARRAY
	 * @see net.minecraft.network.PacketByteBuf#readByteArray(ByteBuf, int)
	 * @see net.minecraft.network.PacketByteBuf#writeByteArray(ByteBuf, byte[])
	 */
	static PacketCodec<ByteBuf, byte[]> byteArray(int maxLength) {
		return new PacketCodec<ByteBuf, byte[]>() {
			public byte[] decode(ByteBuf buf) {
				return PacketByteBuf.readByteArray(buf, maxLength);
			}

			public void encode(ByteBuf buf, byte[] value) {
				if (value.length > maxLength) {
					throw new EncoderException("ByteArray with size " + value.length + " is bigger than allowed " + maxLength);
				} else {
					PacketByteBuf.writeByteArray(buf, value);
				}
			}
		};
	}

	/**
	 * {@return a codec for a string value with maximum length {@code maxLength}}
	 * 
	 * @see #STRING
	 * @see net.minecraft.network.PacketByteBuf#readString(int)
	 * @see net.minecraft.network.PacketByteBuf#writeString(String, int)
	 */
	static PacketCodec<ByteBuf, String> string(int maxLength) {
		return new PacketCodec<ByteBuf, String>() {
			public String decode(ByteBuf byteBuf) {
				return StringEncoding.decode(byteBuf, maxLength);
			}

			public void encode(ByteBuf byteBuf, String string) {
				StringEncoding.encode(byteBuf, string, maxLength);
			}
		};
	}

	/**
	 * {@return a codec for an NBT element}
	 * 
	 * @see #NBT_ELEMENT
	 * @see net.minecraft.network.PacketByteBuf#readNbt(NbtSizeTracker)
	 * @see net.minecraft.network.PacketByteBuf#writeNbt(NbtElement)
	 */
	static PacketCodec<ByteBuf, NbtElement> nbt(Supplier<NbtSizeTracker> sizeTracker) {
		return new PacketCodec<ByteBuf, NbtElement>() {
			public NbtElement decode(ByteBuf byteBuf) {
				NbtElement nbtElement = PacketByteBuf.readNbt(byteBuf, (NbtSizeTracker)sizeTracker.get());
				if (nbtElement == null) {
					throw new DecoderException("Expected non-null compound tag");
				} else {
					return nbtElement;
				}
			}

			public void encode(ByteBuf byteBuf, NbtElement nbtElement) {
				if (nbtElement == NbtEnd.INSTANCE) {
					throw new EncoderException("Expected non-null compound tag");
				} else {
					PacketByteBuf.writeNbt(byteBuf, nbtElement);
				}
			}
		};
	}

	/**
	 * {@return a codec from DataFixerUpper codec {@code codec}}
	 * 
	 * <p>Internally, the data is serialized as an NBT element of unlimited size.
	 */
	static <T> PacketCodec<ByteBuf, T> codec(Codec<T> codec) {
		return NBT_ELEMENT.xmap(
			nbt -> Util.getResult(codec.parse(NbtOps.INSTANCE, nbt), error -> new DecoderException("Failed to decode: " + error + " " + nbt)),
			value -> Util.getResult(codec.encodeStart(NbtOps.INSTANCE, (T)value), error -> new EncoderException("Failed to encode: " + error + " " + value))
		);
	}

	static <T> PacketCodec<RegistryByteBuf, T> registryCodec(Codec<T> codec) {
		return new PacketCodec<RegistryByteBuf, T>() {
			public T decode(RegistryByteBuf registryByteBuf) {
				NbtElement nbtElement = PacketCodecs.NBT_ELEMENT.decode(registryByteBuf);
				RegistryOps<NbtElement> registryOps = registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE);
				return Util.getResult(codec.parse(registryOps, nbtElement), error -> new DecoderException("Failed to decode: " + error + " " + nbtElement));
			}

			public void encode(RegistryByteBuf registryByteBuf, T object) {
				RegistryOps<NbtElement> registryOps = registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE);
				NbtElement nbtElement = Util.getResult(codec.encodeStart(registryOps, object), error -> new EncoderException("Failed to encode: " + error + " " + object));
				PacketCodecs.NBT_ELEMENT.encode(registryByteBuf, nbtElement);
			}
		};
	}

	/**
	 * {@return a codec wrapping another codec, the value of which is optional}
	 * 
	 * <p>This can be used with {@link PacketCodec#collect} like
	 * {@code codec.collect(PacketCodecs::optional)}.
	 * 
	 * @see net.minecraft.network.PacketByteBuf#readOptional
	 * @see net.minecraft.network.PacketByteBuf#writeOptional
	 */
	static <B extends ByteBuf, V> PacketCodec<B, Optional<V>> optional(PacketCodec<B, V> codec) {
		return new PacketCodec<B, Optional<V>>() {
			public Optional<V> decode(B byteBuf) {
				return byteBuf.readBoolean() ? Optional.of(codec.decode(byteBuf)) : Optional.empty();
			}

			public void encode(B byteBuf, Optional<V> optional) {
				if (optional.isPresent()) {
					byteBuf.writeBoolean(true);
					codec.encode(byteBuf, (V)optional.get());
				} else {
					byteBuf.writeBoolean(false);
				}
			}
		};
	}

	/**
	 * {@return a codec for a collection of values}
	 * 
	 * @see net.minecraft.network.PacketByteBuf#readCollection
	 * @see net.minecraft.network.PacketByteBuf#writeCollection
	 * 
	 * @param elementCodec the codec of the collection's elements
	 * @param factory a function that, given the collection's size, returns a new empty collection
	 */
	static <B extends ByteBuf, V, C extends Collection<V>> PacketCodec<B, C> collection(IntFunction<C> factory, PacketCodec<? super B, V> elementCodec) {
		return new PacketCodec<B, C>() {
			public C decode(B byteBuf) {
				int i = VarInts.read(byteBuf);
				C collection = (C)factory.apply(i);

				for (int j = 0; j < i; j++) {
					collection.add(elementCodec.decode(byteBuf));
				}

				return collection;
			}

			public void encode(B byteBuf, C collection) {
				VarInts.write(byteBuf, collection.size());

				for (V object : collection) {
					elementCodec.encode(byteBuf, object);
				}
			}
		};
	}

	/**
	 * Used to make a codec for a collection of values using {@link PacketCodec#collect}.
	 * 
	 * <p>For example, to make a codec for a set of values, write {@code
	 * codec.collect(PacketCodecs.toCollection(HashSet::new))}.
	 * 
	 * @see #toList
	 * 
	 * @param collectionFactory a function that, given the collection's size, returns a new empty collection
	 */
	static <B extends ByteBuf, V, C extends Collection<V>> PacketCodec.ResultFunction<B, V, C> toCollection(IntFunction<C> collectionFactory) {
		return codec -> collection(collectionFactory, codec);
	}

	/**
	 * Used to make a codec for a list of values using {@link PacketCodec#collect}.
	 * This creates an {@link java.util.ArrayList}, so the decoded result can be modified.
	 * 
	 * <p>For example, to make a codec for a list of values, write {@code
	 * codec.collect(PacketCodecs.toList())}.
	 * 
	 * @see #toCollection
	 */
	static <B extends ByteBuf, V> PacketCodec.ResultFunction<B, V, List<V>> toList() {
		return codec -> collection(ArrayList::new, codec);
	}

	/**
	 * {@return a codec for a map}
	 * 
	 * @see net.minecraft.network.PacketByteBuf#readMap(IntFunction, PacketDecoder, PacketDecoder)
	 * @see net.minecraft.network.PacketByteBuf#writeMap(java.util.Map, PacketEncoder, PacketEncoder)
	 * 
	 * @param factory a function that, given the map's size, returns a new empty map
	 * @param keyCodec the codec for the map's keys
	 * @param valueCodec the codec for the map's values
	 */
	static <B extends ByteBuf, K, V, M extends Map<K, V>> PacketCodec<B, M> map(
		IntFunction<? extends M> factory, PacketCodec<? super B, K> keyCodec, PacketCodec<? super B, V> valueCodec
	) {
		return new PacketCodec<B, M>() {
			public void encode(B byteBuf, M map) {
				VarInts.write(byteBuf, map.size());
				map.forEach((k, v) -> {
					keyCodec.encode(byteBuf, (K)k);
					valueCodec.encode(byteBuf, (V)v);
				});
			}

			public M decode(B byteBuf) {
				int i = VarInts.read(byteBuf);
				M map = (M)factory.apply(i);

				for (int j = 0; j < i; j++) {
					K object = keyCodec.decode(byteBuf);
					V object2 = valueCodec.decode(byteBuf);
					map.put(object, object2);
				}

				return map;
			}
		};
	}

	/**
	 * {@return a codec for an indexed value}
	 * 
	 * <p>An example of an indexed value is an enum.
	 * 
	 * @see net.minecraft.util.function.ValueLists
	 * @see net.minecraft.network.PacketByteBuf#encode(ToIntFunction, Object)
	 * @see net.minecraft.network.PacketByteBuf#decode(IntFunction)
	 * 
	 * @param valueToIndex a function that gets a value's index
	 * @param indexToValue a function that gets a value from its index
	 */
	static <T> PacketCodec<ByteBuf, T> indexed(IntFunction<T> indexToValue, ToIntFunction<T> valueToIndex) {
		return new PacketCodec<ByteBuf, T>() {
			public T decode(ByteBuf byteBuf) {
				int i = VarInts.read(byteBuf);
				return (T)indexToValue.apply(i);
			}

			public void encode(ByteBuf byteBuf, T object) {
				int i = valueToIndex.applyAsInt(object);
				VarInts.write(byteBuf, i);
			}
		};
	}

	/**
	 * {@return a codec for an entry of {@code iterable}}
	 * 
	 * @see #indexed
	 */
	static <T> PacketCodec<ByteBuf, T> entryOf(IndexedIterable<T> iterable) {
		return indexed(iterable::getOrThrow, iterable::getRawIdOrThrow);
	}

	private static <T, R> PacketCodec<RegistryByteBuf, R> registry(
		RegistryKey<? extends Registry<T>> registry, Function<Registry<T>, IndexedIterable<R>> registryTransformer
	) {
		return new PacketCodec<RegistryByteBuf, R>() {
			private IndexedIterable<R> getIterable(RegistryByteBuf buf) {
				return (IndexedIterable<R>)registryTransformer.apply(buf.getRegistryManager().get(registry));
			}

			public R decode(RegistryByteBuf registryByteBuf) {
				int i = VarInts.read(registryByteBuf);
				return (R)this.getIterable(registryByteBuf).getOrThrow(i);
			}

			public void encode(RegistryByteBuf registryByteBuf, R object) {
				int i = this.getIterable(registryByteBuf).getRawIdOrThrow(object);
				VarInts.write(registryByteBuf, i);
			}
		};
	}

	/**
	 * {@return a codec for a {@link net.minecraft.registry.Registry}-registered value}
	 * 
	 * <p>This codec only works with {@link net.minecraft.network.RegistryByteBuf}, used
	 * during the play phase. Consider using {@link #entryOf} for encoding a value of a
	 * static registry during login or configuration phases.
	 * 
	 * @implNote The value is serialized as the corresponding raw ID (as {@link #VAR_INT
	 * a var int}).
	 * 
	 * @see #entryOf
	 */
	static <T> PacketCodec<RegistryByteBuf, T> registryValue(RegistryKey<? extends Registry<T>> registry) {
		return registry(registry, registryx -> registryx);
	}

	/**
	 * {@return a codec for a reference {@link net.minecraft.registry.entry.RegistryEntry}}
	 * 
	 * <p>This codec only works with {@link net.minecraft.network.RegistryByteBuf}, used
	 * during the play phase. Consider using {@link #entryOf} for encoding a value of a
	 * static registry during login or configuration phases.
	 * 
	 * @implNote The value is serialized as the corresponding raw ID (as {@link #VAR_INT
	 * a var int}). This does not handle direct (unregistered) entries.
	 * 
	 * @see #registryValue
	 * @see #registryEntry(RegistryKey, PacketCodec)
	 */
	static <T> PacketCodec<RegistryByteBuf, RegistryEntry<T>> registryEntry(RegistryKey<? extends Registry<T>> registry) {
		return registry(registry, Registry::getIndexedEntries);
	}

	/**
	 * {@return a codec for a {@link net.minecraft.registry.entry.RegistryEntry}}
	 * 
	 * <p>This codec only works with {@link net.minecraft.network.RegistryByteBuf}, used
	 * during the play phase. Consider using {@link #entryOf} for encoding a value of a
	 * static registry during login or configuration phases.
	 * 
	 * @implNote If the entry is a reference entry, the value is serialized as the
	 * corresponding raw ID (as {@link #VAR_INT a var int}). If it is a direct entry,
	 * it is encoded using {@code directCodec}.
	 * 
	 * @see #registryValue
	 * @see #registryEntry(RegistryKey)
	 */
	static <T> PacketCodec<RegistryByteBuf, RegistryEntry<T>> registryEntry(
		RegistryKey<? extends Registry<T>> registry, PacketCodec<? super RegistryByteBuf, T> directCodec
	) {
		return new PacketCodec<RegistryByteBuf, RegistryEntry<T>>() {
			private static final int DIRECT_ENTRY_MARKER = 0;

			private IndexedIterable<RegistryEntry<T>> getEntries(RegistryByteBuf buf) {
				return buf.getRegistryManager().get(registry).getIndexedEntries();
			}

			public RegistryEntry<T> decode(RegistryByteBuf registryByteBuf) {
				int i = VarInts.read(registryByteBuf);
				return i == 0 ? RegistryEntry.of(directCodec.decode(registryByteBuf)) : (RegistryEntry)this.getEntries(registryByteBuf).getOrThrow(i - 1);
			}

			public void encode(RegistryByteBuf registryByteBuf, RegistryEntry<T> registryEntry) {
				switch (registryEntry.getType()) {
					case REFERENCE:
						int i = this.getEntries(registryByteBuf).getRawIdOrThrow(registryEntry);
						VarInts.write(registryByteBuf, i + 1);
						break;
					case DIRECT:
						VarInts.write(registryByteBuf, 0);
						directCodec.encode(registryByteBuf, registryEntry.value());
				}
			}
		};
	}
}
