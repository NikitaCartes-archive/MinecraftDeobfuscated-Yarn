package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;

public abstract class BlockPlacer {
	public static final Codec<BlockPlacer> TYPE_CODEC = Registry.BLOCK_PLACER_TYPE.dispatch(BlockPlacer::getType, BlockPlacerType::getCodec);

	public abstract void generate(WorldAccess world, BlockPos pos, BlockState state, Random random);

	protected abstract BlockPlacerType<?> getType();
}
