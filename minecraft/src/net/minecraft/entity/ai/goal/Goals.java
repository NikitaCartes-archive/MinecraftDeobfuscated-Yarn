package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Goals {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Set<Goals.WeighedAiGoal> all = Sets.<Goals.WeighedAiGoal>newLinkedHashSet();
	private final Set<Goals.WeighedAiGoal> executing = Sets.<Goals.WeighedAiGoal>newLinkedHashSet();
	private final Profiler profiler;
	private int timer;
	private int timeInterval = 3;
	private int usedBits;

	public Goals(Profiler profiler) {
		this.profiler = profiler;
	}

	public void add(int i, Goal goal) {
		this.all.add(new Goals.WeighedAiGoal(i, goal));
	}

	public void remove(Goal goal) {
		Iterator<Goals.WeighedAiGoal> iterator = this.all.iterator();

		while (iterator.hasNext()) {
			Goals.WeighedAiGoal weighedAiGoal = (Goals.WeighedAiGoal)iterator.next();
			Goal goal2 = weighedAiGoal.goal;
			if (goal2 == goal) {
				if (weighedAiGoal.executing) {
					weighedAiGoal.executing = false;
					weighedAiGoal.goal.onRemove();
					this.executing.remove(weighedAiGoal);
				}

				iterator.remove();
				return;
			}
		}
	}

	public void tick() {
		this.profiler.push("goalSetup");
		if (this.timer++ % this.timeInterval == 0) {
			for (Goals.WeighedAiGoal weighedAiGoal : this.all) {
				if (weighedAiGoal.executing) {
					if (!this.canStart(weighedAiGoal) || !this.shouldContinueExecution(weighedAiGoal)) {
						weighedAiGoal.executing = false;
						weighedAiGoal.goal.onRemove();
						this.executing.remove(weighedAiGoal);
					}
				} else if (this.canStart(weighedAiGoal) && weighedAiGoal.goal.canStart()) {
					weighedAiGoal.executing = true;
					weighedAiGoal.goal.start();
					this.executing.add(weighedAiGoal);
				}
			}
		} else {
			Iterator<Goals.WeighedAiGoal> iterator = this.executing.iterator();

			while (iterator.hasNext()) {
				Goals.WeighedAiGoal weighedAiGoalx = (Goals.WeighedAiGoal)iterator.next();
				if (!this.shouldContinueExecution(weighedAiGoalx)) {
					weighedAiGoalx.executing = false;
					weighedAiGoalx.goal.onRemove();
					iterator.remove();
				}
			}
		}

		this.profiler.pop();
		if (!this.executing.isEmpty()) {
			this.profiler.push("goalTick");

			for (Goals.WeighedAiGoal weighedAiGoalx : this.executing) {
				weighedAiGoalx.goal.tick();
			}

			this.profiler.pop();
		}
	}

	private boolean shouldContinueExecution(Goals.WeighedAiGoal weighedAiGoal) {
		return weighedAiGoal.goal.shouldContinue();
	}

	private boolean canStart(Goals.WeighedAiGoal weighedAiGoal) {
		if (this.executing.isEmpty()) {
			return true;
		} else if (this.isControlBitUsed(weighedAiGoal.goal.getControlBits())) {
			return false;
		} else {
			for (Goals.WeighedAiGoal weighedAiGoal2 : this.executing) {
				if (weighedAiGoal2 != weighedAiGoal) {
					if (weighedAiGoal.priority >= weighedAiGoal2.priority) {
						if (!this.areCompatible(weighedAiGoal, weighedAiGoal2)) {
							return false;
						}
					} else if (!weighedAiGoal2.goal.canStop()) {
						return false;
					}
				}
			}

			return true;
		}
	}

	private boolean areCompatible(Goals.WeighedAiGoal weighedAiGoal, Goals.WeighedAiGoal weighedAiGoal2) {
		return (weighedAiGoal.goal.getControlBits() & weighedAiGoal2.goal.getControlBits()) == 0;
	}

	public boolean isControlBitUsed(int i) {
		return (this.usedBits & i) > 0;
	}

	public void addBits(int i) {
		this.usedBits |= i;
	}

	public void removeBits(int i) {
		this.usedBits &= ~i;
	}

	public void changeBits(int i, boolean bl) {
		if (bl) {
			this.removeBits(i);
		} else {
			this.addBits(i);
		}
	}

	class WeighedAiGoal {
		public final Goal goal;
		public final int priority;
		public boolean executing;

		public WeighedAiGoal(int i, Goal goal) {
			this.priority = i;
			this.goal = goal;
		}

		public boolean equals(@Nullable Object object) {
			if (this == object) {
				return true;
			} else {
				return object != null && this.getClass() == object.getClass() ? this.goal.equals(((Goals.WeighedAiGoal)object).goal) : false;
			}
		}

		public int hashCode() {
			return this.goal.hashCode();
		}
	}
}
