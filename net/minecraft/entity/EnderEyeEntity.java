/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=FlyingItemEntity.class)})
public class EnderEyeEntity
extends Entity
implements FlyingItemEntity {
    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(EnderEyeEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private int useCount;
    private boolean dropsItem;

    public EnderEyeEntity(EntityType<? extends EnderEyeEntity> entityType, World world) {
        super(entityType, world);
    }

    public EnderEyeEntity(World world, double d, double e, double f) {
        this((EntityType<? extends EnderEyeEntity>)EntityType.EYE_OF_ENDER, world);
        this.useCount = 0;
        this.setPosition(d, e, f);
    }

    public void setItem(ItemStack itemStack2) {
        if (itemStack2.getItem() != Items.ENDER_EYE || itemStack2.hasTag()) {
            this.getDataTracker().set(ITEM, SystemUtil.consume(itemStack2.copy(), itemStack -> itemStack.setCount(1)));
        }
    }

    private ItemStack getTrackedItem() {
        return this.getDataTracker().get(ITEM);
    }

    @Override
    public ItemStack getStack() {
        ItemStack itemStack = this.getTrackedItem();
        return itemStack.isEmpty() ? new ItemStack(Items.ENDER_EYE) : itemStack;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(ITEM, ItemStack.EMPTY);
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

    public void moveTowards(BlockPos blockPos) {
        double g;
        double d = blockPos.getX();
        int i = blockPos.getY();
        double e = blockPos.getZ();
        double f = d - this.getX();
        float h = MathHelper.sqrt(f * f + (g = e - this.getZ()) * g);
        if (h > 12.0f) {
            this.velocityX = this.getX() + f / (double)h * 12.0;
            this.velocityZ = this.getZ() + g / (double)h * 12.0;
            this.velocityY = this.getY() + 8.0;
        } else {
            this.velocityX = d;
            this.velocityY = i;
            this.velocityZ = e;
        }
        this.useCount = 0;
        this.dropsItem = this.random.nextInt(5) > 0;
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
        super.tick();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        float g = MathHelper.sqrt(EnderEyeEntity.squaredHorizontalLength(vec3d));
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
        if (!this.world.isClient) {
            double h = this.velocityX - d;
            double i = this.velocityZ - f;
            float j = (float)Math.sqrt(h * h + i * i);
            float k = (float)MathHelper.atan2(i, h);
            double l = MathHelper.lerp(0.0025, (double)g, (double)j);
            double m = vec3d.y;
            if (j < 1.0f) {
                l *= 0.8;
                m *= 0.8;
            }
            int n = this.getY() < this.velocityY ? 1 : -1;
            vec3d = new Vec3d(Math.cos(k) * l, m + ((double)n - m) * (double)0.015f, Math.sin(k) * l);
            this.setVelocity(vec3d);
        }
        float o = 0.25f;
        if (this.isInsideWater()) {
            for (int p = 0; p < 4; ++p) {
                this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }
        } else {
            this.world.addParticle(ParticleTypes.PORTAL, d - vec3d.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3, e - vec3d.y * 0.25 - 0.5, f - vec3d.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3, vec3d.x, vec3d.y, vec3d.z);
        }
        if (!this.world.isClient) {
            this.setPosition(d, e, f);
            ++this.useCount;
            if (this.useCount > 80 && !this.world.isClient) {
                this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f);
                this.remove();
                if (this.dropsItem) {
                    this.world.spawnEntity(new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), this.getStack()));
                } else {
                    this.world.playLevelEvent(2003, new BlockPos(this), 0);
                }
            }
        } else {
            this.setPos(d, e, f);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        ItemStack itemStack = this.getTrackedItem();
        if (!itemStack.isEmpty()) {
            compoundTag.put("Item", itemStack.toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("Item"));
        this.setItem(itemStack);
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getLightmapCoordinates() {
        return 0xF000F0;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

