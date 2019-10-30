/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.VideoMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public final class Window
implements AutoCloseable {
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
    private boolean field_5177;
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

    public Window(WindowEventHandler windowEventHandler, MonitorTracker monitorTracker, WindowSettings windowSettings, @Nullable String string, String string2) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        this.monitorTracker = monitorTracker;
        this.throwOnGlError();
        this.setPhase("Pre startup");
        this.eventHandler = windowEventHandler;
        Optional<VideoMode> optional = VideoMode.fromString(string);
        this.videoMode = optional.isPresent() ? optional : (windowSettings.fullscreenWidth.isPresent() && windowSettings.fullscreenHeight.isPresent() ? Optional.of(new VideoMode(windowSettings.fullscreenWidth.getAsInt(), windowSettings.fullscreenHeight.getAsInt(), 8, 8, 8, 60)) : Optional.empty());
        this.field_5177 = this.fullscreen = windowSettings.fullscreen;
        Monitor monitor = monitorTracker.getMonitor(GLFW.glfwGetPrimaryMonitor());
        this.width = windowSettings.width > 0 ? windowSettings.width : 1;
        this.windowedWidth = this.width;
        this.height = windowSettings.height > 0 ? windowSettings.height : 1;
        this.windowedHeight = this.height;
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(139265, 196609);
        GLFW.glfwWindowHint(139275, 221185);
        GLFW.glfwWindowHint(139266, 2);
        GLFW.glfwWindowHint(139267, 0);
        GLFW.glfwWindowHint(139272, 0);
        this.handle = GLFW.glfwCreateWindow(this.width, this.height, string2, this.fullscreen && monitor != null ? monitor.getHandle() : 0L, 0L);
        if (monitor != null) {
            VideoMode videoMode = monitor.findClosestVideoMode(this.fullscreen ? this.videoMode : Optional.empty());
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
        this.method_4479();
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

    public static void acceptError(BiConsumer<Integer, String> biConsumer) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
            int i = GLFW.glfwGetError(pointerBuffer);
            if (i != 0) {
                long l = pointerBuffer.get();
                String string = l == 0L ? "" : MemoryUtil.memUTF8(l);
                biConsumer.accept(i, string);
            }
        }
    }

    public void setIcon(InputStream inputStream, InputStream inputStream2) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            if (inputStream == null) {
                throw new FileNotFoundException("icons/icon_16x16.png");
            }
            if (inputStream2 == null) {
                throw new FileNotFoundException("icons/icon_32x32.png");
            }
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            GLFWImage.Buffer buffer = GLFWImage.mallocStack(2, memoryStack);
            ByteBuffer byteBuffer = this.readImage(inputStream, intBuffer, intBuffer2, intBuffer3);
            if (byteBuffer == null) {
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            buffer.position(0);
            buffer.width(intBuffer.get(0));
            buffer.height(intBuffer2.get(0));
            buffer.pixels(byteBuffer);
            ByteBuffer byteBuffer2 = this.readImage(inputStream2, intBuffer, intBuffer2, intBuffer3);
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
        } catch (IOException iOException) {
            LOGGER.error("Couldn't set icon", (Throwable)iOException);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private ByteBuffer readImage(InputStream inputStream, IntBuffer intBuffer, IntBuffer intBuffer2, IntBuffer intBuffer3) throws IOException {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readResource(inputStream);
            byteBuffer.rewind();
            ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, 0);
            return byteBuffer2;
        } finally {
            if (byteBuffer != null) {
                MemoryUtil.memFree(byteBuffer);
            }
        }
    }

    public void setPhase(String string) {
        this.phase = string;
    }

    private void throwOnGlError() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLFW.glfwSetErrorCallback(Window::throwGlError);
    }

    private static void throwGlError(int i, long l) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        throw new IllegalStateException("GLFW error " + i + ": " + MemoryUtil.memUTF8(l));
    }

    public void logGlError(int i, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        String string = MemoryUtil.memUTF8(l);
        LOGGER.error("########## GL ERROR ##########");
        LOGGER.error("@ {}", (Object)this.phase);
        LOGGER.error("{}: {}", (Object)i, (Object)string);
    }

    public void logOnGlError() {
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback(this.errorCallback);
        if (gLFWErrorCallback != null) {
            gLFWErrorCallback.free();
        }
    }

    public void setVsync(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.vsync = bl;
        GLFW.glfwSwapInterval(bl ? 1 : 0);
    }

    @Override
    public void close() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Callbacks.glfwFreeCallbacks(this.handle);
        this.errorCallback.close();
        GLFW.glfwDestroyWindow(this.handle);
        GLFW.glfwTerminate();
    }

    private void onWindowPosChanged(long l, int i, int j) {
        this.x = i;
        this.y = j;
    }

    private void onFramebufferSizeChanged(long l, int i, int j) {
        if (l != this.handle) {
            return;
        }
        int k = this.getFramebufferWidth();
        int m = this.getFramebufferHeight();
        if (i == 0 || j == 0) {
            return;
        }
        this.framebufferWidth = i;
        this.framebufferHeight = j;
        if (this.getFramebufferWidth() != k || this.getFramebufferHeight() != m) {
            this.eventHandler.onResolutionChanged();
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

    private void onWindowSizeChanged(long l, int i, int j) {
        this.width = i;
        this.height = j;
    }

    private void onWindowFocusChanged(long l, boolean bl) {
        if (l == this.handle) {
            this.eventHandler.onWindowFocusChanged(bl);
        }
    }

    public void setFramerateLimit(int i) {
        this.framerateLimit = i;
    }

    public int getFramerateLimit() {
        return this.framerateLimit;
    }

    public void setFullscreen() {
        RenderSystem.flipFrame(this.handle);
        if (this.fullscreen != this.field_5177) {
            this.field_5177 = this.fullscreen;
            this.method_4485(this.vsync);
        }
    }

    public Optional<VideoMode> getVideoMode() {
        return this.videoMode;
    }

    public void setVideoMode(Optional<VideoMode> optional) {
        boolean bl = !optional.equals(this.videoMode);
        this.videoMode = optional;
        if (bl) {
            this.videoModeDirty = true;
        }
    }

    public void method_4475() {
        if (this.fullscreen && this.videoModeDirty) {
            this.videoModeDirty = false;
            this.method_4479();
            this.eventHandler.onResolutionChanged();
        }
    }

    private void method_4479() {
        boolean bl;
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        boolean bl2 = bl = GLFW.glfwGetWindowMonitor(this.handle) != 0L;
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

    private void method_4485(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        try {
            this.method_4479();
            this.eventHandler.onResolutionChanged();
            this.setVsync(bl);
            this.setFullscreen();
        } catch (Exception exception) {
            LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception);
        }
    }

    public int calculateScaleFactor(int i, boolean bl) {
        int j;
        for (j = 1; j != i && j < this.framebufferWidth && j < this.framebufferHeight && this.framebufferWidth / (j + 1) >= 320 && this.framebufferHeight / (j + 1) >= 240; ++j) {
        }
        if (bl && j % 2 != 0) {
            ++j;
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

    public void setRawMouseMotion(boolean bl) {
        InputUtil.setRawMouseMotionMode(this.handle, bl);
    }
}

