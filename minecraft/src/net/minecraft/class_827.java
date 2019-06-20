package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_827<T extends class_2586> {
	public static final class_2960[] field_4368 = new class_2960[]{
		new class_2960("textures/" + class_1088.field_5377.method_12832() + ".png"),
		new class_2960("textures/" + class_1088.field_5385.method_12832() + ".png"),
		new class_2960("textures/" + class_1088.field_5375.method_12832() + ".png"),
		new class_2960("textures/" + class_1088.field_5403.method_12832() + ".png"),
		new class_2960("textures/" + class_1088.field_5393.method_12832() + ".png"),
		new class_2960("textures/" + class_1088.field_5386.method_12832() + ".png"),
		new class_2960("textures/" + class_1088.field_5369.method_12832() + ".png"),
		new class_2960("textures/" + class_1088.field_5401.method_12832() + ".png"),
		new class_2960("textures/" + class_1088.field_5392.method_12832() + ".png"),
		new class_2960("textures/" + class_1088.field_5382.method_12832() + ".png")
	};
	protected class_824 field_4367;

	public void method_3569(T arg, double d, double e, double f, float g, int i) {
		class_239 lv = this.field_4367.field_4350;
		if (arg instanceof class_1275
			&& lv != null
			&& lv.method_17783() == class_239.class_240.field_1332
			&& arg.method_11016().equals(((class_3965)lv).method_17777())) {
			this.method_3570(true);
			this.method_3567(arg, ((class_1275)arg).method_5476().method_10863(), d, e, f, 12);
			this.method_3570(false);
		}
	}

	protected void method_3570(boolean bl) {
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		if (bl) {
			GlStateManager.disableTexture();
		} else {
			GlStateManager.enableTexture();
		}

		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	protected void method_3566(class_2960 arg) {
		class_1060 lv = this.field_4367.field_4347;
		if (lv != null) {
			lv.method_4618(arg);
		}
	}

	protected class_1937 method_3565() {
		return this.field_4367.field_4348;
	}

	public void method_3568(class_824 arg) {
		this.field_4367 = arg;
	}

	public class_327 method_3564() {
		return this.field_4367.method_3556();
	}

	public boolean method_3563(T arg) {
		return false;
	}

	protected void method_3567(T arg, String string, double d, double e, double f, int i) {
		class_4184 lv = this.field_4367.field_4344;
		double g = arg.method_11008(lv.method_19326().field_1352, lv.method_19326().field_1351, lv.method_19326().field_1350);
		if (!(g > (double)(i * i))) {
			float h = lv.method_19330();
			float j = lv.method_19329();
			class_757.method_3179(this.method_3564(), string, (float)d + 0.5F, (float)e + 1.5F, (float)f + 0.5F, 0, h, j, false);
		}
	}
}
