package net.minecraft.client.util.tracy;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;

@Environment(EnvType.CLIENT)
public class TracyFrameCapturer implements AutoCloseable {
	private static final int MAX_WIDTH = 320;
	private static final int MAX_HEIGHT = 180;
	private static final int field_54254 = 4;
	private int framebufferWidth;
	private int framebufferHeight;
	private int width;
	private int height;
	private final Framebuffer framebuffer = new SimpleFramebuffer(320, 180, false);
	private final int buffer = GlStateManager._glGenBuffers();
	private long syncHandle;
	private boolean canUpload;
	private int offset;
	private boolean captured;

	private void resize(int framebufferWidth, int framebufferHeight) {
		float f = (float)framebufferWidth / (float)framebufferHeight;
		if (framebufferWidth > 320) {
			framebufferWidth = 320;
			framebufferHeight = (int)(320.0F / f);
		}

		if (framebufferHeight > 180) {
			framebufferWidth = (int)(180.0F * f);
			framebufferHeight = 180;
		}

		framebufferWidth = framebufferWidth / 4 * 4;
		framebufferHeight = framebufferHeight / 4 * 4;
		if (this.width != framebufferWidth || this.height != framebufferHeight) {
			this.width = framebufferWidth;
			this.height = framebufferHeight;
			this.framebuffer.resize(framebufferWidth, framebufferHeight);
			GlStateManager._glBindBuffer(35051, this.buffer);
			GlStateManager._glBufferData(35051, (long)framebufferWidth * (long)framebufferHeight * 4L, 35041);
			GlStateManager._glBindBuffer(35051, 0);
			this.canUpload = false;
		}
	}

	public void capture(Framebuffer framebuffer) {
		if (!this.canUpload && !this.captured) {
			this.captured = true;
			if (framebuffer.textureWidth != this.framebufferWidth || framebuffer.textureHeight != this.framebufferHeight) {
				this.framebufferWidth = framebuffer.textureWidth;
				this.framebufferHeight = framebuffer.textureHeight;
				this.resize(this.framebufferWidth, this.framebufferHeight);
			}

			GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, this.framebuffer.fbo);
			GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, framebuffer.fbo);
			GlStateManager._glBlitFrameBuffer(0, 0, framebuffer.textureWidth, framebuffer.textureHeight, 0, 0, this.width, this.height, 16384, GlConst.GL_LINEAR);
			GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, 0);
			GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, 0);
			GlStateManager._glBindBuffer(35051, this.buffer);
			GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, this.framebuffer.fbo);
			GlStateManager._readPixels(0, 0, this.width, this.height, GlConst.GL_RGBA, GlConst.GL_UNSIGNED_BYTE, 0L);
			GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, 0);
			GlStateManager._glBindBuffer(35051, 0);
			this.syncHandle = GlStateManager._glFenceSync(37143, 0);
			this.canUpload = true;
			this.offset = 0;
		}
	}

	public void upload() {
		if (this.canUpload) {
			if (GlStateManager._glClientWaitSync(this.syncHandle, 0, 0) != 37147) {
				GlStateManager._glDeleteSync(this.syncHandle);
				GlStateManager._glBindBuffer(35051, this.buffer);
				ByteBuffer byteBuffer = GlStateManager.mapBuffer(35051, 35000);
				if (byteBuffer != null) {
					TracyClient.frameImage(byteBuffer, this.width, this.height, this.offset, true);
				}

				GlStateManager._glUnmapBuffer(35051);
				GlStateManager._glBindBuffer(35051, 0);
				this.canUpload = false;
			}
		}
	}

	public void markFrame() {
		this.offset++;
		this.captured = false;
		TracyClient.markFrame();
	}

	public void close() {
		if (this.canUpload) {
			GlStateManager._glDeleteSync(this.syncHandle);
			this.canUpload = false;
		}

		GlStateManager._glDeleteBuffers(this.buffer);
		this.framebuffer.delete();
	}
}
