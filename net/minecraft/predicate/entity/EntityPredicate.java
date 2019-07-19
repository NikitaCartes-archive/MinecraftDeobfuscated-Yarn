/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntityPredicate {
    public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, EntityEffectPredicate.EMPTY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, null);
    public static final EntityPredicate[] EMPTY = new EntityPredicate[0];
    private final EntityTypePredicate type;
    private final DistancePredicate distance;
    private final LocationPredicate location;
    private final EntityEffectPredicate effects;
    private final NbtPredicate nbt;
    private final EntityFlagsPredicate flags;
    private final EntityEquipmentPredicate equipment;
    private final Identifier catType;

    private EntityPredicate(EntityTypePredicate entityTypePredicate, DistancePredicate distancePredicate, LocationPredicate locationPredicate, EntityEffectPredicate entityEffectPredicate, NbtPredicate nbtPredicate, EntityFlagsPredicate entityFlagsPredicate, EntityEquipmentPredicate entityEquipmentPredicate, @Nullable Identifier identifier) {
        this.type = entityTypePredicate;
        this.distance = distancePredicate;
        this.location = locationPredicate;
        this.effects = entityEffectPredicate;
        this.nbt = nbtPredicate;
        this.flags = entityFlagsPredicate;
        this.equipment = entityEquipmentPredicate;
        this.catType = identifier;
    }

    public boolean test(ServerPlayerEntity serverPlayerEntity, @Nullable Entity entity) {
        return this.test(serverPlayerEntity.getServerWorld(), new Vec3d(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z), entity);
    }

    public boolean test(ServerWorld serverWorld, Vec3d vec3d, @Nullable Entity entity) {
        if (this == ANY) {
            return true;
        }
        if (entity == null) {
            return false;
        }
        if (!this.type.matches(entity.getType())) {
            return false;
        }
        if (!this.distance.test(vec3d.x, vec3d.y, vec3d.z, entity.x, entity.y, entity.z)) {
            return false;
        }
        if (!this.location.test(serverWorld, entity.x, entity.y, entity.z)) {
            return false;
        }
        if (!this.effects.test(entity)) {
            return false;
        }
        if (!this.nbt.test(entity)) {
            return false;
        }
        if (!this.flags.test(entity)) {
            return false;
        }
        if (!this.equipment.test(entity)) {
            return false;
        }
        return this.catType == null || entity instanceof CatEntity && ((CatEntity)entity).getTexture().equals(this.catType);
    }

    public static EntityPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entity");
        EntityTypePredicate entityTypePredicate = EntityTypePredicate.deserialize(jsonObject.get("type"));
        DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
        LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
        EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.deserialize(jsonObject.get("effects"));
        NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
        EntityFlagsPredicate entityFlagsPredicate = EntityFlagsPredicate.deserialize(jsonObject.get("flags"));
        EntityEquipmentPredicate entityEquipmentPredicate = EntityEquipmentPredicate.deserialize(jsonObject.get("equipment"));
        Identifier identifier = jsonObject.has("catType") ? new Identifier(JsonHelper.getString(jsonObject, "catType")) : null;
        return new Builder().type(entityTypePredicate).distance(distancePredicate).location(locationPredicate).effects(entityEffectPredicate).nbt(nbtPredicate).flags(entityFlagsPredicate).equipment(entityEquipmentPredicate).catType(identifier).build();
    }

    public static EntityPredicate[] fromJsonArray(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EMPTY;
        }
        JsonArray jsonArray = JsonHelper.asArray(jsonElement, "entities");
        EntityPredicate[] entityPredicates = new EntityPredicate[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); ++i) {
            entityPredicates[i] = EntityPredicate.fromJson(jsonArray.get(i));
        }
        return entityPredicates;
    }

    public JsonElement serialize() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("type", this.type.toJson());
        jsonObject.add("distance", this.distance.serialize());
        jsonObject.add("location", this.location.toJson());
        jsonObject.add("effects", this.effects.serialize());
        jsonObject.add("nbt", this.nbt.toJson());
        jsonObject.add("flags", this.flags.serialize());
        jsonObject.add("equipment", this.equipment.serialize());
        if (this.catType != null) {
            jsonObject.addProperty("catType", this.catType.toString());
        }
        return jsonObject;
    }

    public static JsonElement serializeAll(EntityPredicate[] entityPredicates) {
        if (entityPredicates == EMPTY) {
            return JsonNull.INSTANCE;
        }
        JsonArray jsonArray = new JsonArray();
        for (EntityPredicate entityPredicate : entityPredicates) {
            JsonElement jsonElement = entityPredicate.serialize();
            if (jsonElement.isJsonNull()) continue;
            jsonArray.add(jsonElement);
        }
        return jsonArray;
    }

    public static class Builder {
        private EntityTypePredicate type = EntityTypePredicate.ANY;
        private DistancePredicate distance = DistancePredicate.ANY;
        private LocationPredicate location = LocationPredicate.ANY;
        private EntityEffectPredicate effects = EntityEffectPredicate.EMPTY;
        private NbtPredicate nbt = NbtPredicate.ANY;
        private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
        private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
        @Nullable
        private Identifier catType;

        public static Builder create() {
            return new Builder();
        }

        public Builder type(EntityType<?> entityType) {
            this.type = EntityTypePredicate.create(entityType);
            return this;
        }

        public Builder type(Tag<EntityType<?>> tag) {
            this.type = EntityTypePredicate.create(tag);
            return this;
        }

        public Builder type(Identifier identifier) {
            this.catType = identifier;
            return this;
        }

        public Builder type(EntityTypePredicate entityTypePredicate) {
            this.type = entityTypePredicate;
            return this;
        }

        public Builder distance(DistancePredicate distancePredicate) {
            this.distance = distancePredicate;
            return this;
        }

        public Builder location(LocationPredicate locationPredicate) {
            this.location = locationPredicate;
            return this;
        }

        public Builder effects(EntityEffectPredicate entityEffectPredicate) {
            this.effects = entityEffectPredicate;
            return this;
        }

        public Builder nbt(NbtPredicate nbtPredicate) {
            this.nbt = nbtPredicate;
            return this;
        }

        public Builder flags(EntityFlagsPredicate entityFlagsPredicate) {
            this.flags = entityFlagsPredicate;
            return this;
        }

        public Builder equipment(EntityEquipmentPredicate entityEquipmentPredicate) {
            this.equipment = entityEquipmentPredicate;
            return this;
        }

        public Builder catType(@Nullable Identifier identifier) {
            this.catType = identifier;
            return this;
        }

        public EntityPredicate build() {
            return new EntityPredicate(this.type, this.distance, this.location, this.effects, this.nbt, this.flags, this.equipment, this.catType);
        }
    }
}

