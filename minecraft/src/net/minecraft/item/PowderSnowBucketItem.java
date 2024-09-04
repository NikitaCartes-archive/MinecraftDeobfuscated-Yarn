package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PowderSnowBucketItem extends BlockItem implements FluidModificationItem {
	private final SoundEvent placeSound;

	public PowderSnowBucketItem(Block block, SoundEvent placeSound, Item.Settings settings) {
		super(block, settings);
		this.placeSound = placeSound;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		ActionResult actionResult = super.useOnBlock(context);
		PlayerEntity playerEntity = context.getPlayer();
		if (actionResult.isAccepted() && playerEntity != null) {
			playerEntity.setStackInHand(context.getHand(), BucketItem.getEmptiedStack(context.getStack(), playerEntity));
		}

		return actionResult;
	}

	@Override
	protected SoundEvent getPlaceSound(BlockState state) {
		return this.placeSound;
	}

	@Override
	public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
		if (world.isInBuildLimit(pos) && world.isAir(pos)) {
			if (!world.isClient) {
				world.setBlockState(pos, this.getBlock().getDefaultState(), Block.NOTIFY_ALL);
			}

			world.emitGameEvent(player, GameEvent.FLUID_PLACE, pos);
			world.playSound(player, pos, this.placeSound, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return true;
		} else {
			return false;
		}
	}
}
