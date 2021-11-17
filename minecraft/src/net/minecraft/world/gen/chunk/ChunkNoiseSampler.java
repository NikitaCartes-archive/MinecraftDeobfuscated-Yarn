package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.NoiseColumnSampler;

public class ChunkNoiseSampler {
	final GenerationShapeConfig field_35674;
	final int horizontalSize;
	final int height;
	final int minimumY;
	final int x;
	final int z;
	private final int biomeX;
	private final int biomeZ;
	final List<ChunkNoiseSampler.NoiseInterpolator> interpolators;
	private final NoiseColumnSampler.class_6747[][] field_35486;
	private final Long2ObjectMap<TerrainNoisePoint> terrainNoisePoints = new Long2ObjectOpenHashMap<>();
	private final AquiferSampler aquiferSampler;
	private final ChunkNoiseSampler.BlockStateSampler initialNoiseBlockStateSampler;
	private final ChunkNoiseSampler.BlockStateSampler oreVeinSampler;
	private final Blender field_35487;

	public static ChunkNoiseSampler method_39543(
		Chunk chunk,
		NoiseColumnSampler noiseColumnSampler,
		Supplier<ChunkNoiseSampler.ColumnSampler> supplier,
		ChunkGeneratorSettings chunkGeneratorSettings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler,
		Blender blender
	) {
		ChunkPos chunkPos = chunk.getPos();
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
		int i = Math.max(generationShapeConfig.minimumY(), chunk.getBottomY());
		int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), chunk.getTopY());
		int k = MathHelper.floorDiv(i, generationShapeConfig.method_39545());
		int l = MathHelper.floorDiv(j - i, generationShapeConfig.method_39545());
		return new ChunkNoiseSampler(
			16 / generationShapeConfig.method_39546(),
			l,
			k,
			noiseColumnSampler,
			chunkPos.getStartX(),
			chunkPos.getStartZ(),
			(ChunkNoiseSampler.ColumnSampler)supplier.get(),
			chunkGeneratorSettings,
			fluidLevelSampler,
			blender
		);
	}

	public static ChunkNoiseSampler method_39542(
		int i,
		int j,
		int k,
		int l,
		NoiseColumnSampler noiseColumnSampler,
		ChunkGeneratorSettings chunkGeneratorSettings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler
	) {
		return new ChunkNoiseSampler(1, l, k, noiseColumnSampler, i, j, (ix, jx, kx) -> 0.0, chunkGeneratorSettings, fluidLevelSampler, Blender.getNoBlending());
	}

	private ChunkNoiseSampler(
		int horizontalNoiseResolution,
		int verticalNoiseResolution,
		int horizontalSize,
		NoiseColumnSampler noiseColumnSampler,
		int minimumY,
		int i,
		ChunkNoiseSampler.ColumnSampler columnSampler,
		ChunkGeneratorSettings chunkGeneratorSettings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler,
		Blender blender
	) {
		this.field_35674 = chunkGeneratorSettings.getGenerationShapeConfig();
		this.horizontalSize = horizontalNoiseResolution;
		this.height = verticalNoiseResolution;
		this.minimumY = horizontalSize;
		int j = this.field_35674.method_39546();
		this.x = Math.floorDiv(minimumY, j);
		this.z = Math.floorDiv(i, j);
		this.interpolators = Lists.<ChunkNoiseSampler.NoiseInterpolator>newArrayList();
		this.biomeX = BiomeCoords.fromBlock(minimumY);
		this.biomeZ = BiomeCoords.fromBlock(i);
		int k = BiomeCoords.fromBlock(horizontalNoiseResolution * j);
		this.field_35486 = new NoiseColumnSampler.class_6747[k + 1][];
		this.field_35487 = blender;

		for (int l = 0; l <= k; l++) {
			int m = this.biomeX + l;
			this.field_35486[l] = new NoiseColumnSampler.class_6747[k + 1];

			for (int n = 0; n <= k; n++) {
				int o = this.biomeZ + n;
				this.field_35486[l][n] = noiseColumnSampler.method_39330(m, o, blender);
			}
		}

		this.aquiferSampler = noiseColumnSampler.createAquiferSampler(
			this, minimumY, i, horizontalSize, verticalNoiseResolution, fluidLevelSampler, chunkGeneratorSettings.hasAquifers()
		);
		this.initialNoiseBlockStateSampler = noiseColumnSampler.createInitialNoiseBlockStateSampler(this, columnSampler, chunkGeneratorSettings.hasNoodleCaves());
		this.oreVeinSampler = noiseColumnSampler.createOreVeinSampler(this, chunkGeneratorSettings.hasOreVeins());
	}

	public NoiseColumnSampler.class_6747 createMultiNoisePoint(int x, int z) {
		return this.field_35486[x - this.biomeX][z - this.biomeZ];
	}

	public TerrainNoisePoint getTerrainNoisePoint(NoiseColumnSampler noiseColumnSampler, int x, int z) {
		int i = x - this.biomeX;
		int j = z - this.biomeZ;
		int k = this.field_35486.length;
		return i >= 0 && j >= 0 && i < k && j < k
			? this.field_35486[i][j].terrainInfo()
			: this.terrainNoisePoints
				.computeIfAbsent(
					ChunkPos.toLong(x, z),
					(Long2ObjectFunction<? extends TerrainNoisePoint>)(l -> noiseColumnSampler.method_39330(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l), this.field_35487)
							.terrainInfo())
				);
	}

	public TerrainNoisePoint getInterpolatedTerrainNoisePoint(int x, int z) {
		int i = BiomeCoords.fromBlock(x) - this.biomeX;
		int j = BiomeCoords.fromBlock(z) - this.biomeZ;
		TerrainNoisePoint terrainNoisePoint = this.field_35486[i][j].terrainInfo();
		TerrainNoisePoint terrainNoisePoint2 = this.field_35486[i][j + 1].terrainInfo();
		TerrainNoisePoint terrainNoisePoint3 = this.field_35486[i + 1][j].terrainInfo();
		TerrainNoisePoint terrainNoisePoint4 = this.field_35486[i + 1][j + 1].terrainInfo();
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

	public Blender method_39327() {
		return this.field_35487;
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
			int i = ChunkNoiseSampler.this.field_35674.method_39546();
			int j = ChunkNoiseSampler.this.field_35674.method_39545();

			for (int k = 0; k < ChunkNoiseSampler.this.horizontalSize + 1; k++) {
				int l = ChunkNoiseSampler.this.z + k;

				for (int m = 0; m < ChunkNoiseSampler.this.height + 1; m++) {
					int n = m + ChunkNoiseSampler.this.minimumY;
					int o = n * j;
					double d = this.columnSampler.calculateNoise(noiseX * i, o, l * i);
					buffer[k][m] = d;
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
