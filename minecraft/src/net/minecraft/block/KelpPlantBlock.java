package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class KelpPlantBlock extends AbstractPlantBlock implements FluidFillable {
	public static final MapCodec<KelpPlantBlock> CODEC = createCodec(KelpPlantBlock::new);

	@Override
	public MapCodec<KelpPlantBlock> getCodec() {
		return CODEC;
	}

	protected KelpPlantBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.UP, VoxelShapes.fullCube(), true);
	}

	@Override
	protected AbstractPlantStemBlock getStem() {
		return (AbstractPlantStemBlock)Blocks.KELP;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getStill(false);
	}

	@Override
	protected boolean canAttachTo(BlockState state) {
		return this.getStem().canAttachTo(state);
	}

	@Override
	public boolean canFillWithFluid(@Nullable PlayerEntity player, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}
}
