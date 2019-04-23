/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ElytraEntityModel<T extends LivingEntity>
extends EntityModel<T> {
    private final Cuboid field_3364;
    private final Cuboid field_3365 = new Cuboid(this, 22, 0);

    public ElytraEntityModel() {
        this.field_3365.addBox(-10.0f, 0.0f, 0.0f, 10, 20, 2, 1.0f);
        this.field_3364 = new Cuboid(this, 22, 0);
        this.field_3364.mirror = true;
        this.field_3364.addBox(0.0f, 0.0f, 0.0f, 10, 20, 2, 1.0f);
    }

    public void method_17078(T livingEntity, float f, float g, float h, float i, float j, float k) {
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();
        if (((LivingEntity)livingEntity).isBaby()) {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 1.5f, -0.1f);
            this.field_3365.render(k);
            this.field_3364.render(k);
            GlStateManager.popMatrix();
        } else {
            this.field_3365.render(k);
            this.field_3364.render(k);
        }
    }

    public void method_17079(T livingEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(livingEntity, f, g, h, i, j, k);
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
        this.field_3365.rotationPointX = 5.0f;
        this.field_3365.rotationPointY = n;
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
        this.field_3364.rotationPointX = -this.field_3365.rotationPointX;
        this.field_3364.yaw = -this.field_3365.yaw;
        this.field_3364.rotationPointY = this.field_3365.rotationPointY;
        this.field_3364.pitch = this.field_3365.pitch;
        this.field_3364.roll = -this.field_3365.roll;
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17079((LivingEntity)entity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17078((LivingEntity)entity, f, g, h, i, j, k);
    }
}

