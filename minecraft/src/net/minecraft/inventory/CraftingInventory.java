package net.minecraft.inventory;

import net.minecraft.class_1662;
import net.minecraft.class_1737;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;

public class CraftingInventory implements Inventory, class_1737 {
	private final DefaultedList<ItemStack> stacks;
	private final int width;
	private final int height;
	private final Container container;

	public CraftingInventory(Container container, int i, int j) {
		this.stacks = DefaultedList.create(i * j, ItemStack.EMPTY);
		this.container = container;
		this.width = i;
		this.height = j;
	}

	@Override
	public int getInvSize() {
		return this.stacks.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.stacks) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int i) {
		return i >= this.getInvSize() ? ItemStack.EMPTY : this.stacks.get(i);
	}

	@Override
	public TextComponent getName() {
		return new TranslatableTextComponent("container.crafting");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return InventoryUtil.removeStack(this.stacks, i);
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		ItemStack itemStack = InventoryUtil.splitStack(this.stacks, i, j);
		if (!itemStack.isEmpty()) {
			this.container.onContentChanged(this);
		}

		return itemStack;
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		this.stacks.set(i, itemStack);
		this.container.onContentChanged(this);
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
		this.stacks.clear();
	}

	@Override
	public int getInvHeight() {
		return this.height;
	}

	@Override
	public int getInvWidth() {
		return this.width;
	}

	@Override
	public void method_7683(class_1662 arg) {
		for (ItemStack itemStack : this.stacks) {
			arg.method_7404(itemStack);
		}
	}
}
