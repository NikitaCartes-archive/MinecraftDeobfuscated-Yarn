package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class OreFeature extends Feature<OreFeatureConfig> {
	public OreFeature(Function<Dynamic<?>, ? extends OreFeatureConfig> function) {
		super(function);
	}

	public boolean method_13628(
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
				if (o <= iWorld.method_8589(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) {
					return this.generateVeinPart(iWorld, random, oreFeatureConfig, d, e, h, j, l, m, n, o, p, q, r);
				}
			}
		}

		return false;
	}

	protected boolean generateVeinPart(
		IWorld iWorld,
		Random random,
		OreFeatureConfig oreFeatureConfig,
		double d,
		double e,
		double f,
		double g,
		double h,
		double i,
		int j,
		int k,
		int l,
		int m,
		int n
	) {
		int o = 0;
		BitSet bitSet = new BitSet(m * n * m);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		double[] ds = new double[oreFeatureConfig.size * 4];

		for (int p = 0; p < oreFeatureConfig.size; p++) {
			float q = (float)p / (float)oreFeatureConfig.size;
			double r = MathHelper.lerp((double)q, d, e);
			double s = MathHelper.lerp((double)q, h, i);
			double t = MathHelper.lerp((double)q, f, g);
			double u = random.nextDouble() * (double)oreFeatureConfig.size / 16.0;
			double v = ((double)(MathHelper.sin((float) Math.PI * q) + 1.0F) * u + 1.0) / 2.0;
			ds[p * 4 + 0] = r;
			ds[p * 4 + 1] = s;
			ds[p * 4 + 2] = t;
			ds[p * 4 + 3] = v;
		}

		for (int p = 0; p < oreFeatureConfig.size - 1; p++) {
			if (!(ds[p * 4 + 3] <= 0.0)) {
				for (int w = p + 1; w < oreFeatureConfig.size; w++) {
					if (!(ds[w * 4 + 3] <= 0.0)) {
						double r = ds[p * 4 + 0] - ds[w * 4 + 0];
						double s = ds[p * 4 + 1] - ds[w * 4 + 1];
						double t = ds[p * 4 + 2] - ds[w * 4 + 2];
						double u = ds[p * 4 + 3] - ds[w * 4 + 3];
						if (u * u > r * r + s * s + t * t) {
							if (u > 0.0) {
								ds[w * 4 + 3] = -1.0;
							} else {
								ds[p * 4 + 3] = -1.0;
							}
						}
					}
				}
			}
		}

		for (int px = 0; px < oreFeatureConfig.size; px++) {
			double x = ds[px * 4 + 3];
			if (!(x < 0.0)) {
				double y = ds[px * 4 + 0];
				double z = ds[px * 4 + 1];
				double aa = ds[px * 4 + 2];
				int ab = Math.max(MathHelper.floor(y - x), j);
				int ac = Math.max(MathHelper.floor(z - x), k);
				int ad = Math.max(MathHelper.floor(aa - x), l);
				int ae = Math.max(MathHelper.floor(y + x), ab);
				int af = Math.max(MathHelper.floor(z + x), ac);
				int ag = Math.max(MathHelper.floor(aa + x), ad);

				for (int ah = ab; ah <= ae; ah++) {
					double ai = ((double)ah + 0.5 - y) / x;
					if (ai * ai < 1.0) {
						for (int aj = ac; aj <= af; aj++) {
							double ak = ((double)aj + 0.5 - z) / x;
							if (ai * ai + ak * ak < 1.0) {
								for (int al = ad; al <= ag; al++) {
									double am = ((double)al + 0.5 - aa) / x;
									if (ai * ai + ak * ak + am * am < 1.0) {
										int an = ah - j + (aj - k) * m + (al - l) * m * n;
										if (!bitSet.get(an)) {
											bitSet.set(an);
											mutable.set(ah, aj, al);
											if (oreFeatureConfig.target.getCondition().test(iWorld.method_8320(mutable))) {
												iWorld.method_8652(mutable, oreFeatureConfig.state, 2);
												o++;
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

		return o > 0;
	}
}
