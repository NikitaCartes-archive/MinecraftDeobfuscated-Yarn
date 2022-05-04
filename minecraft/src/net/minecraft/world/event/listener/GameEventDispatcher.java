package net.minecraft.world.event.listener;

import java.util.function.BiConsumer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;

/**
 * A game event dispatcher dispatches game events to its listeners.
 */
public interface GameEventDispatcher {
	/**
	 * An unmodifiable, empty (non-operative) dispatcher.
	 */
	GameEventDispatcher EMPTY = new GameEventDispatcher() {
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
		public boolean dispatch(GameEvent event, Vec3d pos, GameEvent.Emitter emitter, BiConsumer<GameEventListener, Vec3d> onListenerAccept) {
			return false;
		}
	};

	/**
	 * Returns whether this dispatcher has no listeners.
	 */
	boolean isEmpty();

	/**
	 * Adds a listener to this dispatcher.
	 * 
	 * @param listener the listener to add
	 */
	void addListener(GameEventListener listener);

	/**
	 * Removes a listener from this dispatcher if it is present.
	 * 
	 * @param listener the listener to remove
	 */
	void removeListener(GameEventListener listener);

	/**
	 * Dispatches an event to all the listeners in this dispatcher.
	 * 
	 * @param event the event
	 */
	boolean dispatch(GameEvent event, Vec3d pos, GameEvent.Emitter emitter, BiConsumer<GameEventListener, Vec3d> onListenerAccept);
}
