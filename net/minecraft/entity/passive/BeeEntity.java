/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.entity.BeeHiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BeeEntity
extends AnimalEntity
implements Flutterer {
    private static final TrackedData<Byte> multipleByteTracker = DataTracker.registerData(BeeEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> anger = DataTracker.registerData(BeeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private UUID targetPlayer;
    private float currentPitch;
    private float lastPitch;
    private int ticksSinceSting;
    private int ticksSincePollination;
    private int cannotEnterHiveTicks;
    private int cropsGrownSincePollination;
    private BlockPos flowerPos = BlockPos.ORIGIN;
    private BlockPos hivePos = BlockPos.ORIGIN;
    private PollinateGoal field_21079;
    private int field_21509;

    public BeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.lookControl = new BeeLookControl(this);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
        this.setPathfindingPenalty(PathNodeType.COCOA, -1.0f);
        this.setPathfindingPenalty(PathNodeType.FENCE, -1.0f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(multipleByteTracker, (byte)0);
        this.dataTracker.startTracking(anger, 0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new FindHiveGoal());
        this.goalSelector.add(0, new StingGoal(this, 1.4f, true));
        this.goalSelector.add(1, new EnterHiveGoal());
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal((MobEntityWithAi)this, 1.25, Ingredient.fromTag(ItemTags.FLOWERS), false));
        this.field_21079 = new PollinateGoal();
        this.goalSelector.add(4, this.field_21079);
        this.goalSelector.add(5, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(5, new MoveToHiveGoal());
        this.goalSelector.add(6, new MoveToFlowerGoal());
        this.goalSelector.add(7, new GrowCropsGoal());
        this.goalSelector.add(8, new BeeWanderAroundGoal());
        this.goalSelector.add(9, new SwimGoal(this));
        this.targetSelector.add(1, new BeeRevengeGoal(this).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new BeeFollowTargetGoal(this));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.put("HivePos", NbtHelper.fromBlockPos(this.hivePos));
        compoundTag.put("FlowerPos", NbtHelper.fromBlockPos(this.flowerPos));
        compoundTag.putBoolean("HasNectar", this.hasNectar());
        compoundTag.putBoolean("HasStung", this.hasStung());
        compoundTag.putInt("TicksSincePollination", this.ticksSincePollination);
        compoundTag.putInt("CannotEnterHiveTicks", this.cannotEnterHiveTicks);
        compoundTag.putInt("CropsGrownSincePollination", this.cropsGrownSincePollination);
        compoundTag.putInt("Anger", this.getAnger());
        if (this.targetPlayer != null) {
            compoundTag.putString("HurtBy", this.targetPlayer.toString());
        } else {
            compoundTag.putString("HurtBy", "");
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        this.hivePos = NbtHelper.toBlockPos(compoundTag.getCompound("HivePos"));
        this.flowerPos = NbtHelper.toBlockPos(compoundTag.getCompound("FlowerPos"));
        super.readCustomDataFromTag(compoundTag);
        this.setHasNectar(compoundTag.getBoolean("HasNectar"));
        this.setHasStung(compoundTag.getBoolean("HasStung"));
        this.setAnger(compoundTag.getInt("Anger"));
        this.ticksSincePollination = compoundTag.getInt("TicksSincePollination");
        this.cannotEnterHiveTicks = compoundTag.getInt("CannotEnterHiveTicks");
        this.cropsGrownSincePollination = compoundTag.getInt("NumCropsGrownSincePollination");
        String string = compoundTag.getString("HurtBy");
        if (!string.isEmpty()) {
            this.targetPlayer = UUID.fromString(string);
            PlayerEntity playerEntity = this.world.getPlayerByUuid(this.targetPlayer);
            this.setAttacker(playerEntity);
            if (playerEntity != null) {
                this.attackingPlayer = playerEntity;
                this.playerHitTimer = this.getLastAttackedTime();
            }
        }
    }

    @Override
    public boolean tryAttack(Entity entity) {
        boolean bl = entity.damage(DamageSource.sting(this), (int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue());
        if (bl) {
            this.dealDamage(this, entity);
            if (entity instanceof LivingEntity) {
                ((LivingEntity)entity).setStingerCount(((LivingEntity)entity).getStingerCount() + 1);
                int i = 0;
                if (this.world.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.world.getDifficulty() == Difficulty.HARD) {
                    i = 18;
                }
                if (i > 0) {
                    ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, i * 20, 0));
                }
            }
            this.setHasStung(true);
            this.setTarget(null);
            this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0f, 1.0f);
        }
        return bl;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05f) {
            for (int i = 0; i < this.random.nextInt(2) + 1; ++i) {
                this.addParticle(this.world, this.getX() - (double)0.3f, this.getX() + (double)0.3f, this.getZ() - (double)0.3f, this.getZ() + (double)0.3f, this.getHeightAt(0.5), ParticleTypes.FALLING_NECTAR);
            }
        }
        this.updateBodyPitch();
    }

    private void addParticle(World world, double d, double e, double f, double g, double h, ParticleEffect particleEffect) {
        world.addParticle(particleEffect, MathHelper.lerp(world.random.nextDouble(), d, e), h, MathHelper.lerp(world.random.nextDouble(), f, g), 0.0, 0.0, 0.0);
    }

    public BlockPos getFlowerPos() {
        return this.flowerPos;
    }

    public boolean hasFlower() {
        return this.flowerPos != BlockPos.ORIGIN;
    }

    public void setFlowerPos(BlockPos blockPos) {
        this.flowerPos = blockPos;
    }

    private boolean canEnterHive() {
        boolean bl;
        if (this.cannotEnterHiveTicks > 0) {
            return false;
        }
        if (!this.hasHive()) {
            return false;
        }
        if (this.hasStung()) {
            return false;
        }
        boolean bl2 = bl = this.ticksSincePollination > 3600;
        if (bl || this.hasNectar() || !this.world.isDaylight() || this.world.isRaining()) {
            BlockEntity blockEntity = this.world.getBlockEntity(this.hivePos);
            return blockEntity instanceof BeeHiveBlockEntity && !((BeeHiveBlockEntity)blockEntity).method_23280();
        }
        return false;
    }

    public void setCannotEnterHiveTicks(int i) {
        this.cannotEnterHiveTicks = i;
    }

    @Environment(value=EnvType.CLIENT)
    public float getBodyPitch(float f) {
        return MathHelper.lerp(f, this.lastPitch, this.currentPitch);
    }

    private void updateBodyPitch() {
        this.lastPitch = this.currentPitch;
        this.currentPitch = this.isNearTarget() ? Math.min(1.0f, this.currentPitch + 0.2f) : Math.max(0.0f, this.currentPitch - 0.24f);
    }

    @Override
    public void setAttacker(@Nullable LivingEntity livingEntity) {
        super.setAttacker(livingEntity);
        if (livingEntity != null) {
            this.targetPlayer = livingEntity.getUuid();
        }
    }

    @Override
    protected void mobTick() {
        boolean bl = this.hasStung();
        this.field_21509 = this.isInsideWaterOrBubbleColumn() ? ++this.field_21509 : 0;
        if (this.field_21509 > 20) {
            this.damage(DamageSource.DROWN, 1.0f);
        }
        if (bl) {
            ++this.ticksSinceSting;
            if (this.ticksSinceSting % 5 == 0 && this.random.nextInt(MathHelper.clamp(1200 - this.ticksSinceSting, 1, 1200)) == 0) {
                this.damage(DamageSource.GENERIC, this.getHealth());
            }
        }
        if (this.isAngry()) {
            int i = this.getAnger();
            this.setAnger(i - 1);
            LivingEntity livingEntity = this.getTarget();
            if (i == 0 && livingEntity != null) {
                this.setBeeAttacker(livingEntity);
            }
        }
        if (!this.hasNectar()) {
            ++this.ticksSincePollination;
        }
    }

    public void resetPollinationTicks() {
        this.ticksSincePollination = 0;
    }

    public boolean isAngry() {
        return this.getAnger() > 0;
    }

    public int getAnger() {
        return this.dataTracker.get(anger);
    }

    public void setAnger(int i) {
        this.dataTracker.set(anger, i);
    }

    public boolean hasHive() {
        return this.hivePos != BlockPos.ORIGIN;
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugRendererInfoManager.method_23855(this);
    }

    private int getCropsGrownSincePollination() {
        return this.cropsGrownSincePollination;
    }

    public void resetCropCounter() {
        this.cropsGrownSincePollination = 0;
    }

    private void addCropCounter() {
        ++this.cropsGrownSincePollination;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient) {
            if (this.cannotEnterHiveTicks > 0) {
                --this.cannotEnterHiveTicks;
            }
            boolean bl = this.isAngry() && !this.hasStung() && this.getTarget() != null && this.getTarget().squaredDistanceTo(this) < 4.0;
            this.setNearTarget(bl);
            if (this.age % 20 == 0 && !this.isHiveValid()) {
                this.hivePos = BlockPos.ORIGIN;
            }
        }
    }

    private boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        }
        BlockEntity blockEntity = this.world.getBlockEntity(this.hivePos);
        return blockEntity != null && blockEntity.getType() == BlockEntityType.BEEHIVE;
    }

    public boolean hasNectar() {
        return this.getBeeFlag(8);
    }

    public void setHasNectar(boolean bl) {
        this.setBeeFlag(8, bl);
    }

    public boolean hasStung() {
        return this.getBeeFlag(4);
    }

    public void setHasStung(boolean bl) {
        this.setBeeFlag(4, bl);
    }

    public boolean isNearTarget() {
        return this.getBeeFlag(2);
    }

    public void setNearTarget(boolean bl) {
        this.setBeeFlag(2, bl);
    }

    private void setBeeFlag(int i, boolean bl) {
        if (bl) {
            this.dataTracker.set(multipleByteTracker, (byte)(this.dataTracker.get(multipleByteTracker) | i));
        } else {
            this.dataTracker.set(multipleByteTracker, (byte)(this.dataTracker.get(multipleByteTracker) & ~i));
        }
    }

    private boolean getBeeFlag(int i) {
        return (this.dataTracker.get(multipleByteTracker) & i) != 0;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributes().register(EntityAttributes.FLYING_SPEED);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttributeInstance(EntityAttributes.FLYING_SPEED).setBaseValue(0.6f);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3f);
        this.getAttributes().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world){

            @Override
            public boolean isValidPosition(BlockPos blockPos) {
                return !this.world.getBlockState(blockPos.method_10074()).isAir();
            }

            @Override
            public void tick() {
                if (BeeEntity.this.field_21079.method_23346()) {
                    return;
                }
                super.tick();
            }
        };
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(false);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack.getItem().isIn(ItemTags.FLOWERS);
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_BEE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BEE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    public BeeEntity method_21771(PassiveEntity passiveEntity) {
        return EntityType.BEE.create(this.world);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
        if (this.isBaby()) {
            return entityDimensions.height * 0.5f;
        }
        return entityDimensions.height * 0.5f;
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        return false;
    }

    @Override
    protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
    }

    @Override
    protected boolean hasWings() {
        return true;
    }

    public void onHoneyDelivered() {
        this.setHasNectar(false);
        this.resetCropCounter();
    }

    public boolean setBeeAttacker(Entity entity) {
        this.setAnger(400 + this.random.nextInt(400));
        if (entity instanceof LivingEntity) {
            this.setAttacker((LivingEntity)entity);
        }
        return true;
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        Entity entity = damageSource.getAttacker();
        if (!this.world.isClient && entity instanceof PlayerEntity && !((PlayerEntity)entity).isCreative() && this.canSee(entity) && !this.isAiDisabled()) {
            this.field_21079.method_23748();
            this.setBeeAttacker(entity);
        }
        return super.damage(damageSource, f);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    @Override
    protected void swimUpward(Tag<Fluid> tag) {
        this.setVelocity(this.getVelocity().add(0.0, 0.01, 0.0));
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity passiveEntity) {
        return this.method_21771(passiveEntity);
    }

    class EnterHiveGoal
    extends NotAngryGoal {
        private EnterHiveGoal() {
        }

        @Override
        public boolean canBeeStart() {
            BlockEntity blockEntity;
            if (!BeeEntity.this.canEnterHive()) {
                return false;
            }
            BlockPos blockPos = new BlockPos(BeeEntity.this);
            if (BeeEntity.this.hivePos.getSquaredDistance(blockPos) < 4.0 && (blockEntity = BeeEntity.this.world.getBlockEntity(BeeEntity.this.hivePos)) instanceof BeeHiveBlockEntity) {
                BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
                if (beeHiveBlockEntity.isFullOfBees()) {
                    BeeEntity.this.hivePos = BlockPos.ORIGIN;
                } else {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canBeeContinue() {
            return false;
        }

        @Override
        public void start() {
            BlockEntity blockEntity = BeeEntity.this.world.getBlockEntity(BeeEntity.this.hivePos);
            if (blockEntity instanceof BeeHiveBlockEntity) {
                BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
                beeHiveBlockEntity.tryEnterHive(BeeEntity.this, BeeEntity.this.hasNectar());
            }
        }
    }

    class StingGoal
    extends MeleeAttackGoal {
        public StingGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl) {
            super(mobEntityWithAi, d, bl);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && BeeEntity.this.isAngry() && !BeeEntity.this.hasStung();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && BeeEntity.this.isAngry() && !BeeEntity.this.hasStung();
        }
    }

    class GrowCropsGoal
    extends NotAngryGoal {
        private GrowCropsGoal() {
        }

        @Override
        public boolean canBeeStart() {
            if (BeeEntity.this.getCropsGrownSincePollination() >= 10) {
                return false;
            }
            if (BeeEntity.this.random.nextFloat() < 0.3f) {
                return false;
            }
            return BeeEntity.this.hasNectar() && BeeEntity.this.isHiveValid();
        }

        @Override
        public boolean canBeeContinue() {
            return this.canBeeStart();
        }

        @Override
        public void tick() {
            if (BeeEntity.this.random.nextInt(30) != 0) {
                return;
            }
            for (int i = 1; i <= 2; ++i) {
                int j;
                BlockPos blockPos = new BlockPos(BeeEntity.this).down(i);
                BlockState blockState = BeeEntity.this.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                boolean bl = false;
                IntProperty intProperty = null;
                if (!block.matches(BlockTags.BEE_GROWABLES)) continue;
                if (block instanceof CropBlock) {
                    CropBlock cropBlock = (CropBlock)block;
                    if (!cropBlock.isMature(blockState)) {
                        bl = true;
                        intProperty = cropBlock.getAgeProperty();
                    }
                } else if (block instanceof StemBlock) {
                    int j2 = blockState.get(StemBlock.AGE);
                    if (j2 < 7) {
                        bl = true;
                        intProperty = StemBlock.AGE;
                    }
                } else if (block == Blocks.SWEET_BERRY_BUSH && (j = blockState.get(SweetBerryBushBlock.AGE).intValue()) < 3) {
                    bl = true;
                    intProperty = SweetBerryBushBlock.AGE;
                }
                if (!bl) continue;
                BeeEntity.this.world.playLevelEvent(2005, blockPos, 0);
                BeeEntity.this.world.setBlockState(blockPos, (BlockState)blockState.with(intProperty, blockState.get(intProperty) + 1));
                BeeEntity.this.addCropCounter();
            }
        }
    }

    class FindHiveGoal
    extends NotAngryGoal {
        private FindHiveGoal() {
        }

        @Override
        public boolean canBeeStart() {
            return BeeEntity.this.age % 10 == 0 && !BeeEntity.this.hasHive();
        }

        @Override
        public boolean canBeeContinue() {
            return false;
        }

        @Override
        public void start() {
            Stream<BlockPos> stream = this.method_23742(20);
            Optional<BlockPos> optional = stream.filter(blockPos -> {
                BlockEntity blockEntity = BeeEntity.this.world.getBlockEntity((BlockPos)blockPos);
                if (blockEntity instanceof BeeHiveBlockEntity && !((BeeHiveBlockEntity)blockEntity).isFullOfBees()) {
                    Path path = BeeEntity.this.getNavigation().findPathTo((BlockPos)blockPos, 20);
                    return path != null;
                }
                return false;
            }).findFirst();
            optional.ifPresent(blockPos -> BeeEntity.this.hivePos = blockPos);
        }

        private Stream<BlockPos> method_23742(int i) {
            BlockPos blockPos = new BlockPos(BeeEntity.this);
            if (BeeEntity.this.world instanceof ServerWorld) {
                Stream<PointOfInterest> stream = ((ServerWorld)BeeEntity.this.world).getPointOfInterestStorage().get(pointOfInterestType -> pointOfInterestType == PointOfInterestType.BEEHIVE || pointOfInterestType == PointOfInterestType.BEE_NEST, blockPos, i, PointOfInterestStorage.OccupationStatus.ANY);
                return stream.map(PointOfInterest::getPos);
            }
            return Stream.empty();
        }
    }

    class PollinateGoal
    extends NotAngryGoal {
        private final Predicate<BlockState> flowerPredicate;
        private int pollinationTicks;
        private int lastPollinationTick;
        private boolean field_21080;
        private Vec3d field_21511;

        public PollinateGoal() {
            this.flowerPredicate = blockState -> {
                if (blockState.matches(BlockTags.TALL_FLOWERS)) {
                    if (blockState.getBlock() == Blocks.SUNFLOWER) {
                        return blockState.get(TallPlantBlock.HALF) == DoubleBlockHalf.UPPER;
                    }
                    return true;
                }
                return blockState.matches(BlockTags.SMALL_FLOWERS);
            };
            this.pollinationTicks = 0;
            this.lastPollinationTick = 0;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canBeeStart() {
            if (BeeEntity.this.hasNectar()) {
                return false;
            }
            if (BeeEntity.this.world.isRaining()) {
                return false;
            }
            if (BeeEntity.this.random.nextFloat() < 0.7f) {
                return false;
            }
            Optional<BlockPos> optional = this.getFlower();
            if (optional.isPresent()) {
                BeeEntity.this.flowerPos = optional.get();
                BeeEntity.this.getNavigation().startMovingTo((double)BeeEntity.this.flowerPos.getX() + 0.5, (double)BeeEntity.this.flowerPos.getY() + 0.5, (double)BeeEntity.this.flowerPos.getZ() + 0.5, 1.2f);
                return true;
            }
            return false;
        }

        @Override
        public boolean canBeeContinue() {
            if (!this.field_21080) {
                return false;
            }
            if (BeeEntity.this.world.isRaining()) {
                return false;
            }
            if (this.completedPollination()) {
                return BeeEntity.this.random.nextFloat() < 0.2f;
            }
            if (BeeEntity.this.age % 20 == 0) {
                return BeeEntity.this.world.canSetBlock(BeeEntity.this.flowerPos) && BeeEntity.this.world.getBlockState(BeeEntity.this.flowerPos).getBlock().matches(BlockTags.FLOWERS) && new BlockPos(BeeEntity.this).isWithinDistance(BeeEntity.this.flowerPos, 2.0);
            }
            return true;
        }

        private boolean completedPollination() {
            return this.pollinationTicks > 400;
        }

        private boolean method_23346() {
            return this.field_21080;
        }

        private void method_23748() {
            this.field_21080 = false;
        }

        @Override
        public void start() {
            this.pollinationTicks = 0;
            this.lastPollinationTick = 0;
            this.field_21080 = true;
        }

        @Override
        public void stop() {
            if (this.completedPollination()) {
                BeeEntity.this.setHasNectar(true);
            }
            this.field_21080 = false;
        }

        @Override
        public void tick() {
            Vec3d vec3d = new Vec3d(BeeEntity.this.flowerPos).add(0.5, 0.6f, 0.5);
            if (vec3d.distanceTo(BeeEntity.this.getPos()) > 1.0) {
                this.field_21511 = vec3d;
                this.method_23749();
                return;
            }
            if (this.field_21511 == null) {
                this.field_21511 = vec3d;
            }
            boolean bl = BeeEntity.this.getPos().distanceTo(this.field_21511) <= 0.1;
            boolean bl2 = true;
            if (bl) {
                boolean bl3;
                boolean bl4 = bl3 = BeeEntity.this.random.nextInt(100) == 0;
                if (bl3) {
                    this.field_21511 = new Vec3d(vec3d.getX() + (double)this.method_23750(), vec3d.getY(), vec3d.getZ() + (double)this.method_23750());
                    BeeEntity.this.getNavigation().stop();
                } else {
                    bl2 = false;
                }
                BeeEntity.this.getLookControl().lookAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
            }
            if (bl2) {
                this.method_23749();
            }
            ++this.pollinationTicks;
            if (BeeEntity.this.random.nextFloat() < 0.05f && this.pollinationTicks > this.lastPollinationTick + 60) {
                this.lastPollinationTick = this.pollinationTicks;
                BeeEntity.this.playSound(SoundEvents.ENTITY_BEE_POLLINATE, 1.0f, 1.0f);
            }
        }

        private void method_23749() {
            BeeEntity.this.getMoveControl().moveTo(this.field_21511.getX(), this.field_21511.getY(), this.field_21511.getZ(), 0.35f);
        }

        private float method_23750() {
            return (BeeEntity.this.random.nextFloat() * 2.0f - 1.0f) * 0.33333334f;
        }

        private Optional<BlockPos> getFlower() {
            return this.findFlower(this.flowerPredicate, 5.0);
        }

        private Optional<BlockPos> findFlower(Predicate<BlockState> predicate, double d) {
            BlockPos blockPos = new BlockPos(BeeEntity.this);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            int i = 0;
            while ((double)i <= d) {
                int j = 0;
                while ((double)j < d) {
                    int k = 0;
                    while (k <= j) {
                        int l;
                        int n = l = k < j && k > -j ? j : 0;
                        while (l <= j) {
                            mutable.set(blockPos).setOffset(k, i - 1, l);
                            if (blockPos.getSquaredDistance(mutable) < d * d && predicate.test(BeeEntity.this.world.getBlockState(mutable))) {
                                return Optional.of(mutable);
                            }
                            l = l > 0 ? -l : 1 - l;
                        }
                        k = k > 0 ? -k : 1 - k;
                    }
                    ++j;
                }
                i = i > 0 ? -i : 1 - i;
            }
            return Optional.empty();
        }
    }

    class BeeLookControl
    extends LookControl {
        public BeeLookControl(MobEntity mobEntity) {
            super(mobEntity);
        }

        @Override
        public void tick() {
            if (BeeEntity.this.isAngry()) {
                return;
            }
            super.tick();
        }

        @Override
        protected boolean method_20433() {
            return !BeeEntity.this.field_21079.method_23346();
        }
    }

    public class MoveToFlowerGoal
    extends BeeMoveToTargetGoal {
        public MoveToFlowerGoal() {
            super(1, 48);
        }

        @Override
        public boolean canBeeStart() {
            return this.isHiveValid() && BeeEntity.this.ticksSincePollination > 3600;
        }

        @Override
        public boolean canBeeContinue() {
            return this.isHiveValid();
        }

        @Override
        public void stop() {
            if (!this.isHiveValid()) {
                BeeEntity.this.flowerPos = BlockPos.ORIGIN;
            }
        }

        @Override
        protected BlockPos getTargetPos() {
            return BeeEntity.this.flowerPos;
        }

        private boolean isHiveValid() {
            if (this.method_23741()) {
                return BeeEntity.this.world.getBlockState(this.getTargetPos()).matches(BlockTags.FLOWERS);
            }
            BeeEntity.this.flowerPos = BlockPos.ORIGIN;
            return false;
        }
    }

    class MoveToHiveGoal
    extends BeeMoveToTargetGoal {
        public MoveToHiveGoal() {
            super(2, 48);
        }

        @Override
        protected BlockPos getTargetPos() {
            return BeeEntity.this.hivePos;
        }

        @Override
        public boolean canBeeStart() {
            if (this.method_23741()) {
                return BeeEntity.this.canEnterHive();
            }
            BeeEntity.this.hivePos = BlockPos.ORIGIN;
            return false;
        }

        @Override
        public boolean canBeeContinue() {
            return this.canBeeStart();
        }
    }

    abstract class BeeMoveToTargetGoal
    extends NotAngryGoal {
        protected boolean failedToFindPath;
        private int field_21510;
        private int range;

        public BeeMoveToTargetGoal(int i, int j) {
            this.failedToFindPath = false;
            this.field_21510 = i;
            this.range = j;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        protected abstract BlockPos getTargetPos();

        @Override
        public boolean canBeeContinue() {
            return this.method_23741();
        }

        boolean method_23741() {
            BlockPos blockPos = this.getTargetPos();
            if (blockPos == BlockPos.ORIGIN) {
                return false;
            }
            double d = blockPos.getSquaredDistance(new BlockPos(BeeEntity.this));
            boolean bl = d > (double)(this.field_21510 * this.field_21510);
            boolean bl2 = d < (double)(this.range * this.range);
            return bl && bl2;
        }

        @Override
        public void tick() {
            BlockPos blockPos = this.getTargetPos();
            if (BeeEntity.this.getNavigation().isIdle()) {
                Vec3d vec3d = new Vec3d(blockPos);
                Vec3d vec3d2 = TargetFinder.method_23736(BeeEntity.this, 8, 6, vec3d, 0.3141592741012573, false);
                if (vec3d2 == null) {
                    vec3d2 = TargetFinder.findTargetTowards((MobEntityWithAi)BeeEntity.this, 3, 3, vec3d, false);
                }
                if (vec3d2 == null) {
                    this.failedToFindPath = true;
                    return;
                }
                BeeEntity.this.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, 1.0);
            }
        }
    }

    class BeeWanderAroundGoal
    extends Goal {
        public BeeWanderAroundGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return BeeEntity.this.getNavigation().isIdle() && BeeEntity.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return BeeEntity.this.getNavigation().getCurrentPath() != null && !BeeEntity.this.getNavigation().isIdle();
        }

        @Override
        public void start() {
            Vec3d vec3d = this.getRandomLocation();
            if (vec3d != null) {
                EntityNavigation entityNavigation = BeeEntity.this.getNavigation();
                entityNavigation.startMovingAlong(entityNavigation.findPathTo(new BlockPos(vec3d), 1), 1.0);
            }
        }

        @Nullable
        private Vec3d getRandomLocation() {
            Vec3d vec3d2;
            if (!BeeEntity.this.hivePos.isWithinDistance(BeeEntity.this.getPos(), 40.0) && BeeEntity.this.isHiveValid()) {
                Vec3d vec3d = new Vec3d(BeeEntity.this.hivePos);
                vec3d2 = vec3d.subtract(BeeEntity.this.getPos()).normalize();
            } else {
                vec3d2 = BeeEntity.this.getRotationVec(0.0f);
            }
            int i = 8;
            Vec3d vec3d3 = TargetFinder.method_21757(BeeEntity.this, 8, 7, vec3d2, 1.5707964f, 2, 1);
            if (vec3d3 != null) {
                return vec3d3;
            }
            return TargetFinder.method_21756(BeeEntity.this, 8, 4, -2, vec3d2, 1.5707963705062866);
        }
    }

    abstract class NotAngryGoal
    extends Goal {
        private NotAngryGoal() {
        }

        public abstract boolean canBeeStart();

        public abstract boolean canBeeContinue();

        @Override
        public boolean canStart() {
            return this.canBeeStart() && !BeeEntity.this.isAngry();
        }

        @Override
        public boolean shouldContinue() {
            return this.canBeeContinue() && !BeeEntity.this.isAngry();
        }
    }

    static class BeeFollowTargetGoal
    extends FollowTargetGoal<PlayerEntity> {
        public BeeFollowTargetGoal(BeeEntity beeEntity) {
            super((MobEntity)beeEntity, PlayerEntity.class, true);
        }

        @Override
        public boolean canStart() {
            return this.canSting() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            boolean bl = this.canSting();
            if (!bl || this.mob.getTarget() == null) {
                this.target = null;
                return false;
            }
            return super.shouldContinue();
        }

        private boolean canSting() {
            BeeEntity beeEntity = (BeeEntity)this.mob;
            return beeEntity.isAngry() && !beeEntity.hasStung();
        }
    }

    class BeeRevengeGoal
    extends RevengeGoal {
        public BeeRevengeGoal(BeeEntity beeEntity2) {
            super(beeEntity2, new Class[0]);
        }

        @Override
        protected void setMobEntityTarget(MobEntity mobEntity, LivingEntity livingEntity) {
            if (mobEntity instanceof BeeEntity && this.mob.canSee(livingEntity) && ((BeeEntity)mobEntity).setBeeAttacker(livingEntity)) {
                mobEntity.setTarget(livingEntity);
            }
        }
    }
}

