/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderCall;
import it.unimi.dsi.fastutil.ints.IntConsumer;
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
import net.minecraft.class_5944;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
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
    private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
    private static boolean isReplayingQueue;
    @Nullable
    private static Thread gameThread;
    @Nullable
    private static Thread renderThread;
    private static int MAX_SUPPORTED_TEXTURE_SIZE;
    private static boolean isInInit;
    private static double lastDrawTime;
    private static final IndexBuffer sharedSequential;
    private static final IndexBuffer sharedSequentialQuad;
    private static final IndexBuffer sharedSequentialLines;
    private static Matrix4f projectionMatrix;
    private static Matrix4f savedProjectionMatrix;
    private static MatrixStack modelViewStack;
    private static Matrix4f modelViewMatrix;
    private static Matrix4f textureMatrix;
    private static final int[] shaderTextures;
    private static final float[] shaderColor;
    private static float shaderFogStart;
    private static float shaderFogEnd;
    private static final float[] shaderFogColor;
    private static final Vec3f[] shaderLightDirections;
    private static float shaderGameTime;
    private static float shaderLineWidth;
    @Nullable
    private static class_5944 shader;

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

    public static void disableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.disableDepthTest();
    }

    public static void enableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.enableDepthTest();
    }

    public static void enableScissor(int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.enableScissorTest();
        GlStateManager.scissor(i, j, k, l);
    }

    public static void disableScissor() {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.disableScissorTest();
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
        GlStateManager.method_34410();
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

    public static void bindTextureForSetup(int i) {
        RenderSystem.bindTexture(i);
    }

    public static void bindTexture(int texture) {
        GlStateManager.bindTexture(texture);
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

    public static void setShaderFogStart(float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        RenderSystem._setShaderFogStart(f);
    }

    private static void _setShaderFogStart(float f) {
        shaderFogStart = f;
    }

    public static float getShaderFogStart() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return shaderFogStart;
    }

    public static void setShaderFogEnd(float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        RenderSystem._setShaderFogEnd(f);
    }

    private static void _setShaderFogEnd(float f) {
        shaderFogEnd = f;
    }

    public static float getShaderFogEnd() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return shaderFogEnd;
    }

    public static void setShaderFogColor(float f, float g, float h, float i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        RenderSystem._setShaderFogColor(f, g, h, i);
    }

    public static void setShaderFogColor(float f, float g, float h) {
        RenderSystem.setShaderFogColor(f, g, h, 1.0f);
    }

    private static void _setShaderFogColor(float f, float g, float h, float i) {
        RenderSystem.shaderFogColor[0] = f;
        RenderSystem.shaderFogColor[1] = g;
        RenderSystem.shaderFogColor[2] = h;
        RenderSystem.shaderFogColor[3] = i;
    }

    public static float[] getShaderFogColor() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return shaderFogColor;
    }

    public static void setShaderLights(Vec3f vec3f, Vec3f vec3f2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        RenderSystem._setShaderLights(vec3f, vec3f2);
    }

    public static void _setShaderLights(Vec3f vec3f, Vec3f vec3f2) {
        RenderSystem.shaderLightDirections[0] = vec3f;
        RenderSystem.shaderLightDirections[1] = vec3f2;
    }

    public static void setupShaderLights(class_5944 arg) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (arg.field_29475 != null) {
            arg.field_29475.method_34413(shaderLightDirections[0]);
        }
        if (arg.field_29476 != null) {
            arg.field_29476.method_34413(shaderLightDirections[1]);
        }
    }

    public static void setShaderColor(float f, float g, float h, float i) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> RenderSystem._setShaderColor(f, g, h, i));
        } else {
            RenderSystem._setShaderColor(f, g, h, i);
        }
    }

    public static void setShaderColor(float f, float g, float h) {
        RenderSystem.setShaderColor(f, g, h, 1.0f);
    }

    private static void _setShaderColor(float f, float g, float h, float i) {
        RenderSystem.shaderColor[0] = f;
        RenderSystem.shaderColor[1] = g;
        RenderSystem.shaderColor[2] = h;
        RenderSystem.shaderColor[3] = i;
    }

    public static float[] getShaderColor() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return shaderColor;
    }

    public static void drawElements(int mode, int first, int count) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.drawElements(mode, first, count, 0L);
    }

    public static void lineWidth(float width) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                shaderLineWidth = width;
            });
        } else {
            shaderLineWidth = width;
        }
    }

    public static float getShaderLineWidth() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return shaderLineWidth;
    }

    public static void pixelStore(int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager.pixelStore(pname, param);
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

    public static String getCapsString() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        return "Using framebuffer using OpenGL 3.2";
    }

    public static void setupDefaultState(int x, int y, int width, int height) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GlStateManager.method_34410();
        GlStateManager.clearDepth(1.0);
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        projectionMatrix.loadIdentity();
        savedProjectionMatrix.loadIdentity();
        modelViewMatrix.loadIdentity();
        textureMatrix.loadIdentity();
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

    public static void glBindBuffer(int i, IntSupplier intSupplier) {
        GlStateManager.bindBuffer(i, intSupplier.getAsInt());
    }

    public static void glBindVertexArray(Supplier<Integer> supplier) {
        GlStateManager.bindVertexArray(supplier.get());
    }

    public static void glBufferData(int target, ByteBuffer data, int usage) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bufferData(target, data, usage);
    }

    public static void glDeleteBuffers(int buffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.deleteBuffer(buffer);
    }

    public static void glDeleteVertexArrays(int i) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.deleteVertexArray(i);
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

    public static void setupOverlayColor(IntSupplier texture, int size) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        int i = texture.getAsInt();
        RenderSystem.setShaderTexture(1, i);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        RenderSystem.setShaderTexture(1, 0);
    }

    public static void setupLevelDiffuseLighting(Vec3f vec3f, Vec3f vec3f2, Matrix4f matrix4f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupLevelDiffuseLighting(vec3f, vec3f2, matrix4f);
    }

    public static void setupGuiFlatDiffuseLighting(Vec3f vec3f, Vec3f vec3f2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupGuiFlatDiffuseLighting(vec3f, vec3f2);
    }

    public static void setupGui3DDiffuseLighting(Vec3f vec3f, Vec3f vec3f2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupGui3dDiffuseLighting(vec3f, vec3f2);
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
            RenderSystem.recordRenderCall(() -> consumer.accept(GlStateManager.genBuffer()));
        } else {
            consumer.accept(GlStateManager.genBuffer());
        }
    }

    public static void glGenVertexArrays(Consumer<Integer> consumer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> consumer.accept(GlStateManager.genVertexArray()));
        } else {
            consumer.accept(GlStateManager.genVertexArray());
        }
    }

    public static Tessellator renderThreadTesselator() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return RENDER_THREAD_TESSELATOR;
    }

    public static void defaultBlendFunc() {
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    }

    @Deprecated
    public static void runAsFancy(Runnable runnable) {
        boolean bl = MinecraftClient.isFabulousGraphicsOrBetter();
        if (!bl) {
            runnable.run();
            return;
        }
        GameOptions gameOptions = MinecraftClient.getInstance().options;
        GraphicsMode graphicsMode = gameOptions.graphicsMode;
        gameOptions.graphicsMode = GraphicsMode.FANCY;
        runnable.run();
        gameOptions.graphicsMode = graphicsMode;
    }

    public static void setShader(Supplier<class_5944> supplier) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                shader = (class_5944)supplier.get();
            });
        } else {
            shader = supplier.get();
        }
    }

    @Nullable
    public static class_5944 getShader() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return shader;
    }

    public static int getTextureId(int i) {
        return GlStateManager.method_34412(i);
    }

    public static void setShaderTexture(int i, Identifier identifier) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> RenderSystem._setShaderTexture(i, identifier));
        } else {
            RenderSystem._setShaderTexture(i, identifier);
        }
    }

    public static void _setShaderTexture(int i, Identifier identifier) {
        if (i >= 0 && i < shaderTextures.length) {
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            AbstractTexture abstractTexture = textureManager.getTexture(identifier);
            RenderSystem.shaderTextures[i] = abstractTexture.getGlId();
        }
    }

    public static void setShaderTexture(int i, int j) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> RenderSystem._setShaderTexture(i, j));
        } else {
            RenderSystem._setShaderTexture(i, j);
        }
    }

    public static void _setShaderTexture(int i, int j) {
        if (i >= 0 && i < shaderTextures.length) {
            RenderSystem.shaderTextures[i] = j;
        }
    }

    public static int getShaderTexture(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i >= 0 && i < shaderTextures.length) {
            return shaderTextures[i];
        }
        return 0;
    }

    public static void setProjectionMatrix(Matrix4f matrix4f) {
        Matrix4f matrix4f2 = matrix4f.copy();
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                projectionMatrix = matrix4f2;
            });
        } else {
            projectionMatrix = matrix4f2;
        }
    }

    public static void setTextureMatrix(Matrix4f matrix4f) {
        Matrix4f matrix4f2 = matrix4f.copy();
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                textureMatrix = matrix4f2;
            });
        } else {
            textureMatrix = matrix4f2;
        }
    }

    public static void resetTextureMatrix() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> textureMatrix.loadIdentity());
        } else {
            textureMatrix.loadIdentity();
        }
    }

    public static void applyModelViewMatrix() {
        Matrix4f matrix4f = modelViewStack.peek().getModel().copy();
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                modelViewMatrix = matrix4f;
            });
        } else {
            modelViewMatrix = matrix4f;
        }
    }

    public static void backupProjectionMatrix() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> RenderSystem._backupProjectionMatrix());
        } else {
            RenderSystem._backupProjectionMatrix();
        }
    }

    private static void _backupProjectionMatrix() {
        savedProjectionMatrix = projectionMatrix;
    }

    public static void restoreProjectionMatrix() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> RenderSystem._restoreProjectionMatrix());
        } else {
            RenderSystem._restoreProjectionMatrix();
        }
    }

    private static void _restoreProjectionMatrix() {
        projectionMatrix = savedProjectionMatrix;
    }

    public static Matrix4f getProjectionMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return projectionMatrix;
    }

    public static Matrix4f getModelViewMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return modelViewMatrix;
    }

    public static MatrixStack getModelViewStack() {
        return modelViewStack;
    }

    public static Matrix4f getTextureMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return textureMatrix;
    }

    public static IndexBuffer getSequentialBuffer(VertexFormat.DrawMode drawMode, int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        IndexBuffer indexBuffer = drawMode == VertexFormat.DrawMode.QUADS ? sharedSequentialQuad : (drawMode == VertexFormat.DrawMode.LINES ? sharedSequentialLines : sharedSequential);
        indexBuffer.grow(i);
        return indexBuffer;
    }

    public static void setShaderGameTime(long l, float f) {
        float g = ((float)l + f) / 24000.0f;
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                shaderGameTime = g;
            });
        } else {
            shaderGameTime = g;
        }
    }

    public static float getShaderGameTime() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return shaderGameTime;
    }

    private static /* synthetic */ void lambda$setupGui3DDiffuseLighting$57(Vec3f vec3f, Vec3f vec3f2) {
        GlStateManager.setupGui3dDiffuseLighting(vec3f, vec3f2);
    }

    private static /* synthetic */ void lambda$setupGuiFlatDiffuseLighting$56(Vec3f vec3f, Vec3f vec3f2) {
        GlStateManager.setupGuiFlatDiffuseLighting(vec3f, vec3f2);
    }

    private static /* synthetic */ void lambda$setupLevelDiffuseLighting$55(Vec3f vec3f, Vec3f vec3f2, Matrix4f matrix4f) {
        GlStateManager.setupLevelDiffuseLighting(vec3f, vec3f2, matrix4f);
    }

    private static /* synthetic */ void lambda$teardownOverlayColor$54() {
        RenderSystem.setShaderTexture(1, 0);
    }

    private static /* synthetic */ void lambda$setupOverlayColor$53(IntSupplier intSupplier) {
        int i = intSupplier.getAsInt();
        RenderSystem.setShaderTexture(1, i);
    }

    private static /* synthetic */ void lambda$glUniformMatrix4$52(int i, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager.uniformMatrix4(i, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniformMatrix3$51(int i, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager.uniformMatrix3(i, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniformMatrix2$50(int i, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager.uniformMatrix2(i, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform4$49(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform4(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform3$48(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform3(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform2$47(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform2(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1$46(int i, FloatBuffer floatBuffer) {
        GlStateManager.uniform1(i, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform4$45(int i, IntBuffer intBuffer) {
        GlStateManager.uniform4(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform3$44(int i, IntBuffer intBuffer) {
        GlStateManager.uniform3(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform2$43(int i, IntBuffer intBuffer) {
        GlStateManager.uniform2(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1$42(int i, IntBuffer intBuffer) {
        GlStateManager.uniform1(i, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1i$41(int i, int j) {
        GlStateManager.uniform1(i, j);
    }

    private static /* synthetic */ void lambda$glDeleteVertexArrays$40(int i) {
        GlStateManager.deleteVertexArray(i);
    }

    private static /* synthetic */ void lambda$glDeleteBuffers$39(int i) {
        GlStateManager.deleteBuffer(i);
    }

    private static /* synthetic */ void lambda$glBindVertexArray$38(Supplier supplier) {
        GlStateManager.bindVertexArray((Integer)supplier.get());
    }

    private static /* synthetic */ void lambda$glBindBuffer$37(int i, IntSupplier intSupplier) {
        GlStateManager.bindBuffer(i, intSupplier.getAsInt());
    }

    private static /* synthetic */ void lambda$renderCrosshair$36(int i) {
        GLX._renderCrosshair(i, true, true, true);
    }

    private static /* synthetic */ void lambda$getString$35(int i, Consumer consumer) {
        String string = GlStateManager.getString(i);
        consumer.accept(string);
    }

    private static /* synthetic */ void lambda$readPixels$34(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
        GlStateManager.readPixels(i, j, k, l, m, n, byteBuffer);
    }

    private static /* synthetic */ void lambda$pixelStore$33(int i, int j) {
        GlStateManager.pixelStore(i, j);
    }

    private static /* synthetic */ void lambda$drawElements$31(int i, int j, int k) {
        GlStateManager.drawElements(i, j, k, 0L);
    }

    private static /* synthetic */ void lambda$setShaderLights$29(Vec3f vec3f, Vec3f vec3f2) {
        RenderSystem._setShaderLights(vec3f, vec3f2);
    }

    private static /* synthetic */ void lambda$setShaderFogColor$28(float f, float g, float h, float i) {
        RenderSystem._setShaderFogColor(f, g, h, i);
    }

    private static /* synthetic */ void lambda$setShaderFogEnd$27(float f) {
        RenderSystem._setShaderFogEnd(f);
    }

    private static /* synthetic */ void lambda$setShaderFogStart$26(float f) {
        RenderSystem._setShaderFogStart(f);
    }

    private static /* synthetic */ void lambda$clear$25(int i, boolean bl) {
        GlStateManager.clear(i, bl);
    }

    private static /* synthetic */ void lambda$clearStencil$24(int i) {
        GlStateManager.clearStencil(i);
    }

    private static /* synthetic */ void lambda$clearColor$23(float f, float g, float h, float i) {
        GlStateManager.clearColor(f, g, h, i);
    }

    private static /* synthetic */ void lambda$clearDepth$22(double d) {
        GlStateManager.clearDepth(d);
    }

    private static /* synthetic */ void lambda$stencilOp$21(int i, int j, int k) {
        GlStateManager.stencilOp(i, j, k);
    }

    private static /* synthetic */ void lambda$stencilMask$20(int i) {
        GlStateManager.stencilMask(i);
    }

    private static /* synthetic */ void lambda$stencilFunc$19(int i, int j, int k) {
        GlStateManager.stencilFunc(i, j, k);
    }

    private static /* synthetic */ void lambda$colorMask$18(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        GlStateManager.colorMask(bl, bl2, bl3, bl4);
    }

    private static /* synthetic */ void lambda$viewport$17(int i, int j, int k, int l) {
        GlStateManager.viewport(i, j, k, l);
    }

    private static /* synthetic */ void lambda$bindTexture$16(int i) {
        GlStateManager.bindTexture(i);
    }

    private static /* synthetic */ void lambda$deleteTexture$15(int i) {
        GlStateManager.deleteTexture(i);
    }

    private static /* synthetic */ void lambda$texParameter$14(int i, int j, int k) {
        GlStateManager.texParameter(i, j, k);
    }

    private static /* synthetic */ void lambda$activeTexture$13(int i) {
        GlStateManager.activeTexture(i);
    }

    private static /* synthetic */ void lambda$logicOp$12(GlStateManager.LogicOp logicOp) {
        GlStateManager.logicOp(logicOp.value);
    }

    private static /* synthetic */ void lambda$polygonOffset$11(float f, float g) {
        GlStateManager.polygonOffset(f, g);
    }

    private static /* synthetic */ void lambda$polygonMode$10(int i, int j) {
        GlStateManager.polygonMode(i, j);
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

    private static /* synthetic */ void lambda$enableScissor$2(int i, int j, int k, int l) {
        GlStateManager.enableScissorTest();
        GlStateManager.scissor(i, j, k, l);
    }

    static {
        MAX_SUPPORTED_TEXTURE_SIZE = -1;
        lastDrawTime = Double.MIN_VALUE;
        sharedSequential = new IndexBuffer(1, 1, java.util.function.IntConsumer::accept);
        sharedSequentialQuad = new IndexBuffer(4, 6, (intConsumer, i) -> {
            intConsumer.accept(i + 0);
            intConsumer.accept(i + 1);
            intConsumer.accept(i + 2);
            intConsumer.accept(i + 2);
            intConsumer.accept(i + 3);
            intConsumer.accept(i + 0);
        });
        sharedSequentialLines = new IndexBuffer(4, 6, (intConsumer, i) -> {
            intConsumer.accept(i + 0);
            intConsumer.accept(i + 1);
            intConsumer.accept(i + 2);
            intConsumer.accept(i + 3);
            intConsumer.accept(i + 2);
            intConsumer.accept(i + 1);
        });
        projectionMatrix = new Matrix4f();
        savedProjectionMatrix = new Matrix4f();
        modelViewStack = new MatrixStack();
        modelViewMatrix = new Matrix4f();
        textureMatrix = new Matrix4f();
        shaderTextures = new int[12];
        shaderColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        shaderFogEnd = 1.0f;
        shaderFogColor = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        shaderLightDirections = new Vec3f[2];
        shaderLineWidth = 1.0f;
        projectionMatrix.loadIdentity();
        savedProjectionMatrix.loadIdentity();
        modelViewMatrix.loadIdentity();
        textureMatrix.loadIdentity();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class IndexBuffer {
        private final int sizeMultiplier;
        private final int increment;
        private final IndexMapper indexMapper;
        private int id;
        private VertexFormat.IntType vertexFormat = VertexFormat.IntType.BYTE;
        private int size;

        private IndexBuffer(int i, int j, IndexMapper indexMapper) {
            this.sizeMultiplier = i;
            this.increment = j;
            this.indexMapper = indexMapper;
        }

        private void grow(int newSize) {
            if (newSize <= this.size) {
                return;
            }
            LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", (Object)this.size, (Object)newSize);
            if (this.id == 0) {
                this.id = GlStateManager.genBuffer();
            }
            VertexFormat.IntType intType = VertexFormat.IntType.getSmallestTypeFor(newSize);
            int i = MathHelper.roundUpToMultiple(newSize * intType.size, 4);
            GlStateManager.bindBuffer(34963, this.id);
            GlStateManager.bufferData(34963, i, 35048);
            ByteBuffer byteBuffer = GlStateManager.mapBuffer(34963, 35001);
            if (byteBuffer == null) {
                throw new RuntimeException("Failed to map GL buffer");
            }
            this.vertexFormat = intType;
            IntConsumer intConsumer = this.getIndexConsumer(byteBuffer);
            for (int j = 0; j < newSize; j += this.increment) {
                this.indexMapper.accept(intConsumer, j * this.sizeMultiplier / this.increment);
            }
            GlStateManager.unmapBuffer(34963);
            GlStateManager.bindBuffer(34963, 0);
            this.size = newSize;
            BufferRenderer.unbindElementBuffer();
        }

        private IntConsumer getIndexConsumer(ByteBuffer indicesBuffer) {
            switch (this.vertexFormat) {
                case BYTE: {
                    return i -> indicesBuffer.put((byte)i);
                }
                case SHORT: {
                    return i -> indicesBuffer.putShort((short)i);
                }
            }
            return indicesBuffer::putInt;
        }

        public int getId() {
            return this.id;
        }

        public VertexFormat.IntType getVertexFormat() {
            return this.vertexFormat;
        }

        @Environment(value=EnvType.CLIENT)
        static interface IndexMapper {
            public void accept(IntConsumer var1, int var2);
        }
    }
}

