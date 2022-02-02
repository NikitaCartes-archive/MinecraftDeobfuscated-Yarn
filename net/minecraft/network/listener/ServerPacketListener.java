/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.listener;

import net.minecraft.network.listener.PacketListener;

/**
 * The base interface for serverbound packet listeners.
 * 
 * @implNote Serverbound packet listeners log any uncaught exceptions
 * without crashing.
 */
public interface ServerPacketListener
extends PacketListener {
    @Override
    default public boolean shouldCrashOnException() {
        return false;
    }
}

