package net.minecraft.world.gen.chunk;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;

public class FloatingIslandsChunkGenerator extends SurfaceChunkGenerator<FloatingIslandsChunkGeneratorConfig> {
	private final BlockPos center;

	public FloatingIslandsChunkGenerator(IWorld iWorld, BiomeSource biomeSource, FloatingIslandsChunkGeneratorConfig floatingIslandsChunkGeneratorConfig) {
		super(iWorld, biomeSource, 8, 4, 128, floatingIslandsChunkGeneratorConfig, true);
		this.center = floatingIslandsChunkGeneratorConfig.getCenter();
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
		return new double[]{(double)this.biomeSource.getNoiseRange(x, z), 0.0};
	}

	@Override
	protected double computeNoiseFalloff(double depth, double scale, int y) {
		return 8.0 - depth;
	}

	@Override
	protected double method_16409() {
		return (double)((int)super.method_16409() / 2);
	}

	@Override
	protected double method_16410() {
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

	@Override
	public ChunkGeneratorType<?, ?> method_26490() {
		return ChunkGeneratorType.FLOATING_ISLANDS;
	}
}
