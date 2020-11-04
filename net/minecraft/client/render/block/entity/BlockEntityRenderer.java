/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public interface BlockEntityRenderer<T extends BlockEntity> {
    public void render(T var1, float var2, MatrixStack var3, VertexConsumerProvider var4, int var5, int var6);

    default public boolean rendersOutsideBoundingBox(T blockEntity) {
        return false;
    }
}

