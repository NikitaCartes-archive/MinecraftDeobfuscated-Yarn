package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
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
public class class_4493 {
	private static final FloatBuffer field_20466 = GLX.make(MemoryUtil.memAllocFloat(16), floatBuffer -> Untracker.untrack(MemoryUtil.memAddress(floatBuffer)));
	private static final FloatBuffer field_20467 = GLX.make(MemoryUtil.memAllocFloat(4), floatBuffer -> Untracker.untrack(MemoryUtil.memAddress(floatBuffer)));
	private static final class_4493.AlphaTestState field_20468 = new class_4493.AlphaTestState();
	private static final class_4493.CapabilityTracker field_20469 = new class_4493.CapabilityTracker(2896);
	private static final class_4493.CapabilityTracker[] field_20470 = (class_4493.CapabilityTracker[])IntStream.range(0, 8)
		.mapToObj(i -> new class_4493.CapabilityTracker(16384 + i))
		.toArray(class_4493.CapabilityTracker[]::new);
	private static final class_4493.ColorMaterialState field_20471 = new class_4493.ColorMaterialState();
	private static final class_4493.BlendFuncState field_20472 = new class_4493.BlendFuncState();
	private static final class_4493.DepthTestState field_20473 = new class_4493.DepthTestState();
	private static final class_4493.FogState field_20474 = new class_4493.FogState();
	private static final class_4493.CullFaceState field_20475 = new class_4493.CullFaceState();
	private static final class_4493.PolygonOffsetState field_20476 = new class_4493.PolygonOffsetState();
	private static final class_4493.LogicOpState field_20477 = new class_4493.LogicOpState();
	private static final class_4493.TexGenState field_20478 = new class_4493.TexGenState();
	private static final class_4493.ClearState field_20479 = new class_4493.ClearState();
	private static final class_4493.StencilState field_20480 = new class_4493.StencilState();
	private static final class_4493.CapabilityTracker field_20481 = new class_4493.CapabilityTracker(2977);
	private static int field_20482;
	private static final class_4493.Texture2DState[] field_20483 = (class_4493.Texture2DState[])IntStream.range(0, 8)
		.mapToObj(i -> new class_4493.Texture2DState())
		.toArray(class_4493.Texture2DState[]::new);
	private static int field_20484 = 7425;
	private static final class_4493.CapabilityTracker field_20485 = new class_4493.CapabilityTracker(32826);
	private static final class_4493.ColorMask field_20486 = new class_4493.ColorMask();
	private static final class_4493.Color4 field_20487 = new class_4493.Color4();
	private static class_4493.FBOMode field_20488;

	public static void method_21935() {
		GL11.glPushAttrib(8256);
	}

	public static void method_21976() {
		GL11.glPushAttrib(270336);
	}

	public static void method_21997() {
		GL11.glPopAttrib();
	}

	public static void method_22012() {
		field_20468.capState.disable();
	}

	public static void method_22021() {
		field_20468.capState.enable();
	}

	public static void method_21945(int i, float f) {
		if (i != field_20468.func || f != field_20468.ref) {
			field_20468.func = i;
			field_20468.ref = f;
			GL11.glAlphaFunc(i, f);
		}
	}

	public static void method_22028() {
		field_20469.enable();
	}

	public static void method_22034() {
		field_20469.disable();
	}

	public static void method_21944(int i) {
		field_20470[i].enable();
	}

	public static void method_21982(int i) {
		field_20470[i].disable();
	}

	public static void method_22040() {
		field_20471.capState.enable();
	}

	public static void method_22044() {
		field_20471.capState.disable();
	}

	public static void method_21947(int i, int j) {
		if (i != field_20471.face || j != field_20471.mode) {
			field_20471.face = i;
			field_20471.mode = j;
			GL11.glColorMaterial(i, j);
		}
	}

	public static void method_21960(int i, int j, FloatBuffer floatBuffer) {
		GL11.glLightfv(i, j, floatBuffer);
	}

