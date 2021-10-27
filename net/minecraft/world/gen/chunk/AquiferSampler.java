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
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.jetbrains.annotations.Nullable;

public interface AquiferSampler {
    public static AquiferSampler aquifer(ChunkNoiseSampler chunkNoiseSampler, ChunkPos chunkPos, DoublePerlinNoiseSampler edgeDensityNoise, DoublePerlinNoiseSampler doublePerlinNoiseSampler, DoublePerlinNoiseSampler doublePerlinNoiseSampler2, DoublePerlinNoiseSampler fluidTypeNoise, RandomDeriver randomDeriver, NoiseColumnSampler columnSampler, int i, int j, FluidLevelSampler fluidLevelSampler) {
        return new Impl(chunkNoiseSampler, chunkPos, edgeDensityNoise, doublePerlinNoiseSampler, doublePerlinNoiseSampler2, fluidTypeNoise, randomDeriver, columnSampler, i, j, fluidLevelSampler);
    }

    public static AquiferSampler seaLevel(final FluidLevelSampler fluidLevelSampler) {
        return new AquiferSampler(){

            @Override
            @Nullable
            public BlockState apply(int x, int y, int z, double d, double e) {
                if (e > 0.0) {
                    return null;
                }
                return fluidLevelSampler.getFluidLevel(x, y, z).getBlockState(y);
            }

            @Override
            public boolean needsFluidTick() {
                return false;
            }
        };
    }

    @Nullable
    public BlockState apply(int var1, int var2, int var3, double var4, double var6);

    public boolean needsFluidTick();

