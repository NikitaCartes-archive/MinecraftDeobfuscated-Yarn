/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.math.Vec3d;

public class FormCaravanGoal
extends Goal {
    public final LlamaEntity llama;
    private double speed;
    private int counter;

    public FormCaravanGoal(LlamaEntity llama, double speed) {
        this.llama = llama;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        double e;
        LlamaEntity llamaEntity2;
        if (this.llama.isLeashed() || this.llama.isFollowing()) {
            return false;
        }
        List<Entity> list = this.llama.world.getOtherEntities(this.llama, this.llama.getBoundingBox().expand(9.0, 4.0, 9.0), entity -> {
            EntityType<?> entityType = entity.getType();
            return entityType == EntityType.LLAMA || entityType == EntityType.TRADER_LLAMA;
        });
        MobEntity llamaEntity = null;
        double d = Double.MAX_VALUE;
        for (Entity entity2 : list) {
            llamaEntity2 = (LlamaEntity)entity2;
            if (!llamaEntity2.isFollowing() || llamaEntity2.hasFollower() || (e = this.llama.squaredDistanceTo(llamaEntity2)) > d) continue;
            d = e;
            llamaEntity = llamaEntity2;
        }
        if (llamaEntity == null) {
            for (Entity entity2 : list) {
                llamaEntity2 = (LlamaEntity)entity2;
                if (!llamaEntity2.isLeashed() || llamaEntity2.hasFollower() || (e = this.llama.squaredDistanceTo(llamaEntity2)) > d) continue;
                d = e;
                llamaEntity = llamaEntity2;
            }
        }
        if (llamaEntity == null) {
            return false;
        }
        if (d < 4.0) {
            return false;
        }
        if (!llamaEntity.isLeashed() && !this.canFollow((LlamaEntity)llamaEntity, 1)) {
            return false;
        }
        this.llama.follow((LlamaEntity)llamaEntity);
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (!(this.llama.isFollowing() && this.llama.getFollowing().isAlive() && this.canFollow(this.llama, 0))) {
            return false;
        }
        double d = this.llama.squaredDistanceTo(this.llama.getFollowing());
        if (d > 676.0) {
            if (this.speed <= 3.0) {
                this.speed *= 1.2;
                this.counter = 40;
                return true;
            }
            if (this.counter == 0) {
                return false;
            }
        }
        if (this.counter > 0) {
            --this.counter;
        }
        return true;
    }

    @Override
    public void stop() {
        this.llama.stopFollowing();
        this.speed = 2.1;
    }

    @Override
    public void tick() {
        if (!this.llama.isFollowing()) {
            return;
        }
        if (this.llama.getHoldingEntity() instanceof LeashKnotEntity) {
            return;
        }
        LlamaEntity llamaEntity = this.llama.getFollowing();
        double d = this.llama.distanceTo(llamaEntity);
        float f = 2.0f;
        Vec3d vec3d = new Vec3d(llamaEntity.getX() - this.llama.getX(), llamaEntity.getY() - this.llama.getY(), llamaEntity.getZ() - this.llama.getZ()).normalize().multiply(Math.max(d - 2.0, 0.0));
        this.llama.getNavigation().startMovingTo(this.llama.getX() + vec3d.x, this.llama.getY() + vec3d.y, this.llama.getZ() + vec3d.z, this.speed);
    }

    private boolean canFollow(LlamaEntity llama, int length) {
        if (length > 8) {
            return false;
        }
        if (llama.isFollowing()) {
            if (llama.getFollowing().isLeashed()) {
                return true;
            }
            return this.canFollow(llama.getFollowing(), ++length);
        }
        return false;
    }
}

