package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlAllocationUtils {
	public static synchronized int genLists(int i) {
		int j = RenderSystem.genLists(i);
		if (j == 0) {
			int k = RenderSystem.getError();
			String string = "No error code reported";
			if (k != 0) {
				string = GLX.getErrorString(k);
			}

			throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + i + ", GL error (" + k + "): " + string);
		} else {
			return j;
		}
	}

	public static synchronized void deleteLists(int i, int j) {
		RenderSystem.deleteLists(i, j);
	}

	public static synchronized void deleteSingletonList(int i) {
		deleteLists(i, 1);
	}

	public static synchronized ByteBuffer allocateByteBuffer(int i) {
		return ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
	}

	public static FloatBuffer allocateFloatBuffer(int i) {
		return allocateByteBuffer(i << 2).asFloatBuffer();
	}
}
