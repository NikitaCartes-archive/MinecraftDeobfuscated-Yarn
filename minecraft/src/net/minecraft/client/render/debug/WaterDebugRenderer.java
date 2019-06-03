package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.ViewableWorld;

@Environment(EnvType.CLIENT)
public class WaterDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public WaterDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(long l) {
		Camera camera = this.client.gameRenderer.getCamera();
		double d = camera.getPos().x;
		double e = camera.getPos().y;
		double f = camera.getPos().z;
		BlockPos blockPos = this.client.player.getBlockPos();
		ViewableWorld viewableWorld = this.client.player.world;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
		GlStateManager.disableTexture();
		GlStateManager.lineWidth(6.0F);

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			FluidState fluidState = viewableWorld.getFluidState(blockPos2);
			if (fluidState.matches(FluidTags.field_15517)) {
				double g = (double)((float)blockPos2.getY() + fluidState.getHeight(viewableWorld, blockPos2));
				DebugRenderer.method_19695(
					new Box(
							(double)((float)blockPos2.getX() + 0.01F),
							(double)((float)blockPos2.getY() + 0.01F),
							(double)((float)blockPos2.getZ() + 0.01F),
							(double)((float)blockPos2.getX() + 0.99F),
							g,
							(double)((float)blockPos2.getZ() + 0.99F)
						)
						.offset(-d, -e, -f),
					1.0F,
					1.0F,
					1.0F,
					0.2F
				);
			}
		}

		for (BlockPos blockPos2x : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			FluidState fluidState = viewableWorld.getFluidState(blockPos2x);
			if (fluidState.matches(FluidTags.field_15517)) {
				DebugRenderer.method_3714(
					String.valueOf(fluidState.getLevel()),
					(double)blockPos2x.getX() + 0.5,
					(double)((float)blockPos2x.getY() + fluidState.getHeight(viewableWorld, blockPos2x)),
					(double)blockPos2x.getZ() + 0.5,
					-16777216
				);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
