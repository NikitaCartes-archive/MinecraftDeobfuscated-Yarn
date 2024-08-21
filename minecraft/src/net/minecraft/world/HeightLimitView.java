package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

/**
 * A view with a height limit specification.
 */
public interface HeightLimitView {
	/**
	 * Returns the difference in the {@linkplain #getBottomY() minimum} and
	 * {@linkplain #getTopYInclusive() maximum} height.
	 * 
	 * <p>This is the number of blocks that can be modified in any vertical column
	 * within the view, or the vertical size, in blocks, of the view.
	 * 
	 * @return the difference in the minimum and maximum height
	 * @see #getBottomY()
	 * @see #getTopYInclusive()
	 */
	int getHeight();

	/**
	 * Returns the bottom Y level, or height, inclusive, of this view.
	 * 
	 * @see #getTopYInclusive()
	 * @see #getHeight()
	 */
	int getBottomY();

	/**
	 * Returns the top Y level, or height, inclusive, of this view.
	 * 
	 * @implNote This implementation sums up the bottom Y and the height, then subtracts 1.
	 * @see #getBottomY()
	 * @see #getHeight()
	 */
	default int getTopYInclusive() {
		return this.getBottomY() + this.getHeight() - 1;
	}

	/**
	 * Returns the number of sections, vertically, within this view.
	 * 
	 * @return the number of sections
	 * @see #getTopSectionCoord()
	 * @see #getBottomSectionCoord()
	 */
	default int countVerticalSections() {
		return this.getTopSectionCoord() - this.getBottomSectionCoord() + 1;
	}

	/**
	 * Returns the bottom section coordinate, inclusive, of this view.
	 * 
	 * @implNote This implementation passes the {@linkplain #getBottomY() bottom Y}
	 * through {@link net.minecraft.util.math.ChunkSectionPos#getSectionCoord(int)}.
	 * 
	 * @return the bottom section coordinate
	 * @see #getTopSectionCoord()
	 * @see #getBottomY()
	 */
	default int getBottomSectionCoord() {
		return ChunkSectionPos.getSectionCoord(this.getBottomY());
	}

	/**
	 * Returns the top section coordinate, exclusive, of this view.
	 * 
	 * @implNote This implementation passes the {@linkplain #getTopYInclusive() top Y}
	 * through {@link net.minecraft.util.math.ChunkSectionPos#getSectionCoord(int)}.
	 * 
	 * @return the top section coordinate
	 * @see #getBottomSectionCoord()
	 * @see #getTopYInclusive()
	 */
	default int getTopSectionCoord() {
		return ChunkSectionPos.getSectionCoord(this.getTopYInclusive());
	}

	/**
	 * @see #isOutOfHeightLimit(int)
	 */
	default boolean isInHeightLimit(int y) {
		return y >= this.getBottomY() && y <= this.getTopYInclusive();
	}

	/**
	 * Checks if {@code pos} is out of the height limit of this view.
	 * 
	 * @return {@code true} if {@code pos} is out of bounds, {@code false} otherwise.
	 * @see #isOutOfHeightLimit(int)
	 * 
	 * @param pos the position to check
	 */
	default boolean isOutOfHeightLimit(BlockPos pos) {
		return this.isOutOfHeightLimit(pos.getY());
	}

	/**
	 * Checks if {@code y} is out of the height limit of this view.
	 * 
	 * <p>{@code y} is out of bounds if it's lower than the {@linkplain #getBottomY
	 * bottom} or higher than the {@linkplain #getTopYInclusive() top}.
	 * 
	 * @return {@code true} if {@code y} is out of bounds, {@code false} otherwise.
	 * 
	 * @param y the Y level to check
	 */
	default boolean isOutOfHeightLimit(int y) {
		return y < this.getBottomY() || y > this.getTopYInclusive();
	}

	/**
	 * Returns a zero-based section index to which the {@code y} level belongs.
	 * 
	 * @return a zero-based index
	 */
	default int getSectionIndex(int y) {
		return this.sectionCoordToIndex(ChunkSectionPos.getSectionCoord(y));
	}

	/**
	 * Converts a section coordinate to a zero-based section index.
	 * 
	 * @return a zero-based index
	 * @see #sectionIndexToCoord(int) the inverse operation <code>sectionIndexToCoord</code>
	 * 
	 * @param coord the section coordinate
	 */
	default int sectionCoordToIndex(int coord) {
		return coord - this.getBottomSectionCoord();
	}

	/**
	 * Converts a zero-based section index to a section coordinate.
	 * 
	 * @return a section coordinate
	 * @see #sectionCoordToIndex(int) the inverse operation <code>sectionCoordToIndex</code>
	 * 
	 * @param index the zero-based section index
	 */
	default int sectionIndexToCoord(int index) {
		return index + this.getBottomSectionCoord();
	}

	static HeightLimitView create(int bottomY, int height) {
		return new HeightLimitView() {
			@Override
			public int getHeight() {
				return height;
			}

			@Override
			public int getBottomY() {
				return bottomY;
			}
		};
	}
}
