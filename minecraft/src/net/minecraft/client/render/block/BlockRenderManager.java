package net.minecraft.client.render.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.RenderTypeBlock;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

@Environment(EnvType.CLIENT)
public class BlockRenderManager implements ResourceReloadListener {
	private final BlockModels models;
	private final BlockRenderer renderer;
	private final BlockModelRenderer modelRenderer = new BlockModelRenderer();
	private final BlockLiquidRenderer liquidRenderer;
	private final Random field_4169 = new Random();

	public BlockRenderManager(BlockModels blockModels, BlockColorMap blockColorMap) {
		this.models = blockModels;
		this.renderer = new BlockRenderer(blockColorMap);
		this.liquidRenderer = new BlockLiquidRenderer();
	}

	public BlockModels getModels() {
		return this.models;
	}

	public void tesselate(BlockState blockState, BlockPos blockPos, Sprite sprite, ExtendedBlockView extendedBlockView) {
		if (blockState.getRenderType() == RenderTypeBlock.MODEL) {
			BakedModel bakedModel = this.models.getModel(blockState);
			long l = blockState.getPosRandom(blockPos);
			BakedModel bakedModel2 = new BasicBakedModel.Builder(blockState, bakedModel, sprite, this.field_4169, l).build();
			this.renderer.tesselate(extendedBlockView, bakedModel2, blockState, blockPos, Tessellator.getInstance().getVertexBuffer(), true, this.field_4169, l);
		}
	}

	public boolean method_3355(BlockState blockState, BlockPos blockPos, ExtendedBlockView extendedBlockView, VertexBuffer vertexBuffer, Random random) {
		try {
			RenderTypeBlock renderTypeBlock = blockState.getRenderType();
			if (renderTypeBlock == RenderTypeBlock.NONE) {
				return false;
			} else {
				switch (renderTypeBlock) {
					case MODEL:
						return this.renderer
							.tesselate(extendedBlockView, this.getModel(blockState), blockState, blockPos, vertexBuffer, true, random, blockState.getPosRandom(blockPos));
					case field_11456:
						return false;
					default:
						return false;
				}
			}
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Tesselating block in world");
			CrashReportElement crashReportElement = crashReport.addElement("Block being tesselated");
			CrashReportElement.addBlockInfo(crashReportElement, blockPos, blockState);
			throw new CrashException(crashReport);
		}
	}

	public boolean method_3352(BlockPos blockPos, ExtendedBlockView extendedBlockView, VertexBuffer vertexBuffer, FluidState fluidState) {
		try {
			return this.liquidRenderer.method_3347(extendedBlockView, blockPos, vertexBuffer, fluidState);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Tesselating liquid in world");
			CrashReportElement crashReportElement = crashReport.addElement("Block being tesselated");
			CrashReportElement.addBlockInfo(crashReportElement, blockPos, null);
			throw new CrashException(crashReport);
		}
	}

	public BlockRenderer getRenderer() {
		return this.renderer;
	}

	public BakedModel getModel(BlockState blockState) {
		return this.models.getModel(blockState);
	}

	public void render(BlockState blockState, float f) {
		RenderTypeBlock renderTypeBlock = blockState.getRenderType();
		if (renderTypeBlock != RenderTypeBlock.NONE) {
			switch (renderTypeBlock) {
				case MODEL:
					BakedModel bakedModel = this.getModel(blockState);
					this.renderer.render(bakedModel, blockState, f, true);
					break;
				case field_11456:
					this.modelRenderer.renderBlockModel(blockState.getBlock(), f);
			}
		}
	}

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		this.liquidRenderer.onResourceReload();
	}
}
