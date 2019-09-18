package net.minecraft.client.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlAllocationUtils {
	public static synchronized ByteBuffer allocateByteBuffer(int i) {
		return ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
	}

	public static FloatBuffer allocateFloatBuffer(int i) {
		return allocateByteBuffer(i << 2).asFloatBuffer();
	}
}
