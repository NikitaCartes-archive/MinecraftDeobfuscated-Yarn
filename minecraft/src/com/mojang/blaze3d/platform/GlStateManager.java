package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public class GlStateManager {
	public static final int TEXTURE_COUNT = 12;
	private static final GlStateManager.BlendFuncState BLEND = new GlStateManager.BlendFuncState();
	private static final GlStateManager.DepthTestState DEPTH = new GlStateManager.DepthTestState();
	private static final GlStateManager.CullFaceState CULL = new GlStateManager.CullFaceState();
	private static final GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
	private static final GlStateManager.LogicOpState COLOR_LOGIC = new GlStateManager.LogicOpState();
	private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
	private static final GlStateManager.ScissorTestState SCISSOR = new GlStateManager.ScissorTestState();
	private static int activeTexture;
	private static final GlStateManager.Texture2DState[] TEXTURES = (GlStateManager.Texture2DState[])IntStream.range(0, 12)
		.mapToObj(i -> new GlStateManager.Texture2DState())
		.toArray(GlStateManager.Texture2DState[]::new);
	private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();

	public static void _disableScissorTest() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		SCISSOR.capState.disable();
	}

	public static void _enableScissorTest() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		SCISSOR.capState.enable();
	}

	public static void _scissorBox(int x, int y, int width, int height) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL20.glScissor(x, y, width, height);
	}

	public static void _disableDepthTest() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		DEPTH.capState.disable();
	}

	public static void _enableDepthTest() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		DEPTH.capState.enable();
	}

	public static void _depthFunc(int func) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (func != DEPTH.func) {
			DEPTH.func = func;
			GL11.glDepthFunc(func);
		}
	}

	public static void _depthMask(boolean mask) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (mask != DEPTH.mask) {
			DEPTH.mask = mask;
			GL11.glDepthMask(mask);
		}
	}

	public static void _disableBlend() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		BLEND.capState.disable();
	}

	public static void _enableBlend() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		BLEND.capState.enable();
	}

	public static void _blendFunc(int srcFactor, int dstFactor) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (srcFactor != BLEND.srcFactorRGB || dstFactor != BLEND.dstFactorRGB) {
			BLEND.srcFactorRGB = srcFactor;
			BLEND.dstFactorRGB = dstFactor;
			GL11.glBlendFunc(srcFactor, dstFactor);
		}
	}

	public static void _blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (srcFactorRGB != BLEND.srcFactorRGB
			|| dstFactorRGB != BLEND.dstFactorRGB
			|| srcFactorAlpha != BLEND.srcFactorAlpha
			|| dstFactorAlpha != BLEND.dstFactorAlpha) {
			BLEND.srcFactorRGB = srcFactorRGB;
			BLEND.dstFactorRGB = dstFactorRGB;
			BLEND.srcFactorAlpha = srcFactorAlpha;
			BLEND.dstFactorAlpha = dstFactorAlpha;
			glBlendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
		}
	}

	public static void _blendEquation(int mode) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL14.glBlendEquation(mode);
	}

	public static int glGetProgrami(int program, int pname) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetProgrami(program, pname);
	}

	public static void glAttachShader(int program, int shader) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glAttachShader(program, shader);
	}

	public static void glDeleteShader(int shader) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glDeleteShader(shader);
	}

	public static int glCreateShader(int type) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glCreateShader(type);
	}

	public static void glShaderSource(int shader, List<String> strings) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		StringBuilder stringBuilder = new StringBuilder();

		for (String string : strings) {
			stringBuilder.append(string);
		}

		byte[] bs = stringBuilder.toString().getBytes(Charsets.UTF_8);
		ByteBuffer byteBuffer = MemoryUtil.memAlloc(bs.length + 1);
		byteBuffer.put(bs);
		byteBuffer.put((byte)0);
		byteBuffer.flip();

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
			pointerBuffer.put(byteBuffer);
			GL20C.nglShaderSource(shader, 1, pointerBuffer.address0(), 0L);
		} finally {
			MemoryUtil.memFree(byteBuffer);
		}
	}

	public static void glCompileShader(int shader) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glCompileShader(shader);
	}

	public static int glGetShaderi(int shader, int pname) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetShaderi(shader, pname);
	}

	public static void _glUseProgram(int program) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUseProgram(program);
	}

	public static int glCreateProgram() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glCreateProgram();
	}

	public static void glDeleteProgram(int program) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glDeleteProgram(program);
	}

	public static void glLinkProgram(int program) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glLinkProgram(program);
	}

	public static int _glGetUniformLocation(int program, CharSequence name) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetUniformLocation(program, name);
	}

	public static void _glUniform1(int location, IntBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform1iv(location, value);
	}

	public static void _glUniform1i(int location, int value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform1i(location, value);
	}

	public static void _glUniform1(int location, FloatBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform1fv(location, value);
	}

	public static void _glUniform2(int location, IntBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform2iv(location, value);
	}

	public static void _glUniform2(int location, FloatBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform2fv(location, value);
	}

	public static void _glUniform3(int location, IntBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform3iv(location, value);
	}

	public static void _glUniform3(int location, FloatBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform3fv(location, value);
	}

	public static void _glUniform4(int location, IntBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform4iv(location, value);
	}

	public static void _glUniform4(int location, FloatBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniform4fv(location, value);
	}

	public static void _glUniformMatrix2(int location, boolean transpose, FloatBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniformMatrix2fv(location, transpose, value);
	}

	public static void _glUniformMatrix3(int location, boolean transpose, FloatBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniformMatrix3fv(location, transpose, value);
	}

	public static void _glUniformMatrix4(int location, boolean transpose, FloatBuffer value) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glUniformMatrix4fv(location, transpose, value);
	}

	public static int _glGetAttribLocation(int program, CharSequence name) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetAttribLocation(program, name);
	}

	public static void _glBindAttribLocation(int program, int index, CharSequence name) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glBindAttribLocation(program, index, name);
	}

	public static int _glGenBuffers() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL15.glGenBuffers();
	}

	public static int _glGenVertexArrays() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL30.glGenVertexArrays();
	}

	public static void _glBindBuffer(int target, int buffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL15.glBindBuffer(target, buffer);
	}

	public static void _glBindVertexArray(int array) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL30.glBindVertexArray(array);
	}

	public static void _glBufferData(int target, ByteBuffer data, int usage) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL15.glBufferData(target, data, usage);
	}

	public static void _glBufferData(int target, long size, int usage) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL15.glBufferData(target, size, usage);
	}

	@Nullable
	public static ByteBuffer mapBuffer(int target, int access) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL15.glMapBuffer(target, access);
	}

	public static void _glUnmapBuffer(int target) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL15.glUnmapBuffer(target);
	}

	public static void _glDeleteBuffers(int buffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL15.glDeleteBuffers(buffer);
	}

	public static void _glCopyTexSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL20.glCopyTexSubImage2D(i, j, k, l, m, n, o, p);
	}

	public static void _glDeleteVertexArrays(int array) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL30.glDeleteVertexArrays(array);
	}

	public static void _glBindFramebuffer(int target, int framebuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL30.glBindFramebuffer(target, framebuffer);
	}

	public static void _glBlitFrameBuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
	}

	public static void _glBindRenderbuffer(int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL30.glBindRenderbuffer(i, j);
	}

	public static void _glDeleteRenderbuffers(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL30.glDeleteRenderbuffers(i);
	}

	public static void _glDeleteFramebuffers(int framebuffer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL30.glDeleteFramebuffers(framebuffer);
	}

	public static int glGenFramebuffers() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL30.glGenFramebuffers();
	}

	public static int glGenRenderbuffers() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL30.glGenRenderbuffers();
	}

	public static void _glRenderbufferStorage(int i, int j, int k, int l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL30.glRenderbufferStorage(i, j, k, l);
	}

	public static void _glFramebufferRenderbuffer(int i, int j, int k, int l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL30.glFramebufferRenderbuffer(i, j, k, l);
	}

	public static int glCheckFramebufferStatus(int target) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL30.glCheckFramebufferStatus(target);
	}

	public static void _glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL30.glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
	}

	public static int getBoundFramebuffer() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return _getInteger(36006);
	}

	public static void glActiveTexture(int texture) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL13.glActiveTexture(texture);
	}

	public static void glBlendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL14.glBlendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
	}

	public static String glGetShaderInfoLog(int shader, int maxLength) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetShaderInfoLog(shader, maxLength);
	}

	public static String glGetProgramInfoLog(int program, int maxLength) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL20.glGetProgramInfoLog(program, maxLength);
	}

	public static void setupLevelDiffuseLighting(Vec3f vec3f, Vec3f vec3f2, Matrix4f matrix4f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		Vector4f vector4f = new Vector4f(vec3f);
		vector4f.transform(matrix4f);
		Vector4f vector4f2 = new Vector4f(vec3f2);
		vector4f2.transform(matrix4f);
		RenderSystem.setShaderLights(new Vec3f(vector4f), new Vec3f(vector4f2));
	}

	public static void setupGuiFlatDiffuseLighting(Vec3f vec3f, Vec3f vec3f2) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.multiply(Matrix4f.scale(1.0F, -1.0F, 1.0F));
		matrix4f.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-22.5F));
		matrix4f.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(135.0F));
		setupLevelDiffuseLighting(vec3f, vec3f2, matrix4f);
	}

	public static void setupGui3DDiffuseLighting(Vec3f vec3f, Vec3f vec3f2) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(62.0F));
		matrix4f.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(185.5F));
		matrix4f.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-22.5F));
		matrix4f.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(135.0F));
		setupLevelDiffuseLighting(vec3f, vec3f2, matrix4f);
	}

	public static void _enableCull() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		CULL.capState.enable();
	}

	public static void _disableCull() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		CULL.capState.disable();
	}

	public static void _polygonMode(int face, int mode) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glPolygonMode(face, mode);
	}

	public static void _enablePolygonOffset() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		POLY_OFFSET.capFill.enable();
	}

	public static void _disablePolygonOffset() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		POLY_OFFSET.capFill.disable();
	}

	public static void _polygonOffset(float factor, float units) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (factor != POLY_OFFSET.factor || units != POLY_OFFSET.units) {
			POLY_OFFSET.factor = factor;
			POLY_OFFSET.units = units;
			GL11.glPolygonOffset(factor, units);
		}
	}

	public static void _enableColorLogicOp() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		COLOR_LOGIC.capState.enable();
	}

	public static void _disableColorLogicOp() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		COLOR_LOGIC.capState.disable();
	}

	public static void _logicOp(int op) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (op != COLOR_LOGIC.op) {
			COLOR_LOGIC.op = op;
			GL11.glLogicOp(op);
		}
	}

	public static void _activeTexture(int texture) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (activeTexture != texture - 33984) {
			activeTexture = texture - 33984;
			glActiveTexture(texture);
		}
	}

	public static void _enableTexture() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		TEXTURES[activeTexture].capState = true;
	}

	public static void _disableTexture() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		TEXTURES[activeTexture].capState = false;
	}

	public static void _texParameter(int target, int pname, float param) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glTexParameterf(target, pname, param);
	}

	public static void _texParameter(int target, int pname, int param) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glTexParameteri(target, pname, param);
	}

	public static int _getTexLevelParameter(int target, int level, int pname) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		return GL11.glGetTexLevelParameteri(target, level, pname);
	}

	public static int _genTexture() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL11.glGenTextures();
	}

	public static void _genTextures(int[] is) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glGenTextures(is);
	}

	public static void _deleteTexture(int texture) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glDeleteTextures(texture);

		for (GlStateManager.Texture2DState texture2DState : TEXTURES) {
			if (texture2DState.boundTexture == texture) {
				texture2DState.boundTexture = -1;
			}
		}
	}

	public static void _deleteTextures(int[] is) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

		for (GlStateManager.Texture2DState texture2DState : TEXTURES) {
			for (int i : is) {
				if (texture2DState.boundTexture == i) {
					texture2DState.boundTexture = -1;
				}
			}
		}

		GL11.glDeleteTextures(is);
	}

	public static void _bindTexture(int texture) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (texture != TEXTURES[activeTexture].boundTexture) {
			TEXTURES[activeTexture].boundTexture = texture;
			GL11.glBindTexture(3553, texture);
		}
	}

	public static int _getTextureId(int i) {
		return i >= 0 && i < 12 && TEXTURES[i].capState ? TEXTURES[i].boundTexture : 0;
	}

	public static int _getActiveTexture() {
		return activeTexture + 33984;
	}

	public static void _texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
	}

	public static void _texSubImage2D(int target, int level, int offsetX, int offsetY, int width, int height, int format, int type, long pixels) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glTexSubImage2D(target, level, offsetX, offsetY, width, height, format, type, pixels);
	}

	public static void _getTexImage(int target, int level, int format, int type, long pixels) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glGetTexImage(target, level, format, type, pixels);
	}

	public static void _viewport(int x, int y, int width, int height) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.Viewport.INSTANCE.x = x;
		GlStateManager.Viewport.INSTANCE.y = y;
		GlStateManager.Viewport.INSTANCE.width = width;
		GlStateManager.Viewport.INSTANCE.height = height;
		GL11.glViewport(x, y, width, height);
	}

	public static void _colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (red != COLOR_MASK.red || green != COLOR_MASK.green || blue != COLOR_MASK.blue || alpha != COLOR_MASK.alpha) {
			COLOR_MASK.red = red;
			COLOR_MASK.green = green;
			COLOR_MASK.blue = blue;
			COLOR_MASK.alpha = alpha;
			GL11.glColorMask(red, green, blue, alpha);
		}
	}

	public static void _stencilFunc(int func, int ref, int mask) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (func != STENCIL.subState.func || func != STENCIL.subState.ref || func != STENCIL.subState.mask) {
			STENCIL.subState.func = func;
			STENCIL.subState.ref = ref;
			STENCIL.subState.mask = mask;
			GL11.glStencilFunc(func, ref, mask);
		}
	}

	public static void _stencilMask(int mask) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (mask != STENCIL.mask) {
			STENCIL.mask = mask;
			GL11.glStencilMask(mask);
		}
	}

	public static void _stencilOp(int sfail, int dpfail, int dppass) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (sfail != STENCIL.sfail || dpfail != STENCIL.dpfail || dppass != STENCIL.dppass) {
			STENCIL.sfail = sfail;
			STENCIL.dpfail = dpfail;
			STENCIL.dppass = dppass;
			GL11.glStencilOp(sfail, dpfail, dppass);
		}
	}

	public static void _clearDepth(double depth) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glClearDepth(depth);
	}

	public static void _clearColor(float red, float green, float blue, float alpha) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glClearColor(red, green, blue, alpha);
	}

	public static void _clearStencil(int stencil) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glClearStencil(stencil);
	}

	public static void _clear(int mask, boolean getError) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glClear(mask);
		if (getError) {
			_getError();
		}
	}

	public static void _glDrawPixels(int i, int j, int k, int l, long m) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glDrawPixels(i, j, k, l, m);
	}

	public static void _vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
	}

	public static void _vertexAttribIPointer(int index, int size, int type, int stride, long pointer) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL30.glVertexAttribIPointer(index, size, type, stride, pointer);
	}

	public static void _enableVertexAttribArray(int index) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glEnableVertexAttribArray(index);
	}

	public static void _disableVertexAttribArray(int index) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL20.glDisableVertexAttribArray(index);
	}

	public static void _drawElements(int mode, int first, int type, long indices) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glDrawElements(mode, first, type, indices);
	}

	public static void _pixelStore(int pname, int param) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GL11.glPixelStorei(pname, param);
	}

	public static void _readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glReadPixels(x, y, width, height, format, type, pixels);
	}

	public static void _readPixels(int i, int j, int k, int l, int m, int n, long o) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glReadPixels(i, j, k, l, m, n, o);
	}

	public static int _getError() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL11.glGetError();
	}

	public static String _getString(int name) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return GL11.glGetString(name);
	}

	public static int _getInteger(int pname) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GL11.glGetInteger(pname);
	}

	@Environment(EnvType.CLIENT)
	static class BlendFuncState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(3042);
		public int srcFactorRGB = 1;
		public int dstFactorRGB = 0;
		public int srcFactorAlpha = 1;
		public int dstFactorAlpha = 0;
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	static class ColorMask {
		public boolean red = true;
		public boolean green = true;
		public boolean blue = true;
		public boolean alpha = true;
	}

	@Environment(EnvType.CLIENT)
	static class CullFaceState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(2884);
		public int mode = 1029;
	}

	@Environment(EnvType.CLIENT)
	static class DepthTestState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(2929);
		public boolean mask = true;
		public int func = 513;
	}

	@Environment(EnvType.CLIENT)
	@DeobfuscateClass
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

		public final int value;

		private DstFactor(int j) {
			this.value = j;
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

		public final int value;

		private LogicOp(int value) {
			this.value = value;
		}
	}

	@Environment(EnvType.CLIENT)
	static class LogicOpState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(3058);
		public int op = 5379;
	}

	@Environment(EnvType.CLIENT)
	static class PolygonOffsetState {
		public final GlStateManager.CapabilityTracker capFill = new GlStateManager.CapabilityTracker(32823);
		public final GlStateManager.CapabilityTracker capLine = new GlStateManager.CapabilityTracker(10754);
		public float factor;
		public float units;
	}

	@Environment(EnvType.CLIENT)
	static class ScissorTestState {
		public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(3089);
	}

	@Environment(EnvType.CLIENT)
	@DeobfuscateClass
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

		public final int value;

		private SrcFactor(int j) {
			this.value = j;
		}
	}

	@Environment(EnvType.CLIENT)
	static class StencilState {
		public final GlStateManager.StencilSubState subState = new GlStateManager.StencilSubState();
		public int mask = -1;
		public int sfail = 7680;
		public int dpfail = 7680;
		public int dppass = 7680;
	}

	@Environment(EnvType.CLIENT)
	static class StencilSubState {
		public int func = 519;
		public int ref;
		public int mask = -1;
	}

	@Environment(EnvType.CLIENT)
	static class Texture2DState {
		public boolean capState;
		public int boundTexture;
	}

	@Environment(EnvType.CLIENT)
	public static enum Viewport {
		INSTANCE;

		protected int x;
		protected int y;
		protected int width;
		protected int height;

		public static int getX() {
			return INSTANCE.x;
		}

		public static int getY() {
			return INSTANCE.y;
		}

		public static int getWidth() {
			return INSTANCE.width;
		}

		public static int getHeight() {
			return INSTANCE.height;
		}
	}
}
