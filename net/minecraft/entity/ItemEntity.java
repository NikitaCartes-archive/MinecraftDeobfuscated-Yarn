/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemEntity
extends Entity {
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private int age;
    private int pickupDelay;
    private int health = 5;
    private UUID thrower;
    private UUID owner;
    public final float hoverHeight;

    public ItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType, world);
        this.hoverHeight = (float)(Math.random() * Math.PI * 2.0);
    }

    public ItemEntity(World world, double x, double y, double z) {
        this((EntityType<? extends ItemEntity>)EntityType.ITEM, world);
        this.updatePosition(x, y, z);
        this.yaw = this.random.nextFloat() * 360.0f;
        this.setVelocity(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
    }

    public ItemEntity(World world, double x, double y, double z, ItemStack stack) {
        this(world, x, y, z);
        this.setStack(stack);
    }

    @Environment(value=EnvType.CLIENT)
    private ItemEntity(ItemEntity itemEntity) {
        super(itemEntity.getType(), itemEntity.world);
        this.setStack(itemEntity.getStack().copy());
        this.copyPositionAndRotation(itemEntity);
        this.age = itemEntity.age;
        this.hoverHeight = itemEntity.hoverHeight;
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        double d;
        int i;
        if (this.getStack().isEmpty()) {
            this.remove();
            return;
        }
        super.tick();
        if (this.pickupDelay > 0 && this.pickupDelay != Short.MAX_VALUE) {
            --this.pickupDelay;
        }
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        Vec3d vec3d = this.getVelocity();
        float f = this.getStandingEyeHeight() - 0.11111111f;
        if (this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > (double)f) {
            this.applyBuoyancy();
        } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double)f) {
            this.method_24348();
        } else if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        if (this.world.isClient) {
            this.noClip = false;
        } else {
            boolean bl = this.noClip = !this.world.isSpaceEmpty(this);
            if (this.noClip) {
                this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
            }
        }
        if (!this.onGround || ItemEntity.squaredHorizontalLength(this.getVelocity()) > (double)1.0E-5f || (this.age + this.getEntityId()) % 4 == 0) {
            this.move(MovementType.SELF, this.getVelocity());
            float g = 0.98f;
            if (this.onGround) {
                g = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.98f;
            }
            this.setVelocity(this.getVelocity().multiply(g, 0.98, g));
            if (this.onGround) {
                Vec3d vec3d2 = this.getVelocity();
                if (vec3d2.y < 0.0) {
                    this.setVelocity(vec3d2.multiply(1.0, -0.5, 1.0));
                }
            }
        }
        boolean bl = MathHelper.floor(this.prevX) != MathHelper.floor(this.getX()) || MathHelper.floor(this.prevY) != MathHelper.floor(this.getY()) || MathHelper.floor(this.prevZ) != MathHelper.floor(this.getZ());
        int n = i = bl ? 2 : 40;
        if (this.age % i == 0) {
            if (this.world.getFluidState(this.getBlockPos()).isIn(FluidTags.LAVA) && !this.isFireImmune()) {
                this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
            }
            if (!this.world.isClient && this.canMerge()) {
                this.tryMerge();
            }
        }
        if (this.age != Short.MIN_VALUE) {
            ++this.age;
        }
        this.velocityDirty |= this.updateWaterState();
        if (!this.world.isClient && (d = this.getVelocity().subtract(vec3d).lengthSquared()) > 0.01) {
            this.velocityDirty = true;
        }
        if (!this.world.isClient && this.age >= 6000) {
            this.remove();
        }
    }

    private void applyBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double)0.99f, vec3d.y + (double)(vec3d.y < (double)0.06f ? 5.0E-4f : 0.0f), vec3d.z * (double)0.99f);
    }

    private void method_24348() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double)0.95f, vec3d.y + (double)(vec3d.y < (double)0.06f ? 5.0E-4f : 0.0f), vec3d.z * (double)0.95f);
    }

    private void tryMerge() {
        if (!this.canMerge()) {
            return;
        }
        List<ItemEntity> list = this.world.getEntitiesByClass(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5), itemEntity -> itemEntity != this && itemEntity.canMerge());
        for (ItemEntity itemEntity2 : list) {
            if (!itemEntity2.canMerge()) continue;
            this.tryMerge(itemEntity2);
            if (!this.removed) continue;
            break;
        }
    }

    private boolean canMerge() {
        ItemStack itemStack = this.getStack();
        return this.isAlive() && this.pickupDelay != Short.MAX_VALUE && this.age != Short.MIN_VALUE && this.age < 6000 && itemStack.getCount() < itemStack.getMaxCount();
    }

    private void tryMerge(ItemEntity other) {
        ItemStack itemStack = this.getStack();
        ItemStack itemStack2 = other.getStack();
        if (!Objects.equals(this.getOwner(), other.getOwner()) || !ItemEntity.canMerge(itemStack, itemStack2)) {
            return;
        }
        if (itemStack2.getCount() < itemStack.getCount()) {
            ItemEntity.merge(this, itemStack, other, itemStack2);
        } else {
            ItemEntity.merge(other, itemStack2, this, itemStack);
        }
    }

    public static boolean canMerge(ItemStack stack1, ItemStack stack2) {
        if (stack2.getItem() != stack1.getItem()) {
            return false;
        }
        if (stack2.getCount() + stack1.getCount() > stack2.getMaxCount()) {
            return false;
        }
        if (stack2.hasTag() ^ stack1.hasTag()) {
            return false;
        }
        return !stack2.hasTag() || stack2.getTag().equals(stack1.getTag());
    }

    public static ItemStack merge(ItemStack stack1, ItemStack stack2, int maxCount) {
        int i = Math.min(Math.min(stack1.getMaxCount(), maxCount) - stack1.getCount(), stack2.getCount());
        ItemStack itemStack = stack1.copy();
        itemStack.increment(i);
        stack2.decrement(i);
        return itemStack;
    }

    private static void merge(ItemEntity targetEntity, ItemStack stack1, ItemStack stack2) {
        ItemStack itemStack = ItemEntity.merge(stack1, stack2, 64);
        targetEntity.setStack(itemStack);
    }

    private static void merge(ItemEntity targetEntity, ItemStack targetStack, ItemEntity sourceEntity, ItemStack sourceStack) {
        ItemEntity.merge(targetEntity, targetStack, sourceStack);
        targetEntity.pickupDelay = Math.max(targetEntity.pickupDelay, sourceEntity.pickupDelay);
        targetEntity.age = Math.min(targetEntity.age, sourceEntity.age);
        if (sourceStack.isEmpty()) {
            sourceEntity.remove();
        }
    }

    @Override
    public boolean isFireImmune() {
        return this.getStack().getItem().isFireproof() || super.isFireImmune();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.getStack().isEmpty() && this.getStack().getItem() == Items.NETHER_STAR && source.isExplosive()) {
            return false;
        }
        if (!this.getStack().getItem().damage(source)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        this.health = (int)((float)this.health - amount);
        if (this.health <= 0) {
            this.remove();
        }
        return false;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        tag.putShort("Health", (short)this.health);
        tag.putShort("Age", (short)this.age);
        tag.putShort("PickupDelay", (short)this.pickupDelay);
        if (this.getThrower() != null) {
            tag.putUuid("Thrower", this.getThrower());
        }
        if (this.getOwner() != null) {
            tag.putUuid("Owner", this.getOwner());
        }
        if (!this.getStack().isEmpty()) {
            tag.put("Item", this.getStack().toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        this.health = tag.getShort("Health");
        this.age = tag.getShort("Age");
        if (tag.contains("PickupDelay")) {
            this.pickupDelay = tag.getShort("PickupDelay");
        }
        if (tag.containsUuid("Owner")) {
            this.owner = tag.getUuid("Owner");
        }
        if (tag.containsUuid("Thrower")) {
            this.thrower = tag.getUuid("Thrower");
        }
        CompoundTag compoundTag = tag.getCompound("Item");
        this.setStack(ItemStack.fromTag(compoundTag));
        if (this.getStack().isEmpty()) {
            this.remove();
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.world.isClient) {
            return;
        }
        ItemStack itemStack = this.getStack();
        Item item = itemStack.getItem();
        int i = itemStack.getCount();
        if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUuid())) && player.inventory.insertStack(itemStack)) {
            player.sendPickup(this, i);
            if (itemStack.isEmpty()) {
                this.remove();
                itemStack.setCount(i);
            }
            player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
            player.method_29499(this);
        }
    }

    @Override
    public Text getName() {
        Text text = this.getCustomName();
        if (text != null) {
            return text;
        }
        return new TranslatableText(this.getStack().getTranslationKey());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    @Nullable
    public Entity moveToWorld(ServerWorld destination) {
        Entity entity = super.moveToWorld(destination);
        if (!this.world.isClient && entity instanceof ItemEntity) {
            ((ItemEntity)entity).tryMerge();
        }
        return entity;
    }

    public ItemStack getStack() {
        return this.getDataTracker().get(STACK);
    }

    public void setStack(ItemStack stack) {
        this.getDataTracker().set(STACK, stack);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (STACK.equals(data)) {
            this.getStack().setHolder(this);
        }
    }

    @Nullable
    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable UUID uuid) {
        this.owner = uuid;
    }

    @Nullable
    public UUID getThrower() {
        return this.thrower;
    }

    public void setThrower(@Nullable UUID uuid) {
        this.thrower = uuid;
    }

    @Environment(value=EnvType.CLIENT)
    public int getAge() {
        return this.age;
    }

    public void setToDefaultPickupDelay() {
        this.pickupDelay = 10;
    }

    public void resetPickupDelay() {
        this.pickupDelay = 0;
    }

    public void setPickupDelayInfinite() {
        this.pickupDelay = Short.MAX_VALUE;
    }

    public void setPickupDelay(int pickupDelay) {
        this.pickupDelay = pickupDelay;
    }

    public boolean cannotPickup() {
        return this.pickupDelay > 0;
    }

    public void setCovetedItem() {
        this.age = -6000;
    }

    public void setDespawnImmediately() {
        this.setPickupDelayInfinite();
        this.age = 5999;
    }

    @Environment(value=EnvType.CLIENT)
    public float method_27314(float f) {
        return ((float)this.getAge() + f) / 20.0f + this.hoverHeight;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Environment(value=EnvType.CLIENT)
    public ItemEntity method_29271() {
        return new ItemEntity(this);
    }
}

