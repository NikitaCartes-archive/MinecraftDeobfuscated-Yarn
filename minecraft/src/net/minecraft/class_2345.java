package net.minecraft;

public class class_2345 implements class_2342 {
	private final class_1937 field_11011;
	private final class_2338 field_11012;

	public class_2345(class_1937 arg, class_2338 arg2) {
		this.field_11011 = arg;
		this.field_11012 = arg2;
	}

	@Override
	public class_1937 method_10207() {
		return this.field_11011;
	}

	@Override
	public double method_10216() {
		return (double)this.field_11012.method_10263() + 0.5;
	}

	@Override
	public double method_10214() {
		return (double)this.field_11012.method_10264() + 0.5;
	}

	@Override
	public double method_10215() {
		return (double)this.field_11012.method_10260() + 0.5;
	}

	@Override
	public class_2338 method_10122() {
		return this.field_11012;
	}

	@Override
	public class_2680 method_10120() {
		return this.field_11011.method_8320(this.field_11012);
	}

	@Override
	public <T extends class_2586> T method_10121() {
		return (T)this.field_11011.method_8321(this.field_11012);
	}
}
