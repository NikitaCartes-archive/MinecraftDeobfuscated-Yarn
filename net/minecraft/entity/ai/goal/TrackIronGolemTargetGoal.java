/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;

public class TrackIronGolemTargetGoal
extends TrackTargetGoal {
    private final IronGolemEntity golem;
    private LivingEntity target;

    public TrackIronGolemTargetGoal(IronGolemEntity ironGolemEntity) {
        super(ironGolemEntity, false, true);
        this.golem = ironGolemEntity;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public void start() {
        this.golem.setTarget(this.target);
        super.start();
    }
}

