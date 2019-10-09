/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class BodyControl {
    private final MobEntity entity;
    private int activeTicks;
    private float lastHeadYaw;

    public BodyControl(MobEntity mobEntity) {
        this.entity = mobEntity;
    }

    public void tick() {
        if (this.isMoving()) {
            this.entity.bodyYaw = this.entity.yaw;
            this.rotateHead();
            this.lastHeadYaw = this.entity.headYaw;
            this.activeTicks = 0;
            return;
        }
        if (this.isIndependent()) {
            if (Math.abs(this.entity.headYaw - this.lastHeadYaw) > 15.0f) {
                this.activeTicks = 0;
                this.lastHeadYaw = this.entity.headYaw;
                this.rotateLook();
            } else {
                ++this.activeTicks;
                if (this.activeTicks > 10) {
                    this.rotateBody();
                }
            }
        }
    }

    private void rotateLook() {
        this.entity.bodyYaw = MathHelper.method_20306(this.entity.bodyYaw, this.entity.headYaw, this.entity.method_5986());
    }

    private void rotateHead() {
        this.entity.headYaw = MathHelper.method_20306(this.entity.headYaw, this.entity.bodyYaw, this.entity.method_5986());
    }

    private void rotateBody() {
        int i = this.activeTicks - 10;
        float f = MathHelper.clamp((float)i / 10.0f, 0.0f, 1.0f);
        float g = (float)this.entity.method_5986() * (1.0f - f);
        this.entity.bodyYaw = MathHelper.method_20306(this.entity.bodyYaw, this.entity.headYaw, g);
    }

    private boolean isIndependent() {
        return this.entity.getPassengerList().isEmpty() || !(this.entity.getPassengerList().get(0) instanceof MobEntity);
    }

    private boolean isMoving() {
        double e;
        double d = this.entity.getX() - this.entity.prevX;
        return d * d + (e = this.entity.getZ() - this.entity.prevZ) * e > 2.500000277905201E-7;
    }
}

