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
import net.minecraft.util.IntIterable;
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

public abstract class Registry<T> implements IntIterable<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Identifier, Supplier<?>> field_11140 = Maps.<Identifier, Supplier<?>>newLinkedHashMap();
	public static final ModifiableRegistry<ModifiableRegistry<?>> REGISTRIES = new IdRegistry<>();
	public static final Registry<SoundEvent> SOUND_EVENT = registerRegistry("sound_event", new IdRegistry<>(), () -> SoundEvents.field_15197);
	public static final DefaultMappedRegistry<Fluid> FLUID = registerRegistry("fluid", new DefaultMappedRegistry<>("empty"), () -> Fluids.EMPTY);
	public static final Registry<StatusEffect> STATUS_EFFECT = registerRegistry("mob_effect", new IdRegistry<>(), () -> StatusEffects.field_5926);
	public static final DefaultMappedRegistry<Block> BLOCK = registerRegistry("block", new DefaultMappedRegistry<>("air"), () -> Blocks.field_10124);
	public static final Registry<Enchantment> ENCHANTMENT = registerRegistry("enchantment", new IdRegistry<>(), () -> Enchantments.field_9130);
	public static final DefaultMappedRegistry<EntityType<?>> ENTITY_TYPE = registerRegistry(
		"entity_type", new DefaultMappedRegistry<>("pig"), () -> EntityType.PIG
	);
	public static final DefaultMappedRegistry<Item> ITEM = registerRegistry("item", new DefaultMappedRegistry<>("air"), () -> Items.AIR);
	public static final DefaultMappedRegistry<Potion> POTION = registerRegistry("potion", new DefaultMappedRegistry<>("empty"), () -> Potions.field_8984);
	public static final Registry<Carver<?>> CARVER = registerRegistry("carver", new IdRegistry<>(), () -> Carver.CAVE);
	public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = registerRegistry("surface_builder", new IdRegistry<>(), () -> SurfaceBuilder.DEFAULT);
	public static final Registry<Feature<?>> FEATURE = registerRegistry("feature", new IdRegistry<>(), () -> Feature.field_13517);
	public static final Registry<Decorator<?>> DECORATOR = registerRegistry("decorator", new IdRegistry<>(), () -> Decorator.NOPE);
	public static final Registry<Biome> BIOME = registerRegistry("biome", new IdRegistry<>(), () -> Biomes.DEFAULT);
	public static final Registry<ParticleType<? extends ParticleParameters>> PARTICLE_TYPE = registerRegistry(
		"particle_type", new IdRegistry<>(), () -> ParticleTypes.field_11217
	);
	public static final Registry<BiomeSourceType<?, ?>> BIOME_SOURCE_TYPE = registerRegistry(
		"biome_source_type", new IdRegistry<>(), () -> BiomeSourceType.VANILLA_LAYERED
	);
	public static final Registry<BlockEntityType<?>> BLOCK_ENTITY = registerRegistry("block_entity_type", new IdRegistry<>(), () -> BlockEntityType.FURNACE);
	public static final Registry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE = registerRegistry(
		"chunk_generator_type", new IdRegistry<>(), () -> ChunkGeneratorType.field_12766
	);
	public static final Registry<DimensionType> DIMENSION = registerRegistry("dimension_type", new IdRegistry<>(), () -> DimensionType.field_13072);
	public static final DefaultMappedRegistry<PaintingMotive> MOTIVE = registerRegistry(
		"motive", new DefaultMappedRegistry<>("kebab"), () -> PaintingMotive.field_7146
	);
	public static final Registry<Identifier> CUSTOM_STAT = registerRegistry("custom_stat", new IdRegistry<>(), () -> Stats.field_15428);
	public static final DefaultMappedRegistry<ChunkStatus> CHUNK_STATUS = registerRegistry(
		"chunk_status", new DefaultMappedRegistry<>("empty"), () -> ChunkStatus.EMPTY
	);
	public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE = registerRegistry(
		"structure_feature", new IdRegistry<>(), () -> StructureFeatures.MINESHAFT
	);
	public static final Registry<StructurePieceType> STRUCTURE_PIECE = registerRegistry(
		"structure_piece", new IdRegistry<>(), () -> StructurePieceType.MINESHAFT_ROOM
	);
	public static final Registry<RuleTest> RULE_TEST = registerRegistry("rule_test", new IdRegistry<>(), () -> RuleTest.field_16982);
	public static final Registry<StructureProcessorType> STRUCTURE_PROCESSOR = registerRegistry(
		"structure_processor", new IdRegistry<>(), () -> StructureProcessorType.field_16986
	);
	public static final Registry<StructurePoolElementType> STRUCTURE_POOL_ELEMENT = registerRegistry(
		"structure_pool_element", new IdRegistry<>(), () -> StructurePoolElementType.EMPTY_POOL_ELEMENT
	);
	public static final Registry<ContainerType<?>> CONTAINER = registerRegistry("menu", new IdRegistry<>(), () -> ContainerType.ANVIL);
	public static final Registry<RecipeType<?>> RECIPE_TYPE = registerRegistry("recipe_type", new IdRegistry<>(), () -> RecipeType.CRAFTING);
	public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = registerRegistry(
		"recipe_serializer", new IdRegistry<>(), () -> RecipeSerializer.SHAPELESS
	);
	public static final Registry<StatType<?>> STAT_TYPE = registerRegistry("stat_type", new IdRegistry<>());
	public static final DefaultMappedRegistry<VillagerType> VILLAGER_TYPE = registerRegistry(
		"villager_type", new DefaultMappedRegistry<>("plains"), () -> VillagerType.field_17073
	);
	public static final DefaultMappedRegistry<VillagerProfession> VILLAGER_PROFESSION = registerRegistry(
		"villager_profession", new DefaultMappedRegistry<>("none"), () -> VillagerProfession.field_17051
	);

	private static <T> void method_10227(String string, Supplier<T> supplier) {
		field_11140.put(new Identifier(string), supplier);
	}

	private static <T, R extends ModifiableRegistry<T>> R registerRegistry(String string, R modifiableRegistry, Supplier<T> supplier) {
		method_10227(string, supplier);
		registerRegistry(string, modifiableRegistry);
		return modifiableRegistry;
	}

	private static <T> Registry<T> registerRegistry(String string, ModifiableRegistry<T> modifiableRegistry) {
		REGISTRIES.register(new Identifier(string), modifiableRegistry);
		return modifiableRegistry;
	}

	@Nullable
	public abstract Identifier getId(T object);

	public abstract int getRawId(@Nullable T object);

	@Nullable
	public abstract T get(@Nullable Identifier identifier);

	public abstract Optional<T> method_17966(@Nullable Identifier identifier);

	public abstract Set<Identifier> keys();

	@Nullable
	public abstract T getRandom(Random random);

	public Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	@Environment(EnvType.CLIENT)
	public abstract boolean contains(Identifier identifier);

	public static <T> T register(Registry<? super T> registry, String string, T object) {
		return register(registry, new Identifier(string), object);
	}

	public static <T> T register(Registry<? super T> registry, Identifier identifier, T object) {
		return ((ModifiableRegistry)registry).register(identifier, object);
	}

	public static <T> T set(Registry<? super T> registry, int i, String string, T object) {
		return ((ModifiableRegistry)registry).set(i, new Identifier(string), object);
	}

	static {
		field_11140.entrySet().forEach(entry -> {
			if (((Supplier)entry.getValue()).get() == null) {
				LOGGER.error("Unable to bootstrap registry '{}'", entry.getKey());
			}
		});
		REGISTRIES.forEach(modifiableRegistry -> {
			if (modifiableRegistry.isEmpty()) {
				LOGGER.error("Registry '{}' was empty after loading", REGISTRIES.getId(modifiableRegistry));
				if (SharedConstants.isDevelopment) {
					throw new IllegalStateException("Registry: '" + REGISTRIES.getId(modifiableRegistry) + "' is empty, not allowed, fix me!");
				}
			}

			if (modifiableRegistry instanceof DefaultMappedRegistry) {
				Identifier identifier = ((DefaultMappedRegistry)modifiableRegistry).getDefaultId();
				Validate.notNull(modifiableRegistry.get(identifier), "Missing default of DefaultedMappedRegistry: " + identifier);
			}
		});
	}
}
