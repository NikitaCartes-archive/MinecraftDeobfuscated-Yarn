package net.minecraft.util.dynamic;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.NbtElement;

public class CodecCache {
	final LoadingCache<CodecCache.Key<?, ?>, DataResult<?>> cache;

	public CodecCache(int size) {
		this.cache = CacheBuilder.newBuilder()
			.maximumSize((long)size)
			.concurrencyLevel(1)
			.softValues()
			.build(new CacheLoader<CodecCache.Key<?, ?>, DataResult<?>>() {
				public DataResult<?> load(CodecCache.Key<?, ?> key) {
					return key.encode();
				}
			});
	}

	public <A> Codec<A> wrap(Codec<A> codec) {
		return new Codec<A>() {
			@Override
			public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
				return codec.decode(ops, input);
			}

			@Override
			public <T> DataResult<T> encode(A value, DynamicOps<T> ops, T prefix) {
				return CodecCache.this.cache
					.getUnchecked(new CodecCache.Key<>(codec, value, ops))
					.map(object -> object instanceof NbtElement nbtElement ? nbtElement.copy() : object);
			}
		};
	}

	static record Key<A, T>(Codec<A> codec, A value, DynamicOps<T> ops) {
		public DataResult<T> encode() {
			return this.codec.encodeStart(this.ops, this.value);
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else {
				return !(o instanceof CodecCache.Key<?, ?> key) ? false : this.codec == key.codec && this.value.equals(key.value) && this.ops.equals(key.ops);
			}
		}

		public int hashCode() {
			int i = System.identityHashCode(this.codec);
			i = 31 * i + this.value.hashCode();
			return 31 * i + this.ops.hashCode();
		}
	}
}
