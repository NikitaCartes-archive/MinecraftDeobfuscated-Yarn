/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level.storage;

import net.minecraft.text.Text;

public class LevelStorageException
extends RuntimeException {
    private final Text message;

    public LevelStorageException(Text message) {
        super(message.getString());
        this.message = message;
    }

    public Text getMessage() {
        return this.message;
    }
}

