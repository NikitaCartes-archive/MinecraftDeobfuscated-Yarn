package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
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
		class_4493.method_21935();
	}

	public static void pushTextureAttributes() {
		class_4493.method_21976();
	}

	public static void popAttributes() {
		class_4493.method_21997();
	}

	public static void disableAlphaTest() {
		class_4493.method_22012();
	}

	public static void enableAlphaTest() {
		class_4493.method_22021();
	}

	public static void alphaFunc(int i, float f) {
		class_4493.method_21945(i, f);
	}

	public static void enableLighting() {
		class_4493.method_22028();
	}

	public static void disableLighting() {
		class_4493.method_22034();
	}

	public static void enableLight(int i) {
		class_4493.method_21944(i);
	}

	public static void disableLight(int i) {
		class_4493.method_21982(i);
	}

	public static void enableColorMaterial() {
		class_4493.method_22040();
	}

	public static void disableColorMaterial() {
		class_4493.method_22044();
	}

	public static void colorMaterial(int i, int j) {
		class_4493.method_21947(i, j);
	}

	public static void light(int i, int j, FloatBuffer floatBuffer) {
		class_4493.method_21960(i, j, floatBuffer);
	}

	public static void lightModel(int i, FloatBuffer floatBuffer) {
		class_4493.method_21963(i, floatBuffer);
	}

	public static void normal3f(float f, float g, float h) {
		class_4493.method_21942(f, g, h);
	}

	public static void disableDepthTest() {
		class_4493.method_22047();
	}

	public static void enableDepthTest() {
		class_4493.method_22050();
	}

	public static void depthFunc(int i) {
		class_4493.method_22001(i);
	}

	public static void depthMask(boolean bl) {
		class_4493.method_21974(bl);
	}

	public static void enableBlend() {
		class_4493.method_22056();
	}

	public static void disableBlend() {
		class_4493.method_22053();
	}

	public static void blendFunc(class_4493.class_4535 arg, class_4493.class_4534 arg2) {
		class_4493.method_21984(arg.value, arg2.value);
	}

	public static void blendFunc(int i, int j) {
		class_4493.method_21984(i, j);
	}

	public static void blendFuncSeparate(class_4493.class_4535 arg, class_4493.class_4534 arg2, class_4493.class_4535 arg3, class_4493.class_4534 arg4) {
		class_4493.method_21950(arg.value, arg2.value, arg3.value, arg4.value);
	}

	public static void blendFuncSeparate(int i, int j, int k, int l) {
		class_4493.method_21950(i, j, k, l);
	}

	public static void blendEquation(int i) {
		class_4493.method_22015(i);
	}

	public static void setupSolidRenderingTextureCombine(int i) {
		class_4493.method_22022(i);
	}

	public static void tearDownSolidRenderingTextureCombine() {
		class_4493.method_22059();
	}

	public static void enableFog() {
		class_4493.method_22072();
	}

	public static void disableFog() {
		class_4493.method_22074();
	}

	public static void fogMode(class_4493.FogMode fogMode) {
		class_4493.method_22071(fogMode.glValue);
	}

	public static void fogMode(int i) {
		class_4493.method_22071(i);
	}

	public static void fogDensity(float f) {
		class_4493.method_21940(f);
	}

	public static void fogStart(float f) {
		class_4493.method_21978(f);
	}

	public static void fogEnd(float f) {
		class_4493.method_21998(f);
	}

	public static void fog(int i, FloatBuffer floatBuffer) {
		class_4493.method_22033(i, floatBuffer);
	}

	public static void fogi(int i, int j) {
		class_4493.method_22055(i, j);
	}

	public static void enableCull() {
		class_4493.method_22076();
	}

	public static void disableCull() {
		class_4493.method_22078();
	}

	public static void cullFace(class_4493.FaceSides faceSides) {
		class_4493.method_22073(faceSides.glValue);
	}

	public static void cullFace(int i) {
		class_4493.method_22073(i);
	}

	public static void polygonMode(int i, int j) {
		class_4493.method_22058(i, j);
	}

	public static void enablePolygonOffset() {
		class_4493.method_22080();
	}

	public static void disablePolygonOffset() {
		class_4493.method_22082();
	}

	public static void enableLineOffset() {
		class_4493.method_22084();
	}

	public static void disableLineOffset() {
		class_4493.method_22086();
	}

	public static void polygonOffset(float f, float g) {
		class_4493.method_21941(f, g);
	}

	public static void enableColorLogicOp() {
		class_4493.method_21906();
	}

	public static void disableColorLogicOp() {
		class_4493.method_21908();
	}

	public static void logicOp(class_4493.LogicOp logicOp) {
		class_4493.method_22075(logicOp.glValue);
	}

	public static void logicOp(int i) {
		class_4493.method_22075(i);
	}

	public static void enableTexGen(class_4493.TexCoord texCoord) {
		class_4493.method_21968(texCoord);
	}

	public static void disableTexGen(class_4493.TexCoord texCoord) {
		class_4493.method_21995(texCoord);
	}

	public static void texGenMode(class_4493.TexCoord texCoord, int i) {
		class_4493.method_21969(texCoord, i);
	}

	public static void texGenParam(class_4493.TexCoord texCoord, int i, FloatBuffer floatBuffer) {
		class_4493.method_21970(texCoord, i, floatBuffer);
	}

	public static void activeTexture(int i) {
		class_4493.method_22077(i);
	}

	public static void enableTexture() {
		class_4493.method_21910();
	}

	public static void disableTexture() {
		class_4493.method_21912();
	}

	public static void texEnv(int i, int j, FloatBuffer floatBuffer) {
		class_4493.method_21989(i, j, floatBuffer);
	}

	public static void texEnv(int i, int j, int k) {
		class_4493.method_21949(i, j, k);
	}

	public static void texEnv(int i, int j, float f) {
		class_4493.method_21948(i, j, f);
	}

	public static void texParameter(int i, int j, float f) {
		class_4493.method_21985(i, j, f);
	}

	public static void texParameter(int i, int j, int k) {
		class_4493.method_21986(i, j, k);
	}

	public static int getTexLevelParameter(int i, int j, int k) {
		return class_4493.method_22003(i, j, k);
	}

	public static int genTexture() {
		return class_4493.method_21914();
	}

	public static void deleteTexture(int i) {
		class_4493.method_22079(i);
	}

	public static void bindTexture(int i) {
		class_4493.method_22081(i);
	}

	public static void texImage2D(int i, int j, int k, int l, int m, int n, int o, int p, @Nullable IntBuffer intBuffer) {
		class_4493.method_21954(i, j, k, l, m, n, o, p, intBuffer);
	}

	public static void texSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p, long q) {
		class_4493.method_21953(i, j, k, l, m, n, o, p, q);
	}

	public static void copyTexSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p) {
		class_4493.method_21952(i, j, k, l, m, n, o, p);
	}

	public static void getTexImage(int i, int j, int k, int l, long m) {
		class_4493.method_21957(i, j, k, l, m);
	}

	public static void enableNormalize() {
		class_4493.method_21916();
	}

	public static void disableNormalize() {
		class_4493.method_21918();
	}

	public static void shadeModel(int i) {
		class_4493.method_22083(i);
	}

	public static void enableRescaleNormal() {
		class_4493.method_21920();
	}

	public static void disableRescaleNormal() {
		class_4493.method_21922();
	}

	public static void viewport(int i, int j, int k, int l) {
		class_4493.method_22025(i, j, k, l);
	}

	public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		class_4493.method_21975(bl, bl2, bl3, bl4);
	}

	public static void stencilFunc(int i, int j, int k) {
		class_4493.method_22017(i, j, k);
	}

	public static void stencilMask(int i) {
		class_4493.method_22085(i);
	}

	public static void stencilOp(int i, int j, int k) {
		class_4493.method_22024(i, j, k);
	}

	public static void clearDepth(double d) {
		class_4493.method_21936(d);
	}

	public static void clearColor(float f, float g, float h, float i) {
		class_4493.method_21943(f, g, h, i);
	}

	public static void clearStencil(int i) {
		class_4493.method_22087(i);
	}

	public static void clear(int i, boolean bl) {
		class_4493.method_21965(i, bl);
	}

	public static void matrixMode(int i) {
		class_4493.method_21907(i);
	}

	public static void loadIdentity() {
		class_4493.method_21924();
	}

	public static void pushMatrix() {
		class_4493.method_21926();
	}

	public static void popMatrix() {
		class_4493.method_21928();
	}

	public static void getMatrix(int i, FloatBuffer floatBuffer) {
		class_4493.method_22039(i, floatBuffer);
	}

	public static Matrix4f getMatrix4f(int i) {
		return class_4493.method_21909(i);
	}

	public static void ortho(double d, double e, double f, double g, double h, double i) {
		class_4493.method_21939(d, e, f, g, h, i);
	}

	public static void rotatef(float f, float g, float h, float i) {
		class_4493.method_21981(f, g, h, i);
	}

	public static void rotated(double d, double e, double f, double g) {
		class_4493.method_21938(d, e, f, g);
	}

	public static void scalef(float f, float g, float h) {
		class_4493.method_21980(f, g, h);
	}

	public static void scaled(double d, double e, double f) {
		class_4493.method_21937(d, e, f);
	}

	public static void translatef(float f, float g, float h) {
		class_4493.method_21999(f, g, h);
	}

	public static void translated(double d, double e, double f) {
		class_4493.method_21977(d, e, f);
	}

	public static void multMatrix(FloatBuffer floatBuffer) {
		class_4493.method_21972(floatBuffer);
	}

	public static void multMatrix(Matrix4f matrix4f) {
		class_4493.method_21971(matrix4f);
	}

	public static void color4f(float f, float g, float h, float i) {
		class_4493.method_22000(f, g, h, i);
	}

	public static void color3f(float f, float g, float h) {
		class_4493.method_22000(f, g, h, 1.0F);
	}

	public static void texCoord2f(float f, float g) {
		class_4493.method_21979(f, g);
	}

	public static void vertex3f(float f, float g, float h) {
		class_4493.method_22014(f, g, h);
	}

	public static void clearCurrentColor() {
		class_4493.method_21930();
	}

	public static void normalPointer(int i, int j, int k) {
		class_4493.method_22031(i, j, k);
	}

	public static void normalPointer(int i, int j, ByteBuffer byteBuffer) {
		class_4493.method_21959(i, j, byteBuffer);
	}

	public static void texCoordPointer(int i, int j, int k, int l) {
		class_4493.method_22032(i, j, k, l);
	}

	public static void texCoordPointer(int i, int j, int k, ByteBuffer byteBuffer) {
		class_4493.method_21958(i, j, k, byteBuffer);
	}

	public static void vertexPointer(int i, int j, int k, int l) {
		class_4493.method_22038(i, j, k, l);
	}

	public static void vertexPointer(int i, int j, int k, ByteBuffer byteBuffer) {
		class_4493.method_21988(i, j, k, byteBuffer);
	}

	public static void colorPointer(int i, int j, int k, int l) {
		class_4493.method_22043(i, j, k, l);
	}

	public static void colorPointer(int i, int j, int k, ByteBuffer byteBuffer) {
		class_4493.method_22005(i, j, k, byteBuffer);
	}

	public static void disableClientState(int i) {
		class_4493.method_21911(i);
	}

	public static void enableClientState(int i) {
		class_4493.method_21913(i);
	}

	public static void begin(int i) {
		class_4493.method_21915(i);
	}

	public static void end() {
		class_4493.method_21932();
	}

	public static void drawArrays(int i, int j, int k) {
		class_4493.method_22037(i, j, k);
	}

	public static void lineWidth(float f) {
		class_4493.method_22013(f);
	}

	public static void callList(int i) {
		class_4493.method_21917(i);
	}

	public static void deleteLists(int i, int j) {
		class_4493.method_22061(i, j);
	}

	public static void newList(int i, int j) {
		class_4493.method_22064(i, j);
	}

	public static void endList() {
		class_4493.method_21933();
	}

	public static int genLists(int i) {
		return class_4493.method_21919(i);
	}

	public static void pixelStore(int i, int j) {
		class_4493.method_22067(i, j);
	}

	public static void pixelTransfer(int i, float f) {
		class_4493.method_21983(i, f);
	}

	public static void readPixels(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
		class_4493.method_21956(i, j, k, l, m, n, byteBuffer);
	}

	public static void readPixels(int i, int j, int k, int l, int m, int n, long o) {
		class_4493.method_21955(i, j, k, l, m, n, o);
	}

	public static int getError() {
		return class_4493.method_21934();
	}

	public static String getString(int i) {
		return class_4493.method_21921(i);
	}

	public static void getInteger(int i, IntBuffer intBuffer) {
		class_4493.method_22027(i, intBuffer);
	}

	public static int getInteger(int i) {
		return class_4493.method_21923(i);
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
		class_4493.method_22069(i);
	}

	public static void renderCrosshair(int i) {
		GLX._renderCrosshair(i, true, true, true);
	}

	public static void setupNvFogDistance() {
		GLX._setupNvFogDistance();
	}

	public static void glMultiTexCoord2f(int i, float f, float g) {
		class_4493.method_21946(i, f, g);
	}

	public static String getCapsString() {
		return GLX._getCapsString();
	}
}
