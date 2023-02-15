package net.minecraft.block.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DecoratedPotBlockEntity extends BlockEntity {
	private static final String SHARDS_NBT_KEY = "shards";
	private static final int field_42783 = 4;
	private boolean dropNothing = false;
	private final List<Item> shards = Util.make(new ArrayList(4), arrayList -> {
		arrayList.add(Items.BRICK);
		arrayList.add(Items.BRICK);
		arrayList.add(Items.BRICK);
		arrayList.add(Items.BRICK);
	});

	public DecoratedPotBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.DECORATED_POT, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		writeShardsToNbt(this.shards, nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("shards", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("shards", NbtElement.STRING_TYPE);
			this.shards.clear();
			int i = Math.min(4, nbtList.size());

			for (int j = 0; j < i; j++) {
				if (nbtList.get(j) instanceof NbtString nbtString) {
					this.shards.add(Registries.ITEM.get(new Identifier(nbtString.asString())));
				} else {
					this.shards.add(Items.BRICK);
				}
			}

			int jx = 4 - i;

			for (int k = 0; k < jx; k++) {
				this.shards.add(Items.BRICK);
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

	public static void writeShardsToNbt(List<Item> shards, NbtCompound nbt) {
		NbtList nbtList = new NbtList();

		for (Item item : shards) {
			nbtList.add(NbtString.of(Registries.ITEM.getId(item).toString()));
		}

		nbt.put("shards", nbtList);
	}

	public ItemStack asStack() {
		ItemStack itemStack = new ItemStack(Blocks.DECORATED_POT);
		NbtCompound nbtCompound = new NbtCompound();
		writeShardsToNbt(this.shards, nbtCompound);
		BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.DECORATED_POT, nbtCompound);
		return itemStack;
	}

	public List<Item> getShards() {
		return this.shards;
	}

	public void onBreak(World world, BlockPos pos, ItemStack tool, PlayerEntity player) {
		if (player.isCreative()) {
			this.dropNothing = true;
		} else {
			if (tool.isIn(ItemTags.BREAKS_DECORATED_POTS) && !EnchantmentHelper.hasSilkTouch(tool)) {
				List<Item> list = this.getShards();
				DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(list.size());
				defaultedList.addAll(0, list.stream().map(Item::getDefaultStack).toList());
				ItemScatterer.spawn(world, pos, defaultedList);
				this.dropNothing = true;
				world.playSound(null, pos, SoundEvents.BLOCK_DECORATED_POT_SHATTER, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}
	}

	public boolean shouldDropNothing() {
		return this.dropNothing;
	}

	public Direction getHorizontalFacing() {
		return this.getCachedState().get(Properties.HORIZONTAL_FACING);
	}

	public void readNbtFromStack(ItemStack stack) {
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
		if (nbtCompound != null) {
			this.readNbt(nbtCompound);
		}
	}
}
