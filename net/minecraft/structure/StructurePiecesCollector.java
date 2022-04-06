/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.random.AbstractRandom;
import org.jetbrains.annotations.Nullable;

/**
 * A collector of structure pieces to be added to a structure start.
 * 
 * @see StructurePiecesList
 */
public class StructurePiecesCollector
implements StructurePiecesHolder {
    private final List<StructurePiece> pieces = Lists.newArrayList();

    @Override
    public void addPiece(StructurePiece piece) {
        this.pieces.add(piece);
    }

    @Override
    @Nullable
    public StructurePiece getIntersecting(BlockBox box) {
        return StructurePiece.firstIntersecting(this.pieces, box);
    }

    @Deprecated
    public void shift(int y) {
        for (StructurePiece structurePiece : this.pieces) {
            structurePiece.translate(0, y, 0);
        }
    }

    /**
     * Somewhat like {@code shiftInto(random, bottomY, topY - topPenalty)}.
     */
    @Deprecated
    public void shiftInto(int topY, int bottomY, AbstractRandom random, int topPenalty) {
        int i = topY - topPenalty;
        BlockBox blockBox = this.getBoundingBox();
        int j = blockBox.getBlockCountY() + bottomY + 1;
        if (j < i) {
            j += random.nextInt(i - j);
        }
        int k = j - blockBox.getMaxY();
        this.shift(k);
    }

    /**
     * Shifts all pieces so they lie within {@code [baseY, topY]} vertically.
     */
    public void shiftInto(AbstractRandom random, int baseY, int topY) {
        BlockBox blockBox = this.getBoundingBox();
        int i = topY - baseY + 1 - blockBox.getBlockCountY();
        int j = i > 1 ? baseY + random.nextInt(i) : baseY;
        int k = j - blockBox.getMinY();
        this.shift(k);
    }

    public StructurePiecesList toList() {
        return new StructurePiecesList(this.pieces);
    }

    public void clear() {
        this.pieces.clear();
    }

    public boolean isEmpty() {
        return this.pieces.isEmpty();
    }

    public BlockBox getBoundingBox() {
        return StructurePiece.boundingBox(this.pieces.stream());
    }
}

