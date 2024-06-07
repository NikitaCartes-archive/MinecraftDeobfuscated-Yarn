package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.util.Colors;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public class GLX {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static String cpuInfo;

	public static String getOpenGLVersionString() {
		RenderSystem.assertOnRenderThread();
		return GLFW.glfwGetCurrentContext() == 0L
			? "NO CONTEXT"
			: GlStateManager._getString(GL11.GL_RENDERER)
				+ " GL version "
				+ GlStateManager._getString(GL11.GL_VERSION)
				+ ", "
				+ GlStateManager._getString(GL11.GL_VENDOR);
	}

	public static int _getRefreshRate(Window window) {
		RenderSystem.assertOnRenderThread();
		long l = GLFW.glfwGetWindowMonitor(window.getHandle());
		if (l == 0L) {
			l = GLFW.glfwGetPrimaryMonitor();
		}

		GLFWVidMode gLFWVidMode = l == 0L ? null : GLFW.glfwGetVideoMode(l);
		return gLFWVidMode == null ? 0 : gLFWVidMode.refreshRate();
	}

	public static String _getLWJGLVersion() {
		return Version.getVersion();
	}

	public static LongSupplier _initGlfw() {
		Window.acceptError((code, message) -> {
			throw new IllegalStateException(String.format(Locale.ROOT, "GLFW error before init: [0x%X]%s", code, message));
		});
		List<String> list = Lists.<String>newArrayList();
		GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((code, pointer) -> {
			String stringx = pointer == 0L ? "" : MemoryUtil.memUTF8(pointer);
			list.add(String.format(Locale.ROOT, "GLFW error during init: [0x%X]%s", code, stringx));
		});
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
		GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback(callback);
		if (gLFWErrorCallback != null) {
			gLFWErrorCallback.free();
		}
	}

	public static boolean _shouldClose(Window window) {
		return GLFW.glfwWindowShouldClose(window.getHandle());
	}

	public static void _init(int debugVerbosity, boolean debugSync) {
		try {
			CentralProcessor centralProcessor = new SystemInfo().getHardware().getProcessor();
			cpuInfo = String.format(Locale.ROOT, "%dx %s", centralProcessor.getLogicalProcessorCount(), centralProcessor.getProcessorIdentifier().getName())
				.replaceAll("\\s+", " ");
		} catch (Throwable var3) {
		}

		GlDebug.enableDebug(debugVerbosity, debugSync);
	}

	public static String _getCpuInfo() {
		return cpuInfo == null ? "<unknown>" : cpuInfo;
	}

	public static void _renderCrosshair(int size, boolean drawX, boolean drawY, boolean drawZ) {
		if (drawX || drawY || drawZ) {
			RenderSystem.assertOnRenderThread();
			GlStateManager._depthMask(false);
			GlStateManager._disableCull();
			RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
			Tessellator tessellator = RenderSystem.renderThreadTesselator();
			BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
			RenderSystem.lineWidth(4.0F);
			if (drawX) {
				bufferBuilder.vertex(0.0F, 0.0F, 0.0F).color(Colors.BLACK).normal(1.0F, 0.0F, 0.0F);
				bufferBuilder.vertex((float)size, 0.0F, 0.0F).color(Colors.BLACK).normal(1.0F, 0.0F, 0.0F);
			}

			if (drawY) {
				bufferBuilder.vertex(0.0F, 0.0F, 0.0F).color(Colors.BLACK).normal(0.0F, 1.0F, 0.0F);
				bufferBuilder.vertex(0.0F, (float)size, 0.0F).color(Colors.BLACK).normal(0.0F, 1.0F, 0.0F);
			}

			if (drawZ) {
				bufferBuilder.vertex(0.0F, 0.0F, 0.0F).color(Colors.BLACK).normal(0.0F, 0.0F, 1.0F);
				bufferBuilder.vertex(0.0F, 0.0F, (float)size).color(Colors.BLACK).normal(0.0F, 0.0F, 1.0F);
			}

			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			RenderSystem.lineWidth(2.0F);
			bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
			if (drawX) {
				bufferBuilder.vertex(0.0F, 0.0F, 0.0F).color(Colors.RED).normal(1.0F, 0.0F, 0.0F);
				bufferBuilder.vertex((float)size, 0.0F, 0.0F).color(Colors.RED).normal(1.0F, 0.0F, 0.0F);
			}

			if (drawY) {
				bufferBuilder.vertex(0.0F, 0.0F, 0.0F).color(Colors.GREEN).normal(0.0F, 1.0F, 0.0F);
				bufferBuilder.vertex(0.0F, (float)size, 0.0F).color(Colors.GREEN).normal(0.0F, 1.0F, 0.0F);
			}

			if (drawZ) {
				bufferBuilder.vertex(0.0F, 0.0F, 0.0F).color(-8421377).normal(0.0F, 0.0F, 1.0F);
				bufferBuilder.vertex(0.0F, 0.0F, (float)size).color(-8421377).normal(0.0F, 0.0F, 1.0F);
			}

			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			RenderSystem.lineWidth(1.0F);
			GlStateManager._enableCull();
			GlStateManager._depthMask(true);
		}
	}

	public static <T> T make(Supplier<T> factory) {
		return (T)factory.get();
	}

	public static <T> T make(T object, Consumer<T> initializer) {
		initializer.accept(object);
		return object;
	}
}
