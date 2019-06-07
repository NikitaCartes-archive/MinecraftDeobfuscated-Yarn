/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PhantomEntity
extends FlyingEntity
implements Monster {
    private static final TrackedData<Integer> SIZE = DataTracker.registerData(PhantomEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private Vec3d field_7314 = Vec3d.ZERO;
    private BlockPos field_7312 = BlockPos.ORIGIN;
    private PhantomMovementType movementType = PhantomMovementType.CIRCLE;

    public PhantomEntity(EntityType<? extends PhantomEntity> entityType, World world) {
        super((EntityType<? extends FlyingEntity>)entityType, world);
        this.experiencePoints = 5;
        this.moveControl = new PhantomMoveControl(this);
        this.lookControl = new PhantomLookControl(this);
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
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SIZE, 0);
    }

    public void setPhantomSize(int i) {
        this.dataTracker.set(SIZE, MathHelper.clamp(i, 0, 64));
    }

    private void onSizeChanged() {
        this.calculateDimensions();
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6 + this.getPhantomSize());
    }

    public int getPhantomSize() {
        return this.dataTracker.get(SIZE);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.35f;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> trackedData) {
        if (SIZE.equals(trackedData)) {
            this.onSizeChanged();
        }
        super.onTrackedDataSet(trackedData);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            float f = MathHelper.cos((float)(this.getEntityId() * 3 + this.age) * 0.13f + (float)Math.PI);
            float g = MathHelper.cos((float)(this.getEntityId() * 3 + this.age + 1) * 0.13f + (float)Math.PI);
            if (f > 0.0f && g <= 0.0f) {
                this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95f + this.random.nextFloat() * 0.05f, 0.95f + this.random.nextFloat() * 0.05f, false);
            }
            int i = this.getPhantomSize();
            float h = MathHelper.cos(this.yaw * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)i);
            float j = MathHelper.sin(this.yaw * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)i);
            float k = (0.3f + f * 0.45f) * ((float)i * 0.2f + 1.0f);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.x + (double)h, this.y + (double)k, this.z + (double)j, 0.0, 0.0, 0.0);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.x - (double)h, this.y + (double)k, this.z - (double)j, 0.0, 0.0, 0.0);
        }
        if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }

    @Override
    public void tickMovement() {
        if (this.isAlive() && this.isInDaylight()) {
            this.setOnFireFor(8);
        }
        super.tickMovement();
    }

    @Override
    protected void mobTick() {
        super.mobTick();
    }

    @Override
    public EntityData initialize(IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag) {
        this.field_7312 = new BlockPos(this).up(5);
        this.setPhantomSize(0);
        return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        if (compoundTag.containsKey("AX")) {
            this.field_7312 = new BlockPos(compoundTag.getInt("AX"), compoundTag.getInt("AY"), compoundTag.getInt("AZ"));
        }
        this.setPhantomSize(compoundTag.getInt("Size"));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putInt("AX", this.field_7312.getX());
        compoundTag.putInt("AY", this.field_7312.getY());
        compoundTag.putInt("AZ", this.field_7312.getZ());
        compoundTag.putInt("Size", this.getPhantomSize());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderAtDistance(double d) {
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
    protected SoundEvent getHurtSound(DamageSource damageSource) {
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
    public boolean canTarget(EntityType<?> entityType) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose entityPose) {
        int i = this.getPhantomSize();
        EntityDimensions entityDimensions = super.getDimensions(entityPose);
        float f = (entityDimensions.width + 0.2f * (float)i) / entityDimensions.width;
        return entityDimensions.scaled(f);
    }

    class FindTargetGoal
    extends Goal {
        private final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
        private int delay = 20;

        private FindTargetGoal() {
        }

        @Override
        public boolean canStart() {
            if (this.delay > 0) {
                --this.delay;
                return false;
            }
            this.delay = 60;
            List<PlayerEntity> list = PhantomEntity.this.world.getPlayersInBox(this.PLAYERS_IN_RANGE_PREDICATE, PhantomEntity.this, PhantomEntity.this.getBoundingBox().expand(16.0, 64.0, 16.0));
            if (!list.isEmpty()) {
                list.sort((playerEntity, playerEntity2) -> playerEntity.y > playerEntity2.y ? -1 : 1);
                for (PlayerEntity playerEntity3 : list) {
                    if (!PhantomEntity.this.isTarget(playerEntity3, TargetPredicate.DEFAULT)) continue;
                    PhantomEntity.this.setTarget(playerEntity3);
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

    class StartAttackGoal
    extends Goal {
        private int field_7322;

        private StartAttackGoal() {
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = PhantomEntity.this.getTarget();
            if (livingEntity != null) {
                return PhantomEntity.this.isTarget(PhantomEntity.this.getTarget(), TargetPredicate.DEFAULT);
            }
            return false;
        }

        @Override
        public void start() {
            this.field_7322 = 10;
            PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
            this.method_7102();
        }

        @Override
        public void stop() {
            PhantomEntity.this.field_7312 = PhantomEntity.this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, PhantomEntity.this.field_7312).up(10 + PhantomEntity.this.random.nextInt(20));
        }

        @Override
        public void tick() {
            if (PhantomEntity.this.movementType == PhantomMovementType.CIRCLE) {
                --this.field_7322;
                if (this.field_7322 <= 0) {
                    PhantomEntity.this.movementType = PhantomMovementType.SWOOP;
                    this.method_7102();
                    this.field_7322 = (8 + PhantomEntity.this.random.nextInt(4)) * 20;
                    PhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0f, 0.95f + PhantomEntity.this.random.nextFloat() * 0.1f);
                }
            }
        }

        private void method_7102() {
            PhantomEntity.this.field_7312 = new BlockPos(PhantomEntity.this.getTarget()).up(20 + PhantomEntity.this.random.nextInt(20));
            if (PhantomEntity.this.field_7312.getY() < PhantomEntity.this.world.getSeaLevel()) {
                PhantomEntity.this.field_7312 = new BlockPos(PhantomEntity.this.field_7312.getX(), PhantomEntity.this.world.getSeaLevel() + 1, PhantomEntity.this.field_7312.getZ());
            }
        }
    }

    class SwoopMovementGoal
    extends MovementGoal {
        private SwoopMovementGoal() {
        }

        @Override
        public boolean canStart() {
            return PhantomEntity.this.getTarget() != null && PhantomEntity.this.movementType == PhantomMovementType.SWOOP;
        }

        @Override
        public boolean shouldContinue() {
            List<Entity> list;
            LivingEntity livingEntity = PhantomEntity.this.getTarget();
            if (livingEntity == null) {
                return false;
            }
            if (!livingEntity.isAlive()) {
                return false;
            }
            if (livingEntity instanceof PlayerEntity && (((PlayerEntity)livingEntity).isSpectator() || ((PlayerEntity)livingEntity).isCreative())) {
                return false;
            }
            if (!this.canStart()) {
                return false;
            }
            if (PhantomEntity.this.age % 20 == 0 && !(list = PhantomEntity.this.world.getEntities(CatEntity.class, PhantomEntity.this.getBoundingBox().expand(16.0), EntityPredicates.VALID_ENTITY)).isEmpty()) {
                for (CatEntity catEntity : list) {
                    catEntity.hiss();
                }
                return false;
            }
            return true;
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
            PhantomEntity.this.field_7314 = new Vec3d(livingEntity.x, livingEntity.y + (double)livingEntity.getHeight() * 0.5, livingEntity.z);
            if (PhantomEntity.this.getBoundingBox().expand(0.2f).intersects(livingEntity.getBoundingBox())) {
                PhantomEntity.this.tryAttack(livingEntity);
                PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
                PhantomEntity.this.world.playLevelEvent(1039, new BlockPos(PhantomEntity.this), 0);
            } else if (PhantomEntity.this.horizontalCollision || PhantomEntity.this.hurtTime > 0) {
                PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
            }
        }
    }

    class CircleMovementGoal
    extends MovementGoal {
        private float field_7328;
        private float field_7327;
        private float field_7326;
        private float field_7324;

        private CircleMovementGoal() {
        }

        @Override
        public boolean canStart() {
            return PhantomEntity.this.getTarget() == null || PhantomEntity.this.movementType == PhantomMovementType.CIRCLE;
        }

        @Override
        public void start() {
            this.field_7327 = 5.0f + PhantomEntity.this.random.nextFloat() * 10.0f;
            this.field_7326 = -4.0f + PhantomEntity.this.random.nextFloat() * 9.0f;
            this.field_7324 = PhantomEntity.this.random.nextBoolean() ? 1.0f : -1.0f;
            this.method_7103();
        }

        @Override
        public void tick() {
            if (PhantomEntity.this.random.nextInt(350) == 0) {
                this.field_7326 = -4.0f + PhantomEntity.this.random.nextFloat() * 9.0f;
            }
            if (PhantomEntity.this.random.nextInt(250) == 0) {
                this.field_7327 += 1.0f;
                if (this.field_7327 > 15.0f) {
                    this.field_7327 = 5.0f;
                    this.field_7324 = -this.field_7324;
                }
            }
            if (PhantomEntity.this.random.nextInt(450) == 0) {
                this.field_7328 = PhantomEntity.this.random.nextFloat() * 2.0f * (float)Math.PI;
                this.method_7103();
            }
            if (this.method_7104()) {
                this.method_7103();
            }
            if (((PhantomEntity)PhantomEntity.this).field_7314.y < PhantomEntity.this.y && !PhantomEntity.this.world.isAir(new BlockPos(PhantomEntity.this).down(1))) {
                this.field_7326 = Math.max(1.0f, this.field_7326);
                this.method_7103();
            }
            if (((PhantomEntity)PhantomEntity.this).field_7314.y > PhantomEntity.this.y && !PhantomEntity.this.world.isAir(new BlockPos(PhantomEntity.this).up(1))) {
                this.field_7326 = Math.min(-1.0f, this.field_7326);
                this.method_7103();
            }
        }

        private void method_7103() {
            if (BlockPos.ORIGIN.equals(PhantomEntity.this.field_7312)) {
                PhantomEntity.this.field_7312 = new BlockPos(PhantomEntity.this);
            }
            this.field_7328 += this.field_7324 * 15.0f * ((float)Math.PI / 180);
            PhantomEntity.this.field_7314 = new Vec3d(PhantomEntity.this.field_7312).add(this.field_7327 * MathHelper.cos(this.field_7328), -4.0f + this.field_7326, this.field_7327 * MathHelper.sin(this.field_7328));
        }
    }

    abstract class MovementGoal
    extends Goal {
        public MovementGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        protected boolean method_7104() {
            return PhantomEntity.this.field_7314.squaredDistanceTo(PhantomEntity.this.x, PhantomEntity.this.y, PhantomEntity.this.z) < 4.0;
        }
    }

    class PhantomLookControl
    extends LookControl {
        public PhantomLookControl(MobEntity mobEntity) {
            super(mobEntity);
        }

        @Override
        public void tick() {
        }
    }

    class PhantomBodyControl
    extends BodyControl {
        public PhantomBodyControl(MobEntity mobEntity) {
            super(mobEntity);
        }

        @Override
        public void method_6224() {
            PhantomEntity.this.headYaw = PhantomEntity.this.field_6283;
            PhantomEntity.this.field_6283 = PhantomEntity.this.yaw;
        }
    }

    class PhantomMoveControl
    extends MoveControl {
        private float field_7331;

        public PhantomMoveControl(MobEntity mobEntity) {
            super(mobEntity);
            this.field_7331 = 0.1f;
        }

        @Override
        public void tick() {
            float n;
            if (PhantomEntity.this.horizontalCollision) {
                PhantomEntity.this.yaw += 180.0f;
                this.field_7331 = 0.1f;
            }
            float f = (float)(((PhantomEntity)PhantomEntity.this).field_7314.x - PhantomEntity.this.x);
            float g = (float)(((PhantomEntity)PhantomEntity.this).field_7314.y - PhantomEntity.this.y);
            float h = (float)(((PhantomEntity)PhantomEntity.this).field_7314.z - PhantomEntity.this.z);
            double d = MathHelper.sqrt(f * f + h * h);
            double e = 1.0 - (double)MathHelper.abs(g * 0.7f) / d;
            f = (float)((double)f * e);
            h = (float)((double)h * e);
            d = MathHelper.sqrt(f * f + h * h);
            double i = MathHelper.sqrt(f * f + h * h + g * g);
            float j = PhantomEntity.this.yaw;
            float k = (float)MathHelper.atan2(h, f);
            float l = MathHelper.wrapDegrees(PhantomEntity.this.yaw + 90.0f);
            float m = MathHelper.wrapDegrees(k * 57.295776f);
            PhantomEntity.this.field_6283 = PhantomEntity.this.yaw = MathHelper.method_15388(l, m, 4.0f) - 90.0f;
            this.field_7331 = MathHelper.angleBetween(j, PhantomEntity.this.yaw) < 3.0f ? MathHelper.method_15348(this.field_7331, 1.8f, 0.005f * (1.8f / this.field_7331)) : MathHelper.method_15348(this.field_7331, 0.2f, 0.025f);
            PhantomEntity.this.pitch = n = (float)(-(MathHelper.atan2(-g, d) * 57.2957763671875));
            float o = PhantomEntity.this.yaw + 90.0f;
            double p = (double)(this.field_7331 * MathHelper.cos(o * ((float)Math.PI / 180))) * Math.abs((double)f / i);
            double q = (double)(this.field_7331 * MathHelper.sin(o * ((float)Math.PI / 180))) * Math.abs((double)h / i);
            double r = (double)(this.field_7331 * MathHelper.sin(n * ((float)Math.PI / 180))) * Math.abs((double)g / i);
            Vec3d vec3d = PhantomEntity.this.getVelocity();
            PhantomEntity.this.setVelocity(vec3d.add(new Vec3d(p, r, q).subtract(vec3d).multiply(0.2)));
        }
    }

    static enum PhantomMovementType {
        CIRCLE,
        SWOOP;

    }
}

