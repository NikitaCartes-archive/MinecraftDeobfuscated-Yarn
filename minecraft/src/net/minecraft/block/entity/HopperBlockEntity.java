package net.minecraft.block.entity;

import java.util.List;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HopperBlockEntity extends LootableContainerBlockEntity implements Hopper {
	public static final int TRANSFER_COOLDOWN = 8;
	public static final int INVENTORY_SIZE = 5;
	private static final int[][] AVAILABLE_SLOTS_CACHE = new int[54][];
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long lastTickTime;
	private Direction facing;

	public HopperBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.HOPPER, pos, state);
		this.facing = state.get(HopperBlock.FACING);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.readNbt(nbt, registries);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.readLootTable(nbt)) {
			Inventories.readNbt(nbt, this.inventory, registries);
		}

		this.transferCooldown = nbt.getInt("TransferCooldown");
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.writeNbt(nbt, registries);
		if (!this.writeLootTable(nbt)) {
			Inventories.writeNbt(nbt, this.inventory, registries);
		}

		nbt.putInt("TransferCooldown", this.transferCooldown);
	}

	@Override
	public int size() {
		return this.inventory.size();
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		this.generateLoot(null);
		return Inventories.splitStack(this.getHeldStacks(), slot, amount);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.generateLoot(null);
		this.getHeldStacks().set(slot, stack);
		stack.capCount(this.getMaxCount(stack));
	}

	@Override
	public void setCachedState(BlockState state) {
		super.setCachedState(state);
		this.facing = state.get(HopperBlock.FACING);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.hopper");
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity) {
		blockEntity.transferCooldown--;
		blockEntity.lastTickTime = world.getTime();
		if (!blockEntity.needsCooldown()) {
			blockEntity.setTransferCooldown(0);
			insertAndExtract(world, pos, state, blockEntity, () -> extract(world, blockEntity));
		}
	}

	private static boolean insertAndExtract(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
		if (world.isClient) {
			return false;
		} else {
			if (!blockEntity.needsCooldown() && (Boolean)state.get(HopperBlock.ENABLED)) {
				boolean bl = false;
				if (!blockEntity.isEmpty()) {
					bl = insert(world, pos, blockEntity);
				}

				if (!blockEntity.isFull()) {
					bl |= booleanSupplier.getAsBoolean();
				}

				if (bl) {
					blockEntity.setTransferCooldown(8);
					markDirty(world, pos, state);
					return true;
				}
			}

			return false;
		}
	}

	private boolean isFull() {
		for (ItemStack itemStack : this.inventory) {
			if (itemStack.isEmpty() || itemStack.getCount() != itemStack.getMaxCount()) {
				return false;
			}
		}

		return true;
	}

	private static boolean insert(World world, BlockPos pos, HopperBlockEntity blockEntity) {
		Inventory inventory = getOutputInventory(world, pos, blockEntity);
		if (inventory == null) {
			return false;
		} else {
			Direction direction = blockEntity.facing.getOpposite();
			if (isInventoryFull(inventory, direction)) {
				return false;
			} else {
				for (int i = 0; i < blockEntity.size(); i++) {
					ItemStack itemStack = blockEntity.getStack(i);
					if (!itemStack.isEmpty()) {
						int j = itemStack.getCount();
						ItemStack itemStack2 = transfer(blockEntity, inventory, blockEntity.removeStack(i, 1), direction);
						if (itemStack2.isEmpty()) {
							inventory.markDirty();
							return true;
						}

						itemStack.setCount(j);
						if (j == 1) {
							blockEntity.setStack(i, itemStack);
						}
					}
				}

				return false;
			}
		}
	}

	private static int[] getAvailableSlots(Inventory inventory, Direction side) {
		if (inventory instanceof SidedInventory sidedInventory) {
			return sidedInventory.getAvailableSlots(side);
		} else {
			int i = inventory.size();
			if (i < AVAILABLE_SLOTS_CACHE.length) {
				int[] is = AVAILABLE_SLOTS_CACHE[i];
				if (is != null) {
					return is;
				} else {
					int[] js = indexArray(i);
					AVAILABLE_SLOTS_CACHE[i] = js;
					return js;
				}
			} else {
				return indexArray(i);
			}
		}
	}

	private static int[] indexArray(int size) {
		int[] is = new int[size];
		int i = 0;

		while (i < is.length) {
			is[i] = i++;
		}

		return is;
	}

	private static boolean isInventoryFull(Inventory inventory, Direction direction) {
		int[] is = getAvailableSlots(inventory, direction);

		for (int i : is) {
			ItemStack itemStack = inventory.getStack(i);
			if (itemStack.getCount() < itemStack.getMaxCount()) {
				return false;
			}
		}

		return true;
	}

	public static boolean extract(World world, Hopper hopper) {
		BlockPos blockPos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ());
		BlockState blockState = world.getBlockState(blockPos);
		Inventory inventory = getInputInventory(world, hopper, blockPos, blockState);
		if (inventory != null) {
			Direction direction = Direction.DOWN;

			for (int i : getAvailableSlots(inventory, direction)) {
				if (extract(hopper, inventory, i, direction)) {
					return true;
				}
			}

			return false;
		} else {
			boolean bl = hopper.canBlockFromAbove() && blockState.isFullCube(world, blockPos) && !blockState.isIn(BlockTags.DOES_NOT_BLOCK_HOPPERS);
			if (!bl) {
				for (ItemEntity itemEntity : getInputItemEntities(world, hopper)) {
					if (extract(hopper, itemEntity)) {
						return true;
					}
				}
			}

			return false;
		}
	}

	private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
		ItemStack itemStack = inventory.getStack(slot);
		if (!itemStack.isEmpty() && canExtract(hopper, inventory, itemStack, slot, side)) {
			int i = itemStack.getCount();
			ItemStack itemStack2 = transfer(inventory, hopper, inventory.removeStack(slot, 1), null);
			if (itemStack2.isEmpty()) {
				inventory.markDirty();
				return true;
			}

			itemStack.setCount(i);
			if (i == 1) {
				inventory.setStack(slot, itemStack);
			}
		}

		return false;
	}

	public static boolean extract(Inventory inventory, ItemEntity itemEntity) {
		boolean bl = false;
		ItemStack itemStack = itemEntity.getStack().copy();
		ItemStack itemStack2 = transfer(null, inventory, itemStack, null);
		if (itemStack2.isEmpty()) {
			bl = true;
			itemEntity.setStack(ItemStack.EMPTY);
			itemEntity.discard();
		} else {
			itemEntity.setStack(itemStack2);
		}

		return bl;
	}

	public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
		if (to instanceof SidedInventory sidedInventory && side != null) {
			int[] is = sidedInventory.getAvailableSlots(side);

			for (int i = 0; i < is.length && !stack.isEmpty(); i++) {
				stack = transfer(from, to, stack, is[i], side);
			}

			return stack;
		}

		int j = to.size();

		for (int i = 0; i < j && !stack.isEmpty(); i++) {
			stack = transfer(from, to, stack, i, side);
		}

		return stack;
	}

	private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
		if (!inventory.isValid(slot, stack)) {
			return false;
		} else {
			if (inventory instanceof SidedInventory sidedInventory && !sidedInventory.canInsert(slot, stack, side)) {
				return false;
			}

			return true;
		}
	}

	private static boolean canExtract(Inventory hopperInventory, Inventory fromInventory, ItemStack stack, int slot, Direction facing) {
		if (!fromInventory.canTransferTo(hopperInventory, slot, stack)) {
			return false;
		} else {
			if (fromInventory instanceof SidedInventory sidedInventory && !sidedInventory.canExtract(slot, stack, facing)) {
				return false;
			}

			return true;
		}
	}

	private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction side) {
		ItemStack itemStack = to.getStack(slot);
		if (canInsert(to, stack, slot, side)) {
			boolean bl = false;
			boolean bl2 = to.isEmpty();
			if (itemStack.isEmpty()) {
				to.setStack(slot, stack);
				stack = ItemStack.EMPTY;
				bl = true;
			} else if (canMergeItems(itemStack, stack)) {
				int i = stack.getMaxCount() - itemStack.getCount();
				int j = Math.min(stack.getCount(), i);
				stack.decrement(j);
				itemStack.increment(j);
				bl = j > 0;
			}

			if (bl) {
				if (bl2 && to instanceof HopperBlockEntity hopperBlockEntity && !hopperBlockEntity.isDisabled()) {
					int j = 0;
					if (from instanceof HopperBlockEntity hopperBlockEntity2 && hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
						j = 1;
					}

					hopperBlockEntity.setTransferCooldown(8 - j);
				}

				to.markDirty();
			}
		}

		return stack;
	}

	@Nullable
	private static Inventory getOutputInventory(World world, BlockPos pos, HopperBlockEntity blockEntity) {
		return getInventoryAt(world, pos.offset(blockEntity.facing));
	}

	@Nullable
	private static Inventory getInputInventory(World world, Hopper hopper, BlockPos pos, BlockState state) {
		return getInventoryAt(world, pos, state, hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ());
	}

	public static List<ItemEntity> getInputItemEntities(World world, Hopper hopper) {
		Box box = hopper.getInputAreaShape().offset(hopper.getHopperX() - 0.5, hopper.getHopperY() - 0.5, hopper.getHopperZ() - 0.5);
		return world.getEntitiesByClass(ItemEntity.class, box, EntityPredicates.VALID_ENTITY);
	}

	@Nullable
	public static Inventory getInventoryAt(World world, BlockPos pos) {
		return getInventoryAt(world, pos, world.getBlockState(pos), (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
	}

	@Nullable
	private static Inventory getInventoryAt(World world, BlockPos pos, BlockState state, double x, double y, double z) {
		Inventory inventory = getBlockInventoryAt(world, pos, state);
		if (inventory == null) {
			inventory = getEntityInventoryAt(world, x, y, z);
		}

		return inventory;
	}

	@Nullable
	private static Inventory getBlockInventoryAt(World world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		if (block instanceof InventoryProvider) {
			return ((InventoryProvider)block).getInventory(state, world, pos);
		} else if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof Inventory inventory) {
			if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
				inventory = ChestBlock.getInventory((ChestBlock)block, state, world, pos, true);
			}

			return inventory;
		} else {
			return null;
		}
	}

	@Nullable
	private static Inventory getEntityInventoryAt(World world, double x, double y, double z) {
		List<Entity> list = world.getOtherEntities((Entity)null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.VALID_INVENTORIES);
		return !list.isEmpty() ? (Inventory)list.get(world.random.nextInt(list.size())) : null;
	}

	private static boolean canMergeItems(ItemStack first, ItemStack second) {
		return first.getCount() <= first.getMaxCount() && ItemStack.areItemsAndComponentsEqual(first, second);
	}

	@Override
	public double getHopperX() {
		return (double)this.pos.getX() + 0.5;
	}

	@Override
	public double getHopperY() {
		return (double)this.pos.getY() + 0.5;
	}

	@Override
	public double getHopperZ() {
		return (double)this.pos.getZ() + 0.5;
	}

	@Override
	public boolean canBlockFromAbove() {
		return true;
	}

	private void setTransferCooldown(int transferCooldown) {
		this.transferCooldown = transferCooldown;
	}

	private boolean needsCooldown() {
		return this.transferCooldown > 0;
	}

	private boolean isDisabled() {
		return this.transferCooldown > 8;
	}

	@Override
	protected DefaultedList<ItemStack> getHeldStacks() {
		return this.inventory;
	}

	@Override
	protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
		this.inventory = inventory;
	}

	public static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, HopperBlockEntity blockEntity) {
		if (entity instanceof ItemEntity itemEntity
			&& !itemEntity.getStack().isEmpty()
			&& entity.getBoundingBox().offset((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ())).intersects(blockEntity.getInputAreaShape())) {
			insertAndExtract(world, pos, state, blockEntity, () -> extract(blockEntity, itemEntity));
		}
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new HopperScreenHandler(syncId, playerInventory, this);
	}
}
