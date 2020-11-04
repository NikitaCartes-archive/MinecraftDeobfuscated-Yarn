/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;

@Environment(value=EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer
implements BlockEntityRenderer<MobSpawnerBlockEntity> {
    public MobSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(MobSpawnerBlockEntity mobSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        matrixStack.push();
        matrixStack.translate(0.5, 0.0, 0.5);
        MobSpawnerLogic mobSpawnerLogic = mobSpawnerBlockEntity.getLogic();
        Entity entity = mobSpawnerLogic.getRenderedEntity(mobSpawnerBlockEntity.getWorld());
        if (entity != null) {
            float g = 0.53125f;
            float h = Math.max(entity.getWidth(), entity.getHeight());
            if ((double)h > 1.0) {
                g /= h;
            }
            matrixStack.translate(0.0, 0.4f, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)MathHelper.lerp((double)f, mobSpawnerLogic.method_8279(), mobSpawnerLogic.method_8278()) * 10.0f));
            matrixStack.translate(0.0, -0.2f, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-30.0f));
            matrixStack.scale(g, g, g);
            MinecraftClient.getInstance().getEntityRenderDispatcher().render(entity, 0.0, 0.0, 0.0, 0.0f, f, matrixStack, vertexConsumerProvider, i);
        }
        matrixStack.pop();
    }
}

