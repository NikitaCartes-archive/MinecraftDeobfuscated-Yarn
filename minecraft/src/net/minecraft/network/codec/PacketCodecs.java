package net.minecraft.network.codec;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.IndexedIterable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A set of pre-defined packet codecs.
 * 
 * @see PacketCodec
 */
public interface PacketCodecs {
	int field_49674 = 65536;
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
	 * A codec for an unsigned short value.
	 * 
	 * @see io.netty.buffer.ByteBuf#readUnsignedShort
	 * @see io.netty.buffer.ByteBuf#writeShort
	 */
	PacketCodec<ByteBuf, Integer> UNSIGNED_SHORT = new PacketCodec<ByteBuf, Integer>() {
		public Integer decode(ByteBuf byteBuf) {
			return byteBuf.readUnsignedShort();
		}

		public void encode(ByteBuf byteBuf, Integer integer) {
			byteBuf.writeShort(integer);
		}
	};
	/**
	 * A codec for an integer value.
	 * 
	 * @see io.netty.buffer.ByteBuf#readInt
	 * @see io.netty.buffer.ByteBuf#writeInt
	 */
	PacketCodec<ByteBuf, Integer> INTEGER = new PacketCodec<ByteBuf, Integer>() {
		public Integer decode(ByteBuf byteBuf) {
			return byteBuf.readInt();
		}

		public void encode(ByteBuf byteBuf, Integer integer) {
			byteBuf.writeInt(integer);
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
		public byte[] method_59799(ByteBuf byteBuf) {
			return PacketByteBuf.readByteArray(byteBuf);
		}

		public void method_59800(ByteBuf byteBuf, byte[] bs) {
			PacketByteBuf.writeByteArray(byteBuf, bs);
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
	 * A codec for an NBT element of up to {@code 0x200000L} bytes.
	 * 
	 * @see #nbt
	 * @see net.minecraft.network.PacketByteBuf#readNbt(NbtSizeTracker)
	 * @see net.minecraft.network.PacketByteBuf#writeNbt(NbtElement)
	 */
	PacketCodec<ByteBuf, NbtElement> NBT_ELEMENT = nbt(() -> NbtSizeTracker.of(2097152L));
	/**
	 * A codec for an NBT element of unlimited size.
	 * 
	 * @see #nbt
	 * @see net.minecraft.network.PacketByteBuf#readNbt(NbtSizeTracker)
	 * @see net.minecraft.network.PacketByteBuf#writeNbt(NbtElement)
	 */
	PacketCodec<ByteBuf, NbtElement> UNLIMITED_NBT_ELEMENT = nbt(NbtSizeTracker::ofUnlimitedBytes);
	/**
	 * A codec for an NBT compound of up to {@code 0x200000L} bytes.
	 * 
	 * @see #nbt
	 * @see net.minecraft.network.PacketByteBuf#readNbt(NbtSizeTracker)
	 * @see net.minecraft.network.PacketByteBuf#writeNbt(NbtElement)
	 */
	PacketCodec<ByteBuf, NbtCompound> NBT_COMPOUND = nbtCompound(() -> NbtSizeTracker.of(2097152L));
	/**
	 * A codec for an NBT compound of unlimited size.
	 * 
	 * @see #nbt
	 * @see net.minecraft.network.PacketByteBuf#readNbt(NbtSizeTracker)
	 * @see net.minecraft.network.PacketByteBuf#writeNbt(NbtElement)
	 */
	PacketCodec<ByteBuf, NbtCompound> UNLIMITED_NBT_COMPOUND = nbtCompound(NbtSizeTracker::ofUnlimitedBytes);
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
	PacketCodec<ByteBuf, PropertyMap> PROPERTY_MAP = new PacketCodec<ByteBuf, PropertyMap>() {
		private static final int NAME_MAX_LENGTH = 64;
		private static final int VALUE_MAX_LENGTH = 32767;
		private static final int SIGNATURE_MAX_LENGTH = 1024;
		private static final int MAP_MAX_SIZE = 16;

		public PropertyMap decode(ByteBuf byteBuf) {
			int i = PacketCodecs.readCollectionSize(byteBuf, 16);
			PropertyMap propertyMap = new PropertyMap();

			for (int j = 0; j < i; j++) {
				String string = StringEncoding.decode(byteBuf, 64);
				String string2 = StringEncoding.decode(byteBuf, 32767);
				String string3 = PacketByteBuf.readNullable(byteBuf, buf2 -> StringEncoding.decode(buf2, 1024));
				Property property = new Property(string, string2, string3);
				propertyMap.put(property.name(), property);
			}

			return propertyMap;
		}

		public void encode(ByteBuf byteBuf, PropertyMap propertyMap) {
			PacketCodecs.writeCollectionSize(byteBuf, propertyMap.size(), 16);

			for (Property property : propertyMap.values()) {
				StringEncoding.encode(byteBuf, property.name(), 64);
				StringEncoding.encode(byteBuf, property.value(), 32767);
				PacketByteBuf.writeNullable(byteBuf, property.signature(), (buf2, signature) -> StringEncoding.encode(buf2, signature, 1024));
			}
		}
	};
	PacketCodec<ByteBuf, GameProfile> GAME_PROFILE = new PacketCodec<ByteBuf, GameProfile>() {
		public GameProfile decode(ByteBuf byteBuf) {
			UUID uUID = Uuids.PACKET_CODEC.decode(byteBuf);
			String string = StringEncoding.decode(byteBuf, 16);
			GameProfile gameProfile = new GameProfile(uUID, string);
			gameProfile.getProperties().putAll(PacketCodecs.PROPERTY_MAP.decode(byteBuf));
			return gameProfile;
		}

		public void encode(ByteBuf byteBuf, GameProfile gameProfile) {
			Uuids.PACKET_CODEC.encode(byteBuf, gameProfile.getId());
			StringEncoding.encode(byteBuf, gameProfile.getName(), 16);
			PacketCodecs.PROPERTY_MAP.encode(byteBuf, gameProfile.getProperties());
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

			public void encode(ByteBuf byteBuf, byte[] bs) {
				if (bs.length > maxLength) {
					throw new EncoderException("ByteArray with size " + bs.length + " is bigger than allowed " + maxLength);
				} else {
					PacketByteBuf.writeByteArray(byteBuf, bs);
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

	static PacketCodec<ByteBuf, NbtCompound> nbtCompound(Supplier<NbtSizeTracker> sizeTracker) {
		return nbt(sizeTracker).xmap(nbt -> {
			if (nbt instanceof NbtCompound) {
				return (NbtCompound)nbt;
			} else {
				throw new DecoderException("Not a compound tag: " + nbt);
			}
		}, nbt -> nbt);
	}

	/**
	 * {@return a codec from DataFixerUpper codec {@code codec}}
	 * 
	 * <p>Internally, the data is serialized as an NBT element of unlimited size.
	 */
	static <T> PacketCodec<ByteBuf, T> unlimitedCodec(Codec<T> codec) {
		return codec(codec, NbtSizeTracker::ofUnlimitedBytes);
	}

	/**
	 * {@return a codec from DataFixerUpper codec {@code codec}}
	 * 
	 * <p>Internally, the data is serialized as an NBT element of up to {@code 200000L}
	 * bytes.
	 */
	static <T> PacketCodec<ByteBuf, T> codec(Codec<T> codec) {
		return codec(codec, () -> NbtSizeTracker.of(2097152L));
	}

	static <T> PacketCodec<ByteBuf, T> codec(Codec<T> codec, Supplier<NbtSizeTracker> sizeTracker) {
		return nbt(sizeTracker)
			.xmap(
				nbt -> codec.parse(NbtOps.INSTANCE, nbt).getOrThrow(error -> new DecoderException("Failed to decode: " + error + " " + nbt)),
				value -> codec.encodeStart(NbtOps.INSTANCE, (T)value).getOrThrow(error -> new EncoderException("Failed to encode: " + error + " " + value))
			);
	}

	static <T> PacketCodec<RegistryByteBuf, T> unlimitedRegistryCodec(Codec<T> codec) {
		return registryCodec(codec, NbtSizeTracker::ofUnlimitedBytes);
	}

	static <T> PacketCodec<RegistryByteBuf, T> registryCodec(Codec<T> codec) {
		return registryCodec(codec, () -> NbtSizeTracker.of(2097152L));
	}

	static <T> PacketCodec<RegistryByteBuf, T> registryCodec(Codec<T> codec, Supplier<NbtSizeTracker> sizeTracker) {
		final PacketCodec<ByteBuf, NbtElement> packetCodec = nbt(sizeTracker);
		return new PacketCodec<RegistryByteBuf, T>() {
			public T decode(RegistryByteBuf registryByteBuf) {
				NbtElement nbtElement = packetCodec.decode(registryByteBuf);
				RegistryOps<NbtElement> registryOps = registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE);
				return codec.parse(registryOps, nbtElement).getOrThrow(error -> new DecoderException("Failed to decode: " + error + " " + nbtElement));
			}

			public void encode(RegistryByteBuf registryByteBuf, T object) {
				RegistryOps<NbtElement> registryOps = registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE);
				NbtElement nbtElement = codec.encodeStart(registryOps, object).getOrThrow(error -> new EncoderException("Failed to encode: " + error + " " + object));
				packetCodec.encode(registryByteBuf, nbtElement);
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

	static int readCollectionSize(ByteBuf buf, int maxSize) {
		int i = VarInts.read(buf);
		if (i > maxSize) {
			throw new DecoderException(i + " elements exceeded max size of: " + maxSize);
		} else {
			return i;
		}
	}

	static void writeCollectionSize(ByteBuf buf, int size, int maxSize) {
		if (size > maxSize) {
			throw new EncoderException(size + " elements exceeded max size of: " + maxSize);
		} else {
			VarInts.write(buf, size);
		}
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
		return collection(factory, elementCodec, Integer.MAX_VALUE);
	}

	static <B extends ByteBuf, V, C extends Collection<V>> PacketCodec<B, C> collection(
		IntFunction<C> factory, PacketCodec<? super B, V> elementCodec, int maxSize
	) {
		return new PacketCodec<B, C>() {
			public C decode(B byteBuf) {
				int i = PacketCodecs.readCollectionSize(byteBuf, maxSize);
				C collection = (C)factory.apply(Math.min(i, 65536));

				for (int j = 0; j < i; j++) {
					collection.add(elementCodec.decode(byteBuf));
				}

				return collection;
			}

			public void encode(B byteBuf, C collection) {
				PacketCodecs.writeCollectionSize(byteBuf, collection.size(), maxSize);

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

	static <B extends ByteBuf, V> PacketCodec.ResultFunction<B, V, List<V>> toList(int maxLength) {
		return codec -> collection(ArrayList::new, codec, maxLength);
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
		return map(factory, keyCodec, valueCodec, Integer.MAX_VALUE);
	}

	static <B extends ByteBuf, K, V, M extends Map<K, V>> PacketCodec<B, M> map(
		IntFunction<? extends M> factory, PacketCodec<? super B, K> keyCodec, PacketCodec<? super B, V> valueCodec, int maxSize
	) {
		return new PacketCodec<B, M>() {
			public void encode(B byteBuf, M map) {
				PacketCodecs.writeCollectionSize(byteBuf, map.size(), maxSize);
				map.forEach((k, v) -> {
					keyCodec.encode(byteBuf, (K)k);
					valueCodec.encode(byteBuf, (V)v);
				});
			}

			public M decode(B byteBuf) {
				int i = PacketCodecs.readCollectionSize(byteBuf, maxSize);
				M map = (M)factory.apply(Math.min(i, 65536));

				for (int j = 0; j < i; j++) {
					K object = keyCodec.decode(byteBuf);
					V object2 = valueCodec.decode(byteBuf);
					map.put(object, object2);
				}

				return map;
			}
		};
	}

	static <B extends ByteBuf, L, R> PacketCodec<B, Either<L, R>> either(PacketCodec<? super B, L> left, PacketCodec<? super B, R> right) {
		return new PacketCodec<B, Either<L, R>>() {
			public Either<L, R> decode(B byteBuf) {
				return byteBuf.readBoolean() ? Either.left(left.decode(byteBuf)) : Either.right(right.decode(byteBuf));
			}

			public void encode(B byteBuf, Either<L, R> either) {
				either.ifLeft(leftxx -> {
					byteBuf.writeBoolean(true);
					left.encode(byteBuf, (L)leftxx);
				}).ifRight(rightxx -> {
					byteBuf.writeBoolean(false);
					right.encode(byteBuf, (R)rightxx);
				});
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

	static <T> PacketCodec<RegistryByteBuf, RegistryEntryList<T>> registryEntryList(RegistryKey<? extends Registry<T>> registryRef) {
		return new PacketCodec<RegistryByteBuf, RegistryEntryList<T>>() {
			private static final int DIRECT_MARKER = -1;
			private final PacketCodec<RegistryByteBuf, RegistryEntry<T>> entryPacketCodec = PacketCodecs.registryEntry(registryRef);

			public RegistryEntryList<T> decode(RegistryByteBuf registryByteBuf) {
				int i = VarInts.read(registryByteBuf) - 1;
				if (i == -1) {
					Registry<T> registry = registryByteBuf.getRegistryManager().get(registryRef);
					return (RegistryEntryList<T>)registry.getEntryList(TagKey.of(registryRef, Identifier.PACKET_CODEC.decode(registryByteBuf))).orElseThrow();
				} else {
					List<RegistryEntry<T>> list = new ArrayList(Math.min(i, 65536));

					for (int j = 0; j < i; j++) {
						list.add(this.entryPacketCodec.decode(registryByteBuf));
					}

					return RegistryEntryList.of(list);
				}
			}

			public void encode(RegistryByteBuf registryByteBuf, RegistryEntryList<T> registryEntryList) {
				Optional<TagKey<T>> optional = registryEntryList.getTagKey();
				if (optional.isPresent()) {
					VarInts.write(registryByteBuf, 0);
					Identifier.PACKET_CODEC.encode(registryByteBuf, ((TagKey)optional.get()).id());
				} else {
					VarInts.write(registryByteBuf, registryEntryList.size() + 1);

					for (RegistryEntry<T> registryEntry : registryEntryList) {
						this.entryPacketCodec.encode(registryByteBuf, registryEntry);
					}
				}
			}
		};
	}
}
