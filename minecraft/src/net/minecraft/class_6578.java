package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class class_6578 extends class_6580 {
	public static final Codec<class_6578> field_34702 = RecordCodecBuilder.create(
		instance -> instance.group(
					Range.createRangedCodec(Codec.INT, 1, 64).fieldOf("variety").forGetter(arg -> arg.field_34703),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("slow_noise").forGetter(arg -> arg.field_34704),
					Codecs.field_34387.fieldOf("slow_scale").forGetter(arg -> arg.field_34705)
				)
				.<long, DoublePerlinNoiseSampler.NoiseParameters, float, List<BlockState>>and(method_38447(instance))
				.apply(instance, class_6578::new)
	);
	private final Range<Integer> field_34703;
	private final DoublePerlinNoiseSampler.NoiseParameters field_34704;
	private final float field_34705;
	private final DoublePerlinNoiseSampler field_34706;

	public class_6578(
		Range<Integer> range,
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters,
		float f,
		long l,
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters2,
		float g,
		List<BlockState> list
	) {
		super(l, noiseParameters2, g, list);
		this.field_34703 = range;
		this.field_34704 = noiseParameters;
		this.field_34705 = f;
		this.field_34706 = DoublePerlinNoiseSampler.method_38476(new ChunkRandom(l), noiseParameters);
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.DUAL_NOISE_2D_PROVIDER;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		double d = this.method_38435(pos);
		int i = (int)MathHelper.lerpFromProgress(
			d, -1.0, 1.0, (double)((Integer)this.field_34703.minInclusive()).intValue(), (double)((Integer)this.field_34703.maxInclusive() + 1)
		);
		List<BlockState> list = Lists.<BlockState>newArrayListWithCapacity(i);

		for (int j = 0; j < i; j++) {
			list.add(this.method_38445(this.field_34712, this.method_38435(pos.add(j * 54545, 0, j * 34234))));
		}

		return this.method_38446(list, pos, (double)this.field_34709);
	}

	protected double method_38435(BlockPos blockPos) {
		return this.field_34706
			.sample(
				(double)((float)blockPos.getX() * this.field_34705),
				(double)((float)blockPos.getY() * this.field_34705),
				(double)((float)blockPos.getZ() * this.field_34705)
			);
	}
}
