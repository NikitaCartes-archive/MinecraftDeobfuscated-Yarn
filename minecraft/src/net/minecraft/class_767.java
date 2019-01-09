package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_767 extends class_752 {
	@Override
	public void method_3160(class_1921 arg) {
		if (this.field_3956) {
			for (class_851 lv : this.field_3955) {
				class_848 lv2 = (class_848)lv;
				GlStateManager.pushMatrix();
				this.method_3157(lv);
				GlStateManager.callList(lv2.method_3639(arg, lv2.method_3677()));
				GlStateManager.popMatrix();
			}

			GlStateManager.clearCurrentColor();
			this.field_3955.clear();
		}
	}
}
