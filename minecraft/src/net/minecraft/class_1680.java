package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1680 extends class_3857 {
	public class_1680(class_1937 arg) {
		super(class_1299.field_6068, arg);
	}

	public class_1680(class_1937 arg, class_1309 arg2) {
		super(class_1299.field_6068, arg2, arg);
	}

	public class_1680(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6068, d, e, f, arg);
	}

	@Override
	protected class_1792 method_16942() {
		return class_1802.field_8543;
	}

	@Environment(EnvType.CLIENT)
	private class_2394 method_16939() {
		class_1799 lv = this.method_16943();
		return (class_2394)(lv.method_7960() ? class_2398.field_11230 : new class_2392(class_2398.field_11218, lv));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 3) {
			class_2394 lv = this.method_16939();

			for (int i = 0; i < 8; i++) {
				this.field_6002.method_8406(lv, this.field_5987, this.field_6010, this.field_6035, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void method_7492(class_239 arg) {
		if (arg.field_1326 != null) {
			int i = 0;
			if (arg.field_1326 instanceof class_1545) {
				i = 3;
			}

			arg.field_1326.method_5643(class_1282.method_5524(this, this.method_7491()), (float)i);
		}

		if (!this.field_6002.field_9236) {
			this.field_6002.method_8421(this, (byte)3);
			this.method_5650();
		}
	}
}
