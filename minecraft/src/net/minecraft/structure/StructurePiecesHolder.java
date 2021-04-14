package net.minecraft.structure;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockBox;

/**
 * A holder of generated structure pieces.
 * 
 * @see StructurePiece#fillOpenings
 */
public interface StructurePiecesHolder {
	/**
	 * Adds a structure piece into this holder.
	 * 
	 * @param piece the piece to add
	 */
	void addPiece(StructurePiece piece);

	/**
	 * Returns an arbitrary piece in this holder that intersects the given {@code box},
	 * or {@code null} if there is no such piece.
	 * 
	 * @param box the box to check intersection against
	 */
	@Nullable
	StructurePiece getIntersecting(BlockBox box);
}
