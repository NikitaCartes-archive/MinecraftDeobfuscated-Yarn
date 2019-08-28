package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallbackI;

@Environment(EnvType.CLIENT)
public class RenderSystem {
	private static final ConcurrentLinkedQueue<Runnable> recordingQueue = Queues.newConcurrentLinkedQueue();
	private static Thread clientThread;
	private static Thread renderThread;

	public static void initClientThread() {
		if (clientThread == null && renderThread != Thread.currentThread()) {
			clientThread = Thread.currentThread();
		} else {
			throw new IllegalStateException("Could not initialize tick thread");
		}
	}

	public static boolean isOnClientThread() {
		return clientThread == Thread.currentThread();
	}

	public static void initRenderThread() {
		if (renderThread == null && clientThread != Thread.currentThread()) {
			renderThread = Thread.currentThread();
		} else {
			throw new IllegalStateException("Could not initialize render thread");
		}
	}

	public static boolean isOnRenderThread() {
		return renderThread == Thread.currentThread();
	}

	public static void pushLightingAttributes() {
		GlStateManager.pushLightingAttributes();
	}

	public static void pushTextureAttributes() {
		GlStateManager.pushTextureAttributes();
	}

	public static void popAttributes() {
		GlStateManager.popAttributes();
	}

	public static void disableAlphaTest() {
		GlStateManager.disableAlphaTest();
	}

	public static void enableAlphaTest() {
		GlStateManager.enableAlphaTest();
	}

	public static void alphaFunc(int i, float f) {
		GlStateManager.alphaFunc(i, f);
	}

	public static void enableLighting() {
		GlStateManager.enableLighting();
	}

	public static void disableLighting() {
		GlStateManager.disableLighting();
	}

	public static void enableLight(int i) {
		GlStateManager.enableLight(i);
	}

	public static void disableLight(int i) {
		GlStateManager.disableLight(i);
	}

	public static void enableColorMaterial() {
		GlStateManager.enableColorMaterial();
	}

	public static void disableColorMaterial() {
		GlStateManager.disableColorMaterial();
	}

	public static void colorMaterial(int i, int j) {
		GlStateManager.colorMaterial(i, j);
	}

	public static void light(int i, int j, FloatBuffer floatBuffer) {
		GlStateManager.light(i, j, floatBuffer);
	}

	public static void lightModel(int i, FloatBuffer floatBuffer) {
		GlStateManager.lightModel(i, floatBuffer);
	}

	public static void normal3f(float f, float g, float h) {
		GlStateManager.normal3f(f, g, h);
	}

	public static void disableDepthTest() {
		GlStateManager.disableDepthTest();
	}

	public static void enableDepthTest() {
		GlStateManager.enableDepthTest();
	}

	public static void depthFunc(int i) {
		GlStateManager.depthFunc(i);
	}

	public static void depthMask(boolean bl) {
		GlStateManager.depthMask(bl);
	}

	public static void enableBlend() {
		GlStateManager.enableBlend();
	}

	public static void disableBlend() {
		GlStateManager.disableBlend();
	}

	public static void blendFunc(GlStateManager.class_4535 arg, GlStateManager.class_4534 arg2) {
		GlStateManager.blendFunc(arg.value, arg2.value);
	}

	public static void blendFunc(int i, int j) {
		GlStateManager.blendFunc(i, j);
	}

	public static void blendFuncSeparate(
		GlStateManager.class_4535 arg, GlStateManager.class_4534 arg2, GlStateManager.class_4535 arg3, GlStateManager.class_4534 arg4
	) {
		GlStateManager.blendFuncSeparate(arg.value, arg2.value, arg3.value, arg4.value);
	}

	public static void blendFuncSeparate(int i, int j, int k, int l) {
		GlStateManager.blendFuncSeparate(i, j, k, l);
	}

	public static void blendEquation(int i) {
		GlStateManager.blendEquation(i);
	}

	public static void setupSolidRenderingTextureCombine(int i) {
		GlStateManager.setupSolidRenderingTextureCombine(i);
	}

	public static void tearDownSolidRenderingTextureCombine() {
		GlStateManager.tearDownSolidRenderingTextureCombine();
	}

