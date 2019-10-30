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

@Environment(value=EnvType.CLIENT)
public class RenderSystem {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConcurrentLinkedQueue<RenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
    private static final Tessellator RENDER_THREAD_TESSELATOR = new Tessellator();
    public static final float DEFAULTALPHACUTOFF = 0.1f;
    private static boolean isReplayingQueue;
    private static Thread gameThread;
    private static Thread renderThread;
    private static int MAX_SUPPORTED_TEXTURE_SIZE;
    private static boolean isInInit;
    private static double lastDrawTime;

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

    public static void recordRenderCall(RenderCall renderCall) {
        recordingQueue.add(renderCall);
    }

    public static void flipFrame(long l) {
        GLFW.glfwPollEvents();
        RenderSystem.replayQueue();
        Tessellator.getInstance().getBuffer().clear();
        GLFW.glfwSwapBuffers(l);
        GLFW.glfwPollEvents();
    }

    public static void replayQueue() {
        isReplayingQueue = true;
        while (!recordingQueue.isEmpty()) {
            RenderCall renderCall = recordingQueue.poll();
            renderCall.execute();
        }
        isReplayingQueue = false;
    }

    public static void limitDisplayFPS(int i) {
        double d = lastDrawTime + 1.0 / (double)i;
        double e = GLFW.glfwGetTime();
        while (e < d) {
            GLFW.glfwWaitEventsTimeout(d - e);
            e = GLFW.glfwGetTime();
        }
        lastDrawTime = e;
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

    public static void blendFunc(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFunc(sourceFactor.value, destFactor.value);
    }

    public static void blendFunc(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFunc(i, j);
    }

    public static void blendFuncSeparate(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor, GlStateManager.SourceFactor sourceFactor2, GlStateManager.DestFactor destFactor2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFuncSeparate(sourceFactor.value, destFactor.value, sourceFactor2.value, destFactor2.value);
    }

    public static void blendFuncSeparate(int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFuncSeparate(i, j, k, l);
    }

    public static void blendEquation(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendEquation(i);
    }

    public static void blendColor(float f, float g, float h, float i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22883(f, g, h, i);
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

    public static void ortho(double d, double e, double f, double g, double h, double i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.ortho(d, e, f, g, h, i);
    }

    public static void rotatef(float f, float g, float h, float i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.rotatef(f, g, h, i);
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
        GlStateManager.rotated(d, e, f);
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

    public static void setupOutline() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_23282();
    }

    public static void teardownOutline() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_23283();
    }

    public static void setupOverlayColor(IntSupplier intSupplier, int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22610(intSupplier.getAsInt(), i);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22618();
    }

    public static void setupLevelDiffuseLighting(Matrix4f matrix4f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22616(matrix4f);
    }

    public static void setupGuiDiffuseLighting(Matrix4f matrix4f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22617(matrix4f);
    }

    public static void mulTextureByProjModelView() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22889();
    }

