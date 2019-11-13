/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer
extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerEntityModel<?> model;

    public ShulkerBoxBlockEntityRenderer(ShulkerEntityModel<?> shulkerEntityModel, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.model = shulkerEntityModel;
    }

    public void method_3574(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        DyeColor dyeColor;
        BlockState blockState;
        Direction direction = Direction.UP;
        if (shulkerBoxBlockEntity.hasWorld() && (blockState = shulkerBoxBlockEntity.getWorld().getBlockState(shulkerBoxBlockEntity.getPos())).getBlock() instanceof ShulkerBoxBlock) {
            direction = blockState.get(ShulkerBoxBlock.FACING);
        }
        Identifier identifier = (dyeColor = shulkerBoxBlockEntity.getColor()) == null ? ModelLoader.field_20845 : ModelLoader.field_20846.get(dyeColor.getId());
        Sprite sprite = this.getSprite(identifier);
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        float g = 0.9995f;
        matrixStack.scale(0.9995f, 0.9995f, 0.9995f);
        matrixStack.multiply(direction.getRotationQuaternion());
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        matrixStack.translate(0.0, -1.0, 0.0);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.method_23948());
        this.model.getBottomShell().render(matrixStack, vertexConsumer, i, j, sprite);
        matrixStack.translate(0.0, -shulkerBoxBlockEntity.getAnimationProgress(f) * 0.5f, 0.0);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(270.0f * shulkerBoxBlockEntity.getAnimationProgress(f)));
        this.model.getTopShell().render(matrixStack, vertexConsumer, i, j, sprite);
        matrixStack.pop();
    }
}

