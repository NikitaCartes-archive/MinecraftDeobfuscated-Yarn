/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Represents the model of a worn elytra.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_WING}</td><td>Root part</td><td>{@link #leftWing}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_WING}</td><td>Root part</td><td>{@link #rightWing}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(value=EnvType.CLIENT)
public class ElytraEntityModel<T extends LivingEntity>
extends AnimalModel<T> {
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public ElytraEntityModel(ModelPart root) {
        this.leftWing = root.getChild(EntityModelPartNames.LEFT_WING);
        this.rightWing = root.getChild(EntityModelPartNames.RIGHT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(1.0f);
        modelPartData.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(22, 0).cuboid(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, dilation), ModelTransform.of(5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, -0.2617994f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(22, 0).mirrored().cuboid(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, dilation), ModelTransform.of(-5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, 0.2617994f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.leftWing, this.rightWing);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        float k = 0.2617994f;
        float l = -0.2617994f;
        float m = 0.0f;
        float n = 0.0f;
        if (((LivingEntity)livingEntity).isFallFlying()) {
            float o = 1.0f;
            Vec3d vec3d = ((Entity)livingEntity).getVelocity();
            if (vec3d.y < 0.0) {
                Vec3d vec3d2 = vec3d.normalize();
                o = 1.0f - (float)Math.pow(-vec3d2.y, 1.5);
            }
            k = o * 0.34906584f + (1.0f - o) * k;
            l = o * -1.5707964f + (1.0f - o) * l;
        } else if (((Entity)livingEntity).isInSneakingPose()) {
            k = 0.6981317f;
            l = -0.7853982f;
            m = 3.0f;
            n = 0.08726646f;
        }
        this.leftWing.pivotY = m;
        if (livingEntity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
            abstractClientPlayerEntity.elytraPitch = (float)((double)abstractClientPlayerEntity.elytraPitch + (double)(k - abstractClientPlayerEntity.elytraPitch) * 0.1);
            abstractClientPlayerEntity.elytraYaw = (float)((double)abstractClientPlayerEntity.elytraYaw + (double)(n - abstractClientPlayerEntity.elytraYaw) * 0.1);
            abstractClientPlayerEntity.elytraRoll = (float)((double)abstractClientPlayerEntity.elytraRoll + (double)(l - abstractClientPlayerEntity.elytraRoll) * 0.1);
            this.leftWing.pitch = abstractClientPlayerEntity.elytraPitch;
            this.leftWing.yaw = abstractClientPlayerEntity.elytraYaw;
            this.leftWing.roll = abstractClientPlayerEntity.elytraRoll;
        } else {
            this.leftWing.pitch = k;
            this.leftWing.roll = l;
            this.leftWing.yaw = n;
        }
        this.rightWing.yaw = -this.leftWing.yaw;
        this.rightWing.pivotY = this.leftWing.pivotY;
        this.rightWing.pitch = this.leftWing.pitch;
        this.rightWing.roll = -this.leftWing.roll;
    }
}

