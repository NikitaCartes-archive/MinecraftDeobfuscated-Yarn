package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PistonBlockEntityRenderer extends BlockEntityRenderer<PistonBlockEntity> {
	private final BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();

	public void method_3576(PistonBlockEntity pistonBlockEntity, double d, double e, double f, float g, int i) {
		BlockPos blockPos = pistonBlockEntity.getPos().offset(pistonBlockEntity.method_11506().getOpposite());
		BlockState blockState = pistonBlockEntity.getPushedBlock();
		if (!blockState.isAir() && !(pistonBlockEntity.getProgress(g) >= 1.0F)) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			GuiLighting.disable();
			RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
			RenderSystem.enableBlend();
			RenderSystem.disableCull();
			if (MinecraftClient.isAmbientOcclusionEnabled()) {
				RenderSystem.shadeModel(7425);
			} else {
				RenderSystem.shadeModel(7424);
			}

			BlockModelRenderer.enableBrightnessCache();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
			bufferBuilder.setOffset(
				d - (double)blockPos.getX() + (double)pistonBlockEntity.getRenderOffsetX(g),
				e - (double)blockPos.getY() + (double)pistonBlockEntity.getRenderOffsetY(g),
				f - (double)blockPos.getZ() + (double)pistonBlockEntity.getRenderOffsetZ(g)
			);
			World world = this.getWorld();
			if (blockState.getBlock() == Blocks.PISTON_HEAD && pistonBlockEntity.getProgress(g) <= 4.0F) {
				blockState = blockState.with(PistonHeadBlock.SHORT, Boolean.valueOf(true));
				this.method_3575(blockPos, blockState, bufferBuilder, world, false);
			} else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
				PistonType pistonType = blockState.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState blockState2 = Blocks.PISTON_HEAD
					.getDefaultState()
					.with(PistonHeadBlock.TYPE, pistonType)
					.with(PistonHeadBlock.FACING, blockState.get(PistonBlock.FACING));
				blockState2 = blockState2.with(PistonHeadBlock.SHORT, Boolean.valueOf(pistonBlockEntity.getProgress(g) >= 0.5F));
				this.method_3575(blockPos, blockState2, bufferBuilder, world, false);
				BlockPos blockPos2 = blockPos.offset(pistonBlockEntity.method_11506());
				bufferBuilder.setOffset(d - (double)blockPos2.getX(), e - (double)blockPos2.getY(), f - (double)blockPos2.getZ());
				blockState = blockState.with(PistonBlock.EXTENDED, Boolean.valueOf(true));
				this.method_3575(blockPos2, blockState, bufferBuilder, world, true);
			} else {
				this.method_3575(blockPos, blockState, bufferBuilder, world, false);
			}

			bufferBuilder.setOffset(0.0, 0.0, 0.0);
			tessellator.draw();
			BlockModelRenderer.disableBrightnessCache();
			GuiLighting.enable();
		}
	}

	private boolean method_3575(BlockPos blockPos, BlockState blockState, BufferBuilder bufferBuilder, World world, boolean bl) {
		return this.manager
			.getModelRenderer()
			.tesselate(world, this.manager.getModel(blockState), blockState, blockPos, bufferBuilder, bl, new Random(), blockState.getRenderingSeed(blockPos));
	}
}
