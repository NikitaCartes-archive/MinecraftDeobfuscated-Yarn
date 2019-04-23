/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TraderLlamaEntity
extends LlamaEntity {
    private int despawnDelay;

    public TraderLlamaEntity(EntityType<? extends TraderLlamaEntity> entityType, World world) {
        super((EntityType<? extends LlamaEntity>)entityType, world);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isTrader() {
        return true;
    }

    @Override
    protected LlamaEntity createChild() {
        return EntityType.TRADER_LLAMA.create(this.world);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putInt("DespawnDelay", this.despawnDelay);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        if (compoundTag.containsKey("DespawnDelay", 99)) {
            this.despawnDelay = compoundTag.getInt("DespawnDelay");
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
        this.targetSelector.add(1, new DefendTraderGoal(this));
    }

    public void setDespawnDelay(int i) {
        this.despawnDelay = i;
    }

    @Override
    protected void putPlayerOnBack(PlayerEntity playerEntity) {
        Entity entity = this.getHoldingEntity();
        if (entity instanceof WanderingTraderEntity) {
            return;
        }
        super.putPlayerOnBack(playerEntity);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.despawnDelay > 0 && --this.despawnDelay == 0 && this.getHoldingEntity() instanceof WanderingTraderEntity) {
            WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity)this.getHoldingEntity();
            int i = wanderingTraderEntity.getDespawnDelay();
            if (i - 1 > 0) {
                this.despawnDelay = i - 1;
            } else {
                this.remove();
            }
        }
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag) {
        EntityData entityData2 = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
        if (spawnType == SpawnType.EVENT) {
            this.setBreedingAge(0);
        }
        return entityData2;
    }

    public class DefendTraderGoal
    extends TrackTargetGoal {
        private final LlamaEntity llama;
        private LivingEntity offender;
        private int traderLastAttackedTime;

        public DefendTraderGoal(LlamaEntity llamaEntity) {
            super(llamaEntity, false);
            this.llama = llamaEntity;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }

        @Override
        public boolean canStart() {
            if (!this.llama.isLeashed()) {
                return false;
            }
            Entity entity = this.llama.getHoldingEntity();
            if (!(entity instanceof WanderingTraderEntity)) {
                return false;
            }
            WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity)entity;
            this.offender = wanderingTraderEntity.getAttacker();
            int i = wanderingTraderEntity.getLastAttackedTime();
            return i != this.traderLastAttackedTime && this.canTrack(this.offender, TargetPredicate.DEFAULT);
        }

        @Override
        public void start() {
            this.mob.setTarget(this.offender);
            Entity entity = this.llama.getHoldingEntity();
            if (entity instanceof WanderingTraderEntity) {
                this.traderLastAttackedTime = ((WanderingTraderEntity)entity).getLastAttackedTime();
            }
            super.start();
        }
    }
}

