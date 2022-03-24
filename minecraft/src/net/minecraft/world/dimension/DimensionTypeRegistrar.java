package net.minecraft.world.dimension;

import java.util.OptionalLong;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

public class DimensionTypeRegistrar {
	public static RegistryEntry<DimensionType> initAndGetDefault() {
		Registry<DimensionType> registry = BuiltinRegistries.DIMENSION_TYPE;
		BuiltinRegistries.add(
			registry,
			DimensionTypes.OVERWORLD,
			new DimensionType(
				OptionalLong.empty(),
				true,
				false,
				false,
				true,
				1.0,
				false,
				true,
				false,
				true,
				-64,
				384,
				384,
				BlockTags.INFINIBURN_OVERWORLD,
				DimensionTypes.OVERWORLD_ID,
				0.0F
			)
		);
		BuiltinRegistries.add(
			registry,
			DimensionTypes.THE_NETHER,
			new DimensionType(
				OptionalLong.of(18000L),
				false,
				true,
				true,
				false,
				8.0,
				true,
				false,
				true,
				false,
				0,
				256,
				128,
				BlockTags.INFINIBURN_NETHER,
				DimensionTypes.THE_NETHER_ID,
				0.1F
			)
		);
		BuiltinRegistries.add(
			registry,
			DimensionTypes.THE_END,
			new DimensionType(
				OptionalLong.of(6000L), false, false, false, false, 1.0, false, false, false, true, 0, 256, 256, BlockTags.INFINIBURN_END, DimensionTypes.THE_END_ID, 0.0F
			)
		);
		return BuiltinRegistries.add(
			registry,
			DimensionTypes.OVERWORLD_CAVES,
			new DimensionType(
				OptionalLong.empty(),
				true,
				true,
				false,
				true,
				1.0,
				false,
				true,
				false,
				true,
				-64,
				384,
				384,
				BlockTags.INFINIBURN_OVERWORLD,
				DimensionTypes.OVERWORLD_ID,
				0.0F
			)
		);
	}
}
