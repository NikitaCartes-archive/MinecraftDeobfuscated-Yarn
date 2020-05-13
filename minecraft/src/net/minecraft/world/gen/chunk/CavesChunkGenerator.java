package net.minecraft.world.gen.chunk;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.Feature;

public class CavesChunkGenerator extends SurfaceChunkGenerator<CavesChunkGeneratorConfig> {
	private final double[] noiseFalloff = this.buildNoiseFalloff();
	private final CavesChunkGeneratorConfig field_24511;

	public CavesChunkGenerator(BiomeSource biomeSource, long l, CavesChunkGeneratorConfig cavesChunkGeneratorConfig) {
		super(biomeSource, l, cavesChunkGeneratorConfig, 4, 8, 128, false);
		this.field_24511 = cavesChunkGeneratorConfig;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator method_27997(long l) {
		return new CavesChunkGenerator(this.biomeSource.method_27985(l), l, this.field_24511);
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
	public List<Biome.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor structureAccessor, SpawnGroup spawnGroup, BlockPos blockPos) {
		return spawnGroup == SpawnGroup.MONSTER && Feature.NETHER_BRIDGE.isInsideStructure(structureAccessor, blockPos)
			? Feature.NETHER_BRIDGE.getMonsterSpawns()
			: super.getEntitySpawnList(biome, structureAccessor, spawnGroup, blockPos);
	}

	@Override
	public int getMaxY() {
		return 128;
	}

	@Override
	public int getSeaLevel() {
		return 32;
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		return this.getMaxY() / 2;
	}
}
