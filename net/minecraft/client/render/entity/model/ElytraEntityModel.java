/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ElytraEntityModel<T extends LivingEntity>
extends AnimalModel<T> {
    private final ModelPart field_27412;
    private final ModelPart field_3365;

    public ElytraEntityModel(ModelPart modelPart) {
        this.field_3365 = modelPart.method_32086("left_wing");
        this.field_27412 = modelPart.method_32086("right_wing");
    }

    public static class_5607 method_31994() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        class_5605 lv3 = new class_5605(1.0f);
        lv2.method_32117("left_wing", class_5606.method_32108().method_32101(22, 0).method_32098(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, lv3), class_5603.method_32091(5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, -0.2617994f));
        lv2.method_32117("right_wing", class_5606.method_32108().method_32101(22, 0).method_32096().method_32098(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, lv3), class_5603.method_32091(-5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, 0.2617994f));
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.field_3365, this.field_27412);
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
        this.field_3365.pivotY = m;
        if (livingEntity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
            abstractClientPlayerEntity.elytraPitch = (float)((double)abstractClientPlayerEntity.elytraPitch + (double)(k - abstractClientPlayerEntity.elytraPitch) * 0.1);
            abstractClientPlayerEntity.elytraYaw = (float)((double)abstractClientPlayerEntity.elytraYaw + (double)(n - abstractClientPlayerEntity.elytraYaw) * 0.1);
            abstractClientPlayerEntity.elytraRoll = (float)((double)abstractClientPlayerEntity.elytraRoll + (double)(l - abstractClientPlayerEntity.elytraRoll) * 0.1);
            this.field_3365.pitch = abstractClientPlayerEntity.elytraPitch;
            this.field_3365.yaw = abstractClientPlayerEntity.elytraYaw;
            this.field_3365.roll = abstractClientPlayerEntity.elytraRoll;
        } else {
            this.field_3365.pitch = k;
            this.field_3365.roll = l;
            this.field_3365.yaw = n;
        }
        this.field_27412.yaw = -this.field_3365.yaw;
        this.field_27412.pivotY = this.field_3365.pivotY;
        this.field_27412.pitch = this.field_3365.pitch;
        this.field_27412.roll = -this.field_3365.roll;
    }
}

