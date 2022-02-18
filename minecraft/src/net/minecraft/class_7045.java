package net.minecraft;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public interface class_7045 {
	TagKey<ConfiguredStructureFeature<?, ?>> EYE_OF_ENDER_LOCATED = method_41006("eye_of_ender_located");
	TagKey<ConfiguredStructureFeature<?, ?>> DOLPHIN_LOCATED = method_41006("dolphin_located");
	TagKey<ConfiguredStructureFeature<?, ?>> ON_WOODLAND_EXPLORER_MAPS = method_41006("on_woodland_explorer_maps");
	TagKey<ConfiguredStructureFeature<?, ?>> ON_OCEAN_EXPLORER_MAPS = method_41006("on_ocean_explorer_maps");
	TagKey<ConfiguredStructureFeature<?, ?>> ON_TREASURE_MAPS = method_41006("on_treasure_maps");
	TagKey<ConfiguredStructureFeature<?, ?>> VILLAGE = method_41006("village");
	TagKey<ConfiguredStructureFeature<?, ?>> MINESHAFT = method_41006("mineshaft");
	TagKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK = method_41006("shipwreck");
	TagKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL = method_41006("ruined_portal");
	TagKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN = method_41006("ocean_ruin");

	private static TagKey<ConfiguredStructureFeature<?, ?>> method_41006(String string) {
		return TagKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(string));
	}
}
