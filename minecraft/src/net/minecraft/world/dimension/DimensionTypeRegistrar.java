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
			DimensionType.create(
				OptionalLong.empty(),
				true,
				false,
				false,
				true,
				1.0,
				false,
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
			DimensionType.create(
				OptionalLong.of(18000L),
				false,
				true,
				true,
				false,
				8.0,
				false,
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
			DimensionType.create(
				OptionalLong.of(6000L),
				false,
				false,
				false,
				false,
				1.0,
				true,
				false,
				false,
				false,
				true,
				0,
				256,
				256,
				BlockTags.INFINIBURN_END,
				DimensionTypes.THE_END_ID,
				0.0F
			)
		);
		return BuiltinRegistries.add(
			registry,
			DimensionTypes.OVERWORLD_CAVES,
			DimensionType.create(
				OptionalLong.empty(),
				true,
				true,
				false,
				true,
				1.0,
				false,
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
