/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.PowderSnowJumpGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class RabbitEntity
extends AnimalEntity
implements VariantHolder<RabbitType> {
    public static final double field_30356 = 0.6;
    public static final double field_30357 = 0.8;
    public static final double field_30358 = 1.0;
    public static final double ESCAPE_SPEED = 2.2;
    public static final double field_30360 = 1.4;
    private static final TrackedData<Integer> RABBIT_TYPE = DataTracker.registerData(RabbitEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final Identifier KILLER_BUNNY = new Identifier("killer_bunny");
    public static final int field_30368 = 8;
    public static final int field_30369 = 8;
    private static final int field_30370 = 40;
    private int jumpTicks;
    private int jumpDuration;
    private boolean lastOnGround;
    private int ticksUntilJump;
    int moreCarrotTicks;

    public RabbitEntity(EntityType<? extends RabbitEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
        this.jumpControl = new RabbitJumpControl(this);
        this.moveControl = new RabbitMoveControl(this);
        this.setSpeed(0.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new PowderSnowJumpGoal(this, this.world));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(3, new TemptGoal(this, 1.0, Ingredient.ofItems(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
        this.goalSelector.add(4, new FleeGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 2.2, 2.2));
        this.goalSelector.add(4, new FleeGoal<WolfEntity>(this, WolfEntity.class, 10.0f, 2.2, 2.2));
        this.goalSelector.add(4, new FleeGoal<HostileEntity>(this, HostileEntity.class, 4.0f, 2.2, 2.2));
        this.goalSelector.add(5, new EatCarrotCropGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
    }

    @Override
    protected float getJumpVelocity() {
        if (this.horizontalCollision || this.moveControl.isMoving() && this.moveControl.getTargetY() > this.getY() + 0.5) {
            return 0.5f;
        }
        Path path = this.navigation.getCurrentPath();
        if (path != null && !path.isFinished()) {
            Vec3d vec3d = path.getNodePosition(this);
            if (vec3d.y > this.getY() + 0.5) {
                return 0.5f;
            }
        }
        if (this.moveControl.getSpeed() <= 0.6) {
            return 0.2f;
        }
        return 0.3f;
    }

    @Override
    protected void jump() {
        double e;
        super.jump();
        double d = this.moveControl.getSpeed();
        if (d > 0.0 && (e = this.getVelocity().horizontalLengthSquared()) < 0.01) {
            this.updateVelocity(0.1f, new Vec3d(0.0, 0.0, 1.0));
        }
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, EntityStatuses.ADD_SPRINTING_PARTICLES_OR_RESET_SPAWNER_MINECART_SPAWN_DELAY);
        }
    }

    public float getJumpProgress(float delta) {
        if (this.jumpDuration == 0) {
            return 0.0f;
        }
        return ((float)this.jumpTicks + delta) / (float)this.jumpDuration;
    }

    public void setSpeed(double speed) {
        this.getNavigation().setSpeed(speed);
        this.moveControl.moveTo(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ(), speed);
    }

    @Override
    public void setJumping(boolean jumping) {
        super.setJumping(jumping);
        if (jumping) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 0.8f);
        }
    }

    public void startJump() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RABBIT_TYPE, RabbitType.BROWN.id);
    }

    @Override
    public void mobTick() {
        if (this.ticksUntilJump > 0) {
            --this.ticksUntilJump;
        }
        if (this.moreCarrotTicks > 0) {
            this.moreCarrotTicks -= this.random.nextInt(3);
            if (this.moreCarrotTicks < 0) {
                this.moreCarrotTicks = 0;
            }
        }
        if (this.onGround) {
            RabbitJumpControl rabbitJumpControl;
            LivingEntity livingEntity;
            if (!this.lastOnGround) {
                this.setJumping(false);
                this.scheduleJump();
            }
            if (this.getVariant() == RabbitType.EVIL && this.ticksUntilJump == 0 && (livingEntity = this.getTarget()) != null && this.squaredDistanceTo(livingEntity) < 16.0) {
                this.lookTowards(livingEntity.getX(), livingEntity.getZ());
                this.moveControl.moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), this.moveControl.getSpeed());
                this.startJump();
                this.lastOnGround = true;
            }
            if (!(rabbitJumpControl = (RabbitJumpControl)this.jumpControl).isActive()) {
                if (this.moveControl.isMoving() && this.ticksUntilJump == 0) {
                    Path path = this.navigation.getCurrentPath();
                    Vec3d vec3d = new Vec3d(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ());
                    if (path != null && !path.isFinished()) {
                        vec3d = path.getNodePosition(this);
                    }
                    this.lookTowards(vec3d.x, vec3d.z);
                    this.startJump();
                }
            } else if (!rabbitJumpControl.canJump()) {
                this.enableJump();
            }
        }
        this.lastOnGround = this.onGround;
    }

    @Override
    public boolean shouldSpawnSprintingParticles() {
        return false;
    }

    private void lookTowards(double x, double z) {
        this.setYaw((float)(MathHelper.atan2(z - this.getZ(), x - this.getX()) * 57.2957763671875) - 90.0f);
    }

    private void enableJump() {
        ((RabbitJumpControl)this.jumpControl).setCanJump(true);
    }

    private void disableJump() {
        ((RabbitJumpControl)this.jumpControl).setCanJump(false);
    }

    private void doScheduleJump() {
        this.ticksUntilJump = this.moveControl.getSpeed() < 2.2 ? 10 : 1;
    }

    private void scheduleJump() {
        this.doScheduleJump();
        this.disableJump();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
    }

    public static DefaultAttributeContainer.Builder createRabbitAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 3.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("RabbitType", this.getVariant().id);
        nbt.putInt("MoreCarrotTicks", this.moreCarrotTicks);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(RabbitType.byId(nbt.getInt("RabbitType")));
        this.moreCarrotTicks = nbt.getInt("MoreCarrotTicks");
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_RABBIT_JUMP;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (this.getVariant() == RabbitType.EVIL) {
            this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            return target.damage(DamageSource.mob(this), 8.0f);
        }
        return target.damage(DamageSource.mob(this), 3.0f);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return this.getVariant() == RabbitType.EVIL ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
    }

    private static boolean isTempting(ItemStack stack) {
        return stack.isOf(Items.CARROT) || stack.isOf(Items.GOLDEN_CARROT) || stack.isOf(Blocks.DANDELION.asItem());
    }

    /*
     * Unable to fully structure code
     */
    @Override
    @Nullable
    public RabbitEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        block2: {
            block3: {
                rabbitEntity = EntityType.RABBIT.create(serverWorld);
                if (rabbitEntity == null) break block2;
                rabbitType = RabbitEntity.getTypeFromPos(serverWorld, this.getBlockPos());
                if (this.random.nextInt(20) == 0) break block3;
                if (!(passiveEntity instanceof RabbitEntity)) ** GOTO lbl-1000
                rabbitEntity2 = (RabbitEntity)passiveEntity;
                if (this.random.nextBoolean()) {
                    rabbitType = rabbitEntity2.getVariant();
                } else lbl-1000:
                // 2 sources

                {
                    rabbitType = this.getVariant();
                }
            }
            rabbitEntity.setVariant(rabbitType);
        }
        return rabbitEntity;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return RabbitEntity.isTempting(stack);
    }

    @Override
    public RabbitType getVariant() {
        return RabbitType.byId(this.dataTracker.get(RABBIT_TYPE));
    }

    @Override
    public void setVariant(RabbitType rabbitType) {
        if (rabbitType == RabbitType.EVIL) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(8.0);
            this.goalSelector.add(4, new RabbitAttackGoal(this));
            this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
            this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
            this.targetSelector.add(2, new ActiveTargetGoal<WolfEntity>((MobEntity)this, WolfEntity.class, true));
            if (!this.hasCustomName()) {
                this.setCustomName(Text.translatable(Util.createTranslationKey("entity", KILLER_BUNNY)));
            }
        }
        this.dataTracker.set(RABBIT_TYPE, rabbitType.id);
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        RabbitType rabbitType = RabbitEntity.getTypeFromPos(world, this.getBlockPos());
        if (entityData instanceof RabbitData) {
            rabbitType = ((RabbitData)entityData).type;
        } else {
            entityData = new RabbitData(rabbitType);
        }
        this.setVariant(rabbitType);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    private static RabbitType getTypeFromPos(WorldAccess world, BlockPos pos) {
        RegistryEntry<Biome> registryEntry = world.getBiome(pos);
        int i = world.getRandom().nextInt(100);
        if (registryEntry.isIn(BiomeTags.SPAWNS_WHITE_RABBITS)) {
            return i < 80 ? RabbitType.WHITE : RabbitType.WHITE_SPLOTCHED;
        }
        if (registryEntry.isIn(BiomeTags.SPAWNS_GOLD_RABBITS)) {
            return RabbitType.GOLD;
        }
        return i < 50 ? RabbitType.BROWN : (i < 90 ? RabbitType.SALT : RabbitType.BLACK);
    }

    public static boolean canSpawn(EntityType<RabbitEntity> entity, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getBlockState(pos.down()).isIn(BlockTags.RABBITS_SPAWNABLE_ON) && RabbitEntity.isLightLevelValidForNaturalSpawn(world, pos);
    }

    boolean wantsCarrots() {
        return this.moreCarrotTicks <= 0;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.ADD_SPRINTING_PARTICLES_OR_RESET_SPAWNER_MINECART_SPAWN_DELAY) {
            this.spawnSprintingParticles();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0, 0.6f * this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }

    @Override
    @Nullable
    public /* synthetic */ PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return this.createChild(world, entity);
    }

    @Override
    public /* synthetic */ Object getVariant() {
        return this.getVariant();
    }

    public static class RabbitJumpControl
    extends JumpControl {
        private final RabbitEntity rabbit;
        private boolean canJump;

        public RabbitJumpControl(RabbitEntity rabbit) {
            super(rabbit);
            this.rabbit = rabbit;
        }

        public boolean isActive() {
            return this.active;
        }

        public boolean canJump() {
            return this.canJump;
        }

        public void setCanJump(boolean canJump) {
            this.canJump = canJump;
        }

        @Override
        public void tick() {
            if (this.active) {
                this.rabbit.startJump();
                this.active = false;
            }
        }
    }

    static class RabbitMoveControl
    extends MoveControl {
        private final RabbitEntity rabbit;
        private double rabbitSpeed;

        public RabbitMoveControl(RabbitEntity owner) {
            super(owner);
            this.rabbit = owner;
        }

        @Override
        public void tick() {
            if (this.rabbit.onGround && !this.rabbit.jumping && !((RabbitJumpControl)this.rabbit.jumpControl).isActive()) {
                this.rabbit.setSpeed(0.0);
            } else if (this.isMoving()) {
                this.rabbit.setSpeed(this.rabbitSpeed);
            }
            super.tick();
        }

        @Override
        public void moveTo(double x, double y, double z, double speed) {
            if (this.rabbit.isTouchingWater()) {
                speed = 1.5;
            }
            super.moveTo(x, y, z, speed);
            if (speed > 0.0) {
                this.rabbitSpeed = speed;
            }
        }
    }

    static class EscapeDangerGoal
    extends net.minecraft.entity.ai.goal.EscapeDangerGoal {
        private final RabbitEntity rabbit;

        public EscapeDangerGoal(RabbitEntity rabbit, double speed) {
            super(rabbit, speed);
            this.rabbit = rabbit;
        }

        @Override
        public void tick() {
            super.tick();
            this.rabbit.setSpeed(this.speed);
        }
    }

    static class FleeGoal<T extends LivingEntity>
    extends FleeEntityGoal<T> {
        private final RabbitEntity rabbit;

        public FleeGoal(RabbitEntity rabbit, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
            super(rabbit, fleeFromType, distance, slowSpeed, fastSpeed);
            this.rabbit = rabbit;
        }

        @Override
        public boolean canStart() {
            return this.rabbit.getVariant() != RabbitType.EVIL && super.canStart();
        }
    }

    static class EatCarrotCropGoal
    extends MoveToTargetPosGoal {
        private final RabbitEntity rabbit;
        private boolean wantsCarrots;
        private boolean hasTarget;

        public EatCarrotCropGoal(RabbitEntity rabbit) {
            super(rabbit, 0.7f, 16);
            this.rabbit = rabbit;
        }

        @Override
        public boolean canStart() {
            if (this.cooldown <= 0) {
                if (!this.rabbit.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    return false;
                }
                this.hasTarget = false;
                this.wantsCarrots = this.rabbit.wantsCarrots();
            }
            return super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return this.hasTarget && super.shouldContinue();
        }

        @Override
        public void tick() {
            super.tick();
            this.rabbit.getLookControl().lookAt((double)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (double)this.targetPos.getZ() + 0.5, 10.0f, this.rabbit.getMaxLookPitchChange());
            if (this.hasReached()) {
                World world = this.rabbit.world;
                BlockPos blockPos = this.targetPos.up();
                BlockState blockState = world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (this.hasTarget && block instanceof CarrotsBlock) {
                    int i = blockState.get(CarrotsBlock.AGE);
                    if (i == 0) {
                        world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                        world.breakBlock(blockPos, true, this.rabbit);
                    } else {
                        world.setBlockState(blockPos, (BlockState)blockState.with(CarrotsBlock.AGE, i - 1), Block.NOTIFY_LISTENERS);
                        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
                    }
                    this.rabbit.moreCarrotTicks = 40;
                }
                this.hasTarget = false;
                this.cooldown = 10;
            }
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            BlockState blockState = world.getBlockState(pos);
            if (blockState.isOf(Blocks.FARMLAND) && this.wantsCarrots && !this.hasTarget && (blockState = world.getBlockState(pos.up())).getBlock() instanceof CarrotsBlock && ((CarrotsBlock)blockState.getBlock()).isMature(blockState)) {
                this.hasTarget = true;
                return true;
            }
            return false;
        }
    }

    public static enum RabbitType implements StringIdentifiable
    {
        BROWN(0, "brown"),
        WHITE(1, "white"),
        BLACK(2, "black"),
        WHITE_SPLOTCHED(3, "white_splotched"),
        GOLD(4, "gold"),
        SALT(5, "salt"),
        EVIL(99, "evil");

        private static final IntFunction<RabbitType> BY_ID;
        public static final Codec<RabbitType> CODEC;
        final int id;
        private final String name;

        private RabbitType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }

        public static RabbitType byId(int id) {
            return BY_ID.apply(id);
        }

        static {
            BY_ID = ValueLists.createIdToValueFunction(RabbitType::getId, RabbitType.values(), BROWN);
            CODEC = StringIdentifiable.createCodec(RabbitType::values);
        }
    }

    static class RabbitAttackGoal
    extends MeleeAttackGoal {
        public RabbitAttackGoal(RabbitEntity rabbit) {
            super(rabbit, 1.4, true);
        }

        @Override
        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            return 4.0f + entity.getWidth();
        }
    }

    public static class RabbitData
    extends PassiveEntity.PassiveData {
        public final RabbitType type;

        public RabbitData(RabbitType type) {
            super(1.0f);
            this.type = type;
        }
    }
}

