package net.minecraft.world;

/**
 * Isolates client and server side specific logic when loading and unloading entities to and from a world.
 */
public interface EntityLoader<T> {
	void method_31802(T entity);

	void destroyEntity(T entity);

	void addEntity(T entity);

	void removeEntity(T entity);

	void onLoadEntity(T entity);

	void onUnloadEntity(T entity);
}
