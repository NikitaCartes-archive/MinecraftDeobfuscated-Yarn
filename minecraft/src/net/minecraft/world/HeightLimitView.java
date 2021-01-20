package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface HeightLimitView {
	int getSectionCount();

	int getBottomSectionLimit();

	default int getTopHeightLimit() {
		return this.getBottomSectionLimit() + this.getSectionCount();
	}

	default int getSections() {
		return this.getTopSectionLimit() - this.getMinimumSection();
	}

	default int getMinimumSection() {
		return ChunkSectionPos.getSectionCoord(this.getBottomSectionLimit());
	}

	default int getTopSectionLimit() {
		return ChunkSectionPos.getSectionCoord(this.getTopHeightLimit() - 1) + 1;
	}

	default boolean isOutOfHeightLimit(BlockPos pos) {
		return this.isOutOfHeightLimit(pos.getY());
	}

	default boolean isOutOfHeightLimit(int y) {
		return y < this.getBottomSectionLimit() || y >= this.getTopHeightLimit();
	}

	default int getSectionIndex(int y) {
		return this.getSectionIndexFromSection(ChunkSectionPos.getSectionCoord(y));
	}

	default int getSectionIndexFromSection(int section) {
		return section - this.getMinimumSection();
	}

	default int getSection(int sectionIndex) {
		return sectionIndex + this.getMinimumSection();
	}
}
