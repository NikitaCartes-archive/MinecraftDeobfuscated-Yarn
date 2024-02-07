package net.minecraft.predicate.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerType;

public interface TypeSpecificPredicate {
	Codec<TypeSpecificPredicate> CODEC = TypeSpecificPredicate.Deserializers.TYPE_CODEC.dispatch(TypeSpecificPredicate::type, type -> type.codec().codec());

	boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos);

	TypeSpecificPredicate.Type type();

	static TypeSpecificPredicate cat(CatVariant variant) {
		return TypeSpecificPredicate.Deserializers.CAT.createPredicate(variant);
	}

	static TypeSpecificPredicate frog(FrogVariant variant) {
		return TypeSpecificPredicate.Deserializers.FROG.createPredicate(variant);
	}

	public static final class Deserializers {
		public static final TypeSpecificPredicate.Type ANY = new TypeSpecificPredicate.Type(MapCodec.unit(new TypeSpecificPredicate() {
			@Override
			public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
				return true;
			}

			@Override
			public TypeSpecificPredicate.Type type() {
				return TypeSpecificPredicate.Deserializers.ANY;
			}
		}));
		public static final TypeSpecificPredicate.Type LIGHTNING = new TypeSpecificPredicate.Type(LightningBoltPredicate.CODEC);
		public static final TypeSpecificPredicate.Type FISHING_HOOK = new TypeSpecificPredicate.Type(FishingHookPredicate.CODEC);
		public static final TypeSpecificPredicate.Type PLAYER = new TypeSpecificPredicate.Type(PlayerPredicate.CODEC);
		public static final TypeSpecificPredicate.Type SLIME = new TypeSpecificPredicate.Type(SlimePredicate.CODEC);
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
			Registries.PAINTING_VARIANT.getEntryCodec(),
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
		public static final BiMap<String, TypeSpecificPredicate.Type> TYPES = ImmutableBiMap.<String, TypeSpecificPredicate.Type>builder()
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
		public static final Codec<TypeSpecificPredicate.Type> TYPE_CODEC = Codecs.idChecked(TYPES.inverse()::get, TYPES::get);
	}

	public static record Type(MapCodec<? extends TypeSpecificPredicate> codec) {
	}
}
