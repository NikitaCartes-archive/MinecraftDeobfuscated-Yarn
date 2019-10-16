/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;

@Environment(value=EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer
extends BlockEntityRenderer<MobSpawnerBlockEntity> {
    public MobSpawnerBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void method_3589(MobSpawnerBlockEntity mobSpawnerBlockEntity, double d, double e, double f, float g, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, int j) {
        matrixStack.push();
        matrixStack.translate(0.5, 0.0, 0.5);
        MobSpawnerLogic mobSpawnerLogic = mobSpawnerBlockEntity.getLogic();
        Entity entity = mobSpawnerLogic.getRenderedEntity();
        if (entity != null) {
            float h = 0.53125f;
            float k = Math.max(entity.getWidth(), entity.getHeight());
            if ((double)k > 1.0) {
                h /= k;
            }
            matrixStack.translate(0.0, 0.4f, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)MathHelper.lerp((double)g, mobSpawnerLogic.method_8279(), mobSpawnerLogic.method_8278()) * 10.0f));
            matrixStack.translate(0.0, -0.2f, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-30.0f));
            matrixStack.scale(h, h, h);
            entity.setPositionAndAngles(d, e, f, 0.0f, 0.0f);
            MinecraftClient.getInstance().getEntityRenderManager().render(entity, 0.0, 0.0, 0.0, 0.0f, g, matrixStack, layeredVertexConsumerStorage);
        }
        matrixStack.pop();
    }
}

