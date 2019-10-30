package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
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

	public ChunkBorderDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(long limitTime) {
		RenderSystem.shadeModel(7425);
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		Camera camera = this.client.gameRenderer.getCamera();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		double d = camera.getPos().x;
		double e = camera.getPos().y;
		double f = camera.getPos().z;
		double g = 0.0 - e;
		double h = 256.0 - e;
		RenderSystem.disableTexture();
		RenderSystem.disableBlend();
		double i = (double)(camera.getFocusedEntity().chunkX << 4) - d;
		double j = (double)(camera.getFocusedEntity().chunkZ << 4) - f;
		RenderSystem.lineWidth(1.0F);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		for (int k = -16; k <= 32; k += 16) {
			for (int l = -16; l <= 32; l += 16) {
				bufferBuilder.vertex(i + (double)k, g, j + (double)l).color(1.0F, 0.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex(i + (double)k, g, j + (double)l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(i + (double)k, h, j + (double)l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(i + (double)k, h, j + (double)l).color(1.0F, 0.0F, 0.0F, 0.0F).next();
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
			double m = (double)k - e;
			bufferBuilder.vertex(i, m, j).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i, m, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, m, j + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, m, j + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, m, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, m, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, m, j).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		tessellator.draw();
		RenderSystem.lineWidth(2.0F);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		for (int k = 0; k <= 16; k += 16) {
			for (int l = 0; l <= 16; l += 16) {
				bufferBuilder.vertex(i + (double)k, g, j + (double)l).color(0.25F, 0.25F, 1.0F, 0.0F).next();
				bufferBuilder.vertex(i + (double)k, g, j + (double)l).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(i + (double)k, h, j + (double)l).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(i + (double)k, h, j + (double)l).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			}
		}

		for (int k = 0; k <= 256; k += 16) {
			double m = (double)k - e;
			bufferBuilder.vertex(i, m, j).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(i, m, j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i, m, j + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, m, j + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, m, j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i, m, j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i, m, j).color(0.25F, 0.25F, 1.0F, 0.0F).next();
		}

		tessellator.draw();
		RenderSystem.lineWidth(1.0F);
		RenderSystem.enableBlend();
		RenderSystem.enableTexture();
		RenderSystem.shadeModel(7424);
	}
}
