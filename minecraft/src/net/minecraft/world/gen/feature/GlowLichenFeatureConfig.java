package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public class GlowLichenFeatureConfig implements FeatureConfig {
	public static final Codec<GlowLichenFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.searchRange),
					Codec.intRange(0, 128).fieldOf("min_distance_below_surface").orElse(0).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.field_34241),
					Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.placeOnFloor),
					Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.placeOnCeiling),
					Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.placeOnWalls),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.spreadChance),
					BlockState.CODEC.listOf().fieldOf("can_be_placed_on").forGetter(glowLichenFeatureConfig -> new ArrayList(glowLichenFeatureConfig.canPlaceOn))
				)
				.apply(instance, GlowLichenFeatureConfig::new)
	);
	public final int searchRange;
	public final int field_34241;
	public final boolean placeOnFloor;
	public final boolean placeOnCeiling;
	public final boolean placeOnWalls;
	public final float spreadChance;
	public final List<BlockState> canPlaceOn;
	public final List<Direction> directions;

	public GlowLichenFeatureConfig(int searchRange, int i, boolean bl, boolean bl2, boolean bl3, float f, List<BlockState> list) {
		this.searchRange = searchRange;
		this.field_34241 = i;
		this.placeOnFloor = bl;
		this.placeOnCeiling = bl2;
		this.placeOnWalls = bl3;
		this.spreadChance = f;
		this.canPlaceOn = list;
		List<Direction> list2 = Lists.<Direction>newArrayList();
		if (bl2) {
			list2.add(Direction.UP);
		}

		if (bl) {
			list2.add(Direction.DOWN);
		}

		if (bl3) {
			Direction.Type.HORIZONTAL.forEach(list2::add);
		}

		this.directions = Collections.unmodifiableList(list2);
	}

	public boolean canGrowOn(Block block) {
		return this.canPlaceOn.stream().anyMatch(state -> state.isOf(block));
	}
}
