package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChainedBlockSource;
import net.minecraft.world.gen.OreVeinSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseRouter;

/**
 * {@code ChunkNoiseSampler} is responsible for sampling the density functions for
 * the cells in each chunk, managing caches and interpolators, the {@code Blender},
 * and the block state samplers.
 * 
 * <p>An instance of this is created for every chunk.
 */
public class ChunkNoiseSampler implements DensityFunction.EachApplier, DensityFunction.NoisePos {
	private final GenerationShapeConfig generationShapeConfig;
	final int horizontalCellCount;
	final int verticalCellCount;
	final int minimumCellY;
	private final int startCellX;
	private final int startCellZ;
	final int startBiomeX;
	final int startBiomeZ;
	final List<ChunkNoiseSampler.DensityInterpolator> interpolators;
	final List<ChunkNoiseSampler.CellCache> caches;
	private final Map<DensityFunction, DensityFunction> actualDensityFunctionCache = new HashMap();
	private final Long2IntMap surfaceHeightEstimateCache = new Long2IntOpenHashMap();
	private final AquiferSampler aquiferSampler;
	private final DensityFunction initialDensityWithoutJaggedness;
	private final ChunkNoiseSampler.BlockStateSampler blockStateSampler;
	private final Blender blender;
	private final ChunkNoiseSampler.FlatCache cachedBlendAlphaDensityFunction;
	private final ChunkNoiseSampler.FlatCache cachedBlendOffsetDensityFunction;
	private final DensityFunctionTypes.Beardifying beardifying;
	private long lastBlendingColumnPos = ChunkPos.MARKER;
	private Blender.BlendResult lastBlendingResult = new Blender.BlendResult(1.0, 0.0);
	final int horizontalBiomeEnd;
	final int horizontalCellBlockCount;
	final int verticalCellBlockCount;
	boolean isInInterpolationLoop;
	boolean isSamplingForCaches;
	private int startBlockX;
	int startBlockY;
	private int startBlockZ;
	int cellBlockX;
	int cellBlockY;
	int cellBlockZ;
	long sampleUniqueIndex;
	long cacheOnceUniqueIndex;
	int index;
	/**
	 * The implementation of {@code EachApplier} that is used for filling the
	 * interpolators' density buffers.
	 * 
	 * <p>It runs on a vertical column of cells, with the density being sampled at the first
	 * block in the cell (cell-local coordinate {@code 0} for X, Y and Z).
	 */
	private final DensityFunction.EachApplier interpolationEachApplier = new DensityFunction.EachApplier() {
		@Override
		public DensityFunction.NoisePos at(int index) {
			ChunkNoiseSampler.this.startBlockY = (index + ChunkNoiseSampler.this.minimumCellY) * ChunkNoiseSampler.this.verticalCellBlockCount;
			ChunkNoiseSampler.this.sampleUniqueIndex++;
			ChunkNoiseSampler.this.cellBlockY = 0;
			ChunkNoiseSampler.this.index = index;
			return ChunkNoiseSampler.this;
		}

		@Override
		public void fill(double[] densities, DensityFunction densityFunction) {
			for (int i = 0; i < ChunkNoiseSampler.this.verticalCellCount + 1; i++) {
				ChunkNoiseSampler.this.startBlockY = (i + ChunkNoiseSampler.this.minimumCellY) * ChunkNoiseSampler.this.verticalCellBlockCount;
				ChunkNoiseSampler.this.sampleUniqueIndex++;
				ChunkNoiseSampler.this.cellBlockY = 0;
				ChunkNoiseSampler.this.index = i;
				densities[i] = densityFunction.sample(ChunkNoiseSampler.this);
			}
		}
	};

	public static ChunkNoiseSampler create(
		Chunk chunk,
		NoiseConfig noiseConfig,
		DensityFunctionTypes.Beardifying beardifying,
		ChunkGeneratorSettings chunkGeneratorSettings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler,
		Blender blender
	) {
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.generationShapeConfig().trimHeight(chunk);
		ChunkPos chunkPos = chunk.getPos();
		int i = 16 / generationShapeConfig.horizontalCellBlockCount();
		return new ChunkNoiseSampler(
			i, noiseConfig, chunkPos.getStartX(), chunkPos.getStartZ(), generationShapeConfig, beardifying, chunkGeneratorSettings, fluidLevelSampler, blender
		);
	}

