package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;

public record EnvironmentScanDecoratorConfig() implements DecoratorConfig {
	private final Direction directionOfSearch;
	private final BlockPredicate targetCondition;
	private final int maxSteps;
	public static final Codec<EnvironmentScanDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Direction.VERTICAL_CODEC.fieldOf("direction_of_search").forGetter(EnvironmentScanDecoratorConfig::directionOfSearch),
					BlockPredicate.BASE_CODEC.fieldOf("target_condition").forGetter(EnvironmentScanDecoratorConfig::targetCondition),
					Codec.intRange(1, 32).fieldOf("max_steps").forGetter(EnvironmentScanDecoratorConfig::maxSteps)
				)
				.apply(instance, EnvironmentScanDecoratorConfig::new)
	);

	public EnvironmentScanDecoratorConfig(Direction direction, BlockPredicate blockPredicate, int i) {
		this.directionOfSearch = direction;
		this.targetCondition = blockPredicate;
		this.maxSteps = i;
	}
}
