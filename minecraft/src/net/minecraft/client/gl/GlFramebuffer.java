package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.FramebufferInfo;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureUtil;

@Environment(EnvType.CLIENT)
public class GlFramebuffer {
	public int texWidth;
	public int texHeight;
	public int viewWidth;
	public int viewHeight;
	public final boolean useDepthAttachment;
	public int fbo;
	public int colorAttachment;
	public int depthAttachment;
	public final float[] clearColor;
	public int texFilter;

	public GlFramebuffer(int i, int j, boolean bl, boolean bl2) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.useDepthAttachment = bl;
		this.fbo = -1;
		this.colorAttachment = -1;
		this.depthAttachment = -1;
		this.clearColor = new float[4];
		this.clearColor[0] = 1.0F;
		this.clearColor[1] = 1.0F;
		this.clearColor[2] = 1.0F;
		this.clearColor[3] = 0.0F;
		this.resize(i, j, bl2);
	}

	public void resize(int i, int j, boolean bl) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.method_22596(i, j, bl));
		} else {
			this.method_22596(i, j, bl);
		}
	}

	private void method_22596(int i, int j, boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.enableDepthTest();
		if (this.fbo >= 0) {
			this.delete();
		}

		this.initFbo(i, j, bl);
		GlStateManager.bindFramebuffer(FramebufferInfo.field_20457, 0);
	}

	public void delete() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.endRead();
		this.endWrite();
		if (this.depthAttachment > -1) {
			GlStateManager.deleteRenderbuffers(this.depthAttachment);
			this.depthAttachment = -1;
		}

		if (this.colorAttachment > -1) {
			TextureUtil.releaseTextureId(this.colorAttachment);
			this.colorAttachment = -1;
		}

		if (this.fbo > -1) {
			GlStateManager.bindFramebuffer(FramebufferInfo.field_20457, 0);
			GlStateManager.deleteFramebuffers(this.fbo);
			this.fbo = -1;
		}
	}

	public void initFbo(int i, int j, boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.viewWidth = i;
		this.viewHeight = j;
		this.texWidth = i;
		this.texHeight = j;
		this.fbo = GlStateManager.genFramebuffers();
		this.colorAttachment = TextureUtil.generateTextureId();
		if (this.useDepthAttachment) {
			this.depthAttachment = GlStateManager.genRenderbuffers();
		}

		this.setTexFilter(9728);
		GlStateManager.bindTexture(this.colorAttachment);
		GlStateManager.texImage2D(3553, 0, 32856, this.texWidth, this.texHeight, 0, 6408, 5121, null);
		GlStateManager.bindFramebuffer(FramebufferInfo.field_20457, this.fbo);
		GlStateManager.framebufferTexture2D(FramebufferInfo.field_20457, FramebufferInfo.field_20459, 3553, this.colorAttachment, 0);
		if (this.useDepthAttachment) {
			GlStateManager.bindRenderbuffer(FramebufferInfo.field_20458, this.depthAttachment);
			GlStateManager.renderbufferStorage(FramebufferInfo.field_20458, 33190, this.texWidth, this.texHeight);
			GlStateManager.framebufferRenderbuffer(FramebufferInfo.field_20457, FramebufferInfo.field_20460, FramebufferInfo.field_20458, this.depthAttachment);
		}

		this.checkFramebufferStatus();
		this.clear(bl);
		this.endRead();
	}

	public void setTexFilter(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.texFilter = i;
		GlStateManager.bindTexture(this.colorAttachment);
		GlStateManager.texParameter(3553, 10241, i);
		GlStateManager.texParameter(3553, 10240, i);
		GlStateManager.texParameter(3553, 10242, 10496);
		GlStateManager.texParameter(3553, 10243, 10496);
		GlStateManager.bindTexture(0);
	}

	public void checkFramebufferStatus() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		int i = GlStateManager.checkFramebufferStatus(FramebufferInfo.field_20457);
		if (i != FramebufferInfo.field_20461) {
			if (i == FramebufferInfo.field_20462) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			} else if (i == FramebufferInfo.field_20463) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			} else if (i == FramebufferInfo.field_20464) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
			} else if (i == FramebufferInfo.field_20465) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
			} else {
				throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
			}
		}
	}

	public void beginRead() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlStateManager.bindTexture(this.colorAttachment);
	}

	public void endRead() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.bindTexture(0);
	}

	public void beginWrite(boolean bl) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.method_22595(bl));
		} else {
			this.method_22595(bl);
		}
	}

	private void method_22595(boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.bindFramebuffer(FramebufferInfo.field_20457, this.fbo);
		if (bl) {
			GlStateManager.viewport(0, 0, this.viewWidth, this.viewHeight);
		}
	}

	public void endWrite() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> GlStateManager.bindFramebuffer(FramebufferInfo.field_20457, 0));
		} else {
			GlStateManager.bindFramebuffer(FramebufferInfo.field_20457, 0);
		}
	}

	public void setClearColor(float f, float g, float h, float i) {
		this.clearColor[0] = f;
		this.clearColor[1] = g;
		this.clearColor[2] = h;
		this.clearColor[3] = i;
	}

	public void draw(int i, int j) {
		this.method_22594(i, j, true);
	}

	public void method_22594(int i, int j, boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
		if (!RenderSystem.isInInitPhase()) {
			RenderSystem.recordRenderCall(() -> this.draw(i, j, bl));
		} else {
			this.draw(i, j, bl);
		}
	}

	private void draw(int i, int j, boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
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
		this.beginRead();
		float f = (float)i;
		float g = (float)j;
		float h = (float)this.viewWidth / (float)this.texWidth;
		float k = (float)this.viewHeight / (float)this.texHeight;
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex(0.0, (double)g, 0.0).texture(0.0F, 0.0F).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)f, (double)g, 0.0).texture(h, 0.0F).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)f, 0.0, 0.0).texture(h, k).color(255, 255, 255, 255).next();
		bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0F, k).color(255, 255, 255, 255).next();
		tessellator.draw();
		this.endRead();
		GlStateManager.depthMask(true);
		GlStateManager.colorMask(true, true, true, true);
	}

	public void clear(boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.beginWrite(true);
		GlStateManager.clearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
		int i = 16384;
		if (this.useDepthAttachment) {
			GlStateManager.clearDepth(1.0);
			i |= 256;
		}

		GlStateManager.clear(i, bl);
		this.endWrite();
	}
}
