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
	private static final DispenserBehavior field_8896 = new ItemDispenserBehavior() {
		private final ItemDispenserBehavior field_8898 = new ItemDispenserBehavior();

		@Override
		public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
			Direction direction = blockPointer.getBlockState().method_11654(DispenserBlock.field_10918);
			World world = blockPointer.getWorld();
			double d = blockPointer.getX() + (double)direction.getOffsetX() * 1.125;
			double e = Math.floor(blockPointer.getY()) + (double)direction.getOffsetY();
			double f = blockPointer.getZ() + (double)direction.getOffsetZ() * 1.125;
			BlockPos blockPos = blockPointer.getBlockPos().method_10093(direction);
			BlockState blockState = world.method_8320(blockPos);
			RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock
				? blockState.method_11654(((AbstractRailBlock)blockState.getBlock()).method_9474())
				: RailShape.field_12665;
			double g;
			if (blockState.method_11602(BlockTags.field_15463)) {
				if (railShape.isAscending()) {
					g = 0.6;
				} else {
					g = 0.1;
				}
			} else {
				if (!blockState.isAir() || !world.method_8320(blockPos.down()).method_11602(BlockTags.field_15463)) {
					return this.field_8898.dispense(blockPointer, itemStack);
				}

				BlockState blockState2 = world.method_8320(blockPos.down());
				RailShape railShape2 = blockState2.getBlock() instanceof AbstractRailBlock
					? blockState2.method_11654(((AbstractRailBlock)blockState2.getBlock()).method_9474())
					: RailShape.field_12665;
				if (direction != Direction.DOWN && railShape2.isAscending()) {
					g = -0.4;
				} else {
					g = -0.9;
				}
			}

			AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.method_7523(world, d, e + g, f, ((MinecartItem)itemStack.getItem()).type);
			if (itemStack.hasDisplayName()) {
				abstractMinecartEntity.method_5665(itemStack.method_7964());
			}

			world.spawnEntity(abstractMinecartEntity);
			itemStack.subtractAmount(1);
			return itemStack;
		}

		@Override
		protected void playSound(BlockPointer blockPointer) {
			blockPointer.getWorld().method_8535(1000, blockPointer.getBlockPos(), 0);
		}
	};
	private final AbstractMinecartEntity.Type type;

	public MinecartItem(AbstractMinecartEntity.Type type, Item.Settings settings) {
		super(settings);
		this.type = type;
		DispenserBlock.method_10009(this, field_8896);
	}

	@Override
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.method_8037();
		BlockState blockState = world.method_8320(blockPos);
		if (!blockState.method_11602(BlockTags.field_15463)) {
			return ActionResult.field_5814;
		} else {
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (!world.isClient) {
				RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock
					? blockState.method_11654(((AbstractRailBlock)blockState.getBlock()).method_9474())
					: RailShape.field_12665;
				double d = 0.0;
				if (railShape.isAscending()) {
					d = 0.5;
				}

				AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.method_7523(
					world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.0625 + d, (double)blockPos.getZ() + 0.5, this.type
				);
				if (itemStack.hasDisplayName()) {
					abstractMinecartEntity.method_5665(itemStack.method_7964());
				}

				world.spawnEntity(abstractMinecartEntity);
			}

			itemStack.subtractAmount(1);
			return ActionResult.field_5812;
		}
	}
}
