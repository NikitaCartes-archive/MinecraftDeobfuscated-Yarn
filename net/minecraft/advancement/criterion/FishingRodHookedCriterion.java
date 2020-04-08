/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FishingRodHookedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("fishing_rod_hooked");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("rod"));
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
        ItemPredicate itemPredicate2 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(itemPredicate, entityPredicate, itemPredicate2);
    }

    public void trigger(ServerPlayerEntity player, ItemStack rodStack, FishingBobberEntity bobber, Collection<ItemStack> fishingLoots) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, rodStack, bobber, fishingLoots));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate rod;
        private final EntityPredicate hookedEntity;
        private final ItemPredicate caughtItem;

        public Conditions(ItemPredicate rod, EntityPredicate bobber, ItemPredicate item) {
            super(ID);
            this.rod = rod;
            this.hookedEntity = bobber;
            this.caughtItem = item;
        }

        public static Conditions create(ItemPredicate rod, EntityPredicate bobber, ItemPredicate item) {
            return new Conditions(rod, bobber, item);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, ItemStack rodStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> fishingLoots) {
            if (!this.rod.test(rodStack)) {
                return false;
            }
            Entity entity = fishingBobberEntity.method_26957();
            if (!this.hookedEntity.test(serverPlayerEntity, entity)) {
                return false;
            }
            if (this.caughtItem != ItemPredicate.ANY) {
                ItemEntity itemEntity;
                boolean bl = false;
                if (entity instanceof ItemEntity && this.caughtItem.test((itemEntity = (ItemEntity)entity).getStack())) {
                    bl = true;
                }
                for (ItemStack itemStack : fishingLoots) {
                    if (!this.caughtItem.test(itemStack)) continue;
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
            jsonObject.add("rod", this.rod.toJson());
            jsonObject.add("entity", this.hookedEntity.toJson());
            jsonObject.add("item", this.caughtItem.toJson());
            return jsonObject;
        }
    }
}

