package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class VertexBuffer implements AutoCloseable {
	private int id;
	private int field_27366;
	private VertexFormat.IntType field_27367;
	private int vertexCount;
	private VertexFormat.DrawMode field_27368;
	private boolean field_27369;

	public VertexBuffer() {
		RenderSystem.glGenBuffers(integer -> this.id = integer);
		RenderSystem.glGenBuffers(integer -> this.field_27366 = integer);
	}

	public void bind() {
		RenderSystem.glBindBuffer(34962, () -> this.id);
		if (this.field_27369) {
			RenderSystem.glBindBuffer(34963, () -> {
				RenderSystem.class_5590 lv = RenderSystem.getSequentialBuffer(this.field_27368, this.vertexCount);
				this.field_27367 = lv.method_31924();
				return lv.method_31919();
			});
		} else {
			RenderSystem.glBindBuffer(34963, () -> this.field_27366);
		}
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
		if (this.id != -1) {
			BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
			ByteBuffer byteBuffer = pair.getSecond();
			int i = drawArrayParameters.method_31957();
			this.vertexCount = drawArrayParameters.method_31955();
			this.field_27367 = drawArrayParameters.method_31956();
			this.field_27368 = drawArrayParameters.getMode();
			this.field_27369 = drawArrayParameters.method_31960();
			this.bind();
			if (!drawArrayParameters.method_31959()) {
				byteBuffer.limit(i);
				RenderSystem.glBufferData(34962, byteBuffer, 35044);
				byteBuffer.position(i);
			}

			if (!this.field_27369) {
				byteBuffer.limit(drawArrayParameters.method_31958());
				RenderSystem.glBufferData(34963, byteBuffer, 35044);
				byteBuffer.position(0);
			} else {
				byteBuffer.limit(drawArrayParameters.method_31958());
				byteBuffer.position(0);
			}

			unbind();
		}
	}

	public void draw(Matrix4f matrix) {
		if (this.vertexCount != 0) {
			RenderSystem.pushMatrix();
			RenderSystem.loadIdentity();
			RenderSystem.multMatrix(matrix);
			RenderSystem.drawElements(this.field_27368.mode, this.vertexCount, this.field_27367.field_27374);
			RenderSystem.popMatrix();
		}
	}

	public static void unbind() {
		RenderSystem.glBindBuffer(34962, () -> 0);
		RenderSystem.glBindBuffer(34963, () -> 0);
	}

	public void close() {
		if (this.id >= 0) {
			RenderSystem.glDeleteBuffers(this.id);
			this.id = -1;
		}

		if (this.field_27366 >= 0) {
			RenderSystem.glDeleteBuffers(this.field_27366);
			this.field_27366 = -1;
		}
	}
}
