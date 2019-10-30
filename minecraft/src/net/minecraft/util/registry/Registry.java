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
import net.minecraft.particle.ParticleEffect;
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
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.placer.BlockPlacerType;
import net.minecraft.world.gen.stateprovider.StateProviderType;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T> implements IndexedIterable<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.<Identifier, Supplier<?>>newLinkedHashMap();
	public static final MutableRegistry<MutableRegistry<?>> REGISTRIES = new SimpleRegistry<>();
	public static final Registry<SoundEvent> SOUND_EVENT = create("sound_event", () -> SoundEvents.ENTITY_ITEM_PICKUP);
	public static final DefaultedRegistry<Fluid> FLUID = create("fluid", "empty", () -> Fluids.EMPTY);
	public static final Registry<StatusEffect> MOB_EFFECT = create("mob_effect", () -> StatusEffects.LUCK);
	public static final DefaultedRegistry<Block> BLOCK = create("block", "air", () -> Blocks.AIR);
	public static final Registry<Enchantment> ENCHANTMENT = create("enchantment", () -> Enchantments.FORTUNE);
	public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = create("entity_type", "pig", () -> EntityType.PIG);
	public static final DefaultedRegistry<Item> ITEM = create("item", "air", () -> Items.AIR);
	public static final DefaultedRegistry<Potion> POTION = create("potion", "empty", () -> Potions.EMPTY);
	public static final Registry<Carver<?>> CARVER = create("carver", () -> Carver.CAVE);
	public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = create("surface_builder", () -> SurfaceBuilder.DEFAULT);
	public static final Registry<Feature<?>> FEATURE = create("feature", () -> Feature.ORE);
	public static final Registry<Decorator<?>> DECORATOR = create("decorator", () -> Decorator.NOPE);
	public static final Registry<Biome> BIOME = create("biome", () -> Biomes.DEFAULT);
	public static final Registry<StateProviderType<?>> BLOCK_STATE_PROVIDER_TYPE = create(
		"block_state_provider_type", () -> StateProviderType.SIMPLE_STATE_PROVIDER
	);
	public static final Registry<BlockPlacerType<?>> BLOCK_PLACER_TYPE = create("block_placer_type", () -> BlockPlacerType.SIMPLE_BLOCK_PLACER);
	public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = create("foliage_placer_type", () -> FoliagePlacerType.BLOB_FOLIAGE_PLACER);
	public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = create("tree_decorator_type", () -> TreeDecoratorType.LEAVE_VINE);
	public static final Registry<ParticleType<? extends ParticleEffect>> PARTICLE_TYPE = create("particle_type", () -> ParticleTypes.BLOCK);
	public static final Registry<BiomeSourceType<?, ?>> BIOME_SOURCE_TYPE = create("biome_source_type", () -> BiomeSourceType.VANILLA_LAYERED);
	public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = create("block_entity_type", () -> BlockEntityType.FURNACE);
	public static final Registry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE = create("chunk_generator_type", () -> ChunkGeneratorType.FLAT);
	public static final Registry<DimensionType> DIMENSION_TYPE = create("dimension_type", () -> DimensionType.OVERWORLD);
	public static final DefaultedRegistry<PaintingMotive> MOTIVE = create("motive", "kebab", () -> PaintingMotive.KEBAB);
	public static final Registry<Identifier> CUSTOM_STAT = create("custom_stat", () -> Stats.JUMP);
	public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = create("chunk_status", "empty", () -> ChunkStatus.EMPTY);
	public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE = create("structure_feature", () -> StructureFeatures.MINESHAFT);
	public static final Registry<StructurePieceType> STRUCTURE_PIECE = create("structure_piece", () -> StructurePieceType.MSROOM);
	public static final Registry<RuleTest> RULE_TEST = create("rule_test", () -> RuleTest.ALWAYS_TRUE);
	public static final Registry<StructureProcessorType> STRUCTURE_PROCESSOR = create("structure_processor", () -> StructureProcessorType.BLOCK_IGNORE);
	public static final Registry<StructurePoolElementType> STRUCTURE_POOL_ELEMENT = create(
		"structure_pool_element", () -> StructurePoolElementType.EMPTY_POOL_ELEMENT
	);
	public static final Registry<ContainerType<?>> MENU = create("menu", () -> ContainerType.ANVIL);
	public static final Registry<RecipeType<?>> RECIPE_TYPE = create("recipe_type", () -> RecipeType.CRAFTING);
	public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = create("recipe_serializer", () -> RecipeSerializer.CRAFTING_SHAPELESS);
	public static final Registry<StatType<?>> STAT_TYPE = create("stat_type", () -> Stats.USED);
	public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = create("villager_type", "plains", () -> VillagerType.PLAINS);
	public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = create("villager_profession", "none", () -> VillagerProfession.NONE);
	public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE = create(
		"point_of_interest_type", "unemployed", () -> PointOfInterestType.UNEMPLOYED
	);
	public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = create("memory_module_type", "dummy", () -> MemoryModuleType.DUMMY);
	public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = create("sensor_type", "dummy", () -> SensorType.DUMMY);
	public static final Registry<Schedule> SCHEDULE = create("schedule", () -> Schedule.EMPTY);
	public static final Registry<Activity> ACTIVITY = create("activity", () -> Activity.IDLE);

	private static <T> Registry<T> create(String id, Supplier<T> supplier) {
		return putDefaultEntry(id, new SimpleRegistry<>(), supplier);
	}

	private static <T> DefaultedRegistry<T> create(String string, String string2, Supplier<T> defaultEntry) {
		return putDefaultEntry(string, new DefaultedRegistry<>(string2), defaultEntry);
	}

	private static <T, R extends MutableRegistry<T>> R putDefaultEntry(String id, R mutableRegistry, Supplier<T> supplier) {
		Identifier identifier = new Identifier(id);
		DEFAULT_ENTRIES.put(identifier, supplier);
		return REGISTRIES.add(identifier, mutableRegistry);
	}

	@Nullable
	public abstract Identifier getId(T entry);

	public abstract int getRawId(@Nullable T entry);

	@Nullable
	public abstract T get(@Nullable Identifier id);

	public abstract Optional<T> getOrEmpty(@Nullable Identifier id);

	public abstract Set<Identifier> getIds();

	@Nullable
	public abstract T getRandom(Random random);

	public Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	@Environment(EnvType.CLIENT)
	public abstract boolean containsId(Identifier id);

	public static <T> T register(Registry<? super T> registry, String id, T entry) {
		return register(registry, new Identifier(id), entry);
	}

	public static <T> T register(Registry<? super T> registry, Identifier id, T entry) {
		return ((MutableRegistry)registry).add(id, entry);
	}

	public static <T> T register(Registry<? super T> registry, int rawId, String id, T entry) {
		return ((MutableRegistry)registry).set(rawId, new Identifier(id), entry);
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
