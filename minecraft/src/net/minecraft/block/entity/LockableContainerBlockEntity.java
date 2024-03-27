package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public abstract class LockableContainerBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory, Nameable {
	private ContainerLock lock = ContainerLock.EMPTY;
	@Nullable
	private Text customName;

	protected LockableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.lock = ContainerLock.fromNbt(nbt);
		if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
			this.customName = Text.Serialization.fromJson(nbt.getString("CustomName"), registryLookup);
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		this.lock.writeNbt(nbt);
		if (this.customName != null) {
			nbt.putString("CustomName", Text.Serialization.toJsonString(this.customName, registryLookup));
		}
	}

	@Override
	public Text getName() {
		return this.customName != null ? this.customName : this.getContainerName();
	}

	@Override
	public Text getDisplayName() {
		return this.getName();
	}

	@Nullable
	@Override
	public Text getCustomName() {
		return this.customName;
	}

	protected abstract Text getContainerName();

	public boolean checkUnlocked(PlayerEntity player) {
		return checkUnlocked(player, this.lock, this.getDisplayName());
	}

	public static boolean checkUnlocked(PlayerEntity player, ContainerLock lock, Text containerName) {
		if (!player.isSpectator() && !lock.canOpen(player.getMainHandStack())) {
			player.sendMessage(Text.translatable("container.isLocked", containerName), true);
			player.playSoundToPlayer(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return false;
		} else {
			return true;
		}
	}

	protected abstract DefaultedList<ItemStack> getHeldStacks();

	protected abstract void setHeldStacks(DefaultedList<ItemStack> inventory);

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.getHeldStacks()) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.getHeldStacks().get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack itemStack = Inventories.splitStack(this.getHeldStacks(), slot, amount);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(this.getHeldStacks(), slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.getHeldStacks().set(slot, stack);
		stack.capCount(this.getMaxCount(stack));
		this.markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return Inventory.canPlayerUse(this, player);
	}

	@Override
	public void clear() {
		this.getHeldStacks().clear();
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return this.checkUnlocked(playerEntity) ? this.createScreenHandler(i, playerInventory) : null;
	}

	protected abstract ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory);

	@Override
	protected void readComponents(BlockEntity.ComponentsAccess components) {
		super.readComponents(components);
		this.customName = components.get(DataComponentTypes.CUSTOM_NAME);
		this.lock = components.getOrDefault(DataComponentTypes.LOCK, ContainerLock.EMPTY);
		components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.getHeldStacks());
	}

	@Override
	protected void addComponents(ComponentMap.Builder componentMapBuilder) {
		super.addComponents(componentMapBuilder);
		componentMapBuilder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
		if (!this.lock.equals(ContainerLock.EMPTY)) {
			componentMapBuilder.add(DataComponentTypes.LOCK, this.lock);
		}

		componentMapBuilder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.getHeldStacks()));
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		nbt.remove("CustomName");
		nbt.remove("Lock");
		nbt.remove("Items");
	}
}
