/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4594;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.TropicalFishSomethingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class TropicalFishEntityRenderer
extends MobEntityRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
    private final TropicalFishEntityModelA<TropicalFishEntity> field_4800 = new TropicalFishEntityModelA(0.0f);
    private final TropicalFishEntityModelB<TropicalFishEntity> field_4799 = new TropicalFishEntityModelB(0.0f);

    public TropicalFishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new TropicalFishEntityModelA(0.0f), 0.15f);
        this.addFeature(new TropicalFishSomethingFeatureRenderer(this));
    }

    public Identifier method_4141(TropicalFishEntity tropicalFishEntity) {
        return tropicalFishEntity.getShapeId();
    }

    public void method_4140(TropicalFishEntity tropicalFishEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        class_4594 lv;
        this.model = lv = tropicalFishEntity.getShape() == 0 ? this.field_4800 : this.field_4799;
        float[] fs = tropicalFishEntity.getBaseColorComponents();
        lv.method_22956(fs[0], fs[1], fs[2]);
        super.method_4072(tropicalFishEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
        lv.method_22956(1.0f, 1.0f, 1.0f);
    }

    protected void method_4142(TropicalFishEntity tropicalFishEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(tropicalFishEntity, matrixStack, f, g, h);
        float i = 4.3f * MathHelper.sin(0.6f * f);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(i));
        if (!tropicalFishEntity.isInsideWater()) {
            matrixStack.translate(0.2f, 0.1f, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0f));
        }
    }
}

