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

public class CaveCarver extends Carver<class_5871> {
	public CaveCarver(Codec<class_5871> codec) {
		super(codec);
	}

	@Override
	public boolean shouldCarve(class_5871 arg, Random random) {
		return random.nextFloat() <= arg.probability;
	}

	@Override
	public boolean carve(
		class_5873 arg, class_5871 arg2, Chunk chunk, Function<BlockPos, Biome> function, Random random, int chunkZ, ChunkPos chunkPos, BitSet bitSet
	) {
		int i = ChunkSectionPos.getBlockCoord(this.getBranchFactor() * 2 - 1);
		int j = random.nextInt(random.nextInt(random.nextInt(this.getMaxCaveCount()) + 1) + 1);

		for (int k = 0; k < j; k++) {
			double d = (double)chunkPos.method_33939(random.nextInt(16));
			double e = (double)this.getCaveY(arg, random);
			double f = (double)chunkPos.method_33941(random.nextInt(16));
			double g = (double)MathHelper.nextBetween(random, 0.2F, 1.8F);
			double h = (double)MathHelper.nextBetween(random, 0.2F, 1.8F);
			double l = (double)MathHelper.nextBetween(random, -1.0F, 0.0F);
			Carver.class_5874 lv = (argx, ex, fx, gx, ix) -> method_33974(ex, fx, gx, l);
			int m = 1;
			if (random.nextInt(4) == 0) {
				double n = (double)MathHelper.nextBetween(random, 0.1F, 0.9F);
				float o = 1.0F + random.nextFloat() * 6.0F;
				this.carveCave(arg, arg2, chunk, function, random.nextLong(), chunkZ, d, e, f, o, n, bitSet, lv);
				m += random.nextInt(4);
			}

			for (int p = 0; p < m; p++) {
				float q = random.nextFloat() * (float) (Math.PI * 2);
				float o = (random.nextFloat() - 0.5F) / 4.0F;
				float r = this.getTunnelSystemWidth(random);
				int s = i - random.nextInt(i / 4);
				int t = 0;
				this.carveTunnels(arg, arg2, chunk, function, random.nextLong(), chunkZ, d, e, f, g, h, r, q, o, 0, s, this.getTunnelSystemHeightWidthRatio(), bitSet, lv);
			}
		}

		return true;
	}

	protected int getMaxCaveCount() {
		return 15;
	}

	protected float getTunnelSystemWidth(Random random) {
		float f = random.nextFloat() * 2.0F + random.nextFloat();
		if (random.nextInt(10) == 0) {
			f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.0F;
		}

		return f;
	}

	protected double getTunnelSystemHeightWidthRatio() {
		return 1.0;
	}

	protected int getCaveY(class_5873 arg, Random random) {
		int i = arg.getMinY() + 8;
		int j = 126;
		return i > 126 ? i : MathHelper.nextBetween(random, i, 126);
	}

	protected void carveCave(
		class_5873 arg,
		class_5871 arg2,
		Chunk chunk,
		Function<BlockPos, Biome> function,
		long l,
		int mainChunkZ,
		double x,
		double y,
		double z,
		float yaw,
		double yawPitchRatio,
		BitSet carvingMask,
		Carver.class_5874 arg3
	) {
		double d = 1.5 + (double)(MathHelper.sin((float) (Math.PI / 2)) * yaw);
		double e = d * yawPitchRatio;
		this.method_33978(arg, arg2, chunk, function, l, mainChunkZ, x + 1.0, y, z, d, e, carvingMask, arg3);
	}

	protected void carveTunnels(
		class_5873 arg,
		class_5871 arg2,
		Chunk chunk,
		Function<BlockPos, Biome> function,
		long l,
		int mainChunkZ,
		double d,
		double e,
		double f,
		double g,
		double h,
		float i,
		float j,
		float k,
		int m,
		int n,
		double o,
		BitSet bitSet,
		Carver.class_5874 arg3
	) {
		Random random = new Random(l);
		int p = random.nextInt(n / 2) + n / 4;
		boolean bl = random.nextInt(6) == 0;
		float q = 0.0F;
		float r = 0.0F;

		for (int s = m; s < n; s++) {
			double t = 1.5 + (double)(MathHelper.sin((float) Math.PI * (float)s / (float)n) * i);
			double u = t * o;
			float v = MathHelper.cos(k);
			d += (double)(MathHelper.cos(j) * v);
			e += (double)MathHelper.sin(k);
			f += (double)(MathHelper.sin(j) * v);
			k *= bl ? 0.92F : 0.7F;
			k += r * 0.1F;
			j += q * 0.1F;
			r *= 0.9F;
			q *= 0.75F;
			r += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			q += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (s == p && i > 1.0F) {
				this.carveTunnels(
					arg,
					arg2,
					chunk,
					function,
					random.nextLong(),
					mainChunkZ,
					d,
					e,
					f,
					g,
					h,
					random.nextFloat() * 0.5F + 0.5F,
					j - (float) (Math.PI / 2),
					k / 3.0F,
					s,
					n,
					1.0,
					bitSet,
					arg3
				);
				this.carveTunnels(
					arg,
					arg2,
					chunk,
					function,
					random.nextLong(),
					mainChunkZ,
					d,
					e,
					f,
					g,
					h,
					random.nextFloat() * 0.5F + 0.5F,
					j + (float) (Math.PI / 2),
					k / 3.0F,
					s,
					n,
					1.0,
					bitSet,
					arg3
				);
				return;
			}

			if (random.nextInt(4) != 0) {
				if (!method_33976(chunk.getPos(), d, f, s, n, i)) {
					return;
				}

				this.method_33978(arg, arg2, chunk, function, l, mainChunkZ, d, e, f, t * g, u * h, bitSet, arg3);
			}
		}
	}

	private static boolean method_33974(double d, double e, double f, double g) {
		return e <= g ? true : d * d + e * e + f * f >= 1.0;
	}
}
