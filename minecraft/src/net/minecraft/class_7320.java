package net.minecraft;

import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.world.World;

public class class_7320 {
	public static Optional<FallingBlockEntity> method_42860(World world, double d, double e, double f, ItemStack itemStack) {
		return method_42858(itemStack).map(blockState -> new FallingBlockEntity(world, d, e, f, blockState));
	}

	public static BlockState method_42862(BlockState blockState) {
		BlockState blockState2 = blockState;

		while (true) {
			BlockState blockState3 = class_7323.method_42880(blockState2);
			if (blockState3 == null || blockState3 == blockState2) {
				return blockState2;
			}

			blockState2 = blockState3;
		}
	}

	public static ItemStack method_42867(BlockState blockState) {
		if (blockState.isAir()) {
			return ItemStack.EMPTY;
		} else {
			Item item = class_7323.method_42881(blockState);
			if (item != null) {
				return item.getDefaultStack();
			} else {
				Block block = blockState.getBlock();
				ItemStack itemStack = block.asItem().getDefaultStack();
				itemStack.setSubNbt("BlockStateTag", method_42868(blockState));
				return itemStack;
			}
		}
	}

	public static Optional<BlockState> method_42858(ItemStack itemStack) {
		return itemStack.isEmpty() ? Optional.empty() : method_42857(itemStack.getItem()).map(blockState -> method_42859(itemStack, blockState));
	}

	private static Optional<BlockState> method_42857(Item item) {
		if (item == Items.AIR) {
			return Optional.empty();
		} else {
			return item instanceof BlockItem blockItem ? Optional.of(blockItem.getBlock().getDefaultState()) : Optional.ofNullable(class_7323.method_42878(item));
		}
	}

	private static NbtCompound method_42868(BlockState blockState) {
		NbtCompound nbtCompound = new NbtCompound();

		for (Property<?> property : blockState.getProperties()) {
			nbtCompound.putString(property.getName(), method_42863(blockState, property));
		}

		return nbtCompound;
	}

	private static <T extends Comparable<T>> String method_42863(BlockState blockState, Property<T> property) {
		T comparable = blockState.get(property);
		return property.name(comparable);
	}

	private static BlockState method_42859(ItemStack itemStack, BlockState blockState) {
		NbtCompound nbtCompound = itemStack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("BlockStateTag")) {
			BlockState blockState2 = blockState;
			NbtCompound nbtCompound2 = nbtCompound.getCompound("BlockStateTag");
			StateManager<Block, BlockState> stateManager = blockState.getBlock().getStateManager();

			for (String string : nbtCompound2.getKeys()) {
				Property<?> property = stateManager.getProperty(string);
				if (property != null) {
					String string2 = nbtCompound2.get(string).asString();

					try {
						blockState2 = method_42865(blockState2, property, string2);
					} catch (Throwable var11) {
						var11.printStackTrace();
					}
				}
			}

			return blockState2;
		} else {
			return blockState;
		}
	}

	private static <T extends Comparable<T>> BlockState method_42865(BlockState blockState, Property<T> property, String string) {
		return (BlockState)property.parse(string).map(comparable -> blockState.with(property, comparable)).orElse(blockState);
	}
}
