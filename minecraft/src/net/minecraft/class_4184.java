package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4184 {
	private boolean field_18709;
	private class_1922 field_18710;
	private class_1297 field_18711;
	private class_243 field_18712 = class_243.field_1353;
	private final class_2338.class_2339 field_18713 = new class_2338.class_2339();
	private class_243 field_18714;
	private class_243 field_18715;
	private class_243 field_18716;
	private float field_18717;
	private float field_18718;
	private boolean field_18719;
	private boolean field_18720;
	private float field_18721;
	private float field_18722;

	public void method_19321(class_1922 arg, class_1297 arg2, boolean bl, boolean bl2, float f) {
		this.field_18709 = true;
		this.field_18710 = arg;
		this.field_18711 = arg2;
		this.field_18719 = bl;
		this.field_18720 = bl2;
		this.method_19325(arg2.method_5705(f), arg2.method_5695(f));
		this.method_19327(
			class_3532.method_16436((double)f, arg2.field_6014, arg2.field_5987),
			class_3532.method_16436((double)f, arg2.field_6036, arg2.field_6010) + (double)class_3532.method_16439(f, this.field_18722, this.field_18721),
			class_3532.method_16436((double)f, arg2.field_5969, arg2.field_6035)
		);
		if (bl) {
			if (bl2) {
				this.field_18718 += 180.0F;
				this.field_18717 = this.field_18717 + -this.field_18717 * 2.0F;
				this.method_19323();
			}

			this.method_19324(-this.method_19318(4.0), 0.0, 0.0);
		} else if (arg2 instanceof class_1309 && ((class_1309)arg2).method_6113()) {
			class_2350 lv = ((class_1309)arg2).method_18401();
			this.method_19325(lv != null ? lv.method_10144() - 180.0F : 0.0F, 0.0F);
			this.method_19324(0.0, 0.3, 0.0);
		} else {
			this.method_19324(-0.05F, 0.0, 0.0);
		}

		GlStateManager.rotatef(this.field_18717, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(this.field_18718 + 180.0F, 0.0F, 1.0F, 0.0F);
	}

	public void method_19317() {
		if (this.field_18711 != null) {
			this.field_18722 = this.field_18721;
			this.field_18721 = this.field_18721 + (this.field_18711.method_5751() - this.field_18721) * 0.5F;
		}
	}

	private double method_19318(double d) {
		for (int i = 0; i < 8; i++) {
			float f = (float)((i & 1) * 2 - 1);
			float g = (float)((i >> 1 & 1) * 2 - 1);
			float h = (float)((i >> 2 & 1) * 2 - 1);
			f *= 0.1F;
			g *= 0.1F;
			h *= 0.1F;
			class_243 lv = this.field_18712.method_1031((double)f, (double)g, (double)h);
			class_243 lv2 = new class_243(
				this.field_18712.field_1352 - this.field_18714.field_1352 * d + (double)f + (double)h,
				this.field_18712.field_1351 - this.field_18714.field_1351 * d + (double)g,
				this.field_18712.field_1350 - this.field_18714.field_1350 * d + (double)h
			);
			class_239 lv3 = this.field_18710.method_17742(new class_3959(lv, lv2, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, this.field_18711));
			if (lv3.method_17783() != class_239.class_240.field_1333) {
				double e = lv3.method_17784().method_1022(this.field_18712);
				if (e < d) {
					d = e;
				}
			}
		}

		return d;
	}

	protected void method_19324(double d, double e, double f) {
		double g = this.field_18714.field_1352 * d + this.field_18715.field_1352 * e + this.field_18716.field_1352 * f;
		double h = this.field_18714.field_1351 * d + this.field_18715.field_1351 * e + this.field_18716.field_1351 * f;
		double i = this.field_18714.field_1350 * d + this.field_18715.field_1350 * e + this.field_18716.field_1350 * f;
		this.method_19322(new class_243(this.field_18712.field_1352 + g, this.field_18712.field_1351 + h, this.field_18712.field_1350 + i));
	}

	protected void method_19323() {
		float f = class_3532.method_15362((this.field_18718 + 90.0F) * (float) (Math.PI / 180.0));
		float g = class_3532.method_15374((this.field_18718 + 90.0F) * (float) (Math.PI / 180.0));
		float h = class_3532.method_15362(-this.field_18717 * (float) (Math.PI / 180.0));
		float i = class_3532.method_15374(-this.field_18717 * (float) (Math.PI / 180.0));
		float j = class_3532.method_15362((-this.field_18717 + 90.0F) * (float) (Math.PI / 180.0));
		float k = class_3532.method_15374((-this.field_18717 + 90.0F) * (float) (Math.PI / 180.0));
		this.field_18714 = new class_243((double)(f * h), (double)i, (double)(g * h));
		this.field_18715 = new class_243((double)(f * j), (double)k, (double)(g * j));
		this.field_18716 = this.field_18714.method_1036(this.field_18715).method_1021(-1.0);
	}

	protected void method_19325(float f, float g) {
		this.field_18717 = g;
		this.field_18718 = f;
		this.method_19323();
	}

	protected void method_19327(double d, double e, double f) {
		this.method_19322(new class_243(d, e, f));
	}

	protected void method_19322(class_243 arg) {
		this.field_18712 = arg;
		this.field_18713.method_10102(arg.field_1352, arg.field_1351, arg.field_1350);
	}

	public class_243 method_19326() {
		return this.field_18712;
	}

	public class_2338 method_19328() {
		return this.field_18713;
	}

	public float method_19329() {
		return this.field_18717;
	}

	public float method_19330() {
		return this.field_18718;
	}

	public class_1297 method_19331() {
		return this.field_18711;
	}

	public boolean method_19332() {
		return this.field_18709;
	}

	public boolean method_19333() {
		return this.field_18719;
	}

	public class_3610 method_19334() {
		if (!this.field_18709) {
			return class_3612.field_15906.method_15785();
		} else {
			class_3610 lv = this.field_18710.method_8316(this.field_18713);
			return !lv.method_15769()
					&& this.field_18712.field_1351 >= (double)((float)this.field_18713.method_10264() + lv.method_15763(this.field_18710, this.field_18713))
				? class_3612.field_15906.method_15785()
				: lv;
		}
	}

	public final class_243 method_19335() {
		return this.field_18714;
	}

	public final class_243 method_19336() {
		return this.field_18715;
	}

	public void method_19337() {
		this.field_18710 = null;
		this.field_18711 = null;
		this.field_18709 = false;
	}
}
