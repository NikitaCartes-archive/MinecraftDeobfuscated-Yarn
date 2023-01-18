/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.navigation;

import it.unimi.dsi.fastutil.ints.IntComparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationAxis;

@Environment(value=EnvType.CLIENT)
public enum NavigationDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    private final IntComparator comparator = (a, b) -> a == b ? 0 : (this.isBefore(a, b) ? -1 : 1);

    public NavigationAxis getAxis() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP, DOWN -> NavigationAxis.VERTICAL;
            case LEFT, RIGHT -> NavigationAxis.HORIZONTAL;
        };
    }

    public NavigationDirection getOpposite() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public boolean isPositive() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP, LEFT -> false;
            case DOWN, RIGHT -> true;
        };
    }

    /**
     * {@return whether the coordinate {@code a} comes after {@code b}}
     * 
     * <p>For example, if navigating downwards, {@code 2} comes after {@code 1},
     * while the opposite is true if navigating upwards. This always returns
     * {@code false} if two arguments are equal.
     * 
     * @see #isBefore
     */
    public boolean isAfter(int a, int b) {
        if (this.isPositive()) {
            return a > b;
        }
        return b > a;
    }

    /**
     * {@return whether the coordinate {@code a} comes before {@code b}}
     * 
     * <p>For example, if navigating downwards, {@code 1} comes before {@code 2},
     * while the opposite is true if navigating upwards. This always returns
     * {@code false} if two arguments are equal.
     * 
     * @see #isAfter
     */
    public boolean isBefore(int a, int b) {
        if (this.isPositive()) {
            return a < b;
        }
        return b < a;
    }

    /**
     * {@return the comparator that sorts the coordinates in ascending order}
     */
    public IntComparator getComparator() {
        return this.comparator;
    }
}

