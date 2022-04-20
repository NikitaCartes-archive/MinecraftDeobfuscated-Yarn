/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.minecraft.text.Text;

public interface TranslatableOption {
    public int getId();

    public String getTranslationKey();

    default public Text getText() {
        return Text.method_43471(this.getTranslationKey());
    }
}

