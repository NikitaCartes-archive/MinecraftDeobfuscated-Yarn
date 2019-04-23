/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SnowGolemEntity
extends GolemEntity
implements RangedAttackMob {
    private static final TrackedData<Byte> SNOW_GOLEM_FLAGS = DataTracker.registerData(SnowGolemEntity.class, TrackedDataHandlerRegistry.BYTE);

    public SnowGolemEntity(EntityType<? extends SnowGolemEntity> entityType, World world) {
        super((EntityType<? extends GolemEntity>)entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.25, 20, 10.0f));
        this.goalSelector.add(2, new WanderAroundFarGoal((MobEntityWithAi)this, 1.0, 1.0000001E-5f));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new FollowTargetGoal<MobEntity>(this, MobEntity.class, 10, true, false, livingEntity -> livingEntity instanceof Monster));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(4.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SNOW_GOLEM_FLAGS, (byte)16);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putBoolean("Pumpkin", this.hasPumpkin());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        if (compoundTag.containsKey("Pumpkin")) {
            this.setHasPumpkin(compoundTag.getBoolean("Pumpkin"));
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient) {
            int i = MathHelper.floor(this.x);
            int j = MathHelper.floor(this.y);
            int k = MathHelper.floor(this.z);
            if (this.isTouchingWater()) {
                this.damage(DamageSource.DROWN, 1.0f);
            }
            BlockPos blockPos = new BlockPos(i, 0, k);
            BlockPos blockPos2 = new BlockPos(i, j, k);
            if (this.world.getBiome(blockPos).getTemperature(blockPos2) > 1.0f) {
                this.damage(DamageSource.ON_FIRE, 1.0f);
            }
            if (!this.world.getGameRules().getBoolean("mobGriefing")) {
                return;
            }
            BlockState blockState = Blocks.SNOW.getDefaultState();
            for (int l = 0; l < 4; ++l) {
                i = MathHelper.floor(this.x + (double)((float)(l % 2 * 2 - 1) * 0.25f));
                BlockPos blockPos3 = new BlockPos(i, j = MathHelper.floor(this.y), k = MathHelper.floor(this.z + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25f)));
                if (!this.world.getBlockState(blockPos3).isAir() || !(this.world.getBiome(blockPos3).getTemperature(blockPos3) < 0.8f) || !blockState.canPlaceAt(this.world, blockPos3)) continue;
                this.world.setBlockState(blockPos3, blockState);
            }
        }
    }

    @Override
    public void attack(LivingEntity livingEntity, float f) {
        SnowballEntity snowballEntity = new SnowballEntity(this.world, this);
        double d = livingEntity.y + (double)livingEntity.getStandingEyeHeight() - (double)1.1f;
        double e = livingEntity.x - this.x;
        double g = d - snowballEntity.y;
        double h = livingEntity.z - this.z;
        float i = MathHelper.sqrt(e * e + h * h) * 0.2f;
        snowballEntity.setVelocity(e, g + (double)i, h, 1.6f, 12.0f);
        this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0f, 1.0f / (this.getRand().nextFloat() * 0.4f + 0.8f));
        this.world.spawnEntity(snowballEntity);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose entityPose, EntitySize entitySize) {
        return 1.7f;
    }

    @Override
    protected boolean interactMob(PlayerEntity playerEntity2, Hand hand) {
        ItemStack itemStack = playerEntity2.getStackInHand(hand);
        if (itemStack.getItem() == Items.SHEARS && this.hasPumpkin() && !this.world.isClient) {
            this.setHasPumpkin(false);
            itemStack.applyDamage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(hand));
        }
        return super.interactMob(playerEntity2, hand);
    }

    public boolean hasPumpkin() {
        return (this.dataTracker.get(SNOW_GOLEM_FLAGS) & 0x10) != 0;
    }

    public void setHasPumpkin(boolean bl) {
        byte b = this.dataTracker.get(SNOW_GOLEM_FLAGS);
        if (bl) {
            this.dataTracker.set(SNOW_GOLEM_FLAGS, (byte)(b | 0x10));
        } else {
            this.dataTracker.set(SNOW_GOLEM_FLAGS, (byte)(b & 0xFFFFFFEF));
        }
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SNOW_GOLEM_AMBIENT;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_SNOW_GOLEM_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SNOW_GOLEM_DEATH;
    }
}

