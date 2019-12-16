/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.thrown;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class ThrownEntity
extends Entity
implements Projectile {
    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    protected boolean inGround;
    public int shake;
    protected LivingEntity owner;
    private UUID ownerUuid;
    private Entity field_7637;
    private int field_7638;

    protected ThrownEntity(EntityType<? extends ThrownEntity> type, World world) {
        super(type, world);
    }

    protected ThrownEntity(EntityType<? extends ThrownEntity> type, double x, double y, double z, World world) {
        this(type, world);
        this.setPosition(x, y, z);
    }

    protected ThrownEntity(EntityType<? extends ThrownEntity> type, LivingEntity owner, World world) {
        this(type, owner.getX(), owner.getEyeY() - (double)0.1f, owner.getZ(), world);
        this.owner = owner;
        this.ownerUuid = owner.getUuid();
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

    public void setProperties(Entity user, float pitch, float yaw, float f, float g, float h) {
        float i = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float j = -MathHelper.sin((pitch + f) * ((float)Math.PI / 180));
        float k = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        this.setVelocity(i, j, k, g, h);
        Vec3d vec3d = user.getVelocity();
        this.setVelocity(this.getVelocity().add(vec3d.x, user.onGround ? 0.0 : vec3d.y, vec3d.z));
    }

    @Override
    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        Vec3d vec3d = new Vec3d(x, y, z).normalize().add(this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence).multiply(speed);
        this.setVelocity(vec3d);
        float f = MathHelper.sqrt(ThrownEntity.squaredHorizontalLength(vec3d));
        this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d.y, f) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.yaw = (float)(MathHelper.atan2(x, z) * 57.2957763671875);
            this.pitch = (float)(MathHelper.atan2(y, f) * 57.2957763671875);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
        }
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
        for (Entity entity2 : this.world.getEntities(this, box, entity -> !entity.isSpectator() && entity.collides())) {
            if (entity2 == this.field_7637) {
                ++this.field_7638;
                break;
            }
            if (this.owner == null || this.age >= 2 || this.field_7637 != null) continue;
            this.field_7637 = entity2;
            this.field_7638 = 3;
            break;
        }
        HitResult hitResult = ProjectileUtil.getCollision((Entity)this, box, entity -> !entity.isSpectator() && entity.collides() && entity != this.field_7637, RayTraceContext.ShapeType.OUTLINE, true);
        if (this.field_7637 != null && this.field_7638-- <= 0) {
            this.field_7637 = null;
        }
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
        if (this.isInsideWater()) {
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
        this.setPosition(d, e, f);
    }

    protected float getGravity() {
        return 0.03f;
    }

    protected abstract void onCollision(HitResult var1);

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        tag.putInt("xTile", this.blockX);
        tag.putInt("yTile", this.blockY);
        tag.putInt("zTile", this.blockZ);
        tag.putByte("shake", (byte)this.shake);
        tag.putBoolean("inGround", this.inGround);
        if (this.ownerUuid != null) {
            tag.put("owner", NbtHelper.fromUuid(this.ownerUuid));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        this.blockX = tag.getInt("xTile");
        this.blockY = tag.getInt("yTile");
        this.blockZ = tag.getInt("zTile");
        this.shake = tag.getByte("shake") & 0xFF;
        this.inGround = tag.getBoolean("inGround");
        this.owner = null;
        if (tag.contains("owner", 10)) {
            this.ownerUuid = NbtHelper.toUuid(tag.getCompound("owner"));
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if ((this.owner == null || this.owner.removed) && this.ownerUuid != null && this.world instanceof ServerWorld) {
            Entity entity = ((ServerWorld)this.world).getEntity(this.ownerUuid);
            this.owner = entity instanceof LivingEntity ? (LivingEntity)entity : null;
        }
        return this.owner;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

