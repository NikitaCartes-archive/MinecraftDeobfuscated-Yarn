package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Products.P4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
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
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public abstract class StructureFeature {
	public static final Codec<StructureFeature> field_37744 = Registry.STRUCTURE_TYPE.getCodec().dispatch(StructureFeature::getType, StructureType::codec);
	public static final Codec<RegistryEntry<StructureFeature>> field_37745 = RegistryElementCodec.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, field_37744);
	private final RegistryEntryList<Biome> validBiomes;
	private final Map<SpawnGroup, StructureSpawns> structureSpawns;
	private final GenerationStep.Feature featureGenerationStep;
	private final boolean adaptNoise;

	public static <S extends StructureFeature> P4<Mu<S>, RegistryEntryList<Biome>, Map<SpawnGroup, StructureSpawns>, GenerationStep.Feature, Boolean> method_41608(
		Instance<S> instance
	) {
		return instance.group(
			RegistryCodecs.entryList(Registry.BIOME_KEY).fieldOf("biomes").forGetter(StructureFeature::getValidBiomes),
			Codec.simpleMap(SpawnGroup.CODEC, StructureSpawns.CODEC, StringIdentifiable.toKeyable(SpawnGroup.values()))
				.fieldOf("spawn_overrides")
				.forGetter(StructureFeature::getStructureSpawns),
			GenerationStep.Feature.CODEC.fieldOf("step").forGetter(StructureFeature::getFeatureGenerationStep),
			Codec.BOOL.optionalFieldOf("adapt_noise", Boolean.valueOf(false)).forGetter(StructureFeature::shouldAdaptNoise)
		);
	}

	protected StructureFeature(
		RegistryEntryList<Biome> validBiomes, Map<SpawnGroup, StructureSpawns> structureSpawns, GenerationStep.Feature featureGenerationStep, boolean adaptNoise
	) {
		this.validBiomes = validBiomes;
		this.structureSpawns = structureSpawns;
		this.featureGenerationStep = featureGenerationStep;
		this.adaptNoise = adaptNoise;
	}

	public RegistryEntryList<Biome> getValidBiomes() {
		return this.validBiomes;
	}

	public Map<SpawnGroup, StructureSpawns> getStructureSpawns() {
		return this.structureSpawns;
	}

	public GenerationStep.Feature getFeatureGenerationStep() {
		return this.featureGenerationStep;
	}

	public boolean shouldAdaptNoise() {
		return this.adaptNoise;
	}

	public BlockBox expandBoxIfShouldAdaptNoise(BlockBox box) {
		return this.shouldAdaptNoise() ? box.expand(12) : box;
	}

	public StructureStart method_41614(
		DynamicRegistryManager dynamicRegistryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		NoiseConfig noiseConfig,
		StructureManager structureManager,
		long l,
		ChunkPos chunkPos,
		int i,
		HeightLimitView heightLimitView,
		Predicate<RegistryEntry<Biome>> predicate
	) {
		Optional<StructureFeature.class_7150> optional = this.method_38676(
			new StructureFeature.class_7149(dynamicRegistryManager, chunkGenerator, biomeSource, noiseConfig, structureManager, l, chunkPos, heightLimitView, predicate)
		);
		if (optional.isPresent() && method_41613((StructureFeature.class_7150)optional.get(), chunkGenerator, noiseConfig, predicate)) {
			StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
			((StructureFeature.class_7150)optional.get()).generator().accept(structurePiecesCollector);
			StructureStart structureStart = new StructureStart(this, chunkPos, i, structurePiecesCollector.toList());
			if (structureStart.hasChildren()) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	protected static Optional<StructureFeature.class_7150> method_41612(
		StructureFeature.class_7149 arg, Heightmap.Type type, Consumer<StructurePiecesCollector> consumer
	) {
		ChunkPos chunkPos = arg.chunkPos();
		int i = chunkPos.getCenterX();
		int j = chunkPos.getCenterZ();
		int k = arg.chunkGenerator().getHeightInGround(i, j, type, arg.heightAccessor(), arg.randomState());
		return Optional.of(new StructureFeature.class_7150(new BlockPos(i, k, j), consumer));
	}

	private static boolean method_41613(
		StructureFeature.class_7150 arg, ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, Predicate<RegistryEntry<Biome>> predicate
	) {
		BlockPos blockPos = arg.position();
		return predicate.test(
			chunkGenerator.getBiomeSource()
				.getBiome(
					BiomeCoords.fromBlock(blockPos.getX()), BiomeCoords.fromBlock(blockPos.getY()), BiomeCoords.fromBlock(blockPos.getZ()), noiseConfig.getMultiNoiseSampler()
				)
		);
	}

	public void postPlace(
		StructureWorldAccess structureWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox blockBox,
		ChunkPos chunkPos,
		StructurePiecesList structurePiecesList
	) {
	}

	private static int[] method_41611(StructureFeature.class_7149 arg, int i, int j, int k, int l) {
		ChunkGenerator chunkGenerator = arg.chunkGenerator();
		HeightLimitView heightLimitView = arg.heightAccessor();
		NoiseConfig noiseConfig = arg.randomState();
		return new int[]{
			chunkGenerator.getHeightInGround(i, k, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig),
			chunkGenerator.getHeightInGround(i, k + l, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig),
			chunkGenerator.getHeightInGround(i + j, k, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig),
			chunkGenerator.getHeightInGround(i + j, k + l, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig)
		};
	}

	protected static int method_41610(StructureFeature.class_7149 arg, int i, int j) {
		ChunkPos chunkPos = arg.chunkPos();
		int k = chunkPos.getStartX();
		int l = chunkPos.getStartZ();
		return method_42381(arg, k, l, i, j);
	}

	protected static int method_42381(StructureFeature.class_7149 arg, int i, int j, int k, int l) {
		int[] is = method_41611(arg, i, k, j, l);
		return Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
	}

	@Deprecated
	protected BlockPos method_42382(StructureFeature.class_7149 arg, BlockRotation blockRotation) {
		int i = 5;
		int j = 5;
		if (blockRotation == BlockRotation.CLOCKWISE_90) {
			i = -5;
		} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
			i = -5;
			j = -5;
		} else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
			j = -5;
		}

		ChunkPos chunkPos = arg.chunkPos();
		int k = chunkPos.getOffsetX(7);
		int l = chunkPos.getOffsetZ(7);
		return new BlockPos(k, method_42381(arg, k, l, i, j), l);
	}

	public abstract Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg);

	public abstract StructureType<?> getType();

	public static record class_7149(
		DynamicRegistryManager registryAccess,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		NoiseConfig randomState,
		StructureManager structureTemplateManager,
		ChunkRandom random,
		long seed,
		ChunkPos chunkPos,
		HeightLimitView heightAccessor,
		Predicate<RegistryEntry<Biome>> validBiome
	) {
		public class_7149(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			BiomeSource biomeSource,
			NoiseConfig noiseConfig,
			StructureManager structureManager,
			long l,
			ChunkPos chunkPos,
			HeightLimitView heightLimitView,
			Predicate<RegistryEntry<Biome>> predicate
		) {
			this(dynamicRegistryManager, chunkGenerator, biomeSource, noiseConfig, structureManager, method_41619(l, chunkPos), l, chunkPos, heightLimitView, predicate);
		}

		private static ChunkRandom method_41619(long l, ChunkPos chunkPos) {
			ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
			chunkRandom.setCarverSeed(l, chunkPos.x, chunkPos.z);
			return chunkRandom;
		}
	}

	public static record class_7150(BlockPos position, Consumer<StructurePiecesCollector> generator) {
	}
}
