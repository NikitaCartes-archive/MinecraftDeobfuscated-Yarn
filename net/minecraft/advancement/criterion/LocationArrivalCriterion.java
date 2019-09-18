/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.class_4558;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class LocationArrivalCriterion
extends class_4558<Conditions> {
    private final Identifier id;

    public LocationArrivalCriterion(Identifier identifier) {
        this.id = identifier;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    public Conditions method_9026(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        LocationPredicate locationPredicate = LocationPredicate.deserialize(jsonObject);
        return new Conditions(this.id, locationPredicate);
    }

    public void handle(ServerPlayerEntity serverPlayerEntity) {
        this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity.getServerWorld(), serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9026(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final LocationPredicate location;

        public Conditions(Identifier identifier, LocationPredicate locationPredicate) {
            super(identifier);
            this.location = locationPredicate;
        }

        public static Conditions create(LocationPredicate locationPredicate) {
            return new Conditions(Criterions.LOCATION.id, locationPredicate);
        }

        public static Conditions createSleptInBed() {
            return new Conditions(Criterions.SLEPT_IN_BED.id, LocationPredicate.ANY);
        }

        public static Conditions createHeroOfTheVillage() {
            return new Conditions(Criterions.HERO_OF_THE_VILLAGE.id, LocationPredicate.ANY);
        }

        public boolean matches(ServerWorld serverWorld, double d, double e, double f) {
            return this.location.test(serverWorld, d, e, f);
        }

        @Override
        public JsonElement toJson() {
            return this.location.serialize();
        }
    }
}

