package net.minecraft.block.entity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.container.Container;
import net.minecraft.container.HopperContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import net.minecraft.util.InventoryUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class HopperBlockEntity extends LootableContainerBlockEntity implements Hopper, Tickable {
	private DefaultedList<ItemStack> inventory = DefaultedList.create(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long field_12022;

	public HopperBlockEntity() {
		super(BlockEntityType.HOPPER);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(compoundTag)) {
			InventoryUtil.deserialize(compoundTag, this.inventory);
		}

		if (compoundTag.containsKey("CustomName", 8)) {
			this.setCustomName(TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName")));
		}

		this.transferCooldown = compoundTag.getInt("TransferCooldown");
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (!this.serializeLootTable(compoundTag)) {
			InventoryUtil.serialize(compoundTag, this.inventory);
		}

		compoundTag.putInt("TransferCooldown", this.transferCooldown);
		TextComponent textComponent = this.getCustomName();
		if (textComponent != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(textComponent));
		}

		return compoundTag;
	}

	@Override
	public int getInvSize() {
		return this.inventory.size();
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		this.checkLootInteraction(null);
		return InventoryUtil.splitStack(this.getInvStackList(), i, j);
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
	public TextComponent getName() {
		return (TextComponent)(this.customName != null ? this.customName : new TranslatableTextComponent("container.hopper"));
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public void tick() {
		if (this.world != null && !this.world.isRemote) {
			this.transferCooldown--;
			this.field_12022 = this.world.getTime();
			if (!this.isCooldown()) {
				this.setCooldown(0);
				this.method_11243(() -> tryExtract(this));
			}
		}
	}

	private boolean method_11243(Supplier<Boolean> supplier) {
		if (this.world != null && !this.world.isRemote) {
			if (!this.isCooldown() && (Boolean)this.getCachedState().get(HopperBlock.field_11126)) {
				boolean bl = false;
				if (!this.isEmpty()) {
					bl = this.tryInsert();
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

	private boolean tryInsert() {
		Inventory inventory = this.method_11255();
		if (inventory == null) {
			return false;
		} else {
			Direction direction = ((Direction)this.getCachedState().get(HopperBlock.field_11129)).getOpposite();
			if (this.method_11258(inventory, direction)) {
				return false;
			} else {
				for (int i = 0; i < this.getInvSize(); i++) {
					if (!this.getInvStack(i).isEmpty()) {
						ItemStack itemStack = this.getInvStack(i).copy();
						ItemStack itemStack2 = method_11260(this, inventory, this.takeInvStack(i, 1), direction);
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

	private boolean method_11258(Inventory inventory, Direction direction) {
		if (inventory instanceof SidedInventory) {
			SidedInventory sidedInventory = (SidedInventory)inventory;
			int[] is = sidedInventory.method_5494(direction);

			for (int i : is) {
				ItemStack itemStack = sidedInventory.getInvStack(i);
				if (itemStack.isEmpty() || itemStack.getAmount() != itemStack.getMaxAmount()) {
					return false;
				}
			}
		} else {
			int j = inventory.getInvSize();

			for (int k = 0; k < j; k++) {
				ItemStack itemStack2 = inventory.getInvStack(k);
				if (itemStack2.isEmpty() || itemStack2.getAmount() != itemStack2.getMaxAmount()) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean method_11257(Inventory inventory, Direction direction) {
		if (inventory instanceof SidedInventory) {
			SidedInventory sidedInventory = (SidedInventory)inventory;
			int[] is = sidedInventory.method_5494(direction);

			for (int i : is) {
				if (!sidedInventory.getInvStack(i).isEmpty()) {
					return false;
				}
			}
		} else {
			int j = inventory.getInvSize();

			for (int k = 0; k < j; k++) {
				if (!inventory.getInvStack(k).isEmpty()) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean tryExtract(Hopper hopper) {
		Inventory inventory = getInputInventory(hopper);
		if (inventory != null) {
			Direction direction = Direction.DOWN;
			if (method_11257(inventory, direction)) {
				return false;
			}

			if (inventory instanceof SidedInventory) {
				SidedInventory sidedInventory = (SidedInventory)inventory;
				int[] is = sidedInventory.method_5494(direction);

				for (int i : is) {
					if (method_11261(hopper, inventory, i, direction)) {
						return true;
					}
				}
			} else {
				int j = inventory.getInvSize();

				for (int k = 0; k < j; k++) {
					if (method_11261(hopper, inventory, k, direction)) {
						return true;
					}
				}
			}
		} else {
			for (ItemEntity itemEntity : method_11237(hopper)) {
				if (method_11247(hopper, itemEntity)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean method_11261(Hopper hopper, Inventory inventory, int i, Direction direction) {
		ItemStack itemStack = inventory.getInvStack(i);
		if (!itemStack.isEmpty() && method_11252(inventory, itemStack, i, direction)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = method_11260(inventory, hopper, inventory.takeInvStack(i, 1), null);
			if (itemStack3.isEmpty()) {
				inventory.markDirty();
				return true;
			}

			inventory.setInvStack(i, itemStack2);
		}

		return false;
	}

	public static boolean method_11247(Inventory inventory, ItemEntity itemEntity) {
		boolean bl = false;
		ItemStack itemStack = itemEntity.getStack().copy();
		ItemStack itemStack2 = method_11260(null, inventory, itemStack, null);
		if (itemStack2.isEmpty()) {
			bl = true;
			itemEntity.invalidate();
		} else {
			itemEntity.setStack(itemStack2);
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
		return !inventory.isValidInvStack(i, itemStack)
			? false
			: !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).method_5492(i, itemStack, direction);
	}

	private static boolean method_11252(Inventory inventory, ItemStack itemStack, int i, Direction direction) {
		return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).method_5493(i, itemStack, direction);
	}

	private static ItemStack method_11253(@Nullable Inventory inventory, Inventory inventory2, ItemStack itemStack, int i, @Nullable Direction direction) {
		ItemStack itemStack2 = inventory2.getInvStack(i);
		if (method_11244(inventory2, itemStack, i, direction)) {
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
					if (!hopperBlockEntity.method_11242()) {
						int k = 0;
						if (inventory instanceof HopperBlockEntity) {
							HopperBlockEntity hopperBlockEntity2 = (HopperBlockEntity)inventory;
							if (hopperBlockEntity.field_12022 >= hopperBlockEntity2.field_12022) {
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
	private Inventory method_11255() {
		Direction direction = this.getCachedState().get(HopperBlock.field_11129);
		return method_11250(this.getWorld(), this.pos.method_10093(direction));
	}

	@Nullable
	public static Inventory getInputInventory(Hopper hopper) {
		return getInventory(hopper.getWorld(), hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ());
	}

	public static List<ItemEntity> method_11237(Hopper hopper) {
		return (List<ItemEntity>)hopper.getInputAreaShape()
			.getBoundingBoxList()
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
	public static Inventory method_11250(World world, BlockPos blockPos) {
		return getInventory(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
	}

	@Nullable
	public static Inventory getInventory(World world, double d, double e, double f) {
		Inventory inventory = null;
		BlockPos blockPos = new BlockPos(d, e, f);
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof Inventory) {
				inventory = (Inventory)blockEntity;
				if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
					inventory = ((ChestBlock)block).getContainer(blockState, world, blockPos, true);
				}
			}
		}

		if (inventory == null) {
			List<Entity> list = world.getEntities(null, new BoundingBox(d - 0.5, e - 0.5, f - 0.5, d + 0.5, e + 0.5, f + 0.5), EntityPredicates.VALID_INVENTORIES);
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

	private boolean isCooldown() {
		return this.transferCooldown > 0;
	}

	private boolean method_11242() {
		return this.transferCooldown > 8;
	}

	@Override
	public String getContainerId() {
		return "minecraft:hopper";
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		this.checkLootInteraction(playerEntity);
		return new HopperContainer(playerInventory, this, playerEntity);
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void method_11281(DefaultedList<ItemStack> defaultedList) {
		this.inventory = defaultedList;
	}

	public void method_11236(Entity entity) {
		if (entity instanceof ItemEntity) {
			BlockPos blockPos = this.getPos();
			if (VoxelShapes.compareShapes(
				VoxelShapes.cube(entity.getBoundingBox().offset((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()))),
				this.getInputAreaShape(),
				BooleanBiFunction.AND
			)) {
				this.method_11243(() -> method_11247(this, (ItemEntity)entity));
			}
		}
	}
}
