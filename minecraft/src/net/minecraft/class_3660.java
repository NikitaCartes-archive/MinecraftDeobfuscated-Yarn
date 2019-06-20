package net.minecraft;

public interface class_3660 extends class_3741 {
	default <R extends class_3625> class_3627<R> method_15862(class_3628<R> arg, class_3627<R> arg2) {
		return () -> {
			R lv = arg2.make();
			return arg.method_15832((i, j) -> {
				arg.method_15830((long)i, (long)j);
				return this.method_15863(arg, lv, i, j);
			}, lv);
		};
	}

	int method_15863(class_3628<?> arg, class_3625 arg2, int i, int j);
}
