package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Renderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;

@Environment(EnvType.CLIENT)
public class WaterDebugRenderer implements RenderDebug.DebugRenderer {
	private final MinecraftClient client;
	private PlayerEntity player;
	private double playerX;
	private double playerY;
	private double playerZ;

	public WaterDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(float f, long l) {
		this.player = this.client.player;
		this.playerX = MathHelper.lerp((double)f, this.player.prevRenderX, this.player.x);
		this.playerY = MathHelper.lerp((double)f, this.player.prevRenderY, this.player.y);
		this.playerZ = MathHelper.lerp((double)f, this.player.prevRenderZ, this.player.z);
		BlockPos blockPos = this.client.player.getPos();
		ViewableWorld viewableWorld = this.client.player.world;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
		GlStateManager.disableTexture();
		GlStateManager.lineWidth(6.0F);

		for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			FluidState fluidState = viewableWorld.getFluidState(blockPos2);
			if (fluidState.matches(FluidTags.field_15517)) {
				double d = (double)((float)blockPos2.getY() + fluidState.method_15763());
				Renderer.renderCuboidOutline(
					new BoundingBox(
							(double)((float)blockPos2.getX() + 0.01F),
							(double)((float)blockPos2.getY() + 0.01F),
							(double)((float)blockPos2.getZ() + 0.01F),
							(double)((float)blockPos2.getX() + 0.99F),
							d,
							(double)((float)blockPos2.getZ() + 0.99F)
						)
						.offset(-this.playerX, -this.playerY, -this.playerZ),
					1.0F,
					1.0F,
					1.0F,
					0.2F
				);
			}
		}

		for (BlockPos blockPos2x : BlockPos.iterateBoxPositions(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			FluidState fluidState = viewableWorld.getFluidState(blockPos2x);
			if (fluidState.matches(FluidTags.field_15517)) {
				RenderDebug.method_3714(
					String.valueOf(fluidState.method_15761()),
					(double)blockPos2x.getX() + 0.5,
					(double)((float)blockPos2x.getY() + fluidState.method_15763()),
					(double)blockPos2x.getZ() + 0.5,
					f,
					-16777216
				);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
