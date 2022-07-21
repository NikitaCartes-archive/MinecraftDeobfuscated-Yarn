package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeSupplier;
import net.minecraft.util.annotation.DeobfuscateClass;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public class RenderSystem {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final ConcurrentLinkedQueue<RenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
	private static final Tessellator RENDER_THREAD_TESSELATOR = new Tessellator();
	private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
	private static boolean isReplayingQueue;
	@Nullable
	private static Thread gameThread;
	@Nullable
	private static Thread renderThread;
	private static int MAX_SUPPORTED_TEXTURE_SIZE = -1;
	private static boolean isInInit;
	private static double lastDrawTime = Double.MIN_VALUE;
	private static final RenderSystem.IndexBuffer sharedSequential = new RenderSystem.IndexBuffer(1, 1, IntConsumer::accept);
	private static final RenderSystem.IndexBuffer sharedSequentialQuad = new RenderSystem.IndexBuffer(4, 6, (indexConsumer, vertexCount) -> {
		indexConsumer.accept(vertexCount + 0);
		indexConsumer.accept(vertexCount + 1);
		indexConsumer.accept(vertexCount + 2);
		indexConsumer.accept(vertexCount + 2);
		indexConsumer.accept(vertexCount + 3);
		indexConsumer.accept(vertexCount + 0);
	});
	private static final RenderSystem.IndexBuffer sharedSequentialLines = new RenderSystem.IndexBuffer(4, 6, (indexConsumer, vertexCount) -> {
		indexConsumer.accept(vertexCount + 0);
		indexConsumer.accept(vertexCount + 1);
		indexConsumer.accept(vertexCount + 2);
		indexConsumer.accept(vertexCount + 3);
		indexConsumer.accept(vertexCount + 2);
		indexConsumer.accept(vertexCount + 1);
	});
	private static Matrix3f inverseViewRotationMatrix = new Matrix3f();
	private static Matrix4f projectionMatrix = new Matrix4f();
	private static Matrix4f savedProjectionMatrix = new Matrix4f();
	private static MatrixStack modelViewStack = new MatrixStack();
	private static Matrix4f modelViewMatrix = new Matrix4f();
	private static Matrix4f textureMatrix = new Matrix4f();
	private static final int[] shaderTextures = new int[12];
	private static final float[] shaderColor = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
	private static float shaderFogStart;
	private static float shaderFogEnd = 1.0F;
	private static final float[] shaderFogColor = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
	private static FogShape shaderFogShape = FogShape.SPHERE;
	private static final Vec3f[] shaderLightDirections = new Vec3f[2];
	private static float shaderGameTime;
	private static float shaderLineWidth = 1.0F;
	private static String apiDescription = "Unknown";
	@Nullable
	private static Shader shader;

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

	public static void assertInInitPhase() {
		if (!isInInitPhase()) {
			throw constructThreadException();
		}
	}

	public static void assertOnGameThreadOrInit() {
		if (!isInInit && !isOnGameThread()) {
			throw constructThreadException();
		}
	}

	public static void assertOnRenderThreadOrInit() {
		if (!isInInit && !isOnRenderThread()) {
			throw constructThreadException();
		}
	}

	public static void assertOnRenderThread() {
		if (!isOnRenderThread()) {
			throw constructThreadException();
		}
	}

	public static void assertOnGameThread() {
		if (!isOnGameThread()) {
			throw constructThreadException();
		}
	}

	private static IllegalStateException constructThreadException() {
		return new IllegalStateException("Rendersystem called from wrong thread");
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

	public static void disableDepthTest() {
		assertOnRenderThread();
		GlStateManager._disableDepthTest();
	}

	public static void enableDepthTest() {
		assertOnGameThreadOrInit();
		GlStateManager._enableDepthTest();
	}

	public static void enableScissor(int x, int y, int width, int height) {
		assertOnGameThreadOrInit();
		GlStateManager._enableScissorTest();
		GlStateManager._scissorBox(x, y, width, height);
	}

	public static void disableScissor() {
		assertOnGameThreadOrInit();
		GlStateManager._disableScissorTest();
	}

	public static void depthFunc(int func) {
		assertOnRenderThread();
		GlStateManager._depthFunc(func);
	}

	public static void depthMask(boolean mask) {
		assertOnRenderThread();
		GlStateManager._depthMask(mask);
	}

	public static void enableBlend() {
		assertOnRenderThread();
		GlStateManager._enableBlend();
	}

	public static void disableBlend() {
		assertOnRenderThread();
		GlStateManager._disableBlend();
	}

	public static void blendFunc(GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor) {
		assertOnRenderThread();
		GlStateManager._blendFunc(srcFactor.value, dstFactor.value);
	}

	public static void blendFunc(int srcFactor, int dstFactor) {
		assertOnRenderThread();
		GlStateManager._blendFunc(srcFactor, dstFactor);
	}

	public static void blendFuncSeparate(
		GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor, GlStateManager.SrcFactor srcAlpha, GlStateManager.DstFactor dstAlpha
	) {
		assertOnRenderThread();
		GlStateManager._blendFuncSeparate(srcFactor.value, dstFactor.value, srcAlpha.value, dstAlpha.value);
	}

	public static void blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
		assertOnRenderThread();
		GlStateManager._blendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
	}

	public static void blendEquation(int mode) {
		assertOnRenderThread();
		GlStateManager._blendEquation(mode);
	}

	public static void enableCull() {
		assertOnRenderThread();
		GlStateManager._enableCull();
	}

	public static void disableCull() {
		assertOnRenderThread();
		GlStateManager._disableCull();
	}

	public static void polygonMode(int face, int mode) {
		assertOnRenderThread();
		GlStateManager._polygonMode(face, mode);
	}

	public static void enablePolygonOffset() {
		assertOnRenderThread();
		GlStateManager._enablePolygonOffset();
	}

	public static void disablePolygonOffset() {
		assertOnRenderThread();
		GlStateManager._disablePolygonOffset();
	}

	public static void polygonOffset(float factor, float units) {
		assertOnRenderThread();
		GlStateManager._polygonOffset(factor, units);
	}

	public static void enableColorLogicOp() {
		assertOnRenderThread();
		GlStateManager._enableColorLogicOp();
	}

	public static void disableColorLogicOp() {
		assertOnRenderThread();
		GlStateManager._disableColorLogicOp();
	}

	public static void logicOp(GlStateManager.LogicOp op) {
		assertOnRenderThread();
		GlStateManager._logicOp(op.value);
	}

	public static void activeTexture(int texture) {
		assertOnRenderThread();
		GlStateManager._activeTexture(texture);
	}

	public static void enableTexture() {
		assertOnRenderThread();
		GlStateManager._enableTexture();
	}

	public static void disableTexture() {
		assertOnRenderThread();
		GlStateManager._disableTexture();
	}

	public static void texParameter(int target, int pname, int param) {
		GlStateManager._texParameter(target, pname, param);
	}

	public static void deleteTexture(int texture) {
		assertOnGameThreadOrInit();
		GlStateManager._deleteTexture(texture);
	}

	public static void bindTextureForSetup(int id) {
		bindTexture(id);
	}

	public static void bindTexture(int texture) {
		GlStateManager._bindTexture(texture);
	}

	public static void viewport(int x, int y, int width, int height) {
		assertOnGameThreadOrInit();
		GlStateManager._viewport(x, y, width, height);
	}

	public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		assertOnRenderThread();
		GlStateManager._colorMask(red, green, blue, alpha);
	}

	public static void stencilFunc(int func, int ref, int mask) {
		assertOnRenderThread();
		GlStateManager._stencilFunc(func, ref, mask);
	}

	public static void stencilMask(int mask) {
		assertOnRenderThread();
		GlStateManager._stencilMask(mask);
	}

	public static void stencilOp(int sfail, int dpfail, int dppass) {
		assertOnRenderThread();
		GlStateManager._stencilOp(sfail, dpfail, dppass);
	}

	public static void clearDepth(double depth) {
		assertOnGameThreadOrInit();
		GlStateManager._clearDepth(depth);
	}

	public static void clearColor(float red, float green, float blue, float alpha) {
		assertOnGameThreadOrInit();
		GlStateManager._clearColor(red, green, blue, alpha);
	}

	public static void clearStencil(int stencil) {
		assertOnRenderThread();
		GlStateManager._clearStencil(stencil);
	}

	public static void clear(int mask, boolean getError) {
		assertOnGameThreadOrInit();
		GlStateManager._clear(mask, getError);
	}

	public static void setShaderFogStart(float shaderFogStart) {
		assertOnRenderThread();
		_setShaderFogStart(shaderFogStart);
	}

	private static void _setShaderFogStart(float shaderFogStart) {
		RenderSystem.shaderFogStart = shaderFogStart;
	}

	public static float getShaderFogStart() {
		assertOnRenderThread();
		return shaderFogStart;
	}

	public static void setShaderFogEnd(float shaderFogEnd) {
		assertOnRenderThread();
		_setShaderFogEnd(shaderFogEnd);
	}

	private static void _setShaderFogEnd(float shaderFogEnd) {
		RenderSystem.shaderFogEnd = shaderFogEnd;
	}

	public static float getShaderFogEnd() {
		assertOnRenderThread();
		return shaderFogEnd;
	}

	public static void setShaderFogColor(float red, float green, float blue, float alpha) {
		assertOnRenderThread();
		_setShaderFogColor(red, green, blue, alpha);
	}

	public static void setShaderFogColor(float red, float green, float blue) {
		setShaderFogColor(red, green, blue, 1.0F);
	}

	private static void _setShaderFogColor(float red, float green, float blue, float alpha) {
		shaderFogColor[0] = red;
		shaderFogColor[1] = green;
		shaderFogColor[2] = blue;
		shaderFogColor[3] = alpha;
	}

	public static float[] getShaderFogColor() {
		assertOnRenderThread();
		return shaderFogColor;
	}

	public static void setShaderFogShape(FogShape shaderFogShape) {
		assertOnRenderThread();
		_setShaderFogShape(shaderFogShape);
	}

	private static void _setShaderFogShape(FogShape shaderFogShape) {
		RenderSystem.shaderFogShape = shaderFogShape;
	}

	public static FogShape getShaderFogShape() {
		assertOnRenderThread();
		return shaderFogShape;
	}

	public static void setShaderLights(Vec3f vec3f, Vec3f vec3f2) {
		assertOnRenderThread();
		_setShaderLights(vec3f, vec3f2);
	}

	public static void _setShaderLights(Vec3f vec3f, Vec3f vec3f2) {
		shaderLightDirections[0] = vec3f;
		shaderLightDirections[1] = vec3f2;
	}

	public static void setupShaderLights(Shader shader) {
		assertOnRenderThread();
		if (shader.light0Direction != null) {
			shader.light0Direction.set(shaderLightDirections[0]);
		}

		if (shader.light1Direction != null) {
			shader.light1Direction.set(shaderLightDirections[1]);
		}
	}

	public static void setShaderColor(float red, float green, float blue, float alpha) {
		if (!isOnRenderThread()) {
			recordRenderCall(() -> _setShaderColor(red, green, blue, alpha));
		} else {
			_setShaderColor(red, green, blue, alpha);
		}
	}

	private static void _setShaderColor(float red, float green, float blue, float alpha) {
		shaderColor[0] = red;
		shaderColor[1] = green;
		shaderColor[2] = blue;
		shaderColor[3] = alpha;
	}

	public static float[] getShaderColor() {
		assertOnRenderThread();
		return shaderColor;
	}

	public static void drawElements(int mode, int count, int type) {
		assertOnRenderThread();
		GlStateManager._drawElements(mode, count, type, 0L);
	}

	public static void lineWidth(float width) {
		if (!isOnRenderThread()) {
			recordRenderCall(() -> shaderLineWidth = width);
		} else {
			shaderLineWidth = width;
		}
	}

	public static float getShaderLineWidth() {
		assertOnRenderThread();
		return shaderLineWidth;
	}

	public static void pixelStore(int pname, int param) {
		assertOnGameThreadOrInit();
		GlStateManager._pixelStore(pname, param);
	}

	public static void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
		assertOnRenderThread();
		GlStateManager._readPixels(x, y, width, height, format, type, pixels);
	}

	public static void getString(int name, Consumer<String> consumer) {
		assertOnRenderThread();
		consumer.accept(GlStateManager._getString(name));
	}

	public static String getBackendDescription() {
		assertInInitPhase();
		return String.format(Locale.ROOT, "LWJGL version %s", GLX._getLWJGLVersion());
	}

	public static String getApiDescription() {
		return apiDescription;
	}

	public static TimeSupplier.Nanoseconds initBackendSystem() {
		assertInInitPhase();
		return GLX._initGlfw()::getAsLong;
	}

	public static void initRenderer(int debugVerbosity, boolean debugSync) {
		assertInInitPhase();
		GLX._init(debugVerbosity, debugSync);
		apiDescription = GLX.getOpenGLVersionString();
	}

	public static void setErrorCallback(GLFWErrorCallbackI callback) {
		assertInInitPhase();
		GLX._setGlfwErrorCallback(callback);
	}

	public static void renderCrosshair(int size) {
		assertOnRenderThread();
		GLX._renderCrosshair(size, true, true, true);
	}

	public static String getCapsString() {
		assertOnRenderThread();
		return "Using framebuffer using OpenGL 3.2";
	}

	public static void setupDefaultState(int x, int y, int width, int height) {
		assertInInitPhase();
		GlStateManager._enableTexture();
		GlStateManager._clearDepth(1.0);
		GlStateManager._enableDepthTest();
		GlStateManager._depthFunc(515);
		projectionMatrix.loadIdentity();
		savedProjectionMatrix.loadIdentity();
		modelViewMatrix.loadIdentity();
		textureMatrix.loadIdentity();
		GlStateManager._viewport(x, y, width, height);
	}

	public static int maxSupportedTextureSize() {
		if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
			assertOnRenderThreadOrInit();
			int i = GlStateManager._getInteger(GL11.GL_MAX_TEXTURE_SIZE);

			for (int j = Math.max(32768, i); j >= 1024; j >>= 1) {
				GlStateManager._texImage2D(GlConst.GL_PROXY_TEXTURE_2D, 0, GlConst.GL_RGBA, j, j, 0, GlConst.GL_RGBA, GlConst.GL_UNSIGNED_BYTE, null);
				int k = GlStateManager._getTexLevelParameter(32868, 0, 4096);
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

	public static void glBindBuffer(int target, IntSupplier bufferSupplier) {
		GlStateManager._glBindBuffer(target, bufferSupplier.getAsInt());
	}

	public static void glBindVertexArray(Supplier<Integer> arraySupplier) {
		GlStateManager._glBindVertexArray((Integer)arraySupplier.get());
	}

	public static void glBufferData(int target, ByteBuffer data, int usage) {
		assertOnRenderThreadOrInit();
		GlStateManager._glBufferData(target, data, usage);
	}

	public static void glDeleteBuffers(int buffer) {
		assertOnRenderThread();
		GlStateManager._glDeleteBuffers(buffer);
	}

	public static void glDeleteVertexArrays(int array) {
		assertOnRenderThread();
		GlStateManager._glDeleteVertexArrays(array);
	}

	public static void glUniform1i(int location, int value) {
		assertOnRenderThread();
		GlStateManager._glUniform1i(location, value);
	}

	public static void glUniform1(int location, IntBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniform1(location, value);
	}

	public static void glUniform2(int location, IntBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniform2(location, value);
	}

	public static void glUniform3(int location, IntBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniform3(location, value);
	}

	public static void glUniform4(int location, IntBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniform4(location, value);
	}

	public static void glUniform1(int location, FloatBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniform1(location, value);
	}

	public static void glUniform2(int location, FloatBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniform2(location, value);
	}

	public static void glUniform3(int location, FloatBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniform3(location, value);
	}

	public static void glUniform4(int location, FloatBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniform4(location, value);
	}

	public static void glUniformMatrix2(int location, boolean transpose, FloatBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniformMatrix2(location, transpose, value);
	}

	public static void glUniformMatrix3(int location, boolean transpose, FloatBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniformMatrix3(location, transpose, value);
	}

	public static void glUniformMatrix4(int location, boolean transpose, FloatBuffer value) {
		assertOnRenderThread();
		GlStateManager._glUniformMatrix4(location, transpose, value);
	}

	public static void setupOverlayColor(IntSupplier texture, int size) {
		assertOnRenderThread();
		int i = texture.getAsInt();
		setShaderTexture(1, i);
	}

	public static void teardownOverlayColor() {
		assertOnRenderThread();
		setShaderTexture(1, 0);
	}

	public static void setupLevelDiffuseLighting(Vec3f vec3f, Vec3f vec3f2, Matrix4f matrix4f) {
		assertOnRenderThread();
		GlStateManager.setupLevelDiffuseLighting(vec3f, vec3f2, matrix4f);
	}

	public static void setupGuiFlatDiffuseLighting(Vec3f vec3f, Vec3f vec3f2) {
		assertOnRenderThread();
		GlStateManager.setupGuiFlatDiffuseLighting(vec3f, vec3f2);
	}

	public static void setupGui3DDiffuseLighting(Vec3f vec3f, Vec3f vec3f2) {
		assertOnRenderThread();
		GlStateManager.setupGui3DDiffuseLighting(vec3f, vec3f2);
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
			recordRenderCall(() -> consumer.accept(GlStateManager._glGenBuffers()));
		} else {
			consumer.accept(GlStateManager._glGenBuffers());
		}
	}

	public static void glGenVertexArrays(Consumer<Integer> consumer) {
		if (!isOnRenderThread()) {
			recordRenderCall(() -> consumer.accept(GlStateManager._glGenVertexArrays()));
		} else {
			consumer.accept(GlStateManager._glGenVertexArrays());
		}
	}

	public static Tessellator renderThreadTesselator() {
		assertOnRenderThread();
		return RENDER_THREAD_TESSELATOR;
	}

	public static void defaultBlendFunc() {
		blendFuncSeparate(
			GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
		);
	}

	@Deprecated
	public static void runAsFancy(Runnable runnable) {
		boolean bl = MinecraftClient.isFabulousGraphicsOrBetter();
		if (!bl) {
			runnable.run();
		} else {
			SimpleOption<GraphicsMode> simpleOption = MinecraftClient.getInstance().options.getGraphicsMode();
			GraphicsMode graphicsMode = simpleOption.getValue();
			simpleOption.setValue(GraphicsMode.FANCY);
			runnable.run();
			simpleOption.setValue(graphicsMode);
		}
	}

	public static void setShader(Supplier<Shader> shaderSupplier) {
		if (!isOnRenderThread()) {
			recordRenderCall(() -> shader = (Shader)shaderSupplier.get());
		} else {
			shader = (Shader)shaderSupplier.get();
		}
	}

	@Nullable
	public static Shader getShader() {
		assertOnRenderThread();
		return shader;
	}

	public static int getTextureId(int texture) {
		return GlStateManager._getTextureId(texture);
	}

	public static void setShaderTexture(int texture, Identifier id) {
		if (!isOnRenderThread()) {
			recordRenderCall(() -> _setShaderTexture(texture, id));
		} else {
			_setShaderTexture(texture, id);
		}
	}

	public static void _setShaderTexture(int texture, Identifier id) {
		if (texture >= 0 && texture < shaderTextures.length) {
			TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
			AbstractTexture abstractTexture = textureManager.getTexture(id);
			shaderTextures[texture] = abstractTexture.getGlId();
		}
	}

	public static void setShaderTexture(int texture, int glId) {
		if (!isOnRenderThread()) {
			recordRenderCall(() -> _setShaderTexture(texture, glId));
		} else {
			_setShaderTexture(texture, glId);
		}
	}

	public static void _setShaderTexture(int texture, int glId) {
		if (texture >= 0 && texture < shaderTextures.length) {
			shaderTextures[texture] = glId;
		}
	}

	public static int getShaderTexture(int texture) {
		assertOnRenderThread();
		return texture >= 0 && texture < shaderTextures.length ? shaderTextures[texture] : 0;
	}

	public static void setProjectionMatrix(Matrix4f projectionMatrix) {
		Matrix4f matrix4f = projectionMatrix.copy();
		if (!isOnRenderThread()) {
			recordRenderCall(() -> projectionMatrix = matrix4f);
		} else {
			RenderSystem.projectionMatrix = matrix4f;
		}
	}

	public static void setInverseViewRotationMatrix(Matrix3f inverseViewRotationMatrix) {
		Matrix3f matrix3f = inverseViewRotationMatrix.copy();
		if (!isOnRenderThread()) {
			recordRenderCall(() -> inverseViewRotationMatrix = matrix3f);
		} else {
			RenderSystem.inverseViewRotationMatrix = matrix3f;
		}
	}

	public static void setTextureMatrix(Matrix4f textureMatrix) {
		Matrix4f matrix4f = textureMatrix.copy();
		if (!isOnRenderThread()) {
			recordRenderCall(() -> textureMatrix = matrix4f);
		} else {
			RenderSystem.textureMatrix = matrix4f;
		}
	}

	public static void resetTextureMatrix() {
		if (!isOnRenderThread()) {
			recordRenderCall(() -> textureMatrix.loadIdentity());
		} else {
			textureMatrix.loadIdentity();
		}
	}

	public static void applyModelViewMatrix() {
		Matrix4f matrix4f = modelViewStack.peek().getPositionMatrix().copy();
		if (!isOnRenderThread()) {
			recordRenderCall(() -> modelViewMatrix = matrix4f);
		} else {
			modelViewMatrix = matrix4f;
		}
	}

	public static void backupProjectionMatrix() {
		if (!isOnRenderThread()) {
			recordRenderCall(() -> _backupProjectionMatrix());
		} else {
			_backupProjectionMatrix();
		}
	}

	private static void _backupProjectionMatrix() {
		savedProjectionMatrix = projectionMatrix;
	}

	public static void restoreProjectionMatrix() {
		if (!isOnRenderThread()) {
			recordRenderCall(() -> _restoreProjectionMatrix());
		} else {
			_restoreProjectionMatrix();
		}
	}

	private static void _restoreProjectionMatrix() {
		projectionMatrix = savedProjectionMatrix;
	}

	public static Matrix4f getProjectionMatrix() {
		assertOnRenderThread();
		return projectionMatrix;
	}

	public static Matrix3f getInverseViewRotationMatrix() {
		assertOnRenderThread();
		return inverseViewRotationMatrix;
	}

	public static Matrix4f getModelViewMatrix() {
		assertOnRenderThread();
		return modelViewMatrix;
	}

	public static MatrixStack getModelViewStack() {
		return modelViewStack;
	}

	public static Matrix4f getTextureMatrix() {
		assertOnRenderThread();
		return textureMatrix;
	}

	public static RenderSystem.IndexBuffer getSequentialBuffer(VertexFormat.DrawMode drawMode) {
		assertOnRenderThread();

		return switch (drawMode) {
			case QUADS -> sharedSequentialQuad;
			case LINES -> sharedSequentialLines;
			default -> sharedSequential;
		};
	}

	public static void setShaderGameTime(long time, float tickDelta) {
		float f = ((float)(time % 24000L) + tickDelta) / 24000.0F;
		if (!isOnRenderThread()) {
			recordRenderCall(() -> shaderGameTime = f);
		} else {
			shaderGameTime = f;
		}
	}

	public static float getShaderGameTime() {
		assertOnRenderThread();
		return shaderGameTime;
	}

	static {
		projectionMatrix.loadIdentity();
		savedProjectionMatrix.loadIdentity();
		modelViewMatrix.loadIdentity();
		textureMatrix.loadIdentity();
	}

	@Environment(EnvType.CLIENT)
	public static final class IndexBuffer {
		private final int sizeMultiplier;
		private final int increment;
		private final RenderSystem.IndexBuffer.IndexMapper indexMapper;
		private int id;
		private VertexFormat.IndexType indexType = VertexFormat.IndexType.BYTE;
		private int size;

		IndexBuffer(int sizeMultiplier, int increment, RenderSystem.IndexBuffer.IndexMapper indexMapper) {
			this.sizeMultiplier = sizeMultiplier;
			this.increment = increment;
			this.indexMapper = indexMapper;
		}

		public boolean isSizeLessThanOrEqual(int size) {
			return size <= this.size;
		}

		public void bindAndGrow(int newSize) {
			if (this.id == 0) {
				this.id = GlStateManager._glGenBuffers();
			}

			GlStateManager._glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, this.id);
			this.grow(newSize);
		}

		private void grow(int newSize) {
			if (!this.isSizeLessThanOrEqual(newSize)) {
				newSize = MathHelper.roundUpToMultiple(newSize * 2, this.increment);
				RenderSystem.LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.size, newSize);
				VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor(newSize);
				int i = MathHelper.roundUpToMultiple(newSize * indexType.size, 4);
				GlStateManager._glBufferData(GlConst.GL_ELEMENT_ARRAY_BUFFER, (long)i, GlConst.GL_DYNAMIC_DRAW);
				ByteBuffer byteBuffer = GlStateManager.mapBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, GlConst.GL_WRITE_ONLY);
				if (byteBuffer == null) {
					throw new RuntimeException("Failed to map GL buffer");
				} else {
					this.indexType = indexType;
					it.unimi.dsi.fastutil.ints.IntConsumer intConsumer = this.getIndexConsumer(byteBuffer);

					for (int j = 0; j < newSize; j += this.increment) {
						this.indexMapper.accept(intConsumer, j * this.sizeMultiplier / this.increment);
					}

					GlStateManager._glUnmapBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER);
					this.size = newSize;
				}
			}
		}

		private it.unimi.dsi.fastutil.ints.IntConsumer getIndexConsumer(ByteBuffer indicesBuffer) {
			switch (this.indexType) {
				case BYTE:
					return index -> indicesBuffer.put((byte)index);
				case SHORT:
					return index -> indicesBuffer.putShort((short)index);
				case INT:
				default:
					return indicesBuffer::putInt;
			}
		}

		public VertexFormat.IndexType getIndexType() {
			return this.indexType;
		}

		@Environment(EnvType.CLIENT)
		interface IndexMapper {
			void accept(it.unimi.dsi.fastutil.ints.IntConsumer indexConsumer, int vertexCount);
		}
	}
}
