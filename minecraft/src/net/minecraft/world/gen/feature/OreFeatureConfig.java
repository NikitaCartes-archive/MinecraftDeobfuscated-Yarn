package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;

public class OreFeatureConfig implements FeatureConfig {
	public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.list(OreFeatureConfig.Target.CODEC).fieldOf("targets").forGetter(config -> config.targets),
					Codec.intRange(0, 64).fieldOf("size").forGetter(config -> config.size),
					Codec.floatRange(0.0F, 1.0F).fieldOf("discard_chance_on_air_exposure").forGetter(config -> config.discardOnAirChance)
				)
				.apply(instance, OreFeatureConfig::new)
	);
	public final List<OreFeatureConfig.Target> targets;
	public final int size;
	public final float discardOnAirChance;

	public OreFeatureConfig(List<OreFeatureConfig.Target> targets, int size, float discardOnAirChance) {
		this.size = size;
		this.targets = targets;
		this.discardOnAirChance = discardOnAirChance;
	}

	public OreFeatureConfig(List<OreFeatureConfig.Target> targets, int size) {
		this(targets, size, 0.0F);
	}

	public OreFeatureConfig(RuleTest test, BlockState state, int size, float discardOnAirChance) {
		this(ImmutableList.of(new OreFeatureConfig.Target(test, state)), size, discardOnAirChance);
	}

	public OreFeatureConfig(RuleTest test, BlockState state, int size) {
		this(ImmutableList.of(new OreFeatureConfig.Target(test, state)), size, 0.0F);
	}

	public static OreFeatureConfig.Target createTarget(RuleTest test, BlockState state) {
		return new OreFeatureConfig.Target(test, state);
	}

	public static class Target {
		public static final Codec<OreFeatureConfig.Target> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						RuleTest.TYPE_CODEC.fieldOf("target").forGetter(target -> target.target), BlockState.CODEC.fieldOf("state").forGetter(target -> target.state)
					)
					.apply(instance, OreFeatureConfig.Target::new)
		);
		public final RuleTest target;
		public final BlockState state;

		Target(RuleTest target, BlockState state) {
			this.target = target;
			this.state = state;
		}
	}
}
