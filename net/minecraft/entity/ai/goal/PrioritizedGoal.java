/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

public class PrioritizedGoal
extends Goal {
    private final Goal goal;
    private final int priority;
    private boolean running;

    public PrioritizedGoal(int priority, Goal goal) {
        this.priority = priority;
        this.goal = goal;
    }

    public boolean canBeReplacedBy(PrioritizedGoal goal) {
        return this.canStop() && goal.getPriority() < this.getPriority();
    }

    @Override
    public boolean canStart() {
        return this.goal.canStart();
    }

    @Override
    public boolean shouldContinue() {
        return this.goal.shouldContinue();
    }

    @Override
    public boolean canStop() {
        return this.goal.canStop();
    }

    @Override
    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.goal.start();
    }

    @Override
    public void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
        this.goal.stop();
    }

    @Override
    public void tick() {
        this.goal.tick();
    }

    @Override
    public void setControls(EnumSet<Goal.Control> controls) {
        this.goal.setControls(controls);
    }

    @Override
    public EnumSet<Goal.Control> getControls() {
        return this.goal.getControls();
    }

    public boolean isRunning() {
        return this.running;
    }

    public int getPriority() {
        return this.priority;
    }

    public Goal getGoal() {
        return this.goal;
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        return this.goal.equals(((PrioritizedGoal)object).goal);
    }

    public int hashCode() {
        return this.goal.hashCode();
    }
}

