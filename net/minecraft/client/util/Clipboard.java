/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.google.common.base.Charsets;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class Clipboard {
    private final ByteBuffer clipboardBuffer = BufferUtils.createByteBuffer(8192);

    public String getClipboard(long window, GLFWErrorCallbackI gLFWErrorCallbackI) {
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback(gLFWErrorCallbackI);
        String string = GLFW.glfwGetClipboardString(window);
        string = string != null ? SharedConstants.stripSupplementaryChars(string) : "";
        GLFWErrorCallback gLFWErrorCallback2 = GLFW.glfwSetErrorCallback(gLFWErrorCallback);
        if (gLFWErrorCallback2 != null) {
            gLFWErrorCallback2.free();
        }
        return string;
    }

    private static void setClipboard(long l, ByteBuffer byteBuffer, byte[] bs) {
        byteBuffer.clear();
        byteBuffer.put(bs);
        byteBuffer.put((byte)0);
        byteBuffer.flip();
        GLFW.glfwSetClipboardString(l, byteBuffer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setClipboard(long window, String string) {
        byte[] bs = string.getBytes(Charsets.UTF_8);
        int i = bs.length + 1;
        if (i < this.clipboardBuffer.capacity()) {
            Clipboard.setClipboard(window, this.clipboardBuffer, bs);
        } else {
            ByteBuffer byteBuffer = MemoryUtil.memAlloc(i);
            try {
                Clipboard.setClipboard(window, byteBuffer, bs);
            } finally {
                MemoryUtil.memFree(byteBuffer);
            }
        }
    }
}

