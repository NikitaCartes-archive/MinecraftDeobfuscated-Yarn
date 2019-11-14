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
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ChanneledLightningCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("channeled_lightning");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_8801(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate[] entityPredicates = EntityPredicate.fromJsonArray(jsonObject.get("victims"));
        return new Conditions(entityPredicates);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
        this.test(serverPlayerEntity.getAdvancementTracker(), conditions -> conditions.matches(serverPlayerEntity, collection));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8801(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate[] victims;

        public Conditions(EntityPredicate[] entityPredicates) {
            super(ID);
            this.victims = entityPredicates;
        }

        public static Conditions create(EntityPredicate ... entityPredicates) {
            return new Conditions(entityPredicates);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
            for (EntityPredicate entityPredicate : this.victims) {
                boolean bl = false;
                for (Entity entity : collection) {
                    if (!entityPredicate.test(serverPlayerEntity, entity)) continue;
                    bl = true;
                    break;
                }
                if (bl) continue;
                return false;
            }
            return true;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("victims", EntityPredicate.serializeAll(this.victims));
            return jsonObject;
        }
    }
}

