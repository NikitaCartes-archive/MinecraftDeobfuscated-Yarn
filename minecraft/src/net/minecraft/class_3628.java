package net.minecraft;

public interface class_3628<R extends class_3625> extends class_3630 {
	void method_15830(long l, long m);

	R method_15831(class_4 arg);

	default R method_15832(class_4 arg, R arg2) {
		return this.method_15831(arg);
	}

	default R method_15828(class_4 arg, R arg2, R arg3) {
		return this.method_15831(arg);
	}

	default int method_16670(int i, int j) {
		return this.method_15834(2) == 0 ? i : j;
	}

	default int method_16669(int i, int j, int k, int l) {
		int m = this.method_15834(4);
		if (m == 0) {
			return i;
		} else if (m == 1) {
			return j;
		} else {
			return m == 2 ? k : l;
		}
	}
}
