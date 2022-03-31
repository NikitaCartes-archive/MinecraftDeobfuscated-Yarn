/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface TranslatableOption {
    public int getId();

    public String getTranslationKey();

    default public Text getText() {
        return new TranslatableText(this.getTranslationKey());
    }
}

