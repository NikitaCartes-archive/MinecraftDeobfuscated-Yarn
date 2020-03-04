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

public class OreFeature extends Feature<OreFeatureConfig> {
	public OreFeature(Function<Dynamic<?>, ? extends OreFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, OreFeatureConfig oreFeatureConfig
	) {
		float f = random.nextFloat() * (float) Math.PI;
		float g = (float)oreFeatureConfig.size / 8.0F;
		int i = MathHelper.ceil(((float)oreFeatureConfig.size / 16.0F * 2.0F + 1.0F) / 2.0F);
		double d = (double)((float)blockPos.getX() + MathHelper.sin(f) * g);
		double e = (double)((float)blockPos.getX() - MathHelper.sin(f) * g);
		double h = (double)((float)blockPos.getZ() + MathHelper.cos(f) * g);
		double j = (double)((float)blockPos.getZ() - MathHelper.cos(f) * g);
		int k = 2;
		double l = (double)(blockPos.getY() + random.nextInt(3) - 2);
		double m = (double)(blockPos.getY() + random.nextInt(3) - 2);
		int n = blockPos.getX() - MathHelper.ceil(g) - i;
		int o = blockPos.getY() - 2 - i;
		int p = blockPos.getZ() - MathHelper.ceil(g) - i;
		int q = 2 * (MathHelper.ceil(g) + i);
		int r = 2 * (2 + i);

		for (int s = n; s <= n + q; s++) {
			for (int t = p; t <= p + q; t++) {
				if (o <= iWorld.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) {
					return this.generateVeinPart(iWorld, random, oreFeatureConfig, d, e, h, j, l, m, n, o, p, q, r);
				}
			}
		}

		return false;
	}

	protected boolean generateVeinPart(
		IWorld world,
		Random random,
		OreFeatureConfig config,
		double startX,
		double endX,
		double startZ,
		double endZ,
		double startY,
		double endY,
		int x,
		int y,
		int z,
		int size,
		int i
	) {
		int j = 0;
		BitSet bitSet = new BitSet(size * i * size);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		double[] ds = new double[config.size * 4];

		for (int k = 0; k < config.size; k++) {
			float f = (float)k / (float)config.size;
			double d = MathHelper.lerp((double)f, startX, endX);
			double e = MathHelper.lerp((double)f, startY, endY);
			double g = MathHelper.lerp((double)f, startZ, endZ);
			double h = random.nextDouble() * (double)config.size / 16.0;
			double l = ((double)(MathHelper.sin((float) Math.PI * f) + 1.0F) * h + 1.0) / 2.0;
			ds[k * 4 + 0] = d;
			ds[k * 4 + 1] = e;
			ds[k * 4 + 2] = g;
			ds[k * 4 + 3] = l;
		}

		for (int k = 0; k < config.size - 1; k++) {
			if (!(ds[k * 4 + 3] <= 0.0)) {
				for (int m = k + 1; m < config.size; m++) {
					if (!(ds[m * 4 + 3] <= 0.0)) {
						double d = ds[k * 4 + 0] - ds[m * 4 + 0];
						double e = ds[k * 4 + 1] - ds[m * 4 + 1];
						double g = ds[k * 4 + 2] - ds[m * 4 + 2];
						double h = ds[k * 4 + 3] - ds[m * 4 + 3];
						if (h * h > d * d + e * e + g * g) {
							if (h > 0.0) {
								ds[m * 4 + 3] = -1.0;
							} else {
								ds[k * 4 + 3] = -1.0;
							}
						}
					}
				}
			}
		}

		for (int kx = 0; kx < config.size; kx++) {
			double n = ds[kx * 4 + 3];
			if (!(n < 0.0)) {
				double o = ds[kx * 4 + 0];
				double p = ds[kx * 4 + 1];
				double q = ds[kx * 4 + 2];
				int r = Math.max(MathHelper.floor(o - n), x);
				int s = Math.max(MathHelper.floor(p - n), y);
				int t = Math.max(MathHelper.floor(q - n), z);
				int u = Math.max(MathHelper.floor(o + n), r);
				int v = Math.max(MathHelper.floor(p + n), s);
				int w = Math.max(MathHelper.floor(q + n), t);

				for (int aa = r; aa <= u; aa++) {
					double ab = ((double)aa + 0.5 - o) / n;
					if (ab * ab < 1.0) {
						for (int ac = s; ac <= v; ac++) {
							double ad = ((double)ac + 0.5 - p) / n;
							if (ab * ab + ad * ad < 1.0) {
								for (int ae = t; ae <= w; ae++) {
									double af = ((double)ae + 0.5 - q) / n;
									if (ab * ab + ad * ad + af * af < 1.0) {
										int ag = aa - x + (ac - y) * size + (ae - z) * size * i;
										if (!bitSet.get(ag)) {
											bitSet.set(ag);
											mutable.set(aa, ac, ae);
											if (config.target.getCondition().test(world.getBlockState(mutable))) {
												world.setBlockState(mutable, config.state, 2);
												j++;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return j > 0;
	}
}
