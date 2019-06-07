/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.vehicle;

import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;
import org.jetbrains.annotations.Nullable;

public abstract class StorageMinecartEntity
extends AbstractMinecartEntity
implements Inventory,
NameableContainerProvider {
    private DefaultedList<ItemStack> inventory = DefaultedList.create(36, ItemStack.EMPTY);
    private boolean field_7733 = true;
    @Nullable
    private Identifier lootTableId;
    private long lootSeed;

    protected StorageMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    protected StorageMinecartEntity(EntityType<?> entityType, double d, double e, double f, World world) {
        super(entityType, world, d, e, f);
    }

    @Override
    public void dropItems(DamageSource damageSource) {
        super.dropItems(damageSource);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemScatterer.spawn(this.world, this, (Inventory)this);
        }
    }

    @Override
    public boolean isInvEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getInvStack(int i) {
        this.method_7563(null);
        return this.inventory.get(i);
    }

    @Override
    public ItemStack takeInvStack(int i, int j) {
        this.method_7563(null);
        return Inventories.splitStack(this.inventory, i, j);
    }

    @Override
    public ItemStack removeInvStack(int i) {
        this.method_7563(null);
        ItemStack itemStack = this.inventory.get(i);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.inventory.set(i, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setInvStack(int i, ItemStack itemStack) {
        this.method_7563(null);
        this.inventory.set(i, itemStack);
        if (!itemStack.isEmpty() && itemStack.getCount() > this.getInvMaxStackAmount()) {
            itemStack.setCount(this.getInvMaxStackAmount());
        }
    }

    @Override
    public boolean equip(int i, ItemStack itemStack) {
        if (i >= 0 && i < this.getInvSize()) {
            this.setInvStack(i, itemStack);
            return true;
        }
        return false;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
        if (this.removed) {
            return false;
        }
        return !(playerEntity.squaredDistanceTo(this) > 64.0);
    }

    @Override
    @Nullable
    public Entity changeDimension(DimensionType dimensionType) {
        this.field_7733 = false;
        return super.changeDimension(dimensionType);
    }

    @Override
    public void remove() {
        if (!this.world.isClient && this.field_7733) {
            ItemScatterer.spawn(this.world, this, (Inventory)this);
        }
        super.remove();
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        if (this.lootTableId != null) {
            compoundTag.putString("LootTable", this.lootTableId.toString());
            if (this.lootSeed != 0L) {
                compoundTag.putLong("LootTableSeed", this.lootSeed);
            }
        } else {
            Inventories.toTag(compoundTag, this.inventory);
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
        if (compoundTag.containsKey("LootTable", 8)) {
            this.lootTableId = new Identifier(compoundTag.getString("LootTable"));
            this.lootSeed = compoundTag.getLong("LootTableSeed");
        } else {
            Inventories.fromTag(compoundTag, this.inventory);
        }
    }

    @Override
    public boolean interact(PlayerEntity playerEntity, Hand hand) {
        playerEntity.openContainer(this);
        return true;
    }

    @Override
    protected void method_7525() {
        float f = 0.98f;
        if (this.lootTableId == null) {
            int i = 15 - Container.calculateComparatorOutput(this);
            f += (float)i * 0.001f;
        }
        this.setVelocity(this.getVelocity().multiply(f, 0.0, f));
    }

    public void method_7563(@Nullable PlayerEntity playerEntity) {
        if (this.lootTableId != null && this.world.getServer() != null) {
            LootSupplier lootSupplier = this.world.getServer().getLootManager().getSupplier(this.lootTableId);
            this.lootTableId = null;
            LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world).put(LootContextParameters.POSITION, new BlockPos(this)).setRandom(this.lootSeed);
            if (playerEntity != null) {
                builder.setLuck(playerEntity.getLuck()).put(LootContextParameters.THIS_ENTITY, playerEntity);
            }
            lootSupplier.supplyInventory(this, builder.build(LootContextTypes.CHEST));
        }
    }

    @Override
    public void clear() {
        this.method_7563(null);
        this.inventory.clear();
    }

    public void setLootTable(Identifier identifier, long l) {
        this.lootTableId = identifier;
        this.lootSeed = l;
    }

    @Override
    @Nullable
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.lootTableId == null || !playerEntity.isSpectator()) {
            this.method_7563(playerInventory.player);
            return this.getContainer(i, playerInventory);
        }
        return null;
    }

    protected abstract Container getContainer(int var1, PlayerInventory var2);
}

