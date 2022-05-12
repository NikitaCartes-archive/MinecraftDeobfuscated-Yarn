package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;

@Environment(EnvType.CLIENT)
public class BufferRenderer {
	@Nullable
	private static VertexBuffer currentVertexBuffer;

	public static void unbindAll() {
		if (currentVertexBuffer != null) {
			resetCurrentVertexBuffer();
			VertexBuffer.unbind();
		}
	}

	public static void resetCurrentVertexBuffer() {
		currentVertexBuffer = null;
	}

	public static void drawWithShader(BufferBuilder.BuiltBuffer buffer) {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> drawWithShaderInternal(buffer));
		} else {
			drawWithShaderInternal(buffer);
		}
	}

	private static void drawWithShaderInternal(BufferBuilder.BuiltBuffer buffer) {
		VertexBuffer vertexBuffer = getVertexBuffer(buffer);
		if (vertexBuffer != null) {
			vertexBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
		}
	}

	public static void drawWithoutShader(BufferBuilder.BuiltBuffer buffer) {
		VertexBuffer vertexBuffer = getVertexBuffer(buffer);
		if (vertexBuffer != null) {
			vertexBuffer.drawElements();
		}
	}

	@Nullable
	private static VertexBuffer getVertexBuffer(BufferBuilder.BuiltBuffer buffer) {
		RenderSystem.assertOnRenderThread();
		if (buffer.isEmpty()) {
			buffer.release();
			return null;
		} else {
			VertexBuffer vertexBuffer = bindAndSet(buffer.getParameters().format());
			vertexBuffer.upload(buffer);
			return vertexBuffer;
		}
	}

	private static VertexBuffer bindAndSet(VertexFormat vertexFormat) {
		VertexBuffer vertexBuffer = vertexFormat.getBuffer();
		bindAndSet(vertexBuffer);
		return vertexBuffer;
	}

	private static void bindAndSet(VertexBuffer vertexBuffer) {
		if (vertexBuffer != currentVertexBuffer) {
			vertexBuffer.bind();
			currentVertexBuffer = vertexBuffer;
		}
	}
}
