/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class DolphinLookControl
extends LookControl {
    private final int field_6357;

    public DolphinLookControl(MobEntity mobEntity, int i) {
        super(mobEntity);
        this.field_6357 = i;
    }

    @Override
    public void tick() {
        if (this.active) {
            this.active = false;
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.getTargetYaw() + 20.0f, this.yawSpeed);
            this.entity.pitch = this.changeAngle(this.entity.pitch, this.getTargetPitch() + 10.0f, this.pitchSpeed);
        } else {
            if (this.entity.getNavigation().isIdle()) {
                this.entity.pitch = this.changeAngle(this.entity.pitch, 0.0f, 5.0f);
            }
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.bodyYaw, this.yawSpeed);
        }
        float f = MathHelper.wrapDegrees(this.entity.headYaw - this.entity.bodyYaw);
        if (f < (float)(-this.field_6357)) {
            this.entity.bodyYaw -= 4.0f;
        } else if (f > (float)this.field_6357) {
            this.entity.bodyYaw += 4.0f;
        }
    }
}

