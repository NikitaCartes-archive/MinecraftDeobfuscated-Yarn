package net.minecraft.world.entity;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.collection.TypeFilterableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A collection of entities tracked within a chunk section.
 */
public class EntityTrackingSection<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	private final TypeFilterableList<T> collection;
	private EntityTrackingStatus status;

	public EntityTrackingSection(Class<T> entityClass, EntityTrackingStatus status) {
		this.status = status;
		this.collection = new TypeFilterableList<>(entityClass);
	}

	public void add(T obj) {
		this.collection.add(obj);
	}

	public boolean remove(T obj) {
		return this.collection.remove(obj);
	}

	public void forEach(Predicate<? super T> predicate, Consumer<T> action) {
		for (T object : this.collection) {
			if (predicate.test(object)) {
				action.accept(object);
			}
		}
	}

	public <U extends T> void forEach(TypeFilter<T, U> type, Predicate<? super U> filter, Consumer<? super U> action) {
		for (T object : this.collection.getAllOfType(type.getBaseClass())) {
			U object2 = type.downcast(object);
			if (object2 != null && filter.test(object2)) {
				action.accept(object2);
			}
		}
	}

	public boolean isEmpty() {
		return this.collection.isEmpty();
	}

	public Stream<T> stream() {
		return this.collection.stream();
	}

	public EntityTrackingStatus getStatus() {
		return this.status;
	}

	public EntityTrackingStatus swapStatus(EntityTrackingStatus status) {
		EntityTrackingStatus entityTrackingStatus = this.status;
		this.status = status;
		return entityTrackingStatus;
	}

	@Debug
	public int size() {
		return this.collection.size();
	}
}
