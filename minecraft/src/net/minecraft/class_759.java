package net.minecraft;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_759 {
	private static final class_2960 field_4049 = new class_2960("textures/map/map_background.png");
	private static final class_2960 field_4045 = new class_2960("textures/misc/underwater.png");
	private final class_310 field_4050;
	private class_1799 field_4047 = class_1799.field_8037;
	private class_1799 field_4048 = class_1799.field_8037;
	private float field_4043;
	private float field_4053;
	private float field_4052;
	private float field_4051;
	private final class_898 field_4046;
	private final class_918 field_4044;

	public class_759(class_310 arg) {
		this.field_4050 = arg;
		this.field_4046 = arg.method_1561();
		this.field_4044 = arg.method_1480();
	}

	public void method_3233(class_1309 arg, class_1799 arg2, class_809.class_811 arg3) {
		this.method_3234(arg, arg2, arg3, false);
	}

	public void method_3234(class_1309 arg, class_1799 arg2, class_809.class_811 arg3, boolean bl) {
		if (!arg2.method_7960()) {
			class_1792 lv = arg2.method_7909();
			class_2248 lv2 = class_2248.method_9503(lv);
			GlStateManager.pushMatrix();
			boolean bl2 = this.field_4044.method_4014(arg2) && lv2.method_9551() == class_1921.field_9179;
			if (bl2) {
				GlStateManager.depthMask(false);
			}

			this.field_4044.method_4016(arg2, arg, arg3, bl);
			if (bl2) {
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}
	}

	private void method_3229(float f, float g) {
		GlStateManager.pushMatrix();
		GlStateManager.rotatef(f, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(g, 0.0F, 1.0F, 0.0F);
		class_308.method_1452();
		GlStateManager.popMatrix();
	}

	private void method_3235() {
		class_742 lv = this.field_4050.field_1724;
		int i = this.field_4050.field_1687.method_8313(new class_2338(lv.field_5987, lv.field_6010 + (double)lv.method_5751(), lv.field_6035), 0);
		float f = (float)(i & 65535);
		float g = (float)(i >> 16);
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, f, g);
	}

	private void method_3214(float f) {
		class_746 lv = this.field_4050.field_1724;
		float g = class_3532.method_16439(f, lv.field_3914, lv.field_3916);
		float h = class_3532.method_16439(f, lv.field_3931, lv.field_3932);
		GlStateManager.rotatef((lv.field_5965 - g) * 0.1F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef((lv.field_6031 - h) * 0.1F, 0.0F, 1.0F, 0.0F);
	}

	private float method_3227(float f) {
		float g = 1.0F - f / 45.0F + 0.1F;
		g = class_3532.method_15363(g, 0.0F, 1.0F);
		return -class_3532.method_15362(g * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void method_3221() {
		if (!this.field_4050.field_1724.method_5767()) {
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
			this.method_3216(class_1306.field_6183);
			this.method_3216(class_1306.field_6182);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
		}
	}

	private void method_3216(class_1306 arg) {
		this.field_4050.method_1531().method_4618(this.field_4050.field_1724.method_3117());
		class_897<class_742> lv = this.field_4046.method_3957(this.field_4050.field_1724);
		class_1007 lv2 = (class_1007)lv;
		GlStateManager.pushMatrix();
		float f = arg == class_1306.field_6183 ? 1.0F : -1.0F;
		GlStateManager.rotatef(92.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(f * -41.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translatef(f * 0.3F, -1.1F, 0.45F);
		if (arg == class_1306.field_6183) {
			lv2.method_4220(this.field_4050.field_1724);
		} else {
			lv2.method_4221(this.field_4050.field_1724);
		}

		GlStateManager.popMatrix();
	}

	private void method_3222(float f, class_1306 arg, float g, class_1799 arg2) {
		float h = arg == class_1306.field_6183 ? 1.0F : -1.0F;
		GlStateManager.translatef(h * 0.125F, -0.125F, 0.0F);
		if (!this.field_4050.field_1724.method_5767()) {
			GlStateManager.pushMatrix();
			GlStateManager.rotatef(h * 10.0F, 0.0F, 0.0F, 1.0F);
			this.method_3219(f, g, arg);
			GlStateManager.popMatrix();
		}

		GlStateManager.pushMatrix();
		GlStateManager.translatef(h * 0.51F, -0.08F + f * -1.2F, -0.75F);
		float i = class_3532.method_15355(g);
		float j = class_3532.method_15374(i * (float) Math.PI);
		float k = -0.5F * j;
		float l = 0.4F * class_3532.method_15374(i * (float) (Math.PI * 2));
		float m = -0.3F * class_3532.method_15374(g * (float) Math.PI);
		GlStateManager.translatef(h * k, l - 0.3F * j, m);
		GlStateManager.rotatef(j * -45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(h * j * -30.0F, 0.0F, 1.0F, 0.0F);
		this.method_3223(arg2);
		GlStateManager.popMatrix();
	}

	private void method_3231(float f, float g, float h) {
		float i = class_3532.method_15355(h);
		float j = -0.2F * class_3532.method_15374(h * (float) Math.PI);
		float k = -0.4F * class_3532.method_15374(i * (float) Math.PI);
		GlStateManager.translatef(0.0F, -j / 2.0F, k);
		float l = this.method_3227(f);
		GlStateManager.translatef(0.0F, 0.04F + g * -1.2F + l * -0.5F, -0.72F);
		GlStateManager.rotatef(l * -85.0F, 1.0F, 0.0F, 0.0F);
		this.method_3221();
		float m = class_3532.method_15374(i * (float) Math.PI);
		GlStateManager.rotatef(m * 20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		this.method_3223(this.field_4047);
	}

	private void method_3223(class_1799 arg) {
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scalef(0.38F, 0.38F, 0.38F);
		GlStateManager.disableLighting();
		this.field_4050.method_1531().method_4618(field_4049);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		GlStateManager.translatef(-0.5F, -0.5F, 0.0F);
		GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315(-7.0, 135.0, 0.0).method_1312(0.0, 1.0).method_1344();
		lv2.method_1315(135.0, 135.0, 0.0).method_1312(1.0, 1.0).method_1344();
		lv2.method_1315(135.0, -7.0, 0.0).method_1312(1.0, 0.0).method_1344();
		lv2.method_1315(-7.0, -7.0, 0.0).method_1312(0.0, 0.0).method_1344();
		lv.method_1350();
		class_22 lv3 = class_1806.method_8001(arg, this.field_4050.field_1687);
		if (lv3 != null) {
			this.field_4050.field_1773.method_3194().method_1773(lv3, false);
		}

		GlStateManager.enableLighting();
	}

	private void method_3219(float f, float g, class_1306 arg) {
		boolean bl = arg != class_1306.field_6182;
		float h = bl ? 1.0F : -1.0F;
		float i = class_3532.method_15355(g);
		float j = -0.3F * class_3532.method_15374(i * (float) Math.PI);
		float k = 0.4F * class_3532.method_15374(i * (float) (Math.PI * 2));
		float l = -0.4F * class_3532.method_15374(g * (float) Math.PI);
		GlStateManager.translatef(h * (j + 0.64000005F), k + -0.6F + f * -0.6F, l + -0.71999997F);
		GlStateManager.rotatef(h * 45.0F, 0.0F, 1.0F, 0.0F);
		float m = class_3532.method_15374(g * g * (float) Math.PI);
		float n = class_3532.method_15374(i * (float) Math.PI);
		GlStateManager.rotatef(h * n * 70.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(h * m * -20.0F, 0.0F, 0.0F, 1.0F);
		class_742 lv = this.field_4050.field_1724;
		this.field_4050.method_1531().method_4618(lv.method_3117());
		GlStateManager.translatef(h * -1.0F, 3.6F, 3.5F);
		GlStateManager.rotatef(h * 120.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotatef(200.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(h * -135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(h * 5.6F, 0.0F, 0.0F);
		class_1007 lv2 = this.field_4046.method_3957(lv);
		GlStateManager.disableCull();
		if (bl) {
			lv2.method_4220(lv);
		} else {
			lv2.method_4221(lv);
		}

		GlStateManager.enableCull();
	}

	private void method_3218(float f, class_1306 arg, class_1799 arg2) {
		float g = (float)this.field_4050.field_1724.method_6014() - f + 1.0F;
		float h = g / (float)arg2.method_7935();
		if (h < 0.8F) {
			float i = class_3532.method_15379(class_3532.method_15362(g / 4.0F * (float) Math.PI) * 0.1F);
			GlStateManager.translatef(0.0F, i, 0.0F);
		}

		float i = 1.0F - (float)Math.pow((double)h, 27.0);
		int j = arg == class_1306.field_6183 ? 1 : -1;
		GlStateManager.translatef(i * 0.6F * (float)j, i * -0.5F, i * 0.0F);
		GlStateManager.rotatef((float)j * i * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(i * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef((float)j * i * 30.0F, 0.0F, 0.0F, 1.0F);
	}

	private void method_3217(class_1306 arg, float f) {
		int i = arg == class_1306.field_6183 ? 1 : -1;
		float g = class_3532.method_15374(f * f * (float) Math.PI);
		GlStateManager.rotatef((float)i * (45.0F + g * -20.0F), 0.0F, 1.0F, 0.0F);
		float h = class_3532.method_15374(class_3532.method_15355(f) * (float) Math.PI);
		GlStateManager.rotatef((float)i * h * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotatef(h * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
	}

	private void method_3224(class_1306 arg, float f) {
		int i = arg == class_1306.field_6183 ? 1 : -1;
		GlStateManager.translatef((float)i * 0.56F, -0.52F + f * -0.6F, -0.72F);
	}

	public void method_3225(float f) {
		class_742 lv = this.field_4050.field_1724;
		float g = lv.method_6055(f);
		class_1268 lv2 = MoreObjects.firstNonNull(lv.field_6266, class_1268.field_5808);
		float h = class_3532.method_16439(f, lv.field_6004, lv.field_5965);
		float i = class_3532.method_16439(f, lv.field_5982, lv.field_6031);
		boolean bl = true;
		boolean bl2 = true;
		if (lv.method_6115()) {
			class_1799 lv3 = lv.method_6030();
			if (lv3.method_7909() == class_1802.field_8102 || lv3.method_7909() == class_1802.field_8399) {
				bl = lv.method_6058() == class_1268.field_5808;
				bl2 = !bl;
			}
		} else {
			class_1799 lv3 = lv.method_6047();
			class_1799 lv4 = lv.method_6079();
			if (lv3.method_7909() == class_1802.field_8399 && class_1764.method_7781(lv3)) {
				bl2 = !bl;
			}

			if (lv4.method_7909() == class_1802.field_8399 && class_1764.method_7781(lv4)) {
				bl = !lv3.method_7960();
				bl2 = !bl;
			}
		}

		this.method_3229(h, i);
		this.method_3235();
		this.method_3214(f);
		GlStateManager.enableRescaleNormal();
		if (bl) {
			float j = lv2 == class_1268.field_5808 ? g : 0.0F;
			float k = 1.0F - class_3532.method_16439(f, this.field_4053, this.field_4043);
			this.method_3228(lv, f, h, class_1268.field_5808, j, this.field_4047, k);
		}

		if (bl2) {
			float j = lv2 == class_1268.field_5810 ? g : 0.0F;
			float k = 1.0F - class_3532.method_16439(f, this.field_4051, this.field_4052);
			this.method_3228(lv, f, h, class_1268.field_5810, j, this.field_4048, k);
		}

		GlStateManager.disableRescaleNormal();
		class_308.method_1450();
	}

	public void method_3228(class_742 arg, float f, float g, class_1268 arg2, float h, class_1799 arg3, float i) {
		boolean bl = arg2 == class_1268.field_5808;
		class_1306 lv = bl ? arg.method_6068() : arg.method_6068().method_5928();
		GlStateManager.pushMatrix();
		if (arg3.method_7960()) {
			if (bl && !arg.method_5767()) {
				this.method_3219(i, h, lv);
			}
		} else if (arg3.method_7909() == class_1802.field_8204) {
			if (bl && this.field_4048.method_7960()) {
				this.method_3231(g, i, h);
			} else {
				this.method_3222(i, lv, h, arg3);
			}
		} else if (arg3.method_7909() == class_1802.field_8399) {
			if (!arg.method_5767()) {
				boolean bl2 = class_1764.method_7781(arg3);
				boolean bl3 = lv == class_1306.field_6183;
				int j = bl3 ? 1 : -1;
				if (arg.method_6115() && arg.method_6014() > 0 && arg.method_6058() == arg2) {
					this.method_3224(lv, i);
					GlStateManager.translatef((float)j * -0.4785682F, -0.094387F, 0.05731531F);
					GlStateManager.rotatef(-11.935F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef((float)j * 65.3F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotatef((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
					float k = (float)arg3.method_7935() - ((float)this.field_4050.field_1724.method_6014() - f + 1.0F);
					float l = k / (float)class_1764.method_7775(arg3);
					if (l > 1.0F) {
						l = 1.0F;
					}

					if (l > 0.1F) {
						float m = class_3532.method_15374((k - 0.1F) * 1.3F);
						float n = l - 0.1F;
						float o = m * n;
						GlStateManager.translatef(o * 0.0F, o * 0.004F, o * 0.0F);
					}

					GlStateManager.translatef(l * 0.0F, l * 0.0F, l * 0.04F);
					GlStateManager.scalef(1.0F, 1.0F, 1.0F + l * 0.2F);
					GlStateManager.rotatef((float)j * 45.0F, 0.0F, -1.0F, 0.0F);
				} else {
					float kx = -0.4F * class_3532.method_15374(class_3532.method_15355(h) * (float) Math.PI);
					float lx = 0.2F * class_3532.method_15374(class_3532.method_15355(h) * (float) (Math.PI * 2));
					float m = -0.2F * class_3532.method_15374(h * (float) Math.PI);
					GlStateManager.translatef((float)j * kx, lx, m);
					this.method_3224(lv, i);
					this.method_3217(lv, h);
					if (bl2 && h < 0.001F) {
						GlStateManager.translatef((float)j * -0.641864F, 0.0F, 0.0F);
						GlStateManager.rotatef((float)j * 10.0F, 0.0F, 1.0F, 0.0F);
					}
				}

				this.method_3234(arg, arg3, bl3 ? class_809.class_811.field_4322 : class_809.class_811.field_4321, !bl3);
			}
		} else {
			boolean bl2 = lv == class_1306.field_6183;
			if (arg.method_6115() && arg.method_6014() > 0 && arg.method_6058() == arg2) {
				int p = bl2 ? 1 : -1;
				switch (arg3.method_7976()) {
					case field_8952:
						this.method_3224(lv, i);
						break;
					case field_8950:
					case field_8946:
						this.method_3218(f, lv, arg3);
						this.method_3224(lv, i);
						break;
					case field_8949:
						this.method_3224(lv, i);
						break;
					case field_8953:
						this.method_3224(lv, i);
						GlStateManager.translatef((float)p * -0.2785682F, 0.18344387F, 0.15731531F);
						GlStateManager.rotatef(-13.935F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotatef((float)p * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotatef((float)p * -9.785F, 0.0F, 0.0F, 1.0F);
						float qx = (float)arg3.method_7935() - ((float)this.field_4050.field_1724.method_6014() - f + 1.0F);
						float kxx = qx / 20.0F;
						kxx = (kxx * kxx + kxx * 2.0F) / 3.0F;
						if (kxx > 1.0F) {
							kxx = 1.0F;
						}

						if (kxx > 0.1F) {
							float lx = class_3532.method_15374((qx - 0.1F) * 1.3F);
							float m = kxx - 0.1F;
							float n = lx * m;
							GlStateManager.translatef(n * 0.0F, n * 0.004F, n * 0.0F);
						}

						GlStateManager.translatef(kxx * 0.0F, kxx * 0.0F, kxx * 0.04F);
						GlStateManager.scalef(1.0F, 1.0F, 1.0F + kxx * 0.2F);
						GlStateManager.rotatef((float)p * 45.0F, 0.0F, -1.0F, 0.0F);
						break;
					case field_8951:
						this.method_3224(lv, i);
						GlStateManager.translatef((float)p * -0.5F, 0.7F, 0.1F);
						GlStateManager.rotatef(-55.0F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotatef((float)p * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotatef((float)p * -9.785F, 0.0F, 0.0F, 1.0F);
						float q = (float)arg3.method_7935() - ((float)this.field_4050.field_1724.method_6014() - f + 1.0F);
						float kx = q / 10.0F;
						if (kx > 1.0F) {
							kx = 1.0F;
						}

						if (kx > 0.1F) {
							float lx = class_3532.method_15374((q - 0.1F) * 1.3F);
							float m = kx - 0.1F;
							float n = lx * m;
							GlStateManager.translatef(n * 0.0F, n * 0.004F, n * 0.0F);
						}

						GlStateManager.translatef(0.0F, 0.0F, kx * 0.2F);
						GlStateManager.scalef(1.0F, 1.0F, 1.0F + kx * 0.2F);
						GlStateManager.rotatef((float)p * 45.0F, 0.0F, -1.0F, 0.0F);
				}
			} else if (arg.method_6123()) {
				this.method_3224(lv, i);
				int p = bl2 ? 1 : -1;
				GlStateManager.translatef((float)p * -0.4F, 0.8F, 0.3F);
				GlStateManager.rotatef((float)p * 65.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef((float)p * -85.0F, 0.0F, 0.0F, 1.0F);
			} else {
				float r = -0.4F * class_3532.method_15374(class_3532.method_15355(h) * (float) Math.PI);
				float qxx = 0.2F * class_3532.method_15374(class_3532.method_15355(h) * (float) (Math.PI * 2));
				float kxxx = -0.2F * class_3532.method_15374(h * (float) Math.PI);
				int s = bl2 ? 1 : -1;
				GlStateManager.translatef((float)s * r, qxx, kxxx);
				this.method_3224(lv, i);
				this.method_3217(lv, h);
			}

			this.method_3234(arg, arg3, bl2 ? class_809.class_811.field_4322 : class_809.class_811.field_4321, !bl2);
		}

		GlStateManager.popMatrix();
	}

	public void method_3232(float f) {
		GlStateManager.disableAlphaTest();
		if (this.field_4050.field_1724.method_5757()) {
			class_2680 lv = this.field_4050.field_1687.method_8320(new class_2338(this.field_4050.field_1724));
			class_1657 lv2 = this.field_4050.field_1724;

			for (int i = 0; i < 8; i++) {
				double d = lv2.field_5987 + (double)(((float)((i >> 0) % 2) - 0.5F) * lv2.field_5998 * 0.8F);
				double e = lv2.field_6010 + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
				double g = lv2.field_6035 + (double)(((float)((i >> 2) % 2) - 0.5F) * lv2.field_5998 * 0.8F);
				class_2338 lv3 = new class_2338(d, e + (double)lv2.method_5751(), g);
				class_2680 lv4 = this.field_4050.field_1687.method_8320(lv3);
				if (lv4.method_11582(this.field_4050.field_1687, lv3)) {
					lv = lv4;
				}
			}

			if (lv.method_11610() != class_2464.field_11455) {
				this.method_3226(this.field_4050.method_1541().method_3351().method_3339(lv));
			}
		}

		if (!this.field_4050.field_1724.method_7325()) {
			if (this.field_4050.field_1724.method_5777(class_3486.field_15517)) {
				this.method_3230(f);
			}

			if (this.field_4050.field_1724.method_5809()) {
				this.method_3236();
			}
		}

		GlStateManager.enableAlphaTest();
	}

	private void method_3226(class_1058 arg) {
		this.field_4050.method_1531().method_4618(class_1059.field_5275);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		float f = 0.1F;
		GlStateManager.color4f(0.1F, 0.1F, 0.1F, 0.5F);
		GlStateManager.pushMatrix();
		float g = -1.0F;
		float h = 1.0F;
		float i = -1.0F;
		float j = 1.0F;
		float k = -0.5F;
		float l = arg.method_4594();
		float m = arg.method_4577();
		float n = arg.method_4593();
		float o = arg.method_4575();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315(-1.0, -1.0, -0.5).method_1312((double)m, (double)o).method_1344();
		lv2.method_1315(1.0, -1.0, -0.5).method_1312((double)l, (double)o).method_1344();
		lv2.method_1315(1.0, 1.0, -0.5).method_1312((double)l, (double)n).method_1344();
		lv2.method_1315(-1.0, 1.0, -0.5).method_1312((double)m, (double)n).method_1344();
		lv.method_1350();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void method_3230(float f) {
		this.field_4050.method_1531().method_4618(field_4045);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		float g = this.field_4050.field_1724.method_5718();
		GlStateManager.color4f(g, g, g, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.pushMatrix();
		float h = 4.0F;
		float i = -1.0F;
		float j = 1.0F;
		float k = -1.0F;
		float l = 1.0F;
		float m = -0.5F;
		float n = -this.field_4050.field_1724.field_6031 / 64.0F;
		float o = this.field_4050.field_1724.field_5965 / 64.0F;
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315(-1.0, -1.0, -0.5).method_1312((double)(4.0F + n), (double)(4.0F + o)).method_1344();
		lv2.method_1315(1.0, -1.0, -0.5).method_1312((double)(0.0F + n), (double)(4.0F + o)).method_1344();
		lv2.method_1315(1.0, 1.0, -0.5).method_1312((double)(0.0F + n), (double)(0.0F + o)).method_1344();
		lv2.method_1315(-1.0, 1.0, -0.5).method_1312((double)(4.0F + n), (double)(0.0F + o)).method_1344();
		lv.method_1350();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
	}

	private void method_3236() {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.9F);
		GlStateManager.depthFunc(519);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		float f = 1.0F;

		for (int i = 0; i < 2; i++) {
			GlStateManager.pushMatrix();
			class_1058 lv3 = this.field_4050.method_1549().method_4608(class_1088.field_5370);
			this.field_4050.method_1531().method_4618(class_1059.field_5275);
			float g = lv3.method_4594();
			float h = lv3.method_4577();
			float j = lv3.method_4593();
			float k = lv3.method_4575();
			float l = -0.5F;
			float m = 0.5F;
			float n = -0.5F;
			float o = 0.5F;
			float p = -0.5F;
			GlStateManager.translatef((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
			GlStateManager.rotatef((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
			lv2.method_1328(7, class_290.field_1585);
			lv2.method_1315(-0.5, -0.5, -0.5).method_1312((double)h, (double)k).method_1344();
			lv2.method_1315(0.5, -0.5, -0.5).method_1312((double)g, (double)k).method_1344();
			lv2.method_1315(0.5, 0.5, -0.5).method_1312((double)g, (double)j).method_1344();
			lv2.method_1315(-0.5, 0.5, -0.5).method_1312((double)h, (double)j).method_1344();
			lv.method_1350();
			GlStateManager.popMatrix();
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(515);
	}

	public void method_3220() {
		this.field_4053 = this.field_4043;
		this.field_4051 = this.field_4052;
		class_746 lv = this.field_4050.field_1724;
		class_1799 lv2 = lv.method_6047();
		class_1799 lv3 = lv.method_6079();
		if (lv.method_3144()) {
			this.field_4043 = class_3532.method_15363(this.field_4043 - 0.4F, 0.0F, 1.0F);
			this.field_4052 = class_3532.method_15363(this.field_4052 - 0.4F, 0.0F, 1.0F);
		} else {
			float f = lv.method_7261(1.0F);
			this.field_4043 = this.field_4043 + class_3532.method_15363((Objects.equals(this.field_4047, lv2) ? f * f * f : 0.0F) - this.field_4043, -0.4F, 0.4F);
			this.field_4052 = this.field_4052 + class_3532.method_15363((float)(Objects.equals(this.field_4048, lv3) ? 1 : 0) - this.field_4052, -0.4F, 0.4F);
		}

		if (this.field_4043 < 0.1F) {
			this.field_4047 = lv2;
		}

		if (this.field_4052 < 0.1F) {
			this.field_4048 = lv3;
		}
	}

	public void method_3215(class_1268 arg) {
		if (arg == class_1268.field_5808) {
			this.field_4043 = 0.0F;
		} else {
			this.field_4052 = 0.0F;
		}
	}
}
