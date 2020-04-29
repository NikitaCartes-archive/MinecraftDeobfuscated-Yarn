package net.minecraft.screen;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmithingScreenHandler extends ForgingScreenHandler {
	private static final Map<Item, Item> RECIPES = ImmutableMap.<Item, Item>builder()
		.put(Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE)
		.put(Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS)
		.put(Items.DIAMOND_HELMET, Items.NETHERITE_HELMET)
		.put(Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS)
		.put(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD)
		.put(Items.DIAMOND_AXE, Items.NETHERITE_AXE)
		.put(Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE)
		.put(Items.DIAMOND_HOE, Items.NETHERITE_HOE)
		.put(Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL)
		.build();

	public SmithingScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public SmithingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.SMITHING, syncId, playerInventory, context);
	}

	@Override
	protected boolean canUse(BlockState state) {
		return state.isOf(Blocks.SMITHING_TABLE);
	}

	@Override
	protected boolean canTakeOutput(PlayerEntity player, boolean present) {
		return RECIPES.containsKey(this.input.getStack(0).getItem()) && this.input.getStack(1).getItem() == Items.NETHERITE_INGOT;
	}

	@Override
	protected ItemStack onTakeOutput(PlayerEntity player, ItemStack stack) {
		this.input.setStack(0, ItemStack.EMPTY);
		ItemStack itemStack = this.input.getStack(1);
		itemStack.decrement(1);
		this.input.setStack(1, itemStack);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> world.syncWorldEvent(1044, blockPos, 0)));
		return stack;
	}

	@Override
	public void updateResult() {
		ItemStack itemStack = this.input.getStack(0);
		ItemStack itemStack2 = this.input.getStack(1);
		Item item = (Item)RECIPES.get(itemStack.getItem());
		if (itemStack2.getItem() == Items.NETHERITE_INGOT && item != null) {
			ItemStack itemStack3 = new ItemStack(item);
			CompoundTag compoundTag = itemStack.getTag();
			itemStack3.setTag(compoundTag != null ? compoundTag.copy() : null);
			this.output.setStack(0, itemStack3);
		} else {
			this.output.setStack(0, ItemStack.EMPTY);
		}
	}
}
