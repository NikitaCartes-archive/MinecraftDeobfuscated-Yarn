package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChunkBorderDebugRenderer implements RenderDebug.DebugRenderer {
	private final MinecraftClient client;

	public ChunkBorderDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(float f, long l) {
		PlayerEntity playerEntity = this.client.player;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		double d = MathHelper.lerp((double)f, playerEntity.prevRenderX, playerEntity.x);
		double e = MathHelper.lerp((double)f, playerEntity.prevRenderY, playerEntity.y);
		double g = MathHelper.lerp((double)f, playerEntity.prevRenderZ, playerEntity.z);
		double h = 0.0 - e;
		double i = 256.0 - e;
		GlStateManager.disableTexture();
		GlStateManager.disableBlend();
		double j = (double)(playerEntity.chunkX << 4) - d;
		double k = (double)(playerEntity.chunkZ << 4) - g;
		GlStateManager.lineWidth(1.0F);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		for (int m = -16; m <= 32; m += 16) {
			for (int n = -16; n <= 32; n += 16) {
				bufferBuilder.vertex(j + (double)m, h, k + (double)n).color(1.0F, 0.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex(j + (double)m, h, k + (double)n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(j + (double)m, i, k + (double)n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(j + (double)m, i, k + (double)n).color(1.0F, 0.0F, 0.0F, 0.0F).next();
			}
		}

		for (int m = 2; m < 16; m += 2) {
			bufferBuilder.vertex(j + (double)m, h, k).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(j + (double)m, h, k).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j + (double)m, i, k).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j + (double)m, i, k).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(j + (double)m, h, k + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(j + (double)m, h, k + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j + (double)m, i, k + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j + (double)m, i, k + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		for (int m = 2; m < 16; m += 2) {
			bufferBuilder.vertex(j, h, k + (double)m).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(j, h, k + (double)m).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j, i, k + (double)m).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j, i, k + (double)m).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(j + 16.0, h, k + (double)m).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(j + 16.0, h, k + (double)m).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j + 16.0, i, k + (double)m).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j + 16.0, i, k + (double)m).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		for (int m = 0; m <= 256; m += 2) {
			double o = (double)m - e;
			bufferBuilder.vertex(j, o, k).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(j, o, k).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j, o, k + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j + 16.0, o, k + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j + 16.0, o, k).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j, o, k).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(j, o, k).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		tessellator.draw();
		GlStateManager.lineWidth(2.0F);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		for (int m = 0; m <= 16; m += 16) {
			for (int n = 0; n <= 16; n += 16) {
				bufferBuilder.vertex(j + (double)m, h, k + (double)n).color(0.25F, 0.25F, 1.0F, 0.0F).next();
				bufferBuilder.vertex(j + (double)m, h, k + (double)n).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(j + (double)m, i, k + (double)n).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(j + (double)m, i, k + (double)n).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			}
		}

		for (int m = 0; m <= 256; m += 16) {
			double o = (double)m - e;
			bufferBuilder.vertex(j, o, k).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(j, o, k).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(j, o, k + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(j + 16.0, o, k + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(j + 16.0, o, k).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(j, o, k).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(j, o, k).color(0.25F, 0.25F, 1.0F, 0.0F).next();
		}

		tessellator.draw();
		GlStateManager.lineWidth(1.0F);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture();
	}
}
