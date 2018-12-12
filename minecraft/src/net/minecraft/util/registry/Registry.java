package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
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
import net.minecraft.sortme.StructurePiece;
import net.minecraft.sortme.StructurePoolElement;
import net.minecraft.sortme.rule.RuleTest;
import net.minecraft.sortme.structures.processor.StructureProcessor;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
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
	public static final Registry<SoundEvent> SOUND_EVENT = method_10224("sound_event", new IdRegistry<>(), () -> SoundEvents.field_15197);
	public static final DefaultMappedRegistry<Fluid> FLUID = method_10224("fluid", new DefaultMappedRegistry<>("empty"), () -> Fluids.EMPTY);
	public static final Registry<StatusEffect> STATUS_EFFECT = method_10224("mob_effect", new IdRegistry<>(), () -> StatusEffects.field_5926);
	public static final DefaultMappedRegistry<Block> BLOCK = method_10224("block", new DefaultMappedRegistry<>("air"), () -> Blocks.field_10124);
	public static final Registry<Enchantment> ENCHANTMENT = method_10224("enchantment", new IdRegistry<>(), () -> Enchantments.field_9130);
	public static final Registry<EntityType<?>> ENTITY_TYPE = method_10224("entity_type", new IdRegistry<>(), () -> EntityType.CREEPER);
	public static final DefaultMappedRegistry<Item> ITEM = method_10224("item", new DefaultMappedRegistry<>("air"), () -> Items.AIR);
	public static final Registry<Potion> POTION = method_10224("potion", new DefaultMappedRegistry<>("empty"), () -> Potions.field_8984);
	public static final Registry<Carver<?>> CARVER = method_10224("carver", new IdRegistry<>(), () -> Carver.CAVE);
	public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = method_10224("surface_builder", new IdRegistry<>(), () -> SurfaceBuilder.DEFAULT);
	public static final Registry<Feature<?>> FEATURE = method_10224("feature", new IdRegistry<>(), () -> Feature.field_13517);
	public static final Registry<Decorator<?>> DECORATOR = method_10224("decorator", new IdRegistry<>(), () -> Decorator.NOPE);
	public static final Registry<Biome> BIOME = method_10224("biome", new IdRegistry<>(), () -> Biomes.DEFAULT);
	public static final Registry<ParticleType<? extends ParticleParameters>> PARTICLE_TYPE = method_10224(
		"particle_type", new IdRegistry<>(), () -> ParticleTypes.field_11217
	);
	public static final Registry<BiomeSourceType<?, ?>> BIOME_SOURCE_TYPE = method_10224(
		"biome_source_type", new IdRegistry<>(), () -> BiomeSourceType.VANILLA_LAYERED
	);
	public static final Registry<BlockEntityType<?>> BLOCK_ENTITY = method_10224("block_entity_type", new IdRegistry<>(), () -> BlockEntityType.FURNACE);
	public static final Registry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE = method_10224(
		"chunk_generator_type", new IdRegistry<>(), () -> ChunkGeneratorType.field_12766
	);
	public static final Registry<DimensionType> DIMENSION = method_10224("dimension_type", new IdRegistry<>(), () -> DimensionType.field_13072);
	public static final DefaultMappedRegistry<PaintingMotive> MOTIVE = method_10224(
		"motive", new DefaultMappedRegistry<>("kebab"), () -> PaintingMotive.field_7146
	);
	public static final Registry<Identifier> CUSTOM_STAT = method_10224("custom_stat", new IdRegistry<>(), () -> Stats.field_15428);
	public static final DefaultMappedRegistry<ChunkStatus> CHUNK_STATUS = method_10224(
		"chunk_status", new DefaultMappedRegistry<>("empty"), () -> ChunkStatus.EMPTY
	);
	public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE = method_10224(
		"structure_feature", new IdRegistry<>(), () -> StructureFeatures.field_16709
	);
	public static final Registry<StructurePiece> STRUCTURE_PIECE = method_10224("structure_piece", new IdRegistry<>(), () -> StructurePiece.field_16915);
	public static final Registry<RuleTest> RULE_TEST = method_10224("rule_test", new IdRegistry<>(), () -> RuleTest.field_16982);
	public static final Registry<StructureProcessor> STRUCTURE_PROCESSOR = method_10224(
		"structure_processor", new IdRegistry<>(), () -> StructureProcessor.field_16986
	);
	public static final Registry<StructurePoolElement> STRUCTURE_POOL_ELEMENT = method_10224(
		"structure_pool_element", new IdRegistry<>(), () -> StructurePoolElement.field_16972
	);
	public static final Registry<StatType<?>> STAT_TYPE = registerRegistry("stat_type", new IdRegistry<>());
	public static final DefaultMappedRegistry<VillagerType> VILLAGER_TYPE = method_10224(
		"villager_type", new DefaultMappedRegistry<>("plains"), () -> VillagerType.field_17073
	);
	public static final DefaultMappedRegistry<VillagerProfession> VILLAGER_PROFESSION = method_10224(
		"villager_profession", new DefaultMappedRegistry<>("none"), () -> VillagerProfession.field_17051
	);

	private static <T> void method_10227(String string, Supplier<T> supplier) {
		field_11140.put(new Identifier(string), supplier);
	}

	private static <T, R extends ModifiableRegistry<T>> R method_10224(String string, R modifiableRegistry, Supplier<T> supplier) {
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

	public abstract Set<Identifier> keys();

	@Nullable
	public abstract T getRandom(Random random);

	public Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

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
