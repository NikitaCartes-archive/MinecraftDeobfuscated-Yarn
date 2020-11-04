package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public interface HeightLimitView {
	int getSectionCount();

	int getBottomSectionLimit();

	default int getTopSectionLimit() {
		return this.getBottomSectionLimit() + this.getSectionCount();
	}

	default int getHeight() {
		return this.getSectionCount() * 16;
	}

	default int getBottomHeightLimit() {
		return this.getBottomSectionLimit() * 16;
	}

	default int getTopHeightLimit() {
		return this.getBottomHeightLimit() + this.getHeight();
	}

	default boolean isOutOfHeightLimit(BlockPos pos) {
		return this.isOutOfHeightLimit(pos.getY());
	}

	default boolean isOutOfHeightLimit(int y) {
		return y < this.getBottomHeightLimit() || y >= this.getTopHeightLimit();
	}

	default int getSectionIndex(int y) {
		return this.getSectionIndexFromSection(y >> 4);
	}

	default int getSectionIndexFromSection(int section) {
		return section - this.getBottomSectionLimit();
	}

	default int getSection(int sectionIndex) {
		return sectionIndex + this.getBottomSectionLimit();
	}
}
