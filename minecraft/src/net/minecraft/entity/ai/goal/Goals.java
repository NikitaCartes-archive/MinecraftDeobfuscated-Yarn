package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.class_4135;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Goals {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final class_4135 field_18410 = new class_4135(Integer.MAX_VALUE, new Goal() {
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
	private final Map<Goal.class_4134, class_4135> field_18411 = new EnumMap(Goal.class_4134.class);
	private final Set<class_4135> all = Sets.<class_4135>newLinkedHashSet();
	private final Profiler profiler;
	private final EnumSet<Goal.class_4134> usedBits = EnumSet.noneOf(Goal.class_4134.class);
	private int timeInterval = 3;

	public Goals(Profiler profiler) {
		this.profiler = profiler;
	}

	public void add(int i, Goal goal) {
		this.all.add(new class_4135(i, goal));
	}

	public void remove(Goal goal) {
		this.all.stream().filter(arg -> arg.method_19058() == goal).filter(class_4135::method_19056).forEach(class_4135::onRemove);
		this.all.removeIf(arg -> arg.method_19058() == goal);
	}

	public void tick() {
		this.profiler.push("goalCleanup");
		this.method_19048()
			.filter(arg -> !arg.method_19056() || arg.getControlBits().stream().anyMatch(this.usedBits::contains) || !arg.shouldContinue())
			.forEach(Goal::onRemove);
		this.field_18411.forEach((arg, arg2) -> {
			if (!arg2.method_19056()) {
				this.field_18411.remove(arg);
			}
		});
		this.profiler.pop();
		this.profiler.push("goalUpdate");
		this.all
			.stream()
			.filter(arg -> !arg.method_19056())
			.filter(arg -> arg.getControlBits().stream().noneMatch(this.usedBits::contains))
			.filter(arg -> arg.getControlBits().stream().allMatch(arg2 -> ((class_4135)this.field_18411.getOrDefault(arg2, field_18410)).method_19055(arg)))
			.filter(class_4135::canStart)
			.forEach(arg -> {
				arg.getControlBits().forEach(arg2 -> {
					class_4135 lv = (class_4135)this.field_18411.getOrDefault(arg2, field_18410);
					lv.onRemove();
					this.field_18411.put(arg2, arg);
				});
				arg.start();
			});
		this.profiler.pop();
		this.profiler.push("goalTick");
		this.method_19048().forEach(class_4135::tick);
		this.profiler.pop();
	}

	public Stream<class_4135> method_19048() {
		return this.all.stream().filter(class_4135::method_19056);
	}

	public void addBits(Goal.class_4134 arg) {
		this.usedBits.add(arg);
	}

	public void removeBits(Goal.class_4134 arg) {
		this.usedBits.remove(arg);
	}

	public void changeBits(Goal.class_4134 arg, boolean bl) {
		if (bl) {
			this.removeBits(arg);
		} else {
			this.addBits(arg);
		}
	}
}
