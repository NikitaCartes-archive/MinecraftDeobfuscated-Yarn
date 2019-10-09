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
    private final int field_20349;
    private final boolean field_20350;

    public FlightMoveControl(MobEntity mobEntity, int i, boolean bl) {
        super(mobEntity);
        this.field_20349 = i;
        this.field_20350 = bl;
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
            this.entity.yaw = this.changeAngle(this.entity.yaw, h, 90.0f);
            float i = this.entity.onGround ? (float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()) : (float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.FLYING_SPEED).getValue());
            this.entity.setMovementSpeed(i);
            double j = MathHelper.sqrt(d * d + f * f);
            float k = (float)(-(MathHelper.atan2(e, j) * 57.2957763671875));
            this.entity.pitch = this.changeAngle(this.entity.pitch, k, this.field_20349);
            this.entity.setUpwardSpeed(e > 0.0 ? i : -i);
        } else {
            if (!this.field_20350) {
                this.entity.setNoGravity(false);
            }
            this.entity.setUpwardSpeed(0.0f);
            this.entity.setForwardSpeed(0.0f);
        }
    }
}

