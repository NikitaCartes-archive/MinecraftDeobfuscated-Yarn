package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.Products.P4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.Random;

public class NoiseBlockStateProvider extends AbstractNoiseBlockStateProvider {
	public static final Codec<NoiseBlockStateProvider> CODEC = RecordCodecBuilder.create(
		instance -> fillNoiseCodecFields(instance).apply(instance, NoiseBlockStateProvider::new)
	);
	protected final List<BlockState> states;

	protected static <P extends NoiseBlockStateProvider> P4<Mu<P>, Long, DoublePerlinNoiseSampler.NoiseParameters, Float, List<BlockState>> fillNoiseCodecFields(
		Instance<P> instance
	) {
		return fillCodecFields(instance).and(Codec.list(BlockState.CODEC).fieldOf("states").forGetter(noiseBlockStateProvider -> noiseBlockStateProvider.states));
	}

	public NoiseBlockStateProvider(long seed, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float scale, List<BlockState> states) {
		super(seed, noiseParameters, scale);
		this.states = states;
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.NOISE_PROVIDER;
	}

	@Override
	public BlockState get(Random random, BlockPos pos) {
		return this.getStateFromList(this.states, pos, (double)this.scale);
	}

	protected BlockState getStateFromList(List<BlockState> states, BlockPos pos, double scale) {
		double d = this.getNoiseValue(pos, scale);
		return this.getStateAtValue(states, d);
	}

	protected BlockState getStateAtValue(List<BlockState> states, double value) {
		double d = MathHelper.clamp((1.0 + value) / 2.0, 0.0, 0.9999);
		return (BlockState)states.get((int)(d * (double)states.size()));
	}
}
