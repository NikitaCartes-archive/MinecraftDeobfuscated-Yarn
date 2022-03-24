package net.minecraft.world.event.listener;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;

/**
 * A game event listener listens to game events from {@link GameEventDispatcher}s.
 */
public interface GameEventListener {
	/**
	 * Returns the position source of this listener.
	 */
	PositionSource getPositionSource();

	/**
	 * Returns the range, in blocks, of the listener.
	 */
	int getRange();

	/**
	 * Listens to an incoming game event.
	 * 
	 * @return {@code true} if the game event has been accepted by this listener
	 */
	boolean listen(ServerWorld world, GameEvent event, @Nullable Entity entity, Vec3d pos);
}
