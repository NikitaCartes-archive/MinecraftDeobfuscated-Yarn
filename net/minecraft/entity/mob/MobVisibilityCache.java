/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;

public class MobVisibilityCache {
    private final MobEntity owner;
    private final List<Entity> visibleEntities = Lists.newArrayList();
    private final List<Entity> invisibleEntities = Lists.newArrayList();

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
        }
        if (this.invisibleEntities.contains(entity)) {
            return false;
        }
        this.owner.world.getProfiler().push("canSee");
        boolean bl = this.owner.canSee(entity);
        this.owner.world.getProfiler().pop();
        if (bl) {
            this.visibleEntities.add(entity);
        } else {
            this.invisibleEntities.add(entity);
        }
        return bl;
    }
}

