/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class WaterCreatureEntity
extends MobEntityWithAi {
    protected WaterCreatureEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super((EntityType<? extends MobEntityWithAi>)entityType, world);
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }

    @Override
    protected boolean canSpawnAt(IWorld iWorld, SpawnType spawnType, BlockPos blockPos) {
        return iWorld.getFluidState(blockPos).matches(FluidTags.WATER);
    }

    @Override
    public boolean canSpawn(ViewableWorld viewableWorld) {
        return viewableWorld.intersectsEntities(this);
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 120;
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return true;
    }

    @Override
    protected int getCurrentExperience(PlayerEntity playerEntity) {
        return 1 + this.world.random.nextInt(3);
    }

    protected void tickBreath(int i) {
        if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
            this.setBreath(i - 1);
            if (this.getBreath() == -20) {
                this.setBreath(0);
                this.damage(DamageSource.DROWN, 2.0f);
            }
        } else {
            this.setBreath(300);
        }
    }

    @Override
    public void baseTick() {
        int i = this.getBreath();
        super.baseTick();
        this.tickBreath(i);
    }

    @Override
    public boolean canFly() {
        return false;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity playerEntity) {
        return false;
    }
}

