/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;
import org.jetbrains.annotations.Nullable;

/**
 * A holder of structure pieces to be added.
 * 
 * @see StructurePiece#fillOpenings
 */
public interface StructurePiecesHolder {
    /**
     * Adds a structure piece into this holder.
     * 
     * @param piece the piece to add
     */
    public void addPiece(StructurePiece var1);

    /**
     * Returns an arbitrary piece in this holder that intersects the given {@code box},
     * or {@code null} if there is no such piece.
     * 
     * @param box the box to check intersection against
     */
    @Nullable
    public StructurePiece getIntersecting(BlockBox var1);
}

