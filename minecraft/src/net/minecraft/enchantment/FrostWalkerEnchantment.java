package net.minecraft.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FrostWalkerEnchantment extends Enchantment {
	public FrostWalkerEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.FEET, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return i * 10;
	}

	@Override
	public boolean isLootOnly() {
		return true;
	}

	@Override
	public int getMaximumLevel() {
		return 2;
	}

	public static void method_8236(LivingEntity livingEntity, World world, BlockPos blockPos, int i) {
		if (livingEntity.onGround) {
			BlockState blockState = Blocks.field_10110.getDefaultState();
			float f = (float)Math.min(16, 2 + i);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(blockPos.add((double)(-f), -1.0, (double)(-f)), blockPos.add((double)f, -1.0, (double)f))) {
				if (blockPos2.squaredDistanceToCenter(livingEntity.x, livingEntity.y, livingEntity.z) <= (double)(f * f)) {
					mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
					BlockState blockState2 = world.getBlockState(mutable);
					if (blockState2.isAir()) {
						BlockState blockState3 = world.getBlockState(blockPos2);
						if (blockState3.getMaterial() == Material.WATER
							&& (Integer)blockState3.get(FluidBlock.LEVEL) == 0
							&& blockState.canPlaceAt(world, blockPos2)
							&& world.method_8628(blockState, blockPos2, VerticalEntityPosition.minValue())) {
							world.setBlockState(blockPos2, blockState);
							world.getBlockTickScheduler().schedule(blockPos2.toImmutable(), Blocks.field_10110, MathHelper.nextInt(livingEntity.getRand(), 60, 120));
						}
					}
				}
			}
		}
	}

	@Override
	public boolean differs(Enchantment enchantment) {
		return super.differs(enchantment) && enchantment != Enchantments.field_9128;
	}
}
