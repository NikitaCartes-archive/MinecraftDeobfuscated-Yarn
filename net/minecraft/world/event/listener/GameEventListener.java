/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event.listener;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;

/**
 * A game event listener listens to game events from {@link GameEventDispatcher}s.
 */
public interface GameEventListener {
    default public boolean shouldListenImmediately() {
        return false;
    }

    /**
     * Returns the position source of this listener.
     */
    public PositionSource getPositionSource();

    /**
     * Returns the range, in blocks, of the listener.
     */
    public int getRange();

    /**
     * Listens to an incoming game event.
     * 
     * @return {@code true} if the game event has been accepted by this listener
     */
    public boolean listen(ServerWorld var1, GameEvent.Message var2);
}

