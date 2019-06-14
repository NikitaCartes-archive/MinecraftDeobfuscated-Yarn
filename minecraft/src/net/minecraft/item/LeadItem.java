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
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		Block block = world.method_8320(blockPos).getBlock();
		if (block.matches(BlockTags.field_16584)) {
			PlayerEntity playerEntity = itemUsageContext.getPlayer();
			if (!world.isClient && playerEntity != null) {
				method_7994(playerEntity, world, blockPos);
			}

			return ActionResult.field_5812;
		} else {
			return ActionResult.field_5811;
		}
	}

	public static boolean method_7994(PlayerEntity playerEntity, World world, BlockPos blockPos) {
		LeadKnotEntity leadKnotEntity = null;
		boolean bl = false;
		double d = 7.0;
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();

		for (MobEntity mobEntity : world.method_18467(
			MobEntity.class, new Box((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0)
		)) {
			if (mobEntity.getHoldingEntity() == playerEntity) {
				if (leadKnotEntity == null) {
					leadKnotEntity = LeadKnotEntity.method_6932(world, blockPos);
				}

				mobEntity.attachLeash(leadKnotEntity, true);
				bl = true;
			}
		}

		return bl;
	}
}
