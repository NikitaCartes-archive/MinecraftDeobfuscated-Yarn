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
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class NetherTravelCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("nether_travel");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_9078(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("entered"));
        LocationPredicate locationPredicate2 = LocationPredicate.fromJson(jsonObject.get("exited"));
        DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
        return new Conditions(locationPredicate, locationPredicate2, distancePredicate);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity.getServerWorld(), vec3d, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ()));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9078(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final LocationPredicate enteredPos;
        private final LocationPredicate exitedPos;
        private final DistancePredicate distance;

        public Conditions(LocationPredicate locationPredicate, LocationPredicate locationPredicate2, DistancePredicate distancePredicate) {
            super(ID);
            this.enteredPos = locationPredicate;
            this.exitedPos = locationPredicate2;
            this.distance = distancePredicate;
        }

        public static Conditions distance(DistancePredicate distancePredicate) {
            return new Conditions(LocationPredicate.ANY, LocationPredicate.ANY, distancePredicate);
        }

        public boolean matches(ServerWorld serverWorld, Vec3d vec3d, double d, double e, double f) {
            if (!this.enteredPos.test(serverWorld, vec3d.x, vec3d.y, vec3d.z)) {
                return false;
            }
            if (!this.exitedPos.test(serverWorld, d, e, f)) {
                return false;
            }
            return this.distance.test(vec3d.x, vec3d.y, vec3d.z, d, e, f);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("entered", this.enteredPos.toJson());
            jsonObject.add("exited", this.exitedPos.toJson());
            jsonObject.add("distance", this.distance.serialize());
            return jsonObject;
        }
    }
}

