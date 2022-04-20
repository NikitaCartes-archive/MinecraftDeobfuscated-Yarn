package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
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

	public static void drawWithShader(BufferBuilder builder) {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> drawWithShaderInternal(builder));
		} else {
			drawWithShaderInternal(builder);
		}
	}

	private static void drawWithShaderInternal(BufferBuilder builder) {
		VertexBuffer vertexBuffer = getVertexBuffer(builder);
		if (vertexBuffer != null) {
			vertexBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
		}
	}

	public static void drawWithoutShader(BufferBuilder builder) {
		VertexBuffer vertexBuffer = getVertexBuffer(builder);
		if (vertexBuffer != null) {
			vertexBuffer.drawElements();
		}
	}

	@Nullable
	private static VertexBuffer getVertexBuffer(BufferBuilder builder) {
		RenderSystem.assertOnRenderThread();
		Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = builder.popData();
		BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
		ByteBuffer byteBuffer = pair.getSecond();
		byteBuffer.clear();
		if (drawArrayParameters.getCount() <= 0) {
			return null;
		} else {
			VertexBuffer vertexBuffer = bindAndSet(drawArrayParameters.getVertexFormat());
			vertexBuffer.setFromParameters(drawArrayParameters, byteBuffer);
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
