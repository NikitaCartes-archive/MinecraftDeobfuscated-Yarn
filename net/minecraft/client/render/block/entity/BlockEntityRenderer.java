/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public interface BlockEntityRenderer<T extends BlockEntity> {
    public void render(T var1, float var2, MatrixStack var3, VertexConsumerProvider var4, int var5, int var6);

    default public boolean rendersOutsideBoundingBox(T blockEntity) {
        return false;
    }

    default public int getRenderDistance() {
        return 64;
    }

    default public boolean isInRenderDistance(T blockEntity, Vec3d pos) {
        return Vec3d.ofCenter(((BlockEntity)blockEntity).getPos()).isInRange(pos, this.getRenderDistance());
    }
}

