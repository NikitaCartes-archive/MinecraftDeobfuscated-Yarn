/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class class_4507<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public class_4507(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }

    protected abstract int method_22134(T var1);

    protected abstract void method_22130(Entity var1, float var2, float var3, float var4, float var5);

    protected void method_22131(T livingEntity) {
        GuiLighting.disable();
    }

    protected void method_22133() {
        GuiLighting.enable();
    }

    public void method_22132(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        int m = this.method_22134(livingEntity);
        Random random = new Random(((Entity)livingEntity).getEntityId());
        if (m <= 0) {
            return;
        }
        this.method_22131(livingEntity);
        for (int n = 0; n < m; ++n) {
            RenderSystem.pushMatrix();
            ModelPart modelPart = ((Model)this.getModel()).getRandomCuboid(random);
            Cuboid cuboid = modelPart.cuboids.get(random.nextInt(modelPart.cuboids.size()));
            modelPart.applyTransform(0.0625f);
            float o = random.nextFloat();
            float p = random.nextFloat();
            float q = random.nextFloat();
            float r = MathHelper.lerp(o, cuboid.xMin, cuboid.xMax) / 16.0f;
            float s = MathHelper.lerp(p, cuboid.yMin, cuboid.yMax) / 16.0f;
            float t = MathHelper.lerp(q, cuboid.zMin, cuboid.zMax) / 16.0f;
            RenderSystem.translatef(r, s, t);
            o = -1.0f * (o * 2.0f - 1.0f);
            p = -1.0f * (p * 2.0f - 1.0f);
            q = -1.0f * (q * 2.0f - 1.0f);
            this.method_22130((Entity)livingEntity, o, p, q, h);
            RenderSystem.popMatrix();
        }
        this.method_22133();
    }
}

