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
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
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
        double o;
        double n;
        double m;
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
        double l = switch (structureBlockBlockEntity.getMirror()) {
            case BlockMirror.LEFT_RIGHT -> {
                k = vec3i.getX();
                yield -vec3i.getZ();
            }
            case BlockMirror.FRONT_BACK -> {
                k = -vec3i.getX();
                yield vec3i.getZ();
            }
            default -> {
                k = vec3i.getX();
                yield vec3i.getZ();
            }
        };
        double p = switch (structureBlockBlockEntity.getRotation()) {
            case BlockRotation.CLOCKWISE_90 -> {
                m = l < 0.0 ? d : d + 1.0;
                n = k < 0.0 ? e + 1.0 : e;
                o = m - l;
                yield n + k;
            }
            case BlockRotation.CLOCKWISE_180 -> {
                m = k < 0.0 ? d : d + 1.0;
                n = l < 0.0 ? e : e + 1.0;
                o = m - k;
                yield n - l;
            }
            case BlockRotation.COUNTERCLOCKWISE_90 -> {
                m = l < 0.0 ? d + 1.0 : d;
                n = k < 0.0 ? e : e + 1.0;
                o = m + l;
                yield n - k;
            }
            default -> {
                m = k < 0.0 ? d + 1.0 : d;
                n = l < 0.0 ? e + 1.0 : e;
                o = m + k;
                yield n + l;
            }
        };
        float q = 1.0f;
        float r = 0.9f;
        float s = 0.5f;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
        if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.shouldShowBoundingBox()) {
            WorldRenderer.drawBox(matrixStack, vertexConsumer, m, g, n, o, h, p, 0.9f, 0.9f, 0.9f, 1.0f, 0.5f, 0.5f, 0.5f);
        }
        if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE && structureBlockBlockEntity.shouldShowAir()) {
            this.renderInvisibleBlocks(structureBlockBlockEntity, vertexConsumer, blockPos, matrixStack);
        }
    }

    private void renderInvisibleBlocks(StructureBlockBlockEntity entity, VertexConsumer vertices, BlockPos pos, MatrixStack matrixStack) {
        World blockView = entity.getWorld();
        BlockPos blockPos = entity.getPos();
        BlockPos blockPos2 = blockPos.add(pos);
        for (BlockPos blockPos3 : BlockPos.iterate(blockPos2, blockPos2.add(entity.getSize()).add(-1, -1, -1))) {
            boolean bl5;
            BlockState blockState = blockView.getBlockState(blockPos3);
            boolean bl = blockState.isAir();
            boolean bl2 = blockState.isOf(Blocks.STRUCTURE_VOID);
            boolean bl3 = blockState.isOf(Blocks.BARRIER);
            boolean bl4 = blockState.isOf(Blocks.LIGHT);
            boolean bl6 = bl5 = bl2 || bl3 || bl4;
            if (!bl && !bl5) continue;
            float f = bl ? 0.05f : 0.0f;
            double d = (float)(blockPos3.getX() - blockPos.getX()) + 0.45f - f;
            double e = (float)(blockPos3.getY() - blockPos.getY()) + 0.45f - f;
            double g = (float)(blockPos3.getZ() - blockPos.getZ()) + 0.45f - f;
            double h = (float)(blockPos3.getX() - blockPos.getX()) + 0.55f + f;
            double i = (float)(blockPos3.getY() - blockPos.getY()) + 0.55f + f;
            double j = (float)(blockPos3.getZ() - blockPos.getZ()) + 0.55f + f;
            if (bl) {
                WorldRenderer.drawBox(matrixStack, vertices, d, e, g, h, i, j, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f);
                continue;
            }
            if (bl2) {
                WorldRenderer.drawBox(matrixStack, vertices, d, e, g, h, i, j, 1.0f, 0.75f, 0.75f, 1.0f, 1.0f, 0.75f, 0.75f);
                continue;
            }
            if (bl3) {
                WorldRenderer.drawBox(matrixStack, vertices, d, e, g, h, i, j, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f);
                continue;
            }
            if (!bl4) continue;
            WorldRenderer.drawBox(matrixStack, vertices, d, e, g, h, i, j, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f);
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

