package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
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
	private int vertexArrayId;
	@Nullable
	private VertexFormat vertexFormat;
	@Nullable
	private RenderSystem.IndexBuffer field_38983;
	private VertexFormat.IntType elementFormat;
	private int vertexCount;
	private VertexFormat.DrawMode drawMode;

	public VertexBuffer() {
		RenderSystem.assertOnRenderThread();
		this.vertexBufferId = GlStateManager._glGenBuffers();
		this.indexBufferId = GlStateManager._glGenBuffers();
		this.vertexArrayId = GlStateManager._glGenVertexArrays();
	}

	public CompletableFuture<Void> submitUpload(BufferBuilder buffer) {
		if (!RenderSystem.isOnRenderThread()) {
			return CompletableFuture.runAsync(() -> this.uploadInternal(buffer), action -> RenderSystem.recordRenderCall(action::run));
		} else {
			this.uploadInternal(buffer);
			return CompletableFuture.completedFuture(null);
		}
	}

	private void uploadInternal(BufferBuilder bufferBuilder) {
		if (!this.method_43444()) {
			this.bind();
			this.upload(bufferBuilder);
			unbind();
		}
	}

	public void upload(BufferBuilder buffer) {
		RenderSystem.assertOnRenderThread();
		Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = buffer.popData();
		this.method_43441(pair.getFirst(), pair.getSecond());
	}

	public void method_43441(BufferBuilder.DrawArrayParameters drawArrayParameters, ByteBuffer byteBuffer) {
		if (!this.method_43444()) {
			this.vertexFormat = this.method_43442(drawArrayParameters, byteBuffer);
			this.field_38983 = this.method_43443(drawArrayParameters, byteBuffer);
			byteBuffer.limit(drawArrayParameters.getIndexBufferEnd());
			byteBuffer.position(0);
			this.vertexCount = drawArrayParameters.getVertexCount();
			this.elementFormat = drawArrayParameters.getElementFormat();
			this.drawMode = drawArrayParameters.getMode();
		}
	}

	private VertexFormat method_43442(BufferBuilder.DrawArrayParameters drawArrayParameters, ByteBuffer byteBuffer) {
		boolean bl = false;
		if (!drawArrayParameters.getVertexFormat().equals(this.vertexFormat)) {
			if (this.vertexFormat != null) {
				this.vertexFormat.endDrawing();
			}

			GlStateManager._glBindBuffer(34962, this.vertexBufferId);
			drawArrayParameters.getVertexFormat().startDrawing();
			bl = true;
		}

		if (!drawArrayParameters.hasNoVertexBuffer()) {
			if (!bl) {
				GlStateManager._glBindBuffer(34962, this.vertexBufferId);
			}

			byteBuffer.position(drawArrayParameters.method_43429());
			byteBuffer.limit(drawArrayParameters.method_43430());
			RenderSystem.glBufferData(34962, byteBuffer, 35044);
		}

		return drawArrayParameters.getVertexFormat();
	}

	@Nullable
	private RenderSystem.IndexBuffer method_43443(BufferBuilder.DrawArrayParameters drawArrayParameters, ByteBuffer byteBuffer) {
		if (!drawArrayParameters.hasNoIndexBuffer()) {
			GlStateManager._glBindBuffer(34963, this.indexBufferId);
			byteBuffer.position(drawArrayParameters.method_43431());
			byteBuffer.limit(drawArrayParameters.method_43432());
			RenderSystem.glBufferData(34963, byteBuffer, 35044);
			return null;
		} else {
			RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(drawArrayParameters.getMode());
			if (indexBuffer != this.field_38983 || !indexBuffer.method_43409(drawArrayParameters.getVertexCount())) {
				indexBuffer.method_43410(drawArrayParameters.getVertexCount());
			}

			return indexBuffer;
		}
	}

	public void bind() {
		BufferRenderer.method_43436();
		GlStateManager._glBindVertexArray(this.vertexArrayId);
	}

	public static void unbind() {
		BufferRenderer.method_43436();
		GlStateManager._glBindVertexArray(0);
	}

	public void drawElements() {
		RenderSystem.drawElements(this.drawMode.mode, this.vertexCount, this.method_43445().type);
	}

	private VertexFormat.IntType method_43445() {
		RenderSystem.IndexBuffer indexBuffer = this.field_38983;
		return indexBuffer != null ? indexBuffer.getElementFormat() : this.elementFormat;
	}

	public void setShader(Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.innerSetShader(viewMatrix.copy(), projectionMatrix.copy(), shader));
		} else {
			this.innerSetShader(viewMatrix, projectionMatrix, shader);
		}
	}

	private void innerSetShader(Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
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

		if (shader.viewRotationMat != null) {
			shader.viewRotationMat.method_39978(RenderSystem.getInverseViewRotationMatrix());
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

		if (shader.fogShape != null) {
			shader.fogShape.set(RenderSystem.getShaderFogShape().getId());
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
		shader.bind();
		this.drawElements();
		shader.unbind();
	}

	public void close() {
		if (this.vertexBufferId >= 0) {
			RenderSystem.glDeleteBuffers(this.vertexBufferId);
			this.vertexBufferId = -1;
		}

		if (this.indexBufferId >= 0) {
			RenderSystem.glDeleteBuffers(this.indexBufferId);
			this.indexBufferId = -1;
		}

		if (this.vertexArrayId >= 0) {
			RenderSystem.glDeleteVertexArrays(this.vertexArrayId);
			this.vertexArrayId = -1;
		}
	}

	public VertexFormat getVertexFormat() {
		return this.vertexFormat;
	}

	public boolean method_43444() {
		return this.vertexArrayId == -1;
	}
}
