/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.raid;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToRaidCenterGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

public abstract class RaiderEntity
extends PatrolEntity {
    protected static final TrackedData<Boolean> CELEBRATING = DataTracker.registerData(RaiderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final Predicate<ItemEntity> OBTAINABLE_OMINOUS_BANNER_PREDICATE = itemEntity -> !itemEntity.cannotPickup() && itemEntity.isAlive() && ItemStack.areEqual(itemEntity.getStack(), Raid.getOminousBanner());
    @Nullable
    protected Raid raid;
    private int wave;
    private boolean ableToJoinRaid;
    private int outOfRaidCounter;

    protected RaiderEntity(EntityType<? extends RaiderEntity> entityType, World world) {
        super((EntityType<? extends PatrolEntity>)entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new PickupBannerAsLeaderGoal(this, this));
        this.goalSelector.add(3, new MoveToRaidCenterGoal<RaiderEntity>(this));
        this.goalSelector.add(4, new AttackHomeGoal(this, 1.05f, 1));
        this.goalSelector.add(5, new CelebrateGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CELEBRATING, false);
    }

    public abstract void addBonusForWave(int var1, boolean var2);

    public boolean canJoinRaid() {
        return this.ableToJoinRaid;
    }

    public void setAbleToJoinRaid(boolean ableToJoinRaid) {
        this.ableToJoinRaid = ableToJoinRaid;
    }

    @Override
    public void tickMovement() {
        if (this.world instanceof ServerWorld && this.isAlive()) {
            Raid raid = this.getRaid();
            if (this.canJoinRaid()) {
                if (raid == null) {
                    Raid raid2;
                    if (this.world.getTime() % 20L == 0L && (raid2 = ((ServerWorld)this.world).getRaidAt(this.getBlockPos())) != null && RaidManager.isValidRaiderFor(this, raid2)) {
                        raid2.addRaider(raid2.getGroupsSpawned(), this, null, true);
                    }
                } else {
                    LivingEntity livingEntity = this.getTarget();
                    if (livingEntity != null && (livingEntity.getType() == EntityType.PLAYER || livingEntity.getType() == EntityType.IRON_GOLEM)) {
                        this.despawnCounter = 0;
                    }
                }
            }
        }
        super.tickMovement();
    }

    @Override
    protected void updateDespawnCounter() {
        this.despawnCounter += 2;
    }

    @Override
    public void onDeath(DamageSource source) {
        if (this.world instanceof ServerWorld) {
            Entity entity = source.getAttacker();
            Raid raid = this.getRaid();
            if (raid != null) {
                if (this.isPatrolLeader()) {
                    raid.removeLeader(this.getWave());
                }
                if (entity != null && entity.getType() == EntityType.PLAYER) {
                    raid.addHero(entity);
                }
                raid.removeFromWave(this, false);
            }
            if (this.isPatrolLeader() && raid == null && ((ServerWorld)this.world).getRaidAt(this.getBlockPos()) == null) {
                ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
                PlayerEntity playerEntity = null;
                Entity entity2 = entity;
                if (entity2 instanceof PlayerEntity) {
                    playerEntity = (PlayerEntity)entity2;
                } else if (entity2 instanceof WolfEntity) {
                    WolfEntity wolfEntity = (WolfEntity)entity2;
                    LivingEntity livingEntity = wolfEntity.getOwner();
                    if (wolfEntity.isTamed() && livingEntity instanceof PlayerEntity) {
                        playerEntity = (PlayerEntity)livingEntity;
                    }
                }
                if (!itemStack.isEmpty() && ItemStack.areEqual(itemStack, Raid.getOminousBanner()) && playerEntity != null) {
                    StatusEffectInstance statusEffectInstance = playerEntity.getStatusEffect(StatusEffects.BAD_OMEN);
                    int i = 1;
                    if (statusEffectInstance != null) {
                        i += statusEffectInstance.getAmplifier();
                        playerEntity.removeStatusEffectInternal(StatusEffects.BAD_OMEN);
                    } else {
                        --i;
                    }
                    i = MathHelper.clamp(i, 0, 4);
                    StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, i, false, false, true);
                    if (!this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
                        playerEntity.addStatusEffect(statusEffectInstance2);
                    }
                }
            }
        }
        super.onDeath(source);
    }

    @Override
    public boolean hasNoRaid() {
        return !this.hasActiveRaid();
    }

    public void setRaid(@Nullable Raid raid) {
        this.raid = raid;
    }

    @Nullable
    public Raid getRaid() {
        return this.raid;
    }

    public boolean hasActiveRaid() {
        return this.getRaid() != null && this.getRaid().isActive();
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public int getWave() {
        return this.wave;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isCelebrating() {
        return this.dataTracker.get(CELEBRATING);
    }

    public void setCelebrating(boolean celebrating) {
        this.dataTracker.set(CELEBRATING, celebrating);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Wave", this.wave);
        tag.putBoolean("CanJoinRaid", this.ableToJoinRaid);
        if (this.raid != null) {
            tag.putInt("RaidId", this.raid.getRaidId());
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.wave = tag.getInt("Wave");
        this.ableToJoinRaid = tag.getBoolean("CanJoinRaid");
        if (tag.contains("RaidId", 3)) {
            if (this.world instanceof ServerWorld) {
                this.raid = ((ServerWorld)this.world).getRaidManager().getRaid(tag.getInt("RaidId"));
            }
            if (this.raid != null) {
                this.raid.addToWave(this.wave, this, false);
                if (this.isPatrolLeader()) {
                    this.raid.setWaveCaptain(this.wave, this);
                }
            }
        }
    }

    @Override
    protected void loot(ItemEntity item) {
        boolean bl;
        ItemStack itemStack = item.getStack();
        boolean bl2 = bl = this.hasActiveRaid() && this.getRaid().getCaptain(this.getWave()) != null;
        if (this.hasActiveRaid() && !bl && ItemStack.areEqual(itemStack, Raid.getOminousBanner())) {
            EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;
            ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
            double d = this.getDropChance(equipmentSlot);
            if (!itemStack2.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1f, 0.0f) < d) {
                this.dropStack(itemStack2);
            }
            this.method_27964(item);
            this.equipStack(equipmentSlot, itemStack);
            this.sendPickup(item, itemStack.getCount());
            item.remove();
            this.getRaid().setWaveCaptain(this.getWave(), this);
            this.setPatrolLeader(true);
        } else {
            super.loot(item);
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        if (this.getRaid() == null) {
            return super.canImmediatelyDespawn(distanceSquared);
        }
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return super.cannotDespawn() || this.getRaid() != null;
    }

    public int getOutOfRaidCounter() {
        return this.outOfRaidCounter;
    }

    public void setOutOfRaidCounter(int counter) {
        this.outOfRaidCounter = counter;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.hasActiveRaid()) {
            this.getRaid().updateBar();
        }
        return super.damage(source, amount);
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        this.setAbleToJoinRaid(this.getType() != EntityType.WITCH || spawnReason != SpawnReason.NATURAL);
        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }

    public abstract SoundEvent getCelebratingSound();

    static class AttackHomeGoal
    extends Goal {
        private final RaiderEntity raider;
        private final double speed;
        private BlockPos home;
        private final List<BlockPos> lastHomes = Lists.newArrayList();
        private final int distance;
        private boolean finished;

        public AttackHomeGoal(RaiderEntity raider, double speed, int distance) {
            this.raider = raider;
            this.speed = speed;
            this.distance = distance;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            this.purgeMemory();
            return this.isRaiding() && this.tryFindHome() && this.raider.getTarget() == null;
        }

        private boolean isRaiding() {
            return this.raider.hasActiveRaid() && !this.raider.getRaid().isFinished();
        }

        private boolean tryFindHome() {
            ServerWorld serverWorld = (ServerWorld)this.raider.world;
            BlockPos blockPos = this.raider.getBlockPos();
            Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage().getPosition(pointOfInterestType -> pointOfInterestType == PointOfInterestType.HOME, this::canLootHome, PointOfInterestStorage.OccupationStatus.ANY, blockPos, 48, this.raider.random);
            if (!optional.isPresent()) {
                return false;
            }
            this.home = optional.get().toImmutable();
            return true;
        }

        @Override
        public boolean shouldContinue() {
            if (this.raider.getNavigation().isIdle()) {
                return false;
            }
            return this.raider.getTarget() == null && !this.home.isWithinDistance(this.raider.getPos(), (double)(this.raider.getWidth() + (float)this.distance)) && !this.finished;
        }

        @Override
        public void stop() {
            if (this.home.isWithinDistance(this.raider.getPos(), (double)this.distance)) {
                this.lastHomes.add(this.home);
            }
        }

        @Override
        public void start() {
            super.start();
            this.raider.setDespawnCounter(0);
            this.raider.getNavigation().startMovingTo(this.home.getX(), this.home.getY(), this.home.getZ(), this.speed);
            this.finished = false;
        }

        @Override
        public void tick() {
            if (this.raider.getNavigation().isIdle()) {
                Vec3d vec3d = Vec3d.ofBottomCenter(this.home);
                Vec3d vec3d2 = TargetFinder.findTargetTowards(this.raider, 16, 7, vec3d, 0.3141592741012573);
                if (vec3d2 == null) {
                    vec3d2 = TargetFinder.findTargetTowards(this.raider, 8, 7, vec3d);
                }
                if (vec3d2 == null) {
                    this.finished = true;
                    return;
                }
                this.raider.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
            }
        }

        private boolean canLootHome(BlockPos pos) {
            for (BlockPos blockPos : this.lastHomes) {
                if (!Objects.equals(pos, blockPos)) continue;
                return false;
            }
            return true;
        }

        private void purgeMemory() {
            if (this.lastHomes.size() > 2) {
                this.lastHomes.remove(0);
            }
        }
    }

    public class PatrolApproachGoal
    extends Goal {
        private final RaiderEntity raider;
        private final float squaredDistance;
        public final TargetPredicate closeRaiderPredicate = new TargetPredicate().setBaseMaxDistance(8.0).ignoreEntityTargetRules().includeInvulnerable().includeTeammates().includeHidden().ignoreDistanceScalingFactor();

        public PatrolApproachGoal(IllagerEntity illager, float distance) {
            this.raider = illager;
            this.squaredDistance = distance * distance;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.raider.getAttacker();
            return this.raider.getRaid() == null && this.raider.isRaidCenterSet() && this.raider.getTarget() != null && !this.raider.isAttacking() && (livingEntity == null || livingEntity.getType() != EntityType.PLAYER);
        }

        @Override
        public void start() {
            super.start();
            this.raider.getNavigation().stop();
            List<RaiderEntity> list = this.raider.world.getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0));
            for (RaiderEntity raiderEntity : list) {
                raiderEntity.setTarget(this.raider.getTarget());
            }
        }

        @Override
        public void stop() {
            super.stop();
            LivingEntity livingEntity = this.raider.getTarget();
            if (livingEntity != null) {
                List<RaiderEntity> list = this.raider.world.getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0));
                for (RaiderEntity raiderEntity : list) {
                    raiderEntity.setTarget(livingEntity);
                    raiderEntity.setAttacking(true);
                }
                this.raider.setAttacking(true);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.raider.getTarget();
            if (livingEntity == null) {
                return;
            }
            if (this.raider.squaredDistanceTo(livingEntity) > (double)this.squaredDistance) {
                this.raider.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
                if (this.raider.random.nextInt(50) == 0) {
                    this.raider.playAmbientSound();
                }
            } else {
                this.raider.setAttacking(true);
            }
            super.tick();
        }
    }

    public class CelebrateGoal
    extends Goal {
        private final RaiderEntity raider;

        CelebrateGoal(RaiderEntity raider) {
            this.raider = raider;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            Raid raid = this.raider.getRaid();
            return this.raider.isAlive() && this.raider.getTarget() == null && raid != null && raid.hasLost();
        }

        @Override
        public void start() {
            this.raider.setCelebrating(true);
            super.start();
        }

        @Override
        public void stop() {
            this.raider.setCelebrating(false);
            super.stop();
        }

        @Override
        public void tick() {
            if (!this.raider.isSilent() && this.raider.random.nextInt(100) == 0) {
                RaiderEntity.this.playSound(RaiderEntity.this.getCelebratingSound(), RaiderEntity.this.getSoundVolume(), RaiderEntity.this.getSoundPitch());
            }
            if (!this.raider.hasVehicle() && this.raider.random.nextInt(50) == 0) {
                this.raider.getJumpControl().setActive();
            }
            super.tick();
        }
    }

    public static class PickupBannerAsLeaderGoal<T extends RaiderEntity>
    extends Goal {
        private final T actor;
        final /* synthetic */ RaiderEntity field_16604;

        public PickupBannerAsLeaderGoal(T actor) {
            this.field_16604 = raiderEntity;
            this.actor = actor;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            List<ItemEntity> list;
            Raid raid = ((RaiderEntity)this.actor).getRaid();
            if (!((RaiderEntity)this.actor).hasActiveRaid() || ((RaiderEntity)this.actor).getRaid().isFinished() || !((PatrolEntity)this.actor).canLead() || ItemStack.areEqual(((MobEntity)this.actor).getEquippedStack(EquipmentSlot.HEAD), Raid.getOminousBanner())) {
                return false;
            }
            RaiderEntity raiderEntity = raid.getCaptain(((RaiderEntity)this.actor).getWave());
            if (!(raiderEntity != null && raiderEntity.isAlive() || (list = ((RaiderEntity)this.actor).world.getEntities(ItemEntity.class, ((Entity)this.actor).getBoundingBox().expand(16.0, 8.0, 16.0), OBTAINABLE_OMINOUS_BANNER_PREDICATE)).isEmpty())) {
                return ((MobEntity)this.actor).getNavigation().startMovingTo(list.get(0), 1.15f);
            }
            return false;
        }

        @Override
        public void tick() {
            List<ItemEntity> list;
            if (((MobEntity)this.actor).getNavigation().getTargetPos().isWithinDistance(((Entity)this.actor).getPos(), 1.414) && !(list = ((RaiderEntity)this.actor).world.getEntities(ItemEntity.class, ((Entity)this.actor).getBoundingBox().expand(4.0, 4.0, 4.0), OBTAINABLE_OMINOUS_BANNER_PREDICATE)).isEmpty()) {
                ((RaiderEntity)this.actor).loot(list.get(0));
            }
        }
    }
}

