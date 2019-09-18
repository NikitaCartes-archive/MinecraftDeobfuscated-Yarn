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
    public final void beginTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker2, Criterion.ConditionsContainer<T> conditionsContainer) {
        this.progressions.computeIfAbsent(playerAdvancementTracker2, playerAdvancementTracker -> Sets.newHashSet()).add(conditionsContainer);
    }

    @Override
    public final void endTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<T> conditionsContainer) {
        Set<Criterion.ConditionsContainer<T>> set = this.progressions.get(playerAdvancementTracker);
        if (set != null) {
            set.remove(conditionsContainer);
            if (set.isEmpty()) {
                this.progressions.remove(playerAdvancementTracker);
            }
        }
    }

    @Override
    public final void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
        this.progressions.remove(playerAdvancementTracker);
    }

    protected void test(PlayerAdvancementTracker playerAdvancementTracker, Predicate<T> predicate) {
        Set<Criterion.ConditionsContainer<T>> set = this.progressions.get(playerAdvancementTracker);
        if (set == null) {
            return;
        }
        ArrayList<Criterion.ConditionsContainer<T>> list = null;
        for (Criterion.ConditionsContainer<T> conditionsContainer : set) {
            if (!predicate.test(conditionsContainer.getConditions())) continue;
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(conditionsContainer);
        }
        if (list != null) {
            for (Criterion.ConditionsContainer<Object> conditionsContainer : list) {
                conditionsContainer.grant(playerAdvancementTracker);
            }
        }
    }

    protected void grant(PlayerAdvancementTracker playerAdvancementTracker) {
        Set<Criterion.ConditionsContainer<T>> set = this.progressions.get(playerAdvancementTracker);
        if (set != null && !set.isEmpty()) {
            for (Criterion.ConditionsContainer conditionsContainer : ImmutableSet.copyOf(set)) {
                conditionsContainer.grant(playerAdvancementTracker);
            }
        }
    }
}

