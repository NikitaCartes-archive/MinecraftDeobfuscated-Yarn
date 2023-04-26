package net.minecraft.entity.mob;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.entity.Entity;

public class MobVisibilityCache {
	private final MobEntity owner;
	private final IntSet visibleEntities = new IntOpenHashSet();
	private final IntSet invisibleEntities = new IntOpenHashSet();

	public MobVisibilityCache(MobEntity owner) {
		this.owner = owner;
	}

	public void clear() {
		this.visibleEntities.clear();
		this.invisibleEntities.clear();
	}

	public boolean canSee(Entity entity) {
		int i = entity.getId();
		if (this.visibleEntities.contains(i)) {
			return true;
		} else if (this.invisibleEntities.contains(i)) {
			return false;
		} else {
			this.owner.getWorld().getProfiler().push("hasLineOfSight");
			boolean bl = this.owner.canSee(entity);
			this.owner.getWorld().getProfiler().pop();
			if (bl) {
				this.visibleEntities.add(i);
			} else {
				this.invisibleEntities.add(i);
			}

			return bl;
		}
	}
}
