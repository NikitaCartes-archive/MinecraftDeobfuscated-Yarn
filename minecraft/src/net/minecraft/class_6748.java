package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.gen.chunk.Blender;
import org.apache.commons.lang3.mutable.MutableDouble;

public class class_6748 {
	private static final class_6748 field_35501 = new class_6748(null, List.of(), List.of()) {
		@Override
		public TerrainNoisePoint method_39340(int i, int j, TerrainNoisePoint terrainNoisePoint) {
			return terrainNoisePoint;
		}

		@Override
		public double method_39338(int i, int j, int k, double d) {
			return d;
		}
	};
	private static final int field_35502 = BiomeCoords.fromChunk(7) - 1;
	private static final int field_35503 = BiomeCoords.toChunk(field_35502 + 3);
	private static final int field_35504 = 2;
	private static final int field_35505 = BiomeCoords.toChunk(5);
	private static final double field_35506 = 10.0;
	private static final double field_35507 = 0.0;
	private final ChunkRegion field_35508;
	private final List<Blender> field_35509;
	private final List<Blender> field_35510;

	public static class_6748 method_39336() {
		return field_35501;
	}

	public static class_6748 method_39342(@Nullable ChunkRegion chunkRegion) {
		if (chunkRegion == null) {
			return field_35501;
		} else {
			List<Blender> list = Lists.<Blender>newArrayList();
			List<Blender> list2 = Lists.<Blender>newArrayList();
			ChunkPos chunkPos = chunkRegion.getCenterPos();

			for (int i = -field_35503; i <= field_35503; i++) {
				for (int j = -field_35503; j <= field_35503; j++) {
					Blender blender = Blender.create(chunkRegion, chunkPos.x + i, chunkPos.z + j);
					if (blender != Blender.NULL_BLENDER) {
						list.add(blender);
						if (i >= -field_35505 && i <= field_35505 && j >= -field_35505 && j <= field_35505) {
							list2.add(blender);
						}
					}
				}
			}

			return list.isEmpty() && list2.isEmpty() ? field_35501 : new class_6748(chunkRegion, list, list2);
		}
	}

	class_6748(ChunkRegion chunkRegion, List<Blender> list, List<Blender> list2) {
		this.field_35508 = chunkRegion;
		this.field_35509 = list;
		this.field_35510 = list2;
	}

	public TerrainNoisePoint method_39340(int i, int j, TerrainNoisePoint terrainNoisePoint) {
		int k = ChunkSectionPos.getSectionCoord(i);
		int l = ChunkSectionPos.getSectionCoord(j);
		int m = BiomeCoords.fromBlock(i);
		int n = BiomeCoords.fromBlock(j);
		Blender blender = Blender.create(this.field_35508, k, l);
		if (blender != Blender.NULL_BLENDER) {
			double d = blender.method_39344(m, n);
			if (d != Double.POSITIVE_INFINITY) {
				return new TerrainNoisePoint(method_39337(d), 10.0, 0.0);
			}
		}

		MutableDouble mutableDouble = new MutableDouble(0.0);
		MutableDouble mutableDouble2 = new MutableDouble(0.0);
		MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);

		for (Blender blender2 : this.field_35509) {
			blender2.method_39351((kx, lx, d) -> {
				double e = MathHelper.hypot(m - kx, (double)(n - lx));
				if (!(e > (double)field_35502)) {
					if (e < mutableDouble3.doubleValue()) {
						mutableDouble3.setValue(e);
					}

					double fx = 1.0 / (e * e * e * e);
					mutableDouble2.add(d * fx);
					mutableDouble.add(fx);
				}
			});
		}

		if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
			return terrainNoisePoint;
		} else {
			double e = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
			double f = MathHelper.clamp(mutableDouble3.doubleValue() / (double)(field_35502 + 1), 0.0, 1.0);
			f = 3.0 * f * f - 2.0 * f * f * f;
			double g = MathHelper.lerp(f, method_39337(e), terrainNoisePoint.offset());
			double h = MathHelper.lerp(f, 10.0, terrainNoisePoint.factor());
			double o = MathHelper.lerp(f, 0.0, terrainNoisePoint.peaks());
			return new TerrainNoisePoint(g, h, o);
		}
	}

	private static double method_39337(double d) {
		double e = 1.0;
		double f = d + 0.5;
		double g = MathHelper.floorMod(f, 8.0);
		return 1.0 * (32.0 * (f - 128.0) - 3.0 * (f - 120.0) * g + 3.0 * g * g) / (128.0 * (32.0 - 3.0 * g));
	}

	public double method_39338(int i, int j, int k, double d) {
		int l = ChunkSectionPos.getSectionCoord(i);
		int m = ChunkSectionPos.getSectionCoord(k);
		int n = BiomeCoords.fromBlock(i);
		int o = j / 8;
		int p = BiomeCoords.fromBlock(k);
		Blender blender = Blender.create(this.field_35508, l, m);
		if (blender != Blender.NULL_BLENDER) {
			double e = blender.method_39345(n, o, p);
			if (e != Double.POSITIVE_INFINITY) {
				return e;
			}
		}

		MutableDouble mutableDouble = new MutableDouble(0.0);
		MutableDouble mutableDouble2 = new MutableDouble(0.0);
		MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);

		for (Blender blender2 : this.field_35510) {
			blender2.method_39346(o - 2, o + 2, (lx, mx, nx, dx) -> {
				double e = MathHelper.magnitude(n - lx, (double)(o - mx), p - nx);
				if (!(e > 2.0)) {
					if (e < mutableDouble3.doubleValue()) {
						mutableDouble3.setValue(e);
					}

					double f = 1.0 / (e * e * e * e);
					mutableDouble2.add(dx * f);
					mutableDouble.add(f);
				}
			});
		}

		if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
			return d;
		} else {
			double f = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
			double g = MathHelper.clamp(mutableDouble3.doubleValue() / 3.0, 0.0, 1.0);
			return MathHelper.lerp(g, f, d);
		}
	}
}
