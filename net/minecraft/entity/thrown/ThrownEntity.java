/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.thrown;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ThrownEntity
extends Projectile {
    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    protected boolean inGround;
    private int shake;
    private boolean field_21975;

    protected ThrownEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super((EntityType<? extends Projectile>)entityType, world);
    }

    protected ThrownEntity(EntityType<? extends ThrownEntity> type, double x, double y, double z, World world) {
        this(type, world);
        this.updatePosition(x, y, z);
    }

    protected ThrownEntity(EntityType<? extends ThrownEntity> type, LivingEntity owner, World world) {
        this(type, owner.getX(), owner.getEyeY() - (double)0.1f, owner.getZ(), world);
        this.setOwner(owner);
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
        float j;
        super.tick();
        if (this.shake > 0) {
            --this.shake;
        }
        if (this.inGround) {
            this.inGround = false;
            this.setVelocity(this.getVelocity().multiply(this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f));
        }
        Box box = this.getBoundingBox().stretch(this.getVelocity()).expand(1.0);
        Entity entity3 = this.getOwner();
        if (entity3 == null) {
            this.field_21975 = true;
        } else if (!this.field_21975) {
            boolean bl = false;
            for (Entity entity22 : this.world.getEntities(this, box, entity -> !entity.isSpectator() && entity.collides())) {
                if (!this.method_24354(entity22, entity3)) continue;
                bl = true;
                break;
            }
            if (!bl) {
                this.field_21975 = true;
            }
        }
        Predicate<Entity> predicate = entity2 -> !entity2.isSpectator() && entity2.collides() && (this.field_21975 || !this.method_24354((Entity)entity2, entity3));
        HitResult hitResult = ProjectileUtil.getCollision((Entity)this, box, predicate, RayTraceContext.ShapeType.OUTLINE, true);
        if (hitResult.getType() != HitResult.Type.MISS) {
            if (hitResult.getType() == HitResult.Type.BLOCK && this.world.getBlockState(((BlockHitResult)hitResult).getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
                this.setInNetherPortal(((BlockHitResult)hitResult).getBlockPos());
            } else {
                this.onCollision(hitResult);
            }
        }
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        float g = MathHelper.sqrt(ThrownEntity.squaredHorizontalLength(vec3d));
        this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d.y, g) * 57.2957763671875);
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
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                float h = 0.25f;
                this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }
            j = 0.8f;
        } else {
            j = 0.99f;
        }
        this.setVelocity(vec3d.multiply(j));
        if (!this.hasNoGravity()) {
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, vec3d2.y - (double)this.getGravity(), vec3d2.z);
        }
        this.updatePosition(d, e, f);
    }

    private boolean method_24354(Entity entity, Entity entity2) {
        return entity == entity2 || entity.getPassengerList().contains(entity2);
    }

    protected float getGravity() {
        return 0.03f;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("xTile", this.blockX);
        tag.putInt("yTile", this.blockY);
        tag.putInt("zTile", this.blockZ);
        tag.putByte("shake", (byte)this.shake);
        tag.putBoolean("inGround", this.inGround);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.blockX = tag.getInt("xTile");
        this.blockY = tag.getInt("yTile");
        this.blockZ = tag.getInt("zTile");
        this.shake = tag.getByte("shake") & 0xFF;
        this.inGround = tag.getBoolean("inGround");
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

