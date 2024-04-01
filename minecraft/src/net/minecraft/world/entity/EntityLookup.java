package net.minecraft.world.entity;

import java.util.List;
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
	EntityLookup<?> field_50995 = new EntityLookup<EntityLike>() {
		@Nullable
		@Override
		public EntityLike get(int id) {
			return null;
		}

		@Nullable
		@Override
		public EntityLike get(UUID uuid) {
			return null;
		}

		@Override
		public Iterable<EntityLike> iterate() {
			return List.of();
		}

		@Override
		public <U extends EntityLike> void forEach(TypeFilter<EntityLike, U> filter, LazyIterationConsumer<U> consumer) {
		}

		@Override
		public void forEachIntersects(Box box, Consumer<EntityLike> action) {
		}

		@Override
		public <U extends EntityLike> void forEachIntersects(TypeFilter<EntityLike, U> filter, Box box, LazyIterationConsumer<U> consumer) {
		}
	};

	static <T extends EntityLike> EntityLookup<T> method_59217() {
		return (EntityLookup<T>)field_50995;
	}

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
