package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.class_4553;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.scoreboard.AbstractTeam;
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
		class_4553.field_20722,
		null,
		null
	);
	public static final EntityPredicate[] EMPTY = new EntityPredicate[0];
	private final EntityTypePredicate type;
	private final DistancePredicate distance;
	private final LocationPredicate location;
	private final EntityEffectPredicate effects;
	private final NbtPredicate nbt;
	private final EntityFlagsPredicate flags;
	private final EntityEquipmentPredicate equipment;
	private final class_4553 field_20697;
	@Nullable
	private final String field_20698;
	@Nullable
	private final Identifier catType;

	private EntityPredicate(
		EntityTypePredicate entityTypePredicate,
		DistancePredicate distancePredicate,
		LocationPredicate locationPredicate,
		EntityEffectPredicate entityEffectPredicate,
		NbtPredicate nbtPredicate,
		EntityFlagsPredicate entityFlagsPredicate,
		EntityEquipmentPredicate entityEquipmentPredicate,
		class_4553 arg,
		@Nullable String string,
		@Nullable Identifier identifier
	) {
		this.type = entityTypePredicate;
		this.distance = distancePredicate;
		this.location = locationPredicate;
		this.effects = entityEffectPredicate;
		this.nbt = nbtPredicate;
		this.flags = entityFlagsPredicate;
		this.equipment = entityEquipmentPredicate;
		this.field_20697 = arg;
		this.field_20698 = string;
		this.catType = identifier;
	}

	public boolean test(ServerPlayerEntity serverPlayerEntity, @Nullable Entity entity) {
		return this.test(serverPlayerEntity.getServerWorld(), new Vec3d(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z), entity);
	}

	public boolean test(ServerWorld serverWorld, @Nullable Vec3d vec3d, @Nullable Entity entity) {
		if (this == ANY) {
			return true;
		} else if (entity == null) {
			return false;
		} else if (!this.type.matches(entity.getType())) {
			return false;
		} else {
			if (vec3d == null) {
				if (this.distance != DistancePredicate.ANY) {
					return false;
				}
			} else if (!this.distance.test(vec3d.x, vec3d.y, vec3d.z, entity.x, entity.y, entity.z)) {
				return false;
			}

			if (!this.location.test(serverWorld, entity.x, entity.y, entity.z)) {
				return false;
			} else if (!this.effects.test(entity)) {
				return false;
			} else if (!this.nbt.test(entity)) {
				return false;
			} else if (!this.flags.test(entity)) {
				return false;
			} else if (!this.equipment.test(entity)) {
				return false;
			} else if (!this.field_20697.method_22497(entity)) {
				return false;
			} else {
				if (this.field_20698 != null) {
					AbstractTeam abstractTeam = entity.getScoreboardTeam();
					if (abstractTeam == null || !this.field_20698.equals(abstractTeam.getName())) {
						return false;
					}
				}

				return this.catType == null || entity instanceof CatEntity && ((CatEntity)entity).getTexture().equals(this.catType);
			}
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
			class_4553 lv = class_4553.method_22499(jsonObject.get("player"));
			String string = JsonHelper.getString(jsonObject, "team", null);
			Identifier identifier = jsonObject.has("catType") ? new Identifier(JsonHelper.getString(jsonObject, "catType")) : null;
			return new EntityPredicate.Builder()
				.type(entityTypePredicate)
				.distance(distancePredicate)
				.location(locationPredicate)
				.effects(entityEffectPredicate)
				.nbt(nbtPredicate)
				.flags(entityFlagsPredicate)
				.equipment(entityEquipmentPredicate)
				.method_22469(lv)
				.method_22470(string)
				.catType(identifier)
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
			jsonObject.add("type", this.type.toJson());
			jsonObject.add("distance", this.distance.serialize());
			jsonObject.add("location", this.location.serialize());
			jsonObject.add("effects", this.effects.serialize());
			jsonObject.add("nbt", this.nbt.serialize());
			jsonObject.add("flags", this.flags.serialize());
			jsonObject.add("equipment", this.equipment.serialize());
			jsonObject.add("player", this.field_20697.method_22494());
			jsonObject.addProperty("team", this.field_20698);
			if (this.catType != null) {
				jsonObject.addProperty("catType", this.catType.toString());
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
		private EntityTypePredicate type = EntityTypePredicate.ANY;
		private DistancePredicate distance = DistancePredicate.ANY;
		private LocationPredicate location = LocationPredicate.ANY;
		private EntityEffectPredicate effects = EntityEffectPredicate.EMPTY;
		private NbtPredicate nbt = NbtPredicate.ANY;
		private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
		private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
		private class_4553 field_20699 = class_4553.field_20722;
		private String field_20700;
		private Identifier catType;

		public static EntityPredicate.Builder create() {
			return new EntityPredicate.Builder();
		}

		public EntityPredicate.Builder type(EntityType<?> entityType) {
			this.type = EntityTypePredicate.create(entityType);
			return this;
		}

		public EntityPredicate.Builder type(Tag<EntityType<?>> tag) {
			this.type = EntityTypePredicate.create(tag);
			return this;
		}

		public EntityPredicate.Builder type(Identifier identifier) {
			this.catType = identifier;
			return this;
		}

		public EntityPredicate.Builder type(EntityTypePredicate entityTypePredicate) {
			this.type = entityTypePredicate;
			return this;
		}

		public EntityPredicate.Builder distance(DistancePredicate distancePredicate) {
			this.distance = distancePredicate;
			return this;
		}

		public EntityPredicate.Builder location(LocationPredicate locationPredicate) {
			this.location = locationPredicate;
			return this;
		}

		public EntityPredicate.Builder effects(EntityEffectPredicate entityEffectPredicate) {
			this.effects = entityEffectPredicate;
			return this;
		}

		public EntityPredicate.Builder nbt(NbtPredicate nbtPredicate) {
			this.nbt = nbtPredicate;
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

		public EntityPredicate.Builder method_22469(class_4553 arg) {
			this.field_20699 = arg;
			return this;
		}

		public EntityPredicate.Builder method_22470(@Nullable String string) {
			this.field_20700 = string;
			return this;
		}

		public EntityPredicate.Builder catType(@Nullable Identifier identifier) {
			this.catType = identifier;
			return this;
		}

		public EntityPredicate build() {
			return new EntityPredicate(
				this.type, this.distance, this.location, this.effects, this.nbt, this.flags, this.equipment, this.field_20699, this.field_20700, this.catType
			);
		}
	}
}
