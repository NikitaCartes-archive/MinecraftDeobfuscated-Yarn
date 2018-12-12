package net.minecraft.world.gen.chunk;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Random;
import net.minecraft.class_3443;
import net.minecraft.class_3785;
import net.minecraft.class_3790;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.sortme.structures.StructureStart;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.JigsawJunction;

public abstract class SurfaceChunkGenerator<T extends ChunkGeneratorConfig> extends ChunkGenerator<T> {
	private static final float[] field_16649 = SystemUtil.consume(new float[13824], fs -> {
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				for (int k = 0; k < 24; k++) {
					fs[i * 24 * 24 + j * 24 + k] = (float)method_16571(j - 12, k - 12, i - 12);
				}
			}
		}
	});
	private static final BlockState AIR = Blocks.field_10124.getDefaultState();
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

	public SurfaceChunkGenerator(IWorld iWorld, BiomeSource biomeSource, int i, int j, int k, T chunkGeneratorConfig, boolean bl) {
		super(iWorld, biomeSource, chunkGeneratorConfig);
		this.verticalNoiseResolution = j;
		this.horizontalNoiseResolution = i;
		this.defaultBlock = chunkGeneratorConfig.getDefaultBlock();
		this.defaultFluid = chunkGeneratorConfig.getDefaultFluid();
		this.noiseSizeX = 16 / this.horizontalNoiseResolution;
		this.noiseSizeY = k / this.verticalNoiseResolution;
		this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
		this.random = new ChunkRandom(this.seed);
		this.field_16574 = new OctavePerlinNoiseSampler(this.random, 16);
		this.field_16581 = new OctavePerlinNoiseSampler(this.random, 16);
		this.field_16575 = new OctavePerlinNoiseSampler(this.random, 8);
		this.surfaceDepthNoise = (NoiseSampler)(bl ? new OctaveSimplexNoiseSampler(this.random, 4) : new OctavePerlinNoiseSampler(this.random, 4));
	}

	private double sampleNoise(int i, int j, int k, double d, double e, double f, double g) {
		double h = 0.0;
		double l = 0.0;
		double m = 0.0;
		double n = 1.0;

		for (int o = 0; o < 16; o++) {
			double p = OctavePerlinNoiseSampler.maintainPrecision((double)i * d * n);
			double q = OctavePerlinNoiseSampler.maintainPrecision((double)j * e * n);
			double r = OctavePerlinNoiseSampler.maintainPrecision((double)k * d * n);
			double s = e * n;
			h += this.field_16574.getOctave(o).sample(p, q, r, s, (double)j * s) / n;
			l += this.field_16581.getOctave(o).sample(p, q, r, s, (double)j * s) / n;
			if (o < 8) {
				m += this.field_16575
						.getOctave(o)
						.sample(
							OctavePerlinNoiseSampler.maintainPrecision((double)i * f * n),
							OctavePerlinNoiseSampler.maintainPrecision((double)j * g * n),
							OctavePerlinNoiseSampler.maintainPrecision((double)k * f * n),
							g * n,
							(double)j * g * n
						)
					/ n;
			}

			n /= 2.0;
		}

		return MathHelper.lerpClamped(h / 512.0, l / 512.0, (m / 10.0 + 1.0) / 2.0);
	}

	protected double[] sampleNoiseColumn(int i, int j) {
		double[] ds = new double[this.noiseSizeY + 1];
		this.sampleNoiseColumn(ds, i, j);
		return ds;
	}

	protected void sampleNoiseColumn(double[] ds, int i, int j, double d, double e, double f, double g, int k, int l) {
		double[] es = this.computeNoiseRange(i, j);
		double h = es[0];
		double m = es[1];
		double n = this.method_16409();
		double o = this.method_16410();

		for (int p = 0; p < this.getNoiseSizeY(); p++) {
			double q = this.sampleNoise(i, p, j, d, e, f, g);
			q -= this.computeNoiseFalloff(h, m, p);
			if ((double)p > n) {
				q = MathHelper.lerpClamped(q, (double)l, ((double)p - n) / (double)k);
			} else if ((double)p < o) {
				q = MathHelper.lerpClamped(q, -30.0, (o - (double)p) / (o - 1.0));
			}

			ds[p] = q;
		}
	}

	protected abstract double[] computeNoiseRange(int i, int j);

	protected abstract double computeNoiseFalloff(double d, double e, int i);

	protected double method_16409() {
		return (double)(this.getNoiseSizeY() - 4);
	}

	protected double method_16410() {
		return 0.0;
	}

	@Override
	public int produceHeight(int i, int j, Heightmap.Type type) {
		int k = Math.floorDiv(i, this.horizontalNoiseResolution);
		int l = Math.floorDiv(j, this.horizontalNoiseResolution);
		int m = Math.floorMod(i, this.horizontalNoiseResolution);
		int n = Math.floorMod(j, this.horizontalNoiseResolution);
		double d = (double)m / (double)this.horizontalNoiseResolution;
		double e = (double)n / (double)this.horizontalNoiseResolution;
		double[][] ds = new double[][]{
			this.sampleNoiseColumn(k, l), this.sampleNoiseColumn(k, l + 1), this.sampleNoiseColumn(k + 1, l), this.sampleNoiseColumn(k + 1, l + 1)
		};
		int o = this.method_16398();

		for (int p = this.noiseSizeY - 1; p >= 0; p--) {
			double f = ds[0][p];
			double g = ds[1][p];
			double h = ds[2][p];
			double q = ds[3][p];
			double r = ds[0][p + 1];
			double s = ds[1][p + 1];
			double t = ds[2][p + 1];
			double u = ds[3][p + 1];

			for (int v = this.verticalNoiseResolution - 1; v >= 0; v--) {
				double w = (double)v / (double)this.verticalNoiseResolution;
				double x = MathHelper.lerp3(w, d, e, f, r, h, t, g, s, q, u);
				int y = p * this.verticalNoiseResolution + v;
				if (x > 0.0 || y < o) {
					BlockState blockState;
					if (x > 0.0) {
						blockState = this.defaultBlock;
					} else {
						blockState = this.defaultFluid;
					}

					if (type.getBlockPredicate().test(blockState)) {
						return y + 1;
					}
				}
			}
		}

		return 0;
	}

	protected abstract void sampleNoiseColumn(double[] ds, int i, int j);

	public int getNoiseSizeY() {
		return this.noiseSizeY + 1;
	}

	@Override
	public void buildSurface(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setSeed(i, j);
		ChunkPos chunkPos2 = chunk.getPos();
		int k = chunkPos2.getXStart();
		int l = chunkPos2.getZStart();
		double d = 0.0625;
		Biome[] biomes = chunk.getBiomeArray();

		for (int m = 0; m < 16; m++) {
			for (int n = 0; n < 16; n++) {
				int o = k + m;
				int p = l + n;
				int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, n) + 1;
				double e = this.surfaceDepthNoise.sample((double)o * 0.0625, (double)p * 0.0625, 0.0625, (double)m * 0.0625);
				biomes[n * 16 + m]
					.buildSurface(chunkRandom, chunk, o, p, q, e, this.method_12109().getDefaultBlock(), this.method_12109().getDefaultFluid(), 63, this.world.getSeed());
			}
		}

		this.buildBedrock(chunk, chunkRandom);
	}

	protected void buildBedrock(Chunk chunk, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = chunk.getPos().getXStart();
		int j = chunk.getPos().getZStart();
		T chunkGeneratorConfig = this.method_12109();
		int k = chunkGeneratorConfig.getMinY();
		int l = chunkGeneratorConfig.getMaxY();

		for (BlockPos blockPos : BlockPos.iterateBoxPositions(i, 0, j, i + 16, 0, j + 16)) {
			if (l > 0) {
				for (int m = l; m >= l - 4; m--) {
					if (m >= l - random.nextInt(5)) {
						chunk.setBlockState(mutable.set(blockPos.getX(), m, blockPos.getZ()), Blocks.field_9987.getDefaultState(), false);
					}
				}
			}

			if (k < 256) {
				for (int mx = k + 4; mx >= k; mx--) {
					if (mx <= k + random.nextInt(5)) {
						chunk.setBlockState(mutable.set(blockPos.getX(), mx, blockPos.getZ()), Blocks.field_9987.getDefaultState(), false);
					}
				}
			}
		}
	}

	@Override
	public void populateNoise(IWorld iWorld, Chunk chunk) {
		int i = this.method_16398();
		ObjectList<class_3790> objectList = new ObjectArrayList<>(10);
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
				Chunk chunk2 = iWorld.getChunk(chunkPos2.x, chunkPos2.z);
				StructureStart structureStart = chunk2.getStructureStart(string);
				if (structureStart != null && structureStart.hasChildren()) {
					for (class_3443 lv : structureStart.method_14963()) {
						if (lv.method_16654(chunkPos, 12) && lv instanceof class_3790) {
							class_3790 lv2 = (class_3790)lv;
							class_3785.Projection projection = lv2.method_16644().method_16624();
							if (projection == class_3785.Projection.RIGID) {
								objectList.add(lv2);
							}

							for (JigsawJunction jigsawJunction : lv2.method_16645()) {
								int o = jigsawJunction.getSourceX();
								int p = jigsawJunction.getSourceZ();
								if (o > l - 12 && p > m - 12 && o < l + 15 + 12 && p < m + 15 + 12) {
									objectList2.add(jigsawJunction);
								}
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
		ObjectListIterator<class_3790> objectListIterator = objectList.iterator();
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
									class_3790 lv3 = (class_3790)objectListIterator.next();
									MutableIntBoundingBox mutableIntBoundingBox = lv3.method_14935();
									int as = Math.max(0, Math.max(mutableIntBoundingBox.minX - ah, ah - mutableIntBoundingBox.maxX));
									int at = y - (mutableIntBoundingBox.minY + lv3.method_16646());
									int au = Math.max(0, Math.max(mutableIntBoundingBox.minZ - an, an - mutableIntBoundingBox.maxZ));
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
