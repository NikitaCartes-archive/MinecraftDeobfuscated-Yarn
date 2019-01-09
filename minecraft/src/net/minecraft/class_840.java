package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_840 extends class_827<class_2640> {
	private static final class_2960 field_4406 = new class_2960("textures/environment/end_sky.png");
	private static final class_2960 field_4407 = new class_2960("textures/entity/end_portal.png");
	private static final Random field_4405 = new Random(31100L);
	private static final FloatBuffer field_4408 = class_311.method_1597(16);
	private static final FloatBuffer field_4404 = class_311.method_1597(16);
	private final FloatBuffer field_4403 = class_311.method_1597(16);

	public void method_3591(class_2640 arg, double d, double e, double f, float g, int i) {
		GlStateManager.disableLighting();
		field_4405.setSeed(31100L);
		GlStateManager.getMatrix(2982, field_4408);
		GlStateManager.getMatrix(2983, field_4404);
		double h = d * d + e * e + f * f;
		int j = this.method_3592(h);
		float k = this.method_3594();
		boolean bl = false;
		class_757 lv = class_310.method_1551().field_1773;

		for (int l = 0; l < j; l++) {
			GlStateManager.pushMatrix();
			float m = 2.0F / (float)(18 - l);
			if (l == 0) {
				this.method_3566(field_4406);
				m = 0.15F;
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088);
			}

			if (l >= 1) {
				this.method_3566(field_4407);
				bl = true;
				lv.method_3201(true);
			}

			if (l == 1) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5078);
			}

			GlStateManager.texGenMode(GlStateManager.class_1036.field_5154, 9216);
			GlStateManager.texGenMode(GlStateManager.class_1036.field_5155, 9216);
			GlStateManager.texGenMode(GlStateManager.class_1036.field_5156, 9216);
			GlStateManager.texGenParam(GlStateManager.class_1036.field_5154, 9474, this.method_3593(1.0F, 0.0F, 0.0F, 0.0F));
			GlStateManager.texGenParam(GlStateManager.class_1036.field_5155, 9474, this.method_3593(0.0F, 1.0F, 0.0F, 0.0F));
			GlStateManager.texGenParam(GlStateManager.class_1036.field_5156, 9474, this.method_3593(0.0F, 0.0F, 1.0F, 0.0F));
			GlStateManager.enableTexGen(GlStateManager.class_1036.field_5154);
			GlStateManager.enableTexGen(GlStateManager.class_1036.field_5155);
			GlStateManager.enableTexGen(GlStateManager.class_1036.field_5156);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.translatef(0.5F, 0.5F, 0.0F);
			GlStateManager.scalef(0.5F, 0.5F, 1.0F);
			float n = (float)(l + 1);
			GlStateManager.translatef(17.0F / n, (2.0F + n / 1.5F) * ((float)(class_156.method_658() % 800000L) / 800000.0F), 0.0F);
			GlStateManager.rotatef((n * n * 4321.0F + n * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.scalef(4.5F - n / 4.0F, 4.5F - n / 4.0F, 1.0F);
			GlStateManager.multMatrix(field_4404);
			GlStateManager.multMatrix(field_4408);
			class_289 lv2 = class_289.method_1348();
			class_287 lv3 = lv2.method_1349();
			lv3.method_1328(7, class_290.field_1576);
			float o = (field_4405.nextFloat() * 0.5F + 0.1F) * m;
			float p = (field_4405.nextFloat() * 0.5F + 0.4F) * m;
			float q = (field_4405.nextFloat() * 0.5F + 0.5F) * m;
			if (arg.method_11400(class_2350.field_11035)) {
				lv3.method_1315(d, e, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e + 1.0, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d, e + 1.0, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
			}

			if (arg.method_11400(class_2350.field_11043)) {
				lv3.method_1315(d, e + 1.0, f).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e + 1.0, f).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e, f).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d, e, f).method_1336(o, p, q, 1.0F).method_1344();
			}

			if (arg.method_11400(class_2350.field_11034)) {
				lv3.method_1315(d + 1.0, e + 1.0, f).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e + 1.0, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e, f).method_1336(o, p, q, 1.0F).method_1344();
			}

			if (arg.method_11400(class_2350.field_11039)) {
				lv3.method_1315(d, e, f).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d, e, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d, e + 1.0, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d, e + 1.0, f).method_1336(o, p, q, 1.0F).method_1344();
			}

			if (arg.method_11400(class_2350.field_11033)) {
				lv3.method_1315(d, e, f).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e, f).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d, e, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
			}

			if (arg.method_11400(class_2350.field_11036)) {
				lv3.method_1315(d, e + (double)k, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e + (double)k, f + 1.0).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d + 1.0, e + (double)k, f).method_1336(o, p, q, 1.0F).method_1344();
				lv3.method_1315(d, e + (double)k, f).method_1336(o, p, q, 1.0F).method_1344();
			}

			lv2.method_1350();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			this.method_3566(field_4406);
		}

		GlStateManager.disableBlend();
		GlStateManager.disableTexGen(GlStateManager.class_1036.field_5154);
		GlStateManager.disableTexGen(GlStateManager.class_1036.field_5155);
		GlStateManager.disableTexGen(GlStateManager.class_1036.field_5156);
		GlStateManager.enableLighting();
		if (bl) {
			lv.method_3201(false);
		}
	}

	protected int method_3592(double d) {
		int i;
		if (d > 36864.0) {
			i = 1;
		} else if (d > 25600.0) {
			i = 3;
		} else if (d > 16384.0) {
			i = 5;
		} else if (d > 9216.0) {
			i = 7;
		} else if (d > 4096.0) {
			i = 9;
		} else if (d > 1024.0) {
			i = 11;
		} else if (d > 576.0) {
			i = 13;
		} else if (d > 256.0) {
			i = 14;
		} else {
			i = 15;
		}

		return i;
	}

	protected float method_3594() {
		return 0.75F;
	}

	private FloatBuffer method_3593(float f, float g, float h, float i) {
		this.field_4403.clear();
		this.field_4403.put(f).put(g).put(h).put(i);
		this.field_4403.flip();
		return this.field_4403;
	}
}
