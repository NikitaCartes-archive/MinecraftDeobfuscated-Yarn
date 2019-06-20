package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_286 {
	public void method_1309(class_287 arg) {
		if (arg.method_1337() > 0) {
			class_293 lv = arg.method_1311();
			int i = lv.method_1362();
			ByteBuffer byteBuffer = arg.method_1342();
			List<class_296> list = lv.method_1357();

			for (int j = 0; j < list.size(); j++) {
				class_296 lv2 = (class_296)list.get(j);
				class_296.class_298 lv3 = lv2.method_1382();
				int k = lv2.method_1386().method_1390();
				int l = lv2.method_1385();
				byteBuffer.position(lv.method_1365(j));
				switch (lv3) {
					case field_1633:
						GlStateManager.vertexPointer(lv2.method_1384(), k, i, byteBuffer);
						GlStateManager.enableClientState(32884);
						break;
					case field_1636:
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + l);
						GlStateManager.texCoordPointer(lv2.method_1384(), k, i, byteBuffer);
						GlStateManager.enableClientState(32888);
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
						break;
					case field_1632:
						GlStateManager.colorPointer(lv2.method_1384(), k, i, byteBuffer);
						GlStateManager.enableClientState(32886);
						break;
					case field_1635:
						GlStateManager.normalPointer(k, i, byteBuffer);
						GlStateManager.enableClientState(32885);
				}
			}

			GlStateManager.drawArrays(arg.method_1338(), 0, arg.method_1337());
			int j = 0;

			for (int m = list.size(); j < m; j++) {
				class_296 lv4 = (class_296)list.get(j);
				class_296.class_298 lv5 = lv4.method_1382();
				int l = lv4.method_1385();
				switch (lv5) {
					case field_1633:
						GlStateManager.disableClientState(32884);
						break;
					case field_1636:
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + l);
						GlStateManager.disableClientState(32888);
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
						break;
					case field_1632:
						GlStateManager.disableClientState(32886);
						GlStateManager.clearCurrentColor();
						break;
					case field_1635:
						GlStateManager.disableClientState(32885);
				}
			}
		}

		arg.method_1343();
	}
}
