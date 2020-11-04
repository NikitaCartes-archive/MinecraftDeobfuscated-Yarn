package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;

@Environment(EnvType.CLIENT)
public class ChunkBorderDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public ChunkBorderDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		RenderSystem.enableDepthTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		double d = (double)this.client.world.getBottomHeightLimit() - cameraY;
		double e = (double)this.client.world.getTopHeightLimit() - cameraY;
		RenderSystem.disableTexture();
		RenderSystem.disableBlend();
		ChunkPos chunkPos = entity.getChunkPos();
		double f = (double)chunkPos.getStartX() - cameraX;
		double g = (double)chunkPos.getStartZ() - cameraZ;
		RenderSystem.lineWidth(1.0F);
		bufferBuilder.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);

		for (int i = -16; i <= 32; i += 16) {
			for (int j = -16; j <= 32; j += 16) {
				bufferBuilder.vertex(f + (double)i, d, g + (double)j).color(1.0F, 0.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex(f + (double)i, d, g + (double)j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(f + (double)i, e, g + (double)j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(f + (double)i, e, g + (double)j).color(1.0F, 0.0F, 0.0F, 0.0F).next();
			}
		}

		for (int i = 2; i < 16; i += 2) {
			bufferBuilder.vertex(f + (double)i, d, g).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f + (double)i, d, g).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + (double)i, e, g).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + (double)i, e, g).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f + (double)i, d, g + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f + (double)i, d, g + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + (double)i, e, g + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + (double)i, e, g + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		for (int i = 2; i < 16; i += 2) {
			bufferBuilder.vertex(f, d, g + (double)i).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f, d, g + (double)i).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f, e, g + (double)i).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f, e, g + (double)i).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f + 16.0, d, g + (double)i).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f + 16.0, d, g + (double)i).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, e, g + (double)i).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, e, g + (double)i).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		for (int i = this.client.world.getBottomHeightLimit(); i <= this.client.world.getTopHeightLimit(); i += 2) {
			double h = (double)i - cameraY;
			bufferBuilder.vertex(f, h, g).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f, h, g).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f, h, g + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, h, g + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, h, g).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f, h, g).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f, h, g).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		tessellator.draw();
		RenderSystem.lineWidth(2.0F);
		bufferBuilder.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);

		for (int i = 0; i <= 16; i += 16) {
			for (int j = 0; j <= 16; j += 16) {
				bufferBuilder.vertex(f + (double)i, d, g + (double)j).color(0.25F, 0.25F, 1.0F, 0.0F).next();
				bufferBuilder.vertex(f + (double)i, d, g + (double)j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(f + (double)i, e, g + (double)j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(f + (double)i, e, g + (double)j).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			}
		}

		for (int i = this.client.world.getBottomHeightLimit(); i <= this.client.world.getTopHeightLimit(); i += 16) {
			double h = (double)i - cameraY;
			bufferBuilder.vertex(f, h, g).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(f, h, g).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(f, h, g + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, h, g + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, h, g).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(f, h, g).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(f, h, g).color(0.25F, 0.25F, 1.0F, 0.0F).next();
		}

		tessellator.draw();
		RenderSystem.lineWidth(1.0F);
		RenderSystem.enableBlend();
		RenderSystem.enableTexture();
		RenderSystem.shadeModel(7424);
	}
}
