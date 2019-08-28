package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.util.Untracker;
import net.minecraft.client.util.math.Matrix4f;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
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
	private static final FloatBuffer COLOR_BUFFER = GLX.make(MemoryUtil.memAllocFloat(4), floatBuffer -> Untracker.untrack(MemoryUtil.memAddress(floatBuffer)));
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
	private static final GlStateManager.CapabilityTracker NORMALIZE = new GlStateManager.CapabilityTracker(2977);
	private static int activeTexture;
	private static final GlStateManager.Texture2DState[] TEXTURES = (GlStateManager.Texture2DState[])IntStream.range(0, 8)
		.mapToObj(i -> new GlStateManager.Texture2DState())
		.toArray(GlStateManager.Texture2DState[]::new);
	private static int shadeModel = 7425;
	private static final GlStateManager.CapabilityTracker RESCALE_NORMAL = new GlStateManager.CapabilityTracker(32826);
	private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
	private static final GlStateManager.Color4 COLOR = new GlStateManager.Color4();
	private static GlStateManager.FBOMode fboMode;

	public static void pushLightingAttributes() {
		GL11.glPushAttrib(8256);
	}

	public static void pushTextureAttributes() {
		GL11.glPushAttrib(270336);
	}

	public static void popAttributes() {
		GL11.glPopAttrib();
	}

	public static void disableAlphaTest() {
		ALPHA_TEST.capState.disable();
	}

	public static void enableAlphaTest() {
		ALPHA_TEST.capState.enable();
	}

	public static void alphaFunc(int i, float f) {
		if (i != ALPHA_TEST.func || f != ALPHA_TEST.ref) {
			ALPHA_TEST.func = i;
			ALPHA_TEST.ref = f;
			GL11.glAlphaFunc(i, f);
		}
	}

	public static void enableLighting() {
		LIGHTING.enable();
	}

	public static void disableLighting() {
		LIGHTING.disable();
	}

	public static void enableLight(int i) {
		LIGHT_ENABLE[i].enable();
	}

	public static void disableLight(int i) {
		LIGHT_ENABLE[i].disable();
	}

	public static void enableColorMaterial() {
		COLOR_MATERIAL.capState.enable();
	}

	public static void disableColorMaterial() {
		COLOR_MATERIAL.capState.disable();
	}

	public static void colorMaterial(int i, int j) {
		if (i != COLOR_MATERIAL.face || j != COLOR_MATERIAL.mode) {
			COLOR_MATERIAL.face = i;
			COLOR_MATERIAL.mode = j;
			GL11.glColorMaterial(i, j);
		}
	}

	public static void light(int i, int j, FloatBuffer floatBuffer) {
		GL11.glLightfv(i, j, floatBuffer);
	}

	public static void lightModel(int i, FloatBuffer floatBuffer) {
		GL11.glLightModelfv(i, floatBuffer);
	}

	public static void normal3f(float f, float g, float h) {
		GL11.glNormal3f(f, g, h);
	}

	public static void disableDepthTest() {
		DEPTH.capState.disable();
	}

	public static void enableDepthTest() {
		DEPTH.capState.enable();
	}

	public static void depthFunc(int i) {
		if (i != DEPTH.func) {
			DEPTH.func = i;
			GL11.glDepthFunc(i);
		}
	}

	public static void depthMask(boolean bl) {
		if (bl != DEPTH.mask) {
			DEPTH.mask = bl;
			GL11.glDepthMask(bl);
		}
	}

	public static void disableBlend() {
		BLEND.capState.disable();
	}

	public static void enableBlend() {
		BLEND.capState.enable();
	}

	public static void blendFunc(int i, int j) {
		if (i != BLEND.sfactor || j != BLEND.dfactor) {
			BLEND.sfactor = i;
			BLEND.dfactor = j;
			GL11.glBlendFunc(i, j);
		}
	}

	public static void blendFuncSeparate(int i, int j, int k, int l) {
		if (i != BLEND.sfactor || j != BLEND.dfactor || k != BLEND.srcAlpha || l != BLEND.dstAlpha) {
			BLEND.sfactor = i;
			BLEND.dfactor = j;
			BLEND.srcAlpha = k;
			BLEND.dstAlpha = l;
			blendFuncseparate(i, j, k, l);
		}
	}

	public static void blendEquation(int i) {
		GL14.glBlendEquation(i);
	}

	public static void setupSolidRenderingTextureCombine(int i) {
		COLOR_BUFFER.put(0, (float)(i >> 16 & 0xFF) / 255.0F);
		COLOR_BUFFER.put(1, (float)(i >> 8 & 0xFF) / 255.0F);
		COLOR_BUFFER.put(2, (float)(i >> 0 & 0xFF) / 255.0F);
		COLOR_BUFFER.put(3, (float)(i >> 24 & 0xFF) / 255.0F);
		RenderSystem.texEnv(8960, 8705, COLOR_BUFFER);
		RenderSystem.texEnv(8960, 8704, 34160);
		RenderSystem.texEnv(8960, 34161, 7681);
		RenderSystem.texEnv(8960, 34176, 34166);
		RenderSystem.texEnv(8960, 34192, 768);
		RenderSystem.texEnv(8960, 34162, 7681);
		RenderSystem.texEnv(8960, 34184, 5890);
		RenderSystem.texEnv(8960, 34200, 770);
	}

	public static void tearDownSolidRenderingTextureCombine() {
		RenderSystem.texEnv(8960, 8704, 8448);
		RenderSystem.texEnv(8960, 34161, 8448);
		RenderSystem.texEnv(8960, 34162, 8448);
		RenderSystem.texEnv(8960, 34176, 5890);
		RenderSystem.texEnv(8960, 34184, 5890);
		RenderSystem.texEnv(8960, 34192, 768);
		RenderSystem.texEnv(8960, 34200, 770);
	}

	public static String initFramebufferSupport(GLCapabilities gLCapabilities) {
		if (gLCapabilities.OpenGL30) {
			fboMode = GlStateManager.FBOMode.BASE;
			FramebufferInfo.field_20457 = 36160;
			FramebufferInfo.field_20458 = 36161;
			FramebufferInfo.field_20459 = 36064;
			FramebufferInfo.field_20460 = 36096;
			FramebufferInfo.field_20461 = 36053;
			FramebufferInfo.field_20462 = 36054;
			FramebufferInfo.field_20463 = 36055;
			FramebufferInfo.field_20464 = 36059;
			FramebufferInfo.field_20465 = 36060;
			return "OpenGL 3.0";
		} else if (gLCapabilities.GL_ARB_framebuffer_object) {
			fboMode = GlStateManager.FBOMode.ARB;
			FramebufferInfo.field_20457 = 36160;
			FramebufferInfo.field_20458 = 36161;
			FramebufferInfo.field_20459 = 36064;
			FramebufferInfo.field_20460 = 36096;
			FramebufferInfo.field_20461 = 36053;
			FramebufferInfo.field_20463 = 36055;
			FramebufferInfo.field_20462 = 36054;
			FramebufferInfo.field_20464 = 36059;
			FramebufferInfo.field_20465 = 36060;
			return "ARB_framebuffer_object extension";
		} else if (gLCapabilities.GL_EXT_framebuffer_object) {
			fboMode = GlStateManager.FBOMode.EXT;
			FramebufferInfo.field_20457 = 36160;
			FramebufferInfo.field_20458 = 36161;
			FramebufferInfo.field_20459 = 36064;
			FramebufferInfo.field_20460 = 36096;
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
		return GL20.glGetProgrami(i, j);
	}

	public static void attachShader(int i, int j) {
		GL20.glAttachShader(i, j);
	}

	public static void deleteShader(int i) {
		GL20.glDeleteShader(i);
	}

	public static int createShader(int i) {
		return GL20.glCreateShader(i);
	}

	public static void shaderSource(int i, CharSequence charSequence) {
		GL20.glShaderSource(i, charSequence);
	}

	public static void compileShader(int i) {
		GL20.glCompileShader(i);
	}

	public static int getShader(int i, int j) {
		return GL20.glGetShaderi(i, j);
	}

	public static void useProgram(int i) {
		GL20.glUseProgram(i);
	}

	public static int createProgram() {
		return GL20.glCreateProgram();
	}

	public static void deleteProgram(int i) {
		GL20.glDeleteProgram(i);
	}

	public static void linkProgram(int i) {
		GL20.glLinkProgram(i);
	}

	public static int getUniformLocation(int i, CharSequence charSequence) {
		return GL20.glGetUniformLocation(i, charSequence);
	}

	public static void uniform1(int i, IntBuffer intBuffer) {
		GL20.glUniform1iv(i, intBuffer);
	}

	public static void uniform1(int i, int j) {
		GL20.glUniform1i(i, j);
	}

	public static void uniform1(int i, FloatBuffer floatBuffer) {
		GL20.glUniform1fv(i, floatBuffer);
	}

	public static void uniform2(int i, IntBuffer intBuffer) {
		GL20.glUniform2iv(i, intBuffer);
	}

	public static void uniform2(int i, FloatBuffer floatBuffer) {
		GL20.glUniform2fv(i, floatBuffer);
	}

	public static void uniform3(int i, IntBuffer intBuffer) {
		GL20.glUniform3iv(i, intBuffer);
	}

	public static void uniform3(int i, FloatBuffer floatBuffer) {
		GL20.glUniform3fv(i, floatBuffer);
	}

	public static void uniform4(int i, IntBuffer intBuffer) {
		GL20.glUniform4iv(i, intBuffer);
	}

	public static void uniform4(int i, FloatBuffer floatBuffer) {
		GL20.glUniform4fv(i, floatBuffer);
	}

	public static void uniformMatrix2(int i, boolean bl, FloatBuffer floatBuffer) {
		GL20.glUniformMatrix2fv(i, bl, floatBuffer);
	}

	public static void uniformMatrix3(int i, boolean bl, FloatBuffer floatBuffer) {
		GL20.glUniformMatrix3fv(i, bl, floatBuffer);
	}

	public static void uniformMatrix4(int i, boolean bl, FloatBuffer floatBuffer) {
		GL20.glUniformMatrix4fv(i, bl, floatBuffer);
	}

	public static int getAttribLocation(int i, CharSequence charSequence) {
		return GL20.glGetAttribLocation(i, charSequence);
	}

	public static int genBuffers() {
		return GL15.glGenBuffers();
	}

	public static void bindBuffers(int i, int j) {
		GL15.glBindBuffer(i, j);
	}

	public static void bufferData(int i, ByteBuffer byteBuffer, int j) {
		GL15.glBufferData(i, byteBuffer, j);
	}

	public static void deleteBuffers(int i) {
		GL15.glDeleteBuffers(i);
	}

	public static void bindFramebuffer(int i, int j) {
		switch (fboMode) {
			case BASE:
				GL30.glBindFramebuffer(i, j);
				break;
			case ARB:
				ARBFramebufferObject.glBindFramebuffer(i, j);
				break;
			case EXT:
				EXTFramebufferObject.glBindFramebufferEXT(i, j);
		}
	}

	public static void bindRenderbuffer(int i, int j) {
		switch (fboMode) {
			case BASE:
				GL30.glBindRenderbuffer(i, j);
				break;
			case ARB:
				ARBFramebufferObject.glBindRenderbuffer(i, j);
				break;
			case EXT:
				EXTFramebufferObject.glBindRenderbufferEXT(i, j);
		}
	}

	public static void deleteRenderbuffers(int i) {
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

	public static void framebufferRenderbuffer(int i, int j, int k, int l) {
		switch (fboMode) {
			case BASE:
				GL30.glFramebufferRenderbuffer(i, j, k, l);
				break;
			case ARB:
				ARBFramebufferObject.glFramebufferRenderbuffer(i, j, k, l);
				break;
			case EXT:
				EXTFramebufferObject.glFramebufferRenderbufferEXT(i, j, k, l);
		}
	}

	public static int checkFramebufferStatus(int i) {
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
		GL13.glActiveTexture(i);
	}

	public static void clientActiveTexture(int i) {
		GL13.glClientActiveTexture(i);
	}

	public static void multiTexCoords2f(int i, float f, float g) {
		GL13.glMultiTexCoord2f(i, f, g);
	}

	public static void blendFuncseparate(int i, int j, int k, int l) {
		GL14.glBlendFuncSeparate(i, j, k, l);
	}

	public static String getShaderInfoLog(int i, int j) {
		return GL20.glGetShaderInfoLog(i, j);
	}

	public static String getProgramInfoLog(int i, int j) {
		return GL20.glGetProgramInfoLog(i, j);
	}

	public static void enableFog() {
		FOG.capState.enable();
	}

	public static void disableFog() {
		FOG.capState.disable();
	}

	public static void fogMode(int i) {
		if (i != FOG.mode) {
			FOG.mode = i;
			fogi(2917, i);
		}
	}

	public static void fogDensity(float f) {
		if (f != FOG.density) {
			FOG.density = f;
			GL11.glFogf(2914, f);
		}
	}

	public static void fogStart(float f) {
		if (f != FOG.start) {
			FOG.start = f;
			GL11.glFogf(2915, f);
		}
	}

	public static void fogEnd(float f) {
		if (f != FOG.end) {
			FOG.end = f;
			GL11.glFogf(2916, f);
		}
	}

	public static void fog(int i, FloatBuffer floatBuffer) {
		GL11.glFogfv(i, floatBuffer);
	}

	public static void fogi(int i, int j) {
		GL11.glFogi(i, j);
	}

	public static void enableCull() {
		CULL.capState.enable();
	}

	public static void disableCull() {
		CULL.capState.disable();
	}

	public static void cullFace(int i) {
		if (i != CULL.mode) {
			CULL.mode = i;
			GL11.glCullFace(i);
		}
	}

	public static void polygonMode(int i, int j) {
		GL11.glPolygonMode(i, j);
	}

	public static void enablePolygonOffset() {
		POLY_OFFSET.capFill.enable();
	}

	public static void disablePolygonOffset() {
		POLY_OFFSET.capFill.disable();
	}

	public static void enableLineOffset() {
		POLY_OFFSET.capLine.enable();
	}

	public static void disableLineOffset() {
		POLY_OFFSET.capLine.disable();
	}

	public static void polygonOffset(float f, float g) {
		if (f != POLY_OFFSET.factor || g != POLY_OFFSET.units) {
			POLY_OFFSET.factor = f;
			POLY_OFFSET.units = g;
			GL11.glPolygonOffset(f, g);
		}
	}

	public static void enableColorLogicOp() {
		COLOR_LOGIC.capState.enable();
	}

	public static void disableColorLogicOp() {
		COLOR_LOGIC.capState.disable();
	}

	public static void logicOp(int i) {
		if (i != COLOR_LOGIC.opcode) {
			COLOR_LOGIC.opcode = i;
			GL11.glLogicOp(i);
		}
	}

	public static void enableTexGen(GlStateManager.TexCoord texCoord) {
		getGenCoordState(texCoord).capState.enable();
	}

	public static void disableTexGen(GlStateManager.TexCoord texCoord) {
		getGenCoordState(texCoord).capState.disable();
	}

	public static void texGenMode(GlStateManager.TexCoord texCoord, int i) {
		GlStateManager.TexGenCoordState texGenCoordState = getGenCoordState(texCoord);
		if (i != texGenCoordState.mode) {
			texGenCoordState.mode = i;
			GL11.glTexGeni(texGenCoordState.coord, 9472, i);
		}
	}

	public static void texGenParam(GlStateManager.TexCoord texCoord, int i, FloatBuffer floatBuffer) {
		GL11.glTexGenfv(getGenCoordState(texCoord).coord, i, floatBuffer);
	}

	private static GlStateManager.TexGenCoordState getGenCoordState(GlStateManager.TexCoord texCoord) {
		switch (texCoord) {
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
		if (activeTexture != i - 33984) {
			activeTexture = i - 33984;
			method_22066(i);
		}
	}

	public static void enableTexture() {
		TEXTURES[activeTexture].capState.enable();
	}

	public static void disableTexture() {
		TEXTURES[activeTexture].capState.disable();
	}

	public static void texEnv(int i, int j, FloatBuffer floatBuffer) {
		GL11.glTexEnvfv(i, j, floatBuffer);
	}

	public static void texEnv(int i, int j, int k) {
		GL11.glTexEnvi(i, j, k);
	}

	public static void texEnv(int i, int j, float f) {
		GL11.glTexEnvf(i, j, f);
	}

	public static void texParameter(int i, int j, float f) {
		GL11.glTexParameterf(i, j, f);
	}

	public static void texParameter(int i, int j, int k) {
		GL11.glTexParameteri(i, j, k);
	}

	public static int getTexLevelParameter(int i, int j, int k) {
		return GL11.glGetTexLevelParameteri(i, j, k);
	}

	public static int getTexLevelParameter() {
		return GL11.glGenTextures();
	}

	public static void deleteTexture(int i) {
		GL11.glDeleteTextures(i);

		for (GlStateManager.Texture2DState texture2DState : TEXTURES) {
			if (texture2DState.boundTexture == i) {
				texture2DState.boundTexture = -1;
			}
		}
	}

	public static void bindTexture(int i) {
		if (i != TEXTURES[activeTexture].boundTexture) {
			TEXTURES[activeTexture].boundTexture = i;
			GL11.glBindTexture(3553, i);
		}
	}

	public static void texImage2D(int i, int j, int k, int l, int m, int n, int o, int p, @Nullable IntBuffer intBuffer) {
		GL11.glTexImage2D(i, j, k, l, m, n, o, p, intBuffer);
	}

	public static void texSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p, long q) {
		GL11.glTexSubImage2D(i, j, k, l, m, n, o, p, q);
	}

	public static void copyTexSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p) {
		GL11.glCopyTexSubImage2D(i, j, k, l, m, n, o, p);
	}

	public static void getTexImage(int i, int j, int k, int l, long m) {
		GL11.glGetTexImage(i, j, k, l, m);
	}

	public static void enableNormalize() {
		NORMALIZE.enable();
	}

	public static void disableNormalize() {
		NORMALIZE.disable();
	}

	public static void shadeModel(int i) {
		if (i != shadeModel) {
			shadeModel = i;
			GL11.glShadeModel(i);
		}
	}

	public static void enableRescaleNormal() {
		RESCALE_NORMAL.enable();
	}

	public static void disableRescaleNormal() {
		RESCALE_NORMAL.disable();
	}

	public static void viewport(int i, int j, int k, int l) {
		GlStateManager.Viewport.INSTANCE.x = i;
		GlStateManager.Viewport.INSTANCE.y = j;
		GlStateManager.Viewport.INSTANCE.width = k;
		GlStateManager.Viewport.INSTANCE.height = l;
		GL11.glViewport(i, j, k, l);
	}

	public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		if (bl != COLOR_MASK.red || bl2 != COLOR_MASK.green || bl3 != COLOR_MASK.blue || bl4 != COLOR_MASK.alpha) {
			COLOR_MASK.red = bl;
			COLOR_MASK.green = bl2;
			COLOR_MASK.blue = bl3;
			COLOR_MASK.alpha = bl4;
			GL11.glColorMask(bl, bl2, bl3, bl4);
		}
	}

	public static void stencilFunc(int i, int j, int k) {
		if (i != STENCIL.subState.func || i != STENCIL.subState.field_16203 || i != STENCIL.subState.field_5147) {
			STENCIL.subState.func = i;
			STENCIL.subState.field_16203 = j;
			STENCIL.subState.field_5147 = k;
			GL11.glStencilFunc(i, j, k);
		}
	}

	public static void stencilMask(int i) {
		if (i != STENCIL.field_5153) {
			STENCIL.field_5153 = i;
			GL11.glStencilMask(i);
		}
	}

	public static void stencilOp(int i, int j, int k) {
		if (i != STENCIL.field_5152 || j != STENCIL.field_5151 || k != STENCIL.field_5150) {
			STENCIL.field_5152 = i;
			STENCIL.field_5151 = j;
			STENCIL.field_5150 = k;
			GL11.glStencilOp(i, j, k);
		}
	}

	public static void clearDepth(double d) {
		if (d != CLEAR.clearDepth) {
			CLEAR.clearDepth = d;
			GL11.glClearDepth(d);
		}
	}

	public static void clearColor(float f, float g, float h, float i) {
		if (f != CLEAR.clearColor.red || g != CLEAR.clearColor.green || h != CLEAR.clearColor.blue || i != CLEAR.clearColor.alpha) {
			CLEAR.clearColor.red = f;
			CLEAR.clearColor.green = g;
			CLEAR.clearColor.blue = h;
			CLEAR.clearColor.alpha = i;
			GL11.glClearColor(f, g, h, i);
		}
	}

	public static void clearStencil(int i) {
		if (i != CLEAR.field_16202) {
			CLEAR.field_16202 = i;
			GL11.glClearStencil(i);
		}
	}

	public static void clear(int i, boolean bl) {
		GL11.glClear(i);
		if (bl) {
			RenderSystem.getError();
		}
	}

	public static void matrixMode(int i) {
		GL11.glMatrixMode(i);
	}

	public static void loadIdentity() {
		GL11.glLoadIdentity();
	}

	public static void pushMatrix() {
		GL11.glPushMatrix();
	}

	public static void popMatrix() {
		GL11.glPopMatrix();
	}

	public static void getMatrix(int i, FloatBuffer floatBuffer) {
		GL11.glGetFloatv(i, floatBuffer);
	}

	public static Matrix4f getMatrix4f(int i) {
		getMatrix(i, MATRIX_BUFFER);
		MATRIX_BUFFER.rewind();
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.setFromBuffer(MATRIX_BUFFER);
		MATRIX_BUFFER.rewind();
		return matrix4f;
	}

	public static void ortho(double d, double e, double f, double g, double h, double i) {
		GL11.glOrtho(d, e, f, g, h, i);
	}

	public static void rotatef(float f, float g, float h, float i) {
		GL11.glRotatef(f, g, h, i);
	}

	public static void rotated(double d, double e, double f, double g) {
		GL11.glRotated(d, e, f, g);
	}

	public static void scalef(float f, float g, float h) {
		GL11.glScalef(f, g, h);
	}

	public static void scaled(double d, double e, double f) {
		GL11.glScaled(d, e, f);
	}

	public static void translatef(float f, float g, float h) {
		GL11.glTranslatef(f, g, h);
	}

	public static void translated(double d, double e, double f) {
		GL11.glTranslated(d, e, f);
	}

	public static void multMatrix(FloatBuffer floatBuffer) {
		GL11.glMultMatrixf(floatBuffer);
	}

	public static void multMatrix(Matrix4f matrix4f) {
		matrix4f.putIntoBuffer(MATRIX_BUFFER);
		MATRIX_BUFFER.rewind();
		multMatrix(MATRIX_BUFFER);
	}

	public static void color4f(float f, float g, float h, float i) {
		if (f != COLOR.red || g != COLOR.green || h != COLOR.blue || i != COLOR.alpha) {
			COLOR.red = f;
			COLOR.green = g;
			COLOR.blue = h;
			COLOR.alpha = i;
			GL11.glColor4f(f, g, h, i);
		}
	}

	public static void texCoord2f(float f, float g) {
		GL11.glTexCoord2f(f, g);
	}

	public static void vertex3f(float f, float g, float h) {
		GL11.glVertex3f(f, g, h);
	}

	public static void clearCurrentColor() {
		COLOR.red = -1.0F;
		COLOR.green = -1.0F;
		COLOR.blue = -1.0F;
		COLOR.alpha = -1.0F;
	}

	public static void normalPointer(int i, int j, int k) {
		GL11.glNormalPointer(i, j, (long)k);
	}

	public static void normalPointer(int i, int j, ByteBuffer byteBuffer) {
		GL11.glNormalPointer(i, j, byteBuffer);
	}

	public static void texCoordPointer(int i, int j, int k, int l) {
		GL11.glTexCoordPointer(i, j, k, (long)l);
	}

	public static void texCoordPointer(int i, int j, int k, ByteBuffer byteBuffer) {
		GL11.glTexCoordPointer(i, j, k, byteBuffer);
	}

	public static void vertexPointer(int i, int j, int k, int l) {
		GL11.glVertexPointer(i, j, k, (long)l);
	}

	public static void vertexPointer(int i, int j, int k, ByteBuffer byteBuffer) {
		GL11.glVertexPointer(i, j, k, byteBuffer);
	}

	public static void colorPointer(int i, int j, int k, int l) {
		GL11.glColorPointer(i, j, k, (long)l);
	}

	public static void colorPointer(int i, int j, int k, ByteBuffer byteBuffer) {
		GL11.glColorPointer(i, j, k, byteBuffer);
	}

	public static void disableClientState(int i) {
		GL11.glDisableClientState(i);
	}

	public static void enableClientState(int i) {
		GL11.glEnableClientState(i);
	}

	public static void begin(int i) {
		GL11.glBegin(i);
	}

	public static void end() {
		GL11.glEnd();
	}

	public static void drawArrays(int i, int j, int k) {
		GL11.glDrawArrays(i, j, k);
	}

	public static void lineWidth(float f) {
		GL11.glLineWidth(f);
	}

	public static void callList(int i) {
		GL11.glCallList(i);
	}

	public static void deleteLists(int i, int j) {
		GL11.glDeleteLists(i, j);
	}

	public static void newList(int i, int j) {
		GL11.glNewList(i, j);
	}

	public static void endList() {
		GL11.glEndList();
	}

	public static int genLists(int i) {
		return GL11.glGenLists(i);
	}

	public static void pixelStore(int i, int j) {
		GL11.glPixelStorei(i, j);
	}

	public static void pixelTransfer(int i, float f) {
		GL11.glPixelTransferf(i, f);
	}

	public static void readPixels(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
		GL11.glReadPixels(i, j, k, l, m, n, byteBuffer);
	}

	public static void readPixels(int i, int j, int k, int l, int m, int n, long o) {
		GL11.glReadPixels(i, j, k, l, m, n, o);
	}

	public static int getError() {
		return GL11.glGetError();
	}

	public static String getString(int i) {
		return GL11.glGetString(i);
	}

	public static void getInteger(int i, IntBuffer intBuffer) {
		GL11.glGetIntegerv(i, intBuffer);
	}

	public static int getInteger(int i) {
		return GL11.glGetInteger(i);
	}

	public static void beginRenderMode(GlStateManager.RenderMode renderMode) {
		renderMode.begin();
	}

	public static void endRenderMode(GlStateManager.RenderMode renderMode) {
		renderMode.end();
	}

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
	public static enum FBOMode {
		BASE,
		ARB,
		EXT;
	}

	@Environment(EnvType.CLIENT)
	public static enum FaceSides {
		FRONT(1028),
		BACK(1029),
		FRONT_AND_BACK(1032);

		public final int glValue;

		private FaceSides(int j) {
			this.glValue = j;
		}
	}

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
	public static enum RenderMode {
		DEFAULT {
			@Override
			public void begin() {
				RenderSystem.disableAlphaTest();
				RenderSystem.alphaFunc(519, 0.0F);
				RenderSystem.disableLighting();
				RenderSystem.lightModel(2899, GuiLighting.singletonBuffer(0.2F, 0.2F, 0.2F, 1.0F));

				for (int i = 0; i < 8; i++) {
					RenderSystem.disableLight(i);
					RenderSystem.light(16384 + i, 4608, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 1.0F));
					RenderSystem.light(16384 + i, 4611, GuiLighting.singletonBuffer(0.0F, 0.0F, 1.0F, 0.0F));
					if (i == 0) {
						RenderSystem.light(16384 + i, 4609, GuiLighting.singletonBuffer(1.0F, 1.0F, 1.0F, 1.0F));
						RenderSystem.light(16384 + i, 4610, GuiLighting.singletonBuffer(1.0F, 1.0F, 1.0F, 1.0F));
					} else {
						RenderSystem.light(16384 + i, 4609, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 1.0F));
						RenderSystem.light(16384 + i, 4610, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 1.0F));
					}
				}

				RenderSystem.disableColorMaterial();
				RenderSystem.colorMaterial(1032, 5634);
				RenderSystem.disableDepthTest();
				RenderSystem.depthFunc(513);
				RenderSystem.depthMask(true);
				RenderSystem.disableBlend();
				RenderSystem.blendFunc(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
				RenderSystem.blendFuncSeparate(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
				RenderSystem.blendEquation(32774);
				RenderSystem.disableFog();
				RenderSystem.fogi(2917, 2048);
				RenderSystem.fogDensity(1.0F);
				RenderSystem.fogStart(0.0F);
				RenderSystem.fogEnd(1.0F);
				RenderSystem.fog(2918, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				if (GL.getCapabilities().GL_NV_fog_distance) {
					RenderSystem.fogi(2917, 34140);
				}

				RenderSystem.polygonOffset(0.0F, 0.0F);
				RenderSystem.disableColorLogicOp();
				RenderSystem.logicOp(5379);
				RenderSystem.disableTexGen(GlStateManager.TexCoord.S);
				RenderSystem.texGenMode(GlStateManager.TexCoord.S, 9216);
				RenderSystem.texGenParam(GlStateManager.TexCoord.S, 9474, GuiLighting.singletonBuffer(1.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.texGenParam(GlStateManager.TexCoord.S, 9217, GuiLighting.singletonBuffer(1.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.disableTexGen(GlStateManager.TexCoord.T);
				RenderSystem.texGenMode(GlStateManager.TexCoord.T, 9216);
				RenderSystem.texGenParam(GlStateManager.TexCoord.T, 9474, GuiLighting.singletonBuffer(0.0F, 1.0F, 0.0F, 0.0F));
				RenderSystem.texGenParam(GlStateManager.TexCoord.T, 9217, GuiLighting.singletonBuffer(0.0F, 1.0F, 0.0F, 0.0F));
				RenderSystem.disableTexGen(GlStateManager.TexCoord.R);
				RenderSystem.texGenMode(GlStateManager.TexCoord.R, 9216);
				RenderSystem.texGenParam(GlStateManager.TexCoord.R, 9474, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.texGenParam(GlStateManager.TexCoord.R, 9217, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.disableTexGen(GlStateManager.TexCoord.Q);
				RenderSystem.texGenMode(GlStateManager.TexCoord.Q, 9216);
				RenderSystem.texGenParam(GlStateManager.TexCoord.Q, 9474, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.texGenParam(GlStateManager.TexCoord.Q, 9217, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.activeTexture(0);
				RenderSystem.texParameter(3553, 10240, 9729);
				RenderSystem.texParameter(3553, 10241, 9986);
				RenderSystem.texParameter(3553, 10242, 10497);
				RenderSystem.texParameter(3553, 10243, 10497);
				RenderSystem.texParameter(3553, 33085, 1000);
				RenderSystem.texParameter(3553, 33083, 1000);
				RenderSystem.texParameter(3553, 33082, -1000);
				RenderSystem.texParameter(3553, 34049, 0.0F);
				RenderSystem.texEnv(8960, 8704, 8448);
				RenderSystem.texEnv(8960, 8705, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.texEnv(8960, 34161, 8448);
				RenderSystem.texEnv(8960, 34162, 8448);
				RenderSystem.texEnv(8960, 34176, 5890);
				RenderSystem.texEnv(8960, 34177, 34168);
				RenderSystem.texEnv(8960, 34178, 34166);
				RenderSystem.texEnv(8960, 34184, 5890);
				RenderSystem.texEnv(8960, 34185, 34168);
				RenderSystem.texEnv(8960, 34186, 34166);
				RenderSystem.texEnv(8960, 34192, 768);
				RenderSystem.texEnv(8960, 34193, 768);
				RenderSystem.texEnv(8960, 34194, 770);
				RenderSystem.texEnv(8960, 34200, 770);
				RenderSystem.texEnv(8960, 34201, 770);
				RenderSystem.texEnv(8960, 34202, 770);
				RenderSystem.texEnv(8960, 34163, 1.0F);
				RenderSystem.texEnv(8960, 3356, 1.0F);
				RenderSystem.disableNormalize();
				RenderSystem.shadeModel(7425);
				RenderSystem.disableRescaleNormal();
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.clearDepth(1.0);
				RenderSystem.lineWidth(1.0F);
				RenderSystem.normal3f(0.0F, 0.0F, 1.0F);
				RenderSystem.polygonMode(1028, 6914);
				RenderSystem.polygonMode(1029, 6914);
			}

			@Override
			public void end() {
			}
		},
		PLAYER_SKIN {
			@Override
			public void begin() {
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(770, 771, 1, 0);
			}

			@Override
			public void end() {
				RenderSystem.disableBlend();
			}
		},
		TRANSPARENT_MODEL {
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
				RenderSystem.alphaFunc(516, 0.1F);
				RenderSystem.depthMask(true);
			}
		};

		private RenderMode() {
		}

		public abstract void begin();

		public abstract void end();
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

	@Environment(EnvType.CLIENT)
	public static enum TexCoord {
		S,
		T,
		R,
		Q;
	}

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

	@Environment(EnvType.CLIENT)
	public static enum class_4534 {
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

		private class_4534(int j) {
			this.value = j;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4535 {
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

		private class_4535(int j) {
			this.value = j;
		}
	}
}
