package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

/**
 * A provider for {@linkplain BlockState block states}. Results may be random or based on a block position.
 */
public abstract class BlockStateProvider {
	public static final Codec<BlockStateProvider> TYPE_CODEC = Registries.BLOCK_STATE_PROVIDER_TYPE
		.getCodec()
		.dispatch(BlockStateProvider::getType, BlockStateProviderType::getCodec);

	/**
	 * {@return a block state provider that always returns the given state}
	 * 
	 * @param state the block state that the block state provider should return
	 */
	public static SimpleBlockStateProvider of(BlockState state) {
		return new SimpleBlockStateProvider(state);
	}

	/**
	 * {@return a block state provider that always returns the {@linkplain Block#getDefaultState() default state} for the given block}
	 * 
	 * @param block the block of the default state that the block state provider should return
	 */
	public static SimpleBlockStateProvider of(Block block) {
		return new SimpleBlockStateProvider(block.getDefaultState());
	}

	/**
	 * {@return the type of this block state provider}
	 * 
	 * @implNote The returned block state provider type should be registered so that the {@code type} field is properly serialized.
	 */
	protected abstract BlockStateProviderType<?> getType();

	/**
	 * {@return a provided block state}
	 */
	public abstract BlockState get(Random random, BlockPos pos);
}
