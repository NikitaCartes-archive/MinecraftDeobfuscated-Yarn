package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class BufferRenderer {
	public static void draw(BufferBuilder bufferBuilder) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> {
				Pair<BufferBuilder.class_4574, ByteBuffer> pairxx = bufferBuilder.method_22632();
				BufferBuilder.class_4574 lvxx = pairxx.getFirst();
				method_22639((ByteBuffer)pairxx.getSecond(), lvxx.method_22636(), lvxx.method_22634(), lvxx.method_22635());
			});
		} else {
			Pair<BufferBuilder.class_4574, ByteBuffer> pair = bufferBuilder.method_22632();
			BufferBuilder.class_4574 lv = pair.getFirst();
			method_22639((ByteBuffer)pair.getSecond(), lv.method_22636(), lv.method_22634(), lv.method_22635());
		}
	}

	private static void method_22639(ByteBuffer byteBuffer, int i, VertexFormat vertexFormat, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		byteBuffer.clear();
		if (j > 0) {
			vertexFormat.method_22649(MemoryUtil.memAddress(byteBuffer));
			GlStateManager.drawArrays(i, 0, j);
			vertexFormat.method_22651();
		}
	}
}
