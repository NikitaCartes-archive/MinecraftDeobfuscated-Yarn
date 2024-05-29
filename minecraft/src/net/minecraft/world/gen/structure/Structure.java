package net.minecraft.world.gen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
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

public abstract class Structure {
	public static final Codec<Structure> STRUCTURE_CODEC = Registries.STRUCTURE_TYPE.getCodec().dispatch(Structure::getType, StructureType::codec);
	public static final Codec<RegistryEntry<Structure>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.STRUCTURE, STRUCTURE_CODEC);
	protected final Structure.Config config;

	public static <S extends Structure> RecordCodecBuilder<S, Structure.Config> configCodecBuilder(Instance<S> instance) {
		return Structure.Config.CODEC.forGetter(feature -> feature.config);
	}

	public static <S extends Structure> MapCodec<S> createCodec(Function<Structure.Config, S> featureCreator) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(configCodecBuilder(instance)).apply(instance, featureCreator));
	}

	protected Structure(Structure.Config config) {
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
		StructureTemplateManager structureTemplateManager,
		long seed,
		ChunkPos chunkPos,
		int references,
		HeightLimitView world,
		Predicate<RegistryEntry<Biome>> validBiomes
	) {
		Structure.Context context = new Structure.Context(
			dynamicRegistryManager, chunkGenerator, biomeSource, noiseConfig, structureTemplateManager, seed, chunkPos, world, validBiomes
		);
		Optional<Structure.StructurePosition> optional = this.getValidStructurePosition(context);
		if (optional.isPresent()) {
			StructurePiecesCollector structurePiecesCollector = ((Structure.StructurePosition)optional.get()).generate();
			StructureStart structureStart = new StructureStart(this, chunkPos, references, structurePiecesCollector.toList());
			if (structureStart.hasChildren()) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	protected static Optional<Structure.StructurePosition> getStructurePosition(
		Structure.Context context, Heightmap.Type heightmap, Consumer<StructurePiecesCollector> generator
	) {
		ChunkPos chunkPos = context.chunkPos();
		int i = chunkPos.getCenterX();
		int j = chunkPos.getCenterZ();
		int k = context.chunkGenerator().getHeightInGround(i, j, heightmap, context.world(), context.noiseConfig());
		return Optional.of(new Structure.StructurePosition(new BlockPos(i, k, j), generator));
	}

	private static boolean isBiomeValid(Structure.StructurePosition result, Structure.Context context) {
		BlockPos blockPos = result.position();
		return context.biomePredicate
			.test(
				context.chunkGenerator
					.getBiomeSource()
					.getBiome(
						BiomeCoords.fromBlock(blockPos.getX()),
						BiomeCoords.fromBlock(blockPos.getY()),
						BiomeCoords.fromBlock(blockPos.getZ()),
						context.noiseConfig.getMultiNoiseSampler()
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

	private static int[] getCornerHeights(Structure.Context context, int x, int width, int z, int height) {
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

	public static int getAverageCornerHeights(Structure.Context context, int x, int width, int z, int height) {
		int[] is = getCornerHeights(context, x, width, z, height);
		return (is[0] + is[1] + is[2] + is[3]) / 4;
	}

	protected static int getMinCornerHeight(Structure.Context context, int width, int height) {
		ChunkPos chunkPos = context.chunkPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		return getMinCornerHeight(context, i, j, width, height);
	}

	protected static int getMinCornerHeight(Structure.Context context, int x, int z, int width, int height) {
		int[] is = getCornerHeights(context, x, width, z, height);
		return Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
	}

	@Deprecated
	protected BlockPos getShiftedPos(Structure.Context context, BlockRotation rotation) {
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

	protected abstract Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context);

	public Optional<Structure.StructurePosition> getValidStructurePosition(Structure.Context context) {
		return this.getStructurePosition(context).filter(position -> isBiomeValid(position, context));
	}

	public abstract StructureType<?> getType();

	public static record Config(
		RegistryEntryList<Biome> biomes, Map<SpawnGroup, StructureSpawns> spawnOverrides, GenerationStep.Feature step, StructureTerrainAdaptation terrainAdaptation
	) {
		static final Structure.Config DEFAULT = new Structure.Config(
			RegistryEntryList.of(), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.NONE
		);
		public static final MapCodec<Structure.Config> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						RegistryCodecs.entryList(RegistryKeys.BIOME).fieldOf("biomes").forGetter(Structure.Config::biomes),
						Codec.simpleMap(SpawnGroup.CODEC, StructureSpawns.CODEC, StringIdentifiable.toKeyable(SpawnGroup.values()))
							.fieldOf("spawn_overrides")
							.forGetter(Structure.Config::spawnOverrides),
						GenerationStep.Feature.CODEC.fieldOf("step").forGetter(Structure.Config::step),
						StructureTerrainAdaptation.CODEC.optionalFieldOf("terrain_adaptation", DEFAULT.terrainAdaptation).forGetter(Structure.Config::terrainAdaptation)
					)
					.apply(instance, Structure.Config::new)
		);

		public Config(RegistryEntryList<Biome> biomes) {
			this(biomes, DEFAULT.spawnOverrides, DEFAULT.step, DEFAULT.terrainAdaptation);
		}

		public static class Builder {
			private final RegistryEntryList<Biome> biomes;
			private Map<SpawnGroup, StructureSpawns> spawnOverrides;
			private GenerationStep.Feature step;
			private StructureTerrainAdaptation terrainAdaptation;

			public Builder(RegistryEntryList<Biome> biomes) {
				this.spawnOverrides = Structure.Config.DEFAULT.spawnOverrides;
				this.step = Structure.Config.DEFAULT.step;
				this.terrainAdaptation = Structure.Config.DEFAULT.terrainAdaptation;
				this.biomes = biomes;
			}

			public Structure.Config.Builder spawnOverrides(Map<SpawnGroup, StructureSpawns> spawnOverrides) {
				this.spawnOverrides = spawnOverrides;
				return this;
			}

			public Structure.Config.Builder step(GenerationStep.Feature step) {
				this.step = step;
				return this;
			}

			public Structure.Config.Builder terrainAdaptation(StructureTerrainAdaptation terrainAdaptation) {
				this.terrainAdaptation = terrainAdaptation;
				return this;
			}

			public Structure.Config build() {
				return new Structure.Config(this.biomes, this.spawnOverrides, this.step, this.terrainAdaptation);
			}
		}
	}

	public static record Context(
		DynamicRegistryManager dynamicRegistryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		NoiseConfig noiseConfig,
		StructureTemplateManager structureTemplateManager,
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
			StructureTemplateManager structureTemplateManager,
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
				structureTemplateManager,
				createChunkRandom(seed, chunkPos),
				seed,
				chunkPos,
				world,
				biomePredicate
			);
		}

		private static ChunkRandom createChunkRandom(long seed, ChunkPos chunkPos) {
			ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
			chunkRandom.setCarverSeed(seed, chunkPos.x, chunkPos.z);
			return chunkRandom;
		}
	}

	public static record StructurePosition(BlockPos position, Either<Consumer<StructurePiecesCollector>, StructurePiecesCollector> generator) {
		public StructurePosition(BlockPos pos, Consumer<StructurePiecesCollector> generator) {
			this(pos, Either.left(generator));
		}

		public StructurePiecesCollector generate() {
			return this.generator.map(generator -> {
				StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
				generator.accept(structurePiecesCollector);
				return structurePiecesCollector;
			}, collector -> collector);
		}
	}
}
