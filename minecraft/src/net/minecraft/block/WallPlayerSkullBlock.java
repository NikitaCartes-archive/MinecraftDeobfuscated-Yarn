package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WallPlayerSkullBlock extends WallSkullBlock {
	protected WallPlayerSkullBlock(AbstractBlock.Settings settings) {
		super(SkullBlock.Type.PLAYER, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		Blocks.PLAYER_HEAD.onPlaced(world, pos, state, placer, itemStack);
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		return Blocks.PLAYER_HEAD.getDroppedStacks(state, builder);
	}
}
