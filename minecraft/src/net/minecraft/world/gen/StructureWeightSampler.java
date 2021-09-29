package net.minecraft.world.gen;

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
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Applies weights to noise values if they are near structures, placing terrain under them and hollowing out the space above them.
 */
public class StructureWeightSampler implements ChunkNoiseSampler.ColumnSampler {
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
	private final ObjectList<StructurePiece> pieces;
	private final ObjectList<JigsawJunction> junctions;
	private final ObjectListIterator<StructurePiece> pieceIterator;
	private final ObjectListIterator<JigsawJunction> junctionIterator;

	public StructureWeightSampler(StructureAccessor structureAccessor, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		this.junctions = new ObjectArrayList<>(32);
		this.pieces = new ObjectArrayList<>(10);

		for (StructureFeature<?> structureFeature : StructureFeature.LAND_MODIFYING_STRUCTURES) {
			structureAccessor.method_38853(ChunkSectionPos.from(chunk), structureFeature).forEach(structureStart -> {
				for (StructurePiece structurePiece : structureStart.getChildren()) {
					if (structurePiece.intersectsChunk(chunkPos, 12)) {
						if (structurePiece instanceof PoolStructurePiece) {
							PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
							StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
							if (projection == StructurePool.Projection.RIGID) {
								this.pieces.add(poolStructurePiece);
							}

							for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
								int k = jigsawJunction.getSourceX();
								int l = jigsawJunction.getSourceZ();
								if (k > i - 12 && l > j - 12 && k < i + 15 + 12 && l < j + 15 + 12) {
									this.junctions.add(jigsawJunction);
								}
							}
						} else {
							this.pieces.add(structurePiece);
						}
					}
				}
			});
		}

		this.pieceIterator = this.pieces.iterator();
		this.junctionIterator = this.junctions.iterator();
	}

	@Override
	public double calculateNoise(int i, int j, int k) {
		double d = 0.0;

		while (this.pieceIterator.hasNext()) {
			StructurePiece structurePiece = (StructurePiece)this.pieceIterator.next();
			BlockBox blockBox = structurePiece.getBoundingBox();
			int l = Math.max(0, Math.max(blockBox.getMinX() - i, i - blockBox.getMaxX()));
			int m = j - (blockBox.getMinY() + (structurePiece instanceof PoolStructurePiece ? ((PoolStructurePiece)structurePiece).getGroundLevelDelta() : 0));
			int n = Math.max(0, Math.max(blockBox.getMinZ() - k, k - blockBox.getMaxZ()));
			StructureWeightType structureWeightType = structurePiece.method_33882();
			if (structureWeightType == StructureWeightType.BURY) {
				d += getMagnitudeWeight(l, m, n);
			} else if (structureWeightType == StructureWeightType.BEARD) {
				d += getStructureWeight(l, m, n) * 0.8;
			}
		}

		this.pieceIterator.back(this.pieces.size());

		while (this.junctionIterator.hasNext()) {
			JigsawJunction jigsawJunction = (JigsawJunction)this.junctionIterator.next();
			int o = i - jigsawJunction.getSourceX();
			int l = j - jigsawJunction.getSourceGroundY();
			int m = k - jigsawJunction.getSourceZ();
			d += getStructureWeight(o, l, m) * 0.4;
		}

		this.junctionIterator.back(this.junctions.size());
		return d;
	}

	private static double getMagnitudeWeight(int x, int y, int z) {
		double d = MathHelper.magnitude(x, (double)y / 2.0, z);
		return MathHelper.clampedLerpFromProgress(d, 0.0, 6.0, 1.0, 0.0);
	}

	/**
	 * Gets the structure weight from the array from the given position, or 0 if the position is out of bounds.
	 */
	private static double getStructureWeight(int x, int y, int z) {
		int i = x + 12;
		int j = y + 12;
		int k = z + 12;
		if (i < 0 || i >= 24) {
			return 0.0;
		} else if (j < 0 || j >= 24) {
			return 0.0;
		} else {
			return k >= 0 && k < 24 ? (double)STRUCTURE_WEIGHT_TABLE[k * 24 * 24 + i * 24 + j] : 0.0;
		}
	}

	/**
	 * Calculates the structure weight for the given position.
	 * <p>The weight increases as x and z approach {@code (0, 0)}, and positive y values make the weight negative while negative y values make the weight positive.
	 */
	private static double calculateStructureWeight(int x, int y, int z) {
		double d = (double)(x * x + z * z);
		double e = (double)y + 0.5;
		double f = e * e;
		double g = Math.pow(Math.E, -(f / 16.0 + d / 16.0));
		double h = -e * MathHelper.fastInverseSqrt(f / 2.0 + d / 2.0) / 2.0;
		return h * g;
	}
}