	public ChunkNoiseSampler(
		int horizontalCellCount,
		NoiseConfig noiseConfig,
		int startBlockX,
		int startBlockZ,
		GenerationShapeConfig generationShapeConfig,
		DensityFunctionTypes.Beardifying beardifying,
		ChunkGeneratorSettings chunkGeneratorSettings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler,
		Blender blender
	) {
		this.generationShapeConfig = generationShapeConfig;
		this.horizontalCellBlockCount = generationShapeConfig.horizontalCellBlockCount();
		this.verticalCellBlockCount = generationShapeConfig.verticalCellBlockCount();
		this.horizontalCellCount = horizontalCellCount;
		this.verticalCellCount = MathHelper.floorDiv(generationShapeConfig.height(), this.verticalCellBlockCount);
		this.minimumCellY = MathHelper.floorDiv(generationShapeConfig.minimumY(), this.verticalCellBlockCount);
		this.startCellX = Math.floorDiv(startBlockX, this.horizontalCellBlockCount);
		this.startCellZ = Math.floorDiv(startBlockZ, this.horizontalCellBlockCount);
		this.interpolators = Lists.<ChunkNoiseSampler.DensityInterpolator>newArrayList();
		this.caches = Lists.<ChunkNoiseSampler.CellCache>newArrayList();
		this.startBiomeX = BiomeCoords.fromBlock(startBlockX);
		this.startBiomeZ = BiomeCoords.fromBlock(startBlockZ);
		this.horizontalBiomeEnd = BiomeCoords.fromBlock(horizontalCellCount * this.horizontalCellBlockCount);
		this.blender = blender;
		this.beardifying = beardifying;
		this.cachedBlendAlphaDensityFunction = new ChunkNoiseSampler.FlatCache(new ChunkNoiseSampler.BlendAlphaDensityFunction(), false);
		this.cachedBlendOffsetDensityFunction = new ChunkNoiseSampler.FlatCache(new ChunkNoiseSampler.BlendOffsetDensityFunction(), false);

		for (int i = 0; i <= this.horizontalBiomeEnd; i++) {
			int j = this.startBiomeX + i;
			int k = BiomeCoords.toBlock(j);

			for (int l = 0; l <= this.horizontalBiomeEnd; l++) {
				int m = this.startBiomeZ + l;
				int n = BiomeCoords.toBlock(m);
				Blender.BlendResult blendResult = blender.calculate(k, n);
				this.cachedBlendAlphaDensityFunction.cache[i][l] = blendResult.alpha();
				this.cachedBlendOffsetDensityFunction.cache[i][l] = blendResult.blendingOffset();
			}
		}

		NoiseRouter noiseRouter = noiseConfig.getNoiseRouter();
		NoiseRouter noiseRouter2 = noiseRouter.apply(this::getActualDensityFunction);
		if (!chunkGeneratorSettings.hasAquifers()) {
			this.aquiferSampler = AquiferSampler.seaLevel(fluidLevelSampler);
		} else {
			int k = ChunkSectionPos.getSectionCoord(startBlockX);
			int l = ChunkSectionPos.getSectionCoord(startBlockZ);
			this.aquiferSampler = AquiferSampler.aquifer(
				this,
				new ChunkPos(k, l),
				noiseRouter2,
				noiseConfig.getAquiferRandomDeriver(),
				generationShapeConfig.minimumY(),
				generationShapeConfig.height(),
				fluidLevelSampler
			);
		}

		List<ChunkNoiseSampler.BlockStateSampler> list = new ArrayList();
		DensityFunction densityFunction = DensityFunctionTypes.cacheAllInCell(
				DensityFunctionTypes.add(noiseRouter2.finalDensity(), DensityFunctionTypes.Beardifier.INSTANCE)
			)
			.apply(this::getActualDensityFunction);
		list.add((ChunkNoiseSampler.BlockStateSampler)pos -> this.aquiferSampler.apply(pos, densityFunction.sample(pos)));
		if (chunkGeneratorSettings.oreVeins()) {
			list.add(OreVeinSampler.create(noiseRouter2.veinToggle(), noiseRouter2.veinRidged(), noiseRouter2.veinGap(), noiseConfig.getOreRandomDeriver()));
		}

		this.blockStateSampler = new ChainedBlockSource((ChunkNoiseSampler.BlockStateSampler[])list.toArray(new ChunkNoiseSampler.BlockStateSampler[0]));
		this.initialDensityWithoutJaggedness = noiseRouter2.initialDensityWithoutJaggedness();
	}

