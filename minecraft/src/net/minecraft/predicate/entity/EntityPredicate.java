package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
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
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;

public record EntityPredicate(
	Optional<EntityTypePredicate> type,
	Optional<DistancePredicate> distance,
	Optional<LocationPredicate> location,
	Optional<LocationPredicate> steppingOn,
	Optional<EntityEffectPredicate> effects,
	Optional<NbtPredicate> nbt,
	Optional<EntityFlagsPredicate> flags,
	Optional<EntityEquipmentPredicate> equipment,
	Optional<TypeSpecificPredicate> typeSpecific,
	Optional<EntityPredicate> vehicle,
	Optional<EntityPredicate> passenger,
	Optional<EntityPredicate> targetedEntity,
	Optional<String> team
) {
	public static final Codec<EntityPredicate> CODEC = Codecs.createRecursive(
		codec -> RecordCodecBuilder.create(
				instance -> instance.group(
							Codecs.createStrictOptionalFieldCodec(EntityTypePredicate.CODEC, "type").forGetter(EntityPredicate::type),
							Codecs.createStrictOptionalFieldCodec(DistancePredicate.CODEC, "distance").forGetter(EntityPredicate::distance),
							Codecs.createStrictOptionalFieldCodec(LocationPredicate.CODEC, "location").forGetter(EntityPredicate::location),
							Codecs.createStrictOptionalFieldCodec(LocationPredicate.CODEC, "stepping_on").forGetter(EntityPredicate::steppingOn),
							Codecs.createStrictOptionalFieldCodec(EntityEffectPredicate.CODEC, "effects").forGetter(EntityPredicate::effects),
							Codecs.createStrictOptionalFieldCodec(NbtPredicate.CODEC, "nbt").forGetter(EntityPredicate::nbt),
							Codecs.createStrictOptionalFieldCodec(EntityFlagsPredicate.CODEC, "flags").forGetter(EntityPredicate::flags),
							Codecs.createStrictOptionalFieldCodec(EntityEquipmentPredicate.CODEC, "equipment").forGetter(EntityPredicate::equipment),
							Codecs.createStrictOptionalFieldCodec(TypeSpecificPredicate.CODEC, "type_specific").forGetter(EntityPredicate::typeSpecific),
							Codecs.createStrictOptionalFieldCodec(codec, "vehicle").forGetter(EntityPredicate::vehicle),
							Codecs.createStrictOptionalFieldCodec(codec, "passenger").forGetter(EntityPredicate::passenger),
							Codecs.createStrictOptionalFieldCodec(codec, "targeted_entity").forGetter(EntityPredicate::targetedEntity),
							Codecs.createStrictOptionalFieldCodec(Codec.STRING, "team").forGetter(EntityPredicate::team)
						)
						.apply(instance, EntityPredicate::new)
			)
	);

	public static Optional<EntityPredicate> create(
		Optional<EntityTypePredicate> type,
		Optional<DistancePredicate> distance,
		Optional<LocationPredicate> location,
		Optional<LocationPredicate> steppingOn,
		Optional<EntityEffectPredicate> effects,
		Optional<NbtPredicate> nbt,
		Optional<EntityFlagsPredicate> flags,
		Optional<EntityEquipmentPredicate> equipment,
		Optional<TypeSpecificPredicate> typeSpecific,
		Optional<EntityPredicate> vehicle,
		Optional<EntityPredicate> passenger,
		Optional<EntityPredicate> targetedEntity,
		Optional<String> team
	) {
		return type.isEmpty()
				&& distance.isEmpty()
				&& location.isEmpty()
				&& steppingOn.isEmpty()
				&& effects.isEmpty()
				&& nbt.isEmpty()
				&& flags.isEmpty()
				&& equipment.isEmpty()
				&& typeSpecific.isEmpty()
				&& vehicle.isEmpty()
				&& passenger.isEmpty()
				&& targetedEntity.isEmpty()
				&& team.isEmpty()
			? Optional.empty()
			: Optional.of(
				new EntityPredicate(type, distance, location, steppingOn, effects, nbt, flags, equipment, typeSpecific, vehicle, passenger, targetedEntity, team)
			);
	}

	public static Optional<LootContextPredicate> contextPredicateFromJson(
		JsonObject json, String key, AdvancementEntityPredicateDeserializer predicateDeserializer
	) {
		JsonElement jsonElement = json.get(key);
		return contextPredicateFromJsonElement(key, predicateDeserializer, jsonElement);
	}

	public static List<LootContextPredicate> contextPredicateArrayFromJson(
		JsonObject json, String key, AdvancementEntityPredicateDeserializer predicateDeserializer
	) {
		JsonElement jsonElement = json.get(key);
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = JsonHelper.asArray(jsonElement, key);
			List<LootContextPredicate> list = new ArrayList(jsonArray.size());

			for (int i = 0; i < jsonArray.size(); i++) {
				contextPredicateFromJsonElement(key + "[" + i + "]", predicateDeserializer, jsonArray.get(i)).ifPresent(list::add);
			}

			return List.copyOf(list);
		} else {
			return List.of();
		}
	}

	private static Optional<LootContextPredicate> contextPredicateFromJsonElement(
		String key, AdvancementEntityPredicateDeserializer predicateDeserializer, @Nullable JsonElement json
	) {
		Optional<Optional<LootContextPredicate>> optional = LootContextPredicate.fromJson(key, predicateDeserializer, json, LootContextTypes.ADVANCEMENT_ENTITY);
		if (optional.isPresent()) {
			return (Optional<LootContextPredicate>)optional.get();
		} else {
			Optional<EntityPredicate> optional2 = fromJson(json);
			return contextPredicateFromEntityPredicate(optional2);
		}
	}

	public static Optional<LootContextPredicate> contextPredicateFromEntityPredicate(EntityPredicate.Builder builder) {
		return contextPredicateFromEntityPredicate(builder.build());
	}

	public static Optional<LootContextPredicate> contextPredicateFromEntityPredicate(Optional<EntityPredicate> entityPredicate) {
		return entityPredicate.map(EntityPredicate::asLootContextPredicate);
	}

	public static List<LootContextPredicate> contextPredicateFromEntityPredicates(EntityPredicate.Builder... builders) {
		return Stream.of(builders).flatMap(builder -> contextPredicateFromEntityPredicate(builder).stream()).toList();
	}

	public static LootContextPredicate asLootContextPredicate(EntityPredicate predicate) {
		LootCondition lootCondition = EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, predicate).build();
		return new LootContextPredicate(List.of(lootCondition));
	}

	public boolean test(ServerPlayerEntity player, @Nullable Entity entity) {
		return this.test(player.getServerWorld(), player.getPos(), entity);
	}

	public boolean test(ServerWorld world, @Nullable Vec3d pos, @Nullable Entity entity) {
		if (entity == null) {
			return false;
		} else if (this.type.isPresent() && !((EntityTypePredicate)this.type.get()).matches(entity.getType())) {
			return false;
		} else {
			if (pos == null) {
				if (this.distance.isPresent()) {
					return false;
				}
			} else if (this.distance.isPresent() && !((DistancePredicate)this.distance.get()).test(pos.x, pos.y, pos.z, entity.getX(), entity.getY(), entity.getZ())) {
				return false;
			}

			if (this.location.isPresent() && !((LocationPredicate)this.location.get()).test(world, entity.getX(), entity.getY(), entity.getZ())) {
				return false;
			} else {
				if (this.steppingOn.isPresent()) {
					Vec3d vec3d = Vec3d.ofCenter(entity.getSteppingPos());
					if (!((LocationPredicate)this.steppingOn.get()).test(world, vec3d.getX(), vec3d.getY(), vec3d.getZ())) {
						return false;
					}
				}

				if (this.effects.isPresent() && !((EntityEffectPredicate)this.effects.get()).test(entity)) {
					return false;
				} else if (this.nbt.isPresent() && !((NbtPredicate)this.nbt.get()).test(entity)) {
					return false;
				} else if (this.flags.isPresent() && !((EntityFlagsPredicate)this.flags.get()).test(entity)) {
					return false;
				} else if (this.equipment.isPresent() && !((EntityEquipmentPredicate)this.equipment.get()).test(entity)) {
					return false;
				} else if (this.typeSpecific.isPresent() && !((TypeSpecificPredicate)this.typeSpecific.get()).test(entity, world, pos)) {
					return false;
				} else if (this.vehicle.isPresent() && !((EntityPredicate)this.vehicle.get()).test(world, pos, entity.getVehicle())) {
					return false;
				} else if (this.passenger.isPresent()
					&& entity.getPassengerList().stream().noneMatch(entityx -> ((EntityPredicate)this.passenger.get()).test(world, pos, entityx))) {
					return false;
				} else if (this.targetedEntity.isPresent()
					&& !((EntityPredicate)this.targetedEntity.get()).test(world, pos, entity instanceof MobEntity ? ((MobEntity)entity).getTarget() : null)) {
					return false;
				} else {
					if (this.team.isPresent()) {
						AbstractTeam abstractTeam = entity.getScoreboardTeam();
						if (abstractTeam == null || !((String)this.team.get()).equals(abstractTeam.getName())) {
							return false;
						}
					}

					return true;
				}
			}
		}
	}

	public static Optional<EntityPredicate> fromJson(@Nullable JsonElement json) {
		return json != null && !json.isJsonNull() ? Optional.of(Util.getResult(CODEC.parse(JsonOps.INSTANCE, json), JsonParseException::new)) : Optional.empty();
	}

	public JsonElement toJson() {
		return Util.getResult(CODEC.encodeStart(JsonOps.INSTANCE, this), IllegalStateException::new);
	}

	public static LootContext createAdvancementEntityLootContext(ServerPlayerEntity player, Entity target) {
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(player.getServerWorld())
			.add(LootContextParameters.THIS_ENTITY, target)
			.add(LootContextParameters.ORIGIN, player.getPos())
			.build(LootContextTypes.ADVANCEMENT_ENTITY);
		return new LootContext.Builder(lootContextParameterSet).build(Optional.empty());
	}

	public static class Builder {
		private Optional<EntityTypePredicate> type = Optional.empty();
		private Optional<DistancePredicate> distance = Optional.empty();
		private Optional<LocationPredicate> location = Optional.empty();
		private Optional<LocationPredicate> steppingOn = Optional.empty();
		private Optional<EntityEffectPredicate> effects = Optional.empty();
		private Optional<NbtPredicate> nbt = Optional.empty();
		private Optional<EntityFlagsPredicate> flags = Optional.empty();
		private Optional<EntityEquipmentPredicate> equipment = Optional.empty();
		private Optional<TypeSpecificPredicate> typeSpecific = Optional.empty();
		private Optional<EntityPredicate> vehicle = Optional.empty();
		private Optional<EntityPredicate> passenger = Optional.empty();
		private Optional<EntityPredicate> targetedEntity = Optional.empty();
		private Optional<String> team = Optional.empty();

		public static EntityPredicate.Builder create() {
			return new EntityPredicate.Builder();
		}

		public EntityPredicate.Builder type(EntityType<?> type) {
			this.type = Optional.of(EntityTypePredicate.create(type));
			return this;
		}

		public EntityPredicate.Builder type(TagKey<EntityType<?>> tag) {
			this.type = Optional.of(EntityTypePredicate.create(tag));
			return this;
		}

		public EntityPredicate.Builder type(EntityTypePredicate type) {
			this.type = Optional.of(type);
			return this;
		}

		public EntityPredicate.Builder distance(DistancePredicate distance) {
			this.distance = Optional.of(distance);
			return this;
		}

		public EntityPredicate.Builder location(LocationPredicate.Builder location) {
			this.location = location.build();
			return this;
		}

		public EntityPredicate.Builder steppingOn(LocationPredicate.Builder steppingOn) {
			this.steppingOn = steppingOn.build();
			return this;
		}

		public EntityPredicate.Builder effects(EntityEffectPredicate.Builder effects) {
			this.effects = effects.build();
			return this;
		}

		public EntityPredicate.Builder nbt(NbtPredicate nbt) {
			this.nbt = Optional.of(nbt);
			return this;
		}

		public EntityPredicate.Builder flags(EntityFlagsPredicate.Builder flags) {
			this.flags = flags.build();
			return this;
		}

		public EntityPredicate.Builder equipment(EntityEquipmentPredicate.Builder equipment) {
			this.equipment = equipment.build();
			return this;
		}

		public EntityPredicate.Builder equipment(EntityEquipmentPredicate equipment) {
			this.equipment = Optional.of(equipment);
			return this;
		}

		public EntityPredicate.Builder typeSpecific(TypeSpecificPredicate typeSpecific) {
			this.typeSpecific = Optional.of(typeSpecific);
			return this;
		}

		public EntityPredicate.Builder vehicle(EntityPredicate.Builder vehicle) {
			this.vehicle = vehicle.build();
			return this;
		}

		public EntityPredicate.Builder passenger(EntityPredicate.Builder passenger) {
			this.passenger = passenger.build();
			return this;
		}

		public EntityPredicate.Builder targetedEntity(EntityPredicate.Builder targetedEntity) {
			this.targetedEntity = targetedEntity.build();
			return this;
		}

		public EntityPredicate.Builder team(String team) {
			this.team = Optional.of(team);
			return this;
		}

		public Optional<EntityPredicate> build() {
			return EntityPredicate.create(
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
