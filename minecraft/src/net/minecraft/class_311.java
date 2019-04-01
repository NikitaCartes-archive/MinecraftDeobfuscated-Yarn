package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_311 {
	public static synchronized int method_1593(int i) {
		int j = GlStateManager.genLists(i);
		if (j == 0) {
			int k = GlStateManager.getError();
			String string = "No error code reported";
			if (k != 0) {
				string = GLX.getErrorString(k);
			}

			throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + i + ", GL error (" + k + "): " + string);
		} else {
			return j;
		}
	}

	public static synchronized void method_1594(int i, int j) {
		GlStateManager.deleteLists(i, j);
	}

	public static synchronized void method_1595(int i) {
		method_1594(i, 1);
	}

	public static synchronized ByteBuffer method_1596(int i) {
		return ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
	}

	public static FloatBuffer method_1597(int i) {
		return method_1596(i << 2).asFloatBuffer();
	}
}
