package net.minecraft.world;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;

public interface TestableWorld {
	boolean testBlockState(BlockPos pos, Predicate<BlockState> state);

	boolean testFluidState(BlockPos pos, Predicate<FluidState> state);

	<T extends BlockEntity> Optional<T> getBlockEntity(BlockPos pos, BlockEntityType<T> type);

	BlockPos getTopPosition(Heightmap.Type heightmap, BlockPos pos);
}
