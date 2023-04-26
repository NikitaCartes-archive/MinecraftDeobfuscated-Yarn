package net.minecraft.screen;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class SmithingScreenHandler extends ForgingScreenHandler {
	public static final int field_41924 = 0;
	public static final int field_41925 = 1;
	public static final int field_41926 = 2;
	public static final int field_41927 = 3;
	public static final int field_41928 = 8;
	public static final int field_41929 = 26;
	public static final int field_41930 = 44;
	private static final int field_41932 = 98;
	public static final int field_41931 = 48;
	private final World world;
	@Nullable
	private SmithingRecipe currentRecipe;
	private final List<SmithingRecipe> recipes;

	public SmithingScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public SmithingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.SMITHING, syncId, playerInventory, context);
		this.world = playerInventory.player.getWorld();
		this.recipes = this.world.getRecipeManager().listAllOfType(RecipeType.SMITHING);
	}

	@Override
	protected ForgingSlotsManager getForgingSlotsManager() {
		return ForgingSlotsManager.create()
			.input(0, 8, 48, stack -> this.recipes.stream().anyMatch(recipe -> recipe.testTemplate(stack)))
			.input(1, 26, 48, stack -> this.recipes.stream().anyMatch(recipe -> recipe.testBase(stack) && recipe.testTemplate(this.slots.get(0).getStack())))
			.input(2, 44, 48, stack -> this.recipes.stream().anyMatch(recipe -> recipe.testAddition(stack) && recipe.testTemplate(this.slots.get(0).getStack())))
			.output(3, 98, 48)
			.build();
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
		stack.onCraft(player.getWorld(), player, stack.getCount());
		this.output.unlockLastRecipe(player, this.getInputStacks());
		this.decrementStack(0);
		this.decrementStack(1);
		this.decrementStack(2);
		this.context.run((world, pos) -> world.syncWorldEvent(WorldEvents.SMITHING_TABLE_USED, pos, 0));
	}

	private List<ItemStack> getInputStacks() {
		return List.of(this.input.getStack(0), this.input.getStack(1), this.input.getStack(2));
	}

	private void decrementStack(int slot) {
		ItemStack itemStack = this.input.getStack(slot);
		if (!itemStack.isEmpty()) {
			itemStack.decrement(1);
			this.input.setStack(slot, itemStack);
		}
	}

	@Override
	public void updateResult() {
		List<SmithingRecipe> list = this.world.getRecipeManager().getAllMatches(RecipeType.SMITHING, this.input, this.world);
		if (list.isEmpty()) {
			this.output.setStack(0, ItemStack.EMPTY);
		} else {
			SmithingRecipe smithingRecipe = (SmithingRecipe)list.get(0);
			ItemStack itemStack = smithingRecipe.craft(this.input, this.world.getRegistryManager());
			if (itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
				this.currentRecipe = smithingRecipe;
				this.output.setLastRecipe(smithingRecipe);
				this.output.setStack(0, itemStack);
			}
		}
	}

	@Override
	public int getSlotFor(ItemStack stack) {
		return (Integer)((Optional)this.recipes
				.stream()
				.map(recipe -> getQuickMoveSlot(recipe, stack))
				.filter(Optional::isPresent)
				.findFirst()
				.orElse(Optional.of(0)))
			.get();
	}

	private static Optional<Integer> getQuickMoveSlot(SmithingRecipe recipe, ItemStack stack) {
		if (recipe.testTemplate(stack)) {
			return Optional.of(0);
		} else if (recipe.testBase(stack)) {
			return Optional.of(1);
		} else {
			return recipe.testAddition(stack) ? Optional.of(2) : Optional.empty();
		}
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public boolean isValidIngredient(ItemStack stack) {
		return this.recipes.stream().map(recipe -> getQuickMoveSlot(recipe, stack)).anyMatch(Optional::isPresent);
	}
}
