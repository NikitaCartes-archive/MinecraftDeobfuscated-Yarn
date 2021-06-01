/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class LightningStrikeCriterion
extends AbstractCriterion<Conditions> {
    static final Identifier ID = new Identifier("lightning_strike");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "lightning", advancementEntityPredicateDeserializer);
        EntityPredicate.Extended extended3 = EntityPredicate.Extended.getInJson(jsonObject, "bystander", advancementEntityPredicateDeserializer);
        return new Conditions(extended, extended2, extended3);
    }

    public void test(ServerPlayerEntity player, LightningEntity lightning, List<Entity> bystanders) {
        List list = bystanders.stream().map(bystander -> EntityPredicate.createAdvancementEntityLootContext(player, bystander)).collect(Collectors.toList());
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, lightning);
        this.test(player, conditions -> conditions.test(lootContext, list));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended lightning;
        private final EntityPredicate.Extended bystander;

        public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended lightning, EntityPredicate.Extended bystander) {
            super(ID, player);
            this.lightning = lightning;
            this.bystander = bystander;
        }

        public static Conditions create(EntityPredicate lightning, EntityPredicate bystander) {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(lightning), EntityPredicate.Extended.ofLegacy(bystander));
        }

        public boolean test(LootContext lightning, List<LootContext> bystanders) {
            if (!this.lightning.test(lightning)) {
                return false;
            }
            if (this.bystander != EntityPredicate.Extended.EMPTY) {
                if (bystanders.stream().noneMatch(this.bystander::test)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("lightning", this.lightning.toJson(predicateSerializer));
            jsonObject.add("bystander", this.bystander.toJson(predicateSerializer));
            return jsonObject;
        }
    }
}

