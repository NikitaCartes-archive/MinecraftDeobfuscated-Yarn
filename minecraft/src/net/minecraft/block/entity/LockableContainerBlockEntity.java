package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerLock;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Nameable;

public abstract class LockableContainerBlockEntity extends BlockEntity implements Inventory, NameableContainerProvider, Nameable {
	private ContainerLock lock = ContainerLock.NONE;
	private TextComponent field_17376;

	protected LockableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.lock = ContainerLock.method_5473(compoundTag);
		if (compoundTag.containsKey("CustomName", 8)) {
			this.field_17376 = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		this.lock.method_5474(compoundTag);
		if (this.field_17376 != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.field_17376));
		}

		return compoundTag;
	}

	public void method_17488(TextComponent textComponent) {
		this.field_17376 = textComponent;
	}

	@Override
	public TextComponent method_5477() {
		return this.field_17376 != null ? this.field_17376 : this.method_17823();
	}

	@Override
	public TextComponent method_5476() {
		return this.method_5477();
	}

	@Nullable
	@Override
	public TextComponent method_5797() {
		return this.field_17376;
	}

	protected abstract TextComponent method_17823();

	public boolean checkUnlocked(PlayerEntity playerEntity) {
		return method_17487(playerEntity, this.lock, this.method_5476());
	}

	public static boolean method_17487(PlayerEntity playerEntity, ContainerLock containerLock, TextComponent textComponent) {
		if (!playerEntity.isSpectator() && !containerLock.method_5472(playerEntity.method_6047())) {
			playerEntity.method_7353(new TranslatableTextComponent("container.isLocked", textComponent), true);
			playerEntity.method_17356(SoundEvents.field_14731, SoundCategory.field_15245, 1.0F, 1.0F);
			return false;
		} else {
			return true;
		}
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return this.checkUnlocked(playerEntity) ? this.createContainer(i, playerInventory) : null;
	}

	protected abstract Container createContainer(int i, PlayerInventory playerInventory);
}
