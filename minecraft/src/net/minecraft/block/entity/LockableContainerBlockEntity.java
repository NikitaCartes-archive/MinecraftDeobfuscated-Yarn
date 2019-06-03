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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;

public abstract class LockableContainerBlockEntity extends BlockEntity implements Inventory, NameableContainerProvider, Nameable {
	private ContainerLock lock = ContainerLock.NONE;
	private Text field_17376;

	protected LockableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.lock = ContainerLock.deserialize(compoundTag);
		if (compoundTag.containsKey("CustomName", 8)) {
			this.field_17376 = Text.Serializer.fromJson(compoundTag.getString("CustomName"));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		this.lock.serialize(compoundTag);
		if (this.field_17376 != null) {
			compoundTag.putString("CustomName", Text.Serializer.toJson(this.field_17376));
		}

		return compoundTag;
	}

	public void method_17488(Text text) {
		this.field_17376 = text;
	}

	@Override
	public Text method_5477() {
		return this.field_17376 != null ? this.field_17376 : this.method_17823();
	}

	@Override
	public Text method_5476() {
		return this.method_5477();
	}

	@Nullable
	@Override
	public Text method_5797() {
		return this.field_17376;
	}

	protected abstract Text method_17823();

	public boolean checkUnlocked(PlayerEntity playerEntity) {
		return method_17487(playerEntity, this.lock, this.method_5476());
	}

	public static boolean method_17487(PlayerEntity playerEntity, ContainerLock containerLock, Text text) {
		if (!playerEntity.isSpectator() && !containerLock.isEmpty(playerEntity.getMainHandStack())) {
			playerEntity.method_7353(new TranslatableText("container.isLocked", text), true);
			playerEntity.playSound(SoundEvents.field_14731, SoundCategory.field_15245, 1.0F, 1.0F);
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
