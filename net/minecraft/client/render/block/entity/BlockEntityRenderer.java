/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
    protected final BlockEntityRenderDispatcher field_20989;

    public BlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.field_20989 = blockEntityRenderDispatcher;
    }

    public abstract void render(T var1, double var2, double var4, double var6, float var8, MatrixStack var9, LayeredVertexConsumerStorage var10, int var11);

    protected Sprite getSprite(Identifier identifier) {
        return MinecraftClient.getInstance().getSpriteAtlas().getSprite(identifier);
    }

    public boolean method_3563(T blockEntity) {
        return false;
    }
}

