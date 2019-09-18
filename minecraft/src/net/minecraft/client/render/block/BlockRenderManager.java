package net.minecraft.client.render.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@Environment(EnvType.CLIENT)
public class BlockRenderManager implements SynchronousResourceReloadListener {
	private final BlockModels models;
	private final BlockModelRenderer renderer;
	private final DynamicBlockRenderer dynamicRenderer = new DynamicBlockRenderer();
	private final FluidRenderer fluidRenderer;
	private final Random random = new Random();

	public BlockRenderManager(BlockModels blockModels, BlockColors blockColors) {
		this.models = blockModels;
		this.renderer = new BlockModelRenderer(blockColors);
		this.fluidRenderer = new FluidRenderer();
	}

	public BlockModels getModels() {
		return this.models;
	}

	public void tesselateDamage(BufferBuilder bufferBuilder, BlockState blockState, BlockPos blockPos, Sprite sprite, BlockRenderView blockRenderView) {
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			BakedModel bakedModel = this.models.getModel(blockState);
			long l = blockState.getRenderingSeed(blockPos);
			BakedModel bakedModel2 = new BasicBakedModel.Builder(blockState, bakedModel, sprite, this.random, l).build();
			this.renderer.tesselate(blockRenderView, bakedModel2, blockState, blockPos, bufferBuilder, true, this.random, l);
		}
	}

	public boolean tesselateBlock(BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, BufferBuilder bufferBuilder, Random random) {
		try {
			BlockRenderType blockRenderType = blockState.getRenderType();
			if (blockRenderType == BlockRenderType.INVISIBLE) {
				return false;
			} else {
				switch (blockRenderType) {
					case MODEL:
						return this.renderer
							.tesselate(blockRenderView, this.getModel(blockState), blockState, blockPos, bufferBuilder, true, random, blockState.getRenderingSeed(blockPos));
					case ENTITYBLOCK_ANIMATED:
						return false;
					default:
						return false;
				}
			}
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Tesselating block in world");
			CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
			throw new CrashException(crashReport);
		}
	}

	public boolean tesselateFluid(BlockPos blockPos, BlockRenderView blockRenderView, BufferBuilder bufferBuilder, FluidState fluidState) {
		try {
			return this.fluidRenderer.tesselate(blockRenderView, blockPos, bufferBuilder, fluidState);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Tesselating liquid in world");
			CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, blockPos, null);
			throw new CrashException(crashReport);
		}
	}

	public BlockModelRenderer getModelRenderer() {
		return this.renderer;
	}

	public BakedModel getModel(BlockState blockState) {
		return this.models.getModel(blockState);
	}

	public void renderDynamic(BlockState blockState, float f) {
		BlockRenderType blockRenderType = blockState.getRenderType();
		if (blockRenderType != BlockRenderType.INVISIBLE) {
			switch (blockRenderType) {
				case MODEL:
					BakedModel bakedModel = this.getModel(blockState);
					this.renderer.render(bakedModel, blockState, f, true);
					break;
				case ENTITYBLOCK_ANIMATED:
					this.dynamicRenderer.render(blockState.getBlock(), f);
			}
		}
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		this.fluidRenderer.onResourceReload();
	}
}
