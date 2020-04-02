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
import net.minecraft.util.math.Matrix4f;
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
    private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
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

    public static void initGameThread(boolean assertNotRenderThread) {
        boolean bl;
        boolean bl2 = bl = renderThread == Thread.currentThread();
        if (gameThread != null || renderThread == null || bl == assertNotRenderThread) {
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

    public static void assertThread(Supplier<Boolean> check) {
        if (!check.get().booleanValue()) {
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
        RenderSystem.replayQueue();
        Tessellator.getInstance().getBuffer().clear();
        GLFW.glfwSwapBuffers(window);
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

    public static void limitDisplayFPS(int fps) {
        double d = lastDrawTime + 1.0 / (double)fps;
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

    public static void alphaFunc(int func, float ref) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.alphaFunc(func, ref);
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

    public static void colorMaterial(int face, int mode) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.colorMaterial(face, mode);
    }

    public static void normal3f(float nx, float ny, float nz) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.normal3f(nx, ny, nz);
    }

    public static void disableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableDepthTest();
    }

    public static void enableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.enableDepthTest();
    }

    public static void depthFunc(int func) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.depthFunc(func);
    }

    public static void depthMask(boolean mask) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.depthMask(mask);
    }

    public static void enableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableBlend();
    }

    public static void disableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableBlend();
    }

    public static void blendFunc(GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFunc(srcFactor.field_22545, dstFactor.field_22528);
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFunc(srcFactor, dstFactor);
    }

    public static void blendFuncSeparate(GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor, GlStateManager.SrcFactor srcAlpha, GlStateManager.DstFactor dstAlpha) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFuncSeparate(srcFactor.field_22545, dstFactor.field_22528, srcAlpha.field_22545, dstAlpha.field_22528);
    }

    public static void blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
    }

    public static void blendEquation(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendEquation(mode);
    }

    public static void blendColor(float red, float green, float blue, float alpha) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.blendColor(red, green, blue, alpha);
    }

    public static void enableFog() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableFog();
    }

    public static void disableFog() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableFog();
    }

    public static void fogMode(GlStateManager.FogMode mode) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogMode(mode.value);
    }

    public static void fogMode(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogMode(i);
    }

    public static void fogDensity(float density) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogDensity(density);
    }

    public static void fogStart(float start) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogStart(start);
    }

    public static void fogEnd(float end) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fogEnd(end);
    }

    public static void fog(int pname, float red, float green, float blue, float alpha) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.fog(pname, new float[]{red, green, blue, alpha});
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

    public static void polygonOffset(float factor, float units) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.polygonOffset(factor, units);
    }

    public static void enableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableColorLogicOp();
    }

    public static void disableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableColorLogicOp();
    }

    public static void logicOp(GlStateManager.LogicOp op) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.logicOp(op.value);
    }

    public static void activeTexture(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.activeTexture(texture);
    }

    public static void enableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableTexture();
    }

    public static void disableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableTexture();
    }

    public static void texParameter(int target, int pname, int param) {
        GlStateManager.texParameter(target, pname, param);
    }

    public static void deleteTexture(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.deleteTexture(texture);
    }

    public static void bindTexture(int texture) {
        GlStateManager.bindTexture(texture);
    }

    public static void shadeModel(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.shadeModel(mode);
    }

    public static void enableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.enableRescaleNormal();
    }

    public static void disableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableRescaleNormal();
    }

    public static void viewport(int x, int y, int width, int height) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.viewport(x, y, width, height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.colorMask(red, green, blue, alpha);
    }

    public static void stencilFunc(int func, int ref, int mask) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.stencilFunc(func, ref, mask);
    }

    public static void stencilMask(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.stencilMask(i);
    }

    public static void stencilOp(int sfail, int dpfail, int dppass) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.stencilOp(sfail, dpfail, dppass);
    }

    public static void clearDepth(double depth) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.clearDepth(depth);
    }

    public static void clearColor(float red, float green, float blue, float alpha) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.clearColor(red, green, blue, alpha);
    }

    public static void clearStencil(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.clearStencil(i);
    }

    public static void clear(int mask, boolean getError) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.clear(mask, getError);
    }

    public static void matrixMode(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.matrixMode(mode);
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

    public static void ortho(double l, double r, double b, double t, double n, double f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.ortho(l, r, b, t, n, f);
    }

    public static void rotatef(float angle, float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.rotatef(angle, x, y, z);
    }

    public static void scalef(float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.scalef(x, y, z);
    }

    public static void scaled(double x, double y, double z) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.scaled(x, y, z);
    }

    public static void translatef(float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.translatef(x, y, z);
    }

    public static void translated(double x, double y, double z) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.translated(x, y, z);
    }

    public static void multMatrix(Matrix4f matrix) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.multMatrix(matrix);
    }

    public static void color4f(float red, float green, float blue, float alpha) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.color4f(red, green, blue, alpha);
    }

    public static void color3f(float red, float green, float blue) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.color4f(red, green, blue, 1.0f);
    }

    public static void clearCurrentColor() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.clearCurrentColor();
    }

    public static void drawArrays(int mode, int first, int count) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.drawArrays(mode, first, count);
    }

    public static void lineWidth(float width) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.lineWidth(width);
    }

    public static void pixelStore(int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.pixelStore(pname, param);
    }

    public static void pixelTransfer(int i, float f) {
        GlStateManager.pixelTransfer(i, f);
    }

    public static void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.readPixels(x, y, width, height, format, type, pixels);
    }

    public static void getString(int name, Consumer<String> consumer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        consumer.accept(GlStateManager.getString(name));
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

    public static void initRenderer(int debugVerbosity, boolean debugSync) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLX._init(debugVerbosity, debugSync);
    }

    public static void setErrorCallback(GLFWErrorCallbackI callback) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLX._setGlfwErrorCallback(callback);
    }

    public static void renderCrosshair(int size) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GLX._renderCrosshair(size, true, true, true);
    }

    public static void setupNvFogDistance() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GLX._setupNvFogDistance();
    }

    public static void glMultiTexCoord2f(int texture, float s, float t) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.multiTexCoords2f(texture, s, t);
    }

    public static String getCapsString() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        return GLX._getCapsString();
    }

    public static void setupDefaultState(int x, int y, int width, int height) {
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
        GlStateManager.viewport(x, y, width, height);
    }

    public static int maxSupportedTextureSize() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
            int i = GlStateManager.getInteger(3379);
            for (int j = Math.max(32768, i); j >= 1024; j >>= 1) {
                GlStateManager.texImage2D(32868, 0, 6408, j, j, 0, 6408, 5121, null);
                int k = GlStateManager.getTexLevelParameter(32868, 0, 4096);
                if (k == 0) continue;
                MAX_SUPPORTED_TEXTURE_SIZE = j;
                return j;
            }
            MAX_SUPPORTED_TEXTURE_SIZE = Math.max(i, 1024);
            LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (Object)MAX_SUPPORTED_TEXTURE_SIZE);
        }
        return MAX_SUPPORTED_TEXTURE_SIZE;
    }

    public static void glBindBuffer(int target, Supplier<Integer> buffer) {
        GlStateManager.bindBuffers(target, buffer.get());
    }

    public static void glBufferData(int target, ByteBuffer data, int usage) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bufferData(target, data, usage);
    }

    public static void glDeleteBuffers(int buffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.deleteBuffers(buffer);
    }

    public static void glUniform1i(int location, int value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform1(location, value);
    }

    public static void glUniform1(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform1(location, value);
    }

    public static void glUniform2(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform2(location, value);
    }

    public static void glUniform3(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform3(location, value);
    }

    public static void glUniform4(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform4(location, value);
    }

    public static void glUniform1(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform1(location, value);
    }

    public static void glUniform2(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform2(location, value);
    }

    public static void glUniform3(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform3(location, value);
    }

    public static void glUniform4(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniform4(location, value);
    }

    public static void glUniformMatrix2(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniformMatrix2(location, transpose, value);
    }

    public static void glUniformMatrix3(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniformMatrix3(location, transpose, value);
    }

    public static void glUniformMatrix4(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.uniformMatrix4(location, transpose, value);
    }

    public static void setupOutline() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupOutline();
    }

    public static void teardownOutline() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.teardownOutline();
    }

    public static void setupOverlayColor(IntSupplier texture, int size) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupOverlayColor(texture.getAsInt(), size);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.teardownOverlayColor();
    }

    public static void setupLevelDiffuseLighting(Matrix4f modelMatrix) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupLevelDiffuseLighting(modelMatrix);
    }

    public static void setupGuiFlatDiffuseLighting() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupGuiFlatDiffuseLighting();
    }

    public static void setupGui3DDiffuseLighting() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupGui3dDiffuseLighting();
    }

    public static void mulTextureByProjModelView() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.mulTextureByProjModelView();
    }

    public static void setupEndPortalTexGen() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupEndPortalTexGen();
    }

    public static void clearTexGen() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.clearTexGen();
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
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    }

    public static void defaultAlphaFunc() {
        RenderSystem.alphaFunc(516, 0.1f);
    }

    private static /* synthetic */ void lambda$setupLevelDiffuseLighting$68(Matrix4f matrix4f) {
        GlStateManager.setupLevelDiffuseLighting(matrix4f);
    }

    private static /* synthetic */ void lambda$setupOverlayColor$67(IntSupplier intSupplier, int i) {
        GlStateManager.setupOverlayColor(intSupplier.getAsInt(), i);
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
        GlStateManager.translated(d, e, f);
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
        GlStateManager.logicOp(logicOp.value);
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

    private static /* synthetic */ void lambda$fog$16(int i, float f, float g, float h, float j) {
        GlStateManager.fog(i, new float[]{f, g, h, j});
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
        GlStateManager.fogMode(fogMode.value);
    }

    private static /* synthetic */ void lambda$blendColor$10(float f, float g, float h, float i) {
        GlStateManager.blendColor(f, g, h, i);
    }

    private static /* synthetic */ void lambda$blendEquation$9(int i) {
        GlStateManager.blendEquation(i);
    }

    private static /* synthetic */ void lambda$blendFuncSeparate$8(int i, int j, int k, int l) {
        GlStateManager.blendFuncSeparate(i, j, k, l);
    }

    private static /* synthetic */ void lambda$blendFuncSeparate$7(GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor, GlStateManager.SrcFactor srcFactor2, GlStateManager.DstFactor dstFactor2) {
        GlStateManager.blendFuncSeparate(srcFactor.field_22545, dstFactor.field_22528, srcFactor2.field_22545, dstFactor2.field_22528);
    }

    private static /* synthetic */ void lambda$blendFunc$6(int i, int j) {
        GlStateManager.blendFunc(i, j);
    }

    private static /* synthetic */ void lambda$blendFunc$5(GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor) {
        GlStateManager.blendFunc(srcFactor.field_22545, dstFactor.field_22528);
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

