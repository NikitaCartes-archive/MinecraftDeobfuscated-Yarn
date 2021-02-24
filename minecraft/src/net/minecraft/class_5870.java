package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5870 extends Carver<class_5869> {
	private static final Logger field_29052 = LogManager.getLogger();

	public class_5870(Codec<class_5869> codec) {
		super(codec);
	}

	public boolean shouldCarve(class_5869 arg, Random random) {
		return random.nextFloat() <= arg.probability;
	}

	public boolean carve(class_5873 arg, class_5869 arg2, Chunk chunk, Function<BlockPos, Biome> function, Random random, int i, ChunkPos chunkPos, BitSet bitSet) {
		int j = (this.getBranchFactor() * 2 - 1) * 16;
		double d = (double)chunkPos.method_33939(random.nextInt(16));
		int k = this.method_33963(arg, arg2, random);
		double e = (double)chunkPos.method_33941(random.nextInt(16));
		float f = random.nextFloat() * (float) (Math.PI * 2);
		float g = arg2.method_33953().method_33920(random);
		double h = (double)arg2.method_33951().getValue(random);
		float l = arg2.method_33954().method_33920(random);
		int m = (int)((float)j * arg2.method_33952().method_33920(random));
		int n = 0;
		this.method_33961(arg, arg2, chunk, function, random.nextLong(), i, d, (double)k, e, l, f, g, 0, m, h, bitSet);
		return true;
	}

	private void method_33961(
		class_5873 arg,
		class_5869 arg2,
		Chunk chunk,
		Function<BlockPos, Biome> function,
		long l,
		int i,
		double d,
		double e,
		double f,
		float g,
		float h,
		float j,
		int k,
		int m,
		double n,
		BitSet bitSet
	) {
		Random random = new Random(l);
		float[] fs = this.method_33966(arg, arg2, random);
		float o = 0.0F;
		float p = 0.0F;

		for (int q = k; q < m; q++) {
			double r = 1.5 + (double)(MathHelper.sin((float)q * (float) Math.PI / (float)m) * g);
			double s = r * n;
			r *= (double)arg2.method_33956().method_33920(random);
			s = this.method_33960(arg2, random, s, (float)m, (float)q);
			float t = MathHelper.cos(j);
			float u = MathHelper.sin(j);
			d += (double)(MathHelper.cos(h) * t);
			e += (double)u;
			f += (double)(MathHelper.sin(h) * t);
			j *= 0.7F;
			j += p * 0.05F;
			h += o * 0.05F;
			p *= 0.8F;
			o *= 0.5F;
			p += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			o += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (random.nextInt(4) != 0) {
				if (!method_33976(chunk.getPos(), d, f, q, m, g)) {
					return;
				}

				this.method_33978(arg, arg2, chunk, function, l, i, d, e, f, r, s, bitSet, (argx, dx, ex, fx, ix) -> this.method_33964(argx, fs, dx, ex, fx, ix));
			}
		}
	}

	private int method_33963(class_5873 arg, class_5869 arg2, Random random) {
		int i = arg2.method_33947().getY(arg);
		int j = arg2.method_33950().getY(arg);
		if (i >= j) {
			field_29052.warn("Empty carver: {} [{}-{}]", this, i, j);
			return i;
		} else {
			return MathHelper.nextBetween(random, i, j);
		}
	}

	private float[] method_33966(class_5873 arg, class_5869 arg2, Random random) {
		int i = arg.getMaxY();
		float[] fs = new float[i];
		float f = 1.0F;

		for (int j = 0; j < i; j++) {
			if (j == 0 || random.nextInt(arg2.method_33955()) == 0) {
				f = 1.0F + random.nextFloat() * random.nextFloat();
			}

			fs[j] = f * f;
		}

		return fs;
	}

	private double method_33960(class_5869 arg, Random random, double d, float f, float g) {
		float h = 1.0F - MathHelper.abs(0.5F - g / f) * 2.0F;
		float i = arg.method_33957() + arg.method_33958() * h;
		return (double)i * d * (double)MathHelper.nextBetween(random, 0.75F, 1.0F);
	}

	private boolean method_33964(class_5873 arg, float[] fs, double d, double e, double f, int i) {
		int j = i - arg.getMinY();
		return (d * d + f * f) * (double)fs[j - 1] + e * e / 6.0 >= 1.0;
	}
}
