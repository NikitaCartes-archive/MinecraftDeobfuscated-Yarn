/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;

public abstract class DiveJumpingGoal
extends Goal {
    public DiveJumpingGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP));
    }

    protected float updatePitch(float f, float g, float h) {
        float i;
        for (i = g - f; i < -180.0f; i += 360.0f) {
        }
        while (i >= 180.0f) {
            i -= 360.0f;
        }
        return f + h * i;
    }
}

