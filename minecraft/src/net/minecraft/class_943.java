package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_943 extends class_927<class_1606, class_602<class_1606>> {
	public static final class_2960 field_4781 = new class_2960("textures/entity/shulker/shulker.png");
	public static final class_2960[] field_4780 = new class_2960[]{
		new class_2960("textures/entity/shulker/shulker_white.png"),
		new class_2960("textures/entity/shulker/shulker_orange.png"),
		new class_2960("textures/entity/shulker/shulker_magenta.png"),
		new class_2960("textures/entity/shulker/shulker_light_blue.png"),
		new class_2960("textures/entity/shulker/shulker_yellow.png"),
		new class_2960("textures/entity/shulker/shulker_lime.png"),
		new class_2960("textures/entity/shulker/shulker_pink.png"),
		new class_2960("textures/entity/shulker/shulker_gray.png"),
		new class_2960("textures/entity/shulker/shulker_light_gray.png"),
		new class_2960("textures/entity/shulker/shulker_cyan.png"),
		new class_2960("textures/entity/shulker/shulker_purple.png"),
		new class_2960("textures/entity/shulker/shulker_blue.png"),
		new class_2960("textures/entity/shulker/shulker_brown.png"),
		new class_2960("textures/entity/shulker/shulker_green.png"),
		new class_2960("textures/entity/shulker/shulker_red.png"),
		new class_2960("textures/entity/shulker/shulker_black.png")
	};

	public class_943(class_898 arg) {
		super(arg, new class_602<>(), 0.0F);
		this.method_4046(new class_944(this));
	}

	public void method_4113(class_1606 arg, double d, double e, double f, float g, float h) {
		int i = arg.method_7113();
		if (i > 0 && arg.method_7117()) {
			class_2338 lv = arg.method_7123();
			class_2338 lv2 = arg.method_7120();
			double j = (double)((float)i - h) / 6.0;
			j *= j;
			double k = (double)(lv.method_10263() - lv2.method_10263()) * j;
			double l = (double)(lv.method_10264() - lv2.method_10264()) * j;
			double m = (double)(lv.method_10260() - lv2.method_10260()) * j;
			super.method_4072(arg, d - k, e - l, f - m, g, h);
		} else {
			super.method_4072(arg, d, e, f, g, h);
		}
	}

	public boolean method_4112(class_1606 arg, class_856 arg2, double d, double e, double f) {
		if (super.method_4068(arg, arg2, d, e, f)) {
			return true;
		} else {
			if (arg.method_7113() > 0 && arg.method_7117()) {
				class_2338 lv = arg.method_7120();
				class_2338 lv2 = arg.method_7123();
				class_243 lv3 = new class_243((double)lv2.method_10263(), (double)lv2.method_10264(), (double)lv2.method_10260());
				class_243 lv4 = new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260());
				if (arg2.method_3699(new class_238(lv4.field_1352, lv4.field_1351, lv4.field_1350, lv3.field_1352, lv3.field_1351, lv3.field_1350))) {
					return true;
				}
			}

			return false;
		}
	}

	protected class_2960 method_4111(class_1606 arg) {
		return arg.method_7121() == null ? field_4781 : field_4780[arg.method_7121().method_7789()];
	}

	protected void method_4114(class_1606 arg, float f, float g, float h) {
		super.method_4058(arg, f, g, h);
		switch (arg.method_7119()) {
			case field_11033:
			default:
				break;
			case field_11034:
				GlStateManager.translatef(0.5F, 0.5F, 0.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
				break;
			case field_11039:
				GlStateManager.translatef(-0.5F, 0.5F, 0.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				break;
			case field_11043:
				GlStateManager.translatef(0.0F, 0.5F, -0.5F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				break;
			case field_11035:
				GlStateManager.translatef(0.0F, 0.5F, 0.5F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				break;
			case field_11036:
				GlStateManager.translatef(0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
		}
	}

	protected void method_4109(class_1606 arg, float f) {
		float g = 0.999F;
		GlStateManager.scalef(0.999F, 0.999F, 0.999F);
	}
}
