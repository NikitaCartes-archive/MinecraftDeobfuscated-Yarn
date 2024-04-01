package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PotatoPeelsItem extends Item {
	public static final DyeColor PEELGRASS_COLOR = DyeColor.LIME;
	private final DyeColor color;

	public PotatoPeelsItem(Item.Settings settings, DyeColor color) {
		super(settings);
		this.color = color;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (this.color == PEELGRASS_COLOR) {
			World world = context.getWorld();
			BlockPos blockPos = context.getBlockPos();
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.isOf(Blocks.TERRE_DE_POMME) && context.getSide() == Direction.UP) {
				PlayerEntity playerEntity = context.getPlayer();
				context.getStack().decrement(1);
				world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_PEELGRASS_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, world.random.nextBetweenInclusive(0.9F, 1.1F));
				world.setBlockState(blockPos, Blocks.PEELGRASS_BLOCK.getDefaultState());
				return ActionResult.success(world.isClient);
			}
		}

		return super.useOnBlock(context);
	}
}
