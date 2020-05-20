package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;

public class SimpleBlockFeatureConfig implements FeatureConfig {
	public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.field_24734.fieldOf("to_place").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.toPlace),
					BlockState.field_24734.listOf().fieldOf("place_on").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeOn),
					BlockState.field_24734.listOf().fieldOf("place_in").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeIn),
					BlockState.field_24734.listOf().fieldOf("place_under").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeUnder)
				)
				.apply(instance, SimpleBlockFeatureConfig::new)
	);
	public final BlockState toPlace;
	public final List<BlockState> placeOn;
	public final List<BlockState> placeIn;
	public final List<BlockState> placeUnder;

	public SimpleBlockFeatureConfig(BlockState blockState, List<BlockState> list, List<BlockState> list2, List<BlockState> list3) {
		this.toPlace = blockState;
		this.placeOn = list;
		this.placeIn = list2;
		this.placeUnder = list3;
	}
}
