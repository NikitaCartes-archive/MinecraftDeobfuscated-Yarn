package net.minecraft;

public interface class_3662 extends class_3660, class_3739 {
	int method_15867(class_3630 arg, int i, int j, int k, int l, int m);

	@Override
	default int method_15863(class_3628<?> arg, class_3625 arg2, int i, int j) {
		return this.method_15867(
			arg,
			arg2.method_15825(this.method_16342(i + 0), this.method_16343(j + 2)),
			arg2.method_15825(this.method_16342(i + 2), this.method_16343(j + 2)),
			arg2.method_15825(this.method_16342(i + 2), this.method_16343(j + 0)),
			arg2.method_15825(this.method_16342(i + 0), this.method_16343(j + 0)),
			arg2.method_15825(this.method_16342(i + 1), this.method_16343(j + 1))
		);
	}
}
