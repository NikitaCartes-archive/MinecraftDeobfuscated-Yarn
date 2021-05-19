/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.projectile;

import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class ProjectileEntity
extends Entity {
    @Nullable
    private UUID ownerUuid;
    @Nullable
    private Entity owner;
    private boolean leftOwner;
    private boolean shot;

    ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getUuid();
            this.owner = entity;
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.owner != null) {
            return this.owner;
        }
        if (this.ownerUuid != null && this.world instanceof ServerWorld) {
            this.owner = ((ServerWorld)this.world).getEntity(this.ownerUuid);
            return this.owner;
        }
        return null;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.ownerUuid != null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }
        if (this.leftOwner) {
            nbt.putBoolean("LeftOwner", true);
        }
        nbt.putBoolean("HasBeenShot", this.shot);
    }

    protected boolean isOwner(Entity entity) {
        return entity.getUuid().equals(this.ownerUuid);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }
        this.leftOwner = nbt.getBoolean("LeftOwner");
        this.shot = nbt.getBoolean("HasBeenShot");
    }

    @Override
    public void tick() {
        if (!this.shot) {
            this.emitGameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner(), this.getBlockPos());
            this.shot = true;
        }
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
        double d = Math.sqrt(ProjectileEntity.squaredHorizontalLength(vec3d));
        this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
        this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 57.2957763671875));
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
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
            this.emitGameEvent(GameEvent.PROJECTILE_LAND, this.getOwner());
        }
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
    }

    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockState blockState = this.world.getBlockState(blockHitResult.getBlockPos());
        blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
    }

    @Override
    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            double d = Math.sqrt(x * x + z * z);
            this.setPitch((float)(MathHelper.atan2(y, d) * 57.2957763671875));
            this.setYaw((float)(MathHelper.atan2(x, z) * 57.2957763671875));
            this.prevPitch = this.getPitch();
            this.prevYaw = this.getYaw();
            this.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
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
        double d = Math.sqrt(ProjectileEntity.squaredHorizontalLength(vec3d));
        this.setPitch(ProjectileEntity.updateRotation(this.prevPitch, (float)(MathHelper.atan2(vec3d.y, d) * 57.2957763671875)));
        this.setYaw(ProjectileEntity.updateRotation(this.prevYaw, (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875)));
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
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        Entity entity = this.world.getEntityById(packet.getEntityData());
        if (entity != null) {
            this.setOwner(entity);
        }
    }

    @Override
    public boolean canModifyAt(World world, BlockPos pos) {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntity) {
            return entity.canModifyAt(world, pos);
        }
        return entity == null || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }
}

