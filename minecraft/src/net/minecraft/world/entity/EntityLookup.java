package net.minecraft.world.entity;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;

/**
 * An interface for looking up entities.
 * 
 * <p>It supports iteration, accessing by ID, or by intersection with boxes.
 * 
 * @param <T> the type of indexed entity
 */
public interface EntityLookup<T extends EntityLike> {
	/**
	 * Returns an entity by its network ID, or {@code null} if none is found.
	 */
	@Nullable
	T get(int id);

	/**
	 * Returns an entity by its UUID, or {@code null} if none is found.
	 */
	@Nullable
	T get(UUID uuid);

	/**
	 * Returns an unmodifiable iterable over all entities in this lookup.
	 */
	Iterable<T> iterate();

	/**
	 * Performs an {@code action} on each entity of type {@code U} within this
	 * lookup.
	 * 
	 * @param <U> the type of entity to perform action on
	 * 
	 * @param filter specifies the desired type of entity
	 * @param consumer the consumer, additionally checking whether to perform the next iteration or to stop early
	 */
	<U extends T> void forEach(TypeFilter<T, U> filter, LazyIterationConsumer<U> consumer);

	void forEachIntersects(Box box, Consumer<T> action);

	<U extends T> void forEachIntersects(TypeFilter<T, U> filter, Box box, LazyIterationConsumer<U> consumer);
}
