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
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.method_8320(blockPos);
		if (blockState.getBlock() != Blocks.field_10540 && blockState.getBlock() != Blocks.field_9987) {
			return ActionResult.field_5814;
		} else {
			BlockPos blockPos2 = blockPos.up();
			if (!world.isAir(blockPos2)) {
				return ActionResult.field_5814;
			} else {
				double d = (double)blockPos2.getX();
				double e = (double)blockPos2.getY();
				double f = (double)blockPos2.getZ();
				List<Entity> list = world.method_8335(null, new Box(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return ActionResult.field_5814;
				} else {
					if (!world.isClient) {
						EnderCrystalEntity enderCrystalEntity = new EnderCrystalEntity(world, d + 0.5, e, f + 0.5);
						enderCrystalEntity.setShowBottom(false);
						world.spawnEntity(enderCrystalEntity);
						if (world.field_9247 instanceof TheEndDimension) {
							EnderDragonFight enderDragonFight = ((TheEndDimension)world.field_9247).method_12513();
							enderDragonFight.respawnDragon();
						}
					}

					itemUsageContext.getStack().decrement(1);
					return ActionResult.field_5812;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasEnchantmentGlint(ItemStack itemStack) {
		return true;
	}
}
