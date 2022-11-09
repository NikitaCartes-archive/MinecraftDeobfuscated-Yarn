package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackMappingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class ChiseledBookshelfBlockEntity extends BlockEntity implements Inventory {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int MAX_BOOKS = 6;
	private final StackMappingInventory books = new StackMappingInventory(6);

	public ChiseledBookshelfBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.CHISELED_BOOKSHELF, pos, state);
	}

	private void updateState() {
		int i = (Integer)this.getCachedState().get(Properties.BOOKS_STORED);
		if (i != this.books.getItemCount()) {
			((World)Objects.requireNonNull(this.world))
				.setBlockState(
					this.pos,
					this.getCachedState()
						.with(Properties.BOOKS_STORED, Integer.valueOf(this.books.getItemCount()))
						.with(Properties.LAST_INTERACTION_BOOK_SLOT, Integer.valueOf(i > this.books.getItemCount() ? this.books.getItemCount() + 1 : this.books.getItemCount())),
					Block.NOTIFY_ALL
				);
		}
	}

	public ItemStack getLastBook() {
		ItemStack itemStack = this.books.removeTopStack();
		if (!itemStack.isEmpty()) {
			this.updateState();
		}

		return itemStack;
	}

	public List<ItemStack> getAndClearBooks() {
		return this.books.clear();
	}

	public boolean addBook(ItemStack stack) {
		if (this.isFull()) {
			return false;
		} else if (stack.getCount() > 1) {
			LOGGER.warn("tried to add a stack with more than one items {} at {}", stack, this.pos);
			return false;
		} else if (!stack.isIn(ItemTags.BOOKSHELF_BOOKS)) {
			LOGGER.warn("tried to add a non book: {} at {}", stack, this.pos);
			return false;
		} else if (!this.books.addStack(stack)) {
			LOGGER.warn("failed to add {} at {}", stack, this.pos);
			return false;
		} else {
			this.updateState();
			return true;
		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(6, ItemStack.EMPTY);
		Inventories.readNbt(nbt, defaultedList);
		this.books.clear();

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = defaultedList.get(i);
			if (!itemStack.isEmpty()) {
				this.books.addStack(itemStack, i);
			}
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		Inventories.writeNbt(nbt, getBooksAsList(this.books), true);
	}

	private static DefaultedList<ItemStack> getBooksAsList(StackMappingInventory inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(6, ItemStack.EMPTY);

		for (int i = 0; i < 6; i++) {
			defaultedList.set(i, inventory.getStack(i));
		}

		return defaultedList;
	}

	@Override
	public void clear() {
		this.books.clear();
	}

	public int getBookCount() {
		return this.books.getItemCount();
	}

	public boolean isFull() {
		return this.books.isFull();
	}

	@Override
	public int size() {
		return 6;
	}

	@Override
	public void markDirty() {
		this.books.flatten();
		this.updateState();
	}

	@Override
	public boolean isEmpty() {
		return this.books.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.books.getStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack itemStack = this.removeStack(slot);
		this.updateState();
		return itemStack;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.books.removeStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (this.books.setStack(stack, slot)) {
			this.updateState();
		}
	}

	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		if (this.world == null) {
			return false;
		} else {
			return this.world.getBlockEntity(this.pos) != this
				? false
				: player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
		}
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return !this.isFull() && stack.isIn(ItemTags.BOOKSHELF_BOOKS) && this.books.hasSlot(slot);
	}

	@Override
	public int count(Item item) {
		return (int)this.books.getStacks().stream().filter(stack -> stack.isOf(item)).count();
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		return this.books.getStacks().stream().anyMatch(stack -> items.contains(stack.getItem()));
	}

	@Override
	public boolean containsAny(Predicate<ItemStack> predicate) {
		return this.books.getStacks().stream().anyMatch(predicate);
	}
}
