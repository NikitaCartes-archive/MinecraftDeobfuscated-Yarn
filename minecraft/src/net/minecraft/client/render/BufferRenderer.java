package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5944;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

@Environment(EnvType.CLIENT)
public class BufferRenderer {
	private static int currentVertexArrayObject;
	private static int currentVertexBufferObject;
	private static int currentElementBufferObject;
	@Nullable
	private static VertexFormat field_29334;

	public static void unbindAll() {
		if (field_29334 != null) {
			field_29334.endDrawing();
			field_29334 = null;
		}

		GlStateManager.bindBuffer(34963, 0);
		currentElementBufferObject = 0;
		GlStateManager.bindBuffer(34962, 0);
		currentVertexBufferObject = 0;
		GlStateManager.bindVertexArray(0);
		currentVertexArrayObject = 0;
	}

	public static void unbindElementBuffer() {
		GlStateManager.bindBuffer(34963, 0);
		currentElementBufferObject = 0;
	}

	public static void draw(BufferBuilder bufferBuilder) {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(
				() -> {
					Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pairx = bufferBuilder.popData();
					BufferBuilder.DrawArrayParameters drawArrayParametersx = pairx.getFirst();
					method_34422(
						pairx.getSecond(),
						drawArrayParametersx.getMode(),
						drawArrayParametersx.getVertexFormat(),
						drawArrayParametersx.getCount(),
						drawArrayParametersx.method_31956(),
						drawArrayParametersx.method_31955(),
						drawArrayParametersx.method_31960()
					);
				}
			);
		} else {
			Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = bufferBuilder.popData();
			BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
			method_34422(
				pair.getSecond(),
				drawArrayParameters.getMode(),
				drawArrayParameters.getVertexFormat(),
				drawArrayParameters.getCount(),
				drawArrayParameters.method_31956(),
				drawArrayParameters.method_31955(),
				drawArrayParameters.method_31960()
			);
		}
	}

	private static void method_34422(
		ByteBuffer byteBuffer, VertexFormat.DrawMode drawMode, VertexFormat vertexFormat, int i, VertexFormat.IntType intType, int j, boolean bl
	) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		byteBuffer.clear();
		if (i > 0) {
			int k = i * vertexFormat.getVertexSize();
			method_34421(vertexFormat);
			byteBuffer.position(0);
			byteBuffer.limit(k);
			GlStateManager.bufferData(34962, byteBuffer, 35048);
			int m;
			if (bl) {
				RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(drawMode, j);
				int l = indexBuffer.getId();
				if (l != currentElementBufferObject) {
					GlStateManager.bindBuffer(34963, l);
					currentElementBufferObject = l;
				}

				m = indexBuffer.getVertexFormat().field_27374;
			} else {
				int n = vertexFormat.method_34448();
				if (n != currentElementBufferObject) {
					GlStateManager.bindBuffer(34963, n);
					currentElementBufferObject = n;
				}

				byteBuffer.position(k);
				byteBuffer.limit(k + j * intType.size);
				GlStateManager.bufferData(34963, byteBuffer, 35048);
				m = intType.field_27374;
			}

			class_5944 lv = RenderSystem.getShader();

			for (int l = 0; l < 8; l++) {
				int o = RenderSystem.getShaderTexture(l);
				lv.method_34583("Sampler" + l, o);
			}

			if (lv.field_29470 != null) {
				lv.field_29470.set(RenderSystem.getModelViewMatrix());
			}

			if (lv.field_29471 != null) {
				lv.field_29471.set(RenderSystem.getProjectionMatrix());
			}

			if (lv.field_29474 != null) {
				lv.field_29474.set(RenderSystem.getShaderColor());
			}

			if (lv.field_29477 != null) {
				lv.field_29477.set(RenderSystem.getShaderFogStart());
			}

			if (lv.field_29478 != null) {
				lv.field_29478.set(RenderSystem.getShaderFogEnd());
			}

			if (lv.field_29479 != null) {
				lv.field_29479.set(RenderSystem.getShaderFogColor());
			}

			if (lv.field_29472 != null) {
				lv.field_29472.set(RenderSystem.getTextureMatrix());
			}

			if (lv.field_29481 != null) {
				lv.field_29481.set(RenderSystem.getShaderGameTime());
			}

			if (lv.field_29473 != null) {
				Window window = MinecraftClient.getInstance().getWindow();
				lv.field_29473.set((float)window.getFramebufferWidth(), (float)window.getFramebufferHeight());
			}

			if (lv.field_29480 != null && (drawMode == VertexFormat.DrawMode.LINES || drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
				lv.field_29480.set(RenderSystem.getShaderLineWidth());
			}

			RenderSystem.setupShaderLights(lv);
			lv.method_34586();
			GlStateManager.drawElements(drawMode.mode, j, m, 0L);
			lv.method_34585();
			byteBuffer.position(0);
		}
	}

	public static void method_34424(BufferBuilder bufferBuilder) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = bufferBuilder.popData();
		BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
		ByteBuffer byteBuffer = pair.getSecond();
		VertexFormat vertexFormat = drawArrayParameters.getVertexFormat();
		int i = drawArrayParameters.getCount();
		byteBuffer.clear();
		if (i > 0) {
			int j = i * vertexFormat.getVertexSize();
			method_34421(vertexFormat);
			byteBuffer.position(0);
			byteBuffer.limit(j);
			GlStateManager.bufferData(34962, byteBuffer, 35048);
			RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(drawArrayParameters.getMode(), drawArrayParameters.method_31955());
			int k = indexBuffer.getId();
			if (k != currentElementBufferObject) {
				GlStateManager.bindBuffer(34963, k);
				currentElementBufferObject = k;
			}

			int l = indexBuffer.getVertexFormat().field_27374;
			GlStateManager.drawElements(drawArrayParameters.getMode().mode, drawArrayParameters.method_31955(), l, 0L);
			byteBuffer.position(0);
		}
	}

	private static void method_34421(VertexFormat vertexFormat) {
		int i = vertexFormat.method_34446();
		int j = vertexFormat.method_34447();
		boolean bl = vertexFormat != field_29334;
		if (bl) {
			unbindAll();
		}

		if (i != currentVertexArrayObject) {
			GlStateManager.bindVertexArray(i);
			currentVertexArrayObject = i;
		}

		if (j != currentVertexBufferObject) {
			GlStateManager.bindBuffer(34962, j);
			currentVertexBufferObject = j;
		}

		if (bl) {
			vertexFormat.startDrawing();
			field_29334 = vertexFormat;
		}
	}
}
