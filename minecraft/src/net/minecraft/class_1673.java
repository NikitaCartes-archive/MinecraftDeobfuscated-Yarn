package net.minecraft;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1673 extends class_1297 implements class_1676 {
	public class_1501 field_7622;
	private class_2487 field_7623;

	public class_1673(class_1299<? extends class_1673> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1673(class_1937 arg, class_1501 arg2) {
		this(class_1299.field_6124, arg);
		this.field_7622 = arg2;
		this.method_5814(
			arg2.field_5987 - (double)(arg2.method_17681() + 1.0F) * 0.5 * (double)class_3532.method_15374(arg2.field_6283 * (float) (Math.PI / 180.0)),
			arg2.field_6010 + (double)arg2.method_5751() - 0.1F,
			arg2.field_6035 + (double)(arg2.method_17681() + 1.0F) * 0.5 * (double)class_3532.method_15362(arg2.field_6283 * (float) (Math.PI / 180.0))
		);
	}

	@Environment(EnvType.CLIENT)
	public class_1673(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		this(class_1299.field_6124, arg);
		this.method_5814(d, e, f);

		for (int j = 0; j < 7; j++) {
			double k = 0.4 + 0.1 * (double)j;
			arg.method_8406(class_2398.field_11228, d, e, f, g * k, h, i * k);
		}

		this.method_18800(g, h, i);
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_7623 != null) {
			this.method_7479();
		}

		class_243 lv = this.method_18798();
		class_239 lv2 = class_1675.method_18074(
			this, this.method_5829().method_18804(lv).method_1014(1.0), arg -> !arg.method_7325() && arg != this.field_7622, class_3959.class_3960.field_17559, true
		);
		if (lv2 != null) {
			this.method_7481(lv2);
		}

		this.field_5987 = this.field_5987 + lv.field_1352;
		this.field_6010 = this.field_6010 + lv.field_1351;
		this.field_6035 = this.field_6035 + lv.field_1350;
		float f = class_3532.method_15368(method_17996(lv));
		this.field_6031 = (float)(class_3532.method_15349(lv.field_1352, lv.field_1350) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(lv.field_1351, (double)f) * 180.0F / (float)Math.PI);

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
			this.method_18799(lv.method_1021(0.99F));
			if (!this.method_5740()) {
				this.method_18799(this.method_18798().method_1031(0.0, -0.06F, 0.0));
			}

			this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5750(double d, double e, double f) {
		this.method_18800(d, e, f);
		if (this.field_6004 == 0.0F && this.field_5982 == 0.0F) {
			float g = class_3532.method_15368(d * d + f * f);
			this.field_5965 = (float)(class_3532.method_15349(e, (double)g) * 180.0F / (float)Math.PI);
			this.field_6031 = (float)(class_3532.method_15349(d, f) * 180.0F / (float)Math.PI);
			this.field_6004 = this.field_5965;
			this.field_5982 = this.field_6031;
			this.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
		}
	}

	@Override
	public void method_7485(double d, double e, double f, float g, float h) {
		class_243 lv = new class_243(d, e, f)
			.method_1029()
			.method_1031(
				this.field_5974.nextGaussian() * 0.0075F * (double)h,
				this.field_5974.nextGaussian() * 0.0075F * (double)h,
				this.field_5974.nextGaussian() * 0.0075F * (double)h
			)
			.method_1021((double)g);
		this.method_18799(lv);
		float i = class_3532.method_15368(method_17996(lv));
		this.field_6031 = (float)(class_3532.method_15349(lv.field_1352, f) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(lv.field_1351, (double)i) * 180.0F / (float)Math.PI);
		this.field_5982 = this.field_6031;
		this.field_6004 = this.field_5965;
	}

	public void method_7481(class_239 arg) {
		class_239.class_240 lv = arg.method_17783();
		if (lv == class_239.class_240.field_1331 && this.field_7622 != null) {
			((class_3966)arg).method_17782().method_5643(class_1282.method_5519(this, this.field_7622).method_5517(), 1.0F);
		} else if (lv == class_239.class_240.field_1332 && !this.field_6002.field_9236) {
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

			for (class_1501 lv : this.field_6002.method_18467(class_1501.class, this.method_5829().method_1014(15.0))) {
				if (lv.method_5667().equals(uUID)) {
					this.field_7622 = lv;
					break;
				}
			}
		}

		this.field_7623 = null;
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2604(this);
	}
}
