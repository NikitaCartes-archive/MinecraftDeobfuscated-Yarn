package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1684 extends class_3857 {
	private class_1309 field_7646;

	public class_1684(class_1937 arg) {
		super(class_1299.field_6082, arg);
	}

	public class_1684(class_1937 arg, class_1309 arg2) {
		super(class_1299.field_6082, arg2, arg);
		this.field_7646 = arg2;
	}

	@Environment(EnvType.CLIENT)
	public class_1684(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6082, d, e, f, arg);
	}

	@Override
	protected class_1792 method_16942() {
		return class_1802.field_8634;
	}

	@Override
	protected void method_7492(class_239 arg) {
		class_1309 lv = this.method_7491();
		if (arg.field_1326 != null) {
			if (arg.field_1326 == this.field_7646) {
				return;
			}

			arg.field_1326.method_5643(class_1282.method_5524(this, lv), 0.0F);
		}

		if (arg.field_1330 == class_239.class_240.field_1332) {
			class_2338 lv2 = arg.method_1015();
			class_2586 lv3 = this.field_6002.method_8321(lv2);
			if (lv3 instanceof class_2643) {
				class_2643 lv4 = (class_2643)lv3;
				if (lv != null) {
					if (lv instanceof class_3222) {
						class_174.field_1180.method_8885((class_3222)lv, this.field_6002.method_8320(lv2));
					}

					lv4.method_11409(lv);
					this.method_5650();
					return;
				}

				lv4.method_11409(this);
				return;
			}
		}

		for (int i = 0; i < 32; i++) {
			this.field_6002
				.method_8406(
					class_2398.field_11214,
					this.field_5987,
					this.field_6010 + this.field_5974.nextDouble() * 2.0,
					this.field_6035,
					this.field_5974.nextGaussian(),
					0.0,
					this.field_5974.nextGaussian()
				);
		}

		if (!this.field_6002.field_9236) {
			if (lv instanceof class_3222) {
				class_3222 lv5 = (class_3222)lv;
				if (lv5.field_13987.method_14366().method_10758() && lv5.field_6002 == this.field_6002 && !lv5.method_6113()) {
					if (this.field_5974.nextFloat() < 0.05F && this.field_6002.method_8450().method_8355("doMobSpawning")) {
						class_1559 lv6 = new class_1559(this.field_6002);
						lv6.method_7022(true);
						lv6.method_5808(lv.field_5987, lv.field_6010, lv.field_6035, lv.field_6031, lv.field_5965);
						this.field_6002.method_8649(lv6);
					}

					if (lv.method_5765()) {
						lv.method_5848();
					}

					lv.method_5859(this.field_5987, this.field_6010, this.field_6035);
					lv.field_6017 = 0.0F;
					lv.method_5643(class_1282.field_5868, 5.0F);
				}
			} else if (lv != null) {
				lv.method_5859(this.field_5987, this.field_6010, this.field_6035);
				lv.field_6017 = 0.0F;
			}

			this.method_5650();
		}
	}

	@Override
	public void method_5773() {
		class_1309 lv = this.method_7491();
		if (lv != null && lv instanceof class_1657 && !lv.method_5805()) {
			this.method_5650();
		} else {
			super.method_5773();
		}
	}

	@Nullable
	@Override
	public class_1297 method_5731(class_2874 arg) {
		if (this.field_7642.field_6026 != arg) {
			this.field_7642 = null;
		}

		return super.method_5731(arg);
	}
}
