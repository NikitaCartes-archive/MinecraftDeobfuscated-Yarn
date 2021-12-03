/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EightWayDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class Blender {
    private static final Blender NO_BLENDING = new Blender(null, List.of(), List.of()){

        @Override
        public TerrainNoisePoint method_39340(int i, int j, TerrainNoisePoint terrainNoisePoint) {
            return terrainNoisePoint;
        }

        @Override
        public double method_39338(int i, int j, int k, double d) {
            return d;
        }

        @Override
        public BiomeSupplier getBiomeSupplier(BiomeSupplier biomeSupplier) {
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
    private static final double field_36222 = (double)BlendingData.OLD_HEIGHT_LIMIT.getHeight() / 2.0;
    private static final double field_36223 = (double)BlendingData.OLD_HEIGHT_LIMIT.getBottomY() + field_36222;
    private static final double field_36224 = 8.0;
    private final ChunkRegion chunkRegion;
    private final List<ChunkBlendingData> field_35509;
    private final List<ChunkBlendingData> field_35510;

    public static Blender getNoBlending() {
        return NO_BLENDING;
    }

    public static Blender getBlender(@Nullable ChunkRegion chunkRegion) {
        if (chunkRegion == null) {
            return NO_BLENDING;
        }
        ArrayList<ChunkBlendingData> list = Lists.newArrayList();
        ArrayList<ChunkBlendingData> list2 = Lists.newArrayList();
        ChunkPos chunkPos = chunkRegion.getCenterPos();
        for (int i = -field_35503; i <= field_35503; ++i) {
            for (int j = -field_35503; j <= field_35503; ++j) {
                int k = chunkPos.x + i;
                int l = chunkPos.z + j;
                BlendingData blendingData = BlendingData.getBlendingData(chunkRegion, k, l);
                if (blendingData == null) continue;
                ChunkBlendingData chunkBlendingData = new ChunkBlendingData(k, l, blendingData);
                list.add(chunkBlendingData);
                if (i < -field_35505 || i > field_35505 || j < -field_35505 || j > field_35505) continue;
                list2.add(chunkBlendingData);
            }
        }
        if (list.isEmpty() && list2.isEmpty()) {
            return NO_BLENDING;
        }
        return new Blender(chunkRegion, list, list2);
    }

    Blender(ChunkRegion chunkRegion, List<ChunkBlendingData> list, List<ChunkBlendingData> list2) {
        this.chunkRegion = chunkRegion;
        this.field_35509 = list;
        this.field_35510 = list2;
    }

    public TerrainNoisePoint method_39340(int i, int j, TerrainNoisePoint terrainNoisePoint) {
        int l2;
        int k2 = BiomeCoords.fromBlock(i);
        double d2 = this.method_39562(k2, 0, l2 = BiomeCoords.fromBlock(j), BlendingData::method_39344);
        if (d2 != Double.MAX_VALUE) {
            return new TerrainNoisePoint(Blender.method_39337(d2), 10.0, 0.0);
        }
        MutableDouble mutableDouble = new MutableDouble(0.0);
        MutableDouble mutableDouble2 = new MutableDouble(0.0);
        MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);
        for (ChunkBlendingData chunkBlendingData : this.field_35509) {
            chunkBlendingData.blendingData.method_39351(BiomeCoords.fromChunk(chunkBlendingData.chunkX), BiomeCoords.fromChunk(chunkBlendingData.chunkZ), (k, l, d) -> {
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
        double g = MathHelper.lerp(f, Blender.method_39337(e), terrainNoisePoint.offset());
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
        double e = this.method_39562(l2, m2 = j / 8, n2 = BiomeCoords.fromBlock(k), BlendingData::method_39345);
        if (e != Double.MAX_VALUE) {
            return e;
        }
        MutableDouble mutableDouble = new MutableDouble(0.0);
        MutableDouble mutableDouble2 = new MutableDouble(0.0);
        MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);
        for (ChunkBlendingData chunkBlendingData : this.field_35510) {
            chunkBlendingData.blendingData.method_39346(BiomeCoords.fromChunk(chunkBlendingData.chunkX), BiomeCoords.fromChunk(chunkBlendingData.chunkZ), m2 - 1, m2 + 1, (l, m, n, d) -> {
                double e = MathHelper.magnitude(l2 - l, (m2 - m) * 2, n2 - n);
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
        BlendingData blendingData = BlendingData.getBlendingData(this.chunkRegion, i, j);
        if (blendingData != null) {
            return arg.get(blendingData, k - BiomeCoords.fromChunk(i), l, m - BiomeCoords.fromChunk(j));
        }
        return Double.MAX_VALUE;
    }

    public BiomeSupplier getBiomeSupplier(BiomeSupplier biomeSupplier) {
        return (x, y, z, noise) -> {
            Biome biome = this.blendBiome(x, y, z);
            if (biome == null) {
                return biomeSupplier.getBiome(x, y, z, noise);
            }
            return biome;
        };
    }

    @Nullable
    private Biome blendBiome(int x, int y, int z) {
        double d = (double)x + field_35681.sample(x, 0.0, z) * 12.0;
        double e = (double)z + field_35681.sample(z, x, 0.0) * 12.0;
        MutableDouble mutableDouble = new MutableDouble(Double.POSITIVE_INFINITY);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        MutableObject mutableObject = new MutableObject();
        for (ChunkBlendingData chunkBlendingData : this.field_35509) {
            chunkBlendingData.blendingData.method_39351(BiomeCoords.fromChunk(chunkBlendingData.chunkX), BiomeCoords.fromChunk(chunkBlendingData.chunkZ), (i, j, f) -> {
                double g = MathHelper.hypot(d - (double)i, e - (double)j);
                if (g > (double)field_35502) {
                    return;
                }
                if (g < mutableDouble.doubleValue()) {
                    mutableObject.setValue(new ChunkPos(chunkBlendingData.chunkX, chunkBlendingData.chunkZ));
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
        Chunk chunk = this.chunkRegion.getChunk(((ChunkPos)mutableObject.getValue()).x, ((ChunkPos)mutableObject.getValue()).z);
        return chunk.getBiomeForNoiseGen(Math.min(mutable.getX() & 3, 3), mutable.getY(), Math.min(mutable.getZ() & 3, 3));
    }

    public static void tickLeavesAndFluids(ChunkRegion chunkRegion, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        boolean bl = chunk.usesOldNoise();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ());
        int i = BlendingData.OLD_HEIGHT_LIMIT.getBottomY();
        int j = BlendingData.OLD_HEIGHT_LIMIT.getTopY() - 1;
        if (bl) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    Blender.tickLeavesAndFluids(chunk, mutable.set(blockPos, k, i - 1, l));
                    Blender.tickLeavesAndFluids(chunk, mutable.set(blockPos, k, i, l));
                    Blender.tickLeavesAndFluids(chunk, mutable.set(blockPos, k, j, l));
                    Blender.tickLeavesAndFluids(chunk, mutable.set(blockPos, k, j + 1, l));
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
                        Blender.tickLeavesAndFluids(chunk, mutable.set(blockPos, q, t, r));
                    }
                }
            }
        }
    }

    private static void tickLeavesAndFluids(Chunk chunk, BlockPos pos) {
        FluidState fluidState;
        BlockState blockState = chunk.getBlockState(pos);
        if (blockState.isIn(BlockTags.LEAVES)) {
            chunk.markBlockForPostProcessing(pos);
        }
        if (!(fluidState = chunk.getFluidState(pos)).isEmpty()) {
            chunk.markBlockForPostProcessing(pos);
        }
    }

    public static void method_39809(StructureWorldAccess structureWorldAccess, ProtoChunk protoChunk) {
        ChunkPos chunkPos = protoChunk.getPos();
        class_6831 lv = Blender.method_39815(protoChunk.usesOldNoise(), BlendingData.getAdjacentChunksWithNoise(structureWorldAccess, chunkPos.x, chunkPos.z, true));
        if (lv == null) {
            return;
        }
        CarvingMask.MaskPredicate maskPredicate = (i, j, k) -> {
            double f;
            double e;
            double d = (double)i + 0.5 + field_35681.sample(i, j, k) * 4.0;
            return lv.getDistance(d, e = (double)j + 0.5 + field_35681.sample(j, k, i) * 4.0, f = (double)k + 0.5 + field_35681.sample(k, i, j) * 4.0) < 4.0;
        };
        Stream.of(GenerationStep.Carver.values()).map(protoChunk::getOrCreateCarvingMask).forEach(carvingMask -> carvingMask.setMaskPredicate(maskPredicate));
    }

    @Nullable
    public static class_6831 method_39815(boolean bl, Set<EightWayDirection> set) {
        if (!bl && set.isEmpty()) {
            return null;
        }
        ArrayList<class_6831> list = Lists.newArrayList();
        if (bl) {
            list.add(Blender.method_39812(null));
        }
        set.forEach(eightWayDirection -> list.add(Blender.method_39812(eightWayDirection)));
        return (d, e, f) -> {
            double g = Double.POSITIVE_INFINITY;
            for (class_6831 lv : list) {
                double h = lv.getDistance(d, e, f);
                if (!(h < g)) continue;
                g = h;
            }
            return g;
        };
    }

    private static class_6831 method_39812(@Nullable EightWayDirection eightWayDirection) {
        double d = 0.0;
        double e = 0.0;
        if (eightWayDirection != null) {
            for (Direction direction : eightWayDirection.getDirections()) {
                d += (double)(direction.getOffsetX() * 16);
                e += (double)(direction.getOffsetZ() * 16);
            }
        }
        double f2 = d;
        double g2 = e;
        return (f, g, h) -> Blender.method_39808(f - 8.0 - f2, g - field_36223, h - 8.0 - g2, 8.0, field_36222, 8.0);
    }

    private static double method_39808(double d, double e, double f, double g, double h, double i) {
        double j = Math.abs(d) - g;
        double k = Math.abs(e) - h;
        double l = Math.abs(f) - i;
        return MathHelper.magnitude(Math.max(0.0, j), Math.max(0.0, k), Math.max(0.0, l));
    }

    record ChunkBlendingData(int chunkX, int chunkZ, BlendingData blendingData) {
    }

    static interface class_6781 {
        public double get(BlendingData var1, int var2, int var3, int var4);
    }

    public static interface class_6831 {
        public double getDistance(double var1, double var3, double var5);
    }
}

