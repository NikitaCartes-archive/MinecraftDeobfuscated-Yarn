package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_915 extends class_897<class_1533> {
	private static final class_2960 field_4722 = new class_2960("textures/map/map_background.png");
	private static final class_1091 field_4721 = new class_1091("item_frame", "map=false");
	private static final class_1091 field_4723 = new class_1091("item_frame", "map=true");
	private final class_310 field_4724 = class_310.method_1551();
	private final class_918 field_4720;

	public class_915(class_898 arg, class_918 arg2) {
		super(arg);
		this.field_4720 = arg2;
	}

	public void method_3994(class_1533 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		class_2338 lv = arg.method_6896();
		double i = (double)lv.method_10263() - arg.field_5987 + d;
		double j = (double)lv.method_10264() - arg.field_6010 + e;
		double k = (double)lv.method_10260() - arg.field_6035 + f;
		GlStateManager.translated(i + 0.5, j + 0.5, k + 0.5);
		GlStateManager.rotatef(arg.field_5965, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(180.0F - arg.field_6031, 0.0F, 1.0F, 0.0F);
		this.field_4676.field_4685.method_4618(class_1059.field_5275);
		class_776 lv2 = this.field_4724.method_1541();
		class_1092 lv3 = lv2.method_3351().method_3333();
		class_1091 lv4 = arg.method_6940().method_7909() == class_1802.field_8204 ? field_4723 : field_4721;
		GlStateManager.pushMatrix();
		GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		lv2.method_3350().method_3368(lv3.method_4742(lv4), 1.0F, 1.0F, 1.0F, 1.0F);
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		if (arg.method_6940().method_7909() == class_1802.field_8204) {
			GlStateManager.pushLightingAttributes();
			class_308.method_1452();
		}

		GlStateManager.translatef(0.0F, 0.0F, 0.4375F);
		this.method_3992(arg);
		if (arg.method_6940().method_7909() == class_1802.field_8204) {
			class_308.method_1450();
			GlStateManager.popAttributes();
		}

		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
		this.method_3995(arg, d + (double)((float)arg.field_7099.method_10148() * 0.3F), e - 0.25, f + (double)((float)arg.field_7099.method_10165() * 0.3F));
	}

	@Nullable
	protected class_2960 method_3993(class_1533 arg) {
		return null;
	}

	private void method_3992(class_1533 arg) {
		class_1799 lv = arg.method_6940();
		if (!lv.method_7960()) {
			GlStateManager.pushMatrix();
			boolean bl = lv.method_7909() == class_1802.field_8204;
			int i = bl ? arg.method_6934() % 4 * 2 : arg.method_6934();
			GlStateManager.rotatef((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
			if (bl) {
				GlStateManager.disableLighting();
				this.field_4676.field_4685.method_4618(field_4722);
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				float f = 0.0078125F;
				GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
				GlStateManager.translatef(-64.0F, -64.0F, 0.0F);
				class_22 lv2 = class_1806.method_8001(lv, arg.field_6002);
				GlStateManager.translatef(0.0F, 0.0F, -1.0F);
				if (lv2 != null) {
					this.field_4724.field_1773.method_3194().method_1773(lv2, true);
				}
			} else {
				GlStateManager.scalef(0.5F, 0.5F, 0.5F);
				this.field_4720.method_4009(lv, class_809.class_811.field_4319);
			}

			GlStateManager.popMatrix();
		}
	}

	protected void method_3995(class_1533 arg, double d, double e, double f) {
		if (class_310.method_1498() && !arg.method_6940().method_7960() && arg.method_6940().method_7938() && this.field_4676.field_4678 == arg) {
			double g = arg.method_5858(this.field_4676.field_4686);
			float h = arg.method_5715() ? 32.0F : 64.0F;
			if (!(g >= (double)(h * h))) {
				String string = arg.method_6940().method_7964().method_10863();
				this.method_3923(arg, string, d, e, f, 64);
			}
		}
	}
}
