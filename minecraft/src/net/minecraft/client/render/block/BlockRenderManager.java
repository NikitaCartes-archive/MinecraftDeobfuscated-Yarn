package net.minecraft.client.render.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.world.BlockRenderView;

@Environment(EnvType.CLIENT)
public class BlockRenderManager implements SynchronousResourceReloadListener {
	private final BlockModels models;
	private final BlockModelRenderer renderer;
	private final FluidRenderer fluidRenderer;
	private final Random random = new Random();
	private final BlockColors field_20987;

	public BlockRenderManager(BlockModels blockModels, BlockColors blockColors) {
		this.models = blockModels;
		this.field_20987 = blockColors;
		this.renderer = new BlockModelRenderer(this.field_20987);
		this.fluidRenderer = new FluidRenderer();
	}

	public BlockModels getModels() {
		return this.models;
	}

	public void tesselateDamage(BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, MatrixStack matrixStack, VertexConsumer vertexConsumer) {
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			BakedModel bakedModel = this.models.getModel(blockState);
			long l = blockState.getRenderingSeed(blockPos);
			this.renderer.tesselate(blockRenderView, bakedModel, blockState, blockPos, matrixStack, vertexConsumer, true, this.random, l);
		}
	}

	public boolean tesselateBlock(
		BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, MatrixStack matrixStack, VertexConsumer vertexConsumer, boolean bl, Random random
	) {
		try {
			BlockRenderType blockRenderType = blockState.getRenderType();
			return blockRenderType != BlockRenderType.MODEL
				? false
				: this.renderer
					.tesselate(
						blockRenderView, this.getModel(blockState), blockState, blockPos, matrixStack, vertexConsumer, bl, random, blockState.getRenderingSeed(blockPos)
					);
		} catch (Throwable var11) {
			CrashReport crashReport = CrashReport.create(var11, "Tesselating block in world");
			CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
			throw new CrashException(crashReport);
		}
	}

	public boolean tesselateFluid(BlockPos blockPos, BlockRenderView blockRenderView, VertexConsumer vertexConsumer, FluidState fluidState) {
		try {
			return this.fluidRenderer.tesselate(blockRenderView, blockPos, vertexConsumer, fluidState);
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

	public void renderDynamic(BlockState blockState, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, int j, int k) {
		BlockRenderType blockRenderType = blockState.getRenderType();
		if (blockRenderType != BlockRenderType.INVISIBLE) {
			switch (blockRenderType) {
				case MODEL:
					BakedModel bakedModel = this.getModel(blockState);
					matrixStack.push();
					matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(90.0F, true));
					int l = this.field_20987.getColorMultiplier(blockState, null, null, 0);
					float f = (float)(l >> 16 & 0xFF) / 255.0F;
					float g = (float)(l >> 8 & 0xFF) / 255.0F;
					float h = (float)(l & 0xFF) / 255.0F;
					this.renderer.render(matrixStack.peek(), layeredVertexConsumerStorage.getBuffer(RenderLayer.method_22715(blockState)), blockState, bakedModel, f, g, h, i);
					matrixStack.pop();
					break;
				case ENTITYBLOCK_ANIMATED:
					matrixStack.push();
					matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(90.0F, true));
					ItemDynamicRenderer.INSTANCE.render(new ItemStack(blockState.getBlock()), matrixStack, layeredVertexConsumerStorage, i);
					matrixStack.pop();
			}
		}
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		this.fluidRenderer.onResourceReload();
	}
}
