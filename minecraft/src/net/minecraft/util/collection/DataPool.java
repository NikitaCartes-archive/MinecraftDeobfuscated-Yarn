package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public class DataPool<E> extends Pool<Weighted.Present<E>> {
	public static <E> Codec<DataPool<E>> createEmptyAllowedCodec(Codec<E> dataCodec) {
		return Weighted.Present.createCodec(dataCodec).listOf().xmap(DataPool::new, Pool::getEntries);
	}

	public static <E> Codec<DataPool<E>> createCodec(Codec<E> dataCodec) {
		return Codecs.nonEmptyList(Weighted.Present.createCodec(dataCodec).listOf()).xmap(DataPool::new, Pool::getEntries);
	}

	DataPool(List<? extends Weighted.Present<E>> list) {
		super(list);
	}

	public static <E> DataPool.Builder<E> builder() {
		return new DataPool.Builder<>();
	}

	public static <E> DataPool<E> empty() {
		return new DataPool<>(List.of());
	}

	public static <E> DataPool<E> of(E object) {
		return new DataPool<>(List.of(Weighted.of(object, 1)));
	}

	public Optional<E> getDataOrEmpty(Random random) {
		return this.getOrEmpty(random).map(Weighted.Present::data);
	}

	public static class Builder<E> {
		private final ImmutableList.Builder<Weighted.Present<E>> entries = ImmutableList.builder();

		public DataPool.Builder<E> add(E object) {
			return this.add(object, 1);
		}

		public DataPool.Builder<E> add(E object, int weight) {
			this.entries.add(Weighted.of(object, weight));
			return this;
		}

		public DataPool<E> build() {
			return new DataPool<>(this.entries.build());
		}
	}
}
