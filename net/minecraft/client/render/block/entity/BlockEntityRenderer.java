/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
    protected final BlockEntityRenderDispatcher field_20989;

    public BlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.field_20989 = blockEntityRenderDispatcher;
    }

    public abstract void render(T var1, double var2, double var4, double var6, float var8, class_4587 var9, class_4597 var10, int var11);

    protected Sprite method_23082(Identifier identifier) {
        return MinecraftClient.getInstance().getSpriteAtlas().getSprite(identifier);
    }

    public boolean method_3563(T blockEntity) {
        return false;
    }
}

