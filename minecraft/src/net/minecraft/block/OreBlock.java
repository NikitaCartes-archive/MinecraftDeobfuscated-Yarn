package net.minecraft.block;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class OreBlock extends Block {
	public OreBlock(Block.Settings settings) {
		super(settings);
	}

	protected int getExperienceWhenMined(Random random) {
		if (this == Blocks.field_10418) {
			return MathHelper.nextInt(random, 0, 2);
		} else if (this == Blocks.field_10442) {
			return MathHelper.nextInt(random, 3, 7);
		} else if (this == Blocks.field_10013) {
			return MathHelper.nextInt(random, 3, 7);
		} else if (this == Blocks.field_10090) {
			return MathHelper.nextInt(random, 2, 5);
		} else {
			return this == Blocks.field_10213 ? MathHelper.nextInt(random, 2, 5) : 0;
		}
	}

	@Override
	public void method_9565(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
		super.method_9565(blockState, world, blockPos, itemStack);
		if (EnchantmentHelper.getLevel(Enchantments.field_9099, itemStack) == 0) {
			int i = this.getExperienceWhenMined(world.random);
			if (i > 0) {
				this.method_9583(world, blockPos, i);
			}
		}
	}
}
