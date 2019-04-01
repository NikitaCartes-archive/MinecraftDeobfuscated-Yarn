package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_829 extends class_827<class_2597> {
	private static final class_2960 field_4377 = new class_2960("textures/entity/conduit/base.png");
	private static final class_2960 field_4378 = new class_2960("textures/entity/conduit/cage.png");
	private static final class_2960 field_4373 = new class_2960("textures/entity/conduit/wind.png");
	private static final class_2960 field_4371 = new class_2960("textures/entity/conduit/wind_vertical.png");
	private static final class_2960 field_4379 = new class_2960("textures/entity/conduit/open_eye.png");
	private static final class_2960 field_4380 = new class_2960("textures/entity/conduit/closed_eye.png");
	private final class_829.class_832 field_4372 = new class_829.class_832();
	private final class_829.class_830 field_4375 = new class_829.class_830();
	private final class_829.class_833 field_4374 = new class_829.class_833();
	private final class_829.class_831 field_4376 = new class_829.class_831();

	public void method_3572(class_2597 arg, double d, double e, double f, float g, int i) {
		float h = (float)arg.field_11936 + g;
		if (!arg.method_11065()) {
			float j = arg.method_11061(0.0F);
			this.method_3566(field_4377);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			GlStateManager.rotatef(j, 0.0F, 1.0F, 0.0F);
			this.field_4372.method_17142(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
		} else if (arg.method_11065()) {
			float j = arg.method_11061(g) * (180.0F / (float)Math.PI);
			float k = class_3532.method_15374(h * 0.1F) / 2.0F + 0.5F;
			k = k * k + k;
			this.method_3566(field_4378);
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.3F + k * 0.2F, (float)f + 0.5F);
			GlStateManager.rotatef(j, 0.5F, 1.0F, 0.5F);
			this.field_4375.method_17140(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
			int l = 3;
			int m = arg.field_11936 / 3 % 22;
			this.field_4374.method_3573(m);
			int n = arg.field_11936 / 66 % 3;
			switch (n) {
				case 0:
					this.method_3566(field_4373);
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					this.field_4374.method_17143(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scalef(0.875F, 0.875F, 0.875F);
					GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					this.field_4374.method_17143(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					break;
				case 1:
					this.method_3566(field_4371);
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
					this.field_4374.method_17143(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scalef(0.875F, 0.875F, 0.875F);
					GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					this.field_4374.method_17143(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					break;
				case 2:
					this.method_3566(field_4373);
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
					this.field_4374.method_17143(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scalef(0.875F, 0.875F, 0.875F);
					GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					this.field_4374.method_17143(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
			}

			class_4184 lv = this.field_4367.field_4344;
			if (arg.method_11066()) {
				this.method_3566(field_4379);
			} else {
				this.method_3566(field_4380);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.3F + k * 0.2F, (float)f + 0.5F);
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.rotatef(-lv.method_19330(), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(lv.method_19329(), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
			this.field_4376.method_17141(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.083333336F);
			GlStateManager.popMatrix();
		}

		super.method_3569(arg, d, e, f, g, i);
	}

	@Environment(EnvType.CLIENT)
	static class class_830 extends class_3879 {
		private final class_630 field_4381;

		public class_830() {
			this.field_17138 = 32;
			this.field_17139 = 16;
			this.field_4381 = new class_630(this, 0, 0);
			this.field_4381.method_2844(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		}

		public void method_17140(float f, float g, float h, float i, float j, float k) {
			this.field_4381.method_2846(k);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_831 extends class_3879 {
		private final class_630 field_4382;

		public class_831() {
			this.field_17138 = 8;
			this.field_17139 = 8;
			this.field_4382 = new class_630(this, 0, 0);
			this.field_4382.method_2856(-4.0F, -4.0F, 0.0F, 8, 8, 0, 0.01F);
		}

		public void method_17141(float f, float g, float h, float i, float j, float k) {
			this.field_4382.method_2846(k);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_832 extends class_3879 {
		private final class_630 field_4383;

		public class_832() {
			this.field_17138 = 32;
			this.field_17139 = 16;
			this.field_4383 = new class_630(this, 0, 0);
			this.field_4383.method_2844(-3.0F, -3.0F, -3.0F, 6, 6, 6);
		}

		public void method_17142(float f, float g, float h, float i, float j, float k) {
			this.field_4383.method_2846(k);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_833 extends class_3879 {
		private final class_630[] field_4386 = new class_630[22];
		private int field_4384;

		public class_833() {
			this.field_17138 = 64;
			this.field_17139 = 1024;

			for (int i = 0; i < 22; i++) {
				this.field_4386[i] = new class_630(this, 0, 32 * i);
				this.field_4386[i].method_2844(-8.0F, -8.0F, -8.0F, 16, 16, 16);
			}
		}

		public void method_17143(float f, float g, float h, float i, float j, float k) {
			this.field_4386[this.field_4384].method_2846(k);
		}

		public void method_3573(int i) {
			this.field_4384 = i;
		}
	}
}
