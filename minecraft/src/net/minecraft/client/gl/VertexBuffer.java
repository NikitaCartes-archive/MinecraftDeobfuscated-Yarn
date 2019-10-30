package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class VertexBuffer {
	private int id;
	private final VertexFormat format;
	private int vertexCount;

	public VertexBuffer(VertexFormat format) {
		this.format = format;
		RenderSystem.glGenBuffers(integer -> this.id = integer);
	}

	public void bind() {
		RenderSystem.glBindBuffer(34962, () -> this.id);
	}

	public void upload(BufferBuilder buffer) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.uploadInternal(buffer));
		} else {
			this.uploadInternal(buffer);
		}
	}

	public CompletableFuture<Void> submitUpload(BufferBuilder buffer) {
		if (!RenderSystem.isOnRenderThread()) {
			return CompletableFuture.runAsync(() -> this.uploadInternal(buffer), runnable -> RenderSystem.recordRenderCall(runnable::run));
		} else {
			this.uploadInternal(buffer);
			return CompletableFuture.completedFuture(null);
		}
	}

	private void uploadInternal(BufferBuilder buffer) {
		Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = buffer.popData();
		ByteBuffer byteBuffer = pair.getSecond();
		this.vertexCount = byteBuffer.remaining() / this.format.getVertexSize();
		this.bind();
		RenderSystem.glBufferData(34962, byteBuffer, 35044);
		unbind();
	}

	public void draw(Matrix4f matrix, int mode) {
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		RenderSystem.multMatrix(matrix);
		RenderSystem.drawArrays(mode, 0, this.vertexCount);
		RenderSystem.popMatrix();
	}

	public static void unbind() {
		RenderSystem.glBindBuffer(34962, () -> 0);
	}

	public void delete() {
		if (this.id >= 0) {
			RenderSystem.glDeleteBuffers(this.id);
			this.id = -1;
		}
	}
}
