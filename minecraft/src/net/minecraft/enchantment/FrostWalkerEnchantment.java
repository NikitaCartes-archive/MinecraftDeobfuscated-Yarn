package net.minecraft.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FrostWalkerEnchantment extends Enchantment {
	public FrostWalkerEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.ARMOR_FEET, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return level * 10;
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + 15;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	public static void freezeWater(LivingEntity entity, World world, BlockPos blockPos, int level) {
		if (entity.isOnGround()) {
			BlockState blockState = Blocks.FROSTED_ICE.getDefaultState();
			int i = Math.min(16, 2 + level);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-i, -1, -i), blockPos.add(i, -1, i))) {
				if (blockPos2.isWithinDistance(entity.getPos(), (double)i)) {
					mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
					BlockState blockState2 = world.getBlockState(mutable);
					if (blockState2.isAir()) {
						BlockState blockState3 = world.getBlockState(blockPos2);
						if (blockState3 == FrostedIceBlock.getMeltedState()
							&& blockState.canPlaceAt(world, blockPos2)
							&& world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
							world.setBlockState(blockPos2, blockState);
							world.scheduleBlockTick(blockPos2, Blocks.FROSTED_ICE, MathHelper.nextInt(entity.getRandom(), 60, 120));
						}
					}
				}
			}
		}
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.DEPTH_STRIDER;
	}
}
