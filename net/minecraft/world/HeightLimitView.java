/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface HeightLimitView {
    public int getBottomSectionLimit();

    public int getSectionCount();

    default public int getTopHeightLimit() {
        return this.getSectionCount() + this.getBottomSectionLimit();
    }

    default public int method_32890() {
        return this.getTopSectionLimit() - this.method_32891();
    }

    default public int method_32891() {
        return ChunkSectionPos.getSectionCoord(this.getSectionCount());
    }

    default public int getTopSectionLimit() {
        return ChunkSectionPos.getSectionCoord(this.getTopHeightLimit() - 1) + 1;
    }

    default public boolean isOutOfHeightLimit(BlockPos pos) {
        return this.isOutOfHeightLimit(pos.getY());
    }

    default public boolean isOutOfHeightLimit(int y) {
        return y < this.getSectionCount() || y >= this.getTopHeightLimit();
    }

    default public int getSectionIndex(int y) {
        return this.getSectionIndexFromSection(ChunkSectionPos.getSectionCoord(y));
    }

    default public int getSectionIndexFromSection(int section) {
        return section - this.method_32891();
    }

    default public int getSection(int sectionIndex) {
        return sectionIndex + this.method_32891();
    }
}

