/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import net.minecraft.text.TranslatableTextContent;

public class TranslationException
extends IllegalArgumentException {
    public TranslationException(TranslatableTextContent text, String message) {
        super(String.format("Error parsing: %s: %s", text, message));
    }

    public TranslationException(TranslatableTextContent text, int index) {
        super(String.format("Invalid index %d requested for %s", index, text));
    }

    public TranslationException(TranslatableTextContent text, Throwable cause) {
        super(String.format("Error while parsing: %s", text), cause);
    }
}

