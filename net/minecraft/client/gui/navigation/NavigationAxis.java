/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationDirection;

@Environment(value=EnvType.CLIENT)
public enum NavigationAxis {
    HORIZONTAL,
    VERTICAL;


    public NavigationAxis getOther() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> VERTICAL;
            case VERTICAL -> HORIZONTAL;
        };
    }

    public NavigationDirection getPositiveDirection() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> NavigationDirection.RIGHT;
            case VERTICAL -> NavigationDirection.DOWN;
        };
    }

    public NavigationDirection getNegativeDirection() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> NavigationDirection.LEFT;
            case VERTICAL -> NavigationDirection.UP;
        };
    }

    public NavigationDirection getDirection(boolean positive) {
        return positive ? this.getPositiveDirection() : this.getNegativeDirection();
    }
}

