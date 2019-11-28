package net.minecraft.block.entity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.container.Container;
import net.minecraft.container.HopperContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class HopperBlockEntity extends LootableContainerBlockEntity implements Hopper, Tickable {
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long lastTickTime;

	public HopperBlockEntity() {
		super(BlockEntityType.HOPPER);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(compoundTag)) {
			Inventories.fromTag(compoundTag, this.inventory);
		}

		this.transferCooldown = compoundTag.getInt("TransferCooldown");
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (!this.serializeLootTable(compoundTag)) {
			Inventories.toTag(compoundTag, this.inventory);
		}

		compoundTag.putInt("TransferCooldown", this.transferCooldown);
		return compoundTag;
	}

	@Override
	public int getInvSize() {
		return this.inventory.size();
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		this.checkLootInteraction(null);
		return Inventories.splitStack(this.getInvStackList(), slot, amount);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.checkLootInteraction(null);
		this.getInvStackList().set(slot, stack);
		if (stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.hopper");
	}

	@Override
	public void tick() {
		if (this.world != null && !this.world.isClient) {
			this.transferCooldown--;
			this.lastTickTime = this.world.getTime();
			if (!this.needsCooldown()) {
				this.setCooldown(0);
				this.insertAndExtract(() -> extract(this));
			}
		}
	}

	private boolean insertAndExtract(Supplier<Boolean> extractMethod) {
		if (this.world != null && !this.world.isClient) {
			if (!this.needsCooldown() && (Boolean)this.getCachedState().get(HopperBlock.ENABLED)) {
				boolean bl = false;
				if (!this.isInvEmpty()) {
					bl = this.insert();
				}

				if (!this.isFull()) {
					bl |= extractMethod.get();
				}

				if (bl) {
					this.setCooldown(8);
					this.markDirty();
					return true;
				}
			}

			return false;
		} else {
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

	private boolean insert() {
		Inventory inventory = this.getOutputInventory();
		if (inventory == null) {
			return false;
		} else {
			Direction direction = ((Direction)this.getCachedState().get(HopperBlock.FACING)).getOpposite();
			if (this.isInventoryFull(inventory, direction)) {
				return false;
			} else {
				for (int i = 0; i < this.getInvSize(); i++) {
					if (!this.getInvStack(i).isEmpty()) {
						ItemStack itemStack = this.getInvStack(i).copy();
						ItemStack itemStack2 = transfer(this, inventory, this.takeInvStack(i, 1), direction);
						if (itemStack2.isEmpty()) {
							inventory.markDirty();
							return true;
						}

						this.setInvStack(i, itemStack);
					}
				}

				return false;
			}
		}
	}

	private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
		return inventory instanceof SidedInventory
			? IntStream.of(((SidedInventory)inventory).getInvAvailableSlots(side))
			: IntStream.range(0, inventory.getInvSize());
	}

	private boolean isInventoryFull(Inventory inv, Direction direction) {
		return getAvailableSlots(inv, direction).allMatch(i -> {
			ItemStack itemStack = inv.getInvStack(i);
			return itemStack.getCount() >= itemStack.getMaxCount();
		});
	}

	private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
		return getAvailableSlots(inv, facing).allMatch(i -> inv.getInvStack(i).isEmpty());
	}

	public static boolean extract(Hopper hopper) {
		Inventory inventory = getInputInventory(hopper);
		if (inventory != null) {
			Direction direction = Direction.DOWN;
			return isInventoryEmpty(inventory, direction) ? false : getAvailableSlots(inventory, direction).anyMatch(i -> extract(hopper, inventory, i, direction));
		} else {
			for (ItemEntity itemEntity : getInputItemEntities(hopper)) {
				if (extract(hopper, itemEntity)) {
					return true;
				}
			}

			return false;
		}
	}

	private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
		ItemStack itemStack = inventory.getInvStack(slot);
		if (!itemStack.isEmpty() && canExtract(inventory, itemStack, slot, side)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = transfer(inventory, hopper, inventory.takeInvStack(slot, 1), null);
			if (itemStack3.isEmpty()) {
				inventory.markDirty();
				return true;
			}

			inventory.setInvStack(slot, itemStack2);
		}

		return false;
	}

	public static boolean extract(Inventory inventory, ItemEntity itemEntity) {
		boolean bl = false;
		ItemStack itemStack = itemEntity.getStack().copy();
		ItemStack itemStack2 = transfer(null, inventory, itemStack, null);
		if (itemStack2.isEmpty()) {
			bl = true;
			itemEntity.remove();
		} else {
			itemEntity.setStack(itemStack2);
		}

		return bl;
	}

	public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
		if (to instanceof SidedInventory && side != null) {
			SidedInventory sidedInventory = (SidedInventory)to;
			int[] is = sidedInventory.getInvAvailableSlots(side);

			for (int i = 0; i < is.length && !stack.isEmpty(); i++) {
				stack = transfer(from, to, stack, is[i], side);
			}
		} else {
			int j = to.getInvSize();

			for (int k = 0; k < j && !stack.isEmpty(); k++) {
				stack = transfer(from, to, stack, k, side);
			}
		}

		return stack;
	}

	private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
		return !inventory.isValidInvStack(slot, stack)
			? false
			: !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsertInvStack(slot, stack, side);
	}

	private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
		return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtractInvStack(slot, stack, facing);
	}

	private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction direction) {
		ItemStack itemStack = to.getInvStack(slot);
		if (canInsert(to, stack, slot, direction)) {
			boolean bl = false;
			boolean bl2 = to.isInvEmpty();
			if (itemStack.isEmpty()) {
				to.setInvStack(slot, stack);
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
				if (bl2 && to instanceof HopperBlockEntity) {
					HopperBlockEntity hopperBlockEntity = (HopperBlockEntity)to;
					if (!hopperBlockEntity.isDisabled()) {
						int j = 0;
						if (from instanceof HopperBlockEntity) {
							HopperBlockEntity hopperBlockEntity2 = (HopperBlockEntity)from;
							if (hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
								j = 1;
							}
						}

						hopperBlockEntity.setCooldown(8 - j);
					}
				}

				to.markDirty();
			}
		}

		return stack;
	}

	@Nullable
	private Inventory getOutputInventory() {
		Direction direction = this.getCachedState().get(HopperBlock.FACING);
		return getInventoryAt(this.getWorld(), this.pos.offset(direction));
	}

	@Nullable
	public static Inventory getInputInventory(Hopper hopper) {
		return getInventoryAt(hopper.getWorld(), hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ());
	}

	public static List<ItemEntity> getInputItemEntities(Hopper hopper) {
		return (List<ItemEntity>)hopper.getInputAreaShape()
			.getBoundingBoxes()
			.stream()
			.flatMap(
				box -> hopper.getWorld()
						.getEntities(ItemEntity.class, box.offset(hopper.getHopperX() - 0.5, hopper.getHopperY() - 0.5, hopper.getHopperZ() - 0.5), EntityPredicates.VALID_ENTITY)
						.stream()
			)
			.collect(Collectors.toList());
	}

	@Nullable
	public static Inventory getInventoryAt(World world, BlockPos blockPos) {
		return getInventoryAt(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
	}

	@Nullable
	public static Inventory getInventoryAt(World world, double x, double y, double z) {
		Inventory inventory = null;
		BlockPos blockPos = new BlockPos(x, y, z);
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block instanceof InventoryProvider) {
			inventory = ((InventoryProvider)block).getInventory(blockState, world, blockPos);
		} else if (block.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof Inventory) {
				inventory = (Inventory)blockEntity;
				if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
					inventory = ChestBlock.getInventory((ChestBlock)block, blockState, world, blockPos, true);
				}
			}
		}

		if (inventory == null) {
			List<Entity> list = world.getEntities((Entity)null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.VALID_INVENTORIES);
			if (!list.isEmpty()) {
				inventory = (Inventory)list.get(world.random.nextInt(list.size()));
			}
		}

		return inventory;
	}

	private static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getDamage() != second.getDamage()) {
			return false;
		} else {
			return first.getCount() > first.getMaxCount() ? false : ItemStack.areTagsEqual(first, second);
		}
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

	private void setCooldown(int cooldown) {
		this.transferCooldown = cooldown;
	}

	private boolean needsCooldown() {
		return this.transferCooldown > 0;
	}

	private boolean isDisabled() {
		return this.transferCooldown > 8;
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> list) {
		this.inventory = list;
	}

	public void onEntityCollided(Entity entity) {
		if (entity instanceof ItemEntity) {
			BlockPos blockPos = this.getPos();
			if (VoxelShapes.matchesAnywhere(
				VoxelShapes.cuboid(entity.getBoundingBox().offset((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()))),
				this.getInputAreaShape(),
				BooleanBiFunction.AND
			)) {
				this.insertAndExtract(() -> extract(this, (ItemEntity)entity));
			}
		}
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new HopperContainer(i, playerInventory, this);
	}
}
