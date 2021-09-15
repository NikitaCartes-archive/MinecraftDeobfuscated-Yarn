package net.minecraft;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public abstract class class_6579 extends BlockStateProvider {
	protected final long field_34707;
	protected final DoublePerlinNoiseSampler.NoiseParameters field_34708;
	protected final float field_34709;
	protected final DoublePerlinNoiseSampler field_34710;

	protected static <P extends class_6579> P3<Mu<P>, Long, DoublePerlinNoiseSampler.NoiseParameters, Float> method_38439(Instance<P> instance) {
		return instance.group(
			Codec.LONG.fieldOf("seed").forGetter(arg -> arg.field_34707),
			DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(arg -> arg.field_34708),
			Codecs.field_34387.fieldOf("scale").forGetter(arg -> arg.field_34709)
		);
	}

	protected class_6579(long l, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float f) {
		this.field_34707 = l;
		this.field_34708 = noiseParameters;
		this.field_34709 = f;
		this.field_34710 = DoublePerlinNoiseSampler.method_38476(new ChunkRandom(l), noiseParameters);
	}

	protected double method_38441(BlockPos blockPos, double d) {
		return this.field_34710.sample((double)blockPos.getX() * d, (double)blockPos.getY() * d, (double)blockPos.getZ() * d);
	}
}
