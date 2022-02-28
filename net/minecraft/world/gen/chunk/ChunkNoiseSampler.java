/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
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
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.noise.NoiseRouter;
import org.jetbrains.annotations.Nullable;

public class ChunkNoiseSampler
implements DensityFunction.class_6911,
DensityFunction.NoisePos {
    private final GenerationShapeConfig generationShapeConfig;
    final int horizontalSize;
    final int height;
    final int minimumY;
    private final int x;
    private final int z;
    final int biomeX;
    final int biomeZ;
    final List<NoiseInterpolator> interpolators;
    final List<class_6949> field_36581;
    private final Map<DensityFunction, DensityFunction> field_36582 = new HashMap<DensityFunction, DensityFunction>();
    private final Long2IntMap field_36273 = new Long2IntOpenHashMap();
    private final AquiferSampler aquiferSampler;
    private final DensityFunction field_36583;
    private final BlockStateSampler blockStateSampler;
    private final Blender blender;
    private final class_6951 field_36585;
    private final class_6951 field_36586;
    private final DensityFunctionTypes.class_7050 field_37113;
    private long field_36587 = ChunkPos.MARKER;
    private Blender.class_6956 field_36588 = new Blender.class_6956(1.0, 0.0);
    final int field_36589;
    final int horizontalBlockSize;
    final int verticalBlockSize;
    boolean field_36592;
    boolean field_36593;
    private int field_36594;
    int field_36572;
    private int field_36573;
    int field_36574;
    int field_36575;
    int field_36576;
    long field_36577;
    long field_36578;
    int field_36579;
    private final DensityFunction.class_6911 field_36580 = new DensityFunction.class_6911(){

        @Override
        public DensityFunction.NoisePos method_40477(int i) {
            ChunkNoiseSampler.this.field_36572 = (i + ChunkNoiseSampler.this.minimumY) * ChunkNoiseSampler.this.verticalBlockSize;
            ++ChunkNoiseSampler.this.field_36577;
            ChunkNoiseSampler.this.field_36575 = 0;
            ChunkNoiseSampler.this.field_36579 = i;
            return ChunkNoiseSampler.this;
        }

        @Override
        public void method_40478(double[] ds, DensityFunction densityFunction) {
            for (int i = 0; i < ChunkNoiseSampler.this.height + 1; ++i) {
                ChunkNoiseSampler.this.field_36572 = (i + ChunkNoiseSampler.this.minimumY) * ChunkNoiseSampler.this.verticalBlockSize;
                ++ChunkNoiseSampler.this.field_36577;
                ChunkNoiseSampler.this.field_36575 = 0;
                ChunkNoiseSampler.this.field_36579 = i;
                ds[i] = densityFunction.sample(ChunkNoiseSampler.this);
            }
        }
    };

    public static ChunkNoiseSampler create(Chunk chunk, NoiseRouter noiseRouter, Supplier<DensityFunctionTypes.class_7050> noiseTypeSupplier, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender) {
        ChunkPos chunkPos = chunk.getPos();
        GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.generationShapeConfig();
        int i = Math.max(generationShapeConfig.minimumY(), chunk.getBottomY());
        int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), chunk.getTopY());
        int k = MathHelper.floorDiv(i, generationShapeConfig.verticalBlockSize());
        int l = MathHelper.floorDiv(j - i, generationShapeConfig.verticalBlockSize());
        return new ChunkNoiseSampler(16 / generationShapeConfig.horizontalBlockSize(), l, k, noiseRouter, chunkPos.getStartX(), chunkPos.getStartZ(), noiseTypeSupplier.get(), chunkGeneratorSettings, fluidLevelSampler, blender);
    }

    public static ChunkNoiseSampler create(int x, int z, int minimumY, int height, NoiseRouter noiseRouter, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler) {
        return new ChunkNoiseSampler(1, height, minimumY, noiseRouter, x, z, DensityFunctionTypes.Beardifier.INSTANCE, chunkGeneratorSettings, fluidLevelSampler, Blender.getNoBlending());
    }

    private ChunkNoiseSampler(int horizontalSize, int height, int minimumY, NoiseRouter noiseRouter, int x, int z, DensityFunctionTypes.class_7050 noiseType, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender) {
        int j;
        int i;
        this.generationShapeConfig = chunkGeneratorSettings.generationShapeConfig();
        this.horizontalSize = horizontalSize;
        this.height = height;
        this.minimumY = minimumY;
        this.horizontalBlockSize = this.generationShapeConfig.horizontalBlockSize();
        this.verticalBlockSize = this.generationShapeConfig.verticalBlockSize();
        this.x = Math.floorDiv(x, this.horizontalBlockSize);
        this.z = Math.floorDiv(z, this.horizontalBlockSize);
        this.interpolators = Lists.newArrayList();
        this.field_36581 = Lists.newArrayList();
        this.biomeX = BiomeCoords.fromBlock(x);
        this.biomeZ = BiomeCoords.fromBlock(z);
        this.field_36589 = BiomeCoords.fromBlock(horizontalSize * this.horizontalBlockSize);
        this.blender = blender;
        this.field_37113 = noiseType;
        this.field_36585 = new class_6951(new class_6946(), false);
        this.field_36586 = new class_6951(new class_6947(), false);
        for (i = 0; i <= this.field_36589; ++i) {
            j = this.biomeX + i;
            int k = BiomeCoords.toBlock(j);
            for (int l = 0; l <= this.field_36589; ++l) {
                int m = this.biomeZ + l;
                int n = BiomeCoords.toBlock(m);
                Blender.class_6956 lv = blender.method_39340(k, n);
                this.field_36585.field_36613[i][l] = lv.alpha();
                this.field_36586.field_36613[i][l] = lv.blendingOffset();
            }
        }
        if (!chunkGeneratorSettings.hasAquifers()) {
            this.aquiferSampler = AquiferSampler.seaLevel(fluidLevelSampler);
        } else {
            i = ChunkSectionPos.getSectionCoord(x);
            j = ChunkSectionPos.getSectionCoord(z);
            this.aquiferSampler = AquiferSampler.aquifer(this, new ChunkPos(i, j), noiseRouter.barrierNoise(), noiseRouter.fluidLevelFloodednessNoise(), noiseRouter.fluidLevelSpreadNoise(), noiseRouter.lavaNoise(), noiseRouter.aquiferPositionalRandomFactory(), minimumY * this.verticalBlockSize, height * this.verticalBlockSize, fluidLevelSampler);
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        DensityFunction densityFunction = DensityFunctionTypes.cacheAllInCell(DensityFunctionTypes.method_40486(noiseRouter.finalDensity(), DensityFunctionTypes.Beardifier.INSTANCE)).apply(this::method_40529);
        builder.add(noisePos -> this.aquiferSampler.apply(noisePos, densityFunction.sample(noisePos)));
        if (chunkGeneratorSettings.oreVeins()) {
            builder.add(OreVeinSampler.create(noiseRouter.veinToggle().apply(this::method_40529), noiseRouter.veinRidged().apply(this::method_40529), noiseRouter.veinGap().apply(this::method_40529), noiseRouter.oreVeinsPositionalRandomFactory()));
        }
        this.blockStateSampler = new ChainedBlockSource((List<BlockStateSampler>)((Object)builder.build()));
        this.field_36583 = noiseRouter.initialDensityWithoutJaggedness().apply(this::method_40529);
    }

    protected MultiNoiseUtil.MultiNoiseSampler createMultiNoiseSampler(NoiseRouter noiseRouter) {
        return new MultiNoiseUtil.MultiNoiseSampler(noiseRouter.temperature().apply(this::method_40529), noiseRouter.humidity().apply(this::method_40529), noiseRouter.continents().apply(this::method_40529), noiseRouter.erosion().apply(this::method_40529), noiseRouter.depth().apply(this::method_40529), noiseRouter.ridges().apply(this::method_40529), noiseRouter.spawnTarget());
    }

    @Nullable
    protected BlockState sampleBlockState() {
        return this.blockStateSampler.sample(this);
    }

    @Override
    public int blockX() {
        return this.field_36594 + this.field_36574;
    }

    @Override
    public int blockY() {
        return this.field_36572 + this.field_36575;
    }

    @Override
    public int blockZ() {
        return this.field_36573 + this.field_36576;
    }

    public int method_39900(int i, int j) {
        return this.field_36273.computeIfAbsent(ChunkPos.toLong(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(j)), this::method_39899);
    }

    private int method_39899(long l) {
        int i = ChunkPos.getPackedX(l);
        int j = ChunkPos.getPackedZ(l);
        return (int)DensityFunctions.method_40543(this.generationShapeConfig, this.field_36583, BiomeCoords.toBlock(i), BiomeCoords.toBlock(j));
    }

    @Override
    public Blender getBlender() {
        return this.blender;
    }

    private void method_40532(boolean bl, int i) {
        this.field_36594 = i * this.horizontalBlockSize;
        this.field_36574 = 0;
        for (int j = 0; j < this.horizontalSize + 1; ++j) {
            int k = this.z + j;
            this.field_36573 = k * this.horizontalBlockSize;
            this.field_36576 = 0;
            ++this.field_36578;
            for (NoiseInterpolator noiseInterpolator : this.interpolators) {
                double[] ds = (bl ? noiseInterpolator.startNoiseBuffer : noiseInterpolator.endNoiseBuffer)[j];
                noiseInterpolator.method_40470(ds, this.field_36580);
            }
        }
        ++this.field_36578;
    }

    public void sampleStartNoise() {
        if (this.field_36592) {
            throw new IllegalStateException("Staring interpolation twice");
        }
        this.field_36592 = true;
        this.field_36577 = 0L;
        this.method_40532(true, this.x);
    }

    public void sampleEndNoise(int x) {
        this.method_40532(false, this.x + x + 1);
        this.field_36594 = (this.x + x) * this.horizontalBlockSize;
    }

    @Override
    public ChunkNoiseSampler method_40477(int i) {
        int j = Math.floorMod(i, this.horizontalBlockSize);
        int k = Math.floorDiv(i, this.horizontalBlockSize);
        int l = Math.floorMod(k, this.horizontalBlockSize);
        int m = this.verticalBlockSize - 1 - Math.floorDiv(k, this.horizontalBlockSize);
        this.field_36574 = l;
        this.field_36575 = m;
        this.field_36576 = j;
        this.field_36579 = i;
        return this;
    }

    @Override
    public void method_40478(double[] ds, DensityFunction densityFunction) {
        this.field_36579 = 0;
        for (int i = this.verticalBlockSize - 1; i >= 0; --i) {
            this.field_36575 = i;
            for (int j = 0; j < this.horizontalBlockSize; ++j) {
                this.field_36574 = j;
                int k = 0;
                while (k < this.horizontalBlockSize) {
                    this.field_36576 = k++;
                    ds[this.field_36579++] = densityFunction.sample(this);
                }
            }
        }
    }

    public void sampleNoiseCorners(int noiseY, int noiseZ) {
        this.interpolators.forEach(interpolator -> interpolator.sampleNoiseCorners(noiseY, noiseZ));
        this.field_36593 = true;
        this.field_36572 = (noiseY + this.minimumY) * this.verticalBlockSize;
        this.field_36573 = (this.z + noiseZ) * this.horizontalBlockSize;
        ++this.field_36578;
        for (class_6949 lv : this.field_36581) {
            lv.field_36603.method_40470(lv.field_36604, this);
        }
        ++this.field_36578;
        this.field_36593 = false;
    }

    public void sampleNoiseY(int i, double d) {
        this.field_36575 = i - this.field_36572;
        this.interpolators.forEach(interpolator -> interpolator.sampleNoiseY(d));
    }

    public void sampleNoiseX(int i, double d) {
        this.field_36574 = i - this.field_36594;
        this.interpolators.forEach(interpolator -> interpolator.sampleNoiseX(d));
    }

    public void sampleNoise(int i, double d) {
        this.field_36576 = i - this.field_36573;
        ++this.field_36577;
        this.interpolators.forEach(interpolator -> interpolator.sampleNoise(d));
    }

    public void method_40537() {
        if (!this.field_36592) {
            throw new IllegalStateException("Staring interpolation twice");
        }
        this.field_36592 = false;
    }

    public void swapBuffers() {
        this.interpolators.forEach(NoiseInterpolator::swapBuffers);
    }

    public AquiferSampler getAquiferSampler() {
        return this.aquiferSampler;
    }

    Blender.class_6956 method_40535(int i, int j) {
        Blender.class_6956 lv;
        long l = ChunkPos.toLong(i, j);
        if (this.field_36587 == l) {
            return this.field_36588;
        }
        this.field_36587 = l;
        this.field_36588 = lv = this.blender.method_39340(i, j);
        return lv;
    }

    protected DensityFunction method_40529(DensityFunction densityFunction) {
        return this.field_36582.computeIfAbsent(densityFunction, this::method_40533);
    }

    private DensityFunction method_40533(DensityFunction densityFunction) {
        if (densityFunction instanceof DensityFunctionTypes.class_6927) {
            DensityFunctionTypes.class_6927 lv = (DensityFunctionTypes.class_6927)densityFunction;
            return switch (lv.type()) {
                default -> throw new IncompatibleClassChangeError();
                case DensityFunctionTypes.class_6927.Type.INTERPOLATED -> new NoiseInterpolator(lv.wrapped());
                case DensityFunctionTypes.class_6927.Type.FLAT_CACHE -> new class_6951(lv.wrapped(), true);
                case DensityFunctionTypes.class_6927.Type.CACHE2D -> new class_6948(lv.wrapped());
                case DensityFunctionTypes.class_6927.Type.CACHE_ONCE -> new class_6950(lv.wrapped());
                case DensityFunctionTypes.class_6927.Type.CACHE_ALL_IN_CELL -> new class_6949(lv.wrapped());
            };
        }
        if (this.blender != Blender.getNoBlending()) {
            if (densityFunction == DensityFunctionTypes.BlendAlpha.INSTANCE) {
                return this.field_36585;
            }
            if (densityFunction == DensityFunctionTypes.BlendOffset.INSTANCE) {
                return this.field_36586;
            }
        }
        if (densityFunction == DensityFunctionTypes.Beardifier.INSTANCE) {
            return this.field_37113;
        }
        if (densityFunction instanceof DensityFunctionTypes.class_7051) {
            DensityFunctionTypes.class_7051 lv2 = (DensityFunctionTypes.class_7051)densityFunction;
            return lv2.function().value();
        }
        return densityFunction;
    }

    @Override
    public /* synthetic */ DensityFunction.NoisePos method_40477(int i) {
        return this.method_40477(i);
    }

    class class_6951
    implements DensityFunctionTypes.Wrapper,
    ParentedNoiseType {
        private final DensityFunction field_36612;
        final double[][] field_36613;

        class_6951(DensityFunction densityFunction, boolean bl) {
            this.field_36612 = densityFunction;
            this.field_36613 = new double[ChunkNoiseSampler.this.field_36589 + 1][ChunkNoiseSampler.this.field_36589 + 1];
            if (bl) {
                for (int i = 0; i <= ChunkNoiseSampler.this.field_36589; ++i) {
                    int j = ChunkNoiseSampler.this.biomeX + i;
                    int k = BiomeCoords.toBlock(j);
                    for (int l = 0; l <= ChunkNoiseSampler.this.field_36589; ++l) {
                        int m = ChunkNoiseSampler.this.biomeZ + l;
                        int n = BiomeCoords.toBlock(m);
                        this.field_36613[i][l] = densityFunction.sample(new DensityFunction.UnblendedNoisePos(k, 0, n));
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
            int m = this.field_36613.length;
            if (k >= 0 && l >= 0 && k < m && l < m) {
                return this.field_36613[k][l];
            }
            return this.field_36612.sample(pos);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            arg.method_40478(ds, this);
        }

        @Override
        public DensityFunction wrapped() {
            return this.field_36612;
        }

        @Override
        public DensityFunctionTypes.class_6927.Type type() {
            return DensityFunctionTypes.class_6927.Type.FLAT_CACHE;
        }
    }

    class class_6946
    implements ParentedNoiseType {
        class_6946() {
        }

        @Override
        public DensityFunction wrapped() {
            return DensityFunctionTypes.BlendAlpha.INSTANCE;
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return ChunkNoiseSampler.this.method_40535(pos.blockX(), pos.blockZ()).alpha();
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            arg.method_40478(ds, this);
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
        public Codec<? extends DensityFunction> getCodec() {
            return DensityFunctionTypes.BlendAlpha.CODEC;
        }
    }

    class class_6947
    implements ParentedNoiseType {
        class_6947() {
        }

        @Override
        public DensityFunction wrapped() {
            return DensityFunctionTypes.BlendOffset.INSTANCE;
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return ChunkNoiseSampler.this.method_40535(pos.blockX(), pos.blockZ()).blendingOffset();
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            arg.method_40478(ds, this);
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
        public Codec<? extends DensityFunction> getCodec() {
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
            this.startNoiseBuffer = this.createBuffer(ChunkNoiseSampler.this.height, ChunkNoiseSampler.this.horizontalSize);
            this.endNoiseBuffer = this.createBuffer(ChunkNoiseSampler.this.height, ChunkNoiseSampler.this.horizontalSize);
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

        void sampleNoise(double deltaZ) {
            this.result = MathHelper.lerp(deltaZ, this.z0, this.z1);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            if (pos != ChunkNoiseSampler.this) {
                return this.columnSampler.sample(pos);
            }
            if (!ChunkNoiseSampler.this.field_36592) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            }
            if (ChunkNoiseSampler.this.field_36593) {
                return MathHelper.lerp3((double)ChunkNoiseSampler.this.field_36574 / (double)ChunkNoiseSampler.this.horizontalBlockSize, (double)ChunkNoiseSampler.this.field_36575 / (double)ChunkNoiseSampler.this.verticalBlockSize, (double)ChunkNoiseSampler.this.field_36576 / (double)ChunkNoiseSampler.this.horizontalBlockSize, this.x0y0z0, this.x1y0z0, this.x0y1z0, this.x1y1z0, this.x0y0z1, this.x1y0z1, this.x0y1z1, this.x1y1z1);
            }
            return this.result;
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            if (ChunkNoiseSampler.this.field_36593) {
                arg.method_40478(ds, this);
                return;
            }
            this.wrapped().method_40470(ds, arg);
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
        public DensityFunctionTypes.class_6927.Type type() {
            return DensityFunctionTypes.class_6927.Type.INTERPOLATED;
        }
    }

    class class_6949
    implements DensityFunctionTypes.Wrapper,
    ParentedNoiseType {
        final DensityFunction field_36603;
        final double[] field_36604;

        class_6949(DensityFunction densityFunction) {
            this.field_36603 = densityFunction;
            this.field_36604 = new double[ChunkNoiseSampler.this.horizontalBlockSize * ChunkNoiseSampler.this.horizontalBlockSize * ChunkNoiseSampler.this.verticalBlockSize];
            ChunkNoiseSampler.this.field_36581.add(this);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            if (pos != ChunkNoiseSampler.this) {
                return this.field_36603.sample(pos);
            }
            if (!ChunkNoiseSampler.this.field_36592) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            }
            int i = ChunkNoiseSampler.this.field_36574;
            int j = ChunkNoiseSampler.this.field_36575;
            int k = ChunkNoiseSampler.this.field_36576;
            if (i >= 0 && j >= 0 && k >= 0 && i < ChunkNoiseSampler.this.horizontalBlockSize && j < ChunkNoiseSampler.this.verticalBlockSize && k < ChunkNoiseSampler.this.horizontalBlockSize) {
                return this.field_36604[((ChunkNoiseSampler.this.verticalBlockSize - 1 - j) * ChunkNoiseSampler.this.horizontalBlockSize + i) * ChunkNoiseSampler.this.horizontalBlockSize + k];
            }
            return this.field_36603.sample(pos);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            arg.method_40478(ds, this);
        }

        @Override
        public DensityFunction wrapped() {
            return this.field_36603;
        }

        @Override
        public DensityFunctionTypes.class_6927.Type type() {
            return DensityFunctionTypes.class_6927.Type.CACHE_ALL_IN_CELL;
        }
    }

    static class class_6948
    implements DensityFunctionTypes.Wrapper,
    ParentedNoiseType {
        private final DensityFunction field_36599;
        private long field_36600 = ChunkPos.MARKER;
        private double field_36601;

        class_6948(DensityFunction densityFunction) {
            this.field_36599 = densityFunction;
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            double d;
            int j;
            int i = pos.blockX();
            long l = ChunkPos.toLong(i, j = pos.blockZ());
            if (this.field_36600 == l) {
                return this.field_36601;
            }
            this.field_36600 = l;
            this.field_36601 = d = this.field_36599.sample(pos);
            return d;
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.field_36599.method_40470(ds, arg);
        }

        @Override
        public DensityFunction wrapped() {
            return this.field_36599;
        }

        @Override
        public DensityFunctionTypes.class_6927.Type type() {
            return DensityFunctionTypes.class_6927.Type.CACHE2D;
        }
    }

    class class_6950
    implements DensityFunctionTypes.Wrapper,
    ParentedNoiseType {
        private final DensityFunction field_36606;
        private long field_36607;
        private long field_36608;
        private double field_36609;
        @Nullable
        private double[] field_36610;

        class_6950(DensityFunction densityFunction) {
            this.field_36606 = densityFunction;
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            double d;
            if (pos != ChunkNoiseSampler.this) {
                return this.field_36606.sample(pos);
            }
            if (this.field_36610 != null && this.field_36608 == ChunkNoiseSampler.this.field_36578) {
                return this.field_36610[ChunkNoiseSampler.this.field_36579];
            }
            if (this.field_36607 == ChunkNoiseSampler.this.field_36577) {
                return this.field_36609;
            }
            this.field_36607 = ChunkNoiseSampler.this.field_36577;
            this.field_36609 = d = this.field_36606.sample(pos);
            return d;
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            if (this.field_36610 != null && this.field_36608 == ChunkNoiseSampler.this.field_36578) {
                System.arraycopy(this.field_36610, 0, ds, 0, ds.length);
                return;
            }
            this.wrapped().method_40470(ds, arg);
            if (this.field_36610 != null && this.field_36610.length == ds.length) {
                System.arraycopy(ds, 0, this.field_36610, 0, ds.length);
            } else {
                this.field_36610 = (double[])ds.clone();
            }
            this.field_36608 = ChunkNoiseSampler.this.field_36578;
        }

        @Override
        public DensityFunction wrapped() {
            return this.field_36606;
        }

        @Override
        public DensityFunctionTypes.class_6927.Type type() {
            return DensityFunctionTypes.class_6927.Type.CACHE_ONCE;
        }
    }

    static interface ParentedNoiseType
    extends DensityFunction {
        public DensityFunction wrapped();

        @Override
        default public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return this.wrapped().apply(visitor);
        }

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

