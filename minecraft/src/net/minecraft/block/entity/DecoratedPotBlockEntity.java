package net.minecraft.block.entity;

import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DecoratedPotBlockEntity extends BlockEntity {
	public static final String SHERDS_NBT_KEY = "sherds";
	private static final int SHERD_COUNT = 4;
	private final List<Item> sherds = Util.make(new ArrayList(4), sherds -> {
		sherds.add(Items.BRICK);
		sherds.add(Items.BRICK);
		sherds.add(Items.BRICK);
		sherds.add(Items.BRICK);
	});

	public DecoratedPotBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.DECORATED_POT, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		writeSherdsToNbt(this.sherds, nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("sherds", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("sherds", NbtElement.STRING_TYPE);
			this.sherds.clear();
			int i = Math.min(4, nbtList.size());

			for (int j = 0; j < i; j++) {
				if (nbtList.get(j) instanceof NbtString nbtString) {
					this.sherds.add(Registries.ITEM.get(new Identifier(nbtString.asString())));
				} else {
					this.sherds.add(Items.BRICK);
				}
			}

			int jx = 4 - i;

			for (int k = 0; k < jx; k++) {
				this.sherds.add(Items.BRICK);
			}
		}
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbt();
	}

	public static void writeSherdsToNbt(List<Item> sherds, NbtCompound nbt) {
		NbtList nbtList = new NbtList();

		for (Item item : sherds) {
			nbtList.add(NbtString.of(Registries.ITEM.getId(item).toString()));
		}

		nbt.put("sherds", nbtList);
	}

	public List<Item> getSherds() {
		return this.sherds;
	}

	public Direction getHorizontalFacing() {
		return this.getCachedState().get(Properties.HORIZONTAL_FACING);
	}

	public void readNbtFromStack(ItemStack stack) {
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
		if (nbtCompound != null) {
			this.readNbt(nbtCompound);
		} else {
			this.sherds.clear();

			for (int i = 0; i < 4; i++) {
				this.sherds.add(Items.BRICK);
			}
		}
	}
}
