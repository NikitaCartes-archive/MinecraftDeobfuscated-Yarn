package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class OreFeature extends Feature<OreFeatureConfig> {
	public OreFeature(Codec<OreFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, OreFeatureConfig oreFeatureConfig
	) {
		float f = random.nextFloat() * (float) Math.PI;
		float g = (float)oreFeatureConfig.size / 8.0F;
		int i = MathHelper.ceil(((float)oreFeatureConfig.size / 16.0F * 2.0F + 1.0F) / 2.0F);
		double d = (double)blockPos.getX() + Math.sin((double)f) * (double)g;
		double e = (double)blockPos.getX() - Math.sin((double)f) * (double)g;
		double h = (double)blockPos.getZ() + Math.cos((double)f) * (double)g;
		double j = (double)blockPos.getZ() - Math.cos((double)f) * (double)g;
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
				if (o <= structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) {
					return this.generateVeinPart(structureWorldAccess, random, oreFeatureConfig, d, e, h, j, l, m, n, o, p, q, r);
				}
			}
		}

		return false;
	}

	protected boolean generateVeinPart(
		WorldAccess world,
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
		int k = config.size;
		double[] ds = new double[k * 4];

		for (int l = 0; l < k; l++) {
			float f = (float)l / (float)k;
			double d = MathHelper.lerp((double)f, startX, endX);
			double e = MathHelper.lerp((double)f, startY, endY);
			double g = MathHelper.lerp((double)f, startZ, endZ);
			double h = random.nextDouble() * (double)k / 16.0;
			double m = ((double)(MathHelper.sin((float) Math.PI * f) + 1.0F) * h + 1.0) / 2.0;
			ds[l * 4 + 0] = d;
			ds[l * 4 + 1] = e;
			ds[l * 4 + 2] = g;
			ds[l * 4 + 3] = m;
		}

		for (int l = 0; l < k - 1; l++) {
			if (!(ds[l * 4 + 3] <= 0.0)) {
				for (int n = l + 1; n < k; n++) {
					if (!(ds[n * 4 + 3] <= 0.0)) {
						double d = ds[l * 4 + 0] - ds[n * 4 + 0];
						double e = ds[l * 4 + 1] - ds[n * 4 + 1];
						double g = ds[l * 4 + 2] - ds[n * 4 + 2];
						double h = ds[l * 4 + 3] - ds[n * 4 + 3];
						if (h * h > d * d + e * e + g * g) {
							if (h > 0.0) {
								ds[n * 4 + 3] = -1.0;
							} else {
								ds[l * 4 + 3] = -1.0;
							}
						}
					}
				}
			}
		}

		for (int lx = 0; lx < k; lx++) {
			double o = ds[lx * 4 + 3];
			if (!(o < 0.0)) {
				double p = ds[lx * 4 + 0];
				double q = ds[lx * 4 + 1];
				double r = ds[lx * 4 + 2];
				int s = Math.max(MathHelper.floor(p - o), x);
				int t = Math.max(MathHelper.floor(q - o), y);
				int u = Math.max(MathHelper.floor(r - o), z);
				int v = Math.max(MathHelper.floor(p + o), s);
				int w = Math.max(MathHelper.floor(q + o), t);
				int aa = Math.max(MathHelper.floor(r + o), u);

				for (int ab = s; ab <= v; ab++) {
					double ac = ((double)ab + 0.5 - p) / o;
					if (ac * ac < 1.0) {
						for (int ad = t; ad <= w; ad++) {
							double ae = ((double)ad + 0.5 - q) / o;
							if (ac * ac + ae * ae < 1.0) {
								for (int af = u; af <= aa; af++) {
									double ag = ((double)af + 0.5 - r) / o;
									if (ac * ac + ae * ae + ag * ag < 1.0) {
										int ah = ab - x + (ad - y) * size + (af - z) * size * i;
										if (!bitSet.get(ah)) {
											bitSet.set(ah);
											mutable.set(ab, ad, af);
											if (config.target.test(world.getBlockState(mutable), random)) {
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
