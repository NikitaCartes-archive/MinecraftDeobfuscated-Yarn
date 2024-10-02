package net.minecraft.screen;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class CraftingScreenHandler extends AbstractCraftingScreenHandler {
	private static final int field_52567 = 3;
	private static final int field_52568 = 3;
	public static final int RESULT_ID = 0;
	private static final int INPUT_START = 1;
	private static final int field_52569 = 9;
	private static final int INPUT_END = 10;
	private static final int INVENTORY_START = 10;
	private static final int INVENTORY_END = 37;
	private static final int HOTBAR_START = 37;
	private static final int HOTBAR_END = 46;
	private final ScreenHandlerContext context;
	private final PlayerEntity player;
	private boolean filling;

	public CraftingScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public CraftingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.CRAFTING, syncId, 3, 3);
		this.context = context;
		this.player = playerInventory.player;
		this.addResultSlot(this.player, 124, 35);
		this.addInputSlots(30, 17);
		this.addPlayerSlots(playerInventory, 8, 84);
	}

	protected static void updateResult(
		ScreenHandler handler,
		ServerWorld world,
		PlayerEntity player,
		RecipeInputInventory craftingInventory,
		CraftingResultInventory resultInventory,
		@Nullable RecipeEntry<CraftingRecipe> recipe
	) {
		CraftingRecipeInput craftingRecipeInput = craftingInventory.createRecipeInput();
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
		ItemStack itemStack = ItemStack.EMPTY;
		Optional<RecipeEntry<CraftingRecipe>> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingRecipeInput, world, recipe);
		if (optional.isPresent()) {
			RecipeEntry<CraftingRecipe> recipeEntry = (RecipeEntry<CraftingRecipe>)optional.get();
			CraftingRecipe craftingRecipe = recipeEntry.value();
			if (resultInventory.shouldCraftRecipe(serverPlayerEntity, recipeEntry)) {
				ItemStack itemStack2 = craftingRecipe.craft(craftingRecipeInput, world.getRegistryManager());
				if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
					itemStack = itemStack2;
				}
			}
		}

		resultInventory.setStack(0, itemStack);
		handler.setPreviousTrackedSlot(0, itemStack);
		serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack));
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		if (!this.filling) {
			this.context.run((world, pos) -> {
				if (world instanceof ServerWorld serverWorld) {
					updateResult(this, serverWorld, this.player, this.craftingInventory, this.craftingResultInventory, null);
				}
			});
		}
	}

	@Override
	public void onInputSlotFillStart() {
		this.filling = true;
	}

	@Override
	public void onInputSlotFillFinish(ServerWorld world, RecipeEntry<CraftingRecipe> recipe) {
		this.filling = false;
		updateResult(this, world, this.player, this.craftingInventory, this.craftingResultInventory, recipe);
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.craftingInventory));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(this.context, player, Blocks.CRAFTING_TABLE);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2 != null && slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			itemStack = itemStack2.copy();
			if (slot == 0) {
				this.context.run((world, pos) -> itemStack2.getItem().onCraftByPlayer(itemStack2, world, player));
				if (!this.insertItem(itemStack2, 10, 46, true)) {
					return ItemStack.EMPTY;
				}

				slot2.onQuickTransfer(itemStack2, itemStack);
			} else if (slot >= 10 && slot < 46) {
				if (!this.insertItem(itemStack2, 1, 10, false)) {
					if (slot < 37) {
						if (!this.insertItem(itemStack2, 37, 46, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.insertItem(itemStack2, 10, 37, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.insertItem(itemStack2, 10, 46, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStack(ItemStack.EMPTY);
			} else {
				slot2.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot2.onTakeItem(player, itemStack2);
			if (slot == 0) {
				player.dropItem(itemStack2, false);
			}
		}

		return itemStack;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.craftingResultInventory && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public Slot getOutputSlot() {
		return this.slots.get(0);
	}

	@Override
	public List<Slot> getInputSlots() {
		return this.slots.subList(1, 10);
	}

	@Override
	public RecipeBookType getCategory() {
		return RecipeBookType.CRAFTING;
	}

	@Override
	protected PlayerEntity getPlayer() {
		return this.player;
	}
}
