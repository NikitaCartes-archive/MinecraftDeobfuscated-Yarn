package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.StructureFeature;

public final class SurfaceChunkGenerator extends ChunkGenerator {
	public static final Codec<SurfaceChunkGenerator> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BiomeSource.field_24713.fieldOf("biome_source").forGetter(surfaceChunkGenerator -> surfaceChunkGenerator.biomeSource),
					Codec.LONG.fieldOf("seed").stable().forGetter(surfaceChunkGenerator -> surfaceChunkGenerator.worldSeed),
					ChunkGeneratorType.field_24781.fieldOf("settings").forGetter(surfaceChunkGenerator -> surfaceChunkGenerator.field_24774)
				)
				.apply(instance, instance.stable(SurfaceChunkGenerator::new))
	);
	private static final float[] field_16649 = Util.make(new float[13824], array -> {
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				for (int k = 0; k < 24; k++) {
					array[i * 24 * 24 + j * 24 + k] = (float)method_16571(j - 12, k - 12, i - 12);
				}
			}
		}
	});
	private static final float[] field_24775 = Util.make(new float[25], fs -> {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
				fs[i + 2 + (j + 2) * 5] = f;
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
	private final OctavePerlinNoiseSampler field_24776;
	@Nullable
	private final SimplexNoiseSampler field_24777;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;
	private final long worldSeed;
	protected final ChunkGeneratorType field_24774;
	private final int field_24779;

	public SurfaceChunkGenerator(BiomeSource biomeSource, long l, ChunkGeneratorType chunkGeneratorType) {
		this(biomeSource, biomeSource, l, chunkGeneratorType);
	}

	private SurfaceChunkGenerator(BiomeSource biomeSource, BiomeSource biomeSource2, long worldSeed, ChunkGeneratorType chunkGeneratorType) {
		super(biomeSource, biomeSource2, chunkGeneratorType.getConfig(), worldSeed);
		this.worldSeed = worldSeed;
		this.field_24774 = chunkGeneratorType;
		NoiseConfig noiseConfig = chunkGeneratorType.method_28559();
		this.field_24779 = noiseConfig.getHeight();
		this.verticalNoiseResolution = noiseConfig.getSizeVertical() * 4;
		this.horizontalNoiseResolution = noiseConfig.getSizeHorizontal() * 4;
		this.defaultBlock = chunkGeneratorType.getDefaultBlock();
		this.defaultFluid = chunkGeneratorType.getDefaultFluid();
		this.noiseSizeX = 16 / this.horizontalNoiseResolution;
		this.noiseSizeY = noiseConfig.getHeight() / this.verticalNoiseResolution;
		this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
		this.random = new ChunkRandom(worldSeed);
		this.lowerInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		this.upperInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		this.interpolationNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-7, 0));
		this.surfaceDepthNoise = (NoiseSampler)(noiseConfig.hasSimplexSurfaceNoise()
			? new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0))
			: new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-3, 0)));
		this.random.consume(2620);
		this.field_24776 = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
		if (noiseConfig.hasIslandNoiseOverride()) {
			ChunkRandom chunkRandom = new ChunkRandom(worldSeed);
			chunkRandom.consume(17292);
			this.field_24777 = new SimplexNoiseSampler(chunkRandom);
		} else {
			this.field_24777 = null;
		}
	}

	@Override
	protected Codec<? extends ChunkGenerator> method_28506() {
		return CODEC;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator withSeed(long seed) {
		return new SurfaceChunkGenerator(this.biomeSource.withSeed(seed), seed, this.field_24774);
	}

	public boolean method_28548(long l, ChunkGeneratorType.Preset preset) {
		return this.worldSeed == l && this.field_24774.method_28555(preset);
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
		NoiseConfig noiseConfig = this.field_24774.method_28559();
		double d;
		double e;
		if (this.field_24777 != null) {
			d = (double)(TheEndBiomeSource.getNoiseAt(this.field_24777, x, z) - 8.0F);
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
			float k = this.biomeSource.getBiomeForNoiseGen(x, j, z).getDepth();

			for (int l = -2; l <= 2; l++) {
				for (int m = -2; m <= 2; m++) {
					Biome biome = this.biomeSource.getBiomeForNoiseGen(x + l, j, z + m);
					float n = biome.getDepth();
					float o = biome.getScale();
					float p;
					float q;
					if (noiseConfig.isAmplified() && n > 0.0F) {
						p = 1.0F + n * 2.0F;
						q = 1.0F + o * 4.0F;
					} else {
						p = n;
						q = o;
					}

					float r = n > k ? 0.5F : 1.0F;
					float s = r * field_24775[l + 2 + (m + 2) * 5] / (p + 2.0F);
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

		double y = 684.412 * noiseConfig.getSampling().getXZScale();
		double aa = 684.412 * noiseConfig.getSampling().getYScale();
		double ab = y / noiseConfig.getSampling().getXZFactor();
		double ac = aa / noiseConfig.getSampling().getYFactor();
		double v = (double)noiseConfig.getTopSlide().getTarget();
		double w = (double)noiseConfig.getTopSlide().getSize();
		double ad = (double)noiseConfig.getTopSlide().getOffset();
		double ae = (double)noiseConfig.getBottomSlide().getTarget();
		double af = (double)noiseConfig.getBottomSlide().getSize();
		double ag = (double)noiseConfig.getBottomSlide().getOffset();
		double ah = noiseConfig.hasRandomDensityOffset() ? this.method_28553(x, z) : 0.0;
		double ai = noiseConfig.getDensityFactor();
		double aj = noiseConfig.getDensityOffset();

		for (int ak = 0; ak <= this.noiseSizeY; ak++) {
			double al = this.sampleNoise(x, ak, z, y, aa, ab, ac);
			double am = 1.0 - (double)ak * 2.0 / (double)this.noiseSizeY + ah;
			double an = am * ai + aj;
			double ao = (an + d) * e;
			if (ao > 0.0) {
				al += ao * 4.0;
			} else {
				al += ao;
			}

			if (w > 0.0) {
				double ap = ((double)(this.noiseSizeY - ak) - ad) / w;
				al = MathHelper.clampedLerp(v, al, ap);
			}

			if (af > 0.0) {
				double ap = ((double)ak - ag) / af;
				al = MathHelper.clampedLerp(ae, al, ap);
			}

			buffer[ak] = al;
		}
	}

	private double method_28553(int i, int j) {
		double d = this.field_24776.sample((double)(i * 200), 10.0, (double)(j * 200), 1.0, 0.0, true);
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
	public BlockView getColumnSample(int x, int z) {
		BlockState[] blockStates = new BlockState[this.noiseSizeY * this.verticalNoiseResolution];
		this.sampleHeightmap(x, z, blockStates, null);
		return new VerticalBlockSample(blockStates);
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
				BlockState blockState = this.getBlockState(u, v);
				if (states != null) {
					states[v] = blockState;
				}

				if (predicate != null && predicate.test(blockState)) {
					return v + 1;
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
		int k = this.field_24774.getBedrockFloorY();
		int l = this.field_24779 - 1 - this.field_24774.getBedrockCeilingY();
		int m = 5;
		boolean bl = l + 4 >= 0 && l < this.field_24779;
		boolean bl2 = k + 4 >= 0 && k < this.field_24779;
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
		int k = i << 4;
		int l = j << 4;

		for (StructureFeature<?> structureFeature : StructureFeature.field_24861) {
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

	@Override
	public int getMaxY() {
		return this.field_24779;
	}

	@Override
	public int getSeaLevel() {
		return this.field_24774.method_28561();
	}

	@Override
	public List<Biome.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
		if (accessor.method_28388(pos, true, StructureFeature.SWAMP_HUT).hasChildren()) {
			if (group == SpawnGroup.MONSTER) {
				return StructureFeature.SWAMP_HUT.getMonsterSpawns();
			}

			if (group == SpawnGroup.CREATURE) {
				return StructureFeature.SWAMP_HUT.getCreatureSpawns();
			}
		}

		if (group == SpawnGroup.MONSTER) {
			if (accessor.method_28388(pos, false, StructureFeature.PILLAGER_OUTPOST).hasChildren()) {
				return StructureFeature.PILLAGER_OUTPOST.getMonsterSpawns();
			}

			if (accessor.method_28388(pos, false, StructureFeature.MONUMENT).hasChildren()) {
				return StructureFeature.MONUMENT.getMonsterSpawns();
			}

			if (accessor.method_28388(pos, true, StructureFeature.FORTRESS).hasChildren()) {
				return StructureFeature.FORTRESS.getMonsterSpawns();
			}
		}

		return super.getEntitySpawnList(biome, accessor, group, pos);
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		if (!this.field_24774.method_28562()) {
			int i = region.getCenterChunkX();
			int j = region.getCenterChunkZ();
			Biome biome = region.getBiome(new ChunkPos(i, j).getCenterBlockPos());
			ChunkRandom chunkRandom = new ChunkRandom();
			chunkRandom.setPopulationSeed(region.getSeed(), i << 4, j << 4);
			SpawnHelper.populateEntities(region, biome, i, j, chunkRandom);
		}
	}
}
