/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.entity.ai.control.Control;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

/**
 * The body control ensures a mob's head and body yaws are kept up with each other.
 */
public class BodyControl
implements Control {
    private final MobEntity entity;
    private static final int BODY_KEEP_UP_THRESHOLD = 15;
    private static final int ROTATE_BODY_START_TICK = 10;
    private static final int ROTATION_INCREMENTS = 10;
    private int bodyAdjustTicks;
    private float lastHeadYaw;

    public BodyControl(MobEntity entity) {
        this.entity = entity;
    }

    /**
     * Ticks the body control.
     * 
     * @implSpec If the entity {@linkplain #isMoving() has moved}, its body yaw
     * adjusts to its head yaw. Otherwise, if the entity is {@linkplain
     * #isIndependent() not steered}, its head yaw adjusts to its body yaw.
     */
    public void tick() {
        if (this.isMoving()) {
            this.entity.bodyYaw = this.entity.getYaw();
            this.keepUpHead();
            this.lastHeadYaw = this.entity.headYaw;
            this.bodyAdjustTicks = 0;
            return;
        }
        if (this.isIndependent()) {
            if (Math.abs(this.entity.headYaw - this.lastHeadYaw) > 15.0f) {
                this.bodyAdjustTicks = 0;
                this.lastHeadYaw = this.entity.headYaw;
                this.keepUpBody();
            } else {
                ++this.bodyAdjustTicks;
                if (this.bodyAdjustTicks > 10) {
                    this.slowlyAdjustBody();
                }
            }
        }
    }

    /**
     * Keeps up the body yaw by ensuring it is within the {@linkplain
     * MobEntity#getMaxHeadRotation max head rotation} from the head yaw.
     */
    private void keepUpBody() {
        this.entity.bodyYaw = MathHelper.clampAngle(this.entity.bodyYaw, this.entity.headYaw, this.entity.getMaxHeadRotation());
    }

    /**
     * Keeps up the head yaw by ensuring it is within the {@linkplain
     * MobEntity#getMaxHeadRotation max head rotation} from the body yaw.
     */
    private void keepUpHead() {
        this.entity.headYaw = MathHelper.clampAngle(this.entity.headYaw, this.entity.bodyYaw, this.entity.getMaxHeadRotation());
    }

    /**
     * Gradually adjusts the body yaw toward the head yaw, starting after 10 ticks of
     * {@linkplain #bodyAdjustTicks wait} and finishes by the 20th tick.
     */
    private void slowlyAdjustBody() {
        int i = this.bodyAdjustTicks - 10;
        float f = MathHelper.clamp((float)i / 10.0f, 0.0f, 1.0f);
        float g = (float)this.entity.getMaxHeadRotation() * (1.0f - f);
        this.entity.bodyYaw = MathHelper.clampAngle(this.entity.bodyYaw, this.entity.headYaw, g);
    }

    private boolean isIndependent() {
        return !(this.entity.getFirstPassenger() instanceof MobEntity);
    }

    private boolean isMoving() {
        double e;
        double d = this.entity.getX() - this.entity.prevX;
        return d * d + (e = this.entity.getZ() - this.entity.prevZ) * e > 2.500000277905201E-7;
    }
}

