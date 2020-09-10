package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;

public class SimpleBlockFeatureConfig implements FeatureConfig {
	public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("to_place").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.toPlace),
					BlockState.CODEC.listOf().fieldOf("place_on").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeOn),
					BlockState.CODEC.listOf().fieldOf("place_in").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeIn),
					BlockState.CODEC.listOf().fieldOf("place_under").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeUnder)
				)
				.apply(instance, SimpleBlockFeatureConfig::new)
	);
	public final BlockState toPlace;
	public final List<BlockState> placeOn;
	public final List<BlockState> placeIn;
	public final List<BlockState> placeUnder;

	public SimpleBlockFeatureConfig(BlockState toPlace, List<BlockState> placeOn, List<BlockState> placeIn, List<BlockState> placeUnder) {
		this.toPlace = toPlace;
		this.placeOn = placeOn;
		this.placeIn = placeIn;
		this.placeUnder = placeUnder;
	}
}
