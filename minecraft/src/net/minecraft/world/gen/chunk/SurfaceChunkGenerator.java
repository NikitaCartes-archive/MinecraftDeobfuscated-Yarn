package net.minecraft.world.gen.chunk;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.class_5284;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class SurfaceChunkGenerator<T extends class_5284> extends ChunkGenerator {
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
	private final int field_24512;
	private final int field_24513;

	public SurfaceChunkGenerator(BiomeSource biomeSource, long l, T arg, int i, int j, int k, boolean bl) {
		super(biomeSource, arg.method_28007());
		this.verticalNoiseResolution = j;
		this.horizontalNoiseResolution = i;
		this.defaultBlock = arg.method_28005();
		this.defaultFluid = arg.method_28006();
		this.noiseSizeX = 16 / this.horizontalNoiseResolution;
		this.noiseSizeY = k / this.verticalNoiseResolution;
		this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
		this.random = new ChunkRandom(l);
		this.field_16574 = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		this.field_16581 = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		this.field_16575 = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-7, 0));
		this.surfaceDepthNoise = (NoiseSampler)(bl
			? new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0))
			: new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-3, 0)));
		this.field_24512 = arg.getBedrockFloorY();
		this.field_24513 = arg.getBedrockCeilingY();
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
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		return this.sampleHeightmap(x, z, null, heightmapType.getBlockPredicate());
	}

	@Override
	public BlockView getColumnSample(int x, int z) {
		BlockState[] blockStates = new BlockState[this.noiseSizeY * this.verticalNoiseResolution];
		this.sampleHeightmap(x, z, blockStates, null);
		return new VerticalBlockSample(blockStates);
	}

	private int sampleHeightmap(int i, int j, @Nullable BlockState[] blockStates, @Nullable Predicate<BlockState> predicate) {
		int k = Math.floorDiv(i, this.horizontalNoiseResolution);
		int l = Math.floorDiv(j, this.horizontalNoiseResolution);
		int m = Math.floorMod(i, this.horizontalNoiseResolution);
		int n = Math.floorMod(j, this.horizontalNoiseResolution);
		double d = (double)m / (double)this.horizontalNoiseResolution;
		double e = (double)n / (double)this.horizontalNoiseResolution;
		double[][] ds = new double[][]{
			this.sampleNoiseColumn(k, l), this.sampleNoiseColumn(k, l + 1), this.sampleNoiseColumn(k + 1, l), this.sampleNoiseColumn(k + 1, l + 1)
		};

		for (int o = this.noiseSizeY - 1; o >= 0; o--) {
			double f = ds[0][o];
			double g = ds[1][o];
			double h = ds[2][o];
			double p = ds[3][o];
			double q = ds[0][o + 1];
			double r = ds[1][o + 1];
			double s = ds[2][o + 1];
			double t = ds[3][o + 1];

			for (int u = this.verticalNoiseResolution - 1; u >= 0; u--) {
				double v = (double)u / (double)this.verticalNoiseResolution;
				double w = MathHelper.lerp3(v, d, e, f, q, h, s, g, r, p, t);
				int x = o * this.verticalNoiseResolution + u;
				BlockState blockState = this.getBlockState(w, x);
				if (blockStates != null) {
					blockStates[x] = blockState;
				}

				if (predicate != null && predicate.test(blockState)) {
					return x + 1;
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

	protected abstract void sampleNoiseColumn(double[] buffer, int x, int z);

	public int getNoiseSizeY() {
		return this.noiseSizeY + 1;
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

	protected void buildBedrock(Chunk chunk, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = chunk.getPos().getStartX();
		int j = chunk.getPos().getStartZ();
		int k = this.field_24512;
		int l = this.field_24513;

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
	public void populateNoise(WorldAccess world, StructureAccessor structureAccessor, Chunk chunk) {
		ObjectList<StructurePiece> objectList = new ObjectArrayList<>(10);
		ObjectList<JigsawJunction> objectList2 = new ObjectArrayList<>(32);
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		int k = i << 4;
		int l = j << 4;

		for (StructureFeature<?> structureFeature : Feature.JIGSAW_STRUCTURES) {
			structureAccessor.getStructuresWithChildren(ChunkSectionPos.from(chunkPos, 0), structureFeature).forEach(structureStart -> {
				for (StructurePiece structurePiece : structureStart.getChildren()) {
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
				ChunkSection chunkSection = protoChunk.getSection(15);
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
						int u = p * this.verticalNoiseResolution + t;
						int v = u & 15;
						int w = u >> 4;
						if (chunkSection.getYOffset() >> 4 != w) {
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
									an += method_16572(ao, ap, aq) * 0.8;
								}

								objectListIterator.back(objectList.size());

								while (objectListIterator2.hasNext()) {
									JigsawJunction jigsawJunction = (JigsawJunction)objectListIterator2.next();
									int ar = ad - jigsawJunction.getSourceX();
									int ao = u - jigsawJunction.getSourceGroundY();
									int ap = aj - jigsawJunction.getSourceZ();
									an += method_16572(ar, ao, ap) * 0.4;
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
