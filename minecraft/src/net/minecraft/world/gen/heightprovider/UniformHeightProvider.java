package net.minecraft.world.gen.heightprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UniformHeightProvider extends HeightProvider {
	public static final Codec<UniformHeightProvider> UNIFORM_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter(uniformHeightProvider -> uniformHeightProvider.minOffset),
					YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter(uniformHeightProvider -> uniformHeightProvider.maxOffset)
				)
				.apply(instance, UniformHeightProvider::new)
	);
	private static final Logger LOGGER = LogManager.getLogger();
	private final YOffset minOffset;
	private final YOffset maxOffset;

	private UniformHeightProvider(YOffset minOffset, YOffset maxOffset) {
		this.minOffset = minOffset;
		this.maxOffset = maxOffset;
	}

	public static UniformHeightProvider create(YOffset minOffset, YOffset maxOffset) {
		return new UniformHeightProvider(minOffset, maxOffset);
	}

	@Override
	public int get(Random random, HeightContext context) {
		int i = this.minOffset.getY(context);
		int j = this.maxOffset.getY(context);
		if (i > j) {
			LOGGER.warn("Empty height range: {}", this);
			return i;
		} else {
			return MathHelper.nextBetween(random, i, j);
		}
	}

	@Override
	public HeightProviderType<?> getType() {
		return HeightProviderType.UNIFORM;
	}

	public String toString() {
		return "[" + this.minOffset + "-" + this.maxOffset + "]";
	}
}
