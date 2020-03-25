/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4985;
import net.minecraft.class_4997;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.PigSaddleFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class class_4999
extends MobEntityRenderer<class_4985, class_4997<class_4985>> {
    private static final Identifier field_23372 = new Identifier("textures/entity/strider/strider.png");

    public class_4999(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new class_4997(), 0.5f);
        this.addFeature(new PigSaddleFeatureRenderer(this, new class_4997(), new Identifier("textures/entity/strider/strider_saddle.png")));
    }

    @Override
    public Identifier getTexture(class_4985 arg) {
        return field_23372;
    }

    @Override
    protected void scale(class_4985 arg, MatrixStack matrixStack, float f) {
        float g = 0.9375f;
        if (arg.isBaby()) {
            g *= 0.5f;
            this.shadowSize = 0.25f;
        } else {
            this.shadowSize = 0.5f;
        }
        matrixStack.scale(g, g, g);
    }

    @Override
    protected boolean isShaking(class_4985 arg) {
        return arg.method_26348();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity entity) {
        return this.isShaking((class_4985)entity);
    }
}

