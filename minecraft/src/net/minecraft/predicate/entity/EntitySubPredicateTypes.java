package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
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
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerType;

public class EntitySubPredicateTypes {
	public static final MapCodec<LightningBoltPredicate> LIGHTNING = register("lightning", LightningBoltPredicate.CODEC);
	public static final MapCodec<FishingHookPredicate> FISHING_HOOK = register("fishing_hook", FishingHookPredicate.CODEC);
	public static final MapCodec<PlayerPredicate> PLAYER = register("player", PlayerPredicate.CODEC);
	public static final MapCodec<SlimePredicate> SLIME = register("slime", SlimePredicate.CODEC);
	public static final MapCodec<RaiderPredicate> RAIDER = register("raider", RaiderPredicate.CODEC);
	public static final EntitySubPredicateTypes.VariantType<AxolotlEntity.Variant> AXOLOTL = register(
		"axolotl",
		EntitySubPredicateTypes.VariantType.create(
			AxolotlEntity.Variant.CODEC, entity -> entity instanceof AxolotlEntity axolotlEntity ? Optional.of(axolotlEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.VariantType<BoatEntity.Type> BOAT = register(
		"boat",
		EntitySubPredicateTypes.VariantType.create(
			BoatEntity.Type.CODEC, entity -> entity instanceof BoatEntity boatEntity ? Optional.of(boatEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.VariantType<FoxEntity.Type> FOX = register(
		"fox",
		EntitySubPredicateTypes.VariantType.create(
			FoxEntity.Type.CODEC, entity -> entity instanceof FoxEntity foxEntity ? Optional.of(foxEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.VariantType<MooshroomEntity.Type> MOOSHROOM = register(
		"mooshroom",
		EntitySubPredicateTypes.VariantType.create(
			MooshroomEntity.Type.CODEC, entity -> entity instanceof MooshroomEntity mooshroomEntity ? Optional.of(mooshroomEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.VariantType<RabbitEntity.RabbitType> RABBIT = register(
		"rabbit",
		EntitySubPredicateTypes.VariantType.create(
			RabbitEntity.RabbitType.CODEC, entity -> entity instanceof RabbitEntity rabbitEntity ? Optional.of(rabbitEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.VariantType<HorseColor> HORSE = register(
		"horse",
		EntitySubPredicateTypes.VariantType.create(
			HorseColor.CODEC, entity -> entity instanceof HorseEntity horseEntity ? Optional.of(horseEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.VariantType<LlamaEntity.Variant> LLAMA = register(
		"llama",
		EntitySubPredicateTypes.VariantType.create(
			LlamaEntity.Variant.CODEC, entity -> entity instanceof LlamaEntity llamaEntity ? Optional.of(llamaEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.VariantType<VillagerType> VILLAGER = register(
		"villager",
		EntitySubPredicateTypes.VariantType.create(
			Registries.VILLAGER_TYPE.getCodec(),
			entity -> entity instanceof VillagerDataContainer villagerDataContainer ? Optional.of(villagerDataContainer.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.VariantType<ParrotEntity.Variant> PARROT = register(
		"parrot",
		EntitySubPredicateTypes.VariantType.create(
			ParrotEntity.Variant.CODEC, entity -> entity instanceof ParrotEntity parrotEntity ? Optional.of(parrotEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.VariantType<TropicalFishEntity.Variety> TROPICAL_FISH = register(
		"tropical_fish",
		EntitySubPredicateTypes.VariantType.create(
			TropicalFishEntity.Variety.CODEC,
			entity -> entity instanceof TropicalFishEntity tropicalFishEntity ? Optional.of(tropicalFishEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.DynamicVariantType<PaintingVariant> PAINTING = register(
		"painting",
		EntitySubPredicateTypes.DynamicVariantType.create(
			RegistryKeys.PAINTING_VARIANT, entity -> entity instanceof PaintingEntity paintingEntity ? Optional.of(paintingEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.DynamicVariantType<CatVariant> CAT = register(
		"cat",
		EntitySubPredicateTypes.DynamicVariantType.create(
			RegistryKeys.CAT_VARIANT, entity -> entity instanceof CatEntity catEntity ? Optional.of(catEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.DynamicVariantType<FrogVariant> FROG = register(
		"frog",
		EntitySubPredicateTypes.DynamicVariantType.create(
			RegistryKeys.FROG_VARIANT, entity -> entity instanceof FrogEntity frogEntity ? Optional.of(frogEntity.getVariant()) : Optional.empty()
		)
	);
	public static final EntitySubPredicateTypes.DynamicVariantType<WolfVariant> WOLF = register(
		"wolf",
		EntitySubPredicateTypes.DynamicVariantType.create(
			RegistryKeys.WOLF_VARIANT, entity -> entity instanceof WolfEntity wolfEntity ? Optional.of(wolfEntity.getVariant()) : Optional.empty()
		)
	);

	private static <T extends EntitySubPredicate> MapCodec<T> register(String id, MapCodec<T> codec) {
		return Registry.register(Registries.ENTITY_SUB_PREDICATE_TYPE, id, codec);
	}

	private static <V> EntitySubPredicateTypes.VariantType<V> register(String id, EntitySubPredicateTypes.VariantType<V> type) {
		Registry.register(Registries.ENTITY_SUB_PREDICATE_TYPE, id, type.codec);
		return type;
	}

	private static <V> EntitySubPredicateTypes.DynamicVariantType<V> register(String id, EntitySubPredicateTypes.DynamicVariantType<V> type) {
		Registry.register(Registries.ENTITY_SUB_PREDICATE_TYPE, id, type.codec);
		return type;
	}

	public static MapCodec<? extends EntitySubPredicate> getDefault(Registry<MapCodec<? extends EntitySubPredicate>> registry) {
		return LIGHTNING;
	}

	public static EntitySubPredicate catVariant(RegistryEntry<CatVariant> catVariant) {
		return CAT.createPredicate(RegistryEntryList.of(catVariant));
	}

	public static EntitySubPredicate frogVariant(RegistryEntry<FrogVariant> frogVariant) {
		return FROG.createPredicate(RegistryEntryList.of(frogVariant));
	}

	public static class DynamicVariantType<V> {
		final MapCodec<EntitySubPredicateTypes.DynamicVariantType<V>.DynamicPredicate> codec;
		final Function<Entity, Optional<RegistryEntry<V>>> variantGetter;

		public static <V> EntitySubPredicateTypes.DynamicVariantType<V> create(
			RegistryKey<? extends Registry<V>> registryRef, Function<Entity, Optional<RegistryEntry<V>>> variantGetter
		) {
			return new EntitySubPredicateTypes.DynamicVariantType<>(registryRef, variantGetter);
		}

		public DynamicVariantType(RegistryKey<? extends Registry<V>> registryRef, Function<Entity, Optional<RegistryEntry<V>>> variantGetter) {
			this.variantGetter = variantGetter;
			this.codec = RecordCodecBuilder.mapCodec(
				instance -> instance.group(RegistryCodecs.entryList(registryRef).fieldOf("variant").forGetter(type -> type.variants))
						.apply(instance, entries -> new EntitySubPredicateTypes.DynamicVariantType.DynamicPredicate(entries))
			);
		}

		public EntitySubPredicate createPredicate(RegistryEntryList<V> variants) {
			return new EntitySubPredicateTypes.DynamicVariantType.DynamicPredicate(variants);
		}

		class DynamicPredicate implements EntitySubPredicate {
			final RegistryEntryList<V> variants;

			DynamicPredicate(RegistryEntryList<V> variants) {
				this.variants = variants;
			}

			@Override
			public MapCodec<EntitySubPredicateTypes.DynamicVariantType<V>.DynamicPredicate> getCodec() {
				return DynamicVariantType.this.codec;
			}

			@Override
			public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
				return ((Optional)DynamicVariantType.this.variantGetter.apply(entity)).filter(this.variants::contains).isPresent();
			}
		}
	}

	public static class VariantType<V> {
		final MapCodec<EntitySubPredicateTypes.VariantType<V>.VariantPredicate> codec;
		final Function<Entity, Optional<V>> variantGetter;

		public static <V> EntitySubPredicateTypes.VariantType<V> create(Registry<V> registry, Function<Entity, Optional<V>> variantGetter) {
			return new EntitySubPredicateTypes.VariantType<>(registry.getCodec(), variantGetter);
		}

		public static <V> EntitySubPredicateTypes.VariantType<V> create(Codec<V> codec, Function<Entity, Optional<V>> variantGetter) {
			return new EntitySubPredicateTypes.VariantType<>(codec, variantGetter);
		}

		public VariantType(Codec<V> variantCodec, Function<Entity, Optional<V>> variantGetter) {
			this.variantGetter = variantGetter;
			this.codec = RecordCodecBuilder.mapCodec(
				instance -> instance.group(variantCodec.fieldOf("variant").forGetter(predicate -> predicate.variant))
						.apply(instance, variant -> new EntitySubPredicateTypes.VariantType.VariantPredicate(variant))
			);
		}

		public EntitySubPredicate createPredicate(V variant) {
			return new EntitySubPredicateTypes.VariantType.VariantPredicate(variant);
		}

		class VariantPredicate implements EntitySubPredicate {
			final V variant;

			VariantPredicate(V variant) {
				this.variant = variant;
			}

			@Override
			public MapCodec<EntitySubPredicateTypes.VariantType<V>.VariantPredicate> getCodec() {
				return VariantType.this.codec;
			}

			@Override
			public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
				return ((Optional)VariantType.this.variantGetter.apply(entity)).filter(this.variant::equals).isPresent();
			}
		}
	}
}
