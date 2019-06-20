package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_886 extends class_897<class_1297> {
	public class_886(class_898 arg) {
		super(arg);
	}

	@Override
	public void method_3936(class_1297 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		method_3922(arg.method_5829(), d - arg.field_6038, e - arg.field_5971, f - arg.field_5989);
		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	@Nullable
	@Override
	protected class_2960 method_3931(class_1297 arg) {
		return null;
	}
}
