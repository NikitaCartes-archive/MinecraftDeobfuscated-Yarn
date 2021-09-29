package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.gen.NoiseColumnSampler;

public class ChunkNoiseSampler {
	final int horizontalNoiseResolution;
	final int verticalNoiseResolution;
	final int height;
	final int horizontalSize;
	final int minimumY;
	final int x;
	final int z;
	private final int biomeX;
	private final int biomeZ;
	final List<ChunkNoiseSampler.NoiseInterpolator> interpolators;
	private final double[][] noiseX;
	private final double[][] noiseZ;
	private final double[][] continentalness;
	private final double[][] weirdness;
	private final double[][] erosion;
	private final TerrainNoisePoint[][] terrainNoisePoint;
	private final Long2ObjectMap<TerrainNoisePoint> terrainNoisePoints = new Long2ObjectOpenHashMap<>();
	private final AquiferSampler aquiferSampler;
	private final ChunkNoiseSampler.BlockStateSampler initialNoiseBlockStateSampler;
	private final ChunkNoiseSampler.BlockStateSampler oreVeinSampler;

	public ChunkNoiseSampler(
		int horizontalNoiseResolution,
		int verticalNoiseResolution,
		int horizontalSize,
		int height,
		int minimumY,
		NoiseColumnSampler noiseColumnSampler,
		int x,
		int z,
		ChunkNoiseSampler.ColumnSampler columnSampler,
		Supplier<ChunkGeneratorSettings> settings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler
	) {
		this.horizontalNoiseResolution = horizontalNoiseResolution;
		this.verticalNoiseResolution = verticalNoiseResolution;
		this.height = height;
		this.horizontalSize = horizontalSize;
		this.minimumY = minimumY;
		this.x = Math.floorDiv(x, horizontalNoiseResolution);
		this.z = Math.floorDiv(z, horizontalNoiseResolution);
		this.interpolators = Lists.<ChunkNoiseSampler.NoiseInterpolator>newArrayList();
		this.biomeX = BiomeCoords.fromBlock(x);
		this.biomeZ = BiomeCoords.fromBlock(z);
		int i = BiomeCoords.fromBlock(horizontalSize * horizontalNoiseResolution);
		this.noiseX = new double[i + 1][];
		this.noiseZ = new double[i + 1][];
		this.continentalness = new double[i + 1][];
		this.weirdness = new double[i + 1][];
		this.erosion = new double[i + 1][];
		this.terrainNoisePoint = new TerrainNoisePoint[i + 1][];

		for (int j = 0; j <= i; j++) {
			int k = this.biomeX + j;
			this.noiseX[j] = new double[i + 1];
			this.noiseZ[j] = new double[i + 1];
			this.continentalness[j] = new double[i + 1];
			this.weirdness[j] = new double[i + 1];
			this.erosion[j] = new double[i + 1];
			this.terrainNoisePoint[j] = new TerrainNoisePoint[i + 1];

			for (int l = 0; l <= i; l++) {
				int m = this.biomeZ + l;
				ChunkNoiseSampler.MultiNoisePoint multiNoisePoint = createMultiNoisePoint(noiseColumnSampler, k, m);
				this.noiseX[j][l] = multiNoisePoint.noiseX;
				this.noiseZ[j][l] = multiNoisePoint.noiseZ;
				this.continentalness[j][l] = multiNoisePoint.continentalness;
				this.weirdness[j][l] = multiNoisePoint.weirdness;
				this.erosion[j][l] = multiNoisePoint.erosion;
				this.terrainNoisePoint[j][l] = multiNoisePoint.terrainNoisePoint;
			}
		}

		this.aquiferSampler = noiseColumnSampler.createAquiferSampler(
			this, x, z, minimumY, height, fluidLevelSampler, ((ChunkGeneratorSettings)settings.get()).hasAquifers()
		);
		this.initialNoiseBlockStateSampler = noiseColumnSampler.createInitialNoiseBlockStateSampler(
			this, columnSampler, ((ChunkGeneratorSettings)settings.get()).hasNoodleCaves()
		);
		this.oreVeinSampler = noiseColumnSampler.createOreVeinSampler(this, ((ChunkGeneratorSettings)settings.get()).hasOreVeins());
	}

	@Debug
	public static ChunkNoiseSampler.MultiNoisePoint createMultiNoisePoint(NoiseColumnSampler noiseColumnSampler, int x, int z) {
		return new ChunkNoiseSampler.MultiNoisePoint(noiseColumnSampler, x, z);
	}

	public double getNoiseX(int x, int z) {
		return this.noiseX[x - this.biomeX][z - this.biomeZ];
	}

	public double getNoiseZ(int x, int z) {
		return this.noiseZ[x - this.biomeX][z - this.biomeZ];
	}

	public double getContinentalness(int x, int z) {
		return this.continentalness[x - this.biomeX][z - this.biomeZ];
	}

	public double getWeirdness(int x, int z) {
		return this.weirdness[x - this.biomeX][z - this.biomeZ];
	}

