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
import net.minecraft.util.math.MathHelper;
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

	public static void initGameThread(boolean bl) {
		boolean bl2 = renderThread == Thread.currentThread();
		if (gameThread == null && renderThread != null && bl2 != bl) {
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

	public static void assertThread(Supplier<Boolean> supplier) {
		if (!(Boolean)supplier.get()) {
			throw new IllegalStateException("Rendersystem called from wrong thread");
		}
	}

	public static boolean isInInitPhase() {
		return true;
	}

	public static void recordRenderCall(RenderCall renderCall) {
		recordingQueue.add(renderCall);
	}

	public static void flipFrame(long l) {
		GLFW.glfwPollEvents();
		replayQueue();
		Tessellator.getInstance().getBuffer().clear();
		GLFW.glfwSwapBuffers(l);
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

	public static void limitDisplayFPS(int i) {
		double d = lastDrawTime + 1.0 / (double)i;

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

	public static void blendFunc(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFunc(sourceFactor.value, destFactor.value);
	}

	public static void blendFunc(int sfactor, int dfactor) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFunc(sfactor, dfactor);
	}

	public static void blendFuncSeparate(
		GlStateManager.SourceFactor sourceFactor,
		GlStateManager.DestFactor destFactor,
		GlStateManager.SourceFactor sourceFactor2,
		GlStateManager.DestFactor destFactor2
	) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFuncSeparate(sourceFactor.value, destFactor.value, sourceFactor2.value, destFactor2.value);
	}

	public static void blendFuncSeparate(int sFactorRGB, int dFactorRGB, int sFactorAlpha, int dFactorAlpha) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFuncSeparate(sFactorRGB, dFactorRGB, sFactorAlpha, dFactorAlpha);
	}

	public static void blendEquation(int mode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendEquation(mode);
	}

	public static void blendColor(float f, float g, float h, float i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22883(f, g, h, i);
	}

	public static void enableFog() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableFog();
	}

	public static void disableFog() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableFog();
	}

	public static void fogMode(GlStateManager.FogMode fogMode) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogMode(fogMode.glValue);
	}

	public static void fogMode(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogMode(i);
	}

	public static void fogDensity(float f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogDensity(f);
	}

	public static void fogStart(float f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogStart(f);
	}

	public static void fogEnd(float f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fogEnd(f);
	}

	public static void fog(int i, float f, float g, float h, float j) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fog(i, new float[]{f, g, h, j});
	}

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

	public static void logicOp(GlStateManager.LogicOp logicOp) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.logicOp(logicOp.glValue);
	}

	public static void activeTexture(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.activeTexture(i);
	}

	public static void enableTexture() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableTexture();
	}

	public static void disableTexture() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableTexture();
	}

	public static void texParameter(int i, int j, int k) {
		GlStateManager.texParameter(i, j, k);
	}

	public static void deleteTexture(int i) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.deleteTexture(i);
	}

	public static void bindTexture(int texture) {
		GlStateManager.bindTexture(texture);
	}

	public static void shadeModel(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.shadeModel(i);
	}

	public static void enableRescaleNormal() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableRescaleNormal();
	}

	public static void disableRescaleNormal() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableRescaleNormal();
	}

	public static void viewport(int i, int j, int k, int l) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.viewport(i, j, k, l);
	}

	public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.colorMask(bl, bl2, bl3, bl4);
	}

	public static void stencilFunc(int i, int j, int k) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.stencilFunc(i, j, k);
	}

	public static void stencilMask(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.stencilMask(i);
	}

	public static void stencilOp(int i, int j, int k) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.stencilOp(i, j, k);
	}

	public static void clearDepth(double d) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.clearDepth(d);
	}

	public static void clearColor(float f, float g, float h, float i) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.clearColor(f, g, h, i);
	}

	public static void clearStencil(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.clearStencil(i);
	}

	public static void clear(int i, boolean bl) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.clear(i, bl);
	}

	public static void matrixMode(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.matrixMode(i);
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

	public static void ortho(double d, double e, double f, double g, double h, double i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.ortho(d, e, f, g, h, i);
	}

	public static void rotatef(float angle, float x, float y, float z) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.rotatef(angle, x, y, z);
	}

	public static void scalef(float f, float g, float h) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.scalef(f, g, h);
	}

	public static void scaled(double d, double e, double f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.scaled(d, e, f);
	}

	public static void translatef(float f, float g, float h) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.translatef(f, g, h);
	}

	public static void translated(double d, double e, double f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.rotated(d, e, f);
	}

	public static void multMatrix(Matrix4f matrix4f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.multMatrix(matrix4f);
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

	public static void drawArrays(int i, int j, int k) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.drawArrays(i, j, k);
	}

	public static void lineWidth(float f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.lineWidth(f);
	}

	public static void pixelStore(int i, int j) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.pixelStore(i, j);
	}

	public static void pixelTransfer(int i, float f) {
		GlStateManager.pixelTransfer(i, f);
	}

	public static void readPixels(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.readPixels(i, j, k, l, m, n, byteBuffer);
	}

	public static void getString(int i, Consumer<String> consumer) {
		assertThread(RenderSystem::isOnGameThread);
		consumer.accept(GlStateManager.getString(i));
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

	public static void initRenderer(int i, boolean bl) {
		assertThread(RenderSystem::isInInitPhase);
		GLX._init(i, bl);
	}

	public static void setErrorCallback(GLFWErrorCallbackI gLFWErrorCallbackI) {
		assertThread(RenderSystem::isInInitPhase);
		GLX._setGlfwErrorCallback(gLFWErrorCallbackI);
	}

	public static void renderCrosshair(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GLX._renderCrosshair(i, true, true, true);
	}

	public static void setupNvFogDistance() {
		assertThread(RenderSystem::isOnGameThread);
		GLX._setupNvFogDistance();
	}

	public static void glMultiTexCoord2f(int i, float f, float g) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.multiTexCoords2f(i, f, g);
	}

	public static String getCapsString() {
		assertThread(RenderSystem::isOnGameThread);
		return GLX._getCapsString();
	}

	public static void setupDefaultState(int i, int j, int k, int l) {
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
		GlStateManager.viewport(i, j, k, l);
	}

	public static int maxSupportedTextureSize() {
		assertThread(RenderSystem::isInInitPhase);
		if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
			for (int i = 16384; i > 0; i >>= 1) {
				GlStateManager.texImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, null);
				int j = GlStateManager.getTexLevelParameter(32868, 0, 4096);
				if (j != 0) {
					MAX_SUPPORTED_TEXTURE_SIZE = i;
					return i;
				}
			}

			MAX_SUPPORTED_TEXTURE_SIZE = MathHelper.clamp(GlStateManager.getInteger(3379), 1024, 16384);
			LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", MAX_SUPPORTED_TEXTURE_SIZE);
		}

		return MAX_SUPPORTED_TEXTURE_SIZE;
	}

	public static void glBindBuffer(int i, Supplier<Integer> supplier) {
		GlStateManager.bindBuffers(i, (Integer)supplier.get());
	}

	public static void glBufferData(int i, ByteBuffer byteBuffer, int j) {
		assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.bufferData(i, byteBuffer, j);
	}

	public static void glDeleteBuffers(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.deleteBuffers(i);
	}

	public static void glUniform1i(int i, int j) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform1(i, j);
	}

	public static void glUniform1(int i, IntBuffer intBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform1(i, intBuffer);
	}

	public static void glUniform2(int i, IntBuffer intBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform2(i, intBuffer);
	}

	public static void glUniform3(int i, IntBuffer intBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform3(i, intBuffer);
	}

	public static void glUniform4(int i, IntBuffer intBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform4(i, intBuffer);
	}

	public static void glUniform1(int i, FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform1(i, floatBuffer);
	}

	public static void glUniform2(int i, FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform2(i, floatBuffer);
	}

	public static void glUniform3(int i, FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform3(i, floatBuffer);
	}

	public static void glUniform4(int i, FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniform4(i, floatBuffer);
	}

	public static void glUniformMatrix2(int i, boolean bl, FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniformMatrix2(i, bl, floatBuffer);
	}

	public static void glUniformMatrix3(int i, boolean bl, FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniformMatrix3(i, bl, floatBuffer);
	}

	public static void glUniformMatrix4(int i, boolean bl, FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.uniformMatrix4(i, bl, floatBuffer);
	}

	public static void setupOutline() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_23282();
	}

	public static void teardownOutline() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_23283();
	}

	public static void setupOverlayColor(IntSupplier intSupplier, int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22610(intSupplier.getAsInt(), i);
	}

	public static void teardownOverlayColor() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22618();
	}

	public static void setupLevelDiffuseLighting(Matrix4f matrix4f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22616(matrix4f);
	}

	public static void setupGuiDiffuseLighting(Matrix4f matrix4f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22617(matrix4f);
	}

	public static void mulTextureByProjModelView() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22889();
	}

	public static void setupEndPortalTexGen() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22887();
	}

	public static void clearTexGen() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22888();
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
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
	}

	public static void defaultAlphaFunc() {
		alphaFunc(516, 0.1F);
	}
}
