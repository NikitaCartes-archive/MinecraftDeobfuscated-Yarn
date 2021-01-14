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
		int horizontalSize,
		int verticalSize
	) {
		int i = 0;
		BitSet bitSet = new BitSet(horizontalSize * verticalSize * horizontalSize);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int j = config.size;
		double[] ds = new double[j * 4];

		for (int k = 0; k < j; k++) {
			float f = (float)k / (float)j;
			double d = MathHelper.lerp((double)f, startX, endX);
			double e = MathHelper.lerp((double)f, startY, endY);
			double g = MathHelper.lerp((double)f, startZ, endZ);
			double h = random.nextDouble() * (double)j / 16.0;
			double l = ((double)(MathHelper.sin((float) Math.PI * f) + 1.0F) * h + 1.0) / 2.0;
			ds[k * 4 + 0] = d;
			ds[k * 4 + 1] = e;
			ds[k * 4 + 2] = g;
			ds[k * 4 + 3] = l;
		}

		for (int k = 0; k < j - 1; k++) {
			if (!(ds[k * 4 + 3] <= 0.0)) {
				for (int m = k + 1; m < j; m++) {
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

		for (int kx = 0; kx < j; kx++) {
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
										int ag = aa - x + (ac - y) * horizontalSize + (ae - z) * horizontalSize * verticalSize;
										if (!bitSet.get(ag)) {
											bitSet.set(ag);
											mutable.set(aa, ac, ae);
											if (config.target.test(world.getBlockState(mutable), random)) {
												world.setBlockState(mutable, config.state, 2);
												i++;
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

		return i > 0;
	}
}
