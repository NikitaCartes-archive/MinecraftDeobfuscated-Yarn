package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

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
		double d = 7.0;
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		Box box = new Box((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0);
		List<Entity> list = world.getEntitiesByClass(Entity.class, box, entityx -> {
			if (entityx instanceof Leashable leashable && leashable.getLeashHolder() == player) {
				return true;
			}

			return false;
		});

		for (Entity entity : list) {
			if (leashKnotEntity == null) {
				leashKnotEntity = LeashKnotEntity.getOrCreate(world, pos);
				leashKnotEntity.onPlace();
			}

			((Leashable)entity).attachLeash(leashKnotEntity, true);
		}

		if (!list.isEmpty()) {
			world.emitGameEvent(GameEvent.BLOCK_ATTACH, pos, GameEvent.Emitter.of(player));
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}
}
