/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
    protected final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public BlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
    }

    public abstract void render(T var1, float var2, MatrixStack var3, VertexConsumerProvider var4, int var5, int var6);

    protected Sprite getSprite(Identifier identifier) {
        return MinecraftClient.getInstance().getSpriteAtlas().getSprite(identifier);
    }

    public boolean method_3563(T blockEntity) {
        return false;
    }
}

