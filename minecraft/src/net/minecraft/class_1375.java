package net.minecraft;

public class class_1375 extends class_1343 {
	private final boolean field_6552;
	private int field_6551;

	public class_1375(class_1308 arg, boolean bl) {
		super(arg);
		this.field_6413 = arg;
		this.field_6552 = bl;
	}

	@Override
	public boolean method_6266() {
		return this.field_6552 && this.field_6551 > 0 && super.method_6266();
	}

	@Override
	public void method_6269() {
		this.field_6551 = 20;
		this.method_6255(true);
	}

	@Override
	public void method_6270() {
		this.method_6255(false);
	}

	@Override
	public void method_6268() {
		this.field_6551--;
		super.method_6268();
	}
}
