/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MoveToRaidCenterGoal<T extends RaiderEntity>
extends Goal {
    private final T actor;

    public MoveToRaidCenterGoal(T raiderEntity) {
        this.actor = raiderEntity;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return ((MobEntity)this.actor).getTarget() == null && !((Entity)this.actor).hasPassengers() && ((RaiderEntity)this.actor).hasActiveRaid() && !((RaiderEntity)this.actor).getRaid().isFinished() && !((ServerWorld)((RaiderEntity)this.actor).world).isNearOccupiedPointOfInterest(new BlockPos((Entity)this.actor));
    }

    @Override
    public boolean shouldContinue() {
        return ((RaiderEntity)this.actor).hasActiveRaid() && !((RaiderEntity)this.actor).getRaid().isFinished() && ((RaiderEntity)this.actor).world instanceof ServerWorld && !((ServerWorld)((RaiderEntity)this.actor).world).isNearOccupiedPointOfInterest(new BlockPos((Entity)this.actor));
    }

    @Override
    public void tick() {
        if (((RaiderEntity)this.actor).hasActiveRaid()) {
            Vec3d vec3d;
            Raid raid = ((RaiderEntity)this.actor).getRaid();
            if (((RaiderEntity)this.actor).age % 20 == 0) {
                this.includeFreeRaiders(raid);
            }
            if (!((MobEntityWithAi)this.actor).isNavigating() && (vec3d = TargetFinder.method_23735(this.actor, 15, 4, new Vec3d(raid.getCenter()))) != null) {
                ((MobEntity)this.actor).getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
            }
        }
    }

    private void includeFreeRaiders(Raid raid) {
        if (raid.isActive()) {
            HashSet<RaiderEntity> set = Sets.newHashSet();
            List<RaiderEntity> list = ((RaiderEntity)this.actor).world.getEntities(RaiderEntity.class, ((Entity)this.actor).getBoundingBox().expand(16.0), raiderEntity -> !raiderEntity.hasActiveRaid() && RaidManager.isValidRaiderFor(raiderEntity, raid));
            set.addAll(list);
            for (RaiderEntity raiderEntity2 : set) {
                raid.addRaider(raid.getGroupsSpawned(), raiderEntity2, null, true);
            }
        }
    }
}

