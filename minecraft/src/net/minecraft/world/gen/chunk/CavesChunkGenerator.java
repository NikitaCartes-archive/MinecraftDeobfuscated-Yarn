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
	private final double[] noiseFalloff = this.buidlNoiseFalloff();

	public CavesChunkGenerator(World world, BiomeSource biomeSource, CavesChunkGeneratorConfig cavesChunkGeneratorConfig) {
		super(world, biomeSource, 4, 8, 128, cavesChunkGeneratorConfig, false);
	}

	@Override
	protected void sampleNoiseColumn(double[] ds, int i, int j) {
		double d = 684.412;
		double e = 2053.236;
		double f = 8.555150000000001;
		double g = 34.2206;
		int k = -10;
		int l = 3;
		this.sampleNoiseColumn(ds, i, j, 684.412, 2053.236, 8.555150000000001, 34.2206, 3, -10);
	}

	@Override
	protected double[] computeNoiseRange(int i, int j) {
		return new double[]{0.0, 0.0};
	}

	@Override
	protected double computeNoiseFalloff(double d, double e, int i) {
		return this.noiseFalloff[i];
	}

	private double[] buidlNoiseFalloff() {
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
	public List<Biome.SpawnEntry> getEntitySpawnList(EntityCategory entityCategory, BlockPos blockPos) {
		if (entityCategory == EntityCategory.field_6302) {
			if (Feature.NETHER_BRIDGE.isInsideStructure(this.world, blockPos)) {
				return Feature.NETHER_BRIDGE.getMonsterSpawns();
			}

			if (Feature.NETHER_BRIDGE.isApproximatelyInsideStructure(this.world, blockPos) && this.world.getBlockState(blockPos.down()).getBlock() == Blocks.field_10266
				)
			 {
				return Feature.NETHER_BRIDGE.getMonsterSpawns();
			}
		}

		return super.getEntitySpawnList(entityCategory, blockPos);
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
