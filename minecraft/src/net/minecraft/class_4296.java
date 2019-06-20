package net.minecraft;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class class_4296<T> implements class_1951<T> {
	private final Set<class_1954<T>> field_19275;
	private final Function<T, class_2960> field_19276;

	public class_4296(Function<T, class_2960> function, List<class_1954<T>> list) {
		this(function, Sets.<class_1954<T>>newHashSet(list));
	}

	private class_4296(Function<T, class_2960> function, Set<class_1954<T>> set) {
		this.field_19275 = set;
		this.field_19276 = function;
	}

	@Override
	public boolean method_8674(class_2338 arg, T object) {
		return false;
	}

	@Override
	public void method_8675(class_2338 arg, T object, int i, class_1953 arg2) {
		this.field_19275.add(new class_1954(arg, object, (long)i, arg2));
	}

	@Override
	public boolean method_8677(class_2338 arg, T object) {
		return false;
	}

	@Override
	public void method_20470(Stream<class_1954<T>> stream) {
		stream.forEach(this.field_19275::add);
	}

	public Stream<class_1954<T>> method_20462() {
		return this.field_19275.stream();
	}

	public class_2499 method_20463(long l) {
		return class_1949.method_20469(this.field_19276, this.field_19275, l);
	}

	public static <T> class_4296<T> method_20512(class_2499 arg, Function<T, class_2960> function, Function<class_2960, T> function2) {
		Set<class_1954<T>> set = Sets.<class_1954<T>>newHashSet();

		for (int i = 0; i < arg.size(); i++) {
			class_2487 lv = arg.method_10602(i);
			T object = (T)function2.apply(new class_2960(lv.method_10558("i")));
			if (object != null) {
				set.add(
					new class_1954(
						new class_2338(lv.method_10550("x"), lv.method_10550("y"), lv.method_10550("z")),
						object,
						(long)lv.method_10550("t"),
						class_1953.method_8680(lv.method_10550("p"))
					)
				);
			}
		}

		return new class_4296<>(function, set);
	}
}
