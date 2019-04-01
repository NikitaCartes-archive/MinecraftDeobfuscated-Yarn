package net.minecraft;

public class class_2599 extends class_2586 {
	private int field_11943;

	public class_2599() {
		super(class_2591.field_11908);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		arg.method_10569("OutputSignal", this.field_11943);
		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_11943 = arg.method_10550("OutputSignal");
	}

	public int method_11071() {
		return this.field_11943;
	}

	public void method_11070(int i) {
		this.field_11943 = i;
	}
}
