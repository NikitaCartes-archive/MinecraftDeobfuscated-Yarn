package net.minecraft.block.entity;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DecoratedPotBlockEntity extends BlockEntity implements SingleStackInventory {
	public static final String SHERDS_NBT_KEY = "sherds";
	public static final String ITEM_NBT_KEY = "item";
	public static final int field_46660 = 1;
	public long lastWobbleTime;
	@Nullable
	public DecoratedPotBlockEntity.WobbleType lastWobbleType;
	private DecoratedPotBlockEntity.Sherds sherds;
	private ItemStack stack = ItemStack.EMPTY;

	public DecoratedPotBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.DECORATED_POT, pos, state);
		this.sherds = DecoratedPotBlockEntity.Sherds.DEFAULT;
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		this.sherds.toNbt(nbt);
		if (!this.stack.isEmpty()) {
			nbt.put("item", this.stack.writeNbt(new NbtCompound()));
		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.sherds = DecoratedPotBlockEntity.Sherds.fromNbt(nbt);
		if (nbt.contains("item", NbtElement.COMPOUND_TYPE)) {
			this.stack = ItemStack.fromNbt(nbt.getCompound("item"));
		} else {
			this.stack = ItemStack.EMPTY;
		}
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbt();
	}

	public Direction getHorizontalFacing() {
		return this.getCachedState().get(Properties.HORIZONTAL_FACING);
	}

	public DecoratedPotBlockEntity.Sherds getSherds() {
		return this.sherds;
	}

	public void readNbtFromStack(ItemStack stack) {
		this.sherds = DecoratedPotBlockEntity.Sherds.fromNbt(BlockItem.getBlockEntityNbt(stack));
	}

	public ItemStack asStack() {
		return getStackWith(this.sherds);
	}

	public static ItemStack getStackWith(DecoratedPotBlockEntity.Sherds sherds) {
		ItemStack itemStack = Items.DECORATED_POT.getDefaultStack();
		NbtCompound nbtCompound = sherds.toNbt(new NbtCompound());
		BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.DECORATED_POT, nbtCompound);
		return itemStack;
	}

	@Override
	public ItemStack getStack() {
		return this.stack;
	}

	@Override
	public ItemStack decreaseStack(int count) {
		ItemStack itemStack = this.stack.split(count);
		if (this.stack.isEmpty()) {
			this.stack = ItemStack.EMPTY;
		}

		return itemStack;
	}

	@Override
	public void setStack(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public BlockEntity asBlockEntity() {
		return this;
	}

	public void wobble(DecoratedPotBlockEntity.WobbleType wobbleType) {
		if (this.world != null && !this.world.isClient()) {
			this.world.addSyncedBlockEvent(this.getPos(), this.getCachedState().getBlock(), 1, wobbleType.ordinal());
		}
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (this.world != null && type == 1 && data >= 0 && data < DecoratedPotBlockEntity.WobbleType.values().length) {
			this.lastWobbleTime = this.world.getTime();
			this.lastWobbleType = DecoratedPotBlockEntity.WobbleType.values()[data];
			return true;
		} else {
			return super.onSyncedBlockEvent(type, data);
		}
	}

	public static record Sherds(Item back, Item left, Item right, Item front) {
		public static final DecoratedPotBlockEntity.Sherds DEFAULT = new DecoratedPotBlockEntity.Sherds(Items.BRICK, Items.BRICK, Items.BRICK, Items.BRICK);

		public NbtCompound toNbt(NbtCompound nbt) {
			if (this.equals(DEFAULT)) {
				return nbt;
			} else {
				NbtList nbtList = new NbtList();
				this.stream().forEach(sherd -> nbtList.add(NbtString.of(Registries.ITEM.getId(sherd).toString())));
				nbt.put("sherds", nbtList);
				return nbt;
			}
		}

		public Stream<Item> stream() {
			return Stream.of(this.back, this.left, this.right, this.front);
		}

		public static DecoratedPotBlockEntity.Sherds fromNbt(@Nullable NbtCompound nbt) {
			if (nbt != null && nbt.contains("sherds", NbtElement.LIST_TYPE)) {
				NbtList nbtList = nbt.getList("sherds", NbtElement.STRING_TYPE);
				return new DecoratedPotBlockEntity.Sherds(getSherd(nbtList, 0), getSherd(nbtList, 1), getSherd(nbtList, 2), getSherd(nbtList, 3));
			} else {
				return DEFAULT;
			}
		}

		private static Item getSherd(NbtList list, int index) {
			if (index >= list.size()) {
				return Items.BRICK;
			} else {
				NbtElement nbtElement = list.get(index);
				return Registries.ITEM.get(Identifier.tryParse(nbtElement.asString()));
			}
		}
	}

	public static enum WobbleType {
		POSITIVE(7),
		NEGATIVE(10);

		public final int lengthInTicks;

		private WobbleType(int lengthInTicks) {
			this.lengthInTicks = lengthInTicks;
		}
	}
}
