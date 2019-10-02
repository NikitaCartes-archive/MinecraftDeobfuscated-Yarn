/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer
extends BlockEntityRenderer<StructureBlockBlockEntity> {
    public StructureBlockBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void method_3587(StructureBlockBlockEntity structureBlockBlockEntity, double d, double e, double f, float g, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i) {
        double r;
        double q;
        double p;
        double o;
        double n;
        double m;
        if (!MinecraftClient.getInstance().player.isCreativeLevelTwoOp() && !MinecraftClient.getInstance().player.isSpectator()) {
            return;
        }
        BlockPos blockPos = structureBlockBlockEntity.getOffset();
        BlockPos blockPos2 = structureBlockBlockEntity.getSize();
        if (blockPos2.getX() < 1 || blockPos2.getY() < 1 || blockPos2.getZ() < 1) {
            return;
        }
        if (structureBlockBlockEntity.getMode() != StructureBlockMode.SAVE && structureBlockBlockEntity.getMode() != StructureBlockMode.LOAD) {
            return;
        }
        double h = blockPos.getX();
        double j = blockPos.getZ();
        double k = blockPos.getY();
        double l = k + (double)blockPos2.getY();
        switch (structureBlockBlockEntity.getMirror()) {
            case LEFT_RIGHT: {
                m = blockPos2.getX();
                n = -blockPos2.getZ();
                break;
            }
            case FRONT_BACK: {
                m = -blockPos2.getX();
                n = blockPos2.getZ();
                break;
            }
            default: {
                m = blockPos2.getX();
                n = blockPos2.getZ();
            }
        }
        switch (structureBlockBlockEntity.getRotation()) {
            case CLOCKWISE_90: {
                o = n < 0.0 ? h : h + 1.0;
                p = m < 0.0 ? j + 1.0 : j;
                q = o - n;
                r = p + m;
                break;
            }
            case CLOCKWISE_180: {
                o = m < 0.0 ? h : h + 1.0;
                p = n < 0.0 ? j : j + 1.0;
                q = o - m;
                r = p - n;
                break;
            }
            case COUNTERCLOCKWISE_90: {
                o = n < 0.0 ? h + 1.0 : h;
                p = m < 0.0 ? j : j + 1.0;
                q = o + n;
                r = p - m;
                break;
            }
            default: {
                o = m < 0.0 ? h + 1.0 : h;
                p = n < 0.0 ? j + 1.0 : j;
                q = o + m;
                r = p + n;
            }
        }
        float s = 1.0f;
        float t = 0.9f;
        float u = 0.5f;
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.LINES);
        if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.shouldShowBoundingBox()) {
            WorldRenderer.method_22981(matrixStack, vertexConsumer, o, k, p, q, l, r, 0.9f, 0.9f, 0.9f, 1.0f, 0.5f, 0.5f, 0.5f);
        }
        if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE && structureBlockBlockEntity.shouldShowAir()) {
            this.method_3585(structureBlockBlockEntity, vertexConsumer, blockPos, true, matrixStack);
            this.method_3585(structureBlockBlockEntity, vertexConsumer, blockPos, false, matrixStack);
        }
    }

    private void method_3585(StructureBlockBlockEntity structureBlockBlockEntity, VertexConsumer vertexConsumer, BlockPos blockPos, boolean bl, MatrixStack matrixStack) {
        World blockView = structureBlockBlockEntity.getWorld();
        BlockPos blockPos2 = structureBlockBlockEntity.getPos();
        BlockPos blockPos3 = blockPos2.add(blockPos);
        for (BlockPos blockPos4 : BlockPos.iterate(blockPos3, blockPos3.add(structureBlockBlockEntity.getSize()).add(-1, -1, -1))) {
            boolean bl3;
            BlockState blockState = blockView.getBlockState(blockPos4);
            boolean bl2 = blockState.isAir();
            boolean bl4 = bl3 = blockState.getBlock() == Blocks.STRUCTURE_VOID;
            if (!bl2 && !bl3) continue;
            float f = bl2 ? 0.05f : 0.0f;
            double d = (float)(blockPos4.getX() - blockPos2.getX()) + 0.45f - f;
            double e = (float)(blockPos4.getY() - blockPos2.getY()) + 0.45f - f;
            double g = (float)(blockPos4.getZ() - blockPos2.getZ()) + 0.45f - f;
            double h = (float)(blockPos4.getX() - blockPos2.getX()) + 0.55f + f;
            double i = (float)(blockPos4.getY() - blockPos2.getY()) + 0.55f + f;
            double j = (float)(blockPos4.getZ() - blockPos2.getZ()) + 0.55f + f;
            if (bl) {
                WorldRenderer.method_22981(matrixStack, vertexConsumer, d, e, g, h, i, j, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f);
                continue;
            }
            if (bl2) {
                WorldRenderer.method_22981(matrixStack, vertexConsumer, d, e, g, h, i, j, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f);
                continue;
            }
            WorldRenderer.method_22981(matrixStack, vertexConsumer, d, e, g, h, i, j, 1.0f, 0.25f, 0.25f, 1.0f, 1.0f, 0.25f, 0.25f);
        }
    }

    public boolean method_3588(StructureBlockBlockEntity structureBlockBlockEntity) {
        return true;
    }
}

