/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderCall;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;

@Environment(value=EnvType.CLIENT)
public class RenderSystem {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConcurrentLinkedQueue<RenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
    private static final Tessellator RENDER_THREAD_TESSELATOR = new Tessellator();
    private static final float DEFAULTALPHACUTOFF = 0.1f;
    private static boolean isReplayingQueue;
    private static Thread gameThread;
    private static Thread renderThread;
    private static int MAX_SUPPORTED_TEXTURE_SIZE;
    private static boolean isInInit;

    public static void initRenderThread() {
        if (renderThread != null || gameThread == Thread.currentThread()) {
            throw new IllegalStateException("Could not initialize render thread");
        }
        renderThread = Thread.currentThread();
    }

    public static boolean isOnRenderThread() {
        return Thread.currentThread() == renderThread;
    }

    public static boolean isOnRenderThreadOrInit() {
        return isInInit || RenderSystem.isOnRenderThread();
    }

    public static void initGameThread(boolean bl) {
        boolean bl2;
        boolean bl3 = bl2 = renderThread == Thread.currentThread();
        if (gameThread != null || renderThread == null || bl2 == bl) {
            throw new IllegalStateException("Could not initialize tick thread");
        }
        gameThread = Thread.currentThread();
    }

    public static boolean isOnGameThread() {
        return true;
    }

    public static boolean isOnGameThreadOrInit() {
        return isInInit || RenderSystem.isOnGameThread();
    }

    public static void assertThread(Supplier<Boolean> supplier) {
        if (!supplier.get().booleanValue()) {
            throw new IllegalStateException("Rendersystem called from wrong thread");
        }
    }

    public static boolean isInInitPhase() {
        return true;
    }

