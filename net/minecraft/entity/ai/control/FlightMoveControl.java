/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class FlightMoveControl
extends MoveControl {
    private final int maxPitchChange;
    private final boolean noGravity;

    public FlightMoveControl(MobEntity entity, int maxPitchChange, boolean noGravity) {
        super(entity);
        this.maxPitchChange = maxPitchChange;
        this.noGravity = noGravity;
    }

    @Override
    public void tick() {
        if (this.state == MoveControl.State.MOVE_TO) {
            this.state = MoveControl.State.WAIT;
            this.entity.setNoGravity(true);
            double d = this.targetX - this.entity.getX();
            double e = this.targetY - this.entity.getY();
            double f = this.targetZ - this.entity.getZ();
            double g = d * d + e * e + f * f;
            if (g < 2.500000277905201E-7) {
                this.entity.setUpwardSpeed(0.0f);
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f;
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), h, 90.0f));
            float i = this.entity.isOnGround() ? (float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)) : (float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED));
            this.entity.setMovementSpeed(i);
            double j = Math.sqrt(d * d + f * f);
            if (Math.abs(e) > (double)1.0E-5f || Math.abs(j) > (double)1.0E-5f) {
                float k = (float)(-(MathHelper.atan2(e, j) * 57.2957763671875));
                this.entity.setPitch(this.wrapDegrees(this.entity.getPitch(), k, this.maxPitchChange));
                this.entity.setUpwardSpeed(e > 0.0 ? i : -i);
            }
        } else {
            if (!this.noGravity) {
                this.entity.setNoGravity(false);
            }
            this.entity.setUpwardSpeed(0.0f);
            this.entity.setForwardSpeed(0.0f);
        }
    }
}

