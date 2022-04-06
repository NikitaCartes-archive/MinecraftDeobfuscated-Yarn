package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;

public class MultifaceGrowthFeatureConfig implements FeatureConfig {
	public static final Codec<MultifaceGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registry.BLOCK
						.getCodec()
						.fieldOf("block")
						.<Block>flatXmap(MultifaceGrowthFeatureConfig::validateBlock, DataResult::success)
						.orElse((AbstractLichenBlock)Blocks.GLOW_LICHEN)
						.forGetter(config -> config.lichen),
					Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter(config -> config.searchRange),
					Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter(config -> config.placeOnFloor),
					Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter(config -> config.placeOnCeiling),
					Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter(config -> config.placeOnWalls),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter(config -> config.spreadChance),
					RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("can_be_placed_on").forGetter(config -> config.canPlaceOn)
				)
				.apply(instance, MultifaceGrowthFeatureConfig::new)
	);
	public final AbstractLichenBlock lichen;
	public final int searchRange;
	public final boolean placeOnFloor;
	public final boolean placeOnCeiling;
	public final boolean placeOnWalls;
	public final float spreadChance;
	public final RegistryEntryList<Block> canPlaceOn;
	public final List<Direction> directions;

	private static DataResult<AbstractLichenBlock> validateBlock(Block block) {
		return block instanceof AbstractLichenBlock abstractLichenBlock
			? DataResult.success(abstractLichenBlock)
			: DataResult.error("Growth block should be a multiface block");
	}

	public MultifaceGrowthFeatureConfig(
		AbstractLichenBlock lichen,
		int searchRange,
		boolean placeOnFloor,
		boolean placeOnCeiling,
		boolean placeOnWalls,
		float spreadChance,
		RegistryEntryList<Block> canPlaceOn
	) {
		this.lichen = lichen;
		this.searchRange = searchRange;
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
