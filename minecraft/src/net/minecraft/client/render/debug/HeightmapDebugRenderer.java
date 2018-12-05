package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;

@Environment(EnvType.CLIENT)
public class HeightmapDebugRenderer implements RenderDebug.DebugRenderer {
	private final MinecraftClient client;

	public HeightmapDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(float f, long l) {
		PlayerEntity playerEntity = this.client.player;
		IWorld iWorld = this.client.world;
		double d = MathHelper.lerp((double)f, playerEntity.prevRenderX, playerEntity.x);
		double e = MathHelper.lerp((double)f, playerEntity.prevRenderY, playerEntity.y);
		double g = MathHelper.lerp((double)f, playerEntity.prevRenderZ, playerEntity.z);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.disableTexture();
		BlockPos blockPos = new BlockPos(playerEntity.x, 0.0, playerEntity.z);
		Iterable<BlockPos> iterable = BlockPos.iterateBoxPositions(blockPos.add(-40, 0, -40), blockPos.add(40, 0, 40));
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(5, VertexFormats.POSITION_COLOR);

		for (BlockPos blockPos2 : iterable) {
			int i = iWorld.getTop(Heightmap.Type.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ());
			if (iWorld.getBlockState(blockPos2.add(0, i, 0).down()).isAir()) {
				Renderer.renderCuboid(
					vertexBuffer,
					(double)((float)blockPos2.getX() + 0.25F) - d,
					(double)i - e,
					(double)((float)blockPos2.getZ() + 0.25F) - g,
					(double)((float)blockPos2.getX() + 0.75F) - d,
					(double)i + 0.09375 - e,
					(double)((float)blockPos2.getZ() + 0.75F) - g,
					0.0F,
					0.0F,
					1.0F,
					0.5F
				);
			} else {
				Renderer.renderCuboid(
					vertexBuffer,
					(double)((float)blockPos2.getX() + 0.25F) - d,
					(double)i - e,
					(double)((float)blockPos2.getZ() + 0.25F) - g,
					(double)((float)blockPos2.getX() + 0.75F) - d,
					(double)i + 0.09375 - e,
					(double)((float)blockPos2.getZ() + 0.75F) - g,
					0.0F,
					1.0F,
					0.0F,
					0.5F
				);
			}
		}

		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
