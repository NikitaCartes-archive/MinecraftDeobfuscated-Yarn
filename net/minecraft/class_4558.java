/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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

public abstract class class_4558<T extends CriterionConditions>
implements Criterion<T> {
    private final Map<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>> field_20735 = Maps.newIdentityHashMap();

    @Override
    public final void beginTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker2, Criterion.ConditionsContainer<T> conditionsContainer) {
        this.field_20735.computeIfAbsent(playerAdvancementTracker2, playerAdvancementTracker -> Sets.newHashSet()).add(conditionsContainer);
    }

    @Override
    public final void endTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<T> conditionsContainer) {
        Set<Criterion.ConditionsContainer<T>> set = this.field_20735.get(playerAdvancementTracker);
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
        Set<Criterion.ConditionsContainer<T>> set = this.field_20735.get(playerAdvancementTracker);
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
                conditionsContainer.apply(playerAdvancementTracker);
            }
        }
    }

    protected void method_22511(PlayerAdvancementTracker playerAdvancementTracker) {
        Set<Criterion.ConditionsContainer<T>> set = this.field_20735.get(playerAdvancementTracker);
        if (set != null && !set.isEmpty()) {
            for (Criterion.ConditionsContainer conditionsContainer : ImmutableSet.copyOf(set)) {
                conditionsContainer.apply(playerAdvancementTracker);
            }
        }
    }
}

