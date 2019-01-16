package net.minecraft.block.entity;

import net.minecraft.block.BarrelBlock;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class BarrelBlockEntity extends LootableContainerBlockEntity implements Tickable {
	private DefaultedList<ItemStack> inventory = DefaultedList.create(27, ItemStack.EMPTY);
	protected int field_17583;
	protected boolean field_17584;
	private int field_17585;

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
		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		InventoryUtil.deserialize(compoundTag, this.inventory);
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
	protected TextComponent method_17823() {
		return new TranslatableTextComponent("container.barrel");
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new GenericContainer.Generic9x3(i, playerInventory, this);
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			if (this.field_17583 < 0) {
				this.field_17583 = 0;
			}

			this.field_17583++;
		}
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			this.field_17583--;
		}
	}

	@Override
	public void tick() {
		if (!this.world.isClient) {
			int i = this.pos.getX();
			int j = this.pos.getY();
			int k = this.pos.getZ();
			this.field_17585++;
			this.field_17583 = ChestBlockEntity.method_17765(this.world, this, this.field_17585, i, j, k, this.field_17583);
			if (this.field_17583 > 0 && !this.field_17584) {
				this.method_17764(SoundEvents.field_17604);
				this.field_17584 = true;
			}

			if (this.field_17583 == 0 && this.field_17584) {
				this.method_17764(SoundEvents.field_17603);
				this.field_17584 = false;
			}
		}
	}

	private void method_17764(SoundEvent soundEvent) {
		Direction direction = this.getCachedState().get(BarrelBlock.field_16320);
		double d = (double)this.pos.getX() + 0.5 + (double)direction.getVector().getX() / 2.0;
		double e = (double)this.pos.getY() + 0.5 + (double)direction.getVector().getZ() / 2.0;
		double f = (double)this.pos.getZ() + 0.5;
		this.world.playSound(null, d, e, f, soundEvent, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
	}
}
