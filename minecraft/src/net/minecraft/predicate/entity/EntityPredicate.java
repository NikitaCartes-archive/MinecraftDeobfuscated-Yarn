package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.PlayerPredicate;
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
		PlayerPredicate.ANY,
		FishingHookPredicate.ANY,
		EntityPredicate.ANY,
		EntityPredicate.ANY,
		null,
		null
	);
	private final EntityTypePredicate type;
	private final DistancePredicate distance;
	private final LocationPredicate location;
	private final EntityEffectPredicate effects;
	private final NbtPredicate nbt;
	private final EntityFlagsPredicate flags;
	private final EntityEquipmentPredicate equipment;
	private final PlayerPredicate player;
	private final FishingHookPredicate fishingHook;
	private final EntityPredicate field_24488;
	private final EntityPredicate field_24489;
	@Nullable
	private final String team;
	@Nullable
	private final Identifier catType;

	private EntityPredicate(
		EntityTypePredicate type,
		DistancePredicate distance,
		LocationPredicate location,
		EntityEffectPredicate effects,
		NbtPredicate nbt,
		EntityFlagsPredicate flags,
		EntityEquipmentPredicate equipment,
		PlayerPredicate player,
		FishingHookPredicate fishingHook,
		EntityPredicate entityPredicate,
		EntityPredicate entityPredicate2,
		@Nullable String string,
		@Nullable Identifier identifier
	) {
		this.type = type;
		this.distance = distance;
		this.location = location;
		this.effects = effects;
		this.nbt = nbt;
		this.flags = flags;
		this.equipment = equipment;
		this.player = player;
		this.fishingHook = fishingHook;
		this.field_24488 = entityPredicate;
		this.field_24489 = entityPredicate2;
		this.team = string;
		this.catType = identifier;
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
			} else if (!this.effects.test(entity)) {
				return false;
			} else if (!this.nbt.test(entity)) {
				return false;
			} else if (!this.flags.test(entity)) {
				return false;
			} else if (!this.equipment.test(entity)) {
				return false;
			} else if (!this.player.test(entity)) {
				return false;
			} else if (!this.fishingHook.test(entity)) {
				return false;
			} else if (!this.field_24488.test(world, pos, entity.getVehicle())) {
				return false;
			} else if (!this.field_24489.test(world, pos, entity instanceof MobEntity ? ((MobEntity)entity).getTarget() : null)) {
				return false;
			} else {
				if (this.team != null) {
					AbstractTeam abstractTeam = entity.getScoreboardTeam();
					if (abstractTeam == null || !this.team.equals(abstractTeam.getName())) {
						return false;
					}
				}

				return this.catType == null || entity instanceof CatEntity && ((CatEntity)entity).getTexture().equals(this.catType);
			}
		}
	}

	public static EntityPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "entity");
			EntityTypePredicate entityTypePredicate = EntityTypePredicate.fromJson(jsonObject.get("type"));
			DistancePredicate distancePredicate = DistancePredicate.fromJson(jsonObject.get("distance"));
			LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
			EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.fromJson(jsonObject.get("effects"));
			NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
			EntityFlagsPredicate entityFlagsPredicate = EntityFlagsPredicate.fromJson(jsonObject.get("flags"));
			EntityEquipmentPredicate entityEquipmentPredicate = EntityEquipmentPredicate.fromJson(jsonObject.get("equipment"));
			PlayerPredicate playerPredicate = PlayerPredicate.fromJson(jsonObject.get("player"));
			FishingHookPredicate fishingHookPredicate = FishingHookPredicate.fromJson(jsonObject.get("fishing_hook"));
			EntityPredicate entityPredicate = fromJson(jsonObject.get("vehicle"));
			EntityPredicate entityPredicate2 = fromJson(jsonObject.get("targeted_entity"));
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
				.player(playerPredicate)
				.fishHook(fishingHookPredicate)
				.team(string)
				.method_27971(entityPredicate)
				.method_27972(entityPredicate2)
				.catType(identifier)
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
			jsonObject.add("effects", this.effects.toJson());
			jsonObject.add("nbt", this.nbt.toJson());
			jsonObject.add("flags", this.flags.toJson());
			jsonObject.add("equipment", this.equipment.toJson());
			jsonObject.add("player", this.player.toJson());
			jsonObject.add("fishing_hook", this.fishingHook.toJson());
			jsonObject.add("vehicle", this.field_24488.toJson());
			jsonObject.add("targeted_entity", this.field_24489.toJson());
			jsonObject.addProperty("team", this.team);
			if (this.catType != null) {
				jsonObject.addProperty("catType", this.catType.toString());
			}

			return jsonObject;
		}
	}

	public static LootContext createAdvancementEntityLootContext(ServerPlayerEntity player, Entity target) {
		return new LootContext.Builder(player.getServerWorld())
			.parameter(LootContextParameters.THIS_ENTITY, target)
			.parameter(LootContextParameters.POSITION, target.getBlockPos())
			.parameter(LootContextParameters.ORIGIN, player.getPos())
			.random(player.getRandom())
			.build(LootContextTypes.ADVANCEMENT_ENTITY);
	}

	public static class Builder {
		private EntityTypePredicate type = EntityTypePredicate.ANY;
		private DistancePredicate distance = DistancePredicate.ANY;
		private LocationPredicate location = LocationPredicate.ANY;
		private EntityEffectPredicate effects = EntityEffectPredicate.EMPTY;
		private NbtPredicate nbt = NbtPredicate.ANY;
		private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
		private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
		private PlayerPredicate player = PlayerPredicate.ANY;
		private FishingHookPredicate fishHook = FishingHookPredicate.ANY;
		private EntityPredicate field_24490 = EntityPredicate.ANY;
		private EntityPredicate field_24491 = EntityPredicate.ANY;
		private String team;
		private Identifier catType;

		public static EntityPredicate.Builder create() {
			return new EntityPredicate.Builder();
		}

		public EntityPredicate.Builder type(EntityType<?> type) {
			this.type = EntityTypePredicate.create(type);
			return this;
		}

		public EntityPredicate.Builder type(Tag<EntityType<?>> tag) {
			this.type = EntityTypePredicate.create(tag);
			return this;
		}

		public EntityPredicate.Builder type(Identifier catType) {
			this.catType = catType;
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

		public EntityPredicate.Builder player(PlayerPredicate player) {
			this.player = player;
			return this;
		}

		public EntityPredicate.Builder fishHook(FishingHookPredicate fishHook) {
			this.fishHook = fishHook;
			return this;
		}

		public EntityPredicate.Builder method_27971(EntityPredicate entityPredicate) {
			this.field_24490 = entityPredicate;
			return this;
		}

		public EntityPredicate.Builder method_27972(EntityPredicate entityPredicate) {
			this.field_24491 = entityPredicate;
			return this;
		}

		public EntityPredicate.Builder team(@Nullable String team) {
			this.team = team;
			return this;
		}

		public EntityPredicate.Builder catType(@Nullable Identifier catType) {
			this.catType = catType;
			return this;
		}

		public EntityPredicate build() {
			return new EntityPredicate(
				this.type,
				this.distance,
				this.location,
				this.effects,
				this.nbt,
				this.flags,
				this.equipment,
				this.player,
				this.fishHook,
				this.field_24490,
				this.field_24491,
				this.team,
				this.catType
			);
		}
	}

	public static class Extended {
		public static final EntityPredicate.Extended EMPTY = new EntityPredicate.Extended(new LootCondition[0]);
		private final LootCondition[] conditions;
		private final Predicate<LootContext> combinedCondition;

		private Extended(LootCondition[] conditions) {
			this.conditions = conditions;
			this.combinedCondition = LootConditions.joinAnd(conditions);
		}

		public static EntityPredicate.Extended method_27973(LootCondition... lootConditions) {
			return new EntityPredicate.Extended(lootConditions);
		}

		/**
		 * Parses an extended entity check in a sub-value in JSON, accepting objects
		 * as simple entity predicates or array as list of loot conditions.
		 * 
		 * @see <a href="https://www.minecraft.net/en-us/article/minecraft-snapshot-20w18a">
		 * Mojang description</a>
		 */
		public static EntityPredicate.Extended getInJson(JsonObject root, String key, AdvancementEntityPredicateDeserializer predicateDeserializer) {
			JsonElement jsonElement = root.get(key);
			return fromJson(key, predicateDeserializer, jsonElement);
		}

		/**
		 * Parses an extended entity check in a sub-value in JSON. The value must
		 * be absent, JSON null, or an array, or the parser will error. In other
		 * words, it does not accept object sub-values.
		 */
		public static EntityPredicate.Extended[] requireInJson(JsonObject root, String key, AdvancementEntityPredicateDeserializer predicateDeserializer) {
			JsonElement jsonElement = root.get(key);
			if (jsonElement != null && !jsonElement.isJsonNull()) {
				JsonArray jsonArray = JsonHelper.asArray(jsonElement, key);
				EntityPredicate.Extended[] extendeds = new EntityPredicate.Extended[jsonArray.size()];

				for (int i = 0; i < jsonArray.size(); i++) {
					extendeds[i] = fromJson(key + "[" + i + "]", predicateDeserializer, jsonArray.get(i));
				}

				return extendeds;
			} else {
				return new EntityPredicate.Extended[0];
			}
		}

		private static EntityPredicate.Extended fromJson(String key, AdvancementEntityPredicateDeserializer predicateDeserializer, @Nullable JsonElement json) {
			if (json != null && json.isJsonArray()) {
				LootCondition[] lootConditions = predicateDeserializer.loadConditions(
					json.getAsJsonArray(), predicateDeserializer.getAdvancementId().toString() + "/" + key, LootContextTypes.ADVANCEMENT_ENTITY
				);
				return new EntityPredicate.Extended(lootConditions);
			} else {
				EntityPredicate entityPredicate = EntityPredicate.fromJson(json);
				return ofLegacy(entityPredicate);
			}
		}

		public static EntityPredicate.Extended ofLegacy(EntityPredicate predicate) {
			if (predicate == EntityPredicate.ANY) {
				return EMPTY;
			} else {
				LootCondition lootCondition = EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, predicate).build();
				return new EntityPredicate.Extended(new LootCondition[]{lootCondition});
			}
		}

		public boolean test(LootContext context) {
			return this.combinedCondition.test(context);
		}

		public JsonElement toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			return (JsonElement)(this.conditions.length == 0 ? JsonNull.INSTANCE : predicateSerializer.conditionsToJson(this.conditions));
		}

		public static JsonElement toPredicatesJsonArray(EntityPredicate.Extended[] predicates, AdvancementEntityPredicateSerializer predicateSerializer) {
			if (predicates.length == 0) {
				return JsonNull.INSTANCE;
			} else {
				JsonArray jsonArray = new JsonArray();

				for (EntityPredicate.Extended extended : predicates) {
					jsonArray.add(extended.toJson(predicateSerializer));
				}

				return jsonArray;
			}
		}
	}
}
