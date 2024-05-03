package net.minecraft.screen;

import net.minecraft.block.CrafterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.slot.CrafterInputSlot;
import net.minecraft.screen.slot.CrafterOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class CrafterScreenHandler extends ScreenHandler implements ScreenHandlerListener {
	protected static final int field_46781 = 9;
	private static final int field_46782 = 9;
	private static final int field_46783 = 36;
	private static final int field_46784 = 36;
	private static final int field_46785 = 45;
	private final CraftingResultInventory resultInventory = new CraftingResultInventory();
	private final PropertyDelegate propertyDelegate;
	private final PlayerEntity player;
	private final RecipeInputInventory inputInventory;

	public CrafterScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ScreenHandlerType.CRAFTER_3X3, syncId);
		this.player = playerInventory.player;
		this.propertyDelegate = new ArrayPropertyDelegate(10);
		this.inputInventory = new CraftingInventory(this, 3, 3);
		this.addSlots(playerInventory);
	}

	public CrafterScreenHandler(int syncId, PlayerInventory playerInventory, RecipeInputInventory inputInventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.CRAFTER_3X3, syncId);
		this.player = playerInventory.player;
		this.propertyDelegate = propertyDelegate;
		this.inputInventory = inputInventory;
		checkSize(inputInventory, 9);
		inputInventory.onOpen(playerInventory.player);
		this.addSlots(playerInventory);
		this.addListener(this);
	}

	private void addSlots(PlayerInventory playerInventory) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int k = j + i * 3;
				this.addSlot(new CrafterInputSlot(this.inputInventory, k, 26 + j * 18, 17 + i * 18, this));
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

		this.addSlot(new CrafterOutputSlot(this.resultInventory, 0, 134, 35));
		this.addProperties(this.propertyDelegate);
		this.updateResult();
	}

	public void setSlotEnabled(int slot, boolean enabled) {
		CrafterInputSlot crafterInputSlot = (CrafterInputSlot)this.getSlot(slot);
		this.propertyDelegate.set(crafterInputSlot.id, enabled ? 0 : 1);
		this.sendContentUpdates();
	}

	public boolean isSlotDisabled(int slot) {
		return slot > -1 && slot < 9 ? this.propertyDelegate.get(slot) == 1 : false;
	}

	public boolean isTriggered() {
		return this.propertyDelegate.get(9) == 1;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2 != null && slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			itemStack = itemStack2.copy();
			if (slot < 9) {
				if (!this.insertItem(itemStack2, 9, 45, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 0, 9, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStackNoCallbacks(ItemStack.EMPTY);
			} else {
				slot2.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot2.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inputInventory.canPlayerUse(player);
	}

	private void updateResult() {
		if (this.player instanceof ServerPlayerEntity serverPlayerEntity) {
			World world = serverPlayerEntity.getWorld();
			CraftingRecipeInput craftingRecipeInput = this.inputInventory.createRecipeInput();
			ItemStack itemStack = (ItemStack)CrafterBlock.getCraftingRecipe(world, craftingRecipeInput)
				.map(recipeEntry -> ((CraftingRecipe)recipeEntry.value()).craft(craftingRecipeInput, world.getRegistryManager()))
				.orElse(ItemStack.EMPTY);
			this.resultInventory.setStack(0, itemStack);
		}
	}

	public Inventory getInputInventory() {
		return this.inputInventory;
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
		this.updateResult();
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
	}
}