	public static void enableFog() {
		GlStateManager.enableFog();
	}

	public static void disableFog() {
		GlStateManager.disableFog();
	}

	public static void fogMode(GlStateManager.FogMode fogMode) {
		GlStateManager.fogMode(fogMode.glValue);
	}

	public static void fogMode(int i) {
		GlStateManager.fogMode(i);
	}

	public static void fogDensity(float f) {
		GlStateManager.fogDensity(f);
	}

	public static void fogStart(float f) {
		GlStateManager.fogStart(f);
	}

	public static void fogEnd(float f) {
		GlStateManager.fogEnd(f);
	}

	public static void fog(int i, FloatBuffer floatBuffer) {
		GlStateManager.fog(i, floatBuffer);
	}

	public static void fogi(int i, int j) {
		GlStateManager.fogi(i, j);
	}

	public static void enableCull() {
		GlStateManager.enableCull();
	}

	public static void disableCull() {
		GlStateManager.disableCull();
	}

	public static void cullFace(GlStateManager.FaceSides faceSides) {
		GlStateManager.cullFace(faceSides.glValue);
	}

	public static void cullFace(int i) {
		GlStateManager.cullFace(i);
	}

	public static void polygonMode(int i, int j) {
		GlStateManager.polygonMode(i, j);
	}

	public static void enablePolygonOffset() {
		GlStateManager.enablePolygonOffset();
	}

	public static void disablePolygonOffset() {
		GlStateManager.disablePolygonOffset();
	}

	public static void enableLineOffset() {
		GlStateManager.enableLineOffset();
	}

	public static void disableLineOffset() {
		GlStateManager.disableLineOffset();
	}

	public static void polygonOffset(float f, float g) {
		GlStateManager.polygonOffset(f, g);
	}

	public static void enableColorLogicOp() {
		GlStateManager.enableColorLogicOp();
	}

	public static void disableColorLogicOp() {
		GlStateManager.disableColorLogicOp();
	}

	public static void logicOp(GlStateManager.LogicOp logicOp) {
		GlStateManager.logicOp(logicOp.glValue);
	}

	public static void logicOp(int i) {
		GlStateManager.logicOp(i);
	}

	public static void enableTexGen(GlStateManager.TexCoord texCoord) {
		GlStateManager.enableTexGen(texCoord);
	}

	public static void disableTexGen(GlStateManager.TexCoord texCoord) {
		GlStateManager.disableTexGen(texCoord);
	}

	public static void texGenMode(GlStateManager.TexCoord texCoord, int i) {
		GlStateManager.texGenMode(texCoord, i);
	}

	public static void texGenParam(GlStateManager.TexCoord texCoord, int i, FloatBuffer floatBuffer) {
		GlStateManager.texGenParam(texCoord, i, floatBuffer);
	}

	public static void activeTexture(int i) {
		GlStateManager.activeTexture(i);
	}

	public static void enableTexture() {
		GlStateManager.enableTexture();
	}

	public static void disableTexture() {
		GlStateManager.disableTexture();
	}

	public static void texEnv(int i, int j, FloatBuffer floatBuffer) {
		GlStateManager.texEnv(i, j, floatBuffer);
	}

	public static void texEnv(int i, int j, int k) {
		GlStateManager.texEnv(i, j, k);
	}

	public static void texEnv(int i, int j, float f) {
		GlStateManager.texEnv(i, j, f);
	}

	public static void texParameter(int i, int j, float f) {
		GlStateManager.texParameter(i, j, f);
	}

	public static void texParameter(int i, int j, int k) {
		GlStateManager.texParameter(i, j, k);
	}

	public static int getTexLevelParameter(int i, int j, int k) {
		return GlStateManager.getTexLevelParameter(i, j, k);
	}

	public static int genTexture() {
		return GlStateManager.getTexLevelParameter();
	}

	public static void deleteTexture(int i) {
		GlStateManager.deleteTexture(i);
	}

	public static void bindTexture(int i) {
		GlStateManager.bindTexture(i);
	}