	protected MultiNoiseUtil.MultiNoiseSampler createMultiNoiseSampler(NoiseRouter noiseRouter, List<MultiNoiseUtil.NoiseHypercube> spawnTarget) {
		return new MultiNoiseUtil.MultiNoiseSampler(
			noiseRouter.temperature().apply(this::getActualDensityFunction),
			noiseRouter.vegetation().apply(this::getActualDensityFunction),
			noiseRouter.continents().apply(this::getActualDensityFunction),
			noiseRouter.erosion().apply(this::getActualDensityFunction),
			noiseRouter.depth().apply(this::getActualDensityFunction),
			noiseRouter.ridges().apply(this::getActualDensityFunction),
			spawnTarget
		);
	}

	@Nullable
	protected BlockState sampleBlockState() {
		return this.blockStateSampler.sample(this);
	}

	@Override
	public int blockX() {
		return this.startBlockX + this.cellBlockX;
	}

	@Override
	public int blockY() {
		return this.startBlockY + this.cellBlockY;
	}

	@Override
	public int blockZ() {
		return this.startBlockZ + this.cellBlockZ;
	}

	public int estimateSurfaceHeight(int blockX, int blockZ) {
		int i = BiomeCoords.toBlock(BiomeCoords.fromBlock(blockX));
		int j = BiomeCoords.toBlock(BiomeCoords.fromBlock(blockZ));
		return this.surfaceHeightEstimateCache.computeIfAbsent(ColumnPos.pack(i, j), this::calculateSurfaceHeightEstimate);
	}

	private int calculateSurfaceHeightEstimate(long columnPos) {
		int i = ColumnPos.getX(columnPos);
		int j = ColumnPos.getZ(columnPos);
		int k = this.generationShapeConfig.minimumY();

		for (int l = k + this.generationShapeConfig.height(); l >= k; l -= this.verticalCellBlockCount) {
			if (this.initialDensityWithoutJaggedness.sample(new DensityFunction.UnblendedNoisePos(i, l, j)) > 0.390625) {
				return l;
			}
		}

		return Integer.MAX_VALUE;
	}

	@Override
	public Blender getBlender() {
		return this.blender;
	}

	/**
	 * Samples the density values for all cells in the chunk with the given X coordinate.
	 * This is done for every {@code interpolated} function.
	 * 
	 * @param cellX the current cell X coordinate
	 * @param start whether to store the results in the start or end density buffer
	 */
	private void sampleDensity(boolean start, int cellX) {
		this.startBlockX = cellX * this.horizontalCellBlockCount;
		this.cellBlockX = 0;

		for (int i = 0; i < this.horizontalCellCount + 1; i++) {
			int j = this.startCellZ + i;
			this.startBlockZ = j * this.horizontalCellBlockCount;
			this.cellBlockZ = 0;
			this.cacheOnceUniqueIndex++;

			for (ChunkNoiseSampler.DensityInterpolator densityInterpolator : this.interpolators) {
				double[] ds = (start ? densityInterpolator.startDensityBuffer : densityInterpolator.endDensityBuffer)[i];
				densityInterpolator.fill(ds, this.interpolationEachApplier);
			}
		}

		this.cacheOnceUniqueIndex++;
	}

	/**
	 * Samples the density values for all cells in the chunk with the first X coordinate.
	 * This is done for every {@code interpolated} function. The resulting density values
	 * will be stored in the interpolator's start density buffer.
	 */
	public void sampleStartDensity() {
		if (this.isInInterpolationLoop) {
			throw new IllegalStateException("Staring interpolation twice");
		} else {
			this.isInInterpolationLoop = true;
			this.sampleUniqueIndex = 0L;
			this.sampleDensity(true, this.startCellX);
		}
	}

