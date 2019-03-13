package net.minecraft.client.render.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
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
import net.minecraft.world.ExtendedBlockView;

@Environment(EnvType.CLIENT)
public class BlockRenderManager implements SynchronousResourceReloadListener {
	private final BlockModels models;
	private final BlockModelRenderer field_4170;
	private final DynamicBlockRenderer dynamicRenderer = new DynamicBlockRenderer();
	private final FluidRenderer field_4167;
	private final Random random = new Random();

	public BlockRenderManager(BlockModels blockModels, BlockColorMap blockColorMap) {
		this.models = blockModels;
		this.field_4170 = new BlockModelRenderer(blockColorMap);
		this.field_4167 = new FluidRenderer();
	}

	public BlockModels getModels() {
		return this.models;
	}

	public void method_3354(BlockState blockState, BlockPos blockPos, Sprite sprite, ExtendedBlockView extendedBlockView) {
		if (blockState.getRenderType() == BlockRenderType.field_11458) {
			BakedModel bakedModel = this.models.method_3335(blockState);
			long l = blockState.method_11617(blockPos);
			BakedModel bakedModel2 = new BasicBakedModel.Builder(blockState, bakedModel, sprite, this.random, l).build();
			this.field_4170.method_3374(extendedBlockView, bakedModel2, blockState, blockPos, Tessellator.getInstance().getBufferBuilder(), true, this.random, l);
		}
	}

	public boolean method_3355(BlockState blockState, BlockPos blockPos, ExtendedBlockView extendedBlockView, BufferBuilder bufferBuilder, Random random) {
		try {
			BlockRenderType blockRenderType = blockState.getRenderType();
			if (blockRenderType == BlockRenderType.field_11455) {
				return false;
			} else {
				switch (blockRenderType) {
					case field_11458:
						return this.field_4170
							.method_3374(extendedBlockView, this.method_3349(blockState), blockState, blockPos, bufferBuilder, true, random, blockState.method_11617(blockPos));
					case field_11456:
						return false;
					default:
						return false;
				}
			}
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Tesselating block in world");
			CrashReportSection crashReportSection = crashReport.method_562("Block being tesselated");
			CrashReportSection.method_586(crashReportSection, blockPos, blockState);
			throw new CrashException(crashReport);
		}
	}

	public boolean method_3352(BlockPos blockPos, ExtendedBlockView extendedBlockView, BufferBuilder bufferBuilder, FluidState fluidState) {
		try {
			return this.field_4167.method_3347(extendedBlockView, blockPos, bufferBuilder, fluidState);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Tesselating liquid in world");
			CrashReportSection crashReportSection = crashReport.method_562("Block being tesselated");
			CrashReportSection.method_586(crashReportSection, blockPos, null);
			throw new CrashException(crashReport);
		}
	}

	public BlockModelRenderer method_3350() {
		return this.field_4170;
	}

	public BakedModel method_3349(BlockState blockState) {
		return this.models.method_3335(blockState);
	}

	public void renderDynamic(BlockState blockState, float f) {
		BlockRenderType blockRenderType = blockState.getRenderType();
		if (blockRenderType != BlockRenderType.field_11455) {
			switch (blockRenderType) {
				case field_11458:
					BakedModel bakedModel = this.method_3349(blockState);
					this.field_4170.method_3366(bakedModel, blockState, f, true);
					break;
				case field_11456:
					this.dynamicRenderer.render(blockState.getBlock(), f);
			}
		}
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		this.field_4167.onResourceReload();
	}
}
