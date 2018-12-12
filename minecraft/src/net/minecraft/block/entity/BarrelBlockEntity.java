package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.container.BarrelContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;

public class BarrelBlockEntity extends LootableContainerBlockEntity {
	private DefaultedList<ItemStack> inventory = DefaultedList.create(27, ItemStack.EMPTY);

	private BarrelBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public BarrelBlockEntity() {
		this(BlockEntityType.BARREL);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		InventoryUtil.serialize(compoundTag, this.inventory);
		if (this.customName != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
		}

		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		InventoryUtil.deserialize(compoundTag, this.inventory);
		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}
	}

	@Override
	public int getInvSize() {
		return 27;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int i) {
		return this.inventory.get(i);
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		return InventoryUtil.splitStack(this.inventory, i, j);
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return InventoryUtil.removeStack(this.inventory, i);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		this.inventory.set(i, itemStack);
		if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
			itemStack.setAmount(this.getInvMaxStackAmount());
		}
	}

	@Override
	public void clearInv() {
		this.inventory.clear();
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> defaultedList) {
		this.inventory = defaultedList;
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new BarrelContainer(playerInventory, this, playerEntity);
	}

	@Override
	public String getContainerId() {
		return "minecraft:barrel";
	}

	@Override
	public TextComponent getName() {
		return (TextComponent)(this.customName != null ? this.customName : new TranslatableTextComponent("container.barrel"));
	}

	@Nullable
	@Override
	public TextComponent getCustomName() {
		return this.customName;
	}

	@Override
	public void setCustomName(@Nullable TextComponent textComponent) {
		this.customName = textComponent;
	}
}
