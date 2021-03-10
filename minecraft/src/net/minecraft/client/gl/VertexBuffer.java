package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5944;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class VertexBuffer implements AutoCloseable {
	private int vertexBufferId;
	private int indexBufferId;
	private VertexFormat.IntType field_27367;
	private int field_29338;
	private int vertexCount;
	private VertexFormat.DrawMode field_27368;
	private boolean field_27369;
	private VertexFormat field_29339;

	public VertexBuffer() {
		RenderSystem.glGenBuffers(integer -> this.vertexBufferId = integer);
		RenderSystem.glGenVertexArrays(integer -> this.field_29338 = integer);
		RenderSystem.glGenBuffers(integer -> this.indexBufferId = integer);
	}

	public void bind() {
		RenderSystem.glBindBuffer(34962, () -> this.vertexBufferId);
		if (this.field_27369) {
			RenderSystem.glBindBuffer(34963, () -> {
				RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(this.field_27368, this.vertexCount);
				this.field_27367 = indexBuffer.getVertexFormat();
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
			return CompletableFuture.runAsync(() -> this.uploadInternal(buffer), runnable -> RenderSystem.recordRenderCall(runnable::run));
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
			int i = drawArrayParameters.method_31957();
			this.vertexCount = drawArrayParameters.method_31955();
			this.field_27367 = drawArrayParameters.method_31956();
			this.field_29339 = drawArrayParameters.getVertexFormat();
			this.field_27368 = drawArrayParameters.getMode();
			this.field_27369 = drawArrayParameters.method_31960();
			this.method_34437();
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
			method_34430();
		}
	}

	private void method_34437() {
		RenderSystem.glBindVertexArray(() -> this.field_29338);
	}

	public static void method_34430() {
		RenderSystem.glBindVertexArray(() -> 0);
	}

	public void method_34427(Matrix4f matrix4f, Matrix4f matrix4f2, class_5944 arg) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.method_34431(matrix4f.copy(), matrix4f2.copy(), arg));
		} else {
			this.method_34431(matrix4f, matrix4f2, arg);
		}
	}

	public void method_34431(Matrix4f matrix4f, Matrix4f matrix4f2, class_5944 arg) {
		if (this.vertexCount != 0) {
			RenderSystem.assertThread(RenderSystem::isOnRenderThread);
			BufferRenderer.unbindAll();

			for (int i = 0; i < 12; i++) {
				int j = RenderSystem.getShaderTexture(i);
				arg.method_34583("Sampler" + i, j);
			}

			if (arg.field_29470 != null) {
				arg.field_29470.set(matrix4f);
			}

			if (arg.field_29471 != null) {
				arg.field_29471.set(matrix4f2);
			}

			if (arg.field_29474 != null) {
				arg.field_29474.set(RenderSystem.getShaderColor());
			}

			if (arg.field_29477 != null) {
				arg.field_29477.set(RenderSystem.getShaderFogStart());
			}

			if (arg.field_29478 != null) {
				arg.field_29478.set(RenderSystem.getShaderFogEnd());
			}

			if (arg.field_29479 != null) {
				arg.field_29479.set(RenderSystem.getShaderFogColor());
			}

			if (arg.field_29472 != null) {
				arg.field_29472.set(RenderSystem.getTextureMatrix());
			}

			if (arg.field_29481 != null) {
				arg.field_29481.set(RenderSystem.getShaderGameTime());
			}

			if (arg.field_29473 != null) {
				Window window = MinecraftClient.getInstance().getWindow();
				arg.field_29473.set((float)window.getFramebufferWidth(), (float)window.getFramebufferHeight());
			}

			if (arg.field_29480 != null && (this.field_27368 == VertexFormat.DrawMode.LINES || this.field_27368 == VertexFormat.DrawMode.LINE_STRIP)) {
				arg.field_29480.set(RenderSystem.getShaderLineWidth());
			}

			RenderSystem.setupShaderLights(arg);
			this.method_34437();
			this.bind();
			this.method_34435().startDrawing();
			arg.method_34586();
			RenderSystem.drawElements(this.field_27368.mode, this.vertexCount, this.field_27367.field_27374);
			arg.method_34585();
			this.method_34435().endDrawing();
			unbind();
			method_34430();
		}
	}

	public void method_34432() {
		if (this.vertexCount != 0) {
			RenderSystem.assertThread(RenderSystem::isOnRenderThread);
			this.method_34437();
			this.bind();
			this.field_29339.startDrawing();
			RenderSystem.drawElements(this.field_27368.mode, this.vertexCount, this.field_27367.field_27374);
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

		if (this.field_29338 > 0) {
			RenderSystem.glDeleteVertexArrays(this.field_29338);
			this.field_29338 = 0;
		}
	}

	public VertexFormat method_34435() {
		return this.field_29339;
	}
}
