/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;

public abstract class AbstractCriterion<T extends CriterionConditions>
implements Criterion<T> {
    private final Map<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>> progressions = Maps.newIdentityHashMap();

    @Override
    public final void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditionsContainer) {
        this.progressions.computeIfAbsent(manager, playerAdvancementTracker -> Sets.newHashSet()).add(conditionsContainer);
    }

    @Override
    public final void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditionsContainer) {
        Set<Criterion.ConditionsContainer<T>> set = this.progressions.get(manager);
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
        Set<Criterion.ConditionsContainer<T>> set = this.progressions.get(tracker);
        if (set == null) {
            return;
        }
        ArrayList<Criterion.ConditionsContainer<T>> list = null;
        for (Criterion.ConditionsContainer<T> conditionsContainer : set) {
            if (!tester.test(conditionsContainer.getConditions())) continue;
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(conditionsContainer);
        }
        if (list != null) {
            for (Criterion.ConditionsContainer<Object> conditionsContainer : list) {
                conditionsContainer.grant(tracker);
            }
        }
    }

    protected void grant(PlayerAdvancementTracker tracker) {
        Set<Criterion.ConditionsContainer<T>> set = this.progressions.get(tracker);
        if (set != null && !set.isEmpty()) {
            for (Criterion.ConditionsContainer conditionsContainer : ImmutableSet.copyOf(set)) {
                conditionsContainer.grant(tracker);
            }
        }
    }
}

