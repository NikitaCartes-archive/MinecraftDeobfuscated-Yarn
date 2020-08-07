package net.minecraft.block;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class OreBlock extends Block {
	public OreBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	protected int getExperienceWhenMined(Random random) {
		if (this == Blocks.COAL_ORE) {
			return MathHelper.nextInt(random, 0, 2);
		} else if (this == Blocks.DIAMOND_ORE) {
			return MathHelper.nextInt(random, 3, 7);
		} else if (this == Blocks.EMERALD_ORE) {
			return MathHelper.nextInt(random, 3, 7);
		} else if (this == Blocks.LAPIS_ORE) {
			return MathHelper.nextInt(random, 2, 5);
		} else if (this == Blocks.NETHER_QUARTZ_ORE) {
			return MathHelper.nextInt(random, 2, 5);
		} else {
			return this == Blocks.NETHER_GOLD_ORE ? MathHelper.nextInt(random, 0, 1) : 0;
		}
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
		super.onStacksDropped(state, world, pos, stack);
		if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
			int i = this.getExperienceWhenMined(world.random);
			if (i > 0) {
				this.dropExperience(world, pos, i);
			}
		}
	}
}