	public double getErosion(int x, int z) {
		return this.erosion[x - this.biomeX][z - this.biomeZ];
	}

	public TerrainNoisePoint getTerrainNoisePoint(int x, int z) {
		return this.terrainNoisePoint[x - this.biomeX][z - this.biomeZ];
	}

	public TerrainNoisePoint getTerrainNoisePoint(NoiseColumnSampler columnSampler, int x, int z) {
		int i = x - this.biomeX;
		int j = z - this.biomeZ;
		int k = this.terrainNoisePoint.length;
		return i >= 0 && j >= 0 && i < k && j < k
			? this.terrainNoisePoint[i][j]
			: this.terrainNoisePoints
				.computeIfAbsent(ChunkPos.toLong(x, z), pos -> createMultiNoisePoint(columnSampler, ChunkPos.getPackedX(pos), ChunkPos.getPackedZ(pos)).terrainNoisePoint);
	}

	public TerrainNoisePoint getInterpolatedTerrainNoisePoint(int x, int z) {
		int i = BiomeCoords.fromBlock(x) - this.biomeX;
		int j = BiomeCoords.fromBlock(z) - this.biomeZ;
		TerrainNoisePoint terrainNoisePoint = this.terrainNoisePoint[i][j];
		TerrainNoisePoint terrainNoisePoint2 = this.terrainNoisePoint[i][j + 1];
		TerrainNoisePoint terrainNoisePoint3 = this.terrainNoisePoint[i + 1][j];
		TerrainNoisePoint terrainNoisePoint4 = this.terrainNoisePoint[i + 1][j + 1];
		double d = (double)Math.floorMod(x, 4) / 4.0;
		double e = (double)Math.floorMod(z, 4) / 4.0;
		double f = MathHelper.lerp2(d, e, terrainNoisePoint.offset(), terrainNoisePoint3.offset(), terrainNoisePoint2.offset(), terrainNoisePoint4.offset());
		double g = MathHelper.lerp2(d, e, terrainNoisePoint.factor(), terrainNoisePoint3.factor(), terrainNoisePoint2.factor(), terrainNoisePoint4.factor());
		double h = MathHelper.lerp2(d, e, terrainNoisePoint.peaks(), terrainNoisePoint3.peaks(), terrainNoisePoint2.peaks(), terrainNoisePoint4.peaks());
		return new TerrainNoisePoint(f, g, h);
	}

	public ChunkNoiseSampler.NoiseInterpolator createNoiseInterpolator(ChunkNoiseSampler.ColumnSampler columnSampler) {
		return new ChunkNoiseSampler.NoiseInterpolator(columnSampler);
	}

	public void sampleStartNoise() {
		this.interpolators.forEach(interpolator -> interpolator.sampleStartNoise());
	}

	public void sampleEndNoise(int x) {
		this.interpolators.forEach(interpolator -> interpolator.sampleEndNoise(x));
	}

	public void sampleNoiseCorners(int noiseY, int noiseZ) {
		this.interpolators.forEach(interpolator -> interpolator.sampleNoiseCorners(noiseY, noiseZ));
	}

	public void sampleNoiseY(double deltaY) {
		this.interpolators.forEach(interpolator -> interpolator.sampleNoiseY(deltaY));
	}

	public void sampleNoiseX(double deltaX) {
		this.interpolators.forEach(interpolator -> interpolator.sampleNoiseX(deltaX));
	}

	public void sampleNoise(double deltaZ) {
		this.interpolators.forEach(interpolator -> interpolator.sampleNoise(deltaZ));
	}

	public void swapBuffers() {
		this.interpolators.forEach(ChunkNoiseSampler.NoiseInterpolator::swapBuffers);
	}

	public AquiferSampler getAquiferSampler() {
		return this.aquiferSampler;
	}

	@Nullable
	protected BlockState sampleInitialNoiseBlockState(int x, int y, int z) {
		return this.initialNoiseBlockStateSampler.sample(x, y, z);
	}

	@Nullable
	protected BlockState sampleOreVeins(int x, int y, int z) {
		return this.oreVeinSampler.sample(x, y, z);
	}

	@FunctionalInterface
	public interface BlockStateSampler {
		@Nullable
		BlockState sample(int x, int y, int z);
	}

	@FunctionalInterface
	public interface ColumnSampler {
		double calculateNoise(int x, int y, int z);
	}

	public static final class MultiNoisePoint {
		final double noiseX;
		final double noiseZ;
		final double continentalness;
		final double weirdness;
		final double erosion;
		@Debug
		public final TerrainNoisePoint terrainNoisePoint;

		MultiNoisePoint(NoiseColumnSampler noiseColumnSampler, int x, int z) {
			this.noiseX = (double)x + noiseColumnSampler.sampleShiftNoise(x, 0, z);
			this.noiseZ = (double)z + noiseColumnSampler.sampleShiftNoise(z, x, 0);
			this.continentalness = noiseColumnSampler.sampleContinentalnessNoise(this.noiseX, 0.0, this.noiseZ);
			this.weirdness = noiseColumnSampler.sampleWeirdnessNoise(this.noiseX, 0.0, this.noiseZ);
			this.erosion = noiseColumnSampler.sampleErosionNoise(this.noiseX, 0.0, this.noiseZ);
			this.terrainNoisePoint = noiseColumnSampler.createTerrainNoisePoint(
				BiomeCoords.toBlock(x), BiomeCoords.toBlock(z), (float)this.continentalness, (float)this.weirdness, (float)this.erosion
			);
		}
	}

