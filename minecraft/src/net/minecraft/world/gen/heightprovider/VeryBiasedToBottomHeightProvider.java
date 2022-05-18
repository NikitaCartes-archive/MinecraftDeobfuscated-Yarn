package net.minecraft.world.gen.heightprovider;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import org.slf4j.Logger;

public class VeryBiasedToBottomHeightProvider extends HeightProvider {
	public static final Codec<VeryBiasedToBottomHeightProvider> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter(provider -> provider.minOffset),
					YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter(provider -> provider.maxOffset),
					Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("inner", 1).forGetter(provider -> provider.inner)
				)
				.apply(instance, VeryBiasedToBottomHeightProvider::new)
	);
	private static final Logger LOGGER = LogUtils.getLogger();
	private final YOffset minOffset;
	private final YOffset maxOffset;
	private final int inner;

	private VeryBiasedToBottomHeightProvider(YOffset minOffset, YOffset maxOffset, int inner) {
		this.minOffset = minOffset;
		this.maxOffset = maxOffset;
		this.inner = inner;
	}

	public static VeryBiasedToBottomHeightProvider create(YOffset minOffset, YOffset maxOffset, int inner) {
		return new VeryBiasedToBottomHeightProvider(minOffset, maxOffset, inner);
	}

	@Override
	public int get(Random random, HeightContext context) {
		int i = this.minOffset.getY(context);
		int j = this.maxOffset.getY(context);
		if (j - i - this.inner + 1 <= 0) {
			LOGGER.warn("Empty height range: {}", this);
			return i;
		} else {
			int k = MathHelper.nextInt(random, i + this.inner, j);
			int l = MathHelper.nextInt(random, i, k - 1);
			return MathHelper.nextInt(random, i, l - 1 + this.inner);
		}
	}

	@Override
	public HeightProviderType<?> getType() {
		return HeightProviderType.VERY_BIASED_TO_BOTTOM;
	}

	public String toString() {
		return "biased[" + this.minOffset + "-" + this.maxOffset + " inner: " + this.inner + "]";
	}
}
