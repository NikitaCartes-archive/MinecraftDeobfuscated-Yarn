/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public interface HeightLimitView {
    public int getSectionCount();

    public int getBottomSectionLimit();

    default public int getTopSectionLimit() {
        return this.getBottomSectionLimit() + this.getSectionCount();
    }

    default public int getHeight() {
        return this.getSectionCount() * 16;
    }

    default public int getBottomHeightLimit() {
        return this.getBottomSectionLimit() * 16;
    }

    default public int getTopHeightLimit() {
        return this.getBottomHeightLimit() + this.getHeight();
    }

    default public boolean isOutOfHeightLimit(BlockPos pos) {
        return this.isOutOfHeightLimit(pos.getY());
    }

    default public boolean isOutOfHeightLimit(int y) {
        return y < this.getBottomHeightLimit() || y >= this.getTopHeightLimit();
    }

    default public int getSectionIndex(int y) {
        return this.getSectionIndexFromSection(y >> 4);
    }

    default public int getSectionIndexFromSection(int section) {
        return section - this.getBottomSectionLimit();
    }

    default public int getSection(int sectionIndex) {
        return sectionIndex + this.getBottomSectionLimit();
    }
}

