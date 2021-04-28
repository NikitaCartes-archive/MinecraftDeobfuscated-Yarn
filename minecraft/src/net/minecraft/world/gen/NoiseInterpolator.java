package net.minecraft.world.gen;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

public class NoiseInterpolator {
	private double[][] startNoiseBuffer;
	private double[][] endNoiseBuffer;
	private final int sizeY;
	private final int sizeZ;
	private final int minY;
	private final NoiseInterpolator.ColumnSampler columnSampler;
	private double x0y0z0;
	private double x0y0z1;
	private double x1y0z0;
	private double x1y0z1;
	private double x0y1z0;
	private double x0y1z1;
	private double x1y1z0;
	private double x1y1z1;
	private double x0z0;
	private double x1z0;
	private double x0z1;
	private double x1z1;
	private double z0;
	private double z1;
	private final int startX;
	private final int startZ;

	public NoiseInterpolator(int sizeX, int sizeY, int sizeZ, ChunkPos pos, int minY, NoiseInterpolator.ColumnSampler columnSampler) {
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.minY = minY;
		this.columnSampler = columnSampler;
		this.startNoiseBuffer = createBuffer(sizeY, sizeZ);
		this.endNoiseBuffer = createBuffer(sizeY, sizeZ);
		this.startX = pos.x * sizeX;
		this.startZ = pos.z * sizeZ;
	}

	private static double[][] createBuffer(int sizeY, int sizeZ) {
		int i = sizeZ + 1;
		int j = sizeY + 1;
		double[][] ds = new double[i][j];

		for (int k = 0; k < i; k++) {
			ds[k] = new double[j];
		}

		return ds;
	}

	public void sampleStartNoise() {
		this.sampleNoise(this.startNoiseBuffer, this.startX);
	}

	public void sampleEndNoise(int x) {
		this.sampleNoise(this.endNoiseBuffer, this.startX + x + 1);
	}

	private void sampleNoise(double[][] buffer, int noiseX) {
		for (int i = 0; i < this.sizeZ + 1; i++) {
			int j = this.startZ + i;
			this.columnSampler.fillNoiseColumn(buffer[i], noiseX, j, this.minY, this.sizeY);
		}
	}

	public void sampleNoiseCorners(int noiseY, int noiseZ) {
		this.x0y0z0 = this.startNoiseBuffer[noiseZ][noiseY];
		this.x0y0z1 = this.startNoiseBuffer[noiseZ + 1][noiseY];
		this.x1y0z0 = this.endNoiseBuffer[noiseZ][noiseY];
		this.x1y0z1 = this.endNoiseBuffer[noiseZ + 1][noiseY];
		this.x0y1z0 = this.startNoiseBuffer[noiseZ][noiseY + 1];
		this.x0y1z1 = this.startNoiseBuffer[noiseZ + 1][noiseY + 1];
		this.x1y1z0 = this.endNoiseBuffer[noiseZ][noiseY + 1];
		this.x1y1z1 = this.endNoiseBuffer[noiseZ + 1][noiseY + 1];
	}

	public void sampleNoiseY(double deltaY) {
		this.x0z0 = MathHelper.lerp(deltaY, this.x0y0z0, this.x0y1z0);
		this.x1z0 = MathHelper.lerp(deltaY, this.x1y0z0, this.x1y1z0);
		this.x0z1 = MathHelper.lerp(deltaY, this.x0y0z1, this.x0y1z1);
		this.x1z1 = MathHelper.lerp(deltaY, this.x1y0z1, this.x1y1z1);
	}

	public void sampleNoiseX(double deltaX) {
		this.z0 = MathHelper.lerp(deltaX, this.x0z0, this.x1z0);
		this.z1 = MathHelper.lerp(deltaX, this.x0z1, this.x1z1);
	}

	public double sampleNoise(double deltaZ) {
		return MathHelper.lerp(deltaZ, this.z0, this.z1);
	}

	public void swapBuffers() {
		double[][] ds = this.startNoiseBuffer;
		this.startNoiseBuffer = this.endNoiseBuffer;
		this.endNoiseBuffer = ds;
	}

	@FunctionalInterface
	public interface ColumnSampler {
		void fillNoiseColumn(double[] buffer, int x, int z, int minY, int noiseSizeY);
	}
}
