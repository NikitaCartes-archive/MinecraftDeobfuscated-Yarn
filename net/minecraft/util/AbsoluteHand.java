/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum AbsoluteHand {
    LEFT(new TranslatableComponent("options.mainHand.left", new Object[0])),
    RIGHT(new TranslatableComponent("options.mainHand.right", new Object[0]));

    private final Component name;

    private AbsoluteHand(Component component) {
        this.name = component;
    }

    @Environment(value=EnvType.CLIENT)
    public AbsoluteHand getOpposite() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

    public String toString() {
        return this.name.getString();
    }
}

