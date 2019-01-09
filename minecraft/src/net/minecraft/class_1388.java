package net.minecraft;

import java.util.List;

public class class_1388 extends class_1352 {
	private final class_1646 field_6607;
	private class_1439 field_6604;
	private int field_6605;
	private boolean field_6606;

	public class_1388(class_1646 arg) {
		this.field_6607 = arg;
		this.method_6265(3);
	}

	@Override
	public boolean method_6264() {
		if (this.field_6607.method_5618() >= 0) {
			return false;
		} else if (!this.field_6607.field_6002.method_8530()) {
			return false;
		} else {
			List<class_1439> list = this.field_6607.field_6002.method_8403(class_1439.class, this.field_6607.method_5829().method_1009(6.0, 2.0, 6.0));
			if (list.isEmpty()) {
				return false;
			} else {
				for (class_1439 lv : list) {
					if (lv.method_6502() > 0) {
						this.field_6604 = lv;
						break;
					}
				}

				return this.field_6604 != null;
			}
		}
	}

	@Override
	public boolean method_6266() {
		return this.field_6604.method_6502() > 0;
	}

	@Override
	public void method_6269() {
		this.field_6605 = this.field_6607.method_6051().nextInt(320);
		this.field_6606 = false;
		this.field_6604.method_5942().method_6340();
	}

	@Override
	public void method_6270() {
		this.field_6604 = null;
		this.field_6607.method_5942().method_6340();
	}

	@Override
	public void method_6268() {
		this.field_6607.method_5988().method_6226(this.field_6604, 30.0F, 30.0F);
		if (this.field_6604.method_6502() == this.field_6605) {
			this.field_6607.method_5942().method_6335(this.field_6604, 0.5);
			this.field_6606 = true;
		}

		if (this.field_6606 && this.field_6607.method_5858(this.field_6604) < 4.0) {
			this.field_6604.method_6497(false);
			this.field_6607.method_5942().method_6340();
		}
	}
}
