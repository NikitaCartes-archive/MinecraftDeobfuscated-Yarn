package net.minecraft;

public class class_1337 extends class_1352 {
	private final class_1493 field_6384;
	private class_1657 field_6383;
	private final class_1941 field_6381;
	private final float field_6380;
	private int field_6382;

	public class_1337(class_1493 arg, float f) {
		this.field_6384 = arg;
		this.field_6381 = arg.field_6002;
		this.field_6380 = f;
		this.method_6265(2);
	}

	@Override
	public boolean method_6264() {
		this.field_6383 = this.field_6381.method_8614(this.field_6384, (double)this.field_6380);
		return this.field_6383 == null ? false : this.method_6244(this.field_6383);
	}

	@Override
	public boolean method_6266() {
		if (!this.field_6383.method_5805()) {
			return false;
		} else {
			return this.field_6384.method_5858(this.field_6383) > (double)(this.field_6380 * this.field_6380)
				? false
				: this.field_6382 > 0 && this.method_6244(this.field_6383);
		}
	}

	@Override
	public void method_6269() {
		this.field_6384.method_6712(true);
		this.field_6382 = 40 + this.field_6384.method_6051().nextInt(40);
	}

	@Override
	public void method_6270() {
		this.field_6384.method_6712(false);
		this.field_6383 = null;
	}

	@Override
	public void method_6268() {
		this.field_6384
			.method_5988()
			.method_6230(
				this.field_6383.field_5987,
				this.field_6383.field_6010 + (double)this.field_6383.method_5751(),
				this.field_6383.field_6035,
				10.0F,
				(float)this.field_6384.method_5978()
			);
		this.field_6382--;
	}

	private boolean method_6244(class_1657 arg) {
		for (class_1268 lv : class_1268.values()) {
			class_1799 lv2 = arg.method_5998(lv);
			if (this.field_6384.method_6181() && lv2.method_7909() == class_1802.field_8606) {
				return true;
			}

			if (this.field_6384.method_6481(lv2)) {
				return true;
			}
		}

		return false;
	}
}