	public static void method_21963(int i, FloatBuffer floatBuffer) {
		GL11.glLightModelfv(i, floatBuffer);
	}

	public static void method_21942(float f, float g, float h) {
		GL11.glNormal3f(f, g, h);
	}

	public static void method_22047() {
		field_20473.capState.disable();
	}

	public static void method_22050() {
		field_20473.capState.enable();
	}

	public static void method_22001(int i) {
		if (i != field_20473.func) {
			field_20473.func = i;
			GL11.glDepthFunc(i);
		}
	}

	public static void method_21974(boolean bl) {
		if (bl != field_20473.mask) {
			field_20473.mask = bl;
			GL11.glDepthMask(bl);
		}
	}

	public static void method_22053() {
		field_20472.capState.disable();
	}

	public static void method_22056() {
		field_20472.capState.enable();
	}

	public static void method_21984(int i, int j) {
		if (i != field_20472.sfactor || j != field_20472.dfactor) {
			field_20472.sfactor = i;
			field_20472.dfactor = j;
			GL11.glBlendFunc(i, j);
		}
	}

	public static void method_21950(int i, int j, int k, int l) {
		if (i != field_20472.sfactor || j != field_20472.dfactor || k != field_20472.srcAlpha || l != field_20472.dstAlpha) {
			field_20472.sfactor = i;
			field_20472.dfactor = j;
			field_20472.srcAlpha = k;
			field_20472.dstAlpha = l;
			method_22018(i, j, k, l);
		}
	}

	public static void method_22015(int i) {
		GL14.glBlendEquation(i);
	}

	public static void method_22022(int i) {
		field_20467.put(0, (float)(i >> 16 & 0xFF) / 255.0F);
		field_20467.put(1, (float)(i >> 8 & 0xFF) / 255.0F);
		field_20467.put(2, (float)(i >> 0 & 0xFF) / 255.0F);
		field_20467.put(3, (float)(i >> 24 & 0xFF) / 255.0F);
		RenderSystem.texEnv(8960, 8705, field_20467);
		RenderSystem.texEnv(8960, 8704, 34160);
		RenderSystem.texEnv(8960, 34161, 7681);
		RenderSystem.texEnv(8960, 34176, 34166);
		RenderSystem.texEnv(8960, 34192, 768);
		RenderSystem.texEnv(8960, 34162, 7681);
		RenderSystem.texEnv(8960, 34184, 5890);
		RenderSystem.texEnv(8960, 34200, 770);
	}

	public static void method_22059() {
		RenderSystem.texEnv(8960, 8704, 8448);
		RenderSystem.texEnv(8960, 34161, 8448);
		RenderSystem.texEnv(8960, 34162, 8448);
		RenderSystem.texEnv(8960, 34176, 5890);
		RenderSystem.texEnv(8960, 34184, 5890);
		RenderSystem.texEnv(8960, 34192, 768);
		RenderSystem.texEnv(8960, 34200, 770);
	}

	public static String method_21973(GLCapabilities gLCapabilities) {
		if (gLCapabilities.OpenGL30) {
			field_20488 = class_4493.FBOMode.BASE;
			class_4492.field_20457 = 36160;
			class_4492.field_20458 = 36161;
			class_4492.field_20459 = 36064;
			class_4492.field_20460 = 36096;
			class_4492.field_20461 = 36053;
			class_4492.field_20462 = 36054;
			class_4492.field_20463 = 36055;
			class_4492.field_20464 = 36059;
			class_4492.field_20465 = 36060;
			return "OpenGL 3.0";
		} else if (gLCapabilities.GL_ARB_framebuffer_object) {
			field_20488 = class_4493.FBOMode.ARB;
			class_4492.field_20457 = 36160;
			class_4492.field_20458 = 36161;
			class_4492.field_20459 = 36064;
			class_4492.field_20460 = 36096;
			class_4492.field_20461 = 36053;
			class_4492.field_20463 = 36055;
			class_4492.field_20462 = 36054;
			class_4492.field_20464 = 36059;
			class_4492.field_20465 = 36060;
			return "ARB_framebuffer_object extension";
		} else if (gLCapabilities.GL_EXT_framebuffer_object) {
			field_20488 = class_4493.FBOMode.EXT;
			class_4492.field_20457 = 36160;
			class_4492.field_20458 = 36161;
			class_4492.field_20459 = 36064;
			class_4492.field_20460 = 36096;
			class_4492.field_20461 = 36053;
			class_4492.field_20463 = 36055;
			class_4492.field_20462 = 36054;
			class_4492.field_20464 = 36059;
			class_4492.field_20465 = 36060;
			return "EXT_framebuffer_object extension";
		} else {
			throw new IllegalStateException("Could not initialize framebuffer support.");
		}
	}

