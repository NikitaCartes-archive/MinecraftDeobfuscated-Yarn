package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.WorldView;

@Environment(EnvType.CLIENT)
public class WaterDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public WaterDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		BlockPos blockPos = this.client.player.getBlockPos();
		WorldView worldView = this.client.player.getWorld();

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			FluidState fluidState = worldView.getFluidState(blockPos2);
			if (fluidState.isIn(FluidTags.WATER)) {
				double d = (double)((float)blockPos2.getY() + fluidState.getHeight(worldView, blockPos2));
				DebugRenderer.drawBox(
					matrices,
					vertexConsumers,
					new Box(
							(double)((float)blockPos2.getX() + 0.01F),
							(double)((float)blockPos2.getY() + 0.01F),
							(double)((float)blockPos2.getZ() + 0.01F),
							(double)((float)blockPos2.getX() + 0.99F),
							d,
							(double)((float)blockPos2.getZ() + 0.99F)
						)
						.offset(-cameraX, -cameraY, -cameraZ),
					0.0F,
					1.0F,
					0.0F,
					0.15F
				);
			}
		}

		for (BlockPos blockPos2x : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			FluidState fluidState = worldView.getFluidState(blockPos2x);
			if (fluidState.isIn(FluidTags.WATER)) {
				DebugRenderer.drawString(
					matrices,
					vertexConsumers,
					String.valueOf(fluidState.getLevel()),
					(double)blockPos2x.getX() + 0.5,
					(double)((float)blockPos2x.getY() + fluidState.getHeight(worldView, blockPos2x)),
					(double)blockPos2x.getZ() + 0.5,
					Colors.BLACK
				);
			}
		}
	}
}
