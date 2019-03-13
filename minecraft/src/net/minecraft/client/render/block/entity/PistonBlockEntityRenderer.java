package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PistonBlockEntityRenderer extends BlockEntityRenderer<PistonBlockEntity> {
	private final BlockRenderManager manager = MinecraftClient.getInstance().method_1541();

	public void method_3576(PistonBlockEntity pistonBlockEntity, double d, double e, double f, float g, int i) {
		BlockPos blockPos = pistonBlockEntity.method_11016().method_10093(pistonBlockEntity.method_11506().getOpposite());
		BlockState blockState = pistonBlockEntity.method_11495();
		if (!blockState.isAir() && !(pistonBlockEntity.getProgress(g) >= 1.0F)) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			this.method_3566(SpriteAtlasTexture.field_5275);
			GuiLighting.disable();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			if (MinecraftClient.isAmbientOcclusionEnabled()) {
				GlStateManager.shadeModel(7425);
			} else {
				GlStateManager.shadeModel(7424);
			}

			bufferBuilder.method_1328(7, VertexFormats.field_1582);
			bufferBuilder.setOffset(
				d - (double)blockPos.getX() + (double)pistonBlockEntity.getRenderOffsetX(g),
				e - (double)blockPos.getY() + (double)pistonBlockEntity.getRenderOffsetY(g),
				f - (double)blockPos.getZ() + (double)pistonBlockEntity.getRenderOffsetZ(g)
			);
			World world = this.getWorld();
			if (blockState.getBlock() == Blocks.field_10379 && pistonBlockEntity.getProgress(g) <= 4.0F) {
				blockState = blockState.method_11657(PistonHeadBlock.field_12227, Boolean.valueOf(true));
				this.method_3575(blockPos, blockState, bufferBuilder, world, false);
			} else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
				PistonType pistonType = blockState.getBlock() == Blocks.field_10615 ? PistonType.field_12634 : PistonType.field_12637;
				BlockState blockState2 = Blocks.field_10379
					.method_9564()
					.method_11657(PistonHeadBlock.field_12224, pistonType)
					.method_11657(PistonHeadBlock.field_10927, blockState.method_11654(PistonBlock.field_10927));
				blockState2 = blockState2.method_11657(PistonHeadBlock.field_12227, Boolean.valueOf(pistonBlockEntity.getProgress(g) >= 0.5F));
				this.method_3575(blockPos, blockState2, bufferBuilder, world, false);
				BlockPos blockPos2 = blockPos.method_10093(pistonBlockEntity.method_11506());
				bufferBuilder.setOffset(d - (double)blockPos2.getX(), e - (double)blockPos2.getY(), f - (double)blockPos2.getZ());
				blockState = blockState.method_11657(PistonBlock.field_12191, Boolean.valueOf(true));
				this.method_3575(blockPos2, blockState, bufferBuilder, world, true);
			} else {
				this.method_3575(blockPos, blockState, bufferBuilder, world, false);
			}

			bufferBuilder.setOffset(0.0, 0.0, 0.0);
			tessellator.draw();
			GuiLighting.enable();
		}
	}

	private boolean method_3575(BlockPos blockPos, BlockState blockState, BufferBuilder bufferBuilder, World world, boolean bl) {
		return this.manager
			.method_3350()
			.method_3374(world, this.manager.method_3349(blockState), blockState, blockPos, bufferBuilder, bl, new Random(), blockState.method_11617(blockPos));
	}
}
