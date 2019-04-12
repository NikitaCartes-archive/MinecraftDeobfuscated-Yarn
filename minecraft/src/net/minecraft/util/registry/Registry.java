package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.ContainerType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.structure.StructureFeatures;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.IndexedIterable;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T> implements IndexedIterable<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.<Identifier, Supplier<?>>newLinkedHashMap();
	public static final MutableRegistry<MutableRegistry<?>> REGISTRIES = new SimpleRegistry<>();
	public static final Registry<SoundEvent> SOUND_EVENT = create("sound_event", new SimpleRegistry<>(), () -> SoundEvents.field_15197);
	public static final DefaultedRegistry<Fluid> FLUID = create("fluid", new DefaultedRegistry<>("empty"), () -> Fluids.EMPTY);
	public static final Registry<StatusEffect> STATUS_EFFECT = create("mob_effect", new SimpleRegistry<>(), () -> StatusEffects.field_5926);
	public static final DefaultedRegistry<Block> BLOCK = create("block", new DefaultedRegistry<>("air"), () -> Blocks.AIR);
	public static final Registry<Enchantment> ENCHANTMENT = create("enchantment", new SimpleRegistry<>(), () -> Enchantments.field_9130);
	public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = create("entity_type", new DefaultedRegistry<>("pig"), () -> EntityType.PIG);
	public static final DefaultedRegistry<Item> ITEM = create("item", new DefaultedRegistry<>("air"), () -> Items.AIR);
	public static final DefaultedRegistry<Potion> POTION = create("potion", new DefaultedRegistry<>("empty"), () -> Potions.field_8984);
	public static final Registry<Carver<?>> CARVER = create("carver", new SimpleRegistry<>(), () -> Carver.CAVE);
	public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = create("surface_builder", new SimpleRegistry<>(), () -> SurfaceBuilder.DEFAULT);
	public static final Registry<Feature<?>> FEATURE = create("feature", new SimpleRegistry<>(), () -> Feature.field_13517);
	public static final Registry<Decorator<?>> DECORATOR = create("decorator", new SimpleRegistry<>(), () -> Decorator.NOPE);
	public static final Registry<Biome> BIOME = create("biome", new SimpleRegistry<>(), () -> Biomes.DEFAULT);
	public static final Registry<ParticleType<? extends ParticleParameters>> PARTICLE_TYPE = create(
		"particle_type", new SimpleRegistry<>(), () -> ParticleTypes.field_11217
	);
	public static final Registry<BiomeSourceType<?, ?>> BIOME_SOURCE_TYPE = create(
		"biome_source_type", new SimpleRegistry<>(), () -> BiomeSourceType.VANILLA_LAYERED
	);
	public static final Registry<BlockEntityType<?>> BLOCK_ENTITY = create("block_entity_type", new SimpleRegistry<>(), () -> BlockEntityType.FURNACE);
	public static final Registry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE = create(
		"chunk_generator_type", new SimpleRegistry<>(), () -> ChunkGeneratorType.field_12766
	);
	public static final Registry<DimensionType> DIMENSION = create("dimension_type", new SimpleRegistry<>(), () -> DimensionType.field_13072);
	public static final DefaultedRegistry<PaintingMotive> MOTIVE = create("motive", new DefaultedRegistry<>("kebab"), () -> PaintingMotive.field_7146);
	public static final Registry<Identifier> CUSTOM_STAT = create("custom_stat", new SimpleRegistry<>(), () -> Stats.field_15428);
	public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = create("chunk_status", new DefaultedRegistry<>("empty"), () -> ChunkStatus.EMPTY);
	public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE = create("structure_feature", new SimpleRegistry<>(), () -> StructureFeatures.MINESHAFT);
	public static final Registry<StructurePieceType> STRUCTURE_PIECE = create("structure_piece", new SimpleRegistry<>(), () -> StructurePieceType.MINESHAFT_ROOM);
	public static final Registry<RuleTest> RULE_TEST = create("rule_test", new SimpleRegistry<>(), () -> RuleTest.field_16982);
	public static final Registry<StructureProcessorType> STRUCTURE_PROCESSOR = create(
		"structure_processor", new SimpleRegistry<>(), () -> StructureProcessorType.field_16986
	);
	public static final Registry<StructurePoolElementType> STRUCTURE_POOL_ELEMENT = create(
		"structure_pool_element", new SimpleRegistry<>(), () -> StructurePoolElementType.EMPTY_POOL_ELEMENT
	);
	public static final Registry<ContainerType<?>> CONTAINER = create("menu", new SimpleRegistry<>(), () -> ContainerType.ANVIL);
	public static final Registry<RecipeType<?>> RECIPE_TYPE = create("recipe_type", new SimpleRegistry<>(), () -> RecipeType.CRAFTING);
	public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = create("recipe_serializer", new SimpleRegistry<>(), () -> RecipeSerializer.SHAPELESS);
	public static final Registry<StatType<?>> STAT_TYPE = create("stat_type", new SimpleRegistry<>());
	public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = create("villager_type", new DefaultedRegistry<>("plains"), () -> VillagerType.PLAINS);
	public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = create(
		"villager_profession", new DefaultedRegistry<>("none"), () -> VillagerProfession.field_17051
	);
	public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE = create(
		"point_of_interest_type", new DefaultedRegistry<>("unemployed"), () -> PointOfInterestType.field_18502
	);
	public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = create(
		"memory_module_type", new DefaultedRegistry<>("dummy"), () -> MemoryModuleType.field_18437
	);
	public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = create("sensor_type", new DefaultedRegistry<>("dummy"), () -> SensorType.field_18465);
	public static final SimpleRegistry<Schedule> SCHEDULE = create("schedule", new SimpleRegistry<>(), () -> Schedule.EMPTY);
	public static final SimpleRegistry<Activity> ACTIVITY = create("activity", new SimpleRegistry<>(), () -> Activity.field_18595);

	private static <T> void putDefaultEntry(String string, Supplier<T> supplier) {
		DEFAULT_ENTRIES.put(new Identifier(string), supplier);
	}

	private static <T, R extends MutableRegistry<T>> R create(String string, R mutableRegistry, Supplier<T> supplier) {
		putDefaultEntry(string, supplier);
		create(string, mutableRegistry);
		return mutableRegistry;
	}

	private static <T> Registry<T> create(String string, MutableRegistry<T> mutableRegistry) {
		REGISTRIES.add(new Identifier(string), mutableRegistry);
		return mutableRegistry;
	}

	@Nullable
	public abstract Identifier getId(T object);

	public abstract int getRawId(@Nullable T object);

	@Nullable
	public abstract T get(@Nullable Identifier identifier);

	public abstract Optional<T> getOrEmpty(@Nullable Identifier identifier);

	public abstract Set<Identifier> getIds();

	@Nullable
	public abstract T getRandom(Random random);

	public Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	@Environment(EnvType.CLIENT)
	public abstract boolean containsId(Identifier identifier);

	public static <T> T register(Registry<? super T> registry, String string, T object) {
		return register(registry, new Identifier(string), object);
	}

	public static <T> T register(Registry<? super T> registry, Identifier identifier, T object) {
		return ((MutableRegistry)registry).add(identifier, object);
	}

	public static <T> T register(Registry<? super T> registry, int i, String string, T object) {
		return ((MutableRegistry)registry).set(i, new Identifier(string), object);
	}

	static {
		DEFAULT_ENTRIES.entrySet().forEach(entry -> {
			if (((Supplier)entry.getValue()).get() == null) {
				LOGGER.error("Unable to bootstrap registry '{}'", entry.getKey());
			}
		});
		REGISTRIES.forEach(mutableRegistry -> {
			if (mutableRegistry.isEmpty()) {
				LOGGER.error("Registry '{}' was empty after loading", REGISTRIES.getId(mutableRegistry));
				if (SharedConstants.isDevelopment) {
					throw new IllegalStateException("Registry: '" + REGISTRIES.getId(mutableRegistry) + "' is empty, not allowed, fix me!");
				}
			}

			if (mutableRegistry instanceof DefaultedRegistry) {
				Identifier identifier = ((DefaultedRegistry)mutableRegistry).getDefaultId();
				Validate.notNull(mutableRegistry.get(identifier), "Missing default of DefaultedMappedRegistry: " + identifier);
			}
		});
	}
}
