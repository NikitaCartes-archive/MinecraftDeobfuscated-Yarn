/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.ShulkerLidCollisions;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShulkerEntity
extends GolemEntity
implements Monster {
    private static final UUID COVERED_ARMOR_BONUS_ID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
    private static final EntityAttributeModifier COVERED_ARMOR_BONUS = new EntityAttributeModifier(COVERED_ARMOR_BONUS_ID, "Covered armor bonus", 20.0, EntityAttributeModifier.Operation.ADDITION);
    protected static final TrackedData<Direction> ATTACHED_FACE = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.FACING);
    protected static final TrackedData<Optional<BlockPos>> ATTACHED_BLOCK = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
    protected static final TrackedData<Byte> PEEK_AMOUNT = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Byte> COLOR = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
    private float prevOpenProgress;
    private float openProgress;
    private BlockPos prevAttachedBlock = null;
    private int teleportLerpTimer;

    public ShulkerEntity(EntityType<? extends ShulkerEntity> entityType, World world) {
        super((EntityType<? extends GolemEntity>)entityType, world);
        this.experiencePoints = 5;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(4, new ShootBulletGoal());
        this.goalSelector.add(7, new PeekGoal());
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, this.getClass()).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new SearchForPlayerGoal(this));
        this.targetSelector.add(3, new SearchForTargetGoal(this));
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SHULKER_AMBIENT;
    }

    @Override
    public void playAmbientSound() {
        if (!this.isClosed()) {
            super.playAmbientSound();
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SHULKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (this.isClosed()) {
            return SoundEvents.ENTITY_SHULKER_HURT_CLOSED;
        }
        return SoundEvents.ENTITY_SHULKER_HURT;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACHED_FACE, Direction.DOWN);
        this.dataTracker.startTracking(ATTACHED_BLOCK, Optional.empty());
        this.dataTracker.startTracking(PEEK_AMOUNT, (byte)0);
        this.dataTracker.startTracking(COLOR, (byte)16);
    }

    public static DefaultAttributeContainer.Builder createShulkerAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0);
    }

    @Override
    protected BodyControl createBodyControl() {
        return new ShulkerBodyControl(this);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.dataTracker.set(ATTACHED_FACE, Direction.byId(tag.getByte("AttachFace")));
        this.dataTracker.set(PEEK_AMOUNT, tag.getByte("Peek"));
        this.dataTracker.set(COLOR, tag.getByte("Color"));
        if (tag.contains("APX")) {
            int i = tag.getInt("APX");
            int j = tag.getInt("APY");
            int k = tag.getInt("APZ");
            this.dataTracker.set(ATTACHED_BLOCK, Optional.of(new BlockPos(i, j, k)));
        } else {
            this.dataTracker.set(ATTACHED_BLOCK, Optional.empty());
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putByte("AttachFace", (byte)this.dataTracker.get(ATTACHED_FACE).getId());
        tag.putByte("Peek", this.dataTracker.get(PEEK_AMOUNT));
        tag.putByte("Color", this.dataTracker.get(COLOR));
        BlockPos blockPos = this.getAttachedBlock();
        if (blockPos != null) {
            tag.putInt("APX", blockPos.getX());
            tag.putInt("APY", blockPos.getY());
            tag.putInt("APZ", blockPos.getZ());
        }
    }

    @Override
    public void tick() {
        float f;
        super.tick();
        BlockPos blockPos = this.dataTracker.get(ATTACHED_BLOCK).orElse(null);
        if (blockPos == null && !this.world.isClient) {
            blockPos = this.getBlockPos();
            this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
        }
        if (this.hasVehicle()) {
            blockPos = null;
            this.yaw = f = this.getVehicle().yaw;
            this.bodyYaw = f;
            this.prevBodyYaw = f;
            this.teleportLerpTimer = 0;
        } else if (!this.world.isClient) {
            Direction direction;
            BlockState blockState = this.world.getBlockState(blockPos);
            if (!blockState.isAir()) {
                if (blockState.isOf(Blocks.MOVING_PISTON)) {
                    direction = blockState.get(PistonBlock.FACING);
                    if (this.world.isAir(blockPos.offset(direction))) {
                        blockPos = blockPos.offset(direction);
                        this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
                    } else {
                        this.tryTeleport();
                    }
                } else if (blockState.isOf(Blocks.PISTON_HEAD)) {
                    direction = blockState.get(PistonHeadBlock.FACING);
                    if (this.world.isAir(blockPos.offset(direction))) {
                        blockPos = blockPos.offset(direction);
                        this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
                    } else {
                        this.tryTeleport();
                    }
                } else {
                    this.tryTeleport();
                }
            }
            if (!this.canStay(blockPos, direction = this.getAttachedFace())) {
                Direction direction2 = this.findAttachSide(blockPos);
                if (direction2 != null) {
                    this.dataTracker.set(ATTACHED_FACE, direction2);
                } else {
                    this.tryTeleport();
                }
            }
        }
        f = (float)this.getPeekAmount() * 0.01f;
        this.prevOpenProgress = this.openProgress;
        if (this.openProgress > f) {
            this.openProgress = MathHelper.clamp(this.openProgress - 0.05f, f, 1.0f);
        } else if (this.openProgress < f) {
            this.openProgress = MathHelper.clamp(this.openProgress + 0.05f, 0.0f, f);
        }
        if (blockPos != null) {
            List<Entity> list;
            if (this.world.isClient) {
                if (this.teleportLerpTimer > 0 && this.prevAttachedBlock != null) {
                    --this.teleportLerpTimer;
                } else {
                    this.prevAttachedBlock = blockPos;
                }
            }
            this.resetPosition((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5);
            double d = 0.5 - (double)MathHelper.sin((0.5f + this.openProgress) * (float)Math.PI) * 0.5;
            double e = 0.5 - (double)MathHelper.sin((0.5f + this.prevOpenProgress) * (float)Math.PI) * 0.5;
            Direction direction3 = this.getAttachedFace().getOpposite();
            this.setBoundingBox(new Box(this.getX() - 0.5, this.getY(), this.getZ() - 0.5, this.getX() + 0.5, this.getY() + 1.0, this.getZ() + 0.5).stretch((double)direction3.getOffsetX() * d, (double)direction3.getOffsetY() * d, (double)direction3.getOffsetZ() * d));
            double g = d - e;
            if (g > 0.0 && !(list = this.world.getOtherEntities(this, this.getBoundingBox())).isEmpty()) {
                for (Entity entity : list) {
                    if (entity instanceof ShulkerEntity || entity.noClip) continue;
                    entity.move(MovementType.SHULKER, new Vec3d(g * (double)direction3.getOffsetX(), g * (double)direction3.getOffsetY(), g * (double)direction3.getOffsetZ()));
                }
            }
        }
    }

    @Override
    public void move(MovementType type, Vec3d movement) {
        if (type == MovementType.SHULKER_BOX) {
            this.tryTeleport();
        } else {
            super.move(type, movement);
        }
    }

    @Override
    public void updatePosition(double x, double y, double z) {
        super.updatePosition(x, y, z);
        if (this.dataTracker == null || this.age == 0) {
            return;
        }
        Optional<BlockPos> optional = this.dataTracker.get(ATTACHED_BLOCK);
        Optional<BlockPos> optional2 = Optional.of(new BlockPos(x, y, z));
        if (!optional2.equals(optional)) {
            this.dataTracker.set(ATTACHED_BLOCK, optional2);
            this.dataTracker.set(PEEK_AMOUNT, (byte)0);
            this.velocityDirty = true;
        }
    }

    @Nullable
    protected Direction findAttachSide(BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (!this.canStay(pos, direction)) continue;
            return direction;
        }
        return null;
    }

    private boolean canStay(BlockPos pos, Direction attachSide) {
        return this.world.isDirectionSolid(pos.offset(attachSide), this, attachSide.getOpposite()) && this.world.isSpaceEmpty(this, ShulkerLidCollisions.getLidCollisionBox(pos, attachSide.getOpposite()));
    }

    protected boolean tryTeleport() {
        if (this.isAiDisabled() || !this.isAlive()) {
            return false;
        }
        BlockPos blockPos = this.getBlockPos();
        for (int i = 0; i < 5; ++i) {
            Direction direction;
            BlockPos blockPos2 = blockPos.add(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
            if (blockPos2.getY() <= this.world.getSectionCount() || !this.world.isAir(blockPos2) || !this.world.getWorldBorder().contains(blockPos2) || !this.world.isSpaceEmpty(this, new Box(blockPos2)) || (direction = this.findAttachSide(blockPos2)) == null) continue;
            this.detach();
            this.dataTracker.set(ATTACHED_FACE, direction);
            this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0f, 1.0f);
            this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos2));
            this.dataTracker.set(PEEK_AMOUNT, (byte)0);
            this.setTarget(null);
            return true;
        }
        return false;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.setVelocity(Vec3d.ZERO);
        if (!this.isAiDisabled()) {
            this.prevBodyYaw = 0.0f;
            this.bodyYaw = 0.0f;
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        BlockPos blockPos;
        if (ATTACHED_BLOCK.equals(data) && this.world.isClient && !this.hasVehicle() && (blockPos = this.getAttachedBlock()) != null) {
            if (this.prevAttachedBlock == null) {
                this.prevAttachedBlock = blockPos;
            } else {
                this.teleportLerpTimer = 6;
            }
            this.resetPosition((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5);
        }
        super.onTrackedDataSet(data);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.bodyTrackingIncrements = 0;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity entity;
        if (this.isClosed() && (entity = source.getSource()) instanceof PersistentProjectileEntity) {
            return false;
        }
        if (super.damage(source, amount)) {
            if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5 && this.random.nextInt(4) == 0) {
                this.tryTeleport();
            } else if (source.isProjectile() && (entity = source.getSource()) != null && entity.getType() == EntityType.SHULKER_BULLET) {
                this.spawnNewShulker();
            }
            return true;
        }
        return false;
    }

    private boolean isClosed() {
        return this.getPeekAmount() == 0;
    }

    private void spawnNewShulker() {
        Vec3d vec3d = this.getPos();
        Box box = this.getBoundingBox();
        if (this.isClosed() || !this.tryTeleport()) {
            return;
        }
        int i = this.world.getEntitiesByType(EntityType.SHULKER, box.expand(8.0), Entity::isAlive).size();
        float f = (float)(i - 1) / 5.0f;
        if (this.world.random.nextFloat() < f) {
            return;
        }
        ShulkerEntity shulkerEntity = EntityType.SHULKER.create(this.world);
        DyeColor dyeColor = this.getColor();
        if (dyeColor != null) {
            shulkerEntity.setColor(dyeColor);
        }
        shulkerEntity.refreshPositionAfterTeleport(vec3d);
        this.world.spawnEntity(shulkerEntity);
    }

    @Override
    public boolean isCollidable() {
        return this.isAlive();
    }

    public Direction getAttachedFace() {
        return this.dataTracker.get(ATTACHED_FACE);
    }

    @Nullable
    public BlockPos getAttachedBlock() {
        return this.dataTracker.get(ATTACHED_BLOCK).orElse(null);
    }

    public void setAttachedBlock(@Nullable BlockPos pos) {
        this.dataTracker.set(ATTACHED_BLOCK, Optional.ofNullable(pos));
    }

    public int getPeekAmount() {
        return this.dataTracker.get(PEEK_AMOUNT).byteValue();
    }

    public void setPeekAmount(int peekAmount) {
        if (!this.world.isClient) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(COVERED_ARMOR_BONUS);
            if (peekAmount == 0) {
                this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(COVERED_ARMOR_BONUS);
                this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0f, 1.0f);
            } else {
                this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0f, 1.0f);
            }
        }
        this.dataTracker.set(PEEK_AMOUNT, (byte)peekAmount);
    }

    @Environment(value=EnvType.CLIENT)
    public float getOpenProgress(float delta) {
        return MathHelper.lerp(delta, this.prevOpenProgress, this.openProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public int getTeleportLerpTimer() {
        return this.teleportLerpTimer;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPrevAttachedBlock() {
        return this.prevAttachedBlock;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.5f;
    }

    @Override
    public int getLookPitchSpeed() {
        return 180;
    }

    @Override
    public int getBodyYawSpeed() {
        return 180;
    }

    @Override
    public void pushAwayFrom(Entity entity) {
    }

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasAttachedBlock() {
        return this.prevAttachedBlock != null && this.getAttachedBlock() != null;
    }

    public void setColor(DyeColor color) {
        this.dataTracker.set(COLOR, (byte)color.getId());
    }

    @Nullable
    public DyeColor getColor() {
        byte b = this.dataTracker.get(COLOR);
        if (b == 16 || b > 15) {
            return null;
        }
        return DyeColor.byId(b);
    }

    static class SearchForTargetGoal
    extends FollowTargetGoal<LivingEntity> {
        public SearchForTargetGoal(ShulkerEntity shulker) {
            super(shulker, LivingEntity.class, 10, true, false, entity -> entity instanceof Monster);
        }

        @Override
        public boolean canStart() {
            if (this.mob.getScoreboardTeam() == null) {
                return false;
            }
            return super.canStart();
        }

        @Override
        protected Box getSearchBox(double distance) {
            Direction direction = ((ShulkerEntity)this.mob).getAttachedFace();
            if (direction.getAxis() == Direction.Axis.X) {
                return this.mob.getBoundingBox().expand(4.0, distance, distance);
            }
            if (direction.getAxis() == Direction.Axis.Z) {
                return this.mob.getBoundingBox().expand(distance, distance, 4.0);
            }
            return this.mob.getBoundingBox().expand(distance, 4.0, distance);
        }
    }

    class SearchForPlayerGoal
    extends FollowTargetGoal<PlayerEntity> {
        public SearchForPlayerGoal(ShulkerEntity shulker) {
            super((MobEntity)shulker, PlayerEntity.class, true);
        }

        @Override
        public boolean canStart() {
            if (ShulkerEntity.this.world.getDifficulty() == Difficulty.PEACEFUL) {
                return false;
            }
            return super.canStart();
        }

        @Override
        protected Box getSearchBox(double distance) {
            Direction direction = ((ShulkerEntity)this.mob).getAttachedFace();
            if (direction.getAxis() == Direction.Axis.X) {
                return this.mob.getBoundingBox().expand(4.0, distance, distance);
            }
            if (direction.getAxis() == Direction.Axis.Z) {
                return this.mob.getBoundingBox().expand(distance, distance, 4.0);
            }
            return this.mob.getBoundingBox().expand(distance, 4.0, distance);
        }
    }

    class ShootBulletGoal
    extends Goal {
        private int counter;

        public ShootBulletGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = ShulkerEntity.this.getTarget();
            if (livingEntity == null || !livingEntity.isAlive()) {
                return false;
            }
            return ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL;
        }

        @Override
        public void start() {
            this.counter = 20;
            ShulkerEntity.this.setPeekAmount(100);
        }

        @Override
        public void stop() {
            ShulkerEntity.this.setPeekAmount(0);
        }

        @Override
        public void tick() {
            if (ShulkerEntity.this.world.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            --this.counter;
            LivingEntity livingEntity = ShulkerEntity.this.getTarget();
            ShulkerEntity.this.getLookControl().lookAt(livingEntity, 180.0f, 180.0f);
            double d = ShulkerEntity.this.squaredDistanceTo(livingEntity);
            if (d < 400.0) {
                if (this.counter <= 0) {
                    this.counter = 20 + ShulkerEntity.this.random.nextInt(10) * 20 / 2;
                    ShulkerEntity.this.world.spawnEntity(new ShulkerBulletEntity(ShulkerEntity.this.world, ShulkerEntity.this, livingEntity, ShulkerEntity.this.getAttachedFace().getAxis()));
                    ShulkerEntity.this.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0f, (ShulkerEntity.this.random.nextFloat() - ShulkerEntity.this.random.nextFloat()) * 0.2f + 1.0f);
                }
            } else {
                ShulkerEntity.this.setTarget(null);
            }
            super.tick();
        }
    }

    class PeekGoal
    extends Goal {
        private int counter;

        private PeekGoal() {
        }

        @Override
        public boolean canStart() {
            return ShulkerEntity.this.getTarget() == null && ShulkerEntity.this.random.nextInt(40) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return ShulkerEntity.this.getTarget() == null && this.counter > 0;
        }

        @Override
        public void start() {
            this.counter = 20 * (1 + ShulkerEntity.this.random.nextInt(3));
            ShulkerEntity.this.setPeekAmount(30);
        }

        @Override
        public void stop() {
            if (ShulkerEntity.this.getTarget() == null) {
                ShulkerEntity.this.setPeekAmount(0);
            }
        }

        @Override
        public void tick() {
            --this.counter;
        }
    }

    class ShulkerBodyControl
    extends BodyControl {
        public ShulkerBodyControl(MobEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
        }
    }
}

