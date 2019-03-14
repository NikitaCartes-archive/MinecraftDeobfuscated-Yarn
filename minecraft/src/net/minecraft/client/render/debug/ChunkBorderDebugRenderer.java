package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

@Environment(EnvType.CLIENT)
public class ChunkBorderDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public ChunkBorderDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(long l) {
		Camera camera = this.client.gameRenderer.method_19418();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		double d = camera.getPos().x;
		double e = camera.getPos().y;
		double f = camera.getPos().z;
		double g = 0.0 - e;
		double h = 256.0 - e;
		GlStateManager.disableTexture();
		GlStateManager.disableBlend();
		double i = (double)(camera.getFocusedEntity().chunkX << 4) - d;
		double j = (double)(camera.getFocusedEntity().chunkZ << 4) - f;
		GlStateManager.lineWidth(1.0F);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		for (int k = -16; k <= 32; k += 16) {
			for (int m = -16; m <= 32; m += 16) {
				bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(1.0F, 0.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(1.0F, 0.0F, 0.0F, 0.0F).next();
			}
		}

		for (int k = 2; k < 16; k += 2) {
			bufferBuilder.vertex(i + (double)k, g, j).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i + (double)k, g, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + (double)k, h, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + (double)k, h, j).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i + (double)k, g, j + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i + (double)k, g, j + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + (double)k, h, j + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + (double)k, h, j + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		for (int k = 2; k < 16; k += 2) {
			bufferBuilder.vertex(i, g, j + (double)k).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i, g, j + (double)k).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, h, j + (double)k).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, h, j + (double)k).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i + 16.0, g, j + (double)k).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i + 16.0, g, j + (double)k).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, h, j + (double)k).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, h, j + (double)k).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		for (int k = 0; k <= 256; k += 2) {
			double n = (double)k - e;
			bufferBuilder.vertex(i, n, j).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i, n, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, n, j + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, n, j + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, n, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, n, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, n, j).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		tessellator.draw();
		GlStateManager.lineWidth(2.0F);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		for (int k = 0; k <= 16; k += 16) {
			for (int m = 0; m <= 16; m += 16) {
				bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(0.25F, 0.25F, 1.0F, 0.0F).next();
				bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			}
		}

		for (int k = 0; k <= 256; k += 16) {
			double n = (double)k - e;
			bufferBuilder.vertex(i, n, j).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(i, n, j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i, n, j + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, n, j + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, n, j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i, n, j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i, n, j).color(0.25F, 0.25F, 1.0F, 0.0F).next();
		}

		tessellator.draw();
		GlStateManager.lineWidth(1.0F);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture();
	}
}
