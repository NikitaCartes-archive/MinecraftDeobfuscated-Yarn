package net.minecraft.block.dispenser;

import net.minecraft.class_7320;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class ItemDispenserBehavior implements DispenserBehavior {
	@Override
	public final ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
		ItemStack itemStack2 = this.dispenseSilently(blockPointer, itemStack);
		this.playSound(blockPointer);
		this.spawnParticles(blockPointer, blockPointer.getBlockState().get(DispenserBlock.FACING));
		return itemStack2;
	}

	protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
		Position position = DispenserBlock.getOutputLocation(pointer);
		ItemStack itemStack = stack.split(1);
		spawnItem(pointer.getWorld(), itemStack, 6, direction, position);
		return stack;
	}

	public static void spawnItem(World world, ItemStack stack, int speed, Direction side, Position pos) {
		double d = pos.getX();
		double e = pos.getY();
		double f = pos.getZ();
		if (side.getAxis() == Direction.Axis.Y) {
			e -= 0.125;
		} else {
			e -= 0.15625;
		}

		class_7320.method_42860(world, d, e, f, stack)
			.ifPresent(
				fallingBlockEntity -> {
					double dx = world.random.nextDouble() * 0.1 + 0.3;
					fallingBlockEntity.setVelocity(
						world.random.nextGaussian() * 0.0075F * (double)speed + (double)side.getOffsetX() * dx,
						world.random.nextGaussian() * 0.0075F * (double)speed + 0.3F,
						world.random.nextGaussian() * 0.0075F * (double)speed + (double)side.getOffsetZ() * dx
					);
					world.spawnEntity(fallingBlockEntity);
				}
			);
	}

	protected void playSound(BlockPointer pointer) {
		pointer.getWorld().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.getPos(), 0);
	}

	protected void spawnParticles(BlockPointer pointer, Direction side) {
		pointer.getWorld().syncWorldEvent(WorldEvents.DISPENSER_ACTIVATED, pointer.getPos(), side.getId());
	}
}
