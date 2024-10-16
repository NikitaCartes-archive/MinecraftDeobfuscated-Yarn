package net.minecraft.client.util;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.InputSupplier;
import org.lwjgl.glfw.GLFWNativeCocoa;

@Environment(EnvType.CLIENT)
public class MacWindowUtil {
	public static final boolean field_52734 = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");
	private static final int field_46537 = 8;
	private static final int FULLSCREEN_MASK = 16384;

	public static void toggleFullscreen(long handle) {
		getCocoaWindow(handle).filter(MacWindowUtil::isFullscreen).ifPresent(MacWindowUtil::toggleFullscreen);
	}

	public static void fixStyleMask(long handle) {
		getCocoaWindow(handle).ifPresent(windowHandle -> {
			long l = getStyleMask(windowHandle);
			windowHandle.send("setStyleMask:", new Object[]{l & -9L});
		});
	}

	private static Optional<NSObject> getCocoaWindow(long handle) {
		long l = GLFWNativeCocoa.glfwGetCocoaWindow(handle);
		return l != 0L ? Optional.of(new NSObject(new Pointer(l))) : Optional.empty();
	}

	private static boolean isFullscreen(NSObject handle) {
		return (getStyleMask(handle) & 16384L) != 0L;
	}

	private static long getStyleMask(NSObject handle) {
		return (Long)handle.sendRaw("styleMask", new Object[0]);
	}

	private static void toggleFullscreen(NSObject handle) {
		handle.send("toggleFullScreen:", new Object[]{Pointer.NULL});
	}

	public static void setApplicationIconImage(InputSupplier<InputStream> iconSupplier) throws IOException {
		InputStream inputStream = iconSupplier.get();

		try {
			String string = Base64.getEncoder().encodeToString(inputStream.readAllBytes());
			Client client = Client.getInstance();
			Object object = client.sendProxy("NSData", "alloc").send("initWithBase64Encoding:", string);
			Object object2 = client.sendProxy("NSImage", "alloc").send("initWithData:", object);
			client.sendProxy("NSApplication", "sharedApplication").send("setApplicationIconImage:", object2);
		} catch (Throwable var7) {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable var6) {
					var7.addSuppressed(var6);
				}
			}

			throw var7;
		}

		if (inputStream != null) {
			inputStream.close();
		}
	}
}
