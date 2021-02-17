/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SnowGolemEntity
extends GolemEntity
implements Shearable,
RangedAttackMob {
    private static final TrackedData<Byte> SNOW_GOLEM_FLAGS = DataTracker.registerData(SnowGolemEntity.class, TrackedDataHandlerRegistry.BYTE);

    public SnowGolemEntity(EntityType<? extends SnowGolemEntity> entityType, World world) {
        super((EntityType<? extends GolemEntity>)entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.25, 20, 10.0f));
        this.goalSelector.add(2, new WanderAroundFarGoal((PathAwareEntity)this, 1.0, 1.0000001E-5f));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new FollowTargetGoal<MobEntity>(this, MobEntity.class, 10, true, false, livingEntity -> livingEntity instanceof Monster));
    }

    public static DefaultAttributeContainer.Builder createSnowGolemAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SNOW_GOLEM_FLAGS, (byte)16);
    }

    @Override
    public void writeCustomDataToNbt(CompoundTag tag) {
        super.writeCustomDataToNbt(tag);
        tag.putBoolean("Pumpkin", this.hasPumpkin());
    }

    @Override
    public void readCustomDataFromNbt(CompoundTag tag) {
        super.readCustomDataFromNbt(tag);
        if (tag.contains("Pumpkin")) {
            this.setHasPumpkin(tag.getBoolean("Pumpkin"));
        }
    }

    @Override
    public boolean hurtByWater() {
        return true;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient) {
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY());
            int k = MathHelper.floor(this.getZ());
            BlockPos blockPos = new BlockPos(i, 0, k);
            BlockPos blockPos2 = new BlockPos(i, j, k);
            if (this.world.getBiome(blockPos).getTemperature(blockPos2) > 1.0f) {
                this.damage(DamageSource.ON_FIRE, 1.0f);
            }
            if (!this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return;
            }
            BlockState blockState = Blocks.SNOW.getDefaultState();
            for (int l = 0; l < 4; ++l) {
                i = MathHelper.floor(this.getX() + (double)((float)(l % 2 * 2 - 1) * 0.25f));
                BlockPos blockPos3 = new BlockPos(i, j = MathHelper.floor(this.getY()), k = MathHelper.floor(this.getZ() + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25f)));
                if (!this.world.getBlockState(blockPos3).isAir() || !(this.world.getBiome(blockPos3).getTemperature(blockPos3) < 0.8f) || !blockState.canPlaceAt(this.world, blockPos3)) continue;
                this.world.setBlockState(blockPos3, blockState);
            }
        }
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        SnowballEntity snowballEntity = new SnowballEntity(this.world, this);
        double d = target.getEyeY() - (double)1.1f;
        double e = target.getX() - this.getX();
        double f = d - snowballEntity.getY();
        double g = target.getZ() - this.getZ();
        float h = MathHelper.sqrt(e * e + g * g) * 0.2f;
        snowballEntity.setVelocity(e, f + (double)h, g, 1.6f, 12.0f);
        this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0f, 0.4f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.world.spawnEntity(snowballEntity);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 1.7f;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
            this.sheared(SoundCategory.PLAYERS);
            this.emitGameEvent(GameEvent.SHEAR, player);
            if (!this.world.isClient) {
                itemStack.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
            }
            return ActionResult.success(this.world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public void sheared(SoundCategory shearedSoundCategory) {
        this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_SNOW_GOLEM_SHEAR, shearedSoundCategory, 1.0f, 1.0f);
        if (!this.world.isClient()) {
            this.setHasPumpkin(false);
            this.dropStack(new ItemStack(Items.CARVED_PUMPKIN), 1.7f);
        }
    }

    @Override
    public boolean isShearable() {
        return this.isAlive() && this.hasPumpkin();
    }

    public boolean hasPumpkin() {
        return (this.dataTracker.get(SNOW_GOLEM_FLAGS) & 0x10) != 0;
    }

    public void setHasPumpkin(boolean hasPumpkin) {
        byte b = this.dataTracker.get(SNOW_GOLEM_FLAGS);
        if (hasPumpkin) {
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
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SNOW_GOLEM_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SNOW_GOLEM_DEATH;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d method_29919() {
        return new Vec3d(0.0, 0.75f * this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }
}

