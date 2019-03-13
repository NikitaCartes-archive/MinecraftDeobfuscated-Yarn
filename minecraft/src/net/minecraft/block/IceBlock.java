package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class IceBlock extends TransparentBlock {
	public IceBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public void method_9556(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.method_9556(world, playerEntity, blockPos, blockState, blockEntity, itemStack);
		if (EnchantmentHelper.getLevel(Enchantments.field_9099, itemStack) == 0) {
			if (world.field_9247.doesWaterVaporize()) {
				world.method_8650(blockPos);
				return;
			}

			Material material = world.method_8320(blockPos.down()).method_11620();
			if (material.suffocates() || material.isLiquid()) {
				world.method_8501(blockPos, Blocks.field_10382.method_9564());
			}
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.method_8314(LightType.BLOCK, blockPos) > 11 - blockState.method_11581(world, blockPos)) {
			this.method_10275(blockState, world, blockPos);
		}
	}

	protected void method_10275(BlockState blockState, World world, BlockPos blockPos) {
		if (world.field_9247.doesWaterVaporize()) {
			world.method_8650(blockPos);
		} else {
			world.method_8501(blockPos, Blocks.field_10382.method_9564());
			world.method_8492(blockPos, Blocks.field_10382, blockPos);
		}
	}

	@Override
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15974;
	}
}
