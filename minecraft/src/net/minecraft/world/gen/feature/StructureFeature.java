package net.minecraft.world.gen.feature;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_7061;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import org.slf4j.Logger;

public class StructureFeature<C extends FeatureConfig> {
	private static final Map<StructureFeature<?>, GenerationStep.Feature> STRUCTURE_TO_GENERATION_STEP = Maps.<StructureFeature<?>, GenerationStep.Feature>newHashMap();
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final StructureFeature<StructurePoolFeatureConfig> PILLAGER_OUTPOST = register(
		"pillager_outpost", new PillagerOutpostFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT = register(
		"mineshaft", new MineshaftFeature(MineshaftFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> MANSION = register(
		"mansion", new WoodlandMansionFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> JUNGLE_PYRAMID = register(
		"jungle_pyramid", new JungleTempleFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID = register(
		"desert_pyramid", new DesertPyramidFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> IGLOO = register(
		"igloo", new IglooFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<RuinedPortalFeatureConfig> RUINED_PORTAL = register(
		"ruined_portal", new RuinedPortalFeature(RuinedPortalFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK = register(
		"shipwreck", new ShipwreckFeature(ShipwreckFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> SWAMP_HUT = register(
		"swamp_hut", new SwampHutFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD = register(
		"stronghold", new StrongholdFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.STRONGHOLDS
	);
	public static final StructureFeature<DefaultFeatureConfig> MONUMENT = register(
		"monument", new OceanMonumentFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN = register(
		"ocean_ruin", new OceanRuinFeature(OceanRuinFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> FORTRESS = register(
		"fortress", new NetherFortressFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION
	);
	public static final StructureFeature<DefaultFeatureConfig> ENDCITY = register(
		"endcity", new EndCityFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<ProbabilityConfig> BURIED_TREASURE = register(
		"buried_treasure", new BuriedTreasureFeature(ProbabilityConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES
	);
	public static final StructureFeature<StructurePoolFeatureConfig> VILLAGE = register(
		"village", new VillageFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<RangeFeatureConfig> NETHER_FOSSIL = register(
		"nether_fossil", new NetherFossilFeature(RangeFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION
	);
	public static final StructureFeature<StructurePoolFeatureConfig> BASTION_REMNANT = register(
		"bastion_remnant", new BastionRemnantFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final int field_31518 = 8;
	private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> codec;
	private final StructureGeneratorFactory<C> piecesGenerator;
	private final PostPlacementProcessor postProcessor;

	private static <F extends StructureFeature<?>> F register(String name, F structureFeature, GenerationStep.Feature step) {
		STRUCTURE_TO_GENERATION_STEP.put(structureFeature, step);
		return Registry.register(Registry.STRUCTURE_FEATURE, name, structureFeature);
	}

	public StructureFeature(Codec<C> configCodec, StructureGeneratorFactory<C> piecesGenerator) {
		this(configCodec, piecesGenerator, PostPlacementProcessor.EMPTY);
	}

	public StructureFeature(Codec<C> configCodec, StructureGeneratorFactory<C> piecesGenerator, PostPlacementProcessor postPlacementProcessor) {
		this.codec = RecordCodecBuilder.create(
			instance -> instance.group(
						configCodec.fieldOf("config").forGetter(configuredStructureFeature -> configuredStructureFeature.config),
						RegistryCodecs.entryList(Registry.BIOME_KEY).fieldOf("biomes").forGetter(ConfiguredStructureFeature::getBiomes),
						Codec.BOOL.optionalFieldOf("adapt_noise", Boolean.valueOf(false)).forGetter(configuredStructureFeature -> configuredStructureFeature.field_37144),
						Codec.simpleMap(SpawnGroup.CODEC, class_7061.field_37198, StringIdentifiable.toKeyable(SpawnGroup.values()))
							.fieldOf("spawn_overrides")
							.forGetter(configuredStructureFeature -> configuredStructureFeature.field_37143)
					)
					.apply(
						instance, (featureConfig, registryEntryList, boolean_, map) -> new ConfiguredStructureFeature<>(this, (C)featureConfig, registryEntryList, boolean_, map)
					)
		);
		this.piecesGenerator = piecesGenerator;
		this.postProcessor = postPlacementProcessor;
	}

	/**
	 * Gets the step during which this structure will participate in world generation.
	 * Structures will generate before other features in the same generation step.
	 */
	public GenerationStep.Feature getGenerationStep() {
		return (GenerationStep.Feature)STRUCTURE_TO_GENERATION_STEP.get(this);
	}

	public static void init() {
	}

	@Nullable
	public static StructureStart readStructureStart(StructureContext context, NbtCompound nbt, long worldSeed) {
		String string = nbt.getString("id");
		if ("INVALID".equals(string)) {
			return StructureStart.DEFAULT;
		} else {
			Registry<ConfiguredStructureFeature<?, ?>> registry = context.registryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
			ConfiguredStructureFeature<?, ?> configuredStructureFeature = registry.get(new Identifier(string));
			if (configuredStructureFeature == null) {
				LOGGER.error("Unknown feature id: {}", string);
				return null;
			} else {
				ChunkPos chunkPos = new ChunkPos(nbt.getInt("ChunkX"), nbt.getInt("ChunkZ"));
				int i = nbt.getInt("references");
				NbtList nbtList = nbt.getList("Children", NbtElement.COMPOUND_TYPE);

				try {
					StructurePiecesList structurePiecesList = StructurePiecesList.fromNbt(nbtList, context);
					if (configuredStructureFeature.feature == MONUMENT) {
						structurePiecesList = OceanMonumentFeature.modifyPiecesOnRead(chunkPos, worldSeed, structurePiecesList);
					}

					return new StructureStart(configuredStructureFeature, chunkPos, i, structurePiecesList);
				} catch (Exception var11) {
					LOGGER.error("Failed Start with id {}", string, var11);
					return null;
				}
			}
		}
	}

	public Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> getCodec() {
		return this.codec;
	}

	public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configure(C config, TagKey<Biome> biomeTag) {
		return this.method_41134(config, biomeTag, false);
	}

	public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> method_41134(C featureConfig, TagKey<Biome> tagKey, boolean bl) {
		return new ConfiguredStructureFeature<>(this, featureConfig, BuiltinRegistries.BIOME.getOrCreateEntryList(tagKey), bl, Map.of());
	}

	public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> method_41133(C featureConfig, TagKey<Biome> tagKey, Map<SpawnGroup, class_7061> map) {
		return new ConfiguredStructureFeature<>(this, featureConfig, BuiltinRegistries.BIOME.getOrCreateEntryList(tagKey), false, map);
	}

	public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> method_41135(
		C featureConfig, TagKey<Biome> tagKey, boolean bl, Map<SpawnGroup, class_7061> map
	) {
		return new ConfiguredStructureFeature<>(this, featureConfig, BuiltinRegistries.BIOME.getOrCreateEntryList(tagKey), bl, map);
	}

	/**
	 * {@return a block position for feature location}
	 */
	public static BlockPos getLocatedPos(RandomSpreadStructurePlacement placement, ChunkPos chunkPos) {
		return new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ()).add(placement.locateOffset());
	}

	public boolean canGenerate(
		DynamicRegistryManager registryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		StructureManager structureManager,
		long worldSeed,
		ChunkPos pos,
		C config,
		HeightLimitView world,
		Predicate<RegistryEntry<Biome>> biomePredicate
	) {
		return this.piecesGenerator
			.createGenerator(
				new StructureGeneratorFactory.Context<>(chunkGenerator, biomeSource, worldSeed, pos, config, world, biomePredicate, structureManager, registryManager)
			)
			.isPresent();
	}

	public StructureGeneratorFactory<C> method_41138() {
		return this.piecesGenerator;
	}

	public PostPlacementProcessor getPostProcessor() {
		return this.postProcessor;
	}
}
