package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
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
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class InGameOverlayRenderer {
	private static final Identifier UNDERWATER_TEX = new Identifier("textures/misc/underwater.png");

	public static void renderOverlays(MinecraftClient minecraftClient, MatrixStack matrixStack) {
		RenderSystem.disableAlphaTest();
		PlayerEntity playerEntity = minecraftClient.player;
		if (!playerEntity.noClip) {
			BlockState blockState = getInWallBlockState(playerEntity);
			if (blockState != null) {
				renderInWallOverlay(minecraftClient, minecraftClient.getBlockRenderManager().getModels().getSprite(blockState), matrixStack);
			}
		}

		if (!minecraftClient.player.isSpectator()) {
			if (minecraftClient.player.isSubmergedIn(FluidTags.WATER)) {
				renderUnderwaterOverlay(minecraftClient, matrixStack);
			}

			if (minecraftClient.player.isOnFire()) {
				renderFireOverlay(minecraftClient, matrixStack);
			}
		}

		RenderSystem.enableAlphaTest();
	}

	@Nullable
	private static BlockState getInWallBlockState(PlayerEntity playerEntity) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < 8; i++) {
			double d = playerEntity.getX() + (double)(((float)((i >> 0) % 2) - 0.5F) * playerEntity.getWidth() * 0.8F);
			double e = playerEntity.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
			double f = playerEntity.getZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * playerEntity.getWidth() * 0.8F);
			mutable.set(d, e, f);
			BlockState blockState = playerEntity.world.getBlockState(mutable);
			if (blockState.getRenderType() != BlockRenderType.INVISIBLE && blockState.hasInWallOverlay(playerEntity.world, mutable)) {
				return blockState;
			}
		}

		return null;
	}

	private static void renderInWallOverlay(MinecraftClient minecraftClient, Sprite sprite, MatrixStack matrixStack) {
		minecraftClient.getTextureManager().bindTexture(sprite.getAtlas().getId());
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
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
		Matrix4f matrix4f = matrixStack.peek().getModel();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).texture(m, o).next();
		bufferBuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).texture(l, o).next();
		bufferBuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).texture(l, n).next();
		bufferBuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).texture(m, n).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	private static void renderUnderwaterOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack) {
		minecraftClient.getTextureManager().bindTexture(UNDERWATER_TEX);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
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
		Matrix4f matrix4f = matrixStack.peek().getModel();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(f, f, f, 0.1F).texture(4.0F + m, 4.0F + n).next();
		bufferBuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(f, f, f, 0.1F).texture(0.0F + m, 4.0F + n).next();
		bufferBuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(f, f, f, 0.1F).texture(0.0F + m, 0.0F + n).next();
		bufferBuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(f, f, f, 0.1F).texture(4.0F + m, 0.0F + n).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.disableBlend();
	}

	private static void renderFireOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.depthFunc(519);
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		Sprite sprite = ModelLoader.FIRE_1.getSprite();
		minecraftClient.getTextureManager().bindTexture(sprite.getAtlas().getId());
		float f = sprite.getMinU();
		float g = sprite.getMaxU();
		float h = (f + g) / 2.0F;
		float i = sprite.getMinV();
		float j = sprite.getMaxV();
		float k = (i + j) / 2.0F;
		float l = sprite.getAnimationFrameDelta();
		float m = MathHelper.lerp(l, f, h);
		float n = MathHelper.lerp(l, g, h);
		float o = MathHelper.lerp(l, i, k);
		float p = MathHelper.lerp(l, j, k);
		float q = 1.0F;

		for (int r = 0; r < 2; r++) {
			matrixStack.push();
			float s = -0.5F;
			float t = 0.5F;
			float u = -0.5F;
			float v = 0.5F;
			float w = -0.5F;
			matrixStack.translate((double)((float)(-(r * 2 - 1)) * 0.24F), -0.3F, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)(r * 2 - 1) * 10.0F));
			Matrix4f matrix4f = matrixStack.peek().getModel();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
			bufferBuilder.vertex(matrix4f, -0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).texture(n, p).next();
			bufferBuilder.vertex(matrix4f, 0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).texture(m, p).next();
			bufferBuilder.vertex(matrix4f, 0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).texture(m, o).next();
			bufferBuilder.vertex(matrix4f, -0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).texture(n, o).next();
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			matrixStack.pop();
		}

		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.depthFunc(515);
	}
}
