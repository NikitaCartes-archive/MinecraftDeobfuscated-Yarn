/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer
implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerEntityModel<?> model;

    public ShulkerBoxBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new ShulkerEntityModel(ctx.getLayerModelPart(EntityModelLayers.SHULKER));
    }

    @Override
    public void render(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        DyeColor dyeColor;
        BlockState blockState;
        Direction direction = Direction.UP;
        if (shulkerBoxBlockEntity.hasWorld() && (blockState = shulkerBoxBlockEntity.getWorld().getBlockState(shulkerBoxBlockEntity.getPos())).getBlock() instanceof ShulkerBoxBlock) {
            direction = blockState.get(ShulkerBoxBlock.FACING);
        }
        SpriteIdentifier spriteIdentifier = (dyeColor = shulkerBoxBlockEntity.getColor()) == null ? TexturedRenderLayers.SHULKER_TEXTURE_ID : TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(dyeColor.getId());
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        float g = 0.9995f;
        matrixStack.scale(0.9995f, 0.9995f, 0.9995f);
        matrixStack.multiply(direction.getRotationQuaternion());
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        matrixStack.translate(0.0, -1.0, 0.0);
        ModelPart modelPart = this.model.getLid();
        modelPart.setPivot(0.0f, 24.0f - shulkerBoxBlockEntity.getAnimationProgress(f) * 0.5f * 16.0f, 0.0f);
        modelPart.yaw = 270.0f * shulkerBoxBlockEntity.getAnimationProgress(f) * ((float)Math.PI / 180);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
        this.model.render(matrixStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
    }
}

