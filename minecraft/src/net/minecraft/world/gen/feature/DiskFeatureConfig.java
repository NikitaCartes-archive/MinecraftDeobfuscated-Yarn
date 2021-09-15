package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;

public final class DiskFeatureConfig extends Record implements FeatureConfig {
	private final BlockState state;
	private final IntProvider radius;
	private final int halfHeight;
	private final List<BlockState> targets;
	public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("state").forGetter(DiskFeatureConfig::state),
					IntProvider.createValidatingCodec(0, 8).fieldOf("radius").forGetter(DiskFeatureConfig::radius),
					Codec.intRange(0, 4).fieldOf("half_height").forGetter(DiskFeatureConfig::halfHeight),
					BlockState.CODEC.listOf().fieldOf("targets").forGetter(DiskFeatureConfig::targets)
				)
				.apply(instance, DiskFeatureConfig::new)
	);

	public DiskFeatureConfig(BlockState blockState, IntProvider intProvider, int i, List<BlockState> list) {
		this.state = blockState;
		this.radius = intProvider;
		this.halfHeight = i;
		this.targets = list;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",DiskFeatureConfig,"state;radius;halfHeight;targets",DiskFeatureConfig::state,DiskFeatureConfig::radius,DiskFeatureConfig::halfHeight,DiskFeatureConfig::targets>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",DiskFeatureConfig,"state;radius;halfHeight;targets",DiskFeatureConfig::state,DiskFeatureConfig::radius,DiskFeatureConfig::halfHeight,DiskFeatureConfig::targets>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",DiskFeatureConfig,"state;radius;halfHeight;targets",DiskFeatureConfig::state,DiskFeatureConfig::radius,DiskFeatureConfig::halfHeight,DiskFeatureConfig::targets>(
			this, object
		);
	}

	public BlockState state() {
		return this.state;
	}

	public IntProvider radius() {
		return this.radius;
	}

	public int halfHeight() {
		return this.halfHeight;
	}

	public List<BlockState> targets() {
		return this.targets;
	}
}
