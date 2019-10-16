/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.SimpleRegistry;
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
import org.jetbrains.annotations.Nullable;

public abstract class Registry<T>
implements IndexedIterable<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.newLinkedHashMap();
    public static final MutableRegistry<MutableRegistry<?>> REGISTRIES = new SimpleRegistry();
    public static final Registry<SoundEvent> SOUND_EVENT = Registry.create("sound_event", () -> SoundEvents.ENTITY_ITEM_PICKUP);
    public static final DefaultedRegistry<Fluid> FLUID = Registry.create("fluid", "empty", () -> Fluids.EMPTY);
    public static final Registry<StatusEffect> STATUS_EFFECT = Registry.create("mob_effect", () -> StatusEffects.LUCK);
    public static final DefaultedRegistry<Block> BLOCK = Registry.create("block", "air", () -> Blocks.AIR);
    public static final Registry<Enchantment> ENCHANTMENT = Registry.create("enchantment", () -> Enchantments.FORTUNE);
    public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = Registry.create("entity_type", "pig", () -> EntityType.PIG);
    public static final DefaultedRegistry<Item> ITEM = Registry.create("item", "air", () -> Items.AIR);
    public static final DefaultedRegistry<Potion> POTION = Registry.create("potion", "empty", () -> Potions.EMPTY);
    public static final Registry<Carver<?>> CARVER = Registry.create("carver", () -> Carver.CAVE);
    public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = Registry.create("surface_builder", () -> SurfaceBuilder.DEFAULT);
    public static final Registry<Feature<?>> FEATURE = Registry.create("feature", () -> Feature.ORE);
    public static final Registry<Decorator<?>> DECORATOR = Registry.create("decorator", () -> Decorator.NOPE);
    public static final Registry<Biome> BIOME = Registry.create("biome", () -> Biomes.DEFAULT);
    public static final Registry<StateProviderType<?>> BLOCK_STATE_PROVIDER_TYPE = Registry.create("block_state_provider_type", () -> StateProviderType.SIMPLE_STATE_PROVIDER);
    public static final Registry<BlockPlacerType<?>> BLOCK_PLACER_TYPE = Registry.create("block_placer_type", () -> BlockPlacerType.SIMPLE_BLOCK_PLACER);
    public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = Registry.create("foliage_placer_type", () -> FoliagePlacerType.BLOB_FOLIAGE_PLACER);
    public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = Registry.create("tree_decorator_type", () -> TreeDecoratorType.LEAVE_VINE);
    public static final Registry<ParticleType<? extends ParticleEffect>> PARTICLE_TYPE = Registry.create("particle_type", () -> ParticleTypes.BLOCK);
    public static final Registry<BiomeSourceType<?, ?>> BIOME_SOURCE_TYPE = Registry.create("biome_source_type", () -> BiomeSourceType.VANILLA_LAYERED);
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY = Registry.create("block_entity_type", () -> BlockEntityType.FURNACE);
    public static final Registry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE = Registry.create("chunk_generator_type", () -> ChunkGeneratorType.FLAT);
    public static final Registry<DimensionType> DIMENSION = Registry.create("dimension_type", () -> DimensionType.OVERWORLD);
    public static final DefaultedRegistry<PaintingMotive> MOTIVE = Registry.create("motive", "kebab", () -> PaintingMotive.KEBAB);
    public static final Registry<Identifier> CUSTOM_STAT = Registry.create("custom_stat", () -> Stats.JUMP);
    public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = Registry.create("chunk_status", "empty", () -> ChunkStatus.EMPTY);
    public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE = Registry.create("structure_feature", () -> StructureFeatures.MINESHAFT);
    public static final Registry<StructurePieceType> STRUCTURE_PIECE = Registry.create("structure_piece", () -> StructurePieceType.MINESHAFT_ROOM);
    public static final Registry<RuleTest> RULE_TEST = Registry.create("rule_test", () -> RuleTest.ALWAYS_TRUE);
    public static final Registry<StructureProcessorType> STRUCTURE_PROCESSOR = Registry.create("structure_processor", () -> StructureProcessorType.BLOCK_IGNORE);
    public static final Registry<StructurePoolElementType> STRUCTURE_POOL_ELEMENT = Registry.create("structure_pool_element", () -> StructurePoolElementType.EMPTY_POOL_ELEMENT);
    public static final Registry<ContainerType<?>> CONTAINER = Registry.create("menu", () -> ContainerType.ANVIL);
    public static final Registry<RecipeType<?>> RECIPE_TYPE = Registry.create("recipe_type", () -> RecipeType.CRAFTING);
    public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = Registry.create("recipe_serializer", () -> RecipeSerializer.SHAPELESS);
    public static final Registry<StatType<?>> STAT_TYPE = Registry.create("stat_type", () -> Stats.USED);
    public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = Registry.create("villager_type", "plains", () -> VillagerType.PLAINS);
    public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = Registry.create("villager_profession", "none", () -> VillagerProfession.NONE);
    public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE = Registry.create("point_of_interest_type", "unemployed", () -> PointOfInterestType.UNEMPLOYED);
    public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = Registry.create("memory_module_type", "dummy", () -> MemoryModuleType.DUMMY);
    public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = Registry.create("sensor_type", "dummy", () -> SensorType.DUMMY);
    public static final Registry<Schedule> SCHEDULE = Registry.create("schedule", () -> Schedule.EMPTY);
    public static final Registry<Activity> ACTIVITY = Registry.create("activity", () -> Activity.IDLE);

    private static <T> Registry<T> create(String string, Supplier<T> supplier) {
        return Registry.putDefaultEntry(string, new SimpleRegistry(), supplier);
    }

    private static <T> DefaultedRegistry<T> create(String string, String string2, Supplier<T> supplier) {
        return Registry.putDefaultEntry(string, new DefaultedRegistry(string2), supplier);
    }

    private static <T, R extends MutableRegistry<T>> R putDefaultEntry(String string, R mutableRegistry, Supplier<T> supplier) {
        Identifier identifier = new Identifier(string);
        DEFAULT_ENTRIES.put(identifier, supplier);
        return REGISTRIES.add(identifier, mutableRegistry);
    }

    @Nullable
    public abstract Identifier getId(T var1);

    public abstract int getRawId(@Nullable T var1);

    @Nullable
    public abstract T get(@Nullable Identifier var1);

    public abstract Optional<T> getOrEmpty(@Nullable Identifier var1);

    public abstract Set<Identifier> getIds();

    @Nullable
    public abstract T getRandom(Random var1);

    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    @Environment(value=EnvType.CLIENT)
    public abstract boolean containsId(Identifier var1);

    public static <T> T register(Registry<? super T> registry, String string, T object) {
        return Registry.register(registry, new Identifier(string), object);
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
                LOGGER.error("Registry '{}' was empty after loading", (Object)REGISTRIES.getId((MutableRegistry<?>)mutableRegistry));
                if (SharedConstants.isDevelopment) {
                    throw new IllegalStateException("Registry: '" + REGISTRIES.getId((MutableRegistry<?>)mutableRegistry) + "' is empty, not allowed, fix me!");
                }
            }
            if (mutableRegistry instanceof DefaultedRegistry) {
                Identifier identifier = ((DefaultedRegistry)mutableRegistry).getDefaultId();
                Validate.notNull(mutableRegistry.get(identifier), "Missing default of DefaultedMappedRegistry: " + identifier, new Object[0]);
            }
        });
    }
}

