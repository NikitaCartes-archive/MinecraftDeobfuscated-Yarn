/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface HeightLimitView {
    public int getSectionCount();

    public int getBottomSectionLimit();

    default public int getTopHeightLimit() {
        return this.getBottomSectionLimit() + this.getSectionCount();
    }

    default public int getSections() {
        return this.getTopSectionLimit() - this.getMinimumSection();
    }

    default public int getMinimumSection() {
        return ChunkSectionPos.getSectionCoord(this.getBottomSectionLimit());
    }

    default public int getTopSectionLimit() {
        return ChunkSectionPos.getSectionCoord(this.getTopHeightLimit() - 1) + 1;
    }

    default public boolean isOutOfHeightLimit(BlockPos pos) {
        return this.isOutOfHeightLimit(pos.getY());
    }

    default public boolean isOutOfHeightLimit(int y) {
        return y < this.getBottomSectionLimit() || y >= this.getTopHeightLimit();
    }

    default public int getSectionIndex(int y) {
        return this.getSectionIndexFromSection(ChunkSectionPos.getSectionCoord(y));
    }

    default public int getSectionIndexFromSection(int section) {
        return section - this.getMinimumSection();
    }

    default public int getSection(int sectionIndex) {
        return sectionIndex + this.getMinimumSection();
    }
}

