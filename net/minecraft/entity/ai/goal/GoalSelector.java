/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.WeightedGoal;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final WeightedGoal activeGoal = new WeightedGoal(Integer.MAX_VALUE, new Goal(){

        @Override
        public boolean canStart() {
            return false;
        }
    }){

        @Override
        public boolean isRunning() {
            return false;
        }
    };
    private final Map<Goal.Control, WeightedGoal> goalsByControl = new EnumMap<Goal.Control, WeightedGoal>(Goal.Control.class);
    private final Set<WeightedGoal> goals = Sets.newLinkedHashSet();
    private final Profiler profiler;
    private final EnumSet<Goal.Control> disabledControls = EnumSet.noneOf(Goal.Control.class);
    private int timeInterval = 3;

    public GoalSelector(Profiler profiler) {
        this.profiler = profiler;
    }

    public void add(int i, Goal goal) {
        this.goals.add(new WeightedGoal(i, goal));
    }

    public void remove(Goal goal) {
        this.goals.stream().filter(weightedGoal -> weightedGoal.getGoal() == goal).filter(WeightedGoal::isRunning).forEach(WeightedGoal::stop);
        this.goals.removeIf(weightedGoal -> weightedGoal.getGoal() == goal);
    }

    public void tick() {
        this.profiler.push("goalCleanup");
        this.getRunningGoals().filter(weightedGoal -> {
            if (!weightedGoal.isRunning()) return true;
            if (weightedGoal.getControls().stream().anyMatch(this.disabledControls::contains)) return true;
            if (weightedGoal.shouldContinue()) return false;
            return true;
        }).forEach(Goal::stop);
        this.goalsByControl.forEach((control, weightedGoal) -> {
            if (!weightedGoal.isRunning()) {
                this.goalsByControl.remove(control);
            }
        });
        this.profiler.pop();
        this.profiler.push("goalUpdate");
        this.goals.stream().filter(weightedGoal -> !weightedGoal.isRunning()).filter(weightedGoal -> weightedGoal.getControls().stream().noneMatch(this.disabledControls::contains)).filter(weightedGoal -> weightedGoal.getControls().stream().allMatch(control -> this.goalsByControl.getOrDefault(control, activeGoal).canBeReplacedBy((WeightedGoal)weightedGoal))).filter(WeightedGoal::canStart).forEach(weightedGoal -> {
            weightedGoal.getControls().forEach(control -> {
                WeightedGoal weightedGoal2 = this.goalsByControl.getOrDefault(control, activeGoal);
                weightedGoal2.stop();
                this.goalsByControl.put((Goal.Control)((Object)((Object)control)), (WeightedGoal)weightedGoal);
            });
            weightedGoal.start();
        });
        this.profiler.pop();
        this.profiler.push("goalTick");
        this.getRunningGoals().forEach(WeightedGoal::tick);
        this.profiler.pop();
    }

    public Stream<WeightedGoal> getRunningGoals() {
        return this.goals.stream().filter(WeightedGoal::isRunning);
    }

    public void disableControl(Goal.Control control) {
        this.disabledControls.add(control);
    }

    public void enableControl(Goal.Control control) {
        this.disabledControls.remove((Object)control);
    }

    public void setControlEnabled(Goal.Control control, boolean bl) {
        if (bl) {
            this.enableControl(control);
        } else {
            this.disableControl(control);
        }
    }
}

