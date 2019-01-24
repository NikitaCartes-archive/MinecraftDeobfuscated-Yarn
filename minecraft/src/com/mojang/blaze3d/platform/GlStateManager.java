package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_301;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.util.math.Matrix4f;
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

	public static void blendFunc(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor) {
		blendFunc(sourceFactor.value, destFactor.value);
	}

	public static void blendFunc(int i, int j) {
		if (i != BLEND.sfactor || j != BLEND.dfactor) {
			BLEND.sfactor = i;
			BLEND.dfactor = j;
			GL11.glBlendFunc(i, j);
		}
	}

	public static void blendFuncSeparate(
		GlStateManager.SourceFactor sourceFactor,
		GlStateManager.DestFactor destFactor,
		GlStateManager.SourceFactor sourceFactor2,
		GlStateManager.DestFactor destFactor2
	) {
		blendFuncSeparate(sourceFactor.value, destFactor.value, sourceFactor2.value, destFactor2.value);
	}

	public static void blendFuncSeparate(int i, int j, int k, int l) {
		if (i != BLEND.sfactor || j != BLEND.dfactor || k != BLEND.srcAlpha || l != BLEND.dstAlpha) {
			BLEND.sfactor = i;
			BLEND.dfactor = j;
			BLEND.srcAlpha = k;
			BLEND.dstAlpha = l;
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
		FOG.capState.enable();
	}

	public static void disableFog() {
		FOG.capState.disable();
	}

	public static void fogMode(GlStateManager.FogMode fogMode) {
		fogMode(fogMode.glValue);
	}

	private static void fogMode(int i) {
		if (i != FOG.mode) {
			FOG.mode = i;
			GL11.glFogi(2917, i);
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

	public static void cullFace(GlStateManager.FaceSides faceSides) {
		cullFace(faceSides.glValue);
	}

	private static void cullFace(int i) {
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

	public static void logicOp(GlStateManager.LogicOp logicOp) {
		logicOp(logicOp.glValue);
	}

	public static void logicOp(int i) {
		if (i != COLOR_LOGIC.opcode) {
			COLOR_LOGIC.opcode = i;
			GL11.glLogicOp(i);
		}
	}

	public static void enableTexGen(GlStateManager.TexCoord texCoord) {
		getTexGen(texCoord).capState.enable();
	}

	public static void disableTexGen(GlStateManager.TexCoord texCoord) {
		getTexGen(texCoord).capState.disable();
	}

	public static void texGenMode(GlStateManager.TexCoord texCoord, int i) {
		GlStateManager.TexGenCoordState texGenCoordState = getTexGen(texCoord);
		if (i != texGenCoordState.mode) {
			texGenCoordState.mode = i;
			GL11.glTexGeni(texGenCoordState.coord, 9472, i);
		}
	}

	public static void texGenParam(GlStateManager.TexCoord texCoord, int i, FloatBuffer floatBuffer) {
		GL11.glTexGenfv(getTexGen(texCoord).coord, i, floatBuffer);
	}

	private static GlStateManager.TexGenCoordState getTexGen(GlStateManager.TexCoord texCoord) {
		switch (texCoord) {
			case field_5154:
				return TEX_GEN.s;
			case field_5155:
				return TEX_GEN.t;
			case field_5156:
				return TEX_GEN.r;
			case field_5157:
				return TEX_GEN.q;
			default:
				return TEX_GEN.s;
		}
	}

	public static void activeTexture(int i) {
		if (activeTexture != i - GLX.GL_TEXTURE0) {
			activeTexture = i - GLX.GL_TEXTURE0;
			GLX.glActiveTexture(i);
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

	public static int genTexture() {
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

	public static Matrix4f getMatrix4f(int i) {
		GL11.glGetFloatv(i, MATRIX_BUFFER);
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
		GL11.glMultMatrixf(MATRIX_BUFFER);
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

	public static void setProfile(GlStateManager.RenderMode renderMode) {
		renderMode.begin();
	}

	public static void unsetProfile(GlStateManager.RenderMode renderMode) {
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
	public static enum FaceSides {
		field_5068(1028),
		field_5070(1029),
		field_5071(1032);

		public final int glValue;

		private FaceSides(int j) {
			this.glValue = j;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum FogMode {
		field_5095(9729),
		field_5096(2048),
		field_5097(2049);

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
				GlStateManager.disableAlphaTest();
				GlStateManager.alphaFunc(519, 0.0F);
				GlStateManager.disableLighting();
				GlStateManager.lightModel(2899, GuiLighting.singletonBuffer(0.2F, 0.2F, 0.2F, 1.0F));

				for (int i = 0; i < 8; i++) {
					GlStateManager.disableLight(i);
					GlStateManager.light(16384 + i, 4608, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 1.0F));
					GlStateManager.light(16384 + i, 4611, GuiLighting.singletonBuffer(0.0F, 0.0F, 1.0F, 0.0F));
					if (i == 0) {
						GlStateManager.light(16384 + i, 4609, GuiLighting.singletonBuffer(1.0F, 1.0F, 1.0F, 1.0F));
						GlStateManager.light(16384 + i, 4610, GuiLighting.singletonBuffer(1.0F, 1.0F, 1.0F, 1.0F));
					} else {
						GlStateManager.light(16384 + i, 4609, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 1.0F));
						GlStateManager.light(16384 + i, 4610, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 1.0F));
					}
				}

				GlStateManager.disableColorMaterial();
				GlStateManager.colorMaterial(1032, 5634);
				GlStateManager.disableDepthTest();
				GlStateManager.depthFunc(513);
				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFuncSeparate(
					GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
				);
				GlStateManager.blendEquation(32774);
				GlStateManager.disableFog();
				GlStateManager.fogi(2917, 2048);
				GlStateManager.fogDensity(1.0F);
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(1.0F);
				GlStateManager.fog(2918, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				if (GL.getCapabilities().GL_NV_fog_distance) {
					GlStateManager.fogi(2917, 34140);
				}

				GlStateManager.polygonOffset(0.0F, 0.0F);
				GlStateManager.disableColorLogicOp();
				GlStateManager.logicOp(5379);
				GlStateManager.disableTexGen(GlStateManager.TexCoord.field_5154);
				GlStateManager.texGenMode(GlStateManager.TexCoord.field_5154, 9216);
				GlStateManager.texGenParam(GlStateManager.TexCoord.field_5154, 9474, GuiLighting.singletonBuffer(1.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.texGenParam(GlStateManager.TexCoord.field_5154, 9217, GuiLighting.singletonBuffer(1.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.disableTexGen(GlStateManager.TexCoord.field_5155);
				GlStateManager.texGenMode(GlStateManager.TexCoord.field_5155, 9216);
				GlStateManager.texGenParam(GlStateManager.TexCoord.field_5155, 9474, GuiLighting.singletonBuffer(0.0F, 1.0F, 0.0F, 0.0F));
				GlStateManager.texGenParam(GlStateManager.TexCoord.field_5155, 9217, GuiLighting.singletonBuffer(0.0F, 1.0F, 0.0F, 0.0F));
				GlStateManager.disableTexGen(GlStateManager.TexCoord.field_5156);
				GlStateManager.texGenMode(GlStateManager.TexCoord.field_5156, 9216);
				GlStateManager.texGenParam(GlStateManager.TexCoord.field_5156, 9474, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.texGenParam(GlStateManager.TexCoord.field_5156, 9217, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.disableTexGen(GlStateManager.TexCoord.field_5157);
				GlStateManager.texGenMode(GlStateManager.TexCoord.field_5157, 9216);
				GlStateManager.texGenParam(GlStateManager.TexCoord.field_5157, 9474, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				GlStateManager.texGenParam(GlStateManager.TexCoord.field_5157, 9217, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
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
				GlStateManager.texEnv(8960, 8705, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
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
			public void end() {
			}
		},
		PLAYER_SKIN {
			@Override
			public void begin() {
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			}

			@Override
			public void end() {
				GlStateManager.disableBlend();
			}
		},
		TRANSPARENT_MODEL {
			@Override
			public void begin() {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(516, 0.003921569F);
			}

			@Override
			public void end() {
				GlStateManager.disableBlend();
				GlStateManager.alphaFunc(516, 0.1F);
				GlStateManager.depthMask(true);
			}
		};

		private RenderMode() {
		}

		public abstract void begin();

		public abstract void end();
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

	@Environment(EnvType.CLIENT)
	public static enum TexCoord {
		field_5154,
		field_5155,
		field_5156,
		field_5157;
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
}
