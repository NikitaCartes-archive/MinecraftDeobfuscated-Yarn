package net.minecraft.block;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;

public class OreBlock extends Block {
	private final IntRange field_27195;

	public OreBlock(AbstractBlock.Settings settings) {
		this(settings, IntRange.between(0, 0));
	}

	public OreBlock(AbstractBlock.Settings settings, IntRange intRange) {
		super(settings);
		this.field_27195 = intRange;
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
		super.onStacksDropped(state, world, pos, stack);
		if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
			int i = this.field_27195.choose(world.random);
			if (i > 0) {
				this.dropExperience(world, pos, i);
			}
		}
	}
}
