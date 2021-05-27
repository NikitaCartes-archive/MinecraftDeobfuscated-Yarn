package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public class GLX {
	private static final Logger LOGGER = LogManager.getLogger();
	private static String cpuInfo;

	public static String getOpenGLVersionString() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GLFW.glfwGetCurrentContext() == 0L
			? "NO CONTEXT"
			: GlStateManager._getString(7937) + " GL version " + GlStateManager._getString(7938) + ", " + GlStateManager._getString(7936);
	}

	public static int _getRefreshRate(Window window) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		long l = GLFW.glfwGetWindowMonitor(window.getHandle());
		if (l == 0L) {
			l = GLFW.glfwGetPrimaryMonitor();
		}

		GLFWVidMode gLFWVidMode = l == 0L ? null : GLFW.glfwGetVideoMode(l);
		return gLFWVidMode == null ? 0 : gLFWVidMode.refreshRate();
	}

	public static String _getLWJGLVersion() {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		return Version.getVersion();
	}

	public static LongSupplier _initGlfw() {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		Window.acceptError((integer, stringx) -> {
			throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", integer, stringx));
		});
		List<String> list = Lists.<String>newArrayList();
		GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((i, l) -> list.add(String.format("GLFW error during init: [0x%X]%s", i, l)));
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(list));
		} else {
			LongSupplier longSupplier = () -> (long)(GLFW.glfwGetTime() * 1.0E9);

			for (String string : list) {
				LOGGER.error("GLFW error collected during initialization: {}", string);
			}

			RenderSystem.setErrorCallback(gLFWErrorCallback);
			return longSupplier;
		}
	}

	public static void _setGlfwErrorCallback(GLFWErrorCallbackI callback) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback(callback);
		if (gLFWErrorCallback != null) {
			gLFWErrorCallback.free();
		}
	}

	public static boolean _shouldClose(Window window) {
		return GLFW.glfwWindowShouldClose(window.getHandle());
	}

	public static void _init(int debugVerbosity, boolean debugSync) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);

		try {
			CentralProcessor centralProcessor = new SystemInfo().getHardware().getProcessor();
			cpuInfo = String.format("%dx %s", centralProcessor.getLogicalProcessorCount(), centralProcessor.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
		} catch (Throwable var3) {
		}

		GlDebug.enableDebug(debugVerbosity, debugSync);
	}

	public static String _getCpuInfo() {
		return cpuInfo == null ? "<unknown>" : cpuInfo;
	}

	public static void _renderCrosshair(int size, boolean drawX, boolean drawY, boolean drawZ) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlStateManager._disableTexture();
		GlStateManager._depthMask(false);
		GlStateManager._disableCull();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.lineWidth(4.0F);
		bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		if (drawX) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).normal(1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex((double)size, 0.0, 0.0).color(0, 0, 0, 255).normal(1.0F, 0.0F, 0.0F).next();
		}

		if (drawY) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.0, (double)size, 0.0).color(0, 0, 0, 255).normal(0.0F, 1.0F, 0.0F).next();
		}

		if (drawZ) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).normal(0.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(0.0, 0.0, (double)size).color(0, 0, 0, 255).normal(0.0F, 0.0F, 1.0F).next();
		}

		tessellator.draw();
		RenderSystem.lineWidth(2.0F);
		bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		if (drawX) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(255, 0, 0, 255).normal(1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex((double)size, 0.0, 0.0).color(255, 0, 0, 255).normal(1.0F, 0.0F, 0.0F).next();
		}

		if (drawY) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 255, 0, 255).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.0, (double)size, 0.0).color(0, 255, 0, 255).normal(0.0F, 1.0F, 0.0F).next();
		}

		if (drawZ) {
			bufferBuilder.vertex(0.0, 0.0, 0.0).color(127, 127, 255, 255).normal(0.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(0.0, 0.0, (double)size).color(127, 127, 255, 255).normal(0.0F, 0.0F, 1.0F).next();
		}

		tessellator.draw();
		RenderSystem.lineWidth(1.0F);
		GlStateManager._enableCull();
		GlStateManager._depthMask(true);
		GlStateManager._enableTexture();
	}

	public static <T> T make(Supplier<T> factory) {
		return (T)factory.get();
	}

	public static <T> T make(T object, Consumer<T> initializer) {
		initializer.accept(object);
		return object;
	}
}
