package net.minecraft.item;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MinecartItem extends Item {
	private static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
		private final ItemDispenserBehavior defaultBehavior = new ItemDispenserBehavior();

		@Override
		public ItemStack dispenseSilently(BlockPointer blockPointer, ItemStack itemStack) {
			Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
			World world = blockPointer.getWorld();
			double d = blockPointer.getX() + (double)direction.getOffsetX() * 1.125;
			double e = Math.floor(blockPointer.getY()) + (double)direction.getOffsetY();
			double f = blockPointer.getZ() + (double)direction.getOffsetZ() * 1.125;
			BlockPos blockPos = blockPointer.getBlockPos().offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock
				? blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty())
				: RailShape.field_12665;
			double g;
			if (blockState.matches(BlockTags.field_15463)) {
				if (railShape.isAscending()) {
					g = 0.6;
				} else {
					g = 0.1;
				}
			} else {
				if (!blockState.isAir() || !world.getBlockState(blockPos.down()).matches(BlockTags.field_15463)) {
					return this.defaultBehavior.dispense(blockPointer, itemStack);
				}

				BlockState blockState2 = world.getBlockState(blockPos.down());
				RailShape railShape2 = blockState2.getBlock() instanceof AbstractRailBlock
					? blockState2.get(((AbstractRailBlock)blockState2.getBlock()).getShapeProperty())
					: RailShape.field_12665;
				if (direction != Direction.field_11033 && railShape2.isAscending()) {
					g = -0.4;
				} else {
					g = -0.9;
				}
			}

			AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, d, e + g, f, ((MinecartItem)itemStack.getItem()).type);
			if (itemStack.hasCustomName()) {
				abstractMinecartEntity.setCustomName(itemStack.getName());
			}

			world.spawnEntity(abstractMinecartEntity);
			itemStack.decrement(1);
			return itemStack;
		}

		@Override
		protected void playSound(BlockPointer blockPointer) {
			blockPointer.getWorld().playLevelEvent(1000, blockPointer.getBlockPos(), 0);
		}
	};
	private final AbstractMinecartEntity.Type type;

	public MinecartItem(AbstractMinecartEntity.Type type, Item.Settings settings) {
		super(settings);
		this.type = type;
		DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (!blockState.matches(BlockTags.field_15463)) {
			return ActionResult.field_5814;
		} else {
			ItemStack itemStack = itemUsageContext.getStack();
			if (!world.isClient) {
				RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock
					? blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty())
					: RailShape.field_12665;
				double d = 0.0;
				if (railShape.isAscending()) {
					d = 0.5;
				}

				AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(
					world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.0625 + d, (double)blockPos.getZ() + 0.5, this.type
				);
				if (itemStack.hasCustomName()) {
					abstractMinecartEntity.setCustomName(itemStack.getName());
				}

				world.spawnEntity(abstractMinecartEntity);
			}

			itemStack.decrement(1);
			return ActionResult.field_5812;
		}
	}
}
