package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_826<T extends class_2586 & class_2618> extends class_827<T> {
	private static final class_2960 field_4360 = new class_2960("textures/entity/chest/trapped_double.png");
	private static final class_2960 field_4362 = new class_2960("textures/entity/chest/christmas_double.png");
	private static final class_2960 field_4359 = new class_2960("textures/entity/chest/normal_double.png");
	private static final class_2960 field_4357 = new class_2960("textures/entity/chest/trapped.png");
	private static final class_2960 field_4363 = new class_2960("textures/entity/chest/christmas.png");
	private static final class_2960 field_4364 = new class_2960("textures/entity/chest/normal.png");
	private static final class_2960 field_4366 = new class_2960("textures/entity/chest/ender.png");
	private final class_556 field_4361 = new class_556();
	private final class_556 field_4358 = new class_577();
	private boolean field_4365;

	public class_826() {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.field_4365 = true;
		}
	}

	@Override
	public void method_3569(T arg, double d, double e, double f, float g, int i) {
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		class_2680 lv = arg.method_11002() ? arg.method_11010() : class_2246.field_10034.method_9564().method_11657(class_2281.field_10768, class_2350.field_11035);
		class_2745 lv2 = lv.method_11570((class_2769<T>)class_2281.field_10770) ? lv.method_11654(class_2281.field_10770) : class_2745.field_12569;
		if (lv2 != class_2745.field_12574) {
			boolean bl = lv2 != class_2745.field_12569;
			class_556 lv3 = this.method_3562(arg, i, bl);
			if (i >= 0) {
				GlStateManager.matrixMode(5890);
				GlStateManager.pushMatrix();
				GlStateManager.scalef(bl ? 8.0F : 4.0F, 4.0F, 1.0F);
				GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
				GlStateManager.matrixMode(5888);
			} else {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();
			GlStateManager.translatef((float)d, (float)e + 1.0F, (float)f + 1.0F);
			GlStateManager.scalef(1.0F, -1.0F, -1.0F);
			float h = ((class_2350)lv.method_11654(class_2281.field_10768)).method_10144();
			if ((double)Math.abs(h) > 1.0E-5) {
				GlStateManager.translatef(0.5F, 0.5F, 0.5F);
				GlStateManager.rotatef(h, 0.0F, 1.0F, 0.0F);
				GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
			}

			this.method_3561(arg, g, lv3);
			lv3.method_2799();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (i >= 0) {
				GlStateManager.matrixMode(5890);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
			}
		}
	}

	private class_556 method_3562(T arg, int i, boolean bl) {
		class_2960 lv;
		if (i >= 0) {
			lv = field_4368[i];
		} else if (this.field_4365) {
			lv = bl ? field_4362 : field_4363;
		} else if (arg instanceof class_2646) {
			lv = bl ? field_4360 : field_4357;
		} else if (arg instanceof class_2611) {
			lv = field_4366;
		} else {
			lv = bl ? field_4359 : field_4364;
		}

		this.method_3566(lv);
		return bl ? this.field_4358 : this.field_4361;
	}

	private void method_3561(T arg, float f, class_556 arg2) {
		float g = arg.method_11274(f);
		g = 1.0F - g;
		g = 1.0F - g * g * g;
		arg2.method_2798().field_3654 = -(g * (float) (Math.PI / 2));
	}
}
