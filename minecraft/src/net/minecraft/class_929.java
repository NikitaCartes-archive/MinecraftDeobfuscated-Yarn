package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_929 extends class_927<class_1451, class_3680<class_1451>> {
	public class_929(class_898 arg) {
		super(arg, new class_3680<>(0.0F), 0.4F);
		this.method_4046(new class_3684(this));
	}

	@Nullable
	protected class_2960 method_4078(class_1451 arg) {
		return arg.method_16092();
	}

	protected void method_4079(class_1451 arg, float f) {
		super.method_4042(arg, f);
		GlStateManager.scalef(0.8F, 0.8F, 0.8F);
	}

	protected void method_16045(class_1451 arg, float f, float g, float h) {
		super.method_4058(arg, f, g, h);
		float i = arg.method_16082(h);
		if (i > 0.0F) {
			GlStateManager.translatef(0.4F * i, 0.15F * i, 0.1F * i);
			GlStateManager.rotatef(class_3532.method_17821(i, 0.0F, 90.0F), 0.0F, 0.0F, 1.0F);
			class_2338 lv = new class_2338(arg);

			for (class_1657 lv2 : arg.field_6002.method_18467(class_1657.class, new class_238(lv).method_1009(2.0, 2.0, 2.0))) {
				if (lv2.method_6113()) {
					GlStateManager.translatef(0.15F * i, 0.0F, 0.0F);
					break;
				}
			}
		}
	}
}
