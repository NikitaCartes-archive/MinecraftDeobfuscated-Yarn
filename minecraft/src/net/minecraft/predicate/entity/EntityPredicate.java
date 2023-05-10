package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class EntityPredicate {
	public static final EntityPredicate ANY = new EntityPredicate(
		EntityTypePredicate.ANY,
		DistancePredicate.ANY,
		LocationPredicate.ANY,
		LocationPredicate.ANY,
		EntityEffectPredicate.EMPTY,
		NbtPredicate.ANY,
		EntityFlagsPredicate.ANY,
		EntityEquipmentPredicate.ANY,
		TypeSpecificPredicate.ANY,
		null
	);
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

	private EntityPredicate(
		EntityTypePredicate type,
		DistancePredicate distance,
		LocationPredicate location,
		LocationPredicate steppingOn,
		EntityEffectPredicate effects,
		NbtPredicate nbt,
		EntityFlagsPredicate flags,
		EntityEquipmentPredicate equipment,
		TypeSpecificPredicate typeSpecific,
		@Nullable String team
	) {
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

	EntityPredicate(
		EntityTypePredicate type,
		DistancePredicate distance,
		LocationPredicate location,
		LocationPredicate steppingOn,
		EntityEffectPredicate effects,
		NbtPredicate nbt,
		EntityFlagsPredicate flags,
		EntityEquipmentPredicate equipment,
		TypeSpecificPredicate typeSpecific,
		EntityPredicate vehicle,
		EntityPredicate passenger,
		EntityPredicate targetedEntity,
		@Nullable String team
	) {
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

	public static LootContextPredicate contextPredicateFromJson(JsonObject json, String key, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		JsonElement jsonElement = json.get(key);
		return contextPredicateFromJsonElement(key, predicateDeserializer, jsonElement);
	}

	public static LootContextPredicate[] contextPredicateArrayFromJson(JsonObject json, String key, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		JsonElement jsonElement = json.get(key);
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = JsonHelper.asArray(jsonElement, key);
			LootContextPredicate[] lootContextPredicates = new LootContextPredicate[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {
				lootContextPredicates[i] = contextPredicateFromJsonElement(key + "[" + i + "]", predicateDeserializer, jsonArray.get(i));
			}

			return lootContextPredicates;
		} else {
			return new LootContextPredicate[0];
		}
	}

	private static LootContextPredicate contextPredicateFromJsonElement(
		String key, AdvancementEntityPredicateDeserializer predicateDeserializer, @Nullable JsonElement json
	) {
		LootContextPredicate lootContextPredicate = LootContextPredicate.fromJson(key, predicateDeserializer, json, LootContextTypes.ADVANCEMENT_ENTITY);
		if (lootContextPredicate != null) {
			return lootContextPredicate;
		} else {
			EntityPredicate entityPredicate = fromJson(json);
			return asLootContextPredicate(entityPredicate);
		}
	}

	public static LootContextPredicate asLootContextPredicate(EntityPredicate predicate) {
		if (predicate == ANY) {
			return LootContextPredicate.EMPTY;
		} else {
			LootCondition lootCondition = EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, predicate).build();
			return new LootContextPredicate(new LootCondition[]{lootCondition});
		}
	}

	public boolean test(ServerPlayerEntity player, @Nullable Entity entity) {
		return this.test(player.getServerWorld(), player.getPos(), entity);
	}

	public boolean test(ServerWorld world, @Nullable Vec3d pos, @Nullable Entity entity) {
		if (this == ANY) {
			return true;
		} else if (entity == null) {
			return false;
		} else if (!this.type.matches(entity.getType())) {
			return false;
		} else {
			if (pos == null) {
				if (this.distance != DistancePredicate.ANY) {
					return false;
				}
			} else if (!this.distance.test(pos.x, pos.y, pos.z, entity.getX(), entity.getY(), entity.getZ())) {
				return false;
			}

			if (!this.location.test(world, entity.getX(), entity.getY(), entity.getZ())) {
				return false;
			} else {
				if (this.steppingOn != LocationPredicate.ANY) {
					Vec3d vec3d = Vec3d.ofCenter(entity.getSteppingPos());
					if (!this.steppingOn.test(world, vec3d.getX(), vec3d.getY(), vec3d.getZ())) {
						return false;
					}
				}

				if (!this.effects.test(entity)) {
					return false;
				} else if (!this.nbt.test(entity)) {
					return false;
				} else if (!this.flags.test(entity)) {
					return false;
				} else if (!this.equipment.test(entity)) {
					return false;
				} else if (!this.typeSpecific.test(entity, world, pos)) {
					return false;
				} else if (!this.vehicle.test(world, pos, entity.getVehicle())) {
					return false;
				} else if (this.passenger != ANY && entity.getPassengerList().stream().noneMatch(entityx -> this.passenger.test(world, pos, entityx))) {
					return false;
				} else if (!this.targetedEntity.test(world, pos, entity instanceof MobEntity ? ((MobEntity)entity).getTarget() : null)) {
					return false;
				} else {
					if (this.team != null) {
						AbstractTeam abstractTeam = entity.getScoreboardTeam();
						if (abstractTeam == null || !this.team.equals(abstractTeam.getName())) {
							return false;
						}
					}

					return true;
				}
			}
		}
	}

	public static EntityPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
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
			EntityPredicate entityPredicate = fromJson(jsonObject.get("vehicle"));
			EntityPredicate entityPredicate2 = fromJson(jsonObject.get("passenger"));
			EntityPredicate entityPredicate3 = fromJson(jsonObject.get("targeted_entity"));
			String string = JsonHelper.getString(jsonObject, "team", null);
			return new EntityPredicate.Builder()
				.type(entityTypePredicate)
				.distance(distancePredicate)
				.location(locationPredicate)
				.steppingOn(locationPredicate2)
				.effects(entityEffectPredicate)
				.nbt(nbtPredicate)
				.flags(entityFlagsPredicate)
				.equipment(entityEquipmentPredicate)
				.typeSpecific(typeSpecificPredicate)
				.team(string)
				.vehicle(entityPredicate)
				.passenger(entityPredicate2)
				.targetedEntity(entityPredicate3)
				.build();
		} else {
			return ANY;
		}
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
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
	}

	public static LootContext createAdvancementEntityLootContext(ServerPlayerEntity player, Entity target) {
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(player.getServerWorld())
			.add(LootContextParameters.THIS_ENTITY, target)
			.add(LootContextParameters.ORIGIN, player.getPos())
			.build(LootContextTypes.ADVANCEMENT_ENTITY);
		return new LootContext.Builder(lootContextParameterSet).build(LootTable.DEFAULT_ID);
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
		private EntityPredicate vehicle = EntityPredicate.ANY;
		private EntityPredicate passenger = EntityPredicate.ANY;
		private EntityPredicate targetedEntity = EntityPredicate.ANY;
		@Nullable
		private String team;

		public static EntityPredicate.Builder create() {
			return new EntityPredicate.Builder();
		}

		public EntityPredicate.Builder type(EntityType<?> type) {
			this.type = EntityTypePredicate.create(type);
			return this;
		}

		public EntityPredicate.Builder type(TagKey<EntityType<?>> tag) {
			this.type = EntityTypePredicate.create(tag);
			return this;
		}

		public EntityPredicate.Builder type(EntityTypePredicate type) {
			this.type = type;
			return this;
		}

		public EntityPredicate.Builder distance(DistancePredicate distance) {
			this.distance = distance;
			return this;
		}

		public EntityPredicate.Builder location(LocationPredicate location) {
			this.location = location;
			return this;
		}

		public EntityPredicate.Builder steppingOn(LocationPredicate location) {
			this.steppingOn = location;
			return this;
		}

		public EntityPredicate.Builder effects(EntityEffectPredicate effects) {
			this.effects = effects;
			return this;
		}

		public EntityPredicate.Builder nbt(NbtPredicate nbt) {
			this.nbt = nbt;
			return this;
		}

		public EntityPredicate.Builder flags(EntityFlagsPredicate flags) {
			this.flags = flags;
			return this;
		}

		public EntityPredicate.Builder equipment(EntityEquipmentPredicate equipment) {
			this.equipment = equipment;
			return this;
		}

		public EntityPredicate.Builder typeSpecific(TypeSpecificPredicate typeSpecific) {
			this.typeSpecific = typeSpecific;
			return this;
		}

		public EntityPredicate.Builder vehicle(EntityPredicate vehicle) {
			this.vehicle = vehicle;
			return this;
		}

		public EntityPredicate.Builder passenger(EntityPredicate passenger) {
			this.passenger = passenger;
			return this;
		}

		public EntityPredicate.Builder targetedEntity(EntityPredicate targetedEntity) {
			this.targetedEntity = targetedEntity;
			return this;
		}

		public EntityPredicate.Builder team(@Nullable String team) {
			this.team = team;
			return this;
		}

		public EntityPredicate build() {
			return new EntityPredicate(
				this.type,
				this.distance,
				this.location,
				this.steppingOn,
				this.effects,
				this.nbt,
				this.flags,
				this.equipment,
				this.typeSpecific,
				this.vehicle,
				this.passenger,
				this.targetedEntity,
				this.team
			);
		}
	}
}