	public static void texImage2D(int i, int j, int k, int l, int m, int n, int o, int p, @Nullable IntBuffer intBuffer) {
		GlStateManager.texImage2D(i, j, k, l, m, n, o, p, intBuffer);
	}

	public static void texSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p, long q) {
		GlStateManager.texSubImage2D(i, j, k, l, m, n, o, p, q);
	}

	public static void copyTexSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p) {
		GlStateManager.copyTexSubImage2D(i, j, k, l, m, n, o, p);
	}

	public static void getTexImage(int i, int j, int k, int l, long m) {
		GlStateManager.getTexImage(i, j, k, l, m);
	}

	public static void enableNormalize() {
		GlStateManager.enableNormalize();
	}

	public static void disableNormalize() {
		GlStateManager.disableNormalize();
	}

	public static void shadeModel(int i) {
		GlStateManager.shadeModel(i);
	}

	public static void enableRescaleNormal() {
		GlStateManager.enableRescaleNormal();
	}

	public static void disableRescaleNormal() {
		GlStateManager.disableRescaleNormal();
	}

	public static void viewport(int i, int j, int k, int l) {
		GlStateManager.viewport(i, j, k, l);
	}

	public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		GlStateManager.colorMask(bl, bl2, bl3, bl4);
	}

	public static void stencilFunc(int i, int j, int k) {
		GlStateManager.stencilFunc(i, j, k);
	}

	public static void stencilMask(int i) {
		GlStateManager.stencilMask(i);
	}

	public static void stencilOp(int i, int j, int k) {
		GlStateManager.stencilOp(i, j, k);
	}

	public static void clearDepth(double d) {
		GlStateManager.clearDepth(d);
	}

	public static void clearColor(float f, float g, float h, float i) {
		GlStateManager.clearColor(f, g, h, i);
	}

	public static void clearStencil(int i) {
		GlStateManager.clearStencil(i);
	}

	public static void clear(int i, boolean bl) {
		GlStateManager.clear(i, bl);
	}

	public static void matrixMode(int i) {
		GlStateManager.matrixMode(i);
	}

	public static void loadIdentity() {
		GlStateManager.loadIdentity();
	}

	public static void pushMatrix() {
		GlStateManager.pushMatrix();
	}

	public static void popMatrix() {
		GlStateManager.popMatrix();
	}

	public static void getMatrix(int i, FloatBuffer floatBuffer) {
		GlStateManager.getMatrix(i, floatBuffer);
	}

	public static Matrix4f getMatrix4f(int i) {
		return GlStateManager.getMatrix4f(i);
	}

	public static void ortho(double d, double e, double f, double g, double h, double i) {
		GlStateManager.ortho(d, e, f, g, h, i);
	}

	public static void rotatef(float f, float g, float h, float i) {
		GlStateManager.rotatef(f, g, h, i);
	}

	public static void rotated(double d, double e, double f, double g) {
		GlStateManager.rotated(d, e, f, g);
	}

	public static void scalef(float f, float g, float h) {
		GlStateManager.scalef(f, g, h);
	}

	public static void scaled(double d, double e, double f) {
		GlStateManager.scaled(d, e, f);
	}

	public static void translatef(float f, float g, float h) {
		GlStateManager.translatef(f, g, h);
	}

	public static void translated(double d, double e, double f) {
		GlStateManager.translated(d, e, f);
	}

	public static void multMatrix(FloatBuffer floatBuffer) {
		GlStateManager.multMatrix(floatBuffer);
	}

	public static void multMatrix(Matrix4f matrix4f) {
		GlStateManager.multMatrix(matrix4f);
	}

	public static void color4f(float f, float g, float h, float i) {
		GlStateManager.color4f(f, g, h, i);
	}

	public static void color3f(float f, float g, float h) {
		GlStateManager.color4f(f, g, h, 1.0F);
	}

	public static void texCoord2f(float f, float g) {
		GlStateManager.texCoord2f(f, g);
	}

	public static void vertex3f(float f, float g, float h) {
		GlStateManager.vertex3f(f, g, h);
	}

	public static void clearCurrentColor() {
		GlStateManager.clearCurrentColor();
	}

	public static void normalPointer(int i, int j, int k) {
		GlStateManager.normalPointer(i, j, k);
	}

	public static void normalPointer(int i, int j, ByteBuffer byteBuffer) {
		GlStateManager.normalPointer(i, j, byteBuffer);
	}

	public static void texCoordPointer(int i, int j, int k, int l) {
		GlStateManager.texCoordPointer(i, j, k, l);
	}

	public static void texCoordPointer(int i, int j, int k, ByteBuffer byteBuffer) {
		GlStateManager.texCoordPointer(i, j, k, byteBuffer);
	}

	public static void vertexPointer(int i, int j, int k, int l) {
		GlStateManager.vertexPointer(i, j, k, l);
	}

	public static void vertexPointer(int i, int j, int k, ByteBuffer byteBuffer) {
		GlStateManager.vertexPointer(i, j, k, byteBuffer);
	}

	public static void colorPointer(int i, int j, int k, int l) {
		GlStateManager.colorPointer(i, j, k, l);
	}

	public static void colorPointer(int i, int j, int k, ByteBuffer byteBuffer) {
		GlStateManager.colorPointer(i, j, k, byteBuffer);
	}

	public static void disableClientState(int i) {
		GlStateManager.disableClientState(i);
	}

	public static void enableClientState(int i) {
		GlStateManager.enableClientState(i);
	}

	public static void begin(int i) {
		GlStateManager.begin(i);
	}

	public static void end() {
		GlStateManager.end();
	}

	public static void drawArrays(int i, int j, int k) {
		GlStateManager.drawArrays(i, j, k);
	}

	public static void lineWidth(float f) {
		GlStateManager.lineWidth(f);
	}

	public static void callList(int i) {
		GlStateManager.callList(i);
	}

	public static void deleteLists(int i, int j) {
		GlStateManager.deleteLists(i, j);
	}

	public static void newList(int i, int j) {
		GlStateManager.newList(i, j);
	}

	public static void endList() {
		GlStateManager.endList();
	}

	public static int genLists(int i) {
		return GlStateManager.genLists(i);
	}

	public static void pixelStore(int i, int j) {
		GlStateManager.pixelStore(i, j);
	}

	public static void pixelTransfer(int i, float f) {
		GlStateManager.pixelTransfer(i, f);
	}

	public static void readPixels(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
		GlStateManager.readPixels(i, j, k, l, m, n, byteBuffer);
	}

	public static void readPixels(int i, int j, int k, int l, int m, int n, long o) {
		GlStateManager.readPixels(i, j, k, l, m, n, o);
	}

	public static int getError() {
		return GlStateManager.getError();
	}

	public static String getString(int i) {
		return GlStateManager.getString(i);
	}

	public static void getInteger(int i, IntBuffer intBuffer) {
		GlStateManager.getInteger(i, intBuffer);
	}

	public static int getInteger(int i) {
		return GlStateManager.getInteger(i);
	}

	public static String getBackendDescription() {
		return String.format("LWJGL version %s", GLX._getLWJGLVersion());
	}

	public static String getApiDescription() {
		return GLX.getOpenGLVersionString();
	}

	public static LongSupplier initBackendSystem() {
		return GLX._initGlfw();
	}

	public static void initRenderer(int i, boolean bl) {
		GLX._init(i, bl);
	}

	public static void setErrorCallback(GLFWErrorCallbackI gLFWErrorCallbackI) {
		GLX._setGlfwErrorCallback(gLFWErrorCallbackI);
	}

	public static void pollEvents() {
		GLX._pollEvents();
	}

	public static void glClientActiveTexture(int i) {
		GlStateManager.clientActiveTexture(i);
	}

	public static void renderCrosshair(int i) {
		GLX._renderCrosshair(i, true, true, true);
	}

	public static void setupNvFogDistance() {
		GLX._setupNvFogDistance();
	}

	public static void glMultiTexCoord2f(int i, float f, float g) {
		GlStateManager.multiTexCoords2f(i, f, g);
	}

	public static String getCapsString() {
		return GLX._getCapsString();
	}
}
