package net.minecraft;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public class class_7071 extends AbstractTagProvider<ConfiguredStructureFeature<?, ?>> {
	public class_7071(DataGenerator dataGenerator) {
		super(dataGenerator, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(class_7045.VILLAGE)
			.add(class_7058.VILLAGE_PLAINS)
			.add(class_7058.VILLAGE_DESERT)
			.add(class_7058.VILLAGE_SAVANNA)
			.add(class_7058.VILLAGE_SNOWY)
			.add(class_7058.VILLAGE_TAIGA);
		this.getOrCreateTagBuilder(class_7045.MINESHAFT).add(class_7058.MINESHAFT).add(class_7058.MINESHAFT_MESA);
		this.getOrCreateTagBuilder(class_7045.OCEAN_RUIN).add(class_7058.OCEAN_RUIN_COLD).add(class_7058.OCEAN_RUIN_WARM);
		this.getOrCreateTagBuilder(class_7045.SHIPWRECK).add(class_7058.SHIPWRECK).add(class_7058.SHIPWRECK_BEACHED);
		this.getOrCreateTagBuilder(class_7045.RUINED_PORTAL)
			.add(class_7058.RUINED_PORTAL_DESERT)
			.add(class_7058.RUINED_PORTAL_JUNGLE)
			.add(class_7058.RUINED_PORTAL_MOUNTAIN)
			.add(class_7058.RUINED_PORTAL_NETHER)
			.add(class_7058.RUINED_PORTAL_OCEAN)
			.add(class_7058.RUINED_PORTAL)
			.add(class_7058.RUINED_PORTAL_SWAMP);
		this.getOrCreateTagBuilder(class_7045.EYE_OF_ENDER_LOCATED).add(class_7058.STRONGHOLD);
		this.getOrCreateTagBuilder(class_7045.DOLPHIN_LOCATED).addTag(class_7045.OCEAN_RUIN).addTag(class_7045.SHIPWRECK);
		this.getOrCreateTagBuilder(class_7045.ON_WOODLAND_EXPLORER_MAPS).add(class_7058.MANSION);
		this.getOrCreateTagBuilder(class_7045.ON_OCEAN_EXPLORER_MAPS).add(class_7058.MONUMENT);
		this.getOrCreateTagBuilder(class_7045.ON_TREASURE_MAPS).add(class_7058.BURIED_TREASURE);
	}

	@Override
	public String getName() {
		return "Configured Structure Feature Tags";
	}
}
