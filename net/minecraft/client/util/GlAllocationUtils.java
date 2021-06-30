/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class GlAllocationUtils {
    private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);

    public static ByteBuffer allocateByteBuffer(int size) {
        long l = ALLOCATOR.malloc(size);
        if (l == 0L) {
            throw new OutOfMemoryError("Failed to allocate " + size + " bytes");
        }
        return MemoryUtil.memByteBuffer(l, size);
    }

    public static ByteBuffer resizeByteBuffer(ByteBuffer source, int size) {
        long l = ALLOCATOR.realloc(MemoryUtil.memAddress0(source), size);
        if (l == 0L) {
            throw new OutOfMemoryError("Failed to resize buffer from " + source.capacity() + " bytes to " + size + " bytes");
        }
        return MemoryUtil.memByteBuffer(l, size);
    }
}

