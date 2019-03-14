package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Goals {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final WeightedGoal field_18410 = new WeightedGoal(Integer.MAX_VALUE, new Goal() {
		@Override
		public boolean canStart() {
			return false;
		}
	}) {
		@Override
		public boolean method_19056() {
			return false;
		}
	};
	private final Map<Goal.ControlBit, WeightedGoal> field_18411 = new EnumMap(Goal.ControlBit.class);
	private final Set<WeightedGoal> all = Sets.<WeightedGoal>newLinkedHashSet();
	private final Profiler profiler;
	private final EnumSet<Goal.ControlBit> usedBits = EnumSet.noneOf(Goal.ControlBit.class);
	private int timeInterval = 3;

	public Goals(Profiler profiler) {
		this.profiler = profiler;
	}

	public void add(int i, Goal goal) {
		this.all.add(new WeightedGoal(i, goal));
	}

	public void remove(Goal goal) {
		this.all.stream().filter(weightedGoal -> weightedGoal.getGoal() == goal).filter(WeightedGoal::method_19056).forEach(WeightedGoal::onRemove);
		this.all.removeIf(weightedGoal -> weightedGoal.getGoal() == goal);
	}

	public void tick() {
		this.profiler.push("goalCleanup");
		this.method_19048()
			.filter(
				weightedGoal -> !weightedGoal.method_19056() || weightedGoal.getControlBits().stream().anyMatch(this.usedBits::contains) || !weightedGoal.shouldContinue()
			)
			.forEach(Goal::onRemove);
		this.field_18411.forEach((controlBit, weightedGoal) -> {
			if (!weightedGoal.method_19056()) {
				this.field_18411.remove(controlBit);
			}
		});
		this.profiler.pop();
		this.profiler.push("goalUpdate");
		this.all
			.stream()
			.filter(weightedGoal -> !weightedGoal.method_19056())
			.filter(weightedGoal -> weightedGoal.getControlBits().stream().noneMatch(this.usedBits::contains))
			.filter(
				weightedGoal -> weightedGoal.getControlBits()
						.stream()
						.allMatch(controlBit -> ((WeightedGoal)this.field_18411.getOrDefault(controlBit, field_18410)).method_19055(weightedGoal))
			)
			.filter(WeightedGoal::canStart)
			.forEach(weightedGoal -> {
				weightedGoal.getControlBits().forEach(controlBit -> {
					WeightedGoal weightedGoal2 = (WeightedGoal)this.field_18411.getOrDefault(controlBit, field_18410);
					weightedGoal2.onRemove();
					this.field_18411.put(controlBit, weightedGoal);
				});
				weightedGoal.start();
			});
		this.profiler.pop();
		this.profiler.push("goalTick");
		this.method_19048().forEach(WeightedGoal::tick);
		this.profiler.pop();
	}

	public Stream<WeightedGoal> method_19048() {
		return this.all.stream().filter(WeightedGoal::method_19056);
	}

	public void addBits(Goal.ControlBit controlBit) {
		this.usedBits.add(controlBit);
	}

	public void removeBits(Goal.ControlBit controlBit) {
		this.usedBits.remove(controlBit);
	}

	public void changeBits(Goal.ControlBit controlBit, boolean bl) {
		if (bl) {
			this.removeBits(controlBit);
		} else {
			this.addBits(controlBit);
		}
	}
}
