/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_5869;
import net.minecraft.class_5873;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5870
extends Carver<class_5869> {
    private static final Logger field_29052 = LogManager.getLogger();

    public class_5870(Codec<class_5869> codec) {
        super(codec);
    }

    @Override
    public boolean shouldCarve(class_5869 arg, Random random) {
        return random.nextFloat() <= arg.probability;
    }

    @Override
    public boolean carve(class_5873 arg, class_5869 arg2, Chunk chunk, Function<BlockPos, Biome> function, Random random, int i, ChunkPos chunkPos, BitSet bitSet) {
        int j = (this.getBranchFactor() * 2 - 1) * 16;
        double d = chunkPos.method_33939(random.nextInt(16));
        int k = this.method_33963(arg, arg2, random);
        double e = chunkPos.method_33941(random.nextInt(16));
        float f = random.nextFloat() * ((float)Math.PI * 2);
        float g = arg2.method_33953().method_33920(random);
        double h = arg2.method_33951().getValue(random);
        float l = arg2.method_33954().method_33920(random);
        int m = (int)((float)j * arg2.method_33952().method_33920(random));
        boolean n = false;
        this.method_33961(arg, arg2, chunk, function, random.nextLong(), i, d, k, e, l, f, g, 0, m, h, bitSet);
        return true;
    }

    private void method_33961(class_5873 arg2, class_5869 arg22, Chunk chunk, Function<BlockPos, Biome> function, long l, int i2, double d2, double e2, double f2, float g, float h, float j, int k, int m, double n, BitSet bitSet) {
        Random random = new Random(l);
        float[] fs = this.method_33966(arg2, arg22, random);
        float o = 0.0f;
        float p = 0.0f;
        for (int q = k; q < m; ++q) {
            double r = 1.5 + (double)(MathHelper.sin((float)q * (float)Math.PI / (float)m) * g);
            double s = r * n;
            r *= (double)arg22.method_33956().method_33920(random);
            s = this.method_33960(arg22, random, s, m, q);
            float t = MathHelper.cos(j);
            float u = MathHelper.sin(j);
            d2 += (double)(MathHelper.cos(h) * t);
            e2 += (double)u;
            f2 += (double)(MathHelper.sin(h) * t);
            j *= 0.7f;
            j += p * 0.05f;
            h += o * 0.05f;
            p *= 0.8f;
            o *= 0.5f;
            p += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            o += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (random.nextInt(4) == 0) continue;
            if (!class_5870.method_33976(chunk.getPos(), d2, f2, q, m, g)) {
                return;
            }
            this.method_33978(arg2, arg22, chunk, function, l, i2, d2, e2, f2, r, s, bitSet, (arg, d, e, f, i) -> this.method_33964(arg, fs, d, e, f, i));
        }
    }

    private int method_33963(class_5873 arg, class_5869 arg2, Random random) {
        int j;
        int i = arg2.method_33947().getY(arg);
        if (i >= (j = arg2.method_33950().getY(arg))) {
            field_29052.warn("Empty carver: {} [{}-{}]", (Object)this, (Object)i, (Object)j);
            return i;
        }
        return MathHelper.nextBetween(random, i, j);
    }

    private float[] method_33966(class_5873 arg, class_5869 arg2, Random random) {
        int i = arg.getMaxY();
        float[] fs = new float[i];
        float f = 1.0f;
        for (int j = 0; j < i; ++j) {
            if (j == 0 || random.nextInt(arg2.method_33955()) == 0) {
                f = 1.0f + random.nextFloat() * random.nextFloat();
            }
            fs[j] = f * f;
        }
        return fs;
    }

    private double method_33960(class_5869 arg, Random random, double d, float f, float g) {
        float h = 1.0f - MathHelper.abs(0.5f - g / f) * 2.0f;
        float i = arg.method_33957() + arg.method_33958() * h;
        return (double)i * d * (double)MathHelper.nextBetween(random, 0.75f, 1.0f);
    }

    private boolean method_33964(class_5873 arg, float[] fs, double d, double e, double f, int i) {
        int j = i - arg.getMinY();
        return (d * d + f * f) * (double)fs[j - 1] + e * e / 6.0 >= 1.0;
    }
}