	/**
	 * Samples the density values for all cells in the chunk with the given X coordinate.
	 * This is done for every {@code interpolated} function. The resulting density values
	 * will be stored in the interpolator's end density buffer.
	 * 
	 * @param cellX the chunk-local cell X coordinate
	 */
	public void sampleEndDensity(int cellX) {
		this.sampleDensity(false, this.startCellX + cellX + 1);
		this.startBlockX = (this.startCellX + cellX) * this.horizontalCellBlockCount;
	}

	public ChunkNoiseSampler at(int i) {
		int j = Math.floorMod(i, this.horizontalCellBlockCount);
		int k = Math.floorDiv(i, this.horizontalCellBlockCount);
		int l = Math.floorMod(k, this.horizontalCellBlockCount);
		int m = this.verticalCellBlockCount - 1 - Math.floorDiv(k, this.horizontalCellBlockCount);
		this.cellBlockX = l;
		this.cellBlockY = m;
		this.cellBlockZ = j;
		this.index = i;
		return this;
	}

	@Override
	public void fill(double[] densities, DensityFunction densityFunction) {
		this.index = 0;

		for (int i = this.verticalCellBlockCount - 1; i >= 0; i--) {
			this.cellBlockY = i;

			for (int j = 0; j < this.horizontalCellBlockCount; j++) {
				this.cellBlockX = j;

				for (int k = 0; k < this.horizontalCellBlockCount; k++) {
					this.cellBlockZ = k;
					densities[this.index++] = densityFunction.sample(this);
				}
			}
		}
	}

	/**
	 * This should be called when the start and end density buffers are
	 * correctly filled for the current cell.
	 * 
	 * <p>This starts the interpolators and fills the cell caches.
	 * 
	 * @implNote For filling the cell caches, {@code this} is used as the
	 * {@link net.minecraft.world.gen.densityfunction.DensityFunction.EachApplier EachApplier}.
	 * 
	 * @param cellZ the chunk-local cell Z coordinate
	 * @param cellY the chunk-local cell Y coordinate
	 */
	public void onSampledCellCorners(int cellY, int cellZ) {
		for (ChunkNoiseSampler.DensityInterpolator densityInterpolator : this.interpolators) {
			densityInterpolator.onSampledCellCorners(cellY, cellZ);
		}

		this.isSamplingForCaches = true;
		this.startBlockY = (cellY + this.minimumCellY) * this.verticalCellBlockCount;
		this.startBlockZ = (this.startCellZ + cellZ) * this.horizontalCellBlockCount;
		this.cacheOnceUniqueIndex++;

		for (ChunkNoiseSampler.CellCache cellCache : this.caches) {
			cellCache.delegate.fill(cellCache.cache, this);
		}

		this.cacheOnceUniqueIndex++;
		this.isSamplingForCaches = false;
	}

	/**
	 * Interpolates density values on the Y axis for every interpolator.
	 * 
	 * @param blockY the absolute block Y coordinate
	 * @param deltaY the cell-local block Y coordinate divided by the number of blocks in a cell vertically
	 */
	public void interpolateY(int blockY, double deltaY) {
		this.cellBlockY = blockY - this.startBlockY;

		for (ChunkNoiseSampler.DensityInterpolator densityInterpolator : this.interpolators) {
			densityInterpolator.interpolateY(deltaY);
		}
	}

	/**
	 * Interpolates density values on the X axis for every interpolator.
	 * 
	 * @param deltaX the cell-local block X coordinate divided by the number of blocks in a cell horizontally
	 * @param blockX the absolute block X coordinate
	 */
	public void interpolateX(int blockX, double deltaX) {
		this.cellBlockX = blockX - this.startBlockX;

		for (ChunkNoiseSampler.DensityInterpolator densityInterpolator : this.interpolators) {
			densityInterpolator.interpolateX(deltaX);
		}
	}

	/**
	 * Interpolates density values on the Z axis for every interpolator.
	 * 
	 * @param blockZ the absolute block Z coordinate
	 * @param deltaZ the cell-local block Z coordinate divided by the number of blocks in a cell vertically
	 */
	public void interpolateZ(int blockZ, double deltaZ) {
		this.cellBlockZ = blockZ - this.startBlockZ;
		this.sampleUniqueIndex++;

		for (ChunkNoiseSampler.DensityInterpolator densityInterpolator : this.interpolators) {
			densityInterpolator.interpolateZ(deltaZ);
		}
	}

