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
    private final int field_28319;
    private final int field_28320;
    private final float field_28321;
    private final float field_28322;
    private final boolean field_28323;

    public AquaticMoveControl(MobEntity entity, int i, int j, float f, float g, boolean bl) {
        super(entity);
        this.field_28319 = i;
        this.field_28320 = j;
        this.field_28321 = f;
        this.field_28322 = g;
        this.field_28323 = bl;
    }

    @Override
    public void tick() {
        double f;
        double e;
        if (this.field_28323 && this.entity.isTouchingWater()) {
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
        this.entity.bodyYaw = this.entity.yaw = this.changeAngle(this.entity.yaw, h, this.field_28320);
        this.entity.headYaw = this.entity.yaw;
        float i = (float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
        if (this.entity.isTouchingWater()) {
            this.entity.setMovementSpeed(i * this.field_28321);
            float j = -((float)(MathHelper.atan2(e, MathHelper.sqrt(d * d + f * f)) * 57.2957763671875));
            j = MathHelper.clamp(MathHelper.wrapDegrees(j), (float)(-this.field_28319), (float)this.field_28319);
            this.entity.pitch = this.changeAngle(this.entity.pitch, j, 5.0f);
            float k = MathHelper.cos(this.entity.pitch * ((float)Math.PI / 180));
            float l = MathHelper.sin(this.entity.pitch * ((float)Math.PI / 180));
            this.entity.forwardSpeed = k * i;
            this.entity.upwardSpeed = -l * i;
        } else {
            this.entity.setMovementSpeed(i * this.field_28322);
        }
    }
}

