/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.Control;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LookControl
implements Control {
    protected final MobEntity entity;
    protected float yawSpeed;
    protected float pitchSpeed;
    protected boolean active;
    protected double lookX;
    protected double lookY;
    protected double lookZ;

    public LookControl(MobEntity entity) {
        this.entity = entity;
    }

    public void lookAt(Vec3d direction) {
        this.lookAt(direction.x, direction.y, direction.z);
    }

    public void lookAt(Entity entity) {
        this.lookAt(entity.getX(), LookControl.getLookingHeightFor(entity), entity.getZ());
    }

    public void lookAt(Entity entity, float yawSpeed, float pitchSpeed) {
        this.lookAt(entity.getX(), LookControl.getLookingHeightFor(entity), entity.getZ(), yawSpeed, pitchSpeed);
    }

    public void lookAt(double x, double y, double z) {
        this.lookAt(x, y, z, this.entity.getLookYawSpeed(), this.entity.getLookPitchSpeed());
    }

    public void lookAt(double x, double y, double z, float yawSpeed, float pitchSpeed) {
        this.lookX = x;
        this.lookY = y;
        this.lookZ = z;
        this.yawSpeed = yawSpeed;
        this.pitchSpeed = pitchSpeed;
        this.active = true;
    }

    public void tick() {
        if (this.shouldStayHorizontal()) {
            this.entity.pitch = 0.0f;
        }
        if (this.active) {
            this.active = false;
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.getTargetYaw(), this.yawSpeed);
            this.entity.pitch = this.changeAngle(this.entity.pitch, this.getTargetPitch(), this.pitchSpeed);
        } else {
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.bodyYaw, 10.0f);
        }
        if (!this.entity.getNavigation().isIdle()) {
            this.entity.headYaw = MathHelper.stepAngleTowards(this.entity.headYaw, this.entity.bodyYaw, this.entity.getBodyYawSpeed());
        }
    }

    protected boolean shouldStayHorizontal() {
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
        double d = this.lookX - this.entity.getX();
        double e = this.lookY - this.entity.getEyeY();
        double f = this.lookZ - this.entity.getZ();
        double g = MathHelper.sqrt(d * d + f * f);
        return (float)(-(MathHelper.atan2(e, g) * 57.2957763671875));
    }

    protected float getTargetYaw() {
        double d = this.lookX - this.entity.getX();
        double e = this.lookZ - this.entity.getZ();
        return (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f;
    }

    protected float changeAngle(float from, float to, float max) {
        float f = MathHelper.subtractAngles(from, to);
        float g = MathHelper.clamp(f, -max, max);
        return from + g;
    }

    private static double getLookingHeightFor(Entity entity) {
        if (entity instanceof LivingEntity) {
            return entity.getEyeY();
        }
        return (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
    }
}

