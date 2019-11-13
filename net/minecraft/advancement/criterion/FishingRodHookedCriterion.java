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

    public Conditions method_8941(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("rod"));
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
        ItemPredicate itemPredicate2 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(itemPredicate, entityPredicate, itemPredicate2);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, itemStack, fishingBobberEntity, collection));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8941(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate rod;
        private final EntityPredicate hookedEntity;
        private final ItemPredicate caughtItem;

        public Conditions(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
            super(ID);
            this.rod = itemPredicate;
            this.hookedEntity = entityPredicate;
            this.caughtItem = itemPredicate2;
        }

        public static Conditions create(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
            return new Conditions(itemPredicate, entityPredicate, itemPredicate2);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
            if (!this.rod.test(itemStack)) {
                return false;
            }
            if (!this.hookedEntity.test(serverPlayerEntity, fishingBobberEntity.hookedEntity)) {
                return false;
            }
            if (this.caughtItem != ItemPredicate.ANY) {
                ItemEntity itemEntity;
                boolean bl = false;
                if (fishingBobberEntity.hookedEntity instanceof ItemEntity && this.caughtItem.test((itemEntity = (ItemEntity)fishingBobberEntity.hookedEntity).getStack())) {
                    bl = true;
                }
                for (ItemStack itemStack2 : collection) {
                    if (!this.caughtItem.test(itemStack2)) continue;
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
            jsonObject.add("entity", this.hookedEntity.serialize());
            jsonObject.add("item", this.caughtItem.toJson());
            return jsonObject;
        }
    }
}

