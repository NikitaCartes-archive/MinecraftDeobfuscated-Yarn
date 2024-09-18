package net.minecraft.client.util.tracy;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.jtracy.TracyClient;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBufferTarget;
import net.minecraft.client.gl.GlFenceSync;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.GpuBuffer;
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
	private final GpuBuffer buffer = new GpuBuffer(GlBufferTarget.PIXEL_PACK, GlUsage.STREAM_READ, 0);
	@Nullable
	private GlFenceSync fenceSync;
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
			this.buffer.resize(framebufferWidth * framebufferHeight * 4);
			if (this.fenceSync != null) {
				this.fenceSync.close();
				this.fenceSync = null;
			}
		}
	}

	public void capture(Framebuffer framebuffer) {
		if (this.fenceSync == null && !this.captured) {
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
			this.buffer.bind();
			GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, this.framebuffer.fbo);
			GlStateManager._readPixels(0, 0, this.width, this.height, GlConst.GL_RGBA, GlConst.GL_UNSIGNED_BYTE, 0L);
			GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, 0);
			this.fenceSync = new GlFenceSync();
			this.offset = 0;
		}
	}

	public void upload() {
		if (this.fenceSync != null) {
			if (this.fenceSync.wait(0L)) {
				this.fenceSync = null;

				try (GpuBuffer.ReadResult readResult = this.buffer.read()) {
					if (readResult != null) {
						TracyClient.frameImage(readResult.getBuf(), this.width, this.height, this.offset, true);
					}
				}
			}
		}
	}

	public void markFrame() {
		this.offset++;
		this.captured = false;
		TracyClient.markFrame();
	}

	public void close() {
		if (this.fenceSync != null) {
			this.fenceSync.close();
			this.fenceSync = null;
		}

		this.buffer.close();
		this.framebuffer.delete();
	}
}
