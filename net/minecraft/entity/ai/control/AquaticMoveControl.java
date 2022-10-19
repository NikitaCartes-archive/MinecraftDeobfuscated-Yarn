/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class AquaticMoveControl
extends MoveControl {
    private static final float field_40123 = 10.0f;
    private static final float field_40124 = 60.0f;
    private final int pitchChange;
    private final int yawChange;
    private final float speedInWater;
    private final float speedInAir;
    private final boolean buoyant;

    public AquaticMoveControl(MobEntity entity, int pitchChange, int yawChange, float speedInWater, float speedInAir, boolean buoyant) {
        super(entity);
        this.pitchChange = pitchChange;
        this.yawChange = yawChange;
        this.speedInWater = speedInWater;
        this.speedInAir = speedInAir;
        this.buoyant = buoyant;
    }

    @Override
    public void tick() {
        double f;
        double e;
        if (this.buoyant && this.entity.isTouchingWater()) {
            this.entity.setVelocity(this.entity.getVelocity().add(0.0, 0.005, 0.0));
        }
        if (this.state != MoveControl.State.MOVE_TO || this.entity.getNavigation().isIdle()) {
            this.entity.setMovementSpeed(0.0f);
            this.entity.setSidewaysSpeed(0.0f);
            this.entity.setUpwardSpeed(0.0f);
            this.entity.setForwardSpeed(0.0f);
            return;
        }
        double d = this.targetX - this.entity.getX();
        double g = d * d + (e = this.targetY - this.entity.getY()) * e + (f = this.targetZ - this.entity.getZ()) * f;
        if (g < 2.500000277905201E-7) {
            this.entity.setForwardSpeed(0.0f);
            return;
        }
        float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f;
        this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), h, this.yawChange));
        this.entity.bodyYaw = this.entity.getYaw();
        this.entity.headYaw = this.entity.getYaw();
        float i = (float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
        if (this.entity.isTouchingWater()) {
            float k;
            this.entity.setMovementSpeed(i * this.speedInWater);
            double j = Math.sqrt(d * d + f * f);
            if (Math.abs(e) > (double)1.0E-5f || Math.abs(j) > (double)1.0E-5f) {
                k = -((float)(MathHelper.atan2(e, j) * 57.2957763671875));
                k = MathHelper.clamp(MathHelper.wrapDegrees(k), (float)(-this.pitchChange), (float)this.pitchChange);
                this.entity.setPitch(this.wrapDegrees(this.entity.getPitch(), k, 5.0f));
            }
            k = MathHelper.cos(this.entity.getPitch() * ((float)Math.PI / 180));
            float l = MathHelper.sin(this.entity.getPitch() * ((float)Math.PI / 180));
            this.entity.forwardSpeed = k * i;
            this.entity.upwardSpeed = -l * i;
        } else {
            float m = Math.abs(MathHelper.wrapDegrees(this.entity.getYaw() - h));
            float n = AquaticMoveControl.method_45335(m);
            this.entity.setMovementSpeed(i * this.speedInAir * n);
        }
    }

    private static float method_45335(float f) {
        return 1.0f - MathHelper.clamp((f - 10.0f) / 50.0f, 0.0f, 1.0f);
    }
}

