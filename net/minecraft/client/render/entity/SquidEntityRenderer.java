/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class SquidEntityRenderer
extends MobEntityRenderer<SquidEntity, SquidEntityModel<SquidEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/squid.png");

    public SquidEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SquidEntityModel(), 0.7f);
    }

    @Override
    public Identifier getTexture(SquidEntity squidEntity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(SquidEntity squidEntity, MatrixStack matrixStack, float f, float g, float h) {
        float i = MathHelper.lerp(h, squidEntity.prevTiltAngle, squidEntity.tiltAngle);
        float j = MathHelper.lerp(h, squidEntity.prevRollAngle, squidEntity.rollAngle);
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - g));
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(i));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(j));
        matrixStack.translate(0.0, -1.2f, 0.0);
    }

    @Override
    protected float getAnimationProgress(SquidEntity squidEntity, float f) {
        return MathHelper.lerp(f, squidEntity.prevTentacleAngle, squidEntity.tentacleAngle);
    }
}

