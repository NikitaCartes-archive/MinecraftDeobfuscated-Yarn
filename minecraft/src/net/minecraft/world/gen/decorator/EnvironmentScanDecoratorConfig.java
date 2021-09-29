package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;

public final class EnvironmentScanDecoratorConfig extends Record implements DecoratorConfig {
	private final Direction directionOfSearch;
	private final BlockPredicate targetCondition;
	private final int maxSteps;
	public static final Codec<EnvironmentScanDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Direction.field_35088.fieldOf("direction_of_search").forGetter(EnvironmentScanDecoratorConfig::directionOfSearch),
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",EnvironmentScanDecoratorConfig,"directionOfSearch;targetCondition;maxSteps",EnvironmentScanDecoratorConfig::directionOfSearch,EnvironmentScanDecoratorConfig::targetCondition,EnvironmentScanDecoratorConfig::maxSteps>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",EnvironmentScanDecoratorConfig,"directionOfSearch;targetCondition;maxSteps",EnvironmentScanDecoratorConfig::directionOfSearch,EnvironmentScanDecoratorConfig::targetCondition,EnvironmentScanDecoratorConfig::maxSteps>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",EnvironmentScanDecoratorConfig,"directionOfSearch;targetCondition;maxSteps",EnvironmentScanDecoratorConfig::directionOfSearch,EnvironmentScanDecoratorConfig::targetCondition,EnvironmentScanDecoratorConfig::maxSteps>(
			this, object
		);
	}

	public Direction directionOfSearch() {
		return this.directionOfSearch;
	}

	public BlockPredicate targetCondition() {
		return this.targetCondition;
	}

	public int maxSteps() {
		return this.maxSteps;
	}
}
