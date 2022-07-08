/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseRouter;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.jetbrains.annotations.Nullable;

public interface AquiferSampler {
    public static AquiferSampler aquifer(ChunkNoiseSampler chunkNoiseSampler, ChunkPos chunkPos, NoiseRouter noiseRouter, RandomSplitter randomSplitter, int minimumY, int height, FluidLevelSampler fluidLevelSampler) {
        return new Impl(chunkNoiseSampler, chunkPos, noiseRouter, randomSplitter, minimumY, height, fluidLevelSampler);
    }

    public static AquiferSampler seaLevel(final FluidLevelSampler fluidLevelSampler) {
        return new AquiferSampler(){

            @Override
            @Nullable
            public BlockState apply(DensityFunction.NoisePos pos, double density) {
                if (density > 0.0) {
                    return null;
                }
                return fluidLevelSampler.getFluidLevel(pos.blockX(), pos.blockY(), pos.blockZ()).getBlockState(pos.blockY());
            }

            @Override
            public boolean needsFluidTick() {
                return false;
            }
        };
    }

    @Nullable
    public BlockState apply(DensityFunction.NoisePos var1, double var2);

    public boolean needsFluidTick();

    public static class Impl
    implements AquiferSampler {
        private static final int field_31451 = 10;
        private static final int field_31452 = 9;
        private static final int field_31453 = 10;
        private static final int field_31454 = 6;
        private static final int field_31455 = 3;
        private static final int field_31456 = 6;
        private static final int field_31457 = 16;
        private static final int field_31458 = 12;
        private static final int field_31459 = 16;
        private static final int field_36220 = 11;
        private static final double NEEDS_FLUID_TICK_DISTANCE_THRESHOLD = Impl.maxDistance(MathHelper.square(10), MathHelper.square(12));
        private final ChunkNoiseSampler chunkNoiseSampler;
        private final DensityFunction barrierNoise;
        private final DensityFunction fluidLevelFloodednessNoise;
        private final DensityFunction fluidLevelSpreadNoise;
        private final DensityFunction fluidTypeNoise;
        private final RandomSplitter randomDeriver;
        private final FluidLevel[] waterLevels;
        private final long[] blockPositions;
        private final FluidLevelSampler fluidLevelSampler;
        private final DensityFunction erosionDensityFunction;
        private final DensityFunction depthDensityFunction;
        private boolean needsFluidTick;
        private final int startX;
        private final int startY;
        private final int startZ;
        private final int sizeX;
        private final int sizeZ;
        private static final int[][] field_34581 = new int[][]{{-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}};

        Impl(ChunkNoiseSampler chunkNoiseSampler, ChunkPos chunkPos, NoiseRouter noiseRouter, RandomSplitter randomSplitter, int minimumY, int height, FluidLevelSampler fluidLevelSampler) {
            this.chunkNoiseSampler = chunkNoiseSampler;
            this.barrierNoise = noiseRouter.barrierNoise();
            this.fluidLevelFloodednessNoise = noiseRouter.fluidLevelFloodednessNoise();
            this.fluidLevelSpreadNoise = noiseRouter.fluidLevelSpreadNoise();
            this.fluidTypeNoise = noiseRouter.lavaNoise();
            this.erosionDensityFunction = noiseRouter.erosion();
            this.depthDensityFunction = noiseRouter.depth();
            this.randomDeriver = randomSplitter;
            this.startX = this.getLocalX(chunkPos.getStartX()) - 1;
            this.fluidLevelSampler = fluidLevelSampler;
            int i = this.getLocalX(chunkPos.getEndX()) + 1;
            this.sizeX = i - this.startX + 1;
            this.startY = this.getLocalY(minimumY) - 1;
            int j = this.getLocalY(minimumY + height) + 1;
            int k = j - this.startY + 1;
            this.startZ = this.getLocalZ(chunkPos.getStartZ()) - 1;
            int l = this.getLocalZ(chunkPos.getEndZ()) + 1;
            this.sizeZ = l - this.startZ + 1;
            int m = this.sizeX * k * this.sizeZ;
            this.waterLevels = new FluidLevel[m];
            this.blockPositions = new long[m];
            Arrays.fill(this.blockPositions, Long.MAX_VALUE);
        }

        private int index(int x, int y, int z) {
            int i = x - this.startX;
            int j = y - this.startY;
            int k = z - this.startZ;
            return (j * this.sizeZ + k) * this.sizeX + i;
        }

        @Override
        @Nullable
        public BlockState apply(DensityFunction.NoisePos pos, double density) {
            double h;
            double g;
            BlockState blockState;
            int i = pos.blockX();
            int j = pos.blockY();
            int k = pos.blockZ();
            if (density > 0.0) {
                this.needsFluidTick = false;
                return null;
            }
            FluidLevel fluidLevel = this.fluidLevelSampler.getFluidLevel(i, j, k);
            if (fluidLevel.getBlockState(j).isOf(Blocks.LAVA)) {
                this.needsFluidTick = false;
                return Blocks.LAVA.getDefaultState();
            }
            int l = Math.floorDiv(i - 5, 16);
            int m = Math.floorDiv(j + 1, 12);
            int n = Math.floorDiv(k - 5, 16);
            int o = Integer.MAX_VALUE;
            int p = Integer.MAX_VALUE;
            int q = Integer.MAX_VALUE;
            long r = 0L;
            long s = 0L;
            long t = 0L;
            for (int u = 0; u <= 1; ++u) {
                for (int v = -1; v <= 1; ++v) {
                    for (int w = 0; w <= 1; ++w) {
                        long ac;
                        int x = l + u;
                        int y = m + v;
                        int z = n + w;
                        int aa = this.index(x, y, z);
                        long ab = this.blockPositions[aa];
                        if (ab != Long.MAX_VALUE) {
                            ac = ab;
                        } else {
                            Random random = this.randomDeriver.split(x, y, z);
                            this.blockPositions[aa] = ac = BlockPos.asLong(x * 16 + random.nextInt(10), y * 12 + random.nextInt(9), z * 16 + random.nextInt(10));
                        }
                        int ad = BlockPos.unpackLongX(ac) - i;
                        int ae = BlockPos.unpackLongY(ac) - j;
                        int af = BlockPos.unpackLongZ(ac) - k;
                        int ag = ad * ad + ae * ae + af * af;
                        if (o >= ag) {
                            t = s;
                            s = r;
                            r = ac;
                            q = p;
                            p = o;
                            o = ag;
                            continue;
                        }
                        if (p >= ag) {
                            t = s;
                            s = ac;
                            q = p;
                            p = ag;
                            continue;
                        }
                        if (q < ag) continue;
                        t = ac;
                        q = ag;
                    }
                }
            }
            FluidLevel fluidLevel2 = this.getWaterLevel(r);
            double d = Impl.maxDistance(o, p);
            BlockState blockState2 = blockState = fluidLevel2.getBlockState(j);
            if (d <= 0.0) {
                this.needsFluidTick = d >= NEEDS_FLUID_TICK_DISTANCE_THRESHOLD;
                return blockState2;
            }
            if (blockState.isOf(Blocks.WATER) && this.fluidLevelSampler.getFluidLevel(i, j - 1, k).getBlockState(j - 1).isOf(Blocks.LAVA)) {
                this.needsFluidTick = true;
                return blockState2;
            }
            MutableDouble mutableDouble = new MutableDouble(Double.NaN);
            FluidLevel fluidLevel3 = this.getWaterLevel(s);
            double e = d * this.calculateDensity(pos, mutableDouble, fluidLevel2, fluidLevel3);
            if (density + e > 0.0) {
                this.needsFluidTick = false;
                return null;
            }
            FluidLevel fluidLevel4 = this.getWaterLevel(t);
            double f = Impl.maxDistance(o, q);
            if (f > 0.0 && density + (g = d * f * this.calculateDensity(pos, mutableDouble, fluidLevel2, fluidLevel4)) > 0.0) {
                this.needsFluidTick = false;
                return null;
            }
            double g2 = Impl.maxDistance(p, q);
            if (g2 > 0.0 && density + (h = d * g2 * this.calculateDensity(pos, mutableDouble, fluidLevel3, fluidLevel4)) > 0.0) {
                this.needsFluidTick = false;
                return null;
            }
            this.needsFluidTick = true;
            return blockState2;
        }

        @Override
        public boolean needsFluidTick() {
            return this.needsFluidTick;
        }

        private static double maxDistance(int i, int a) {
            double d = 25.0;
            return 1.0 - (double)Math.abs(a - i) / 25.0;
        }

        private double calculateDensity(DensityFunction.NoisePos pos, MutableDouble mutableDouble, FluidLevel fluidLevel, FluidLevel fluidLevel2) {
            double r;
            double p;
            int i = pos.blockY();
            BlockState blockState = fluidLevel.getBlockState(i);
            BlockState blockState2 = fluidLevel2.getBlockState(i);
            if (blockState.isOf(Blocks.LAVA) && blockState2.isOf(Blocks.WATER) || blockState.isOf(Blocks.WATER) && blockState2.isOf(Blocks.LAVA)) {
                return 2.0;
            }
            int j = Math.abs(fluidLevel.y - fluidLevel2.y);
            if (j == 0) {
                return 0.0;
            }
            double d = 0.5 * (double)(fluidLevel.y + fluidLevel2.y);
            double e = (double)i + 0.5 - d;
            double f = (double)j / 2.0;
            double g = 0.0;
            double h = 2.5;
            double k = 1.5;
            double l = 3.0;
            double m = 10.0;
            double n = 3.0;
            double o = f - Math.abs(e);
            double q = e > 0.0 ? ((p = 0.0 + o) > 0.0 ? p / 1.5 : p / 2.5) : ((p = 3.0 + o) > 0.0 ? p / 3.0 : p / 10.0);
            p = 2.0;
            if (q < -2.0 || q > 2.0) {
                r = 0.0;
            } else {
                double s = mutableDouble.getValue();
                if (Double.isNaN(s)) {
                    double t = this.barrierNoise.sample(pos);
                    mutableDouble.setValue(t);
                    r = t;
                } else {
                    r = s;
                }
            }
            return 2.0 * (r + q);
        }

        private int getLocalX(int x) {
            return Math.floorDiv(x, 16);
        }

        private int getLocalY(int y) {
            return Math.floorDiv(y, 12);
        }

        private int getLocalZ(int z) {
            return Math.floorDiv(z, 16);
        }

        private FluidLevel getWaterLevel(long pos) {
            FluidLevel fluidLevel2;
            int n;
            int m;
            int i = BlockPos.unpackLongX(pos);
            int j = BlockPos.unpackLongY(pos);
            int k = BlockPos.unpackLongZ(pos);
            int l = this.getLocalX(i);
            int o = this.index(l, m = this.getLocalY(j), n = this.getLocalZ(k));
            FluidLevel fluidLevel = this.waterLevels[o];
            if (fluidLevel != null) {
                return fluidLevel;
            }
            this.waterLevels[o] = fluidLevel2 = this.method_40463(i, j, k);
            return fluidLevel2;
        }

        private FluidLevel method_40463(int blockX, int blockY, int blockZ) {
            FluidLevel fluidLevel = this.fluidLevelSampler.getFluidLevel(blockX, blockY, blockZ);
            int i = Integer.MAX_VALUE;
            int j = blockY + 12;
            int k = blockY - 12;
            boolean bl = false;
            for (int[] is : field_34581) {
                FluidLevel fluidLevel2;
                boolean bl3;
                boolean bl2;
                int l = blockX + ChunkSectionPos.getBlockCoord(is[0]);
                int m = blockZ + ChunkSectionPos.getBlockCoord(is[1]);
                int n = this.chunkNoiseSampler.estimateSurfaceHeight(l, m);
                int o = n + 8;
                boolean bl4 = bl2 = is[0] == 0 && is[1] == 0;
                if (bl2 && k > o) {
                    return fluidLevel;
                }
                boolean bl5 = bl3 = j > o;
                if ((bl3 || bl2) && !(fluidLevel2 = this.fluidLevelSampler.getFluidLevel(l, o, m)).getBlockState(o).isAir()) {
                    if (bl2) {
                        bl = true;
                    }
                    if (bl3) {
                        return fluidLevel2;
                    }
                }
                i = Math.min(i, n);
            }
            int p = this.method_42354(blockX, blockY, blockZ, fluidLevel, i, bl);
            return new FluidLevel(p, this.method_42353(blockX, blockY, blockZ, fluidLevel, p));
        }

        private int method_42354(int blockX, int blockY, int blockZ, FluidLevel fluidLevel, int surfaceHeightEstimate, boolean bl) {
            int i;
            double e;
            double d;
            DensityFunction.UnblendedNoisePos unblendedNoisePos = new DensityFunction.UnblendedNoisePos(blockX, blockY, blockZ);
            if (VanillaBiomeParameters.method_43718(this.erosionDensityFunction.sample(unblendedNoisePos), this.depthDensityFunction.sample(unblendedNoisePos))) {
                d = -1.0;
                e = -1.0;
            } else {
                i = surfaceHeightEstimate + 8 - blockY;
                int j = 64;
                double f = bl ? MathHelper.clampedLerpFromProgress((double)i, 0.0, 64.0, 1.0, 0.0) : 0.0;
                double g = MathHelper.clamp(this.fluidLevelFloodednessNoise.sample(unblendedNoisePos), -1.0, 1.0);
                double h = MathHelper.lerpFromProgress(f, 1.0, 0.0, -0.3, 0.8);
                double k = MathHelper.lerpFromProgress(f, 1.0, 0.0, -0.8, 0.4);
                d = g - k;
                e = g - h;
            }
            i = e > 0.0 ? fluidLevel.y : (d > 0.0 ? this.method_42352(blockX, blockY, blockZ, surfaceHeightEstimate) : DimensionType.field_35479);
            return i;
        }

        private int method_42352(int i, int j, int k, int l) {
            int m = 16;
            int n = 40;
            int o = Math.floorDiv(i, 16);
            int p = Math.floorDiv(j, 40);
            int q = Math.floorDiv(k, 16);
            int r = p * 40 + 20;
            int s = 10;
            double d = this.fluidLevelSpreadNoise.sample(new DensityFunction.UnblendedNoisePos(o, p, q)) * 10.0;
            int t = MathHelper.roundDownToMultiple(d, 3);
            int u = r + t;
            return Math.min(l, u);
        }

        private BlockState method_42353(int i, int j, int k, FluidLevel fluidLevel, int l) {
            BlockState blockState = fluidLevel.state;
            if (l <= -10 && l != DimensionType.field_35479 && fluidLevel.state != Blocks.LAVA.getDefaultState()) {
                int q;
                int p;
                int m = 64;
                int n = 40;
                int o = Math.floorDiv(i, 64);
                double d = this.fluidTypeNoise.sample(new DensityFunction.UnblendedNoisePos(o, p = Math.floorDiv(j, 40), q = Math.floorDiv(k, 64)));
                if (Math.abs(d) > 0.3) {
                    blockState = Blocks.LAVA.getDefaultState();
                }
            }
            return blockState;
        }
    }

    public static interface FluidLevelSampler {
        public FluidLevel getFluidLevel(int var1, int var2, int var3);
    }

    public static final class FluidLevel {
        final int y;
        final BlockState state;

        public FluidLevel(int y, BlockState state) {
            this.y = y;
            this.state = state;
        }

        public BlockState getBlockState(int y) {
            return y < this.y ? this.state : Blocks.AIR.getDefaultState();
        }
    }
}