    public static void pushLightingAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.pushLightingAttributes();
    }

    public static void pushTextureAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.pushTextureAttributes();
    }

    public static void popAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.popAttributes();
    }

    public static void disableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableAlphaTest();
    }

    public static void enableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableAlphaTest();
    }

    public static void alphaFunc(int i, float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.alphaFunc(i, f);
    }

    public static void enableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableLighting();
    }

    public static void disableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableLighting();
    }

    public static void enableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableColorMaterial();
    }

    public static void disableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableColorMaterial();
    }

    public static void colorMaterial(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.colorMaterial(i, j);
    }

    public static void normal3f(float f, float g, float h) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.normal3f(f, g, h);
    }

    public static void disableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableDepthTest();
    }

    public static void enableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.enableDepthTest();
    }

    public static void depthFunc(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.depthFunc(i);
    }

    public static void depthMask(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.depthMask(bl);
    }

    public static void enableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableBlend();
    }

    public static void disableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableBlend();
    }

    public static void blendFunc(GlStateManager.class_4535 arg, GlStateManager.class_4534 arg2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFunc(arg.value, arg2.value);
    }

    public static void blendFunc(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFunc(i, j);
    }

    public static void blendFuncSeparate(GlStateManager.class_4535 arg, GlStateManager.class_4534 arg2, GlStateManager.class_4535 arg3, GlStateManager.class_4534 arg4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFuncSeparate(arg.value, arg2.value, arg3.value, arg4.value);
    }

    public static void blendFuncSeparate(int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFuncSeparate(i, j, k, l);
    }

    public static void blendEquation(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendEquation(i);
    }

    public static void setupSolidRenderingTextureCombine(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupSolidRenderingTextureCombine(i);
    }

    public static void tearDownSolidRenderingTextureCombine() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.tearDownSolidRenderingTextureCombine();
    }

    public static void enableFog() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableFog();
    }

    public static void disableFog() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableFog();
    }

    public static void fogMode(GlStateManager.FogMode fogMode) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogMode(fogMode.glValue);
    }

    public static void fogMode(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogMode(i);
    }

    public static void fogDensity(float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogDensity(f);
    }

    public static void fogStart(float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogStart(f);
    }

    public static void fogEnd(float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogEnd(f);
    }

    public static void fog(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fog(i, floatBuffer);
    }

    public static void fogi(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogi(i, j);
    }

    public static void enableCull() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableCull();
    }

    public static void disableCull() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableCull();
    }

    public static void cullFace(GlStateManager.FaceSides faceSides) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.cullFace(faceSides.glValue);
    }

    public static void cullFace(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.cullFace(i);
    }

    public static void polygonMode(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.polygonMode(i, j);
    }

    public static void enablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enablePolygonOffset();
    }

    public static void disablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disablePolygonOffset();
    }

    public static void enableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableLineOffset();
    }

    public static void disableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableLineOffset();
    }

    public static void polygonOffset(float f, float g) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.polygonOffset(f, g);
    }

    public static void enableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableColorLogicOp();
    }

    public static void disableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableColorLogicOp();
    }

    public static void logicOp(GlStateManager.LogicOp logicOp) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.logicOp(logicOp.glValue);
    }

    public static void logicOp(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.logicOp(i);
    }

    public static void enableTexGen(GlStateManager.TexCoord texCoord) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableTexGen(texCoord);
    }

    public static void disableTexGen(GlStateManager.TexCoord texCoord) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableTexGen(texCoord);
    }

    public static void texGenMode(GlStateManager.TexCoord texCoord, int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.texGenMode(texCoord, i);
    }

    public static void texGenParam(GlStateManager.TexCoord texCoord, int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.texGenParam(texCoord, i, floatBuffer);
    }

    public static void activeTexture(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.activeTexture(i);
    }

    public static void enableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableTexture();
    }

    public static void disableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableTexture();
    }

    public static void texParameter(int i, int j, int k) {
        GlStateManager.texParameter(i, j, k);
    }

    public static void deleteTexture(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.deleteTexture(i);
    }

    public static void bindTexture(int i) {
        GlStateManager.bindTexture(i);
    }

    public static void texImage2D(int i, int j, int k, int l, int m, int n, int o, int p, @Nullable IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.texImage2D(i, j, k, l, m, n, o, p, intBuffer);
    }

    public static void texSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p, long q) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.texSubImage2D(i, j, k, l, m, n, o, p, q);
    }

    public static void copyTexSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.copyTexSubImage2D(i, j, k, l, m, n, o, p);
    }

    public static void getTexImage(int i, int j, int k, int l, long m) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.getTexImage(i, j, k, l, m);
    }

    public static void enableNormalize() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableNormalize();
    }

    public static void disableNormalize() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableNormalize();
    }

    public static void shadeModel(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.shadeModel(i);
    }

    public static void enableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableRescaleNormal();
    }

    public static void disableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableRescaleNormal();
    }

    public static void viewport(int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.viewport(i, j, k, l);
    }

    public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.colorMask(bl, bl2, bl3, bl4);
    }

    public static void stencilFunc(int i, int j, int k) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.stencilFunc(i, j, k);
    }

    public static void stencilMask(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.stencilMask(i);
    }

    public static void stencilOp(int i, int j, int k) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.stencilOp(i, j, k);
    }

    public static void clearDepth(double d) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.clearDepth(d);
    }

    public static void clearColor(float f, float g, float h, float i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.clearColor(f, g, h, i);
    }

    public static void clearStencil(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.clearStencil(i);
    }

    public static void clear(int i, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.clear(i, bl);
    }

    public static void matrixMode(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.matrixMode(i);
    }

    public static void loadIdentity() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.loadIdentity();
    }

    public static void pushMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.pushMatrix();
    }

    public static void popMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.popMatrix();
    }

    public static void getMatrix(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.getMatrix(i, floatBuffer);
    }

    public static void getMatrix4f(int i, Consumer<Matrix4f> consumer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        consumer.accept(GlStateManager.getMatrix4f(i));
    }

    public static void ortho(double d, double e, double f, double g, double h, double i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.ortho(d, e, f, g, h, i);
    }

    public static void rotatef(float f, float g, float h, float i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.rotatef(f, g, h, i);
    }

    public static void rotated(double d, double e, double f, double g) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.rotated(d, e, f, g);
    }

    public static void scalef(float f, float g, float h) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.scalef(f, g, h);
    }

    public static void scaled(double d, double e, double f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.scaled(d, e, f);
    }

    public static void translatef(float f, float g, float h) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.translatef(f, g, h);
    }

    public static void translated(double d, double e, double f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.translated(d, e, f);
    }

    public static void multMatrix(FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.multMatrix(floatBuffer);
    }

    public static void multMatrix(Matrix4f matrix4f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.multMatrix(matrix4f);
    }

    public static void color4f(float f, float g, float h, float i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.color4f(f, g, h, i);
    }

    public static void color3f(float f, float g, float h) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.color4f(f, g, h, 1.0f);
    }

    public static void clearCurrentColor() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.clearCurrentColor();
    }

    public static void drawArrays(int i, int j, int k) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.drawArrays(i, j, k);
    }

    public static void lineWidth(float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.lineWidth(f);
    }

    public static void pixelStore(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.pixelStore(i, j);
    }

    public static void pixelTransfer(int i, float f) {
        GlStateManager.pixelTransfer(i, f);
    }

    public static void readPixels(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.readPixels(i, j, k, l, m, n, byteBuffer);
    }

    public static void getError(Consumer<Integer> consumer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        consumer.accept(GlStateManager.getError());
    }

    public static void getString(int i, Consumer<String> consumer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        consumer.accept(GlStateManager.getString(i));
    }

    public static String getBackendDescription() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return String.format("LWJGL version %s", GLX._getLWJGLVersion());
    }

    public static String getApiDescription() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return GLX.getOpenGLVersionString();
    }

    public static LongSupplier initBackendSystem() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return GLX._initGlfw();
    }

    public static void initRenderer(int i, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLX._init(i, bl);
    }

    public static void setErrorCallback(GLFWErrorCallbackI gLFWErrorCallbackI) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLX._setGlfwErrorCallback(gLFWErrorCallbackI);
    }

    public static void pollEvents() {
    }

    public static void recordRenderCall(RenderCall renderCall) {
        recordingQueue.add(renderCall);
    }

    public static void glClientActiveTexture(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.clientActiveTexture(i);
    }

    public static void renderCrosshair(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GLX._renderCrosshair(i, true, true, true);
    }

    public static void setupNvFogDistance() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GLX._setupNvFogDistance();
    }

    public static void glMultiTexCoord2f(int i, float f, float g) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.multiTexCoords2f(i, f, g);
    }

    public static String getCapsString() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        return GLX._getCapsString();
    }

    public static void setupDefaultState(int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0);
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.cullFace(GlStateManager.FaceSides.BACK.glValue);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.viewport(i, j, k, l);
    }

    public static int maxSupportedTextureSize() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
            for (int i = 16384; i > 0; i >>= 1) {
                GlStateManager.texImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, null);
                int j = GlStateManager.getTexLevelParameter(32868, 0, 4096);
                if (j == 0) continue;
                MAX_SUPPORTED_TEXTURE_SIZE = i;
                return i;
            }
            MAX_SUPPORTED_TEXTURE_SIZE = MathHelper.clamp(GlStateManager.getInteger(3379), 1024, 16384);
            LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (Object)MAX_SUPPORTED_TEXTURE_SIZE);
        }
        return MAX_SUPPORTED_TEXTURE_SIZE;
    }

    public static void flipFrame() {
        GLFW.glfwPollEvents();
        isReplayingQueue = true;
        while (!recordingQueue.isEmpty()) {
            RenderCall renderCall = recordingQueue.poll();
            renderCall.execute();
        }
        isReplayingQueue = false;
        Tessellator.getInstance().getBufferBuilder().clear();
    }

    public static void glBindFramebuffer(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.bindFramebuffer(i, j);
    }

    public static void glDeleteRenderbuffers(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.deleteRenderbuffers(i);
    }

    public static void glDeleteFramebuffers(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.deleteFramebuffers(i);
    }

    public static void glFramebufferTexture2D(int i, int j, int k, int l, int m) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.framebufferTexture2D(i, j, k, l, m);
    }

    public static void glBindRenderbuffer(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.bindRenderbuffer(i, j);
    }

    public static void glRenderbufferStorage(int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.renderbufferStorage(i, j, k, l);
    }

    public static void glFramebufferRenderbuffer(int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.framebufferRenderbuffer(i, j, k, l);
    }

    public static void glBindBuffer(int i, Supplier<Integer> supplier) {
        GlStateManager.bindBuffers(i, supplier.get());
    }

    public static void glBufferData(int i, ByteBuffer byteBuffer, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bufferData(i, byteBuffer, j);
    }

    public static void glDeleteBuffers(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.deleteBuffers(i);
    }

    public static void glUniform1i(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform1(i, j);
    }

    public static void glUniform1(int i, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform1(i, intBuffer);
    }

    public static void glUniform2(int i, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform2(i, intBuffer);
    }

    public static void glUniform3(int i, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform3(i, intBuffer);
    }

    public static void glUniform4(int i, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform4(i, intBuffer);
    }

    public static void glUniform1(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform1(i, floatBuffer);
    }

    public static void glUniform2(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform2(i, floatBuffer);
    }

    public static void glUniform3(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform3(i, floatBuffer);
    }

    public static void glUniform4(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform4(i, floatBuffer);
    }

    public static void glUniformMatrix2(int i, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniformMatrix2(i, bl, floatBuffer);
    }

    public static void glUniformMatrix3(int i, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniformMatrix3(i, bl, floatBuffer);
    }

    public static void glUniformMatrix4(int i, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniformMatrix4(i, bl, floatBuffer);
    }

    public static void glUseProgram(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.useProgram(i);
    }

    public static void setupOverlayColor(int i, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22610(i, bl);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22614();
    }

    public static void enableUsualDiffuseLighting() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22616();
    }

    public static void enableGuiDiffuseLighting() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22617();
    }

    public static void disableDiffuseLighting() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22618();
    }

    public static void setProfile(class_4564 arg) {
        arg.begin();
    }

    public static void unsetProfile(class_4564 arg) {
        arg.end();
    }

    public static void beginInitialization() {
        isInInit = true;
    }

    public static void finishInitialization() {
        isInInit = false;
        if (!recordingQueue.isEmpty()) {
            RenderSystem.flipFrame();
        }
        if (!recordingQueue.isEmpty()) {
            throw new IllegalStateException("Recorded to render queue during initialization");
        }
    }

    public static void glGenBuffers(Consumer<Integer> consumer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> consumer.accept(GlStateManager.genBuffers()));
        } else {
            consumer.accept(GlStateManager.genBuffers());
        }
    }

    public static Tessellator renderThreadTesselator() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return RENDER_THREAD_TESSELATOR;
    }

    public static void defaultBlendFunc() {
        RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
    }

    public static void defaultAlphaFunc() {
        RenderSystem.alphaFunc(516, 0.1f);
    }

    private static void setupDefaultGlState() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22615();
    }

    private static /* synthetic */ void lambda$setupOverlayColor$92(int i, boolean bl) {
        GlStateManager.method_22610(i, bl);
    }

    private static /* synthetic */ void lambda$glUseProgram$91(int i) {
        GlStateManager.useProgram(i);
    }

    private static /* synthetic */ void lambda$glUniformMatrix4$90(int i, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager.uniformMatrix4(i, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniformMatrix3$89(int i, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager.uniformMatrix3(i, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniformMatrix2$88(int i, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager.uniformMatrix2(i, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform4$87(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform4(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform3$86(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform3(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform2$85(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform2(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1$84(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform1(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform4$83(int i, IntBuffer intBuffer) {
        GlStateManager.uniform4(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform3$82(int i, IntBuffer intBuffer) {
        GlStateManager.uniform3(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform2$81(int i, IntBuffer intBuffer) {
        GlStateManager.uniform2(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1$80(int i, IntBuffer intBuffer) {
        GlStateManager.uniform1(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1i$79(int i, int j) {
        GlStateManager.uniform1(i, j);
    }

    private static /* synthetic */ void lambda$glDeleteBuffers$78(int i) {
        GlStateManager.deleteBuffers(i);
    }

    private static /* synthetic */ void lambda$glBindBuffer$77(int i, Supplier supplier) {
        GlStateManager.bindBuffers(i, (Integer)supplier.get());
    }

    private static /* synthetic */ void lambda$glFramebufferRenderbuffer$76(int i, int j, int k, int l) {
        GlStateManager.framebufferRenderbuffer(i, j, k, l);
    }

    private static /* synthetic */ void lambda$glRenderbufferStorage$75(int i, int j, int k, int l) {
        GlStateManager.renderbufferStorage(i, j, k, l);
    }

    private static /* synthetic */ void lambda$glBindRenderbuffer$74(int i, int j) {
        GlStateManager.bindRenderbuffer(i, j);
    }

    private static /* synthetic */ void lambda$glFramebufferTexture2D$73(int i, int j, int k, int l, int m) {
        GlStateManager.framebufferTexture2D(i, j, k, l, m);
    }

    private static /* synthetic */ void lambda$glDeleteFramebuffers$72(int i) {
        GlStateManager.deleteFramebuffers(i);
    }

    private static /* synthetic */ void lambda$glDeleteRenderbuffers$71(int i) {
        GlStateManager.deleteRenderbuffers(i);
    }

    private static /* synthetic */ void lambda$glBindFramebuffer$70(int i, int j) {
        GlStateManager.bindFramebuffer(i, j);
    }

    private static /* synthetic */ void lambda$glMultiTexCoord2f$69(int i, float f, float g) {
        GlStateManager.multiTexCoords2f(i, f, g);
    }

    private static /* synthetic */ void lambda$renderCrosshair$68(int i) {
        GLX._renderCrosshair(i, true, true, true);
    }

    private static /* synthetic */ void lambda$glClientActiveTexture$67(int i) {
        GlStateManager.clientActiveTexture(i);
    }

    private static /* synthetic */ void lambda$getString$66(int i, Consumer consumer) {
        String string = GlStateManager.getString(i);
        consumer.accept(string);
    }

    private static /* synthetic */ void lambda$getError$65(Consumer consumer) {
        int i = GlStateManager.getError();
        consumer.accept(i);
    }

    private static /* synthetic */ void lambda$readPixels$64(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
        GlStateManager.readPixels(i, j, k, l, m, n, byteBuffer);
    }

    private static /* synthetic */ void lambda$pixelTransfer$63(int i, float f) {
        GlStateManager.pixelTransfer(i, f);
    }

    private static /* synthetic */ void lambda$pixelStore$62(int i, int j) {
        GlStateManager.pixelStore(i, j);
    }

    private static /* synthetic */ void lambda$lineWidth$61(float f) {
        GlStateManager.lineWidth(f);
    }

    private static /* synthetic */ void lambda$drawArrays$60(int i, int j, int k) {
        GlStateManager.drawArrays(i, j, k);
    }

    private static /* synthetic */ void lambda$color3f$59(float f, float g, float h) {
        GlStateManager.color4f(f, g, h, 1.0f);
    }

    private static /* synthetic */ void lambda$color4f$58(float f, float g, float h, float i) {
        GlStateManager.color4f(f, g, h, i);
    }

    private static /* synthetic */ void lambda$multMatrix$57(Matrix4f matrix4f) {
        GlStateManager.multMatrix(matrix4f);
    }

    private static /* synthetic */ void lambda$multMatrix$56(FloatBuffer floatBuffer) {
        GlStateManager.multMatrix(floatBuffer);
    }

    private static /* synthetic */ void lambda$translated$55(double d, double e, double f) {
        GlStateManager.translated(d, e, f);
    }

    private static /* synthetic */ void lambda$translatef$54(float f, float g, float h) {
        GlStateManager.translatef(f, g, h);
    }

    private static /* synthetic */ void lambda$scaled$53(double d, double e, double f) {
        GlStateManager.scaled(d, e, f);
    }

    private static /* synthetic */ void lambda$scalef$52(float f, float g, float h) {
        GlStateManager.scalef(f, g, h);
    }

    private static /* synthetic */ void lambda$rotated$51(double d, double e, double f, double g) {
        GlStateManager.rotated(d, e, f, g);
    }

    private static /* synthetic */ void lambda$rotatef$50(float f, float g, float h, float i) {
        GlStateManager.rotatef(f, g, h, i);
    }

    private static /* synthetic */ void lambda$ortho$49(double d, double e, double f, double g, double h, double i) {
        GlStateManager.ortho(d, e, f, g, h, i);
    }

    private static /* synthetic */ void lambda$getMatrix4f$48(int i, Consumer consumer) {
        Matrix4f matrix4f = GlStateManager.getMatrix4f(i);
        consumer.accept(matrix4f);
    }

    private static /* synthetic */ void lambda$getMatrix$47(int i, FloatBuffer floatBuffer) {
        GlStateManager.getMatrix(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$matrixMode$46(int i) {
        GlStateManager.matrixMode(i);
    }

    private static /* synthetic */ void lambda$clear$45(int i, boolean bl) {
        GlStateManager.clear(i, bl);
    }

    private static /* synthetic */ void lambda$clearStencil$44(int i) {
        GlStateManager.clearStencil(i);
    }

    private static /* synthetic */ void lambda$clearColor$43(float f, float g, float h, float i) {
        GlStateManager.clearColor(f, g, h, i);
    }

    private static /* synthetic */ void lambda$clearDepth$42(double d) {
        GlStateManager.clearDepth(d);
    }

    private static /* synthetic */ void lambda$stencilOp$41(int i, int j, int k) {
        GlStateManager.stencilOp(i, j, k);
    }

    private static /* synthetic */ void lambda$stencilMask$40(int i) {
        GlStateManager.stencilMask(i);
    }

    private static /* synthetic */ void lambda$stencilFunc$39(int i, int j, int k) {
        GlStateManager.stencilFunc(i, j, k);
    }

    private static /* synthetic */ void lambda$colorMask$38(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        GlStateManager.colorMask(bl, bl2, bl3, bl4);
    }

    private static /* synthetic */ void lambda$viewport$37(int i, int j, int k, int l) {
        GlStateManager.viewport(i, j, k, l);
    }

    private static /* synthetic */ void lambda$shadeModel$36(int i) {
        GlStateManager.shadeModel(i);
    }

    private static /* synthetic */ void lambda$getTexImage$35(int i, int j, int k, int l, long m) {
        GlStateManager.getTexImage(i, j, k, l, m);
    }

    private static /* synthetic */ void lambda$copyTexSubImage2D$34(int i, int j, int k, int l, int m, int n, int o, int p) {
        GlStateManager.copyTexSubImage2D(i, j, k, l, m, n, o, p);
    }

    private static /* synthetic */ void lambda$texSubImage2D$33(int i, int j, int k, int l, int m, int n, int o, int p, long q) {
        GlStateManager.texSubImage2D(i, j, k, l, m, n, o, p, q);
    }

    private static /* synthetic */ void lambda$texImage2D$32(int i, int j, int k, int l, int m, int n, int o, int p, IntBuffer intBuffer) {
        GlStateManager.texImage2D(i, j, k, l, m, n, o, p, intBuffer);
    }

    private static /* synthetic */ void lambda$bindTexture$31(int i) {
        GlStateManager.bindTexture(i);
    }

    private static /* synthetic */ void lambda$deleteTexture$30(int i) {
        GlStateManager.deleteTexture(i);
    }

    private static /* synthetic */ void lambda$texParameter$29(int i, int j, int k) {
        GlStateManager.texParameter(i, j, k);
    }

    private static /* synthetic */ void lambda$activeTexture$28(int i) {
        GlStateManager.activeTexture(i);
    }

    private static /* synthetic */ void lambda$texGenParam$27(GlStateManager.TexCoord texCoord, int i, FloatBuffer floatBuffer) {
        GlStateManager.texGenParam(texCoord, i, floatBuffer);
    }

    private static /* synthetic */ void lambda$texGenMode$26(GlStateManager.TexCoord texCoord, int i) {
        GlStateManager.texGenMode(texCoord, i);
    }

    private static /* synthetic */ void lambda$disableTexGen$25(GlStateManager.TexCoord texCoord) {
        GlStateManager.disableTexGen(texCoord);
    }

    private static /* synthetic */ void lambda$enableTexGen$24(GlStateManager.TexCoord texCoord) {
        GlStateManager.enableTexGen(texCoord);
    }

    private static /* synthetic */ void lambda$logicOp$23(int i) {
        GlStateManager.logicOp(i);
    }

    private static /* synthetic */ void lambda$logicOp$22(GlStateManager.LogicOp logicOp) {
        GlStateManager.logicOp(logicOp.glValue);
    }

    private static /* synthetic */ void lambda$polygonOffset$21(float f, float g) {
        GlStateManager.polygonOffset(f, g);
    }

    private static /* synthetic */ void lambda$polygonMode$20(int i, int j) {
        GlStateManager.polygonMode(i, j);
    }

    private static /* synthetic */ void lambda$cullFace$19(int i) {
        GlStateManager.cullFace(i);
    }

    private static /* synthetic */ void lambda$cullFace$18(GlStateManager.FaceSides faceSides) {
        GlStateManager.cullFace(faceSides.glValue);
    }

    private static /* synthetic */ void lambda$fogi$17(int i, int j) {
        GlStateManager.fogi(i, j);
    }

    private static /* synthetic */ void lambda$fog$16(int i, FloatBuffer floatBuffer) {
        GlStateManager.fog(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$fogEnd$15(float f) {
        GlStateManager.fogEnd(f);
    }

    private static /* synthetic */ void lambda$fogStart$14(float f) {
        GlStateManager.fogStart(f);
    }

    private static /* synthetic */ void lambda$fogDensity$13(float f) {
        GlStateManager.fogDensity(f);
    }

    private static /* synthetic */ void lambda$fogMode$12(int i) {
        GlStateManager.fogMode(i);
    }

    private static /* synthetic */ void lambda$fogMode$11(GlStateManager.FogMode fogMode) {
        GlStateManager.fogMode(fogMode.glValue);
    }

    private static /* synthetic */ void lambda$setupSolidRenderingTextureCombine$10(int i) {
        GlStateManager.setupSolidRenderingTextureCombine(i);
    }

    private static /* synthetic */ void lambda$blendEquation$9(int i) {
        GlStateManager.blendEquation(i);
    }

    private static /* synthetic */ void lambda$blendFuncSeparate$8(int i, int j, int k, int l) {
        GlStateManager.blendFuncSeparate(i, j, k, l);
    }

    private static /* synthetic */ void lambda$blendFuncSeparate$7(GlStateManager.class_4535 arg, GlStateManager.class_4534 arg2, GlStateManager.class_4535 arg3, GlStateManager.class_4534 arg4) {
        GlStateManager.blendFuncSeparate(arg.value, arg2.value, arg3.value, arg4.value);
    }

    private static /* synthetic */ void lambda$blendFunc$6(int i, int j) {
        GlStateManager.blendFunc(i, j);
    }

    private static /* synthetic */ void lambda$blendFunc$5(GlStateManager.class_4535 arg, GlStateManager.class_4534 arg2) {
        GlStateManager.blendFunc(arg.value, arg2.value);
    }

    private static /* synthetic */ void lambda$depthMask$4(boolean bl) {
        GlStateManager.depthMask(bl);
    }

    private static /* synthetic */ void lambda$depthFunc$3(int i) {
        GlStateManager.depthFunc(i);
    }

    private static /* synthetic */ void lambda$normal3f$2(float f, float g, float h) {
        GlStateManager.normal3f(f, g, h);
    }

    private static /* synthetic */ void lambda$colorMaterial$1(int i, int j) {
        GlStateManager.colorMaterial(i, j);
    }

    private static /* synthetic */ void lambda$alphaFunc$0(int i, float f) {
        GlStateManager.alphaFunc(i, f);
    }

    static {
        MAX_SUPPORTED_TEXTURE_SIZE = -1;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface class_4564 {
        public static final class_4564 field_20744 = new class_4564(){

            @Override
            public void begin() {
                RenderSystem.setupDefaultGlState();
            }

            @Override
            public void end() {
            }
        };
        public static final class_4564 field_20745 = new class_4564(){

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
        public static final class_4564 field_20746 = new class_4564(){

            @Override
            public void begin() {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.15f);
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
                RenderSystem.alphaFunc(516, 0.003921569f);
            }

            @Override
            public void end() {
                RenderSystem.disableBlend();
                RenderSystem.defaultAlphaFunc();
                RenderSystem.depthMask(true);
            }
        };

        public void begin();

        public void end();
    }
}

