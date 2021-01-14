/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=FlyingItemEntity.class)})
public class EyeOfEnderEntity
extends Entity
implements FlyingItemEntity {
    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(EyeOfEnderEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private double targetX;
    private double targetY;
    private double targetZ;
    private int lifespan;
    private boolean dropsItem;

    public EyeOfEnderEntity(EntityType<? extends EyeOfEnderEntity> entityType, World world) {
        super(entityType, world);
    }

    public EyeOfEnderEntity(World world, double x, double y, double z) {
        this((EntityType<? extends EyeOfEnderEntity>)EntityType.EYE_OF_ENDER, world);
        this.lifespan = 0;
        this.setPosition(x, y, z);
    }

    public void setItem(ItemStack stack2) {
        if (stack2.getItem() != Items.ENDER_EYE || stack2.hasTag()) {
            this.getDataTracker().set(ITEM, Util.make(stack2.copy(), stack -> stack.setCount(1)));
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
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(d)) {
            d = 4.0;
        }
        return distance < (d *= 64.0) * d;
    }

    /**
     * Sets where the eye will fly towards.
     * If close enough, it will fly directly towards it, otherwise, it will fly upwards, in the direction of the BlockPos.
     * 
     * @param pos the block the eye of ender is drawn towards
     */
    public void initTargetPos(BlockPos pos) {
        double g;
        double d = pos.getX();
        int i = pos.getY();
        double e = pos.getZ();
        double f = d - this.getX();
        float h = MathHelper.sqrt(f * f + (g = e - this.getZ()) * g);
        if (h > 12.0f) {
            this.targetX = this.getX() + f / (double)h * 12.0;
            this.targetZ = this.getZ() + g / (double)h * 12.0;
            this.targetY = this.getY() + 8.0;
        } else {
            this.targetX = d;
            this.targetY = i;
            this.targetZ = e;
        }
        this.lifespan = 0;
        this.dropsItem = this.random.nextInt(5) > 0;
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
        super.tick();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        float g = MathHelper.sqrt(EyeOfEnderEntity.squaredHorizontalLength(vec3d));
        this.pitch = ProjectileEntity.updateRotation(this.prevPitch, (float)(MathHelper.atan2(vec3d.y, g) * 57.2957763671875));
        this.yaw = ProjectileEntity.updateRotation(this.prevYaw, (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
        if (!this.world.isClient) {
            double h = this.targetX - d;
            double i = this.targetZ - f;
            float j = (float)Math.sqrt(h * h + i * i);
            float k = (float)MathHelper.atan2(i, h);
            double l = MathHelper.lerp(0.0025, (double)g, (double)j);
            double m = vec3d.y;
            if (j < 1.0f) {
                l *= 0.8;
                m *= 0.8;
            }
            int n = this.getY() < this.targetY ? 1 : -1;
            vec3d = new Vec3d(Math.cos(k) * l, m + ((double)n - m) * (double)0.015f, Math.sin(k) * l);
            this.setVelocity(vec3d);
        }
        float o = 0.25f;
        if (this.isTouchingWater()) {
            for (int p = 0; p < 4; ++p) {
                this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }
        } else {
            this.world.addParticle(ParticleTypes.PORTAL, d - vec3d.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3, e - vec3d.y * 0.25 - 0.5, f - vec3d.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3, vec3d.x, vec3d.y, vec3d.z);
        }
        if (!this.world.isClient) {
            this.setPosition(d, e, f);
            ++this.lifespan;
            if (this.lifespan > 80 && !this.world.isClient) {
                this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f);
                this.remove();
                if (this.dropsItem) {
                    this.world.spawnEntity(new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), this.getStack()));
                } else {
                    this.world.syncWorldEvent(2003, this.getBlockPos(), 0);
                }
            }
        } else {
            this.setPos(d, e, f);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        ItemStack itemStack = this.getTrackedItem();
        if (!itemStack.isEmpty()) {
            nbt.put("Item", itemStack.writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("Item"));
        this.setItem(itemStack);
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
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

