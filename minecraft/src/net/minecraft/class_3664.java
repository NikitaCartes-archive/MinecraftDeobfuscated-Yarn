package net.minecraft;

public interface class_3664 extends class_3660, class_3739 {
	int method_15869(class_3630 arg, int i);

	@Override
	default int method_15863(class_3628<?> arg, class_3625 arg2, int i, int j) {
		int k = arg2.method_15825(this.method_16342(i + 1), this.method_16343(j + 1));
		return this.method_15869(arg, k);
	}
}
