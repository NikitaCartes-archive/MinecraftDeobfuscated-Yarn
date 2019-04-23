/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer
extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerEntityModel<?> model;

    public ShulkerBoxBlockEntityRenderer(ShulkerEntityModel<?> shulkerEntityModel) {
        this.model = shulkerEntityModel;
    }

    public void method_3574(ShulkerBoxBlockEntity shulkerBoxBlockEntity, double d, double e, double f, float g, int i) {
        BlockState blockState;
        Direction direction = Direction.UP;
        if (shulkerBoxBlockEntity.hasWorld() && (blockState = this.getWorld().getBlockState(shulkerBoxBlockEntity.getPos())).getBlock() instanceof ShulkerBoxBlock) {
            direction = blockState.get(ShulkerBoxBlock.FACING);
        }
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        if (i >= 0) {
            this.bindTexture(DESTROY_STAGE_TEXTURES[i]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 4.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        } else {
            DyeColor dyeColor = shulkerBoxBlockEntity.getColor();
            if (dyeColor == null) {
                this.bindTexture(ShulkerEntityRenderer.SKIN);
            } else {
                this.bindTexture(ShulkerEntityRenderer.SKIN_COLOR[dyeColor.getId()]);
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        if (i < 0) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GlStateManager.translatef((float)d + 0.5f, (float)e + 1.5f, (float)f + 0.5f);
        GlStateManager.scalef(1.0f, -1.0f, -1.0f);
        GlStateManager.translatef(0.0f, 1.0f, 0.0f);
        float h = 0.9995f;
        GlStateManager.scalef(0.9995f, 0.9995f, 0.9995f);
        GlStateManager.translatef(0.0f, -1.0f, 0.0f);
        switch (direction) {
            case DOWN: {
                GlStateManager.translatef(0.0f, 2.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                break;
            }
            case UP: {
                break;
            }
            case NORTH: {
                GlStateManager.translatef(0.0f, 1.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case SOUTH: {
                GlStateManager.translatef(0.0f, 1.0f, -1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                break;
            }
            case WEST: {
                GlStateManager.translatef(-1.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case EAST: {
                GlStateManager.translatef(1.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
            }
        }
        this.model.method_2831().render(0.0625f);
        GlStateManager.translatef(0.0f, -shulkerBoxBlockEntity.getAnimationProgress(g) * 0.5f, 0.0f);
        GlStateManager.rotatef(270.0f * shulkerBoxBlockEntity.getAnimationProgress(g), 0.0f, 1.0f, 0.0f);
        this.model.method_2829().render(0.0625f);
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (i >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}

