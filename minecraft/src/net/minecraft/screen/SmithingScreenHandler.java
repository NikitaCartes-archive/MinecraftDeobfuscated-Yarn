package net.minecraft.screen;

import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmithingScreenHandler extends ForgingScreenHandler {
	private final World field_25385;
	@Nullable
	private SmithingRecipe field_25386;
	private final List<SmithingRecipe> field_25668;

	public SmithingScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public SmithingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.SMITHING, syncId, playerInventory, context);
		this.field_25385 = playerInventory.player.world;
		this.field_25668 = this.field_25385.getRecipeManager().listAllOfType(RecipeType.SMITHING);
	}

	@Override
	protected boolean canUse(BlockState state) {
		return state.isOf(Blocks.SMITHING_TABLE);
	}

	@Override
	protected boolean canTakeOutput(PlayerEntity player, boolean present) {
		return this.field_25386 != null && this.field_25386.matches(this.input, this.field_25385);
	}

	@Override
	protected ItemStack onTakeOutput(PlayerEntity player, ItemStack stack) {
		stack.onCraft(player.world, player, stack.getCount());
		this.output.unlockLastRecipe(player);
		this.method_29539(0);
		this.method_29539(1);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> world.syncWorldEvent(1044, blockPos, 0)));
		return stack;
	}

	private void method_29539(int i) {
		ItemStack itemStack = this.input.getStack(i);
		itemStack.decrement(1);
		this.input.setStack(i, itemStack);
	}

	@Override
	public void updateResult() {
		List<SmithingRecipe> list = this.field_25385.getRecipeManager().getAllMatches(RecipeType.SMITHING, this.input, this.field_25385);
		if (list.isEmpty()) {
			this.output.setStack(0, ItemStack.EMPTY);
		} else {
			this.field_25386 = (SmithingRecipe)list.get(0);
			ItemStack itemStack = this.field_25386.craft(this.input);
			this.output.setLastRecipe(this.field_25386);
			this.output.setStack(0, itemStack);
		}
	}

	@Override
	protected boolean method_30025(ItemStack itemStack) {
		return this.field_25668.stream().anyMatch(smithingRecipe -> smithingRecipe.method_30029(itemStack));
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
	}
}
