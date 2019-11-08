/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.BakedModel;
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

@Environment(value=EnvType.CLIENT)
public class BlockRenderManager
implements SynchronousResourceReloadListener {
    private final BlockModels models;
    private final BlockModelRenderer blockModelRenderer;
    private final FluidRenderer fluidRenderer;
    private final Random random = new Random();
    private final BlockColors blockColors;

    public BlockRenderManager(BlockModels blockModels, BlockColors blockColors) {
        this.models = blockModels;
        this.blockColors = blockColors;
        this.blockModelRenderer = new BlockModelRenderer(this.blockColors);
        this.fluidRenderer = new FluidRenderer();
    }

    public BlockModels getModels() {
        return this.models;
    }

    public void renderDamage(BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, MatrixStack matrixStack, VertexConsumer vertexConsumer) {
        if (blockState.getRenderType() != BlockRenderType.MODEL) {
            return;
        }
        BakedModel bakedModel = this.models.getModel(blockState);
        long l = blockState.getRenderingSeed(blockPos);
        this.blockModelRenderer.render(blockRenderView, bakedModel, blockState, blockPos, matrixStack, vertexConsumer, true, this.random, l, OverlayTexture.DEFAULT_UV);
    }

    public boolean renderBlock(BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, MatrixStack matrixStack, VertexConsumer vertexConsumer, boolean bl, Random random) {
        try {
            BlockRenderType blockRenderType = blockState.getRenderType();
            if (blockRenderType != BlockRenderType.MODEL) {
                return false;
            }
            return this.blockModelRenderer.render(blockRenderView, this.getModel(blockState), blockState, blockPos, matrixStack, vertexConsumer, bl, random, blockState.getRenderingSeed(blockPos), OverlayTexture.DEFAULT_UV);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Tesselating block in world");
            CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
            throw new CrashException(crashReport);
        }
    }

    public boolean renderFluid(BlockPos blockPos, BlockRenderView blockRenderView, VertexConsumer vertexConsumer, FluidState fluidState) {
        try {
            return this.fluidRenderer.render(blockRenderView, blockPos, vertexConsumer, fluidState);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Tesselating liquid in world");
            CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, blockPos, null);
            throw new CrashException(crashReport);
        }
    }

    public BlockModelRenderer getModelRenderer() {
        return this.blockModelRenderer;
    }

    public BakedModel getModel(BlockState blockState) {
        return this.models.getModel(blockState);
    }

    public void renderBlockAsEntity(BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockRenderType blockRenderType = blockState.getRenderType();
        if (blockRenderType == BlockRenderType.INVISIBLE) {
            return;
        }
        switch (blockRenderType) {
            case MODEL: {
                BakedModel bakedModel = this.getModel(blockState);
                int k = this.blockColors.getColor(blockState, null, null, 0);
                float f = (float)(k >> 16 & 0xFF) / 255.0f;
                float g = (float)(k >> 8 & 0xFF) / 255.0f;
                float h = (float)(k & 0xFF) / 255.0f;
                this.blockModelRenderer.render(matrixStack.peek(), vertexConsumerProvider.getBuffer(RenderLayers.getEntityBlockLayer(blockState)), blockState, bakedModel, f, g, h, i, j);
                break;
            }
            case ENTITYBLOCK_ANIMATED: {
                BuiltinModelItemRenderer.INSTANCE.render(new ItemStack(blockState.getBlock()), matrixStack, vertexConsumerProvider, i, j);
            }
        }
    }

    @Override
    public void apply(ResourceManager resourceManager) {
        this.fluidRenderer.onResourceReload();
    }
}