	/**
	 * Stops the interpolation loop for this chunk.
	 */
	public void stopInterpolation() {
		if (!this.isInInterpolationLoop) {
			throw new IllegalStateException("Staring interpolation twice");
		} else {
			this.isInInterpolationLoop = false;
		}
	}

	/**
	 * Swaps the start and end density buffers of every interpolator.
	 */
	public void swapBuffers() {
		this.interpolators.forEach(ChunkNoiseSampler.DensityInterpolator::swapBuffers);
	}

	public AquiferSampler getAquiferSampler() {
		return this.aquiferSampler;
	}

	protected int getHorizontalCellBlockCount() {
		return this.horizontalCellBlockCount;
	}

	protected int getVerticalCellBlockCount() {
		return this.verticalCellBlockCount;
	}

	Blender.BlendResult calculateBlendResult(int blockX, int blockZ) {
		long l = ChunkPos.toLong(blockX, blockZ);
		if (this.lastBlendingColumnPos == l) {
			return this.lastBlendingResult;
		} else {
			this.lastBlendingColumnPos = l;
			Blender.BlendResult blendResult = this.blender.calculate(blockX, blockZ);
			this.lastBlendingResult = blendResult;
			return blendResult;
		}
	}

	/**
	 * {@return the actual density function for a given density function}
	 * 
	 * <p>The classes in {@link DensityFunctionTypes} for caches, interpolators,
	 * {@code blend_alpha} and {@code blend_offset} use simple stub implementations
	 * that get replaced with their actual implementation by calling this method.
	 * 
	 * @param function the density function to get an actual implementation for
	 */
	protected DensityFunction getActualDensityFunction(DensityFunction function) {
		return (DensityFunction)this.actualDensityFunctionCache.computeIfAbsent(function, this::getActualDensityFunctionImpl);
	}

	/**
	 * {@return the actual density function for a given density function}
	 */
	private DensityFunction getActualDensityFunctionImpl(DensityFunction function) {
		if (function instanceof DensityFunctionTypes.Wrapping wrapping) {
			return (DensityFunction)(switch (wrapping.type()) {
				case INTERPOLATED -> new ChunkNoiseSampler.DensityInterpolator(wrapping.wrapped());
				case FLAT_CACHE -> new ChunkNoiseSampler.FlatCache(wrapping.wrapped(), true);
				case CACHE2D -> new ChunkNoiseSampler.Cache2D(wrapping.wrapped());
				case CACHE_ONCE -> new ChunkNoiseSampler.CacheOnce(wrapping.wrapped());
				case CACHE_ALL_IN_CELL -> new ChunkNoiseSampler.CellCache(wrapping.wrapped());
			});
		} else {
			if (this.blender != Blender.getNoBlending()) {
				if (function == DensityFunctionTypes.BlendAlpha.INSTANCE) {
					return this.cachedBlendAlphaDensityFunction;
				}

				if (function == DensityFunctionTypes.BlendOffset.INSTANCE) {
					return this.cachedBlendOffsetDensityFunction;
				}
			}

			if (function == DensityFunctionTypes.Beardifier.INSTANCE) {
				return this.beardifying;
			} else {
				return function instanceof DensityFunctionTypes.RegistryEntryHolder registryEntryHolder ? registryEntryHolder.function().value() : function;
			}
		}
	}

	class BlendAlphaDensityFunction implements ChunkNoiseSampler.ParentedNoiseType {
		@Override
		public DensityFunction wrapped() {
			return DensityFunctionTypes.BlendAlpha.INSTANCE;
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return this.wrapped().apply(visitor);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return ChunkNoiseSampler.this.calculateBlendResult(pos.blockX(), pos.blockZ()).alpha();
		}

		@Override
		public void fill(double[] densities, DensityFunction.EachApplier applier) {
			applier.fill(densities, this);
		}

		@Override
		public double minValue() {
			return 0.0;
		}

		@Override
		public double maxValue() {
			return 1.0;
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodecHolder() {
			return DensityFunctionTypes.BlendAlpha.CODEC;
		}
	}

