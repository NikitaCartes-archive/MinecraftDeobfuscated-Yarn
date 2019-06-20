package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1159;
import net.minecraft.class_301;
import net.minecraft.class_308;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class GlStateManager {
	private static final int LIGHT_COUNT = 8;
	private static final int TEXTURE_COUNT = 8;
	private static final FloatBuffer MATRIX_BUFFER = GLX.make(
		MemoryUtil.memAllocFloat(16), floatBuffer -> class_301.method_1407(MemoryUtil.memAddress(floatBuffer))
	);
	private static final FloatBuffer COLOR_BUFFER = GLX.make(MemoryUtil.memAllocFloat(4), floatBuffer -> class_301.method_1407(MemoryUtil.memAddress(floatBuffer)));
	private static final GlStateManager.class_1016 ALPHA_TEST = new GlStateManager.class_1016();
	private static final GlStateManager.class_1018 LIGHTING = new GlStateManager.class_1018(2896);
	private static final GlStateManager.class_1018[] LIGHT_ENABLE = (GlStateManager.class_1018[])IntStream.range(0, 8)
		.mapToObj(i -> new GlStateManager.class_1018(16384 + i))
		.toArray(GlStateManager.class_1018[]::new);
	private static final GlStateManager.class_1023 COLOR_MATERIAL = new GlStateManager.class_1023();
	private static final GlStateManager.class_1017 BLEND = new GlStateManager.class_1017();
	private static final GlStateManager.class_1026 DEPTH = new GlStateManager.class_1026();
	private static final GlStateManager.class_1029 FOG = new GlStateManager.class_1029();
	private static final GlStateManager.class_1025 CULL = new GlStateManager.class_1025();
	private static final GlStateManager.class_1031 POLY_OFFSET = new GlStateManager.class_1031();
	private static final GlStateManager.class_1021 COLOR_LOGIC = new GlStateManager.class_1021();
	private static final GlStateManager.class_1038 TEX_GEN = new GlStateManager.class_1038();
	private static final GlStateManager.class_1019 CLEAR = new GlStateManager.class_1019();
	private static final GlStateManager.class_1035 STENCIL = new GlStateManager.class_1035();
	private static final GlStateManager.class_1018 NORMALIZE = new GlStateManager.class_1018(2977);
	private static int activeTexture;
	private static final GlStateManager.class_1039[] TEXTURES = (GlStateManager.class_1039[])IntStream.range(0, 8)
		.mapToObj(i -> new GlStateManager.class_1039())
		.toArray(GlStateManager.class_1039[]::new);
	private static int shadeModel = 7425;
	private static final GlStateManager.class_1018 RESCALE_NORMAL = new GlStateManager.class_1018(32826);
	private static final GlStateManager.class_1022 COLOR_MASK = new GlStateManager.class_1022();
	private static final GlStateManager.class_1020 COLOR = new GlStateManager.class_1020();
	private static final float DEFAULTALPHACUTOFF = 0.1F;

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
		ALPHA_TEST.field_5042.method_4469();
	}

	public static void enableAlphaTest() {
		ALPHA_TEST.field_5042.method_4471();
	}

	public static void alphaFunc(int i, float f) {
		if (i != ALPHA_TEST.field_5044 || f != ALPHA_TEST.field_5043) {
			ALPHA_TEST.field_5044 = i;
			ALPHA_TEST.field_5043 = f;
			GL11.glAlphaFunc(i, f);
		}
	}

	public static void enableLighting() {
		LIGHTING.method_4471();
	}

	public static void disableLighting() {
		LIGHTING.method_4469();
	}

	public static void enableLight(int i) {
		LIGHT_ENABLE[i].method_4471();
	}

	public static void disableLight(int i) {
		LIGHT_ENABLE[i].method_4469();
	}

	public static void enableColorMaterial() {
		COLOR_MATERIAL.field_5064.method_4471();
	}

	public static void disableColorMaterial() {
		COLOR_MATERIAL.field_5064.method_4469();
	}

	public static void colorMaterial(int i, int j) {
		if (i != COLOR_MATERIAL.field_5066 || j != COLOR_MATERIAL.field_5065) {
			COLOR_MATERIAL.field_5066 = i;
			COLOR_MATERIAL.field_5065 = j;
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
		DEPTH.field_5074.method_4469();
	}

	public static void enableDepthTest() {
		DEPTH.field_5074.method_4471();
	}

	public static void depthFunc(int i) {
		if (i != DEPTH.field_5075) {
			DEPTH.field_5075 = i;
			GL11.glDepthFunc(i);
		}
	}

	public static void depthMask(boolean bl) {
		if (bl != DEPTH.field_5076) {
			DEPTH.field_5076 = bl;
			GL11.glDepthMask(bl);
		}
	}

	public static void disableBlend() {
		BLEND.field_5045.method_4469();
	}

	public static void enableBlend() {
		BLEND.field_5045.method_4471();
	}

	public static void blendFunc(GlStateManager.class_5119 arg, GlStateManager.class_5118 arg2) {
		blendFunc(arg.value, arg2.value);
	}

	public static void blendFunc(int i, int j) {
		if (i != BLEND.field_5049 || j != BLEND.field_5048) {
			BLEND.field_5049 = i;
			BLEND.field_5048 = j;
			GL11.glBlendFunc(i, j);
		}
	}

	public static void blendFuncSeparate(
		GlStateManager.class_5119 arg, GlStateManager.class_5118 arg2, GlStateManager.class_5119 arg3, GlStateManager.class_5118 arg4
	) {
		blendFuncSeparate(arg.value, arg2.value, arg3.value, arg4.value);
	}

	public static void blendFuncSeparate(int i, int j, int k, int l) {
		if (i != BLEND.field_5049 || j != BLEND.field_5048 || k != BLEND.field_5047 || l != BLEND.field_5046) {
			BLEND.field_5049 = i;
			BLEND.field_5048 = j;
			BLEND.field_5047 = k;
			BLEND.field_5046 = l;
			GLX.glBlendFuncSeparate(i, j, k, l);
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
		texEnv(8960, 8705, COLOR_BUFFER);
		texEnv(8960, 8704, 34160);
		texEnv(8960, 34161, 7681);
		texEnv(8960, 34176, 34166);
		texEnv(8960, 34192, 768);
		texEnv(8960, 34162, 7681);
		texEnv(8960, 34184, 5890);
		texEnv(8960, 34200, 770);
	}

	public static void tearDownSolidRenderingTextureCombine() {
		texEnv(8960, 8704, 8448);
		texEnv(8960, 34161, 8448);
		texEnv(8960, 34162, 8448);
		texEnv(8960, 34176, 5890);
		texEnv(8960, 34184, 5890);
		texEnv(8960, 34192, 768);
		texEnv(8960, 34200, 770);
	}

	public static void enableFog() {
		FOG.field_5100.method_4471();
	}

	public static void disableFog() {
		FOG.field_5100.method_4469();
	}

	public static void fogMode(GlStateManager.class_1028 arg) {
		fogMode(arg.field_5093);
	}

	private static void fogMode(int i) {
		if (i != FOG.field_5102) {
			FOG.field_5102 = i;
			GL11.glFogi(2917, i);
		}
	}

	public static void fogDensity(float f) {
		if (f != FOG.field_5101) {
			FOG.field_5101 = f;
			GL11.glFogf(2914, f);
		}
	}

	public static void fogStart(float f) {
		if (f != FOG.field_5099) {
			FOG.field_5099 = f;
			GL11.glFogf(2915, f);
		}
	}

	public static void fogEnd(float f) {
		if (f != FOG.field_5098) {
			FOG.field_5098 = f;
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
		CULL.field_5072.method_4471();
	}

	public static void disableCull() {
		CULL.field_5072.method_4469();
	}

	public static void cullFace(GlStateManager.class_1024 arg) {
		cullFace(arg.field_5069);
	}

	private static void cullFace(int i) {
		if (i != CULL.field_5073) {
			CULL.field_5073 = i;
			GL11.glCullFace(i);
		}
	}

	public static void polygonMode(int i, int j) {
		GL11.glPolygonMode(i, j);
	}

	public static void enablePolygonOffset() {
		POLY_OFFSET.field_5123.method_4471();
	}

	public static void disablePolygonOffset() {
		POLY_OFFSET.field_5123.method_4469();
	}

	public static void enableLineOffset() {
		POLY_OFFSET.field_5121.method_4471();
	}

	public static void disableLineOffset() {
		POLY_OFFSET.field_5121.method_4469();
	}

	public static void polygonOffset(float f, float g) {
		if (f != POLY_OFFSET.field_5124 || g != POLY_OFFSET.field_5122) {
			POLY_OFFSET.field_5124 = f;
			POLY_OFFSET.field_5122 = g;
			GL11.glPolygonOffset(f, g);
		}
	}

	public static void enableColorLogicOp() {
		COLOR_LOGIC.field_5058.method_4471();
	}

	public static void disableColorLogicOp() {
		COLOR_LOGIC.field_5058.method_4469();
	}

	public static void logicOp(GlStateManager.class_1030 arg) {
		logicOp(arg.field_5108);
	}

	public static void logicOp(int i) {
		if (i != COLOR_LOGIC.field_5059) {
			COLOR_LOGIC.field_5059 = i;
			GL11.glLogicOp(i);
		}
	}

	public static void enableTexGen(GlStateManager.class_1036 arg) {
		getTexGen(arg).field_5159.method_4471();
	}

	public static void disableTexGen(GlStateManager.class_1036 arg) {
		getTexGen(arg).field_5159.method_4469();
	}

	public static void texGenMode(GlStateManager.class_1036 arg, int i) {
		GlStateManager.class_1037 lv = getTexGen(arg);
		if (i != lv.field_5160) {
			lv.field_5160 = i;
			GL11.glTexGeni(lv.field_5161, 9472, i);
		}
	}

	public static void texGenParam(GlStateManager.class_1036 arg, int i, FloatBuffer floatBuffer) {
		GL11.glTexGenfv(getTexGen(arg).field_5161, i, floatBuffer);
	}

	private static GlStateManager.class_1037 getTexGen(GlStateManager.class_1036 arg) {
		switch (arg) {
			case field_5154:
				return TEX_GEN.field_5162;
			case field_5155:
				return TEX_GEN.field_5163;
			case field_5156:
				return TEX_GEN.field_5164;
			case field_5157:
				return TEX_GEN.field_5165;
			default:
				return TEX_GEN.field_5162;
		}
	}

	public static void activeTexture(int i) {
		if (activeTexture != i - GLX.GL_TEXTURE0) {
			activeTexture = i - GLX.GL_TEXTURE0;
			GLX.glActiveTexture(i);
		}
	}

	public static void enableTexture() {
		TEXTURES[activeTexture].field_5166.method_4471();
	}

	public static void disableTexture() {
		TEXTURES[activeTexture].field_5166.method_4469();
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

	public static int genTexture() {
		return GL11.glGenTextures();
	}

	public static void deleteTexture(int i) {
		GL11.glDeleteTextures(i);

		for (GlStateManager.class_1039 lv : TEXTURES) {
			if (lv.field_5167 == i) {
				lv.field_5167 = -1;
			}
		}
	}

	public static void bindTexture(int i) {
		if (i != TEXTURES[activeTexture].field_5167) {
			TEXTURES[activeTexture].field_5167 = i;
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
		NORMALIZE.method_4471();
	}

	public static void disableNormalize() {
		NORMALIZE.method_4469();
	}

	public static void shadeModel(int i) {
		if (i != shadeModel) {
			shadeModel = i;
			GL11.glShadeModel(i);
		}
	}

	public static void enableRescaleNormal() {
		RESCALE_NORMAL.method_4471();
	}

	public static void disableRescaleNormal() {
		RESCALE_NORMAL.method_4469();
	}

	public static void viewport(int i, int j, int k, int l) {
		GlStateManager.class_1040.field_5169.field_5172 = i;
		GlStateManager.class_1040.field_5169.field_5171 = j;
		GlStateManager.class_1040.field_5169.field_5170 = k;
		GlStateManager.class_1040.field_5169.field_5168 = l;
		GL11.glViewport(i, j, k, l);
	}

	public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		if (bl != COLOR_MASK.field_5063 || bl2 != COLOR_MASK.field_5062 || bl3 != COLOR_MASK.field_5061 || bl4 != COLOR_MASK.field_5060) {
			COLOR_MASK.field_5063 = bl;
			COLOR_MASK.field_5062 = bl2;
			COLOR_MASK.field_5061 = bl3;
			COLOR_MASK.field_5060 = bl4;
			GL11.glColorMask(bl, bl2, bl3, bl4);
		}
	}

	public static void stencilFunc(int i, int j, int k) {
		if (i != STENCIL.field_5149.field_5148 || i != STENCIL.field_5149.field_16203 || i != STENCIL.field_5149.field_5147) {
			STENCIL.field_5149.field_5148 = i;
			STENCIL.field_5149.field_16203 = j;
			STENCIL.field_5149.field_5147 = k;
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
		if (d != CLEAR.field_5053) {
			CLEAR.field_5053 = d;
			GL11.glClearDepth(d);
		}
	}

	public static void clearColor(float f, float g, float h, float i) {
		if (f != CLEAR.field_5052.field_5057 || g != CLEAR.field_5052.field_5056 || h != CLEAR.field_5052.field_5055 || i != CLEAR.field_5052.field_5054) {
			CLEAR.field_5052.field_5057 = f;
			CLEAR.field_5052.field_5056 = g;
			CLEAR.field_5052.field_5055 = h;
			CLEAR.field_5052.field_5054 = i;
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
			getError();
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

	public static class_1159 getMatrix4f(int i) {
		GL11.glGetFloatv(i, MATRIX_BUFFER);
		MATRIX_BUFFER.rewind();
		class_1159 lv = new class_1159();
		lv.method_4928(MATRIX_BUFFER);
		MATRIX_BUFFER.rewind();
		return lv;
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

	public static void multMatrix(class_1159 arg) {
		arg.method_4932(MATRIX_BUFFER);
		MATRIX_BUFFER.rewind();
		GL11.glMultMatrixf(MATRIX_BUFFER);
	}

	public static void color4f(float f, float g, float h, float i) {
		if (f != COLOR.field_5057 || g != COLOR.field_5056 || h != COLOR.field_5055 || i != COLOR.field_5054) {
			COLOR.field_5057 = f;
			COLOR.field_5056 = g;
			COLOR.field_5055 = h;
			COLOR.field_5054 = i;
			GL11.glColor4f(f, g, h, i);
		}
	}

	public static void color3f(float f, float g, float h) {
		color4f(f, g, h, 1.0F);
	}

	public static void texCoord2f(float f, float g) {
		GL11.glTexCoord2f(f, g);
	}

	public static void vertex3f(float f, float g, float h) {
		GL11.glVertex3f(f, g, h);
	}

	public static void clearCurrentColor() {
		COLOR.field_5057 = -1.0F;
		COLOR.field_5056 = -1.0F;
		COLOR.field_5055 = -1.0F;
		COLOR.field_5054 = -1.0F;
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

	public static void setProfile(GlStateManager.class_1032 arg) {
		arg.method_4472();
	}

	public static void unsetProfile(GlStateManager.class_1032 arg) {
		arg.method_4473();
	}

	@Environment(EnvType.CLIENT)
	static class class_1016 {
		public final GlStateManager.class_1018 field_5042 = new GlStateManager.class_1018(3008);
		public int field_5044 = 519;
		public float field_5043 = -1.0F;

		private class_1016() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1017 {
		public final GlStateManager.class_1018 field_5045 = new GlStateManager.class_1018(3042);
		public int field_5049 = 1;
		public int field_5048 = 0;
		public int field_5047 = 1;
		public int field_5046 = 0;

		private class_1017() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1018 {
		private final int field_5050;
		private boolean field_5051;

		public class_1018(int i) {
			this.field_5050 = i;
		}

		public void method_4469() {
			this.method_4470(false);
		}

		public void method_4471() {
			this.method_4470(true);
		}

		public void method_4470(boolean bl) {
			if (bl != this.field_5051) {
				this.field_5051 = bl;
				if (bl) {
					GL11.glEnable(this.field_5050);
				} else {
					GL11.glDisable(this.field_5050);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1019 {
		public double field_5053 = 1.0;
		public final GlStateManager.class_1020 field_5052 = new GlStateManager.class_1020(0.0F, 0.0F, 0.0F, 0.0F);
		public int field_16202;

		private class_1019() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1020 {
		public float field_5057 = 1.0F;
		public float field_5056 = 1.0F;
		public float field_5055 = 1.0F;
		public float field_5054 = 1.0F;

		public class_1020() {
			this(1.0F, 1.0F, 1.0F, 1.0F);
		}

		public class_1020(float f, float g, float h, float i) {
			this.field_5057 = f;
			this.field_5056 = g;
			this.field_5055 = h;
			this.field_5054 = i;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1021 {
		public final GlStateManager.class_1018 field_5058 = new GlStateManager.class_1018(3058);
		public int field_5059 = 5379;

		private class_1021() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1022 {
		public boolean field_5063 = true;
		public boolean field_5062 = true;
		public boolean field_5061 = true;
		public boolean field_5060 = true;

		private class_1022() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1023 {
		public final GlStateManager.class_1018 field_5064 = new GlStateManager.class_1018(2903);
		public int field_5066 = 1032;
		public int field_5065 = 5634;

		private class_1023() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1024 {
		field_5068(1028),
		field_5070(1029),
		field_5071(1032);

		public final int field_5069;

		private class_1024(int j) {
			this.field_5069 = j;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1025 {
		public final GlStateManager.class_1018 field_5072 = new GlStateManager.class_1018(2884);
		public int field_5073 = 1029;

		private class_1025() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1026 {
		public final GlStateManager.class_1018 field_5074 = new GlStateManager.class_1018(2929);
		public boolean field_5076 = true;
		public int field_5075 = 513;

		private class_1026() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1028 {
		field_5095(9729),
		field_5096(2048),
		field_5097(2049);

		public final int field_5093;

		private class_1028(int j) {
			this.field_5093 = j;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1029 {
		public final GlStateManager.class_1018 field_5100 = new GlStateManager.class_1018(2912);
		public int field_5102 = 2048;
		public float field_5101 = 1.0F;
		public float field_5099;
		public float field_5098 = 1.0F;

		private class_1029() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1030 {
		field_5120(5377),
		field_5112(5380),
		field_5117(5378),
		field_5103(5376),
		field_5118(5379),
		field_5113(5388),
		field_5119(5385),
		field_5109(5386),
		field_5114(5390),
		field_5115(5381),
		field_5104(5384),
		field_5105(5383),
		field_5116(5389),
		field_5110(5387),
		field_5107(5391),
		field_5111(5382);

		public final int field_5108;

		private class_1030(int j) {
			this.field_5108 = j;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1031 {
		public final GlStateManager.class_1018 field_5123 = new GlStateManager.class_1018(32823);
		public final GlStateManager.class_1018 field_5121 = new GlStateManager.class_1018(10754);
		public float field_5124;
		public float field_5122;

		private class_1031() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1032 {
		field_5127 {
			@Override
			public void method_4472() {
				GlStateManager.disableAlphaTest();
				GlStateManager.alphaFunc(519, 0.0F);
				GlStateManager.disableLighting();
				GlStateManager.lightModel(2899, class_308.method_1451(0.2F, 0.2F, 0.2F, 1.0F));

				for (int i = 0; i < 8; i++) {
					GlStateManager.disableLight(i);
					GlStateManager.light(16384 + i, 4608, class_308.method_1451(0.0F, 0.0F, 0.0F, 1.0F));
					GlStateManager.light(16384 + i, 4611, class_308.method_1451(0.0F, 0.0F, 1.0F, 0.0F));
					if (i == 0) {
						GlStateManager.light(16384 + i, 4609, class_308.method_1451(1.0F, 1.0F, 1.0F, 1.0F));
						GlStateManager.light(16384 + i, 4610, class_308.method_1451(1.0F, 1.0F, 1.0F, 1.0F));
					} else {
						GlStateManager.light(16384 + i, 4609, class_308.method_1451(0.0F, 0.0F, 0.0F, 1.0F));
						GlStateManager.light(16384 + i, 4610, class_308.method_1451(0.0F, 0.0F, 0.0F, 1.0F));
					}
				}

				GlStateManager.disableColorMaterial();
				GlStateManager.colorMaterial(1032, 5634);
				GlStateManager.disableDepthTest();
				GlStateManager.depthFunc(513);
				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
				GlStateManager.blendFunc(GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO);
				GlStateManager.blendFuncSeparate(
					GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
				);
				GlStateManager.blendEquation(32774);
				GlStateManager.disableFog();
				GlStateManager.fogi(2917, 2048);
				GlStateManager.fogDensity(1.0F);
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(1.0F);
				GlStateManager.fog(2918, class_308.method_1451(0.0F, 0.0F, 0.0F, 0.0F));
				if (GL.getCapabilities().GL_NV_fog_distance) {
					GlStateManager.fogi(2917, 34140);
				}

				GlStateManager.polygonOffset(0.0F, 0.0F);
				GlStateManager.disableColorLogicOp();
				GlStateManager.logicOp(5379);
				GlStateManager.disableTexGen(GlStateManager.class_1036.field_5154);
				GlStateManager.texGenMode(GlStateManager.class_1036.field_5154, 9216);
				GlStateManager.texGenParam(GlStateManager.class_1036.field_5154, 9474, class_308.method_1451(1.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.texGenParam(GlStateManager.class_1036.field_5154, 9217, class_308.method_1451(1.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.disableTexGen(GlStateManager.class_1036.field_5155);
				GlStateManager.texGenMode(GlStateManager.class_1036.field_5155, 9216);
				GlStateManager.texGenParam(GlStateManager.class_1036.field_5155, 9474, class_308.method_1451(0.0F, 1.0F, 0.0F, 0.0F));
				GlStateManager.texGenParam(GlStateManager.class_1036.field_5155, 9217, class_308.method_1451(0.0F, 1.0F, 0.0F, 0.0F));
				GlStateManager.disableTexGen(GlStateManager.class_1036.field_5156);
				GlStateManager.texGenMode(GlStateManager.class_1036.field_5156, 9216);
				GlStateManager.texGenParam(GlStateManager.class_1036.field_5156, 9474, class_308.method_1451(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.texGenParam(GlStateManager.class_1036.field_5156, 9217, class_308.method_1451(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.disableTexGen(GlStateManager.class_1036.field_5157);
				GlStateManager.texGenMode(GlStateManager.class_1036.field_5157, 9216);
				GlStateManager.texGenParam(GlStateManager.class_1036.field_5157, 9474, class_308.method_1451(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.texGenParam(GlStateManager.class_1036.field_5157, 9217, class_308.method_1451(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.activeTexture(0);
				GlStateManager.texParameter(3553, 10240, 9729);
				GlStateManager.texParameter(3553, 10241, 9986);
				GlStateManager.texParameter(3553, 10242, 10497);
				GlStateManager.texParameter(3553, 10243, 10497);
				GlStateManager.texParameter(3553, 33085, 1000);
				GlStateManager.texParameter(3553, 33083, 1000);
				GlStateManager.texParameter(3553, 33082, -1000);
				GlStateManager.texParameter(3553, 34049, 0.0F);
				GlStateManager.texEnv(8960, 8704, 8448);
				GlStateManager.texEnv(8960, 8705, class_308.method_1451(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.texEnv(8960, 34161, 8448);
				GlStateManager.texEnv(8960, 34162, 8448);
				GlStateManager.texEnv(8960, 34176, 5890);
				GlStateManager.texEnv(8960, 34177, 34168);
				GlStateManager.texEnv(8960, 34178, 34166);
				GlStateManager.texEnv(8960, 34184, 5890);
				GlStateManager.texEnv(8960, 34185, 34168);
				GlStateManager.texEnv(8960, 34186, 34166);
				GlStateManager.texEnv(8960, 34192, 768);
				GlStateManager.texEnv(8960, 34193, 768);
				GlStateManager.texEnv(8960, 34194, 770);
				GlStateManager.texEnv(8960, 34200, 770);
				GlStateManager.texEnv(8960, 34201, 770);
				GlStateManager.texEnv(8960, 34202, 770);
				GlStateManager.texEnv(8960, 34163, 1.0F);
				GlStateManager.texEnv(8960, 3356, 1.0F);
				GlStateManager.disableNormalize();
				GlStateManager.shadeModel(7425);
				GlStateManager.disableRescaleNormal();
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.clearDepth(1.0);
				GlStateManager.lineWidth(1.0F);
				GlStateManager.normal3f(0.0F, 0.0F, 1.0F);
				GlStateManager.polygonMode(1028, 6914);
				GlStateManager.polygonMode(1029, 6914);
			}

			@Override
			public void method_4473() {
			}
		},
		field_5128 {
			@Override
			public void method_4472() {
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			}

			@Override
			public void method_4473() {
				GlStateManager.disableBlend();
			}
		},
		field_5125 {
			@Override
			public void method_4472() {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(516, 0.003921569F);
			}

			@Override
			public void method_4473() {
				GlStateManager.disableBlend();
				GlStateManager.alphaFunc(516, 0.1F);
				GlStateManager.depthMask(true);
			}
		};

		private class_1032() {
		}

		public abstract void method_4472();

		public abstract void method_4473();
	}

	@Environment(EnvType.CLIENT)
	static class class_1034 {
		public int field_5148 = 519;
		public int field_16203;
		public int field_5147 = -1;

		private class_1034() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1035 {
		public final GlStateManager.class_1034 field_5149 = new GlStateManager.class_1034();
		public int field_5153 = -1;
		public int field_5152 = 7680;
		public int field_5151 = 7680;
		public int field_5150 = 7680;

		private class_1035() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1036 {
		field_5154,
		field_5155,
		field_5156,
		field_5157;
	}

	@Environment(EnvType.CLIENT)
	static class class_1037 {
		public final GlStateManager.class_1018 field_5159;
		public final int field_5161;
		public int field_5160 = -1;

		public class_1037(int i, int j) {
			this.field_5161 = i;
			this.field_5159 = new GlStateManager.class_1018(j);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1038 {
		public final GlStateManager.class_1037 field_5162 = new GlStateManager.class_1037(8192, 3168);
		public final GlStateManager.class_1037 field_5163 = new GlStateManager.class_1037(8193, 3169);
		public final GlStateManager.class_1037 field_5164 = new GlStateManager.class_1037(8194, 3170);
		public final GlStateManager.class_1037 field_5165 = new GlStateManager.class_1037(8195, 3171);

		private class_1038() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1039 {
		public final GlStateManager.class_1018 field_5166 = new GlStateManager.class_1018(3553);
		public int field_5167;

		private class_1039() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1040 {
		field_5169;

		protected int field_5172;
		protected int field_5171;
		protected int field_5170;
		protected int field_5168;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_5118 {
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

		private class_5118(int j) {
			this.value = j;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_5119 {
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

		private class_5119(int j) {
			this.value = j;
		}
	}
}
