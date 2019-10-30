package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;

public class EndCrystalItem extends Item {
	public EndCrystalItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != Blocks.OBSIDIAN && blockState.getBlock() != Blocks.BEDROCK) {
			return ActionResult.FAIL;
		} else {
			BlockPos blockPos2 = blockPos.up();
			if (!world.isAir(blockPos2)) {
				return ActionResult.FAIL;
			} else {
				double d = (double)blockPos2.getX();
				double e = (double)blockPos2.getY();
				double f = (double)blockPos2.getZ();
				List<Entity> list = world.getEntities(null, new Box(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return ActionResult.FAIL;
				} else {
					if (!world.isClient) {
						EnderCrystalEntity enderCrystalEntity = new EnderCrystalEntity(world, d + 0.5, e, f + 0.5);
						enderCrystalEntity.setShowBottom(false);
						world.spawnEntity(enderCrystalEntity);
						if (world.dimension instanceof TheEndDimension) {
							EnderDragonFight enderDragonFight = ((TheEndDimension)world.dimension).method_12513();
							enderDragonFight.respawnDragon();
						}
					}

					context.getStack().decrement(1);
					return ActionResult.SUCCESS;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
	}
}
