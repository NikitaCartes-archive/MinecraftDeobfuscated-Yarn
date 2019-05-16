/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BoundingBox;

public class TrackIronGolemTargetGoal
extends TrackTargetGoal {
    private final IronGolemEntity golem;
    private LivingEntity target;
    private final TargetPredicate field_19340 = new TargetPredicate().setBaseMaxDistance(64.0);

    public TrackIronGolemTargetGoal(IronGolemEntity ironGolemEntity) {
        super(ironGolemEntity, false, true);
        this.golem = ironGolemEntity;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        BoundingBox boundingBox = this.golem.getBoundingBox().expand(10.0, 8.0, 10.0);
        List<VillagerEntity> list = this.golem.world.getTargets(VillagerEntity.class, this.field_19340, this.golem, boundingBox);
        List<PlayerEntity> list2 = this.golem.world.getPlayersInBox(this.field_19340, this.golem, boundingBox);
        for (LivingEntity livingEntity : list) {
            VillagerEntity villagerEntity = (VillagerEntity)livingEntity;
            for (PlayerEntity playerEntity : list2) {
                int i = villagerEntity.method_20594(playerEntity);
                if (i > -100) continue;
                this.target = playerEntity;
            }
        }
        return this.target != null;
    }

    @Override
    public void start() {
        this.golem.setTarget(this.target);
        super.start();
    }
}

