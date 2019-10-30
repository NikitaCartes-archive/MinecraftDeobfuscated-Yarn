package net.minecraft.world.gen.chunk;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.feature.Feature;

public class CavesChunkGenerator extends SurfaceChunkGenerator<CavesChunkGeneratorConfig> {
	private final double[] noiseFalloff = this.buildNoiseFalloff();

	public CavesChunkGenerator(World world, BiomeSource biomeSource, CavesChunkGeneratorConfig config) {
		super(world, biomeSource, 4, 8, 128, config, false);
	}

	@Override
	protected void sampleNoiseColumn(double[] buffer, int x, int z) {
		double d = 684.412;
		double e = 2053.236;
		double f = 8.555150000000001;
		double g = 34.2206;
		int i = -10;
		int j = 3;
		this.sampleNoiseColumn(buffer, x, z, 684.412, 2053.236, 8.555150000000001, 34.2206, 3, -10);
	}

	@Override
	protected double[] computeNoiseRange(int x, int z) {
		return new double[]{0.0, 0.0};
	}

	@Override
	protected double computeNoiseFalloff(double depth, double scale, int y) {
		return this.noiseFalloff[y];
	}

	private double[] buildNoiseFalloff() {
		double[] ds = new double[this.getNoiseSizeY()];

		for (int i = 0; i < this.getNoiseSizeY(); i++) {
			ds[i] = Math.cos((double)i * Math.PI * 6.0 / (double)this.getNoiseSizeY()) * 2.0;
			double d = (double)i;
			if (i > this.getNoiseSizeY() / 2) {
				d = (double)(this.getNoiseSizeY() - 1 - i);
			}

			if (d < 4.0) {
				d = 4.0 - d;
				ds[i] -= d * d * d * 10.0;
			}
		}

		return ds;
	}

	@Override
	public List<Biome.SpawnEntry> getEntitySpawnList(EntityCategory category, BlockPos pos) {
		if (category == EntityCategory.MONSTER) {
			if (Feature.NETHER_BRIDGE.isInsideStructure(this.world, pos)) {
				return Feature.NETHER_BRIDGE.getMonsterSpawns();
			}

			if (Feature.NETHER_BRIDGE.isApproximatelyInsideStructure(this.world, pos) && this.world.getBlockState(pos.method_10074()).getBlock() == Blocks.NETHER_BRICKS
				)
			 {
				return Feature.NETHER_BRIDGE.getMonsterSpawns();
			}
		}

		return super.getEntitySpawnList(category, pos);
	}

	@Override
	public int getSpawnHeight() {
		return this.world.getSeaLevel() + 1;
	}

	@Override
	public int getMaxY() {
		return 128;
	}

	@Override
	public int getSeaLevel() {
		return 32;
	}
}
