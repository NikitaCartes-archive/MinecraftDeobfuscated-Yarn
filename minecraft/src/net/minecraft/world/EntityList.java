package net.minecraft.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;

public class EntityList {
	private Int2ObjectMap<Entity> entities = new Int2ObjectLinkedOpenHashMap<>();
	private Int2ObjectMap<Entity> field_27255 = new Int2ObjectLinkedOpenHashMap<>();
	@Nullable
	private Int2ObjectMap<Entity> cachedEntityList;

	private void method_31789() {
		if (this.cachedEntityList == this.entities) {
			this.field_27255.clear();

			for (Entry<Entity> entry : Int2ObjectMaps.fastIterable(this.entities)) {
				this.field_27255.put(entry.getIntKey(), (Entity)entry.getValue());
			}

			Int2ObjectMap<Entity> int2ObjectMap = this.entities;
			this.entities = this.field_27255;
			this.field_27255 = int2ObjectMap;
		}
	}

	public void addEntity(Entity entity) {
		this.method_31789();
		this.entities.put(entity.getEntityId(), entity);
	}

	public void removeEntity(Entity entity) {
		this.method_31789();
		this.entities.remove(entity.getEntityId());
	}

	public boolean hasEntity(Entity entity) {
		return this.entities.containsKey(entity.getEntityId());
	}

	/**
	 * Runs an action on every single entity.
	 * 
	 * @throws UnsupportedOperationException if there is an attempt to have more than one concurrent iteration
	 */
	public void forEachEntity(Consumer<Entity> action) {
		if (this.cachedEntityList != null) {
			throw new UnsupportedOperationException("Only one concurrent iteration supported");
		} else {
			this.cachedEntityList = this.entities;

			try {
				for (Entity entity : this.entities.values()) {
					action.accept(entity);
				}
			} finally {
				this.cachedEntityList = null;
			}
		}
	}
}
