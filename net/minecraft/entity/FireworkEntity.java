/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import java.util.List;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=FlyingItemEntity.class)})
public class FireworkEntity
extends Entity
implements FlyingItemEntity,
Projectile {
    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(FireworkEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<OptionalInt> SHOOTER_ENTITY_ID = DataTracker.registerData(FireworkEntity.class, TrackedDataHandlerRegistry.field_17910);
    private static final TrackedData<Boolean> SHOT_AT_ANGLE = DataTracker.registerData(FireworkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int life;
    private int lifeTime;
    private LivingEntity shooter;

    public FireworkEntity(EntityType<? extends FireworkEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(SHOOTER_ENTITY_ID, OptionalInt.empty());
        this.dataTracker.startTracking(SHOT_AT_ANGLE, false);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderAtDistance(double d) {
        return d < 4096.0 && !this.wasShotByEntity();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderFrom(double d, double e, double f) {
        return super.shouldRenderFrom(d, e, f) && !this.wasShotByEntity();
    }

    public FireworkEntity(World world, double d, double e, double f, ItemStack itemStack) {
        super(EntityType.FIREWORK_ROCKET, world);
        this.life = 0;
        this.setPosition(d, e, f);
        int i = 1;
        if (!itemStack.isEmpty() && itemStack.hasTag()) {
            this.dataTracker.set(ITEM, itemStack.copy());
            i += itemStack.getOrCreateSubCompoundTag("Fireworks").getByte("Flight");
        }
        this.setVelocity(this.random.nextGaussian() * 0.001, 0.05, this.random.nextGaussian() * 0.001);
        this.lifeTime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
    }

    public FireworkEntity(World world, ItemStack itemStack, LivingEntity livingEntity) {
        this(world, livingEntity.x, livingEntity.y, livingEntity.z, itemStack);
        this.dataTracker.set(SHOOTER_ENTITY_ID, OptionalInt.of(livingEntity.getEntityId()));
        this.shooter = livingEntity;
    }

    public FireworkEntity(World world, ItemStack itemStack, double d, double e, double f, boolean bl) {
        this(world, d, e, f, itemStack);
        this.dataTracker.set(SHOT_AT_ANGLE, bl);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double d, double e, double f) {
        this.setVelocity(d, e, f);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float g = MathHelper.sqrt(d * d + f * f);
            this.yaw = (float)(MathHelper.atan2(d, f) * 57.2957763671875);
            this.pitch = (float)(MathHelper.atan2(e, g) * 57.2957763671875);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
        }
    }

    @Override
    public void tick() {
        Vec3d vec3d;
        this.prevRenderX = this.x;
        this.prevRenderY = this.y;
        this.prevRenderZ = this.z;
        super.tick();
        if (this.wasShotByEntity()) {
            if (this.shooter == null) {
                this.dataTracker.get(SHOOTER_ENTITY_ID).ifPresent(i -> {
                    Entity entity = this.world.getEntityById(i);
                    if (entity instanceof LivingEntity) {
                        this.shooter = (LivingEntity)entity;
                    }
                });
            }
            if (this.shooter != null) {
                if (this.shooter.isFallFlying()) {
                    vec3d = this.shooter.getRotationVector();
                    double d = 1.5;
                    double e = 0.1;
                    Vec3d vec3d2 = this.shooter.getVelocity();
                    this.shooter.setVelocity(vec3d2.add(vec3d.x * 0.1 + (vec3d.x * 1.5 - vec3d2.x) * 0.5, vec3d.y * 0.1 + (vec3d.y * 1.5 - vec3d2.y) * 0.5, vec3d.z * 0.1 + (vec3d.z * 1.5 - vec3d2.z) * 0.5));
                }
                this.setPosition(this.shooter.x, this.shooter.y, this.shooter.z);
                this.setVelocity(this.shooter.getVelocity());
            }
        } else {
            if (!this.wasShotAtAngle()) {
                this.setVelocity(this.getVelocity().multiply(1.15, 1.0, 1.15).add(0.0, 0.04, 0.0));
            }
            this.move(MovementType.SELF, this.getVelocity());
        }
        vec3d = this.getVelocity();
        HitResult hitResult = ProjectileUtil.getCollision((Entity)this, this.getBoundingBox().stretch(vec3d).expand(1.0), entity -> !entity.isSpectator() && entity.isAlive() && entity.collides(), RayTraceContext.ShapeType.COLLIDER, true);
        if (!this.noClip) {
            this.handleCollision(hitResult);
            this.velocityDirty = true;
        }
        float f = MathHelper.sqrt(FireworkEntity.squaredHorizontalLength(vec3d));
        this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d.y, f) * 57.2957763671875);
        while (this.pitch - this.prevPitch < -180.0f) {
            this.prevPitch -= 360.0f;
        }
        while (this.pitch - this.prevPitch >= 180.0f) {
            this.prevPitch += 360.0f;
        }
        while (this.yaw - this.prevYaw < -180.0f) {
            this.prevYaw -= 360.0f;
        }
        while (this.yaw - this.prevYaw >= 180.0f) {
            this.prevYaw += 360.0f;
        }
        this.pitch = MathHelper.lerp(0.2f, this.prevPitch, this.pitch);
        this.yaw = MathHelper.lerp(0.2f, this.prevYaw, this.yaw);
        if (this.life == 0 && !this.isSilent()) {
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0f, 1.0f);
        }
        ++this.life;
        if (this.world.isClient && this.life % 2 < 2) {
            this.world.addParticle(ParticleTypes.FIREWORK, this.x, this.y - 0.3, this.z, this.random.nextGaussian() * 0.05, -this.getVelocity().y * 0.5, this.random.nextGaussian() * 0.05);
        }
        if (!this.world.isClient && this.life > this.lifeTime) {
            this.explodeAndRemove();
        }
    }

    private void explodeAndRemove() {
        this.world.sendEntityStatus(this, (byte)17);
        this.explode();
        this.remove();
    }

    protected void handleCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY && !this.world.isClient) {
            this.explodeAndRemove();
        } else if (this.collided) {
            BlockPos blockPos = hitResult.getType() == HitResult.Type.BLOCK ? new BlockPos(((BlockHitResult)hitResult).getBlockPos()) : new BlockPos(this);
            this.world.getBlockState(blockPos).onEntityCollision(this.world, blockPos, this);
            if (this.hasExplosionEffects()) {
                this.explodeAndRemove();
            }
        }
    }

    private boolean hasExplosionEffects() {
        ItemStack itemStack = this.dataTracker.get(ITEM);
        CompoundTag compoundTag = itemStack.isEmpty() ? null : itemStack.getSubCompoundTag("Fireworks");
        ListTag listTag = compoundTag != null ? compoundTag.getList("Explosions", 10) : null;
        return listTag != null && !listTag.isEmpty();
    }

    private void explode() {
        ListTag listTag;
        float f = 0.0f;
        ItemStack itemStack = this.dataTracker.get(ITEM);
        CompoundTag compoundTag = itemStack.isEmpty() ? null : itemStack.getSubCompoundTag("Fireworks");
        ListTag listTag2 = listTag = compoundTag != null ? compoundTag.getList("Explosions", 10) : null;
        if (listTag != null && !listTag.isEmpty()) {
            f = 5.0f + (float)(listTag.size() * 2);
        }
        if (f > 0.0f) {
            if (this.shooter != null) {
                this.shooter.damage(DamageSource.FIREWORKS, 5.0f + (float)(listTag.size() * 2));
            }
            double d = 5.0;
            Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
            List<LivingEntity> list = this.world.getEntities(LivingEntity.class, this.getBoundingBox().expand(5.0));
            for (LivingEntity livingEntity : list) {
                if (livingEntity == this.shooter || this.squaredDistanceTo(livingEntity) > 25.0) continue;
                boolean bl = false;
                for (int i = 0; i < 2; ++i) {
                    Vec3d vec3d2 = new Vec3d(livingEntity.x, livingEntity.y + (double)livingEntity.getHeight() * 0.5 * (double)i, livingEntity.z);
                    BlockHitResult hitResult = this.world.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this));
                    if (((HitResult)hitResult).getType() != HitResult.Type.MISS) continue;
                    bl = true;
                    break;
                }
                if (!bl) continue;
                float g = f * (float)Math.sqrt((5.0 - (double)this.distanceTo(livingEntity)) / 5.0);
                livingEntity.damage(DamageSource.FIREWORKS, g);
            }
        }
    }

    private boolean wasShotByEntity() {
        return this.dataTracker.get(SHOOTER_ENTITY_ID).isPresent();
    }

    public boolean wasShotAtAngle() {
        return this.dataTracker.get(SHOT_AT_ANGLE);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 17 && this.world.isClient) {
            if (!this.hasExplosionEffects()) {
                for (int i = 0; i < this.random.nextInt(3) + 2; ++i) {
                    this.world.addParticle(ParticleTypes.POOF, this.x, this.y, this.z, this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
                }
            } else {
                ItemStack itemStack = this.dataTracker.get(ITEM);
                CompoundTag compoundTag = itemStack.isEmpty() ? null : itemStack.getSubCompoundTag("Fireworks");
                Vec3d vec3d = this.getVelocity();
                this.world.addFireworkParticle(this.x, this.y, this.z, vec3d.x, vec3d.y, vec3d.z, compoundTag);
            }
        }
        super.handleStatus(b);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        compoundTag.putInt("Life", this.life);
        compoundTag.putInt("LifeTime", this.lifeTime);
        ItemStack itemStack = this.dataTracker.get(ITEM);
        if (!itemStack.isEmpty()) {
            compoundTag.put("FireworksItem", itemStack.toTag(new CompoundTag()));
        }
        compoundTag.putBoolean("ShotAtAngle", this.dataTracker.get(SHOT_AT_ANGLE));
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        this.life = compoundTag.getInt("Life");
        this.lifeTime = compoundTag.getInt("LifeTime");
        ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("FireworksItem"));
        if (!itemStack.isEmpty()) {
            this.dataTracker.set(ITEM, itemStack);
        }
        if (compoundTag.containsKey("ShotAtAngle")) {
            this.dataTracker.set(SHOT_AT_ANGLE, compoundTag.getBoolean("ShotAtAngle"));
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getStack() {
        ItemStack itemStack = this.dataTracker.get(ITEM);
        return itemStack.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : itemStack;
    }

    @Override
    public boolean canPlayerAttack() {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void setVelocity(double d, double e, double f, float g, float h) {
        float i = MathHelper.sqrt(d * d + e * e + f * f);
        d /= (double)i;
        e /= (double)i;
        f /= (double)i;
        d += this.random.nextGaussian() * (double)0.0075f * (double)h;
        e += this.random.nextGaussian() * (double)0.0075f * (double)h;
        f += this.random.nextGaussian() * (double)0.0075f * (double)h;
        this.setVelocity(d *= (double)g, e *= (double)g, f *= (double)g);
    }
}

