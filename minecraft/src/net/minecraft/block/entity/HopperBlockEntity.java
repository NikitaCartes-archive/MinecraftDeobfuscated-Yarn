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
import net.minecraft.sortme.Hopper;
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
	private DefaultedList<ItemStack> field_12024 = DefaultedList.create(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long lastTickTime;

	public HopperBlockEntity() {
		super(BlockEntityType.HOPPER);
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.field_12024 = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.method_11283(compoundTag)) {
			Inventories.method_5429(compoundTag, this.field_12024);
		}

		this.transferCooldown = compoundTag.getInt("TransferCooldown");
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		if (!this.method_11286(compoundTag)) {
			Inventories.method_5426(compoundTag, this.field_12024);
		}

		compoundTag.putInt("TransferCooldown", this.transferCooldown);
		return compoundTag;
	}

	@Override
	public int getInvSize() {
		return this.field_12024.size();
	}

	@Override
	public ItemStack method_5434(int i, int j) {
		this.checkLootInteraction(null);
		return Inventories.method_5430(this.method_11282(), i, j);
	}

	@Override
	public void method_5447(int i, ItemStack itemStack) {
		this.checkLootInteraction(null);
		this.method_11282().set(i, itemStack);
		if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
			itemStack.setAmount(this.getInvMaxStackAmount());
		}
	}

	@Override
	protected TextComponent method_17823() {
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
			if (!this.needsCooldown() && (Boolean)this.method_11010().method_11654(HopperBlock.field_11126)) {
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
		for (ItemStack itemStack : this.field_12024) {
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
		for (ItemStack itemStack : this.field_12024) {
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
			Direction direction = ((Direction)this.method_11010().method_11654(HopperBlock.field_11129)).getOpposite();
			if (this.method_11258(inventory, direction)) {
				return false;
			} else {
				for (int i = 0; i < this.getInvSize(); i++) {
					if (!this.method_5438(i).isEmpty()) {
						ItemStack itemStack = this.method_5438(i).copy();
						ItemStack itemStack2 = method_11260(this, inventory, this.method_5434(i, 1), direction);
						if (itemStack2.isEmpty()) {
							inventory.markDirty();
							return true;
						}

						this.method_5447(i, itemStack);
					}
				}

				return false;
			}
		}
	}

	private static IntStream method_17767(Inventory inventory, Direction direction) {
		return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory)inventory).method_5494(direction)) : IntStream.range(0, inventory.getInvSize());
	}

	private boolean method_11258(Inventory inventory, Direction direction) {
		return method_17767(inventory, direction).allMatch(i -> {
			ItemStack itemStack = inventory.method_5438(i);
			return itemStack.getAmount() >= itemStack.getMaxAmount();
		});
	}

	private static boolean method_11257(Inventory inventory, Direction direction) {
		return method_17767(inventory, direction).allMatch(i -> inventory.method_5438(i).isEmpty());
	}

	public static boolean extract(Hopper hopper) {
		Inventory inventory = getInputInventory(hopper);
		if (inventory != null) {
			Direction direction = Direction.DOWN;
			return method_11257(inventory, direction) ? false : method_17767(inventory, direction).anyMatch(i -> method_11261(hopper, inventory, i, direction));
		} else {
			for (ItemEntity itemEntity : getInputItemEntities(hopper)) {
				if (extract(hopper, itemEntity)) {
					return true;
				}
			}

			return false;
		}
	}

	private static boolean method_11261(Hopper hopper, Inventory inventory, int i, Direction direction) {
		ItemStack itemStack = inventory.method_5438(i);
		if (!itemStack.isEmpty() && method_11252(inventory, itemStack, i, direction)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = method_11260(inventory, hopper, inventory.method_5434(i, 1), null);
			if (itemStack3.isEmpty()) {
				inventory.markDirty();
				return true;
			}

			inventory.method_5447(i, itemStack2);
		}

		return false;
	}

	public static boolean extract(Inventory inventory, ItemEntity itemEntity) {
		boolean bl = false;
		ItemStack itemStack = itemEntity.method_6983().copy();
		ItemStack itemStack2 = method_11260(null, inventory, itemStack, null);
		if (itemStack2.isEmpty()) {
			bl = true;
			itemEntity.invalidate();
		} else {
			itemEntity.method_6979(itemStack2);
		}

		return bl;
	}

	public static ItemStack method_11260(@Nullable Inventory inventory, Inventory inventory2, ItemStack itemStack, @Nullable Direction direction) {
		if (inventory2 instanceof SidedInventory && direction != null) {
			SidedInventory sidedInventory = (SidedInventory)inventory2;
			int[] is = sidedInventory.method_5494(direction);

			for (int i = 0; i < is.length && !itemStack.isEmpty(); i++) {
				itemStack = method_11253(inventory, inventory2, itemStack, is[i], direction);
			}
		} else {
			int j = inventory2.getInvSize();

			for (int k = 0; k < j && !itemStack.isEmpty(); k++) {
				itemStack = method_11253(inventory, inventory2, itemStack, k, direction);
			}
		}

		return itemStack;
	}

	private static boolean method_11244(Inventory inventory, ItemStack itemStack, int i, @Nullable Direction direction) {
		return !inventory.method_5437(i, itemStack)
			? false
			: !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).method_5492(i, itemStack, direction);
	}

	private static boolean method_11252(Inventory inventory, ItemStack itemStack, int i, Direction direction) {
		return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).method_5493(i, itemStack, direction);
	}

	private static ItemStack method_11253(@Nullable Inventory inventory, Inventory inventory2, ItemStack itemStack, int i, @Nullable Direction direction) {
		ItemStack itemStack2 = inventory2.method_5438(i);
		if (method_11244(inventory2, itemStack, i, direction)) {
			boolean bl = false;
			boolean bl2 = inventory2.isInvEmpty();
			if (itemStack2.isEmpty()) {
				inventory2.method_5447(i, itemStack);
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
		Direction direction = this.method_11010().method_11654(HopperBlock.field_11129);
		return method_11250(this.getWorld(), this.field_11867.method_10093(direction));
	}

	@Nullable
	public static Inventory getInputInventory(Hopper hopper) {
		return getInventoryAt(hopper.getWorld(), hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ());
	}

	public static List<ItemEntity> getInputItemEntities(Hopper hopper) {
		return (List<ItemEntity>)hopper.method_11262()
			.getBoundingBoxList()
			.stream()
			.flatMap(
				boundingBox -> hopper.getWorld()
						.method_8390(
							ItemEntity.class, boundingBox.offset(hopper.getHopperX() - 0.5, hopper.getHopperY() - 0.5, hopper.getHopperZ() - 0.5), EntityPredicates.VALID_ENTITY
						)
						.stream()
			)
			.collect(Collectors.toList());
	}

	@Nullable
	public static Inventory method_11250(World world, BlockPos blockPos) {
		return getInventoryAt(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
	}

	@Nullable
	public static Inventory getInventoryAt(World world, double d, double e, double f) {
		Inventory inventory = null;
		BlockPos blockPos = new BlockPos(d, e, f);
		BlockState blockState = world.method_8320(blockPos);
		Block block = blockState.getBlock();
		if (block instanceof InventoryProvider) {
			inventory = ((InventoryProvider)block).method_17680(blockState, world, blockPos);
		} else if (block.hasBlockEntity()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof Inventory) {
				inventory = (Inventory)blockEntity;
				if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
					inventory = ChestBlock.method_17458(blockState, world, blockPos, true);
				}
			}
		}

		if (inventory == null) {
			List<Entity> list = world.method_8333(
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
		return (double)this.field_11867.getX() + 0.5;
	}

	@Override
	public double getHopperY() {
		return (double)this.field_11867.getY() + 0.5;
	}

	@Override
	public double getHopperZ() {
		return (double)this.field_11867.getZ() + 0.5;
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
	protected DefaultedList<ItemStack> method_11282() {
		return this.field_12024;
	}

	@Override
	protected void method_11281(DefaultedList<ItemStack> defaultedList) {
		this.field_12024 = defaultedList;
	}

	public void onEntityCollided(Entity entity) {
		if (entity instanceof ItemEntity) {
			BlockPos blockPos = this.method_11016();
			if (VoxelShapes.method_1074(
				VoxelShapes.method_1078(entity.method_5829().offset((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()))),
				this.method_11262(),
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
