package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5742;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.InterpolatedNoise;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.WorldGenRandom;
import net.minecraft.world.gen.feature.StructureFeature;

public final class NoiseChunkGenerator extends ChunkGenerator {
	public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BiomeSource.CODEC.fieldOf("biome_source").forGetter(noiseChunkGenerator -> noiseChunkGenerator.populationSource),
					Codec.LONG.fieldOf("seed").stable().forGetter(noiseChunkGenerator -> noiseChunkGenerator.seed),
					ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(noiseChunkGenerator -> noiseChunkGenerator.settings)
				)
				.apply(instance, instance.stable(NoiseChunkGenerator::new))
	);
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private static final BlockState[] EMPTY = new BlockState[0];
	private final int verticalNoiseResolution;
	private final int horizontalNoiseResolution;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	protected final WorldGenRandom random;
	private final NoiseSampler surfaceDepthNoise;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;
	private final long seed;
	protected final Supplier<ChunkGeneratorSettings> settings;
	private final int worldHeight;
	private final NoiseColumnSampler noiseColumnSampler;

	public NoiseChunkGenerator(BiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> settings) {
		this(biomeSource, biomeSource, seed, settings);
	}

	private NoiseChunkGenerator(BiomeSource populationSource, BiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> settings) {
		super(populationSource, biomeSource, ((ChunkGeneratorSettings)settings.get()).getStructuresConfig(), seed);
		this.seed = seed;
		ChunkGeneratorSettings chunkGeneratorSettings = (ChunkGeneratorSettings)settings.get();
		this.settings = settings;
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
		this.worldHeight = generationShapeConfig.getHeight();
		this.verticalNoiseResolution = class_5742.method_33101(generationShapeConfig.getSizeVertical());
		this.horizontalNoiseResolution = class_5742.method_33101(generationShapeConfig.getSizeHorizontal());
		this.defaultBlock = chunkGeneratorSettings.getDefaultBlock();
		this.defaultFluid = chunkGeneratorSettings.getDefaultFluid();
		this.noiseSizeX = 16 / this.horizontalNoiseResolution;
		this.noiseSizeY = generationShapeConfig.getHeight() / this.verticalNoiseResolution;
		this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
		this.random = new ChunkRandom(seed);
		InterpolatedNoise interpolatedNoise = new InterpolatedNoise(this.random);
		this.surfaceDepthNoise = (NoiseSampler)(generationShapeConfig.hasSimplexSurfaceNoise()
			? new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0))
			: new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-3, 0)));
		this.random.skip(2620);
		OctavePerlinNoiseSampler octavePerlinNoiseSampler = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		SimplexNoiseSampler simplexNoiseSampler;
		if (generationShapeConfig.hasIslandNoiseOverride()) {
			ChunkRandom chunkRandom = new ChunkRandom(seed);
			chunkRandom.skip(17292);
			simplexNoiseSampler = new SimplexNoiseSampler(chunkRandom);
		} else {
			simplexNoiseSampler = null;
		}

		this.noiseColumnSampler = new NoiseColumnSampler(
			populationSource,
			this.horizontalNoiseResolution,
			this.verticalNoiseResolution,
			this.noiseSizeY,
			generationShapeConfig,
			interpolatedNoise,
			simplexNoiseSampler,
			octavePerlinNoiseSampler
		);
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator withSeed(long seed) {
		return new NoiseChunkGenerator(this.populationSource.withSeed(seed), seed, this.settings);
	}

	public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
		return this.seed == seed && ((ChunkGeneratorSettings)this.settings.get()).equals(settingsKey);
	}

	private double[] sampleNoiseColumn(int x, int z, int minY, int noiseSizeY) {
		double[] ds = new double[noiseSizeY + 1];
		this.noiseColumnSampler
			.sampleNoiseColumn(ds, x, z, ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig(), this.getSeaLevel(), minY, noiseSizeY);
		return ds;
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY(), world.getBottomY());
		int j = Math.min(
			((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY()
				+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getHeight(),
			world.getTopY()
		);
		int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
		int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
		return l <= 0 ? world.getBottomY() : this.sampleHeightmap(x, z, null, heightmap.getBlockPredicate(), k, l).orElse(world.getBottomY());
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY(), world.getBottomY());
		int j = Math.min(
			((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY()
				+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getHeight(),
			world.getTopY()
		);
		int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
		int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
		if (l <= 0) {
			return new VerticalBlockSample(i, EMPTY);
		} else {
			BlockState[] blockStates = new BlockState[l * this.verticalNoiseResolution];
			this.sampleHeightmap(x, z, blockStates, null, k, l);
			return new VerticalBlockSample(i, blockStates);
		}
	}

	private OptionalInt sampleHeightmap(int x, int z, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate, int minY, int noiseSizeY) {
		int i = Math.floorDiv(x, this.horizontalNoiseResolution);
		int j = Math.floorDiv(z, this.horizontalNoiseResolution);
		int k = Math.floorMod(x, this.horizontalNoiseResolution);
		int l = Math.floorMod(z, this.horizontalNoiseResolution);
		double d = (double)k / (double)this.horizontalNoiseResolution;
		double e = (double)l / (double)this.horizontalNoiseResolution;
		double[][] ds = new double[][]{
			this.sampleNoiseColumn(i, j, minY, noiseSizeY),
			this.sampleNoiseColumn(i, j + 1, minY, noiseSizeY),
			this.sampleNoiseColumn(i + 1, j, minY, noiseSizeY),
			this.sampleNoiseColumn(i + 1, j + 1, minY, noiseSizeY)
		};

		for (int m = noiseSizeY - 1; m >= 0; m--) {
			double f = ds[0][m];
			double g = ds[1][m];
			double h = ds[2][m];
			double n = ds[3][m];
			double o = ds[0][m + 1];
			double p = ds[1][m + 1];
			double q = ds[2][m + 1];
			double r = ds[3][m + 1];

			for (int s = this.verticalNoiseResolution - 1; s >= 0; s--) {
				double t = (double)s / (double)this.verticalNoiseResolution;
				double u = MathHelper.lerp3(t, d, e, f, o, h, q, g, p, n, r);
				int v = m * this.verticalNoiseResolution + s;
				int w = v + minY * this.verticalNoiseResolution;
				BlockState blockState = this.getBlockState(StructureWeightSampler.INSTANCE, x, w, z, u);
				if (states != null) {
					states[v] = blockState;
				}

				if (predicate != null && predicate.test(blockState)) {
					return OptionalInt.of(w + 1);
				}
			}
		}

		return OptionalInt.empty();
	}

	protected BlockState getBlockState(StructureWeightSampler structures, int x, int y, int z, double noise) {
		double d = MathHelper.clamp(noise / 200.0, -1.0, 1.0);
		d = d / 2.0 - d * d * d / 24.0;
		d += structures.getWeight(x, y, z);
		BlockState blockState;
		if (d > 0.0) {
			blockState = this.defaultBlock;
		} else if (y < this.getSeaLevel()) {
			blockState = this.defaultFluid;
		} else {
			blockState = AIR;
		}

		return blockState;
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setTerrainSeed(i, j);
		ChunkPos chunkPos2 = chunk.getPos();
		int k = chunkPos2.getStartX();
		int l = chunkPos2.getStartZ();
		double d = 0.0625;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = 0; m < 16; m++) {
			for (int n = 0; n < 16; n++) {
				int o = k + m;
				int p = l + n;
				int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, n) + 1;
				double e = this.surfaceDepthNoise.sample((double)o * 0.0625, (double)p * 0.0625, 0.0625, (double)m * 0.0625) * 15.0;
				region.getBiome(mutable.set(k + m, q, l + n))
					.buildSurface(chunkRandom, chunk, o, p, q, e, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), region.getSeed());
			}
		}

		this.buildBedrock(chunk, chunkRandom);
	}

	private void buildBedrock(Chunk chunk, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = chunk.getPos().getStartX();
		int j = chunk.getPos().getStartZ();
		ChunkGeneratorSettings chunkGeneratorSettings = (ChunkGeneratorSettings)this.settings.get();
		int k = chunkGeneratorSettings.getBedrockFloorY();
		int l = this.worldHeight - 1 - chunkGeneratorSettings.getBedrockCeilingY();
		int m = 5;
		boolean bl = l + 5 - 1 >= chunk.getBottomY() && l < chunk.getTopY();
		boolean bl2 = k + 5 - 1 >= chunk.getBottomY() && k < chunk.getTopY();
		if (bl || bl2) {
			for (BlockPos blockPos : BlockPos.iterate(i, 0, j, i + 15, 0, j + 15)) {
				if (bl) {
					for (int n = 0; n < 5; n++) {
						if (n <= random.nextInt(5)) {
							chunk.setBlockState(mutable.set(blockPos.getX(), l - n, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
						}
					}
				}

				if (bl2) {
					for (int nx = 4; nx >= 0; nx--) {
						if (nx <= random.nextInt(5)) {
							chunk.setBlockState(mutable.set(blockPos.getX(), k + nx, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
						}
					}
				}
			}
		}
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		ProtoChunk protoChunk = (ProtoChunk)chunk;
		Heightmap heightmap = protoChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = protoChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY(), chunk.getBottomY());
		int j = Math.min(
			((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY()
				+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getHeight(),
			chunk.getTopY()
		);
		int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
		int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
		if (l > 0) {
			int m = chunkPos.x;
			int n = chunkPos.z;
			int o = chunkPos.getStartX();
			int p = chunkPos.getStartZ();
			StructureWeightSampler structureWeightSampler = new StructureWeightSampler(accessor, chunk);
			double[][][] ds = new double[2][this.noiseSizeZ + 1][l + 1];
			GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();

			for (int q = 0; q < this.noiseSizeZ + 1; q++) {
				ds[0][q] = new double[l + 1];
				double[] es = ds[0][q];
				int r = m * this.noiseSizeX;
				int s = n * this.noiseSizeZ + q;
				this.noiseColumnSampler.sampleNoiseColumn(es, r, s, generationShapeConfig, this.getSeaLevel(), k, l);
				ds[1][q] = new double[l + 1];
			}

			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int t = 0; t < this.noiseSizeX; t++) {
				int r = m * this.noiseSizeX + t + 1;

				for (int s = 0; s < this.noiseSizeZ + 1; s++) {
					double[] fs = ds[1][s];
					int u = n * this.noiseSizeZ + s;
					this.noiseColumnSampler.sampleNoiseColumn(fs, r, u, generationShapeConfig, this.getSeaLevel(), k, l);
				}

				for (int s = 0; s < this.noiseSizeZ; s++) {
					ChunkSection chunkSection = protoChunk.getSection(protoChunk.countVerticalSections() - 1);
					chunkSection.lock();

					for (int u = l - 1; u >= 0; u--) {
						double d = ds[0][s][u];
						double e = ds[0][s + 1][u];
						double f = ds[1][s][u];
						double g = ds[1][s + 1][u];
						double h = ds[0][s][u + 1];
						double v = ds[0][s + 1][u + 1];
						double w = ds[1][s][u + 1];
						double x = ds[1][s + 1][u + 1];

						for (int y = this.verticalNoiseResolution - 1; y >= 0; y--) {
							int z = u * this.verticalNoiseResolution + y + ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY();
							int aa = z & 15;
							int ab = protoChunk.getSectionIndex(z);
							if (protoChunk.getSectionIndex(chunkSection.getYOffset()) != ab) {
								chunkSection.unlock();
								chunkSection = protoChunk.getSection(ab);
								chunkSection.lock();
							}

							double ac = (double)y / (double)this.verticalNoiseResolution;
							double ad = MathHelper.lerp(ac, d, h);
							double ae = MathHelper.lerp(ac, f, w);
							double af = MathHelper.lerp(ac, e, v);
							double ag = MathHelper.lerp(ac, g, x);

							for (int ah = 0; ah < this.horizontalNoiseResolution; ah++) {
								int ai = o + t * this.horizontalNoiseResolution + ah;
								int aj = ai & 15;
								double ak = (double)ah / (double)this.horizontalNoiseResolution;
								double al = MathHelper.lerp(ak, ad, ae);
								double am = MathHelper.lerp(ak, af, ag);

								for (int an = 0; an < this.horizontalNoiseResolution; an++) {
									int ao = p + s * this.horizontalNoiseResolution + an;
									int ap = ao & 15;
									double aq = (double)an / (double)this.horizontalNoiseResolution;
									double ar = MathHelper.lerp(aq, al, am);
									BlockState blockState = this.getBlockState(structureWeightSampler, ai, z, ao, ar);
									if (blockState != AIR) {
										if (blockState.getLuminance() != 0) {
											mutable.set(ai, z, ao);
											protoChunk.addLightSource(mutable);
										}

										chunkSection.setBlockState(aj, aa, ap, blockState, false);
										heightmap.trackUpdate(aj, z, ap, blockState);
										heightmap2.trackUpdate(aj, z, ap, blockState);
									}
								}
							}
						}
					}

					chunkSection.unlock();
				}

				this.swapElements(ds);
			}
		}
	}

	public <T> void swapElements(T[] array) {
		T object = array[0];
		array[0] = array[1];
		array[1] = object;
	}

	@Override
	public int getWorldHeight() {
		return this.worldHeight;
	}

	@Override
	public int getSeaLevel() {
		return ((ChunkGeneratorSettings)this.settings.get()).getSeaLevel();
	}

	@Override
	public List<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
		if (accessor.getStructureAt(pos, true, StructureFeature.SWAMP_HUT).hasChildren()) {
			if (group == SpawnGroup.MONSTER) {
				return StructureFeature.SWAMP_HUT.getMonsterSpawns();
			}

			if (group == SpawnGroup.CREATURE) {
				return StructureFeature.SWAMP_HUT.getCreatureSpawns();
			}
		}

		if (group == SpawnGroup.MONSTER) {
			if (accessor.getStructureAt(pos, false, StructureFeature.PILLAGER_OUTPOST).hasChildren()) {
				return StructureFeature.PILLAGER_OUTPOST.getMonsterSpawns();
			}

			if (accessor.getStructureAt(pos, false, StructureFeature.MONUMENT).hasChildren()) {
				return StructureFeature.MONUMENT.getMonsterSpawns();
			}

			if (accessor.getStructureAt(pos, true, StructureFeature.FORTRESS).hasChildren()) {
				return StructureFeature.FORTRESS.getMonsterSpawns();
			}
		}

		return super.getEntitySpawnList(biome, accessor, group, pos);
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		if (!((ChunkGeneratorSettings)this.settings.get()).isMobGenerationDisabled()) {
			ChunkPos chunkPos = region.getCenterPos();
			Biome biome = region.getBiome(chunkPos.getStartPos());
			ChunkRandom chunkRandom = new ChunkRandom();
			chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
			SpawnHelper.populateEntities(region, biome, chunkPos, chunkRandom);
		}
	}
}
