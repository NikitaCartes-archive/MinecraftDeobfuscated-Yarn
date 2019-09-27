/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TntMinecartEntityRenderer
extends MinecartEntityRenderer<TntMinecartEntity> {
    public TntMinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    protected void method_4137(TntMinecartEntity tntMinecartEntity, float f, BlockState blockState, class_4587 arg, class_4597 arg2, int i) {
        int j = tntMinecartEntity.getFuseTicks();
        if (j > -1 && (float)j - f + 1.0f < 10.0f) {
            float g = 1.0f - ((float)j - f + 1.0f) / 10.0f;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            arg.method_22905(h, h, h);
        }
        if (j > -1 && j / 5 % 2 == 0) {
            TntMinecartEntityRenderer.method_23190(blockState, arg, arg2, i);
        } else {
            MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, arg, arg2, i, 0, 10);
        }
    }

    public static void method_23190(BlockState blockState, class_4587 arg, class_4597 arg2, int i) {
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        lv.method_22922(class_4608.method_23210(1.0f), 10);
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, arg, blockRenderLayer -> blockRenderLayer == BlockRenderLayer.SOLID ? lv : arg2.getBuffer(blockRenderLayer), i, 0, 10);
        lv.method_22923();
    }
}

