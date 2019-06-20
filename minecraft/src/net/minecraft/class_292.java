package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_292 extends class_752 {
	@Override
	public void method_3160(class_1921 arg) {
		if (this.field_3956) {
			for (class_851 lv : this.field_3955) {
				class_291 lv2 = lv.method_3656(arg.ordinal());
				GlStateManager.pushMatrix();
				this.method_3157(lv);
				lv2.method_1353();
				this.method_1356();
				lv2.method_1351(7);
				GlStateManager.popMatrix();
			}

			class_291.method_1354();
			GlStateManager.clearCurrentColor();
			this.field_3955.clear();
		}
	}

	private void method_1356() {
		GlStateManager.vertexPointer(3, 5126, 28, 0);
		GlStateManager.colorPointer(4, 5121, 28, 12);
		GlStateManager.texCoordPointer(2, 5126, 28, 16);
		GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
		GlStateManager.texCoordPointer(2, 5122, 28, 24);
		GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
	}
}