	public static int method_22002(int i, int j) {
		return GL20.glGetProgrami(i, j);
	}

	public static void method_22016(int i, int j) {
		GL20.glAttachShader(i, j);
	}

	public static void method_22029(int i) {
		GL20.glDeleteShader(i);
	}

	public static int method_22035(int i) {
		return GL20.glCreateShader(i);
	}

	public static void method_21961(int i, CharSequence charSequence) {
		GL20.glShaderSource(i, charSequence);
	}

	public static void method_22041(int i) {
		GL20.glCompileShader(i);
	}

	public static int method_22023(int i, int j) {
		return GL20.glGetShaderi(i, j);
	}

	public static void method_22045(int i) {
		GL20.glUseProgram(i);
	}

	public static int method_22062() {
		return GL20.glCreateProgram();
	}

	public static void method_22048(int i) {
		GL20.glDeleteProgram(i);
	}

	public static void method_22051(int i) {
		GL20.glLinkProgram(i);
	}

	public static int method_21990(int i, CharSequence charSequence) {
		return GL20.glGetUniformLocation(i, charSequence);
	}

	public static void method_21964(int i, IntBuffer intBuffer) {
		GL20.glUniform1iv(i, intBuffer);
	}

	public static void method_22030(int i, int j) {
		GL20.glUniform1i(i, j);
	}

	public static void method_21991(int i, FloatBuffer floatBuffer) {
		GL20.glUniform1fv(i, floatBuffer);
	}

	public static void method_21992(int i, IntBuffer intBuffer) {
		GL20.glUniform2iv(i, intBuffer);
	}

	public static void method_22007(int i, FloatBuffer floatBuffer) {
		GL20.glUniform2fv(i, floatBuffer);
	}

	public static void method_22008(int i, IntBuffer intBuffer) {
		GL20.glUniform3iv(i, intBuffer);
	}

	public static void method_22019(int i, FloatBuffer floatBuffer) {
		GL20.glUniform3fv(i, floatBuffer);
	}

	public static void method_22020(int i, IntBuffer intBuffer) {
		GL20.glUniform4iv(i, intBuffer);
	}

	public static void method_22026(int i, FloatBuffer floatBuffer) {
		GL20.glUniform4fv(i, floatBuffer);
	}

	public static void method_21966(int i, boolean bl, FloatBuffer floatBuffer) {
		GL20.glUniformMatrix2fv(i, bl, floatBuffer);
	}

	public static void method_21993(int i, boolean bl, FloatBuffer floatBuffer) {
		GL20.glUniformMatrix3fv(i, bl, floatBuffer);
	}

	public static void method_22009(int i, boolean bl, FloatBuffer floatBuffer) {
		GL20.glUniformMatrix4fv(i, bl, floatBuffer);
	}

	public static int method_22006(int i, CharSequence charSequence) {
		return GL20.glGetAttribLocation(i, charSequence);
	}

	public static int method_22065() {
		return GL15.glGenBuffers();
	}

	public static void method_22036(int i, int j) {
		GL15.glBindBuffer(i, j);
	}

	public static void method_21962(int i, ByteBuffer byteBuffer, int j) {
		GL15.glBufferData(i, byteBuffer, j);
	}

