package net.minecraft.world.gen.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5284;
import net.minecraft.world.biome.source.BiomeSource;

public class FloatingIslandsChunkGenerator extends SurfaceChunkGenerator<class_5284> {
	private final class_5284 generatorConfig;

	public FloatingIslandsChunkGenerator(BiomeSource biomeSource, long seed, class_5284 config) {
		super(biomeSource, seed, config, 8, 4, 128, true);
		this.generatorConfig = config;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator create(long seed) {
		return new FloatingIslandsChunkGenerator(this.biomeSource.create(seed), seed, this.generatorConfig);
	}

	@Override
	protected void sampleNoiseColumn(double[] buffer, int x, int z) {
		double d = 1368.824;
		double e = 684.412;
		double f = 17.110300000000002;
		double g = 4.277575000000001;
		int i = 64;
		int j = -3000;
		this.sampleNoiseColumn(buffer, x, z, 1368.824, 684.412, 17.110300000000002, 4.277575000000001, 64, -3000);
	}

	@Override
	protected double[] computeNoiseRange(int x, int z) {
		return new double[]{(double)this.biomeSource.getNoiseAt(x, z), 0.0};
	}

	@Override
	protected double computeNoiseFalloff(double depth, double scale, int y) {
		return 8.0 - depth;
	}

	@Override
	protected double topInterpolationStart() {
		return (double)((int)super.topInterpolationStart() / 2);
	}

	@Override
	protected double bottomInterpolationStart() {
		return 8.0;
	}

	@Override
	public int getSpawnHeight() {
		return 50;
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}
}
