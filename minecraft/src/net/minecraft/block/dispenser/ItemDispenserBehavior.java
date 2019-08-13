package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class ItemDispenserBehavior implements DispenserBehavior {
	@Override
	public final ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
		ItemStack itemStack2 = this.dispenseSilently(blockPointer, itemStack);
		this.playSound(blockPointer);
		this.spawnParticles(blockPointer, blockPointer.getBlockState().get(DispenserBlock.FACING));
		return itemStack2;
	}

	protected ItemStack dispenseSilently(BlockPointer blockPointer, ItemStack itemStack) {
		Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
		Position position = DispenserBlock.getOutputLocation(blockPointer);
		ItemStack itemStack2 = itemStack.split(1);
		spawnItem(blockPointer.getWorld(), itemStack2, 6, direction, position);
		return itemStack;
	}

	public static void spawnItem(World world, ItemStack itemStack, int i, Direction direction, Position position) {
		double d = position.getX();
		double e = position.getY();
		double f = position.getZ();
		if (direction.getAxis() == Direction.Axis.field_11052) {
			e -= 0.125;
		} else {
			e -= 0.15625;
		}

		ItemEntity itemEntity = new ItemEntity(world, d, e, f, itemStack);
		double g = world.random.nextDouble() * 0.1 + 0.2;
		itemEntity.setVelocity(
			world.random.nextGaussian() * 0.0075F * (double)i + (double)direction.getOffsetX() * g,
			world.random.nextGaussian() * 0.0075F * (double)i + 0.2F,
			world.random.nextGaussian() * 0.0075F * (double)i + (double)direction.getOffsetZ() * g
		);
		world.spawnEntity(itemEntity);
	}

	protected void playSound(BlockPointer blockPointer) {
		blockPointer.getWorld().playLevelEvent(1000, blockPointer.getBlockPos(), 0);
	}

	protected void spawnParticles(BlockPointer blockPointer, Direction direction) {
		blockPointer.getWorld().playLevelEvent(2000, blockPointer.getBlockPos(), direction.getId());
	}
}
