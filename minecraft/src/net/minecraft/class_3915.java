package net.minecraft;

public abstract class class_3915 {
	private int field_17307;

	public static class_3915 method_17405(class_3913 arg, int i) {
		return new class_3915() {
			@Override
			public int method_17407() {
				return arg.method_17390(i);
			}

			@Override
			public void method_17404(int i) {
				arg.method_17391(i, i);
			}
		};
	}

	public static class_3915 method_17406(int[] is, int i) {
		return new class_3915() {
			@Override
			public int method_17407() {
				return is[i];
			}

			@Override
			public void method_17404(int i) {
				is[i] = i;
			}
		};
	}

	public static class_3915 method_17403() {
		return new class_3915() {
			private int field_17312;

			@Override
			public int method_17407() {
				return this.field_17312;
			}

			@Override
			public void method_17404(int i) {
				this.field_17312 = i;
			}
		};
	}

	public abstract int method_17407();

	public abstract void method_17404(int i);

	public boolean method_17408() {
		int i = this.method_17407();
		boolean bl = i != this.field_17307;
		this.field_17307 = i;
		return bl;
	}
}
