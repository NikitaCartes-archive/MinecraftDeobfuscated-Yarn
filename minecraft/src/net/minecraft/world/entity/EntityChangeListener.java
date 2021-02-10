package net.minecraft.world.entity;

import net.minecraft.entity.Entity;

/**
 * A listener for an entity's changes associated to saving.
 * 
 * <p>Each callback is associated with an {@link net.minecraft.entity.Entity}.
 */
public interface EntityChangeListener {
	/**
	 * An empty listener.
	 */
	EntityChangeListener NONE = new EntityChangeListener() {
		@Override
		public void updateEntityPosition() {
		}

		@Override
		public void remove(Entity.RemovalReason reason) {
		}
	};

	void updateEntityPosition();

	void remove(Entity.RemovalReason reason);
}
