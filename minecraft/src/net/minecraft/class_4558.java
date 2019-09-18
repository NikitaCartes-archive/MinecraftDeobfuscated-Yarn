package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;

public abstract class class_4558<T extends CriterionConditions> implements Criterion<T> {
	private final Map<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>> field_20735 = Maps.<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>>newIdentityHashMap();

	@Override
	public final void beginTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<T> conditionsContainer) {
		((Set)this.field_20735.computeIfAbsent(playerAdvancementTracker, playerAdvancementTrackerx -> Sets.newHashSet())).add(conditionsContainer);
	}

	@Override
	public final void endTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<T> conditionsContainer) {
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.field_20735.get(playerAdvancementTracker);
		if (set != null) {
			set.remove(conditionsContainer);
			if (set.isEmpty()) {
				this.field_20735.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public final void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.field_20735.remove(playerAdvancementTracker);
	}

	protected void method_22510(PlayerAdvancementTracker playerAdvancementTracker, Predicate<T> predicate) {
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.field_20735.get(playerAdvancementTracker);
		if (set != null) {
			List<Criterion.ConditionsContainer<T>> list = null;

			for (Criterion.ConditionsContainer<T> conditionsContainer : set) {
				if (predicate.test(conditionsContainer.getConditions())) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<T>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<T> conditionsContainerx : list) {
					conditionsContainerx.apply(playerAdvancementTracker);
				}
			}
		}
	}

	protected void method_22511(PlayerAdvancementTracker playerAdvancementTracker) {
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.field_20735.get(playerAdvancementTracker);
		if (set != null && !set.isEmpty()) {
			for (Criterion.ConditionsContainer<T> conditionsContainer : ImmutableSet.copyOf(set)) {
				conditionsContainer.apply(playerAdvancementTracker);
			}
		}
	}
}