    public static class Impl
    implements AquiferSampler,
    FluidLevelSampler {
        private static final int field_31451 = 10;
        private static final int field_31452 = 9;
        private static final int field_31453 = 10;
        private static final int field_31454 = 6;
        private static final int field_31455 = 3;
        private static final int field_31456 = 6;
        private static final int field_31457 = 16;
        private static final int field_31458 = 12;
        private static final int field_31459 = 16;
        private final ChunkNoiseSampler chunkNoiseSampler;
        private final DoublePerlinNoiseSampler edgeDensityNoise;
        private final DoublePerlinNoiseSampler field_35122;
        private final DoublePerlinNoiseSampler field_35123;
        private final DoublePerlinNoiseSampler fluidTypeNoise;
        private final RandomDeriver field_34579;
        private final FluidLevel[] waterLevels;
        private final long[] blockPositions;
        private final FluidLevelSampler fluidLevelSampler;
        private boolean needsFluidTick;
        private final NoiseColumnSampler columnSampler;
        private final int startX;
        private final int startY;
        private final int startZ;
        private final int sizeX;
        private final int sizeZ;
        private static final int[][] field_34581 = new int[][]{{-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}};

        Impl(ChunkNoiseSampler chunkNoiseSampler, ChunkPos chunkPos, DoublePerlinNoiseSampler edgeDensityNoise, DoublePerlinNoiseSampler doublePerlinNoiseSampler, DoublePerlinNoiseSampler doublePerlinNoiseSampler2, DoublePerlinNoiseSampler fluidTypeNoise, RandomDeriver randomDeriver, NoiseColumnSampler columnSampler, int i, int j, FluidLevelSampler fluidLevelSampler) {
            this.chunkNoiseSampler = chunkNoiseSampler;
            this.edgeDensityNoise = edgeDensityNoise;
            this.field_35122 = doublePerlinNoiseSampler;
            this.field_35123 = doublePerlinNoiseSampler2;
            this.fluidTypeNoise = fluidTypeNoise;
            this.field_34579 = randomDeriver;
            this.columnSampler = columnSampler;
            this.startX = this.getLocalX(chunkPos.getStartX()) - 1;
            this.fluidLevelSampler = fluidLevelSampler;
            int k = this.getLocalX(chunkPos.getEndX()) + 1;
            this.sizeX = k - this.startX + 1;
            this.startY = this.getLocalY(i) - 1;
            int l = this.getLocalY(i + j) + 1;
            int m = l - this.startY + 1;
            this.startZ = this.getLocalZ(chunkPos.getStartZ()) - 1;
            int n = this.getLocalZ(chunkPos.getEndZ()) + 1;
            this.sizeZ = n - this.startZ + 1;
            int o = this.sizeX * m * this.sizeZ;
            this.waterLevels = new FluidLevel[o];
            this.blockPositions = new long[o];
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
        public BlockState apply(int x, int y, int z, double d, double e) {
            if (d <= -64.0) {
                return this.fluidLevelSampler.getFluidLevel(x, y, z).getBlockState(y);
            }
            if (e <= 0.0) {
                boolean bl;
                double f;
                BlockState blockState;
                FluidLevel fluidLevel = this.fluidLevelSampler.getFluidLevel(x, y, z);
                if (fluidLevel.getBlockState(y).isOf(Blocks.LAVA)) {
                    blockState = Blocks.LAVA.getDefaultState();
                    f = 0.0;
                    bl = false;
                } else {
                    int i = Math.floorDiv(x - 5, 16);
                    int j = Math.floorDiv(y + 1, 12);
                    int k = Math.floorDiv(z - 5, 16);
                    int l = Integer.MAX_VALUE;
                    int m = Integer.MAX_VALUE;
                    int n = Integer.MAX_VALUE;
                    long o = 0L;
                    long p = 0L;
                    long q = 0L;
                    for (int r = 0; r <= 1; ++r) {
                        for (int s = -1; s <= 1; ++s) {
                            for (int t = 0; t <= 1; ++t) {
                                long ac;
                                int u = i + r;
                                int v = j + s;
                                int w = k + t;
                                int aa = this.index(u, v, w);
                                long ab = this.blockPositions[aa];
                                if (ab != Long.MAX_VALUE) {
                                    ac = ab;
                                } else {
                                    AbstractRandom abstractRandom = this.field_34579.createRandom(u, v, w);
                                    this.blockPositions[aa] = ac = BlockPos.asLong(u * 16 + abstractRandom.nextInt(10), v * 12 + abstractRandom.nextInt(9), w * 16 + abstractRandom.nextInt(10));
                                }
                                int ad = BlockPos.unpackLongX(ac) - x;
                                int ae = BlockPos.unpackLongY(ac) - y;
                                int af = BlockPos.unpackLongZ(ac) - z;
                                int ag = ad * ad + ae * ae + af * af;
                                if (l >= ag) {
                                    q = p;
                                    p = o;
                                    o = ac;
                                    n = m;
                                    m = l;
                                    l = ag;
                                    continue;
                                }
                                if (m >= ag) {
                                    q = p;
                                    p = ac;
                                    n = m;
                                    m = ag;
                                    continue;
                                }
                                if (n < ag) continue;
                                q = ac;
                                n = ag;
                            }
                        }
                    }
                    FluidLevel fluidLevel2 = this.getWaterLevel(o);
                    FluidLevel fluidLevel3 = this.getWaterLevel(p);
                    FluidLevel fluidLevel4 = this.getWaterLevel(q);
                    double g = this.maxDistance(l, m);
                    double h = this.maxDistance(l, n);
                    double ah = this.maxDistance(m, n);
                    boolean bl2 = bl = g > 0.0;
                    if (fluidLevel2.getBlockState(y).isOf(Blocks.WATER) && this.fluidLevelSampler.getFluidLevel(x, y - 1, z).getBlockState(y - 1).isOf(Blocks.LAVA)) {
                        f = 1.0;
                    } else if (g > -1.0) {
                        MutableDouble mutableDouble = new MutableDouble(Double.NaN);
                        double ai = this.calculateDensity(x, y, z, mutableDouble, fluidLevel2, fluidLevel3);
                        double aj = this.calculateDensity(x, y, z, mutableDouble, fluidLevel2, fluidLevel4);
                        double ak = this.calculateDensity(x, y, z, mutableDouble, fluidLevel3, fluidLevel4);
                        double al = Math.max(0.0, g);
                        double am = Math.max(0.0, h);
                        double an = Math.max(0.0, ah);
                        double ao = 2.0 * al * Math.max(ai, Math.max(aj * am, ak * an));
                        f = Math.max(0.0, ao);
                    } else {
                        f = 0.0;
                    }
                    blockState = fluidLevel2.getBlockState(y);
                }
                if (e + f <= 0.0) {
                    this.needsFluidTick = bl;
                    return blockState;
                }
            }
            this.needsFluidTick = false;
            return null;
        }

        @Override
        public boolean needsFluidTick() {
            return this.needsFluidTick;
        }

        private double maxDistance(int a, int b) {
            double d = 25.0;
            return 1.0 - (double)Math.abs(b - a) / 25.0;
        }

        private double calculateDensity(int i, int j, int k, MutableDouble mutableDouble, FluidLevel fluidLevel, FluidLevel fluidLevel2) {
            double r;
            BlockState blockState = fluidLevel.getBlockState(j);
            BlockState blockState2 = fluidLevel2.getBlockState(j);
            if (blockState.isOf(Blocks.LAVA) && blockState2.isOf(Blocks.WATER) || blockState.isOf(Blocks.WATER) && blockState2.isOf(Blocks.LAVA)) {
                return 1.0;
            }
            int l = Math.abs(fluidLevel.y - fluidLevel2.y);
            if (l == 0) {
                return 0.0;
            }
            double d = 0.5 * (double)(fluidLevel.y + fluidLevel2.y);
            double e = (double)j + 0.5 - d;
            double f = (double)l / 2.0;
            double g = 0.0;
            double h = 2.5;
            double m = 1.5;
            double n = 3.0;
            double o = 10.0;
            double p = 3.0;
            double q = f - Math.abs(e);
            double s = e > 0.0 ? ((r = 0.0 + q) > 0.0 ? r / 1.5 : r / 2.5) : ((r = 3.0 + q) > 0.0 ? r / 3.0 : r / 10.0);
            if (s < -2.0 || s > 2.0) {
                return s;
            }
            r = mutableDouble.getValue();
            if (Double.isNaN(r)) {
                double t = 0.5;
                double u = this.edgeDensityNoise.sample(i, (double)j * 0.5, k);
                mutableDouble.setValue(u);
                return u + s;
            }
            return r + s;
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
            this.waterLevels[o] = fluidLevel2 = this.getFluidLevel(i, j, k);
            return fluidLevel2;
        }

        @Override
        public FluidLevel getFluidLevel(int x, int y, int z) {
            double g;
            FluidLevel fluidLevel = this.fluidLevelSampler.getFluidLevel(x, y, z);
            int i = Integer.MAX_VALUE;
            int j = y + 12;
            int k = y - 12;
            boolean bl = false;
            for (int[] is : field_34581) {
                FluidLevel fluidLevel2;
                boolean bl3;
                boolean bl2;
                int l = x + ChunkSectionPos.getBlockCoord(is[0]);
                int m = z + ChunkSectionPos.getBlockCoord(is[1]);
                int n = this.columnSampler.method_38383(l, m, this.chunkNoiseSampler.getTerrainNoisePoint(this.columnSampler, BiomeCoords.fromBlock(l), BiomeCoords.fromBlock(m)));
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
            int p = i + 8 - y;
            int q = 64;
            double d = bl ? MathHelper.clampedLerpFromProgress((double)p, 0.0, 64.0, 1.0, 0.0) : 0.0;
            double e = 0.67;
            double f = MathHelper.clamp(this.field_35122.sample(x, (double)y * 0.67, z), -1.0, 1.0);
            if (f > (g = MathHelper.lerpFromProgress(d, 1.0, 0.0, -0.3, 0.8))) {
                return fluidLevel;
            }
            double h = MathHelper.lerpFromProgress(d, 1.0, 0.0, -0.8, 0.4);
            if (f <= h) {
                return new FluidLevel(DimensionType.field_35479, fluidLevel.state);
            }
            int r = 16;
            int s = 40;
            int t = Math.floorDiv(x, 16);
            int u = Math.floorDiv(y, 40);
            int v = Math.floorDiv(z, 16);
            int w = u * 40 + 20;
            int aa = 10;
            double ab = this.field_35123.sample(t, (double)u / 1.4, v) * 10.0;
            int ac = MathHelper.roundDownToMultiple(ab, 3);
            int ad = w + ac;
            int ae = Math.min(i, ad);
            BlockState blockState = this.method_38993(x, y, z, fluidLevel, ad);
            return new FluidLevel(ae, blockState);
        }

        private BlockState method_38993(int i, int j, int k, FluidLevel fluidLevel, int l) {
            if (l <= -10) {
                int q;
                int p;
                int m = 64;
                int n = 40;
                int o = Math.floorDiv(i, 64);
                double d = this.fluidTypeNoise.sample(o, p = Math.floorDiv(j, 40), q = Math.floorDiv(k, 64));
                if (Math.abs(d) > 0.3) {
                    return Blocks.LAVA.getDefaultState();
                }
            }
            return fluidLevel.state;
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

