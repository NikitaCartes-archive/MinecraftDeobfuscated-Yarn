/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;
import org.jetbrains.annotations.Nullable;

public abstract class LootableContainerBlockEntity
extends LockableContainerBlockEntity {
    @Nullable
    protected Identifier lootTableId;
    protected long lootTableSeed;

    protected LootableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    public static void setLootTable(BlockView blockView, Random random, BlockPos blockPos, Identifier identifier) {
        BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
        if (blockEntity instanceof LootableContainerBlockEntity) {
            ((LootableContainerBlockEntity)blockEntity).setLootTable(identifier, random.nextLong());
        }
    }

    protected boolean deserializeLootTable(CompoundTag compoundTag) {
        if (compoundTag.containsKey("LootTable", 8)) {
            this.lootTableId = new Identifier(compoundTag.getString("LootTable"));
            this.lootTableSeed = compoundTag.getLong("LootTableSeed");
            return true;
        }
        return false;
    }

    protected boolean serializeLootTable(CompoundTag compoundTag) {
        if (this.lootTableId == null) {
            return false;
        }
        compoundTag.putString("LootTable", this.lootTableId.toString());
        if (this.lootTableSeed != 0L) {
            compoundTag.putLong("LootTableSeed", this.lootTableSeed);
        }
        return true;
    }

    public void checkLootInteraction(@Nullable PlayerEntity playerEntity) {
        if (this.lootTableId != null && this.world.getServer() != null) {
            LootSupplier lootSupplier = this.world.getServer().getLootManager().getSupplier(this.lootTableId);
            this.lootTableId = null;
            LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world).put(LootContextParameters.POSITION, new BlockPos(this.pos)).setRandom(this.lootTableSeed);
            if (playerEntity != null) {
                builder.setLuck(playerEntity.getLuck()).put(LootContextParameters.THIS_ENTITY, playerEntity);
            }
            lootSupplier.supplyInventory(this, builder.build(LootContextTypes.CHEST));
        }
    }

    public void setLootTable(Identifier identifier, long l) {
        this.lootTableId = identifier;
        this.lootTableSeed = l;
    }

    @Override
    public ItemStack getInvStack(int i) {
        this.checkLootInteraction(null);
        return this.getInvStackList().get(i);
    }

    @Override
    public ItemStack takeInvStack(int i, int j) {
        this.checkLootInteraction(null);
        ItemStack itemStack = Inventories.splitStack(this.getInvStackList(), i, j);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeInvStack(int i) {
        this.checkLootInteraction(null);
        return Inventories.removeStack(this.getInvStackList(), i);
    }

    @Override
    public void setInvStack(int i, ItemStack itemStack) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(i, itemStack);
        if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
            itemStack.setAmount(this.getInvMaxStackAmount());
        }
        this.markDirty();
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return !(playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
    }

    @Override
    public void clear() {
        this.getInvStackList().clear();
    }

    protected abstract DefaultedList<ItemStack> getInvStackList();

    protected abstract void setInvStackList(DefaultedList<ItemStack> var1);

    @Override
    public boolean checkUnlocked(PlayerEntity playerEntity) {
        return super.checkUnlocked(playerEntity) && (this.lootTableId == null || !playerEntity.isSpectator());
    }

    @Override
    @Nullable
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.checkUnlocked(playerEntity)) {
            this.checkLootInteraction(playerInventory.player);
            return this.createContainer(i, playerInventory);
        }
        return null;
    }
}

