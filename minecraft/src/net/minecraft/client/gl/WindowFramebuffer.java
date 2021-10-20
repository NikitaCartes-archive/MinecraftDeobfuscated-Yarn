package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WindowFramebuffer extends Framebuffer {
	public static final int DEFAULT_WIDTH = 854;
	public static final int DEFAULT_HEIGHT = 480;
	static final WindowFramebuffer.Size DEFAULT = new WindowFramebuffer.Size(854, 480);

	public WindowFramebuffer(int width, int height) {
		super(true);
		RenderSystem.assertOnRenderThreadOrInit();
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.initSize(width, height));
		} else {
			this.initSize(width, height);
		}
	}

	private void initSize(int width, int height) {
		RenderSystem.assertOnRenderThreadOrInit();
		WindowFramebuffer.Size size = this.findSuitableSize(width, height);
		this.fbo = GlStateManager.glGenFramebuffers();
		GlStateManager._glBindFramebuffer(36160, this.fbo);
		GlStateManager._bindTexture(this.colorAttachment);
		GlStateManager._texParameter(3553, 10241, 9728);
		GlStateManager._texParameter(3553, 10240, 9728);
		GlStateManager._texParameter(3553, 10242, 33071);
		GlStateManager._texParameter(3553, 10243, 33071);
		GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.colorAttachment, 0);
		GlStateManager._bindTexture(this.depthAttachment);
		GlStateManager._texParameter(3553, 34892, 0);
		GlStateManager._texParameter(3553, 10241, 9728);
		GlStateManager._texParameter(3553, 10240, 9728);
		GlStateManager._texParameter(3553, 10242, 33071);
		GlStateManager._texParameter(3553, 10243, 33071);
		GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthAttachment, 0);
		GlStateManager._bindTexture(0);
		this.viewportWidth = size.width;
		this.viewportHeight = size.height;
		this.textureWidth = size.width;
		this.textureHeight = size.height;
		this.checkFramebufferStatus();
		GlStateManager._glBindFramebuffer(36160, 0);
	}

	private WindowFramebuffer.Size findSuitableSize(int width, int height) {
		RenderSystem.assertOnRenderThreadOrInit();
		this.colorAttachment = TextureUtil.generateTextureId();
		this.depthAttachment = TextureUtil.generateTextureId();
		WindowFramebuffer.Attachment attachment = WindowFramebuffer.Attachment.NONE;

		for (WindowFramebuffer.Size size : WindowFramebuffer.Size.findCompatible(width, height)) {
			attachment = WindowFramebuffer.Attachment.NONE;
			if (this.supportColor(size)) {
				attachment = attachment.with(WindowFramebuffer.Attachment.COLOR);
			}

			if (this.supportsDepth(size)) {
				attachment = attachment.with(WindowFramebuffer.Attachment.DEPTH);
			}

			if (attachment == WindowFramebuffer.Attachment.COLOR_DEPTH) {
				return size;
			}
		}

		throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (allocated attachments = " + attachment.name() + ")");
	}

	private boolean supportColor(WindowFramebuffer.Size size) {
		RenderSystem.assertOnRenderThreadOrInit();
		GlStateManager._getError();
		GlStateManager._bindTexture(this.colorAttachment);
		GlStateManager._texImage2D(3553, 0, 32856, size.width, size.height, 0, 6408, 5121, null);
		return GlStateManager._getError() != 1285;
	}

	private boolean supportsDepth(WindowFramebuffer.Size size) {
		RenderSystem.assertOnRenderThreadOrInit();
		GlStateManager._getError();
		GlStateManager._bindTexture(this.depthAttachment);
		GlStateManager._texImage2D(3553, 0, 6402, size.width, size.height, 0, 6402, 5126, null);
		return GlStateManager._getError() != 1285;
	}

	@Environment(EnvType.CLIENT)
	static enum Attachment {
		NONE,
		COLOR,
		DEPTH,
		COLOR_DEPTH;

		private static final WindowFramebuffer.Attachment[] VALUES = values();

		WindowFramebuffer.Attachment with(WindowFramebuffer.Attachment other) {
			return VALUES[this.ordinal() | other.ordinal()];
		}
	}

	@Environment(EnvType.CLIENT)
	static class Size {
		public final int width;
		public final int height;

		Size(int width, int height) {
			this.width = width;
			this.height = height;
		}

		static List<WindowFramebuffer.Size> findCompatible(int width, int height) {
			RenderSystem.assertOnRenderThreadOrInit();
			int i = RenderSystem.maxSupportedTextureSize();
			return width > 0 && width <= i && height > 0 && height <= i
				? ImmutableList.of(new WindowFramebuffer.Size(width, height), WindowFramebuffer.DEFAULT)
				: ImmutableList.of(WindowFramebuffer.DEFAULT);
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				WindowFramebuffer.Size size = (WindowFramebuffer.Size)o;
				return this.width == size.width && this.height == size.height;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.width, this.height});
		}

		public String toString() {
			return this.width + "x" + this.height;
		}
	}
}
