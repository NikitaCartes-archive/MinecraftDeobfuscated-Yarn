/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

public class PhantomEntity
extends FlyingEntity
implements Monster {
    public static final float field_30475 = 7.448451f;
    public static final int field_28641 = MathHelper.ceil(24.166098f);
    private static final TrackedData<Integer> SIZE = DataTracker.registerData(PhantomEntity.class, TrackedDataHandlerRegistry.INTEGER);
    Vec3d targetPosition = Vec3d.ZERO;
    BlockPos circlingCenter = BlockPos.ORIGIN;
    PhantomMovementType movementType = PhantomMovementType.CIRCLE;

    public PhantomEntity(EntityType<? extends PhantomEntity> entityType, World world) {
        super((EntityType<? extends FlyingEntity>)entityType, world);
        this.experiencePoints = 5;
        this.moveControl = new PhantomMoveControl(this);
        this.lookControl = new PhantomLookControl(this);
    }

    @Override
    public boolean hasWings() {
        return (this.method_33588() + this.age) % field_28641 == 0;
    }

    @Override
    protected BodyControl createBodyControl() {
        return new PhantomBodyControl(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new StartAttackGoal());
        this.goalSelector.add(2, new SwoopMovementGoal());
        this.goalSelector.add(3, new CircleMovementGoal());
        this.targetSelector.add(1, new FindTargetGoal());
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SIZE, 0);
    }

    public void setPhantomSize(int size) {
        this.dataTracker.set(SIZE, MathHelper.clamp(size, 0, 64));
    }

    private void onSizeChanged() {
        this.calculateDimensions();
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(6 + this.getPhantomSize());
    }

    public int getPhantomSize() {
        return this.dataTracker.get(SIZE);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.35f;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (SIZE.equals(data)) {
            this.onSizeChanged();
        }
        super.onTrackedDataSet(data);
    }

    public int method_33588() {
        return this.getId() * 3;
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            float f = MathHelper.cos((float)(this.method_33588() + this.age) * 7.448451f * ((float)Math.PI / 180) + (float)Math.PI);
            float g = MathHelper.cos((float)(this.method_33588() + this.age + 1) * 7.448451f * ((float)Math.PI / 180) + (float)Math.PI);
            if (f > 0.0f && g <= 0.0f) {
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95f + this.random.nextFloat() * 0.05f, 0.95f + this.random.nextFloat() * 0.05f, false);
            }
            int i = this.getPhantomSize();
            float h = MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)i);
            float j = MathHelper.sin(this.getYaw() * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)i);
            float k = (0.3f + f * 0.45f) * ((float)i * 0.2f + 1.0f);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)h, this.getY() + (double)k, this.getZ() + (double)j, 0.0, 0.0, 0.0);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)h, this.getY() + (double)k, this.getZ() - (double)j, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void tickMovement() {
        if (this.isAlive() && this.isAffectedByDaylight()) {
            this.setOnFireFor(8);
        }
        super.tickMovement();
    }

    @Override
    protected void mobTick() {
        super.mobTick();
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.circlingCenter = this.getBlockPos().up(5);
        this.setPhantomSize(0);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("AX")) {
            this.circlingCenter = new BlockPos(nbt.getInt("AX"), nbt.getInt("AY"), nbt.getInt("AZ"));
        }
        this.setPhantomSize(nbt.getInt("Size"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("AX", this.circlingCenter.getX());
        nbt.putInt("AY", this.circlingCenter.getY());
        nbt.putInt("AZ", this.circlingCenter.getZ());
        nbt.putInt("Size", this.getPhantomSize());
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PHANTOM_DEATH;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0f;
    }

    @Override
    public boolean canTarget(EntityType<?> type) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        int i = this.getPhantomSize();
        EntityDimensions entityDimensions = super.getDimensions(pose);
        float f = (entityDimensions.width + 0.2f * (float)i) / entityDimensions.width;
        return entityDimensions.scaled(f);
    }

    static enum PhantomMovementType {
        CIRCLE,
        SWOOP;

    }

    class PhantomMoveControl
    extends MoveControl {
        private float targetSpeed;

        public PhantomMoveControl(MobEntity owner) {
            super(owner);
            this.targetSpeed = 0.1f;
        }

        @Override
        public void tick() {
            if (PhantomEntity.this.horizontalCollision) {
                PhantomEntity.this.setYaw(PhantomEntity.this.getYaw() + 180.0f);
                this.targetSpeed = 0.1f;
            }
            float f = (float)(PhantomEntity.this.targetPosition.x - PhantomEntity.this.getX());
            float g = (float)(PhantomEntity.this.targetPosition.y - PhantomEntity.this.getY());
            float h = (float)(PhantomEntity.this.targetPosition.z - PhantomEntity.this.getZ());
            double d = MathHelper.sqrt(f * f + h * h);
            if (Math.abs(d) > (double)1.0E-5f) {
                double e = 1.0 - (double)MathHelper.abs(g * 0.7f) / d;
                f = (float)((double)f * e);
                h = (float)((double)h * e);
                d = MathHelper.sqrt(f * f + h * h);
                double i = MathHelper.sqrt(f * f + h * h + g * g);
                float j = PhantomEntity.this.getYaw();
                float k = (float)MathHelper.atan2(h, f);
                float l = MathHelper.wrapDegrees(PhantomEntity.this.getYaw() + 90.0f);
                float m = MathHelper.wrapDegrees(k * 57.295776f);
                PhantomEntity.this.setYaw(MathHelper.stepUnwrappedAngleTowards(l, m, 4.0f) - 90.0f);
                PhantomEntity.this.bodyYaw = PhantomEntity.this.getYaw();
                this.targetSpeed = MathHelper.angleBetween(j, PhantomEntity.this.getYaw()) < 3.0f ? MathHelper.stepTowards(this.targetSpeed, 1.8f, 0.005f * (1.8f / this.targetSpeed)) : MathHelper.stepTowards(this.targetSpeed, 0.2f, 0.025f);
                float n = (float)(-(MathHelper.atan2(-g, d) * 57.2957763671875));
                PhantomEntity.this.setPitch(n);
                float o = PhantomEntity.this.getYaw() + 90.0f;
                double p = (double)(this.targetSpeed * MathHelper.cos(o * ((float)Math.PI / 180))) * Math.abs((double)f / i);
                double q = (double)(this.targetSpeed * MathHelper.sin(o * ((float)Math.PI / 180))) * Math.abs((double)h / i);
                double r = (double)(this.targetSpeed * MathHelper.sin(n * ((float)Math.PI / 180))) * Math.abs((double)g / i);
                Vec3d vec3d = PhantomEntity.this.getVelocity();
                PhantomEntity.this.setVelocity(vec3d.add(new Vec3d(p, r, q).subtract(vec3d).multiply(0.2)));
            }
        }
    }

    class PhantomLookControl
    extends LookControl {
        public PhantomLookControl(MobEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
        }
    }

    class PhantomBodyControl
    extends BodyControl {
        public PhantomBodyControl(MobEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
            PhantomEntity.this.headYaw = PhantomEntity.this.bodyYaw;
            PhantomEntity.this.bodyYaw = PhantomEntity.this.getYaw();
        }
    }

    class StartAttackGoal
    extends Goal {
        private int cooldown;

        StartAttackGoal() {
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = PhantomEntity.this.getTarget();
            if (livingEntity != null) {
                return PhantomEntity.this.isTarget(livingEntity, TargetPredicate.DEFAULT);
            }
            return false;
        }

        @Override
        public void start() {
            this.cooldown = this.getTickCount(10);
            PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
            this.startSwoop();
        }

        @Override
        public void stop() {
            PhantomEntity.this.circlingCenter = PhantomEntity.this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, PhantomEntity.this.circlingCenter).up(10 + PhantomEntity.this.random.nextInt(20));
        }

        @Override
        public void tick() {
            if (PhantomEntity.this.movementType == PhantomMovementType.CIRCLE) {
                --this.cooldown;
                if (this.cooldown <= 0) {
                    PhantomEntity.this.movementType = PhantomMovementType.SWOOP;
                    this.startSwoop();
                    this.cooldown = this.getTickCount((8 + PhantomEntity.this.random.nextInt(4)) * 20);
                    PhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0f, 0.95f + PhantomEntity.this.random.nextFloat() * 0.1f);
                }
            }
        }

        private void startSwoop() {
            PhantomEntity.this.circlingCenter = PhantomEntity.this.getTarget().getBlockPos().up(20 + PhantomEntity.this.random.nextInt(20));
            if (PhantomEntity.this.circlingCenter.getY() < PhantomEntity.this.world.getSeaLevel()) {
                PhantomEntity.this.circlingCenter = new BlockPos(PhantomEntity.this.circlingCenter.getX(), PhantomEntity.this.world.getSeaLevel() + 1, PhantomEntity.this.circlingCenter.getZ());
            }
        }
    }

    class SwoopMovementGoal
    extends MovementGoal {
        private static final int field_36305 = 20;
        private boolean catsNearby;
        private int field_36307;

        SwoopMovementGoal() {
        }

        @Override
        public boolean canStart() {
            return PhantomEntity.this.getTarget() != null && PhantomEntity.this.movementType == PhantomMovementType.SWOOP;
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = PhantomEntity.this.getTarget();
            if (livingEntity == null) {
                return false;
            }
            if (!livingEntity.isAlive()) {
                return false;
            }
            if (livingEntity instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)livingEntity;
                if (livingEntity.isSpectator() || playerEntity.isCreative()) {
                    return false;
                }
            }
            if (!this.canStart()) {
                return false;
            }
            if (PhantomEntity.this.age > this.field_36307) {
                this.field_36307 = PhantomEntity.this.age + 20;
                List<Entity> list = PhantomEntity.this.world.getEntitiesByClass(CatEntity.class, PhantomEntity.this.getBoundingBox().expand(16.0), EntityPredicates.VALID_ENTITY);
                for (CatEntity catEntity : list) {
                    catEntity.hiss();
                }
                this.catsNearby = !list.isEmpty();
            }
            return !this.catsNearby;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
            PhantomEntity.this.setTarget(null);
            PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = PhantomEntity.this.getTarget();
            if (livingEntity == null) {
                return;
            }
            PhantomEntity.this.targetPosition = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5), livingEntity.getZ());
            if (PhantomEntity.this.getBoundingBox().expand(0.2f).intersects(livingEntity.getBoundingBox())) {
                PhantomEntity.this.tryAttack(livingEntity);
                PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
                if (!PhantomEntity.this.isSilent()) {
                    PhantomEntity.this.world.syncWorldEvent(WorldEvents.PHANTOM_BITES, PhantomEntity.this.getBlockPos(), 0);
                }
            } else if (PhantomEntity.this.horizontalCollision || PhantomEntity.this.hurtTime > 0) {
                PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
            }
        }
    }

    class CircleMovementGoal
    extends MovementGoal {
        private float angle;
        private float radius;
        private float yOffset;
        private float circlingDirection;

        CircleMovementGoal() {
        }

        @Override
        public boolean canStart() {
            return PhantomEntity.this.getTarget() == null || PhantomEntity.this.movementType == PhantomMovementType.CIRCLE;
        }

        @Override
        public void start() {
            this.radius = 5.0f + PhantomEntity.this.random.nextFloat() * 10.0f;
            this.yOffset = -4.0f + PhantomEntity.this.random.nextFloat() * 9.0f;
            this.circlingDirection = PhantomEntity.this.random.nextBoolean() ? 1.0f : -1.0f;
            this.adjustDirection();
        }

        @Override
        public void tick() {
            if (PhantomEntity.this.random.nextInt(this.getTickCount(350)) == 0) {
                this.yOffset = -4.0f + PhantomEntity.this.random.nextFloat() * 9.0f;
            }
            if (PhantomEntity.this.random.nextInt(this.getTickCount(250)) == 0) {
                this.radius += 1.0f;
                if (this.radius > 15.0f) {
                    this.radius = 5.0f;
                    this.circlingDirection = -this.circlingDirection;
                }
            }
            if (PhantomEntity.this.random.nextInt(this.getTickCount(450)) == 0) {
                this.angle = PhantomEntity.this.random.nextFloat() * 2.0f * (float)Math.PI;
                this.adjustDirection();
            }
            if (this.isNearTarget()) {
                this.adjustDirection();
            }
            if (PhantomEntity.this.targetPosition.y < PhantomEntity.this.getY() && !PhantomEntity.this.world.isAir(PhantomEntity.this.getBlockPos().down(1))) {
                this.yOffset = Math.max(1.0f, this.yOffset);
                this.adjustDirection();
            }
            if (PhantomEntity.this.targetPosition.y > PhantomEntity.this.getY() && !PhantomEntity.this.world.isAir(PhantomEntity.this.getBlockPos().up(1))) {
                this.yOffset = Math.min(-1.0f, this.yOffset);
                this.adjustDirection();
            }
        }

        private void adjustDirection() {
            if (BlockPos.ORIGIN.equals(PhantomEntity.this.circlingCenter)) {
                PhantomEntity.this.circlingCenter = PhantomEntity.this.getBlockPos();
            }
            this.angle += this.circlingDirection * 15.0f * ((float)Math.PI / 180);
            PhantomEntity.this.targetPosition = Vec3d.of(PhantomEntity.this.circlingCenter).add(this.radius * MathHelper.cos(this.angle), -4.0f + this.yOffset, this.radius * MathHelper.sin(this.angle));
        }
    }

    class FindTargetGoal
    extends Goal {
        private final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(64.0);
        private int delay = FindTargetGoal.toGoalTicks(20);

        FindTargetGoal() {
        }

        @Override
        public boolean canStart() {
            if (this.delay > 0) {
                --this.delay;
                return false;
            }
            this.delay = FindTargetGoal.toGoalTicks(60);
            List<PlayerEntity> list = PhantomEntity.this.world.getPlayers(this.PLAYERS_IN_RANGE_PREDICATE, PhantomEntity.this, PhantomEntity.this.getBoundingBox().expand(16.0, 64.0, 16.0));
            if (!list.isEmpty()) {
                list.sort(Comparator.comparing(Entity::getY).reversed());
                for (PlayerEntity playerEntity : list) {
                    if (!PhantomEntity.this.isTarget(playerEntity, TargetPredicate.DEFAULT)) continue;
                    PhantomEntity.this.setTarget(playerEntity);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = PhantomEntity.this.getTarget();
            if (livingEntity != null) {
                return PhantomEntity.this.isTarget(livingEntity, TargetPredicate.DEFAULT);
            }
            return false;
        }
    }

    abstract class MovementGoal
    extends Goal {
        public MovementGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        protected boolean isNearTarget() {
            return PhantomEntity.this.targetPosition.squaredDistanceTo(PhantomEntity.this.getX(), PhantomEntity.this.getY(), PhantomEntity.this.getZ()) < 4.0;
        }
    }
}

