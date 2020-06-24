/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.MobEntity;

public class StopAndLookAtEntityGoal
extends LookAtEntityGoal {
    public StopAndLookAtEntityGoal(MobEntity mobEntity, Class<? extends LivingEntity> class_, float f, float g) {
        super(mobEntity, class_, f, g);
        this.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
    }
}

