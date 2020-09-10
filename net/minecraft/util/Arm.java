/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Arm {
    LEFT(new TranslatableText("options.mainHand.left")),
    RIGHT(new TranslatableText("options.mainHand.right"));

    private final Text optionName;

    private Arm(Text optionName) {
        this.optionName = optionName;
    }

    @Environment(value=EnvType.CLIENT)
    public Arm getOpposite() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

    public String toString() {
        return this.optionName.getString();
    }

    @Environment(value=EnvType.CLIENT)
    public Text getOptionName() {
        return this.optionName;
    }
}

