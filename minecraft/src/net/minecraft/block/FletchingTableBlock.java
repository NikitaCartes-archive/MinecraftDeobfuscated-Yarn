package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FletchingTableBlock extends CraftingTableBlock {
	public static final MapCodec<FletchingTableBlock> CODEC = createCodec(FletchingTableBlock::new);

	@Override
	public MapCodec<FletchingTableBlock> getCodec() {
		return CODEC;
	}

	protected FletchingTableBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult method_55766(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
		return ActionResult.PASS;
	}
}
