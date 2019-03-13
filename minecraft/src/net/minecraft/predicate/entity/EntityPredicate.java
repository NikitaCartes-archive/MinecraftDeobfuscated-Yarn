package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class EntityPredicate {
	public static final EntityPredicate ANY = new EntityPredicate(
		EntityTypePredicate.ANY,
		DistancePredicate.ANY,
		LocationPredicate.ANY,
		EntityEffectPredicate.EMPTY,
		NbtPredicate.ANY,
		EntityFlagsPredicate.ANY,
		EntityEquipmentPredicate.ANY,
		null
	);
	public static final EntityPredicate[] EMPTY = new EntityPredicate[0];
	private final EntityTypePredicate field_9595;
	private final DistancePredicate distance;
	private final LocationPredicate field_9596;
	private final EntityEffectPredicate field_9594;
	private final NbtPredicate field_9600;
	private final EntityFlagsPredicate flags;
	private final EntityEquipmentPredicate equipment;
	private final Identifier field_16317;

	private EntityPredicate(
		EntityTypePredicate entityTypePredicate,
		DistancePredicate distancePredicate,
		LocationPredicate locationPredicate,
		EntityEffectPredicate entityEffectPredicate,
		NbtPredicate nbtPredicate,
		EntityFlagsPredicate entityFlagsPredicate,
		EntityEquipmentPredicate entityEquipmentPredicate,
		@Nullable Identifier identifier
	) {
		this.field_9595 = entityTypePredicate;
		this.distance = distancePredicate;
		this.field_9596 = locationPredicate;
		this.field_9594 = entityEffectPredicate;
		this.field_9600 = nbtPredicate;
		this.flags = entityFlagsPredicate;
		this.equipment = entityEquipmentPredicate;
		this.field_16317 = identifier;
	}

	public boolean method_8914(ServerPlayerEntity serverPlayerEntity, @Nullable Entity entity) {
		return this.method_8909(serverPlayerEntity.getServerWorld(), new Vec3d(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z), entity);
	}

	public boolean method_8909(ServerWorld serverWorld, Vec3d vec3d, @Nullable Entity entity) {
		if (this == ANY) {
			return true;
		} else if (entity == null) {
			return false;
		} else if (!this.field_9595.matches(entity.method_5864())) {
			return false;
		} else if (!this.distance.test(vec3d.x, vec3d.y, vec3d.z, entity.x, entity.y, entity.z)) {
			return false;
		} else if (!this.field_9596.method_9018(serverWorld, entity.x, entity.y, entity.z)) {
			return false;
		} else if (!this.field_9594.test(entity)) {
			return false;
		} else if (!this.field_9600.test(entity)) {
			return false;
		} else if (!this.flags.test(entity)) {
			return false;
		} else {
			return !this.equipment.test(entity)
				? false
				: this.field_16317 == null || entity instanceof CatEntity && ((CatEntity)entity).method_16092().equals(this.field_16317);
		}
	}

	public static EntityPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entity");
			EntityTypePredicate entityTypePredicate = EntityTypePredicate.deserialize(jsonObject.get("type"));
			DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
			LocationPredicate locationPredicate = LocationPredicate.deserialize(jsonObject.get("location"));
			EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.deserialize(jsonObject.get("effects"));
			NbtPredicate nbtPredicate = NbtPredicate.deserialize(jsonObject.get("nbt"));
			EntityFlagsPredicate entityFlagsPredicate = EntityFlagsPredicate.deserialize(jsonObject.get("flags"));
			EntityEquipmentPredicate entityEquipmentPredicate = EntityEquipmentPredicate.deserialize(jsonObject.get("equipment"));
			Identifier identifier = jsonObject.has("catType") ? new Identifier(JsonHelper.getString(jsonObject, "catType")) : null;
			return new EntityPredicate.Builder()
				.method_8917(entityTypePredicate)
				.distance(distancePredicate)
				.method_8918(locationPredicate)
				.method_8923(entityEffectPredicate)
				.method_8915(nbtPredicate)
				.flags(entityFlagsPredicate)
				.equipment(entityEquipmentPredicate)
				.method_16112(identifier)
				.build();
		} else {
			return ANY;
		}
	}

	public static EntityPredicate[] deserializeAll(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = JsonHelper.asArray(jsonElement, "entities");
			EntityPredicate[] entityPredicates = new EntityPredicate[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {
				entityPredicates[i] = deserialize(jsonArray.get(i));
			}

			return entityPredicates;
		} else {
			return EMPTY;
		}
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("type", this.field_9595.toJson());
			jsonObject.add("distance", this.distance.serialize());
			jsonObject.add("location", this.field_9596.serialize());
			jsonObject.add("effects", this.field_9594.serialize());
			jsonObject.add("nbt", this.field_9600.serialize());
			jsonObject.add("flags", this.flags.serialize());
			jsonObject.add("equipment", this.equipment.serialize());
			if (this.field_16317 != null) {
				jsonObject.addProperty("catType", this.field_16317.toString());
			}

			return jsonObject;
		}
	}

	public static JsonElement serializeAll(EntityPredicate[] entityPredicates) {
		if (entityPredicates == EMPTY) {
			return JsonNull.INSTANCE;
		} else {
			JsonArray jsonArray = new JsonArray();

			for (EntityPredicate entityPredicate : entityPredicates) {
				JsonElement jsonElement = entityPredicate.serialize();
				if (!jsonElement.isJsonNull()) {
					jsonArray.add(jsonElement);
				}
			}

			return jsonArray;
		}
	}

	public static class Builder {
		private EntityTypePredicate field_9607 = EntityTypePredicate.ANY;
		private DistancePredicate distance = DistancePredicate.ANY;
		private LocationPredicate field_9604 = LocationPredicate.ANY;
		private EntityEffectPredicate field_9605 = EntityEffectPredicate.EMPTY;
		private NbtPredicate field_9603 = NbtPredicate.ANY;
		private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
		private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
		@Nullable
		private Identifier field_16318;

		public static EntityPredicate.Builder create() {
			return new EntityPredicate.Builder();
		}

		public EntityPredicate.Builder type(EntityType<?> entityType) {
			this.field_9607 = EntityTypePredicate.create(entityType);
			return this;
		}

		public EntityPredicate.Builder method_8922(Tag<EntityType<?>> tag) {
			this.field_9607 = EntityTypePredicate.method_8926(tag);
			return this;
		}

		public EntityPredicate.Builder method_16113(Identifier identifier) {
			this.field_16318 = identifier;
			return this;
		}

		public EntityPredicate.Builder method_8917(EntityTypePredicate entityTypePredicate) {
			this.field_9607 = entityTypePredicate;
			return this;
		}

		public EntityPredicate.Builder distance(DistancePredicate distancePredicate) {
			this.distance = distancePredicate;
			return this;
		}

		public EntityPredicate.Builder method_8918(LocationPredicate locationPredicate) {
			this.field_9604 = locationPredicate;
			return this;
		}

		public EntityPredicate.Builder method_8923(EntityEffectPredicate entityEffectPredicate) {
			this.field_9605 = entityEffectPredicate;
			return this;
		}

		public EntityPredicate.Builder method_8915(NbtPredicate nbtPredicate) {
			this.field_9603 = nbtPredicate;
			return this;
		}

		public EntityPredicate.Builder flags(EntityFlagsPredicate entityFlagsPredicate) {
			this.flags = entityFlagsPredicate;
			return this;
		}

		public EntityPredicate.Builder equipment(EntityEquipmentPredicate entityEquipmentPredicate) {
			this.equipment = entityEquipmentPredicate;
			return this;
		}

		public EntityPredicate.Builder method_16112(@Nullable Identifier identifier) {
			this.field_16318 = identifier;
			return this;
		}

		public EntityPredicate build() {
			return new EntityPredicate(this.field_9607, this.distance, this.field_9604, this.field_9605, this.field_9603, this.flags, this.equipment, this.field_16318);
		}
	}
}
