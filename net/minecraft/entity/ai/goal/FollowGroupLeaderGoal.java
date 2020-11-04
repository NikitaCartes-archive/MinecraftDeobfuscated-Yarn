/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.SchoolingFishEntity;

public class FollowGroupLeaderGoal
extends Goal {
    private final SchoolingFishEntity fish;
    private int moveDelay;
    private int checkSurroundingDelay;

    public FollowGroupLeaderGoal(SchoolingFishEntity fish) {
        this.fish = fish;
        this.checkSurroundingDelay = this.getSurroundingSearchDelay(fish);
    }

    protected int getSurroundingSearchDelay(SchoolingFishEntity fish) {
        return 200 + fish.getRandom().nextInt(200) % 20;
    }

    @Override
    public boolean canStart() {
        if (this.fish.hasOtherFishInGroup()) {
            return false;
        }
        if (this.fish.hasLeader()) {
            return true;
        }
        if (this.checkSurroundingDelay > 0) {
            --this.checkSurroundingDelay;
            return false;
        }
        this.checkSurroundingDelay = this.getSurroundingSearchDelay(this.fish);
        Predicate<SchoolingFishEntity> predicate = schoolingFishEntity -> schoolingFishEntity.canHaveMoreFishInGroup() || !schoolingFishEntity.hasLeader();
        List<SchoolingFishEntity> list = this.fish.world.getEntitiesByClass(this.fish.getClass(), this.fish.getBoundingBox().expand(8.0, 8.0, 8.0), predicate);
        SchoolingFishEntity schoolingFishEntity2 = DataFixUtils.orElse(list.stream().filter(SchoolingFishEntity::canHaveMoreFishInGroup).findAny(), this.fish);
        schoolingFishEntity2.pullInOtherFish(list.stream().filter(schoolingFishEntity -> !schoolingFishEntity.hasLeader()));
        return this.fish.hasLeader();
    }

    @Override
    public boolean shouldContinue() {
        return this.fish.hasLeader() && this.fish.isCloseEnoughToLeader();
    }

    @Override
    public void start() {
        this.moveDelay = 0;
    }

    @Override
    public void stop() {
        this.fish.leaveGroup();
    }

    @Override
    public void tick() {
        if (--this.moveDelay > 0) {
            return;
        }
        this.moveDelay = 10;
        this.fish.moveTowardLeader();
    }
}

