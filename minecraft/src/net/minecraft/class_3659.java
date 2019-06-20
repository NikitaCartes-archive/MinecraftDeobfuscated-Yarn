package net.minecraft;

public interface class_3659 extends class_3741 {
	default <R extends class_3625> class_3627<R> method_15860(class_3628<R> arg, class_3627<R> arg2, class_3627<R> arg3) {
		return () -> {
			R lv = arg2.make();
			R lv2 = arg3.make();
			return arg.method_15828((i, j) -> {
				arg.method_15830((long)i, (long)j);
				return this.method_15861(arg, lv, lv2, i, j);
			}, lv, lv2);
		};
	}

	int method_15861(class_3630 arg, class_3625 arg2, class_3625 arg3, int i, int j);
}
