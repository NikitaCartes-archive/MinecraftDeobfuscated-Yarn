package net.minecraft.entity.ai.goal;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.util.profiler.Profiler;

/**
 * Manages a set of goals, which are competing for certain controls on the mob.
 * Multiple goals can run at the same time, so long as they are all using different controls.
 * 
 * <p>A running goal will always be replaced with a goal with a <i>lower</i> priority, if
 * such a goal exists, it's competing for the same control and its
 * {@link Goal#canStart() canStart()} method returns true. (Note that some goals randomize
 * this method.)
 * 
 * <p>If two goals have the same priority and are competing for the same control, then one
 * goal cannot replace the other if it's running. The goal selector tries to run goals in the order
 * they were added.
 */
public class GoalSelector {
	private static final PrioritizedGoal REPLACEABLE_GOAL = new PrioritizedGoal(Integer.MAX_VALUE, new Goal() {
		@Override
		public boolean canStart() {
			return false;
		}
	}) {
		@Override
		public boolean isRunning() {
			return false;
		}
	};
	private final Map<Goal.Control, PrioritizedGoal> goalsByControl = new EnumMap(Goal.Control.class);
	private final Set<PrioritizedGoal> goals = new ObjectLinkedOpenHashSet<>();
	private final Supplier<Profiler> profiler;
	private final EnumSet<Goal.Control> disabledControls = EnumSet.noneOf(Goal.Control.class);

	public GoalSelector(Supplier<Profiler> profiler) {
		this.profiler = profiler;
	}

	/**
	 * Adds a goal with a certain priority. Goals with <i>lower</i> priorities will replace running goals
	 * with a higher priority.
	 */
	public void add(int priority, Goal goal) {
		this.goals.add(new PrioritizedGoal(priority, goal));
	}

	@VisibleForTesting
	public void clear(Predicate<Goal> predicate) {
		this.goals.removeIf(goal -> predicate.test(goal.getGoal()));
	}

	public void remove(Goal goal) {
		for (PrioritizedGoal prioritizedGoal : this.goals) {
			if (prioritizedGoal.getGoal() == goal && prioritizedGoal.isRunning()) {
				prioritizedGoal.stop();
			}
		}

		this.goals.removeIf(prioritizedGoalx -> prioritizedGoalx.getGoal() == goal);
	}

	private static boolean usesAny(PrioritizedGoal goal, EnumSet<Goal.Control> controls) {
		for (Goal.Control control : goal.getControls()) {
			if (controls.contains(control)) {
				return true;
			}
		}

		return false;
	}

	private static boolean canReplaceAll(PrioritizedGoal goal, Map<Goal.Control, PrioritizedGoal> goalsByControl) {
		for (Goal.Control control : goal.getControls()) {
			if (!((PrioritizedGoal)goalsByControl.getOrDefault(control, REPLACEABLE_GOAL)).canBeReplacedBy(goal)) {
				return false;
			}
		}

		return true;
	}

	public void tick() {
		Profiler profiler = (Profiler)this.profiler.get();
		profiler.push("goalCleanup");

		for (PrioritizedGoal prioritizedGoal : this.goals) {
			if (prioritizedGoal.isRunning() && (usesAny(prioritizedGoal, this.disabledControls) || !prioritizedGoal.shouldContinue())) {
				prioritizedGoal.stop();
			}
		}

		this.goalsByControl.entrySet().removeIf(entry -> !((PrioritizedGoal)entry.getValue()).isRunning());
		profiler.pop();
		profiler.push("goalUpdate");

		for (PrioritizedGoal prioritizedGoalx : this.goals) {
			if (!prioritizedGoalx.isRunning()
				&& !usesAny(prioritizedGoalx, this.disabledControls)
				&& canReplaceAll(prioritizedGoalx, this.goalsByControl)
				&& prioritizedGoalx.canStart()) {
				for (Goal.Control control : prioritizedGoalx.getControls()) {
					PrioritizedGoal prioritizedGoal2 = (PrioritizedGoal)this.goalsByControl.getOrDefault(control, REPLACEABLE_GOAL);
					prioritizedGoal2.stop();
					this.goalsByControl.put(control, prioritizedGoalx);
				}

				prioritizedGoalx.start();
			}
		}

		profiler.pop();
		this.tickGoals(true);
	}

	public void tickGoals(boolean tickAll) {
		Profiler profiler = (Profiler)this.profiler.get();
		profiler.push("goalTick");

		for (PrioritizedGoal prioritizedGoal : this.goals) {
			if (prioritizedGoal.isRunning() && (tickAll || prioritizedGoal.shouldRunEveryTick())) {
				prioritizedGoal.tick();
			}
		}

		profiler.pop();
	}

	public Set<PrioritizedGoal> getGoals() {
		return this.goals;
	}

	public void disableControl(Goal.Control control) {
		this.disabledControls.add(control);
	}

	public void enableControl(Goal.Control control) {
		this.disabledControls.remove(control);
	}

	public void setControlEnabled(Goal.Control control, boolean enabled) {
		if (enabled) {
			this.enableControl(control);
		} else {
			this.disableControl(control);
		}
	}
}
