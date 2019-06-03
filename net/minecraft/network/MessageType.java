/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum MessageType {
    CHAT(0, false),
    SYSTEM(1, true),
    GAME_INFO(2, true);

    private final byte id;
    private final boolean interruptsNarration;

    private MessageType(byte b, boolean bl) {
        this.id = b;
        this.interruptsNarration = bl;
    }

    public byte getId() {
        return this.id;
    }

    public static MessageType byId(byte b) {
        for (MessageType messageType : MessageType.values()) {
            if (b != messageType.id) continue;
            return messageType;
        }
        return CHAT;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean interruptsNarration() {
        return this.interruptsNarration;
    }
}

