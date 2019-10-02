/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4592;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ElytraEntityModel<T extends LivingEntity>
extends class_4592<T> {
    private final ModelPart field_3364;
    private final ModelPart field_3365 = new ModelPart(this, 22, 0);

    public ElytraEntityModel() {
        this.field_3365.addCuboid(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, 1.0f);
        this.field_3364 = new ModelPart(this, 22, 0);
        this.field_3364.mirror = true;
        this.field_3364.addCuboid(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, 1.0f);
    }

    @Override
    protected Iterable<ModelPart> method_22946() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> method_22948() {
        return ImmutableList.of(this.field_3365, this.field_3364);
    }

    public void method_17079(T livingEntity, float f, float g, float h, float i, float j, float k) {
        float l = 0.2617994f;
        float m = -0.2617994f;
        float n = 0.0f;
        float o = 0.0f;
        if (((LivingEntity)livingEntity).isFallFlying()) {
            float p = 1.0f;
            Vec3d vec3d = ((Entity)livingEntity).getVelocity();
            if (vec3d.y < 0.0) {
                Vec3d vec3d2 = vec3d.normalize();
                p = 1.0f - (float)Math.pow(-vec3d2.y, 1.5);
            }
            l = p * 0.34906584f + (1.0f - p) * l;
            m = p * -1.5707964f + (1.0f - p) * m;
        } else if (((Entity)livingEntity).isInSneakingPose()) {
            l = 0.6981317f;
            m = -0.7853982f;
            n = 3.0f;
            o = 0.08726646f;
        }
        this.field_3365.pivotX = 5.0f;
        this.field_3365.pivotY = n;
        if (livingEntity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
            abstractClientPlayerEntity.elytraPitch = (float)((double)abstractClientPlayerEntity.elytraPitch + (double)(l - abstractClientPlayerEntity.elytraPitch) * 0.1);
            abstractClientPlayerEntity.elytraYaw = (float)((double)abstractClientPlayerEntity.elytraYaw + (double)(o - abstractClientPlayerEntity.elytraYaw) * 0.1);
            abstractClientPlayerEntity.elytraRoll = (float)((double)abstractClientPlayerEntity.elytraRoll + (double)(m - abstractClientPlayerEntity.elytraRoll) * 0.1);
            this.field_3365.pitch = abstractClientPlayerEntity.elytraPitch;
            this.field_3365.yaw = abstractClientPlayerEntity.elytraYaw;
            this.field_3365.roll = abstractClientPlayerEntity.elytraRoll;
        } else {
            this.field_3365.pitch = l;
            this.field_3365.roll = m;
            this.field_3365.yaw = o;
        }
        this.field_3364.pivotX = -this.field_3365.pivotX;
        this.field_3364.yaw = -this.field_3365.yaw;
        this.field_3364.pivotY = this.field_3365.pivotY;
        this.field_3364.pitch = this.field_3365.pitch;
        this.field_3364.roll = -this.field_3365.roll;
    }
}

