/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.AttackPosOffsettingMount;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CamelBrain;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CamelEntity
extends AbstractHorseEntity
implements JumpingMount,
AttackPosOffsettingMount,
Saddleable {
    public static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.CACTUS);
    public static final int field_40132 = 55;
    public static final int field_41764 = 30;
    private static final float field_40146 = 0.1f;
    private static final float field_40147 = 1.4285f;
    private static final float field_40148 = 22.2222f;
    private static final int field_40149 = 40;
    private static final int field_40133 = 52;
    private static final int field_40134 = 80;
    private static final float field_40135 = 1.43f;
    public static final TrackedData<Boolean> DASHING = DataTracker.registerData(CamelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Long> LAST_POSE_TICK = DataTracker.registerData(CamelEntity.class, TrackedDataHandlerRegistry.LONG);
    public final AnimationState walkingAnimationState = new AnimationState();
    public final AnimationState sittingTransitionAnimationState = new AnimationState();
    public final AnimationState sittingAnimationState = new AnimationState();
    public final AnimationState standingTransitionAnimationState = new AnimationState();
    public final AnimationState idlingAnimationState = new AnimationState();
    public final AnimationState dashingAnimationState = new AnimationState();
    private static final EntityDimensions SITTING_DIMENSIONS = EntityDimensions.changing(EntityType.CAMEL.getWidth(), EntityType.CAMEL.getHeight() - 1.43f);
    private int dashCooldown = 0;
    private int idleAnimationCooldown = 0;

    public CamelEntity(EntityType<? extends CamelEntity> entityType, World world) {
        super((EntityType<? extends AbstractHorseEntity>)entityType, world);
        this.stepHeight = 1.5f;
        MobNavigation mobNavigation = (MobNavigation)this.getNavigation();
        mobNavigation.setCanSwim(true);
        mobNavigation.setCanWalkOverFences(true);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putLong("LastPoseTick", this.dataTracker.get(LAST_POSE_TICK));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        long l = nbt.getLong("LastPoseTick");
        if (l < 0L) {
            this.setPose(EntityPose.SITTING);
        }
        this.setLastPoseTick(l);
    }

    public static DefaultAttributeContainer.Builder createCamelAttributes() {
        return CamelEntity.createBaseHorseAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 32.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.09f).add(EntityAttributes.HORSE_JUMP_STRENGTH, 0.42f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DASHING, false);
        this.dataTracker.startTracking(LAST_POSE_TICK, 0L);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        CamelBrain.method_45367(this, world.getRandom());
        this.initLastPoseTick(world.toServerWorld().getTime());
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    protected Brain.Profile<CamelEntity> createBrainProfile() {
        return CamelBrain.createProfile();
    }

    @Override
    protected void initGoals() {
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return CamelBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return pose == EntityPose.SITTING ? SITTING_DIMENSIONS.scaled(this.getScaleFactor()) : super.getDimensions(pose);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height - 0.1f;
    }

    @Override
    public double getPassengerAttackYOffset() {
        return 0.5;
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("camelBrain");
        Brain<?> brain = this.getBrain();
        brain.tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("camelActivityUpdate");
        CamelBrain.updateActivities(this);
        this.world.getProfiler().pop();
        super.mobTick();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isDashing() && this.dashCooldown < 55 && (this.onGround || this.isTouchingWater())) {
            this.setDashing(false);
        }
        if (this.dashCooldown > 0) {
            --this.dashCooldown;
            if (this.dashCooldown == 0) {
                this.world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_CAMEL_DASH_READY, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
        if (this.world.isClient()) {
            this.updateAnimations();
        }
        if (this.isStationary()) {
            this.clampHeadYaw(this, 30.0f);
        }
    }

    private void updateAnimations() {
        if (this.idleAnimationCooldown <= 0) {
            this.idleAnimationCooldown = this.random.nextInt(40) + 80;
            this.idlingAnimationState.start(this.age);
        } else {
            --this.idleAnimationCooldown;
        }
        if (this.isSitting()) {
            this.walkingAnimationState.stop();
            this.standingTransitionAnimationState.stop();
            this.dashingAnimationState.stop();
            if (this.shouldPlaySittingTransitionAnimation()) {
                this.sittingTransitionAnimationState.startIfNotRunning(this.age);
                this.sittingAnimationState.stop();
            } else {
                this.sittingTransitionAnimationState.stop();
                this.sittingAnimationState.startIfNotRunning(this.age);
            }
        } else {
            this.sittingTransitionAnimationState.stop();
            this.sittingAnimationState.stop();
            this.dashingAnimationState.setRunning(this.isDashing(), this.age);
            this.standingTransitionAnimationState.setRunning(this.isChangingPose(), this.age);
            this.walkingAnimationState.setRunning((this.onGround || this.hasPrimaryPassenger()) && this.getVelocity().horizontalLengthSquared() > 1.0E-6, this.age);
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (!this.isAlive()) {
            return;
        }
        if (this.isStationary() && this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(0.0, 1.0, 0.0));
            movementInput = movementInput.multiply(0.0, 1.0, 0.0);
        }
        super.travel(movementInput);
    }

    public boolean isStationary() {
        return this.isSitting() || this.isChangingPose();
    }

    @Override
    protected float getHorsebackMovementSpeed(LivingEntity passenger) {
        float f = passenger.isSprinting() && this.getJumpCooldown() == 0 ? 0.1f : 0.0f;
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + f;
    }

    @Override
    protected boolean ignoresMovementInput(LivingEntity passenger) {
        boolean bl = this.isChangingPose();
        if (this.isSitting() && !bl && passenger.forwardSpeed > 0.0f) {
            this.startStanding();
        }
        return this.isStationary() || super.ignoresMovementInput(passenger);
    }

    @Override
    public boolean canJump(PlayerEntity player) {
        return !this.isStationary() && this.getPrimaryPassenger() == player && super.canJump(player);
    }

    @Override
    public void setJumpStrength(int strength) {
        if (!this.isSaddled() || this.dashCooldown > 0 || !this.isOnGround()) {
            return;
        }
        super.setJumpStrength(strength);
    }

    @Override
    public boolean canSprintAsVehicle() {
        return true;
    }

    @Override
    protected void jump(float strength, float sidewaysSpeed, float forwardSpeed) {
        double d = this.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH) * (double)this.getJumpVelocityMultiplier() + this.getJumpBoostVelocityModifier();
        this.addVelocity(this.getRotationVector().multiply(1.0, 0.0, 1.0).normalize().multiply((double)(22.2222f * strength) * this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * (double)this.getVelocityMultiplier()).add(0.0, (double)(1.4285f * strength) * d, 0.0));
        this.dashCooldown = 55;
        this.setDashing(true);
        this.velocityDirty = true;
    }

    public boolean isDashing() {
        return this.dataTracker.get(DASHING);
    }

    public void setDashing(boolean dashing) {
        this.dataTracker.set(DASHING, dashing);
    }

    public boolean isPanicking() {
        return this.getBrain().isMemoryInState(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_PRESENT);
    }

    @Override
    public void startJumping(int height) {
        this.playSound(SoundEvents.ENTITY_CAMEL_DASH, 1.0f, 1.0f);
        this.setDashing(true);
    }

    @Override
    public void stopJumping() {
    }

    @Override
    public int getJumpCooldown() {
        return this.dashCooldown;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_CAMEL_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CAMEL_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_CAMEL_HURT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (state.getSoundGroup() == BlockSoundGroup.SAND) {
            this.playSound(SoundEvents.ENTITY_CAMEL_STEP_SAND, 1.0f, 1.0f);
        } else {
            this.playSound(SoundEvents.ENTITY_CAMEL_STEP, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (player.shouldCancelInteraction()) {
            this.openInventory(player);
            return ActionResult.success(this.world.isClient);
        }
        ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
        if (actionResult.isAccepted()) {
            return actionResult;
        }
        if (this.isBreedingItem(itemStack)) {
            return this.interactHorse(player, itemStack);
        }
        if (this.getPassengerList().size() < 2 && !this.isBaby()) {
            this.putPlayerOnBack(player);
        }
        return ActionResult.success(this.world.isClient);
    }

    @Override
    protected void updateForLeashLength(float leashLength) {
        if (leashLength > 6.0f && this.isSitting() && !this.isChangingPose()) {
            this.startStanding();
        }
    }

    @Override
    protected boolean receiveFood(PlayerEntity player, ItemStack item) {
        boolean bl3;
        boolean bl2;
        boolean bl;
        if (!this.isBreedingItem(item)) {
            return false;
        }
        boolean bl4 = bl = this.getHealth() < this.getMaxHealth();
        if (bl) {
            this.heal(2.0f);
        }
        boolean bl5 = bl2 = this.isTame() && this.getBreedingAge() == 0 && this.canEat();
        if (bl2) {
            this.lovePlayer(player);
        }
        if (bl3 = this.isBaby()) {
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.growUp(10);
            }
        }
        if (bl || bl2 || bl3) {
            SoundEvent soundEvent;
            if (!this.isSilent() && (soundEvent = this.getEatSound()) != null) {
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean shouldAmbientStand() {
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) return false;
        if (!(other instanceof CamelEntity)) return false;
        CamelEntity camelEntity = (CamelEntity)other;
        if (!this.canBreed()) return false;
        if (!camelEntity.canBreed()) return false;
        return true;
    }

    @Override
    @Nullable
    public CamelEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return EntityType.CAMEL.create(serverWorld);
    }

    @Override
    @Nullable
    protected SoundEvent getEatSound() {
        return SoundEvents.ENTITY_CAMEL_EAT;
    }

    @Override
    protected void applyDamage(DamageSource source, float amount) {
        this.setStanding();
        super.applyDamage(source, amount);
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        int i = this.getPassengerList().indexOf(passenger);
        if (i < 0) {
            return;
        }
        boolean bl = i == 0;
        float f = 0.5f;
        float g = (float)(this.isRemoved() ? (double)0.01f : this.method_45346(bl, 0.0f) + passenger.getHeightOffset());
        if (this.getPassengerList().size() > 1) {
            if (!bl) {
                f = -0.7f;
            }
            if (passenger instanceof AnimalEntity) {
                f += 0.2f;
            }
        }
        Vec3d vec3d = new Vec3d(0.0, 0.0, f).rotateY(-this.bodyYaw * ((float)Math.PI / 180));
        passenger.setPosition(this.getX() + vec3d.x, this.getY() + (double)g, this.getZ() + vec3d.z);
        this.clampPassengerYaw(passenger);
    }

    private double method_45346(boolean bl, float f) {
        double d = this.getMountedHeightOffset();
        float g = this.getScaleFactor() * 1.43f;
        float h = g - this.getScaleFactor() * 0.2f;
        float i = g - h;
        boolean bl2 = this.isChangingPose();
        boolean bl3 = this.isSitting();
        if (bl2) {
            float l;
            int k;
            int j;
            int n = j = bl3 ? 40 : 52;
            if (bl3) {
                k = 28;
                l = bl ? 0.5f : 0.1f;
            } else {
                k = bl ? 24 : 32;
                l = bl ? 0.6f : 0.35f;
            }
            float m = MathHelper.clamp((float)this.getLastPoseTickDelta() + f, 0.0f, (float)j);
            boolean bl4 = m < (float)k;
            float n2 = bl4 ? m / (float)k : (m - (float)k) / (float)(j - k);
            float o = g - l * h;
            d += bl3 ? (double)MathHelper.lerp(n2, bl4 ? g : o, bl4 ? o : i) : (double)MathHelper.lerp(n2, bl4 ? i - g : i - o, bl4 ? i - o : 0.0f);
        }
        if (bl3 && !bl2) {
            d += (double)i;
        }
        return d;
    }

    @Override
    public Vec3d getLeashOffset(float tickDelta) {
        return new Vec3d(0.0, this.method_45346(true, tickDelta) - (double)(0.2f * this.getScaleFactor()), this.getWidth() * 0.56f);
    }

    @Override
    public double getMountedHeightOffset() {
        return this.getDimensions((EntityPose)(this.isSitting() ? EntityPose.SITTING : EntityPose.STANDING)).height - (this.isBaby() ? 0.35f : 0.6f);
    }

    @Override
    public void onPassengerLookAround(Entity passenger) {
        if (this.getPrimaryPassenger() != passenger) {
            this.clampPassengerYaw(passenger);
        }
    }

    private void clampPassengerYaw(Entity passenger) {
        passenger.setBodyYaw(this.getYaw());
        float f = passenger.getYaw();
        float g = MathHelper.wrapDegrees(f - this.getYaw());
        float h = MathHelper.clamp(g, -160.0f, 160.0f);
        passenger.prevYaw += h - g;
        float i = f + h - g;
        passenger.setYaw(i);
        passenger.setHeadYaw(i);
    }

    private void clampHeadYaw(Entity entity, float range) {
        float f = entity.getHeadYaw();
        float g = MathHelper.wrapDegrees(this.bodyYaw - f);
        float h = MathHelper.clamp(MathHelper.wrapDegrees(this.bodyYaw - f), -range, range);
        float i = f + g - h;
        entity.setHeadYaw(i);
    }

    @Override
    public int getMaxHeadRotation() {
        return 30;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() <= 2;
    }

    @Override
    @Nullable
    public LivingEntity getPrimaryPassenger() {
        Entity entity;
        if (!this.getPassengerList().isEmpty() && this.isSaddled() && (entity = this.getPassengerList().get(0)) instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            return livingEntity;
        }
        return null;
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public boolean isSitting() {
        return this.dataTracker.get(LAST_POSE_TICK) < 0L;
    }

    public boolean isChangingPose() {
        long l = this.getLastPoseTickDelta();
        return l < (long)(this.isSitting() ? 40 : 52);
    }

    private boolean shouldPlaySittingTransitionAnimation() {
        return this.isSitting() && this.getLastPoseTickDelta() < 40L;
    }

    public void startSitting() {
        if (this.isSitting()) {
            return;
        }
        this.playSound(SoundEvents.ENTITY_CAMEL_SIT, 1.0f, 1.0f);
        this.setPose(EntityPose.SITTING);
        this.setLastPoseTick(-this.world.getTime());
    }

    public void startStanding() {
        if (!this.isSitting()) {
            return;
        }
        this.playSound(SoundEvents.ENTITY_CAMEL_STAND, 1.0f, 1.0f);
        this.setPose(EntityPose.STANDING);
        this.setLastPoseTick(this.world.getTime());
    }

    public void setStanding() {
        this.setPose(EntityPose.STANDING);
        this.initLastPoseTick(this.world.getTime());
    }

    @VisibleForTesting
    public void setLastPoseTick(long lastPoseTick) {
        this.dataTracker.set(LAST_POSE_TICK, lastPoseTick);
    }

    private void initLastPoseTick(long time) {
        this.setLastPoseTick(Math.max(0L, time - 52L - 1L));
    }

    public long getLastPoseTickDelta() {
        return this.world.getTime() - Math.abs(this.dataTracker.get(LAST_POSE_TICK));
    }

    @Override
    public SoundEvent getSaddleSound() {
        return SoundEvents.ENTITY_CAMEL_SADDLE;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (!this.firstUpdate && DASHING.equals(data)) {
            this.dashCooldown = this.dashCooldown == 0 ? 55 : this.dashCooldown;
        }
        super.onTrackedDataSet(data);
    }

    @Override
    protected BodyControl createBodyControl() {
        return new CamelBodyControl(this);
    }

    @Override
    public boolean isTame() {
        return true;
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!this.world.isClient) {
            player.openHorseInventory(this, this.items);
        }
    }

    @Override
    @Nullable
    public /* synthetic */ PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return this.createChild(world, entity);
    }

    @Override
    @Nullable
    public /* synthetic */ Entity getPrimaryPassenger() {
        return this.getPrimaryPassenger();
    }

    class CamelBodyControl
    extends BodyControl {
        public CamelBodyControl(CamelEntity camel) {
            super(camel);
        }

        @Override
        public void tick() {
            if (!CamelEntity.this.isStationary()) {
                super.tick();
            }
        }
    }
}

