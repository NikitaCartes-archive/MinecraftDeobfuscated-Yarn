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
	private static final DispenserBehavior field_10949 = new ItemDispenserBehavior();

	public DropperBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	protected DispenserBehavior getBehaviorForItem(ItemStack itemStack) {
		return field_10949;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new DropperBlockEntity();
	}

	@Override
	protected void method_10012(World world, BlockPos blockPos) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, blockPos);
		DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
		int i = dispenserBlockEntity.method_11076();
		if (i < 0) {
			world.fireWorldEvent(1001, blockPos, 0);
		} else {
			ItemStack itemStack = dispenserBlockEntity.getInvStack(i);
			if (!itemStack.isEmpty()) {
				Direction direction = world.getBlockState(blockPos).get(field_10918);
				Inventory inventory = HopperBlockEntity.getInventoryAt(world, blockPos.offset(direction));
				ItemStack itemStack2;
				if (inventory == null) {
					itemStack2 = field_10949.dispense(blockPointerImpl, itemStack);
				} else {
					itemStack2 = HopperBlockEntity.method_11260(dispenserBlockEntity, inventory, itemStack.copy().split(1), direction.getOpposite());
					if (itemStack2.isEmpty()) {
						itemStack2 = itemStack.copy();
						itemStack2.subtractAmount(1);
					} else {
						itemStack2 = itemStack.copy();
					}
				}

				dispenserBlockEntity.setInvStack(i, itemStack2);
			}
		}
	}
}
