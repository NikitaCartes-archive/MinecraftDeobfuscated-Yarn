package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public final class Window implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int field_52250 = 320;
	public static final int field_52251 = 240;
	private final GLFWErrorCallback errorCallback = GLFWErrorCallback.create(this::logGlError);
	private final WindowEventHandler eventHandler;
	private final MonitorTracker monitorTracker;
	private final long handle;
	private int windowedX;
	private int windowedY;
	private int windowedWidth;
	private int windowedHeight;
	private Optional<VideoMode> fullscreenVideoMode;
	private boolean fullscreen;
	private boolean currentFullscreen;
	private int x;
	private int y;
	private int width;
	private int height;
	private int framebufferWidth;
	private int framebufferHeight;
	private int scaledWidth;
	private int scaledHeight;
	private double scaleFactor;
	private String phase = "";
	private boolean fullscreenVideoModeDirty;
	private boolean vsync;
	private boolean minimized;

	public Window(WindowEventHandler eventHandler, MonitorTracker monitorTracker, WindowSettings settings, @Nullable String fullscreenVideoMode, String title) {
		this.monitorTracker = monitorTracker;
		this.throwOnGlError();
		this.setPhase("Pre startup");
		this.eventHandler = eventHandler;
		Optional<VideoMode> optional = VideoMode.fromString(fullscreenVideoMode);
		if (optional.isPresent()) {
			this.fullscreenVideoMode = optional;
		} else if (settings.fullscreenWidth.isPresent() && settings.fullscreenHeight.isPresent()) {
			this.fullscreenVideoMode = Optional.of(new VideoMode(settings.fullscreenWidth.getAsInt(), settings.fullscreenHeight.getAsInt(), 8, 8, 8, 60));
		} else {
			this.fullscreenVideoMode = Optional.empty();
		}

		this.currentFullscreen = this.fullscreen = settings.fullscreen;
		Monitor monitor = monitorTracker.getMonitor(GLFW.glfwGetPrimaryMonitor());
		this.windowedWidth = this.width = settings.width > 0 ? settings.width : 1;
		this.windowedHeight = this.height = settings.height > 0 ? settings.height : 1;
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_CREATION_API, GLFW.GLFW_NATIVE_CONTEXT_API);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, 1);
		this.handle = GLFW.glfwCreateWindow(this.width, this.height, title, this.fullscreen && monitor != null ? monitor.getHandle() : 0L, 0L);
		if (monitor != null) {
			VideoMode videoMode = monitor.findClosestVideoMode(this.fullscreen ? this.fullscreenVideoMode : Optional.empty());
			this.windowedX = this.x = monitor.getViewportX() + videoMode.getWidth() / 2 - this.width / 2;
			this.windowedY = this.y = monitor.getViewportY() + videoMode.getHeight() / 2 - this.height / 2;
		} else {
			int[] is = new int[1];
			int[] js = new int[1];
			GLFW.glfwGetWindowPos(this.handle, is, js);
			this.windowedX = this.x = is[0];
			this.windowedY = this.y = js[0];
		}

		GLFW.glfwMakeContextCurrent(this.handle);
		GL.createCapabilities();
		int i = RenderSystem.maxSupportedTextureSize();
		GLFW.glfwSetWindowSizeLimits(this.handle, -1, -1, i, i);
		this.updateWindowRegion();
		this.updateFramebufferSize();
		GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSizeChanged);
		GLFW.glfwSetWindowPosCallback(this.handle, this::onWindowPosChanged);
		GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeChanged);
		GLFW.glfwSetWindowFocusCallback(this.handle, this::onWindowFocusChanged);
		GLFW.glfwSetCursorEnterCallback(this.handle, this::onCursorEnterChanged);
		GLFW.glfwSetWindowIconifyCallback(this.handle, this::onMinimizeChanged);
	}

	public static String getGlfwPlatform() {
		int i = GLFW.glfwGetPlatform();

		return switch (i) {
			case 0 -> "<error>";
			case 393217 -> "win32";
			case 393218 -> "cocoa";
			case 393219 -> "wayland";
			case 393220 -> "x11";
			case 393221 -> "null";
			default -> String.format(Locale.ROOT, "unknown (%08X)", i);
		};
	}

	public int getRefreshRate() {
		RenderSystem.assertOnRenderThread();
		return GLX._getRefreshRate(this);
	}

	public boolean shouldClose() {
		return GLX._shouldClose(this);
	}

	public static void acceptError(BiConsumer<Integer, String> consumer) {
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
			int i = GLFW.glfwGetError(pointerBuffer);
			if (i != 0) {
				long l = pointerBuffer.get();
				String string = l == 0L ? "" : MemoryUtil.memUTF8(l);
				consumer.accept(i, string);
			}
		}
	}

	public void setIcon(ResourcePack resourcePack, Icons icons) throws IOException {
		int i = GLFW.glfwGetPlatform();
		switch (i) {
			case 393217:
			case 393220:
				List<InputSupplier<InputStream>> list = icons.getIcons(resourcePack);
				List<ByteBuffer> list2 = new ArrayList(list.size());

				try (MemoryStack memoryStack = MemoryStack.stackPush()) {
					Buffer buffer = GLFWImage.malloc(list.size(), memoryStack);

					for (int j = 0; j < list.size(); j++) {
						try (NativeImage nativeImage = NativeImage.read((InputStream)((InputSupplier)list.get(j)).get())) {
							ByteBuffer byteBuffer = MemoryUtil.memAlloc(nativeImage.getWidth() * nativeImage.getHeight() * 4);
							list2.add(byteBuffer);
							byteBuffer.asIntBuffer().put(nativeImage.copyPixelsAbgr());
							buffer.position(j);
							buffer.width(nativeImage.getWidth());
							buffer.height(nativeImage.getHeight());
							buffer.pixels(byteBuffer);
						}
					}

					GLFW.glfwSetWindowIcon(this.handle, buffer.position(0));
					break;
				} finally {
					list2.forEach(MemoryUtil::memFree);
				}
			case 393218:
				MacWindowUtil.setApplicationIconImage(icons.getMacIcon(resourcePack));
			case 393219:
			case 393221:
				break;
			default:
				LOGGER.warn("Not setting icon for unrecognized platform: {}", i);
		}
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	private void throwOnGlError() {
		GLFW.glfwSetErrorCallback(Window::throwGlError);
	}

	private static void throwGlError(int error, long description) {
		String string = "GLFW error " + error + ": " + MemoryUtil.memUTF8(description);
		TinyFileDialogs.tinyfd_messageBox(
			"Minecraft", string + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).", "ok", "error", false
		);
		throw new Window.GlErroredException(string);
	}

	public void logGlError(int error, long description) {
		RenderSystem.assertOnRenderThread();
		String string = MemoryUtil.memUTF8(description);
		LOGGER.error("########## GL ERROR ##########");
		LOGGER.error("@ {}", this.phase);
		LOGGER.error("{}: {}", error, string);
	}

	public void logOnGlError() {
		GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback(this.errorCallback);
		if (gLFWErrorCallback != null) {
			gLFWErrorCallback.free();
		}
	}

	public void setVsync(boolean vsync) {
		RenderSystem.assertOnRenderThreadOrInit();
		this.vsync = vsync;
		GLFW.glfwSwapInterval(vsync ? 1 : 0);
	}

	public void close() {
		RenderSystem.assertOnRenderThread();
		Callbacks.glfwFreeCallbacks(this.handle);
		this.errorCallback.close();
		GLFW.glfwDestroyWindow(this.handle);
		GLFW.glfwTerminate();
	}

	private void onWindowPosChanged(long window, int x, int y) {
		this.x = x;
		this.y = y;
	}

	private void onFramebufferSizeChanged(long window, int width, int height) {
		if (window == this.handle) {
			int i = this.getFramebufferWidth();
			int j = this.getFramebufferHeight();
			if (width != 0 && height != 0) {
				this.framebufferWidth = width;
				this.framebufferHeight = height;
				if (this.getFramebufferWidth() != i || this.getFramebufferHeight() != j) {
					try {
						this.eventHandler.onResolutionChanged();
					} catch (Exception var10) {
						CrashReport crashReport = CrashReport.create(var10, "Window resize");
						CrashReportSection crashReportSection = crashReport.addElement("Window Dimensions");
						crashReportSection.add("Old", i + "x" + j);
						crashReportSection.add("New", width + "x" + height);
						throw new CrashException(crashReport);
					}
				}
			}
		}
	}

	private void updateFramebufferSize() {
		int[] is = new int[1];
		int[] js = new int[1];
		GLFW.glfwGetFramebufferSize(this.handle, is, js);
		this.framebufferWidth = is[0] > 0 ? is[0] : 1;
		this.framebufferHeight = js[0] > 0 ? js[0] : 1;
	}

	private void onWindowSizeChanged(long window, int width, int height) {
		this.width = width;
		this.height = height;
	}

	private void onWindowFocusChanged(long window, boolean focused) {
		if (window == this.handle) {
			this.eventHandler.onWindowFocusChanged(focused);
		}
	}

	private void onCursorEnterChanged(long window, boolean entered) {
		if (entered) {
			this.eventHandler.onCursorEnterChanged();
		}
	}

	private void onMinimizeChanged(long window, boolean minimized) {
		this.minimized = minimized;
	}

	public void swapBuffers(@Nullable TracyFrameCapturer capturer) {
		RenderSystem.flipFrame(this.handle, capturer);
		if (this.fullscreen != this.currentFullscreen) {
			this.currentFullscreen = this.fullscreen;
			this.updateFullscreen(this.vsync, capturer);
		}
	}

	public Optional<VideoMode> getFullscreenVideoMode() {
		return this.fullscreenVideoMode;
	}

	public void setFullscreenVideoMode(Optional<VideoMode> fullscreenVideoMode) {
		boolean bl = !fullscreenVideoMode.equals(this.fullscreenVideoMode);
		this.fullscreenVideoMode = fullscreenVideoMode;
		if (bl) {
			this.fullscreenVideoModeDirty = true;
		}
	}

	public void applyFullscreenVideoMode() {
		if (this.fullscreen && this.fullscreenVideoModeDirty) {
			this.fullscreenVideoModeDirty = false;
			this.updateWindowRegion();
			this.eventHandler.onResolutionChanged();
		}
	}

	private void updateWindowRegion() {
		boolean bl = GLFW.glfwGetWindowMonitor(this.handle) != 0L;
		if (this.fullscreen) {
			Monitor monitor = this.monitorTracker.getMonitor(this);
			if (monitor == null) {
				LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
				this.fullscreen = false;
			} else {
				if (MacWindowUtil.field_52734) {
					MacWindowUtil.toggleFullscreen(this.handle);
				}

				VideoMode videoMode = monitor.findClosestVideoMode(this.fullscreenVideoMode);
				if (!bl) {
					this.windowedX = this.x;
					this.windowedY = this.y;
					this.windowedWidth = this.width;
					this.windowedHeight = this.height;
				}

				this.x = 0;
				this.y = 0;
				this.width = videoMode.getWidth();
				this.height = videoMode.getHeight();
				GLFW.glfwSetWindowMonitor(this.handle, monitor.getHandle(), this.x, this.y, this.width, this.height, videoMode.getRefreshRate());
				if (MacWindowUtil.field_52734) {
					MacWindowUtil.fixStyleMask(this.handle);
				}
			}
		} else {
			this.x = this.windowedX;
			this.y = this.windowedY;
			this.width = this.windowedWidth;
			this.height = this.windowedHeight;
			GLFW.glfwSetWindowMonitor(this.handle, 0L, this.x, this.y, this.width, this.height, -1);
		}
	}

	public void toggleFullscreen() {
		this.fullscreen = !this.fullscreen;
	}

	public void setWindowedSize(int width, int height) {
		this.windowedWidth = width;
		this.windowedHeight = height;
		this.fullscreen = false;
		this.updateWindowRegion();
	}

	private void updateFullscreen(boolean vsync, @Nullable TracyFrameCapturer capturer) {
		RenderSystem.assertOnRenderThread();

		try {
			this.updateWindowRegion();
			this.eventHandler.onResolutionChanged();
			this.setVsync(vsync);
			this.swapBuffers(capturer);
		} catch (Exception var4) {
			LOGGER.error("Couldn't toggle fullscreen", (Throwable)var4);
		}
	}

	public int calculateScaleFactor(int guiScale, boolean forceUnicodeFont) {
		int i = 1;

		while (
			i != guiScale
				&& i < this.framebufferWidth
				&& i < this.framebufferHeight
				&& this.framebufferWidth / (i + 1) >= 320
				&& this.framebufferHeight / (i + 1) >= 240
		) {
			i++;
		}

		if (forceUnicodeFont && i % 2 != 0) {
			i++;
		}

		return i;
	}

	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
		int i = (int)((double)this.framebufferWidth / scaleFactor);
		this.scaledWidth = (double)this.framebufferWidth / scaleFactor > (double)i ? i + 1 : i;
		int j = (int)((double)this.framebufferHeight / scaleFactor);
		this.scaledHeight = (double)this.framebufferHeight / scaleFactor > (double)j ? j + 1 : j;
	}

	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(this.handle, title);
	}

	public long getHandle() {
		return this.handle;
	}

	public boolean isFullscreen() {
		return this.fullscreen;
	}

	public boolean isMinimized() {
		return this.minimized;
	}

	public int getFramebufferWidth() {
		return this.framebufferWidth;
	}

	public int getFramebufferHeight() {
		return this.framebufferHeight;
	}

	public void setFramebufferWidth(int framebufferWidth) {
		this.framebufferWidth = framebufferWidth;
	}

	public void setFramebufferHeight(int framebufferHeight) {
		this.framebufferHeight = framebufferHeight;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getScaledWidth() {
		return this.scaledWidth;
	}

	public int getScaledHeight() {
		return this.scaledHeight;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public double getScaleFactor() {
		return this.scaleFactor;
	}

	@Nullable
	public Monitor getMonitor() {
		return this.monitorTracker.getMonitor(this);
	}

	public void setRawMouseMotion(boolean rawMouseMotion) {
		InputUtil.setRawMouseMotionMode(this.handle, rawMouseMotion);
	}

	public void setCloseCallback(Runnable callback) {
		GLFWWindowCloseCallback gLFWWindowCloseCallback = GLFW.glfwSetWindowCloseCallback(this.handle, l -> callback.run());
		if (gLFWWindowCloseCallback != null) {
			gLFWWindowCloseCallback.free();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class GlErroredException extends GlException {
		GlErroredException(String string) {
			super(string);
		}
	}
}
