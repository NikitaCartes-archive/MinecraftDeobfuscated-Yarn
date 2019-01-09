package net.minecraft;

import java.util.List;
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
	public class_1297 field_7637;
	private int field_7638;

	protected class_1682(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_5835(0.25F, 0.25F);
	}

	protected class_1682(class_1299<?> arg, double d, double e, double f, class_1937 arg2) {
		this(arg, arg2);
		this.method_5814(d, e, f);
	}

	protected class_1682(class_1299<?> arg, class_1309 arg2, class_1937 arg3) {
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

	public void method_7489(class_1297 arg, float f, float g, float h, float i, float j) {
		float k = -class_3532.method_15374(g * (float) (Math.PI / 180.0)) * class_3532.method_15362(f * (float) (Math.PI / 180.0));
		float l = -class_3532.method_15374((f + h) * (float) (Math.PI / 180.0));
		float m = class_3532.method_15362(g * (float) (Math.PI / 180.0)) * class_3532.method_15362(f * (float) (Math.PI / 180.0));
		this.method_7485((double)k, (double)l, (double)m, i, j);
		this.field_5967 = this.field_5967 + arg.field_5967;
		this.field_6006 = this.field_6006 + arg.field_6006;
		if (!arg.field_5952) {
			this.field_5984 = this.field_5984 + arg.field_5984;
		}
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

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5750(double d, double e, double f) {
		this.field_5967 = d;
		this.field_5984 = e;
		this.field_6006 = f;
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
			this.field_5967 = this.field_5967 * (double)(this.field_5974.nextFloat() * 0.2F);
			this.field_5984 = this.field_5984 * (double)(this.field_5974.nextFloat() * 0.2F);
			this.field_6006 = this.field_6006 * (double)(this.field_5974.nextFloat() * 0.2F);
		}

		class_243 lv = new class_243(this.field_5987, this.field_6010, this.field_6035);
		class_243 lv2 = new class_243(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
		class_239 lv3 = this.field_6002.method_8549(lv, lv2);
		lv = new class_243(this.field_5987, this.field_6010, this.field_6035);
		lv2 = new class_243(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
		if (lv3 != null) {
			lv2 = new class_243(lv3.field_1329.field_1352, lv3.field_1329.field_1351, lv3.field_1329.field_1350);
		}

		class_1297 lv4 = null;
		List<class_1297> list = this.field_6002.method_8335(this, this.method_5829().method_1012(this.field_5967, this.field_5984, this.field_6006).method_1014(1.0));
		double d = 0.0;
		boolean bl = false;

		for (int i = 0; i < list.size(); i++) {
			class_1297 lv5 = (class_1297)list.get(i);
			if (lv5.method_5863()) {
				if (lv5 == this.field_7637) {
					bl = true;
				} else if (this.field_7642 != null && this.field_6012 < 2 && this.field_7637 == null) {
					this.field_7637 = lv5;
					bl = true;
				} else {
					bl = false;
					class_238 lv6 = lv5.method_5829().method_1014(0.3F);
					class_239 lv7 = lv6.method_1004(lv, lv2);
					if (lv7 != null) {
						double e = lv.method_1025(lv7.field_1329);
						if (e < d || d == 0.0) {
							lv4 = lv5;
							d = e;
						}
					}
				}
			}
		}

		if (this.field_7637 != null) {
			if (bl) {
				this.field_7638 = 2;
			} else if (this.field_7638-- <= 0) {
				this.field_7637 = null;
			}
		}

		if (lv4 != null) {
			lv3 = new class_239(lv4);
		}

		if (lv3 != null) {
			if (lv3.field_1330 == class_239.class_240.field_1332 && this.field_6002.method_8320(lv3.method_1015()).method_11614() == class_2246.field_10316) {
				this.method_5717(lv3.method_1015());
			} else {
				this.method_7492(lv3);
			}
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
		float h = this.method_7490();
		if (this.method_5799()) {
			for (int j = 0; j < 4; j++) {
				float k = 0.25F;
				this.field_6002
					.method_8406(
						class_2398.field_11247,
						this.field_5987 - this.field_5967 * 0.25,
						this.field_6010 - this.field_5984 * 0.25,
						this.field_6035 - this.field_6006 * 0.25,
						this.field_5967,
						this.field_5984,
						this.field_6006
					);
			}

			g = 0.8F;
		}

		this.field_5967 *= (double)g;
		this.field_5984 *= (double)g;
		this.field_6006 *= (double)g;
		if (!this.method_5740()) {
			this.field_5984 -= (double)h;
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
			class_1297 lv = this.field_6002.method_14190(this.field_7644);
			if (lv instanceof class_1309) {
				this.field_7642 = (class_1309)lv;
			} else {
				this.field_7644 = null;
			}
		}

		return this.field_7642;
	}
}
