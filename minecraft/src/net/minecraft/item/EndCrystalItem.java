package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class EndCrystalItem extends Item {
	public EndCrystalItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (!blockState.isOf(Blocks.OBSIDIAN) && !blockState.isOf(Blocks.BEDROCK)) {
			return ActionResult.FAIL;
		} else {
			BlockPos blockPos2 = blockPos.up();
			if (!world.isAir(blockPos2)) {
				return ActionResult.FAIL;
			} else {
				double d = (double)blockPos2.getX();
				double e = (double)blockPos2.getY();
				double f = (double)blockPos2.getZ();
				List<Entity> list = world.getOtherEntities(null, new Box(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return ActionResult.FAIL;
				} else {
					if (world instanceof ServerWorld) {
						EndCrystalEntity endCrystalEntity = new EndCrystalEntity(world, d + 0.5, e, f + 0.5);
						endCrystalEntity.setShowBottom(false);
						world.spawnEntity(endCrystalEntity);
						world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos2);
						EnderDragonFight enderDragonFight = ((ServerWorld)world).getEnderDragonFight();
						if (enderDragonFight != null) {
							enderDragonFight.respawnDragon();
						}
					}

					context.getStack().decrement(1);
					return ActionResult.success(world.isClient);
				}
			}
		}
	}
}
