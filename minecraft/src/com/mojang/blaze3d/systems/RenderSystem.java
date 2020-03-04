package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.Matrix4f;
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

	public static void pushLightingAttributes() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.pushLightingAttributes();
	}

	public static void pushTextureAttributes() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.pushTextureAttributes();
	}

	public static void popAttributes() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.popAttributes();
	}

	public static void disableAlphaTest() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableAlphaTest();
	}

	public static void enableAlphaTest() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableAlphaTest();
	}

	public static void alphaFunc(int func, float ref) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.alphaFunc(func, ref);
	}

	public static void enableLighting() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableLighting();
	}

	public static void disableLighting() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableLighting();
	}

	public static void enableColorMaterial() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableColorMaterial();
	}

	public static void disableColorMaterial() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableColorMaterial();
	}

	public static void colorMaterial(int face, int mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.colorMaterial(face, mode);
	}

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

	public static void enableFog() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableFog();
	}

	public static void disableFog() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableFog();
	}

	public static void fogMode(GlStateManager.FogMode mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogMode(mode.value);
	}

	public static void fogMode(int mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogMode(mode);
	}

	public static void fogDensity(float density) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogDensity(density);
	}

	public static void fogStart(float start) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogStart(start);
	}

	public static void fogEnd(float end) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogEnd(end);
	}

	public static void fog(int pname, float red, float green, float blue, float alpha) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fog(pname, new float[]{red, green, blue, alpha});
	}

	public static void fogi(int pname, int param) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogi(pname, param);
	}

	public static void enableCull() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableCull();
	}

	public static void disableCull() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableCull();
	}

	public static void polygonMode(int face, int mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.polygonMode(face, mode);
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

	public static void shadeModel(int mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.shadeModel(mode);
	}

	public static void enableRescaleNormal() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableRescaleNormal();
	}

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

	public static void stencilMask(int mask) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.stencilMask(mask);
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

	public static void clearStencil(int stencil) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.clearStencil(stencil);
	}

	public static void clear(int mask, boolean getError) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.clear(mask, getError);
	}

	public static void matrixMode(int mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.matrixMode(mode);
	}

	public static void loadIdentity() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.loadIdentity();
	}

	public static void pushMatrix() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.pushMatrix();
	}

	public static void popMatrix() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.popMatrix();
	}

	public static void ortho(double l, double r, double b, double t, double n, double f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.ortho(l, r, b, t, n, f);
	}

	public static void rotatef(float angle, float x, float y, float z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.rotatef(angle, x, y, z);
	}

	public static void scalef(float x, float y, float z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.scalef(x, y, z);
	}

	public static void scaled(double x, double y, double z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.scaled(x, y, z);
	}

	public static void translatef(float x, float y, float z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.translatef(x, y, z);
	}

	public static void translated(double x, double y, double z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.translated(x, y, z);
	}

	public static void multMatrix(Matrix4f matrix) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.multMatrix(matrix);
	}

	public static void color4f(float red, float green, float blue, float alpha) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.color4f(red, green, blue, alpha);
	}

	public static void color3f(float red, float green, float blue) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.color4f(red, green, blue, 1.0F);
	}

	public static void clearCurrentColor() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.clearCurrentColor();
	}

	public static void drawArrays(int mode, int first, int count) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.drawArrays(mode, first, count);
	}

	public static void lineWidth(float width) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.lineWidth(width);
	}

	public static void pixelStore(int pname, int param) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.pixelStore(pname, param);
	}

	public static void pixelTransfer(int pname, float param) {
		GlStateManager.pixelTransfer(pname, param);
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

	public static void setupLevelDiffuseLighting(Matrix4f modelMatrix) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.setupLevelDiffuseLighting(modelMatrix);
	}

	public static void setupGuiFlatDiffuseLighting() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_24221();
	}

	public static void setupGui3DDiffuseLighting() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_24222();
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
}
