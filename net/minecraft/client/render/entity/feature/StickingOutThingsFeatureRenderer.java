/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class StickingOutThingsFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>>
extends FeatureRenderer<T, M> {
    public StickingOutThingsFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }

    protected abstract int getThingCount(T var1);

    protected abstract void renderThing(class_4587 var1, class_4597 var2, Entity var3, float var4, float var5, float var6, float var7);

    public void method_22132(class_4587 arg, class_4597 arg2, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
        int n = this.getThingCount(livingEntity);
        Random random = new Random(((Entity)livingEntity).getEntityId());
        if (n <= 0) {
            return;
        }
        for (int o = 0; o < n; ++o) {
            arg.method_22903();
            ModelPart modelPart = ((PlayerEntityModel)this.getModel()).method_22697(random);
            ModelPart.Cuboid cuboid = modelPart.method_22700(random);
            modelPart.method_22703(arg, 0.0625f);
            float p = random.nextFloat();
            float q = random.nextFloat();
            float r = random.nextFloat();
            float s = MathHelper.lerp(p, cuboid.xMin, cuboid.xMax) / 16.0f;
            float t = MathHelper.lerp(q, cuboid.yMin, cuboid.yMax) / 16.0f;
            float u = MathHelper.lerp(r, cuboid.zMin, cuboid.zMax) / 16.0f;
            arg.method_22904(s, t, u);
            p = -1.0f * (p * 2.0f - 1.0f);
            q = -1.0f * (q * 2.0f - 1.0f);
            r = -1.0f * (r * 2.0f - 1.0f);
            this.renderThing(arg, arg2, (Entity)livingEntity, p, q, r, h);
            arg.method_22909();
        }
    }
}

