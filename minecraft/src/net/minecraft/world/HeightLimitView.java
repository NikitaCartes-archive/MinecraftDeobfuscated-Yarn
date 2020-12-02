package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface HeightLimitView {
	int getBottomSectionLimit();

	int getSectionCount();

	default int getTopHeightLimit() {
		return this.getSectionCount() + this.getBottomSectionLimit();
	}

	default int method_32890() {
		return this.getTopSectionLimit() - this.method_32891();
	}

	default int method_32891() {
		return ChunkSectionPos.getSectionCoord(this.getSectionCount());
	}

	default int getTopSectionLimit() {
		return ChunkSectionPos.getSectionCoord(this.getTopHeightLimit() - 1) + 1;
	}

	default boolean isOutOfHeightLimit(BlockPos pos) {
		return this.isOutOfHeightLimit(pos.getY());
	}

	default boolean isOutOfHeightLimit(int y) {
		return y < this.getSectionCount() || y >= this.getTopHeightLimit();
	}

	default int getSectionIndex(int y) {
		return this.getSectionIndexFromSection(ChunkSectionPos.getSectionCoord(y));
	}

	default int getSectionIndexFromSection(int section) {
		return section - this.method_32891();
	}

	default int getSection(int sectionIndex) {
		return sectionIndex + this.method_32891();
	}
}
