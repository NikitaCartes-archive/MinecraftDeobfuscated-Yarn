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
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class HopperBlockEntity extends LootableContainerBlockEntity implements Hopper, Tickable {
	private DefaultedList<ItemStack> inventory = DefaultedList.create(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long lastTickTime;

	public HopperBlockEntity() {
		super(BlockEntityType.HOPPER);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
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
	public ItemStack takeInvStack(int i, int j) {
		this.checkLootInteraction(null);
		return Inventories.splitStack(this.getInvStackList(), i, j);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		this.checkLootInteraction(null);
		this.getInvStackList().set(i, itemStack);
		if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
			itemStack.setAmount(this.getInvMaxStackAmount());
		}
	}

	@Override
	protected TextComponent getContainerName() {
		return new TranslatableTextComponent("container.hopper");
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

	private boolean insertAndExtract(Supplier<Boolean> supplier) {
		if (this.world != null && !this.world.isClient) {
			if (!this.needsCooldown() && (Boolean)this.getCachedState().get(HopperBlock.ENABLED)) {
				boolean bl = false;
				if (!this.isEmpty()) {
					bl = this.insert();
				}

				if (!this.isFull()) {
					bl |= supplier.get();
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

	private boolean isEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isInvEmpty() {
		return this.isEmpty();
	}

	private boolean isFull() {
		for (ItemStack itemStack : this.inventory) {
			if (itemStack.isEmpty() || itemStack.getAmount() != itemStack.getMaxAmount()) {
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

	private static IntStream getAvailableSlots(Inventory inventory, Direction direction) {
		return inventory instanceof SidedInventory
			? IntStream.of(((SidedInventory)inventory).getInvAvailableSlots(direction))
			: IntStream.range(0, inventory.getInvSize());
	}

	private boolean isInventoryFull(Inventory inventory, Direction direction) {
		return getAvailableSlots(inventory, direction).allMatch(i -> {
			ItemStack itemStack = inventory.getInvStack(i);
			return itemStack.getAmount() >= itemStack.getMaxAmount();
		});
	}

	private static boolean isInventoryEmpty(Inventory inventory, Direction direction) {
		return getAvailableSlots(inventory, direction).allMatch(i -> inventory.getInvStack(i).isEmpty());
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

	private static boolean extract(Hopper hopper, Inventory inventory, int i, Direction direction) {
		ItemStack itemStack = inventory.getInvStack(i);
		if (!itemStack.isEmpty() && canExtract(inventory, itemStack, i, direction)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = transfer(inventory, hopper, inventory.takeInvStack(i, 1), null);
			if (itemStack3.isEmpty()) {
				inventory.markDirty();
				return true;
			}

			inventory.setInvStack(i, itemStack2);
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

	public static ItemStack transfer(@Nullable Inventory inventory, Inventory inventory2, ItemStack itemStack, @Nullable Direction direction) {
		if (inventory2 instanceof SidedInventory && direction != null) {
			SidedInventory sidedInventory = (SidedInventory)inventory2;
			int[] is = sidedInventory.getInvAvailableSlots(direction);

			for (int i = 0; i < is.length && !itemStack.isEmpty(); i++) {
				itemStack = transfer(inventory, inventory2, itemStack, is[i], direction);
			}
		} else {
			int j = inventory2.getInvSize();

			for (int k = 0; k < j && !itemStack.isEmpty(); k++) {
				itemStack = transfer(inventory, inventory2, itemStack, k, direction);
			}
		}

		return itemStack;
	}

	private static boolean canInsert(Inventory inventory, ItemStack itemStack, int i, @Nullable Direction direction) {
		return !inventory.isValidInvStack(i, itemStack)
			? false
			: !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsertInvStack(i, itemStack, direction);
	}

	private static boolean canExtract(Inventory inventory, ItemStack itemStack, int i, Direction direction) {
		return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canExtractInvStack(i, itemStack, direction);
	}

	private static ItemStack transfer(@Nullable Inventory inventory, Inventory inventory2, ItemStack itemStack, int i, @Nullable Direction direction) {
		ItemStack itemStack2 = inventory2.getInvStack(i);
		if (canInsert(inventory2, itemStack, i, direction)) {
			boolean bl = false;
			boolean bl2 = inventory2.isInvEmpty();
			if (itemStack2.isEmpty()) {
				inventory2.setInvStack(i, itemStack);
				itemStack = ItemStack.EMPTY;
				bl = true;
			} else if (canMergeItems(itemStack2, itemStack)) {
				int j = itemStack.getMaxAmount() - itemStack2.getAmount();
				int k = Math.min(itemStack.getAmount(), j);
				itemStack.subtractAmount(k);
				itemStack2.addAmount(k);
				bl = k > 0;
			}

			if (bl) {
				if (bl2 && inventory2 instanceof HopperBlockEntity) {
					HopperBlockEntity hopperBlockEntity = (HopperBlockEntity)inventory2;
					if (!hopperBlockEntity.isDisabled()) {
						int k = 0;
						if (inventory instanceof HopperBlockEntity) {
							HopperBlockEntity hopperBlockEntity2 = (HopperBlockEntity)inventory;
							if (hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
								k = 1;
							}
						}

						hopperBlockEntity.setCooldown(8 - k);
					}
				}

				inventory2.markDirty();
			}
		}

		return itemStack;
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
				boundingBox -> hopper.getWorld()
						.getEntities(
							ItemEntity.class, boundingBox.offset(hopper.getHopperX() - 0.5, hopper.getHopperY() - 0.5, hopper.getHopperZ() - 0.5), EntityPredicates.VALID_ENTITY
						)
						.stream()
			)
			.collect(Collectors.toList());
	}

	@Nullable
	public static Inventory getInventoryAt(World world, BlockPos blockPos) {
		return getInventoryAt(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
	}

	@Nullable
	public static Inventory getInventoryAt(World world, double d, double e, double f) {
		Inventory inventory = null;
		BlockPos blockPos = new BlockPos(d, e, f);
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block instanceof InventoryProvider) {
			inventory = ((InventoryProvider)block).getInventory(blockState, world, blockPos);
		} else if (block.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof Inventory) {
				inventory = (Inventory)blockEntity;
				if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
					inventory = ChestBlock.getInventory(blockState, world, blockPos, true);
				}
			}
		}

		if (inventory == null) {
			List<Entity> list = world.getEntities(
				(Entity)null, new BoundingBox(d - 0.5, e - 0.5, f - 0.5, d + 0.5, e + 0.5, f + 0.5), EntityPredicates.VALID_INVENTORIES
			);
			if (!list.isEmpty()) {
				inventory = (Inventory)list.get(world.random.nextInt(list.size()));
			}
		}

		return inventory;
	}

	private static boolean canMergeItems(ItemStack itemStack, ItemStack itemStack2) {
		if (itemStack.getItem() != itemStack2.getItem()) {
			return false;
		} else if (itemStack.getDamage() != itemStack2.getDamage()) {
			return false;
		} else {
			return itemStack.getAmount() > itemStack.getMaxAmount() ? false : ItemStack.areTagsEqual(itemStack, itemStack2);
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

	private void setCooldown(int i) {
		this.transferCooldown = i;
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
	protected void setInvStackList(DefaultedList<ItemStack> defaultedList) {
		this.inventory = defaultedList;
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
