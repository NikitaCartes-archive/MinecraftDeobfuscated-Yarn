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

	public static ShortBuffer method_35614(int i) {
		return allocateByteBuffer(i << 1).asShortBuffer();
	}

	public static CharBuffer method_35615(int i) {
		return allocateByteBuffer(i << 1).asCharBuffer();
	}

	public static IntBuffer method_35616(int i) {
		return allocateByteBuffer(i << 2).asIntBuffer();
	}

	public static LongBuffer method_35617(int i) {
		return allocateByteBuffer(i << 3).asLongBuffer();
	}

	public static FloatBuffer method_35618(int i) {
		return allocateByteBuffer(i << 2).asFloatBuffer();
	}

	public static DoubleBuffer method_35619(int i) {
		return allocateByteBuffer(i << 3).asDoubleBuffer();
	}
}
