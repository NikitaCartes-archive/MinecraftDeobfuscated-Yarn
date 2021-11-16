/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.tick.OrderedTick;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class class_6748 {
    private static final class_6748 field_35501 = new class_6748(null, List.of(), List.of()){

        @Override
        public TerrainNoisePoint method_39340(int i, int j, TerrainNoisePoint terrainNoisePoint) {
            return terrainNoisePoint;
        }

        @Override
        public double method_39338(int i, int j, int k, double d) {
            return d;
        }

        @Override
        public BiomeSupplier method_39563(BiomeSupplier biomeSupplier) {
            return biomeSupplier;
        }
    };
    private static final DoublePerlinNoiseSampler field_35681 = DoublePerlinNoiseSampler.create(new Xoroshiro128PlusPlusRandom(42L), BuiltinRegistries.NOISE_PARAMETERS.getOrThrow(NoiseParametersKeys.OFFSET));
    private static final int field_35502 = BiomeCoords.fromChunk(7) - 1;
    private static final int field_35503 = BiomeCoords.toChunk(field_35502 + 3);
    private static final int field_35504 = 2;
    private static final int field_35505 = BiomeCoords.toChunk(5);
    private static final double field_35506 = 10.0;
    private static final double field_35507 = 0.0;
    private final ChunkRegion field_35508;
    private final List<class_6782> field_35509;
    private final List<class_6782> field_35510;

    public static class_6748 method_39336() {
        return field_35501;
    }

    public static class_6748 method_39342(@Nullable ChunkRegion chunkRegion) {
        if (chunkRegion == null) {
            return field_35501;
        }
        ArrayList<class_6782> list = Lists.newArrayList();
        ArrayList<class_6782> list2 = Lists.newArrayList();
        ChunkPos chunkPos = chunkRegion.getCenterPos();
        for (int i = -field_35503; i <= field_35503; ++i) {
            for (int j = -field_35503; j <= field_35503; ++j) {
                int k = chunkPos.x + i;
                int l = chunkPos.z + j;
                Blender blender = Blender.method_39570(chunkRegion, k, l);
                if (blender == null) continue;
                class_6782 lv = new class_6782(k, l, blender);
                list.add(lv);
                if (i < -field_35505 || i > field_35505 || j < -field_35505 || j > field_35505) continue;
                list2.add(lv);
            }
        }
        if (list.isEmpty() && list2.isEmpty()) {
            return field_35501;
        }
        return new class_6748(chunkRegion, list, list2);
    }

    class_6748(ChunkRegion chunkRegion, List<class_6782> list, List<class_6782> list2) {
        this.field_35508 = chunkRegion;
        this.field_35509 = list;
        this.field_35510 = list2;
    }

    public TerrainNoisePoint method_39340(int i, int j, TerrainNoisePoint terrainNoisePoint) {
        int l2;
        int k2 = BiomeCoords.fromBlock(i);
        double d2 = this.method_39562(k2, 0, l2 = BiomeCoords.fromBlock(j), Blender::method_39344);
        if (d2 != Double.MAX_VALUE) {
            return new TerrainNoisePoint(class_6748.method_39337(d2), 10.0, 0.0);
        }
        MutableDouble mutableDouble = new MutableDouble(0.0);
        MutableDouble mutableDouble2 = new MutableDouble(0.0);
        MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);
        for (class_6782 lv : this.field_35509) {
            lv.blendingData.method_39351(BiomeCoords.fromChunk(lv.chunkX), BiomeCoords.fromChunk(lv.chunkZ), (k, l, d) -> {
                double e = MathHelper.hypot(k2 - k, l2 - l);
                if (e > (double)field_35502) {
                    return;
                }
                if (e < mutableDouble3.doubleValue()) {
                    mutableDouble3.setValue(e);
                }
                double f = 1.0 / (e * e * e * e);
                mutableDouble2.add(d * f);
                mutableDouble.add(f);
            });
        }
        if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
            return terrainNoisePoint;
        }
        double e = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
        double f = MathHelper.clamp(mutableDouble3.doubleValue() / (double)(field_35502 + 1), 0.0, 1.0);
        f = 3.0 * f * f - 2.0 * f * f * f;
        double g = MathHelper.lerp(f, class_6748.method_39337(e), terrainNoisePoint.offset());
        double h = MathHelper.lerp(f, 10.0, terrainNoisePoint.factor());
        double m = MathHelper.lerp(f, 0.0, terrainNoisePoint.peaks());
        return new TerrainNoisePoint(g, h, m);
    }

    private static double method_39337(double d) {
        double e = 1.0;
        double f = d + 0.5;
        double g = MathHelper.floorMod(f, 8.0);
        return 1.0 * (32.0 * (f - 128.0) - 3.0 * (f - 120.0) * g + 3.0 * g * g) / (128.0 * (32.0 - 3.0 * g));
    }

    public double method_39338(int i, int j, int k, double d2) {
        int n2;
        int m2;
        int l2 = BiomeCoords.fromBlock(i);
        double e = this.method_39562(l2, m2 = j / 8, n2 = BiomeCoords.fromBlock(k), Blender::method_39345);
        if (e != Double.MAX_VALUE) {
            return e;
        }
        MutableDouble mutableDouble = new MutableDouble(0.0);
        MutableDouble mutableDouble2 = new MutableDouble(0.0);
        MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);
        for (class_6782 lv : this.field_35510) {
            lv.blendingData.method_39346(BiomeCoords.fromChunk(lv.chunkX), BiomeCoords.fromChunk(lv.chunkZ), m2 - 2, m2 + 2, (l, m, n, d) -> {
                double e = MathHelper.magnitude(l2 - l, m2 - m, n2 - n);
                if (e > 2.0) {
                    return;
                }
                if (e < mutableDouble3.doubleValue()) {
                    mutableDouble3.setValue(e);
                }
                double f = 1.0 / (e * e * e * e);
                mutableDouble2.add(d * f);
                mutableDouble.add(f);
            });
        }
        if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
            return d2;
        }
        double f = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
        double g = MathHelper.clamp(mutableDouble3.doubleValue() / 3.0, 0.0, 1.0);
        return MathHelper.lerp(g, f, d2);
    }

    private double method_39562(int i, int j, int k, class_6781 arg) {
        int l = BiomeCoords.toChunk(i);
        int m = BiomeCoords.toChunk(k);
        boolean bl = (i & 3) == 0;
        boolean bl2 = (k & 3) == 0;
        double d = this.method_39565(arg, l, m, i, j, k);
        if (d == Double.MAX_VALUE) {
            if (bl && bl2) {
                d = this.method_39565(arg, l - 1, m - 1, i, j, k);
            }
            if (d == Double.MAX_VALUE) {
                if (bl) {
                    d = this.method_39565(arg, l - 1, m, i, j, k);
                }
                if (d == Double.MAX_VALUE && bl2) {
                    d = this.method_39565(arg, l, m - 1, i, j, k);
                }
            }
        }
        return d;
    }

    private double method_39565(class_6781 arg, int i, int j, int k, int l, int m) {
        Blender blender = Blender.method_39570(this.field_35508, i, j);
        if (blender != null) {
            return arg.get(blender, k - BiomeCoords.fromChunk(i), l, m - BiomeCoords.fromChunk(j));
        }
        return Double.MAX_VALUE;
    }

    public BiomeSupplier method_39563(BiomeSupplier biomeSupplier) {
        return (i, j, k, multiNoiseSampler) -> {
            Biome biome = this.method_39561(i, j, k);
            if (biome == null) {
                return biomeSupplier.getBiome(i, j, k, multiNoiseSampler);
            }
            return biome;
        };
    }

    @Nullable
    private Biome method_39561(int i2, int j2, int k) {
        double d = (double)i2 + field_35681.sample(i2, 0.0, k) * 12.0;
        double e = (double)k + field_35681.sample(k, i2, 0.0) * 12.0;
        MutableDouble mutableDouble = new MutableDouble(Double.POSITIVE_INFINITY);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        MutableObject mutableObject = new MutableObject();
        for (class_6782 lv : this.field_35509) {
            lv.blendingData.method_39351(BiomeCoords.fromChunk(lv.chunkX), BiomeCoords.fromChunk(lv.chunkZ), (i, j, f) -> {
                double g = MathHelper.hypot(d - (double)i, e - (double)j);
                if (g > (double)field_35502) {
                    return;
                }
                if (g < mutableDouble.doubleValue()) {
                    mutableObject.setValue(new ChunkPos(arg.chunkX, arg.chunkZ));
                    mutable.set(i, BiomeCoords.fromBlock(MathHelper.floor(f)), j);
                    mutableDouble.setValue(g);
                }
            });
        }
        if (mutableDouble.doubleValue() == Double.POSITIVE_INFINITY) {
            return null;
        }
        double f2 = MathHelper.clamp(mutableDouble.doubleValue() / (double)(field_35502 + 1), 0.0, 1.0);
        if (f2 > 0.5) {
            return null;
        }
        Chunk chunk = this.field_35508.getChunk(((ChunkPos)mutableObject.getValue()).x, ((ChunkPos)mutableObject.getValue()).z);
        return chunk.getBiomeForNoiseGen(Math.min(mutable.getX() & 3, 3), mutable.getY(), Math.min(mutable.getZ() & 3, 3));
    }

    public static void method_39772(ChunkRegion chunkRegion, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        boolean bl = chunk.usesOldNoise();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ());
        int i = Blender.OLD_HEIGHT_LIMIT.getBottomY();
        int j = Blender.OLD_HEIGHT_LIMIT.getTopY() - 1;
        if (bl) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    class_6748.method_39773(chunk, mutable.set(blockPos, k, i - 1, l));
                    class_6748.method_39773(chunk, mutable.set(blockPos, k, i, l));
                    class_6748.method_39773(chunk, mutable.set(blockPos, k, j, l));
                    class_6748.method_39773(chunk, mutable.set(blockPos, k, j + 1, l));
                }
            }
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (chunkRegion.getChunk(chunkPos.x + direction.getOffsetX(), chunkPos.z + direction.getOffsetZ()).usesOldNoise() == bl) continue;
            int m = direction == Direction.EAST ? 15 : 0;
            int n = direction == Direction.WEST ? 0 : 15;
            int o = direction == Direction.SOUTH ? 15 : 0;
            int p = direction == Direction.NORTH ? 0 : 15;
            for (int q = m; q <= n; ++q) {
                for (int r = o; r <= p; ++r) {
                    int s = Math.min(j, chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, q, r)) + 1;
                    for (int t = i; t < s; ++t) {
                        class_6748.method_39773(chunk, mutable.set(blockPos, q, t, r));
                    }
                }
            }
        }
    }

    private static void method_39773(Chunk chunk, BlockPos blockPos) {
        FluidState fluidState;
        BlockState blockState = chunk.getBlockState(blockPos);
        if (blockState.isIn(BlockTags.LEAVES)) {
            chunk.getBlockTickScheduler().scheduleTick(OrderedTick.create(blockState.getBlock(), blockPos, 0L));
        }
        if (!(fluidState = chunk.getFluidState(blockPos)).isEmpty()) {
            chunk.getFluidTickScheduler().scheduleTick(OrderedTick.create(fluidState.getFluid(), blockPos, 0L));
        }
    }

    record class_6782(int chunkX, int chunkZ, Blender blendingData) {
    }

    static interface class_6781 {
        public double get(Blender var1, int var2, int var3, int var4);
    }
}

