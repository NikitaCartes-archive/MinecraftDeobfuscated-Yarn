/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class OreFeature
extends Feature<OreFeatureConfig> {
    public OreFeature(Function<Dynamic<?>, ? extends OreFeatureConfig> function) {
        super(function);
    }

    public boolean method_13628(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, OreFeatureConfig oreFeatureConfig) {
        float f = random.nextFloat() * (float)Math.PI;
        float g = (float)oreFeatureConfig.size / 8.0f;
        int i = MathHelper.ceil(((float)oreFeatureConfig.size / 16.0f * 2.0f + 1.0f) / 2.0f);
        double d = (float)blockPos.getX() + MathHelper.sin(f) * g;
        double e = (float)blockPos.getX() - MathHelper.sin(f) * g;
        double h = (float)blockPos.getZ() + MathHelper.cos(f) * g;
        double j = (float)blockPos.getZ() - MathHelper.cos(f) * g;
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
                if (o > iWorld.getTop(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) continue;
                return this.generateVeinPart(iWorld, random, oreFeatureConfig, d, e, h, j, l, m, n, o, p, q, r);
            }
        }
        return false;
    }

    protected boolean generateVeinPart(IWorld iWorld, Random random, OreFeatureConfig oreFeatureConfig, double d, double e, double f, double g, double h, double i, int j, int k, int l, int m, int n) {
        double u;
        double t;
        double s;
        double r;
        int p;
        int o = 0;
        BitSet bitSet = new BitSet(m * n * m);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double[] ds = new double[oreFeatureConfig.size * 4];
        for (p = 0; p < oreFeatureConfig.size; ++p) {
            float q = (float)p / (float)oreFeatureConfig.size;
            r = MathHelper.lerp((double)q, d, e);
            s = MathHelper.lerp((double)q, h, i);
            t = MathHelper.lerp((double)q, f, g);
            u = random.nextDouble() * (double)oreFeatureConfig.size / 16.0;
            double v = ((double)(MathHelper.sin((float)Math.PI * q) + 1.0f) * u + 1.0) / 2.0;
            ds[p * 4 + 0] = r;
            ds[p * 4 + 1] = s;
            ds[p * 4 + 2] = t;
            ds[p * 4 + 3] = v;
        }
        for (p = 0; p < oreFeatureConfig.size - 1; ++p) {
            if (ds[p * 4 + 3] <= 0.0) continue;
            for (int w = p + 1; w < oreFeatureConfig.size; ++w) {
                if (ds[w * 4 + 3] <= 0.0 || !((u = ds[p * 4 + 3] - ds[w * 4 + 3]) * u > (r = ds[p * 4 + 0] - ds[w * 4 + 0]) * r + (s = ds[p * 4 + 1] - ds[w * 4 + 1]) * s + (t = ds[p * 4 + 2] - ds[w * 4 + 2]) * t)) continue;
                if (u > 0.0) {
                    ds[w * 4 + 3] = -1.0;
                    continue;
                }
                ds[p * 4 + 3] = -1.0;
            }
        }
        for (p = 0; p < oreFeatureConfig.size; ++p) {
            double x = ds[p * 4 + 3];
            if (x < 0.0) continue;
            double y = ds[p * 4 + 0];
            double z = ds[p * 4 + 1];
            double aa = ds[p * 4 + 2];
            int ab = Math.max(MathHelper.floor(y - x), j);
            int ac = Math.max(MathHelper.floor(z - x), k);
            int ad = Math.max(MathHelper.floor(aa - x), l);
            int ae = Math.max(MathHelper.floor(y + x), ab);
            int af = Math.max(MathHelper.floor(z + x), ac);
            int ag = Math.max(MathHelper.floor(aa + x), ad);
            for (int ah = ab; ah <= ae; ++ah) {
                double ai = ((double)ah + 0.5 - y) / x;
                if (!(ai * ai < 1.0)) continue;
                for (int aj = ac; aj <= af; ++aj) {
                    double ak = ((double)aj + 0.5 - z) / x;
                    if (!(ai * ai + ak * ak < 1.0)) continue;
                    for (int al = ad; al <= ag; ++al) {
                        int an;
                        double am = ((double)al + 0.5 - aa) / x;
                        if (!(ai * ai + ak * ak + am * am < 1.0) || bitSet.get(an = ah - j + (aj - k) * m + (al - l) * m * n)) continue;
                        bitSet.set(an);
                        mutable.set(ah, aj, al);
                        if (!oreFeatureConfig.target.getCondition().test(iWorld.getBlockState(mutable))) continue;
                        iWorld.setBlockState(mutable, oreFeatureConfig.state, 2);
                        ++o;
                    }
                }
            }
        }
        return o > 0;
    }
}

