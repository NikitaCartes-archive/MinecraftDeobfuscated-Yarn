package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.concurrent.CompletableFuture;
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
public class class_751 {
	private final Identifier[] field_3952 = new Identifier[6];

	public class_751(Identifier identifier) {
		for (int i = 0; i < 6; i++) {
			this.field_3952[i] = new Identifier(identifier.getNamespace(), identifier.getPath() + '_' + i + ".png");
		}
	}

	public void method_3156(MinecraftClient minecraftClient, float f, float g, float h) {
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
				minecraftClient.getTextureManager().bindTexture(this.field_3952[n]);
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

	public CompletableFuture<Void> method_18143(TextureManager textureManager) {
		CompletableFuture<?>[] completableFutures = new CompletableFuture[6];

		for (int i = 0; i < completableFutures.length; i++) {
			completableFutures[i] = textureManager.method_18168(this.field_3952[i]);
		}

		return CompletableFuture.allOf(completableFutures);
	}
}
