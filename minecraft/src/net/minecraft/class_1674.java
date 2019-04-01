package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1674 extends class_3855 {
	public int field_7624 = 1;

	public class_1674(class_1299<? extends class_1674> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Environment(EnvType.CLIENT)
	public class_1674(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(class_1299.field_6066, d, e, f, g, h, i, arg);
	}

	public class_1674(class_1937 arg, class_1309 arg2, double d, double e, double f) {
		super(class_1299.field_6066, arg2, d, e, f, arg);
	}

	@Override
	protected void method_7469(class_239 arg) {
		if (!this.field_6002.field_9236) {
			if (arg.method_17783() == class_239.class_240.field_1331) {
				class_1297 lv = ((class_3966)arg).method_17782();
				lv.method_5643(class_1282.method_5521(this, this.field_7604), 6.0F);
				this.method_5723(this.field_7604, lv);
			}

			boolean bl = this.field_6002.method_8450().method_8355("mobGriefing");
			this.field_6002
				.method_8537(
					null,
					this.field_5987,
					this.field_6010,
					this.field_6035,
					(float)this.field_7624,
					bl,
					bl ? class_1927.class_4179.field_18687 : class_1927.class_4179.field_18685
				);
			this.method_5650();
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("ExplosionPower", this.field_7624);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("ExplosionPower", 99)) {
			this.field_7624 = arg.method_10550("ExplosionPower");
		}
	}
}
