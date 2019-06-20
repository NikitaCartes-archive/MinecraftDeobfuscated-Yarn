package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class class_1949<T> implements class_1951<T> {
	protected final Predicate<T> field_9297;
	private final Function<T, class_2960> field_9294;
	private final Function<class_2960, T> field_19309;
	private final Set<class_1954<T>> field_9296 = Sets.<class_1954<T>>newHashSet();
	private final TreeSet<class_1954<T>> field_19341 = Sets.newTreeSet(class_1954.method_20597());
	private final class_3218 field_9301;
	private final Queue<class_1954<T>> field_9299 = Queues.<class_1954<T>>newArrayDeque();
	private final List<class_1954<T>> field_19338 = Lists.<class_1954<T>>newArrayList();
	private final Consumer<class_1954<T>> field_9300;

	public class_1949(
		class_3218 arg, Predicate<T> predicate, Function<T, class_2960> function, Function<class_2960, T> function2, Consumer<class_1954<T>> consumer
	) {
		this.field_9297 = predicate;
		this.field_9294 = function;
		this.field_19309 = function2;
		this.field_9301 = arg;
		this.field_9300 = consumer;
	}

	public void method_8670() {
		int i = this.field_19341.size();
		if (i != this.field_9296.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if (i > 65536) {
				i = 65536;
			}

			class_3215 lv = this.field_9301.method_14178();
			Iterator<class_1954<T>> iterator = this.field_19341.iterator();
			this.field_9301.method_16107().method_15396("cleaning");

			while (i > 0 && iterator.hasNext()) {
				class_1954<T> lv2 = (class_1954<T>)iterator.next();
				if (lv2.field_9321 > this.field_9301.method_8510()) {
					break;
				}

				if (lv.method_20529(lv2.field_9322)) {
					iterator.remove();
					this.field_9296.remove(lv2);
					this.field_9299.add(lv2);
					i--;
				}
			}

			this.field_9301.method_16107().method_15405("ticking");

			class_1954<T> lv2x;
			while ((lv2x = (class_1954<T>)this.field_9299.poll()) != null) {
				if (lv.method_20529(lv2x.field_9322)) {
					try {
						this.field_19338.add(lv2x);
						this.field_9300.accept(lv2x);
					} catch (Throwable var8) {
						class_128 lv3 = class_128.method_560(var8, "Exception while ticking");
						class_129 lv4 = lv3.method_562("Block being ticked");
						class_129.method_586(lv4, lv2x.field_9322, null);
						throw new class_148(lv3);
					}
				} else {
					this.method_8676(lv2x.field_9322, lv2x.method_8683(), 0);
				}
			}

			this.field_9301.method_16107().method_15407();
			this.field_19338.clear();
			this.field_9299.clear();
		}
	}

	@Override
	public boolean method_8677(class_2338 arg, T object) {
		return this.field_9299.contains(new class_1954(arg, object));
	}

	@Override
	public void method_20470(Stream<class_1954<T>> stream) {
		stream.forEach(this::method_20514);
	}

	public List<class_1954<T>> method_8671(class_1923 arg, boolean bl, boolean bl2) {
		int i = (arg.field_9181 << 4) - 2;
		int j = i + 16 + 2;
		int k = (arg.field_9180 << 4) - 2;
		int l = k + 16 + 2;
		return this.method_8672(new class_3341(i, 0, k, j, 256, l), bl, bl2);
	}

	public List<class_1954<T>> method_8672(class_3341 arg, boolean bl, boolean bl2) {
		List<class_1954<T>> list = this.method_20596(null, this.field_19341, arg, bl);
		if (bl && list != null) {
			this.field_9296.removeAll(list);
		}

		list = this.method_20596(list, this.field_9299, arg, bl);
		if (!bl2) {
			list = this.method_20596(list, this.field_19338, arg, bl);
		}

		return list == null ? Collections.emptyList() : list;
	}

	@Nullable
	private List<class_1954<T>> method_20596(@Nullable List<class_1954<T>> list, Collection<class_1954<T>> collection, class_3341 arg, boolean bl) {
		Iterator<class_1954<T>> iterator = collection.iterator();

		while (iterator.hasNext()) {
			class_1954<T> lv = (class_1954<T>)iterator.next();
			class_2338 lv2 = lv.field_9322;
			if (lv2.method_10263() >= arg.field_14381
				&& lv2.method_10263() < arg.field_14378
				&& lv2.method_10260() >= arg.field_14379
				&& lv2.method_10260() < arg.field_14376) {
				if (bl) {
					iterator.remove();
				}

				if (list == null) {
					list = Lists.<class_1954<T>>newArrayList();
				}

				list.add(lv);
			}
		}

		return list;
	}

	public void method_8666(class_3341 arg, class_2338 arg2) {
		for (class_1954<T> lv : this.method_8672(arg, false, false)) {
			if (arg.method_14662(lv.field_9322)) {
				class_2338 lv2 = lv.field_9322.method_10081(arg2);
				T object = lv.method_8683();
				this.method_20514(new class_1954<>(lv2, object, lv.field_9321, lv.field_9320));
			}
		}
	}

	public class_2499 method_8669(class_1923 arg) {
		List<class_1954<T>> list = this.method_8671(arg, false, true);
		return method_20469(this.field_9294, list, this.field_9301.method_8510());
	}

	public static <T> class_2499 method_20469(Function<T, class_2960> function, Iterable<class_1954<T>> iterable, long l) {
		class_2499 lv = new class_2499();

		for (class_1954<T> lv2 : iterable) {
			class_2487 lv3 = new class_2487();
			lv3.method_10582("i", ((class_2960)function.apply(lv2.method_8683())).toString());
			lv3.method_10569("x", lv2.field_9322.method_10263());
			lv3.method_10569("y", lv2.field_9322.method_10264());
			lv3.method_10569("z", lv2.field_9322.method_10260());
			lv3.method_10569("t", (int)(lv2.field_9321 - l));
			lv3.method_10569("p", lv2.field_9320.method_8681());
			lv.add(lv3);
		}

		return lv;
	}

	@Override
	public boolean method_8674(class_2338 arg, T object) {
		return this.field_9296.contains(new class_1954(arg, object));
	}

	@Override
	public void method_8675(class_2338 arg, T object, int i, class_1953 arg2) {
		if (!this.field_9297.test(object)) {
			this.method_20514(new class_1954<>(arg, object, (long)i + this.field_9301.method_8510(), arg2));
		}
	}

	private void method_20514(class_1954<T> arg) {
		if (!this.field_9296.contains(arg)) {
			this.field_9296.add(arg);
			this.field_19341.add(arg);
		}
	}
}
