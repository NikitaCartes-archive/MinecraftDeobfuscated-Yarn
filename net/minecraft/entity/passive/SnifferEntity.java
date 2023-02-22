/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SnifferEntity
extends AnimalEntity {
    private static final int field_42656 = 1700;
    private static final int field_42657 = 6000;
    private static final int field_42658 = 30;
    private static final int field_42659 = 120;
    private static final int field_42660 = 10;
    private static final int field_42661 = 48000;
    private static final TrackedData<State> STATE = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.SNIFFER_STATE);
    private static final TrackedData<Integer> FINISH_DIG_TIME = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public final AnimationState walkingAnimationState = new AnimationState();
    public final AnimationState panickingAnimationState = new AnimationState();
    public final AnimationState feelingHappyAnimationState = new AnimationState();
    public final AnimationState scentingAnimationState = new AnimationState();
    public final AnimationState sniffingAnimationState = new AnimationState();
    public final AnimationState searchingAnimationState = new AnimationState();
    public final AnimationState diggingAnimationState = new AnimationState();
    public final AnimationState risingAnimationState = new AnimationState();

    public static DefaultAttributeContainer.Builder createSnifferAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1f).add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0);
    }

    public SnifferEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.dataTracker.startTracking(STATE, State.IDLING);
        this.dataTracker.startTracking(FINISH_DIG_TIME, 0);
        this.getNavigation().setCanSwim(true);
        this.setPathfindingPenalty(PathNodeType.WATER, -2.0f);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return this.getDimensions((EntityPose)pose).height * 0.6f;
    }

    private boolean isMoving() {
        boolean bl = this.onGround || this.isInsideWaterOrBubbleColumn();
        return bl && this.getVelocity().horizontalLengthSquared() > 1.0E-6;
    }

    private boolean isMovingInWater() {
        return this.isMoving() && this.isTouchingWater() && !this.isSubmergedInWater() && this.getVelocity().horizontalLengthSquared() > 9.999999999999999E-6;
    }

    private boolean isMovingOutsideWater() {
        return this.isMoving() && !this.isSubmergedInWater() && !this.isTouchingWater();
    }

    public boolean isPanicking() {
        return this.brain.getOptionalRegisteredMemory(MemoryModuleType.IS_PANICKING).isPresent();
    }

    public boolean isDiggingOrSearching() {
        return this.getState() == State.DIGGING || this.getState() == State.SEARCHING;
    }

    private BlockPos getDigPos() {
        Vec3d vec3d = this.getPos().add(this.getRotationVecClient().multiply(2.25));
        return BlockPos.ofFloored(vec3d.getX(), this.getY(), vec3d.getZ());
    }

    private State getState() {
        return this.dataTracker.get(STATE);
    }

    private SnifferEntity setState(State state) {
        this.dataTracker.set(STATE, state);
        return this;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (STATE.equals(data)) {
            State state = this.getState();
            this.stopAnimations();
            switch (state) {
                case SCENTING: {
                    this.scentingAnimationState.startIfNotRunning(this.age);
                    break;
                }
                case SNIFFING: {
                    this.sniffingAnimationState.startIfNotRunning(this.age);
                    break;
                }
                case SEARCHING: {
                    this.searchingAnimationState.startIfNotRunning(this.age);
                    break;
                }
                case DIGGING: {
                    this.diggingAnimationState.startIfNotRunning(this.age);
                    break;
                }
                case RISING: {
                    this.risingAnimationState.startIfNotRunning(this.age);
                    break;
                }
                case FEELING_HAPPY: {
                    this.feelingHappyAnimationState.startIfNotRunning(this.age);
                }
            }
        }
        super.onTrackedDataSet(data);
    }

    private void stopAnimations() {
        this.searchingAnimationState.stop();
        this.diggingAnimationState.stop();
        this.sniffingAnimationState.stop();
        this.risingAnimationState.stop();
        this.feelingHappyAnimationState.stop();
        this.scentingAnimationState.stop();
    }

    public SnifferEntity startState(State state) {
        switch (state) {
            case IDLING: {
                this.setState(State.IDLING);
                break;
            }
            case SCENTING: {
                this.playSound(SoundEvents.ENTITY_SNIFFER_SCENTING, 1.0f, 1.0f);
                this.setState(State.SCENTING);
                break;
            }
            case SNIFFING: {
                this.playSound(SoundEvents.ENTITY_SNIFFER_SNIFFING, 1.0f, 1.0f);
                this.setState(State.SNIFFING);
                break;
            }
            case SEARCHING: {
                this.setState(State.SEARCHING);
                break;
            }
            case DIGGING: {
                this.setState(State.DIGGING).setDigging();
                break;
            }
            case RISING: {
                this.playSound(SoundEvents.ENTITY_SNIFFER_DIGGING_STOP, 1.0f, 1.0f);
                this.setState(State.RISING);
                break;
            }
            case FEELING_HAPPY: {
                this.playSound(SoundEvents.ENTITY_SNIFFER_HAPPY, 1.0f, 1.0f);
                this.setState(State.FEELING_HAPPY);
            }
        }
        return this;
    }

    private SnifferEntity setDigging() {
        this.dataTracker.set(FINISH_DIG_TIME, this.age + 120);
        this.world.sendEntityStatus(this, (byte)63);
        return this;
    }

    public SnifferEntity finishDigging(boolean explored) {
        if (explored) {
            this.addExploredPosition(this.getSteppingPos());
        }
        return this;
    }

    Optional<BlockPos> findSniffingTargetPos() {
        return IntStream.range(0, 5).mapToObj(i -> FuzzyTargeting.find(this, 10 + 2 * i, 3)).filter(Objects::nonNull).map(BlockPos::ofFloored).map(BlockPos::down).filter(this::isDiggable).findFirst();
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    boolean canDig() {
        return !this.isPanicking() && !this.isBaby() && !this.isTouchingWater() && this.isDiggable(this.getDigPos().down());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean isDiggable(BlockPos pos) {
        if (!this.world.getBlockState(pos).isIn(BlockTags.SNIFFER_DIGGABLE_BLOCK)) return false;
        if (!this.world.getBlockState(pos.up()).isAir()) return false;
        if (!this.getExploredPositions().noneMatch(pos::equals)) return false;
        return true;
    }

    private void dropSeeds() {
        if (this.world.isClient() || this.dataTracker.get(FINISH_DIG_TIME) != this.age) {
            return;
        }
        ItemStack itemStack = new ItemStack(Items.TORCHFLOWER_SEEDS);
        BlockPos blockPos = this.getDigPos();
        ItemEntity itemEntity = new ItemEntity(this.world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
        itemEntity.setToDefaultPickupDelay();
        this.world.spawnEntity(itemEntity);
        this.playSound(SoundEvents.ENTITY_SNIFFER_DROP_SEED, 1.0f, 1.0f);
    }

    private SnifferEntity spawnDiggingParticles(AnimationState diggingAnimationState) {
        boolean bl;
        boolean bl2 = bl = diggingAnimationState.getTimeRunning() > 1700L && diggingAnimationState.getTimeRunning() < 6000L;
        if (bl) {
            BlockState blockState = this.getSteppingBlockState();
            BlockPos blockPos = this.getDigPos();
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                for (int i = 0; i < 30; ++i) {
                    Vec3d vec3d = Vec3d.ofCenter(blockPos);
                    this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
                }
                if (this.age % 10 == 0) {
                    this.world.playSound(this.getX(), this.getY(), this.getZ(), blockState.getSoundGroup().getHitSound(), this.getSoundCategory(), 0.5f, 0.5f, false);
                }
            }
        }
        return this;
    }

    private SnifferEntity addExploredPosition(BlockPos pos) {
        List list = this.getExploredPositions().limit(20L).collect(Collectors.toList());
        list.add(0, pos);
        this.getBrain().remember(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, list);
        return this;
    }

    private Stream<BlockPos> getExploredPositions() {
        return this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
    }

    @Override
    protected void jump() {
        double e;
        super.jump();
        double d = this.moveControl.getSpeed();
        if (d > 0.0 && (e = this.getVelocity().horizontalLengthSquared()) < 0.01) {
            this.updateVelocity(0.1f, new Vec3d(0.0, 0.0, 1.0));
        }
    }

    @Override
    public void tick() {
        boolean bl = this.isTouchingWater() && !this.isSubmergedInWater();
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(bl ? (double)0.2f : (double)0.1f);
        if (this.isMovingOutsideWater() || this.isMovingInWater()) {
            if (this.isPanicking()) {
                this.walkingAnimationState.stop();
                this.panickingAnimationState.startIfNotRunning(this.age);
            } else {
                this.panickingAnimationState.stop();
                this.walkingAnimationState.startIfNotRunning(this.age);
            }
        } else {
            this.panickingAnimationState.stop();
            this.walkingAnimationState.stop();
        }
        switch (this.getState()) {
            case DIGGING: {
                this.spawnDiggingParticles(this.diggingAnimationState).dropSeeds();
                break;
            }
            case SEARCHING: {
                this.playSearchingSound();
            }
        }
        super.tick();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        ActionResult actionResult = super.interactMob(player, hand);
        if (actionResult.isAccepted() && this.isBreedingItem(itemStack)) {
            this.world.playSoundFromEntity(null, this, this.getEatSound(itemStack), SoundCategory.NEUTRAL, 1.0f, MathHelper.nextBetween(this.world.random, 0.8f, 1.2f));
        }
        return actionResult;
    }

    private void playSearchingSound() {
        if (this.world.isClient() && this.age % 20 == 0) {
            this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_SNIFFER_SEARCHING, this.getSoundCategory(), 1.0f, 1.0f, false);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_SNIFFER_STEP, 0.15f, 1.0f);
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return SoundEvents.ENTITY_SNIFFER_EAT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return Set.of(State.DIGGING, State.SEARCHING).contains((Object)this.getState()) ? null : SoundEvents.ENTITY_SNIFFER_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SNIFFER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SNIFFER_DEATH;
    }

    @Override
    public int getMaxHeadRotation() {
        return 50;
    }

    @Override
    public void setBaby(boolean baby) {
        this.setBreedingAge(baby ? -48000 : 0);
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return EntityType.SNIFFER.create(world);
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other instanceof SnifferEntity) {
            SnifferEntity snifferEntity = (SnifferEntity)other;
            Set<State> set = Set.of(State.IDLING, State.SCENTING, State.FEELING_HAPPY);
            return set.contains((Object)this.getState()) && set.contains((Object)snifferEntity.getState()) && super.canBreedWith(other);
        }
        return false;
    }

    @Override
    public Box getVisibilityBoundingBox() {
        return super.getVisibilityBoundingBox().expand(0.6f);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ItemTags.SNIFFER_FOOD);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return SnifferBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<SnifferEntity> getBrain() {
        return super.getBrain();
    }

    protected Brain.Profile<SnifferEntity> createBrainProfile() {
        return Brain.createProfile(SnifferBrain.MEMORY_MODULES, SnifferBrain.SENSORS);
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("snifferBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().swap("snifferActivityUpdate");
        SnifferBrain.updateActivities(this);
        this.world.getProfiler().pop();
        super.mobTick();
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public static enum State {
        IDLING,
        FEELING_HAPPY,
        SCENTING,
        SNIFFING,
        SEARCHING,
        DIGGING,
        RISING;

    }
}

