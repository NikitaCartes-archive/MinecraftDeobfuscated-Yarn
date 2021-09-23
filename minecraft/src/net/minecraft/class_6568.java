package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class class_6568 {
	final int field_34596;
	final int field_34597;
	final int field_34598;
	final int field_34599;
	final int field_34600;
	final int field_34601;
	final int field_34602;
	private final int field_34603;
	private final int field_34604;
	final List<class_6568.NoiseInterpolator> field_34605;
	private final double[][] field_34606;
	private final double[][] field_34607;
	private final double[][] field_34608;
	private final double[][] field_34609;
	private final double[][] field_34610;
	private final TerrainNoisePoint[][] field_34611;
	private final Long2ObjectMap<TerrainNoisePoint> field_34612 = new Long2ObjectOpenHashMap<>();
	private final AquiferSampler field_34613;
	private final class_6568.class_6569 field_34614;
	private final class_6568.class_6569 field_34615;

	public class_6568(
		int i,
		int j,
		int k,
		int l,
		int m,
		NoiseColumnSampler noiseColumnSampler,
		int n,
		int o,
		class_6568.ColumnSampler columnSampler,
		Supplier<ChunkGeneratorSettings> supplier,
		AquiferSampler.class_6565 arg
	) {
		this.field_34596 = i;
		this.field_34597 = j;
		this.field_34598 = l;
		this.field_34599 = k;
		this.field_34600 = m;
		this.field_34601 = Math.floorDiv(n, i);
		this.field_34602 = Math.floorDiv(o, i);
		this.field_34605 = Lists.<class_6568.NoiseInterpolator>newArrayList();
		this.field_34603 = BiomeCoords.fromBlock(n);
		this.field_34604 = BiomeCoords.fromBlock(o);
		int p = BiomeCoords.fromBlock(k * i);
		this.field_34606 = new double[p + 1][];
		this.field_34607 = new double[p + 1][];
		this.field_34608 = new double[p + 1][];
		this.field_34609 = new double[p + 1][];
		this.field_34610 = new double[p + 1][];
		this.field_34611 = new TerrainNoisePoint[p + 1][];

		for (int q = 0; q <= p; q++) {
			int r = this.field_34603 + q;
			this.field_34606[q] = new double[p + 1];
			this.field_34607[q] = new double[p + 1];
			this.field_34608[q] = new double[p + 1];
			this.field_34609[q] = new double[p + 1];
			this.field_34610[q] = new double[p + 1];
			this.field_34611[q] = new TerrainNoisePoint[p + 1];

			for (int s = 0; s <= p; s++) {
				int t = this.field_34604 + s;
				class_6568.class_6570 lv = method_38346(noiseColumnSampler, r, t);
				this.field_34606[q][s] = lv.field_34617;
				this.field_34607[q][s] = lv.field_34618;
				this.field_34608[q][s] = lv.field_34619;
				this.field_34609[q][s] = lv.field_34620;
				this.field_34610[q][s] = lv.field_34621;
				this.field_34611[q][s] = lv.field_34616;
			}
		}

		this.field_34613 = noiseColumnSampler.method_38389(this, n, o, m, l, arg, ((ChunkGeneratorSettings)supplier.get()).hasAquifers());
		this.field_34614 = noiseColumnSampler.method_38390(this, columnSampler, ((ChunkGeneratorSettings)supplier.get()).hasNoodleCaves());
		this.field_34615 = noiseColumnSampler.method_38391(this, ((ChunkGeneratorSettings)supplier.get()).hasOreVeins());
	}

	@Debug
	public static class_6568.class_6570 method_38346(NoiseColumnSampler noiseColumnSampler, int i, int j) {
		return new class_6568.class_6570(noiseColumnSampler, i, j);
	}

	public double method_38340(int i, int j) {
		return this.field_34606[i - this.field_34603][j - this.field_34604];
	}

	public double method_38351(int i, int j) {
		return this.field_34607[i - this.field_34603][j - this.field_34604];
	}

	public double method_38357(int i, int j) {
		return this.field_34608[i - this.field_34603][j - this.field_34604];
	}

	public double method_38358(int i, int j) {
		return this.field_34609[i - this.field_34603][j - this.field_34604];
	}

	public double method_38359(int i, int j) {
		return this.field_34610[i - this.field_34603][j - this.field_34604];
	}

	public TerrainNoisePoint method_38360(int i, int j) {
		return this.field_34611[i - this.field_34603][j - this.field_34604];
	}

	public TerrainNoisePoint method_38353(NoiseColumnSampler noiseColumnSampler, int i, int j) {
		int k = i - this.field_34603;
		int l = j - this.field_34604;
		int m = this.field_34611.length;
		return k >= 0 && l >= 0 && k < m && l < m
			? this.field_34611[k][l]
			: this.field_34612
				.computeIfAbsent(ChunkPos.toLong(i, j), lx -> method_38346(noiseColumnSampler, ChunkPos.getPackedX(lx), ChunkPos.getPackedZ(lx)).field_34616);
	}

	public TerrainNoisePoint method_38361(int i, int j) {
		int k = BiomeCoords.fromBlock(i) - this.field_34603;
		int l = BiomeCoords.fromBlock(j) - this.field_34604;
		TerrainNoisePoint terrainNoisePoint = this.field_34611[k][l];
		TerrainNoisePoint terrainNoisePoint2 = this.field_34611[k][l + 1];
		TerrainNoisePoint terrainNoisePoint3 = this.field_34611[k + 1][l];
		TerrainNoisePoint terrainNoisePoint4 = this.field_34611[k + 1][l + 1];
		double d = (double)Math.floorMod(i, 4) / 4.0;
		double e = (double)Math.floorMod(j, 4) / 4.0;
		double f = MathHelper.lerp2(d, e, terrainNoisePoint.offset(), terrainNoisePoint3.offset(), terrainNoisePoint2.offset(), terrainNoisePoint4.offset());
		double g = MathHelper.lerp2(d, e, terrainNoisePoint.factor(), terrainNoisePoint3.factor(), terrainNoisePoint2.factor(), terrainNoisePoint4.factor());
		double h = MathHelper.lerp2(d, e, terrainNoisePoint.peaks(), terrainNoisePoint3.peaks(), terrainNoisePoint2.peaks(), terrainNoisePoint4.peaks());
		return new TerrainNoisePoint(f, g, h);
	}

	public class_6568.NoiseInterpolator method_38344(class_6568.ColumnSampler columnSampler) {
		return new class_6568.NoiseInterpolator(columnSampler);
	}

	public void method_38336() {
		this.field_34605.forEach(noiseInterpolator -> noiseInterpolator.sampleStartNoise());
	}

	public void method_38339(int i) {
		this.field_34605.forEach(noiseInterpolator -> noiseInterpolator.sampleEndNoise(i));
	}

	public void method_38362(int i, int j) {
		this.field_34605.forEach(noiseInterpolator -> noiseInterpolator.sampleNoiseCorners(i, j));
	}

	public void method_38337(double d) {
		this.field_34605.forEach(noiseInterpolator -> noiseInterpolator.sampleNoiseY(d));
	}

	public void method_38349(double d) {
		this.field_34605.forEach(noiseInterpolator -> noiseInterpolator.sampleNoiseX(d));
	}

	public void method_38355(double d) {
		this.field_34605.forEach(noiseInterpolator -> noiseInterpolator.sampleNoise(d));
	}

	public void method_38348() {
		this.field_34605.forEach(class_6568.NoiseInterpolator::swapBuffers);
	}

	public AquiferSampler method_38354() {
		return this.field_34613;
	}

	@Nullable
	protected BlockState method_38341(int i, int j, int k) {
		return this.field_34614.calculate(i, j, k);
	}

	@Nullable
	protected BlockState method_38352(int i, int j, int k) {
		return this.field_34615.calculate(i, j, k);
	}

	@FunctionalInterface
	public interface ColumnSampler {
		double calculateNoise(int x, int y, int z);
	}

	public class NoiseInterpolator implements class_6568.class_6573 {
		private double[][] startNoiseBuffer;
		private double[][] endNoiseBuffer;
		private final class_6568.ColumnSampler columnSampler;
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
		private double result;

		NoiseInterpolator(class_6568.ColumnSampler columnSampler) {
			this.columnSampler = columnSampler;
			this.startNoiseBuffer = this.createBuffer(class_6568.this.field_34598, class_6568.this.field_34599);
			this.endNoiseBuffer = this.createBuffer(class_6568.this.field_34598, class_6568.this.field_34599);
			class_6568.this.field_34605.add(this);
		}

		private double[][] createBuffer(int i, int j) {
			int k = j + 1;
			int l = i + 1;
			double[][] ds = new double[k][l];

			for (int m = 0; m < k; m++) {
				ds[m] = new double[l];
			}

			return ds;
		}

		void sampleStartNoise() {
			this.sampleNoise(this.startNoiseBuffer, class_6568.this.field_34601);
		}

		void sampleEndNoise(int x) {
			this.sampleNoise(this.endNoiseBuffer, class_6568.this.field_34601 + x + 1);
		}

		private void sampleNoise(double[][] ds, int noiseX) {
			for (int i = 0; i < class_6568.this.field_34599 + 1; i++) {
				int j = class_6568.this.field_34602 + i;

				for (int k = 0; k < class_6568.this.field_34598 + 1; k++) {
					int l = k + class_6568.this.field_34600;
					int m = l * class_6568.this.field_34597;
					double d = this.columnSampler.calculateNoise(noiseX * class_6568.this.field_34596, m, j * class_6568.this.field_34596);
					ds[i][k] = d;
				}
			}
		}

		void sampleNoiseCorners(int noiseY, int noiseZ) {
			this.x0y0z0 = this.startNoiseBuffer[noiseZ][noiseY];
			this.x0y0z1 = this.startNoiseBuffer[noiseZ + 1][noiseY];
			this.x1y0z0 = this.endNoiseBuffer[noiseZ][noiseY];
			this.x1y0z1 = this.endNoiseBuffer[noiseZ + 1][noiseY];
			this.x0y1z0 = this.startNoiseBuffer[noiseZ][noiseY + 1];
			this.x0y1z1 = this.startNoiseBuffer[noiseZ + 1][noiseY + 1];
			this.x1y1z0 = this.endNoiseBuffer[noiseZ][noiseY + 1];
			this.x1y1z1 = this.endNoiseBuffer[noiseZ + 1][noiseY + 1];
		}

		void sampleNoiseY(double deltaY) {
			this.x0z0 = MathHelper.lerp(deltaY, this.x0y0z0, this.x0y1z0);
			this.x1z0 = MathHelper.lerp(deltaY, this.x1y0z0, this.x1y1z0);
			this.x0z1 = MathHelper.lerp(deltaY, this.x0y0z1, this.x0y1z1);
			this.x1z1 = MathHelper.lerp(deltaY, this.x1y0z1, this.x1y1z1);
		}

		void sampleNoiseX(double deltaX) {
			this.z0 = MathHelper.lerp(deltaX, this.x0z0, this.x1z0);
			this.z1 = MathHelper.lerp(deltaX, this.x0z1, this.x1z1);
		}

		void sampleNoise(double deltaZ) {
			this.result = MathHelper.lerp(deltaZ, this.z0, this.z1);
		}

		@Override
		public double sample() {
			return this.result;
		}

		private void swapBuffers() {
			double[][] ds = this.startNoiseBuffer;
			this.startNoiseBuffer = this.endNoiseBuffer;
			this.endNoiseBuffer = ds;
		}
	}

	@FunctionalInterface
	public interface class_6569 {
		@Nullable
		BlockState calculate(int i, int j, int k);
	}

	public static final class class_6570 {
		final double field_34617;
		final double field_34618;
		final double field_34619;
		final double field_34620;
		final double field_34621;
		@Debug
		public final TerrainNoisePoint field_34616;

		class_6570(NoiseColumnSampler noiseColumnSampler, int i, int j) {
			this.field_34617 = (double)i + noiseColumnSampler.method_38377(i, 0, j);
			this.field_34618 = (double)j + noiseColumnSampler.method_38377(j, i, 0);
			this.field_34619 = noiseColumnSampler.method_38401(this.field_34617, 0.0, this.field_34618);
			this.field_34620 = noiseColumnSampler.method_38407(this.field_34617, 0.0, this.field_34618);
			this.field_34621 = noiseColumnSampler.method_38404(this.field_34617, 0.0, this.field_34618);
			this.field_34616 = noiseColumnSampler.createTerrainNoisePoint(
				BiomeCoords.toBlock(i), BiomeCoords.toBlock(j), (float)this.field_34619, (float)this.field_34620, (float)this.field_34621
			);
		}
	}

	@FunctionalInterface
	public interface class_6571 {
		class_6568.class_6573 instantiate(class_6568 arg);
	}

	@FunctionalInterface
	public interface class_6573 {
		double sample();
	}
}
