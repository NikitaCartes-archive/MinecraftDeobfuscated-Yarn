package net.minecraft.world.gen;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;

/**
 * Applies weights to noise values if they are near structures, placing terrain under them and hollowing out the space above them.
 */
public class StructureWeightSampler implements DensityFunctionTypes.class_7050 {
	public static final int field_31461 = 12;
	private static final int field_31462 = 24;
	private static final float[] STRUCTURE_WEIGHT_TABLE = Util.make(new float[13824], array -> {
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				for (int k = 0; k < 24; k++) {
					array[i * 24 * 24 + j * 24 + k] = (float)calculateStructureWeight(j - 12, k - 12, i - 12);
				}
			}
		}
	});
	private final ObjectListIterator<StructureWeightSampler.class_7301> pieceIterator;
	private final ObjectListIterator<JigsawJunction> junctionIterator;

	public static StructureWeightSampler method_42695(StructureAccessor structureAccessor, ChunkPos chunkPos) {
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		ObjectList<StructureWeightSampler.class_7301> objectList = new ObjectArrayList<>(10);
		ObjectList<JigsawJunction> objectList2 = new ObjectArrayList<>(32);
		structureAccessor.method_41035(chunkPos, structureFeature -> structureFeature.getTerrainAdaptation() != StructureTerrainAdaptation.NONE)
			.forEach(
				structureStart -> {
					StructureTerrainAdaptation structureTerrainAdaptation = structureStart.getFeature().getTerrainAdaptation();

					for (StructurePiece structurePiece : structureStart.getChildren()) {
						if (structurePiece.intersectsChunk(chunkPos, 12)) {
							if (structurePiece instanceof PoolStructurePiece) {
								PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
								StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
								if (projection == StructurePool.Projection.RIGID) {
									objectList.add(
										new StructureWeightSampler.class_7301(poolStructurePiece.getBoundingBox(), structureTerrainAdaptation, poolStructurePiece.getGroundLevelDelta())
									);
								}

								for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
									int k = jigsawJunction.getSourceX();
									int l = jigsawJunction.getSourceZ();
									if (k > i - 12 && l > j - 12 && k < i + 15 + 12 && l < j + 15 + 12) {
										objectList2.add(jigsawJunction);
									}
								}
							} else {
								objectList.add(new StructureWeightSampler.class_7301(structurePiece.getBoundingBox(), structureTerrainAdaptation, 0));
							}
						}
					}
				}
			);
		return new StructureWeightSampler(objectList.iterator(), objectList2.iterator());
	}

	@VisibleForTesting
	public StructureWeightSampler(ObjectListIterator<StructureWeightSampler.class_7301> objectListIterator, ObjectListIterator<JigsawJunction> objectListIterator2) {
		this.pieceIterator = objectListIterator;
		this.junctionIterator = objectListIterator2;
	}

	@Override
	public double sample(DensityFunction.NoisePos pos) {
		int i = pos.blockX();
		int j = pos.blockY();
		int k = pos.blockZ();
		double d = 0.0;

		while (this.pieceIterator.hasNext()) {
			StructureWeightSampler.class_7301 lv = (StructureWeightSampler.class_7301)this.pieceIterator.next();
			BlockBox blockBox = lv.box();
			int l = lv.groundLevelDelta();
			int m = Math.max(0, Math.max(blockBox.getMinX() - i, i - blockBox.getMaxX()));
			int n = Math.max(0, Math.max(blockBox.getMinZ() - k, k - blockBox.getMaxZ()));
			int o = blockBox.getMinY() + l;
			int p = j - o;

			int q = switch (lv.terrainAdjustment()) {
				case NONE -> 0;
				case BURY, BEARD_THIN -> p;
				case BEARD_BOX -> Math.max(0, Math.max(o - j, j - blockBox.getMaxY()));
			};

			d += switch (lv.terrainAdjustment()) {
				case NONE -> 0.0;
				case BURY -> getMagnitudeWeight(m, q, n);
				case BEARD_THIN, BEARD_BOX -> getStructureWeight(m, q, n, p) * 0.8;
			};
		}

		this.pieceIterator.back(Integer.MAX_VALUE);

		while (this.junctionIterator.hasNext()) {
			JigsawJunction jigsawJunction = (JigsawJunction)this.junctionIterator.next();
			int r = i - jigsawJunction.getSourceX();
			int l = j - jigsawJunction.getSourceGroundY();
			int m = k - jigsawJunction.getSourceZ();
			d += getStructureWeight(r, l, m, l) * 0.4;
		}

		this.junctionIterator.back(Integer.MAX_VALUE);
		return d;
	}

	@Override
	public double minValue() {
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public double maxValue() {
		return Double.POSITIVE_INFINITY;
	}

	private static double getMagnitudeWeight(int x, int y, int z) {
		double d = MathHelper.magnitude((double)x, (double)y / 2.0, (double)z);
		return MathHelper.clampedLerpFromProgress(d, 0.0, 6.0, 1.0, 0.0);
	}

	/**
	 * Gets the structure weight from the array from the given position, or 0 if the position is out of bounds.
	 */
	private static double getStructureWeight(int x, int y, int z, int i) {
		int j = x + 12;
		int k = y + 12;
		int l = z + 12;
		if (method_42692(j) && method_42692(k) && method_42692(l)) {
			double d = (double)i + 0.5;
			double e = MathHelper.squaredMagnitude((double)x, d, (double)z);
			double f = -d * MathHelper.fastInverseSqrt(e / 2.0) / 2.0;
			return f * (double)STRUCTURE_WEIGHT_TABLE[l * 24 * 24 + j * 24 + k];
		} else {
			return 0.0;
		}
	}

	private static boolean method_42692(int i) {
		return i >= 0 && i < 24;
	}

	/**
	 * Calculates the structure weight for the given position.
	 * <p>The weight increases as x and z approach {@code (0, 0)}, and positive y values make the weight negative while negative y values make the weight positive.
	 */
	private static double calculateStructureWeight(int x, int y, int z) {
		return method_42693(x, (double)y + 0.5, z);
	}

	private static double method_42693(int i, double d, int j) {
		double e = MathHelper.squaredMagnitude((double)i, d, (double)j);
		return Math.pow(Math.E, -e / 16.0);
	}

	@VisibleForTesting
	public static record class_7301(BlockBox box, StructureTerrainAdaptation terrainAdjustment, int groundLevelDelta) {
	}
}
