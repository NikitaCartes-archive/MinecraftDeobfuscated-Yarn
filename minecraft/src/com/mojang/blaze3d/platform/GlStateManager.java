package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.Untracker;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.Util;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class GlStateManager {
	private static final FloatBuffer MATRIX_BUFFER = GLX.make(MemoryUtil.memAllocFloat(16), floatBuffer -> Untracker.untrack(MemoryUtil.memAddress(floatBuffer)));
	private static final GlStateManager.AlphaTestState ALPHA_TEST = new GlStateManager.AlphaTestState();
	private static final GlStateManager.CapabilityTracker LIGHTING = new GlStateManager.CapabilityTracker(2896);
	private static final GlStateManager.CapabilityTracker[] LIGHT_ENABLE = (GlStateManager.CapabilityTracker[])IntStream.range(0, 8)
		.mapToObj(i -> new GlStateManager.CapabilityTracker(16384 + i))
		.toArray(GlStateManager.CapabilityTracker[]::new);
	private static final GlStateManager.ColorMaterialState COLOR_MATERIAL = new GlStateManager.ColorMaterialState();
	private static final GlStateManager.BlendFuncState BLEND = new GlStateManager.BlendFuncState();
	private static final GlStateManager.DepthTestState DEPTH = new GlStateManager.DepthTestState();
	private static final GlStateManager.FogState FOG = new GlStateManager.FogState();
	private static final GlStateManager.CullFaceState CULL = new GlStateManager.CullFaceState();
	private static final GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
	private static final GlStateManager.LogicOpState COLOR_LOGIC = new GlStateManager.LogicOpState();
	private static final GlStateManager.TexGenState TEX_GEN = new GlStateManager.TexGenState();
	private static final GlStateManager.ClearState CLEAR = new GlStateManager.ClearState();
	private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
	private static final FloatBuffer field_20771 = GlAllocationUtils.allocateFloatBuffer(4);
	private static final Vector3f field_20772 = Util.create(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::reciprocal);
	private static final Vector3f field_20773 = Util.create(new Vector3f(-0.2F, 1.0F, 0.7F), Vector3f::reciprocal);
	private static int activeTexture;
	private static final GlStateManager.Texture2DState[] TEXTURES = (GlStateManager.Texture2DState[])IntStream.range(0, 8)
		.mapToObj(i -> new GlStateManager.Texture2DState())
		.toArray(GlStateManager.Texture2DState[]::new);
	private static int shadeModel = 7425;
	private static final GlStateManager.CapabilityTracker RESCALE_NORMAL = new GlStateManager.CapabilityTracker(32826);
	private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
	private static final GlStateManager.Color4 COLOR = new GlStateManager.Color4();
	private static GlStateManager.FBOMode fboMode;

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
		ALPHA_TEST.capState.disable();
	}

	@Deprecated
	public static void enableAlphaTest() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		ALPHA_TEST.capState.enable();
	}

	@Deprecated
	public static void alphaFunc(int func, float ref) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (func != ALPHA_TEST.func || ref != ALPHA_TEST.ref) {
			ALPHA_TEST.func = func;
			ALPHA_TEST.ref = ref;
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
	public static void enableLight(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		LIGHT_ENABLE[i].enable();
	}

	@Deprecated
	public static void enableColorMaterial() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		COLOR_MATERIAL.capState.enable();
	}

	@Deprecated
	public static void disableColorMaterial() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		COLOR_MATERIAL.capState.disable();
	}

	@Deprecated
	public static void colorMaterial(int face, int mode) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (face != COLOR_MATERIAL.face || mode != COLOR_MATERIAL.mode) {
			COLOR_MATERIAL.face = face;
			COLOR_MATERIAL.mode = mode;
			GL11.glColorMaterial(face, mode);
		}
	}

	@Deprecated
	public static void light(int i, int j, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glLightfv(i, j, floatBuffer);
	}

	@Deprecated
	public static void lightModel(int i, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glLightModelfv(i, floatBuffer);
	}

	@Deprecated
	public static void normal3f(float f, float g, float h) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glNormal3f(f, g, h);
	}

	public static void disableDepthTest() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		DEPTH.capState.disable();
	}

	public static void enableDepthTest() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		DEPTH.capState.enable();
	}

	public static void depthFunc(int func) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (func != DEPTH.func) {
			DEPTH.func = func;
			GL11.glDepthFunc(func);
		}
	}

	public static void depthMask(boolean mask) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (mask != DEPTH.mask) {
			DEPTH.mask = mask;
			GL11.glDepthMask(mask);
		}
	}

	public static void disableBlend() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		BLEND.capState.disable();
	}

	public static void enableBlend() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		BLEND.capState.enable();
	}

	public static void blendFunc(int sfactor, int dfactor) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (sfactor != BLEND.sfactor || dfactor != BLEND.dfactor) {
			BLEND.sfactor = sfactor;
			BLEND.dfactor = dfactor;
			GL11.glBlendFunc(sfactor, dfactor);
		}
	}

	public static void blendFuncSeparate(int sfactor, int dfactor, int srcAlpha, int dstAlpha) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (sfactor != BLEND.sfactor || dfactor != BLEND.dfactor || srcAlpha != BLEND.srcAlpha || dstAlpha != BLEND.dstAlpha) {
			BLEND.sfactor = sfactor;
			BLEND.dfactor = dfactor;
			BLEND.srcAlpha = srcAlpha;
			BLEND.dstAlpha = dstAlpha;
			blendFuncseparate(sfactor, dfactor, srcAlpha, dstAlpha);
		}
	}

	public static void method_22883(float f, float g, float h, float i) {
		GL14.glBlendColor(f, g, h, i);
	}

	public static void blendEquation(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL14.glBlendEquation(i);
	}

	public static String initFramebufferSupport(GLCapabilities capabilities) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		if (capabilities.OpenGL30) {
			fboMode = GlStateManager.FBOMode.BASE;
			FramebufferInfo.target = 36160;
			FramebufferInfo.renderBufferTarget = 36161;
			FramebufferInfo.field_20459 = 36064;
			FramebufferInfo.attachment = 36096;
			FramebufferInfo.field_20461 = 36053;
			FramebufferInfo.field_20462 = 36054;
			FramebufferInfo.field_20463 = 36055;
			FramebufferInfo.field_20464 = 36059;
			FramebufferInfo.field_20465 = 36060;
			return "OpenGL 3.0";
		} else if (capabilities.GL_ARB_framebuffer_object) {
			fboMode = GlStateManager.FBOMode.ARB;
			FramebufferInfo.target = 36160;
			FramebufferInfo.renderBufferTarget = 36161;
			FramebufferInfo.field_20459 = 36064;
			FramebufferInfo.attachment = 36096;
			FramebufferInfo.field_20461 = 36053;
			FramebufferInfo.field_20463 = 36055;
			FramebufferInfo.field_20462 = 36054;
			FramebufferInfo.field_20464 = 36059;
			FramebufferInfo.field_20465 = 36060;
			return "ARB_framebuffer_object extension";
		} else if (capabilities.GL_EXT_framebuffer_object) {
			fboMode = GlStateManager.FBOMode.EXT;
			FramebufferInfo.target = 36160;
			FramebufferInfo.renderBufferTarget = 36161;
			FramebufferInfo.field_20459 = 36064;
			FramebufferInfo.attachment = 36096;
			FramebufferInfo.field_20461 = 36053;
			FramebufferInfo.field_20463 = 36055;
			FramebufferInfo.field_20462 = 36054;
			FramebufferInfo.field_20464 = 36059;
			FramebufferInfo.field_20465 = 36060;
			return "EXT_framebuffer_object extension";
		} else {
			throw new IllegalStateException("Could not initialize framebuffer support.");
		}
	}

	public static int getProgram(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetProgrami(i, j);
	}

	public static void attachShader(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glAttachShader(i, j);
	}

	public static void deleteShader(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glDeleteShader(i);
	}

	public static int createShader(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glCreateShader(i);
	}

	public static void shaderSource(int i, CharSequence charSequence) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glShaderSource(i, charSequence);
	}

	public static void compileShader(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glCompileShader(i);
	}

	public static int getShader(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetShaderi(i, j);
	}

	public static void useProgram(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUseProgram(i);
	}

	public static int createProgram() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glCreateProgram();
	}

	public static void deleteProgram(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glDeleteProgram(i);
	}

	public static void linkProgram(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glLinkProgram(i);
	}

	public static int getUniformLocation(int i, CharSequence charSequence) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetUniformLocation(i, charSequence);
	}

	public static void uniform1(int i, IntBuffer intBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform1iv(i, intBuffer);
	}

	public static void uniform1(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform1i(i, j);
	}

	public static void uniform1(int i, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform1fv(i, floatBuffer);
	}

	public static void uniform2(int i, IntBuffer intBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform2iv(i, intBuffer);
	}

	public static void uniform2(int i, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform2fv(i, floatBuffer);
	}

	public static void uniform3(int i, IntBuffer intBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform3iv(i, intBuffer);
	}

	public static void uniform3(int i, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform3fv(i, floatBuffer);
	}

	public static void uniform4(int i, IntBuffer intBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform4iv(i, intBuffer);
	}

	public static void uniform4(int i, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform4fv(i, floatBuffer);
	}

	public static void uniformMatrix2(int i, boolean bl, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniformMatrix2fv(i, bl, floatBuffer);
	}

	public static void uniformMatrix3(int i, boolean bl, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniformMatrix3fv(i, bl, floatBuffer);
	}

	public static void uniformMatrix4(int i, boolean bl, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniformMatrix4fv(i, bl, floatBuffer);
	}

	public static int getAttribLocation(int i, CharSequence charSequence) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetAttribLocation(i, charSequence);
	}

	public static int genBuffers() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL15.glGenBuffers();
	}

	public static void bindBuffers(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL15.glBindBuffer(i, j);
	}

	public static void bufferData(int i, ByteBuffer byteBuffer, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL15.glBufferData(i, byteBuffer, j);
	}

	public static void deleteBuffers(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL15.glDeleteBuffers(i);
	}

	public static void bindFramebuffer(int target, int framebuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		switch (fboMode) {
			case BASE:
				GL30.glBindFramebuffer(target, framebuffer);
				break;
			case ARB:
				ARBFramebufferObject.glBindFramebuffer(target, framebuffer);
				break;
			case EXT:
				EXTFramebufferObject.glBindFramebufferEXT(target, framebuffer);
		}
	}

	public static void bindRenderbuffer(int target, int renderbuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		switch (fboMode) {
			case BASE:
				GL30.glBindRenderbuffer(target, renderbuffer);
				break;
			case ARB:
				ARBFramebufferObject.glBindRenderbuffer(target, renderbuffer);
				break;
			case EXT:
				EXTFramebufferObject.glBindRenderbufferEXT(target, renderbuffer);
		}
	}

	public static void deleteRenderbuffers(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		switch (fboMode) {
			case BASE:
				GL30.glDeleteRenderbuffers(i);
				break;
			case ARB:
				ARBFramebufferObject.glDeleteRenderbuffers(i);
				break;
			case EXT:
				EXTFramebufferObject.glDeleteRenderbuffersEXT(i);
		}
	}

	public static void deleteFramebuffers(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		switch (fboMode) {
			case BASE:
				GL30.glDeleteFramebuffers(i);
				break;
			case ARB:
				ARBFramebufferObject.glDeleteFramebuffers(i);
				break;
			case EXT:
				EXTFramebufferObject.glDeleteFramebuffersEXT(i);
		}
	}

	public static int genFramebuffers() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		switch (fboMode) {
			case BASE:
				return GL30.glGenFramebuffers();
			case ARB:
				return ARBFramebufferObject.glGenFramebuffers();
			case EXT:
				return EXTFramebufferObject.glGenFramebuffersEXT();
			default:
				return -1;
		}
	}

	public static int genRenderbuffers() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		switch (fboMode) {
			case BASE:
				return GL30.glGenRenderbuffers();
			case ARB:
				return ARBFramebufferObject.glGenRenderbuffers();
			case EXT:
				return EXTFramebufferObject.glGenRenderbuffersEXT();
			default:
				return -1;
		}
	}

	public static void renderbufferStorage(int i, int j, int k, int l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		switch (fboMode) {
			case BASE:
				GL30.glRenderbufferStorage(i, j, k, l);
				break;
			case ARB:
				ARBFramebufferObject.glRenderbufferStorage(i, j, k, l);
				break;
			case EXT:
				EXTFramebufferObject.glRenderbufferStorageEXT(i, j, k, l);
		}
	}

	public static void framebufferRenderbuffer(int target, int attachment, int renderBufferTarget, int renderBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		switch (fboMode) {
			case BASE:
				GL30.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
				break;
			case ARB:
				ARBFramebufferObject.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
				break;
			case EXT:
				EXTFramebufferObject.glFramebufferRenderbufferEXT(target, attachment, renderBufferTarget, renderBuffer);
		}
	}

	public static int checkFramebufferStatus(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		switch (fboMode) {
			case BASE:
				return GL30.glCheckFramebufferStatus(i);
			case ARB:
				return ARBFramebufferObject.glCheckFramebufferStatus(i);
			case EXT:
				return EXTFramebufferObject.glCheckFramebufferStatusEXT(i);
			default:
				return -1;
		}
	}

	public static void framebufferTexture2D(int i, int j, int k, int l, int m) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		switch (fboMode) {
			case BASE:
				GL30.glFramebufferTexture2D(i, j, k, l, m);
				break;
			case ARB:
				ARBFramebufferObject.glFramebufferTexture2D(i, j, k, l, m);
				break;
			case EXT:
				EXTFramebufferObject.glFramebufferTexture2DEXT(i, j, k, l, m);
		}
	}

	public static void method_22066(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL13.glActiveTexture(i);
	}

	@Deprecated
	public static void clientActiveTexture(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL13.glClientActiveTexture(i);
	}

	@Deprecated
	public static void multiTexCoords2f(int i, float f, float g) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL13.glMultiTexCoord2f(i, f, g);
	}

	public static void blendFuncseparate(int i, int j, int k, int l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL14.glBlendFuncSeparate(i, j, k, l);
	}

	public static String getShaderInfoLog(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetShaderInfoLog(i, j);
	}

	public static String getProgramInfoLog(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetProgramInfoLog(i, j);
	}

	public static void method_23282() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		texEnv(8960, 8704, 34160);
		method_23281(7681, 34168);
	}

	public static void method_23283() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		texEnv(8960, 8704, 8448);
		method_22885(8448, 5890, 34168, 34166);
	}

	public static void method_22610(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		activeTexture(33985);
		enableTexture();
		matrixMode(5890);
		loadIdentity();
		float f = 1.0F / (float)(j - 1);
		scalef(f, f, f);
		matrixMode(5888);
		bindTexture(i);
		texParameter(3553, 10241, 9728);
		texParameter(3553, 10240, 9728);
		texParameter(3553, 10242, 10496);
		texParameter(3553, 10243, 10496);
		texEnv(8960, 8704, 34160);
		method_22885(34165, 34168, 5890, 5890);
		method_22886(7681, 34168);
		activeTexture(33984);
	}

	public static void method_22618() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		activeTexture(33985);
		disableTexture();
		activeTexture(33984);
	}

	private static void method_23281(int i, int j) {
		texEnv(8960, 34161, i);
		texEnv(8960, 34176, j);
		texEnv(8960, 34192, 768);
	}

	private static void method_22885(int i, int j, int k, int l) {
		texEnv(8960, 34161, i);
		texEnv(8960, 34176, j);
		texEnv(8960, 34192, 768);
		texEnv(8960, 34177, k);
		texEnv(8960, 34193, 768);
		texEnv(8960, 34178, l);
		texEnv(8960, 34194, 770);
	}

	private static void method_22886(int i, int j) {
		texEnv(8960, 34162, i);
		texEnv(8960, 34184, j);
		texEnv(8960, 34200, 770);
	}

	public static void method_22616(Matrix4f matrix4f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		enableLight(0);
		enableLight(1);
		Vector4f vector4f = new Vector4f(field_20772);
		vector4f.multiply(matrix4f);
		light(16384, 4611, method_22613(vector4f.getX(), vector4f.getY(), vector4f.getZ(), 0.0F));
		float f = 0.6F;
		light(16384, 4609, method_22613(0.6F, 0.6F, 0.6F, 1.0F));
		light(16384, 4608, method_22613(0.0F, 0.0F, 0.0F, 1.0F));
		light(16384, 4610, method_22613(0.0F, 0.0F, 0.0F, 1.0F));
		Vector4f vector4f2 = new Vector4f(field_20773);
		vector4f2.multiply(matrix4f);
		light(16385, 4611, method_22613(vector4f2.getX(), vector4f2.getY(), vector4f2.getZ(), 0.0F));
		light(16385, 4609, method_22613(0.6F, 0.6F, 0.6F, 1.0F));
		light(16385, 4608, method_22613(0.0F, 0.0F, 0.0F, 1.0F));
		light(16385, 4610, method_22613(0.0F, 0.0F, 0.0F, 1.0F));
		shadeModel(7424);
		float g = 0.4F;
		lightModel(2899, method_22613(0.4F, 0.4F, 0.4F, 1.0F));
	}

	public static void method_22617(Matrix4f matrix4f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		Matrix4f matrix4f2 = matrix4f.copy();
		matrix4f2.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-22.5F));
		matrix4f2.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(135.0F));
		method_22616(matrix4f2);
	}

	private static FloatBuffer method_22613(float f, float g, float h, float i) {
		field_20771.clear();
		field_20771.put(f).put(g).put(h).put(i);
		field_20771.flip();
		return field_20771;
	}

	public static void method_22887() {
		texGenMode(GlStateManager.TexCoord.S, 9216);
		texGenMode(GlStateManager.TexCoord.T, 9216);
		texGenMode(GlStateManager.TexCoord.R, 9216);
		texGenParam(GlStateManager.TexCoord.S, 9474, method_22613(1.0F, 0.0F, 0.0F, 0.0F));
		texGenParam(GlStateManager.TexCoord.T, 9474, method_22613(0.0F, 1.0F, 0.0F, 0.0F));
		texGenParam(GlStateManager.TexCoord.R, 9474, method_22613(0.0F, 0.0F, 1.0F, 0.0F));
		enableTexGen(GlStateManager.TexCoord.S);
		enableTexGen(GlStateManager.TexCoord.T);
		enableTexGen(GlStateManager.TexCoord.R);
	}

	public static void method_22888() {
		disableTexGen(GlStateManager.TexCoord.S);
		disableTexGen(GlStateManager.TexCoord.T);
		disableTexGen(GlStateManager.TexCoord.R);
	}

	public static void method_22889() {
		texEnv(2983, MATRIX_BUFFER);
		multMatrix(MATRIX_BUFFER);
		texEnv(2982, MATRIX_BUFFER);
		multMatrix(MATRIX_BUFFER);
	}

	@Deprecated
	public static void enableFog() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		FOG.capState.enable();
	}

	@Deprecated
	public static void disableFog() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		FOG.capState.disable();
	}

	@Deprecated
	public static void fogMode(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (i != FOG.mode) {
			FOG.mode = i;
			fogi(2917, i);
		}
	}

	@Deprecated
	public static void fogDensity(float f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (f != FOG.density) {
			FOG.density = f;
			GL11.glFogf(2914, f);
		}
	}

	@Deprecated
	public static void fogStart(float f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (f != FOG.start) {
			FOG.start = f;
			GL11.glFogf(2915, f);
		}
	}

	@Deprecated
	public static void fogEnd(float f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (f != FOG.end) {
			FOG.end = f;
			GL11.glFogf(2916, f);
		}
	}

	@Deprecated
	public static void fog(int i, float[] fs) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glFogfv(i, fs);
	}

	@Deprecated
	public static void fogi(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glFogi(i, j);
	}

	public static void enableCull() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		CULL.capState.enable();
	}

	public static void disableCull() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		CULL.capState.disable();
	}

	public static void polygonMode(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glPolygonMode(i, j);
	}

	public static void enablePolygonOffset() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		POLY_OFFSET.capFill.enable();
	}

	public static void disablePolygonOffset() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		POLY_OFFSET.capFill.disable();
	}

	public static void enableLineOffset() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		POLY_OFFSET.capLine.enable();
	}

	public static void disableLineOffset() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		POLY_OFFSET.capLine.disable();
	}

	public static void polygonOffset(float f, float g) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (f != POLY_OFFSET.factor || g != POLY_OFFSET.units) {
			POLY_OFFSET.factor = f;
			POLY_OFFSET.units = g;
			GL11.glPolygonOffset(f, g);
		}
	}

	public static void enableColorLogicOp() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		COLOR_LOGIC.capState.enable();
	}

	public static void disableColorLogicOp() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		COLOR_LOGIC.capState.disable();
	}

	public static void logicOp(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (i != COLOR_LOGIC.opcode) {
			COLOR_LOGIC.opcode = i;
			GL11.glLogicOp(i);
		}
	}

	@Deprecated
	public static void enableTexGen(GlStateManager.TexCoord texCoord) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		getGenCoordState(texCoord).capState.enable();
	}

	@Deprecated
	public static void disableTexGen(GlStateManager.TexCoord texCoord) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		getGenCoordState(texCoord).capState.disable();
	}

	@Deprecated
	public static void texGenMode(GlStateManager.TexCoord texCoord, int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlStateManager.TexGenCoordState texGenCoordState = getGenCoordState(texCoord);
		if (i != texGenCoordState.mode) {
			texGenCoordState.mode = i;
			GL11.glTexGeni(texGenCoordState.coord, 9472, i);
		}
	}

	@Deprecated
	public static void texGenParam(GlStateManager.TexCoord texCoord, int i, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glTexGenfv(getGenCoordState(texCoord).coord, i, floatBuffer);
	}

	@Deprecated
	private static GlStateManager.TexGenCoordState getGenCoordState(GlStateManager.TexCoord coord) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		switch (coord) {
			case S:
				return TEX_GEN.s;
			case T:
				return TEX_GEN.t;
			case R:
				return TEX_GEN.r;
			case Q:
				return TEX_GEN.q;
			default:
				return TEX_GEN.s;
		}
	}

	public static void activeTexture(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (activeTexture != i - 33984) {
			activeTexture = i - 33984;
			method_22066(i);
		}
	}

	public static void enableTexture() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		TEXTURES[activeTexture].capState.enable();
	}

	public static void disableTexture() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		TEXTURES[activeTexture].capState.disable();
	}

	@Deprecated
	public static void texEnv(int i, int j, int k) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glTexEnvi(i, j, k);
	}

	public static void texParameter(int i, int j, float f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glTexParameterf(i, j, f);
	}

	public static void texParameter(int i, int j, int k) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glTexParameteri(i, j, k);
	}

	public static int getTexLevelParameter(int i, int j, int k) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		return GL11.glGetTexLevelParameteri(i, j, k);
	}

	public static int getTexLevelParameter() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL11.glGenTextures();
	}

	public static void deleteTexture(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glDeleteTextures(i);

		for (GlStateManager.Texture2DState texture2DState : TEXTURES) {
			if (texture2DState.boundTexture == i) {
				texture2DState.boundTexture = -1;
			}
		}
	}

	public static void bindTexture(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (i != TEXTURES[activeTexture].boundTexture) {
			TEXTURES[activeTexture].boundTexture = i;
			GL11.glBindTexture(3553, i);
		}
	}

	public static void texImage2D(int i, int j, int k, int l, int m, int n, int o, int p, @Nullable IntBuffer intBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glTexImage2D(i, j, k, l, m, n, o, p, intBuffer);
	}

	public static void texSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p, long q) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glTexSubImage2D(i, j, k, l, m, n, o, p, q);
	}

	public static void getTexImage(int i, int j, int k, int l, long m) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glGetTexImage(i, j, k, l, m);
	}

	@Deprecated
	public static void shadeModel(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (i != shadeModel) {
			shadeModel = i;
			GL11.glShadeModel(i);
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

	public static void viewport(int i, int j, int k, int l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.Viewport.INSTANCE.x = i;
		GlStateManager.Viewport.INSTANCE.y = j;
		GlStateManager.Viewport.INSTANCE.width = k;
		GlStateManager.Viewport.INSTANCE.height = l;
		GL11.glViewport(i, j, k, l);
	}

	public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (bl != COLOR_MASK.red || bl2 != COLOR_MASK.green || bl3 != COLOR_MASK.blue || bl4 != COLOR_MASK.alpha) {
			COLOR_MASK.red = bl;
			COLOR_MASK.green = bl2;
			COLOR_MASK.blue = bl3;
			COLOR_MASK.alpha = bl4;
			GL11.glColorMask(bl, bl2, bl3, bl4);
		}
	}

	public static void stencilFunc(int i, int j, int k) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (i != STENCIL.subState.func || i != STENCIL.subState.field_16203 || i != STENCIL.subState.field_5147) {
			STENCIL.subState.func = i;
			STENCIL.subState.field_16203 = j;
			STENCIL.subState.field_5147 = k;
			GL11.glStencilFunc(i, j, k);
		}
	}

	public static void stencilMask(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (i != STENCIL.field_5153) {
			STENCIL.field_5153 = i;
			GL11.glStencilMask(i);
		}
	}

	public static void stencilOp(int i, int j, int k) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (i != STENCIL.field_5152 || j != STENCIL.field_5151 || k != STENCIL.field_5150) {
			STENCIL.field_5152 = i;
			STENCIL.field_5151 = j;
			STENCIL.field_5150 = k;
			GL11.glStencilOp(i, j, k);
		}
	}

	public static void clearDepth(double d) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (d != CLEAR.clearDepth) {
			CLEAR.clearDepth = d;
			GL11.glClearDepth(d);
		}
	}

	public static void clearColor(float f, float g, float h, float i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (f != CLEAR.clearColor.red || g != CLEAR.clearColor.green || h != CLEAR.clearColor.blue || i != CLEAR.clearColor.alpha) {
			CLEAR.clearColor.red = f;
			CLEAR.clearColor.green = g;
			CLEAR.clearColor.blue = h;
			CLEAR.clearColor.alpha = i;
			GL11.glClearColor(f, g, h, i);
		}
	}

	public static void clearStencil(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (i != CLEAR.field_16202) {
			CLEAR.field_16202 = i;
			GL11.glClearStencil(i);
		}
	}

	public static void clear(int mask, boolean getError) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glClear(mask);
		if (getError) {
			getError();
		}
	}

	@Deprecated
	public static void matrixMode(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glMatrixMode(i);
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
	public static void texEnv(int i, FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glGetFloatv(i, floatBuffer);
	}

	@Deprecated
	public static void ortho(double d, double e, double f, double g, double h, double i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glOrtho(d, e, f, g, h, i);
	}

	@Deprecated
	public static void rotatef(float f, float g, float h, float i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glRotatef(f, g, h, i);
	}

	@Deprecated
	public static void scalef(float f, float g, float h) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glScalef(f, g, h);
	}

	@Deprecated
	public static void scaled(double d, double e, double f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glScaled(d, e, f);
	}

	@Deprecated
	public static void translatef(float f, float g, float h) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glTranslatef(f, g, h);
	}

	@Deprecated
	public static void rotated(double d, double e, double f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glTranslated(d, e, f);
	}

	@Deprecated
	public static void multMatrix(FloatBuffer floatBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glMultMatrixf(floatBuffer);
	}

	@Deprecated
	public static void multMatrix(Matrix4f matrix4f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		matrix4f.writeToBuffer(MATRIX_BUFFER);
		MATRIX_BUFFER.rewind();
		multMatrix(MATRIX_BUFFER);
	}

	@Deprecated
	public static void color4f(float f, float g, float h, float i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (f != COLOR.red || g != COLOR.green || h != COLOR.blue || i != COLOR.alpha) {
			COLOR.red = f;
			COLOR.green = g;
			COLOR.blue = h;
			COLOR.alpha = i;
			GL11.glColor4f(f, g, h, i);
		}
	}

	@Deprecated
	public static void clearCurrentColor() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		COLOR.red = -1.0F;
		COLOR.green = -1.0F;
		COLOR.blue = -1.0F;
		COLOR.alpha = -1.0F;
	}

	@Deprecated
	public static void normalPointer(int i, int j, long l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glNormalPointer(i, j, l);
	}

	@Deprecated
	public static void texCoordPointer(int i, int j, int k, long l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glTexCoordPointer(i, j, k, l);
	}

	@Deprecated
	public static void vertexPointer(int i, int j, int k, long l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glVertexPointer(i, j, k, l);
	}

	@Deprecated
	public static void colorPointer(int i, int j, int k, long l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glColorPointer(i, j, k, l);
	}

	public static void vertexAttribPointer(int i, int j, int k, boolean bl, int l, long m) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glVertexAttribPointer(i, j, k, bl, l, m);
	}

	@Deprecated
	public static void enableClientState(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glEnableClientState(i);
	}

	@Deprecated
	public static void disableClientState(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glDisableClientState(i);
	}

	public static void enableVertexAttribArray(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glEnableVertexAttribArray(i);
	}

	public static void method_22607(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glEnableVertexAttribArray(i);
	}

	public static void drawArrays(int i, int j, int k) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glDrawArrays(i, j, k);
	}

	public static void lineWidth(float f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glLineWidth(f);
	}

	public static void pixelStore(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glPixelStorei(i, j);
	}

	public static void pixelTransfer(int i, float f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glPixelTransferf(i, f);
	}

	public static void readPixels(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glReadPixels(i, j, k, l, m, n, byteBuffer);
	}

	public static int getError() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL11.glGetError();
	}

	public static String getString(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL11.glGetString(i);
	}

	public static int getInteger(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL11.glGetInteger(i);
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	static class AlphaTestState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(3008);
		public int func = 519;
		public float ref = -1.0F;

		private AlphaTestState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class BlendFuncState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(3042);
		public int sfactor = 1;
		public int dfactor = 0;
		public int srcAlpha = 1;
		public int dstAlpha = 0;

		private BlendFuncState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class CapabilityTracker {
		private final int cap;
		private boolean state;

		public CapabilityTracker(int i) {
			this.cap = i;
		}

		public void disable() {
			this.setState(false);
		}

		public void enable() {
			this.setState(true);
		}

		public void setState(boolean bl) {
			RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
			if (bl != this.state) {
				this.state = bl;
				if (bl) {
					GL11.glEnable(this.cap);
				} else {
					GL11.glDisable(this.cap);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class ClearState {
		public double clearDepth = 1.0;
		public final GlStateManager.Color4 clearColor = new GlStateManager.Color4(0.0F, 0.0F, 0.0F, 0.0F);
		public int field_16202;

		private ClearState() {
		}
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	static class Color4 {
		public float red = 1.0F;
		public float green = 1.0F;
		public float blue = 1.0F;
		public float alpha = 1.0F;

		public Color4() {
			this(1.0F, 1.0F, 1.0F, 1.0F);
		}

		public Color4(float f, float g, float h, float i) {
			this.red = f;
			this.green = g;
			this.blue = h;
			this.alpha = i;
		}
	}

	@Environment(EnvType.CLIENT)
	static class ColorMask {
		public boolean red = true;
		public boolean green = true;
		public boolean blue = true;
		public boolean alpha = true;

		private ColorMask() {
		}
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	static class ColorMaterialState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(2903);
		public int face = 1032;
		public int mode = 5634;

		private ColorMaterialState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class CullFaceState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(2884);
		public int mode = 1029;

		private CullFaceState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class DepthTestState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(2929);
		public boolean mask = true;
		public int func = 513;

		private DepthTestState() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum DestFactor {
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

		public final int value;

		private DestFactor(int j) {
			this.value = j;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum FBOMode {
		BASE,
		ARB,
		EXT;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public static enum FogMode {
		LINEAR(9729),
		EXP(2048),
		EXP2(2049);

		public final int glValue;

		private FogMode(int j) {
			this.glValue = j;
		}
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	static class FogState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(2912);
		public int mode = 2048;
		public float density = 1.0F;
		public float start;
		public float end = 1.0F;

		private FogState() {
		}
	}

	@Environment(EnvType.CLIENT)
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

		public final int glValue;

		private LogicOp(int j) {
			this.glValue = j;
		}
	}

	@Environment(EnvType.CLIENT)
	static class LogicOpState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(3058);
		public int opcode = 5379;

		private LogicOpState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class PolygonOffsetState {
		public final GlStateManager.CapabilityTracker capFill = new GlStateManager.CapabilityTracker(32823);
		public final GlStateManager.CapabilityTracker capLine = new GlStateManager.CapabilityTracker(10754);
		public float factor;
		public float units;

		private PolygonOffsetState() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum SourceFactor {
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

		public final int value;

		private SourceFactor(int j) {
			this.value = j;
		}
	}

	@Environment(EnvType.CLIENT)
	static class StencilState {
		public final GlStateManager.StencilSubState subState = new GlStateManager.StencilSubState();
		public int field_5153 = -1;
		public int field_5152 = 7680;
		public int field_5151 = 7680;
		public int field_5150 = 7680;

		private StencilState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class StencilSubState {
		public int func = 519;
		public int field_16203;
		public int field_5147 = -1;

		private StencilSubState() {
		}
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public static enum TexCoord {
		S,
		T,
		R,
		Q;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	static class TexGenCoordState {
		public final GlStateManager.CapabilityTracker capState;
		public final int coord;
		public int mode = -1;

		public TexGenCoordState(int i, int j) {
			this.coord = i;
			this.capState = new GlStateManager.CapabilityTracker(j);
		}
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	static class TexGenState {
		public final GlStateManager.TexGenCoordState s = new GlStateManager.TexGenCoordState(8192, 3168);
		public final GlStateManager.TexGenCoordState t = new GlStateManager.TexGenCoordState(8193, 3169);
		public final GlStateManager.TexGenCoordState r = new GlStateManager.TexGenCoordState(8194, 3170);
		public final GlStateManager.TexGenCoordState q = new GlStateManager.TexGenCoordState(8195, 3171);

		private TexGenState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class Texture2DState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(3553);
		public int boundTexture;

		private Texture2DState() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Viewport {
		INSTANCE;

		protected int x;
		protected int y;
		protected int width;
		protected int height;
	}
}
