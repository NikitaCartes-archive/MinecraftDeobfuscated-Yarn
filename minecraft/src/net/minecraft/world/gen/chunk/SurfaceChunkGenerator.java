package net.minecraft.world.gen.chunk;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class SurfaceChunkGenerator<T extends ChunkGeneratorConfig> extends ChunkGenerator<T> {
	private static final float[] field_16649 = Util.make(new float[13824], fs -> {
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				for (int k = 0; k < 24; k++) {
					fs[i * 24 * 24 + j * 24 + k] = (float)method_16571(j - 12, k - 12, i - 12);
				}
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
	private final OctavePerlinNoiseSampler field_16574;
	private final OctavePerlinNoiseSampler field_16581;
	private final OctavePerlinNoiseSampler field_16575;
	private final NoiseSampler surfaceDepthNoise;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;

	public SurfaceChunkGenerator(
		IWorld world, BiomeSource biomeSource, int verticalNoiseResolution, int horizontalNoiseResolution, int worldHeight, T config, boolean useSimplexNoise
	) {
		super(world, biomeSource, config);
		this.verticalNoiseResolution = horizontalNoiseResolution;
		this.horizontalNoiseResolution = verticalNoiseResolution;
		this.defaultBlock = config.getDefaultBlock();
		this.defaultFluid = config.getDefaultFluid();
		this.noiseSizeX = 16 / this.horizontalNoiseResolution;
		this.noiseSizeY = worldHeight / this.verticalNoiseResolution;
		this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
		this.random = new ChunkRandom(this.seed);
		this.field_16574 = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		this.field_16581 = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		this.field_16575 = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-7, 0));
		this.surfaceDepthNoise = (NoiseSampler)(useSimplexNoise
			? new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0))
			: new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-3, 0)));
	}

	private double sampleNoise(int x, int y, int z, double d, double e, double f, double g) {
		double h = 0.0;
		double i = 0.0;
		double j = 0.0;
		double k = 1.0;

		for (int l = 0; l < 16; l++) {
			double m = OctavePerlinNoiseSampler.maintainPrecision((double)x * d * k);
			double n = OctavePerlinNoiseSampler.maintainPrecision((double)y * e * k);
			double o = OctavePerlinNoiseSampler.maintainPrecision((double)z * d * k);
			double p = e * k;
			PerlinNoiseSampler perlinNoiseSampler = this.field_16574.getOctave(l);
			if (perlinNoiseSampler != null) {
				h += perlinNoiseSampler.sample(m, n, o, p, (double)y * p) / k;
			}

			PerlinNoiseSampler perlinNoiseSampler2 = this.field_16581.getOctave(l);
			if (perlinNoiseSampler2 != null) {
				i += perlinNoiseSampler2.sample(m, n, o, p, (double)y * p) / k;
			}

			if (l < 8) {
				PerlinNoiseSampler perlinNoiseSampler3 = this.field_16575.getOctave(l);
				if (perlinNoiseSampler3 != null) {
					j += perlinNoiseSampler3.sample(
							OctavePerlinNoiseSampler.maintainPrecision((double)x * f * k),
							OctavePerlinNoiseSampler.maintainPrecision((double)y * g * k),
							OctavePerlinNoiseSampler.maintainPrecision((double)z * f * k),
							g * k,
							(double)y * g * k
						)
						/ k;
				}
			}

			k /= 2.0;
		}

		return MathHelper.clampedLerp(h / 512.0, i / 512.0, (j / 10.0 + 1.0) / 2.0);
	}

	protected double[] sampleNoiseColumn(int x, int z) {
		double[] ds = new double[this.noiseSizeY + 1];
		this.sampleNoiseColumn(ds, x, z);
		return ds;
	}

	protected void sampleNoiseColumn(double[] buffer, int x, int z, double d, double e, double f, double g, int i, int j) {
		double[] ds = this.computeNoiseRange(x, z);
		double h = ds[0];
		double k = ds[1];
		double l = this.method_16409();
		double m = this.method_16410();

		for (int n = 0; n < this.getNoiseSizeY(); n++) {
			double o = this.sampleNoise(x, n, z, d, e, f, g);
			o -= this.computeNoiseFalloff(h, k, n);
			if ((double)n > l) {
				o = MathHelper.clampedLerp(o, (double)j, ((double)n - l) / (double)i);
			} else if ((double)n < m) {
				o = MathHelper.clampedLerp(o, -30.0, (m - (double)n) / (m - 1.0));
			}

			buffer[n] = o;
		}
	}

	protected abstract double[] computeNoiseRange(int x, int z);

	protected abstract double computeNoiseFalloff(double depth, double scale, int y);

	protected double method_16409() {
		return (double)(this.getNoiseSizeY() - 4);
	}

	protected double method_16410() {
		return 0.0;
	}

	@Override
	public int getHeightOnGround(int x, int z, Heightmap.Type heightmapType) {
		int i = Math.floorDiv(x, this.horizontalNoiseResolution);
		int j = Math.floorDiv(z, this.horizontalNoiseResolution);
		int k = Math.floorMod(x, this.horizontalNoiseResolution);
		int l = Math.floorMod(z, this.horizontalNoiseResolution);
		double d = (double)k / (double)this.horizontalNoiseResolution;
		double e = (double)l / (double)this.horizontalNoiseResolution;
		double[][] ds = new double[][]{
			this.sampleNoiseColumn(i, j), this.sampleNoiseColumn(i, j + 1), this.sampleNoiseColumn(i + 1, j), this.sampleNoiseColumn(i + 1, j + 1)
		};
		int m = this.getSeaLevel();

		for (int n = this.noiseSizeY - 1; n >= 0; n--) {
			double f = ds[0][n];
			double g = ds[1][n];
			double h = ds[2][n];
			double o = ds[3][n];
			double p = ds[0][n + 1];
			double q = ds[1][n + 1];
			double r = ds[2][n + 1];
			double s = ds[3][n + 1];

			for (int t = this.verticalNoiseResolution - 1; t >= 0; t--) {
				double u = (double)t / (double)this.verticalNoiseResolution;
				double v = MathHelper.lerp3(u, d, e, f, p, h, r, g, q, o, s);
				int w = n * this.verticalNoiseResolution + t;
				if (v > 0.0 || w < m) {
					BlockState blockState;
					if (v > 0.0) {
						blockState = this.defaultBlock;
					} else {
						blockState = this.defaultFluid;
					}

					if (heightmapType.getBlockPredicate().test(blockState)) {
						return w + 1;
					}
				}
			}
		}

		return 0;
	}

	protected abstract void sampleNoiseColumn(double[] buffer, int x, int z);

	public int getNoiseSizeY() {
		return this.noiseSizeY + 1;
	}

	@Override
	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setSeed(i, j);
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
				chunkRegion.getBiome(mutable.set(k + m, q, l + n))
					.buildSurface(
						chunkRandom, chunk, o, p, q, e, this.getConfig().getDefaultBlock(), this.getConfig().getDefaultFluid(), this.getSeaLevel(), this.world.getSeed()
					);
			}
		}

		this.buildBedrock(chunk, chunkRandom);
	}

	protected void buildBedrock(Chunk chunk, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = chunk.getPos().getStartX();
		int j = chunk.getPos().getStartZ();
		T chunkGeneratorConfig = this.getConfig();
		int k = chunkGeneratorConfig.getMinY();
		int l = chunkGeneratorConfig.getMaxY();

		for (BlockPos blockPos : BlockPos.iterate(i, 0, j, i + 15, 0, j + 15)) {
			if (l > 0) {
				for (int m = l; m >= l - 4; m--) {
					if (m >= l - random.nextInt(5)) {
						chunk.setBlockState(mutable.set(blockPos.getX(), m, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
					}
				}
			}

			if (k < 256) {
				for (int mx = k + 4; mx >= k; mx--) {
					if (mx <= k + random.nextInt(5)) {
						chunk.setBlockState(mutable.set(blockPos.getX(), mx, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
					}
				}
			}
		}
	}

	@Override
	public void populateNoise(IWorld world, Chunk chunk) {
		int i = this.getSeaLevel();
		ObjectList<StructurePiece> objectList = new ObjectArrayList<>(10);
		ObjectList<JigsawJunction> objectList2 = new ObjectArrayList<>(32);
		ChunkPos chunkPos = chunk.getPos();
		int j = chunkPos.x;
		int k = chunkPos.z;
		int l = j << 4;
		int m = k << 4;

		for (StructureFeature<?> structureFeature : Feature.JIGSAW_STRUCTURES) {
			String string = structureFeature.getName();
			LongIterator longIterator = chunk.getStructureReferences(string).iterator();

			while (longIterator.hasNext()) {
				long n = longIterator.nextLong();
				ChunkPos chunkPos2 = new ChunkPos(n);
				Chunk chunk2 = world.getChunk(chunkPos2.x, chunkPos2.z);
				StructureStart structureStart = chunk2.getStructureStart(string);
				if (structureStart != null && structureStart.hasChildren()) {
					for (StructurePiece structurePiece : structureStart.getChildren()) {
						if (structurePiece.method_16654(chunkPos, 12)) {
							if (structurePiece instanceof PoolStructurePiece) {
								PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
								StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
								if (projection == StructurePool.Projection.RIGID) {
									objectList.add(poolStructurePiece);
								}

								for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
									int o = jigsawJunction.getSourceX();
									int p = jigsawJunction.getSourceZ();
									if (o > l - 12 && p > m - 12 && o < l + 15 + 12 && p < m + 15 + 12) {
										objectList2.add(jigsawJunction);
									}
								}
							} else {
								objectList.add(structurePiece);
							}
						}
					}
				}
			}
		}

		double[][][] ds = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];

		for (int q = 0; q < this.noiseSizeZ + 1; q++) {
			ds[0][q] = new double[this.noiseSizeY + 1];
			this.sampleNoiseColumn(ds[0][q], j * this.noiseSizeX, k * this.noiseSizeZ + q);
			ds[1][q] = new double[this.noiseSizeY + 1];
		}

		ProtoChunk protoChunk = (ProtoChunk)chunk;
		Heightmap heightmap = protoChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = protoChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		ObjectListIterator<StructurePiece> objectListIterator = objectList.iterator();
		ObjectListIterator<JigsawJunction> objectListIterator2 = objectList2.iterator();

		for (int r = 0; r < this.noiseSizeX; r++) {
			for (int s = 0; s < this.noiseSizeZ + 1; s++) {
				this.sampleNoiseColumn(ds[1][s], j * this.noiseSizeX + r + 1, k * this.noiseSizeZ + s);
			}

			for (int s = 0; s < this.noiseSizeZ; s++) {
				ChunkSection chunkSection = protoChunk.getSection(15);
				chunkSection.lock();

				for (int t = this.noiseSizeY - 1; t >= 0; t--) {
					double d = ds[0][s][t];
					double e = ds[0][s + 1][t];
					double f = ds[1][s][t];
					double g = ds[1][s + 1][t];
					double h = ds[0][s][t + 1];
					double u = ds[0][s + 1][t + 1];
					double v = ds[1][s][t + 1];
					double w = ds[1][s + 1][t + 1];

					for (int x = this.verticalNoiseResolution - 1; x >= 0; x--) {
						int y = t * this.verticalNoiseResolution + x;
						int z = y & 15;
						int aa = y >> 4;
						if (chunkSection.getYOffset() >> 4 != aa) {
							chunkSection.unlock();
							chunkSection = protoChunk.getSection(aa);
							chunkSection.lock();
						}

						double ab = (double)x / (double)this.verticalNoiseResolution;
						double ac = MathHelper.lerp(ab, d, h);
						double ad = MathHelper.lerp(ab, f, v);
						double ae = MathHelper.lerp(ab, e, u);
						double af = MathHelper.lerp(ab, g, w);

						for (int ag = 0; ag < this.horizontalNoiseResolution; ag++) {
							int ah = l + r * this.horizontalNoiseResolution + ag;
							int ai = ah & 15;
							double aj = (double)ag / (double)this.horizontalNoiseResolution;
							double ak = MathHelper.lerp(aj, ac, ad);
							double al = MathHelper.lerp(aj, ae, af);

							for (int am = 0; am < this.horizontalNoiseResolution; am++) {
								int an = m + s * this.horizontalNoiseResolution + am;
								int ao = an & 15;
								double ap = (double)am / (double)this.horizontalNoiseResolution;
								double aq = MathHelper.lerp(ap, ak, al);
								double ar = MathHelper.clamp(aq / 200.0, -1.0, 1.0);
								ar = ar / 2.0 - ar * ar * ar / 24.0;

								while (objectListIterator.hasNext()) {
									StructurePiece structurePiece2 = (StructurePiece)objectListIterator.next();
									BlockBox blockBox = structurePiece2.getBoundingBox();
									int as = Math.max(0, Math.max(blockBox.minX - ah, ah - blockBox.maxX));
									int at = y - (blockBox.minY + (structurePiece2 instanceof PoolStructurePiece ? ((PoolStructurePiece)structurePiece2).getGroundLevelDelta() : 0));
									int au = Math.max(0, Math.max(blockBox.minZ - an, an - blockBox.maxZ));
									ar += method_16572(as, at, au) * 0.8;
								}

								objectListIterator.back(objectList.size());

								while (objectListIterator2.hasNext()) {
									JigsawJunction jigsawJunction2 = (JigsawJunction)objectListIterator2.next();
									int av = ah - jigsawJunction2.getSourceX();
									int as = y - jigsawJunction2.getSourceGroundY();
									int at = an - jigsawJunction2.getSourceZ();
									ar += method_16572(av, as, at) * 0.4;
								}

								objectListIterator2.back(objectList2.size());
								BlockState blockState;
								if (ar > 0.0) {
									blockState = this.defaultBlock;
								} else if (y < i) {
									blockState = this.defaultFluid;
								} else {
									blockState = AIR;
								}

								if (blockState != AIR) {
									if (blockState.getLuminance() != 0) {
										mutable.set(ah, y, an);
										protoChunk.addLightSource(mutable);
									}

									chunkSection.setBlockState(ai, z, ao, blockState, false);
									heightmap.trackUpdate(ai, y, ao, blockState);
									heightmap2.trackUpdate(ai, y, ao, blockState);
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

	private static double method_16572(int i, int j, int k) {
		int l = i + 12;
		int m = j + 12;
		int n = k + 12;
		if (l < 0 || l >= 24) {
			return 0.0;
		} else if (m < 0 || m >= 24) {
			return 0.0;
		} else {
			return n >= 0 && n < 24 ? (double)field_16649[n * 24 * 24 + l * 24 + m] : 0.0;
		}
	}

	private static double method_16571(int i, int j, int k) {
		double d = (double)(i * i + k * k);
		double e = (double)j + 0.5;
		double f = e * e;
		double g = Math.pow(Math.E, -(f / 16.0 + d / 16.0));
		double h = -e * MathHelper.fastInverseSqrt(f / 2.0 + d / 2.0) / 2.0;
		return h * g;
	}
}
