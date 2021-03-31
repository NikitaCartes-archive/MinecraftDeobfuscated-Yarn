package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class LeadItem extends Item {
	public LeadItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.isIn(BlockTags.FENCES)) {
			PlayerEntity playerEntity = context.getPlayer();
			if (!world.isClient && playerEntity != null) {
				attachHeldMobsToBlock(playerEntity, world, blockPos);
			}

			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.PASS;
		}
	}

	public static ActionResult attachHeldMobsToBlock(PlayerEntity player, World world, BlockPos pos) {
		LeashKnotEntity leashKnotEntity = null;
		boolean bl = false;
		double d = 7.0;
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		for (MobEntity mobEntity : world.getNonSpectatingEntities(
			MobEntity.class, new Box((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0)
		)) {
			if (mobEntity.getHoldingEntity() == player) {
				if (leashKnotEntity == null) {
					leashKnotEntity = LeashKnotEntity.getOrCreate(world, pos);
					leashKnotEntity.onPlace();
				}

				mobEntity.attachLeash(leashKnotEntity, true);
				bl = true;
			}
		}

		return bl ? ActionResult.SUCCESS : ActionResult.PASS;
	}
}
