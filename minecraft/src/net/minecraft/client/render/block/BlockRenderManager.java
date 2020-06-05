package net.minecraft.client.render.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
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
	private final BlockModelRenderer blockModelRenderer;
	private final FluidRenderer fluidRenderer;
	private final Random random = new Random();
	private final BlockColors blockColors;

	public BlockRenderManager(BlockModels models, BlockColors blockColors) {
		this.models = models;
		this.blockColors = blockColors;
		this.blockModelRenderer = new BlockModelRenderer(this.blockColors);
		this.fluidRenderer = new FluidRenderer();
	}

	public BlockModels getModels() {
		return this.models;
	}

	public void renderDamage(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix, VertexConsumer vertexConsumer) {
		if (state.getRenderType() == BlockRenderType.MODEL) {
			BakedModel bakedModel = this.models.getModel(state);
			long l = state.getRenderingSeed(pos);
			this.blockModelRenderer.render(world, bakedModel, state, pos, matrix, vertexConsumer, true, this.random, l, OverlayTexture.DEFAULT_UV);
		}
	}

	public boolean renderBlock(
		BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix, VertexConsumer vertexConsumer, boolean cull, Random random
	) {
		try {
			BlockRenderType blockRenderType = state.getRenderType();
			return blockRenderType != BlockRenderType.MODEL
				? false
				: this.blockModelRenderer
					.render(world, this.getModel(state), state, pos, matrix, vertexConsumer, cull, random, state.getRenderingSeed(pos), OverlayTexture.DEFAULT_UV);
		} catch (Throwable var11) {
			CrashReport crashReport = CrashReport.create(var11, "Tesselating block in world");
			CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, pos, state);
			throw new CrashException(crashReport);
		}
	}

	public boolean renderFluid(BlockPos pos, BlockRenderView blockRenderView, VertexConsumer vertexConsumer, FluidState fluidState) {
		try {
			return this.fluidRenderer.render(blockRenderView, pos, vertexConsumer, fluidState);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Tesselating liquid in world");
			CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, pos, null);
			throw new CrashException(crashReport);
		}
	}

	public BlockModelRenderer getModelRenderer() {
		return this.blockModelRenderer;
	}

	public BakedModel getModel(BlockState state) {
		return this.models.getModel(state);
	}

	public void renderBlockAsEntity(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, int overlay) {
		BlockRenderType blockRenderType = state.getRenderType();
		if (blockRenderType != BlockRenderType.INVISIBLE) {
			switch (blockRenderType) {
				case MODEL:
					BakedModel bakedModel = this.getModel(state);
					int i = this.blockColors.getColor(state, null, null, 0);
					float f = (float)(i >> 16 & 0xFF) / 255.0F;
					float g = (float)(i >> 8 & 0xFF) / 255.0F;
					float h = (float)(i & 0xFF) / 255.0F;
					this.blockModelRenderer
						.render(matrices.peek(), vertexConsumer.getBuffer(RenderLayers.getEntityBlockLayer(state, false)), state, bakedModel, f, g, h, light, overlay);
					break;
				case ENTITYBLOCK_ANIMATED:
					BuiltinModelItemRenderer.INSTANCE.render(new ItemStack(state.getBlock()), ModelTransformation.Mode.NONE, matrices, vertexConsumer, light, overlay);
			}
		}
	}

	@Override
	public void apply(ResourceManager manager) {
		this.fluidRenderer.onResourceReload();
	}
}
