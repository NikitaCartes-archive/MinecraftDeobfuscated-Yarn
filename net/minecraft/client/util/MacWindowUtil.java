/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFWNativeCocoa;

@Environment(value=EnvType.CLIENT)
public class MacWindowUtil {
    private static final int FULLSCREEN_MASK = 16384;

    public static void toggleFullscreen(long handle) {
        MacWindowUtil.getCocoaWindow(handle).filter(MacWindowUtil::isFullscreen).ifPresent(MacWindowUtil::toggleFullscreen);
    }

    private static Optional<NSObject> getCocoaWindow(long handle) {
        long l = GLFWNativeCocoa.glfwGetCocoaWindow(handle);
        if (l != 0L) {
            return Optional.of(new NSObject(new Pointer(l)));
        }
        return Optional.empty();
    }

    private static boolean isFullscreen(NSObject handle) {
        return ((Long)handle.sendRaw("styleMask", new Object[0]) & 0x4000L) == 16384L;
    }

    private static void toggleFullscreen(NSObject handle) {
        handle.send("toggleFullScreen:", new Object[0]);
    }

    public static void setApplicationIconImage(InputStream stream) throws IOException {
        String string = Base64.getEncoder().encodeToString(stream.readAllBytes());
        Client client = Client.getInstance();
        Object object = client.sendProxy("NSData", "alloc", new Object[0]).send("initWithBase64Encoding:", new Object[]{string});
        Object object2 = client.sendProxy("NSImage", "alloc", new Object[0]).send("initWithData:", new Object[]{object});
        client.sendProxy("NSApplication", "sharedApplication", new Object[0]).send("setApplicationIconImage:", new Object[]{object2});
    }
}

