package net.minecraft.client.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlAllocationUtils {
	public static synchronized ByteBuffer allocateByteBuffer(int size) {
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}

	public static ShortBuffer allocateShortBuffer(int size) {
		return allocateByteBuffer(size << 1).asShortBuffer();
	}

	public static CharBuffer allocateCharBuffer(int size) {
		return allocateByteBuffer(size << 1).asCharBuffer();
	}

	public static IntBuffer allocateIntBuffer(int size) {
		return allocateByteBuffer(size << 2).asIntBuffer();
	}

	public static LongBuffer allocateLongBuffer(int size) {
		return allocateByteBuffer(size << 3).asLongBuffer();
	}

	public static FloatBuffer allocateFloatBuffer(int size) {
		return allocateByteBuffer(size << 2).asFloatBuffer();
	}

	public static DoubleBuffer allocateDoubleBuffer(int size) {
		return allocateByteBuffer(size << 3).asDoubleBuffer();
	}
}
