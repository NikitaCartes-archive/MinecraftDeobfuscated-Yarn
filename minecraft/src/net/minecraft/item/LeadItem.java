package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.decoration.LeadKnotEntity;
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
		Block block = world.getBlockState(blockPos).getBlock();
		if (block.matches(BlockTags.FENCES)) {
			PlayerEntity playerEntity = context.getPlayer();
			if (!world.isClient && playerEntity != null) {
				attachHeldMobsToBlock(playerEntity, world, blockPos);
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	public static ActionResult attachHeldMobsToBlock(PlayerEntity playerEntity, World world, BlockPos blockPos) {
		LeadKnotEntity leadKnotEntity = null;
		boolean bl = false;
		double d = 7.0;
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();

		for (MobEntity mobEntity : world.getNonSpectatingEntities(
			MobEntity.class, new Box((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0)
		)) {
			if (mobEntity.getHoldingEntity() == playerEntity) {
				if (leadKnotEntity == null) {
					leadKnotEntity = LeadKnotEntity.getOrCreate(world, blockPos);
				}

				mobEntity.attachLeash(leadKnotEntity, true);
				bl = true;
			}
		}

		return bl ? ActionResult.SUCCESS : ActionResult.PASS;
	}
}
