/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.FramebufferInfo;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.Untracker;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class GlStateManager {
    private static final FloatBuffer MATRIX_BUFFER = GLX.make(MemoryUtil.memAllocFloat(16), floatBuffer -> Untracker.untrack(MemoryUtil.memAddress(floatBuffer)));
    private static final AlphaTestState ALPHA_TEST = new AlphaTestState();
    private static final CapabilityTracker LIGHTING = new CapabilityTracker(2896);
    private static final CapabilityTracker[] LIGHT_ENABLE = (CapabilityTracker[])IntStream.range(0, 8).mapToObj(i -> new CapabilityTracker(16384 + i)).toArray(CapabilityTracker[]::new);
    private static final ColorMaterialState COLOR_MATERIAL = new ColorMaterialState();
    private static final BlendFuncState BLEND = new BlendFuncState();
    private static final DepthTestState DEPTH = new DepthTestState();
    private static final FogState FOG = new FogState();
    private static final CullFaceState CULL = new CullFaceState();
    private static final PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
    private static final LogicOpState COLOR_LOGIC = new LogicOpState();
    private static final TexGenState TEX_GEN = new TexGenState();
    private static final StencilState STENCIL = new StencilState();
    private static final ScissorTestState SCISSOR_TEST = new ScissorTestState();
    private static final FloatBuffer colorBuffer = GlAllocationUtils.allocateFloatBuffer(4);
    private static int activeTexture;
    private static final Texture2DState[] TEXTURES;
    private static int modelShadeMode;
    private static final CapabilityTracker RESCALE_NORMAL;
    private static final ColorMask COLOR_MASK;
    private static final Color4 COLOR;
    private static FBOMode fboMode;
    private static FBOBlitMode fboBlitMode;

    @Deprecated
    public static void pushLightingAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushAttrib(8256);
    }

    @Deprecated
    public static void pushTextureAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushAttrib(270336);
    }

    @Deprecated
    public static void popAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopAttrib();
    }

    @Deprecated
    public static void disableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.ALPHA_TEST.capState.disable();
    }

    @Deprecated
    public static void enableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.ALPHA_TEST.capState.enable();
    }

    @Deprecated
    public static void alphaFunc(int func, float ref) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (func != GlStateManager.ALPHA_TEST.func || ref != GlStateManager.ALPHA_TEST.ref) {
            GlStateManager.ALPHA_TEST.func = func;
            GlStateManager.ALPHA_TEST.ref = ref;
            GL11.glAlphaFunc(func, ref);
        }
    }

    @Deprecated
    public static void enableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHTING.enable();
    }

    @Deprecated
    public static void disableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHTING.disable();
    }

    @Deprecated
    public static void enableLight(int light) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHT_ENABLE[light].enable();
    }

    @Deprecated
    public static void enableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_MATERIAL.capState.enable();
    }

    @Deprecated
    public static void disableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_MATERIAL.capState.disable();
    }

    @Deprecated
    public static void colorMaterial(int face, int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (face != GlStateManager.COLOR_MATERIAL.face || mode != GlStateManager.COLOR_MATERIAL.mode) {
            GlStateManager.COLOR_MATERIAL.face = face;
            GlStateManager.COLOR_MATERIAL.mode = mode;
            GL11.glColorMaterial(face, mode);
        }
    }

    @Deprecated
    public static void light(int light, int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightfv(light, pname, params);
    }

    @Deprecated
    public static void lightModel(int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightModelfv(pname, params);
    }

    @Deprecated
    public static void normal3f(float nx, float ny, float nz) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormal3f(nx, ny, nz);
    }

    public static void method_31318() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.SCISSOR_TEST.capState.disable();
    }

    public static void enableScissorTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.SCISSOR_TEST.capState.enable();
    }

    public static void scissor(int x, int y, int width, int height) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL20.glScissor(x, y, width, height);
    }

    public static void disableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.DEPTH.capState.disable();
    }

    public static void enableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.DEPTH.capState.enable();
    }

    public static void depthFunc(int func) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (func != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = func;
            GL11.glDepthFunc(func);
        }
    }

    public static void depthMask(boolean mask) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (mask != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = mask;
            GL11.glDepthMask(mask);
        }
    }

    public static void disableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.BLEND.capState.disable();
    }

    public static void enableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.BLEND.capState.enable();
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (srcFactor != GlStateManager.BLEND.srcFactorRGB || dstFactor != GlStateManager.BLEND.dstFactorRGB) {
            GlStateManager.BLEND.srcFactorRGB = srcFactor;
            GlStateManager.BLEND.dstFactorRGB = dstFactor;
            GL11.glBlendFunc(srcFactor, dstFactor);
        }
    }

    public static void blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (srcFactorRGB != GlStateManager.BLEND.srcFactorRGB || dstFactorRGB != GlStateManager.BLEND.dstFactorRGB || srcFactorAlpha != GlStateManager.BLEND.srcFactorAlpha || dstFactorAlpha != GlStateManager.BLEND.dstFactorAlpha) {
            GlStateManager.BLEND.srcFactorRGB = srcFactorRGB;
            GlStateManager.BLEND.dstFactorRGB = dstFactorRGB;
            GlStateManager.BLEND.srcFactorAlpha = srcFactorAlpha;
            GlStateManager.BLEND.dstFactorAlpha = dstFactorAlpha;
            GlStateManager.blendFuncSeparateUntracked(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static void blendColor(float red, float green, float blue, float alpha) {
        GL14.glBlendColor(red, green, blue, alpha);
    }

    public static void blendEquation(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendEquation(mode);
    }

    /**
     * Configures the frame buffer and populates {@link FramebufferInfo} with the appropriate constants
     * for the current GLCapabilities.
     * 
     * @return human-readable string representing the selected frame buffer technology
     * @throws IllegalStateException if no known frame buffer technology is supported
     */
    public static String initFramebufferSupport(GLCapabilities capabilities) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        fboBlitMode = capabilities.OpenGL30 ? FBOBlitMode.BASE : (capabilities.GL_EXT_framebuffer_blit ? FBOBlitMode.EXT : FBOBlitMode.NONE);
        if (capabilities.OpenGL30) {
            fboMode = FBOMode.BASE;
            FramebufferInfo.FRAME_BUFFER = 36160;
            FramebufferInfo.RENDER_BUFFER = 36161;
            FramebufferInfo.COLOR_ATTACHMENT = 36064;
            FramebufferInfo.DEPTH_ATTACHMENT = 36096;
            FramebufferInfo.FRAME_BUFFER_COMPLETE = 36053;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "OpenGL 3.0";
        }
        if (capabilities.GL_ARB_framebuffer_object) {
            fboMode = FBOMode.ARB;
            FramebufferInfo.FRAME_BUFFER = 36160;
            FramebufferInfo.RENDER_BUFFER = 36161;
            FramebufferInfo.COLOR_ATTACHMENT = 36064;
            FramebufferInfo.DEPTH_ATTACHMENT = 36096;
            FramebufferInfo.FRAME_BUFFER_COMPLETE = 36053;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "ARB_framebuffer_object extension";
        }
        if (capabilities.GL_EXT_framebuffer_object) {
            fboMode = FBOMode.EXT;
            FramebufferInfo.FRAME_BUFFER = 36160;
            FramebufferInfo.RENDER_BUFFER = 36161;
            FramebufferInfo.COLOR_ATTACHMENT = 36064;
            FramebufferInfo.DEPTH_ATTACHMENT = 36096;
            FramebufferInfo.FRAME_BUFFER_COMPLETE = 36053;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "EXT_framebuffer_object extension";
        }
        throw new IllegalStateException("Could not initialize framebuffer support.");
    }

    public static int getProgram(int program, int pname) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgrami(program, pname);
    }

    public static void attachShader(int program, int shader) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glAttachShader(program, shader);
    }

    public static void deleteShader(int shader) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteShader(shader);
    }

    public static int createShader(int type) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateShader(type);
    }

    public static void shaderSource(int shader, CharSequence source) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glShaderSource(shader, source);
    }

    public static void compileShader(int shader) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glCompileShader(shader);
    }

    public static int getShader(int shader, int pname) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderi(shader, pname);
    }

    public static void useProgram(int program) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUseProgram(program);
    }

    public static int createProgram() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateProgram();
    }

    public static void deleteProgram(int program) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteProgram(program);
    }

    public static void linkProgram(int program) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glLinkProgram(program);
    }

    public static int getUniformLocation(int program, CharSequence name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetUniformLocation(program, name);
    }

    public static void uniform1(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1iv(location, value);
    }

    public static void uniform1(int location, int value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1i(location, value);
    }

    public static void uniform1(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1fv(location, value);
    }

    public static void uniform2(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2iv(location, value);
    }

    public static void uniform2(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2fv(location, value);
    }

    public static void uniform3(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3iv(location, value);
    }

    public static void uniform3(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3fv(location, value);
    }

    public static void uniform4(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4iv(location, value);
    }

    public static void uniform4(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4fv(location, value);
    }

    public static void uniformMatrix2(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix2fv(location, transpose, value);
    }

    public static void uniformMatrix3(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix3fv(location, transpose, value);
    }

    public static void uniformMatrix4(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix4fv(location, transpose, value);
    }

    public static int getAttribLocation(int program, CharSequence name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetAttribLocation(program, name);
    }

    public static int genBuffers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL15.glGenBuffers();
    }

    public static void bindBuffer(int target, int buffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBindBuffer(target, buffer);
    }

    public static void bufferData(int target, ByteBuffer data, int usage) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBufferData(target, data, usage);
    }

    public static void bufferData(int target, long size, int usage) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBufferData(target, size, usage);
    }

    @Nullable
    public static ByteBuffer mapBuffer(int target, int access) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL15.glMapBuffer(target, access);
    }

    public static void unmapBuffer(int target) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glUnmapBuffer(target);
    }

    public static void deleteBuffers(int buffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL15.glDeleteBuffers(buffer);
    }

    public static void copyTexSubImage2d(int target, int level, int xOffset, int yOffset, int x, int y, int width, int height) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL20.glCopyTexSubImage2D(target, level, xOffset, yOffset, x, y, width, height);
    }

    public static void bindFramebuffer(int target, int framebuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glBindFramebuffer(target, framebuffer);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glBindFramebuffer(target, framebuffer);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glBindFramebufferEXT(target, framebuffer);
            }
        }
    }

    public static int getFramebufferDepthAttachment() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                if (GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36048) != 5890) break;
                return GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
            }
            case ARB: {
                if (ARBFramebufferObject.glGetFramebufferAttachmentParameteri(36160, 36096, 36048) != 5890) break;
                return ARBFramebufferObject.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
            }
            case EXT: {
                if (EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT(36160, 36096, 36048) != 5890) break;
                return EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT(36160, 36096, 36049);
            }
        }
        return 0;
    }

    public static void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboBlitMode) {
            case BASE: {
                GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
                break;
            }
            case EXT: {
                EXTFramebufferBlit.glBlitFramebufferEXT(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
                break;
            }
        }
    }

    public static void deleteFramebuffers(int framebuffers) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glDeleteFramebuffers(framebuffers);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glDeleteFramebuffers(framebuffers);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffers);
            }
        }
    }

    public static int genFramebuffers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                return GL30.glGenFramebuffers();
            }
            case ARB: {
                return ARBFramebufferObject.glGenFramebuffers();
            }
            case EXT: {
                return EXTFramebufferObject.glGenFramebuffersEXT();
            }
        }
        return -1;
    }

    public static int checkFramebufferStatus(int target) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                return GL30.glCheckFramebufferStatus(target);
            }
            case ARB: {
                return ARBFramebufferObject.glCheckFramebufferStatus(target);
            }
            case EXT: {
                return EXTFramebufferObject.glCheckFramebufferStatusEXT(target);
            }
        }
        return -1;
    }

    public static void framebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, textureTarget, texture, level);
            }
        }
    }

    @Deprecated
    public static int getActiveBoundTexture() {
        return GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture;
    }

    public static void activeTextureUntracked(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glActiveTexture(texture);
    }

    @Deprecated
    public static void clientActiveTexture(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glClientActiveTexture(texture);
    }

    @Deprecated
    public static void multiTexCoords2f(int texture, float s, float t) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glMultiTexCoord2f(texture, s, t);
    }

    public static void blendFuncSeparateUntracked(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
    }

    public static String getShaderInfoLog(int shader, int maxLength) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderInfoLog(shader, maxLength);
    }

    public static String getProgramInfoLog(int program, int maxLength) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgramInfoLog(program, maxLength);
    }

    public static void setupOutline() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.texEnv(8960, 8704, 34160);
        GlStateManager.combineColor(7681, 34168);
    }

    public static void teardownOutline() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.texEnv(8960, 8704, 8448);
        GlStateManager.combineColor(8448, 5890, 34168, 34166);
    }

    public static void setupOverlayColor(int texture, int size) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.activeTexture(33985);
        GlStateManager.enableTexture();
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = 1.0f / (float)(size - 1);
        GlStateManager.scalef(f, f, f);
        GlStateManager.matrixMode(5888);
        GlStateManager.bindTexture(texture);
        GlStateManager.texParameter(3553, 10241, 9728);
        GlStateManager.texParameter(3553, 10240, 9728);
        GlStateManager.texParameter(3553, 10242, 10496);
        GlStateManager.texParameter(3553, 10243, 10496);
        GlStateManager.texEnv(8960, 8704, 34160);
        GlStateManager.combineColor(34165, 34168, 5890, 5890);
        GlStateManager.combineAlpha(7681, 34168);
        GlStateManager.activeTexture(33984);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.activeTexture(33985);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(33984);
    }

    private static void combineColor(int combineColor, int source0Color) {
        GlStateManager.texEnv(8960, 34161, combineColor);
        GlStateManager.texEnv(8960, 34176, source0Color);
        GlStateManager.texEnv(8960, 34192, 768);
    }

    private static void combineColor(int combineColor, int source0Color, int source1Color, int source2Color) {
        GlStateManager.texEnv(8960, 34161, combineColor);
        GlStateManager.texEnv(8960, 34176, source0Color);
        GlStateManager.texEnv(8960, 34192, 768);
        GlStateManager.texEnv(8960, 34177, source1Color);
        GlStateManager.texEnv(8960, 34193, 768);
        GlStateManager.texEnv(8960, 34178, source2Color);
        GlStateManager.texEnv(8960, 34194, 770);
    }

    private static void combineAlpha(int combineAlpha, int source0Alpha) {
        GlStateManager.texEnv(8960, 34162, combineAlpha);
        GlStateManager.texEnv(8960, 34184, source0Alpha);
        GlStateManager.texEnv(8960, 34200, 770);
    }

    public static void setupLevelDiffuseLighting(Vec3f vec3f, Vec3f vec3f2, Matrix4f matrix4f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        Vector4f vector4f = new Vector4f(vec3f);
        vector4f.transform(matrix4f);
        GlStateManager.light(16384, 4611, GlStateManager.getBuffer(vector4f.getX(), vector4f.getY(), vector4f.getZ(), 0.0f));
        float f = 0.6f;
        GlStateManager.light(16384, 4609, GlStateManager.getBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager.light(16384, 4608, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16384, 4610, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        Vector4f vector4f2 = new Vector4f(vec3f2);
        vector4f2.transform(matrix4f);
        GlStateManager.light(16385, 4611, GlStateManager.getBuffer(vector4f2.getX(), vector4f2.getY(), vector4f2.getZ(), 0.0f));
        GlStateManager.light(16385, 4609, GlStateManager.getBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager.light(16385, 4608, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16385, 4610, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.shadeModel(7424);
        float g = 0.4f;
        GlStateManager.lightModel(2899, GlStateManager.getBuffer(0.4f, 0.4f, 0.4f, 1.0f));
        GlStateManager.popMatrix();
    }

    public static void setupGuiFlatDiffuseLighting(Vec3f vec3f, Vec3f vec3f2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.loadIdentity();
        matrix4f.multiply(Matrix4f.scale(1.0f, -1.0f, 1.0f));
        matrix4f.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-22.5f));
        matrix4f.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(135.0f));
        GlStateManager.setupLevelDiffuseLighting(vec3f, vec3f2, matrix4f);
    }

    public static void setupGui3dDiffuseLighting(Vec3f vec3f, Vec3f vec3f2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.loadIdentity();
        matrix4f.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(62.0f));
        matrix4f.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(185.5f));
        matrix4f.multiply(Matrix4f.scale(1.0f, -1.0f, 1.0f));
        matrix4f.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-22.5f));
        matrix4f.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(135.0f));
        GlStateManager.setupLevelDiffuseLighting(vec3f, vec3f2, matrix4f);
    }

    private static FloatBuffer getBuffer(float a, float b, float c, float d) {
        colorBuffer.clear();
        colorBuffer.put(a).put(b).put(c).put(d);
        colorBuffer.flip();
        return colorBuffer;
    }

    public static void setupEndPortalTexGen() {
        GlStateManager.texGenMode(TexCoord.S, 9216);
        GlStateManager.texGenMode(TexCoord.T, 9216);
        GlStateManager.texGenMode(TexCoord.R, 9216);
        GlStateManager.texGenParam(TexCoord.S, 9474, GlStateManager.getBuffer(1.0f, 0.0f, 0.0f, 0.0f));
        GlStateManager.texGenParam(TexCoord.T, 9474, GlStateManager.getBuffer(0.0f, 1.0f, 0.0f, 0.0f));
        GlStateManager.texGenParam(TexCoord.R, 9474, GlStateManager.getBuffer(0.0f, 0.0f, 1.0f, 0.0f));
        GlStateManager.enableTexGen(TexCoord.S);
        GlStateManager.enableTexGen(TexCoord.T);
        GlStateManager.enableTexGen(TexCoord.R);
    }

    public static void clearTexGen() {
        GlStateManager.disableTexGen(TexCoord.S);
        GlStateManager.disableTexGen(TexCoord.T);
        GlStateManager.disableTexGen(TexCoord.R);
    }

    public static void mulTextureByProjModelView() {
        GlStateManager.getFloat(2983, MATRIX_BUFFER);
        GlStateManager.multMatrix(MATRIX_BUFFER);
        GlStateManager.getFloat(2982, MATRIX_BUFFER);
        GlStateManager.multMatrix(MATRIX_BUFFER);
    }

    @Deprecated
    public static void enableFog() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.FOG.capState.enable();
    }

    @Deprecated
    public static void disableFog() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.FOG.capState.disable();
    }

    @Deprecated
    public static void fogMode(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (mode != GlStateManager.FOG.mode) {
            GlStateManager.FOG.mode = mode;
            GlStateManager.fogi(2917, mode);
        }
    }

    @Deprecated
    public static void fogDensity(float density) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (density != GlStateManager.FOG.density) {
            GlStateManager.FOG.density = density;
            GL11.glFogf(2914, density);
        }
    }

    @Deprecated
    public static void fogStart(float start) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (start != GlStateManager.FOG.start) {
            GlStateManager.FOG.start = start;
            GL11.glFogf(2915, start);
        }
    }

    @Deprecated
    public static void fogEnd(float end) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (end != GlStateManager.FOG.end) {
            GlStateManager.FOG.end = end;
            GL11.glFogf(2916, end);
        }
    }

    @Deprecated
    public static void fog(int pname, float[] params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogfv(pname, params);
    }

    @Deprecated
    public static void fogi(int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogi(pname, param);
    }

    public static void enableCull() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.CULL.capState.enable();
    }

    public static void disableCull() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.CULL.capState.disable();
    }

    public static void polygonMode(int face, int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPolygonMode(face, mode);
    }

    public static void enablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.capFill.enable();
    }

    public static void disablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.capFill.disable();
    }

    public static void enableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.capLine.enable();
    }

    public static void disableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.capLine.disable();
    }

    public static void polygonOffset(float factor, float units) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (factor != GlStateManager.POLY_OFFSET.factor || units != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = factor;
            GlStateManager.POLY_OFFSET.units = units;
            GL11.glPolygonOffset(factor, units);
        }
    }

    public static void enableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_LOGIC.capState.enable();
    }

    public static void disableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_LOGIC.capState.disable();
    }

    public static void logicOp(int op) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (op != GlStateManager.COLOR_LOGIC.op) {
            GlStateManager.COLOR_LOGIC.op = op;
            GL11.glLogicOp(op);
        }
    }

    @Deprecated
    public static void enableTexGen(TexCoord coord) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.getGenCoordState((TexCoord)coord).capState.enable();
    }

    @Deprecated
    public static void disableTexGen(TexCoord coord) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.getGenCoordState((TexCoord)coord).capState.disable();
    }

    @Deprecated
    public static void texGenMode(TexCoord coord, int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        TexGenCoordState texGenCoordState = GlStateManager.getGenCoordState(coord);
        if (mode != texGenCoordState.mode) {
            texGenCoordState.mode = mode;
            GL11.glTexGeni(texGenCoordState.coord, 9472, mode);
        }
    }

    @Deprecated
    public static void texGenParam(TexCoord coord, int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexGenfv(GlStateManager.getGenCoordState((TexCoord)coord).coord, pname, params);
    }

    @Deprecated
    private static TexGenCoordState getGenCoordState(TexCoord coord) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        switch (coord) {
            case S: {
                return GlStateManager.TEX_GEN.s;
            }
            case T: {
                return GlStateManager.TEX_GEN.t;
            }
            case R: {
                return GlStateManager.TEX_GEN.r;
            }
            case Q: {
                return GlStateManager.TEX_GEN.q;
            }
        }
        return GlStateManager.TEX_GEN.s;
    }

    public static void activeTexture(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (activeTexture != texture - 33984) {
            activeTexture = texture - 33984;
            GlStateManager.activeTextureUntracked(texture);
        }
    }

    public static void enableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.TEXTURES[GlStateManager.activeTexture].capState.enable();
    }

    public static void disableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.TEXTURES[GlStateManager.activeTexture].capState.disable();
    }

    @Deprecated
    public static void texEnv(int target, int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexEnvi(target, pname, param);
    }

    public static void texParameter(int target, int pname, float param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameterf(target, pname, param);
    }

    public static void texParameter(int target, int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameteri(target, pname, param);
    }

    public static int getTexLevelParameter(int target, int level, int pname) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return GL11.glGetTexLevelParameteri(target, level, pname);
    }

    public static int genTextures() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGenTextures();
    }

    public static void genTextures(int[] is) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glGenTextures(is);
    }

    public static void deleteTexture(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glDeleteTextures(texture);
        for (Texture2DState texture2DState : TEXTURES) {
            if (texture2DState.boundTexture != texture) continue;
            texture2DState.boundTexture = -1;
        }
    }

    public static void deleteTextures(int[] is) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        for (Texture2DState texture2DState : TEXTURES) {
            for (int i : is) {
                if (texture2DState.boundTexture != i) continue;
                texture2DState.boundTexture = -1;
            }
        }
        GL11.glDeleteTextures(is);
    }

    public static void bindTexture(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (texture != GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture = texture;
            GL11.glBindTexture(3553, texture);
        }
    }

    public static void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    public static void texSubImage2D(int target, int level, int offsetX, int offsetY, int width, int height, int format, int type, long pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexSubImage2D(target, level, offsetX, offsetY, width, height, format, type, pixels);
    }

    public static void getTexImage(int target, int level, int format, int type, long pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetTexImage(target, level, format, type, pixels);
    }

    @Deprecated
    public static void shadeModel(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (mode != modelShadeMode) {
            modelShadeMode = mode;
            GL11.glShadeModel(mode);
        }
    }

    @Deprecated
    public static void enableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RESCALE_NORMAL.enable();
    }

    @Deprecated
    public static void disableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RESCALE_NORMAL.disable();
    }

    public static void viewport(int x, int y, int width, int height) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        Viewport.INSTANCE.x = x;
        Viewport.INSTANCE.y = y;
        Viewport.INSTANCE.width = width;
        Viewport.INSTANCE.height = height;
        GL11.glViewport(x, y, width, height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (red != GlStateManager.COLOR_MASK.red || green != GlStateManager.COLOR_MASK.green || blue != GlStateManager.COLOR_MASK.blue || alpha != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = red;
            GlStateManager.COLOR_MASK.green = green;
            GlStateManager.COLOR_MASK.blue = blue;
            GlStateManager.COLOR_MASK.alpha = alpha;
            GL11.glColorMask(red, green, blue, alpha);
        }
    }

    public static void stencilFunc(int func, int ref, int mask) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (func != GlStateManager.STENCIL.subState.func || func != GlStateManager.STENCIL.subState.ref || func != GlStateManager.STENCIL.subState.mask) {
            GlStateManager.STENCIL.subState.func = func;
            GlStateManager.STENCIL.subState.ref = ref;
            GlStateManager.STENCIL.subState.mask = mask;
            GL11.glStencilFunc(func, ref, mask);
        }
    }

    public static void stencilMask(int mask) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (mask != GlStateManager.STENCIL.mask) {
            GlStateManager.STENCIL.mask = mask;
            GL11.glStencilMask(mask);
        }
    }

    public static void stencilOp(int sfail, int dpfail, int dppass) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (sfail != GlStateManager.STENCIL.sfail || dpfail != GlStateManager.STENCIL.dpfail || dppass != GlStateManager.STENCIL.dppass) {
            GlStateManager.STENCIL.sfail = sfail;
            GlStateManager.STENCIL.dpfail = dpfail;
            GlStateManager.STENCIL.dppass = dppass;
            GL11.glStencilOp(sfail, dpfail, dppass);
        }
    }

    public static void clearDepth(double depth) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClearDepth(depth);
    }

    public static void clearColor(float red, float green, float blue, float alpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClearColor(red, green, blue, alpha);
    }

    public static void clearStencil(int stencil) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glClearStencil(stencil);
    }

    public static void clear(int mask, boolean getError) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClear(mask);
        if (getError) {
            GlStateManager.getError();
        }
    }

    @Deprecated
    public static void matrixMode(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glMatrixMode(mode);
    }

    @Deprecated
    public static void loadIdentity() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glLoadIdentity();
    }

    @Deprecated
    public static void pushMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushMatrix();
    }

    @Deprecated
    public static void popMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopMatrix();
    }

    @Deprecated
    public static void getFloat(int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetFloatv(pname, params);
    }

    @Deprecated
    public static void ortho(double l, double r, double b, double t, double n, double f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glOrtho(l, r, b, t, n, f);
    }

    @Deprecated
    public static void rotatef(float angle, float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glRotatef(angle, x, y, z);
    }

    @Deprecated
    public static void scalef(float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScalef(x, y, z);
    }

    @Deprecated
    public static void scaled(double x, double y, double z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScaled(x, y, z);
    }

    @Deprecated
    public static void translatef(float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslatef(x, y, z);
    }

    @Deprecated
    public static void translated(double x, double y, double z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslated(x, y, z);
    }

    @Deprecated
    public static void multMatrix(FloatBuffer matrix) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glMultMatrixf(matrix);
    }

    @Deprecated
    public static void multMatrix(Matrix4f matrix) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        matrix.writeToBuffer(MATRIX_BUFFER);
        MATRIX_BUFFER.rewind();
        GlStateManager.multMatrix(MATRIX_BUFFER);
    }

    @Deprecated
    public static void color4f(float red, float green, float blue, float alpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (red != GlStateManager.COLOR.red || green != GlStateManager.COLOR.green || blue != GlStateManager.COLOR.blue || alpha != GlStateManager.COLOR.alpha) {
            GlStateManager.COLOR.red = red;
            GlStateManager.COLOR.green = green;
            GlStateManager.COLOR.blue = blue;
            GlStateManager.COLOR.alpha = alpha;
            GL11.glColor4f(red, green, blue, alpha);
        }
    }

    @Deprecated
    public static void clearCurrentColor() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR.red = -1.0f;
        GlStateManager.COLOR.green = -1.0f;
        GlStateManager.COLOR.blue = -1.0f;
        GlStateManager.COLOR.alpha = -1.0f;
    }

    @Deprecated
    public static void normalPointer(int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormalPointer(type, stride, pointer);
    }

    @Deprecated
    public static void texCoordPointer(int size, int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexCoordPointer(size, type, stride, pointer);
    }

    @Deprecated
    public static void vertexPointer(int size, int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glVertexPointer(size, type, stride, pointer);
    }

    @Deprecated
    public static void colorPointer(int size, int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glColorPointer(size, type, stride, pointer);
    }

    public static void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    @Deprecated
    public static void enableClientState(int cap) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glEnableClientState(cap);
    }

    @Deprecated
    public static void disableClientState(int cap) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDisableClientState(cap);
    }

    public static void enableVertexAttribArray(int index) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray(index);
    }

    public static void method_22607(int index) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray(index);
    }

    public static void drawElements(int mode, int first, int type, long indices) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDrawElements(mode, first, type, indices);
    }

    public static void lineWidth(float width) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLineWidth(width);
    }

    public static void pixelStore(int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glPixelStorei(pname, param);
    }

    public static void pixelTransfer(int pname, float param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPixelTransferf(pname, param);
    }

    public static void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glReadPixels(x, y, width, height, format, type, pixels);
    }

    public static int getError() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetError();
    }

    public static String getString(int name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetString(name);
    }

    public static int getInteger(int pname) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGetInteger(pname);
    }

    public static boolean supportsGl30() {
        return fboBlitMode != FBOBlitMode.NONE;
    }

    static {
        TEXTURES = (Texture2DState[])IntStream.range(0, 12).mapToObj(i -> new Texture2DState()).toArray(Texture2DState[]::new);
        modelShadeMode = 7425;
        RESCALE_NORMAL = new CapabilityTracker(32826);
        COLOR_MASK = new ColorMask();
        COLOR = new Color4();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum DstFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int field_22528;

        private DstFactor(int j) {
            this.field_22528 = j;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum SrcFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int field_22545;

        private SrcFactor(int j) {
            this.field_22545 = j;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CapabilityTracker {
        private final int cap;
        private boolean state;

        public CapabilityTracker(int cap) {
            this.cap = cap;
        }

        public void disable() {
            this.setState(false);
        }

        public void enable() {
            this.setState(true);
        }

        public void setState(boolean state) {
            RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
            if (state != this.state) {
                this.state = state;
                if (state) {
                    GL11.glEnable(this.cap);
                } else {
                    GL11.glDisable(this.cap);
                }
            }
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class Color4 {
        public float red = 1.0f;
        public float green = 1.0f;
        public float blue = 1.0f;
        public float alpha = 1.0f;

        public Color4() {
            this(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public Color4(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        private ColorMask() {
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public static enum TexCoord {
        S,
        T,
        R,
        Q;

    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class TexGenCoordState {
        public final CapabilityTracker capState;
        public final int coord;
        public int mode = -1;

        public TexGenCoordState(int coord, int cap) {
            this.coord = coord;
            this.capState = new CapabilityTracker(cap);
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class TexGenState {
        public final TexGenCoordState s = new TexGenCoordState(8192, 3168);
        public final TexGenCoordState t = new TexGenCoordState(8193, 3169);
        public final TexGenCoordState r = new TexGenCoordState(8194, 3170);
        public final TexGenCoordState q = new TexGenCoordState(8195, 3171);

        private TexGenState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ScissorTestState {
        public final CapabilityTracker capState = new CapabilityTracker(3089);

        private ScissorTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class StencilState {
        public final StencilSubState subState = new StencilSubState();
        public int mask = -1;
        public int sfail = 7680;
        public int dpfail = 7680;
        public int dppass = 7680;

        private StencilState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class StencilSubState {
        public int func = 519;
        public int ref;
        public int mask = -1;

        private StencilSubState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class LogicOpState {
        public final CapabilityTracker capState = new CapabilityTracker(3058);
        public int op = 5379;

        private LogicOpState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class PolygonOffsetState {
        public final CapabilityTracker capFill = new CapabilityTracker(32823);
        public final CapabilityTracker capLine = new CapabilityTracker(10754);
        public float factor;
        public float units;

        private PolygonOffsetState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CullFaceState {
        public final CapabilityTracker capState = new CapabilityTracker(2884);
        public int mode = 1029;

        private CullFaceState() {
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class FogState {
        public final CapabilityTracker capState = new CapabilityTracker(2912);
        public int mode = 2048;
        public float density = 1.0f;
        public float start;
        public float end = 1.0f;

        private FogState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DepthTestState {
        public final CapabilityTracker capState = new CapabilityTracker(2929);
        public boolean mask = true;
        public int func = 513;

        private DepthTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BlendFuncState {
        public final CapabilityTracker capState = new CapabilityTracker(3042);
        public int srcFactorRGB = 1;
        public int dstFactorRGB = 0;
        public int srcFactorAlpha = 1;
        public int dstFactorAlpha = 0;

        private BlendFuncState() {
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class ColorMaterialState {
        public final CapabilityTracker capState = new CapabilityTracker(2903);
        public int face = 1032;
        public int mode = 5634;

        private ColorMaterialState() {
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class AlphaTestState {
        public final CapabilityTracker capState = new CapabilityTracker(3008);
        public int func = 519;
        public float ref = -1.0f;

        private AlphaTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Texture2DState {
        public final CapabilityTracker capState = new CapabilityTracker(3553);
        public int boundTexture;

        private Texture2DState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum FBOBlitMode {
        BASE,
        EXT,
        NONE;

    }

    @Environment(value=EnvType.CLIENT)
    public static enum FBOMode {
        BASE,
        ARB,
        EXT;

    }

    @Environment(value=EnvType.CLIENT)
    public static enum Viewport {
        INSTANCE;

        protected int x;
        protected int y;
        protected int width;
        protected int height;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum LogicOp {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int value;

        private LogicOp(int j) {
            this.value = j;
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public static enum FogMode {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);

        public final int value;

        private FogMode(int j) {
            this.value = j;
        }
    }
}

