/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FishingRodHookedCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("fishing_rod_hooked");
    private final Map<PlayerAdvancementTracker, Handler> field_9618 = Maps.newHashMap();

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void beginTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler = this.field_9618.get(playerAdvancementTracker);
        if (handler == null) {
            handler = new Handler(playerAdvancementTracker);
            this.field_9618.put(playerAdvancementTracker, handler);
        }
        handler.addCondition(conditionsContainer);
    }

    @Override
    public void endTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler = this.field_9618.get(playerAdvancementTracker);
        if (handler != null) {
            handler.removeCondition(conditionsContainer);
            if (handler.isEmpty()) {
                this.field_9618.remove(playerAdvancementTracker);
            }
        }
    }

    @Override
    public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
        this.field_9618.remove(playerAdvancementTracker);
    }

    public Conditions method_8941(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("rod"));
        EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("entity"));
        ItemPredicate itemPredicate2 = ItemPredicate.deserialize(jsonObject.get("item"));
        return new Conditions(itemPredicate, entityPredicate, itemPredicate2);
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
        Handler handler = this.field_9618.get(serverPlayerEntity.getAdvancementManager());
        if (handler != null) {
            handler.handle(serverPlayerEntity, itemStack, fishingBobberEntity, collection);
        }
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8941(jsonObject, jsonDeserializationContext);
    }

    static class Handler {
        private final PlayerAdvancementTracker manager;
        private final Set<Criterion.ConditionsContainer<Conditions>> conditions = Sets.newHashSet();

        public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
            this.manager = playerAdvancementTracker;
        }

        public boolean isEmpty() {
            return this.conditions.isEmpty();
        }

        public void addCondition(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.add(conditionsContainer);
        }

        public void removeCondition(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.remove(conditionsContainer);
        }

        public void handle(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
            ArrayList<Criterion.ConditionsContainer<Conditions>> list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().matches(serverPlayerEntity, itemStack, fishingBobberEntity, collection)) continue;
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(conditionsContainer);
            }
            if (list != null) {
                for (Criterion.ConditionsContainer<Conditions> conditionsContainer : list) {
                    conditionsContainer.apply(this.manager);
                }
            }
        }
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate rod;
        private final EntityPredicate entity;
        private final ItemPredicate item;

        public Conditions(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
            super(ID);
            this.rod = itemPredicate;
            this.entity = entityPredicate;
            this.item = itemPredicate2;
        }

        public static Conditions create(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
            return new Conditions(itemPredicate, entityPredicate, itemPredicate2);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
            if (!this.rod.test(itemStack)) {
                return false;
            }
            if (!this.entity.test(serverPlayerEntity, fishingBobberEntity.hookedEntity)) {
                return false;
            }
            if (this.item != ItemPredicate.ANY) {
                ItemEntity itemEntity;
                boolean bl = false;
                if (fishingBobberEntity.hookedEntity instanceof ItemEntity && this.item.test((itemEntity = (ItemEntity)fishingBobberEntity.hookedEntity).getStack())) {
                    bl = true;
                }
                for (ItemStack itemStack2 : collection) {
                    if (!this.item.test(itemStack2)) continue;
                    bl = true;
                    break;
                }
                if (!bl) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("rod", this.rod.serialize());
            jsonObject.add("entity", this.entity.serialize());
            jsonObject.add("item", this.item.serialize());
            return jsonObject;
        }
    }
}

