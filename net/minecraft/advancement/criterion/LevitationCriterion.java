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

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("duration"));
        return new Conditions(distancePredicate, intRange);
    }

    public void trigger(ServerPlayerEntity player, Vec3d startPos, int duration) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, startPos, duration));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final DistancePredicate distance;
        private final NumberRange.IntRange duration;

        public Conditions(DistancePredicate distance, NumberRange.IntRange intRange) {
            super(ID);
            this.distance = distance;
            this.duration = intRange;
        }

        public static Conditions create(DistancePredicate distance) {
            return new Conditions(distance, NumberRange.IntRange.ANY);
        }

        public boolean matches(ServerPlayerEntity player, Vec3d startPos, int duration) {
            if (!this.distance.test(startPos.x, startPos.y, startPos.z, player.getX(), player.getY(), player.getZ())) {
                return false;
            }
            return this.duration.test(duration);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("distance", this.distance.toJson());
            jsonObject.add("duration", this.duration.toJson());
            return jsonObject;
        }
    }
}

