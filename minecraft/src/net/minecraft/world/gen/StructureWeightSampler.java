package net.minecraft.world.gen;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.structure.Structure;

/**
 * Applies weights to noise values if they are near structures, placing terrain under them and hollowing out the space above them.
 */
public class StructureWeightSampler implements DensityFunctionTypes.Beardifying {
	public static final int INDEX_OFFSET = 12;
	private static final int EDGE_LENGTH = 24;
	private static final float[] STRUCTURE_WEIGHT_TABLE = Util.make(new float[13824], array -> {
		for(int i = 0; i < 24; ++i) {
			for(int j = 0; j < 24; ++j) {
				for(int k = 0; k < 24; ++k) {
					array[i * 24 * 24 + j * 24 + k] = (float)calculateStructureWeight(j - 12, k - 12, i - 12);
				}
			}
		}
	});
	private final ObjectListIterator<StructureWeightSampler.Piece> pieceIterator;
	private final ObjectListIterator<JigsawJunction> junctionIterator;

	public static StructureWeightSampler createStructureWeightSampler(StructureAccessor world, ChunkPos pos) {
		int i = pos.getStartX();
		int j = pos.getStartZ();
		ObjectList<StructureWeightSampler.Piece> objectList = new ObjectArrayList(10);
		ObjectList<JigsawJunction> objectList2 = new ObjectArrayList<>(32);
		world.getStructureStarts(pos, structure -> structure.getTerrainAdaptation() != StructureTerrainAdaptation.NONE)
			.forEach(
				start -> {
					StructureTerrainAdaptation structureTerrainAdaptation = start.getStructure().getTerrainAdaptation();
		
					for(StructurePiece structurePiece : start.getChildren()) {
						if (structurePiece.intersectsChunk(pos, 12)) {
							if (structurePiece instanceof PoolStructurePiece poolStructurePiece) {
								StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
								if (projection == StructurePool.Projection.RIGID) {
									objectList.add(
										new StructureWeightSampler.Piece(poolStructurePiece.getBoundingBox(), structureTerrainAdaptation, poolStructurePiece.getGroundLevelDelta())
									);
								}
		
								for(JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
									int ixx = jigsawJunction.getSourceX();
									int jxx = jigsawJunction.getSourceZ();
									if (ixx > i - 12 && jxx > j - 12 && ixx < i + 15 + 12 && jxx < j + 15 + 12) {
										objectList2.add(jigsawJunction);
									}
								}
							} else {
								objectList.add(new StructureWeightSampler.Piece(structurePiece.getBoundingBox(), structureTerrainAdaptation, 0));
							}
						}
					}
				}
			);
		return new StructureWeightSampler(objectList.iterator(), objectList2.iterator());
	}

	@VisibleForTesting
	public StructureWeightSampler(ObjectListIterator<StructureWeightSampler.Piece> pieceIterator, ObjectListIterator<JigsawJunction> junctionIterator) {
		this.pieceIterator = pieceIterator;
		this.junctionIterator = junctionIterator;
	}

	@Override
	public double sample(DensityFunction.NoisePos pos) {
		int i = pos.blockX();
		int j = pos.blockY();
		int k = pos.blockZ();

		double d;
		double var10001;
		for(d = 0.0; this.pieceIterator.hasNext(); d += var10001) {
			StructureWeightSampler.Piece piece = (StructureWeightSampler.Piece)this.pieceIterator.next();
			BlockBox blockBox = piece.box();
			int l = piece.groundLevelDelta();
			int m = Math.max(0, Math.max(blockBox.getMinX() - i, i - blockBox.getMaxX()));
			int n = Math.max(0, Math.max(blockBox.getMinZ() - k, k - blockBox.getMaxZ()));
			int o = blockBox.getMinY() + l;
			int p = j - o;

			int q = switch(piece.terrainAdjustment()) {
				case NONE -> 0;
				case BURY, BEARD_THIN -> p;
				case BEARD_BOX -> Math.max(0, Math.max(o - j, j - blockBox.getMaxY()));
			};
			switch(piece.terrainAdjustment()) {
				case NONE:
					var10001 = 0.0;
					break;
				case BURY:
					var10001 = getMagnitudeWeight(m, q, n);
					break;
				case BEARD_THIN:
				case BEARD_BOX:
					var10001 = getStructureWeight(m, q, n, p) * 0.8;
					break;
				default:
					throw new IncompatibleClassChangeError();
			}
		}

		this.pieceIterator.back(Integer.MAX_VALUE);

		while(this.junctionIterator.hasNext()) {
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
		return MathHelper.clampedMap(d, 0.0, 6.0, 1.0, 0.0);
	}

	/**
	 * Gets the structure weight from the array from the given position, or 0 if the position is out of bounds.
	 */
	private static double getStructureWeight(int x, int y, int z, int yy) {
		int i = x + 12;
		int j = y + 12;
		int k = z + 12;
		if (indexInBounds(i) && indexInBounds(j) && indexInBounds(k)) {
			double d = (double)yy + 0.5;
			double e = MathHelper.squaredMagnitude((double)x, d, (double)z);
			double f = -d * MathHelper.fastInverseSqrt(e / 2.0) / 2.0;
			return f * (double)STRUCTURE_WEIGHT_TABLE[k * 24 * 24 + i * 24 + j];
		} else {
			return 0.0;
		}
	}

	private static boolean indexInBounds(int i) {
		return i >= 0 && i < 24;
	}

	/**
	 * Calculates the structure weight for the given position.
	 * <p>The weight increases as x and z approach {@code (0, 0)}, and positive y values make the weight negative while negative y values make the weight positive.
	 */
	private static double calculateStructureWeight(int x, int y, int z) {
		return structureWeight(x, (double)y + 0.5, z);
	}

	private static double structureWeight(int x, double y, int z) {
		double d = MathHelper.squaredMagnitude((double)x, y, (double)z);
		return Math.pow(Math.E, -d / 16.0);
	}

	@VisibleForTesting
	public static record Piece(BlockBox box, StructureTerrainAdaptation terrainAdjustment, int groundLevelDelta) {
	}
}
