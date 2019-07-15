package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
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

@Environment(EnvType.CLIENT)
public final class Window implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final GLFWErrorCallback errorCallback = GLFWErrorCallback.create(this::logGlError);
	private final WindowEventHandler windowEventHandler;
	private final MonitorTracker monitorTracker;
	private final long handle;
	private int field_5175;
	private int field_5185;
	private int field_5174;
	private int field_5184;
	private Optional<VideoMode> videoMode;
	private boolean fullscreen;
	private boolean field_5177;
	private int positionX;
	private int positionY;
	private int width;
	private int height;
	private int framebufferWidth;
	private int framebufferHeight;
	private int scaledWidth;
	private int scaledHeight;
	private double scaleFactor;
	private String phase = "";
	private boolean field_5186;
	private double field_5189 = Double.MIN_VALUE;
	private int framerateLimit;
	private boolean field_16517;

	public Window(WindowEventHandler windowEventHandler, MonitorTracker monitorTracker, WindowSettings windowSettings, String string, String string2) {
		this.monitorTracker = monitorTracker;
		this.throwExceptionOnGlError();
		this.setPhase("Pre startup");
		this.windowEventHandler = windowEventHandler;
		Optional<VideoMode> optional = VideoMode.fromString(string);
		if (optional.isPresent()) {
			this.videoMode = optional;
		} else if (windowSettings.fullscreenWidth.isPresent() && windowSettings.fullscreenHeight.isPresent()) {
			this.videoMode = Optional.of(new VideoMode(windowSettings.fullscreenWidth.getAsInt(), windowSettings.fullscreenHeight.getAsInt(), 8, 8, 8, 60));
		} else {
			this.videoMode = Optional.empty();
		}

		this.field_5177 = this.fullscreen = windowSettings.fullscreen;
		Monitor monitor = monitorTracker.getMonitor(GLFW.glfwGetPrimaryMonitor());
		this.field_5174 = this.width = windowSettings.width > 0 ? windowSettings.width : 1;
		this.field_5184 = this.height = windowSettings.height > 0 ? windowSettings.height : 1;
		GLFW.glfwDefaultWindowHints();
		this.handle = GLFW.glfwCreateWindow(this.width, this.height, string2, this.fullscreen && monitor != null ? monitor.getHandle() : 0L, 0L);
		if (monitor != null) {
			VideoMode videoMode = monitor.findClosestVideoMode(this.fullscreen ? this.videoMode : Optional.empty());
			this.field_5175 = this.positionX = monitor.getViewportX() + videoMode.getWidth() / 2 - this.width / 2;
			this.field_5185 = this.positionY = monitor.getViewportY() + videoMode.getHeight() / 2 - this.height / 2;
		} else {
			int[] is = new int[1];
			int[] js = new int[1];
			GLFW.glfwGetWindowPos(this.handle, is, js);
			this.field_5175 = this.positionX = is[0];
			this.field_5185 = this.positionY = js[0];
		}

		GLFW.glfwMakeContextCurrent(this.handle);
		GL.createCapabilities();
		this.method_4479();
		this.method_4483();
		GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSizeChanged);
		GLFW.glfwSetWindowPosCallback(this.handle, this::onWindowPosChanged);
		GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeChanged);
		GLFW.glfwSetWindowFocusCallback(this.handle, this::onWindowFocusChanged);
	}

	public static void method_4492(BiConsumer<Integer, String> biConsumer) {
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
			int i = GLFW.glfwGetError(pointerBuffer);
			if (i != 0) {
				long l = pointerBuffer.get();
				String string = l == 0L ? "" : MemoryUtil.memUTF8(l);
				biConsumer.accept(i, string);
			}
		}
	}

	public void method_4493(boolean bl) {
		GlStateManager.clear(256, bl);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(
			0.0, (double)this.getFramebufferWidth() / this.getScaleFactor(), (double)this.getFramebufferHeight() / this.getScaleFactor(), 0.0, 1000.0, 3000.0
		);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
	}

	public void setIcon(InputStream inputStream, InputStream inputStream2) {
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			if (inputStream == null) {
				throw new FileNotFoundException("icons/icon_16x16.png");
			}

			if (inputStream2 == null) {
				throw new FileNotFoundException("icons/icon_32x32.png");
			}

			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);
			IntBuffer intBuffer3 = memoryStack.mallocInt(1);
			Buffer buffer = GLFWImage.mallocStack(2, memoryStack);
			ByteBuffer byteBuffer = this.method_4510(inputStream, intBuffer, intBuffer2, intBuffer3);
			if (byteBuffer == null) {
				throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
			}

			buffer.position(0);
			buffer.width(intBuffer.get(0));
			buffer.height(intBuffer2.get(0));
			buffer.pixels(byteBuffer);
			ByteBuffer byteBuffer2 = this.method_4510(inputStream2, intBuffer, intBuffer2, intBuffer3);
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
	private ByteBuffer method_4510(InputStream inputStream, IntBuffer intBuffer, IntBuffer intBuffer2, IntBuffer intBuffer3) throws IOException {
		ByteBuffer byteBuffer = null;

		ByteBuffer var6;
		try {
			byteBuffer = TextureUtil.readResource(inputStream);
			byteBuffer.rewind();
			var6 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, 0);
		} finally {
			if (byteBuffer != null) {
				MemoryUtil.memFree(byteBuffer);
			}
		}

		return var6;
	}

	public void setPhase(String string) {
		this.phase = string;
	}

	private void throwExceptionOnGlError() {
		GLFW.glfwSetErrorCallback(Window::throwExceptionForGlError);
	}

	private static void throwExceptionForGlError(int i, long l) {
		throw new IllegalStateException("GLFW error " + i + ": " + MemoryUtil.memUTF8(l));
	}

	public void logGlError(int i, long l) {
		String string = MemoryUtil.memUTF8(l);
		LOGGER.error("########## GL ERROR ##########");
		LOGGER.error("@ {}", this.phase);
		LOGGER.error("{}: {}", i, string);
	}

	public void logOnGlError() {
		GLFW.glfwSetErrorCallback(this.errorCallback).free();
	}

	public void setVsync(boolean bl) {
		this.field_16517 = bl;
		GLFW.glfwSwapInterval(bl ? 1 : 0);
	}

	public void close() {
		Callbacks.glfwFreeCallbacks(this.handle);
		this.errorCallback.close();
		GLFW.glfwDestroyWindow(this.handle);
		GLFW.glfwTerminate();
	}

	private void onWindowPosChanged(long l, int i, int j) {
		this.positionX = i;
		this.positionY = j;
	}

	private void onFramebufferSizeChanged(long l, int i, int j) {
		if (l == this.handle) {
			int k = this.getFramebufferWidth();
			int m = this.getFramebufferHeight();
			if (i != 0 && j != 0) {
				this.framebufferWidth = i;
				this.framebufferHeight = j;
				if (this.getFramebufferWidth() != k || this.getFramebufferHeight() != m) {
					this.windowEventHandler.onResolutionChanged();
				}
			}
		}
	}

	private void method_4483() {
		int[] is = new int[1];
		int[] js = new int[1];
		GLFW.glfwGetFramebufferSize(this.handle, is, js);
		this.framebufferWidth = is[0];
		this.framebufferHeight = js[0];
	}

	private void onWindowSizeChanged(long l, int i, int j) {
		this.width = i;
		this.height = j;
	}

	private void onWindowFocusChanged(long l, boolean bl) {
		if (l == this.handle) {
			this.windowEventHandler.onWindowFocusChanged(bl);
		}
	}

	public void setFramerateLimit(int i) {
		this.framerateLimit = i;
	}

	public int getFramerateLimit() {
		return this.framerateLimit;
	}

	public void setFullscreen(boolean bl) {
		GLFW.glfwSwapBuffers(this.handle);
		pollEvents();
		if (this.fullscreen != this.field_5177) {
			this.field_5177 = this.fullscreen;
			this.method_4485(this.field_16517);
		}
	}

	public void waitForFramerateLimit() {
		double d = this.field_5189 + 1.0 / (double)this.getFramerateLimit();

		double e;
		for (e = GLFW.glfwGetTime(); e < d; e = GLFW.glfwGetTime()) {
			GLFW.glfwWaitEventsTimeout(d - e);
		}

		this.field_5189 = e;
	}

	public Optional<VideoMode> getVideoMode() {
		return this.videoMode;
	}

	public void setVideoMode(Optional<VideoMode> optional) {
		boolean bl = !optional.equals(this.videoMode);
		this.videoMode = optional;
		if (bl) {
			this.field_5186 = true;
		}
	}

	public void method_4475() {
		if (this.fullscreen && this.field_5186) {
			this.field_5186 = false;
			this.method_4479();
			this.windowEventHandler.onResolutionChanged();
		}
	}

	private void method_4479() {
		boolean bl = GLFW.glfwGetWindowMonitor(this.handle) != 0L;
		if (this.fullscreen) {
			Monitor monitor = this.monitorTracker.getMonitor(this);
			if (monitor == null) {
				LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
				this.fullscreen = false;
			} else {
				VideoMode videoMode = monitor.findClosestVideoMode(this.videoMode);
				if (!bl) {
					this.field_5175 = this.positionX;
					this.field_5185 = this.positionY;
					this.field_5174 = this.width;
					this.field_5184 = this.height;
				}

				this.positionX = 0;
				this.positionY = 0;
				this.width = videoMode.getWidth();
				this.height = videoMode.getHeight();
				GLFW.glfwSetWindowMonitor(this.handle, monitor.getHandle(), this.positionX, this.positionY, this.width, this.height, videoMode.getRefreshRate());
			}
		} else {
			this.positionX = this.field_5175;
			this.positionY = this.field_5185;
			this.width = this.field_5174;
			this.height = this.field_5184;
			GLFW.glfwSetWindowMonitor(this.handle, 0L, this.positionX, this.positionY, this.width, this.height, -1);
		}
	}

	public void toggleFullscreen() {
		this.fullscreen = !this.fullscreen;
	}

	private void method_4485(boolean bl) {
		try {
			this.method_4479();
			this.windowEventHandler.onResolutionChanged();
			this.setVsync(bl);
			this.windowEventHandler.updateDisplay(false);
		} catch (Exception var3) {
			LOGGER.error("Couldn't toggle fullscreen", (Throwable)var3);
		}
	}

	public int calculateScaleFactor(int i, boolean bl) {
		int j = 1;

		while (j != i && j < this.framebufferWidth && j < this.framebufferHeight && this.framebufferWidth / (j + 1) >= 320 && this.framebufferHeight / (j + 1) >= 240) {
			j++;
		}

		if (bl && j % 2 != 0) {
			j++;
		}

		return j;
	}

	public void setScaleFactor(double d) {
		this.scaleFactor = d;
		int i = (int)((double)this.framebufferWidth / d);
		this.scaledWidth = (double)this.framebufferWidth / d > (double)i ? i + 1 : i;
		int j = (int)((double)this.framebufferHeight / d);
		this.scaledHeight = (double)this.framebufferHeight / d > (double)j ? j + 1 : j;
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

	public static void pollEvents() {
		GLFW.glfwPollEvents();
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

	public int getPositionY() {
		return this.positionX;
	}

	public int getPositionX() {
		return this.positionY;
	}

	public double getScaleFactor() {
		return this.scaleFactor;
	}

	@Nullable
	public Monitor getMonitor() {
		return this.monitorTracker.getMonitor(this);
	}

	public void method_21668(boolean bl) {
		InputUtil.method_21736(this.handle, bl);
	}
}
