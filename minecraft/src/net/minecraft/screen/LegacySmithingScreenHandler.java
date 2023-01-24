package net.minecraft.screen;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.LegacySmithingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

@Deprecated(
	forRemoval = true
)
public class LegacySmithingScreenHandler extends ForgingScreenHandler {
	private final World world;
	public static final int field_41912 = 0;
	public static final int field_41913 = 1;
	public static final int field_41914 = 2;
	private static final int field_41916 = 27;
	private static final int field_41917 = 76;
	private static final int field_41918 = 134;
	private static final int field_41919 = 47;
	@Nullable
	private LegacySmithingRecipe currentRecipe;
	private final List<LegacySmithingRecipe> recipes;

	public LegacySmithingScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public LegacySmithingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.LEGACY_SMITHING, syncId, playerInventory, context);
		this.world = playerInventory.player.world;
		this.recipes = this.world
			.getRecipeManager()
			.<Inventory, SmithingRecipe>listAllOfType(RecipeType.SMITHING)
			.stream()
			.filter(recipe -> recipe instanceof LegacySmithingRecipe)
			.map(recipe -> (LegacySmithingRecipe)recipe)
			.toList();
	}

	@Override
	protected ForgingSlotsManager getForgingSlotsManager() {
		return ForgingSlotsManager.create().input(0, 27, 47, stack -> true).input(1, 76, 47, stack -> true).output(2, 134, 47).build();
	}

	@Override
	protected boolean canUse(BlockState state) {
		return state.isOf(Blocks.SMITHING_TABLE);
	}

	@Override
	protected boolean canTakeOutput(PlayerEntity player, boolean present) {
		return this.currentRecipe != null && this.currentRecipe.matches(this.input, this.world);
	}

	@Override
	protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
		stack.onCraft(player.world, player, stack.getCount());
		this.output.unlockLastRecipe(player);
		this.decrementStack(0);
		this.decrementStack(1);
		this.context.run((world, pos) -> world.syncWorldEvent(WorldEvents.SMITHING_TABLE_USED, pos, 0));
	}

	private void decrementStack(int slot) {
		ItemStack itemStack = this.input.getStack(slot);
		itemStack.decrement(1);
		this.input.setStack(slot, itemStack);
	}

	@Override
	public void updateResult() {
		List<LegacySmithingRecipe> list = this.world
			.getRecipeManager()
			.getAllMatches(RecipeType.SMITHING, this.input, this.world)
			.stream()
			.filter(recipe -> recipe instanceof LegacySmithingRecipe)
			.map(recipe -> (LegacySmithingRecipe)recipe)
			.toList();
		if (list.isEmpty()) {
			this.output.setStack(0, ItemStack.EMPTY);
		} else {
			LegacySmithingRecipe legacySmithingRecipe = (LegacySmithingRecipe)list.get(0);
			ItemStack itemStack = legacySmithingRecipe.craft(this.input, this.world.getRegistryManager());
			if (itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
				this.currentRecipe = legacySmithingRecipe;
				this.output.setLastRecipe(legacySmithingRecipe);
				this.output.setStack(0, itemStack);
			}
		}
	}

	@Override
	public int getSlotFor(ItemStack stack) {
		return this.testAddition(stack) ? 1 : 0;
	}

	protected boolean testAddition(ItemStack stack) {
		return this.recipes.stream().anyMatch(recipe -> recipe.testAddition(stack));
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
	}
}
