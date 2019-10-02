/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public abstract class StickingOutThingsFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>>
extends FeatureRenderer<T, M> {
    public StickingOutThingsFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }

    protected abstract int getThingCount(T var1);

    protected abstract void renderThing(MatrixStack var1, LayeredVertexConsumerStorage var2, Entity var3, float var4, float var5, float var6, float var7);

    public void method_22132(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
        int n = this.getThingCount(livingEntity);
        Random random = new Random(((Entity)livingEntity).getEntityId());
        if (n <= 0) {
            return;
        }
        for (int o = 0; o < n; ++o) {
            matrixStack.push();
            ModelPart modelPart = ((PlayerEntityModel)this.getModel()).getRandomPart(random);
            ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(random);
            modelPart.rotate(matrixStack, 0.0625f);
            float p = random.nextFloat();
            float q = random.nextFloat();
            float r = random.nextFloat();
            float s = MathHelper.lerp(p, cuboid.minX, cuboid.maxX) / 16.0f;
            float t = MathHelper.lerp(q, cuboid.minY, cuboid.maxY) / 16.0f;
            float u = MathHelper.lerp(r, cuboid.minZ, cuboid.maxZ) / 16.0f;
            matrixStack.translate(s, t, u);
            p = -1.0f * (p * 2.0f - 1.0f);
            q = -1.0f * (q * 2.0f - 1.0f);
            r = -1.0f * (r * 2.0f - 1.0f);
            this.renderThing(matrixStack, layeredVertexConsumerStorage, (Entity)livingEntity, p, q, r, h);
            matrixStack.pop();
        }
    }
}

