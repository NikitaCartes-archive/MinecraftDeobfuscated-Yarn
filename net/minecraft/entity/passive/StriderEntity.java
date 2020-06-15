/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.LinkedHashSet;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class StriderEntity
extends AnimalEntity
implements ItemSteerable,
Saddleable {
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.WARPED_FUNGUS);
    private static final Ingredient ATTRACTING_INGREDIENT = Ingredient.ofItems(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
    private static final TrackedData<Integer> BOOST_TIME = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> COLD = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final SaddledComponent saddledComponent;
    private TemptGoal temptGoal;
    private EscapeDangerGoal escapeDangerGoal;

    public StriderEntity(EntityType<? extends StriderEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
        this.saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME, SADDLED);
        this.inanimate = true;
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0f);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0f);
    }

    public static boolean canSpawn(EntityType<StriderEntity> type, WorldAccess worldAccess, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        do {
            mutable.move(Direction.UP);
        } while (worldAccess.getFluidState(mutable).matches(FluidTags.LAVA));
        return worldAccess.getBlockState(mutable).isAir();
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (BOOST_TIME.equals(data) && this.world.isClient) {
            this.saddledComponent.boost();
        }
        super.onTrackedDataSet(data);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BOOST_TIME, 0);
        this.dataTracker.startTracking(COLD, false);
        this.dataTracker.startTracking(SADDLED, false);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        this.saddledComponent.toTag(tag);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.saddledComponent.fromTag(tag);
    }

    @Override
    public boolean isSaddled() {
        return this.saddledComponent.isSaddled();
    }

    @Override
    public boolean canBeSaddled() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void saddle(@Nullable SoundCategory sound) {
        this.saddledComponent.setSaddled(true);
        if (sound != null) {
            this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_STRIDER_SADDLE, sound, 0.5f, 1.0f);
        }
    }

    @Override
    protected void initGoals() {
        this.escapeDangerGoal = new EscapeDangerGoal(this, 1.65);
        this.goalSelector.add(1, this.escapeDangerGoal);
        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
        this.temptGoal = new TemptGoal((MobEntityWithAi)this, 1.4, false, ATTRACTING_INGREDIENT);
        this.goalSelector.add(4, this.temptGoal);
        this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(7, new WanderAroundGoal(this, 1.0, 60));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(9, new LookAtEntityGoal(this, StriderEntity.class, 8.0f));
    }

    public void setCold(boolean cold) {
        this.dataTracker.set(COLD, cold);
    }

    public boolean isCold() {
        if (this.getVehicle() instanceof StriderEntity) {
            return ((StriderEntity)this.getVehicle()).isCold();
        }
        return this.dataTracker.get(COLD);
    }

    @Override
    public boolean canWalkOnFluid(Fluid fluid) {
        return fluid.isIn(FluidTags.LAVA);
    }

    @Override
    @Nullable
    public Box getHardCollisionBox(Entity collidingEntity) {
        if (collidingEntity.isPushable()) {
            return collidingEntity.getBoundingBox();
        }
        return null;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public double getMountedHeightOffset() {
        float f = Math.min(0.25f, this.limbDistance);
        float g = this.limbAngle;
        return (double)this.getHeight() - 0.2 + (double)(0.12f * MathHelper.cos(g * 1.5f) * 2.0f * f);
    }

    @Override
    public boolean canBeControlledByRider() {
        Entity entity = this.getPrimaryPassenger();
        if (!(entity instanceof PlayerEntity)) {
            return false;
        }
        PlayerEntity playerEntity = (PlayerEntity)entity;
        return playerEntity.getMainHandStack().getItem() == Items.WARPED_FUNGUS_ON_A_STICK || playerEntity.getOffHandStack().getItem() == Items.WARPED_FUNGUS_ON_A_STICK;
    }

    @Override
    public boolean canSpawn(WorldView world) {
        return world.intersectsEntities(this);
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        if (this.getPassengerList().isEmpty()) {
            return null;
        }
        return this.getPassengerList().get(0);
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        double f;
        Vec3d[] vec3ds = new Vec3d[]{StriderEntity.getPassengerDismountOffset(this.getWidth(), passenger.getWidth(), passenger.yaw), StriderEntity.getPassengerDismountOffset(this.getWidth(), passenger.getWidth(), passenger.yaw - 22.5f), StriderEntity.getPassengerDismountOffset(this.getWidth(), passenger.getWidth(), passenger.yaw + 22.5f), StriderEntity.getPassengerDismountOffset(this.getWidth(), passenger.getWidth(), passenger.yaw - 45.0f), StriderEntity.getPassengerDismountOffset(this.getWidth(), passenger.getWidth(), passenger.yaw + 45.0f)};
        LinkedHashSet<BlockPos> set = Sets.newLinkedHashSet();
        double d = this.getBoundingBox().maxY;
        double e = this.getBoundingBox().minY - 0.5;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Vec3d vec3d : vec3ds) {
            mutable.set(this.getX() + vec3d.x, d, this.getZ() + vec3d.z);
            for (f = d; f > e; f -= 1.0) {
                set.add(mutable.toImmutable());
                mutable.move(Direction.DOWN);
            }
        }
        for (BlockPos blockPos : set) {
            if (this.world.getFluidState(blockPos).matches(FluidTags.LAVA)) continue;
            for (EntityPose entityPose : passenger.getPoses()) {
                Vec3d vec3d2;
                Box box;
                f = this.world.getCollisionHeightAt(blockPos);
                if (!Dismounting.canDismountInBlock(f) || !Dismounting.canPlaceEntityAt(this.world, passenger, (box = passenger.getBoundingBox(entityPose)).offset(vec3d2 = Vec3d.ofCenter(blockPos, f)))) continue;
                passenger.setPose(entityPose);
                return vec3d2;
            }
        }
        return new Vec3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    @Override
    public void travel(Vec3d movementInput) {
        this.setMovementSpeed(this.getSpeed());
        this.travel(this, this.saddledComponent, movementInput);
    }

    public float getSpeed() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * (this.isCold() ? 0.66f : 1.0f);
    }

    @Override
    public float getSaddledSpeed() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * (this.isCold() ? 0.23f : 0.55f);
    }

    @Override
    public void setMovementInput(Vec3d movementInput) {
        super.travel(movementInput);
    }

    @Override
    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.6f;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.isInLava() ? SoundEvents.ENTITY_STRIDER_STEP_LAVA : SoundEvents.ENTITY_STRIDER_STEP, 1.0f, 1.0f);
    }

    @Override
    public boolean consumeOnAStickItem() {
        return this.saddledComponent.boost(this.getRandom());
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        this.checkBlockCollision();
        if (this.isInLava()) {
            this.fallDistance = 0.0f;
            return;
        }
        super.fall(heightDifference, onGround, landedState, landedPosition);
    }

    @Override
    public void tick() {
        if (this.method_30079() && this.random.nextInt(140) == 0) {
            this.playSound(SoundEvents.ENTITY_STRIDER_HAPPY, 1.0f, this.getSoundPitch());
        } else if (this.method_30078() && this.random.nextInt(60) == 0) {
            this.playSound(SoundEvents.ENTITY_STRIDER_RETREAT, 1.0f, this.getSoundPitch());
        }
        BlockState blockState = this.world.getBlockState(this.getBlockPos());
        BlockState blockState2 = this.getLandingBlockState();
        boolean bl = blockState.isIn(BlockTags.STRIDER_WARM_BLOCKS) || blockState2.isIn(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0;
        this.setCold(!bl);
        super.tick();
        this.updateFloating();
        this.checkBlockCollision();
    }

    private boolean method_30078() {
        return this.escapeDangerGoal != null && this.escapeDangerGoal.isActive();
    }

    private boolean method_30079() {
        return this.temptGoal != null && this.temptGoal.isActive();
    }

    @Override
    protected boolean movesIndependently() {
        return true;
    }

    private void updateFloating() {
        if (this.isInLava()) {
            ShapeContext shapeContext = ShapeContext.of(this);
            if (!shapeContext.isAbove(FluidBlock.COLLISION_SHAPE, this.getBlockPos(), true) || this.world.getFluidState(this.getBlockPos().up()).matches(FluidTags.LAVA)) {
                this.setVelocity(this.getVelocity().multiply(0.5).add(0.0, 0.05, 0.0));
            } else {
                this.onGround = true;
            }
        }
    }

    public static DefaultAttributeContainer.Builder createStriderAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.175f).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.method_30078() || this.method_30079()) {
            return null;
        }
        return SoundEvents.ENTITY_STRIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_STRIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_STRIDER_DEATH;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().isEmpty() && !this.isSubmergedIn(FluidTags.LAVA);
    }

    @Override
    public boolean hurtByWater() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new Navigation(this, world);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        if (world.getBlockState(pos).getFluidState().matches(FluidTags.LAVA)) {
            return 10.0f;
        }
        return 0.0f;
    }

    @Override
    public StriderEntity createChild(PassiveEntity passiveEntity) {
        return EntityType.STRIDER.create(this.world);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.isSaddled()) {
            this.dropItem(Items.SADDLE);
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        boolean bl = this.isBreedingItem(player.getStackInHand(hand));
        if (!bl && this.isSaddled() && !this.hasPassengers()) {
            if (!this.world.isClient) {
                player.startRiding(this);
            }
            return ActionResult.success(this.world.isClient);
        }
        ActionResult actionResult = super.interactMob(player, hand);
        if (!actionResult.isAccepted()) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.getItem() == Items.SADDLE) {
                return itemStack.useOnEntity(player, this, hand);
            }
            return ActionResult.PASS;
        }
        if (bl && !this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_STRIDER_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        return actionResult;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d method_29919() {
        return new Vec3d(0.0, 0.6f * this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        ZombifiedPiglinEntity zombifiedPiglinEntity;
        StriderData.RiderType riderType;
        ZombieEntity.ZombieData entityData2 = null;
        if (entityData instanceof StriderData) {
            riderType = ((StriderData)entityData).type;
        } else if (!this.isBaby()) {
            if (this.random.nextInt(30) == 0) {
                riderType = StriderData.RiderType.PIGLIN_RIDER;
                entityData2 = new ZombieEntity.ZombieData(ZombieEntity.method_29936(this.random), false);
            } else {
                riderType = this.random.nextInt(10) == 0 ? StriderData.RiderType.BABY_RIDER : StriderData.RiderType.NO_RIDER;
            }
            entityData = new StriderData(riderType);
            ((PassiveEntity.PassiveData)entityData).setBabyChance(riderType == StriderData.RiderType.NO_RIDER ? 0.5f : 0.0f);
        } else {
            riderType = StriderData.RiderType.NO_RIDER;
        }
        MobEntityWithAi mobEntity = null;
        if (riderType == StriderData.RiderType.BABY_RIDER) {
            StriderEntity striderEntity = EntityType.STRIDER.create(world.getWorld());
            if (striderEntity != null) {
                mobEntity = striderEntity;
                striderEntity.setBreedingAge(-24000);
            }
        } else if (riderType == StriderData.RiderType.PIGLIN_RIDER && (zombifiedPiglinEntity = EntityType.ZOMBIFIED_PIGLIN.create(world.getWorld())) != null) {
            mobEntity = zombifiedPiglinEntity;
            this.saddle(null);
        }
        if (mobEntity != null) {
            mobEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, 0.0f);
            mobEntity.initialize(world, difficulty, SpawnReason.JOCKEY, entityData2, null);
            mobEntity.startRiding(this, true);
            world.spawnEntity(mobEntity);
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity mate) {
        return this.createChild(mate);
    }

    static class Navigation
    extends MobNavigation {
        Navigation(StriderEntity entity, World world) {
            super(entity, world);
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new LandPathNodeMaker();
            return new PathNodeNavigator(this.nodeMaker, range);
        }

        @Override
        protected boolean canWalkOnPath(PathNodeType pathType) {
            if (pathType == PathNodeType.LAVA || pathType == PathNodeType.DAMAGE_FIRE || pathType == PathNodeType.DANGER_FIRE) {
                return true;
            }
            return super.canWalkOnPath(pathType);
        }

        @Override
        public boolean isValidPosition(BlockPos pos) {
            return this.world.getBlockState(pos).isOf(Blocks.LAVA) || super.isValidPosition(pos);
        }
    }

    public static class StriderData
    extends PassiveEntity.PassiveData {
        public final RiderType type;

        public StriderData(RiderType type) {
            this.type = type;
        }

        public static enum RiderType {
            NO_RIDER,
            BABY_RIDER,
            PIGLIN_RIDER;

        }
    }
}

