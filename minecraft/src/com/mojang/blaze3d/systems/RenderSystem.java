package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.GraphicsMode;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;

@Environment(EnvType.CLIENT)
public class RenderSystem {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ConcurrentLinkedQueue<RenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
	private static final Tessellator RENDER_THREAD_TESSELATOR = new Tessellator();
	public static final float DEFAULTALPHACUTOFF = 0.1F;
	private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
	private static boolean isReplayingQueue;
	private static Thread gameThread;
	private static Thread renderThread;
	private static int MAX_SUPPORTED_TEXTURE_SIZE = -1;
	private static boolean isInInit;
	private static double lastDrawTime = Double.MIN_VALUE;
	private static final RenderSystem.class_5590 sharedSequential = new RenderSystem.class_5590(1, 1, IntConsumer::accept);
	private static final RenderSystem.class_5590 sharedSequentialQuad = new RenderSystem.class_5590(4, 6, (intConsumer, i) -> {
		intConsumer.accept(i + 0);
		intConsumer.accept(i + 1);
		intConsumer.accept(i + 2);
		intConsumer.accept(i + 2);
		intConsumer.accept(i + 3);
		intConsumer.accept(i + 0);
	});

	public static void initRenderThread() {
		if (renderThread == null && gameThread != Thread.currentThread()) {
			renderThread = Thread.currentThread();
		} else {
			throw new IllegalStateException("Could not initialize render thread");
		}
	}

	public static boolean isOnRenderThread() {
		return Thread.currentThread() == renderThread;
	}

	public static boolean isOnRenderThreadOrInit() {
		return isInInit || isOnRenderThread();
	}

	public static void initGameThread(boolean assertNotRenderThread) {
		boolean bl = renderThread == Thread.currentThread();
		if (gameThread == null && renderThread != null && bl != assertNotRenderThread) {
			gameThread = Thread.currentThread();
		} else {
			throw new IllegalStateException("Could not initialize tick thread");
		}
	}

	public static boolean isOnGameThread() {
		return true;
	}

	public static boolean isOnGameThreadOrInit() {
		return isInInit || isOnGameThread();
	}

	public static void assertThread(Supplier<Boolean> check) {
		if (!(Boolean)check.get()) {
			throw new IllegalStateException("Rendersystem called from wrong thread");
		}
	}

	public static boolean isInInitPhase() {
		return true;
	}

	public static void recordRenderCall(RenderCall renderCall) {
		recordingQueue.add(renderCall);
	}

	public static void flipFrame(long window) {
		GLFW.glfwPollEvents();
		replayQueue();
		Tessellator.getInstance().getBuffer().clear();
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
	}

	public static void replayQueue() {
		isReplayingQueue = true;

		while (!recordingQueue.isEmpty()) {
			RenderCall renderCall = (RenderCall)recordingQueue.poll();
			renderCall.execute();
		}

		isReplayingQueue = false;
	}

	public static void limitDisplayFPS(int fps) {
		double d = lastDrawTime + 1.0 / (double)fps;

		double e;
		for (e = GLFW.glfwGetTime(); e < d; e = GLFW.glfwGetTime()) {
			GLFW.glfwWaitEventsTimeout(d - e);
		}

		lastDrawTime = e;
	}

