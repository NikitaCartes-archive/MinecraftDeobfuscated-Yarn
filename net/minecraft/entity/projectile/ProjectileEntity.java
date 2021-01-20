/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.projectile;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class ProjectileEntity
extends Entity {
    private UUID ownerUuid;
    private int ownerEntityId;
    private boolean leftOwner;

    ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getUuid();
            this.ownerEntityId = entity.getId();
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUuid != null && this.world instanceof ServerWorld) {
            return ((ServerWorld)this.world).getEntity(this.ownerUuid);
        }
        if (this.ownerEntityId != 0) {
            return this.world.getEntityById(this.ownerEntityId);
        }
        return null;
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        if (this.ownerUuid != null) {
            tag.putUuid("Owner", this.ownerUuid);
        }
        if (this.leftOwner) {
            tag.putBoolean("LeftOwner", true);
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.containsUuid("Owner")) {
            this.ownerUuid = tag.getUuid("Owner");
        }
        this.leftOwner = tag.getBoolean("LeftOwner");
    }

    @Override
    public void tick() {
        if (!this.leftOwner) {
            this.leftOwner = this.shouldLeaveOwner();
        }
        super.tick();
    }

    private boolean shouldLeaveOwner() {
        Entity entity2 = this.getOwner();
        if (entity2 != null) {
            for (Entity entity22 : this.world.getOtherEntities(this, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), entity -> !entity.isSpectator() && entity.collides())) {
                if (entity22.getRootVehicle() != entity2.getRootVehicle()) continue;
                return false;
            }
        }
        return true;
    }

    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        Vec3d vec3d = new Vec3d(x, y, z).normalize().add(this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence).multiply(speed);
        this.setVelocity(vec3d);
        float f = MathHelper.sqrt(ProjectileEntity.squaredHorizontalLength(vec3d));
        this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d.y, f) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.emitGameEvent(this.getOwner(), GameEvent.PROJECTILE_SHOOT);
    }

    public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        float f = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float g = -MathHelper.sin((pitch + roll) * ((float)Math.PI / 180));
        float h = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        this.setVelocity(f, g, h, modifierZ, modifierXYZ);
        Vec3d vec3d = user.getVelocity();
        this.setVelocity(this.getVelocity().add(vec3d.x, user.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
    }

    protected void onCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult)hitResult);
        } else if (type == HitResult.Type.BLOCK) {
            this.onBlockHit((BlockHitResult)hitResult);
        }
        if (type != HitResult.Type.MISS) {
            this.emitGameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND);
        }
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
    }

    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockHitResult blockHitResult2 = blockHitResult;
        BlockState blockState = this.world.getBlockState(blockHitResult2.getBlockPos());
        blockState.onProjectileHit(this.world, blockState, blockHitResult2, this);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.pitch = (float)(MathHelper.atan2(y, f) * 57.2957763671875);
            this.yaw = (float)(MathHelper.atan2(x, z) * 57.2957763671875);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
        }
    }

    protected boolean canHit(Entity entity) {
        if (entity.isSpectator() || !entity.isAlive() || !entity.collides()) {
            return false;
        }
        Entity entity2 = this.getOwner();
        return entity2 == null || this.leftOwner || !entity2.isConnectedThroughVehicle(entity);
    }

    protected void updateRotation() {
        Vec3d vec3d = this.getVelocity();
        float f = MathHelper.sqrt(ProjectileEntity.squaredHorizontalLength(vec3d));
        this.pitch = ProjectileEntity.updateRotation(this.prevPitch, (float)(MathHelper.atan2(vec3d.y, f) * 57.2957763671875));
        this.yaw = ProjectileEntity.updateRotation(this.prevYaw, (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
    }

    protected static float updateRotation(float prevRot, float newRot) {
        while (newRot - prevRot < -180.0f) {
            prevRot -= 360.0f;
        }
        while (newRot - prevRot >= 180.0f) {
            prevRot += 360.0f;
        }
        return MathHelper.lerp(0.2f, prevRot, newRot);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        return new EntitySpawnS2CPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        Entity entity = this.world.getEntityById(packet.getEntityData());
        if (entity != null) {
            this.setOwner(entity);
        }
    }
}

