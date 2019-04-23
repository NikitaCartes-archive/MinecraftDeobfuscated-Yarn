/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public class ItemEntity
extends Entity {
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private int age;
    private int pickupDelay;
    private int health = 5;
    private UUID thrower;
    private UUID owner;
    public final float hoverHeight = (float)(Math.random() * Math.PI * 2.0);

    public ItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public ItemEntity(World world, double d, double e, double f) {
        this((EntityType<? extends ItemEntity>)EntityType.ITEM, world);
        this.setPosition(d, e, f);
        this.yaw = this.random.nextFloat() * 360.0f;
        this.setVelocity(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
    }

    public ItemEntity(World world, double d, double e, double f, ItemStack itemStack) {
        this(world, d, e, f);
        this.setStack(itemStack);
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
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        Vec3d vec3d = this.getVelocity();
        if (this.isInFluid(FluidTags.WATER)) {
            this.method_6974();
        } else if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        if (this.world.isClient) {
            this.noClip = false;
        } else {
            boolean bl = this.noClip = !this.world.doesNotCollide(this);
            if (this.noClip) {
                this.pushOutOfBlocks(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
            }
        }
        this.move(MovementType.SELF, this.getVelocity());
        boolean bl = (int)this.prevX != (int)this.x || (int)this.prevY != (int)this.y || (int)this.prevZ != (int)this.z;
        int n = i = bl ? 2 : 40;
        if (this.age % i == 0) {
            if (this.world.getFluidState(new BlockPos(this)).matches(FluidTags.LAVA)) {
                this.setVelocity((this.random.nextFloat() - this.random.nextFloat()) * 0.2f, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
                this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
            }
            if (!this.world.isClient && this.method_20397()) {
                this.tryMerge();
            }
        }
        float f = 0.98f;
        if (this.onGround) {
            f = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getSlipperiness() * 0.98f;
        }
        this.setVelocity(this.getVelocity().multiply(f, 0.98, f));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(1.0, -0.5, 1.0));
        }
        if (this.age != Short.MIN_VALUE) {
            ++this.age;
        }
        this.velocityDirty |= this.method_5713();
        if (!this.world.isClient && (d = this.getVelocity().subtract(vec3d).lengthSquared()) > 0.01) {
            this.velocityDirty = true;
        }
        if (!this.world.isClient && this.age >= 6000) {
            this.remove();
        }
    }

    private void method_6974() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double)0.99f, vec3d.y + (double)(vec3d.y < (double)0.06f ? 5.0E-4f : 0.0f), vec3d.z * (double)0.99f);
    }

    private void tryMerge() {
        List<ItemEntity> list = this.world.getEntities(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5), itemEntity -> itemEntity != this && itemEntity.method_20397());
        if (!list.isEmpty()) {
            for (ItemEntity itemEntity2 : list) {
                if (this.method_20397()) {
                    this.tryMerge(itemEntity2);
                    continue;
                }
                return;
            }
        }
    }

    private boolean method_20397() {
        ItemStack itemStack = this.getStack();
        return this.isAlive() && this.pickupDelay != Short.MAX_VALUE && this.age != Short.MIN_VALUE && this.age < 6000 && itemStack.getAmount() < itemStack.getMaxAmount();
    }

    private void tryMerge(ItemEntity itemEntity) {
        ItemStack itemStack = this.getStack();
        ItemStack itemStack2 = itemEntity.getStack();
        if (itemStack2.getItem() != itemStack.getItem()) {
            return;
        }
        if (itemStack2.getAmount() + itemStack.getAmount() > itemStack2.getMaxAmount()) {
            return;
        }
        if (itemStack2.hasTag() ^ itemStack.hasTag()) {
            return;
        }
        if (itemStack2.hasTag() && !itemStack2.getTag().equals(itemStack.getTag())) {
            return;
        }
        if (itemStack2.getAmount() < itemStack.getAmount()) {
            ItemEntity.merge(this, itemStack, itemEntity, itemStack2);
        } else {
            ItemEntity.merge(itemEntity, itemStack2, this, itemStack);
        }
    }

    private static void merge(ItemEntity itemEntity, ItemStack itemStack, ItemEntity itemEntity2, ItemStack itemStack2) {
        int i = Math.min(itemStack.getMaxAmount() - itemStack.getAmount(), itemStack2.getAmount());
        ItemStack itemStack3 = itemStack.copy();
        itemStack3.addAmount(i);
        itemEntity.setStack(itemStack3);
        itemStack2.subtractAmount(i);
        itemEntity2.setStack(itemStack2);
        itemEntity.pickupDelay = Math.max(itemEntity.pickupDelay, itemEntity2.pickupDelay);
        itemEntity.age = Math.min(itemEntity.age, itemEntity2.age);
        if (itemStack2.isEmpty()) {
            itemEntity2.remove();
        }
    }

    public void method_6980() {
        this.age = 4800;
    }

    @Override
    protected void burn(int i) {
        this.damage(DamageSource.IN_FIRE, i);
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (!this.getStack().isEmpty() && this.getStack().getItem() == Items.NETHER_STAR && damageSource.isExplosive()) {
            return false;
        }
        this.scheduleVelocityUpdate();
        this.health = (int)((float)this.health - f);
        if (this.health <= 0) {
            this.remove();
        }
        return false;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        compoundTag.putShort("Health", (short)this.health);
        compoundTag.putShort("Age", (short)this.age);
        compoundTag.putShort("PickupDelay", (short)this.pickupDelay);
        if (this.getThrower() != null) {
            compoundTag.put("Thrower", TagHelper.serializeUuid(this.getThrower()));
        }
        if (this.getOwner() != null) {
            compoundTag.put("Owner", TagHelper.serializeUuid(this.getOwner()));
        }
        if (!this.getStack().isEmpty()) {
            compoundTag.put("Item", this.getStack().toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        this.health = compoundTag.getShort("Health");
        this.age = compoundTag.getShort("Age");
        if (compoundTag.containsKey("PickupDelay")) {
            this.pickupDelay = compoundTag.getShort("PickupDelay");
        }
        if (compoundTag.containsKey("Owner", 10)) {
            this.owner = TagHelper.deserializeUuid(compoundTag.getCompound("Owner"));
        }
        if (compoundTag.containsKey("Thrower", 10)) {
            this.thrower = TagHelper.deserializeUuid(compoundTag.getCompound("Thrower"));
        }
        CompoundTag compoundTag2 = compoundTag.getCompound("Item");
        this.setStack(ItemStack.fromTag(compoundTag2));
        if (this.getStack().isEmpty()) {
            this.remove();
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity playerEntity) {
        if (this.world.isClient) {
            return;
        }
        ItemStack itemStack = this.getStack();
        Item item = itemStack.getItem();
        int i = itemStack.getAmount();
        if (this.pickupDelay == 0 && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(playerEntity.getUuid())) && playerEntity.inventory.insertStack(itemStack)) {
            playerEntity.sendPickup(this, i);
            if (itemStack.isEmpty()) {
                this.remove();
                itemStack.setAmount(i);
            }
            playerEntity.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
        }
    }

    @Override
    public Component getName() {
        Component component = this.getCustomName();
        if (component != null) {
            return component;
        }
        return new TranslatableComponent(this.getStack().getTranslationKey(), new Object[0]);
    }

    @Override
    public boolean canPlayerAttack() {
        return false;
    }

    @Override
    @Nullable
    public Entity changeDimension(DimensionType dimensionType) {
        Entity entity = super.changeDimension(dimensionType);
        if (!this.world.isClient && entity instanceof ItemEntity) {
            ((ItemEntity)entity).tryMerge();
        }
        return entity;
    }

    public ItemStack getStack() {
        return this.getDataTracker().get(STACK);
    }

    public void setStack(ItemStack itemStack) {
        this.getDataTracker().set(STACK, itemStack);
    }

    @Nullable
    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable UUID uUID) {
        this.owner = uUID;
    }

    @Nullable
    public UUID getThrower() {
        return this.thrower;
    }

    public void setThrower(@Nullable UUID uUID) {
        this.thrower = uUID;
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

    public void setPickupDelay(int i) {
        this.pickupDelay = i;
    }

    public boolean cannotPickup() {
        return this.pickupDelay > 0;
    }

    public void method_6976() {
        this.age = -6000;
    }

    public void method_6987() {
        this.setPickupDelayInfinite();
        this.age = 5999;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