	public class NoiseInterpolator implements ChunkNoiseSampler.ValueSampler {
		private double[][] startNoiseBuffer;
		private double[][] endNoiseBuffer;
		private final ChunkNoiseSampler.ColumnSampler columnSampler;
		private double x0y0z0;
		private double x0y0z1;
		private double x1y0z0;
		private double x1y0z1;
		private double x0y1z0;
		private double x0y1z1;
		private double x1y1z0;
		private double x1y1z1;
		private double x0z0;
		private double x1z0;
		private double x0z1;
		private double x1z1;
		private double z0;
		private double z1;
		private double result;

		NoiseInterpolator(ChunkNoiseSampler.ColumnSampler columnSampler) {
			this.columnSampler = columnSampler;
			this.startNoiseBuffer = this.createBuffer(ChunkNoiseSampler.this.height, ChunkNoiseSampler.this.horizontalSize);
			this.endNoiseBuffer = this.createBuffer(ChunkNoiseSampler.this.height, ChunkNoiseSampler.this.horizontalSize);
			ChunkNoiseSampler.this.interpolators.add(this);
		}

		private double[][] createBuffer(int sizeZ, int sizeX) {
			int i = sizeX + 1;
			int j = sizeZ + 1;
			double[][] ds = new double[i][j];

			for (int k = 0; k < i; k++) {
				ds[k] = new double[j];
			}

			return ds;
		}

		void sampleStartNoise() {
			this.sampleNoise(this.startNoiseBuffer, ChunkNoiseSampler.this.x);
		}

		void sampleEndNoise(int x) {
			this.sampleNoise(this.endNoiseBuffer, ChunkNoiseSampler.this.x + x + 1);
		}

		private void sampleNoise(double[][] buffer, int noiseX) {
			for (int i = 0; i < ChunkNoiseSampler.this.horizontalSize + 1; i++) {
				int j = ChunkNoiseSampler.this.z + i;

				for (int k = 0; k < ChunkNoiseSampler.this.height + 1; k++) {
					int l = k + ChunkNoiseSampler.this.minimumY;
					int m = l * ChunkNoiseSampler.this.verticalNoiseResolution;
					double d = this.columnSampler
						.calculateNoise(noiseX * ChunkNoiseSampler.this.horizontalNoiseResolution, m, j * ChunkNoiseSampler.this.horizontalNoiseResolution);
					buffer[i][k] = d;
				}
			}
		}

		void sampleNoiseCorners(int noiseY, int noiseZ) {
			this.x0y0z0 = this.startNoiseBuffer[noiseZ][noiseY];
			this.x0y0z1 = this.startNoiseBuffer[noiseZ + 1][noiseY];
			this.x1y0z0 = this.endNoiseBuffer[noiseZ][noiseY];
			this.x1y0z1 = this.endNoiseBuffer[noiseZ + 1][noiseY];
			this.x0y1z0 = this.startNoiseBuffer[noiseZ][noiseY + 1];
			this.x0y1z1 = this.startNoiseBuffer[noiseZ + 1][noiseY + 1];
			this.x1y1z0 = this.endNoiseBuffer[noiseZ][noiseY + 1];
			this.x1y1z1 = this.endNoiseBuffer[noiseZ + 1][noiseY + 1];
		}

		void sampleNoiseY(double deltaY) {
			this.x0z0 = MathHelper.lerp(deltaY, this.x0y0z0, this.x0y1z0);
			this.x1z0 = MathHelper.lerp(deltaY, this.x1y0z0, this.x1y1z0);
			this.x0z1 = MathHelper.lerp(deltaY, this.x0y0z1, this.x0y1z1);
			this.x1z1 = MathHelper.lerp(deltaY, this.x1y0z1, this.x1y1z1);
		}

		void sampleNoiseX(double deltaX) {
			this.z0 = MathHelper.lerp(deltaX, this.x0z0, this.x1z0);
			this.z1 = MathHelper.lerp(deltaX, this.x0z1, this.x1z1);
		}

		void sampleNoise(double deltaZ) {
			this.result = MathHelper.lerp(deltaZ, this.z0, this.z1);
		}

		@Override
		public double sample() {
			return this.result;
		}

		private void swapBuffers() {
			double[][] ds = this.startNoiseBuffer;
			this.startNoiseBuffer = this.endNoiseBuffer;
			this.endNoiseBuffer = ds;
		}
	}

	@FunctionalInterface
	public interface ValueSampler {
		double sample();
	}

	@FunctionalInterface
	public interface ValueSamplerFactory {
		ChunkNoiseSampler.ValueSampler create(ChunkNoiseSampler chunkNoiseSampler);
	}
}
