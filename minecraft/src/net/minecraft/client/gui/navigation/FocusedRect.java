package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A rectangle on the screen that is focused.
 */
@Environment(EnvType.CLIENT)
public record FocusedRect(FocusedPos position, int width, int height) {
	private static final FocusedRect EMPTY = new FocusedRect(0, 0, 0, 0);

	public FocusedRect(int sameAxis, int otherAxis, int width, int height) {
		this(new FocusedPos(sameAxis, otherAxis), width, height);
	}

	/**
	 * {@return an empty rect}
	 */
	public static FocusedRect empty() {
		return EMPTY;
	}

	/**
	 * {@return a new rect}
	 * 
	 * @param sameAxisCoord the coordinate of the {@code axis} axis
	 * @param otherAxisCoord the coordinate of the {@code axis}'s other axis
	 * @param sameAxisLength the length of the edge whose axis is the same as {@code axis}
	 * @param otherAxisLength the length of the edge whose axis is different from {@code axis}
	 */
	public static FocusedRect of(NavigationAxis axis, int sameAxisCoord, int otherAxisCoord, int sameAxisLength, int otherAxisLength) {
		return switch (axis) {
			case HORIZONTAL -> new FocusedRect(sameAxisCoord, otherAxisCoord, sameAxisLength, otherAxisLength);
			case VERTICAL -> new FocusedRect(otherAxisCoord, sameAxisCoord, otherAxisLength, sameAxisLength);
		};
	}

	/**
	 * {@return a new rect of the same dimensions with the position incremented}
	 */
	public FocusedRect add(NavigationDirection direction) {
		return new FocusedRect(this.position.add(direction), this.width, this.height);
	}

	/**
	 * {@return the length of the rect in the given {@code axis}}
	 */
	public int getLength(NavigationAxis axis) {
		return switch (axis) {
			case HORIZONTAL -> this.width;
			case VERTICAL -> this.height;
		};
	}

	/**
	 * {@return the coordinate of the bounding box in the given {@code direction}}
	 */
	public int getBoundingCoordinate(NavigationDirection direction) {
		NavigationAxis navigationAxis = direction.getAxis();
		return direction.isPositive() ? this.position.getComponent(navigationAxis) + this.getLength(navigationAxis) - 1 : this.position.getComponent(navigationAxis);
	}

	/**
	 * {@return a rect representing the border of this rect in the given {@code direction}}
	 * 
	 * <p>Borders are one pixel thick.
	 */
	public FocusedRect getBorder(NavigationDirection direction) {
		int i = this.getBoundingCoordinate(direction);
		NavigationAxis navigationAxis = direction.getAxis().getOther();
		int j = this.getBoundingCoordinate(navigationAxis.getNegativeDirection());
		int k = this.getLength(navigationAxis);
		return of(direction.getAxis(), i, j, 1, k).add(direction);
	}

	/**
	 * {@return whether this rect overlaps with {@code rect} in both axes}
	 */
	public boolean overlaps(FocusedRect other) {
		return this.overlaps(other, NavigationAxis.HORIZONTAL) && this.overlaps(other, NavigationAxis.VERTICAL);
	}

	/**
	 * {@return whether this rect overlaps with {@code rect} in {@code axis}}
	 */
	public boolean overlaps(FocusedRect other, NavigationAxis axis) {
		int i = this.getBoundingCoordinate(axis.getNegativeDirection());
		int j = other.getBoundingCoordinate(axis.getNegativeDirection());
		int k = this.getBoundingCoordinate(axis.getPositiveDirection());
		int l = other.getBoundingCoordinate(axis.getPositiveDirection());
		return Math.max(i, j) <= Math.min(k, l);
	}

	/**
	 * {@return the center of this rect in the given {@code axis}}
	 */
	public int getCenter(NavigationAxis axis) {
		return (this.getBoundingCoordinate(axis.getPositiveDirection()) + this.getBoundingCoordinate(axis.getNegativeDirection())) / 2;
	}
}
