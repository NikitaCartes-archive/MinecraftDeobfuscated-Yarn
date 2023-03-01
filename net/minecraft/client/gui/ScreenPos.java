/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;

/**
 * Represents the position of a {@link ScreenRect}.
 */
@Environment(value=EnvType.CLIENT)
public record ScreenPos(int x, int y) {
    public static ScreenPos of(NavigationAxis axis, int sameAxis, int otherAxis) {
        return switch (axis) {
            default -> throw new IncompatibleClassChangeError();
            case NavigationAxis.HORIZONTAL -> new ScreenPos(sameAxis, otherAxis);
            case NavigationAxis.VERTICAL -> new ScreenPos(otherAxis, sameAxis);
        };
    }

    public ScreenPos add(NavigationDirection direction) {
        return switch (direction) {
            default -> throw new IncompatibleClassChangeError();
            case NavigationDirection.DOWN -> new ScreenPos(this.x, this.y + 1);
            case NavigationDirection.UP -> new ScreenPos(this.x, this.y - 1);
            case NavigationDirection.LEFT -> new ScreenPos(this.x - 1, this.y);
            case NavigationDirection.RIGHT -> new ScreenPos(this.x + 1, this.y);
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

