/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class NetherTravelCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("nether_travel");
    private final Map<PlayerAdvancementTracker, Handler> handlers = Maps.newHashMap();

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void beginTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler = this.handlers.get(playerAdvancementTracker);
        if (handler == null) {
            handler = new Handler(playerAdvancementTracker);
            this.handlers.put(playerAdvancementTracker, handler);
        }
        handler.addCondition(conditionsContainer);
    }

    @Override
    public void endTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler = this.handlers.get(playerAdvancementTracker);
        if (handler != null) {
            handler.removeCondition(conditionsContainer);
            if (handler.isEmpty()) {
                this.handlers.remove(playerAdvancementTracker);
            }
        }
    }

    @Override
    public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
        this.handlers.remove(playerAdvancementTracker);
    }

    public Conditions method_9078(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        LocationPredicate locationPredicate = LocationPredicate.deserialize(jsonObject.get("entered"));
        LocationPredicate locationPredicate2 = LocationPredicate.deserialize(jsonObject.get("exited"));
        DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
        return new Conditions(locationPredicate, locationPredicate2, distancePredicate);
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d) {
        Handler handler = this.handlers.get(serverPlayerEntity.getAdvancementManager());
        if (handler != null) {
            handler.handle(serverPlayerEntity.getServerWorld(), vec3d, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z);
        }
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9078(jsonObject, jsonDeserializationContext);
    }

    static class Handler {
        private final PlayerAdvancementTracker manager;
        private final Set<Criterion.ConditionsContainer<Conditions>> conditions = Sets.newHashSet();

        public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
            this.manager = playerAdvancementTracker;
        }

        public boolean isEmpty() {
            return this.conditions.isEmpty();
        }

        public void addCondition(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.add(conditionsContainer);
        }

        public void removeCondition(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.remove(conditionsContainer);
        }

        public void handle(ServerWorld serverWorld, Vec3d vec3d, double d, double e, double f) {
            ArrayList<Criterion.ConditionsContainer<Conditions>> list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().matches(serverWorld, vec3d, d, e, f)) continue;
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(conditionsContainer);
            }
            if (list != null) {
                for (Criterion.ConditionsContainer<Conditions> conditionsContainer : list) {
                    conditionsContainer.apply(this.manager);
                }
            }
        }
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final LocationPredicate entered;
        private final LocationPredicate exited;
        private final DistancePredicate distance;

        public Conditions(LocationPredicate locationPredicate, LocationPredicate locationPredicate2, DistancePredicate distancePredicate) {
            super(ID);
            this.entered = locationPredicate;
            this.exited = locationPredicate2;
            this.distance = distancePredicate;
        }

        public static Conditions distance(DistancePredicate distancePredicate) {
            return new Conditions(LocationPredicate.ANY, LocationPredicate.ANY, distancePredicate);
        }

        public boolean matches(ServerWorld serverWorld, Vec3d vec3d, double d, double e, double f) {
            if (!this.entered.test(serverWorld, vec3d.x, vec3d.y, vec3d.z)) {
                return false;
            }
            if (!this.exited.test(serverWorld, d, e, f)) {
                return false;
            }
            return this.distance.test(vec3d.x, vec3d.y, vec3d.z, d, e, f);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("entered", this.entered.serialize());
            jsonObject.add("exited", this.exited.serialize());
            jsonObject.add("distance", this.distance.serialize());
            return jsonObject;
        }
    }
}

