/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum AbsoluteHand {
    LEFT(new TranslatableText("options.mainHand.left", new Object[0])),
    RIGHT(new TranslatableText("options.mainHand.right", new Object[0]));

    private final Text name;

    private AbsoluteHand(Text text) {
        this.name = text;
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

