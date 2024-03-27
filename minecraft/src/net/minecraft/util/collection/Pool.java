package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.math.random.Random;

public class Pool<E extends Weighted> {
	private final int totalWeight;
	private final ImmutableList<E> entries;

	Pool(List<? extends E> entries) {
		this.entries = ImmutableList.copyOf(entries);
		this.totalWeight = Weighting.getWeightSum(entries);
	}

	public static <E extends Weighted> Pool<E> empty() {
		return new Pool<>(ImmutableList.of());
	}

	@SafeVarargs
	public static <E extends Weighted> Pool<E> of(E... entries) {
		return new Pool<>(ImmutableList.copyOf(entries));
	}

	public static <E extends Weighted> Pool<E> of(List<E> entries) {
		return new Pool<>(entries);
	}

	public boolean isEmpty() {
		return this.entries.isEmpty();
	}

	public Optional<E> getOrEmpty(Random random) {
		if (this.totalWeight == 0) {
			return Optional.empty();
		} else {
			int i = random.nextInt(this.totalWeight);
			return Weighting.getAt(this.entries, i);
		}
	}

	public List<E> getEntries() {
		return this.entries;
	}

	public static <E extends Weighted> Codec<Pool<E>> createCodec(Codec<E> entryCodec) {
		return entryCodec.listOf().xmap(Pool::of, Pool::getEntries);
	}

	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Pool<?> pool = (Pool<?>)o;
			return this.totalWeight == pool.totalWeight && Objects.equals(this.entries, pool.entries);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.totalWeight, this.entries});
	}
}
