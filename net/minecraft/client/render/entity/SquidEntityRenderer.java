/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class SquidEntityRenderer<T extends SquidEntity>
extends MobEntityRenderer<T, SquidEntityModel<T>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/squid/squid.png");

    public SquidEntityRenderer(EntityRendererFactory.Context ctx, SquidEntityModel<T> model) {
        super(ctx, model, 0.7f);
    }

    @Override
    public Identifier getTexture(T squidEntity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(T squidEntity, MatrixStack matrixStack, float f, float g, float h) {
        float i = MathHelper.lerp(h, ((SquidEntity)squidEntity).prevTiltAngle, ((SquidEntity)squidEntity).tiltAngle);
        float j = MathHelper.lerp(h, ((SquidEntity)squidEntity).prevRollAngle, ((SquidEntity)squidEntity).rollAngle);
        matrixStack.translate(0.0f, 0.5f, 0.0f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - g));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.translate(0.0f, -1.2f, 0.0f);
    }

    @Override
    protected float getAnimationProgress(T squidEntity, float f) {
        return MathHelper.lerp(f, ((SquidEntity)squidEntity).prevTentacleAngle, ((SquidEntity)squidEntity).tentacleAngle);
    }
}

