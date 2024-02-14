package net.minecraft.data.server.advancement.onetwentyone;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import net.minecraft.data.server.advancement.vanilla.VanillaAdventureTabAdvancementGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;

public class OneTwentyOneAdventureTabAdvancementGenerator implements AdvancementTabGenerator {
	@Override
	public void accept(RegistryWrapper.WrapperLookup lookup, Consumer<AdvancementEntry> exporter) {
		AdvancementEntry advancementEntry = AdvancementTabGenerator.reference("adventure/root");
		VanillaAdventureTabAdvancementGenerator.createKillMobAdvancements(
			advancementEntry,
			exporter,
			(List<EntityType<?>>)Stream.concat(VanillaAdventureTabAdvancementGenerator.MONSTERS.stream(), Stream.of(EntityType.BREEZE, EntityType.BOGGED))
				.collect(Collectors.toList())
		);
	}
}
