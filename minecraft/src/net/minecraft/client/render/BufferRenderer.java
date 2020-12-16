package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BufferRenderer {
	private static int field_27364;
	private static int field_27365;

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
						drawArrayParametersx.method_31956(),
						drawArrayParametersx.method_31955(),
						drawArrayParametersx.method_31960()
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
				drawArrayParameters.method_31956(),
				drawArrayParameters.method_31955(),
				drawArrayParameters.method_31960()
			);
		}
	}

	private static void draw(ByteBuffer buffer, VertexFormat.DrawMode drawMode, VertexFormat vertexFormat, int i, VertexFormat.IntType intType, int j, boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		buffer.clear();
		if (i > 0) {
			if (field_27364 == 0) {
				field_27364 = GlStateManager.genBuffers();
			}

			int k = i * vertexFormat.getVertexSize();
			GlStateManager.bindBuffer(34962, field_27364);
			buffer.position(0);
			buffer.limit(k);
			GlStateManager.bufferData(34962, buffer, 35044);
			int l;
			if (bl) {
				RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(drawMode, j);
				GlStateManager.bindBuffer(34963, indexBuffer.getId());
				l = indexBuffer.getVertexFormat().field_27374;
			} else {
				if (field_27365 == 0) {
					field_27365 = GlStateManager.genBuffers();
				}

				GlStateManager.bindBuffer(34963, field_27365);
				buffer.position(k);
				buffer.limit(k + j * intType.size);
				GlStateManager.bufferData(34963, buffer, 35044);
				l = intType.field_27374;
			}

			vertexFormat.startDrawing(0L);
			GlStateManager.drawElements(drawMode.mode, j, l, 0L);
			vertexFormat.endDrawing();
			buffer.position(0);
			GlStateManager.bindBuffer(34963, 0);
			GlStateManager.bindBuffer(34962, 0);
		}
	}
}
