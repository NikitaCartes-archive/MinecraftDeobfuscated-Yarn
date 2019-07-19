/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LookControl {
    protected final MobEntity entity;
    protected float yawSpeed;
    protected float pitchSpeed;
    protected boolean active;
    protected double lookX;
    protected double lookY;
    protected double lookZ;

    public LookControl(MobEntity mobEntity) {
        this.entity = mobEntity;
    }

    public void lookAt(Vec3d vec3d) {
        this.lookAt(vec3d.x, vec3d.y, vec3d.z);
    }

    public void lookAt(Entity entity, float f, float g) {
        this.lookAt(entity.x, LookControl.getLookingHeightFor(entity), entity.z, f, g);
    }

    public void lookAt(double d, double e, double f) {
        this.lookAt(d, e, f, this.entity.getLookYawSpeed(), this.entity.getLookPitchSpeed());
    }

    public void lookAt(double d, double e, double f, float g, float h) {
        this.lookX = d;
        this.lookY = e;
        this.lookZ = f;
        this.yawSpeed = g;
        this.pitchSpeed = h;
        this.active = true;
    }

    public void tick() {
        if (this.method_20433()) {
            this.entity.pitch = 0.0f;
        }
        if (this.active) {
            this.active = false;
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.getTargetYaw(), this.yawSpeed);
            this.entity.pitch = this.changeAngle(this.entity.pitch, this.getTargetPitch(), this.pitchSpeed);
        } else {
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.field_6283, 10.0f);
        }
        if (!this.entity.getNavigation().isIdle()) {
            this.entity.headYaw = MathHelper.capRotation(this.entity.headYaw, this.entity.field_6283, this.entity.method_5986());
        }
    }

    protected boolean method_20433() {
        return true;
    }

    public boolean isActive() {
        return this.active;
    }

    public double getLookX() {
        return this.lookX;
    }

    public double getLookY() {
        return this.lookY;
    }

    public double getLookZ() {
        return this.lookZ;
    }

    protected float getTargetPitch() {
        double d = this.lookX - this.entity.x;
        double e = this.lookY - (this.entity.y + (double)this.entity.getStandingEyeHeight());
        double f = this.lookZ - this.entity.z;
        double g = MathHelper.sqrt(d * d + f * f);
        return (float)(-(MathHelper.atan2(e, g) * 57.2957763671875));
    }

    protected float getTargetYaw() {
        double d = this.lookX - this.entity.x;
        double e = this.lookZ - this.entity.z;
        return (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f;
    }

    protected float changeAngle(float f, float g, float h) {
        float i = MathHelper.subtractAngles(f, g);
        float j = MathHelper.clamp(i, -h, h);
        return f + j;
    }

    private static double getLookingHeightFor(Entity entity) {
        if (entity instanceof LivingEntity) {
            return entity.y + (double)entity.getStandingEyeHeight();
        }
        return (entity.getBoundingBox().y1 + entity.getBoundingBox().y2) / 2.0;
    }
}

