package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

@Environment(EnvType.CLIENT)
public class BufferRenderer {
	private static int currentVertexArray;
	private static int currentVertexBuffer;
	private static int currentElementBuffer;
	@Nullable
	private static VertexFormat vertexFormat;

	public static void unbindAll() {
		if (vertexFormat != null) {
			vertexFormat.endDrawing();
			vertexFormat = null;
		}

		GlStateManager._glBindBuffer(34963, 0);
		currentElementBuffer = 0;
		GlStateManager._glBindBuffer(34962, 0);
		currentVertexBuffer = 0;
		GlStateManager._glBindVertexArray(0);
		currentVertexArray = 0;
	}

	public static void unbindElementBuffer() {
		GlStateManager._glBindBuffer(34963, 0);
		currentElementBuffer = 0;
	}

	public static void draw(BufferBuilder bufferBuilder) {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(
				() -> {
					Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pairx = bufferBuilder.popData();
					BufferBuilder.DrawArrayParameters drawArrayParametersx = pairx.getFirst();
					draw(
						pairx.getSecond(),
						drawArrayParametersx.getMode(),
						drawArrayParametersx.getVertexFormat(),
						drawArrayParametersx.getCount(),
						drawArrayParametersx.getElementFormat(),
						drawArrayParametersx.getVertexCount(),
						drawArrayParametersx.isTextured()
					);
				}
			);
		} else {
			Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = bufferBuilder.popData();
			BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
			draw(
				pair.getSecond(),
				drawArrayParameters.getMode(),
				drawArrayParameters.getVertexFormat(),
				drawArrayParameters.getCount(),
				drawArrayParameters.getElementFormat(),
				drawArrayParameters.getVertexCount(),
				drawArrayParameters.isTextured()
			);
		}
	}

	private static void draw(
		ByteBuffer buffer,
		VertexFormat.DrawMode drawMode,
		VertexFormat vertexFormat,
		int count,
		VertexFormat.IntType elementFormat,
		int vertexCount,
		boolean textured
	) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		buffer.clear();
		if (count > 0) {
			int i = count * vertexFormat.getVertexSize();
			bind(vertexFormat);
			buffer.position(0);
			buffer.limit(i);
			GlStateManager._glBufferData(34962, buffer, 35048);
			int k;
			if (textured) {
				RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(drawMode, vertexCount);
				int j = indexBuffer.getId();
				if (j != currentElementBuffer) {
					GlStateManager._glBindBuffer(34963, j);
					currentElementBuffer = j;
				}

				k = indexBuffer.getElementFormat().count;
			} else {
				int l = vertexFormat.getElementBuffer();
				if (l != currentElementBuffer) {
					GlStateManager._glBindBuffer(34963, l);
					currentElementBuffer = l;
				}

				buffer.position(i);
				buffer.limit(i + vertexCount * elementFormat.size);
				GlStateManager._glBufferData(34963, buffer, 35048);
				k = elementFormat.count;
			}

			Shader shader = RenderSystem.getShader();

			for (int j = 0; j < 8; j++) {
				int m = RenderSystem.getShaderTexture(j);
				shader.addSampler("Sampler" + j, m);
			}

			if (shader.modelViewMat != null) {
				shader.modelViewMat.set(RenderSystem.getModelViewMatrix());
			}

			if (shader.projectionMat != null) {
				shader.projectionMat.set(RenderSystem.getProjectionMatrix());
			}

			if (shader.colorModulator != null) {
				shader.colorModulator.set(RenderSystem.getShaderColor());
			}

			if (shader.fogStart != null) {
				shader.fogStart.set(RenderSystem.getShaderFogStart());
			}

			if (shader.fogEnd != null) {
				shader.fogEnd.set(RenderSystem.getShaderFogEnd());
			}

			if (shader.fogColor != null) {
				shader.fogColor.set(RenderSystem.getShaderFogColor());
			}

			if (shader.textureMat != null) {
				shader.textureMat.set(RenderSystem.getTextureMatrix());
			}

			if (shader.gameTime != null) {
				shader.gameTime.set(RenderSystem.getShaderGameTime());
			}

			if (shader.screenSize != null) {
				Window window = MinecraftClient.getInstance().getWindow();
				shader.screenSize.set((float)window.getFramebufferWidth(), (float)window.getFramebufferHeight());
			}

			if (shader.lineWidth != null && (drawMode == VertexFormat.DrawMode.LINES || drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
				shader.lineWidth.set(RenderSystem.getShaderLineWidth());
			}

			RenderSystem.setupShaderLights(shader);
			shader.upload();
			GlStateManager._drawElements(drawMode.mode, vertexCount, k, 0L);
			shader.bind();
			buffer.position(0);
		}
	}

	/**
	 * Similar to a regular draw, however this method will skip rendering shaders.
	 */
	public static void postDraw(BufferBuilder builder) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = builder.popData();
		BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
		ByteBuffer byteBuffer = pair.getSecond();
		VertexFormat vertexFormat = drawArrayParameters.getVertexFormat();
		int i = drawArrayParameters.getCount();
		byteBuffer.clear();
		if (i > 0) {
			int j = i * vertexFormat.getVertexSize();
			bind(vertexFormat);
			byteBuffer.position(0);
			byteBuffer.limit(j);
			GlStateManager._glBufferData(34962, byteBuffer, 35048);
			RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(drawArrayParameters.getMode(), drawArrayParameters.getVertexCount());
			int k = indexBuffer.getId();
			if (k != currentElementBuffer) {
				GlStateManager._glBindBuffer(34963, k);
				currentElementBuffer = k;
			}

			int l = indexBuffer.getElementFormat().count;
			GlStateManager._drawElements(drawArrayParameters.getMode().mode, drawArrayParameters.getVertexCount(), l, 0L);
			byteBuffer.position(0);
		}
	}

	private static void bind(VertexFormat vertexFormat) {
		int i = vertexFormat.getVertexArray();
		int j = vertexFormat.getVertexBuffer();
		boolean bl = vertexFormat != BufferRenderer.vertexFormat;
		if (bl) {
			unbindAll();
		}

		if (i != currentVertexArray) {
			GlStateManager._glBindVertexArray(i);
			currentVertexArray = i;
		}

		if (j != currentVertexBuffer) {
			GlStateManager._glBindBuffer(34962, j);
			currentVertexBuffer = j;
		}

		if (bl) {
			vertexFormat.startDrawing();
			BufferRenderer.vertexFormat = vertexFormat;
		}
	}
}
