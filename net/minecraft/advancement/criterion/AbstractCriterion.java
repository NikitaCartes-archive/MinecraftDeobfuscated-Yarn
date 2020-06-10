/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractCriterion<T extends AbstractCriterionConditions>
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

    protected abstract T conditionsFromJson(JsonObject var1, EntityPredicate.Extended var2, AdvancementEntityPredicateDeserializer var3);

    @Override
    public final T conditionsFromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        EntityPredicate.Extended extended = EntityPredicate.Extended.getInJson(jsonObject, "player", advancementEntityPredicateDeserializer);
        return this.conditionsFromJson(jsonObject, extended, advancementEntityPredicateDeserializer);
    }

    protected void test(ServerPlayerEntity player, Predicate<T> tester) {
        PlayerAdvancementTracker playerAdvancementTracker = player.getAdvancementTracker();
        Set<Criterion.ConditionsContainer<T>> set = this.progressions.get(playerAdvancementTracker);
        if (set == null || set.isEmpty()) {
            return;
        }
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, player);
        ArrayList<Criterion.ConditionsContainer<T>> list = null;
        for (Criterion.ConditionsContainer<T> conditionsContainer : set) {
            AbstractCriterionConditions abstractCriterionConditions = (AbstractCriterionConditions)conditionsContainer.getConditions();
            if (!abstractCriterionConditions.getPlayerPredicate().test(lootContext) || !tester.test(abstractCriterionConditions)) continue;
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

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, predicateDeserializer);
    }
}

