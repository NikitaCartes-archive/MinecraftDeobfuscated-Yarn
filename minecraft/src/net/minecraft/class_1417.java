package net.minecraft;

public class class_1417 {
	private final class_2338 field_6713;
	private final class_2338 field_6712;
	private final class_2350 field_6711;
	private int field_6709;
	private boolean field_6710;
	private int field_6708;

	public class_1417(class_2338 arg, int i, int j, int k) {
		this(arg, method_6427(i, j), k);
	}

	private static class_2350 method_6427(int i, int j) {
		if (i < 0) {
			return class_2350.field_11039;
		} else if (i > 0) {
			return class_2350.field_11034;
		} else {
			return j < 0 ? class_2350.field_11043 : class_2350.field_11035;
		}
	}

	public class_1417(class_2338 arg, class_2350 arg2, int i) {
		this.field_6713 = arg.method_10062();
		this.field_6711 = arg2;
		this.field_6712 = arg.method_10079(arg2, 2);
		this.field_6709 = i;
	}

	public int method_6415(int i, int j, int k) {
		return (int)this.field_6713.method_10261((double)i, (double)j, (double)k);
	}

	public int method_6423(class_2338 arg) {
		return (int)arg.method_10262(this.method_6429());
	}

	public int method_6417(class_2338 arg) {
		return (int)this.field_6712.method_10262(arg);
	}

	public boolean method_6425(class_2338 arg) {
		int i = arg.method_10263() - this.field_6713.method_10263();
		int j = arg.method_10260() - this.field_6713.method_10264();
		return i * this.field_6711.method_10148() + j * this.field_6711.method_10165() >= 0;
	}

	public void method_6426() {
		this.field_6708 = 0;
	}

	public void method_6428() {
		this.field_6708++;
	}

	public int method_6416() {
		return this.field_6708;
	}

	public class_2338 method_6429() {
		return this.field_6713;
	}

	public class_2338 method_6422() {
		return this.field_6712;
	}

	public int method_6419() {
		return this.field_6711.method_10148() * 2;
	}

	public int method_6420() {
		return this.field_6711.method_10165() * 2;
	}

	public int method_6421() {
		return this.field_6709;
	}

	public void method_6414(int i) {
		this.field_6709 = i;
	}

	public boolean method_6413() {
		return this.field_6710;
	}

	public void method_6418(boolean bl) {
		this.field_6710 = bl;
	}

	public class_2350 method_6424() {
		return this.field_6711;
	}
}
