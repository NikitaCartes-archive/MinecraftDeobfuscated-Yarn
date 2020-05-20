package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;

public abstract class BlockPlacer {
	public static final Codec<BlockPlacer> field_24865 = Registry.BLOCK_PLACER_TYPE.dispatch(BlockPlacer::method_28673, BlockPlacerType::method_28674);

	public abstract void method_23403(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState, Random random);

	protected abstract BlockPlacerType<?> method_28673();
}
