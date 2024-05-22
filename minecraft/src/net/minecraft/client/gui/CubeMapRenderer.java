package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

@Environment(EnvType.CLIENT)
public class CubeMapRenderer {
	private static final int FACES_COUNT = 6;
	private final Identifier[] faces = new Identifier[6];

	public CubeMapRenderer(Identifier faces) {
		for (int i = 0; i < 6; i++) {
			this.faces[i] = faces.withPath(faces.getPath() + "_" + i + ".png");
		}
	}

	public void draw(MinecraftClient client, float x, float y, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		Matrix4f matrix4f = new Matrix4f()
			.setPerspective(1.4835298F, (float)client.getWindow().getFramebufferWidth() / (float)client.getWindow().getFramebufferHeight(), 0.05F, 10.0F);
		RenderSystem.backupProjectionMatrix();
		RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_DISTANCE);
		Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
		matrix4fStack.pushMatrix();
		matrix4fStack.rotationX((float) Math.PI);
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.enableBlend();
		RenderSystem.disableCull();
		RenderSystem.depthMask(false);
		int i = 2;

		for (int j = 0; j < 4; j++) {
			matrix4fStack.pushMatrix();
			float f = ((float)(j % 2) / 2.0F - 0.5F) / 256.0F;
			float g = ((float)(j / 2) / 2.0F - 0.5F) / 256.0F;
			float h = 0.0F;
			matrix4fStack.translate(f, g, 0.0F);
			matrix4fStack.rotateX(x * (float) (Math.PI / 180.0));
			matrix4fStack.rotateY(y * (float) (Math.PI / 180.0));
			RenderSystem.applyModelViewMatrix();

			for (int k = 0; k < 6; k++) {
				RenderSystem.setShaderTexture(0, this.faces[k]);
				BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
				int l = Math.round(255.0F * alpha) / (j + 1);
				if (k == 0) {
					bufferBuilder.vertex(-1.0F, -1.0F, 1.0F).texture(0.0F, 0.0F).colorRgb(l);
					bufferBuilder.vertex(-1.0F, 1.0F, 1.0F).texture(0.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(1.0F, 1.0F, 1.0F).texture(1.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(1.0F, -1.0F, 1.0F).texture(1.0F, 0.0F).colorRgb(l);
				}

				if (k == 1) {
					bufferBuilder.vertex(1.0F, -1.0F, 1.0F).texture(0.0F, 0.0F).colorRgb(l);
					bufferBuilder.vertex(1.0F, 1.0F, 1.0F).texture(0.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(1.0F, 1.0F, -1.0F).texture(1.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(1.0F, -1.0F, -1.0F).texture(1.0F, 0.0F).colorRgb(l);
				}

				if (k == 2) {
					bufferBuilder.vertex(1.0F, -1.0F, -1.0F).texture(0.0F, 0.0F).colorRgb(l);
					bufferBuilder.vertex(1.0F, 1.0F, -1.0F).texture(0.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(-1.0F, 1.0F, -1.0F).texture(1.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(-1.0F, -1.0F, -1.0F).texture(1.0F, 0.0F).colorRgb(l);
				}

				if (k == 3) {
					bufferBuilder.vertex(-1.0F, -1.0F, -1.0F).texture(0.0F, 0.0F).colorRgb(l);
					bufferBuilder.vertex(-1.0F, 1.0F, -1.0F).texture(0.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(-1.0F, 1.0F, 1.0F).texture(1.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(-1.0F, -1.0F, 1.0F).texture(1.0F, 0.0F).colorRgb(l);
				}

				if (k == 4) {
					bufferBuilder.vertex(-1.0F, -1.0F, -1.0F).texture(0.0F, 0.0F).colorRgb(l);
					bufferBuilder.vertex(-1.0F, -1.0F, 1.0F).texture(0.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(1.0F, -1.0F, 1.0F).texture(1.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(1.0F, -1.0F, -1.0F).texture(1.0F, 0.0F).colorRgb(l);
				}

				if (k == 5) {
					bufferBuilder.vertex(-1.0F, 1.0F, 1.0F).texture(0.0F, 0.0F).colorRgb(l);
					bufferBuilder.vertex(-1.0F, 1.0F, -1.0F).texture(0.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(1.0F, 1.0F, -1.0F).texture(1.0F, 1.0F).colorRgb(l);
					bufferBuilder.vertex(1.0F, 1.0F, 1.0F).texture(1.0F, 0.0F).colorRgb(l);
				}

				BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			}

			matrix4fStack.popMatrix();
			RenderSystem.colorMask(true, true, true, false);
		}

		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.restoreProjectionMatrix();
		matrix4fStack.popMatrix();
		RenderSystem.applyModelViewMatrix();
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
