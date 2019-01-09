package net.minecraft;

public interface class_3658 {
	default <R extends class_3625> class_3627<R> method_15854(class_3628<R> arg) {
		return () -> arg.method_15831((i, j) -> {
				arg.method_15830((long)i, (long)j);
				return this.method_15855(arg, i, j);
			});
	}

	int method_15855(class_3630 arg, int i, int j);
}
