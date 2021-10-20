package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;

public class ClampedNormalIntProvider extends IntProvider {
	public static final Codec<ClampedNormalIntProvider> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("mean").forGetter(clampedNormalIntProvider -> clampedNormalIntProvider.field_35353),
						Codec.FLOAT.fieldOf("deviation").forGetter(clampedNormalIntProvider -> clampedNormalIntProvider.field_35354),
						Codec.INT.fieldOf("min_inclusive").forGetter(clampedNormalIntProvider -> clampedNormalIntProvider.field_35355),
						Codec.INT.fieldOf("max_inclusive").forGetter(clampedNormalIntProvider -> clampedNormalIntProvider.field_35356)
					)
					.apply(instance, ClampedNormalIntProvider::new)
		)
		.comapFlatMap(
			clampedNormalIntProvider -> clampedNormalIntProvider.field_35356 < clampedNormalIntProvider.field_35355
					? DataResult.error("Max must be larger than min: [" + clampedNormalIntProvider.field_35355 + ", " + clampedNormalIntProvider.field_35356 + "]")
					: DataResult.success(clampedNormalIntProvider),
			Function.identity()
		);
	private float field_35353;
	private float field_35354;
	private int field_35355;
	private int field_35356;

	public static ClampedNormalIntProvider method_39156(float f, float g, int i, int j) {
		return new ClampedNormalIntProvider(f, g, i, j);
	}

	private ClampedNormalIntProvider(float f, float g, int i, int j) {
		this.field_35353 = f;
		this.field_35354 = g;
		this.field_35355 = i;
		this.field_35356 = j;
	}

	@Override
	public int get(Random random) {
		return method_39159(random, this.field_35353, this.field_35354, (float)this.field_35355, (float)this.field_35356);
	}

	public static int method_39159(Random random, float f, float g, float h, float i) {
		return (int)MathHelper.clamp(MathHelper.nextGaussian(random, f, g), h, i);
	}

	@Override
	public int getMin() {
		return this.field_35355;
	}

	@Override
	public int getMax() {
		return this.field_35356;
	}

	@Override
	public IntProviderType<?> getType() {
		return IntProviderType.CLAMPED_NORMAL;
	}

	public String toString() {
		return "normal(" + this.field_35353 + ", " + this.field_35354 + ") in [" + this.field_35355 + "-" + this.field_35356 + "]";
	}
}