    public static void setupEndPortalTexGen() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22887();
    }

    public static void clearTexGen() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.method_22888();
    }

    public static void beginInitialization() {
        isInInit = true;
    }

    public static void finishInitialization() {
        isInInit = false;
        if (!recordingQueue.isEmpty()) {
            RenderSystem.replayQueue();
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
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    public static void defaultAlphaFunc() {
        RenderSystem.alphaFunc(516, 0.1f);
    }

    private static /* synthetic */ void lambda$setupGuiDiffuseLighting$69(Matrix4f matrix4f) {
        GlStateManager.method_22617(matrix4f);
    }

    private static /* synthetic */ void lambda$setupLevelDiffuseLighting$68(Matrix4f matrix4f) {
        GlStateManager.method_22616(matrix4f);
    }

    private static /* synthetic */ void lambda$setupOverlayColor$67(IntSupplier intSupplier, int i) {
        GlStateManager.method_22610(intSupplier.getAsInt(), i);
    }

    private static /* synthetic */ void lambda$glUniformMatrix4$66(int i, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager.uniformMatrix4(i, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniformMatrix3$65(int i, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager.uniformMatrix3(i, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniformMatrix2$64(int i, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager.uniformMatrix2(i, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform4$63(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform4(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform3$62(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform3(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform2$61(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform2(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1$60(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform1(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform4$59(int i, IntBuffer intBuffer) {
        GlStateManager.uniform4(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform3$58(int i, IntBuffer intBuffer) {
        GlStateManager.uniform3(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform2$57(int i, IntBuffer intBuffer) {
        GlStateManager.uniform2(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1$56(int i, IntBuffer intBuffer) {
        GlStateManager.uniform1(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1i$55(int i, int j) {
        GlStateManager.uniform1(i, j);
    }

    private static /* synthetic */ void lambda$glDeleteBuffers$54(int i) {
        GlStateManager.deleteBuffers(i);
    }

    private static /* synthetic */ void lambda$glBindBuffer$53(int i, Supplier supplier) {
        GlStateManager.bindBuffers(i, (Integer)supplier.get());
    }

    private static /* synthetic */ void lambda$glMultiTexCoord2f$52(int i, float f, float g) {
        GlStateManager.multiTexCoords2f(i, f, g);
    }

    private static /* synthetic */ void lambda$renderCrosshair$51(int i) {
        GLX._renderCrosshair(i, true, true, true);
    }

    private static /* synthetic */ void lambda$getString$50(int i, Consumer consumer) {
        String string = GlStateManager.getString(i);
        consumer.accept(string);
    }

    private static /* synthetic */ void lambda$readPixels$49(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
        GlStateManager.readPixels(i, j, k, l, m, n, byteBuffer);
    }

    private static /* synthetic */ void lambda$pixelTransfer$48(int i, float f) {
        GlStateManager.pixelTransfer(i, f);
    }

    private static /* synthetic */ void lambda$pixelStore$47(int i, int j) {
        GlStateManager.pixelStore(i, j);
    }

    private static /* synthetic */ void lambda$lineWidth$46(float f) {
        GlStateManager.lineWidth(f);
    }

    private static /* synthetic */ void lambda$drawArrays$45(int i, int j, int k) {
        GlStateManager.drawArrays(i, j, k);
    }

    private static /* synthetic */ void lambda$color3f$44(float f, float g, float h) {
        GlStateManager.color4f(f, g, h, 1.0f);
    }

    private static /* synthetic */ void lambda$color4f$43(float f, float g, float h, float i) {
        GlStateManager.color4f(f, g, h, i);
    }

    private static /* synthetic */ void lambda$multMatrix$42(Matrix4f matrix4f) {
        GlStateManager.multMatrix(matrix4f);
    }

    private static /* synthetic */ void lambda$translated$41(double d, double e, double f) {
        GlStateManager.rotated(d, e, f);
    }

    private static /* synthetic */ void lambda$translatef$40(float f, float g, float h) {
        GlStateManager.translatef(f, g, h);
    }

    private static /* synthetic */ void lambda$scaled$39(double d, double e, double f) {
        GlStateManager.scaled(d, e, f);
    }

    private static /* synthetic */ void lambda$scalef$38(float f, float g, float h) {
        GlStateManager.scalef(f, g, h);
    }

    private static /* synthetic */ void lambda$rotatef$37(float f, float g, float h, float i) {
        GlStateManager.rotatef(f, g, h, i);
    }

    private static /* synthetic */ void lambda$ortho$36(double d, double e, double f, double g, double h, double i) {
        GlStateManager.ortho(d, e, f, g, h, i);
    }

    private static /* synthetic */ void lambda$matrixMode$35(int i) {
        GlStateManager.matrixMode(i);
    }

    private static /* synthetic */ void lambda$clear$34(int i, boolean bl) {
        GlStateManager.clear(i, bl);
    }

    private static /* synthetic */ void lambda$clearStencil$33(int i) {
        GlStateManager.clearStencil(i);
    }

    private static /* synthetic */ void lambda$clearColor$32(float f, float g, float h, float i) {
        GlStateManager.clearColor(f, g, h, i);
    }

    private static /* synthetic */ void lambda$clearDepth$31(double d) {
        GlStateManager.clearDepth(d);
    }

    private static /* synthetic */ void lambda$stencilOp$30(int i, int j, int k) {
        GlStateManager.stencilOp(i, j, k);
    }

    private static /* synthetic */ void lambda$stencilMask$29(int i) {
        GlStateManager.stencilMask(i);
    }

    private static /* synthetic */ void lambda$stencilFunc$28(int i, int j, int k) {
        GlStateManager.stencilFunc(i, j, k);
    }

    private static /* synthetic */ void lambda$colorMask$27(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        GlStateManager.colorMask(bl, bl2, bl3, bl4);
    }

    private static /* synthetic */ void lambda$viewport$26(int i, int j, int k, int l) {
        GlStateManager.viewport(i, j, k, l);
    }

    private static /* synthetic */ void lambda$shadeModel$25(int i) {
        GlStateManager.shadeModel(i);
    }

    private static /* synthetic */ void lambda$bindTexture$24(int i) {
        GlStateManager.bindTexture(i);
    }

    private static /* synthetic */ void lambda$deleteTexture$23(int i) {
        GlStateManager.deleteTexture(i);
    }

    private static /* synthetic */ void lambda$texParameter$22(int i, int j, int k) {
        GlStateManager.texParameter(i, j, k);
    }

    private static /* synthetic */ void lambda$activeTexture$21(int i) {
        GlStateManager.activeTexture(i);
    }

    private static /* synthetic */ void lambda$logicOp$20(GlStateManager.LogicOp logicOp) {
        GlStateManager.logicOp(logicOp.glValue);
    }

    private static /* synthetic */ void lambda$polygonOffset$19(float f, float g) {
        GlStateManager.polygonOffset(f, g);
    }

    private static /* synthetic */ void lambda$polygonMode$18(int i, int j) {
        GlStateManager.polygonMode(i, j);
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

    private static /* synthetic */ void lambda$blendColor$10(float f, float g, float h, float i) {
        GlStateManager.method_22883(f, g, h, i);
    }

    private static /* synthetic */ void lambda$blendEquation$9(int i) {
        GlStateManager.blendEquation(i);
    }

    private static /* synthetic */ void lambda$blendFuncSeparate$8(int i, int j, int k, int l) {
        GlStateManager.blendFuncSeparate(i, j, k, l);
    }

    private static /* synthetic */ void lambda$blendFuncSeparate$7(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor, GlStateManager.SourceFactor sourceFactor2, GlStateManager.DestFactor destFactor2) {
        GlStateManager.blendFuncSeparate(sourceFactor.value, destFactor.value, sourceFactor2.value, destFactor2.value);
    }

    private static /* synthetic */ void lambda$blendFunc$6(int i, int j) {
        GlStateManager.blendFunc(i, j);
    }

    private static /* synthetic */ void lambda$blendFunc$5(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor) {
        GlStateManager.blendFunc(sourceFactor.value, destFactor.value);
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
        lastDrawTime = Double.MIN_VALUE;
    }
}

