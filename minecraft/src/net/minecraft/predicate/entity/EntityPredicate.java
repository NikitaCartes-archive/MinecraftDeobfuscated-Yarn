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
	private final EntityTypePredicate type;
	private final DistancePredicate distance;
	private final LocationPredicate location;
	private final EntityEffectPredicate effects;
	private final NbtPredicate nbt;
	private final EntityFlagsPredicate flags;
	private final EntityEquipmentPredicate equipment;
	private final Identifier catType;

	private EntityPredicate(
		EntityTypePredicate type,
		DistancePredicate distance,
		LocationPredicate location,
		EntityEffectPredicate effects,
		NbtPredicate nbt,
		EntityFlagsPredicate flags,
		EntityEquipmentPredicate equipment,
		@Nullable Identifier identifier
	) {
		this.type = type;
		this.distance = distance;
		this.location = location;
		this.effects = effects;
		this.nbt = nbt;
		this.flags = flags;
		this.equipment = equipment;
		this.catType = identifier;
	}

	public boolean test(ServerPlayerEntity player, @Nullable Entity entity) {
		return this.test(player.getServerWorld(), new Vec3d(player.x, player.y, player.z), entity);
	}

	public boolean test(ServerWorld world, Vec3d pos, @Nullable Entity entity) {
		if (this == ANY) {
			return true;
		} else if (entity == null) {
			return false;
		} else if (!this.type.matches(entity.getType())) {
			return false;
		} else if (!this.distance.test(pos.x, pos.y, pos.z, entity.x, entity.y, entity.z)) {
			return false;
		} else if (!this.location.test(world, entity.x, entity.y, entity.z)) {
			return false;
		} else if (!this.effects.test(entity)) {
			return false;
		} else if (!this.nbt.test(entity)) {
			return false;
		} else if (!this.flags.test(entity)) {
			return false;
		} else {
			return !this.equipment.test(entity) ? false : this.catType == null || entity instanceof CatEntity && ((CatEntity)entity).getTexture().equals(this.catType);
		}
	}

	public static EntityPredicate fromJson(@Nullable JsonElement el) {
		if (el != null && !el.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(el, "entity");
			EntityTypePredicate entityTypePredicate = EntityTypePredicate.deserialize(jsonObject.get("type"));
			DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
			LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
			EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.deserialize(jsonObject.get("effects"));
			NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
			EntityFlagsPredicate entityFlagsPredicate = EntityFlagsPredicate.deserialize(jsonObject.get("flags"));
			EntityEquipmentPredicate entityEquipmentPredicate = EntityEquipmentPredicate.deserialize(jsonObject.get("equipment"));
			Identifier identifier = jsonObject.has("catType") ? new Identifier(JsonHelper.getString(jsonObject, "catType")) : null;
			return new EntityPredicate.Builder()
				.type(entityTypePredicate)
				.distance(distancePredicate)
				.location(locationPredicate)
				.effects(entityEffectPredicate)
				.nbt(nbtPredicate)
				.flags(entityFlagsPredicate)
				.equipment(entityEquipmentPredicate)
				.catType(identifier)
				.build();
		} else {
			return ANY;
		}
	}

	public static EntityPredicate[] fromJsonArray(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			JsonArray jsonArray = JsonHelper.asArray(element, "entities");
			EntityPredicate[] entityPredicates = new EntityPredicate[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {
				entityPredicates[i] = fromJson(jsonArray.get(i));
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
	}

	public static JsonElement serializeAll(EntityPredicate[] predicates) {
		if (predicates == EMPTY) {
			return JsonNull.INSTANCE;
		} else {
			JsonArray jsonArray = new JsonArray();

			for (EntityPredicate entityPredicate : predicates) {
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
		@Nullable
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

		public EntityPredicate.Builder catType(@Nullable Identifier identifier) {
			this.catType = identifier;
			return this;
		}

		public EntityPredicate build() {
			return new EntityPredicate(this.type, this.distance, this.location, this.effects, this.nbt, this.flags, this.equipment, this.catType);
		}
	}
}
