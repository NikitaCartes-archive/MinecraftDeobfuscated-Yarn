package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public abstract class Framebuffer {
	private static final int field_31901 = 0;
	private static final int field_31902 = 1;
	private static final int field_31903 = 2;
	private static final int field_31904 = 3;
	public int textureWidth;
	public int textureHeight;
	public int viewportWidth;
	public int viewportHeight;
	public final boolean useDepthAttachment;
	public int fbo;
	protected int colorAttachment;
	protected int depthAttachment;
	private final float[] clearColor = Util.make(() -> new float[]{1.0F, 1.0F, 1.0F, 0.0F});
	public int texFilter;

	public Framebuffer(boolean useDepth) {
		this.useDepthAttachment = useDepth;
		this.fbo = -1;
		this.colorAttachment = -1;
		this.depthAttachment = -1;
	}

	public void resize(int width, int height, boolean getError) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.resizeInternal(width, height, getError));
		} else {
			this.resizeInternal(width, height, getError);
		}
	}

	private void resizeInternal(int width, int height, boolean getError) {
		RenderSystem.assertOnRenderThreadOrInit();
		GlStateManager._enableDepthTest();
		if (this.fbo >= 0) {
			this.delete();
		}

		this.initFbo(width, height, getError);
		GlStateManager._glBindFramebuffer(36160, 0);
	}

	public void delete() {
		RenderSystem.assertOnRenderThreadOrInit();
		this.endRead();
		this.endWrite();
		if (this.depthAttachment > -1) {
			TextureUtil.releaseTextureId(this.depthAttachment);
			this.depthAttachment = -1;
		}

		if (this.colorAttachment > -1) {
			TextureUtil.releaseTextureId(this.colorAttachment);
			this.colorAttachment = -1;
		}

		if (this.fbo > -1) {
			GlStateManager._glBindFramebuffer(36160, 0);
			GlStateManager._glDeleteFramebuffers(this.fbo);
			this.fbo = -1;
		}
	}

	public void copyDepthFrom(Framebuffer framebuffer) {
		RenderSystem.assertOnRenderThreadOrInit();
		GlStateManager._glBindFramebuffer(36008, framebuffer.fbo);
		GlStateManager._glBindFramebuffer(36009, this.fbo);
		GlStateManager._glBlitFrameBuffer(0, 0, framebuffer.textureWidth, framebuffer.textureHeight, 0, 0, this.textureWidth, this.textureHeight, 256, 9728);
		GlStateManager._glBindFramebuffer(36160, 0);
	}

	public void initFbo(int width, int height, boolean getError) {
		RenderSystem.assertOnRenderThreadOrInit();
		int i = RenderSystem.maxSupportedTextureSize();
		if (width > 0 && width <= i && height > 0 && height <= i) {
			this.viewportWidth = width;
			this.viewportHeight = height;
			this.textureWidth = width;
			this.textureHeight = height;
			this.fbo = GlStateManager.glGenFramebuffers();
			this.colorAttachment = TextureUtil.generateTextureId();
			if (this.useDepthAttachment) {
				this.depthAttachment = TextureUtil.generateTextureId();
				GlStateManager._bindTexture(this.depthAttachment);
				GlStateManager._texParameter(3553, 10241, 9728);
				GlStateManager._texParameter(3553, 10240, 9728);
				GlStateManager._texParameter(3553, 34892, 0);
				GlStateManager._texParameter(3553, 10242, 33071);
				GlStateManager._texParameter(3553, 10243, 33071);
				GlStateManager._texImage2D(3553, 0, 6402, this.textureWidth, this.textureHeight, 0, 6402, 5126, null);
			}

			this.setTexFilter(9728);
			GlStateManager._bindTexture(this.colorAttachment);
			GlStateManager._texParameter(3553, 10242, 33071);
			GlStateManager._texParameter(3553, 10243, 33071);
			GlStateManager._texImage2D(3553, 0, 32856, this.textureWidth, this.textureHeight, 0, 6408, 5121, null);
			GlStateManager._glBindFramebuffer(36160, this.fbo);
			GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.colorAttachment, 0);
			if (this.useDepthAttachment) {
				GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthAttachment, 0);
			}

			this.checkFramebufferStatus();
			this.clear(getError);
			this.endRead();
		} else {
			throw new IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + i + ")");
		}
	}

	public void setTexFilter(int i) {
		RenderSystem.assertOnRenderThreadOrInit();
		this.texFilter = i;
		GlStateManager._bindTexture(this.colorAttachment);
		GlStateManager._texParameter(3553, 10241, i);
		GlStateManager._texParameter(3553, 10240, i);
		GlStateManager._bindTexture(0);
	}

	public void checkFramebufferStatus() {
		RenderSystem.assertOnRenderThreadOrInit();
		int i = GlStateManager.glCheckFramebufferStatus(36160);
		if (i != 36053) {
			if (i == 36054) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			} else if (i == 36055) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			} else if (i == 36059) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
			} else if (i == 36060) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
			} else if (i == 36061) {
				throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");
			} else if (i == 1285) {
				throw new RuntimeException("GL_OUT_OF_MEMORY");
			} else {
				throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
			}
		}
	}

	public void beginRead() {
		RenderSystem.assertOnRenderThread();
		GlStateManager._bindTexture(this.colorAttachment);
	}

	public void endRead() {
		RenderSystem.assertOnRenderThreadOrInit();
		GlStateManager._bindTexture(0);
	}

	public void beginWrite(boolean setViewport) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.bind(setViewport));
		} else {
			this.bind(setViewport);
		}
	}

	private void bind(boolean updateViewport) {
		RenderSystem.assertOnRenderThreadOrInit();
		GlStateManager._glBindFramebuffer(36160, this.fbo);
		if (updateViewport) {
			GlStateManager._viewport(0, 0, this.viewportWidth, this.viewportHeight);
		}
	}

	public void endWrite() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> GlStateManager._glBindFramebuffer(36160, 0));
		} else {
			GlStateManager._glBindFramebuffer(36160, 0);
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

	public void draw(int width, int height, boolean disableBlend) {
		RenderSystem.assertOnGameThreadOrInit();
		if (!RenderSystem.isInInitPhase()) {
			RenderSystem.recordRenderCall(() -> this.drawInternal(width, height, disableBlend));
		} else {
			this.drawInternal(width, height, disableBlend);
		}
	}

	private void drawInternal(int width, int height, boolean disableBlend) {
		RenderSystem.assertOnRenderThread();
		GlStateManager._colorMask(true, true, true, false);
		GlStateManager._disableDepthTest();
		GlStateManager._depthMask(false);
		GlStateManager._viewport(0, 0, width, height);
		if (disableBlend) {
			GlStateManager._disableBlend();
		}

		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		Shader shader = minecraftClient.gameRenderer.blitScreenShader;
		shader.addSampler("DiffuseSampler", this.colorAttachment);
		Matrix4f matrix4f = Matrix4f.projectionMatrix((float)width, (float)(-height), 1000.0F, 3000.0F);
		RenderSystem.setProjectionMatrix(matrix4f);
		if (shader.modelViewMat != null) {
			shader.modelViewMat.set(Matrix4f.translate(0.0F, 0.0F, -2000.0F));
		}

		if (shader.projectionMat != null) {
			shader.projectionMat.set(matrix4f);
		}

		shader.bind();
		float f = (float)width;
		float g = (float)height;
		float h = (float)this.viewportWidth / (float)this.textureWidth;
		float i = (float)this.viewportHeight / (float)this.textureHeight;
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)g, 0.0).texture(0.0F, 0.0F).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)f, (double)g, 0.0).texture(h, 0.0F).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)f, 0.0, 0.0).texture(h, i).color(255, 255, 255, 255).next();
		bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0F, i).color(255, 255, 255, 255).next();
		bufferBuilder.end();
		BufferRenderer.method_43437(bufferBuilder);
		shader.unbind();
		GlStateManager._depthMask(true);
		GlStateManager._colorMask(true, true, true, true);
	}

	public void clear(boolean getError) {
		RenderSystem.assertOnRenderThreadOrInit();
		this.beginWrite(true);
		GlStateManager._clearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
		int i = 16384;
		if (this.useDepthAttachment) {
			GlStateManager._clearDepth(1.0);
			i |= 256;
		}

		GlStateManager._clear(i, getError);
		this.endWrite();
	}

	public int getColorAttachment() {
		return this.colorAttachment;
	}

	public int getDepthAttachment() {
		return this.depthAttachment;
	}
}
