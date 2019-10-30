package net.minecraft.advancement.criterion;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;

public abstract class AbstractCriterion<T extends CriterionConditions> implements Criterion<T> {
	private final Map<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>> progressions = Maps.<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>>newIdentityHashMap();

	@Override
	public final void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditionsContainer) {
		((Set)this.progressions.computeIfAbsent(manager, playerAdvancementTracker -> Sets.newHashSet())).add(conditionsContainer);
	}

	@Override
	public final void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditionsContainer) {
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.progressions.get(manager);
		if (set != null) {
			set.remove(conditionsContainer);
			if (set.isEmpty()) {
				this.progressions.remove(manager);
			}
		}
	}

	@Override
	public final void endTracking(PlayerAdvancementTracker tracker) {
		this.progressions.remove(tracker);
	}

	protected void test(PlayerAdvancementTracker tracker, Predicate<T> tester) {
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.progressions.get(tracker);
		if (set != null) {
			List<Criterion.ConditionsContainer<T>> list = null;

			for (Criterion.ConditionsContainer<T> conditionsContainer : set) {
				if (tester.test(conditionsContainer.getConditions())) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<T>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<T> conditionsContainerx : list) {
					conditionsContainerx.grant(tracker);
				}
			}
		}
	}

	protected void grant(PlayerAdvancementTracker tracker) {
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.progressions.get(tracker);
		if (set != null && !set.isEmpty()) {
			for (Criterion.ConditionsContainer<T> conditionsContainer : ImmutableSet.copyOf(set)) {
				conditionsContainer.grant(tracker);
			}
		}
	}
}
