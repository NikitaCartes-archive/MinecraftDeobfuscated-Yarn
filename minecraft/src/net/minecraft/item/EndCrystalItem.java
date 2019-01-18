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
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;

public class EndCrystalItem extends Item {
	public EndCrystalItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != Blocks.field_10540 && blockState.getBlock() != Blocks.field_9987) {
			return ActionResult.FAILURE;
		} else {
			BlockPos blockPos2 = blockPos.up();
			if (!world.isAir(blockPos2)) {
				return ActionResult.FAILURE;
			} else {
				double d = (double)blockPos2.getX();
				double e = (double)blockPos2.getY();
				double f = (double)blockPos2.getZ();
				List<Entity> list = world.getVisibleEntities(null, new BoundingBox(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return ActionResult.FAILURE;
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

					itemUsageContext.getItemStack().subtractAmount(1);
					return ActionResult.SUCCESS;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasEnchantmentGlow(ItemStack itemStack) {
		return true;
	}
}
