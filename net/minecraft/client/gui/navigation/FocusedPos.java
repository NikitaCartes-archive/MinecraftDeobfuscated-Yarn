/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;

/**
 * Represents the position of an {@link FocusedRect}.
 */
@Environment(value=EnvType.CLIENT)
public record FocusedPos(int x, int y) {
    public static FocusedPos of(NavigationAxis axis, int sameAxis, int otherAxis) {
        return switch (axis) {
            default -> throw new IncompatibleClassChangeError();
            case NavigationAxis.HORIZONTAL -> new FocusedPos(sameAxis, otherAxis);
            case NavigationAxis.VERTICAL -> new FocusedPos(otherAxis, sameAxis);
        };
    }

    public FocusedPos add(NavigationDirection direction) {
        return switch (direction) {
            default -> throw new IncompatibleClassChangeError();
            case NavigationDirection.DOWN -> new FocusedPos(this.x, this.y + 1);
            case NavigationDirection.UP -> new FocusedPos(this.x, this.y - 1);
            case NavigationDirection.LEFT -> new FocusedPos(this.x - 1, this.y);
            case NavigationDirection.RIGHT -> new FocusedPos(this.x + 1, this.y);
        };
    }

    public int getComponent(NavigationAxis axis) {
        return switch (axis) {
            default -> throw new IncompatibleClassChangeError();
            case NavigationAxis.HORIZONTAL -> this.x;
            case NavigationAxis.VERTICAL -> this.y;
        };
    }
}

