package net.minecraft;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1673 extends class_1297 implements class_1676 {
	public class_1501 field_7622;
	private class_2487 field_7623;

	public class_1673(class_1937 arg) {
		super(class_1299.field_6124, arg);
		this.method_5835(0.25F, 0.25F);
	}

	public class_1673(class_1937 arg, class_1501 arg2) {
		this(arg);
		this.field_7622 = arg2;
		this.method_5814(
			arg2.field_5987 - (double)(arg2.field_5998 + 1.0F) * 0.5 * (double)class_3532.method_15374(arg2.field_6283 * (float) (Math.PI / 180.0)),
			arg2.field_6010 + (double)arg2.method_5751() - 0.1F,
			arg2.field_6035 + (double)(arg2.field_5998 + 1.0F) * 0.5 * (double)class_3532.method_15362(arg2.field_6283 * (float) (Math.PI / 180.0))
		);
	}

	@Environment(EnvType.CLIENT)
	public class_1673(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		this(arg);
		this.method_5814(d, e, f);

		for (int j = 0; j < 7; j++) {
			double k = 0.4 + 0.1 * (double)j;
			arg.method_8406(class_2398.field_11228, d, e, f, g * k, h, i * k);
		}

		this.field_5967 = g;
		this.field_5984 = h;
		this.field_6006 = i;
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_7623 != null) {
			this.method_7479();
		}

		class_243 lv = new class_243(this.field_5987, this.field_6010, this.field_6035);
		class_243 lv2 = new class_243(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
		class_239 lv3 = this.field_6002.method_8549(lv, lv2);
		lv = new class_243(this.field_5987, this.field_6010, this.field_6035);
		lv2 = new class_243(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
		if (lv3 != null) {
			lv2 = new class_243(lv3.field_1329.field_1352, lv3.field_1329.field_1351, lv3.field_1329.field_1350);
		}

		class_1297 lv4 = this.method_7480(lv, lv2);
		if (lv4 != null) {
			lv3 = new class_239(lv4);
		}

		if (lv3 != null) {
			this.method_7481(lv3);
		}

		this.field_5987 = this.field_5987 + this.field_5967;
		this.field_6010 = this.field_6010 + this.field_5984;
		this.field_6035 = this.field_6035 + this.field_6006;
		float f = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
		this.field_6031 = (float)(class_3532.method_15349(this.field_5967, this.field_6006) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(this.field_5984, (double)f) * 180.0F / (float)Math.PI);

		while (this.field_5965 - this.field_6004 < -180.0F) {
			this.field_6004 -= 360.0F;
		}

		while (this.field_5965 - this.field_6004 >= 180.0F) {
			this.field_6004 += 360.0F;
		}

		while (this.field_6031 - this.field_5982 < -180.0F) {
			this.field_5982 -= 360.0F;
		}

		while (this.field_6031 - this.field_5982 >= 180.0F) {
			this.field_5982 += 360.0F;
		}

		this.field_5965 = class_3532.method_16439(0.2F, this.field_6004, this.field_5965);
		this.field_6031 = class_3532.method_16439(0.2F, this.field_5982, this.field_6031);
		float g = 0.99F;
		float h = 0.06F;
		if (!this.field_6002.method_8422(this.method_5829(), class_3614.field_15959)) {
			this.method_5650();
		} else if (this.method_5816()) {
			this.method_5650();
		} else {
			this.field_5967 *= 0.99F;
			this.field_5984 *= 0.99F;
			this.field_6006 *= 0.99F;
			if (!this.method_5740()) {
				this.field_5984 -= 0.06F;
			}

			this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5750(double d, double e, double f) {
		this.field_5967 = d;
		this.field_5984 = e;
		this.field_6006 = f;
		if (this.field_6004 == 0.0F && this.field_5982 == 0.0F) {
			float g = class_3532.method_15368(d * d + f * f);
			this.field_5965 = (float)(class_3532.method_15349(e, (double)g) * 180.0F / (float)Math.PI);
			this.field_6031 = (float)(class_3532.method_15349(d, f) * 180.0F / (float)Math.PI);
			this.field_6004 = this.field_5965;
			this.field_5982 = this.field_6031;
			this.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
		}
	}

	@Nullable
	private class_1297 method_7480(class_243 arg, class_243 arg2) {
		class_1297 lv = null;
		List<class_1297> list = this.field_6002.method_8335(this, this.method_5829().method_1012(this.field_5967, this.field_5984, this.field_6006).method_1014(1.0));
		double d = 0.0;

		for (class_1297 lv2 : list) {
			if (lv2 != this.field_7622) {
				class_238 lv3 = lv2.method_5829().method_1014(0.3F);
				class_239 lv4 = lv3.method_1004(arg, arg2);
				if (lv4 != null) {
					double e = arg.method_1025(lv4.field_1329);
					if (e < d || d == 0.0) {
						lv = lv2;
						d = e;
					}
				}
			}
		}

		return lv;
	}

	@Override
	public void method_7485(double d, double e, double f, float g, float h) {
		float i = class_3532.method_15368(d * d + e * e + f * f);
		d /= (double)i;
		e /= (double)i;
		f /= (double)i;
		d += this.field_5974.nextGaussian() * 0.0075F * (double)h;
		e += this.field_5974.nextGaussian() * 0.0075F * (double)h;
		f += this.field_5974.nextGaussian() * 0.0075F * (double)h;
		d *= (double)g;
		e *= (double)g;
		f *= (double)g;
		this.field_5967 = d;
		this.field_5984 = e;
		this.field_6006 = f;
		float j = class_3532.method_15368(d * d + f * f);
		this.field_6031 = (float)(class_3532.method_15349(d, f) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(e, (double)j) * 180.0F / (float)Math.PI);
		this.field_5982 = this.field_6031;
		this.field_6004 = this.field_5965;
	}

	public void method_7481(class_239 arg) {
		if (arg.field_1326 != null && this.field_7622 != null) {
			arg.field_1326.method_5643(class_1282.method_5519(this, this.field_7622).method_5517(), 1.0F);
		}

		if (!this.field_6002.field_9236) {
			this.method_5650();
		}
	}

	@Override
	protected void method_5693() {
	}

	@Override
	protected void method_5749(class_2487 arg) {
		if (arg.method_10573("Owner", 10)) {
			this.field_7623 = arg.method_10562("Owner");
		}
	}

	@Override
	protected void method_5652(class_2487 arg) {
		if (this.field_7622 != null) {
			class_2487 lv = new class_2487();
			UUID uUID = this.field_7622.method_5667();
			lv.method_10560("OwnerUUID", uUID);
			arg.method_10566("Owner", lv);
		}
	}

	private void method_7479() {
		if (this.field_7623 != null && this.field_7623.method_10576("OwnerUUID")) {
			UUID uUID = this.field_7623.method_10584("OwnerUUID");

			for (class_1501 lv : this.field_6002.method_8403(class_1501.class, this.method_5829().method_1014(15.0))) {
				if (lv.method_5667().equals(uUID)) {
					this.field_7622 = lv;
					break;
				}
			}
		}

		this.field_7623 = null;
	}
}
