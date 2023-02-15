/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.SuspiciousSandBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class SuspiciousSandBlockEntityRenderer
implements BlockEntityRenderer<SuspiciousSandBlockEntity> {
    private final ItemRenderer itemRenderer;

    public SuspiciousSandBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SuspiciousSandBlockEntity suspiciousSandBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        if (suspiciousSandBlockEntity.getWorld() == null) {
            return;
        }
        int k = suspiciousSandBlockEntity.getCachedState().get(Properties.DUSTED);
        if (k <= 0) {
            return;
        }
        Direction direction = suspiciousSandBlockEntity.getHitDirection();
        if (direction == null) {
            return;
        }
        ItemStack itemStack = suspiciousSandBlockEntity.getItem();
        if (itemStack.isEmpty()) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.0f, 0.5f, 0.0f);
        float[] fs = this.getTranslation(direction, k);
        matrixStack.translate(fs[0], fs[1], fs[2]);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(75.0f));
        boolean bl = direction == Direction.EAST || direction == Direction.WEST;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((bl ? 90 : 0) + 11));
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        int l = WorldRenderer.getLightmapCoordinates(suspiciousSandBlockEntity.getWorld(), suspiciousSandBlockEntity.getCachedState(), suspiciousSandBlockEntity.getPos().offset(direction));
        this.itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, l, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, suspiciousSandBlockEntity.getWorld(), 0);
        matrixStack.pop();
    }

    private float[] getTranslation(Direction direction, int dustedLevel) {
        float[] fs = new float[]{0.5f, 0.0f, 0.5f};
        float f = (float)dustedLevel / 10.0f * 0.75f;
        switch (direction) {
            case EAST: {
                fs[0] = 0.73f + f;
                break;
            }
            case WEST: {
                fs[0] = 0.25f - f;
                break;
            }
            case UP: {
                fs[1] = 0.25f + f;
                break;
            }
            case DOWN: {
                fs[1] = -0.23f - f;
                break;
            }
            case NORTH: {
                fs[2] = 0.25f - f;
                break;
            }
            case SOUTH: {
                fs[2] = 0.73f + f;
            }
        }
        return fs;
    }
}

