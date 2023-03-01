/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

/**
 * The yaw adjusting look control adjusts the entity's body yaw to be close to the
 * head yaw. In vanilla, this is used by entities that cannot rotate their heads
 * easily, such as axolotl and dolphin.
 */
public class YawAdjustingLookControl
extends LookControl {
    private final int yawAdjustThreshold;
    private static final int ADDED_PITCH = 10;
    private static final int ADDED_YAW = 20;

    public YawAdjustingLookControl(MobEntity entity, int yawAdjustThreshold) {
        super(entity);
        this.yawAdjustThreshold = yawAdjustThreshold;
    }

    @Override
    public void tick() {
        if (this.lookAtTimer > 0) {
            --this.lookAtTimer;
            this.getTargetYaw().ifPresent(yaw -> {
                this.entity.headYaw = this.changeAngle(this.entity.headYaw, yaw.floatValue() + 20.0f, this.maxYawChange);
            });
            this.getTargetPitch().ifPresent(pitch -> this.entity.setPitch(this.changeAngle(this.entity.getPitch(), pitch.floatValue() + 10.0f, this.maxPitchChange)));
        } else {
            if (this.entity.getNavigation().isIdle()) {
                this.entity.setPitch(this.changeAngle(this.entity.getPitch(), 0.0f, 5.0f));
            }
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.bodyYaw, this.maxYawChange);
        }
        float f = MathHelper.wrapDegrees(this.entity.headYaw - this.entity.bodyYaw);
        if (f < (float)(-this.yawAdjustThreshold)) {
            this.entity.bodyYaw -= 4.0f;
        } else if (f > (float)this.yawAdjustThreshold) {
            this.entity.bodyYaw += 4.0f;
        }
    }
}

