package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4573;
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
	private static final ConcurrentLinkedQueue<class_4573> recordingQueue = Queues.newConcurrentLinkedQueue();
	private static final Tessellator RENDER_THREAD_TESSELATOR = new Tessellator();
	private static final float DEFAULTALPHACUTOFF = 0.1F;
	private static boolean isReplayingQueue;
	private static Thread gameThread;
	private static Thread renderThread;
	private static int MAX_SUPPORTED_TEXTURE_SIZE = -1;
	private static boolean isInInit;

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

	public static void alphaFunc(int i, float f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.alphaFunc(i, f);
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

	public static void colorMaterial(int i, int j) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.colorMaterial(i, j);
	}

	public static void normal3f(float f, float g, float h) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.normal3f(f, g, h);
	}

	public static void disableDepthTest() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableDepthTest();
	}

	public static void enableDepthTest() {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.enableDepthTest();
	}

	public static void depthFunc(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.depthFunc(i);
	}

	public static void depthMask(boolean bl) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.depthMask(bl);
	}

	public static void enableBlend() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableBlend();
	}

	public static void disableBlend() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableBlend();
	}

	public static void blendFunc(GlStateManager.class_4535 arg, GlStateManager.class_4534 arg2) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFunc(arg.value, arg2.value);
	}

	public static void blendFunc(int i, int j) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFunc(i, j);
	}

	public static void blendFuncSeparate(
		GlStateManager.class_4535 arg, GlStateManager.class_4534 arg2, GlStateManager.class_4535 arg3, GlStateManager.class_4534 arg4
	) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFuncSeparate(arg.value, arg2.value, arg3.value, arg4.value);
	}

	public static void blendFuncSeparate(int i, int j, int k, int l) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendFuncSeparate(i, j, k, l);
	}

	public static void blendEquation(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.blendEquation(i);
	}

	public static void setupSolidRenderingTextureCombine(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.setupSolidRenderingTextureCombine(i);
	}

	public static void tearDownSolidRenderingTextureCombine() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.tearDownSolidRenderingTextureCombine();
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

	public static void fog(int i, FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.fog(i, floatBuffer);
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

	public static void cullFace(GlStateManager.FaceSides faceSides) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.cullFace(faceSides.glValue);
	}

	public static void cullFace(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.cullFace(i);
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

	public static void polygonOffset(float f, float g) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.polygonOffset(f, g);
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

	public static void logicOp(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.logicOp(i);
	}

	public static void enableTexGen(GlStateManager.TexCoord texCoord) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableTexGen(texCoord);
	}

	public static void disableTexGen(GlStateManager.TexCoord texCoord) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableTexGen(texCoord);
	}

	public static void texGenMode(GlStateManager.TexCoord texCoord, int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.texGenMode(texCoord, i);
	}

	public static void texGenParam(GlStateManager.TexCoord texCoord, int i, FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.texGenParam(texCoord, i, floatBuffer);
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

	public static void bindTexture(int i) {
		GlStateManager.bindTexture(i);
	}

	public static void texImage2D(int i, int j, int k, int l, int m, int n, int o, int p, @Nullable IntBuffer intBuffer) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.texImage2D(i, j, k, l, m, n, o, p, intBuffer);
	}

	public static void texSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p, long q) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.texSubImage2D(i, j, k, l, m, n, o, p, q);
	}

	public static void copyTexSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.copyTexSubImage2D(i, j, k, l, m, n, o, p);
	}

	public static void getTexImage(int i, int j, int k, int l, long m) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.getTexImage(i, j, k, l, m);
	}

	public static void enableNormalize() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.enableNormalize();
	}

	public static void disableNormalize() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.disableNormalize();
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

	public static void getMatrix(int i, FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.getMatrix(i, floatBuffer);
	}

	public static void getMatrix4f(int i, Consumer<Matrix4f> consumer) {
		assertThread(RenderSystem::isOnGameThread);
		consumer.accept(GlStateManager.getMatrix4f(i));
	}

	public static void ortho(double d, double e, double f, double g, double h, double i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.ortho(d, e, f, g, h, i);
	}

	public static void rotatef(float f, float g, float h, float i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.rotatef(f, g, h, i);
	}

	public static void rotated(double d, double e, double f, double g) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.rotated(d, e, f, g);
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
		GlStateManager.translated(d, e, f);
	}

	public static void multMatrix(FloatBuffer floatBuffer) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.multMatrix(floatBuffer);
	}

	public static void multMatrix(Matrix4f matrix4f) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.multMatrix(matrix4f);
	}

	public static void color4f(float f, float g, float h, float i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.color4f(f, g, h, i);
	}

	public static void color3f(float f, float g, float h) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.color4f(f, g, h, 1.0F);
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

	public static void getError(Consumer<Integer> consumer) {
		assertThread(RenderSystem::isOnGameThread);
		consumer.accept(GlStateManager.getError());
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

	public static void pollEvents() {
	}

	public static void recordRenderCall(class_4573 arg) {
		recordingQueue.add(arg);
	}

	public static void glClientActiveTexture(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.clientActiveTexture(i);
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
		GlStateManager.cullFace(GlStateManager.FaceSides.BACK.glValue);
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

	public static void flipFrame() {
		GLFW.glfwPollEvents();
		isReplayingQueue = true;

		while (!recordingQueue.isEmpty()) {
			class_4573 lv = (class_4573)recordingQueue.poll();
			lv.execute();
		}

		isReplayingQueue = false;
		Tessellator.getInstance().getBufferBuilder().clear();
	}

	public static void glBindFramebuffer(int i, int j) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.bindFramebuffer(i, j);
	}

	public static void glDeleteRenderbuffers(int i) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.deleteRenderbuffers(i);
	}

	public static void glDeleteFramebuffers(int i) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.deleteFramebuffers(i);
	}

	public static void glFramebufferTexture2D(int i, int j, int k, int l, int m) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.framebufferTexture2D(i, j, k, l, m);
	}

	public static void glBindRenderbuffer(int i, int j) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.bindRenderbuffer(i, j);
	}

	public static void glRenderbufferStorage(int i, int j, int k, int l) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.renderbufferStorage(i, j, k, l);
	}

	public static void glFramebufferRenderbuffer(int i, int j, int k, int l) {
		assertThread(RenderSystem::isOnGameThreadOrInit);
		GlStateManager.framebufferRenderbuffer(i, j, k, l);
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

	public static void glUseProgram(int i) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.useProgram(i);
	}

	public static void setupOverlayColor(int i, boolean bl) {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22610(i, bl);
	}

	public static void teardownOverlayColor() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22614();
	}

	public static void enableUsualDiffuseLighting() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22616();
	}

	public static void enableGuiDiffuseLighting() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22617();
	}

	public static void disableDiffuseLighting() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22618();
	}

	public static void setProfile(RenderSystem.class_4564 arg) {
		arg.begin();
	}

	public static void unsetProfile(RenderSystem.class_4564 arg) {
		arg.end();
	}

	public static void beginInitialization() {
		isInInit = true;
	}

	public static void finishInitialization() {
		isInInit = false;
		if (!recordingQueue.isEmpty()) {
			flipFrame();
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
			GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
		);
	}

	public static void defaultAlphaFunc() {
		alphaFunc(516, 0.1F);
	}

	private static void setupDefaultGlState() {
		assertThread(RenderSystem::isOnGameThread);
		GlStateManager.method_22615();
	}

	@Environment(EnvType.CLIENT)
	public interface class_4564 {
		RenderSystem.class_4564 field_20744 = new RenderSystem.class_4564() {
			@Override
			public void begin() {
				RenderSystem.setupDefaultGlState();
			}

			@Override
			public void end() {
			}
		};
		RenderSystem.class_4564 field_20745 = new RenderSystem.class_4564() {
			@Override
			public void begin() {
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(770, 771, 1, 0);
			}

			@Override
			public void end() {
				RenderSystem.disableBlend();
			}
		};
		RenderSystem.class_4564 field_20746 = new RenderSystem.class_4564() {
			@Override
			public void begin() {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.15F);
				RenderSystem.depthMask(false);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
				RenderSystem.alphaFunc(516, 0.003921569F);
			}

			@Override
			public void end() {
				RenderSystem.disableBlend();
				RenderSystem.defaultAlphaFunc();
				RenderSystem.depthMask(true);
			}
		};

		void begin();

		void end();
	}
}
