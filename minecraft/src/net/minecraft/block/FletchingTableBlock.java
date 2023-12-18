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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		return ActionResult.PASS;
	}
}
