package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class VertexBuffer implements AutoCloseable {
	private int vertexBufferId;
	private int indexBufferId;
	private VertexFormat.IntType vertexFormat;
	private int vertexArrayId;
	private int vertexCount;
	private VertexFormat.DrawMode drawMode;
	private boolean usesTexture;
	private VertexFormat elementFormat;

	public VertexBuffer() {
		RenderSystem.glGenBuffers(id -> this.vertexBufferId = id);
		RenderSystem.glGenVertexArrays(id -> this.vertexArrayId = id);
		RenderSystem.glGenBuffers(id -> this.indexBufferId = id);
	}

	public void bind() {
		RenderSystem.glBindBuffer(34962, () -> this.vertexBufferId);
		if (this.usesTexture) {
			RenderSystem.glBindBuffer(34963, () -> {
				RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(this.drawMode, this.vertexCount);
				this.vertexFormat = indexBuffer.getElementFormat();
				return indexBuffer.getId();
			});
		} else {
			RenderSystem.glBindBuffer(34963, () -> this.indexBufferId);
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
			return CompletableFuture.runAsync(() -> this.uploadInternal(buffer), action -> RenderSystem.recordRenderCall(action::run));
		} else {
			this.uploadInternal(buffer);
			return CompletableFuture.completedFuture(null);
		}
	}

	private void uploadInternal(BufferBuilder buffer) {
		Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = buffer.popData();
		if (this.vertexBufferId != 0) {
			BufferRenderer.unbindAll();
			BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
			ByteBuffer byteBuffer = pair.getSecond();
			int i = drawArrayParameters.getLimit();
			this.vertexCount = drawArrayParameters.getVertexCount();
			this.vertexFormat = drawArrayParameters.getElementFormat();
			this.elementFormat = drawArrayParameters.getVertexFormat();
			this.drawMode = drawArrayParameters.getMode();
			this.usesTexture = drawArrayParameters.isTextured();
			this.bindVertexArray();
			this.bind();
			if (!drawArrayParameters.isCameraOffset()) {
				byteBuffer.limit(i);
				RenderSystem.glBufferData(34962, byteBuffer, 35044);
				byteBuffer.position(i);
			}

			if (!this.usesTexture) {
				byteBuffer.limit(drawArrayParameters.getDrawStart());
				RenderSystem.glBufferData(34963, byteBuffer, 35044);
				byteBuffer.position(0);
			} else {
				byteBuffer.limit(drawArrayParameters.getDrawStart());
				byteBuffer.position(0);
			}

			unbind();
			unbindVertexArray();
		}
	}

	private void bindVertexArray() {
		RenderSystem.glBindVertexArray(() -> this.vertexArrayId);
	}

	public static void unbindVertexArray() {
		RenderSystem.glBindVertexArray(() -> 0);
	}

	public void drawElements() {
		if (this.vertexCount != 0) {
			RenderSystem.drawElements(this.drawMode.mode, this.vertexCount, this.vertexFormat.count);
		}
	}

	public void setShader(Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.innerSetShader(viewMatrix.copy(), projectionMatrix.copy(), shader));
		} else {
			this.innerSetShader(viewMatrix, projectionMatrix, shader);
		}
	}

	public void innerSetShader(Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
		if (this.vertexCount != 0) {
			RenderSystem.assertThread(RenderSystem::isOnRenderThread);
			BufferRenderer.unbindAll();

			for (int i = 0; i < 12; i++) {
				int j = RenderSystem.getShaderTexture(i);
				shader.addSampler("Sampler" + i, j);
			}

			if (shader.modelViewMat != null) {
				shader.modelViewMat.set(viewMatrix);
			}

			if (shader.projectionMat != null) {
				shader.projectionMat.set(projectionMatrix);
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

			if (shader.lineWidth != null && (this.drawMode == VertexFormat.DrawMode.LINES || this.drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
				shader.lineWidth.set(RenderSystem.getShaderLineWidth());
			}

			RenderSystem.setupShaderLights(shader);
			this.bindVertexArray();
			this.bind();
			this.getElementFormat().startDrawing();
			shader.upload();
			RenderSystem.drawElements(this.drawMode.mode, this.vertexCount, this.vertexFormat.count);
			shader.bind();
			this.getElementFormat().endDrawing();
			unbind();
			unbindVertexArray();
		}
	}

	public void drawVertices() {
		if (this.vertexCount != 0) {
			RenderSystem.assertThread(RenderSystem::isOnRenderThread);
			this.bindVertexArray();
			this.bind();
			this.elementFormat.startDrawing();
			RenderSystem.drawElements(this.drawMode.mode, this.vertexCount, this.vertexFormat.count);
		}
	}

	public static void unbind() {
		RenderSystem.glBindBuffer(34962, () -> 0);
		RenderSystem.glBindBuffer(34963, () -> 0);
	}

	public void close() {
		if (this.indexBufferId >= 0) {
			RenderSystem.glDeleteBuffers(this.indexBufferId);
			this.indexBufferId = -1;
		}

		if (this.vertexBufferId > 0) {
			RenderSystem.glDeleteBuffers(this.vertexBufferId);
			this.vertexBufferId = 0;
		}

		if (this.vertexArrayId > 0) {
			RenderSystem.glDeleteVertexArrays(this.vertexArrayId);
			this.vertexArrayId = 0;
		}
	}

	public VertexFormat getElementFormat() {
		return this.elementFormat;
	}
}
