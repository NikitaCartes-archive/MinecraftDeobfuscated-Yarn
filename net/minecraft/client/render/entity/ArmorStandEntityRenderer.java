/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityRenderer
extends LivingEntityRenderer<ArmorStandEntity, ArmorStandArmorEntityModel> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/armorstand/wood.png");

    public ArmorStandEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ArmorStandEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND)), 0.0f);
        this.addFeature(new ArmorFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel, ArmorStandArmorEntityModel>(this, new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_INNER_ARMOR)), new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_OUTER_ARMOR))));
        this.addFeature(new HeldItemFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
        this.addFeature(new ElytraFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this, context.getModelLoader()));
        this.addFeature(new HeadFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this, context.getModelLoader()));
    }

    @Override
    public Identifier getTexture(ArmorStandEntity armorStandEntity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(ArmorStandEntity armorStandEntity, MatrixStack matrixStack, float f, float g, float h) {
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - g));
        float i = (float)(armorStandEntity.world.getTime() - armorStandEntity.lastHitTime) + h;
        if (i < 5.0f) {
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.sin(i / 1.5f * (float)Math.PI) * 3.0f));
        }
    }

    @Override
    protected boolean hasLabel(ArmorStandEntity armorStandEntity) {
        float f;
        double d = this.dispatcher.getSquaredDistanceToCamera(armorStandEntity);
        float f2 = f = armorStandEntity.isInSneakingPose() ? 32.0f : 64.0f;
        if (d >= (double)(f * f)) {
            return false;
        }
        return armorStandEntity.isCustomNameVisible();
    }

    @Override
    @Nullable
    protected RenderLayer getRenderLayer(ArmorStandEntity armorStandEntity, boolean bl, boolean bl2, boolean bl3) {
        if (!armorStandEntity.isMarker()) {
            return super.getRenderLayer(armorStandEntity, bl, bl2, bl3);
        }
        Identifier identifier = this.getTexture(armorStandEntity);
        if (bl2) {
            return RenderLayer.getEntityTranslucent(identifier, false);
        }
        if (bl) {
            return RenderLayer.getEntityCutoutNoCull(identifier, false);
        }
        return null;
    }
}

