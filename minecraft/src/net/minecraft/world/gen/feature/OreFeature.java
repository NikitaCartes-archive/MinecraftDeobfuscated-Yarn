package net.minecraft.world.gen.feature;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class OreFeature extends Feature<OreFeatureConfig> {
	public OreFeature(Codec<OreFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<OreFeatureConfig> context) {
		Random random = context.getRandom();
		BlockPos blockPos = context.getPos();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		OreFeatureConfig oreFeatureConfig = context.getConfig();
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

		Set<ChunkSection> set = Sets.<ChunkSection>newHashSet();

		for (int nx = 0; nx < k; nx++) {
			double d = ds[nx * 4 + 3];
			if (!(d < 0.0)) {
				double e = ds[nx * 4 + 0];
				double g = ds[nx * 4 + 1];
				double h = ds[nx * 4 + 2];
				int o = Math.max(MathHelper.floor(e - d), x);
				int p = Math.max(MathHelper.floor(g - d), y);
				int q = Math.max(MathHelper.floor(h - d), z);
				int r = Math.max(MathHelper.floor(e + d), o);
				int s = Math.max(MathHelper.floor(g + d), p);
				int t = Math.max(MathHelper.floor(h + d), q);

				for (int u = o; u <= r; u++) {
					double v = ((double)u + 0.5 - e) / d;
					if (v * v < 1.0) {
						for (int w = p; w <= s; w++) {
							double aa = ((double)w + 0.5 - g) / d;
							if (v * v + aa * aa < 1.0) {
								for (int ab = q; ab <= t; ab++) {
									double ac = ((double)ab + 0.5 - h) / d;
									if (v * v + aa * aa + ac * ac < 1.0 && !world.isOutOfHeightLimit(w)) {
										int ad = u - x + (w - y) * size + (ab - z) * size * i;
										if (!bitSet.get(ad)) {
											bitSet.set(ad);
											mutable.set(u, w, ab);
											Chunk chunk = world.getChunk(ChunkSectionPos.getSectionCoord(u), ChunkSectionPos.getSectionCoord(ab));
											ChunkSection chunkSection = chunk.getSection(chunk.getSectionIndex(w));
											if (set.add(chunkSection)) {
												chunkSection.lock();
											}

											int ae = ChunkSectionPos.getLocalCoord(u);
											int af = ChunkSectionPos.getLocalCoord(w);
											int ag = ChunkSectionPos.getLocalCoord(ab);
											if (config.target.test(chunkSection.getBlockState(ae, af, ag), random)) {
												chunkSection.setBlockState(ae, af, ag, config.state, false);
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

		for (ChunkSection chunkSection2 : set) {
			chunkSection2.unlock();
		}

		return j > 0;
	}
}
