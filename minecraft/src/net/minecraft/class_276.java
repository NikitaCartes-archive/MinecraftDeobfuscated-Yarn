package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_276 {
	public int field_1482;
	public int field_1481;
	public int field_1480;
	public int field_1477;
	public final boolean field_1478;
	public int field_1476;
	public int field_1475;
	public int field_1474;
	public final float[] field_1479;
	public int field_1483;

	public class_276(int i, int j, boolean bl, boolean bl2) {
		this.field_1478 = bl;
		this.field_1476 = -1;
		this.field_1475 = -1;
		this.field_1474 = -1;
		this.field_1479 = new float[4];
		this.field_1479[0] = 1.0F;
		this.field_1479[1] = 1.0F;
		this.field_1479[2] = 1.0F;
		this.field_1479[3] = 0.0F;
		this.method_1234(i, j, bl2);
	}

	public void method_1234(int i, int j, boolean bl) {
		if (!GLX.isUsingFBOs()) {
			this.field_1480 = i;
			this.field_1477 = j;
		} else {
			GlStateManager.enableDepthTest();
			if (this.field_1476 >= 0) {
				this.method_1238();
			}

			this.method_1231(i, j, bl);
			GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
		}
	}

	public void method_1238() {
		if (GLX.isUsingFBOs()) {
			this.method_1242();
			this.method_1240();
			if (this.field_1474 > -1) {
				GLX.glDeleteRenderbuffers(this.field_1474);
				this.field_1474 = -1;
			}

			if (this.field_1475 > -1) {
				TextureUtil.releaseTextureId(this.field_1475);
				this.field_1475 = -1;
			}

			if (this.field_1476 > -1) {
				GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
				GLX.glDeleteFramebuffers(this.field_1476);
				this.field_1476 = -1;
			}
		}
	}

	public void method_1231(int i, int j, boolean bl) {
		this.field_1480 = i;
		this.field_1477 = j;
		this.field_1482 = i;
		this.field_1481 = j;
		if (!GLX.isUsingFBOs()) {
			this.method_1230(bl);
		} else {
			this.field_1476 = GLX.glGenFramebuffers();
			this.field_1475 = TextureUtil.generateTextureId();
			if (this.field_1478) {
				this.field_1474 = GLX.glGenRenderbuffers();
			}

			this.method_1232(9728);
			GlStateManager.bindTexture(this.field_1475);
			GlStateManager.texImage2D(3553, 0, 32856, this.field_1482, this.field_1481, 0, 6408, 5121, null);
			GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, this.field_1476);
			GLX.glFramebufferTexture2D(GLX.GL_FRAMEBUFFER, GLX.GL_COLOR_ATTACHMENT0, 3553, this.field_1475, 0);
			if (this.field_1478) {
				GLX.glBindRenderbuffer(GLX.GL_RENDERBUFFER, this.field_1474);
				GLX.glRenderbufferStorage(GLX.GL_RENDERBUFFER, 33190, this.field_1482, this.field_1481);
				GLX.glFramebufferRenderbuffer(GLX.GL_FRAMEBUFFER, GLX.GL_DEPTH_ATTACHMENT, GLX.GL_RENDERBUFFER, this.field_1474);
			}

			this.method_1239();
			this.method_1230(bl);
			this.method_1242();
		}
	}

	public void method_1232(int i) {
		if (GLX.isUsingFBOs()) {
			this.field_1483 = i;
			GlStateManager.bindTexture(this.field_1475);
			GlStateManager.texParameter(3553, 10241, i);
			GlStateManager.texParameter(3553, 10240, i);
			GlStateManager.texParameter(3553, 10242, 10496);
			GlStateManager.texParameter(3553, 10243, 10496);
			GlStateManager.bindTexture(0);
		}
	}

	public void method_1239() {
		int i = GLX.glCheckFramebufferStatus(GLX.GL_FRAMEBUFFER);
		if (i != GLX.GL_FRAMEBUFFER_COMPLETE) {
			if (i == GLX.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			} else if (i == GLX.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			} else if (i == GLX.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
			} else if (i == GLX.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
			} else {
				throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
			}
		}
	}

	public void method_1241() {
		if (GLX.isUsingFBOs()) {
			GlStateManager.bindTexture(this.field_1475);
		}
	}

	public void method_1242() {
		if (GLX.isUsingFBOs()) {
			GlStateManager.bindTexture(0);
		}
	}

	public void method_1235(boolean bl) {
		if (GLX.isUsingFBOs()) {
			GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, this.field_1476);
			if (bl) {
				GlStateManager.viewport(0, 0, this.field_1480, this.field_1477);
			}
		}
	}

	public void method_1240() {
		if (GLX.isUsingFBOs()) {
			GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
		}
	}

	public void method_1236(float f, float g, float h, float i) {
		this.field_1479[0] = f;
		this.field_1479[1] = g;
		this.field_1479[2] = h;
		this.field_1479[3] = i;
	}

	public void method_1237(int i, int j) {
		this.method_1233(i, j, true);
	}

	public void method_1233(int i, int j, boolean bl) {
		if (GLX.isUsingFBOs()) {
			GlStateManager.colorMask(true, true, true, false);
			GlStateManager.disableDepthTest();
			GlStateManager.depthMask(false);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0.0, (double)i, (double)j, 0.0, 1000.0, 3000.0);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
			GlStateManager.viewport(0, 0, i, j);
			GlStateManager.enableTexture();
			GlStateManager.disableLighting();
			GlStateManager.disableAlphaTest();
			if (bl) {
				GlStateManager.disableBlend();
				GlStateManager.enableColorMaterial();
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.method_1241();
			float f = (float)i;
			float g = (float)j;
			float h = (float)this.field_1480 / (float)this.field_1482;
			float k = (float)this.field_1477 / (float)this.field_1481;
			class_289 lv = class_289.method_1348();
			class_287 lv2 = lv.method_1349();
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315(0.0, (double)g, 0.0).method_1312(0.0, 0.0).method_1323(255, 255, 255, 255).method_1344();
			lv2.method_1315((double)f, (double)g, 0.0).method_1312((double)h, 0.0).method_1323(255, 255, 255, 255).method_1344();
			lv2.method_1315((double)f, 0.0, 0.0).method_1312((double)h, (double)k).method_1323(255, 255, 255, 255).method_1344();
			lv2.method_1315(0.0, 0.0, 0.0).method_1312(0.0, (double)k).method_1323(255, 255, 255, 255).method_1344();
			lv.method_1350();
			this.method_1242();
			GlStateManager.depthMask(true);
			GlStateManager.colorMask(true, true, true, true);
		}
	}

	public void method_1230(boolean bl) {
		this.method_1235(true);
		GlStateManager.clearColor(this.field_1479[0], this.field_1479[1], this.field_1479[2], this.field_1479[3]);
		int i = 16384;
		if (this.field_1478) {
			GlStateManager.clearDepth(1.0);
			i |= 256;
		}

		GlStateManager.clear(i, bl);
		this.method_1240();
	}
}
