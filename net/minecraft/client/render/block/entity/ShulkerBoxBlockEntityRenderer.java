/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
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

    public void method_3574(ShulkerBoxBlockEntity shulkerBoxBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
        DyeColor dyeColor;
        BlockState blockState;
        Direction direction = Direction.UP;
        if (shulkerBoxBlockEntity.hasWorld() && (blockState = shulkerBoxBlockEntity.getWorld().getBlockState(shulkerBoxBlockEntity.getPos())).getBlock() instanceof ShulkerBoxBlock) {
            direction = blockState.get(ShulkerBoxBlock.FACING);
        }
        Identifier identifier = (dyeColor = shulkerBoxBlockEntity.getColor()) == null ? ModelLoader.field_20845 : ModelLoader.field_20846.get(dyeColor.getId());
        Sprite sprite = this.method_23082(identifier);
        arg.method_22903();
        arg.method_22904(0.5, 1.5, 0.5);
        arg.method_22905(1.0f, -1.0f, -1.0f);
        arg.method_22904(0.0, 1.0, 0.0);
        float h = 0.9995f;
        arg.method_22905(0.9995f, 0.9995f, 0.9995f);
        arg.method_22907(direction.method_23224());
        arg.method_22904(0.0, -1.0, 0.0);
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.CUTOUT_MIPPED);
        this.model.method_2831().method_22698(arg, lv, 0.0625f, i, sprite);
        arg.method_22904(0.0, -shulkerBoxBlockEntity.getAnimationProgress(g) * 0.5f, 0.0);
        arg.method_22907(Vector3f.field_20705.method_23214(270.0f * shulkerBoxBlockEntity.getAnimationProgress(g), true));
        this.model.method_2829().method_22698(arg, lv, 0.0625f, i, sprite);
        arg.method_22909();
    }
}

