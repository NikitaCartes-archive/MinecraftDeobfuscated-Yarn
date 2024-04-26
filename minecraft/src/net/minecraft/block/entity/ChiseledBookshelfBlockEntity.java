package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public class ChiseledBookshelfBlockEntity extends BlockEntity implements Inventory {
	public static final int MAX_BOOKS = 6;
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
	private int lastInteractedSlot = -1;

	public ChiseledBookshelfBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.CHISELED_BOOKSHELF, pos, state);
	}

	private void updateState(int interactedSlot) {
		if (interactedSlot >= 0 && interactedSlot < 6) {
			this.lastInteractedSlot = interactedSlot;
			BlockState blockState = this.getCachedState();

			for (int i = 0; i < ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); i++) {
				boolean bl = !this.getStack(i).isEmpty();
				BooleanProperty booleanProperty = (BooleanProperty)ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i);
				blockState = blockState.with(booleanProperty, Boolean.valueOf(bl));
			}

			((World)Objects.requireNonNull(this.world)).setBlockState(this.pos, blockState, Block.NOTIFY_ALL);
			this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(blockState));
		} else {
			LOGGER.error("Expected slot 0-5, got {}", interactedSlot);
		}
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.inventory.clear();
		Inventories.readNbt(nbt, this.inventory, registryLookup);
		this.lastInteractedSlot = nbt.getInt("last_interacted_slot");
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		Inventories.writeNbt(nbt, this.inventory, true, registryLookup);
		nbt.putInt("last_interacted_slot", this.lastInteractedSlot);
	}

	public int getFilledSlotCount() {
		return (int)this.inventory.stream().filter(Predicate.not(ItemStack::isEmpty)).count();
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Override
	public int size() {
		return 6;
	}

	@Override
	public boolean isEmpty() {
		return this.inventory.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.inventory.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack itemStack = (ItemStack)Objects.requireNonNullElse(this.inventory.get(slot), ItemStack.EMPTY);
		this.inventory.set(slot, ItemStack.EMPTY);
		if (!itemStack.isEmpty()) {
			this.updateState(slot);
		}

		return itemStack;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeStack(slot, 1);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (stack.isIn(ItemTags.BOOKSHELF_BOOKS)) {
			this.inventory.set(slot, stack);
			this.updateState(slot);
		} else if (stack.isEmpty()) {
			this.removeStack(slot, 1);
		}
	}

	@Override
	public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
		return hopperInventory.containsAny(
			(Predicate<ItemStack>)(stack2 -> stack2.isEmpty()
					? true
					: ItemStack.areItemsAndComponentsEqual(stack, stack2) && stack2.getCount() + stack.getCount() <= hopperInventory.getMaxCount(stack2))
		);
	}

	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return Inventory.canPlayerUse(this, player);
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return stack.isIn(ItemTags.BOOKSHELF_BOOKS) && this.getStack(slot).isEmpty() && stack.getCount() == this.getMaxCountPerStack();
	}

	public int getLastInteractedSlot() {
		return this.lastInteractedSlot;
	}

	@Override
	protected void readComponents(BlockEntity.ComponentsAccess components) {
		super.readComponents(components);
		components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.inventory);
	}

	@Override
	protected void addComponents(ComponentMap.Builder componentMapBuilder) {
		super.addComponents(componentMapBuilder);
		componentMapBuilder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.inventory));
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		nbt.remove("Items");
	}
}
