package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;

/**
 * A rectangle on the screen.
 */
@Environment(EnvType.CLIENT)
public record ScreenRect(ScreenPos position, int width, int height) {
	private static final ScreenRect EMPTY = new ScreenRect(0, 0, 0, 0);

	public ScreenRect(int sameAxis, int otherAxis, int width, int height) {
		this(new ScreenPos(sameAxis, otherAxis), width, height);
	}

	/**
	 * {@return an empty rect}
	 */
	public static ScreenRect empty() {
		return EMPTY;
	}

	/**
	 * {@return a new rect}
	 * 
	 * @param otherAxisCoord the coordinate of the {@code axis}'s other axis
	 * @param sameAxisCoord the coordinate of the {@code axis} axis
	 * @param otherAxisLength the length of the edge whose axis is different from {@code axis}
	 * @param sameAxisLength the length of the edge whose axis is the same as {@code axis}
	 */
	public static ScreenRect of(NavigationAxis axis, int sameAxisCoord, int otherAxisCoord, int sameAxisLength, int otherAxisLength) {
		return switch (axis) {
			case HORIZONTAL -> new ScreenRect(sameAxisCoord, otherAxisCoord, sameAxisLength, otherAxisLength);
			case VERTICAL -> new ScreenRect(otherAxisCoord, sameAxisCoord, otherAxisLength, sameAxisLength);
		};
	}

	/**
	 * {@return a new rect of the same dimensions with the position incremented}
	 */
	public ScreenRect add(NavigationDirection direction) {
		return new ScreenRect(this.position.add(direction), this.width, this.height);
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
	public ScreenRect getBorder(NavigationDirection direction) {
		int i = this.getBoundingCoordinate(direction);
		NavigationAxis navigationAxis = direction.getAxis().getOther();
		int j = this.getBoundingCoordinate(navigationAxis.getNegativeDirection());
		int k = this.getLength(navigationAxis);
		return of(direction.getAxis(), i, j, 1, k).add(direction);
	}

	/**
	 * {@return whether this rect overlaps with {@code rect} in both axes}
	 */
	public boolean overlaps(ScreenRect other) {
		return this.overlaps(other, NavigationAxis.HORIZONTAL) && this.overlaps(other, NavigationAxis.VERTICAL);
	}

	/**
	 * {@return whether this rect overlaps with {@code rect} in {@code axis}}
	 */
	public boolean overlaps(ScreenRect other, NavigationAxis axis) {
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

	/**
	 * {@return the rect that intersects with {@code other}, or {@code null} if they do not
	 * intersect}
	 */
	@Nullable
	public ScreenRect intersection(ScreenRect other) {
		int i = Math.max(this.getLeft(), other.getLeft());
		int j = Math.max(this.getTop(), other.getTop());
		int k = Math.min(this.getRight(), other.getRight());
		int l = Math.min(this.getBottom(), other.getBottom());
		return i < k && j < l ? new ScreenRect(i, j, k - i, l - j) : null;
	}

	public int getTop() {
		return this.position.y();
	}

	public int getBottom() {
		return this.position.y() + this.height;
	}

	public int getLeft() {
		return this.position.x();
	}

	public int getRight() {
		return this.position.x() + this.width;
	}
}
