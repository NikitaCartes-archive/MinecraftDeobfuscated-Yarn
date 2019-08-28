/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VexEntity
extends HostileEntity {
    protected static final TrackedData<Byte> VEX_FLAGS = DataTracker.registerData(VexEntity.class, TrackedDataHandlerRegistry.BYTE);
    private MobEntity owner;
    @Nullable
    private BlockPos bounds;
    private boolean alive;
    private int lifeTicks;

    public VexEntity(EntityType<? extends VexEntity> entityType, World world) {
        super((EntityType<? extends HostileEntity>)entityType, world);
        this.moveControl = new VexMoveControl(this);
        this.experiencePoints = 3;
    }

    @Override
    public void move(MovementType movementType, Vec3d vec3d) {
        super.move(movementType, vec3d);
        this.checkBlockCollision();
    }

    @Override
    public void tick() {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setNoGravity(true);
        if (this.alive && --this.lifeTicks <= 0) {
            this.lifeTicks = 20;
            this.damage(DamageSource.STARVE, 1.0f);
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new ChargeTargetGoal());
        this.goalSelector.add(8, new LookAtTargetGoal());
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new TrackOwnerTargetGoal(this));
        this.targetSelector.add(3, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(14.0);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VEX_FLAGS, (byte)0);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        if (compoundTag.containsKey("BoundX")) {
            this.bounds = new BlockPos(compoundTag.getInt("BoundX"), compoundTag.getInt("BoundY"), compoundTag.getInt("BoundZ"));
        }
        if (compoundTag.containsKey("LifeTicks")) {
            this.setLifeTicks(compoundTag.getInt("LifeTicks"));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        if (this.bounds != null) {
            compoundTag.putInt("BoundX", this.bounds.getX());
            compoundTag.putInt("BoundY", this.bounds.getY());
            compoundTag.putInt("BoundZ", this.bounds.getZ());
        }
        if (this.alive) {
            compoundTag.putInt("LifeTicks", this.lifeTicks);
        }
    }

    public MobEntity getOwner() {
        return this.owner;
    }

    @Nullable
    public BlockPos getBounds() {
        return this.bounds;
    }

    public void setBounds(@Nullable BlockPos blockPos) {
        this.bounds = blockPos;
    }

    private boolean areFlagsSet(int i) {
        byte j = this.dataTracker.get(VEX_FLAGS);
        return (j & i) != 0;
    }

    private void setVexFlag(int i, boolean bl) {
        int j = this.dataTracker.get(VEX_FLAGS).byteValue();
        j = bl ? (j |= i) : (j &= ~i);
        this.dataTracker.set(VEX_FLAGS, (byte)(j & 0xFF));
    }

    public boolean isCharging() {
        return this.areFlagsSet(1);
    }

    public void setCharging(boolean bl) {
        this.setVexFlag(1, bl);
    }

    public void setOwner(MobEntity mobEntity) {
        this.owner = mobEntity;
    }

    public void setLifeTicks(int i) {
        this.alive = true;
        this.lifeTicks = i;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getLightmapCoordinates() {
        return 0xF000F0;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag) {
        this.initEquipment(localDifficulty);
        this.updateEnchantments(localDifficulty);
        return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
    }

    @Override
    protected void initEquipment(LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    class TrackOwnerTargetGoal
    extends TrackTargetGoal {
        private final TargetPredicate TRACK_OWNER_PREDICATE;

        public TrackOwnerTargetGoal(MobEntityWithAi mobEntityWithAi) {
            super(mobEntityWithAi, false);
            this.TRACK_OWNER_PREDICATE = new TargetPredicate().includeHidden().ignoreDistanceScalingFactor();
        }

        @Override
        public boolean canStart() {
            return VexEntity.this.owner != null && VexEntity.this.owner.getTarget() != null && this.canTrack(VexEntity.this.owner.getTarget(), this.TRACK_OWNER_PREDICATE);
        }

        @Override
        public void start() {
            VexEntity.this.setTarget(VexEntity.this.owner.getTarget());
            super.start();
        }
    }

    class LookAtTargetGoal
    extends Goal {
        public LookAtTargetGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !VexEntity.this.getMoveControl().isMoving() && VexEntity.this.random.nextInt(7) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos blockPos = VexEntity.this.getBounds();
            if (blockPos == null) {
                blockPos = new BlockPos(VexEntity.this);
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = blockPos.add(VexEntity.this.random.nextInt(15) - 7, VexEntity.this.random.nextInt(11) - 5, VexEntity.this.random.nextInt(15) - 7);
                if (!VexEntity.this.world.method_22347(blockPos2)) continue;
                VexEntity.this.moveControl.moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 0.25);
                if (VexEntity.this.getTarget() != null) break;
                VexEntity.this.getLookControl().lookAt((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class ChargeTargetGoal
    extends Goal {
        public ChargeTargetGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (VexEntity.this.getTarget() != null && !VexEntity.this.getMoveControl().isMoving() && VexEntity.this.random.nextInt(7) == 0) {
                return VexEntity.this.squaredDistanceTo(VexEntity.this.getTarget()) > 4.0;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return VexEntity.this.getMoveControl().isMoving() && VexEntity.this.isCharging() && VexEntity.this.getTarget() != null && VexEntity.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity livingEntity = VexEntity.this.getTarget();
            Vec3d vec3d = livingEntity.getCameraPosVec(1.0f);
            VexEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
            VexEntity.this.setCharging(true);
            VexEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0f, 1.0f);
        }

        @Override
        public void stop() {
            VexEntity.this.setCharging(false);
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = VexEntity.this.getTarget();
            if (VexEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                VexEntity.this.tryAttack(livingEntity);
                VexEntity.this.setCharging(false);
            } else {
                double d = VexEntity.this.squaredDistanceTo(livingEntity);
                if (d < 9.0) {
                    Vec3d vec3d = livingEntity.getCameraPosVec(1.0f);
                    VexEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
                }
            }
        }
    }

    class VexMoveControl
    extends MoveControl {
        public VexMoveControl(VexEntity vexEntity2) {
            super(vexEntity2);
        }

        @Override
        public void tick() {
            if (this.state != MoveControl.State.MOVE_TO) {
                return;
            }
            Vec3d vec3d = new Vec3d(this.targetX - VexEntity.this.x, this.targetY - VexEntity.this.y, this.targetZ - VexEntity.this.z);
            double d = vec3d.length();
            if (d < VexEntity.this.getBoundingBox().averageDimension()) {
                this.state = MoveControl.State.WAIT;
                VexEntity.this.setVelocity(VexEntity.this.getVelocity().multiply(0.5));
            } else {
                VexEntity.this.setVelocity(VexEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
                if (VexEntity.this.getTarget() == null) {
                    Vec3d vec3d2 = VexEntity.this.getVelocity();
                    VexEntity.this.bodyYaw = VexEntity.this.yaw = -((float)MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776f;
                } else {
                    double e = VexEntity.this.getTarget().x - VexEntity.this.x;
                    double f = VexEntity.this.getTarget().z - VexEntity.this.z;
                    VexEntity.this.bodyYaw = VexEntity.this.yaw = -((float)MathHelper.atan2(e, f)) * 57.295776f;
                }
            }
        }
    }
}

