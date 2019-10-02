package net.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class class_4603 {
	private static final Identifier field_20986 = new Identifier("textures/misc/underwater.png");

	public static void method_23067(MinecraftClient minecraftClient, MatrixStack matrixStack) {
		RenderSystem.disableAlphaTest();
		if (minecraftClient.player.isInsideWall()) {
			BlockState blockState = minecraftClient.world.getBlockState(new BlockPos(minecraftClient.player));
			PlayerEntity playerEntity = minecraftClient.player;

			for (int i = 0; i < 8; i++) {
				double d = playerEntity.x + (double)(((float)((i >> 0) % 2) - 0.5F) * playerEntity.getWidth() * 0.8F);
				double e = playerEntity.y + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
				double f = playerEntity.z + (double)(((float)((i >> 2) % 2) - 0.5F) * playerEntity.getWidth() * 0.8F);
				BlockPos blockPos = new BlockPos(d, e + (double)playerEntity.getStandingEyeHeight(), f);
				BlockState blockState2 = minecraftClient.world.getBlockState(blockPos);
				if (blockState2.canSuffocate(minecraftClient.world, blockPos)) {
					blockState = blockState2;
				}
			}

			if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				method_23068(minecraftClient, minecraftClient.getBlockRenderManager().getModels().getSprite(blockState), matrixStack);
			}
		}

		if (!minecraftClient.player.isSpectator()) {
			if (minecraftClient.player.isInFluid(FluidTags.WATER)) {
				method_23069(minecraftClient, matrixStack);
			}

			if (minecraftClient.player.isOnFire()) {
				method_23070(minecraftClient, matrixStack);
			}
		}

		RenderSystem.enableAlphaTest();
	}

	private static void method_23068(MinecraftClient minecraftClient, Sprite sprite, MatrixStack matrixStack) {
		minecraftClient.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
		float f = 0.1F;
		float g = -1.0F;
		float h = 1.0F;
		float i = -1.0F;
		float j = 1.0F;
		float k = -0.5F;
		float l = sprite.getMinU();
		float m = sprite.getMaxU();
		float n = sprite.getMinV();
		float o = sprite.getMaxV();
		Matrix4f matrix4f = matrixStack.peek();
		bufferBuilder.begin(7, VertexFormats.field_20887);
		bufferBuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).texture(m, o).next();
		bufferBuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).texture(l, o).next();
		bufferBuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).texture(l, n).next();
		bufferBuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).texture(m, n).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	private static void method_23069(MinecraftClient minecraftClient, MatrixStack matrixStack) {
		minecraftClient.getTextureManager().bindTexture(field_20986);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
		float f = minecraftClient.player.getBrightnessAtEyes();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		float g = 4.0F;
		float h = -1.0F;
		float i = 1.0F;
		float j = -1.0F;
		float k = 1.0F;
		float l = -0.5F;
		float m = -minecraftClient.player.yaw / 64.0F;
		float n = minecraftClient.player.pitch / 64.0F;
		Matrix4f matrix4f = matrixStack.peek();
		bufferBuilder.begin(7, VertexFormats.field_20887);
		bufferBuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(f, f, f, 0.1F).texture(4.0F + m, 4.0F + n).next();
		bufferBuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(f, f, f, 0.1F).texture(0.0F + m, 4.0F + n).next();
		bufferBuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(f, f, f, 0.1F).texture(0.0F + m, 0.0F + n).next();
		bufferBuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(f, f, f, 0.1F).texture(4.0F + m, 0.0F + n).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.disableBlend();
	}

	private static void method_23070(MinecraftClient minecraftClient, MatrixStack matrixStack) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
		RenderSystem.depthFunc(519);
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		float f = 1.0F;

		for (int i = 0; i < 2; i++) {
			matrixStack.push();
			Sprite sprite = minecraftClient.getSpriteAtlas().getSprite(ModelLoader.FIRE_1);
			minecraftClient.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			float g = sprite.getMinU();
			float h = sprite.getMaxU();
			float j = sprite.getMinV();
			float k = sprite.getMaxV();
			float l = -0.5F;
			float m = 0.5F;
			float n = -0.5F;
			float o = 0.5F;
			float p = -0.5F;
			matrixStack.translate((double)((float)(-(i * 2 - 1)) * 0.24F), -0.3F, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)(i * 2 - 1) * 10.0F, true));
			Matrix4f matrix4f = matrixStack.peek();
			bufferBuilder.begin(7, VertexFormats.field_20887);
			bufferBuilder.vertex(matrix4f, -0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).texture(h, k).next();
			bufferBuilder.vertex(matrix4f, 0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).texture(g, k).next();
			bufferBuilder.vertex(matrix4f, 0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).texture(g, j).next();
			bufferBuilder.vertex(matrix4f, -0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).texture(h, j).next();
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			matrixStack.pop();
		}

		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.depthFunc(515);
	}
}
