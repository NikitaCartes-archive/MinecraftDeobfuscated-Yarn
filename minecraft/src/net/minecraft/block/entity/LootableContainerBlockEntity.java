package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public abstract class LootableContainerBlockEntity extends LockableContainerBlockEntity implements LootableInventory {
	@Nullable
	protected Identifier lootTableId;
	protected long lootTableSeed;

	protected LootableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	@Nullable
	@Override
	public Identifier getLootTableId() {
		return this.lootTableId;
	}

	@Override
	public void setLootTableId(@Nullable Identifier lootTableId) {
		this.lootTableId = lootTableId;
	}

	@Override
	public long getLootTableSeed() {
		return this.lootTableSeed;
	}

	@Override
	public void setLootTableSeed(long lootTableSeed) {
		this.lootTableSeed = lootTableSeed;
	}

	@Override
	public boolean isEmpty() {
		this.generateLoot(null);

		for (ItemStack itemStack : this.getHeldStacks()) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		this.generateLoot(null);
		return this.getHeldStacks().get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		this.generateLoot(null);
		ItemStack itemStack = Inventories.splitStack(this.getHeldStacks(), slot, amount);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	@Override
	public ItemStack removeStack(int slot) {
		this.generateLoot(null);
		return Inventories.removeStack(this.getHeldStacks(), slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.generateLoot(null);
		this.getHeldStacks().set(slot, stack);
		if (stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}

		this.markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return Inventory.canPlayerUse(this, player);
	}

	@Override
	public void clear() {
		this.getHeldStacks().clear();
	}

	protected abstract DefaultedList<ItemStack> getHeldStacks();

	protected abstract void setHeldStacks(DefaultedList<ItemStack> list);

	@Override
	public boolean checkUnlocked(PlayerEntity player) {
		return super.checkUnlocked(player) && (this.lootTableId == null || !player.isSpectator());
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		if (this.checkUnlocked(playerEntity)) {
			this.generateLoot(playerInventory.player);
			return this.createScreenHandler(i, playerInventory);
		} else {
			return null;
		}
	}
}
