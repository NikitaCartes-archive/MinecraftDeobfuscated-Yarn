package net.minecraft.world.entity;

import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

/**
 * A prototype of entity that's suitable for entity manager to handle.
 */
public interface EntityLike {
	/**
	 * Returns the network ID of this entity.
	 */
	int getId();

	UUID getUuid();

	BlockPos getBlockPos();

	Box getBoundingBox();

	void setListener(EntityChangeListener listener);

	/**
	 * Returns a stream consisting of this entity and its passengers in which
	 * this entity's passengers are iterated before this entity.
	 * 
	 * <p>Moreover, this stream guarantees that any entity only appears after
	 * all its passengers have appeared in the stream. This is useful for
	 * certain actions that must be applied on passengers before applying on
	 * this entity.
	 * 
	 * @implNote The default implementation is very costly.
	 * 
	 * @see net.minecraft.entity.Entity#streamSelfAndPassengers()
	 */
	Stream<? extends EntityLike> streamPassengersAndSelf();

	void setRemoved(Entity.RemovalReason reason);

	boolean shouldSave();

	boolean isPlayer();
}
