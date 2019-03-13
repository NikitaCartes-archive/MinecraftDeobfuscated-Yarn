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
		ItemStack itemStack2 = this.dispenseStack(blockPointer, itemStack);
		this.playSound(blockPointer);
		this.spawnParticles(blockPointer, blockPointer.getBlockState().method_11654(DispenserBlock.field_10918));
		return itemStack2;
	}

	protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
		Direction direction = blockPointer.getBlockState().method_11654(DispenserBlock.field_10918);
		Position position = DispenserBlock.method_10010(blockPointer);
		ItemStack itemStack2 = itemStack.split(1);
		dispenseItem(blockPointer.getWorld(), itemStack2, 6, direction, position);
		return itemStack;
	}

	public static void dispenseItem(World world, ItemStack itemStack, int i, Direction direction, Position position) {
		double d = position.getX();
		double e = position.getY();
		double f = position.getZ();
		if (direction.getAxis() == Direction.Axis.Y) {
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
		blockPointer.getWorld().method_8535(1000, blockPointer.getBlockPos(), 0);
	}

	protected void spawnParticles(BlockPointer blockPointer, Direction direction) {
		blockPointer.getWorld().method_8535(2000, blockPointer.getBlockPos(), direction.getId());
	}
}
