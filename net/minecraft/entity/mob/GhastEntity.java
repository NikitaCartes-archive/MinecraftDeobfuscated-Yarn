/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class GhastEntity
extends FlyingEntity
implements Monster {
    private static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(GhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int fireballStrength = 1;

    public GhastEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super((EntityType<? extends FlyingEntity>)entityType, world);
        this.experiencePoints = 5;
        this.moveControl = new GhastMoveControl(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(5, new FlyRandomlyGoal(this));
        this.goalSelector.add(7, new LookAtTargetGoal(this));
        this.goalSelector.add(7, new ShootFireballGoal(this));
        this.targetSelector.add(1, new FollowTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, livingEntity -> Math.abs(livingEntity.y - this.y) <= 4.0));
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }

    public void setShooting(boolean bl) {
        this.dataTracker.set(SHOOTING, bl);
    }

    public int getFireballStrength() {
        return this.fireballStrength;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (damageSource.getSource() instanceof FireballEntity && damageSource.getAttacker() instanceof PlayerEntity) {
            super.damage(damageSource, 1000.0f);
            return true;
        }
        return super.damage(damageSource, f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOTING, false);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(100.0);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_GHAST_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_GHAST_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GHAST_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 10.0f;
    }

    public static boolean method_20675(EntityType<GhastEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        return iWorld.getDifficulty() != Difficulty.PEACEFUL && random.nextInt(20) == 0 && GhastEntity.method_20636(entityType, iWorld, spawnType, blockPos, random);
    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putInt("ExplosionPower", this.fireballStrength);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        if (compoundTag.containsKey("ExplosionPower", 99)) {
            this.fireballStrength = compoundTag.getInt("ExplosionPower");
        }
    }

    @Override
    protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
        return 2.6f;
    }

    static class ShootFireballGoal
    extends Goal {
        private final GhastEntity ghast;
        public int cooldown;

        public ShootFireballGoal(GhastEntity ghastEntity) {
            this.ghast = ghastEntity;
        }

        @Override
        public boolean canStart() {
            return this.ghast.getTarget() != null;
        }

        @Override
        public void start() {
            this.cooldown = 0;
        }

        @Override
        public void stop() {
            this.ghast.setShooting(false);
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.ghast.getTarget();
            double d = 64.0;
            if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0 && this.ghast.canSee(livingEntity)) {
                World world = this.ghast.world;
                ++this.cooldown;
                if (this.cooldown == 10) {
                    world.playLevelEvent(null, 1015, new BlockPos(this.ghast), 0);
                }
                if (this.cooldown == 20) {
                    double e = 4.0;
                    Vec3d vec3d = this.ghast.getRotationVec(1.0f);
                    double f = livingEntity.x - (this.ghast.x + vec3d.x * 4.0);
                    double g = livingEntity.getBoundingBox().minY + (double)(livingEntity.getHeight() / 2.0f) - (0.5 + this.ghast.y + (double)(this.ghast.getHeight() / 2.0f));
                    double h = livingEntity.z - (this.ghast.z + vec3d.z * 4.0);
                    world.playLevelEvent(null, 1016, new BlockPos(this.ghast), 0);
                    FireballEntity fireballEntity = new FireballEntity(world, this.ghast, f, g, h);
                    fireballEntity.explosionPower = this.ghast.getFireballStrength();
                    fireballEntity.x = this.ghast.x + vec3d.x * 4.0;
                    fireballEntity.y = this.ghast.y + (double)(this.ghast.getHeight() / 2.0f) + 0.5;
                    fireballEntity.z = this.ghast.z + vec3d.z * 4.0;
                    world.spawnEntity(fireballEntity);
                    this.cooldown = -40;
                }
            } else if (this.cooldown > 0) {
                --this.cooldown;
            }
            this.ghast.setShooting(this.cooldown > 10);
        }
    }

    static class LookAtTargetGoal
    extends Goal {
        private final GhastEntity ghast;

        public LookAtTargetGoal(GhastEntity ghastEntity) {
            this.ghast = ghastEntity;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return true;
        }

        @Override
        public void tick() {
            if (this.ghast.getTarget() == null) {
                Vec3d vec3d = this.ghast.getVelocity();
                this.ghast.bodyYaw = this.ghast.yaw = -((float)MathHelper.atan2(vec3d.x, vec3d.z)) * 57.295776f;
            } else {
                LivingEntity livingEntity = this.ghast.getTarget();
                double d = 64.0;
                if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0) {
                    double e = livingEntity.x - this.ghast.x;
                    double f = livingEntity.z - this.ghast.z;
                    this.ghast.bodyYaw = this.ghast.yaw = -((float)MathHelper.atan2(e, f)) * 57.295776f;
                }
            }
        }
    }

    static class FlyRandomlyGoal
    extends Goal {
        private final GhastEntity ghast;

        public FlyRandomlyGoal(GhastEntity ghastEntity) {
            this.ghast = ghastEntity;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            double f;
            double e;
            MoveControl moveControl = this.ghast.getMoveControl();
            if (!moveControl.isMoving()) {
                return true;
            }
            double d = moveControl.getTargetX() - this.ghast.x;
            double g = d * d + (e = moveControl.getTargetY() - this.ghast.y) * e + (f = moveControl.getTargetZ() - this.ghast.z) * f;
            return g < 1.0 || g > 3600.0;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void start() {
            Random random = this.ghast.getRand();
            double d = this.ghast.x + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
            double e = this.ghast.y + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
            double f = this.ghast.z + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
            this.ghast.getMoveControl().moveTo(d, e, f, 1.0);
        }
    }

    static class GhastMoveControl
    extends MoveControl {
        private final GhastEntity ghast;
        private int field_7276;

        public GhastMoveControl(GhastEntity ghastEntity) {
            super(ghastEntity);
            this.ghast = ghastEntity;
        }

        @Override
        public void tick() {
            if (this.state != MoveControl.State.MOVE_TO) {
                return;
            }
            if (this.field_7276-- <= 0) {
                this.field_7276 += this.ghast.getRand().nextInt(5) + 2;
                Vec3d vec3d = new Vec3d(this.targetX - this.ghast.x, this.targetY - this.ghast.y, this.targetZ - this.ghast.z);
                double d = vec3d.length();
                if (this.method_7051(vec3d = vec3d.normalize(), MathHelper.ceil(d))) {
                    this.ghast.setVelocity(this.ghast.getVelocity().add(vec3d.multiply(0.1)));
                } else {
                    this.state = MoveControl.State.WAIT;
                }
            }
        }

        private boolean method_7051(Vec3d vec3d, int i) {
            Box box = this.ghast.getBoundingBox();
            for (int j = 1; j < i; ++j) {
                if (this.ghast.world.doesNotCollide(this.ghast, box = box.offset(vec3d))) continue;
                return false;
            }
            return true;
        }
    }
}

