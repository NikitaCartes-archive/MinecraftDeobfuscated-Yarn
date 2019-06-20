package net.minecraft;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1682 extends class_1297 implements class_1676 {
	private int field_7640 = -1;
	private int field_7639 = -1;
	private int field_7641 = -1;
	protected boolean field_7645;
	public int field_7643;
	protected class_1309 field_7642;
	private UUID field_7644;
	private class_1297 field_7637;
	private int field_7638;

	protected class_1682(class_1299<? extends class_1682> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	protected class_1682(class_1299<? extends class_1682> arg, double d, double e, double f, class_1937 arg2) {
		this(arg, arg2);
		this.method_5814(d, e, f);
	}

	protected class_1682(class_1299<? extends class_1682> arg, class_1309 arg2, class_1937 arg3) {
		this(arg, arg2.field_5987, arg2.field_6010 + (double)arg2.method_5751() - 0.1F, arg2.field_6035, arg3);
		this.field_7642 = arg2;
		this.field_7644 = arg2.method_5667();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		double e = this.method_5829().method_995() * 4.0;
		if (Double.isNaN(e)) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	public void method_19207(class_1297 arg, float f, float g, float h, float i, float j) {
		float k = -class_3532.method_15374(g * (float) (Math.PI / 180.0)) * class_3532.method_15362(f * (float) (Math.PI / 180.0));
		float l = -class_3532.method_15374((f + h) * (float) (Math.PI / 180.0));
		float m = class_3532.method_15362(g * (float) (Math.PI / 180.0)) * class_3532.method_15362(f * (float) (Math.PI / 180.0));
		this.method_7485((double)k, (double)l, (double)m, i, j);
		class_243 lv = arg.method_18798();
		this.method_18799(this.method_18798().method_1031(lv.field_1352, arg.field_5952 ? 0.0 : lv.field_1351, lv.field_1350));
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
		this.field_6031 = (float)(class_3532.method_15349(lv.field_1352, lv.field_1350) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(lv.field_1351, (double)i) * 180.0F / (float)Math.PI);
		this.field_5982 = this.field_6031;
		this.field_6004 = this.field_5965;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5750(double d, double e, double f) {
		this.method_18800(d, e, f);
		if (this.field_6004 == 0.0F && this.field_5982 == 0.0F) {
			float g = class_3532.method_15368(d * d + f * f);
			this.field_6031 = (float)(class_3532.method_15349(d, f) * 180.0F / (float)Math.PI);
			this.field_5965 = (float)(class_3532.method_15349(e, (double)g) * 180.0F / (float)Math.PI);
			this.field_5982 = this.field_6031;
			this.field_6004 = this.field_5965;
		}
	}

	@Override
	public void method_5773() {
		this.field_6038 = this.field_5987;
		this.field_5971 = this.field_6010;
		this.field_5989 = this.field_6035;
		super.method_5773();
		if (this.field_7643 > 0) {
			this.field_7643--;
		}

		if (this.field_7645) {
			this.field_7645 = false;
			this.method_18799(
				this.method_18798()
					.method_18805((double)(this.field_5974.nextFloat() * 0.2F), (double)(this.field_5974.nextFloat() * 0.2F), (double)(this.field_5974.nextFloat() * 0.2F))
			);
		}

		class_238 lv = this.method_5829().method_18804(this.method_18798()).method_1014(1.0);

		for (class_1297 lv2 : this.field_6002.method_8333(this, lv, arg -> !arg.method_7325() && arg.method_5863())) {
			if (lv2 == this.field_7637) {
				this.field_7638++;
				break;
			}

			if (this.field_7642 != null && this.field_6012 < 2 && this.field_7637 == null) {
				this.field_7637 = lv2;
				this.field_7638 = 3;
				break;
			}
		}

		class_239 lv3 = class_1675.method_18074(
			this, lv, arg -> !arg.method_7325() && arg.method_5863() && arg != this.field_7637, class_3959.class_3960.field_17559, true
		);
		if (this.field_7637 != null && this.field_7638-- <= 0) {
			this.field_7637 = null;
		}

		if (lv3.method_17783() != class_239.class_240.field_1333) {
			if (lv3.method_17783() == class_239.class_240.field_1332
				&& this.field_6002.method_8320(((class_3965)lv3).method_17777()).method_11614() == class_2246.field_10316) {
				this.method_5717(((class_3965)lv3).method_17777());
			} else {
				this.method_7492(lv3);
			}
		}

		class_243 lv4 = this.method_18798();
		this.field_5987 = this.field_5987 + lv4.field_1352;
		this.field_6010 = this.field_6010 + lv4.field_1351;
		this.field_6035 = this.field_6035 + lv4.field_1350;
		float f = class_3532.method_15368(method_17996(lv4));
		this.field_6031 = (float)(class_3532.method_15349(lv4.field_1352, lv4.field_1350) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(lv4.field_1351, (double)f) * 180.0F / (float)Math.PI);

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
		float h;
		if (this.method_5799()) {
			for (int i = 0; i < 4; i++) {
				float g = 0.25F;
				this.field_6002
					.method_8406(
						class_2398.field_11247,
						this.field_5987 - lv4.field_1352 * 0.25,
						this.field_6010 - lv4.field_1351 * 0.25,
						this.field_6035 - lv4.field_1350 * 0.25,
						lv4.field_1352,
						lv4.field_1351,
						lv4.field_1350
					);
			}

			h = 0.8F;
		} else {
			h = 0.99F;
		}

		this.method_18799(lv4.method_1021((double)h));
		if (!this.method_5740()) {
			class_243 lv5 = this.method_18798();
			this.method_18800(lv5.field_1352, lv5.field_1351 - (double)this.method_7490(), lv5.field_1350);
		}

		this.method_5814(this.field_5987, this.field_6010, this.field_6035);
	}

	protected float method_7490() {
		return 0.03F;
	}

	protected abstract void method_7492(class_239 arg);

	@Override
	public void method_5652(class_2487 arg) {
		arg.method_10569("xTile", this.field_7640);
		arg.method_10569("yTile", this.field_7639);
		arg.method_10569("zTile", this.field_7641);
		arg.method_10567("shake", (byte)this.field_7643);
		arg.method_10567("inGround", (byte)(this.field_7645 ? 1 : 0));
		if (this.field_7644 != null) {
			arg.method_10566("owner", class_2512.method_10689(this.field_7644));
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		this.field_7640 = arg.method_10550("xTile");
		this.field_7639 = arg.method_10550("yTile");
		this.field_7641 = arg.method_10550("zTile");
		this.field_7643 = arg.method_10571("shake") & 255;
		this.field_7645 = arg.method_10571("inGround") == 1;
		this.field_7642 = null;
		if (arg.method_10573("owner", 10)) {
			this.field_7644 = class_2512.method_10690(arg.method_10562("owner"));
		}
	}

	@Nullable
	public class_1309 method_7491() {
		if (this.field_7642 == null && this.field_7644 != null && this.field_6002 instanceof class_3218) {
			class_1297 lv = ((class_3218)this.field_6002).method_14190(this.field_7644);
			if (lv instanceof class_1309) {
				this.field_7642 = (class_1309)lv;
			} else {
				this.field_7644 = null;
			}
		}

		return this.field_7642;
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2604(this);
	}
}
