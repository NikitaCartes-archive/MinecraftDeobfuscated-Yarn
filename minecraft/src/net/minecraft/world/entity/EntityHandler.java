package net.minecraft.world.entity;

/**
 * The entity handler exposes world's entity handling to entity managers.
 * 
 * <p>Each handler is usually associated with a {@link net.minecraft.world.World}.
 * 
 * @param <T> the type of entity handled
 */
public interface EntityHandler<T> {
	/**
	 * Called when an entity is newly created.
	 * 
	 * @param entity the created entity
	 */
	void create(T entity);

	/**
	 * Called when an entity is permanently destroyed.
	 * 
	 * @param entity the destroyed entity
	 */
	void destroy(T entity);

	/**
	 * Registers an entity for ticking.
	 * 
	 * @param entity the entity to tick
	 */
	void startTicking(T entity);

	/**
	 * Unregisters an entity for ticking.
	 * 
	 * @param entity the ticked entity
	 */
	void stopTicking(T entity);

	/**
	 * Registers an entity for tracking.
	 * 
	 * @param entity the entity to track
	 */
	void startTracking(T entity);

	/**
	 * Unregisters an entity for tracking.
	 * 
	 * @param entity the tracked entity
	 */
	void stopTracking(T entity);

	void updateLoadStatus(T entity);
}
