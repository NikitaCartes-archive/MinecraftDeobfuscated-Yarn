/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event.listener;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;

/**
 * A game event dispatcher dispatches game events to its listeners.
 */
public interface GameEventDispatcher {
    /**
     * An unmodifiable, empty (non-operative) dispatcher.
     */
    public static final GameEventDispatcher EMPTY = new GameEventDispatcher(){

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void addListener(GameEventListener listener) {
        }

        @Override
        public void removeListener(GameEventListener listener) {
        }

        @Override
        public void dispatch(GameEvent event, Vec3d pos, GameEvent.Emitter emitter) {
        }
    };

    /**
     * Returns whether this dispatcher has no listeners.
     */
    public boolean isEmpty();

    /**
     * Adds a listener to this dispatcher.
     * 
     * @param listener the listener to add
     */
    public void addListener(GameEventListener var1);

    /**
     * Removes a listener from this dispatcher if it is present.
     * 
     * @param listener the listener to remove
     */
    public void removeListener(GameEventListener var1);

    /**
     * Dispatches an event to all the listeners in this dispatcher.
     * 
     * @param event the event
     */
    public void dispatch(GameEvent var1, Vec3d var2, GameEvent.Emitter var3);
}

