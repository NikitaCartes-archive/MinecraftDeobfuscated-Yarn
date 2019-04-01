package net.minecraft;

import java.util.function.Function;
import java.util.function.Supplier;

public class class_1969<C extends class_1970, T extends class_1966> {
	public static final class_1969<class_1976, class_1973> field_9398 = method_8773("checkerboard", class_1973::new, class_1976::new);
	public static final class_1969<class_1991, class_1992> field_9401 = method_8773("fixed", class_1992::new, class_1991::new);
	public static final class_1969<class_2084, class_2088> field_9402 = method_8773("vanilla_layered", class_2088::new, class_2084::new);
	public static final class_1969<class_2167, class_2169> field_9399 = method_8773("the_end", class_2169::new, class_2167::new);
	private final Function<C, T> field_9403;
	private final Supplier<C> field_9400;

	private static <C extends class_1970, T extends class_1966> class_1969<C, T> method_8773(String string, Function<C, T> function, Supplier<C> supplier) {
		return class_2378.method_10226(class_2378.field_11151, string, new class_1969<>(function, supplier));
	}

	public class_1969(Function<C, T> function, Supplier<C> supplier) {
		this.field_9403 = function;
		this.field_9400 = supplier;
	}

	public T method_8772(C arg) {
		return (T)this.field_9403.apply(arg);
	}

	public C method_8774() {
		return (C)this.field_9400.get();
	}
}
