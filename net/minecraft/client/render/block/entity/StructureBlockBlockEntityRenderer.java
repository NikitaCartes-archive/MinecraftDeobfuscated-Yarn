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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer
implements BlockEntityRenderer<StructureBlockBlockEntity> {
    public StructureBlockBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(StructureBlockBlockEntity structureBlockBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        double p;
        double o;
        double n;
        double m;
        double l;
        double k;
        if (!MinecraftClient.getInstance().player.isCreativeLevelTwoOp() && !MinecraftClient.getInstance().player.isSpectator()) {
            return;
        }
        BlockPos blockPos = structureBlockBlockEntity.getOffset();
        Vec3i vec3i = structureBlockBlockEntity.getSize();
        if (vec3i.getX() < 1 || vec3i.getY() < 1 || vec3i.getZ() < 1) {
            return;
        }
        if (structureBlockBlockEntity.getMode() != StructureBlockMode.SAVE && structureBlockBlockEntity.getMode() != StructureBlockMode.LOAD) {
            return;
        }
        double d = blockPos.getX();
        double e = blockPos.getZ();
        double g = blockPos.getY();
        double h = g + (double)vec3i.getY();
        switch (structureBlockBlockEntity.getMirror()) {
            case LEFT_RIGHT: {
                k = vec3i.getX();
                l = -vec3i.getZ();
                break;
            }
            case FRONT_BACK: {
                k = -vec3i.getX();
                l = vec3i.getZ();
                break;
            }
            default: {
                k = vec3i.getX();
                l = vec3i.getZ();
            }
        }
        switch (structureBlockBlockEntity.getRotation()) {
            case CLOCKWISE_90: {
                m = l < 0.0 ? d : d + 1.0;
                n = k < 0.0 ? e + 1.0 : e;
                o = m - l;
                p = n + k;
                break;
            }
            case CLOCKWISE_180: {
                m = k < 0.0 ? d : d + 1.0;
                n = l < 0.0 ? e : e + 1.0;
                o = m - k;
                p = n - l;
                break;
            }
            case COUNTERCLOCKWISE_90: {
                m = l < 0.0 ? d + 1.0 : d;
                n = k < 0.0 ? e : e + 1.0;
                o = m + l;
                p = n - k;
                break;
            }
            default: {
                m = k < 0.0 ? d + 1.0 : d;
                n = l < 0.0 ? e + 1.0 : e;
                o = m + k;
                p = n + l;
            }
        }
        float q = 1.0f;
        float r = 0.9f;
        float s = 0.5f;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
        if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.shouldShowBoundingBox()) {
            WorldRenderer.drawBox(matrixStack, vertexConsumer, m, g, n, o, h, p, 0.9f, 0.9f, 0.9f, 1.0f, 0.5f, 0.5f, 0.5f);
        }
        if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE && structureBlockBlockEntity.shouldShowAir()) {
            this.renderInvisibleBlocks(structureBlockBlockEntity, vertexConsumer, blockPos, true, matrixStack);
            this.renderInvisibleBlocks(structureBlockBlockEntity, vertexConsumer, blockPos, false, matrixStack);
        }
    }

    private void renderInvisibleBlocks(StructureBlockBlockEntity entity, VertexConsumer vertices, BlockPos pos, boolean bl, MatrixStack matrices) {
        World blockView = entity.getWorld();
        BlockPos blockPos = entity.getPos();
        BlockPos blockPos2 = blockPos.add(pos);
        for (BlockPos blockPos3 : BlockPos.iterate(blockPos2, blockPos2.add(entity.getSize()).add(-1, -1, -1))) {
            BlockState blockState = blockView.getBlockState(blockPos3);
            boolean bl2 = blockState.isAir();
            boolean bl3 = blockState.isOf(Blocks.STRUCTURE_VOID);
            if (!bl2 && !bl3) continue;
            float f = bl2 ? 0.05f : 0.0f;
            double d = (float)(blockPos3.getX() - blockPos.getX()) + 0.45f - f;
            double e = (float)(blockPos3.getY() - blockPos.getY()) + 0.45f - f;
            double g = (float)(blockPos3.getZ() - blockPos.getZ()) + 0.45f - f;
            double h = (float)(blockPos3.getX() - blockPos.getX()) + 0.55f + f;
            double i = (float)(blockPos3.getY() - blockPos.getY()) + 0.55f + f;
            double j = (float)(blockPos3.getZ() - blockPos.getZ()) + 0.55f + f;
            if (bl) {
                WorldRenderer.drawBox(matrices, vertices, d, e, g, h, i, j, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f);
                continue;
            }
            if (bl2) {
                WorldRenderer.drawBox(matrices, vertices, d, e, g, h, i, j, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f);
                continue;
            }
            WorldRenderer.drawBox(matrices, vertices, d, e, g, h, i, j, 1.0f, 0.25f, 0.25f, 1.0f, 1.0f, 0.25f, 0.25f);
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(StructureBlockBlockEntity structureBlockBlockEntity) {
        return true;
    }

    @Override
    public int method_33893() {
        return 96;
    }
}

