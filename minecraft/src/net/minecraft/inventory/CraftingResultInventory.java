package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.class_1732;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;

public class CraftingResultInventory implements Inventory, class_1732 {
	private final DefaultedList<ItemStack> stack = DefaultedList.create(1, ItemStack.EMPTY);
	private Recipe field_7865;

	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.stack) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int i) {
		return this.stack.get(0);
	}

	@Override
	public TextComponent getName() {
		return new StringTextComponent("Result");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		return InventoryUtil.removeStack(this.stack, 0);
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return InventoryUtil.removeStack(this.stack, 0);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		this.stack.set(0, itemStack);
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
	}

	@Override
	public boolean isValidInvStack(int i, ItemStack itemStack) {
		return true;
	}

	@Override
	public int getInvProperty(int i) {
		return 0;
	}

	@Override
	public void setInvProperty(int i, int j) {
	}

	@Override
	public int getInvPropertyCount() {
		return 0;
	}

	@Override
	public void clearInv() {
		this.stack.clear();
	}

	@Override
	public void method_7662(@Nullable Recipe recipe) {
		this.field_7865 = recipe;
	}

	@Nullable
	@Override
	public Recipe method_7663() {
		return this.field_7865;
	}
}
