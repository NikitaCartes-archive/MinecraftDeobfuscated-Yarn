/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event.listener;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

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
        public void dispatch(GameEvent event, @Nullable Entity entity, BlockPos pos) {
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
     * @param entity an entity related to the event
     * @param pos the block position of the event
     */
    public void dispatch(GameEvent var1, @Nullable Entity var2, BlockPos var3);
}