	class BlendOffsetDensityFunction implements ChunkNoiseSampler.ParentedNoiseType {
		@Override
		public DensityFunction wrapped() {
			return DensityFunctionTypes.BlendOffset.INSTANCE;
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return this.wrapped().apply(visitor);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return ChunkNoiseSampler.this.calculateBlendResult(pos.blockX(), pos.blockZ()).blendingOffset();
		}

		@Override
		public void fill(double[] densities, DensityFunction.EachApplier applier) {
			applier.fill(densities, this);
		}

		@Override
		public double minValue() {
			return Double.NEGATIVE_INFINITY;
		}

		@Override
		public double maxValue() {
			return Double.POSITIVE_INFINITY;
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodecHolder() {
			return DensityFunctionTypes.BlendOffset.CODEC;
		}
	}

	@FunctionalInterface
	public interface BlockStateSampler {
		@Nullable
		BlockState sample(DensityFunction.NoisePos pos);
	}

	static class Cache2D implements DensityFunctionTypes.Wrapper, ChunkNoiseSampler.ParentedNoiseType {
		private final DensityFunction delegate;
		private long lastSamplingColumnPos = ChunkPos.MARKER;
		private double lastSamplingResult;

		Cache2D(DensityFunction delegate) {
			this.delegate = delegate;
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			int i = pos.blockX();
			int j = pos.blockZ();
			long l = ChunkPos.toLong(i, j);
			if (this.lastSamplingColumnPos == l) {
				return this.lastSamplingResult;
			} else {
				this.lastSamplingColumnPos = l;
				double d = this.delegate.sample(pos);
				this.lastSamplingResult = d;
				return d;
			}
		}

		@Override
		public void fill(double[] densities, DensityFunction.EachApplier applier) {
			this.delegate.fill(densities, applier);
		}

		@Override
		public DensityFunction wrapped() {
			return this.delegate;
		}

		@Override
		public DensityFunctionTypes.Wrapping.Type type() {
			return DensityFunctionTypes.Wrapping.Type.CACHE2D;
		}
	}

	class CacheOnce implements DensityFunctionTypes.Wrapper, ChunkNoiseSampler.ParentedNoiseType {
		private final DensityFunction delegate;
		private long sampleUniqueIndex;
		private long cacheOnceUniqueIndex;
		private double lastSamplingResult;
		@Nullable
		private double[] cache;

		CacheOnce(final DensityFunction delegate) {
			this.delegate = delegate;
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			if (pos != ChunkNoiseSampler.this) {
				return this.delegate.sample(pos);
			} else if (this.cache != null && this.cacheOnceUniqueIndex == ChunkNoiseSampler.this.cacheOnceUniqueIndex) {
				return this.cache[ChunkNoiseSampler.this.index];
			} else if (this.sampleUniqueIndex == ChunkNoiseSampler.this.sampleUniqueIndex) {
				return this.lastSamplingResult;
			} else {
				this.sampleUniqueIndex = ChunkNoiseSampler.this.sampleUniqueIndex;
				double d = this.delegate.sample(pos);
				this.lastSamplingResult = d;
				return d;
			}
		}

		@Override
		public void fill(double[] densities, DensityFunction.EachApplier applier) {
			if (this.cache != null && this.cacheOnceUniqueIndex == ChunkNoiseSampler.this.cacheOnceUniqueIndex) {
				System.arraycopy(this.cache, 0, densities, 0, densities.length);
			} else {
				this.wrapped().fill(densities, applier);
				if (this.cache != null && this.cache.length == densities.length) {
					System.arraycopy(densities, 0, this.cache, 0, densities.length);
				} else {
					this.cache = (double[])densities.clone();
				}

				this.cacheOnceUniqueIndex = ChunkNoiseSampler.this.cacheOnceUniqueIndex;
			}
		}

		@Override
		public DensityFunction wrapped() {
			return this.delegate;
		}

		@Override
		public DensityFunctionTypes.Wrapping.Type type() {
			return DensityFunctionTypes.Wrapping.Type.CACHE_ONCE;
		}
	}

	class CellCache implements DensityFunctionTypes.Wrapper, ChunkNoiseSampler.ParentedNoiseType {
		final DensityFunction delegate;
		final double[] cache;

