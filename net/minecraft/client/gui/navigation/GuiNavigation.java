/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;

/**
 * Represents a directional navigation initiated by keyboard.
 */
@Environment(value=EnvType.CLIENT)
public interface GuiNavigation {
    public NavigationDirection getDirection();

    @Environment(value=EnvType.CLIENT)
    public record Arrow(NavigationDirection direction) implements GuiNavigation
    {
        @Override
        public NavigationDirection getDirection() {
            return this.direction.getAxis() == NavigationAxis.VERTICAL ? this.direction : NavigationDirection.DOWN;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Down
    implements GuiNavigation {
        @Override
        public NavigationDirection getDirection() {
            return NavigationDirection.DOWN;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Tab(boolean forward) implements GuiNavigation
    {
        @Override
        public NavigationDirection getDirection() {
            return this.forward ? NavigationDirection.DOWN : NavigationDirection.UP;
        }
    }
}

