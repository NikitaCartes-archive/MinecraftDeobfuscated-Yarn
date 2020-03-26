/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SquidEntity
extends WaterCreatureEntity {
    public float field_6907;
    public float field_6905;
    public float field_6903;
    public float field_6906;
    public float field_6908;
    public float field_6902;
    public float field_6904;
    public float field_6900;
    private float constantVelocityRate;
    private float field_6912;
    private float field_6913;
    private float constantVelocityX;
    private float constantVelocityY;
    private float constantVelocityZ;

    public SquidEntity(EntityType<? extends SquidEntity> entityType, World world) {
        super((EntityType<? extends WaterCreatureEntity>)entityType, world);
        this.random.setSeed(this.getEntityId());
        this.field_6912 = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeAttackerGoal());
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.5f;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SQUID_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SQUID_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.field_6905 = this.field_6907;
        this.field_6906 = this.field_6903;
        this.field_6902 = this.field_6908;
        this.field_6900 = this.field_6904;
        this.field_6908 += this.field_6912;
        if ((double)this.field_6908 > Math.PI * 2) {
            if (this.world.isClient) {
                this.field_6908 = (float)Math.PI * 2;
            } else {
                this.field_6908 = (float)((double)this.field_6908 - Math.PI * 2);
                if (this.random.nextInt(10) == 0) {
                    this.field_6912 = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
                }
                this.world.sendEntityStatus(this, (byte)19);
            }
        }
        if (this.isInsideWaterOrBubbleColumn()) {
            if (this.field_6908 < (float)Math.PI) {
                float f = this.field_6908 / (float)Math.PI;
                this.field_6904 = MathHelper.sin(f * f * (float)Math.PI) * (float)Math.PI * 0.25f;
                if ((double)f > 0.75) {
                    this.constantVelocityRate = 1.0f;
                    this.field_6913 = 1.0f;
                } else {
                    this.field_6913 *= 0.8f;
                }
            } else {
                this.field_6904 = 0.0f;
                this.constantVelocityRate *= 0.9f;
                this.field_6913 *= 0.99f;
            }
            if (!this.world.isClient) {
                this.setVelocity(this.constantVelocityX * this.constantVelocityRate, this.constantVelocityY * this.constantVelocityRate, this.constantVelocityZ * this.constantVelocityRate);
            }
            Vec3d vec3d = this.getVelocity();
            float g = MathHelper.sqrt(SquidEntity.squaredHorizontalLength(vec3d));
            this.bodyYaw += (-((float)MathHelper.atan2(vec3d.x, vec3d.z)) * 57.295776f - this.bodyYaw) * 0.1f;
            this.yaw = this.bodyYaw;
            this.field_6903 = (float)((double)this.field_6903 + Math.PI * (double)this.field_6913 * 1.5);
            this.field_6907 += (-((float)MathHelper.atan2(g, vec3d.y)) * 57.295776f - this.field_6907) * 0.1f;
        } else {
            this.field_6904 = MathHelper.abs(MathHelper.sin(this.field_6908)) * (float)Math.PI * 0.25f;
            if (!this.world.isClient) {
                double d = this.getVelocity().y;
                if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
                    d = 0.05 * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1);
                } else if (!this.hasNoGravity()) {
                    d -= 0.08;
                }
                this.setVelocity(0.0, d * (double)0.98f, 0.0);
            }
            this.field_6907 = (float)((double)this.field_6907 + (double)(-90.0f - this.field_6907) * 0.02);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (super.damage(source, amount) && this.getAttacker() != null) {
            this.squirt();
            return true;
        }
        return false;
    }

    private Vec3d method_6671(Vec3d vec3d) {
        Vec3d vec3d2 = vec3d.rotateX(this.field_6905 * ((float)Math.PI / 180));
        vec3d2 = vec3d2.rotateY(-this.prevBodyYaw * ((float)Math.PI / 180));
        return vec3d2;
    }

    private void squirt() {
        this.playSound(SoundEvents.ENTITY_SQUID_SQUIRT, this.getSoundVolume(), this.getSoundPitch());
        Vec3d vec3d = this.method_6671(new Vec3d(0.0, -1.0, 0.0)).add(this.getX(), this.getY(), this.getZ());
        for (int i = 0; i < 30; ++i) {
            Vec3d vec3d2 = this.method_6671(new Vec3d((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
            Vec3d vec3d3 = vec3d2.multiply(0.3 + (double)(this.random.nextFloat() * 2.0f));
            ((ServerWorld)this.world).spawnParticles(ParticleTypes.SQUID_INK, vec3d.x, vec3d.y + 0.5, vec3d.z, 0, vec3d3.x, vec3d3.y, vec3d3.z, 0.1f);
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        this.move(MovementType.SELF, this.getVelocity());
    }

    public static boolean canSpawn(EntityType<SquidEntity> type, IWorld world, SpawnType spawnType, BlockPos pos, Random random) {
        return pos.getY() > 45 && pos.getY() < world.getSeaLevel();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 19) {
            this.field_6908 = 0.0f;
        } else {
            super.handleStatus(status);
        }
    }

    public void setConstantVelocity(float x, float y, float z) {
        this.constantVelocityX = x;
        this.constantVelocityY = y;
        this.constantVelocityZ = z;
    }

    public boolean hasConstantVelocity() {
        return this.constantVelocityX != 0.0f || this.constantVelocityY != 0.0f || this.constantVelocityZ != 0.0f;
    }

    class EscapeAttackerGoal
    extends Goal {
        private int timer;

        private EscapeAttackerGoal() {
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = SquidEntity.this.getAttacker();
            if (SquidEntity.this.isTouchingWater() && livingEntity != null) {
                return SquidEntity.this.squaredDistanceTo(livingEntity) < 100.0;
            }
            return false;
        }

        @Override
        public void start() {
            this.timer = 0;
        }

        @Override
        public void tick() {
            ++this.timer;
            LivingEntity livingEntity = SquidEntity.this.getAttacker();
            if (livingEntity == null) {
                return;
            }
            Vec3d vec3d = new Vec3d(SquidEntity.this.getX() - livingEntity.getX(), SquidEntity.this.getY() - livingEntity.getY(), SquidEntity.this.getZ() - livingEntity.getZ());
            BlockState blockState = SquidEntity.this.world.getBlockState(new BlockPos(SquidEntity.this.getX() + vec3d.x, SquidEntity.this.getY() + vec3d.y, SquidEntity.this.getZ() + vec3d.z));
            FluidState fluidState = SquidEntity.this.world.getFluidState(new BlockPos(SquidEntity.this.getX() + vec3d.x, SquidEntity.this.getY() + vec3d.y, SquidEntity.this.getZ() + vec3d.z));
            if (fluidState.matches(FluidTags.WATER) || blockState.isAir()) {
                double d = vec3d.length();
                if (d > 0.0) {
                    vec3d.normalize();
                    float f = 3.0f;
                    if (d > 5.0) {
                        f = (float)((double)f - (d - 5.0) / 5.0);
                    }
                    if (f > 0.0f) {
                        vec3d = vec3d.multiply(f);
                    }
                }
                if (blockState.isAir()) {
                    vec3d = vec3d.subtract(0.0, vec3d.y, 0.0);
                }
                SquidEntity.this.setConstantVelocity((float)vec3d.x / 20.0f, (float)vec3d.y / 20.0f, (float)vec3d.z / 20.0f);
            }
            if (this.timer % 10 == 5) {
                SquidEntity.this.world.addParticle(ParticleTypes.BUBBLE, SquidEntity.this.getX(), SquidEntity.this.getY(), SquidEntity.this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    class SwimGoal
    extends Goal {
        private final SquidEntity squid;

        public SwimGoal(SquidEntity squid) {
            this.squid = squid;
        }

        @Override
        public boolean canStart() {
            return true;
        }

        @Override
        public void tick() {
            int i = this.squid.getDespawnCounter();
            if (i > 100) {
                this.squid.setConstantVelocity(0.0f, 0.0f, 0.0f);
            } else if (this.squid.getRandom().nextInt(50) == 0 || !this.squid.touchingWater || !this.squid.hasConstantVelocity()) {
                float f = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2);
                float g = MathHelper.cos(f) * 0.2f;
                float h = -0.1f + this.squid.getRandom().nextFloat() * 0.2f;
                float j = MathHelper.sin(f) * 0.2f;
                this.squid.setConstantVelocity(g, h, j);
            }
        }
    }
}

