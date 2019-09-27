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

@Environment(EnvType.CLIENT)
public class class_4603 {
	private static final Identifier field_20986 = new Identifier("textures/misc/underwater.png");

	public static void method_23067(MinecraftClient minecraftClient, class_4587 arg) {
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
				method_23068(minecraftClient, minecraftClient.getBlockRenderManager().getModels().getSprite(blockState), arg);
			}
		}

		if (!minecraftClient.player.isSpectator()) {
			if (minecraftClient.player.isInFluid(FluidTags.WATER)) {
				method_23069(minecraftClient, arg);
			}

			if (minecraftClient.player.isOnFire()) {
				method_23070(minecraftClient, arg);
			}
		}

		RenderSystem.enableAlphaTest();
	}

	private static void method_23068(MinecraftClient minecraftClient, Sprite sprite, class_4587 arg) {
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
		Matrix4f matrix4f = arg.method_22910();
		bufferBuilder.begin(7, VertexFormats.field_20887);
		bufferBuilder.method_22918(matrix4f, -1.0F, -1.0F, -0.5F).method_22915(0.1F, 0.1F, 0.1F, 0.5F).texture(m, o).next();
		bufferBuilder.method_22918(matrix4f, 1.0F, -1.0F, -0.5F).method_22915(0.1F, 0.1F, 0.1F, 0.5F).texture(l, o).next();
		bufferBuilder.method_22918(matrix4f, 1.0F, 1.0F, -0.5F).method_22915(0.1F, 0.1F, 0.1F, 0.5F).texture(l, n).next();
		bufferBuilder.method_22918(matrix4f, -1.0F, 1.0F, -0.5F).method_22915(0.1F, 0.1F, 0.1F, 0.5F).texture(m, n).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	private static void method_23069(MinecraftClient minecraftClient, class_4587 arg) {
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
		Matrix4f matrix4f = arg.method_22910();
		bufferBuilder.begin(7, VertexFormats.field_20887);
		bufferBuilder.method_22918(matrix4f, -1.0F, -1.0F, -0.5F).method_22915(f, f, f, 0.1F).texture(4.0F + m, 4.0F + n).next();
		bufferBuilder.method_22918(matrix4f, 1.0F, -1.0F, -0.5F).method_22915(f, f, f, 0.1F).texture(0.0F + m, 4.0F + n).next();
		bufferBuilder.method_22918(matrix4f, 1.0F, 1.0F, -0.5F).method_22915(f, f, f, 0.1F).texture(0.0F + m, 0.0F + n).next();
		bufferBuilder.method_22918(matrix4f, -1.0F, 1.0F, -0.5F).method_22915(f, f, f, 0.1F).texture(4.0F + m, 0.0F + n).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.disableBlend();
	}

	private static void method_23070(MinecraftClient minecraftClient, class_4587 arg) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
		RenderSystem.depthFunc(519);
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		float f = 1.0F;

		for (int i = 0; i < 2; i++) {
			arg.method_22903();
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
			arg.method_22904((double)((float)(-(i * 2 - 1)) * 0.24F), -0.3F, 0.0);
			arg.method_22907(Vector3f.field_20705.method_23214((float)(i * 2 - 1) * 10.0F, true));
			Matrix4f matrix4f = arg.method_22910();
			bufferBuilder.begin(7, VertexFormats.field_20887);
			bufferBuilder.method_22918(matrix4f, -0.5F, -0.5F, -0.5F).method_22915(1.0F, 1.0F, 1.0F, 0.9F).texture(h, k).next();
			bufferBuilder.method_22918(matrix4f, 0.5F, -0.5F, -0.5F).method_22915(1.0F, 1.0F, 1.0F, 0.9F).texture(g, k).next();
			bufferBuilder.method_22918(matrix4f, 0.5F, 0.5F, -0.5F).method_22915(1.0F, 1.0F, 1.0F, 0.9F).texture(g, j).next();
			bufferBuilder.method_22918(matrix4f, -0.5F, 0.5F, -0.5F).method_22915(1.0F, 1.0F, 1.0F, 0.9F).texture(h, j).next();
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			arg.method_22909();
		}

		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.depthFunc(515);
	}
}
