package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class class_6581 extends class_6579 {
	public static final Codec<class_6581> field_34713 = RecordCodecBuilder.create(
		instance -> method_38439(instance)
				.<float, float, BlockState, List<BlockState>, List<BlockState>>and(
					instance.group(
						Codec.floatRange(-1.0F, 1.0F).fieldOf("threshold").forGetter(arg -> arg.field_34714),
						Codec.floatRange(0.0F, 1.0F).fieldOf("high_chance").forGetter(arg -> arg.field_34715),
						BlockState.CODEC.fieldOf("default_state").forGetter(arg -> arg.field_34716),
						Codec.list(BlockState.CODEC).fieldOf("low_states").forGetter(arg -> arg.field_34717),
						Codec.list(BlockState.CODEC).fieldOf("high_states").forGetter(arg -> arg.field_34718)
					)
				)
				.apply(instance, class_6581::new)
	);
	private final float field_34714;
	private final float field_34715;
	private final BlockState field_34716;
	private final List<BlockState> field_34717;
	private final List<BlockState> field_34718;

	public class_6581(
		long l,
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters,
		float f,
		float g,
		float h,
		BlockState blockState,
		List<BlockState> list,
		List<BlockState> list2
	) {
		super(l, noiseParameters, f);
		this.field_34714 = g;
		this.field_34715 = h;
		this.field_34716 = blockState;
		this.field_34717 = list;
		this.field_34718 = list2;
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.NOISE_2D_CUTOFF_PROVIDER;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		double d = this.method_38441(pos, (double)this.field_34709);
		if (d < (double)this.field_34714) {
			return Util.getRandom(this.field_34717, random);
		} else {
			return random.nextFloat() < this.field_34715 ? Util.getRandom(this.field_34718, random) : this.field_34716;
		}
	}
}
