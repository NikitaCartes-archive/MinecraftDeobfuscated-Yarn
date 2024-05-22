package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.BufferAllocator;
import org.joml.Matrix4f;

/**
 * Stores vertex data on GPU.
 * 
 * <p>If you don't need to change the geometry, you can upload data once
 * and reuse it every frame. For example, {@linkplain
 * net.minecraft.client.render.WorldRenderer#renderStars star rendering}
 * uses this technique to save bandwidth.
 * 
 * @implNote This is mostly a wrapper around vertex buffer object (VBO),
 * element buffer object (EBO), and vertex array object (VAO).
 */
@Environment(EnvType.CLIENT)
public class VertexBuffer implements AutoCloseable {
	private final VertexBuffer.Usage usage;
	private int vertexBufferId;
	private int indexBufferId;
	private int vertexArrayId;
	@Nullable
	private VertexFormat vertexFormat;
	@Nullable
	private RenderSystem.ShapeIndexBuffer sharedSequentialIndexBuffer;
	private VertexFormat.IndexType indexType;
	private int indexCount;
	private VertexFormat.DrawMode drawMode;

	public VertexBuffer(VertexBuffer.Usage usage) {
		this.usage = usage;
		RenderSystem.assertOnRenderThread();
		this.vertexBufferId = GlStateManager._glGenBuffers();
		this.indexBufferId = GlStateManager._glGenBuffers();
		this.vertexArrayId = GlStateManager._glGenVertexArrays();
	}

	/**
	 * Uploads the contents of {@code buffer} to GPU, discarding previously
	 * uploaded data.
	 * 
	 * <p>The caller of this method must {@linkplain #bind bind} this vertex
	 * buffer before calling this method.
	 */
	public void upload(BuiltBuffer data) {
		BuiltBuffer var2 = data;

		label40: {
			try {
				if (this.isClosed()) {
					break label40;
				}

				RenderSystem.assertOnRenderThread();
				BuiltBuffer.DrawParameters drawParameters = data.getDrawParameters();
				this.vertexFormat = this.uploadVertexBuffer(drawParameters, data.getBuffer());
				this.sharedSequentialIndexBuffer = this.uploadIndexBuffer(drawParameters, data.getSortedBuffer());
				this.indexCount = drawParameters.indexCount();
				this.indexType = drawParameters.indexType();
				this.drawMode = drawParameters.mode();
			} catch (Throwable var6) {
				if (data != null) {
					try {
						var2.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (data != null) {
				data.close();
			}

			return;
		}

		if (data != null) {
			data.close();
		}
	}

	public void uploadIndexBuffer(BufferAllocator.CloseableBuffer indexBuffer) {
		BufferAllocator.CloseableBuffer var2 = indexBuffer;

		label40: {
			try {
				if (this.isClosed()) {
					break label40;
				}

				RenderSystem.assertOnRenderThread();
				GlStateManager._glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
				RenderSystem.glBufferData(GlConst.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBuffer(), this.usage.id);
				this.sharedSequentialIndexBuffer = null;
			} catch (Throwable var6) {
				if (indexBuffer != null) {
					try {
						var2.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (indexBuffer != null) {
				indexBuffer.close();
			}

			return;
		}

		if (indexBuffer != null) {
			indexBuffer.close();
		}
	}

	private VertexFormat uploadVertexBuffer(BuiltBuffer.DrawParameters parameters, @Nullable ByteBuffer vertexBuffer) {
		boolean bl = false;
		if (!parameters.format().equals(this.vertexFormat)) {
			if (this.vertexFormat != null) {
				this.vertexFormat.clearState();
			}

			GlStateManager._glBindBuffer(GlConst.GL_ARRAY_BUFFER, this.vertexBufferId);
			parameters.format().setupState();
			bl = true;
		}

		if (vertexBuffer != null) {
			if (!bl) {
				GlStateManager._glBindBuffer(GlConst.GL_ARRAY_BUFFER, this.vertexBufferId);
			}

			RenderSystem.glBufferData(GlConst.GL_ARRAY_BUFFER, vertexBuffer, this.usage.id);
		}

		return parameters.format();
	}

	@Nullable
	private RenderSystem.ShapeIndexBuffer uploadIndexBuffer(BuiltBuffer.DrawParameters parameters, @Nullable ByteBuffer indexBuffer) {
		if (indexBuffer != null) {
			GlStateManager._glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
			RenderSystem.glBufferData(GlConst.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, this.usage.id);
			return null;
		} else {
			RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(parameters.mode());
			if (shapeIndexBuffer != this.sharedSequentialIndexBuffer || !shapeIndexBuffer.isLargeEnough(parameters.indexCount())) {
				shapeIndexBuffer.bindAndGrow(parameters.indexCount());
			}

			return shapeIndexBuffer;
		}
	}

	/**
	 * Sets this vertex buffer as the current one.
	 * 
	 * <p>This method must be called before uploading or drawing data.
	 */
	public void bind() {
		BufferRenderer.resetCurrentVertexBuffer();
		GlStateManager._glBindVertexArray(this.vertexArrayId);
	}

	public static void unbind() {
		BufferRenderer.resetCurrentVertexBuffer();
		GlStateManager._glBindVertexArray(0);
	}

	/**
	 * Draws the contents in this vertex buffer.
	 * 
	 * <p>The caller of this method must {@linkplain #bind bind} this vertex
	 * buffer before calling this method.
	 * 
	 * <p>Unlike {@link #draw(Matrix4f, Matrix4f, ShaderProgram)}, the caller
	 * of this method must manually bind a shader program before calling this
	 * method.
	 */
	public void draw() {
		RenderSystem.drawElements(this.drawMode.glMode, this.indexCount, this.getIndexType().glType);
	}

	private VertexFormat.IndexType getIndexType() {
		RenderSystem.ShapeIndexBuffer shapeIndexBuffer = this.sharedSequentialIndexBuffer;
		return shapeIndexBuffer != null ? shapeIndexBuffer.getIndexType() : this.indexType;
	}

	/**
	 * Draws the contents in this vertex buffer with {@code program}.
	 * 
	 * <p>The caller of this method must {@linkplain #bind bind} this vertex
	 * buffer before calling this method.
	 */
	public void draw(Matrix4f viewMatrix, Matrix4f projectionMatrix, ShaderProgram program) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.drawInternal(new Matrix4f(viewMatrix), new Matrix4f(projectionMatrix), program));
		} else {
			this.drawInternal(viewMatrix, projectionMatrix, program);
		}
	}

	private void drawInternal(Matrix4f viewMatrix, Matrix4f projectionMatrix, ShaderProgram shader) {
		shader.initializeUniforms(this.drawMode, viewMatrix, projectionMatrix, MinecraftClient.getInstance().getWindow());
		shader.bind();
		this.draw();
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

	public boolean isClosed() {
		return this.vertexArrayId == -1;
	}

	@Environment(EnvType.CLIENT)
	public static enum Usage {
		STATIC(35044),
		DYNAMIC(35048);

		final int id;

		private Usage(final int id) {
			this.id = id;
		}
	}
}
