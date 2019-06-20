package net.minecraft;

public class class_4099 implements class_4115 {
	private final class_2338 field_18340;
	private final class_243 field_18341;

	public class_4099(class_2338 arg) {
		this.field_18340 = arg;
		this.field_18341 = new class_243((double)arg.method_10263() + 0.5, (double)arg.method_10264() + 0.5, (double)arg.method_10260() + 0.5);
	}

	@Override
	public class_2338 method_18989() {
		return this.field_18340;
	}

	@Override
	public class_243 method_18991() {
		return this.field_18341;
	}

	@Override
	public boolean method_18990(class_1309 arg) {
		return true;
	}
}
