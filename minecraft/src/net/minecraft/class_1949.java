package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class class_1949<T> implements class_1951<T> {
	protected final Predicate<T> field_9297;
	protected final Function<T, class_2960> field_9294;
	protected final Function<class_2960, T> field_9298;
	protected final Set<class_1954<T>> field_9296 = Sets.<class_1954<T>>newHashSet();
	protected final TreeSet<class_1954<T>> field_9295 = new TreeSet();
	private final class_3218 field_9301;
	private final List<class_1954<T>> field_9299 = Lists.<class_1954<T>>newArrayList();
	private final Consumer<class_1954<T>> field_9300;

	public class_1949(
		class_3218 arg, Predicate<T> predicate, Function<T, class_2960> function, Function<class_2960, T> function2, Consumer<class_1954<T>> consumer
	) {
		this.field_9297 = predicate;
		this.field_9294 = function;
		this.field_9298 = function2;
		this.field_9301 = arg;
		this.field_9300 = consumer;
	}

	public void method_8670() {
		int i = this.field_9295.size();
		if (i != this.field_9296.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if (i > 65536) {
				i = 65536;
			}

			this.field_9301.method_16107().method_15396("cleaning");

			for (int j = 0; j < i; j++) {
				class_1954<T> lv = (class_1954<T>)this.field_9295.first();
				if (lv.field_9321 > this.field_9301.method_8510()) {
					break;
				}

				this.field_9295.remove(lv);
				this.field_9296.remove(lv);
				this.field_9299.add(lv);
			}

			this.field_9301.method_16107().method_15407();
			this.field_9301.method_16107().method_15396("ticking");
			Iterator<class_1954<T>> iterator = this.field_9299.iterator();

			while (iterator.hasNext()) {
				class_1954<T> lv = (class_1954<T>)iterator.next();
				iterator.remove();
				if (this.field_9301.method_8591(lv.field_9322)) {
					try {
						this.field_9300.accept(lv);
					} catch (Throwable var7) {
						class_128 lv2 = class_128.method_560(var7, "Exception while ticking");
						class_129 lv3 = lv2.method_562("Block being ticked");
						class_129.method_586(lv3, lv.field_9322, null);
						throw new class_148(lv2);
					}
				} else {
					this.method_8676(lv.field_9322, lv.method_8683(), 0);
				}
			}

			this.field_9301.method_16107().method_15407();
			this.field_9299.clear();
		}
	}

	@Override
	public boolean method_8677(class_2338 arg, T object) {
		return this.field_9299.contains(new class_1954(arg, object));
	}

	public List<class_1954<T>> method_8671(boolean bl, class_1923 arg) {
		int i = (arg.field_9181 << 4) - 2;
		int j = i + 16 + 2;
		int k = (arg.field_9180 << 4) - 2;
		int l = k + 16 + 2;
		return this.method_8672(new class_3341(i, 0, k, j, 256, l), bl);
	}

	public List<class_1954<T>> method_8672(class_3341 arg, boolean bl) {
		List<class_1954<T>> list = null;

		for (int i = 0; i < 2; i++) {
			Iterator<class_1954<T>> iterator;
			if (i == 0) {
				iterator = this.field_9295.iterator();
			} else {
				iterator = this.field_9299.iterator();
			}

			while (iterator.hasNext()) {
				class_1954<T> lv = (class_1954<T>)iterator.next();
				class_2338 lv2 = lv.field_9322;
				if (lv2.method_10263() >= arg.field_14381
					&& lv2.method_10263() < arg.field_14378
					&& lv2.method_10260() >= arg.field_14379
					&& lv2.method_10260() < arg.field_14376) {
					if (bl) {
						if (i == 0) {
							this.field_9296.remove(lv);
						}

						iterator.remove();
					}

					if (list == null) {
						list = Lists.<class_1954<T>>newArrayList();
					}

					list.add(lv);
				}
			}
		}

		return list == null ? Collections.emptyList() : list;
	}

	public void method_8666(class_3341 arg, class_2338 arg2) {
		for (class_1954<T> lv : this.method_8672(arg, false)) {
			if (arg.method_14662(lv.field_9322)) {
				class_2338 lv2 = lv.field_9322.method_10081(arg2);
				this.method_8668(lv2, lv.method_8683(), (int)(lv.field_9321 - this.field_9301.method_8401().method_188()), lv.field_9320);
			}
		}
	}

	public class_2499 method_8669(class_1923 arg) {
		List<class_1954<T>> list = this.method_8671(false, arg);
		long l = this.field_9301.method_8510();
		class_2499 lv = new class_2499();

		for (class_1954<T> lv2 : list) {
			class_2487 lv3 = new class_2487();
			lv3.method_10582("i", ((class_2960)this.field_9294.apply(lv2.method_8683())).toString());
			lv3.method_10569("x", lv2.field_9322.method_10263());
			lv3.method_10569("y", lv2.field_9322.method_10264());
			lv3.method_10569("z", lv2.field_9322.method_10260());
			lv3.method_10569("t", (int)(lv2.field_9321 - l));
			lv3.method_10569("p", lv2.field_9320.method_8681());
			lv.add(lv3);
		}

		return lv;
	}

	public void method_8665(class_2499 arg) {
		for (int i = 0; i < arg.size(); i++) {
			class_2487 lv = arg.method_10602(i);
			T object = (T)this.field_9298.apply(new class_2960(lv.method_10558("i")));
			if (object != null) {
				this.method_8668(
					new class_2338(lv.method_10550("x"), lv.method_10550("y"), lv.method_10550("z")),
					object,
					lv.method_10550("t"),
					class_1953.method_8680(lv.method_10550("p"))
				);
			}
		}
	}

	@Override
	public boolean method_8674(class_2338 arg, T object) {
		return this.field_9296.contains(new class_1954(arg, object));
	}

	@Override
	public void method_8675(class_2338 arg, T object, int i, class_1953 arg2) {
		if (!this.field_9297.test(object)) {
			this.method_8667(arg, object, i, arg2);
		}
	}

	protected void method_8668(class_2338 arg, T object, int i, class_1953 arg2) {
		if (!this.field_9297.test(object)) {
			this.method_8667(arg, object, i, arg2);
		}
	}

	private void method_8667(class_2338 arg, T object, int i, class_1953 arg2) {
		class_1954<T> lv = new class_1954<>(arg, object, (long)i + this.field_9301.method_8510(), arg2);
		if (!this.field_9296.contains(lv)) {
			this.field_9296.add(lv);
			this.field_9295.add(lv);
		}
	}
}
