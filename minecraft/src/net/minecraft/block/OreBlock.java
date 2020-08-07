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
		if (this == Blocks.field_10418) {
			return MathHelper.nextInt(random, 0, 2);
		} else if (this == Blocks.field_10442) {
			return MathHelper.nextInt(random, 3, 7);
		} else if (this == Blocks.field_10013) {
			return MathHelper.nextInt(random, 3, 7);
		} else if (this == Blocks.field_10090) {
			return MathHelper.nextInt(random, 2, 5);
		} else if (this == Blocks.field_10213) {
			return MathHelper.nextInt(random, 2, 5);
		} else {
			return this == Blocks.field_23077 ? MathHelper.nextInt(random, 0, 1) : 0;
		}
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld serverWorld, BlockPos pos, ItemStack stack) {
		super.onStacksDropped(state, serverWorld, pos, stack);
		if (EnchantmentHelper.getLevel(Enchantments.field_9099, stack) == 0) {
			int i = this.getExperienceWhenMined(serverWorld.random);
			if (i > 0) {
				this.dropExperience(serverWorld, pos, i);
			}
		}
	}
}
