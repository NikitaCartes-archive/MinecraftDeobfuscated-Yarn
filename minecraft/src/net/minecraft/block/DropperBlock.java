package net.minecraft.block;

import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldEvents;

public class DropperBlock extends DispenserBlock {
	private static final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();

	public DropperBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
		return BEHAVIOR;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new DropperBlockEntity(pos, state);
	}

	@Override
	protected void dispense(ServerWorld world, BlockPos pos) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
		DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
		int i = dispenserBlockEntity.chooseNonEmptySlot();
		if (i < 0) {
			world.syncWorldEvent(WorldEvents.DISPENSER_FAILS, pos, 0);
		} else {
			ItemStack itemStack = dispenserBlockEntity.getStack(i);
			if (!itemStack.isEmpty()) {
				Direction direction = world.getBlockState(pos).get(FACING);
				Inventory inventory = HopperBlockEntity.getInventoryAt(world, pos.offset(direction));
				ItemStack itemStack2;
				if (inventory == null) {
					itemStack2 = BEHAVIOR.dispense(blockPointerImpl, itemStack);
				} else {
					itemStack2 = HopperBlockEntity.transfer(dispenserBlockEntity, inventory, itemStack.copy().split(1), direction.getOpposite());
					if (itemStack2.isEmpty()) {
						itemStack2 = itemStack.copy();
						itemStack2.decrement(1);
					} else {
						itemStack2 = itemStack.copy();
					}
				}

				dispenserBlockEntity.setStack(i, itemStack2);
			}
		}
	}
}
