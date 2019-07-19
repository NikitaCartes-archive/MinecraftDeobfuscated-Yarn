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

	public CubeMapRenderer(Identifier faces) {
		for (int i = 0; i < 6; i++) {
			this.faces[i] = new Identifier(faces.getNamespace(), faces.getPath() + '_' + i + ".png");
		}
	}

	public void draw(MinecraftClient client, float x, float y, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.multMatrix(Matrix4f.method_4929(85.0, (float)client.window.getFramebufferWidth() / (float)client.window.getFramebufferHeight(), 0.05F, 10.0F));
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
			float f = ((float)(j % 2) / 2.0F - 0.5F) / 256.0F;
			float g = ((float)(j / 2) / 2.0F - 0.5F) / 256.0F;
			float h = 0.0F;
			GlStateManager.translatef(f, g, 0.0F);
			GlStateManager.rotatef(x, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(y, 0.0F, 1.0F, 0.0F);

			for (int k = 0; k < 6; k++) {
				client.getTextureManager().bindTexture(this.faces[k]);
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				int l = Math.round(255.0F * alpha) / (j + 1);
				if (k == 0) {
					bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(1.0, 1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(1.0, -1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, l).next();
				}

				if (k == 1) {
					bufferBuilder.vertex(1.0, -1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(1.0, 1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, l).next();
				}

				if (k == 2) {
					bufferBuilder.vertex(1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, l).next();
				}

				if (k == 3) {
					bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, l).next();
				}

				if (k == 4) {
					bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(1.0, -1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, l).next();
				}

				if (k == 5) {
					bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, l).next();
					bufferBuilder.vertex(1.0, 1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, l).next();
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

	public CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
		CompletableFuture<?>[] completableFutures = new CompletableFuture[6];

		for (int i = 0; i < completableFutures.length; i++) {
			completableFutures[i] = textureManager.loadTextureAsync(this.faces[i], executor);
		}

		return CompletableFuture.allOf(completableFutures);
	}
}
