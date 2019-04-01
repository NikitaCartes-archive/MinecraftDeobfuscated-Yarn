package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1007 extends class_922<class_742, class_591<class_742>> {
	public class_1007(class_898 arg) {
		this(arg, false);
	}

	public class_1007(class_898 arg, boolean bl) {
		super(arg, new class_591<>(0.0F, bl), 0.5F);
		this.method_4046(new class_987<>(this, new class_572(0.5F), new class_572(1.0F)));
		this.method_4046(new class_989<>(this));
		this.method_4046(new class_973<>(this));
		this.method_4046(new class_978(this));
		this.method_4046(new class_972(this));
		this.method_4046(new class_976<>(this));
		this.method_4046(new class_979<>(this));
		this.method_4046(new class_983<>(this));
		this.method_4046(new class_998<>(this));
	}

	public void method_4215(class_742 arg, double d, double e, double f, float g, float h) {
		if (!arg.method_7340() || this.field_4676.field_4686.method_19331() == arg) {
			double i = e;
			if (arg.method_5715()) {
				i = e - 0.125;
			}

			this.method_4218(arg);
			GlStateManager.setProfile(GlStateManager.class_1032.field_5128);
			super.method_4054(arg, d, i, f, g, h);
			GlStateManager.unsetProfile(GlStateManager.class_1032.field_5128);
		}
	}

	private void method_4218(class_742 arg) {
		class_591<class_742> lv = this.method_4038();
		if (arg.method_7325()) {
			lv.method_2805(false);
			lv.field_3398.field_3665 = true;
			lv.field_3394.field_3665 = true;
		} else {
			class_1799 lv2 = arg.method_6047();
			class_1799 lv3 = arg.method_6079();
			lv.method_2805(true);
			lv.field_3394.field_3665 = arg.method_7348(class_1664.field_7563);
			lv.field_3483.field_3665 = arg.method_7348(class_1664.field_7564);
			lv.field_3482.field_3665 = arg.method_7348(class_1664.field_7566);
			lv.field_3479.field_3665 = arg.method_7348(class_1664.field_7565);
			lv.field_3484.field_3665 = arg.method_7348(class_1664.field_7568);
			lv.field_3486.field_3665 = arg.method_7348(class_1664.field_7570);
			lv.field_3400 = arg.method_5715();
			class_572.class_573 lv4 = this.method_4210(arg, lv2, lv3, class_1268.field_5808);
			class_572.class_573 lv5 = this.method_4210(arg, lv2, lv3, class_1268.field_5810);
			if (arg.method_6068() == class_1306.field_6183) {
				lv.field_3395 = lv4;
				lv.field_3399 = lv5;
			} else {
				lv.field_3395 = lv5;
				lv.field_3399 = lv4;
			}
		}
	}

	private class_572.class_573 method_4210(class_742 arg, class_1799 arg2, class_1799 arg3, class_1268 arg4) {
		class_572.class_573 lv = class_572.class_573.field_3409;
		class_1799 lv2 = arg4 == class_1268.field_5808 ? arg2 : arg3;
		if (!lv2.method_7960()) {
			lv = class_572.class_573.field_3410;
			if (arg.method_6014() > 0) {
				class_1839 lv3 = lv2.method_7976();
				if (lv3 == class_1839.field_8949) {
					lv = class_572.class_573.field_3406;
				} else if (lv3 == class_1839.field_8953) {
					lv = class_572.class_573.field_3403;
				} else if (lv3 == class_1839.field_8951) {
					lv = class_572.class_573.field_3407;
				} else if (lv3 == class_1839.field_8947 && arg4 == arg.method_6058()) {
					lv = class_572.class_573.field_3405;
				}
			} else {
				boolean bl = arg2.method_7909() == class_1802.field_8399;
				boolean bl2 = class_1764.method_7781(arg2);
				boolean bl3 = arg3.method_7909() == class_1802.field_8399;
				boolean bl4 = class_1764.method_7781(arg3);
				if (bl && bl2) {
					lv = class_572.class_573.field_3408;
				}

				if (bl3 && bl4 && arg2.method_7909().method_7853(arg2) == class_1839.field_8952) {
					lv = class_572.class_573.field_3408;
				}
			}
		}

		return lv;
	}

	public class_2960 method_4216(class_742 arg) {
		return arg.method_3117();
	}

	protected void method_4217(class_742 arg, float f) {
		float g = 0.9375F;
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}

	protected void method_20285(double d, double e, class_742 arg, float f) {
		if (!(arg instanceof class_746)) {
			super.method_20283(d, e, arg, f);
		}
	}

	protected void method_4213(class_742 arg, double d, double e, double f, String string, double g) {
		if (g < 100.0) {
			class_269 lv = arg.method_7327();
			class_266 lv2 = lv.method_1189(2);
			if (lv2 != null) {
				class_267 lv3 = lv.method_1180(arg.method_5820(), lv2);
				this.method_3923(arg, lv3.method_1126() + " " + lv2.method_1114().method_10863(), d, e, f, 64);
				e += (double)(9.0F * 1.15F * 0.025F);
			}
		}

		super.method_3930(arg, d, e, f, string, g);
	}

	public void method_4220(class_742 arg) {
		float f = 1.0F;
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		float g = 0.0625F;
		class_591<class_742> lv = this.method_4038();
		this.method_4218(arg);
		GlStateManager.enableBlend();
		lv.field_3447 = 0.0F;
		lv.field_3400 = false;
		lv.field_3396 = 0.0F;
		lv.method_17087(arg, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		lv.field_3401.field_3654 = 0.0F;
		lv.field_3401.method_2846(0.0625F);
		lv.field_3486.field_3654 = 0.0F;
		lv.field_3486.method_2846(0.0625F);
		GlStateManager.disableBlend();
	}

	public void method_4221(class_742 arg) {
		float f = 1.0F;
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		float g = 0.0625F;
		class_591<class_742> lv = this.method_4038();
		this.method_4218(arg);
		GlStateManager.enableBlend();
		lv.field_3400 = false;
		lv.field_3447 = 0.0F;
		lv.field_3396 = 0.0F;
		lv.method_17087(arg, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		lv.field_3390.field_3654 = 0.0F;
		lv.field_3390.method_2846(0.0625F);
		lv.field_3484.field_3654 = 0.0F;
		lv.field_3484.method_2846(0.0625F);
		GlStateManager.disableBlend();
	}

	protected void method_4212(class_742 arg, float f, float g, float h) {
		float i = arg.method_6024(h);
		if (arg.method_6128()) {
			super.method_4058(arg, f, g, h);
			float j = (float)arg.method_6003() + h;
			float k = class_3532.method_15363(j * j / 100.0F, 0.0F, 1.0F);
			if (!arg.method_6123()) {
				GlStateManager.rotatef(k * (-90.0F - arg.field_5965), 1.0F, 0.0F, 0.0F);
			}

			class_243 lv = arg.method_5828(h);
			class_243 lv2 = arg.method_18798();
			double d = class_1297.method_17996(lv2);
			double e = class_1297.method_17996(lv);
			if (d > 0.0 && e > 0.0) {
				double l = (lv2.field_1352 * lv.field_1352 + lv2.field_1350 * lv.field_1350) / (Math.sqrt(d) * Math.sqrt(e));
				double m = lv2.field_1352 * lv.field_1350 - lv2.field_1350 * lv.field_1352;
				GlStateManager.rotatef((float)(Math.signum(m) * Math.acos(l)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
			}
		} else if (i > 0.0F) {
			super.method_4058(arg, f, g, h);
			GlStateManager.rotatef(class_3532.method_16439(i, arg.field_5965, -90.0F - arg.field_5965), 1.0F, 0.0F, 0.0F);
			if (arg.method_5681()) {
				GlStateManager.translatef(0.0F, -1.0F, 0.3F);
			}
		} else {
			super.method_4058(arg, f, g, h);
		}
	}
}
