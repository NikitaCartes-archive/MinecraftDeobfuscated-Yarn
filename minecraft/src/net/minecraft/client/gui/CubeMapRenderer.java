package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
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
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.multMatrix(
			Matrix4f.method_4929(85.0, (float)minecraftClient.window.getFramebufferWidth() / (float)minecraftClient.window.getFramebufferHeight(), 0.05F, 10.0F)
		);
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		int i = 2;

		for (int j = 0; j < 4; j++) {
			GlStateManager.pushMatrix();
			float k = ((float)(j % 2) / 2.0F - 0.5F) / 256.0F;
			float l = ((float)(j / 2) / 2.0F - 0.5F) / 256.0F;
			float m = 0.0F;
			GlStateManager.translatef(k, l, 0.0F);
			GlStateManager.rotatef(f, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(g, 0.0F, 1.0F, 0.0F);

			for (int n = 0; n < 6; n++) {
				minecraftClient.method_1531().bindTexture(this.faces[n]);
				bufferBuilder.method_1328(7, VertexFormats.field_1575);
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

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		bufferBuilder.setOffset(0.0, 0.0, 0.0);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepthTest();
	}

	public CompletableFuture<Void> method_18143(TextureManager textureManager, Executor executor) {
		CompletableFuture<?>[] completableFutures = new CompletableFuture[6];

		for (int i = 0; i < completableFutures.length; i++) {
			completableFutures[i] = textureManager.loadTextureAsync(this.faces[i], executor);
		}

		return CompletableFuture.allOf(completableFutures);
	}
}
