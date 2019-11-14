/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class BredAnimalsCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("bred_animals");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_854(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("parent"));
        EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("partner"));
        EntityPredicate entityPredicate3 = EntityPredicate.fromJson(jsonObject.get("child"));
        return new Conditions(entityPredicate, entityPredicate2, entityPredicate3);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, @Nullable AnimalEntity animalEntity2, @Nullable PassiveEntity passiveEntity) {
        this.test(serverPlayerEntity.getAdvancementTracker(), conditions -> conditions.matches(serverPlayerEntity, animalEntity, animalEntity2, passiveEntity));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_854(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate parent;
        private final EntityPredicate partner;
        private final EntityPredicate child;

        public Conditions(EntityPredicate entityPredicate, EntityPredicate entityPredicate2, EntityPredicate entityPredicate3) {
            super(ID);
            this.parent = entityPredicate;
            this.partner = entityPredicate2;
            this.child = entityPredicate3;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
        }

        public static Conditions create(EntityPredicate.Builder builder) {
            return new Conditions(builder.build(), EntityPredicate.ANY, EntityPredicate.ANY);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, @Nullable AnimalEntity animalEntity2, @Nullable PassiveEntity passiveEntity) {
            if (!this.child.test(serverPlayerEntity, passiveEntity)) {
                return false;
            }
            return this.parent.test(serverPlayerEntity, animalEntity) && this.partner.test(serverPlayerEntity, animalEntity2) || this.parent.test(serverPlayerEntity, animalEntity2) && this.partner.test(serverPlayerEntity, animalEntity);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("parent", this.parent.serialize());
            jsonObject.add("partner", this.partner.serialize());
            jsonObject.add("child", this.child.serialize());
            return jsonObject;
        }
    }
}

