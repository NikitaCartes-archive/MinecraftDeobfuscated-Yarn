/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntityPredicate {
    public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, LocationPredicate.ANY, EntityEffectPredicate.EMPTY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, TypeSpecificPredicate.ANY, null);
    private final EntityTypePredicate type;
    private final DistancePredicate distance;
    private final LocationPredicate location;
    private final LocationPredicate steppingOn;
    private final EntityEffectPredicate effects;
    private final NbtPredicate nbt;
    private final EntityFlagsPredicate flags;
    private final EntityEquipmentPredicate equipment;
    private final TypeSpecificPredicate typeSpecific;
    private final EntityPredicate vehicle;
    private final EntityPredicate passenger;
    private final EntityPredicate targetedEntity;
    @Nullable
    private final String team;

    private EntityPredicate(EntityTypePredicate type, DistancePredicate distance, LocationPredicate location, LocationPredicate steppingOn, EntityEffectPredicate effects, NbtPredicate nbt, EntityFlagsPredicate flags, EntityEquipmentPredicate equipment, TypeSpecificPredicate typeSpecific, @Nullable String team) {
        this.type = type;
        this.distance = distance;
        this.location = location;
        this.steppingOn = steppingOn;
        this.effects = effects;
        this.nbt = nbt;
        this.flags = flags;
        this.equipment = equipment;
        this.typeSpecific = typeSpecific;
        this.passenger = this;
        this.vehicle = this;
        this.targetedEntity = this;
        this.team = team;
    }

    EntityPredicate(EntityTypePredicate type, DistancePredicate distance, LocationPredicate location, LocationPredicate steppingOn, EntityEffectPredicate effects, NbtPredicate nbt, EntityFlagsPredicate flags, EntityEquipmentPredicate equipment, TypeSpecificPredicate typeSpecific, EntityPredicate vehicle, EntityPredicate passenger, EntityPredicate targetedEntity, @Nullable String team) {
        this.type = type;
        this.distance = distance;
        this.location = location;
        this.steppingOn = steppingOn;
        this.effects = effects;
        this.nbt = nbt;
        this.flags = flags;
        this.equipment = equipment;
        this.typeSpecific = typeSpecific;
        this.vehicle = vehicle;
        this.passenger = passenger;
        this.targetedEntity = targetedEntity;
        this.team = team;
    }

    public boolean test(ServerPlayerEntity player, @Nullable Entity entity) {
        return this.test(player.getWorld(), player.getPos(), entity);
    }

    public boolean test(ServerWorld world, @Nullable Vec3d pos, @Nullable Entity entity2) {
        AbstractTeam abstractTeam;
        Vec3d vec3d;
        if (this == ANY) {
            return true;
        }
        if (entity2 == null) {
            return false;
        }
        if (!this.type.matches(entity2.getType())) {
            return false;
        }
        if (pos == null ? this.distance != DistancePredicate.ANY : !this.distance.test(pos.x, pos.y, pos.z, entity2.getX(), entity2.getY(), entity2.getZ())) {
            return false;
        }
        if (!this.location.test(world, entity2.getX(), entity2.getY(), entity2.getZ())) {
            return false;
        }
        if (this.steppingOn != LocationPredicate.ANY && !this.steppingOn.test(world, (vec3d = Vec3d.ofCenter(entity2.getLandingPos())).getX(), vec3d.getY(), vec3d.getZ())) {
            return false;
        }
        if (!this.effects.test(entity2)) {
            return false;
        }
        if (!this.nbt.test(entity2)) {
            return false;
        }
        if (!this.flags.test(entity2)) {
            return false;
        }
        if (!this.equipment.test(entity2)) {
            return false;
        }
        if (!this.typeSpecific.test(entity2, world, pos)) {
            return false;
        }
        if (!this.vehicle.test(world, pos, entity2.getVehicle())) {
            return false;
        }
        if (this.passenger != ANY && entity2.getPassengerList().stream().noneMatch(entity -> this.passenger.test(world, pos, (Entity)entity))) {
            return false;
        }
        if (!this.targetedEntity.test(world, pos, entity2 instanceof MobEntity ? ((MobEntity)entity2).getTarget() : null)) {
            return false;
        }
        return this.team == null || (abstractTeam = entity2.getScoreboardTeam()) != null && this.team.equals(abstractTeam.getName());
    }

    public static EntityPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "entity");
        EntityTypePredicate entityTypePredicate = EntityTypePredicate.fromJson(jsonObject.get("type"));
        DistancePredicate distancePredicate = DistancePredicate.fromJson(jsonObject.get("distance"));
        LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
        LocationPredicate locationPredicate2 = LocationPredicate.fromJson(jsonObject.get("stepping_on"));
        EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.fromJson(jsonObject.get("effects"));
        NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
        EntityFlagsPredicate entityFlagsPredicate = EntityFlagsPredicate.fromJson(jsonObject.get("flags"));
        EntityEquipmentPredicate entityEquipmentPredicate = EntityEquipmentPredicate.fromJson(jsonObject.get("equipment"));
        TypeSpecificPredicate typeSpecificPredicate = TypeSpecificPredicate.fromJson(jsonObject.get("type_specific"));
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("vehicle"));
        EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("passenger"));
        EntityPredicate entityPredicate3 = EntityPredicate.fromJson(jsonObject.get("targeted_entity"));
        String string = JsonHelper.getString(jsonObject, "team", null);
        return new Builder().type(entityTypePredicate).distance(distancePredicate).location(locationPredicate).steppingOn(locationPredicate2).effects(entityEffectPredicate).nbt(nbtPredicate).flags(entityFlagsPredicate).equipment(entityEquipmentPredicate).typeSpecific(typeSpecificPredicate).team(string).vehicle(entityPredicate).passenger(entityPredicate2).targetedEntity(entityPredicate3).build();
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("type", this.type.toJson());
        jsonObject.add("distance", this.distance.toJson());
        jsonObject.add("location", this.location.toJson());
        jsonObject.add("stepping_on", this.steppingOn.toJson());
        jsonObject.add("effects", this.effects.toJson());
        jsonObject.add("nbt", this.nbt.toJson());
        jsonObject.add("flags", this.flags.toJson());
        jsonObject.add("equipment", this.equipment.toJson());
        jsonObject.add("type_specific", this.typeSpecific.toJson());
        jsonObject.add("vehicle", this.vehicle.toJson());
        jsonObject.add("passenger", this.passenger.toJson());
        jsonObject.add("targeted_entity", this.targetedEntity.toJson());
        jsonObject.addProperty("team", this.team);
        return jsonObject;
    }

    public static LootContext createAdvancementEntityLootContext(ServerPlayerEntity player, Entity target) {
        return new LootContext.Builder(player.getWorld()).parameter(LootContextParameters.THIS_ENTITY, target).parameter(LootContextParameters.ORIGIN, player.getPos()).random(player.getRandom()).build(LootContextTypes.ADVANCEMENT_ENTITY);
    }

    public static class Builder {
        private EntityTypePredicate type = EntityTypePredicate.ANY;
        private DistancePredicate distance = DistancePredicate.ANY;
        private LocationPredicate location = LocationPredicate.ANY;
        private LocationPredicate steppingOn = LocationPredicate.ANY;
        private EntityEffectPredicate effects = EntityEffectPredicate.EMPTY;
        private NbtPredicate nbt = NbtPredicate.ANY;
        private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
        private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
        private TypeSpecificPredicate typeSpecific = TypeSpecificPredicate.ANY;
        private EntityPredicate vehicle = ANY;
        private EntityPredicate passenger = ANY;
        private EntityPredicate targetedEntity = ANY;
        @Nullable
        private String team;

        public static Builder create() {
            return new Builder();
        }

        public Builder type(EntityType<?> type) {
            this.type = EntityTypePredicate.create(type);
            return this;
        }

        public Builder type(TagKey<EntityType<?>> tag) {
            this.type = EntityTypePredicate.create(tag);
            return this;
        }

        public Builder type(EntityTypePredicate type) {
            this.type = type;
            return this;
        }

        public Builder distance(DistancePredicate distance) {
            this.distance = distance;
            return this;
        }

        public Builder location(LocationPredicate location) {
            this.location = location;
            return this;
        }

        public Builder steppingOn(LocationPredicate location) {
            this.steppingOn = location;
            return this;
        }

        public Builder effects(EntityEffectPredicate effects) {
            this.effects = effects;
            return this;
        }

        public Builder nbt(NbtPredicate nbt) {
            this.nbt = nbt;
            return this;
        }

        public Builder flags(EntityFlagsPredicate flags) {
            this.flags = flags;
            return this;
        }

        public Builder equipment(EntityEquipmentPredicate equipment) {
            this.equipment = equipment;
            return this;
        }

        public Builder typeSpecific(TypeSpecificPredicate typeSpecific) {
            this.typeSpecific = typeSpecific;
            return this;
        }

        public Builder vehicle(EntityPredicate vehicle) {
            this.vehicle = vehicle;
            return this;
        }

        public Builder passenger(EntityPredicate passenger) {
            this.passenger = passenger;
            return this;
        }

        public Builder targetedEntity(EntityPredicate targetedEntity) {
            this.targetedEntity = targetedEntity;
            return this;
        }

        public Builder team(@Nullable String team) {
            this.team = team;
            return this;
        }

        public EntityPredicate build() {
            return new EntityPredicate(this.type, this.distance, this.location, this.steppingOn, this.effects, this.nbt, this.flags, this.equipment, this.typeSpecific, this.vehicle, this.passenger, this.targetedEntity, this.team);
        }
    }

    public static class Extended {
        public static final Extended EMPTY = new Extended(new LootCondition[0]);
        private final LootCondition[] conditions;
        private final Predicate<LootContext> combinedCondition;

        private Extended(LootCondition[] conditions) {
            this.conditions = conditions;
            this.combinedCondition = LootConditionTypes.joinAnd(conditions);
        }

        public static Extended create(LootCondition ... conditions) {
            return new Extended(conditions);
        }

        public static Extended getInJson(JsonObject root, String key, AdvancementEntityPredicateDeserializer predicateDeserializer) {
            JsonElement jsonElement = root.get(key);
            return Extended.fromJson(key, predicateDeserializer, jsonElement);
        }

        public static Extended[] requireInJson(JsonObject root, String key, AdvancementEntityPredicateDeserializer predicateDeserializer) {
            JsonElement jsonElement = root.get(key);
            if (jsonElement == null || jsonElement.isJsonNull()) {
                return new Extended[0];
            }
            JsonArray jsonArray = JsonHelper.asArray(jsonElement, key);
            Extended[] extendeds = new Extended[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); ++i) {
                extendeds[i] = Extended.fromJson(key + "[" + i + "]", predicateDeserializer, jsonArray.get(i));
            }
            return extendeds;
        }

        private static Extended fromJson(String key, AdvancementEntityPredicateDeserializer predicateDeserializer, @Nullable JsonElement json) {
            if (json != null && json.isJsonArray()) {
                LootCondition[] lootConditions = predicateDeserializer.loadConditions(json.getAsJsonArray(), predicateDeserializer.getAdvancementId() + "/" + key, LootContextTypes.ADVANCEMENT_ENTITY);
                return new Extended(lootConditions);
            }
            EntityPredicate entityPredicate = EntityPredicate.fromJson(json);
            return Extended.ofLegacy(entityPredicate);
        }

        public static Extended ofLegacy(EntityPredicate predicate) {
            if (predicate == ANY) {
                return EMPTY;
            }
            LootCondition lootCondition = EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, predicate).build();
            return new Extended(new LootCondition[]{lootCondition});
        }

        public boolean test(LootContext context) {
            return this.combinedCondition.test(context);
        }

        public JsonElement toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            if (this.conditions.length == 0) {
                return JsonNull.INSTANCE;
            }
            return predicateSerializer.conditionsToJson(this.conditions);
        }

        public static JsonElement toPredicatesJsonArray(Extended[] predicates, AdvancementEntityPredicateSerializer predicateSerializer) {
            if (predicates.length == 0) {
                return JsonNull.INSTANCE;
            }
            JsonArray jsonArray = new JsonArray();
            for (Extended extended : predicates) {
                jsonArray.add(extended.toJson(predicateSerializer));
            }
            return jsonArray;
        }
    }
}

