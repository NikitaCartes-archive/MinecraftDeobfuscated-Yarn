/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Material;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class LlamaSpitEntity
extends Entity
implements Projectile {
    public LlamaEntity owner;
    private CompoundTag tag;

    public LlamaSpitEntity(EntityType<? extends LlamaSpitEntity> entityType, World world) {
        super(entityType, world);
    }

    public LlamaSpitEntity(World world, LlamaEntity llamaEntity) {
        this((EntityType<? extends LlamaSpitEntity>)EntityType.LLAMA_SPIT, world);
        this.owner = llamaEntity;
        this.setPosition(llamaEntity.getX() - (double)(llamaEntity.getWidth() + 1.0f) * 0.5 * (double)MathHelper.sin(llamaEntity.bodyYaw * ((float)Math.PI / 180)), llamaEntity.getEyeY() - (double)0.1f, llamaEntity.getZ() + (double)(llamaEntity.getWidth() + 1.0f) * 0.5 * (double)MathHelper.cos(llamaEntity.bodyYaw * ((float)Math.PI / 180)));
    }

    @Environment(value=EnvType.CLIENT)
    public LlamaSpitEntity(World world, double d, double e, double f, double g, double h, double i) {
        this((EntityType<? extends LlamaSpitEntity>)EntityType.LLAMA_SPIT, world);
        this.setPosition(d, e, f);
        for (int j = 0; j < 7; ++j) {
            double k = 0.4 + 0.1 * (double)j;
            world.addParticle(ParticleTypes.SPIT, d, e, f, g * k, h, i * k);
        }
        this.setVelocity(g, h, i);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tag != null) {
            this.readTag();
        }
        Vec3d vec3d = this.getVelocity();
        HitResult hitResult = ProjectileUtil.getCollision((Entity)this, this.getBoundingBox().stretch(vec3d).expand(1.0), entity -> !entity.isSpectator() && entity != this.owner, RayTraceContext.ShapeType.OUTLINE, true);
        if (hitResult != null) {
            this.method_7481(hitResult);
        }
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        float g = MathHelper.sqrt(LlamaSpitEntity.squaredHorizontalLength(vec3d));
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
        float h = 0.99f;
        float i = 0.06f;
        if (!this.world.containsBlockWithMaterial(this.getBoundingBox(), Material.AIR)) {
            this.remove();
            return;
        }
        if (this.isInsideWaterOrBubbleColumn()) {
            this.remove();
            return;
        }
        this.setVelocity(vec3d.multiply(0.99f));
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.06f, 0.0));
        }
        this.setPosition(d, e, f);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double d, double e, double f) {
        this.setVelocity(d, e, f);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float g = MathHelper.sqrt(d * d + f * f);
            this.pitch = (float)(MathHelper.atan2(e, g) * 57.2957763671875);
            this.yaw = (float)(MathHelper.atan2(d, f) * 57.2957763671875);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.setPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
        }
    }

    @Override
    public void setVelocity(double d, double e, double f, float g, float h) {
        Vec3d vec3d = new Vec3d(d, e, f).normalize().add(this.random.nextGaussian() * (double)0.0075f * (double)h, this.random.nextGaussian() * (double)0.0075f * (double)h, this.random.nextGaussian() * (double)0.0075f * (double)h).multiply(g);
        this.setVelocity(vec3d);
        float i = MathHelper.sqrt(LlamaSpitEntity.squaredHorizontalLength(vec3d));
        this.yaw = (float)(MathHelper.atan2(vec3d.x, f) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d.y, i) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    public void method_7481(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY && this.owner != null) {
            ((EntityHitResult)hitResult).getEntity().damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 1.0f);
        } else if (type == HitResult.Type.BLOCK && !this.world.isClient) {
            this.remove();
        }
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag compoundTag) {
        if (compoundTag.contains("Owner", 10)) {
            this.tag = compoundTag.getCompound("Owner");
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag compoundTag) {
        if (this.owner != null) {
            CompoundTag compoundTag2 = new CompoundTag();
            UUID uUID = this.owner.getUuid();
            compoundTag2.putUuid("OwnerUUID", uUID);
            compoundTag.put("Owner", compoundTag2);
        }
    }

    private void readTag() {
        if (this.tag != null && this.tag.containsUuid("OwnerUUID")) {
            UUID uUID = this.tag.getUuid("OwnerUUID");
            List<LlamaEntity> list = this.world.getNonSpectatingEntities(LlamaEntity.class, this.getBoundingBox().expand(15.0));
            for (LlamaEntity llamaEntity : list) {
                if (!llamaEntity.getUuid().equals(uUID)) continue;
                this.owner = llamaEntity;
                break;
            }
        }
        this.tag = null;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

