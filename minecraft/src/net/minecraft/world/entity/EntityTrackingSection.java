package net.minecraft.world.entity;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.util.math.Box;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A collection of entities tracked within a chunk section.
 */
public class EntityTrackingSection<T extends EntityLike> {
	protected static final Logger LOGGER = LogManager.getLogger();
	private final TypeFilterableList<T> collection;
	private EntityTrackingStatus status;

	public EntityTrackingSection(Class<T> entityClass, EntityTrackingStatus status) {
		this.status = status;
		this.collection = new TypeFilterableList<>(entityClass);
	}

	public void add(T entityLike) {
		this.collection.add(entityLike);
	}

	public boolean remove(T entityLike) {
		return this.collection.remove(entityLike);
	}

	public void forEach(Box box, Consumer<T> action) {
		for (T entityLike : this.collection) {
			if (entityLike.getBoundingBox().intersects(box)) {
				action.accept(entityLike);
			}
		}
	}

	public <U extends T> void forEach(TypeFilter<T, U> type, Box box, Consumer<? super U> action) {
		Collection<? extends T> collection = this.collection.getAllOfType(type.getBaseClass());
		if (!collection.isEmpty()) {
			for (T entityLike : collection) {
				U entityLike2 = (U)type.downcast(entityLike);
				if (entityLike2 != null && entityLike.getBoundingBox().intersects(box)) {
					action.accept(entityLike2);
				}
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
