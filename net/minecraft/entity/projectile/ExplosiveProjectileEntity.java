/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity
extends ProjectileEntity {
    public double posX;
    public double posY;
    public double posZ;

    protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super((EntityType<? extends ProjectileEntity>)entityType, world);
    }

    public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, double directionX, double directionY, double directionZ, World world) {
        this(type, world);
        this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
        this.refreshPosition();
        double d = MathHelper.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
        if (d != 0.0) {
            this.posX = directionX / d * 0.1;
            this.posY = directionY / d * 0.1;
            this.posZ = directionZ / d * 0.1;
        }
    }

    public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, LivingEntity owner, double directionX, double directionY, double directionZ, World world) {
        this(type, owner.getX(), owner.getY(), owner.getZ(), directionX, directionY, directionZ, world);
        this.setOwner(owner);
        this.setRotation(owner.yaw, owner.pitch);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(d)) {
            d = 4.0;
        }
        return distance < (d *= 64.0) * d;
    }

    @Override
    public void tick() {
        HitResult hitResult;
        Entity entity = this.getOwner();
        if (!this.world.isClient && (entity != null && entity.removed || !this.world.isChunkLoaded(this.getBlockPos()))) {
            this.remove();
            return;
        }
        super.tick();
        if (this.isBurning()) {
            this.setOnFireFor(1);
        }
        if ((hitResult = ProjectileUtil.getCollision(this, this::method_26958)).getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }
        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        ProjectileUtil.method_7484(this, 0.2f);
        float g = this.getDrag();
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                float h = 0.25f;
                this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }
            g = 0.8f;
        }
        this.setVelocity(vec3d.add(this.posX, this.posY, this.posZ).multiply(g));
        this.world.addParticle(this.getParticleType(), d, e + 0.5, f, 0.0, 0.0, 0.0);
        this.updatePosition(d, e, f);
    }

    @Override
    protected boolean method_26958(Entity entity) {
        return super.method_26958(entity) && !entity.noClip;
    }

    protected boolean isBurning() {
        return true;
    }

    protected ParticleEffect getParticleType() {
        return ParticleTypes.SMOKE;
    }

    protected float getDrag() {
        return 0.95f;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.put("power", this.toListTag(this.posX, this.posY, this.posZ));
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        ListTag listTag;
        super.readCustomDataFromTag(tag);
        if (tag.contains("power", 9) && (listTag = tag.getList("power", 6)).size() == 3) {
            this.posX = listTag.getDouble(0);
            this.posY = listTag.getDouble(1);
            this.posZ = listTag.getDouble(2);
        }
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public float getTargetingMargin() {
        return 1.0f;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        Entity entity = source.getAttacker();
        if (entity != null) {
            Vec3d vec3d = entity.getRotationVector();
            this.setVelocity(vec3d);
            this.posX = vec3d.x * 0.1;
            this.posY = vec3d.y * 0.1;
            this.posZ = vec3d.z * 0.1;
            this.setOwner(entity);
            return true;
        }
        return false;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        int i = entity == null ? 0 : entity.getEntityId();
        return new EntitySpawnS2CPacket(this.getEntityId(), this.getUuid(), this.getX(), this.getY(), this.getZ(), this.pitch, this.yaw, this.getType(), i, new Vec3d(this.posX, this.posY, this.posZ));
    }
}

