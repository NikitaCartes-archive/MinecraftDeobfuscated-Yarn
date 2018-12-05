package net.minecraft.entity.mob;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;

public class MobVisibilityCache {
	private final MobEntity owner;
	private final List<Entity> visibleEntities = Lists.<Entity>newArrayList();
	private final List<Entity> invisibleEntities = Lists.<Entity>newArrayList();

	public MobVisibilityCache(MobEntity mobEntity) {
		this.owner = mobEntity;
	}

	public void clear() {
		this.visibleEntities.clear();
		this.invisibleEntities.clear();
	}

	public boolean canSee(Entity entity) {
		if (this.visibleEntities.contains(entity)) {
			return true;
		} else if (this.invisibleEntities.contains(entity)) {
			return false;
		} else {
			this.owner.world.getProfiler().begin("canSee");
			boolean bl = this.owner.canSee(entity);
			this.owner.world.getProfiler().end();
			if (bl) {
				this.visibleEntities.add(entity);
			} else {
				this.invisibleEntities.add(entity);
			}

			return bl;
		}
	}
}
