package net.minecraft.screen;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class CraftingScreenHandler extends AbstractRecipeScreenHandler<CraftingRecipeInput, CraftingRecipe> {
	public static final int RESULT_ID = 0;
	private static final int INPUT_START = 1;
	private static final int INPUT_END = 10;
	private static final int INVENTORY_START = 10;
	private static final int INVENTORY_END = 37;
	private static final int HOTBAR_START = 37;
	private static final int HOTBAR_END = 46;
	private final RecipeInputInventory input = new CraftingInventory(this, 3, 3);
	private final CraftingResultInventory result = new CraftingResultInventory();
	private final ScreenHandlerContext context;
	private final PlayerEntity player;
	private boolean filling;

	public CraftingScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public CraftingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.CRAFTING, syncId);
		this.context = context;
		this.player = playerInventory.player;
		this.addSlot(new CraftingResultSlot(playerInventory.player, this.input, this.result, 0, 124, 35));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(new Slot(this.input, j + i * 3, 30 + j * 18, 17 + i * 18));
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

	protected static void updateResult(
		ScreenHandler handler,
		World world,
		PlayerEntity player,
		RecipeInputInventory craftingInventory,
		CraftingResultInventory resultInventory,
		@Nullable RecipeEntry<CraftingRecipe> recipe
	) {
		if (!world.isClient) {
			CraftingRecipeInput craftingRecipeInput = craftingInventory.createRecipeInput();
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
			ItemStack itemStack = ItemStack.EMPTY;
			Optional<RecipeEntry<CraftingRecipe>> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingRecipeInput, world, recipe);
			if (optional.isPresent()) {
				RecipeEntry<CraftingRecipe> recipeEntry = (RecipeEntry<CraftingRecipe>)optional.get();
				CraftingRecipe craftingRecipe = recipeEntry.value();
				if (resultInventory.shouldCraftRecipe(world, serverPlayerEntity, recipeEntry)) {
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
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		if (!this.filling) {
			this.context.run((world, pos) -> updateResult(this, world, this.player, this.input, this.result, null));
		}
	}

	@Override
	public void onInputSlotFillStart() {
		this.filling = true;
	}

	@Override
	public void onInputSlotFillFinish(RecipeEntry<CraftingRecipe> recipe) {
		this.filling = false;
		this.context.run((world, pos) -> updateResult(this, world, this.player, this.input, this.result, recipe));
	}

	@Override
	public void populateRecipeFinder(RecipeMatcher finder) {
		this.input.provideRecipeInputs(finder);
	}

	@Override
	public void clearCraftingSlots() {
		this.input.clear();
		this.result.clear();
	}

	@Override
	public boolean matches(RecipeEntry<CraftingRecipe> recipe) {
		return recipe.value().matches(this.input.createRecipeInput(), this.player.getWorld());
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.input));
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
		return slot.inventory != this.result && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 0;
	}

	@Override
	public int getCraftingWidth() {
		return this.input.getWidth();
	}

	@Override
	public int getCraftingHeight() {
		return this.input.getHeight();
	}

	@Override
	public int getCraftingSlotCount() {
		return 10;
	}

	@Override
	public RecipeBookCategory getCategory() {
		return RecipeBookCategory.CRAFTING;
	}

	@Override
	public boolean canInsertIntoSlot(int index) {
		return index != this.getCraftingResultSlotIndex();
	}
}
