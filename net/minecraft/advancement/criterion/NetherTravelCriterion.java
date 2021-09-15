/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class NetherTravelCriterion
extends AbstractCriterion<Conditions> {
    static final Identifier ID = new Identifier("nether_travel");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("entered"));
        LocationPredicate locationPredicate2 = LocationPredicate.fromJson(jsonObject.get("exited"));
        DistancePredicate distancePredicate = DistancePredicate.fromJson(jsonObject.get("distance"));
        return new Conditions(extended, locationPredicate, locationPredicate2, distancePredicate);
    }

    public void trigger(ServerPlayerEntity player, Vec3d enteredPos) {
        this.trigger(player, (T conditions) -> conditions.matches(player.getWorld(), enteredPos, player.getX(), player.getY(), player.getZ()));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final LocationPredicate enteredPos;
        private final LocationPredicate exitedPos;
        private final DistancePredicate distance;

        public Conditions(EntityPredicate.Extended player, LocationPredicate enteredPos, LocationPredicate exitedPos, DistancePredicate distance) {
            super(ID, player);
            this.enteredPos = enteredPos;
            this.exitedPos = exitedPos;
            this.distance = distance;
        }

        public static Conditions distance(DistancePredicate distance) {
            return new Conditions(EntityPredicate.Extended.EMPTY, LocationPredicate.ANY, LocationPredicate.ANY, distance);
        }

        public boolean matches(ServerWorld world, Vec3d enteredPos, double exitedPosX, double exitedPosY, double exitedPosZ) {
            if (!this.enteredPos.test(world, enteredPos.x, enteredPos.y, enteredPos.z)) {
                return false;
            }
            if (!this.exitedPos.test(world, exitedPosX, exitedPosY, exitedPosZ)) {
                return false;
            }
            return this.distance.test(enteredPos.x, enteredPos.y, enteredPos.z, exitedPosX, exitedPosY, exitedPosZ);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("entered", this.enteredPos.toJson());
            jsonObject.add("exited", this.exitedPos.toJson());
            jsonObject.add("distance", this.distance.toJson());
            return jsonObject;
        }
    }
}

