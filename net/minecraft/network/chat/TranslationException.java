/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.chat;

import net.minecraft.network.chat.TranslatableComponent;

public class TranslationException
extends IllegalArgumentException {
    public TranslationException(TranslatableComponent translatableComponent, String string) {
        super(String.format("Error parsing: %s: %s", translatableComponent, string));
    }

    public TranslationException(TranslatableComponent translatableComponent, int i) {
        super(String.format("Invalid index %d requested for %s", i, translatableComponent));
    }

    public TranslationException(TranslatableComponent translatableComponent, Throwable throwable) {
        super(String.format("Error while parsing: %s", translatableComponent), throwable);
    }
}

