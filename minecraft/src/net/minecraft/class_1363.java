package net.minecraft;

public class class_1363 extends class_1352 {
	private final class_1646 field_6494;
	private class_1646 field_6493;
	private final class_1937 field_6490;
	private int field_6491;
	private class_1415 field_6492;

	public class_1363(class_1646 arg) {
		this.field_6494 = arg;
		this.field_6490 = arg.field_6002;
		this.method_6265(3);
	}

	@Override
	public boolean method_6264() {
		if (this.field_6494.method_5618() != 0) {
			return false;
		} else if (this.field_6494.method_6051().nextInt(500) != 0) {
			return false;
		} else {
			this.field_6492 = this.field_6490.method_8557().method_6438(new class_2338(this.field_6494), 0);
			if (this.field_6492 == null) {
				return false;
			} else if (this.method_6287() && this.field_6494.method_7245(true)) {
				class_1297 lv = this.field_6490.method_8472(class_1646.class, this.field_6494.method_5829().method_1009(8.0, 3.0, 8.0), this.field_6494);
				if (lv == null) {
					return false;
				} else {
					this.field_6493 = (class_1646)lv;
					return this.field_6493.method_5618() == 0 && this.field_6493.method_7245(true);
				}
			} else {
				return false;
			}
		}
	}

	@Override
	public void method_6269() {
		this.field_6491 = 300;
		this.field_6494.method_7226(true);
	}

	@Override
	public void method_6270() {
		this.field_6492 = null;
		this.field_6493 = null;
		this.field_6494.method_7226(false);
	}

	@Override
	public boolean method_6266() {
		return this.field_6491 >= 0 && this.method_6287() && this.field_6494.method_5618() == 0 && this.field_6494.method_7245(false);
	}

	@Override
	public void method_6268() {
		this.field_6491--;
		this.field_6494.method_5988().method_6226(this.field_6493, 10.0F, 30.0F);
		if (this.field_6494.method_5858(this.field_6493) > 2.25) {
			this.field_6494.method_5942().method_6335(this.field_6493, 0.25);
		} else if (this.field_6491 == 0 && this.field_6493.method_7241()) {
			this.method_6286();
		}

		if (this.field_6494.method_6051().nextInt(35) == 0) {
			this.field_6490.method_8421(this.field_6494, (byte)12);
		}
	}

	private boolean method_6287() {
		if (!this.field_6492.method_6381()) {
			return false;
		} else {
			int i = (int)((double)((float)this.field_6492.method_6384()) * 0.35);
			return this.field_6492.method_6387() < i;
		}
	}

	private void method_6286() {
		class_1646 lv = this.field_6494.method_7225(this.field_6493);
		this.field_6493.method_5614(6000);
		this.field_6494.method_5614(6000);
		this.field_6493.method_7243(false);
		this.field_6494.method_7243(false);
		lv.method_5614(-24000);
		lv.method_5808(this.field_6494.field_5987, this.field_6494.field_6010, this.field_6494.field_6035, 0.0F, 0.0F);
		this.field_6490.method_8649(lv);
		this.field_6490.method_8421(lv, (byte)12);
	}
}
