package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
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
	private static final float[] NOISE_WEIGHT_TABLE = Util.make(new float[13824], array -> {
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				for (int k = 0; k < 24; k++) {
					array[i * 24 * 24 + j * 24 + k] = (float)calculateNoiseWeight(j - 12, k - 12, i - 12);
				}
			}
		}
	});
	private static final float[] BIOME_WEIGHT_TABLE = Util.make(new float[25], array -> {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
				array[i + 2 + (j + 2) * 5] = f;
			}
		}
	});
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private final int verticalNoiseResolution;
	private final int horizontalNoiseResolution;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	protected final ChunkRandom random;
	private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
	private final OctavePerlinNoiseSampler upperInterpolatedNoise;
	private final OctavePerlinNoiseSampler interpolationNoise;
	private final NoiseSampler surfaceDepthNoise;
	private final OctavePerlinNoiseSampler densityNoise;
	@Nullable
	private final SimplexNoiseSampler islandNoise;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;
	private final long seed;
	protected final Supplier<ChunkGeneratorSettings> settings;
	private final int worldHeight;

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
		this.verticalNoiseResolution = generationShapeConfig.getSizeVertical() * 4;
		this.horizontalNoiseResolution = generationShapeConfig.getSizeHorizontal() * 4;
		this.defaultBlock = chunkGeneratorSettings.getDefaultBlock();
		this.defaultFluid = chunkGeneratorSettings.getDefaultFluid();
		this.noiseSizeX = 16 / this.horizontalNoiseResolution;
		this.noiseSizeY = generationShapeConfig.getHeight() / this.verticalNoiseResolution;
		this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
		this.random = new ChunkRandom(seed);
		this.lowerInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		this.upperInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		this.interpolationNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-7, 0));
		this.surfaceDepthNoise = (NoiseSampler)(generationShapeConfig.hasSimplexSurfaceNoise()
			? new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0))
			: new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-3, 0)));
		this.random.consume(2620);
		this.densityNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		if (generationShapeConfig.hasIslandNoiseOverride()) {
			ChunkRandom chunkRandom = new ChunkRandom(seed);
			chunkRandom.consume(17292);
			this.islandNoise = new SimplexNoiseSampler(chunkRandom);
		} else {
			this.islandNoise = null;
		}
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

	private double sampleNoise(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		boolean bl = true;
		double g = 1.0;

		for (int i = 0; i < 16; i++) {
			double h = OctavePerlinNoiseSampler.maintainPrecision((double)x * horizontalScale * g);
			double j = OctavePerlinNoiseSampler.maintainPrecision((double)y * verticalScale * g);
			double k = OctavePerlinNoiseSampler.maintainPrecision((double)z * horizontalScale * g);
			double l = verticalScale * g;
			PerlinNoiseSampler perlinNoiseSampler = this.lowerInterpolatedNoise.getOctave(i);
			if (perlinNoiseSampler != null) {
				d += perlinNoiseSampler.sample(h, j, k, l, (double)y * l) / g;
			}

			PerlinNoiseSampler perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(i);
			if (perlinNoiseSampler2 != null) {
				e += perlinNoiseSampler2.sample(h, j, k, l, (double)y * l) / g;
			}

			if (i < 8) {
				PerlinNoiseSampler perlinNoiseSampler3 = this.interpolationNoise.getOctave(i);
				if (perlinNoiseSampler3 != null) {
					f += perlinNoiseSampler3.sample(
							OctavePerlinNoiseSampler.maintainPrecision((double)x * horizontalStretch * g),
							OctavePerlinNoiseSampler.maintainPrecision((double)y * verticalStretch * g),
							OctavePerlinNoiseSampler.maintainPrecision((double)z * horizontalStretch * g),
							verticalStretch * g,
							(double)y * verticalStretch * g
						)
						/ g;
				}
			}

			g /= 2.0;
		}

		return MathHelper.clampedLerp(d / 512.0, e / 512.0, (f / 10.0 + 1.0) / 2.0);
	}

	private double[] sampleNoiseColumn(int x, int z) {
		double[] ds = new double[this.noiseSizeY + 1];
		this.sampleNoiseColumn(ds, x, z);
		return ds;
	}

	private void sampleNoiseColumn(double[] buffer, int x, int z) {
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		double d;
		double e;
		if (this.islandNoise != null) {
			d = (double)(TheEndBiomeSource.getNoiseAt(this.islandNoise, x, z) - 8.0F);
			if (d > 0.0) {
				e = 0.25;
			} else {
				e = 1.0;
			}
		} else {
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			int i = 2;
			int j = this.getSeaLevel();
			float k = this.populationSource.getBiomeForNoiseGen(x, j, z).getDepth();

			for (int l = -2; l <= 2; l++) {
				for (int m = -2; m <= 2; m++) {
					Biome biome = this.populationSource.getBiomeForNoiseGen(x + l, j, z + m);
					float n = biome.getDepth();
					float o = biome.getScale();
					float p;
					float q;
					if (generationShapeConfig.isAmplified() && n > 0.0F) {
						p = 1.0F + n * 2.0F;
						q = 1.0F + o * 4.0F;
					} else {
						p = n;
						q = o;
					}

					float r = n > k ? 0.5F : 1.0F;
					float s = r * BIOME_WEIGHT_TABLE[l + 2 + (m + 2) * 5] / (p + 2.0F);
					f += q * s;
					g += p * s;
					h += s;
				}
			}

			float t = g / h;
			float u = f / h;
			double v = (double)(t * 0.5F - 0.125F);
			double w = (double)(u * 0.9F + 0.1F);
			d = v * 0.265625;
			e = 96.0 / w;
		}

		double y = 684.412 * generationShapeConfig.getSampling().getXZScale();
		double aa = 684.412 * generationShapeConfig.getSampling().getYScale();
		double ab = y / generationShapeConfig.getSampling().getXZFactor();
		double ac = aa / generationShapeConfig.getSampling().getYFactor();
		double v = (double)generationShapeConfig.getTopSlide().getTarget();
		double w = (double)generationShapeConfig.getTopSlide().getSize();
		double ad = (double)generationShapeConfig.getTopSlide().getOffset();
		double ae = (double)generationShapeConfig.getBottomSlide().getTarget();
		double af = (double)generationShapeConfig.getBottomSlide().getSize();
		double ag = (double)generationShapeConfig.getBottomSlide().getOffset();
		double ah = generationShapeConfig.hasRandomDensityOffset() ? this.getRandomDensityAt(x, z) : 0.0;
		double ai = generationShapeConfig.getDensityFactor();
		double aj = generationShapeConfig.getDensityOffset();
		int ak = MathHelper.floorDiv(generationShapeConfig.getMinimumY(), this.verticalNoiseResolution);

		for (int al = 0; al <= this.noiseSizeY; al++) {
			int am = al + ak;
			double an = this.sampleNoise(x, am, z, y, aa, ab, ac);
			double ao = 1.0 - (double)am * 2.0 / (double)this.noiseSizeY + ah;
			double ap = ao * ai + aj;
			double aq = (ap + d) * e;
			if (aq > 0.0) {
				an += aq * 4.0;
			} else {
				an += aq;
			}

			if (w > 0.0) {
				double ar = ((double)(this.noiseSizeY - al) - ad) / w;
				an = MathHelper.clampedLerp(v, an, ar);
			}

			if (af > 0.0) {
				double ar = ((double)al - ag) / af;
				an = MathHelper.clampedLerp(ae, an, ar);
			}

			buffer[al] = an;
		}
	}

	private double getRandomDensityAt(int x, int z) {
		double d = this.densityNoise.sample((double)(x * 200), 10.0, (double)(z * 200), 1.0, 0.0, true);
		double e;
		if (d < 0.0) {
			e = -d * 0.3;
		} else {
			e = d;
		}

		double f = e * 24.575625 - 2.0;
		return f < 0.0 ? f * 0.009486607142857142 : Math.min(f, 1.0) * 0.006640625;
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		return this.sampleHeightmap(x, z, null, heightmapType.getBlockPredicate());
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z) {
		BlockState[] blockStates = new BlockState[this.noiseSizeY * this.verticalNoiseResolution];
		this.sampleHeightmap(x, z, blockStates, null);
		return new VerticalBlockSample(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY(), blockStates);
	}

	private int sampleHeightmap(int x, int z, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate) {
		int i = Math.floorDiv(x, this.horizontalNoiseResolution);
		int j = Math.floorDiv(z, this.horizontalNoiseResolution);
		int k = Math.floorMod(x, this.horizontalNoiseResolution);
		int l = Math.floorMod(z, this.horizontalNoiseResolution);
		double d = (double)k / (double)this.horizontalNoiseResolution;
		double e = (double)l / (double)this.horizontalNoiseResolution;
		double[][] ds = new double[][]{
			this.sampleNoiseColumn(i, j), this.sampleNoiseColumn(i, j + 1), this.sampleNoiseColumn(i + 1, j), this.sampleNoiseColumn(i + 1, j + 1)
		};

		for (int m = this.noiseSizeY - 1; m >= 0; m--) {
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
				int w = v + ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY();
				BlockState blockState = this.getBlockState(u, w);
				if (states != null) {
					states[v] = blockState;
				}

				if (predicate != null && predicate.test(blockState)) {
					return w + 1;
				}
			}
		}

		return 0;
	}

	protected BlockState getBlockState(double density, int y) {
		BlockState blockState;
		if (density > 0.0) {
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
		boolean bl = l + 5 - 1 >= chunk.getSectionCount() && l < chunk.getTopHeightLimit();
		boolean bl2 = k + 5 - 1 >= chunk.getSectionCount() && k < chunk.getTopHeightLimit();
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
		ObjectList<StructurePiece> objectList = new ObjectArrayList<>(10);
		ObjectList<JigsawJunction> objectList2 = new ObjectArrayList<>(32);
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		int k = ChunkSectionPos.getBlockCoord(i);
		int l = ChunkSectionPos.getBlockCoord(j);

		for (StructureFeature<?> structureFeature : StructureFeature.JIGSAW_STRUCTURES) {
			accessor.getStructuresWithChildren(ChunkSectionPos.from(chunkPos, 0), structureFeature).forEach(start -> {
				for (StructurePiece structurePiece : start.getChildren()) {
					if (structurePiece.intersectsChunk(chunkPos, 12)) {
						if (structurePiece instanceof PoolStructurePiece) {
							PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
							StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
							if (projection == StructurePool.Projection.RIGID) {
								objectList.add(poolStructurePiece);
							}

							for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
								int kx = jigsawJunction.getSourceX();
								int lx = jigsawJunction.getSourceZ();
								if (kx > k - 12 && lx > l - 12 && kx < k + 15 + 12 && lx < l + 15 + 12) {
									objectList2.add(jigsawJunction);
								}
							}
						} else {
							objectList.add(structurePiece);
						}
					}
				}
			});
		}

		double[][][] ds = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];

		for (int m = 0; m < this.noiseSizeZ + 1; m++) {
			ds[0][m] = new double[this.noiseSizeY + 1];
			this.sampleNoiseColumn(ds[0][m], i * this.noiseSizeX, j * this.noiseSizeZ + m);
			ds[1][m] = new double[this.noiseSizeY + 1];
		}

		ProtoChunk protoChunk = (ProtoChunk)chunk;
		Heightmap heightmap = protoChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = protoChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		ObjectListIterator<StructurePiece> objectListIterator = objectList.iterator();
		ObjectListIterator<JigsawJunction> objectListIterator2 = objectList2.iterator();

		for (int n = 0; n < this.noiseSizeX; n++) {
			for (int o = 0; o < this.noiseSizeZ + 1; o++) {
				this.sampleNoiseColumn(ds[1][o], i * this.noiseSizeX + n + 1, j * this.noiseSizeZ + o);
			}

			for (int o = 0; o < this.noiseSizeZ; o++) {
				ChunkSection chunkSection = protoChunk.getSection(protoChunk.method_32890() - 1);
				chunkSection.lock();

				for (int p = this.noiseSizeY - 1; p >= 0; p--) {
					double d = ds[0][o][p];
					double e = ds[0][o + 1][p];
					double f = ds[1][o][p];
					double g = ds[1][o + 1][p];
					double h = ds[0][o][p + 1];
					double q = ds[0][o + 1][p + 1];
					double r = ds[1][o][p + 1];
					double s = ds[1][o + 1][p + 1];

					for (int t = this.verticalNoiseResolution - 1; t >= 0; t--) {
						int u = p * this.verticalNoiseResolution + t + ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY();
						int v = u & 15;
						int w = protoChunk.getSectionIndex(u);
						if (protoChunk.getSectionIndex(chunkSection.getYOffset()) != w) {
							chunkSection.unlock();
							chunkSection = protoChunk.getSection(w);
							chunkSection.lock();
						}

						double x = (double)t / (double)this.verticalNoiseResolution;
						double y = MathHelper.lerp(x, d, h);
						double z = MathHelper.lerp(x, f, r);
						double aa = MathHelper.lerp(x, e, q);
						double ab = MathHelper.lerp(x, g, s);

						for (int ac = 0; ac < this.horizontalNoiseResolution; ac++) {
							int ad = k + n * this.horizontalNoiseResolution + ac;
							int ae = ad & 15;
							double af = (double)ac / (double)this.horizontalNoiseResolution;
							double ag = MathHelper.lerp(af, y, z);
							double ah = MathHelper.lerp(af, aa, ab);

							for (int ai = 0; ai < this.horizontalNoiseResolution; ai++) {
								int aj = l + o * this.horizontalNoiseResolution + ai;
								int ak = aj & 15;
								double al = (double)ai / (double)this.horizontalNoiseResolution;
								double am = MathHelper.lerp(al, ag, ah);
								double an = MathHelper.clamp(am / 200.0, -1.0, 1.0);
								an = an / 2.0 - an * an * an / 24.0;

								while (objectListIterator.hasNext()) {
									StructurePiece structurePiece = (StructurePiece)objectListIterator.next();
									BlockBox blockBox = structurePiece.getBoundingBox();
									int ao = Math.max(0, Math.max(blockBox.minX - ad, ad - blockBox.maxX));
									int ap = u - (blockBox.minY + (structurePiece instanceof PoolStructurePiece ? ((PoolStructurePiece)structurePiece).getGroundLevelDelta() : 0));
									int aq = Math.max(0, Math.max(blockBox.minZ - aj, aj - blockBox.maxZ));
									an += getNoiseWeight(ao, ap, aq) * 0.8;
								}

								objectListIterator.back(objectList.size());

								while (objectListIterator2.hasNext()) {
									JigsawJunction jigsawJunction = (JigsawJunction)objectListIterator2.next();
									int ar = ad - jigsawJunction.getSourceX();
									int ao = u - jigsawJunction.getSourceGroundY();
									int ap = aj - jigsawJunction.getSourceZ();
									an += getNoiseWeight(ar, ao, ap) * 0.4;
								}

								objectListIterator2.back(objectList2.size());
								BlockState blockState = this.getBlockState(an, u);
								if (blockState != AIR) {
									if (blockState.getLuminance() != 0) {
										mutable.set(ad, u, aj);
										protoChunk.addLightSource(mutable);
									}

									chunkSection.setBlockState(ae, v, ak, blockState, false);
									heightmap.trackUpdate(ae, u, ak, blockState);
									heightmap2.trackUpdate(ae, u, ak, blockState);
								}
							}
						}
					}
				}

				chunkSection.unlock();
			}

			double[][] es = ds[0];
			ds[0] = ds[1];
			ds[1] = es;
		}
	}

	private static double getNoiseWeight(int x, int y, int z) {
		int i = x + 12;
		int j = y + 12;
		int k = z + 12;
		if (i < 0 || i >= 24) {
			return 0.0;
		} else if (j < 0 || j >= 24) {
			return 0.0;
		} else {
			return k >= 0 && k < 24 ? (double)NOISE_WEIGHT_TABLE[k * 24 * 24 + i * 24 + j] : 0.0;
		}
	}

	private static double calculateNoiseWeight(int x, int y, int z) {
		double d = (double)(x * x + z * z);
		double e = (double)y + 0.5;
		double f = e * e;
		double g = Math.pow(Math.E, -(f / 16.0 + d / 16.0));
		double h = -e * MathHelper.fastInverseSqrt(f / 2.0 + d / 2.0) / 2.0;
		return h * g;
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
			int i = region.getCenterChunkX();
			int j = region.getCenterChunkZ();
			Biome biome = region.getBiome(new ChunkPos(i, j).getStartPos());
			ChunkRandom chunkRandom = new ChunkRandom();
			chunkRandom.setPopulationSeed(region.getSeed(), ChunkSectionPos.getBlockCoord(i), ChunkSectionPos.getBlockCoord(j));
			SpawnHelper.populateEntities(region, biome, i, j, chunkRandom);
		}
	}
}
