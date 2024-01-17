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
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.encoding.VarLongs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface PacketCodecs {
	PacketCodec<ByteBuf, Boolean> BOOL = new PacketCodec<ByteBuf, Boolean>() {
		public Boolean decode(ByteBuf byteBuf) {
			return byteBuf.readBoolean();
		}

		public void encode(ByteBuf byteBuf, Boolean boolean_) {
			byteBuf.writeBoolean(boolean_);
		}
	};
	PacketCodec<ByteBuf, Byte> BYTE = new PacketCodec<ByteBuf, Byte>() {
		public Byte decode(ByteBuf byteBuf) {
			return byteBuf.readByte();
		}

		public void encode(ByteBuf byteBuf, Byte byte_) {
			byteBuf.writeByte(byte_);
		}
	};
	PacketCodec<ByteBuf, Short> SHORT = new PacketCodec<ByteBuf, Short>() {
		public Short decode(ByteBuf byteBuf) {
			return byteBuf.readShort();
		}

		public void encode(ByteBuf byteBuf, Short short_) {
			byteBuf.writeShort(short_);
		}
	};
	PacketCodec<ByteBuf, Integer> VAR_INT = new PacketCodec<ByteBuf, Integer>() {
		public Integer decode(ByteBuf byteBuf) {
			return VarInts.read(byteBuf);
		}

		public void encode(ByteBuf byteBuf, Integer integer) {
			VarInts.write(byteBuf, integer);
		}
	};
	PacketCodec<ByteBuf, Long> VAR_LONG = new PacketCodec<ByteBuf, Long>() {
		public Long decode(ByteBuf byteBuf) {
			return VarLongs.read(byteBuf);
		}

		public void encode(ByteBuf byteBuf, Long long_) {
			VarLongs.write(byteBuf, long_);
		}
	};
	PacketCodec<ByteBuf, Float> FLOAT = new PacketCodec<ByteBuf, Float>() {
		public Float decode(ByteBuf byteBuf) {
			return byteBuf.readFloat();
		}

		public void encode(ByteBuf byteBuf, Float float_) {
			byteBuf.writeFloat(float_);
		}
	};
	PacketCodec<ByteBuf, Double> DOUBLE = new PacketCodec<ByteBuf, Double>() {
		public Double decode(ByteBuf byteBuf) {
			return byteBuf.readDouble();
		}

		public void encode(ByteBuf byteBuf, Double double_) {
			byteBuf.writeDouble(double_);
		}
	};
	PacketCodec<ByteBuf, String> STRING = string(32767);
	PacketCodec<ByteBuf, NbtElement> NBT_ELEMENT = nbt(NbtSizeTracker::ofUnlimitedBytes);
	PacketCodec<ByteBuf, NbtCompound> NBT_COMPUND = nbt(NbtSizeTracker::ofUnlimitedBytes).xmap(nbt -> {
		if (nbt instanceof NbtCompound) {
			return (NbtCompound)nbt;
		} else {
			throw new DecoderException("Not a compound tag: " + nbt);
		}
	}, nbt -> nbt);
	PacketCodec<ByteBuf, Optional<NbtCompound>> OPTIONAL_NBT = new PacketCodec<ByteBuf, Optional<NbtCompound>>() {
		public Optional<NbtCompound> decode(ByteBuf byteBuf) {
			return Optional.ofNullable(PacketByteBuf.readNbt(byteBuf));
		}

		public void encode(ByteBuf byteBuf, Optional<NbtCompound> optional) {
			PacketByteBuf.writeNbt(byteBuf, (NbtElement)optional.orElse(null));
		}
	};
	PacketCodec<ByteBuf, Vector3f> VECTOR3F = new PacketCodec<ByteBuf, Vector3f>() {
		public Vector3f decode(ByteBuf byteBuf) {
			return PacketByteBuf.readVector3f(byteBuf);
		}

		public void encode(ByteBuf byteBuf, Vector3f vector3f) {
			PacketByteBuf.writeVector3f(byteBuf, vector3f);
		}
	};
	PacketCodec<ByteBuf, Quaternionf> QUATERNION = new PacketCodec<ByteBuf, Quaternionf>() {
		public Quaternionf decode(ByteBuf byteBuf) {
			return PacketByteBuf.readQuaternionf(byteBuf);
		}

		public void encode(ByteBuf byteBuf, Quaternionf quaternionf) {
			PacketByteBuf.writeQuaternionf(byteBuf, quaternionf);
		}
	};

	static PacketCodec<ByteBuf, String> string(int length) {
		return new PacketCodec<ByteBuf, String>() {
			public String decode(ByteBuf byteBuf) {
				return StringEncoding.decode(byteBuf, length);
			}

			public void encode(ByteBuf byteBuf, String string) {
				StringEncoding.encode(byteBuf, string, length);
			}
		};
	}

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

	static <T> PacketCodec<ByteBuf, T> ofCodec(Codec<T> codec) {
		return NBT_ELEMENT.xmap(
			nbt -> Util.getResult(codec.parse(NbtOps.INSTANCE, nbt), error -> new DecoderException("Failed to decode: " + error + " " + nbt)),
			value -> Util.getResult(codec.encodeStart(NbtOps.INSTANCE, (T)value), error -> new EncoderException("Failed to encode: " + error + " " + value))
		);
	}

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

	static <B extends ByteBuf, V, C extends Collection<V>> PacketCodec.ResultFunction<B, V, C> collectionMapper(IntFunction<C> factory) {
		return codec -> collection(factory, codec);
	}

	static <B extends ByteBuf, V> PacketCodec.ResultFunction<B, V, List<V>> listMapper() {
		return codec -> collection(ArrayList::new, codec);
	}

	static <B extends ByteBuf, K, V, M extends Map<K, V>> PacketCodec<B, M> map(
		IntFunction<? extends M> factory, PacketCodec<? super B, K> keyCodec, PacketCodec<? super B, V> valueCodec
	) {
		return new PacketCodec<B, M>() {
			public void encode(B byteBuf, M map) {
				VarInts.write(byteBuf, map.size());
				map.forEach((object, object2) -> {
					keyCodec.encode(byteBuf, (K)object);
					valueCodec.encode(byteBuf, (V)object2);
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

	static <T> PacketCodec<ByteBuf, T> indexed(IntFunction<T> from, ToIntFunction<T> to) {
		return new PacketCodec<ByteBuf, T>() {
			public T decode(ByteBuf byteBuf) {
				int i = VarInts.read(byteBuf);
				return (T)from.apply(i);
			}

			public void encode(ByteBuf byteBuf, T object) {
				int i = to.applyAsInt(object);
				VarInts.write(byteBuf, i);
			}
		};
	}

	static <T> PacketCodec<ByteBuf, T> ofIterable(IndexedIterable<T> iterable) {
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

	static <T> PacketCodec<RegistryByteBuf, T> registry(RegistryKey<? extends Registry<T>> registry) {
		return registry(registry, registryx -> registryx);
	}

	static <T> PacketCodec<RegistryByteBuf, RegistryEntry<T>> registryEntry(RegistryKey<? extends Registry<T>> registry) {
		return registry(registry, Registry::getIndexedEntries);
	}

	static <T> PacketCodec<RegistryByteBuf, RegistryEntry<T>> registryEntry(
		RegistryKey<? extends Registry<T>> registry, PacketCodec<? super RegistryByteBuf, T> fallback
	) {
		return new PacketCodec<RegistryByteBuf, RegistryEntry<T>>() {
			private static final int DIRECT_ENTRY_MARKER = 0;

			private IndexedIterable<RegistryEntry<T>> getEntries(RegistryByteBuf buf) {
				return buf.getRegistryManager().get(registry).getIndexedEntries();
			}

			public RegistryEntry<T> decode(RegistryByteBuf registryByteBuf) {
				int i = VarInts.read(registryByteBuf);
				return i == 0 ? RegistryEntry.of(fallback.decode(registryByteBuf)) : (RegistryEntry)this.getEntries(registryByteBuf).getOrThrow(i - 1);
			}

			public void encode(RegistryByteBuf registryByteBuf, RegistryEntry<T> registryEntry) {
				switch (registryEntry.getType()) {
					case REFERENCE:
						int i = this.getEntries(registryByteBuf).getRawIdOrThrow(registryEntry);
						VarInts.write(registryByteBuf, i + 1);
						break;
					case DIRECT:
						VarInts.write(registryByteBuf, 0);
						fallback.encode(registryByteBuf, registryEntry.value());
				}
			}
		};
	}
}
