package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class DataPool<E> extends Pool<Weighted.Present<E>> {
	public static <E> Codec<DataPool<E>> createCodec(Codec<E> dataCodec) {
		return Weighted.Present.createCodec(dataCodec).listOf().xmap(DataPool::new, Pool::getEntries);
	}

	DataPool(List<? extends Weighted.Present<E>> list) {
		super(list);
	}

	public static <E> DataPool.Builder<E> builder() {
		return new DataPool.Builder<>();
	}

	public Optional<E> getDataOrEmpty(Random random) {
		return this.getOrEmpty(random).map(Weighted.Present::getData);
	}

	public static class Builder<E> {
		private final ImmutableList.Builder<Weighted.Present<E>> entries = ImmutableList.builder();

		public DataPool.Builder<E> add(E object, int weight) {
			this.entries.add(Weighted.of(object, weight));
			return this;
		}

		public DataPool<E> build() {
			return new DataPool<>(this.entries.build());
		}
	}
}
