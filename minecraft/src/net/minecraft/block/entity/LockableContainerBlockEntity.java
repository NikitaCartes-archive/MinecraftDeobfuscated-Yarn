package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.container.Container;
import net.minecraft.container.LockContainer;
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
	private LockContainer lock = LockContainer.EMPTY;
	private TextComponent customName;

	protected LockableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.lock = LockContainer.deserialize(compoundTag);
		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		this.lock.serialize(compoundTag);
		if (this.customName != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
		}

		return compoundTag;
	}

	public void setCustomName(TextComponent textComponent) {
		this.customName = textComponent;
	}

	@Override
	public TextComponent getName() {
		return this.customName != null ? this.customName : this.method_17823();
	}

	@Override
	public TextComponent getDisplayName() {
		return this.getName();
	}

	@Nullable
	@Override
	public TextComponent getCustomName() {
		return this.customName;
	}

	protected abstract TextComponent method_17823();

	public boolean method_17489(PlayerEntity playerEntity) {
		return method_17487(playerEntity, this.lock, this.getDisplayName());
	}

	public static boolean method_17487(PlayerEntity playerEntity, LockContainer lockContainer, TextComponent textComponent) {
		if (!playerEntity.isSpectator() && !lockContainer.isEmpty(playerEntity.getMainHandStack())) {
			playerEntity.addChatMessage(new TranslatableTextComponent("container.isLocked", textComponent), true);
			playerEntity.method_17356(SoundEvents.field_14731, SoundCategory.field_15245, 1.0F, 1.0F);
			return false;
		} else {
			return true;
		}
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return this.method_17489(playerEntity) ? this.createContainer(i, playerInventory) : null;
	}

	protected abstract Container createContainer(int i, PlayerInventory playerInventory);
}
