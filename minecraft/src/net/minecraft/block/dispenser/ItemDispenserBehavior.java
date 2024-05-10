package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class ItemDispenserBehavior implements DispenserBehavior {
	private static final int field_51916 = 6;

	@Override
	public final ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
		ItemStack itemStack2 = this.dispenseSilently(blockPointer, itemStack);
		this.playSound(blockPointer);
		this.spawnParticles(blockPointer, blockPointer.state().get(DispenserBlock.FACING));
		return itemStack2;
	}

	protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		Direction direction = pointer.state().get(DispenserBlock.FACING);
		Position position = DispenserBlock.getOutputLocation(pointer);
		ItemStack itemStack = stack.split(1);
		spawnItem(pointer.world(), itemStack, 6, direction, position);
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

		ItemEntity itemEntity = new ItemEntity(world, d, e, f, stack);
		double g = world.random.nextDouble() * 0.1 + 0.2;
		itemEntity.setVelocity(
			world.random.nextTriangular((double)side.getOffsetX() * g, 0.0172275 * (double)speed),
			world.random.nextTriangular(0.2, 0.0172275 * (double)speed),
			world.random.nextTriangular((double)side.getOffsetZ() * g, 0.0172275 * (double)speed)
		);
		world.spawnEntity(itemEntity);
	}

	protected void playSound(BlockPointer pointer) {
		method_60578(pointer);
	}

	protected void spawnParticles(BlockPointer pointer, Direction side) {
		method_60580(pointer, side);
	}

	private static void method_60578(BlockPointer blockPointer) {
		blockPointer.world().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, blockPointer.pos(), 0);
	}

	private static void method_60580(BlockPointer blockPointer, Direction direction) {
		blockPointer.world().syncWorldEvent(WorldEvents.DISPENSER_ACTIVATED, blockPointer.pos(), direction.getId());
	}

	protected ItemStack method_60577(BlockPointer blockPointer, ItemStack itemStack, ItemStack itemStack2) {
		itemStack.decrement(1);
		if (itemStack.isEmpty()) {
			return itemStack2;
		} else {
			this.method_60579(blockPointer, itemStack2);
			return itemStack;
		}
	}

	private void method_60579(BlockPointer blockPointer, ItemStack itemStack) {
		ItemStack itemStack2 = blockPointer.blockEntity().addToFirstFreeSlot(itemStack);
		if (!itemStack2.isEmpty()) {
			Direction direction = blockPointer.state().get(DispenserBlock.FACING);
			spawnItem(blockPointer.world(), itemStack2, 6, direction, DispenserBlock.getOutputLocation(blockPointer));
			method_60578(blockPointer);
			method_60580(blockPointer, direction);
		}
	}
}
