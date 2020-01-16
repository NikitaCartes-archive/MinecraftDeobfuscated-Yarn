package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.texture.TextureUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@Environment(EnvType.CLIENT)
public final class Window implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final GLFWErrorCallback errorCallback = GLFWErrorCallback.create(this::logGlError);
	private final WindowEventHandler eventHandler;
	private final MonitorTracker monitorTracker;
	private final long handle;
	private int windowedX;
	private int windowedY;
	private int windowedWidth;
	private int windowedHeight;
	private Optional<VideoMode> videoMode;
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
	private boolean videoModeDirty;
	private int framerateLimit;
	private boolean vsync;

	public Window(WindowEventHandler eventHandler, MonitorTracker monitorTracker, WindowSettings settings, @Nullable String videoMode, String title) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		this.monitorTracker = monitorTracker;
		this.throwOnGlError();
		this.setPhase("Pre startup");
		this.eventHandler = eventHandler;
		Optional<VideoMode> optional = VideoMode.fromString(videoMode);
		if (optional.isPresent()) {
			this.videoMode = optional;
		} else if (settings.fullscreenWidth.isPresent() && settings.fullscreenHeight.isPresent()) {
			this.videoMode = Optional.of(new VideoMode(settings.fullscreenWidth.getAsInt(), settings.fullscreenHeight.getAsInt(), 8, 8, 8, 60));
		} else {
			this.videoMode = Optional.empty();
		}

		this.currentFullscreen = this.fullscreen = settings.fullscreen;
		Monitor monitor = monitorTracker.getMonitor(GLFW.glfwGetPrimaryMonitor());
		this.windowedWidth = this.width = settings.width > 0 ? settings.width : 1;
		this.windowedHeight = this.height = settings.height > 0 ? settings.height : 1;
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(139265, 196609);
		GLFW.glfwWindowHint(139275, 221185);
		GLFW.glfwWindowHint(139266, 2);
		GLFW.glfwWindowHint(139267, 0);
		GLFW.glfwWindowHint(139272, 0);
		this.handle = GLFW.glfwCreateWindow(this.width, this.height, title, this.fullscreen && monitor != null ? monitor.getHandle() : 0L, 0L);
		if (monitor != null) {
			VideoMode videoMode2 = monitor.findClosestVideoMode(this.fullscreen ? this.videoMode : Optional.empty());
			this.windowedX = this.x = monitor.getViewportX() + videoMode2.getWidth() / 2 - this.width / 2;
			this.windowedY = this.y = monitor.getViewportY() + videoMode2.getHeight() / 2 - this.height / 2;
		} else {
			int[] is = new int[1];
			int[] js = new int[1];
			GLFW.glfwGetWindowPos(this.handle, is, js);
			this.windowedX = this.x = is[0];
			this.windowedY = this.y = js[0];
		}

		GLFW.glfwMakeContextCurrent(this.handle);
		GL.createCapabilities();
		this.updateWindowRegion();
		this.updateFramebufferSize();
		GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSizeChanged);
		GLFW.glfwSetWindowPosCallback(this.handle, this::onWindowPosChanged);
		GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeChanged);
		GLFW.glfwSetWindowFocusCallback(this.handle, this::onWindowFocusChanged);
	}

	public int getRefreshRate() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GLX._getRefreshRate(this);
	}

	public boolean shouldClose() {
		return GLX._shouldClose(this);
	}

	public static void acceptError(BiConsumer<Integer, String> consumer) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);

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

	public void setIcon(InputStream icon16, InputStream icon32) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			if (icon16 == null) {
				throw new FileNotFoundException("icons/icon_16x16.png");
			}

			if (icon32 == null) {
				throw new FileNotFoundException("icons/icon_32x32.png");
			}

			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);
			IntBuffer intBuffer3 = memoryStack.mallocInt(1);
			Buffer buffer = GLFWImage.mallocStack(2, memoryStack);
			ByteBuffer byteBuffer = this.readImage(icon16, intBuffer, intBuffer2, intBuffer3);
			if (byteBuffer == null) {
				throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
			}

			buffer.position(0);
			buffer.width(intBuffer.get(0));
			buffer.height(intBuffer2.get(0));
			buffer.pixels(byteBuffer);
			ByteBuffer byteBuffer2 = this.readImage(icon32, intBuffer, intBuffer2, intBuffer3);
			if (byteBuffer2 == null) {
				throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
			}

			buffer.position(1);
			buffer.width(intBuffer.get(0));
			buffer.height(intBuffer2.get(0));
			buffer.pixels(byteBuffer2);
			buffer.position(0);
			GLFW.glfwSetWindowIcon(this.handle, buffer);
			STBImage.stbi_image_free(byteBuffer);
			STBImage.stbi_image_free(byteBuffer2);
		} catch (IOException var21) {
			LOGGER.error("Couldn't set icon", (Throwable)var21);
		}
	}

	@Nullable
	private ByteBuffer readImage(InputStream in, IntBuffer x, IntBuffer y, IntBuffer channels) throws IOException {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		ByteBuffer byteBuffer = null;

		ByteBuffer var6;
		try {
			byteBuffer = TextureUtil.readResource(in);
			byteBuffer.rewind();
			var6 = STBImage.stbi_load_from_memory(byteBuffer, x, y, channels, 0);
		} finally {
			if (byteBuffer != null) {
				MemoryUtil.memFree(byteBuffer);
			}
		}

		return var6;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	private void throwOnGlError() {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		GLFW.glfwSetErrorCallback(Window::throwGlError);
	}

	private static void throwGlError(int error, long description) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		String string = "GLFW error " + error + ": " + MemoryUtil.memUTF8(description);
		TinyFileDialogs.tinyfd_messageBox(
			"Minecraft", string + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).", "ok", "error", false
		);
		throw new Window.GlErroredException(string);
	}

	public void logGlError(int error, long description) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
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
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.vsync = vsync;
		GLFW.glfwSwapInterval(vsync ? 1 : 0);
	}

	public void close() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
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
					this.eventHandler.onResolutionChanged();
				}
			}
		}
	}

	private void updateFramebufferSize() {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		int[] is = new int[1];
		int[] js = new int[1];
		GLFW.glfwGetFramebufferSize(this.handle, is, js);
		this.framebufferWidth = is[0];
		this.framebufferHeight = js[0];
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

	public void setFramerateLimit(int framerateLimit) {
		this.framerateLimit = framerateLimit;
	}

	public int getFramerateLimit() {
		return this.framerateLimit;
	}

	public void swapBuffers() {
		RenderSystem.flipFrame(this.handle);
		if (this.fullscreen != this.currentFullscreen) {
			this.currentFullscreen = this.fullscreen;
			this.updateFullscreen(this.vsync);
		}
	}

	public Optional<VideoMode> getVideoMode() {
		return this.videoMode;
	}

	public void setVideoMode(Optional<VideoMode> videoMode) {
		boolean bl = !videoMode.equals(this.videoMode);
		this.videoMode = videoMode;
		if (bl) {
			this.videoModeDirty = true;
		}
	}

	public void applyVideoMode() {
		if (this.fullscreen && this.videoModeDirty) {
			this.videoModeDirty = false;
			this.updateWindowRegion();
			this.eventHandler.onResolutionChanged();
		}
	}

	private void updateWindowRegion() {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		boolean bl = GLFW.glfwGetWindowMonitor(this.handle) != 0L;
		if (this.fullscreen) {
			Monitor monitor = this.monitorTracker.getMonitor(this);
			if (monitor == null) {
				LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
				this.fullscreen = false;
			} else {
				VideoMode videoMode = monitor.findClosestVideoMode(this.videoMode);
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

	private void updateFullscreen(boolean vsync) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);

		try {
			this.updateWindowRegion();
			this.eventHandler.onResolutionChanged();
			this.setVsync(vsync);
			this.swapBuffers();
		} catch (Exception var3) {
			LOGGER.error("Couldn't toggle fullscreen", (Throwable)var3);
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

	public void setTitle(String string) {
		GLFW.glfwSetWindowTitle(this.handle, string);
	}

	public long getHandle() {
		return this.handle;
	}

	public boolean isFullscreen() {
		return this.fullscreen;
	}

	public int getFramebufferWidth() {
		return this.framebufferWidth;
	}

	public int getFramebufferHeight() {
		return this.framebufferHeight;
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

	@Environment(EnvType.CLIENT)
	public static class GlErroredException extends GlException {
		private GlErroredException(String string) {
			super(string);
		}
	}
}
