package net.minecraft.block.entity;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
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

public class DecoratedPotBlockEntity extends BlockEntity {
	public static final String SHERDS_NBT_KEY = "sherds";
	private DecoratedPotBlockEntity.Sherds sherds = DecoratedPotBlockEntity.Sherds.DEFAULT;

	public DecoratedPotBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.DECORATED_POT, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		this.sherds.toNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.sherds = DecoratedPotBlockEntity.Sherds.fromNbt(nbt);
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
}
