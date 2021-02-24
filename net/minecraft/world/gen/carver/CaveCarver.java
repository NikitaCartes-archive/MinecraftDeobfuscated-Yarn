/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_5871;
import net.minecraft.class_5873;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;

public class CaveCarver
extends Carver<class_5871> {
    public CaveCarver(Codec<class_5871> codec) {
        super(codec);
    }

    @Override
    public boolean shouldCarve(class_5871 arg, Random random) {
        return random.nextFloat() <= arg.probability;
    }

    @Override
    public boolean carve(class_5873 arg2, class_5871 arg22, Chunk chunk, Function<BlockPos, Biome> function, Random random, int chunkZ, ChunkPos chunkPos, BitSet bitSet) {
        int i2 = ChunkSectionPos.getBlockCoord(this.getBranchFactor() * 2 - 1);
        int j = random.nextInt(random.nextInt(random.nextInt(this.getMaxCaveCount()) + 1) + 1);
        for (int k = 0; k < j; ++k) {
            float o;
            double d = chunkPos.method_33939(random.nextInt(16));
            double e2 = this.getCaveY(arg2, random);
            double f2 = chunkPos.method_33941(random.nextInt(16));
            double g2 = MathHelper.nextBetween(random, 0.2f, 1.8f);
            double h = MathHelper.nextBetween(random, 0.2f, 1.8f);
            double l = MathHelper.nextBetween(random, -1.0f, 0.0f);
            Carver.class_5874 lv = (arg, e, f, g, i) -> CaveCarver.method_33974(e, f, g, l);
            int m = 1;
            if (random.nextInt(4) == 0) {
                double n = MathHelper.nextBetween(random, 0.1f, 0.9f);
                o = 1.0f + random.nextFloat() * 6.0f;
                this.carveCave(arg2, arg22, chunk, function, random.nextLong(), chunkZ, d, e2, f2, o, n, bitSet, lv);
                m += random.nextInt(4);
            }
            for (int p = 0; p < m; ++p) {
                float q = random.nextFloat() * ((float)Math.PI * 2);
                o = (random.nextFloat() - 0.5f) / 4.0f;
                float r = this.getTunnelSystemWidth(random);
                int s = i2 - random.nextInt(i2 / 4);
                boolean t = false;
                this.carveTunnels(arg2, arg22, chunk, function, random.nextLong(), chunkZ, d, e2, f2, g2, h, r, q, o, 0, s, this.getTunnelSystemHeightWidthRatio(), bitSet, lv);
            }
        }
        return true;
    }

    protected int getMaxCaveCount() {
        return 15;
    }

    protected float getTunnelSystemWidth(Random random) {
        float f = random.nextFloat() * 2.0f + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0f + 1.0f;
        }
        return f;
    }

    protected double getTunnelSystemHeightWidthRatio() {
        return 1.0;
    }

    protected int getCaveY(class_5873 arg, Random random) {
        int i = arg.getMinY() + 8;
        int j = 126;
        if (i > 126) {
            return i;
        }
        return MathHelper.nextBetween(random, i, 126);
    }

    protected void carveCave(class_5873 arg, class_5871 arg2, Chunk chunk, Function<BlockPos, Biome> function, long l, int mainChunkZ, double x, double y, double z, float yaw, double yawPitchRatio, BitSet carvingMask, Carver.class_5874 arg3) {
        double d = 1.5 + (double)(MathHelper.sin(1.5707964f) * yaw);
        double e = d * yawPitchRatio;
        this.method_33978(arg, arg2, chunk, function, l, mainChunkZ, x + 1.0, y, z, d, e, carvingMask, arg3);
    }

    protected void carveTunnels(class_5873 arg, class_5871 arg2, Chunk chunk, Function<BlockPos, Biome> function, long l, int mainChunkZ, double d, double e, double f, double g, double h, float i, float j, float k, int m, int n, double o, BitSet bitSet, Carver.class_5874 arg3) {
        Random random = new Random(l);
        int p = random.nextInt(n / 2) + n / 4;
        boolean bl = random.nextInt(6) == 0;
        float q = 0.0f;
        float r = 0.0f;
        for (int s = m; s < n; ++s) {
            double t = 1.5 + (double)(MathHelper.sin((float)Math.PI * (float)s / (float)n) * i);
            double u = t * o;
            float v = MathHelper.cos(k);
            d += (double)(MathHelper.cos(j) * v);
            e += (double)MathHelper.sin(k);
            f += (double)(MathHelper.sin(j) * v);
            k *= bl ? 0.92f : 0.7f;
            k += r * 0.1f;
            j += q * 0.1f;
            r *= 0.9f;
            q *= 0.75f;
            r += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            q += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (s == p && i > 1.0f) {
                this.carveTunnels(arg, arg2, chunk, function, random.nextLong(), mainChunkZ, d, e, f, g, h, random.nextFloat() * 0.5f + 0.5f, j - 1.5707964f, k / 3.0f, s, n, 1.0, bitSet, arg3);
                this.carveTunnels(arg, arg2, chunk, function, random.nextLong(), mainChunkZ, d, e, f, g, h, random.nextFloat() * 0.5f + 0.5f, j + 1.5707964f, k / 3.0f, s, n, 1.0, bitSet, arg3);
                return;
            }
            if (random.nextInt(4) == 0) continue;
            if (!CaveCarver.method_33976(chunk.getPos(), d, f, s, n, i)) {
                return;
            }
            this.method_33978(arg, arg2, chunk, function, l, mainChunkZ, d, e, f, t * g, u * h, bitSet, arg3);
        }
    }

    private static boolean method_33974(double d, double e, double f, double g) {
        if (e <= g) {
            return true;
        }
        return d * d + e * e + f * f >= 1.0;
    }
}

