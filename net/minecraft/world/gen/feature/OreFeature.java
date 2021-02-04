/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class OreFeature
extends Feature<OreFeatureConfig> {
    public OreFeature(Codec<OreFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<OreFeatureConfig> featureContext) {
        Random random = featureContext.getRandom();
        BlockPos blockPos = featureContext.getPos();
        StructureWorldAccess structureWorldAccess = featureContext.getWorld();
        OreFeatureConfig oreFeatureConfig = featureContext.getConfig();
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

    protected boolean generateVeinPart(WorldAccess world, Random random, OreFeatureConfig config, double startX, double endX, double startZ, double endZ, double startY, double endY, int x, int y, int z, int size, int i) {
        double h;
        double g;
        double e;
        double d;
        int l;
        int j = 0;
        BitSet bitSet = new BitSet(size * i * size);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int k = config.size;
        double[] ds = new double[k * 4];
        for (l = 0; l < k; ++l) {
            float f = (float)l / (float)k;
            d = MathHelper.lerp((double)f, startX, endX);
            e = MathHelper.lerp((double)f, startY, endY);
            g = MathHelper.lerp((double)f, startZ, endZ);
            h = random.nextDouble() * (double)k / 16.0;
            double m = ((double)(MathHelper.sin((float)Math.PI * f) + 1.0f) * h + 1.0) / 2.0;
            ds[l * 4 + 0] = d;
            ds[l * 4 + 1] = e;
            ds[l * 4 + 2] = g;
            ds[l * 4 + 3] = m;
        }
        for (l = 0; l < k - 1; ++l) {
            if (ds[l * 4 + 3] <= 0.0) continue;
            for (int n = l + 1; n < k; ++n) {
                if (ds[n * 4 + 3] <= 0.0 || !((h = ds[l * 4 + 3] - ds[n * 4 + 3]) * h > (d = ds[l * 4 + 0] - ds[n * 4 + 0]) * d + (e = ds[l * 4 + 1] - ds[n * 4 + 1]) * e + (g = ds[l * 4 + 2] - ds[n * 4 + 2]) * g)) continue;
                if (h > 0.0) {
                    ds[n * 4 + 3] = -1.0;
                    continue;
                }
                ds[l * 4 + 3] = -1.0;
            }
        }
        for (l = 0; l < k; ++l) {
            double o = ds[l * 4 + 3];
            if (o < 0.0) continue;
            double p = ds[l * 4 + 0];
            double q = ds[l * 4 + 1];
            double r = ds[l * 4 + 2];
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
                    for (int af = u; af <= aa; ++af) {
                        int ah;
                        double ag = ((double)af + 0.5 - r) / o;
                        if (!(ac * ac + ae * ae + ag * ag < 1.0) || bitSet.get(ah = ab - x + (ad - y) * size + (af - z) * size * i)) continue;
                        bitSet.set(ah);
                        mutable.set(ab, ad, af);
                        if (!config.target.test(world.getBlockState(mutable), random)) continue;
                        world.setBlockState(mutable, config.state, 2);
                        ++j;
                    }
                }
            }
        }
        return j > 0;
    }
}

