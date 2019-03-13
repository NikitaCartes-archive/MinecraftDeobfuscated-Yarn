package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.container.Container;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;

public class DispenserBlockEntity extends LootableContainerBlockEntity {
	private static final Random RANDOM = new Random();
	private DefaultedList<ItemStack> field_11945 = DefaultedList.create(9, ItemStack.EMPTY);

	protected DispenserBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public DispenserBlockEntity() {
		this(BlockEntityType.DISPENSER);
	}

	@Override
	public int getInvSize() {
		return 9;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.field_11945) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public int chooseNonEmptySlot() {
		this.checkLootInteraction(null);
		int i = -1;
		int j = 1;

		for (int k = 0; k < this.field_11945.size(); k++) {
			if (!this.field_11945.get(k).isEmpty() && RANDOM.nextInt(j++) == 0) {
				i = k;
			}
		}

		return i;
	}

	public int addToFirstFreeSlot(ItemStack itemStack) {
		for (int i = 0; i < this.field_11945.size(); i++) {
			if (this.field_11945.get(i).isEmpty()) {
				this.method_5447(i, itemStack);
				return i;
			}
		}

		return -1;
	}

	@Override
	protected TextComponent method_17823() {
		return new TranslatableTextComponent("container.dispenser");
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.field_11945 = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.method_11283(compoundTag)) {
			Inventories.method_5429(compoundTag, this.field_11945);
		}
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		if (!this.method_11286(compoundTag)) {
			Inventories.method_5426(compoundTag, this.field_11945);
		}

		return compoundTag;
	}

	@Override
	protected DefaultedList<ItemStack> method_11282() {
		return this.field_11945;
	}

	@Override
	protected void method_11281(DefaultedList<ItemStack> defaultedList) {
		this.field_11945 = defaultedList;
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new Generic3x3Container(i, playerInventory, this);
	}
}
