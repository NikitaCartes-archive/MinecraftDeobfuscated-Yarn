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
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TagHelper;
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

    protected ThrownEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
    }

    protected ThrownEntity(EntityType<? extends ThrownEntity> entityType, double d, double e, double f, World world) {
        this(entityType, world);
        this.setPosition(d, e, f);
    }

    protected ThrownEntity(EntityType<? extends ThrownEntity> entityType, LivingEntity livingEntity, World world) {
        this(entityType, livingEntity.x, livingEntity.y + (double)livingEntity.getStandingEyeHeight() - (double)0.1f, livingEntity.z, world);
        this.owner = livingEntity;
        this.ownerUuid = livingEntity.getUuid();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderAtDistance(double d) {
        double e = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(e)) {
            e = 4.0;
        }
        return d < (e *= 64.0) * e;
    }

    public void setProperties(Entity entity, float f, float g, float h, float i, float j) {
        float k = -MathHelper.sin(g * ((float)Math.PI / 180)) * MathHelper.cos(f * ((float)Math.PI / 180));
        float l = -MathHelper.sin((f + h) * ((float)Math.PI / 180));
        float m = MathHelper.cos(g * ((float)Math.PI / 180)) * MathHelper.cos(f * ((float)Math.PI / 180));
        this.setVelocity(k, l, m, i, j);
        Vec3d vec3d = entity.getVelocity();
        this.setVelocity(this.getVelocity().add(vec3d.x, entity.onGround ? 0.0 : vec3d.y, vec3d.z));
    }

    @Override
    public void setVelocity(double d, double e, double f, float g, float h) {
        Vec3d vec3d = new Vec3d(d, e, f).normalize().add(this.random.nextGaussian() * (double)0.0075f * (double)h, this.random.nextGaussian() * (double)0.0075f * (double)h, this.random.nextGaussian() * (double)0.0075f * (double)h).multiply(g);
        this.setVelocity(vec3d);
        float i = MathHelper.sqrt(ThrownEntity.squaredHorizontalLength(vec3d));
        this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d.y, i) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
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
        float h;
        this.prevRenderX = this.x;
        this.prevRenderY = this.y;
        this.prevRenderZ = this.z;
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
                this.setInPortal(((BlockHitResult)hitResult).getBlockPos());
            } else {
                this.onCollision(hitResult);
            }
        }
        Vec3d vec3d = this.getVelocity();
        this.x += vec3d.x;
        this.y += vec3d.y;
        this.z += vec3d.z;
        float f = MathHelper.sqrt(ThrownEntity.squaredHorizontalLength(vec3d));
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
        if (this.isInsideWater()) {
            for (int i = 0; i < 4; ++i) {
                float g = 0.25f;
                this.world.addParticle(ParticleTypes.BUBBLE, this.x - vec3d.x * 0.25, this.y - vec3d.y * 0.25, this.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }
            h = 0.8f;
        } else {
            h = 0.99f;
        }
        this.setVelocity(vec3d.multiply(h));
        if (!this.hasNoGravity()) {
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, vec3d2.y - (double)this.getGravity(), vec3d2.z);
        }
        this.setPosition(this.x, this.y, this.z);
    }

    protected float getGravity() {
        return 0.03f;
    }

    protected abstract void onCollision(HitResult var1);

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        compoundTag.putInt("xTile", this.blockX);
        compoundTag.putInt("yTile", this.blockY);
        compoundTag.putInt("zTile", this.blockZ);
        compoundTag.putByte("shake", (byte)this.shake);
        compoundTag.putByte("inGround", (byte)(this.inGround ? 1 : 0));
        if (this.ownerUuid != null) {
            compoundTag.put("owner", TagHelper.serializeUuid(this.ownerUuid));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        this.blockX = compoundTag.getInt("xTile");
        this.blockY = compoundTag.getInt("yTile");
        this.blockZ = compoundTag.getInt("zTile");
        this.shake = compoundTag.getByte("shake") & 0xFF;
        this.inGround = compoundTag.getByte("inGround") == 1;
        this.owner = null;
        if (compoundTag.containsKey("owner", 10)) {
            this.ownerUuid = TagHelper.deserializeUuid(compoundTag.getCompound("owner"));
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

