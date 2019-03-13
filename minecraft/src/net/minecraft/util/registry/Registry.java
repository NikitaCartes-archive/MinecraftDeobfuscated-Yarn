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
import net.minecraft.class_4140;
import net.minecraft.class_4149;
import net.minecraft.class_4158;
import net.minecraft.class_4168;
import net.minecraft.class_4170;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.ContainerType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
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
import net.minecraft.sortme.rule.RuleTest;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.IndexedIterable;
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
import net.minecraft.world.gen.feature.StructureFeatures;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T> implements IndexedIterable<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.<Identifier, Supplier<?>>newLinkedHashMap();
	public static final MutableRegistry<MutableRegistry<?>> field_11144 = new SimpleRegistry<>();
	public static final Registry<SoundEvent> SOUND_EVENT = method_10224("sound_event", new SimpleRegistry<>(), () -> SoundEvents.field_15197);
	public static final DefaultedRegistry<Fluid> FLUID = method_10224("fluid", new DefaultedRegistry<>("empty"), () -> Fluids.EMPTY);
	public static final Registry<StatusEffect> STATUS_EFFECT = method_10224("mob_effect", new SimpleRegistry<>(), () -> StatusEffects.field_5926);
	public static final DefaultedRegistry<Block> BLOCK = method_10224("block", new DefaultedRegistry<>("air"), () -> Blocks.field_10124);
	public static final Registry<Enchantment> ENCHANTMENT = method_10224("enchantment", new SimpleRegistry<>(), () -> Enchantments.field_9130);
	public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = method_10224("entity_type", new DefaultedRegistry<>("pig"), () -> EntityType.PIG);
	public static final DefaultedRegistry<Item> ITEM = method_10224("item", new DefaultedRegistry<>("air"), () -> Items.AIR);
	public static final DefaultedRegistry<Potion> POTION = method_10224("potion", new DefaultedRegistry<>("empty"), () -> Potions.field_8984);
	public static final Registry<Carver<?>> CARVER = method_10224("carver", new SimpleRegistry<>(), () -> Carver.CAVE);
	public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = method_10224("surface_builder", new SimpleRegistry<>(), () -> SurfaceBuilder.DEFAULT);
	public static final Registry<Feature<?>> FEATURE = method_10224("feature", new SimpleRegistry<>(), () -> Feature.field_13517);
	public static final Registry<Decorator<?>> DECORATOR = method_10224("decorator", new SimpleRegistry<>(), () -> Decorator.NOPE);
	public static final Registry<Biome> BIOME = method_10224("biome", new SimpleRegistry<>(), () -> Biomes.DEFAULT);
	public static final Registry<ParticleType<? extends ParticleParameters>> PARTICLE_TYPE = method_10224(
		"particle_type", new SimpleRegistry<>(), () -> ParticleTypes.field_11217
	);
	public static final Registry<BiomeSourceType<?, ?>> BIOME_SOURCE_TYPE = method_10224(
		"biome_source_type", new SimpleRegistry<>(), () -> BiomeSourceType.VANILLA_LAYERED
	);
	public static final Registry<BlockEntityType<?>> BLOCK_ENTITY = method_10224("block_entity_type", new SimpleRegistry<>(), () -> BlockEntityType.FURNACE);
	public static final Registry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE = method_10224(
		"chunk_generator_type", new SimpleRegistry<>(), () -> ChunkGeneratorType.field_12766
	);
	public static final Registry<DimensionType> DIMENSION = method_10224("dimension_type", new SimpleRegistry<>(), () -> DimensionType.field_13072);
	public static final DefaultedRegistry<PaintingMotive> MOTIVE = method_10224("motive", new DefaultedRegistry<>("kebab"), () -> PaintingMotive.field_7146);
	public static final Registry<Identifier> CUSTOM_STAT = method_10224("custom_stat", new SimpleRegistry<>(), () -> Stats.field_15428);
	public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = method_10224("chunk_status", new DefaultedRegistry<>("empty"), () -> ChunkStatus.EMPTY);
	public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE = method_10224(
		"structure_feature", new SimpleRegistry<>(), () -> StructureFeatures.MINESHAFT
	);
	public static final Registry<StructurePieceType> STRUCTURE_PIECE = method_10224(
		"structure_piece", new SimpleRegistry<>(), () -> StructurePieceType.MINESHAFT_ROOM
	);
	public static final Registry<RuleTest> RULE_TEST = method_10224("rule_test", new SimpleRegistry<>(), () -> RuleTest.field_16982);
	public static final Registry<StructureProcessorType> STRUCTURE_PROCESSOR = method_10224(
		"structure_processor", new SimpleRegistry<>(), () -> StructureProcessorType.field_16986
	);
	public static final Registry<StructurePoolElementType> STRUCTURE_POOL_ELEMENT = method_10224(
		"structure_pool_element", new SimpleRegistry<>(), () -> StructurePoolElementType.EMPTY_POOL_ELEMENT
	);
	public static final Registry<ContainerType<?>> CONTAINER = method_10224("menu", new SimpleRegistry<>(), () -> ContainerType.ANVIL);
	public static final Registry<RecipeType<?>> RECIPE_TYPE = method_10224("recipe_type", new SimpleRegistry<>(), () -> RecipeType.CRAFTING);
	public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = method_10224(
		"recipe_serializer", new SimpleRegistry<>(), () -> RecipeSerializer.SHAPELESS
	);
	public static final Registry<StatType<?>> STAT_TYPE = method_10247("stat_type", new SimpleRegistry<>());
	public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = method_10224(
		"villager_type", new DefaultedRegistry<>("plains"), () -> VillagerType.field_17073
	);
	public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = method_10224(
		"villager_profession", new DefaultedRegistry<>("none"), () -> VillagerProfession.field_17051
	);
	public static final DefaultedRegistry<class_4158> field_18792 = method_10224(
		"point_of_interest_type", new DefaultedRegistry<>("unemployed"), () -> class_4158.field_18502
	);
	public static final DefaultedRegistry<class_4140<?>> field_18793 = method_10224(
		"memory_module_type", new DefaultedRegistry<>("dummy"), () -> class_4140.field_18437
	);
	public static final DefaultedRegistry<class_4149<?>> field_18794 = method_10224("sensor_type", new DefaultedRegistry<>("dummy"), () -> class_4149.field_18465);
	public static final SimpleRegistry<class_4170> field_18795 = method_10224("schedule", new SimpleRegistry<>(), () -> class_4170.field_18603);
	public static final SimpleRegistry<class_4168> field_18796 = method_10224("activity", new SimpleRegistry<>(), () -> class_4168.field_18595);

	private static <T> void putDefaultEntry(String string, Supplier<T> supplier) {
		DEFAULT_ENTRIES.put(new Identifier(string), supplier);
	}

	private static <T, R extends MutableRegistry<T>> R method_10224(String string, R mutableRegistry, Supplier<T> supplier) {
		putDefaultEntry(string, supplier);
		method_10247(string, mutableRegistry);
		return mutableRegistry;
	}

	private static <T> Registry<T> method_10247(String string, MutableRegistry<T> mutableRegistry) {
		field_11144.method_10272(new Identifier(string), mutableRegistry);
		return mutableRegistry;
	}

	@Nullable
	public abstract Identifier method_10221(T object);

	public abstract int getRawId(@Nullable T object);

	@Nullable
	public abstract T method_10223(@Nullable Identifier identifier);

	public abstract Optional<T> method_17966(@Nullable Identifier identifier);

	public abstract Set<Identifier> getIds();

	@Nullable
	public abstract T getRandom(Random random);

	public Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	@Environment(EnvType.CLIENT)
	public abstract boolean method_10250(Identifier identifier);

	public static <T> T register(Registry<? super T> registry, String string, T object) {
		return method_10230(registry, new Identifier(string), object);
	}

	public static <T> T method_10230(Registry<? super T> registry, Identifier identifier, T object) {
		return ((MutableRegistry)registry).method_10272(identifier, object);
	}

	public static <T> T register(Registry<? super T> registry, int i, String string, T object) {
		return ((MutableRegistry)registry).method_10273(i, new Identifier(string), object);
	}

	static {
		DEFAULT_ENTRIES.entrySet().forEach(entry -> {
			if (((Supplier)entry.getValue()).get() == null) {
				LOGGER.error("Unable to bootstrap registry '{}'", entry.getKey());
			}
		});
		field_11144.forEach(mutableRegistry -> {
			if (mutableRegistry.isEmpty()) {
				LOGGER.error("Registry '{}' was empty after loading", field_11144.method_10221(mutableRegistry));
				if (SharedConstants.isDevelopment) {
					throw new IllegalStateException("Registry: '" + field_11144.method_10221(mutableRegistry) + "' is empty, not allowed, fix me!");
				}
			}

			if (mutableRegistry instanceof DefaultedRegistry) {
				Identifier identifier = ((DefaultedRegistry)mutableRegistry).method_10137();
				Validate.notNull(mutableRegistry.method_10223(identifier), "Missing default of DefaultedMappedRegistry: " + identifier);
			}
		});
	}
}
