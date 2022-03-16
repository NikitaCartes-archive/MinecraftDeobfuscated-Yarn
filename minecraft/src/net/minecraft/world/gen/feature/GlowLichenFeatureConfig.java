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

public class GlowLichenFeatureConfig implements FeatureConfig {
	public static final Codec<GlowLichenFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registry.BLOCK
						.getCodec()
						.fieldOf("block")
						.<Block>flatXmap(GlowLichenFeatureConfig::method_41573, DataResult::success)
						.orElse((AbstractLichenBlock)Blocks.GLOW_LICHEN)
						.forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.field_37709),
					Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter(config -> config.searchRange),
					Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter(config -> config.placeOnFloor),
					Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter(config -> config.placeOnCeiling),
					Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter(config -> config.placeOnWalls),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter(config -> config.spreadChance),
					RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("can_be_placed_on").forGetter(config -> config.canPlaceOn)
				)
				.apply(instance, GlowLichenFeatureConfig::new)
	);
	public final AbstractLichenBlock field_37709;
	public final int searchRange;
	public final boolean placeOnFloor;
	public final boolean placeOnCeiling;
	public final boolean placeOnWalls;
	public final float spreadChance;
	public final RegistryEntryList<Block> canPlaceOn;
	public final List<Direction> directions;

	private static DataResult<AbstractLichenBlock> method_41573(Block block) {
		return block instanceof AbstractLichenBlock abstractLichenBlock
			? DataResult.success(abstractLichenBlock)
			: DataResult.error("Growth block should be a multiface block");
	}

	public GlowLichenFeatureConfig(
		AbstractLichenBlock abstractLichenBlock, int i, boolean bl, boolean bl2, boolean bl3, float f, RegistryEntryList<Block> registryEntryList
	) {
		this.field_37709 = abstractLichenBlock;
		this.searchRange = i;
		this.placeOnFloor = bl;
		this.placeOnCeiling = bl2;
		this.placeOnWalls = bl3;
		this.spreadChance = f;
		this.canPlaceOn = registryEntryList;
		List<Direction> list = Lists.<Direction>newArrayList();
		if (bl2) {
			list.add(Direction.UP);
		}

		if (bl) {
			list.add(Direction.DOWN);
		}

		if (bl3) {
			Direction.Type.HORIZONTAL.forEach(list::add);
		}

		this.directions = Collections.unmodifiableList(list);
	}
}
