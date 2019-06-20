package net.minecraft;

public class class_1360 extends class_1352 {
	private final class_1471 field_6478;
	private class_3222 field_6479;
	private boolean field_6480;

	public class_1360(class_1471 arg) {
		this.field_6478 = arg;
	}

	@Override
	public boolean method_6264() {
		class_3222 lv = (class_3222)this.field_6478.method_6177();
		boolean bl = lv != null && !lv.method_7325() && !lv.field_7503.field_7479 && !lv.method_5799();
		return !this.field_6478.method_6172() && bl && this.field_6478.method_6626();
	}

	@Override
	public boolean method_6267() {
		return !this.field_6480;
	}

	@Override
	public void method_6269() {
		this.field_6479 = (class_3222)this.field_6478.method_6177();
		this.field_6480 = false;
	}

	@Override
	public void method_6268() {
		if (!this.field_6480 && !this.field_6478.method_6172() && !this.field_6478.method_5934()) {
			if (this.field_6478.method_5829().method_994(this.field_6479.method_5829())) {
				this.field_6480 = this.field_6478.method_6627(this.field_6479);
			}
		}
	}
}
