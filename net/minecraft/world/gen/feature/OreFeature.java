/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.class_5867;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class OreFeature
extends Feature<OreFeatureConfig> {
    public OreFeature(Codec<OreFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<OreFeatureConfig> context) {
        Random random = context.getRandom();
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        OreFeatureConfig oreFeatureConfig = context.getConfig();
        float f = random.nextFloat() * (float)Math.PI;
        float g = (float)oreFeatureConfig.size / 8.0f;
        int i = MathHelper.ceil(((float)oreFeatureConfig.size / 16.0f * 2.0f + 1.0f) / 2.0f);
        double d = (double)blockPos.getX() + Math.sin(f) * (double)g;
        double e = (double)blockPos.getX() - Math.sin(f) * (double)g;
        double h = (double)blockPos.getZ() + Math.cos(f) * (double)g;
        double j = (double)blockPos.getZ() - Math.cos(f) * (double)g;
        int k = 2;
        double l = blockPos.getY() + random.nextInt(3) - 2;
        double m = blockPos.getY() + random.nextInt(3) - 2;
        int n = blockPos.getX() - MathHelper.ceil(g) - i;
        int o = blockPos.getY() - 2 - i;
        int p = blockPos.getZ() - MathHelper.ceil(g) - i;
        int q = 2 * (MathHelper.ceil(g) + i);
        int r = 2 * (2 + i);
        for (int s = n; s <= n + q; ++s) {
            for (int t = p; t <= p + q; ++t) {
                if (o > structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) continue;
                return this.generateVeinPart(structureWorldAccess, random, oreFeatureConfig, d, e, h, j, l, m, n, o, p, q, r);
            }
        }
        return false;
    }

    protected boolean generateVeinPart(WorldAccess world, Random random, OreFeatureConfig config, double startX, double endX, double startZ, double endZ, double startY, double endY, int x, int y, int z, int horizontalSize, int verticalSize) {
        double h;
        double g;
        double e;
        double d;
        int k;
        int i = 0;
        BitSet bitSet = new BitSet(horizontalSize * verticalSize * horizontalSize);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int j = config.size;
        double[] ds = new double[j * 4];
        for (k = 0; k < j; ++k) {
            float f = (float)k / (float)j;
            d = MathHelper.lerp((double)f, startX, endX);
            e = MathHelper.lerp((double)f, startY, endY);
            g = MathHelper.lerp((double)f, startZ, endZ);
            h = random.nextDouble() * (double)j / 16.0;
            double l = ((double)(MathHelper.sin((float)Math.PI * f) + 1.0f) * h + 1.0) / 2.0;
            ds[k * 4 + 0] = d;
            ds[k * 4 + 1] = e;
            ds[k * 4 + 2] = g;
            ds[k * 4 + 3] = l;
        }
        for (k = 0; k < j - 1; ++k) {
            if (ds[k * 4 + 3] <= 0.0) continue;
            for (int m = k + 1; m < j; ++m) {
                if (ds[m * 4 + 3] <= 0.0 || !((h = ds[k * 4 + 3] - ds[m * 4 + 3]) * h > (d = ds[k * 4 + 0] - ds[m * 4 + 0]) * d + (e = ds[k * 4 + 1] - ds[m * 4 + 1]) * e + (g = ds[k * 4 + 2] - ds[m * 4 + 2]) * g)) continue;
                if (h > 0.0) {
                    ds[m * 4 + 3] = -1.0;
                    continue;
                }
                ds[k * 4 + 3] = -1.0;
            }
        }
        try (class_5867 lv = new class_5867(world);){
            for (int n = 0; n < j; ++n) {
                double o = ds[n * 4 + 3];
                if (o < 0.0) continue;
                double p = ds[n * 4 + 0];
                double q = ds[n * 4 + 1];
                double r = ds[n * 4 + 2];
                int s = Math.max(MathHelper.floor(p - o), x);
                int t = Math.max(MathHelper.floor(q - o), y);
                int u = Math.max(MathHelper.floor(r - o), z);
                int v = Math.max(MathHelper.floor(p + o), s);
                int w = Math.max(MathHelper.floor(q + o), t);
                int aa = Math.max(MathHelper.floor(r + o), u);
                for (int ab = s; ab <= v; ++ab) {
                    double ac = ((double)ab + 0.5 - p) / o;
                    if (!(ac * ac < 1.0)) continue;
                    for (int ad = t; ad <= w; ++ad) {
                        double ae = ((double)ad + 0.5 - q) / o;
                        if (!(ac * ac + ae * ae < 1.0)) continue;
                        block15: for (int af = u; af <= aa; ++af) {
                            int ah;
                            double ag = ((double)af + 0.5 - r) / o;
                            if (!(ac * ac + ae * ae + ag * ag < 1.0) || world.isOutOfHeightLimit(ad) || bitSet.get(ah = ab - x + (ad - y) * horizontalSize + (af - z) * horizontalSize * verticalSize)) continue;
                            bitSet.set(ah);
                            mutable.set(ab, ad, af);
                            ChunkSection chunkSection = lv.method_33944(mutable);
                            int ai = ChunkSectionPos.getLocalCoord(ab);
                            int aj = ChunkSectionPos.getLocalCoord(ad);
                            int ak = ChunkSectionPos.getLocalCoord(af);
                            BlockState blockState = chunkSection.getBlockState(ai, aj, ak);
                            for (OreFeatureConfig.class_5876 lv2 : config.field_29063) {
                                if (!OreFeature.method_33983(blockState, lv::method_33946, random, config, lv2, mutable)) continue;
                                chunkSection.setBlockState(ai, aj, ak, lv2.field_29069, false);
                                ++i;
                                continue block15;
                            }
                        }
                    }
                }
            }
        }
        return i > 0;
    }

    public static boolean method_33983(BlockState blockState, Function<BlockPos, BlockState> function, Random random, OreFeatureConfig oreFeatureConfig, OreFeatureConfig.class_5876 arg, BlockPos.Mutable mutable) {
        if (!arg.field_29068.test(blockState, random)) {
            return false;
        }
        if (OreFeature.method_33984(random, oreFeatureConfig.field_29064)) {
            return true;
        }
        return !OreFeature.method_33981(function, mutable);
    }

    protected static boolean method_33984(Random random, float f) {
        if (f <= 0.0f) {
            return true;
        }
        if (f >= 1.0f) {
            return false;
        }
        return random.nextFloat() >= f;
    }
}