	public static void method_22054(int i) {
		GL15.glDeleteBuffers(i);
	}

	public static void method_22042(int i, int j) {
		switch (field_20488) {
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

	public static void method_22046(int i, int j) {
		switch (field_20488) {
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

	public static void method_22057(int i) {
		switch (field_20488) {
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

	public static void method_22060(int i) {
		switch (field_20488) {
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

	public static int method_22068() {
		switch (field_20488) {
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

	public static int method_22070() {
		switch (field_20488) {
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

	public static void method_21987(int i, int j, int k, int l) {
		switch (field_20488) {
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

	public static void method_22004(int i, int j, int k, int l) {
		switch (field_20488) {
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

	public static int method_22063(int i) {
		switch (field_20488) {
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

	public static void method_21951(int i, int j, int k, int l, int m) {
		switch (field_20488) {
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

	public static void method_22069(int i) {
		GL13.glClientActiveTexture(i);
	}

	public static void method_21946(int i, float f, float g) {
		GL13.glMultiTexCoord2f(i, f, g);
	}

	public static void method_22018(int i, int j, int k, int l) {
		GL14.glBlendFuncSeparate(i, j, k, l);
	}

	public static String method_22049(int i, int j) {
		return GL20.glGetShaderInfoLog(i, j);
	}

	public static String method_22052(int i, int j) {
		return GL20.glGetProgramInfoLog(i, j);
	}

	public static void method_22072() {
		field_20474.capState.enable();
	}

	public static void method_22074() {
		field_20474.capState.disable();
	}

	public static void method_22071(int i) {
		if (i != field_20474.mode) {
			field_20474.mode = i;
			method_22055(2917, i);
		}
	}

	public static void method_21940(float f) {
		if (f != field_20474.density) {
			field_20474.density = f;
			GL11.glFogf(2914, f);
		}
	}

	public static void method_21978(float f) {
		if (f != field_20474.start) {
			field_20474.start = f;
			GL11.glFogf(2915, f);
		}
	}

	public static void method_21998(float f) {
		if (f != field_20474.end) {
			field_20474.end = f;
			GL11.glFogf(2916, f);
		}
	}

	public static void method_22033(int i, FloatBuffer floatBuffer) {
		GL11.glFogfv(i, floatBuffer);
	}

	public static void method_22055(int i, int j) {
		GL11.glFogi(i, j);
	}

	public static void method_22076() {
		field_20475.capState.enable();
	}

	public static void method_22078() {
		field_20475.capState.disable();
	}

	public static void method_22073(int i) {
		if (i != field_20475.mode) {
			field_20475.mode = i;
			GL11.glCullFace(i);
		}
	}

	public static void method_22058(int i, int j) {
		GL11.glPolygonMode(i, j);
	}

	public static void method_22080() {
		field_20476.capFill.enable();
	}

	public static void method_22082() {
		field_20476.capFill.disable();
	}

	public static void method_22084() {
		field_20476.capLine.enable();
	}

	public static void method_22086() {
		field_20476.capLine.disable();
	}

	public static void method_21941(float f, float g) {
		if (f != field_20476.factor || g != field_20476.units) {
			field_20476.factor = f;
			field_20476.units = g;
			GL11.glPolygonOffset(f, g);
		}
	}

	public static void method_21906() {
		field_20477.capState.enable();
	}

	public static void method_21908() {
		field_20477.capState.disable();
	}

	public static void method_22075(int i) {
		if (i != field_20477.opcode) {
			field_20477.opcode = i;
			GL11.glLogicOp(i);
		}
	}

	public static void method_21968(class_4493.TexCoord texCoord) {
		method_22010(texCoord).capState.enable();
	}

	public static void method_21995(class_4493.TexCoord texCoord) {
		method_22010(texCoord).capState.disable();
	}

	public static void method_21969(class_4493.TexCoord texCoord, int i) {
		class_4493.TexGenCoordState texGenCoordState = method_22010(texCoord);
		if (i != texGenCoordState.mode) {
			texGenCoordState.mode = i;
			GL11.glTexGeni(texGenCoordState.coord, 9472, i);
		}
	}

	public static void method_21970(class_4493.TexCoord texCoord, int i, FloatBuffer floatBuffer) {
		GL11.glTexGenfv(method_22010(texCoord).coord, i, floatBuffer);
	}

	private static class_4493.TexGenCoordState method_22010(class_4493.TexCoord texCoord) {
		switch (texCoord) {
			case S:
				return field_20478.s;
			case T:
				return field_20478.t;
			case R:
				return field_20478.r;
			case Q:
				return field_20478.q;
			default:
				return field_20478.s;
		}
	}

	public static void method_22077(int i) {
		if (field_20482 != i - 33984) {
			field_20482 = i - 33984;
			method_22066(i);
		}
	}

	public static void method_21910() {
		field_20483[field_20482].capState.enable();
	}

	public static void method_21912() {
		field_20483[field_20482].capState.disable();
	}

	public static void method_21989(int i, int j, FloatBuffer floatBuffer) {
		GL11.glTexEnvfv(i, j, floatBuffer);
	}

	public static void method_21949(int i, int j, int k) {
		GL11.glTexEnvi(i, j, k);
	}

	public static void method_21948(int i, int j, float f) {
		GL11.glTexEnvf(i, j, f);
	}

	public static void method_21985(int i, int j, float f) {
		GL11.glTexParameterf(i, j, f);
	}

	public static void method_21986(int i, int j, int k) {
		GL11.glTexParameteri(i, j, k);
	}

	public static int method_22003(int i, int j, int k) {
		return GL11.glGetTexLevelParameteri(i, j, k);
	}

	public static int method_21914() {
		return GL11.glGenTextures();
	}

	public static void method_22079(int i) {
		GL11.glDeleteTextures(i);

		for (class_4493.Texture2DState texture2DState : field_20483) {
			if (texture2DState.boundTexture == i) {
				texture2DState.boundTexture = -1;
			}
		}
	}

	public static void method_22081(int i) {
		if (i != field_20483[field_20482].boundTexture) {
			field_20483[field_20482].boundTexture = i;
			GL11.glBindTexture(3553, i);
		}
	}

	public static void method_21954(int i, int j, int k, int l, int m, int n, int o, int p, @Nullable IntBuffer intBuffer) {
		GL11.glTexImage2D(i, j, k, l, m, n, o, p, intBuffer);
	}

	public static void method_21953(int i, int j, int k, int l, int m, int n, int o, int p, long q) {
		GL11.glTexSubImage2D(i, j, k, l, m, n, o, p, q);
	}

	public static void method_21952(int i, int j, int k, int l, int m, int n, int o, int p) {
		GL11.glCopyTexSubImage2D(i, j, k, l, m, n, o, p);
	}

	public static void method_21957(int i, int j, int k, int l, long m) {
		GL11.glGetTexImage(i, j, k, l, m);
	}

	public static void method_21916() {
		field_20481.enable();
	}

	public static void method_21918() {
		field_20481.disable();
	}

	public static void method_22083(int i) {
		if (i != field_20484) {
			field_20484 = i;
			GL11.glShadeModel(i);
		}
	}

	public static void method_21920() {
		field_20485.enable();
	}

	public static void method_21922() {
		field_20485.disable();
	}

	public static void method_22025(int i, int j, int k, int l) {
		class_4493.Viewport.INSTANCE.x = i;
		class_4493.Viewport.INSTANCE.y = j;
		class_4493.Viewport.INSTANCE.width = k;
		class_4493.Viewport.INSTANCE.height = l;
		GL11.glViewport(i, j, k, l);
	}

	public static void method_21975(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		if (bl != field_20486.red || bl2 != field_20486.green || bl3 != field_20486.blue || bl4 != field_20486.alpha) {
			field_20486.red = bl;
			field_20486.green = bl2;
			field_20486.blue = bl3;
			field_20486.alpha = bl4;
			GL11.glColorMask(bl, bl2, bl3, bl4);
		}
	}

	public static void method_22017(int i, int j, int k) {
		if (i != field_20480.subState.func || i != field_20480.subState.field_16203 || i != field_20480.subState.field_5147) {
			field_20480.subState.func = i;
			field_20480.subState.field_16203 = j;
			field_20480.subState.field_5147 = k;
			GL11.glStencilFunc(i, j, k);
		}
	}

	public static void method_22085(int i) {
		if (i != field_20480.field_5153) {
			field_20480.field_5153 = i;
			GL11.glStencilMask(i);
		}
	}

	public static void method_22024(int i, int j, int k) {
		if (i != field_20480.field_5152 || j != field_20480.field_5151 || k != field_20480.field_5150) {
			field_20480.field_5152 = i;
			field_20480.field_5151 = j;
			field_20480.field_5150 = k;
			GL11.glStencilOp(i, j, k);
		}
	}

	public static void method_21936(double d) {
		if (d != field_20479.clearDepth) {
			field_20479.clearDepth = d;
			GL11.glClearDepth(d);
		}
	}

	public static void method_21943(float f, float g, float h, float i) {
		if (f != field_20479.clearColor.red || g != field_20479.clearColor.green || h != field_20479.clearColor.blue || i != field_20479.clearColor.alpha) {
			field_20479.clearColor.red = f;
			field_20479.clearColor.green = g;
			field_20479.clearColor.blue = h;
			field_20479.clearColor.alpha = i;
			GL11.glClearColor(f, g, h, i);
		}
	}

	public static void method_22087(int i) {
		if (i != field_20479.field_16202) {
			field_20479.field_16202 = i;
			GL11.glClearStencil(i);
		}
	}

	public static void method_21965(int i, boolean bl) {
		GL11.glClear(i);
		if (bl) {
			RenderSystem.getError();
		}
	}

	public static void method_21907(int i) {
		GL11.glMatrixMode(i);
	}

	public static void method_21924() {
		GL11.glLoadIdentity();
	}

	public static void method_21926() {
		GL11.glPushMatrix();
	}

	public static void method_21928() {
		GL11.glPopMatrix();
	}

	public static void method_22039(int i, FloatBuffer floatBuffer) {
		GL11.glGetFloatv(i, floatBuffer);
	}

	public static Matrix4f method_21909(int i) {
		method_22039(i, field_20466);
		field_20466.rewind();
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.setFromBuffer(field_20466);
		field_20466.rewind();
		return matrix4f;
	}

	public static void method_21939(double d, double e, double f, double g, double h, double i) {
		GL11.glOrtho(d, e, f, g, h, i);
	}

	public static void method_21981(float f, float g, float h, float i) {
		GL11.glRotatef(f, g, h, i);
	}

	public static void method_21938(double d, double e, double f, double g) {
		GL11.glRotated(d, e, f, g);
	}

	public static void method_21980(float f, float g, float h) {
		GL11.glScalef(f, g, h);
	}

	public static void method_21937(double d, double e, double f) {
		GL11.glScaled(d, e, f);
	}

	public static void method_21999(float f, float g, float h) {
		GL11.glTranslatef(f, g, h);
	}

	public static void method_21977(double d, double e, double f) {
		GL11.glTranslated(d, e, f);
	}

	public static void method_21972(FloatBuffer floatBuffer) {
		GL11.glMultMatrixf(floatBuffer);
	}

	public static void method_21971(Matrix4f matrix4f) {
		matrix4f.putIntoBuffer(field_20466);
		field_20466.rewind();
		method_21972(field_20466);
	}

	public static void method_22000(float f, float g, float h, float i) {
		if (f != field_20487.red || g != field_20487.green || h != field_20487.blue || i != field_20487.alpha) {
			field_20487.red = f;
			field_20487.green = g;
			field_20487.blue = h;
			field_20487.alpha = i;
			GL11.glColor4f(f, g, h, i);
		}
	}

	public static void method_21979(float f, float g) {
		GL11.glTexCoord2f(f, g);
	}

	public static void method_22014(float f, float g, float h) {
		GL11.glVertex3f(f, g, h);
	}

	public static void method_21930() {
		field_20487.red = -1.0F;
		field_20487.green = -1.0F;
		field_20487.blue = -1.0F;
		field_20487.alpha = -1.0F;
	}

	public static void method_22031(int i, int j, int k) {
		GL11.glNormalPointer(i, j, (long)k);
	}

	public static void method_21959(int i, int j, ByteBuffer byteBuffer) {
		GL11.glNormalPointer(i, j, byteBuffer);
	}

	public static void method_22032(int i, int j, int k, int l) {
		GL11.glTexCoordPointer(i, j, k, (long)l);
	}

	public static void method_21958(int i, int j, int k, ByteBuffer byteBuffer) {
		GL11.glTexCoordPointer(i, j, k, byteBuffer);
	}

	public static void method_22038(int i, int j, int k, int l) {
		GL11.glVertexPointer(i, j, k, (long)l);
	}

	public static void method_21988(int i, int j, int k, ByteBuffer byteBuffer) {
		GL11.glVertexPointer(i, j, k, byteBuffer);
	}

	public static void method_22043(int i, int j, int k, int l) {
		GL11.glColorPointer(i, j, k, (long)l);
	}

	public static void method_22005(int i, int j, int k, ByteBuffer byteBuffer) {
		GL11.glColorPointer(i, j, k, byteBuffer);
	}

	public static void method_21911(int i) {
		GL11.glDisableClientState(i);
	}

	public static void method_21913(int i) {
		GL11.glEnableClientState(i);
	}

	public static void method_21915(int i) {
		GL11.glBegin(i);
	}

	public static void method_21932() {
		GL11.glEnd();
	}

	public static void method_22037(int i, int j, int k) {
		GL11.glDrawArrays(i, j, k);
	}

	public static void method_22013(float f) {
		GL11.glLineWidth(f);
	}

	public static void method_21917(int i) {
		GL11.glCallList(i);
	}

	public static void method_22061(int i, int j) {
		GL11.glDeleteLists(i, j);
	}

	public static void method_22064(int i, int j) {
		GL11.glNewList(i, j);
	}

	public static void method_21933() {
		GL11.glEndList();
	}

	public static int method_21919(int i) {
		return GL11.glGenLists(i);
	}

	public static void method_22067(int i, int j) {
		GL11.glPixelStorei(i, j);
	}

	public static void method_21983(int i, float f) {
		GL11.glPixelTransferf(i, f);
	}

	public static void method_21956(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
		GL11.glReadPixels(i, j, k, l, m, n, byteBuffer);
	}

	public static void method_21955(int i, int j, int k, int l, int m, int n, long o) {
		GL11.glReadPixels(i, j, k, l, m, n, o);
	}

	public static int method_21934() {
		return GL11.glGetError();
	}

	public static String method_21921(int i) {
		return GL11.glGetString(i);
	}

	public static void method_22027(int i, IntBuffer intBuffer) {
		GL11.glGetIntegerv(i, intBuffer);
	}

	public static int method_21923(int i) {
		return GL11.glGetInteger(i);
	}

	public static void method_21967(class_4493.RenderMode renderMode) {
		renderMode.begin();
	}

	public static void method_21994(class_4493.RenderMode renderMode) {
		renderMode.end();
	}

	@Environment(EnvType.CLIENT)
	static class AlphaTestState {
		public final class_4493.CapabilityTracker capState = new class_4493.CapabilityTracker(3008);
		public int func = 519;
		public float ref = -1.0F;

		private AlphaTestState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class BlendFuncState {
		public final class_4493.CapabilityTracker capState = new class_4493.CapabilityTracker(3042);
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
		public final class_4493.Color4 clearColor = new class_4493.Color4(0.0F, 0.0F, 0.0F, 0.0F);
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
		public final class_4493.CapabilityTracker capState = new class_4493.CapabilityTracker(2903);
		public int face = 1032;
		public int mode = 5634;

		private ColorMaterialState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class CullFaceState {
		public final class_4493.CapabilityTracker capState = new class_4493.CapabilityTracker(2884);
		public int mode = 1029;

		private CullFaceState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class DepthTestState {
		public final class_4493.CapabilityTracker capState = new class_4493.CapabilityTracker(2929);
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
		public final class_4493.CapabilityTracker capState = new class_4493.CapabilityTracker(2912);
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
		public final class_4493.CapabilityTracker capState = new class_4493.CapabilityTracker(3058);
		public int opcode = 5379;

		private LogicOpState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class PolygonOffsetState {
		public final class_4493.CapabilityTracker capFill = new class_4493.CapabilityTracker(32823);
		public final class_4493.CapabilityTracker capLine = new class_4493.CapabilityTracker(10754);
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
				RenderSystem.blendFunc(class_4493.class_4535.ONE, class_4493.class_4534.ZERO);
				RenderSystem.blendFuncSeparate(class_4493.class_4535.ONE, class_4493.class_4534.ZERO, class_4493.class_4535.ONE, class_4493.class_4534.ZERO);
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
				RenderSystem.disableTexGen(class_4493.TexCoord.S);
				RenderSystem.texGenMode(class_4493.TexCoord.S, 9216);
				RenderSystem.texGenParam(class_4493.TexCoord.S, 9474, GuiLighting.singletonBuffer(1.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.texGenParam(class_4493.TexCoord.S, 9217, GuiLighting.singletonBuffer(1.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.disableTexGen(class_4493.TexCoord.T);
				RenderSystem.texGenMode(class_4493.TexCoord.T, 9216);
				RenderSystem.texGenParam(class_4493.TexCoord.T, 9474, GuiLighting.singletonBuffer(0.0F, 1.0F, 0.0F, 0.0F));
				RenderSystem.texGenParam(class_4493.TexCoord.T, 9217, GuiLighting.singletonBuffer(0.0F, 1.0F, 0.0F, 0.0F));
				RenderSystem.disableTexGen(class_4493.TexCoord.R);
				RenderSystem.texGenMode(class_4493.TexCoord.R, 9216);
				RenderSystem.texGenParam(class_4493.TexCoord.R, 9474, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.texGenParam(class_4493.TexCoord.R, 9217, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.disableTexGen(class_4493.TexCoord.Q);
				RenderSystem.texGenMode(class_4493.TexCoord.Q, 9216);
				RenderSystem.texGenParam(class_4493.TexCoord.Q, 9474, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
				RenderSystem.texGenParam(class_4493.TexCoord.Q, 9217, GuiLighting.singletonBuffer(0.0F, 0.0F, 0.0F, 0.0F));
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
				RenderSystem.blendFunc(class_4493.class_4535.SRC_ALPHA, class_4493.class_4534.ONE_MINUS_SRC_ALPHA);
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
		public final class_4493.StencilSubState subState = new class_4493.StencilSubState();
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
		public final class_4493.CapabilityTracker capState;
		public final int coord;
		public int mode = -1;

		public TexGenCoordState(int i, int j) {
			this.coord = i;
			this.capState = new class_4493.CapabilityTracker(j);
		}
	}

	@Environment(EnvType.CLIENT)
	static class TexGenState {
		public final class_4493.TexGenCoordState s = new class_4493.TexGenCoordState(8192, 3168);
		public final class_4493.TexGenCoordState t = new class_4493.TexGenCoordState(8193, 3169);
		public final class_4493.TexGenCoordState r = new class_4493.TexGenCoordState(8194, 3170);
		public final class_4493.TexGenCoordState q = new class_4493.TexGenCoordState(8195, 3171);

		private TexGenState() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class Texture2DState {
		public final class_4493.CapabilityTracker capState = new class_4493.CapabilityTracker(3553);
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
