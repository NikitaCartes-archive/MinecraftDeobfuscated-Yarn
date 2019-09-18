package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CubeMapRenderer {
	private final Identifier[] faces = new Identifier[6];

	public CubeMapRenderer(Identifier identifier) {
		for (int i = 0; i < 6; i++) {
			this.faces[i] = new Identifier(identifier.getNamespace(), identifier.getPath() + '_' + i + ".png");
		}
	}

	public void draw(MinecraftClient minecraftClient, float f, float g, float h) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.matrixMode(5889);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		RenderSystem.multMatrix(
			Matrix4f.method_4929(
				85.0, (float)minecraftClient.method_22683().getFramebufferWidth() / (float)minecraftClient.method_22683().getFramebufferHeight(), 0.05F, 10.0F
			)
		);
		RenderSystem.matrixMode(5888);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableCull();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		int i = 2;

		for (int j = 0; j < 4; j++) {
			RenderSystem.pushMatrix();
			float k = ((float)(j % 2) / 2.0F - 0.5F) / 256.0F;
			float l = ((float)(j / 2) / 2.0F - 0.5F) / 256.0F;
			float m = 0.0F;
			RenderSystem.translatef(k, l, 0.0F);
			RenderSystem.rotatef(f, 1.0F, 0.0F, 0.0F);
			RenderSystem.rotatef(g, 0.0F, 1.0F, 0.0F);

			for (int n = 0; n < 6; n++) {
				minecraftClient.getTextureManager().method_22813(this.faces[n]);
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				int o = Math.round(255.0F * h) / (j + 1);
				if (n == 0) {
					bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(1.0, 1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(1.0, -1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, o).next();
				}

				if (n == 1) {
					bufferBuilder.vertex(1.0, -1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(1.0, 1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, o).next();
				}

				if (n == 2) {
					bufferBuilder.vertex(1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, o).next();
				}

				if (n == 3) {
					bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, o).next();
				}

				if (n == 4) {
					bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(1.0, -1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, o).next();
				}

				if (n == 5) {
					bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, o).next();
					bufferBuilder.vertex(1.0, 1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, o).next();
				}

				tessellator.draw();
			}

			RenderSystem.popMatrix();
			RenderSystem.colorMask(true, true, true, false);
		}

		bufferBuilder.setOffset(0.0, 0.0, 0.0);
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.matrixMode(5889);
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(5888);
		RenderSystem.popMatrix();
		RenderSystem.depthMask(true);
		RenderSystem.enableCull();
		RenderSystem.enableDepthTest();
	}

	public CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
		CompletableFuture<?>[] completableFutures = new CompletableFuture[6];

		for (int i = 0; i < completableFutures.length; i++) {
			completableFutures[i] = textureManager.loadTextureAsync(this.faces[i], executor);
		}

		return CompletableFuture.allOf(completableFutures);
	}
}
