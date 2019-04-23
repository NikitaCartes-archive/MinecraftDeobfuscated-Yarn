/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

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
            Raid raid = ((RaiderEntity)this.actor).getRaid();
            if (((RaiderEntity)this.actor).age % 20 == 0) {
                this.includeFreeRaiders(raid);
            }
            if (!((MobEntityWithAi)this.actor).isNavigating()) {
                Vec3d vec3d = new Vec3d(raid.getCenter());
                Vec3d vec3d2 = new Vec3d(((RaiderEntity)this.actor).x, ((RaiderEntity)this.actor).y, ((RaiderEntity)this.actor).z);
                Vec3d vec3d3 = vec3d2.subtract(vec3d);
                vec3d = vec3d3.multiply(0.4).add(vec3d);
                Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
                BlockPos blockPos = new BlockPos(vec3d4);
                blockPos = ((RaiderEntity)this.actor).world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
                if (!((MobEntity)this.actor).getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0)) {
                    this.moveToAlternativePosition();
                }
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

    private void moveToAlternativePosition() {
        Random random = ((LivingEntity)this.actor).getRand();
        BlockPos blockPos = ((RaiderEntity)this.actor).world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos((Entity)this.actor).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
        ((MobEntity)this.actor).getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0);
    }
}

