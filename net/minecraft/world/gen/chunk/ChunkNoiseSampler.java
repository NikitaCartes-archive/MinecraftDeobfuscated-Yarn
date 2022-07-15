/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseRouter;
import org.jetbrains.annotations.Nullable;

public class ChunkNoiseSampler
implements DensityFunction.EachApplier,
DensityFunction.NoisePos {
    private final GenerationShapeConfig generationShapeConfig;
    final int horizontalCellCount;
    final int cellHeight;
    final int minimumCellY;
    private final int startCellX;
    private final int startCellZ;
    final int biomeX;
    final int biomeZ;
    final List<NoiseInterpolator> interpolators;
    final List<CellCache> caches;
    private final Map<DensityFunction, DensityFunction> actualDensityFunctionCache = new HashMap<DensityFunction, DensityFunction>();
    private final Long2IntMap surfaceHeightEstimateCache = new Long2IntOpenHashMap();
    private final AquiferSampler aquiferSampler;
    private final DensityFunction initialDensityWithoutJaggedness;
    private final BlockStateSampler blockStateSampler;
    private final Blender blender;
    private final FlatCacheDensityFunction cachedBlendAlphaDensityFunction;
    private final FlatCacheDensityFunction cachedBlendOffsetDensityFunction;
    private final DensityFunctionTypes.Beardifying beardifying;
    private long lastBlendingColumnPos = ChunkPos.MARKER;
    private Blender.BlendResult lastBlendingResult = new Blender.BlendResult(1.0, 0.0);
    final int biomeHorizontalEnd;
    final int horizontalBlockSize;
    final int verticalBlockSize;
    boolean isInInterpolationLoop;
    boolean isSamplingNoise;
    private int startBlockX;
    int startBlockY;
    private int startBlockZ;
    int cellBlockX;
    int cellBlockY;
    int cellBlockZ;
    long sampleUniqueIndex;
    long cacheOnceUniqueIndex;
    int index;
    private final DensityFunction.EachApplier eachApplier = new DensityFunction.EachApplier(){

        @Override
        public DensityFunction.NoisePos getPosAt(int index) {
            ChunkNoiseSampler.this.startBlockY = (index + ChunkNoiseSampler.this.minimumCellY) * ChunkNoiseSampler.this.verticalBlockSize;
            ++ChunkNoiseSampler.this.sampleUniqueIndex;
            ChunkNoiseSampler.this.cellBlockY = 0;
            ChunkNoiseSampler.this.index = index;
            return ChunkNoiseSampler.this;
        }

        @Override
        public void applyEach(double[] densities, DensityFunction densityFunction) {
            for (int i = 0; i < ChunkNoiseSampler.this.cellHeight + 1; ++i) {
                ChunkNoiseSampler.this.startBlockY = (i + ChunkNoiseSampler.this.minimumCellY) * ChunkNoiseSampler.this.verticalBlockSize;
                ++ChunkNoiseSampler.this.sampleUniqueIndex;
                ChunkNoiseSampler.this.cellBlockY = 0;
                ChunkNoiseSampler.this.index = i;
                densities[i] = densityFunction.sample(ChunkNoiseSampler.this);
            }
        }
    };

    public static ChunkNoiseSampler create(Chunk chunk, NoiseConfig noiseConfig, DensityFunctionTypes.Beardifying beardifying, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender) {
        GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.generationShapeConfig().trimHeight(chunk);
        ChunkPos chunkPos = chunk.getPos();
        int i = 16 / generationShapeConfig.horizontalBlockSize();
        return new ChunkNoiseSampler(i, noiseConfig, chunkPos.getStartX(), chunkPos.getStartZ(), generationShapeConfig, beardifying, chunkGeneratorSettings, fluidLevelSampler, blender);
    }

    public ChunkNoiseSampler(int horizontalCellCount, NoiseConfig noiseConfig, int startX, int startZ, GenerationShapeConfig generationShapeConfig, DensityFunctionTypes.Beardifying beardifying, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender) {
        int l;
        int k;
        this.generationShapeConfig = generationShapeConfig;
        this.horizontalBlockSize = generationShapeConfig.horizontalBlockSize();
        this.verticalBlockSize = generationShapeConfig.verticalBlockSize();
        this.horizontalCellCount = horizontalCellCount;
        this.cellHeight = MathHelper.floorDiv(generationShapeConfig.height(), this.verticalBlockSize);
        this.minimumCellY = MathHelper.floorDiv(generationShapeConfig.minimumY(), this.verticalBlockSize);
        this.startCellX = Math.floorDiv(startX, this.horizontalBlockSize);
        this.startCellZ = Math.floorDiv(startZ, this.horizontalBlockSize);
        this.interpolators = Lists.newArrayList();
        this.caches = Lists.newArrayList();
        this.biomeX = BiomeCoords.fromBlock(startX);
        this.biomeZ = BiomeCoords.fromBlock(startZ);
        this.biomeHorizontalEnd = BiomeCoords.fromBlock(horizontalCellCount * this.horizontalBlockSize);
        this.blender = blender;
        this.beardifying = beardifying;
        this.cachedBlendAlphaDensityFunction = new FlatCacheDensityFunction(new BlendAlphaDensityFunction(), false);
        this.cachedBlendOffsetDensityFunction = new FlatCacheDensityFunction(new BlendOffsetDensityFunction(), false);
        for (int i = 0; i <= this.biomeHorizontalEnd; ++i) {
            int j = this.biomeX + i;
            k = BiomeCoords.toBlock(j);
            for (l = 0; l <= this.biomeHorizontalEnd; ++l) {
                int m = this.biomeZ + l;
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
            k = ChunkSectionPos.getSectionCoord(startX);
            l = ChunkSectionPos.getSectionCoord(startZ);
            this.aquiferSampler = AquiferSampler.aquifer(this, new ChunkPos(k, l), noiseRouter2, noiseConfig.getAquiferRandomDeriver(), generationShapeConfig.minimumY(), generationShapeConfig.height(), fluidLevelSampler);
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        DensityFunction densityFunction = DensityFunctionTypes.cacheAllInCell(DensityFunctionTypes.add(noiseRouter2.finalDensity(), DensityFunctionTypes.Beardifier.INSTANCE)).apply(this::getActualDensityFunction);
        builder.add(pos -> this.aquiferSampler.apply(pos, densityFunction.sample(pos)));
        if (chunkGeneratorSettings.oreVeins()) {
            builder.add(OreVeinSampler.create(noiseRouter2.veinToggle(), noiseRouter2.veinRidged(), noiseRouter2.veinGap(), noiseConfig.getOreRandomDeriver()));
        }
        this.blockStateSampler = new ChainedBlockSource((List<BlockStateSampler>)((Object)builder.build()));
        this.initialDensityWithoutJaggedness = noiseRouter2.initialDensityWithoutJaggedness();
    }

    protected MultiNoiseUtil.MultiNoiseSampler createMultiNoiseSampler(NoiseRouter noiseRouter, List<MultiNoiseUtil.NoiseHypercube> spawnTarget) {
        return new MultiNoiseUtil.MultiNoiseSampler(noiseRouter.temperature().apply(this::getActualDensityFunction), noiseRouter.vegetation().apply(this::getActualDensityFunction), noiseRouter.continents().apply(this::getActualDensityFunction), noiseRouter.erosion().apply(this::getActualDensityFunction), noiseRouter.depth().apply(this::getActualDensityFunction), noiseRouter.ridges().apply(this::getActualDensityFunction), spawnTarget);
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
        for (int l = k + this.generationShapeConfig.height(); l >= k; l -= this.verticalBlockSize) {
            DensityFunction.UnblendedNoisePos unblendedNoisePos = new DensityFunction.UnblendedNoisePos(i, l, j);
            if (!(this.initialDensityWithoutJaggedness.sample(unblendedNoisePos) > 0.390625)) continue;
            return l;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public Blender getBlender() {
        return this.blender;
    }

    private void sampleNoise(boolean start, int nextCellX) {
        this.startBlockX = nextCellX * this.horizontalBlockSize;
        this.cellBlockX = 0;
        for (int i = 0; i < this.horizontalCellCount + 1; ++i) {
            int j = this.startCellZ + i;
            this.startBlockZ = j * this.horizontalBlockSize;
            this.cellBlockZ = 0;
            ++this.cacheOnceUniqueIndex;
            for (NoiseInterpolator noiseInterpolator : this.interpolators) {
                double[] ds = (start ? noiseInterpolator.startNoiseBuffer : noiseInterpolator.endNoiseBuffer)[i];
                noiseInterpolator.applyEach(ds, this.eachApplier);
            }
        }
        ++this.cacheOnceUniqueIndex;
    }

    public void sampleStartNoise() {
        if (this.isInInterpolationLoop) {
            throw new IllegalStateException("Staring interpolation twice");
        }
        this.isInInterpolationLoop = true;
        this.sampleUniqueIndex = 0L;
        this.sampleNoise(true, this.startCellX);
    }

    public void sampleEndNoise(int cellX) {
        this.sampleNoise(false, this.startCellX + cellX + 1);
        this.startBlockX = (this.startCellX + cellX) * this.horizontalBlockSize;
    }

    @Override
    public ChunkNoiseSampler getPosAt(int i) {
        int j = Math.floorMod(i, this.horizontalBlockSize);
        int k = Math.floorDiv(i, this.horizontalBlockSize);
        int l = Math.floorMod(k, this.horizontalBlockSize);
        int m = this.verticalBlockSize - 1 - Math.floorDiv(k, this.horizontalBlockSize);
        this.cellBlockX = l;
        this.cellBlockY = m;
        this.cellBlockZ = j;
        this.index = i;
        return this;
    }

    @Override
    public void applyEach(double[] densities, DensityFunction densityFunction) {
        this.index = 0;
        for (int i = this.verticalBlockSize - 1; i >= 0; --i) {
            this.cellBlockY = i;
            for (int j = 0; j < this.horizontalBlockSize; ++j) {
                this.cellBlockX = j;
                int k = 0;
                while (k < this.horizontalBlockSize) {
                    this.cellBlockZ = k++;
                    densities[this.index++] = densityFunction.sample(this);
                }
            }
        }
    }

    public void sampleNoiseCorners(int cellY, int cellZ) {
        this.interpolators.forEach(interpolator -> interpolator.sampleNoiseCorners(cellY, cellZ));
        this.isSamplingNoise = true;
        this.startBlockY = (cellY + this.minimumCellY) * this.verticalBlockSize;
        this.startBlockZ = (this.startCellZ + cellZ) * this.horizontalBlockSize;
        ++this.cacheOnceUniqueIndex;
        for (CellCache cellCache : this.caches) {
            cellCache.delegate.applyEach(cellCache.cache, this);
        }
        ++this.cacheOnceUniqueIndex;
        this.isSamplingNoise = false;
    }

    public void sampleNoiseY(int blockY, double cellDeltaY) {
        this.cellBlockY = blockY - this.startBlockY;
        this.interpolators.forEach(interpolator -> interpolator.sampleNoiseY(cellDeltaY));
    }

    public void sampleNoiseX(int blockX, double cellDeltaX) {
        this.cellBlockX = blockX - this.startBlockX;
        this.interpolators.forEach(interpolator -> interpolator.sampleNoiseX(cellDeltaX));
    }

    public void sampleNoiseZ(int blockZ, double cellDeltaZ) {
        this.cellBlockZ = blockZ - this.startBlockZ;
        ++this.sampleUniqueIndex;
        this.interpolators.forEach(interpolator -> interpolator.sampleNoiseZ(cellDeltaZ));
    }

    public void stopInterpolation() {
        if (!this.isInInterpolationLoop) {
            throw new IllegalStateException("Staring interpolation twice");
        }
        this.isInInterpolationLoop = false;
    }

    public void swapBuffers() {
        this.interpolators.forEach(NoiseInterpolator::swapBuffers);
    }

    public AquiferSampler getAquiferSampler() {
        return this.aquiferSampler;
    }

    protected int getHorizontalBlockSize() {
        return this.horizontalBlockSize;
    }

    protected int getVerticalBlockSize() {
        return this.verticalBlockSize;
    }

    Blender.BlendResult calculateBlendResult(int blockX, int blockZ) {
        Blender.BlendResult blendResult;
        long l = ChunkPos.toLong(blockX, blockZ);
        if (this.lastBlendingColumnPos == l) {
            return this.lastBlendingResult;
        }
        this.lastBlendingColumnPos = l;
        this.lastBlendingResult = blendResult = this.blender.calculate(blockX, blockZ);
        return blendResult;
    }

    protected DensityFunction getActualDensityFunction(DensityFunction densityFunction) {
        return this.actualDensityFunctionCache.computeIfAbsent(densityFunction, this::getActualDensityFunctionImpl);
    }

    private DensityFunction getActualDensityFunctionImpl(DensityFunction densityFunction) {
        if (densityFunction instanceof DensityFunctionTypes.Wrapping) {
            DensityFunctionTypes.Wrapping wrapping = (DensityFunctionTypes.Wrapping)densityFunction;
            return switch (wrapping.type()) {
                default -> throw new IncompatibleClassChangeError();
                case DensityFunctionTypes.Wrapping.Type.INTERPOLATED -> new NoiseInterpolator(wrapping.wrapped());
                case DensityFunctionTypes.Wrapping.Type.FLAT_CACHE -> new FlatCacheDensityFunction(wrapping.wrapped(), true);
                case DensityFunctionTypes.Wrapping.Type.CACHE2D -> new Cache2D(wrapping.wrapped());
                case DensityFunctionTypes.Wrapping.Type.CACHE_ONCE -> new CacheOnce(wrapping.wrapped());
                case DensityFunctionTypes.Wrapping.Type.CACHE_ALL_IN_CELL -> new CellCache(wrapping.wrapped());
            };
        }
        if (this.blender != Blender.getNoBlending()) {
            if (densityFunction == DensityFunctionTypes.BlendAlpha.INSTANCE) {
                return this.cachedBlendAlphaDensityFunction;
            }
            if (densityFunction == DensityFunctionTypes.BlendOffset.INSTANCE) {
                return this.cachedBlendOffsetDensityFunction;
            }
        }
        if (densityFunction == DensityFunctionTypes.Beardifier.INSTANCE) {
            return this.beardifying;
        }
        if (densityFunction instanceof DensityFunctionTypes.RegistryEntryHolder) {
            DensityFunctionTypes.RegistryEntryHolder registryEntryHolder = (DensityFunctionTypes.RegistryEntryHolder)densityFunction;
            return registryEntryHolder.function().value();
        }
        return densityFunction;
    }

    @Override
    public /* synthetic */ DensityFunction.NoisePos getPosAt(int index) {
        return this.getPosAt(index);
    }

    class FlatCacheDensityFunction
    implements DensityFunctionTypes.Wrapper,
    ParentedNoiseType {
        private final DensityFunction delegate;
        final double[][] cache;

        FlatCacheDensityFunction(DensityFunction delegate, boolean sample) {
            this.delegate = delegate;
            this.cache = new double[ChunkNoiseSampler.this.biomeHorizontalEnd + 1][ChunkNoiseSampler.this.biomeHorizontalEnd + 1];
            if (sample) {
                for (int i = 0; i <= ChunkNoiseSampler.this.biomeHorizontalEnd; ++i) {
                    int j = ChunkNoiseSampler.this.biomeX + i;
                    int k = BiomeCoords.toBlock(j);
                    for (int l = 0; l <= ChunkNoiseSampler.this.biomeHorizontalEnd; ++l) {
                        int m = ChunkNoiseSampler.this.biomeZ + l;
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
            int k = i - ChunkNoiseSampler.this.biomeX;
            int l = j - ChunkNoiseSampler.this.biomeZ;
            int m = this.cache.length;
            if (k >= 0 && l >= 0 && k < m && l < m) {
                return this.cache[k][l];
            }
            return this.delegate.sample(pos);
        }

        @Override
        public void applyEach(double[] densities, DensityFunction.EachApplier applier) {
            applier.applyEach(densities, this);
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

    class BlendAlphaDensityFunction
    implements ParentedNoiseType {
        BlendAlphaDensityFunction() {
        }

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
        public void applyEach(double[] densities, DensityFunction.EachApplier applier) {
            applier.applyEach(densities, this);
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

    class BlendOffsetDensityFunction
    implements ParentedNoiseType {
        BlendOffsetDensityFunction() {
        }

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
        public void applyEach(double[] densities, DensityFunction.EachApplier applier) {
            applier.applyEach(densities, this);
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
    public static interface BlockStateSampler {
        @Nullable
        public BlockState sample(DensityFunction.NoisePos var1);
    }

    public class NoiseInterpolator
    implements DensityFunctionTypes.Wrapper,
    ParentedNoiseType {
        double[][] startNoiseBuffer;
        double[][] endNoiseBuffer;
        private final DensityFunction columnSampler;
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

        NoiseInterpolator(DensityFunction columnSampler) {
            this.columnSampler = columnSampler;
            this.startNoiseBuffer = this.createBuffer(ChunkNoiseSampler.this.cellHeight, ChunkNoiseSampler.this.horizontalCellCount);
            this.endNoiseBuffer = this.createBuffer(ChunkNoiseSampler.this.cellHeight, ChunkNoiseSampler.this.horizontalCellCount);
            ChunkNoiseSampler.this.interpolators.add(this);
        }

        private double[][] createBuffer(int sizeZ, int sizeX) {
            int i = sizeX + 1;
            int j = sizeZ + 1;
            double[][] ds = new double[i][j];
            for (int k = 0; k < i; ++k) {
                ds[k] = new double[j];
            }
            return ds;
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

        void sampleNoiseZ(double deltaZ) {
            this.result = MathHelper.lerp(deltaZ, this.z0, this.z1);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            if (pos != ChunkNoiseSampler.this) {
                return this.columnSampler.sample(pos);
            }
            if (!ChunkNoiseSampler.this.isInInterpolationLoop) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            }
            if (ChunkNoiseSampler.this.isSamplingNoise) {
                return MathHelper.lerp3((double)ChunkNoiseSampler.this.cellBlockX / (double)ChunkNoiseSampler.this.horizontalBlockSize, (double)ChunkNoiseSampler.this.cellBlockY / (double)ChunkNoiseSampler.this.verticalBlockSize, (double)ChunkNoiseSampler.this.cellBlockZ / (double)ChunkNoiseSampler.this.horizontalBlockSize, this.x0y0z0, this.x1y0z0, this.x0y1z0, this.x1y1z0, this.x0y0z1, this.x1y0z1, this.x0y1z1, this.x1y1z1);
            }
            return this.result;
        }

        @Override
        public void applyEach(double[] densities, DensityFunction.EachApplier applier) {
            if (ChunkNoiseSampler.this.isSamplingNoise) {
                applier.applyEach(densities, this);
                return;
            }
            this.wrapped().applyEach(densities, applier);
        }

        @Override
        public DensityFunction wrapped() {
            return this.columnSampler;
        }

        private void swapBuffers() {
            double[][] ds = this.startNoiseBuffer;
            this.startNoiseBuffer = this.endNoiseBuffer;
            this.endNoiseBuffer = ds;
        }

        @Override
        public DensityFunctionTypes.Wrapping.Type type() {
            return DensityFunctionTypes.Wrapping.Type.INTERPOLATED;
        }
    }

    class CellCache
    implements DensityFunctionTypes.Wrapper,
    ParentedNoiseType {
        final DensityFunction delegate;
        final double[] cache;

        CellCache(DensityFunction delegate) {
            this.delegate = delegate;
            this.cache = new double[ChunkNoiseSampler.this.horizontalBlockSize * ChunkNoiseSampler.this.horizontalBlockSize * ChunkNoiseSampler.this.verticalBlockSize];
            ChunkNoiseSampler.this.caches.add(this);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            if (pos != ChunkNoiseSampler.this) {
                return this.delegate.sample(pos);
            }
            if (!ChunkNoiseSampler.this.isInInterpolationLoop) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            }
            int i = ChunkNoiseSampler.this.cellBlockX;
            int j = ChunkNoiseSampler.this.cellBlockY;
            int k = ChunkNoiseSampler.this.cellBlockZ;
            if (i >= 0 && j >= 0 && k >= 0 && i < ChunkNoiseSampler.this.horizontalBlockSize && j < ChunkNoiseSampler.this.verticalBlockSize && k < ChunkNoiseSampler.this.horizontalBlockSize) {
                return this.cache[((ChunkNoiseSampler.this.verticalBlockSize - 1 - j) * ChunkNoiseSampler.this.horizontalBlockSize + i) * ChunkNoiseSampler.this.horizontalBlockSize + k];
            }
            return this.delegate.sample(pos);
        }

        @Override
        public void applyEach(double[] densities, DensityFunction.EachApplier applier) {
            applier.applyEach(densities, this);
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

    static class Cache2D
    implements DensityFunctionTypes.Wrapper,
    ParentedNoiseType {
        private final DensityFunction delegate;
        private long lastSamplingColumnPos = ChunkPos.MARKER;
        private double lastSamplingResult;

        Cache2D(DensityFunction delegate) {
            this.delegate = delegate;
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            double d;
            int j;
            int i = pos.blockX();
            long l = ChunkPos.toLong(i, j = pos.blockZ());
            if (this.lastSamplingColumnPos == l) {
                return this.lastSamplingResult;
            }
            this.lastSamplingColumnPos = l;
            this.lastSamplingResult = d = this.delegate.sample(pos);
            return d;
        }

        @Override
        public void applyEach(double[] densities, DensityFunction.EachApplier applier) {
            this.delegate.applyEach(densities, applier);
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

    class CacheOnce
    implements DensityFunctionTypes.Wrapper,
    ParentedNoiseType {
        private final DensityFunction delegate;
        private long sampleUniqueIndex;
        private long cacheOnceUniqueIndex;
        private double lastSamplingResult;
        @Nullable
        private double[] cache;

        CacheOnce(DensityFunction delegate) {
            this.delegate = delegate;
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            double d;
            if (pos != ChunkNoiseSampler.this) {
                return this.delegate.sample(pos);
            }
            if (this.cache != null && this.cacheOnceUniqueIndex == ChunkNoiseSampler.this.cacheOnceUniqueIndex) {
                return this.cache[ChunkNoiseSampler.this.index];
            }
            if (this.sampleUniqueIndex == ChunkNoiseSampler.this.sampleUniqueIndex) {
                return this.lastSamplingResult;
            }
            this.sampleUniqueIndex = ChunkNoiseSampler.this.sampleUniqueIndex;
            this.lastSamplingResult = d = this.delegate.sample(pos);
            return d;
        }

        @Override
        public void applyEach(double[] densities, DensityFunction.EachApplier applier) {
            if (this.cache != null && this.cacheOnceUniqueIndex == ChunkNoiseSampler.this.cacheOnceUniqueIndex) {
                System.arraycopy(this.cache, 0, densities, 0, densities.length);
                return;
            }
            this.wrapped().applyEach(densities, applier);
            if (this.cache != null && this.cache.length == densities.length) {
                System.arraycopy(densities, 0, this.cache, 0, densities.length);
            } else {
                this.cache = (double[])densities.clone();
            }
            this.cacheOnceUniqueIndex = ChunkNoiseSampler.this.cacheOnceUniqueIndex;
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

    static interface ParentedNoiseType
    extends DensityFunction {
        public DensityFunction wrapped();

        @Override
        default public double minValue() {
            return this.wrapped().minValue();
        }

        @Override
        default public double maxValue() {
            return this.wrapped().maxValue();
        }
    }
}

