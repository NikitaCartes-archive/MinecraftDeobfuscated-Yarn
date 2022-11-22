package net.minecraft.predicate.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerType;

public interface TypeSpecificPredicate {
	TypeSpecificPredicate ANY = new TypeSpecificPredicate() {
		@Override
		public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
			return true;
		}

		@Override
		public JsonObject typeSpecificToJson() {
			return new JsonObject();
		}

		@Override
		public TypeSpecificPredicate.Deserializer getDeserializer() {
			return TypeSpecificPredicate.Deserializers.ANY;
		}
	};

	static TypeSpecificPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "type_specific");
			String string = JsonHelper.getString(jsonObject, "type", null);
			if (string == null) {
				return ANY;
			} else {
				TypeSpecificPredicate.Deserializer deserializer = (TypeSpecificPredicate.Deserializer)TypeSpecificPredicate.Deserializers.TYPES.get(string);
				if (deserializer == null) {
					throw new JsonSyntaxException("Unknown sub-predicate type: " + string);
				} else {
					return deserializer.deserialize(jsonObject);
				}
			}
		} else {
			return ANY;
		}
	}

	boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos);

	JsonObject typeSpecificToJson();

	default JsonElement toJson() {
		if (this.getDeserializer() == TypeSpecificPredicate.Deserializers.ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = this.typeSpecificToJson();
			String string = (String)TypeSpecificPredicate.Deserializers.TYPES.inverse().get(this.getDeserializer());
			jsonObject.addProperty("type", string);
			return jsonObject;
		}
	}

	TypeSpecificPredicate.Deserializer getDeserializer();

	static TypeSpecificPredicate cat(CatVariant variant) {
		return TypeSpecificPredicate.Deserializers.CAT.createPredicate(variant);
	}

	static TypeSpecificPredicate frog(FrogVariant variant) {
		return TypeSpecificPredicate.Deserializers.FROG.createPredicate(variant);
	}

	public interface Deserializer {
		TypeSpecificPredicate deserialize(JsonObject json);
	}

	public static final class Deserializers {
		public static final TypeSpecificPredicate.Deserializer ANY = json -> TypeSpecificPredicate.ANY;
		public static final TypeSpecificPredicate.Deserializer LIGHTNING = LightningBoltPredicate::fromJson;
		public static final TypeSpecificPredicate.Deserializer FISHING_HOOK = FishingHookPredicate::fromJson;
		public static final TypeSpecificPredicate.Deserializer PLAYER = PlayerPredicate::fromJson;
		public static final TypeSpecificPredicate.Deserializer SLIME = SlimePredicate::fromJson;
		public static final VariantPredicates<CatVariant> CAT = VariantPredicates.create(
			Registries.CAT_VARIANT, entity -> entity instanceof CatEntity catEntity ? Optional.of(catEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<FrogVariant> FROG = VariantPredicates.create(
			Registries.FROG_VARIANT, entity -> entity instanceof FrogEntity frogEntity ? Optional.of(frogEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<AxolotlEntity.Variant> AXOLOTL = VariantPredicates.create(
			AxolotlEntity.Variant.CODEC, entity -> entity instanceof AxolotlEntity axolotlEntity ? Optional.of(axolotlEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<BoatEntity.Type> BOAT = VariantPredicates.create(
			BoatEntity.Type.CODEC, entity -> entity instanceof BoatEntity boatEntity ? Optional.of(boatEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<FoxEntity.Type> FOX = VariantPredicates.create(
			FoxEntity.Type.CODEC, entity -> entity instanceof FoxEntity foxEntity ? Optional.of(foxEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<MooshroomEntity.Type> MOOSHROOM = VariantPredicates.create(
			MooshroomEntity.Type.CODEC, entity -> entity instanceof MooshroomEntity mooshroomEntity ? Optional.of(mooshroomEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<RegistryEntry<PaintingVariant>> PAINTING = VariantPredicates.create(
			Registries.PAINTING_VARIANT.createEntryCodec(),
			entity -> entity instanceof PaintingEntity paintingEntity ? Optional.of(paintingEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<RabbitEntity.RabbitType> RABBIT = VariantPredicates.create(
			RabbitEntity.RabbitType.CODEC, entity -> entity instanceof RabbitEntity rabbitEntity ? Optional.of(rabbitEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<HorseColor> HORSE = VariantPredicates.create(
			HorseColor.CODEC, entity -> entity instanceof HorseEntity horseEntity ? Optional.of(horseEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<LlamaEntity.Variant> LLAMA = VariantPredicates.create(
			LlamaEntity.Variant.CODEC, entity -> entity instanceof LlamaEntity llamaEntity ? Optional.of(llamaEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<VillagerType> VILLAGER = VariantPredicates.create(
			Registries.VILLAGER_TYPE.getCodec(),
			entity -> entity instanceof VillagerDataContainer villagerDataContainer ? Optional.of(villagerDataContainer.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<ParrotEntity.Variant> PARROT = VariantPredicates.create(
			ParrotEntity.Variant.CODEC, entity -> entity instanceof ParrotEntity parrotEntity ? Optional.of(parrotEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<TropicalFishEntity.Variety> TROPICAL_FISH = VariantPredicates.create(
			TropicalFishEntity.Variety.CODEC,
			entity -> entity instanceof TropicalFishEntity tropicalFishEntity ? Optional.of(tropicalFishEntity.getVariant()) : Optional.empty()
		);
		public static final BiMap<String, TypeSpecificPredicate.Deserializer> TYPES = ImmutableBiMap.<String, TypeSpecificPredicate.Deserializer>builder()
			.put("any", ANY)
			.put("lightning", LIGHTNING)
			.put("fishing_hook", FISHING_HOOK)
			.put("player", PLAYER)
			.put("slime", SLIME)
			.put("cat", CAT.getDeserializer())
			.put("frog", FROG.getDeserializer())
			.put("axolotl", AXOLOTL.getDeserializer())
			.put("boat", BOAT.getDeserializer())
			.put("fox", FOX.getDeserializer())
			.put("mooshroom", MOOSHROOM.getDeserializer())
			.put("painting", PAINTING.getDeserializer())
			.put("rabbit", RABBIT.getDeserializer())
			.put("horse", HORSE.getDeserializer())
			.put("llama", LLAMA.getDeserializer())
			.put("villager", VILLAGER.getDeserializer())
			.put("parrot", PARROT.getDeserializer())
			.put("tropical_fish", TROPICAL_FISH.getDeserializer())
			.buildOrThrow();
	}
}
