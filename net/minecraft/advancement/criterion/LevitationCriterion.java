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
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class LevitationCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("levitation");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_9006(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("duration"));
        return new Conditions(distancePredicate, intRange);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, vec3d, i));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9006(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final DistancePredicate distance;
        private final NumberRange.IntRange duration;

        public Conditions(DistancePredicate distancePredicate, NumberRange.IntRange intRange) {
            super(ID);
            this.distance = distancePredicate;
            this.duration = intRange;
        }

        public static Conditions create(DistancePredicate distancePredicate) {
            return new Conditions(distancePredicate, NumberRange.IntRange.ANY);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
            if (!this.distance.test(vec3d.x, vec3d.y, vec3d.z, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ())) {
                return false;
            }
            return this.duration.test(i);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("distance", this.distance.serialize());
            jsonObject.add("duration", this.duration.toJson());
            return jsonObject;
        }
    }
}

