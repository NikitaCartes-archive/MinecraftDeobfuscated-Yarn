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
    public OreFeature(Function<Dynamic<?>, ? extends OreFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, OreFeatureConfig oreFeatureConfig) {
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
                if (o > iWorld.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) continue;
                return this.generateVeinPart(iWorld, random, oreFeatureConfig, d, e, h, j, l, m, n, o, p, q, r);
            }
        }
        return false;
    }

    protected boolean generateVeinPart(IWorld world, Random random, OreFeatureConfig config, double startX, double endX, double startZ, double endZ, double startY, double endY, int x, int y, int z, int size, int i) {
        double h;
        double g;
        double e;
        double d;
        int k;
        int j = 0;
        BitSet bitSet = new BitSet(size * i * size);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double[] ds = new double[config.size * 4];
        for (k = 0; k < config.size; ++k) {
            float f = (float)k / (float)config.size;
            d = MathHelper.lerp((double)f, startX, endX);
            e = MathHelper.lerp((double)f, startY, endY);
            g = MathHelper.lerp((double)f, startZ, endZ);
            h = random.nextDouble() * (double)config.size / 16.0;
            double l = ((double)(MathHelper.sin((float)Math.PI * f) + 1.0f) * h + 1.0) / 2.0;
            ds[k * 4 + 0] = d;
            ds[k * 4 + 1] = e;
            ds[k * 4 + 2] = g;
            ds[k * 4 + 3] = l;
        }
        for (k = 0; k < config.size - 1; ++k) {
            if (ds[k * 4 + 3] <= 0.0) continue;
            for (int m = k + 1; m < config.size; ++m) {
                if (ds[m * 4 + 3] <= 0.0 || !((h = ds[k * 4 + 3] - ds[m * 4 + 3]) * h > (d = ds[k * 4 + 0] - ds[m * 4 + 0]) * d + (e = ds[k * 4 + 1] - ds[m * 4 + 1]) * e + (g = ds[k * 4 + 2] - ds[m * 4 + 2]) * g)) continue;
                if (h > 0.0) {
                    ds[m * 4 + 3] = -1.0;
                    continue;
                }
                ds[k * 4 + 3] = -1.0;
            }
        }
        for (k = 0; k < config.size; ++k) {
            double n = ds[k * 4 + 3];
            if (n < 0.0) continue;
            double o = ds[k * 4 + 0];
            double p = ds[k * 4 + 1];
            double q = ds[k * 4 + 2];
            int r = Math.max(MathHelper.floor(o - n), x);
            int s = Math.max(MathHelper.floor(p - n), y);
            int t = Math.max(MathHelper.floor(q - n), z);
            int u = Math.max(MathHelper.floor(o + n), r);
            int v = Math.max(MathHelper.floor(p + n), s);
            int w = Math.max(MathHelper.floor(q + n), t);
            for (int aa = r; aa <= u; ++aa) {
                double ab = ((double)aa + 0.5 - o) / n;
                if (!(ab * ab < 1.0)) continue;
                for (int ac = s; ac <= v; ++ac) {
                    double ad = ((double)ac + 0.5 - p) / n;
                    if (!(ab * ab + ad * ad < 1.0)) continue;
                    for (int ae = t; ae <= w; ++ae) {
                        int ag;
                        double af = ((double)ae + 0.5 - q) / n;
                        if (!(ab * ab + ad * ad + af * af < 1.0) || bitSet.get(ag = aa - x + (ac - y) * size + (ae - z) * size * i)) continue;
                        bitSet.set(ag);
                        mutable.set(aa, ac, ae);
                        if (!config.target.getCondition().test(world.getBlockState(mutable))) continue;
                        world.setBlockState(mutable, config.state, 2);
                        ++j;
                    }
                }
            }
        }
        return j > 0;
    }
}

