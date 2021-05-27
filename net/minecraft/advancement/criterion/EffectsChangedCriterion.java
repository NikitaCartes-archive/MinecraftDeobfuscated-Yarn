/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class EffectsChangedCriterion
extends AbstractCriterion<Conditions> {
    static final Identifier ID = new Identifier("effects_changed");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.fromJson(jsonObject.get("effects"));
        EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "source", advancementEntityPredicateDeserializer);
        return new Conditions(extended, entityEffectPredicate, extended2);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, @Nullable Entity entity) {
        LootContext lootContext = entity != null ? EntityPredicate.createAdvancementEntityLootContext(serverPlayerEntity, entity) : null;
        this.test(serverPlayerEntity, conditions -> conditions.matches(serverPlayerEntity, lootContext));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityEffectPredicate effects;
        private final EntityPredicate.Extended field_33909;

        public Conditions(EntityPredicate.Extended player, EntityEffectPredicate effects, EntityPredicate.Extended extended) {
            super(ID, player);
            this.effects = effects;
            this.field_33909 = extended;
        }

        public static Conditions create(EntityEffectPredicate effects) {
            return new Conditions(EntityPredicate.Extended.EMPTY, effects, EntityPredicate.Extended.EMPTY);
        }

        public static Conditions method_37224(EntityPredicate entityPredicate) {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityEffectPredicate.EMPTY, EntityPredicate.Extended.ofLegacy(entityPredicate));
        }

        public boolean matches(ServerPlayerEntity player, @Nullable LootContext lootContext) {
            if (!this.effects.test(player)) {
                return false;
            }
            return this.field_33909 == EntityPredicate.Extended.EMPTY || lootContext != null && this.field_33909.test(lootContext);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("effects", this.effects.toJson());
            jsonObject.add("source", this.field_33909.toJson(predicateSerializer));
            return jsonObject;
        }
    }
}

