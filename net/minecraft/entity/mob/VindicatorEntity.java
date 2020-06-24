/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class VindicatorEntity
extends IllagerEntity {
    private static final Predicate<Difficulty> DIFFICULTY_ALLOWS_DOOR_BREAKING_PREDICATE = difficulty -> difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD;
    private boolean johnny;

    public VindicatorEntity(EntityType<? extends VindicatorEntity> entityType, World world) {
        super((EntityType<? extends IllagerEntity>)entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new BreakDoorGoal(this));
        this.goalSelector.add(2, new IllagerEntity.LongDoorInteractGoal(this, this));
        this.goalSelector.add(3, new RaiderEntity.PatrolApproachGoal(this, 10.0f));
        this.goalSelector.add(4, new AttackGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<AbstractTraderEntity>((MobEntity)this, AbstractTraderEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, true));
        this.targetSelector.add(4, new FollowEntityGoal(this));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
    }

    @Override
    protected void mobTick() {
        EntityNavigation entityNavigation;
        if (!this.isAiDisabled() && (entityNavigation = this.getNavigation()) instanceof MobNavigation) {
            boolean bl = ((ServerWorld)this.world).hasRaidAt(this.getBlockPos());
            ((MobNavigation)entityNavigation).setCanPathThroughDoors(bl);
        }
        super.mobTick();
    }

    public static DefaultAttributeContainer.Builder createVindicatorAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.johnny) {
            tag.putBoolean("Johnny", true);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public IllagerEntity.State getState() {
        if (this.isAttacking()) {
            return IllagerEntity.State.ATTACKING;
        }
        if (this.isCelebrating()) {
            return IllagerEntity.State.CELEBRATING;
        }
        return IllagerEntity.State.CROSSED;
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("Johnny", 99)) {
            this.johnny = tag.getBoolean("Johnny");
        }
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_VINDICATOR_CELEBRATE;
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        EntityData entityData2 = super.initialize(world, difficulty, spawnReason, entityData, entityTag);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.initEquipment(difficulty);
        this.updateEnchantments(difficulty);
        return entityData2;
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        if (this.getRaid() == null) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        }
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (super.isTeammate(other)) {
            return true;
        }
        if (other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        }
        return false;
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        if (!this.johnny && name != null && name.getString().equals("Johnny")) {
            this.johnny = true;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VINDICATOR_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VINDICATOR_HURT;
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {
        boolean bl;
        ItemStack itemStack = new ItemStack(Items.IRON_AXE);
        Raid raid = this.getRaid();
        int i = 1;
        if (wave > raid.getMaxWaves(Difficulty.NORMAL)) {
            i = 2;
        }
        boolean bl2 = bl = this.random.nextFloat() <= raid.getEnchantmentChance();
        if (bl) {
            HashMap<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.SHARPNESS, i);
            EnchantmentHelper.set(map, itemStack);
        }
        this.equipStack(EquipmentSlot.MAINHAND, itemStack);
    }

    static class FollowEntityGoal
    extends FollowTargetGoal<LivingEntity> {
        public FollowEntityGoal(VindicatorEntity vindicator) {
            super(vindicator, LivingEntity.class, 0, true, true, LivingEntity::isMobOrPlayer);
        }

        @Override
        public boolean canStart() {
            return ((VindicatorEntity)this.mob).johnny && super.canStart();
        }

        @Override
        public void start() {
            super.start();
            this.mob.setDespawnCounter(0);
        }
    }

    static class BreakDoorGoal
    extends net.minecraft.entity.ai.goal.BreakDoorGoal {
        public BreakDoorGoal(MobEntity mobEntity) {
            super(mobEntity, 6, DIFFICULTY_ALLOWS_DOOR_BREAKING_PREDICATE);
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean shouldContinue() {
            VindicatorEntity vindicatorEntity = (VindicatorEntity)this.mob;
            return vindicatorEntity.hasActiveRaid() && super.shouldContinue();
        }

        @Override
        public boolean canStart() {
            VindicatorEntity vindicatorEntity = (VindicatorEntity)this.mob;
            return vindicatorEntity.hasActiveRaid() && vindicatorEntity.random.nextInt(10) == 0 && super.canStart();
        }

        @Override
        public void start() {
            super.start();
            this.mob.setDespawnCounter(0);
        }
    }

    class AttackGoal
    extends MeleeAttackGoal {
        public AttackGoal(VindicatorEntity vindicator) {
            super(vindicator, 1.0, false);
        }

        @Override
        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            if (this.mob.getVehicle() instanceof RavagerEntity) {
                float f = this.mob.getVehicle().getWidth() - 0.1f;
                return f * 2.0f * (f * 2.0f) + entity.getWidth();
            }
            return super.getSquaredMaxAttackDistance(entity);
        }
    }
}