	@Deprecated
	public static void pushLightingAttributes() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.pushLightingAttributes();
	}

	@Deprecated
	public static void pushTextureAttributes() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.pushTextureAttributes();
	}

	@Deprecated
	public static void popAttributes() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.popAttributes();
	}

	@Deprecated
	public static void disableAlphaTest() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableAlphaTest();
	}

	@Deprecated
	public static void enableAlphaTest() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableAlphaTest();
	}

	@Deprecated
	public static void alphaFunc(int func, float ref) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.alphaFunc(func, ref);
	}

	@Deprecated
	public static void enableLighting() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableLighting();
	}

	@Deprecated
	public static void disableLighting() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableLighting();
	}

	@Deprecated
	public static void enableColorMaterial() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableColorMaterial();
	}

	@Deprecated
	public static void disableColorMaterial() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableColorMaterial();
	}

	@Deprecated
	public static void colorMaterial(int face, int mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.colorMaterial(face, mode);
	}

	@Deprecated
	public static void normal3f(float nx, float ny, float nz) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.normal3f(nx, ny, nz);
	}

	public static void disableDepthTest() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableDepthTest();
	}

	public static void enableDepthTest() {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.enableDepthTest();
	}

	public static void enableScissor(int i, int j, int k, int l) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.method_31319();
		GlStateManager.method_31317(i, j, k, l);
	}

	public static void disableScissor() {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.method_31318();
	}

	public static void depthFunc(int func) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.depthFunc(func);
	}

	public static void depthMask(boolean mask) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.depthMask(mask);
	}

	public static void enableBlend() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableBlend();
	}

	public static void disableBlend() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableBlend();
	}

	public static void blendFunc(GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFunc(srcFactor.field_22545, dstFactor.field_22528);
	}

	public static void blendFunc(int srcFactor, int dstFactor) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFunc(srcFactor, dstFactor);
	}

	public static void blendFuncSeparate(
		GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor, GlStateManager.SrcFactor srcAlpha, GlStateManager.DstFactor dstAlpha
	) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFuncSeparate(srcFactor.field_22545, dstFactor.field_22528, srcAlpha.field_22545, dstAlpha.field_22528);
	}

	public static void blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
	}

	public static void blendEquation(int mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendEquation(mode);
	}

	public static void blendColor(float red, float green, float blue, float alpha) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendColor(red, green, blue, alpha);
	}

	@Deprecated
	public static void enableFog() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableFog();
	}

	@Deprecated
	public static void disableFog() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableFog();
	}

	@Deprecated
	public static void fogMode(GlStateManager.FogMode mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogMode(mode.value);
	}

	@Deprecated
	public static void fogMode(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogMode(i);
	}

	@Deprecated
	public static void fogDensity(float density) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogDensity(density);
	}

	@Deprecated
	public static void fogStart(float start) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogStart(start);
	}

	@Deprecated
	public static void fogEnd(float end) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogEnd(end);
	}

	@Deprecated
	public static void fog(int pname, float red, float green, float blue, float alpha) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fog(pname, new float[]{red, green, blue, alpha});
	}

	@Deprecated
	public static void fogi(int i, int j) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogi(i, j);
	}

	public static void enableCull() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableCull();
	}

	public static void disableCull() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableCull();
	}

	public static void polygonMode(int i, int j) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.polygonMode(i, j);
	}

	public static void enablePolygonOffset() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enablePolygonOffset();
	}

	public static void disablePolygonOffset() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disablePolygonOffset();
	}

	public static void enableLineOffset() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableLineOffset();
	}

	public static void disableLineOffset() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableLineOffset();
	}

	public static void polygonOffset(float factor, float units) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.polygonOffset(factor, units);
	}

	public static void enableColorLogicOp() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableColorLogicOp();
	}

	public static void disableColorLogicOp() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableColorLogicOp();
	}

	public static void logicOp(GlStateManager.LogicOp op) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.logicOp(op.value);
	}

	public static void activeTexture(int texture) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.activeTexture(texture);
	}

	public static void enableTexture() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableTexture();
	}

	public static void disableTexture() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableTexture();
	}

	public static void texParameter(int target, int pname, int param) {
		GlStateManager.texParameter(target, pname, param);
	}

	public static void deleteTexture(int texture) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.deleteTexture(texture);
	}

	public static void bindTexture(int texture) {
		GlStateManager.bindTexture(texture);
	}

	@Deprecated
	public static void shadeModel(int mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.shadeModel(mode);
	}

	@Deprecated
	public static void enableRescaleNormal() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableRescaleNormal();
	}

	@Deprecated
	public static void disableRescaleNormal() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableRescaleNormal();
	}

	public static void viewport(int x, int y, int width, int height) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.viewport(x, y, width, height);
	}

	public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.colorMask(red, green, blue, alpha);
	}

	public static void stencilFunc(int func, int ref, int mask) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.stencilFunc(func, ref, mask);
	}

	public static void stencilMask(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.stencilMask(i);
	}

	public static void stencilOp(int sfail, int dpfail, int dppass) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.stencilOp(sfail, dpfail, dppass);
	}

	public static void clearDepth(double depth) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.clearDepth(depth);
	}

	public static void clearColor(float red, float green, float blue, float alpha) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.clearColor(red, green, blue, alpha);
	}

	public static void clearStencil(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.clearStencil(i);
	}

	public static void clear(int mask, boolean getError) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.clear(mask, getError);
	}

	@Deprecated
	public static void matrixMode(int mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.matrixMode(mode);
	}

	@Deprecated
	public static void loadIdentity() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.loadIdentity();
	}

	@Deprecated
	public static void pushMatrix() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.pushMatrix();
	}

	@Deprecated
	public static void popMatrix() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.popMatrix();
	}

	@Deprecated
	public static void ortho(double l, double r, double b, double t, double n, double f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.ortho(l, r, b, t, n, f);
	}

	@Deprecated
	public static void rotatef(float angle, float x, float y, float z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.rotatef(angle, x, y, z);
	}

	@Deprecated
	public static void scalef(float x, float y, float z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.scalef(x, y, z);
	}

	@Deprecated
	public static void scaled(double x, double y, double z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.scaled(x, y, z);
	}

	@Deprecated
	public static void translatef(float x, float y, float z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.translatef(x, y, z);
	}

	@Deprecated
	public static void translated(double x, double y, double z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.translated(x, y, z);
	}

	@Deprecated
	public static void multMatrix(Matrix4f matrix) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.multMatrix(matrix);
	}

	@Deprecated
	public static void color4f(float red, float green, float blue, float alpha) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.color4f(red, green, blue, alpha);
	}

	@Deprecated
	public static void color3f(float red, float green, float blue) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.color4f(red, green, blue, 1.0F);
	}

	@Deprecated
	public static void clearCurrentColor() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.clearCurrentColor();
	}

	public static void drawElements(int mode, int first, int count) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.drawArrays(mode, first, count, 0L);
	}

	public static void lineWidth(float width) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.lineWidth(width);
	}

	public static void pixelStore(int pname, int param) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.pixelStore(pname, param);
	}

	public static void pixelTransfer(int i, float f) {
		GlStateManager.pixelTransfer(i, f);
	}

	public static void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.readPixels(x, y, width, height, format, type, pixels);
	}

	public static void getString(int name, Consumer<String> consumer) {
		assertThread(RenderSystem::isOnGameThread);
		consumer.accept(GlStateManager.getString(name));
	}

	public static String getBackendDescription() {
		assertThread(RenderSystem::isInInitPhase);
		return String.format("LWJGL version %s", GLX._getLWJGLVersion());
	}

	public static String getApiDescription() {
		assertThread(RenderSystem::isInInitPhase);
		return GLX.getOpenGLVersionString();
	}

	public static LongSupplier initBackendSystem() {
		assertThread(RenderSystem::isInInitPhase);
		return GLX._initGlfw();
	}

	public static void initRenderer(int debugVerbosity, boolean debugSync) {
		assertThread(RenderSystem::isInInitPhase);
		GLX._init(debugVerbosity, debugSync);
	}

	public static void setErrorCallback(GLFWErrorCallbackI callback) {
		assertThread(RenderSystem::isInInitPhase);
		GLX._setGlfwErrorCallback(callback);
	}

	public static void renderCrosshair(int size) {
		assertThread(RenderSystem::isOnGameThread);
		GLX._renderCrosshair(size, true, true, true);
	}

	public static void setupNvFogDistance() {
		assertThread(RenderSystem::isOnGameThread);
		GLX._setupNvFogDistance();
	}

	@Deprecated
	public static void glMultiTexCoord2f(int texture, float s, float t) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.multiTexCoords2f(texture, s, t);
	}

	public static String getCapsString() {
		assertThread(RenderSystem::isOnGameThread);
		return GLX._getCapsString();
	}

	public static void setupDefaultState(int x, int y, int width, int height) {
		assertThread(RenderSystem::isInInitPhase);
		GlStateManager.enableTexture();
		GlStateManager.shadeModel(7425);
		GlStateManager.clearDepth(1.0);
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		GlStateManager.viewport(x, y, width, height);
	}

	public static int maxSupportedTextureSize() {
		assertThread(RenderSystem::isInInitPhase);
		if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
			int i = GlStateManager.getInteger(3379);

			for (int j = Math.max(32768, i); j >= 1024; j >>= 1) {
				GlStateManager.texImage2D(32868, 0, 6408, j, j, 0, 6408, 5121, null);
				int k = GlStateManager.getTexLevelParameter(32868, 0, 4096);
				if (k != 0) {
					MAX_SUPPORTED_TEXTURE_SIZE = j;
					return j;
				}
			}

			MAX_SUPPORTED_TEXTURE_SIZE = Math.max(i, 1024);
			LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", MAX_SUPPORTED_TEXTURE_SIZE);
		}

		return MAX_SUPPORTED_TEXTURE_SIZE;
	}

	public static void glBindBuffer(int target, Supplier<Integer> buffer) {
		GlStateManager.bindBuffers(target, (Integer)buffer.get());
	}

	public static void glBufferData(int target, ByteBuffer data, int usage) {
		assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.bufferData(target, data, usage);
	}

	public static void glDeleteBuffers(int buffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.deleteBuffers(buffer);
	}

	public static void glUniform1i(int location, int value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform1(location, value);
	}

	public static void glUniform1(int location, IntBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform1(location, value);
	}

	public static void glUniform2(int location, IntBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform2(location, value);
	}

	public static void glUniform3(int location, IntBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform3(location, value);
	}

	public static void glUniform4(int location, IntBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform4(location, value);
	}

	public static void glUniform1(int location, FloatBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform1(location, value);
	}

	public static void glUniform2(int location, FloatBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform2(location, value);
	}

	public static void glUniform3(int location, FloatBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform3(location, value);
	}

	public static void glUniform4(int location, FloatBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform4(location, value);
	}

	public static void glUniformMatrix2(int location, boolean transpose, FloatBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniformMatrix2(location, transpose, value);
	}

	public static void glUniformMatrix3(int location, boolean transpose, FloatBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniformMatrix3(location, transpose, value);
	}

	public static void glUniformMatrix4(int location, boolean transpose, FloatBuffer value) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniformMatrix4(location, transpose, value);
	}

	public static void setupOutline() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.setupOutline();
	}

	public static void teardownOutline() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.teardownOutline();
	}

	public static void setupOverlayColor(IntSupplier texture, int size) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.setupOverlayColor(texture.getAsInt(), size);
	}

	public static void teardownOverlayColor() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.teardownOverlayColor();
	}

	public static void setupLevelDiffuseLighting(Vec3f vec3f, Vec3f vec3f2, Matrix4f matrix4f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.setupLevelDiffuseLighting(vec3f, vec3f2, matrix4f);
	}

	public static void setupGuiFlatDiffuseLighting(Vec3f vec3f, Vec3f vec3f2) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.setupGuiFlatDiffuseLighting(vec3f, vec3f2);
	}

	public static void setupGui3DDiffuseLighting(Vec3f vec3f, Vec3f vec3f2) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.setupGui3dDiffuseLighting(vec3f, vec3f2);
	}

	public static void mulTextureByProjModelView() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.mulTextureByProjModelView();
	}

	public static void setupEndPortalTexGen() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.setupEndPortalTexGen();
	}

	public static void clearTexGen() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.clearTexGen();
	}

	public static void beginInitialization() {
		isInInit = true;
	}

	public static void finishInitialization() {
		isInInit = false;
		if (!recordingQueue.isEmpty()) {
			replayQueue();
		}

		if (!recordingQueue.isEmpty()) {
			throw new IllegalStateException("Recorded to render queue during initialization");
		}
	}

	public static void glGenBuffers(Consumer<Integer> consumer) {
		if (!isOnRenderThread()) {
			recordRenderCall(() -> consumer.accept(GlStateManager.genBuffers()));
		} else {
			consumer.accept(GlStateManager.genBuffers());
		}
	}

	public static Tessellator renderThreadTesselator() {
		assertThread(RenderSystem::isOnRenderThread);
		return RENDER_THREAD_TESSELATOR;
	}

	public static void defaultBlendFunc() {
		blendFuncSeparate(
			GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
		);
	}

	public static void defaultAlphaFunc() {
		alphaFunc(516, 0.1F);
	}

	@Deprecated
	public static void runAsFancy(Runnable runnable) {
		boolean bl = MinecraftClient.isFabulousGraphicsOrBetter();
		if (!bl) {
			runnable.run();
		} else {
			GameOptions gameOptions = MinecraftClient.getInstance().options;
			GraphicsMode graphicsMode = gameOptions.graphicsMode;
			gameOptions.graphicsMode = GraphicsMode.FANCY;
			runnable.run();
			gameOptions.graphicsMode = graphicsMode;
		}
	}

	public static RenderSystem.class_5590 getSequentialBuffer(VertexFormat.DrawMode drawMode, int i) {
		assertThread(RenderSystem::isOnRenderThread);
		RenderSystem.class_5590 lv = drawMode == VertexFormat.DrawMode.QUADS ? sharedSequentialQuad : sharedSequential;
		lv.method_31920(i);
		return lv;
	}

	@Environment(EnvType.CLIENT)
	public static final class class_5590 {
		private final int field_27332;
		private final int field_27333;
		private final RenderSystem.class_5590.class_5591 field_27334;
		private int field_27335;
		private VertexFormat.IntType field_27336 = VertexFormat.IntType.BYTE;
		private int field_27337;

		private class_5590(int i, int j, RenderSystem.class_5590.class_5591 arg) {
			this.field_27332 = i;
			this.field_27333 = j;
			this.field_27334 = arg;
		}

		private void method_31920(int i) {
			if (i > this.field_27337) {
				RenderSystem.LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.field_27337, i);
				if (this.field_27335 == 0) {
					this.field_27335 = GlStateManager.genBuffers();
				}

				VertexFormat.IntType intType = VertexFormat.IntType.getSmallestTypeFor(i);
				int j = MathHelper.roundUpToMultiple(i * intType.size, 4);
				GlStateManager.bindBuffers(34963, this.field_27335);
				GlStateManager.method_31945(34963, (long)j, 35044);
				ByteBuffer byteBuffer = GlStateManager.method_31946(34963, 35001);
				if (byteBuffer == null) {
					throw new RuntimeException("Failed to map GL buffer");
				} else {
					this.field_27336 = intType;
					it.unimi.dsi.fastutil.ints.IntConsumer intConsumer = this.method_31922(byteBuffer);

					for (int k = 0; k < i; k += this.field_27333) {
						this.field_27334.accept(intConsumer, k * this.field_27332 / this.field_27333);
					}

					GlStateManager.method_31947(34963);
					GlStateManager.bindBuffers(34963, 0);
					this.field_27337 = i;
				}
			}
		}

		private it.unimi.dsi.fastutil.ints.IntConsumer method_31922(ByteBuffer byteBuffer) {
			switch (this.field_27336) {
				case BYTE:
					return i -> byteBuffer.put((byte)i);
				case SHORT:
					return i -> byteBuffer.putShort((short)i);
				case INT:
				default:
					return byteBuffer::putInt;
			}
		}

		public int method_31919() {
			return this.field_27335;
		}

		public VertexFormat.IntType method_31924() {
			return this.field_27336;
		}

		@Environment(EnvType.CLIENT)
		interface class_5591 {
			void accept(it.unimi.dsi.fastutil.ints.IntConsumer intConsumer, int i);
		}
	}
}
