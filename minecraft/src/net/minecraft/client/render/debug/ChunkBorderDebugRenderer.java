package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class ChunkBorderDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private static final int DARK_CYAN = ColorHelper.Argb.getArgb(255, 0, 155, 155);
	private static final int YELLOW = ColorHelper.Argb.getArgb(255, 255, 255, 0);

	public ChunkBorderDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
		float f = (float)((double)this.client.world.getBottomY() - cameraY);
		float g = (float)((double)this.client.world.getTopY() - cameraY);
		ChunkPos chunkPos = entity.getChunkPos();
		float h = (float)((double)chunkPos.getStartX() - cameraX);
		float i = (float)((double)chunkPos.getStartZ() - cameraZ);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1.0));
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();

		for (int j = -16; j <= 32; j += 16) {
			for (int k = -16; k <= 32; k += 16) {
				vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k).color(1.0F, 0.0F, 0.0F, 0.0F);
				vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k).color(1.0F, 0.0F, 0.0F, 0.5F);
				vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k).color(1.0F, 0.0F, 0.0F, 0.5F);
				vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k).color(1.0F, 0.0F, 0.0F, 0.0F);
			}
		}

		for (int j = 2; j < 16; j += 2) {
			int k = j % 4 == 0 ? DARK_CYAN : YELLOW;
			vertexConsumer.vertex(matrix4f, h + (float)j, f, i).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h + (float)j, f, i).color(k);
			vertexConsumer.vertex(matrix4f, h + (float)j, g, i).color(k);
			vertexConsumer.vertex(matrix4f, h + (float)j, g, i).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h + (float)j, f, i + 16.0F).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h + (float)j, f, i + 16.0F).color(k);
			vertexConsumer.vertex(matrix4f, h + (float)j, g, i + 16.0F).color(k);
			vertexConsumer.vertex(matrix4f, h + (float)j, g, i + 16.0F).color(1.0F, 1.0F, 0.0F, 0.0F);
		}

		for (int j = 2; j < 16; j += 2) {
			int k = j % 4 == 0 ? DARK_CYAN : YELLOW;
			vertexConsumer.vertex(matrix4f, h, f, i + (float)j).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h, f, i + (float)j).color(k);
			vertexConsumer.vertex(matrix4f, h, g, i + (float)j).color(k);
			vertexConsumer.vertex(matrix4f, h, g, i + (float)j).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h + 16.0F, f, i + (float)j).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h + 16.0F, f, i + (float)j).color(k);
			vertexConsumer.vertex(matrix4f, h + 16.0F, g, i + (float)j).color(k);
			vertexConsumer.vertex(matrix4f, h + 16.0F, g, i + (float)j).color(1.0F, 1.0F, 0.0F, 0.0F);
		}

		for (int j = this.client.world.getBottomY(); j <= this.client.world.getTopY(); j += 2) {
			float l = (float)((double)j - cameraY);
			int m = j % 8 == 0 ? DARK_CYAN : YELLOW;
			vertexConsumer.vertex(matrix4f, h, l, i).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h, l, i).color(m);
			vertexConsumer.vertex(matrix4f, h, l, i + 16.0F).color(m);
			vertexConsumer.vertex(matrix4f, h + 16.0F, l, i + 16.0F).color(m);
			vertexConsumer.vertex(matrix4f, h + 16.0F, l, i).color(m);
			vertexConsumer.vertex(matrix4f, h, l, i).color(m);
			vertexConsumer.vertex(matrix4f, h, l, i).color(1.0F, 1.0F, 0.0F, 0.0F);
		}

		vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(2.0));

		for (int j = 0; j <= 16; j += 16) {
			for (int k = 0; k <= 16; k += 16) {
				vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k).color(0.25F, 0.25F, 1.0F, 0.0F);
				vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k).color(0.25F, 0.25F, 1.0F, 1.0F);
				vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k).color(0.25F, 0.25F, 1.0F, 1.0F);
				vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k).color(0.25F, 0.25F, 1.0F, 0.0F);
			}
		}

		for (int j = this.client.world.getBottomY(); j <= this.client.world.getTopY(); j += 16) {
			float l = (float)((double)j - cameraY);
			vertexConsumer.vertex(matrix4f, h, l, i).color(0.25F, 0.25F, 1.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h, l, i).color(0.25F, 0.25F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h, l, i + 16.0F).color(0.25F, 0.25F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h + 16.0F, l, i + 16.0F).color(0.25F, 0.25F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h + 16.0F, l, i).color(0.25F, 0.25F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h, l, i).color(0.25F, 0.25F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h, l, i).color(0.25F, 0.25F, 1.0F, 0.0F);
		}
	}
}
