package net.minecraft.container;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3914;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.crafting.CraftingRecipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class CraftingTableContainer extends CraftingContainer<CraftingInventory> {
	private final CraftingInventory craftingInv = new CraftingInventory(this, 3, 3);
	private final CraftingResultInventory resultInv = new CraftingResultInventory();
	private final class_3914 world;
	private final PlayerEntity player;

	public CraftingTableContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, class_3914.field_17304);
	}

	public CraftingTableContainer(int i, PlayerInventory playerInventory, class_3914 arg) {
		super(ContainerType.CRAFTING, i);
		this.world = arg;
		this.player = playerInventory.player;
		this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInv, this.resultInv, 0, 124, 35));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 3; k++) {
				this.addSlot(new Slot(this.craftingInv, k + j * 3, 30 + k * 18, 17 + j * 18));
			}
		}

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
		}
	}

	protected static void method_17399(
		int i, World world, PlayerEntity playerEntity, CraftingInventory craftingInventory, CraftingResultInventory craftingResultInventory
	) {
		if (!world.isClient) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
			ItemStack itemStack = ItemStack.EMPTY;
			Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().get(RecipeType.CRAFTING, craftingInventory, world);
			if (optional.isPresent()) {
				CraftingRecipe craftingRecipe = (CraftingRecipe)optional.get();
				if (craftingResultInventory.shouldCraftRecipe(world, serverPlayerEntity, craftingRecipe)) {
					itemStack = craftingRecipe.craft(craftingInventory);
				}
			}

			craftingResultInventory.setInvStack(0, itemStack);
			serverPlayerEntity.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(i, 0, itemStack));
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.world.method_17393((world, blockPos) -> method_17399(this.syncId, world, this.player, this.craftingInv, this.resultInv));
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
	public boolean matches(Recipe<? super CraftingInventory> recipe) {
		return recipe.matches(this.craftingInv, this.player.world);
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.world.method_17393((world, blockPos) -> this.dropInventory(playerEntity, world, this.craftingInv));
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return canUse(this.world, playerEntity, Blocks.field_9980);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i == 0) {
				this.world.method_17393((world, blockPos) -> itemStack2.getItem().onCrafted(itemStack2, world, playerEntity));
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
		return this.craftingInv.getWidth();
	}

	@Override
	public int getCraftingHeight() {
		return this.craftingInv.getHeight();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getCraftingSlotCount() {
		return 10;
	}
}
