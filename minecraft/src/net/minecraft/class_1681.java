package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1681 extends class_3857 {
	public class_1681(class_1937 arg) {
		super(class_1299.field_6144, arg);
	}

	public class_1681(class_1937 arg, class_1309 arg2) {
		super(class_1299.field_6144, arg2, arg);
	}

	public class_1681(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6144, d, e, f, arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 3) {
			double d = 0.08;

			for (int i = 0; i < 8; i++) {
				this.field_6002
					.method_8406(
						new class_2392(class_2398.field_11218, this.method_7495()),
						this.field_5987,
						this.field_6010,
						this.field_6035,
						((double)this.field_5974.nextFloat() - 0.5) * 0.08,
						((double)this.field_5974.nextFloat() - 0.5) * 0.08,
						((double)this.field_5974.nextFloat() - 0.5) * 0.08
					);
			}
		}
	}

	@Override
	protected void method_7492(class_239 arg) {
		if (arg.field_1326 != null) {
			arg.field_1326.method_5643(class_1282.method_5524(this, this.method_7491()), 0.0F);
		}

		if (!this.field_6002.field_9236) {
			if (this.field_5974.nextInt(8) == 0) {
				int i = 1;
				if (this.field_5974.nextInt(32) == 0) {
					i = 4;
				}

				for (int j = 0; j < i; j++) {
					class_1428 lv = new class_1428(this.field_6002);
					lv.method_5614(-24000);
					lv.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, 0.0F);
					this.field_6002.method_8649(lv);
				}
			}

			this.field_6002.method_8421(this, (byte)3);
			this.method_5650();
		}
	}

	@Override
	protected class_1792 method_16942() {
		return class_1802.field_8803;
	}
}
