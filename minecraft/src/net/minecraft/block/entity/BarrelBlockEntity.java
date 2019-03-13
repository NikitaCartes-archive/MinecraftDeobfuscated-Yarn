package net.minecraft.block.entity;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class BarrelBlockEntity extends LootableContainerBlockEntity implements Tickable {
	private DefaultedList<ItemStack> field_16410 = DefaultedList.create(27, ItemStack.EMPTY);
	private int viewerCount;
	private int ticksOpen;

	private BarrelBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public BarrelBlockEntity() {
		this(BlockEntityType.BARREL);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		if (!this.method_11286(compoundTag)) {
			Inventories.method_5426(compoundTag, this.field_16410);
		}

		return compoundTag;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.field_16410 = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.method_11283(compoundTag)) {
			Inventories.method_5429(compoundTag, this.field_16410);
		}
	}

	@Override
	public int getInvSize() {
		return 27;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.field_16410) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack method_5438(int i) {
		return this.field_16410.get(i);
	}

	@Override
	public ItemStack method_5434(int i, int j) {
		return Inventories.method_5430(this.field_16410, i, j);
	}

	@Override
	public ItemStack method_5441(int i) {
		return Inventories.method_5428(this.field_16410, i);
	}

	@Override
	public void method_5447(int i, ItemStack itemStack) {
		this.field_16410.set(i, itemStack);
		if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
			itemStack.setAmount(this.getInvMaxStackAmount());
		}
	}

	@Override
	public void clear() {
		this.field_16410.clear();
	}

	@Override
	protected DefaultedList<ItemStack> method_11282() {
		return this.field_16410;
	}

	@Override
	protected void method_11281(DefaultedList<ItemStack> defaultedList) {
		this.field_16410 = defaultedList;
	}

	@Override
	protected TextComponent method_17823() {
		return new TranslatableTextComponent("container.barrel");
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return GenericContainer.method_19245(i, playerInventory, this);
	}

	@Override
	public void method_5435(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			if (this.viewerCount < 0) {
				this.viewerCount = 0;
			}

			this.viewerCount++;
		}
	}

	@Override
	public void method_5432(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			this.viewerCount--;
		}
	}

	@Override
	public void tick() {
		if (!this.world.isClient) {
			int i = this.field_11867.getX();
			int j = this.field_11867.getY();
			int k = this.field_11867.getZ();
			this.ticksOpen++;
			this.viewerCount = ChestBlockEntity.recalculateViewerCountIfNecessary(this.world, this, this.ticksOpen, i, j, k, this.viewerCount);
			BlockState blockState = this.method_11010();
			boolean bl = (Boolean)blockState.method_11654(BarrelBlock.field_18006);
			boolean bl2 = this.viewerCount > 0;
			if (bl2 != bl) {
				this.method_17764(blockState, bl2 ? SoundEvents.field_17604 : SoundEvents.field_17603);
				this.method_18318(blockState, bl2);
			}
		}
	}

	private void method_18318(BlockState blockState, boolean bl) {
		this.world.method_8652(this.method_11016(), blockState.method_11657(BarrelBlock.field_18006, Boolean.valueOf(bl)), 3);
	}

	private void method_17764(BlockState blockState, SoundEvent soundEvent) {
		Vec3i vec3i = ((Direction)blockState.method_11654(BarrelBlock.field_16320)).method_10163();
		double d = (double)this.field_11867.getX() + 0.5 + (double)vec3i.getX() / 2.0;
		double e = (double)this.field_11867.getY() + 0.5 + (double)vec3i.getY() / 2.0;
		double f = (double)this.field_11867.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
		this.world.method_8465(null, d, e, f, soundEvent, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
	}
}
