package net.minecraft;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class class_2850<T> implements class_1951<T> {
	protected final Predicate<T> field_12991;
	private final class_1923 field_12993;
	private final ShortList[] field_12990 = new ShortList[16];

	public class_2850(Predicate<T> predicate, class_1923 arg) {
		this(predicate, arg, new class_2499());
	}

	public class_2850(Predicate<T> predicate, class_1923 arg, class_2499 arg2) {
		this.field_12991 = predicate;
		this.field_12993 = arg;

		for (int i = 0; i < arg2.size(); i++) {
			class_2499 lv = arg2.method_10603(i);

			for (int j = 0; j < lv.size(); j++) {
				class_2791.method_12026(this.field_12990, i).add(lv.method_10609(j));
			}
		}
	}

	public class_2499 method_12367() {
		return class_2852.method_12393(this.field_12990);
	}

	public void method_12368(class_1951<T> arg, Function<class_2338, T> function) {
		for (int i = 0; i < this.field_12990.length; i++) {
			if (this.field_12990[i] != null) {
				for (Short short_ : this.field_12990[i]) {
					class_2338 lv = class_2839.method_12314(short_, i, this.field_12993);
					arg.method_8676(lv, (T)function.apply(lv), 0);
				}

				this.field_12990[i].clear();
			}
		}
	}

	@Override
	public boolean method_8674(class_2338 arg, T object) {
		return false;
	}

	@Override
	public void method_8675(class_2338 arg, T object, int i, class_1953 arg2) {
		class_2791.method_12026(this.field_12990, arg.method_10264() >> 4).add(class_2839.method_12300(arg));
	}

	@Override
	public boolean method_8677(class_2338 arg, T object) {
		return false;
	}

	@Override
	public void method_20470(Stream<class_1954<T>> stream) {
		stream.forEach(arg -> this.method_8675(arg.field_9322, (T)arg.method_8683(), 0, arg.field_9320));
	}
}
