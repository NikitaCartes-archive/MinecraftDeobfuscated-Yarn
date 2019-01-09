package net.minecraft;

public class class_1389 extends class_1352 {
	private final class_1548 field_6608;
	private class_1309 field_6609;

	public class_1389(class_1548 arg) {
		this.field_6608 = arg;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		class_1309 lv = this.field_6608.method_5968();
		return this.field_6608.method_7007() > 0 || lv != null && this.field_6608.method_5858(lv) < 9.0;
	}

	@Override
	public void method_6269() {
		this.field_6608.method_5942().method_6340();
		this.field_6609 = this.field_6608.method_5968();
	}

	@Override
	public void method_6270() {
		this.field_6609 = null;
	}

	@Override
	public void method_6268() {
		if (this.field_6609 == null) {
			this.field_6608.method_7005(-1);
		} else if (this.field_6608.method_5858(this.field_6609) > 49.0) {
			this.field_6608.method_7005(-1);
		} else if (!this.field_6608.method_5985().method_6369(this.field_6609)) {
			this.field_6608.method_7005(-1);
		} else {
			this.field_6608.method_7005(1);
		}
	}
}
