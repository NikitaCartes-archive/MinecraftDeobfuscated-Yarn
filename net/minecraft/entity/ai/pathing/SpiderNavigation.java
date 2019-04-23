/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpiderNavigation
extends MobNavigation {
    private BlockPos field_6687;

    public SpiderNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    public Path findPathTo(BlockPos blockPos) {
        this.field_6687 = blockPos;
        return super.findPathTo(blockPos);
    }

    @Override
    public Path findPathTo(Entity entity) {
        this.field_6687 = new BlockPos(entity);
        return super.findPathTo(entity);
    }

    @Override
    public boolean startMovingTo(Entity entity, double d) {
        Path path = this.findPathTo(entity);
        if (path != null) {
            return this.startMovingAlong(path, d);
        }
        this.field_6687 = new BlockPos(entity);
        this.speed = d;
        return true;
    }

    @Override
    public void tick() {
        if (this.isIdle()) {
            if (this.field_6687 != null) {
                if (this.field_6687.isWithinDistance(this.entity.getPos(), (double)this.entity.getWidth()) || this.entity.y > (double)this.field_6687.getY() && new BlockPos(this.field_6687.getX(), MathHelper.floor(this.entity.y), this.field_6687.getZ()).isWithinDistance(this.entity.getPos(), (double)this.entity.getWidth())) {
                    this.field_6687 = null;
                } else {
                    this.entity.getMoveControl().moveTo(this.field_6687.getX(), this.field_6687.getY(), this.field_6687.getZ(), this.speed);
                }
            }
            return;
        }
        super.tick();
    }
}

