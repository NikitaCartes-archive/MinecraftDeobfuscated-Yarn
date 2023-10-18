package net.minecraft.client.util;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.MemoryUtil.MemoryAllocator;

@Environment(EnvType.CLIENT)
public class GlAllocationUtils {
	private static final MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);

	public static ByteBuffer allocateByteBuffer(int size) {
		long l = ALLOCATOR.malloc((long)size);
		if (l == 0L) {
			throw new OutOfMemoryError("Failed to allocate " + size + " bytes");
		} else {
			return MemoryUtil.memByteBuffer(l, size);
		}
	}

	public static ByteBuffer resizeByteBuffer(ByteBuffer source, int size) {
		long l = ALLOCATOR.realloc(MemoryUtil.memAddress0(source), (long)size);
		if (l == 0L) {
			throw new OutOfMemoryError("Failed to resize buffer from " + source.capacity() + " bytes to " + size + " bytes");
		} else {
			return MemoryUtil.memByteBuffer(l, size);
		}
	}

	public static void free(ByteBuffer buf) {
		ALLOCATOR.free(MemoryUtil.memAddress0(buf));
	}
}
