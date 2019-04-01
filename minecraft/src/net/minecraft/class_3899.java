package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class class_3899<T> {
	public static final int field_17241 = class_3898.field_18239 + 2;
	private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> field_17243 = (List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>>)IntStream.range(
			0, field_17241
		)
		.mapToObj(ix -> new Long2ObjectLinkedOpenHashMap())
		.collect(Collectors.toList());
	private volatile int field_17244 = field_17241;
	private final String field_17247;
	private final LongSet field_17444 = new LongOpenHashSet();
	private final int field_17445;

	public class_3899(String string, int i) {
		this.field_17247 = string;
		this.field_17445 = i;
	}

	protected void method_17272(int i, class_1923 arg, int j) {
		if (i < field_17241) {
			Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap<List<Optional<T>>>)this.field_17243.get(i);
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.remove(arg.method_8324());
			if (i == this.field_17244) {
				while (this.field_17244 < field_17241 && ((Long2ObjectLinkedOpenHashMap)this.field_17243.get(this.field_17244)).isEmpty()) {
					this.field_17244++;
				}
			}

			if (list != null && !list.isEmpty()) {
				((List)((Long2ObjectLinkedOpenHashMap)this.field_17243.get(j)).computeIfAbsent(arg.method_8324(), l -> Lists.newArrayList())).addAll(list);
				this.field_17244 = Math.min(this.field_17244, j);
			}
		}
	}

	protected void method_17274(Optional<T> optional, long l, int i) {
		((List)((Long2ObjectLinkedOpenHashMap)this.field_17243.get(i)).computeIfAbsent(l, lx -> Lists.newArrayList())).add(optional);
		this.field_17244 = Math.min(this.field_17244, i);
	}

	protected void method_17609(long l, boolean bl) {
		for (Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap : this.field_17243) {
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.get(l);
			if (list != null) {
				if (bl) {
					list.clear();
				} else {
					list.removeIf(optional -> !optional.isPresent());
				}

				if (list.isEmpty()) {
					long2ObjectLinkedOpenHashMap.remove(l);
				}
			}
		}

		while (this.field_17244 < field_17241 && ((Long2ObjectLinkedOpenHashMap)this.field_17243.get(this.field_17244)).isEmpty()) {
			this.field_17244++;
		}

		this.field_17444.remove(l);
	}

	private Runnable method_17607(long l) {
		return () -> this.field_17444.add(l);
	}

	@Nullable
	public Stream<Either<T, Runnable>> method_17606() {
		if (this.field_17444.size() >= this.field_17445) {
			return null;
		} else if (this.field_17244 >= field_17241) {
			return null;
		} else {
			int i = this.field_17244;
			Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap<List<Optional<T>>>)this.field_17243.get(i);
			long l = long2ObjectLinkedOpenHashMap.firstLongKey();
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.removeFirst();

			while (this.field_17244 < field_17241 && ((Long2ObjectLinkedOpenHashMap)this.field_17243.get(this.field_17244)).isEmpty()) {
				this.field_17244++;
			}

			return list.stream().map(optional -> (Either)optional.map(Either::left).orElseGet(() -> Either.right(this.method_17607(l))));
		}
	}

	public String toString() {
		return this.field_17247 + " " + this.field_17244 + "...";
	}
}
