package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4184;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.ViewableWorld;

@Environment(EnvType.CLIENT)
public class WaterDebugRenderer implements DebugRenderer.DebugRenderer {
	private final MinecraftClient client;

	public WaterDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(long l) {
		class_4184 lv = this.client.field_1773.method_19418();
		double d = lv.method_19326().x;
		double e = lv.method_19326().y;
		double f = lv.method_19326().z;
		BlockPos blockPos = this.client.field_1724.method_5704();
		ViewableWorld viewableWorld = this.client.field_1724.field_6002;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
		GlStateManager.disableTexture();
		GlStateManager.lineWidth(6.0F);

		for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			FluidState fluidState = viewableWorld.method_8316(blockPos2);
			if (fluidState.method_15767(FluidTags.field_15517)) {
				double g = (double)((float)blockPos2.getY() + fluidState.method_15763(viewableWorld, blockPos2));
				WorldRenderer.drawBox(
					new BoundingBox(
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

		for (BlockPos blockPos2x : BlockPos.iterateBoxPositions(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			FluidState fluidState = viewableWorld.method_8316(blockPos2x);
			if (fluidState.method_15767(FluidTags.field_15517)) {
				DebugRenderer.method_3714(
					String.valueOf(fluidState.getLevel()),
					(double)blockPos2x.getX() + 0.5,
					(double)((float)blockPos2x.getY() + fluidState.method_15763(viewableWorld, blockPos2x)),
					(double)blockPos2x.getZ() + 0.5,
					-16777216
				);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
