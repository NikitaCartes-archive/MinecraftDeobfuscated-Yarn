package net.minecraft;

public class class_1508 extends class_1297 {
	public final class_1510 field_7007;
	public final String field_7006;
	private final class_4048 field_18119;

	public class_1508(class_1510 arg, String string, float f, float g) {
		super(arg.method_5864(), arg.field_6002);
		this.field_18119 = class_4048.method_18384(f, g);
		this.method_18382();
		this.field_7007 = arg;
		this.field_7006 = string;
	}

	@Override
	protected void method_5693() {
	}

	@Override
	protected void method_5749(class_2487 arg) {
	}

	@Override
	protected void method_5652(class_2487 arg) {
	}

	@Override
	public boolean method_5863() {
		return true;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		return this.method_5679(arg) ? false : this.field_7007.method_6816(this, arg, f);
	}

	@Override
	public boolean method_5779(class_1297 arg) {
		return this == arg || this.field_7007 == arg;
	}

	@Override
	public class_2596<?> method_18002() {
		throw new UnsupportedOperationException();
	}

	@Override
	public class_4048 method_18377(class_4050 arg) {
		return this.field_18119;
	}
}
