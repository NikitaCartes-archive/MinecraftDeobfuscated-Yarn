/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.entity.ai.goal.ChaseBoatGoal;
import net.minecraft.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveIntoWaterGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class DolphinEntity
extends WaterCreatureEntity {
    private static final TrackedData<BlockPos> TREASURE_POS = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Boolean> HAS_FISH = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> MOISTNESS = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.INTEGER);
    static final TargetPredicate CLOSE_PLAYER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(10.0).ignoreVisibility();
    public static final int MAX_AIR = 4800;
    private static final int MAX_MOISTNESS = 2400;
    public static final Predicate<ItemEntity> CAN_TAKE = item -> !item.cannotPickup() && item.isAlive() && item.isTouchingWater();

    public DolphinEntity(EntityType<? extends DolphinEntity> entityType, World world) {
        super((EntityType<? extends WaterCreatureEntity>)entityType, world);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02f, 0.1f, true);
        this.lookControl = new YawAdjustingLookControl(this, 10);
        this.setCanPickUpLoot(true);
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setAir(this.getMaxAir());
        this.setPitch(0.0f);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public boolean canBreatheInWater() {
        return false;
    }

    @Override
    protected void tickWaterBreathingAir(int air) {
    }

    public void setTreasurePos(BlockPos treasurePos) {
        this.dataTracker.set(TREASURE_POS, treasurePos);
    }

    public BlockPos getTreasurePos() {
        return this.dataTracker.get(TREASURE_POS);
    }

    public boolean hasFish() {
        return this.dataTracker.get(HAS_FISH);
    }

    public void setHasFish(boolean hasFish) {
        this.dataTracker.set(HAS_FISH, hasFish);
    }

    public int getMoistness() {
        return this.dataTracker.get(MOISTNESS);
    }

    public void setMoistness(int moistness) {
        this.dataTracker.set(MOISTNESS, moistness);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TREASURE_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(HAS_FISH, false);
        this.dataTracker.startTracking(MOISTNESS, 2400);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("TreasurePosX", this.getTreasurePos().getX());
        nbt.putInt("TreasurePosY", this.getTreasurePos().getY());
        nbt.putInt("TreasurePosZ", this.getTreasurePos().getZ());
        nbt.putBoolean("GotFish", this.hasFish());
        nbt.putInt("Moistness", this.getMoistness());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        int i = nbt.getInt("TreasurePosX");
        int j = nbt.getInt("TreasurePosY");
        int k = nbt.getInt("TreasurePosZ");
        this.setTreasurePos(new BlockPos(i, j, k));
        super.readCustomDataFromNbt(nbt);
        this.setHasFish(nbt.getBoolean("GotFish"));
        this.setMoistness(nbt.getInt("Moistness"));
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BreatheAirGoal(this));
        this.goalSelector.add(0, new MoveIntoWaterGoal(this));
        this.goalSelector.add(1, new LeadToNearbyTreasureGoal(this));
        this.goalSelector.add(2, new SwimWithPlayerGoal(this, 4.0));
        this.goalSelector.add(4, new SwimAroundGoal(this, 1.0, 10));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(5, new DolphinJumpGoal(this, 10));
        this.goalSelector.add(6, new MeleeAttackGoal(this, 1.2f, true));
        this.goalSelector.add(8, new PlayWithItemsGoal());
        this.goalSelector.add(8, new ChaseBoatGoal(this));
        this.goalSelector.add(9, new FleeEntityGoal<GuardianEntity>(this, GuardianEntity.class, 8.0f, 1.0, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this, GuardianEntity.class).setGroupRevenge(new Class[0]));
    }

    public static DefaultAttributeContainer.Builder createDolphinAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.2f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = target.damage(DamageSource.mob(this), (int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        if (bl) {
            this.applyDamageEffects(this, target);
            this.playSound(SoundEvents.ENTITY_DOLPHIN_ATTACK, 1.0f, 1.0f);
        }
        return bl;
    }

    @Override
    public int getMaxAir() {
        return 4800;
    }

    @Override
    protected int getNextAirOnLand(int air) {
        return this.getMaxAir();
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.3f;
    }

    @Override
    public int getMaxLookPitchChange() {
        return 1;
    }

    @Override
    public int getMaxHeadRotation() {
        return 1;
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return true;
    }

    @Override
    public boolean canEquip(ItemStack stack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
        if (!this.getEquippedStack(equipmentSlot).isEmpty()) {
            return false;
        }
        return equipmentSlot == EquipmentSlot.MAINHAND && super.canEquip(stack);
    }

    @Override
    protected void loot(ItemEntity item) {
        ItemStack itemStack;
        if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && this.canPickupItem(itemStack = item.getStack())) {
            this.triggerItemPickedUpByEntityCriteria(item);
            this.equipStack(EquipmentSlot.MAINHAND, itemStack);
            this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0f;
            this.sendPickup(item, itemStack.getCount());
            item.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAiDisabled()) {
            this.setAir(this.getMaxAir());
            return;
        }
        if (this.isWet()) {
            this.setMoistness(2400);
        } else {
            this.setMoistness(this.getMoistness() - 1);
            if (this.getMoistness() <= 0) {
                this.damage(DamageSource.DRYOUT, 1.0f);
            }
            if (this.onGround) {
                this.setVelocity(this.getVelocity().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.2f, 0.5, (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f));
                this.setYaw(this.random.nextFloat() * 360.0f);
                this.onGround = false;
                this.velocityDirty = true;
            }
        }
        if (this.world.isClient && this.isTouchingWater() && this.getVelocity().lengthSquared() > 0.03) {
            Vec3d vec3d = this.getRotationVec(0.0f);
            float f = MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)) * 0.3f;
            float g = MathHelper.sin(this.getYaw() * ((float)Math.PI / 180)) * 0.3f;
            float h = 1.2f - this.random.nextFloat() * 0.7f;
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3d.x * (double)h + (double)f, this.getY() - vec3d.y, this.getZ() - vec3d.z * (double)h + (double)g, 0.0, 0.0, 0.0);
                this.world.addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3d.x * (double)h - (double)f, this.getY() - vec3d.y, this.getZ() - vec3d.z * (double)h - (double)g, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 38) {
            this.spawnParticlesAround(ParticleTypes.HAPPY_VILLAGER);
        } else {
            super.handleStatus(status);
        }
    }

    private void spawnParticlesAround(ParticleEffect parameters) {
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.01;
            double e = this.random.nextGaussian() * 0.01;
            double f = this.random.nextGaussian() * 0.01;
            this.world.addParticle(parameters, this.getParticleX(1.0), this.getRandomBodyY() + 0.2, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isEmpty() && itemStack.isIn(ItemTags.FISHES)) {
            if (!this.world.isClient) {
                this.playSound(SoundEvents.ENTITY_DOLPHIN_EAT, 1.0f, 1.0f);
            }
            this.setHasFish(true);
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(player, hand);
    }

    public static boolean canSpawn(EntityType<DolphinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (pos.getY() <= 45 || pos.getY() >= world.getSeaLevel()) {
            return false;
        }
        Optional<RegistryKey<Biome>> optional = world.getBiomeKey(pos);
        return (!Objects.equals(optional, Optional.of(BiomeKeys.OCEAN)) || !Objects.equals(optional, Optional.of(BiomeKeys.DEEP_OCEAN))) && world.getFluidState(pos).isIn(FluidTags.WATER);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_DOLPHIN_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_DOLPHIN_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.isTouchingWater() ? SoundEvents.ENTITY_DOLPHIN_AMBIENT_WATER : SoundEvents.ENTITY_DOLPHIN_AMBIENT;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_DOLPHIN_SPLASH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_DOLPHIN_SWIM;
    }

    protected boolean isNearTarget() {
        BlockPos blockPos = this.getNavigation().getTargetPos();
        if (blockPos != null) {
            return blockPos.isWithinDistance(this.getPos(), 12.0);
        }
        return false;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(this.getMovementSpeed(), movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return true;
    }

    static class LeadToNearbyTreasureGoal
    extends Goal {
        private final DolphinEntity dolphin;
        private boolean noPathToStructure;

        LeadToNearbyTreasureGoal(DolphinEntity dolphin) {
            this.dolphin = dolphin;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStop() {
            return false;
        }

        @Override
        public boolean canStart() {
            return this.dolphin.hasFish() && this.dolphin.getAir() >= 100;
        }

        @Override
        public boolean shouldContinue() {
            BlockPos blockPos = this.dolphin.getTreasurePos();
            return !new BlockPos((double)blockPos.getX(), this.dolphin.getY(), (double)blockPos.getZ()).isWithinDistance(this.dolphin.getPos(), 4.0) && !this.noPathToStructure && this.dolphin.getAir() >= 100;
        }

        /*
         * Enabled aggressive block sorting
         */
        @Override
        public void start() {
            if (!(this.dolphin.world instanceof ServerWorld)) {
                return;
            }
            ServerWorld serverWorld = (ServerWorld)this.dolphin.world;
            this.noPathToStructure = false;
            this.dolphin.getNavigation().stop();
            BlockPos blockPos = this.dolphin.getBlockPos();
            StructureFeature<FeatureConfig> structureFeature = (double)serverWorld.random.nextFloat() >= 0.5 ? StructureFeature.OCEAN_RUIN : StructureFeature.SHIPWRECK;
            BlockPos blockPos2 = serverWorld.locateStructure(structureFeature, blockPos, 50, false);
            if (blockPos2 == null) {
                StructureFeature<FeatureConfig> structureFeature2 = structureFeature.equals(StructureFeature.OCEAN_RUIN) ? StructureFeature.SHIPWRECK : StructureFeature.OCEAN_RUIN;
                BlockPos blockPos3 = serverWorld.locateStructure(structureFeature2, blockPos, 50, false);
                if (blockPos3 == null) {
                    this.noPathToStructure = true;
                    return;
                }
                this.dolphin.setTreasurePos(blockPos3);
            } else {
                this.dolphin.setTreasurePos(blockPos2);
            }
            serverWorld.sendEntityStatus(this.dolphin, (byte)38);
        }

        @Override
        public void stop() {
            BlockPos blockPos = this.dolphin.getTreasurePos();
            if (new BlockPos((double)blockPos.getX(), this.dolphin.getY(), (double)blockPos.getZ()).isWithinDistance(this.dolphin.getPos(), 4.0) || this.noPathToStructure) {
                this.dolphin.setHasFish(false);
            }
        }

        @Override
        public void tick() {
            World world = this.dolphin.world;
            if (this.dolphin.isNearTarget() || this.dolphin.getNavigation().isIdle()) {
                BlockPos blockPos;
                Vec3d vec3d = Vec3d.ofCenter(this.dolphin.getTreasurePos());
                Vec3d vec3d2 = NoPenaltyTargeting.find(this.dolphin, 16, 1, vec3d, 0.3926991f);
                if (vec3d2 == null) {
                    vec3d2 = NoPenaltyTargeting.find(this.dolphin, 8, 4, vec3d, 1.5707963705062866);
                }
                if (!(vec3d2 == null || world.getFluidState(blockPos = new BlockPos(vec3d2)).isIn(FluidTags.WATER) && world.getBlockState(blockPos).canPathfindThrough(world, blockPos, NavigationType.WATER))) {
                    vec3d2 = NoPenaltyTargeting.find(this.dolphin, 8, 5, vec3d, 1.5707963705062866);
                }
                if (vec3d2 == null) {
                    this.noPathToStructure = true;
                    return;
                }
                this.dolphin.getLookControl().lookAt(vec3d2.x, vec3d2.y, vec3d2.z, this.dolphin.getMaxHeadRotation() + 20, this.dolphin.getMaxLookPitchChange());
                this.dolphin.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, 1.3);
                if (world.random.nextInt(this.getTickCount(80)) == 0) {
                    world.sendEntityStatus(this.dolphin, (byte)38);
                }
            }
        }
    }

    static class SwimWithPlayerGoal
    extends Goal {
        private final DolphinEntity dolphin;
        private final double speed;
        @Nullable
        private PlayerEntity closestPlayer;

        SwimWithPlayerGoal(DolphinEntity dolphin, double speed) {
            this.dolphin = dolphin;
            this.speed = speed;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            this.closestPlayer = this.dolphin.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, this.dolphin);
            if (this.closestPlayer == null) {
                return false;
            }
            return this.closestPlayer.isSwimming() && this.dolphin.getTarget() != this.closestPlayer;
        }

        @Override
        public boolean shouldContinue() {
            return this.closestPlayer != null && this.closestPlayer.isSwimming() && this.dolphin.squaredDistanceTo(this.closestPlayer) < 256.0;
        }

        @Override
        public void start() {
            this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100), this.dolphin);
        }

        @Override
        public void stop() {
            this.closestPlayer = null;
            this.dolphin.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.dolphin.getLookControl().lookAt(this.closestPlayer, this.dolphin.getMaxHeadRotation() + 20, this.dolphin.getMaxLookPitchChange());
            if (this.dolphin.squaredDistanceTo(this.closestPlayer) < 6.25) {
                this.dolphin.getNavigation().stop();
            } else {
                this.dolphin.getNavigation().startMovingTo(this.closestPlayer, this.speed);
            }
            if (this.closestPlayer.isSwimming() && this.closestPlayer.world.random.nextInt(6) == 0) {
                this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100), this.dolphin);
            }
        }
    }

    class PlayWithItemsGoal
    extends Goal {
        private int nextPlayingTime;

        PlayWithItemsGoal() {
        }

        @Override
        public boolean canStart() {
            if (this.nextPlayingTime > DolphinEntity.this.age) {
                return false;
            }
            List<ItemEntity> list = DolphinEntity.this.world.getEntitiesByClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), CAN_TAKE);
            return !list.isEmpty() || !DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Override
        public void start() {
            List<ItemEntity> list = DolphinEntity.this.world.getEntitiesByClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), CAN_TAKE);
            if (!list.isEmpty()) {
                DolphinEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
                DolphinEntity.this.playSound(SoundEvents.ENTITY_DOLPHIN_PLAY, 1.0f, 1.0f);
            }
            this.nextPlayingTime = 0;
        }

        @Override
        public void stop() {
            ItemStack itemStack = DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                this.spitOutItem(itemStack);
                DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                this.nextPlayingTime = DolphinEntity.this.age + DolphinEntity.this.random.nextInt(100);
            }
        }

        @Override
        public void tick() {
            List<ItemEntity> list = DolphinEntity.this.world.getEntitiesByClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), CAN_TAKE);
            ItemStack itemStack = DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                this.spitOutItem(itemStack);
                DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else if (!list.isEmpty()) {
                DolphinEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
            }
        }

        private void spitOutItem(ItemStack stack) {
            if (stack.isEmpty()) {
                return;
            }
            double d = DolphinEntity.this.getEyeY() - (double)0.3f;
            ItemEntity itemEntity = new ItemEntity(DolphinEntity.this.world, DolphinEntity.this.getX(), d, DolphinEntity.this.getZ(), stack);
            itemEntity.setPickupDelay(40);
            itemEntity.setThrower(DolphinEntity.this.getUuid());
            float f = 0.3f;
            float g = DolphinEntity.this.random.nextFloat() * ((float)Math.PI * 2);
            float h = 0.02f * DolphinEntity.this.random.nextFloat();
            itemEntity.setVelocity(0.3f * -MathHelper.sin(DolphinEntity.this.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(DolphinEntity.this.getPitch() * ((float)Math.PI / 180)) + MathHelper.cos(g) * h, 0.3f * MathHelper.sin(DolphinEntity.this.getPitch() * ((float)Math.PI / 180)) * 1.5f, 0.3f * MathHelper.cos(DolphinEntity.this.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(DolphinEntity.this.getPitch() * ((float)Math.PI / 180)) + MathHelper.sin(g) * h);
            DolphinEntity.this.world.spawnEntity(itemEntity);
        }
    }
}

