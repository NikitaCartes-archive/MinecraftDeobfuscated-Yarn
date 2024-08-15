package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;

/**
 * Containing methods for immediately drawing a buffer built with {@link
 * BufferBuilder}.
 */
@Environment(EnvType.CLIENT)
public class BufferRenderer {
	@Nullable
	private static VertexBuffer currentVertexBuffer;

	public static void reset() {
		if (currentVertexBuffer != null) {
			resetCurrentVertexBuffer();
			VertexBuffer.unbind();
		}
	}

	public static void resetCurrentVertexBuffer() {
		currentVertexBuffer = null;
	}

	/**
	 * Draws {@code buffer} using the shader program specified with {@link
	 * com.mojang.blaze3d.systems.RenderSystem#setShader
	 * RenderSystem#setShader}
	 */
	public static void drawWithGlobalProgram(BuiltBuffer buffer) {
		RenderSystem.assertOnRenderThread();
		VertexBuffer vertexBuffer = upload(buffer);
		vertexBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
	}

	/**
	 * Draws {@code buffer}.
	 * 
	 * <p>Unlike {@link #drawWithGlobalProgram}, the shader program cannot be
	 * specified with {@link com.mojang.blaze3d.systems.RenderSystem#setShader
	 * RenderSystem#setShader}. The caller of this method must manually bind a
	 * shader program before calling this method.
	 */
	public static void draw(BuiltBuffer buffer) {
		RenderSystem.assertOnRenderThread();
		VertexBuffer vertexBuffer = upload(buffer);
		vertexBuffer.draw();
	}

	private static VertexBuffer upload(BuiltBuffer buffer) {
		VertexBuffer vertexBuffer = bind(buffer.getDrawParameters().format());
		vertexBuffer.upload(buffer);
		return vertexBuffer;
	}

	private static VertexBuffer bind(VertexFormat vertexFormat) {
		VertexBuffer vertexBuffer = vertexFormat.getBuffer();
		bind(vertexBuffer);
		return vertexBuffer;
	}

	private static void bind(VertexBuffer vertexBuffer) {
		if (vertexBuffer != currentVertexBuffer) {
			vertexBuffer.bind();
			currentVertexBuffer = vertexBuffer;
		}
	}
}
