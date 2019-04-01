package net.minecraft;

public class class_4255 extends class_1343 {
	private final boolean field_19004;
	private int field_19005;

	public class_4255(class_1308 arg, boolean bl) {
		super(arg);
		this.field_6413 = arg;
		this.field_19004 = bl;
	}

	@Override
	public boolean method_6266() {
		return this.field_19004 && this.field_19005 > 0 && super.method_6266();
	}

	@Override
	public void method_6269() {
		this.field_19005 = 20;
		this.method_19995(true);
	}

	@Override
	public void method_6270() {
		this.method_19995(false);
	}

	@Override
	public void method_6268() {
		this.field_19005--;
		super.method_6268();
	}
}
