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
public class Framebuffer {
	public int textureWidth;
	public int textureHeight;
	public int viewportWidth;
	public int viewportHeight;
	public final boolean useDepthAttachment;
	public int fbo;
	public int colorAttachment;
	public int depthAttachment;
	public final float[] clearColor;
	public int texFilter;

	public Framebuffer(int width, int height, boolean useDepth, boolean getError) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.useDepthAttachment = useDepth;
		this.fbo = -1;
		this.colorAttachment = -1;
		this.depthAttachment = -1;
		this.clearColor = new float[4];
		this.clearColor[0] = 1.0F;
		this.clearColor[1] = 1.0F;
		this.clearColor[2] = 1.0F;
		this.clearColor[3] = 0.0F;
		this.resize(width, height, getError);
	}

	public void resize(int width, int height, boolean getError) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.resizeInternal(width, height, getError));
		} else {
			this.resizeInternal(width, height, getError);
		}
	}

	private void resizeInternal(int width, int height, boolean getError) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.enableDepthTest();
		if (this.fbo >= 0) {
			this.delete();
		}

		this.initFbo(width, height, getError);
		GlStateManager.bindFramebuffer(FramebufferInfo.FRAME_BUFFER, 0);
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
			TextureUtil.method_24957(this.colorAttachment);
			this.colorAttachment = -1;
		}

		if (this.fbo > -1) {
			GlStateManager.bindFramebuffer(FramebufferInfo.FRAME_BUFFER, 0);
			GlStateManager.deleteFramebuffers(this.fbo);
			this.fbo = -1;
		}
	}

	public void initFbo(int width, int height, boolean getError) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.viewportWidth = width;
		this.viewportHeight = height;
		this.textureWidth = width;
		this.textureHeight = height;
		this.fbo = GlStateManager.genFramebuffers();
		this.colorAttachment = TextureUtil.method_24956();
		if (this.useDepthAttachment) {
			this.depthAttachment = GlStateManager.genRenderbuffers();
		}

		this.setTexFilter(9728);
		GlStateManager.bindTexture(this.colorAttachment);
		GlStateManager.texImage2D(3553, 0, 32856, this.textureWidth, this.textureHeight, 0, 6408, 5121, null);
		GlStateManager.bindFramebuffer(FramebufferInfo.FRAME_BUFFER, this.fbo);
		GlStateManager.framebufferTexture2D(FramebufferInfo.FRAME_BUFFER, FramebufferInfo.COLOR_ATTACHMENT, 3553, this.colorAttachment, 0);
		if (this.useDepthAttachment) {
			GlStateManager.bindRenderbuffer(FramebufferInfo.RENDER_BUFFER, this.depthAttachment);
			GlStateManager.renderbufferStorage(FramebufferInfo.RENDER_BUFFER, 33190, this.textureWidth, this.textureHeight);
			GlStateManager.framebufferRenderbuffer(FramebufferInfo.FRAME_BUFFER, FramebufferInfo.DEPTH_ATTACHMENT, FramebufferInfo.RENDER_BUFFER, this.depthAttachment);
		}

		this.checkFramebufferStatus();
		this.clear(getError);
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
		int i = GlStateManager.checkFramebufferStatus(FramebufferInfo.FRAME_BUFFER);
		if (i != FramebufferInfo.FRAME_BUFFER_COMPLETE) {
			if (i == FramebufferInfo.FRAME_BUFFER_INCOMPLETE_ATTACHMENT) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			} else if (i == FramebufferInfo.FRAME_BUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			} else if (i == FramebufferInfo.FRAME_BUFFER_INCOMPLETE_DRAW_BUFFER) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
			} else if (i == FramebufferInfo.FRAME_BUFFER_INCOMPLETE_READ_BUFFER) {
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

	public void beginWrite(boolean setViewport) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.bind(setViewport));
		} else {
			this.bind(setViewport);
		}
	}

	private void bind(boolean updateViewport) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.bindFramebuffer(FramebufferInfo.FRAME_BUFFER, this.fbo);
		if (updateViewport) {
			GlStateManager.viewport(0, 0, this.viewportWidth, this.viewportHeight);
		}
	}

	public void endWrite() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> GlStateManager.bindFramebuffer(FramebufferInfo.FRAME_BUFFER, 0));
		} else {
			GlStateManager.bindFramebuffer(FramebufferInfo.FRAME_BUFFER, 0);
		}
	}

	public void setClearColor(float r, float g, float b, float a) {
		this.clearColor[0] = r;
		this.clearColor[1] = g;
		this.clearColor[2] = b;
		this.clearColor[3] = a;
	}

	public void draw(int width, int height) {
		this.draw(width, height, true);
	}

	public void draw(int width, int height, boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
		if (!RenderSystem.isInInitPhase()) {
			RenderSystem.recordRenderCall(() -> this.drawInternal(width, height, bl));
		} else {
			this.drawInternal(width, height, bl);
		}
	}

	private void drawInternal(int width, int height, boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlStateManager.colorMask(true, true, true, false);
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0, (double)width, (double)height, 0.0, 1000.0, 3000.0);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
		GlStateManager.viewport(0, 0, width, height);
		GlStateManager.enableTexture();
		GlStateManager.disableLighting();
		GlStateManager.disableAlphaTest();
		if (bl) {
			GlStateManager.disableBlend();
			GlStateManager.enableColorMaterial();
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.beginRead();
		float f = (float)width;
		float g = (float)height;
		float h = (float)this.viewportWidth / (float)this.textureWidth;
		float i = (float)this.viewportHeight / (float)this.textureHeight;
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)g, 0.0).texture(0.0F, 0.0F).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)f, (double)g, 0.0).texture(h, 0.0F).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)f, 0.0, 0.0).texture(h, i).color(255, 255, 255, 255).next();
		bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0F, i).color(255, 255, 255, 255).next();
		tessellator.draw();
		this.endRead();
		GlStateManager.depthMask(true);
		GlStateManager.colorMask(true, true, true, true);
	}

	public void clear(boolean getError) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.beginWrite(true);
		GlStateManager.clearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
		int i = 16384;
		if (this.useDepthAttachment) {
			GlStateManager.clearDepth(1.0);
			i |= 256;
		}

		GlStateManager.clear(i, getError);
		this.endWrite();
	}
}
