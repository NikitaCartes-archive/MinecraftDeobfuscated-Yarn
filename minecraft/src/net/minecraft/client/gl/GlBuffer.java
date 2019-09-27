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
public class GlBuffer {
	private int id;
	private final VertexFormat format;
	private int vertexCount;

	public GlBuffer(VertexFormat vertexFormat) {
		this.format = vertexFormat;
		RenderSystem.glGenBuffers(integer -> this.id = integer);
	}

	public void bind() {
		RenderSystem.glBindBuffer(34962, () -> this.id);
	}

	public void set(BufferBuilder bufferBuilder) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.method_22644(bufferBuilder));
		} else {
			this.method_22644(bufferBuilder);
		}
	}

	public CompletableFuture<Void> method_22643(BufferBuilder bufferBuilder) {
		if (!RenderSystem.isOnRenderThread()) {
			return CompletableFuture.runAsync(() -> this.method_22644(bufferBuilder), runnable -> RenderSystem.recordRenderCall(runnable::run));
		} else {
			this.method_22644(bufferBuilder);
			return CompletableFuture.completedFuture(null);
		}
	}

	private void method_22644(BufferBuilder bufferBuilder) {
		Pair<BufferBuilder.class_4574, ByteBuffer> pair = bufferBuilder.method_22632();
		ByteBuffer byteBuffer = pair.getSecond();
		this.vertexCount = byteBuffer.remaining() / this.format.getVertexSize();
		this.bind();
		RenderSystem.glBufferData(34962, byteBuffer, 35044);
		unbind();
	}

	public void draw(Matrix4f matrix4f, int i) {
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		RenderSystem.multMatrix(matrix4f);
		RenderSystem.drawArrays(i, 0, this.vertexCount);
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
