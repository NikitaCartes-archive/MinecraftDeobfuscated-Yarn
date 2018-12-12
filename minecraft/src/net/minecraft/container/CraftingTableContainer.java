package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableContainer extends CraftingContainer {
	public CraftingInventory craftingInv = new CraftingInventory(this, 3, 3);
	public CraftingResultInventory resultInv = new CraftingResultInventory();
	private final World world;
	private final BlockPos pos;
	private final PlayerEntity player;

	public CraftingTableContainer(PlayerInventory playerInventory, World world, BlockPos blockPos) {
		this.world = world;
		this.pos = blockPos;
		this.player = playerInventory.player;
		this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInv, this.resultInv, 0, 124, 35));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(new Slot(this.craftingInv, j + i * 3, 30 + j * 18, 17 + i * 18));
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.onCraftingContentChanged(this.world, this.player, this.craftingInv, this.resultInv);
	}

	@Override
	public void populateRecipeFinder(RecipeFinder recipeFinder) {
		this.craftingInv.provideRecipeInputs(recipeFinder);
	}

	@Override
	public void clearCraftingSlots() {
		this.craftingInv.clearInv();
		this.resultInv.clearInv();
	}

	@Override
	public boolean matches(Recipe recipe) {
		return recipe.matches(this.craftingInv, this.player.world);
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		if (!this.world.isClient) {
			this.method_7607(playerEntity, this.world, this.craftingInv);
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.world.getBlockState(this.pos).getBlock() != Blocks.field_9980
			? false
			: playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i == 0) {
				itemStack2.getItem().onCrafted(itemStack2, this.world, playerEntity);
				if (!this.insertItem(itemStack2, 10, 46, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (i >= 10 && i < 37) {
				if (!this.insertItem(itemStack2, 37, 46, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 37 && i < 46) {
				if (!this.insertItem(itemStack2, 10, 37, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 10, 46, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemStack3 = slot.onTakeItem(playerEntity, itemStack2);
			if (i == 0) {
				playerEntity.dropItem(itemStack3, false);
			}
		}

		return itemStack;
	}

	@Override
	public boolean method_7613(ItemStack itemStack, Slot slot) {
		return slot.inventory != this.resultInv && super.method_7613(itemStack, slot);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 0;
	}

	@Override
	public int getCraftingWidth() {
		return this.craftingInv.getInvWidth();
	}

	@Override
	public int getCraftingHeight() {
		return this.craftingInv.getInvHeight();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getCraftingSlotCount() {
		return 10;
	}
}