		CellCache(final DensityFunction delegate) {
			this.delegate = delegate;
			this.cache = new double[ChunkNoiseSampler.this.horizontalCellBlockCount
				* ChunkNoiseSampler.this.horizontalCellBlockCount
				* ChunkNoiseSampler.this.verticalCellBlockCount];
			ChunkNoiseSampler.this.caches.add(this);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			if (pos != ChunkNoiseSampler.this) {
				return this.delegate.sample(pos);
			} else if (!ChunkNoiseSampler.this.isInInterpolationLoop) {
				throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
			} else {
				int i = ChunkNoiseSampler.this.cellBlockX;
				int j = ChunkNoiseSampler.this.cellBlockY;
				int k = ChunkNoiseSampler.this.cellBlockZ;
				return i >= 0
						&& j >= 0
						&& k >= 0
						&& i < ChunkNoiseSampler.this.horizontalCellBlockCount
						&& j < ChunkNoiseSampler.this.verticalCellBlockCount
						&& k < ChunkNoiseSampler.this.horizontalCellBlockCount
					? this.cache[((ChunkNoiseSampler.this.verticalCellBlockCount - 1 - j) * ChunkNoiseSampler.this.horizontalCellBlockCount + i)
							* ChunkNoiseSampler.this.horizontalCellBlockCount
						+ k]
					: this.delegate.sample(pos);
			}
		}

		@Override
		public void fill(double[] densities, DensityFunction.EachApplier applier) {
			applier.fill(densities, this);
		}

		@Override
		public DensityFunction wrapped() {
			return this.delegate;
		}

		@Override
		public DensityFunctionTypes.Wrapping.Type type() {
			return DensityFunctionTypes.Wrapping.Type.CACHE_ALL_IN_CELL;
		}
	}

	public class DensityInterpolator implements DensityFunctionTypes.Wrapper, ChunkNoiseSampler.ParentedNoiseType {
		double[][] startDensityBuffer;
		double[][] endDensityBuffer;
		private final DensityFunction delegate;
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

		DensityInterpolator(final DensityFunction delegate) {
			this.delegate = delegate;
			this.startDensityBuffer = this.createBuffer(ChunkNoiseSampler.this.verticalCellCount, ChunkNoiseSampler.this.horizontalCellCount);
			this.endDensityBuffer = this.createBuffer(ChunkNoiseSampler.this.verticalCellCount, ChunkNoiseSampler.this.horizontalCellCount);
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

		/**
		 * Copies the densities of the eight corners of the current cell into their
		 * respective fields.
		 * 
		 * @param cellY the cell's Y coordinate
		 * @param cellZ the cell's chunk-local Z coordinate
		 */
		void onSampledCellCorners(int cellY, int cellZ) {
			this.x0y0z0 = this.startDensityBuffer[cellZ][cellY];
			this.x0y0z1 = this.startDensityBuffer[cellZ + 1][cellY];
			this.x1y0z0 = this.endDensityBuffer[cellZ][cellY];
			this.x1y0z1 = this.endDensityBuffer[cellZ + 1][cellY];
			this.x0y1z0 = this.startDensityBuffer[cellZ][cellY + 1];
			this.x0y1z1 = this.startDensityBuffer[cellZ + 1][cellY + 1];
			this.x1y1z0 = this.endDensityBuffer[cellZ][cellY + 1];
			this.x1y1z1 = this.endDensityBuffer[cellZ + 1][cellY + 1];
		}

		/**
		 * Interpolates the eight densities on the Y axis.
		 */
		void interpolateY(double deltaY) {
			this.x0z0 = MathHelper.lerp(deltaY, this.x0y0z0, this.x0y1z0);
			this.x1z0 = MathHelper.lerp(deltaY, this.x1y0z0, this.x1y1z0);
			this.x0z1 = MathHelper.lerp(deltaY, this.x0y0z1, this.x0y1z1);
			this.x1z1 = MathHelper.lerp(deltaY, this.x1y0z1, this.x1y1z1);
		}

		/**
		 * Interpolates the four remaining densities on the X axis.
		 */
		void interpolateX(double deltaX) {
			this.z0 = MathHelper.lerp(deltaX, this.x0z0, this.x1z0);
			this.z1 = MathHelper.lerp(deltaX, this.x0z1, this.x1z1);
		}

		/**
		 * Interpolates the two remaining densities on the Z axis.
		 */
		void interpolateZ(double deltaZ) {
			this.result = MathHelper.lerp(deltaZ, this.z0, this.z1);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			if (pos != ChunkNoiseSampler.this) {
				return this.delegate.sample(pos);
			} else if (!ChunkNoiseSampler.this.isInInterpolationLoop) {
				throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
			} else {
				return ChunkNoiseSampler.this.isSamplingForCaches
					? MathHelper.lerp3(
						(double)ChunkNoiseSampler.this.cellBlockX / (double)ChunkNoiseSampler.this.horizontalCellBlockCount,
						(double)ChunkNoiseSampler.this.cellBlockY / (double)ChunkNoiseSampler.this.verticalCellBlockCount,
						(double)ChunkNoiseSampler.this.cellBlockZ / (double)ChunkNoiseSampler.this.horizontalCellBlockCount,
						this.x0y0z0,
						this.x1y0z0,
						this.x0y1z0,
						this.x1y1z0,
						this.x0y0z1,
						this.x1y0z1,
						this.x0y1z1,
						this.x1y1z1
					)
					: this.result;
			}
		}

		@Override
		public void fill(double[] densities, DensityFunction.EachApplier applier) {
			if (ChunkNoiseSampler.this.isSamplingForCaches) {
				applier.fill(densities, this);
			} else {
				this.wrapped().fill(densities, applier);
			}
		}

		@Override
		public DensityFunction wrapped() {
			return this.delegate;
		}

		private void swapBuffers() {
			double[][] ds = this.startDensityBuffer;
			this.startDensityBuffer = this.endDensityBuffer;
			this.endDensityBuffer = ds;
		}

		@Override
		public DensityFunctionTypes.Wrapping.Type type() {
			return DensityFunctionTypes.Wrapping.Type.INTERPOLATED;
		}
	}

