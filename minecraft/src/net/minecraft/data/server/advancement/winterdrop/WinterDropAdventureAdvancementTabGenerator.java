package net.minecraft.data.server.advancement.winterdrop;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import net.minecraft.data.server.advancement.vanilla.VanillaAdventureTabAdvancementGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;

public class WinterDropAdventureAdvancementTabGenerator implements AdvancementTabGenerator {
	@Override
	public void accept(RegistryWrapper.WrapperLookup registries, Consumer<AdvancementEntry> exporter) {
		AdvancementEntry advancementEntry = AdvancementTabGenerator.reference("adventure/root");
		VanillaAdventureTabAdvancementGenerator.createKillMobAdvancements(
			advancementEntry,
			exporter,
			registries.getOrThrow(RegistryKeys.ENTITY_TYPE),
			(List<EntityType<?>>)Stream.concat(VanillaAdventureTabAdvancementGenerator.MONSTERS.stream(), Stream.of(EntityType.CREAKING_TRANSIENT))
				.collect(Collectors.toList())
		);
		AdvancementEntry advancementEntry2 = AdvancementTabGenerator.reference("adventure/sleep_in_bed");
		VanillaAdventureTabAdvancementGenerator.buildAdventuringTime(
			registries, exporter, advancementEntry2, MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD_WINTER_DROP
		);
	}
}
