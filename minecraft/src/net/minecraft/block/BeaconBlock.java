package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BeaconBlock extends BlockWithEntity implements Stainable {
	public static final MapCodec<BeaconBlock> CODEC = createCodec(BeaconBlock::new);

	@Override
	public MapCodec<BeaconBlock> getCodec() {
		return CODEC;
	}

	public BeaconBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public DyeColor getColor() {
		return DyeColor.WHITE;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BeaconBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(type, BlockEntityType.BEACON, BeaconBlockEntity::tick);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!world.isClient && world.getBlockEntity(pos) instanceof BeaconBlockEntity beaconBlockEntity) {
			player.openHandledScreen(beaconBlockEntity);
			player.incrementStat(Stats.INTERACT_WITH_BEACON);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
