package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class LichenFeatureConfig implements FeatureConfig {
	public static final Codec<LichenFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("block_to_place").forGetter(lichenFeatureConfig -> lichenFeatureConfig.blockToPlace),
					Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter(lichenFeatureConfig -> lichenFeatureConfig.searchRange),
					Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter(lichenFeatureConfig -> lichenFeatureConfig.placeOnFloor),
					Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter(lichenFeatureConfig -> lichenFeatureConfig.placeOnCeiling),
					Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter(lichenFeatureConfig -> lichenFeatureConfig.placeOnWalls),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter(lichenFeatureConfig -> lichenFeatureConfig.spreadChance),
					Registry.BLOCK.getCodec().listOf().fieldOf("can_be_placed_on").forGetter(lichenFeatureConfig -> lichenFeatureConfig.canPlaceOn)
				)
				.apply(instance, LichenFeatureConfig::new)
	);
	public final BlockState blockToPlace;
	public final int searchRange;
	public final boolean placeOnFloor;
	public final boolean placeOnCeiling;
	public final boolean placeOnWalls;
	public final float spreadChance;
	public final List<Block> canPlaceOn;
	public final List<Direction> directions;

	public LichenFeatureConfig(
		BlockState blockToPlace, int searcnRange, boolean placeOnFloor, boolean placeOnCeiling, boolean placeOnWalls, float spreadChance, List<Block> canPlaceOn
	) {
		this.blockToPlace = blockToPlace;
		this.searchRange = searcnRange;
		this.placeOnFloor = placeOnFloor;
		this.placeOnCeiling = placeOnCeiling;
		this.placeOnWalls = placeOnWalls;
		this.spreadChance = spreadChance;
		this.canPlaceOn = canPlaceOn;
		List<Direction> list = Lists.<Direction>newArrayList();
		if (placeOnCeiling) {
			list.add(Direction.UP);
		}

		if (placeOnFloor) {
			list.add(Direction.DOWN);
		}

		if (placeOnWalls) {
			Direction.Type.HORIZONTAL.forEach(list::add);
		}

		this.directions = Collections.unmodifiableList(list);
	}
}
