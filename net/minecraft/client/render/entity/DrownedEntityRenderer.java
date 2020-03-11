/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DrownedEntityRenderer
extends ZombieBaseEntityRenderer<DrownedEntity, DrownedEntityModel<DrownedEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/drowned.png");

    public DrownedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new DrownedEntityModel(0.0f, 0.0f, 64, 64), new DrownedEntityModel(0.5f, true), new DrownedEntityModel(1.0f, true));
        this.addFeature(new DrownedOverlayFeatureRenderer<DrownedEntity>(this));
    }

    @Override
    public Identifier getTexture(ZombieEntity zombieEntity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(DrownedEntity drownedEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(drownedEntity, matrixStack, f, g, h);
        float i = drownedEntity.getLeaningPitch(h);
        if (i > 0.0f) {
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(i, drownedEntity.pitch, -10.0f - drownedEntity.pitch)));
        }
    }
}

