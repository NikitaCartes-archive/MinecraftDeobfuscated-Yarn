package net.minecraft.data.server.tag.rebalance;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

public class TradeRebalanceStructureTagProvider extends TagProvider<Structure> {
	public TradeRebalanceStructureTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.STRUCTURE, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(StructureTags.ON_SAVANNA_VILLAGE_MAPS).add(StructureKeys.VILLAGE_SAVANNA);
		this.getOrCreateTagBuilder(StructureTags.ON_DESERT_VILLAGE_MAPS).add(StructureKeys.VILLAGE_DESERT);
		this.getOrCreateTagBuilder(StructureTags.ON_PLAINS_VILLAGE_MAPS).add(StructureKeys.VILLAGE_PLAINS);
		this.getOrCreateTagBuilder(StructureTags.ON_TAIGA_VILLAGE_MAPS).add(StructureKeys.VILLAGE_TAIGA);
		this.getOrCreateTagBuilder(StructureTags.ON_SNOWY_VILLAGE_MAPS).add(StructureKeys.VILLAGE_SNOWY);
		this.getOrCreateTagBuilder(StructureTags.ON_SWAMP_EXPLORER_MAPS).add(StructureKeys.SWAMP_HUT);
		this.getOrCreateTagBuilder(StructureTags.ON_JUNGLE_EXPLORER_MAPS).add(StructureKeys.JUNGLE_PYRAMID);
	}
}