	class FlatCache implements DensityFunctionTypes.Wrapper, ChunkNoiseSampler.ParentedNoiseType {
		private final DensityFunction delegate;
		final double[][] cache;

		FlatCache(final DensityFunction delegate, final boolean sample) {
			this.delegate = delegate;
			this.cache = new double[ChunkNoiseSampler.this.horizontalBiomeEnd + 1][ChunkNoiseSampler.this.horizontalBiomeEnd + 1];
			if (sample) {
				for (int i = 0; i <= ChunkNoiseSampler.this.horizontalBiomeEnd; i++) {
					int j = ChunkNoiseSampler.this.startBiomeX + i;
					int k = BiomeCoords.toBlock(j);

					for (int l = 0; l <= ChunkNoiseSampler.this.horizontalBiomeEnd; l++) {
						int m = ChunkNoiseSampler.this.startBiomeZ + l;
						int n = BiomeCoords.toBlock(m);
						this.cache[i][l] = delegate.sample(new DensityFunction.UnblendedNoisePos(k, 0, n));
					}
				}
			}
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			int i = BiomeCoords.fromBlock(pos.blockX());
			int j = BiomeCoords.fromBlock(pos.blockZ());
			int k = i - ChunkNoiseSampler.this.startBiomeX;
			int l = j - ChunkNoiseSampler.this.startBiomeZ;
			int m = this.cache.length;
			return k >= 0 && l >= 0 && k < m && l < m ? this.cache[k][l] : this.delegate.sample(pos);
		}

		@Override
		public void fill(double[] densities, DensityFunction.EachApplier applier) {
			applier.fill(densities, this);
		}

		@Override
		public DensityFunction wrapped() {
			return this.delegate;
		}

		@Override
		public DensityFunctionTypes.Wrapping.Type type() {
			return DensityFunctionTypes.Wrapping.Type.FLAT_CACHE;
		}
	}

	interface ParentedNoiseType extends DensityFunction {
		DensityFunction wrapped();

		@Override
		default double minValue() {
			return this.wrapped().minValue();
		}

		@Override
		default double maxValue() {
			return this.wrapped().maxValue();
		}
	}
}
