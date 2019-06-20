package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1687 extends class_1668 {
	private static final class_2940<Boolean> field_7654 = class_2945.method_12791(class_1687.class, class_2943.field_13323);

	public class_1687(class_1299<? extends class_1687> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1687(class_1937 arg, class_1309 arg2, double d, double e, double f) {
		super(class_1299.field_6130, arg2, d, e, f, arg);
	}

	@Environment(EnvType.CLIENT)
	public class_1687(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(class_1299.field_6130, d, e, f, g, h, i, arg);
	}

	@Override
	protected float method_7466() {
		return this.method_7503() ? 0.73F : super.method_7466();
	}

	@Override
	public boolean method_5809() {
		return false;
	}

	@Override
	public float method_5774(class_1927 arg, class_1922 arg2, class_2338 arg3, class_2680 arg4, class_3610 arg5, float f) {
		return this.method_7503() && class_1528.method_6883(arg4) ? Math.min(0.8F, f) : f;
	}

	@Override
	protected void method_7469(class_239 arg) {
		if (!this.field_6002.field_9236) {
			if (arg.method_17783() == class_239.class_240.field_1331) {
				class_1297 lv = ((class_3966)arg).method_17782();
				if (this.field_7604 != null) {
					if (lv.method_5643(class_1282.method_5511(this.field_7604), 8.0F)) {
						if (lv.method_5805()) {
							this.method_5723(this.field_7604, lv);
						} else {
							this.field_7604.method_6025(5.0F);
						}
					}
				} else {
					lv.method_5643(class_1282.field_5846, 5.0F);
				}

				if (lv instanceof class_1309) {
					int i = 0;
					if (this.field_6002.method_8407() == class_1267.field_5802) {
						i = 10;
					} else if (this.field_6002.method_8407() == class_1267.field_5807) {
						i = 40;
					}

					if (i > 0) {
						((class_1309)lv).method_6092(new class_1293(class_1294.field_5920, 20 * i, 1));
					}
				}
			}

			class_1927.class_4179 lv2 = this.field_6002.method_8450().method_8355(class_1928.field_19388)
				? class_1927.class_4179.field_18687
				: class_1927.class_4179.field_18685;
			this.field_6002.method_8537(this, this.field_5987, this.field_6010, this.field_6035, 1.0F, false, lv2);
			this.method_5650();
		}
	}

	@Override
	public boolean method_5863() {
		return false;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		return false;
	}

	@Override
	protected void method_5693() {
		this.field_6011.method_12784(field_7654, false);
	}

	public boolean method_7503() {
		return this.field_6011.method_12789(field_7654);
	}

	public void method_7502(boolean bl) {
		this.field_6011.method_12778(field_7654, bl);
	}

	@Override
	protected boolean method_7468() {
		return false;
	}
}
