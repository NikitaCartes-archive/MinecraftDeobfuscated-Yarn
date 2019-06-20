package net.minecraft;

public class class_1396 extends class_1366 {
	private final class_1642 field_6628;
	private int field_6627;

	public class_1396(class_1642 arg, double d, boolean bl) {
		super(arg, d, bl);
		this.field_6628 = arg;
	}

	@Override
	public void method_6269() {
		super.method_6269();
		this.field_6627 = 0;
	}

	@Override
	public void method_6270() {
		super.method_6270();
		this.field_6628.method_19540(false);
	}

	@Override
	public void method_6268() {
		super.method_6268();
		this.field_6627++;
		if (this.field_6627 >= 5 && this.field_6505 < 10) {
			this.field_6628.method_19540(true);
		} else {
			this.field_6628.method_19540(false);
		}
	}
}
