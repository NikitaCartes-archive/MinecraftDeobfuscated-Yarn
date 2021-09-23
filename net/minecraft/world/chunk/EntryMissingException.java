/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

public class EntryMissingException
extends RuntimeException {
    public EntryMissingException(int index) {
        super("Missing Palette entry for index " + index + ".");
    }
}

