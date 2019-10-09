/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityRenderer
extends LivingEntityRenderer<ArmorStandEntity, ArmorStandArmorEntityModel> {
    public static final Identifier SKIN = new Identifier("textures/entity/armorstand/wood.png");

    public ArmorStandEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ArmorStandEntityModel(), 0.0f);
        this.addFeature(new ArmorBipedFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel, ArmorStandArmorEntityModel>(this, new ArmorStandArmorEntityModel(0.5f), new ArmorStandArmorEntityModel(1.0f)));
        this.addFeature(new HeldItemFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
        this.addFeature(new ElytraFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
        this.addFeature(new HeadFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
    }

    public Identifier method_3880(ArmorStandEntity armorStandEntity) {
        return SKIN;
    }

    protected void method_3877(ArmorStandEntity armorStandEntity, MatrixStack matrixStack, float f, float g, float h) {
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f - g));
        float i = (float)(armorStandEntity.world.getTime() - armorStandEntity.field_7112) + h;
        if (i < 5.0f) {
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(MathHelper.sin(i / 1.5f * (float)Math.PI) * 3.0f));
        }
    }

    protected boolean method_3878(ArmorStandEntity armorStandEntity) {
        float f;
        double d = this.renderManager.method_23168(armorStandEntity);
        float f2 = f = armorStandEntity.isInSneakingPose() ? 32.0f : 64.0f;
        if (d >= (double)(f * f)) {
            return false;
        }
        return armorStandEntity.isCustomNameVisible();
    }

    protected boolean method_23152(ArmorStandEntity armorStandEntity, boolean bl) {
        if (armorStandEntity.isMarker()) {
            return !armorStandEntity.isInvisible() && !bl;
        }
        return !armorStandEntity.isInvisible() || bl;
    }

    @Override
    protected /* synthetic */ boolean method_4055(LivingEntity livingEntity) {
        return this.method_3878((ArmorStandEntity)livingEntity);
    }
}

