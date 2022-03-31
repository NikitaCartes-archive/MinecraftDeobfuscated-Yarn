package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public abstract class StructureFeature {
	public static final Codec<StructureFeature> FEATURE_CODEC = Registry.STRUCTURE_TYPE.getCodec().dispatch(StructureFeature::getType, StructureType::codec);
	public static final Codec<RegistryEntry<StructureFeature>> FEATURE_ENTRY_CODEC = RegistryElementCodec.of(
		Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, FEATURE_CODEC
	);
	protected final StructureFeature.Config config;

	public static <S extends StructureFeature> RecordCodecBuilder<S, StructureFeature.Config> configCodecBuilder(Instance<S> instance) {
		return StructureFeature.Config.CODEC.forGetter(feature -> feature.config);
	}

	public static <S extends StructureFeature> Codec<S> createCodec(Function<StructureFeature.Config, S> featureCreator) {
		return RecordCodecBuilder.create(instance -> instance.group(configCodecBuilder(instance)).apply(instance, featureCreator));
	}

	protected StructureFeature(StructureFeature.Config config) {
		this.config = config;
	}

	public RegistryEntryList<Biome> getValidBiomes() {
		return this.config.biomes;
	}

	public Map<SpawnGroup, StructureSpawns> getStructureSpawns() {
		return this.config.spawnOverrides;
	}

	public GenerationStep.Feature getFeatureGenerationStep() {
		return this.config.step;
	}

	public StructureTerrainAdaptation getTerrainAdaptation() {
		return this.config.terrainAdaptation;
	}

	public BlockBox expandBoxIfShouldAdaptNoise(BlockBox box) {
		return this.getTerrainAdaptation() != StructureTerrainAdaptation.NONE ? box.expand(12) : box;
	}

	public StructureStart createStructureStart(
		DynamicRegistryManager dynamicRegistryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		NoiseConfig noiseConfig,
		StructureManager structureManager,
		long seed,
		ChunkPos chunkPos,
		int references,
		HeightLimitView world,
		Predicate<RegistryEntry<Biome>> validBiomes
	) {
		Optional<StructureFeature.StructurePosition> optional = this.getStructurePosition(
			new StructureFeature.Context(dynamicRegistryManager, chunkGenerator, biomeSource, noiseConfig, structureManager, seed, chunkPos, world, validBiomes)
		);
		if (optional.isPresent() && isBiomeValid((StructureFeature.StructurePosition)optional.get(), chunkGenerator, noiseConfig, validBiomes)) {
			StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
			((StructureFeature.StructurePosition)optional.get()).generator().accept(structurePiecesCollector);
			StructureStart structureStart = new StructureStart(this, chunkPos, references, structurePiecesCollector.toList());
			if (structureStart.hasChildren()) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	protected static Optional<StructureFeature.StructurePosition> getStructurePosition(
		StructureFeature.Context context, Heightmap.Type heightmap, Consumer<StructurePiecesCollector> generator
	) {
		ChunkPos chunkPos = context.chunkPos();
		int i = chunkPos.getCenterX();
		int j = chunkPos.getCenterZ();
		int k = context.chunkGenerator().getHeightInGround(i, j, heightmap, context.world(), context.noiseConfig());
		return Optional.of(new StructureFeature.StructurePosition(new BlockPos(i, k, j), generator));
	}

	private static boolean isBiomeValid(
		StructureFeature.StructurePosition result, ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, Predicate<RegistryEntry<Biome>> validBiomes
	) {
		BlockPos blockPos = result.position();
		return validBiomes.test(
			chunkGenerator.getBiomeSource()
				.getBiome(
					BiomeCoords.fromBlock(blockPos.getX()), BiomeCoords.fromBlock(blockPos.getY()), BiomeCoords.fromBlock(blockPos.getZ()), noiseConfig.getMultiNoiseSampler()
				)
		);
	}

	public void postPlace(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox box,
		ChunkPos chunkPos,
		StructurePiecesList pieces
	) {
	}

	private static int[] getCornerHeights(StructureFeature.Context context, int x, int width, int z, int height) {
		ChunkGenerator chunkGenerator = context.chunkGenerator();
		HeightLimitView heightLimitView = context.world();
		NoiseConfig noiseConfig = context.noiseConfig();
		return new int[]{
			chunkGenerator.getHeightInGround(x, z, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig),
			chunkGenerator.getHeightInGround(x, z + height, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig),
			chunkGenerator.getHeightInGround(x + width, z, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig),
			chunkGenerator.getHeightInGround(x + width, z + height, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig)
		};
	}

	protected static int getMinCornerHeight(StructureFeature.Context context, int width, int height) {
		ChunkPos chunkPos = context.chunkPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		return getMinCornerHeight(context, i, j, width, height);
	}

	protected static int getMinCornerHeight(StructureFeature.Context context, int x, int z, int width, int height) {
		int[] is = getCornerHeights(context, x, width, z, height);
		return Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
	}

	@Deprecated
	protected BlockPos getShiftedPos(StructureFeature.Context context, BlockRotation rotation) {
		int i = 5;
		int j = 5;
		if (rotation == BlockRotation.CLOCKWISE_90) {
			i = -5;
		} else if (rotation == BlockRotation.CLOCKWISE_180) {
			i = -5;
			j = -5;
		} else if (rotation == BlockRotation.COUNTERCLOCKWISE_90) {
			j = -5;
		}

		ChunkPos chunkPos = context.chunkPos();
		int k = chunkPos.getOffsetX(7);
		int l = chunkPos.getOffsetZ(7);
		return new BlockPos(k, getMinCornerHeight(context, k, l, i, j), l);
	}

	public abstract Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context);

	public abstract StructureType<?> getType();

	public static record Config(
		RegistryEntryList<Biome> biomes, Map<SpawnGroup, StructureSpawns> spawnOverrides, GenerationStep.Feature step, StructureTerrainAdaptation terrainAdaptation
	) {
		public static final MapCodec<StructureFeature.Config> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						RegistryCodecs.entryList(Registry.BIOME_KEY).fieldOf("biomes").forGetter(StructureFeature.Config::biomes),
						Codec.simpleMap(SpawnGroup.CODEC, StructureSpawns.CODEC, StringIdentifiable.toKeyable(SpawnGroup.values()))
							.fieldOf("spawn_overrides")
							.forGetter(StructureFeature.Config::spawnOverrides),
						GenerationStep.Feature.CODEC.fieldOf("step").forGetter(StructureFeature.Config::step),
						StructureTerrainAdaptation.CODEC
							.optionalFieldOf("terrain_adaptation", StructureTerrainAdaptation.NONE)
							.forGetter(StructureFeature.Config::terrainAdaptation)
					)
					.apply(instance, StructureFeature.Config::new)
		);
	}

	public static record Context(
		DynamicRegistryManager dynamicRegistryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		NoiseConfig noiseConfig,
		StructureManager structureManager,
		ChunkRandom random,
		long seed,
		ChunkPos chunkPos,
		HeightLimitView world,
		Predicate<RegistryEntry<Biome>> biomePredicate
	) {
		public Context(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			BiomeSource biomeSource,
			NoiseConfig noiseConfig,
			StructureManager structureManager,
			long seed,
			ChunkPos chunkPos,
			HeightLimitView world,
			Predicate<RegistryEntry<Biome>> biomePredicate
		) {
			this(
				dynamicRegistryManager,
				chunkGenerator,
				biomeSource,
				noiseConfig,
				structureManager,
				createChunkRandom(seed, chunkPos),
				seed,
				chunkPos,
				world,
				biomePredicate
			);
		}

		private static ChunkRandom createChunkRandom(long seed, ChunkPos chunkPos) {
			ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
			chunkRandom.setCarverSeed(seed, chunkPos.x, chunkPos.z);
			return chunkRandom;
		}
	}

	public static record StructurePosition(BlockPos position, Consumer<StructurePiecesCollector> generator) {
	}
}
