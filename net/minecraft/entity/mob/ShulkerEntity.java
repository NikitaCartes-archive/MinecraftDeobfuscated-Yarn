/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
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
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ShulkerEntity
extends GolemEntity
implements Monster {
    private static final UUID COVERED_ARMOR_BONUS_ID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
    private static final EntityAttributeModifier COVERED_ARMOR_BONUS = new EntityAttributeModifier(COVERED_ARMOR_BONUS_ID, "Covered armor bonus", 20.0, EntityAttributeModifier.Operation.ADDITION);
    protected static final TrackedData<Direction> ATTACHED_FACE = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.FACING);
    protected static final TrackedData<Byte> PEEK_AMOUNT = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Byte> COLOR = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final int field_30487 = 6;
    private static final byte field_30488 = 16;
    private static final byte field_30489 = 16;
    private static final int field_30490 = 8;
    private static final int field_30491 = 8;
    private static final int field_30492 = 5;
    private static final float field_30493 = 0.05f;
    static final Vec3f SOUTH_VECTOR = Util.make(() -> {
        Vec3i vec3i = Direction.SOUTH.getVector();
        return new Vec3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    });
    private float prevOpenProgress;
    private float openProgress;
    @Nullable
    private BlockPos prevAttachedBlock;
    private int teleportLerpTimer;
    private static final float field_30494 = 1.0f;

    public ShulkerEntity(EntityType<? extends ShulkerEntity> entityType, World world) {
        super((EntityType<? extends GolemEntity>)entityType, world);
        this.experiencePoints = 5;
        this.lookControl = new ShulkerLookControl(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f, 0.02f, true));
        this.goalSelector.add(4, new ShootBulletGoal());
        this.goalSelector.add(7, new PeekGoal());
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, this.getClass()).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new TargetPlayerGoal(this));
        this.targetSelector.add(3, new TargetOtherTeamGoal(this));
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
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
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setAttachedFace(Direction.byId(nbt.getByte("AttachFace")));
        this.dataTracker.set(PEEK_AMOUNT, nbt.getByte("Peek"));
        if (nbt.contains("Color", 99)) {
            this.dataTracker.set(COLOR, nbt.getByte("Color"));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("AttachFace", (byte)this.getAttachedFace().getId());
        nbt.putByte("Peek", this.dataTracker.get(PEEK_AMOUNT));
        nbt.putByte("Color", this.dataTracker.get(COLOR));
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.world.isClient || this.hasVehicle() || this.canStay(this.getBlockPos(), this.getAttachedFace()))) {
            this.tryAttachOrTeleport();
        }
        if (this.tickOpenProgress()) {
            this.moveEntities();
        }
        if (this.world.isClient) {
            if (this.teleportLerpTimer > 0) {
                --this.teleportLerpTimer;
            } else {
                this.prevAttachedBlock = null;
            }
        }
    }

    private void tryAttachOrTeleport() {
        Direction direction = this.findAttachSide(this.getBlockPos());
        if (direction != null) {
            this.setAttachedFace(direction);
        } else {
            this.tryTeleport();
        }
    }

    @Override
    protected Box calculateBoundingBox() {
        float f = ShulkerEntity.method_33342(this.openProgress);
        Direction direction = this.getAttachedFace().getOpposite();
        float g = this.getType().getWidth() / 2.0f;
        return ShulkerEntity.method_33346(direction, f).offset(this.getX() - (double)g, this.getY(), this.getZ() - (double)g);
    }

    private static float method_33342(float f) {
        return 0.5f - MathHelper.sin((0.5f + f) * (float)Math.PI) * 0.5f;
    }

    private boolean tickOpenProgress() {
        this.prevOpenProgress = this.openProgress;
        float f = (float)this.getPeekAmount() * 0.01f;
        if (this.openProgress == f) {
            return false;
        }
        this.openProgress = this.openProgress > f ? MathHelper.clamp(this.openProgress - 0.05f, f, 1.0f) : MathHelper.clamp(this.openProgress + 0.05f, 0.0f, f);
        return true;
    }

    private void moveEntities() {
        this.refreshPosition();
        float f = ShulkerEntity.method_33342(this.openProgress);
        float g = ShulkerEntity.method_33342(this.prevOpenProgress);
        Direction direction = this.getAttachedFace().getOpposite();
        float h = f - g;
        if (h <= 0.0f) {
            return;
        }
        List<Entity> list = this.world.getOtherEntities(this, ShulkerEntity.method_33347(direction, g, f).offset(this.getX() - 0.5, this.getY(), this.getZ() - 0.5), EntityPredicates.EXCEPT_SPECTATOR.and(entity -> !entity.isConnectedThroughVehicle(this)));
        for (Entity entity2 : list) {
            if (entity2 instanceof ShulkerEntity || entity2.noClip) continue;
            entity2.move(MovementType.SHULKER, new Vec3d(h * (float)direction.getOffsetX(), h * (float)direction.getOffsetY(), h * (float)direction.getOffsetZ()));
        }
    }

    public static Box method_33346(Direction direction, float f) {
        return ShulkerEntity.method_33347(direction, -1.0f, f);
    }

    public static Box method_33347(Direction direction, float f, float g) {
        double d = Math.max(f, g);
        double e = Math.min(f, g);
        return new Box(BlockPos.ORIGIN).stretch((double)direction.getOffsetX() * d, (double)direction.getOffsetY() * d, (double)direction.getOffsetZ() * d).shrink((double)(-direction.getOffsetX()) * (1.0 + e), (double)(-direction.getOffsetY()) * (1.0 + e), (double)(-direction.getOffsetZ()) * (1.0 + e));
    }

    @Override
    public double getHeightOffset() {
        EntityType<?> entityType = this.getVehicle().getType();
        if (entityType == EntityType.BOAT || entityType == EntityType.MINECART) {
            return 0.1875 - this.getVehicle().getMountedHeightOffset();
        }
        return super.getHeightOffset();
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        if (this.world.isClient()) {
            this.prevAttachedBlock = null;
            this.teleportLerpTimer = 0;
        }
        this.setAttachedFace(Direction.DOWN);
        return super.startRiding(entity, force);
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        if (this.world.isClient) {
            this.prevAttachedBlock = this.getBlockPos();
        }
        this.prevBodyYaw = 0.0f;
        this.bodyYaw = 0.0f;
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setYaw(0.0f);
        this.headYaw = this.getYaw();
        this.resetPosition();
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        if (movementType == MovementType.SHULKER_BOX) {
            this.tryTeleport();
        } else {
            super.move(movementType, movement);
        }
    }

    @Override
    public Vec3d getVelocity() {
        return Vec3d.ZERO;
    }

    @Override
    public void setVelocity(Vec3d velocity) {
    }

    @Override
    public void setPosition(double x, double y, double z) {
        BlockPos blockPos = this.getBlockPos();
        if (this.hasVehicle()) {
            super.setPosition(x, y, z);
        } else {
            super.setPosition((double)MathHelper.floor(x) + 0.5, MathHelper.floor(y + 0.5), (double)MathHelper.floor(z) + 0.5);
        }
        if (this.age == 0) {
            return;
        }
        BlockPos blockPos2 = this.getBlockPos();
        if (!blockPos2.equals(blockPos)) {
            this.dataTracker.set(PEEK_AMOUNT, (byte)0);
            this.velocityDirty = true;
            if (this.world.isClient && !this.hasVehicle() && !blockPos2.equals(this.prevAttachedBlock)) {
                this.prevAttachedBlock = blockPos;
                this.teleportLerpTimer = 6;
                this.lastRenderX = this.getX();
                this.lastRenderY = this.getY();
                this.lastRenderZ = this.getZ();
            }
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

    boolean canStay(BlockPos pos, Direction direction) {
        if (this.method_33351(pos)) {
            return false;
        }
        Direction direction2 = direction.getOpposite();
        if (!this.world.isDirectionSolid(pos.offset(direction), this, direction2)) {
            return false;
        }
        Box box = ShulkerEntity.method_33346(direction2, 1.0f).offset(pos).contract(1.0E-6);
        return this.world.isSpaceEmpty(this, box);
    }

    private boolean method_33351(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        }
        boolean bl = blockState.isOf(Blocks.MOVING_PISTON) && pos.equals(this.getBlockPos());
        return !bl;
    }

    protected boolean tryTeleport() {
        if (this.isAiDisabled() || !this.isAlive()) {
            return false;
        }
        BlockPos blockPos = this.getBlockPos();
        for (int i = 0; i < 5; ++i) {
            Direction direction;
            BlockPos blockPos2 = blockPos.add(MathHelper.nextBetween(this.random, -8, 8), MathHelper.nextBetween(this.random, -8, 8), MathHelper.nextBetween(this.random, -8, 8));
            if (blockPos2.getY() <= this.world.getBottomY() || !this.world.isAir(blockPos2) || !this.world.getWorldBorder().contains(blockPos2) || !this.world.isSpaceEmpty(this, new Box(blockPos2).contract(1.0E-6)) || (direction = this.findAttachSide(blockPos2)) == null) continue;
            this.detach();
            this.setAttachedFace(direction);
            this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0f, 1.0f);
            this.setPosition((double)blockPos2.getX() + 0.5, blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
            this.dataTracker.set(PEEK_AMOUNT, (byte)0);
            this.setTarget(null);
            return true;
        }
        return false;
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.bodyTrackingIncrements = 0;
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
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

    private void setAttachedFace(Direction face) {
        this.dataTracker.set(ATTACHED_FACE, face);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (ATTACHED_FACE.equals(data)) {
            this.setBoundingBox(this.calculateBoundingBox());
        }
        super.onTrackedDataSet(data);
    }

    private int getPeekAmount() {
        return this.dataTracker.get(PEEK_AMOUNT).byteValue();
    }

    void setPeekAmount(int peekAmount) {
        if (!this.world.isClient) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(COVERED_ARMOR_BONUS);
            if (peekAmount == 0) {
                this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(COVERED_ARMOR_BONUS);
                this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0f, 1.0f);
                this.emitGameEvent(GameEvent.SHULKER_CLOSE);
            } else {
                this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0f, 1.0f);
                this.emitGameEvent(GameEvent.SHULKER_OPEN);
            }
        }
        this.dataTracker.set(PEEK_AMOUNT, (byte)peekAmount);
    }

    public float getOpenProgress(float delta) {
        return MathHelper.lerp(delta, this.prevOpenProgress, this.openProgress);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.5f;
    }

    @Override
    public void readFromPacket(MobSpawnS2CPacket packet) {
        super.readFromPacket(packet);
        this.bodyYaw = 0.0f;
        this.prevBodyYaw = 0.0f;
    }

    @Override
    public int getMaxLookPitchChange() {
        return 180;
    }

    @Override
    public int getMaxHeadRotation() {
        return 180;
    }

    @Override
    public void pushAwayFrom(Entity entity) {
    }

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    public Optional<Vec3d> method_33352(float f) {
        if (this.prevAttachedBlock == null || this.teleportLerpTimer <= 0) {
            return Optional.empty();
        }
        double d = (double)((float)this.teleportLerpTimer - f) / 6.0;
        d *= d;
        BlockPos blockPos = this.getBlockPos();
        double e = (double)(blockPos.getX() - this.prevAttachedBlock.getX()) * d;
        double g = (double)(blockPos.getY() - this.prevAttachedBlock.getY()) * d;
        double h = (double)(blockPos.getZ() - this.prevAttachedBlock.getZ()) * d;
        return Optional.of(new Vec3d(-e, -g, -h));
    }

    private void setColor(DyeColor color) {
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

    class ShulkerLookControl
    extends LookControl {
        public ShulkerLookControl(MobEntity entity) {
            super(entity);
        }

        @Override
        protected void clampHeadYaw() {
        }

        @Override
        protected Optional<Float> getTargetYaw() {
            Direction direction = ShulkerEntity.this.getAttachedFace().getOpposite();
            Vec3f vec3f = SOUTH_VECTOR.copy();
            vec3f.rotate(direction.getRotationQuaternion());
            Vec3i vec3i = direction.getVector();
            Vec3f vec3f2 = new Vec3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            vec3f2.cross(vec3f);
            double d = this.x - this.entity.getX();
            double e = this.y - this.entity.getEyeY();
            double f = this.z - this.entity.getZ();
            Vec3f vec3f3 = new Vec3f((float)d, (float)e, (float)f);
            float g = vec3f2.dot(vec3f3);
            float h = vec3f.dot(vec3f3);
            return Math.abs(g) > 1.0E-5f || Math.abs(h) > 1.0E-5f ? Optional.of(Float.valueOf((float)(MathHelper.atan2(-g, h) * 57.2957763671875))) : Optional.empty();
        }

        @Override
        protected Optional<Float> getTargetPitch() {
            return Optional.of(Float.valueOf(0.0f));
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
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (ShulkerEntity.this.world.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            --this.counter;
            LivingEntity livingEntity = ShulkerEntity.this.getTarget();
            if (livingEntity == null) {
                return;
            }
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

        PeekGoal() {
        }

        @Override
        public boolean canStart() {
            return ShulkerEntity.this.getTarget() == null && ShulkerEntity.this.random.nextInt(PeekGoal.toGoalTicks(40)) == 0 && ShulkerEntity.this.canStay(ShulkerEntity.this.getBlockPos(), ShulkerEntity.this.getAttachedFace());
        }

        @Override
        public boolean shouldContinue() {
            return ShulkerEntity.this.getTarget() == null && this.counter > 0;
        }

        @Override
        public void start() {
            this.counter = this.getTickCount(20 * (1 + ShulkerEntity.this.random.nextInt(3)));
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

    class TargetPlayerGoal
    extends ActiveTargetGoal<PlayerEntity> {
        public TargetPlayerGoal(ShulkerEntity shulker) {
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

    static class TargetOtherTeamGoal
    extends ActiveTargetGoal<LivingEntity> {
        public TargetOtherTeamGoal(ShulkerEntity shulker) {
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

    static class ShulkerBodyControl
    extends BodyControl {
        public ShulkerBodyControl(MobEntity mobEntity) {
            super(mobEntity);
        }

        @Override
        public void tick() {
        }
    }
}

