package net.minecraft.data.server.advancement.onetwenty;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import net.minecraft.data.server.advancement.vanilla.VanillaAdventureTabAdvancementGenerator;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;

public class OneTwentyAdventureTabAdvancementGenerator implements AdvancementTabGenerator {
	@Override
	public void accept(RegistryWrapper.WrapperLookup lookup, Consumer<Advancement> exporter) {
		Advancement advancement = AdvancementTabGenerator.createEmptyAdvancement("adventure/sleep_in_bed");
		VanillaAdventureTabAdvancementGenerator.buildAdventuringTime(exporter, advancement, MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD_UPDATE_1_20);
	}
}
