package net.minecraft;

public interface class_3661 extends class_3660, class_3740 {
	int method_15866(class_3630 arg, int i);

	@Override
	default int method_15863(class_3628<?> arg, class_3625 arg2, int i, int j) {
		return this.method_15866(arg, arg2.method_15825(this.method_16342(i), this.method_16343(j)));
	}
}
