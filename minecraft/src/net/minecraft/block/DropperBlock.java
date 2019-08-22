package net.minecraft.block;

import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DropperBlock extends DispenserBlock {
	private static final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();

	public DropperBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	protected DispenserBehavior getBehaviorForItem(ItemStack itemStack) {
		return BEHAVIOR;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new DropperBlockEntity();
	}

	@Override
	protected void dispense(World world, BlockPos blockPos) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, blockPos);
		DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
		int i = dispenserBlockEntity.chooseNonEmptySlot();
		if (i < 0) {
			world.playLevelEvent(1001, blockPos, 0);
		} else {
			ItemStack itemStack = dispenserBlockEntity.getInvStack(i);
			if (!itemStack.isEmpty()) {
				Direction direction = world.getBlockState(blockPos).get(FACING);
				Inventory inventory = HopperBlockEntity.getInventoryAt(world, blockPos.offset(direction));
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

				dispenserBlockEntity.setInvStack(i, itemStack2);
			}
		}
	}
}
