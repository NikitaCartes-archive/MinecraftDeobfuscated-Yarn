package net.minecraft.world.gen.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5284;
import net.minecraft.world.biome.source.BiomeSource;

public class FloatingIslandsChunkGenerator extends SurfaceChunkGenerator<class_5284> {
	private final class_5284 field_24519;

	public FloatingIslandsChunkGenerator(BiomeSource biomeSource, long l, class_5284 arg) {
		super(biomeSource, l, arg, 8, 4, 128, true);
		this.field_24519 = arg;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator method_27997(long l) {
		return new FloatingIslandsChunkGenerator(this.biomeSource.method_27985(l), l, this.field_24519);
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
}
