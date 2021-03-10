package net.minecraft.client.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlAllocationUtils {
	public static synchronized ByteBuffer allocateByteBuffer(int size) {
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}
}
