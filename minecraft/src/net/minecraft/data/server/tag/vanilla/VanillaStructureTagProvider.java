package net.minecraft.data.server.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

public class VanillaStructureTagProvider extends TagProvider<Structure> {
	public VanillaStructureTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.STRUCTURE, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(StructureTags.VILLAGE)
			.add(StructureKeys.VILLAGE_PLAINS)
			.add(StructureKeys.VILLAGE_DESERT)
			.add(StructureKeys.VILLAGE_SAVANNA)
			.add(StructureKeys.VILLAGE_SNOWY)
			.add(StructureKeys.VILLAGE_TAIGA);
		this.getOrCreateTagBuilder(StructureTags.MINESHAFT).add(StructureKeys.MINESHAFT).add(StructureKeys.MINESHAFT_MESA);
		this.getOrCreateTagBuilder(StructureTags.OCEAN_RUIN).add(StructureKeys.OCEAN_RUIN_COLD).add(StructureKeys.OCEAN_RUIN_WARM);
		this.getOrCreateTagBuilder(StructureTags.SHIPWRECK).add(StructureKeys.SHIPWRECK).add(StructureKeys.SHIPWRECK_BEACHED);
		this.getOrCreateTagBuilder(StructureTags.RUINED_PORTAL)
			.add(StructureKeys.RUINED_PORTAL_DESERT)
			.add(StructureKeys.RUINED_PORTAL_JUNGLE)
			.add(StructureKeys.RUINED_PORTAL_MOUNTAIN)
			.add(StructureKeys.RUINED_PORTAL_NETHER)
			.add(StructureKeys.RUINED_PORTAL_OCEAN)
			.add(StructureKeys.RUINED_PORTAL)
			.add(StructureKeys.RUINED_PORTAL_SWAMP);
		this.getOrCreateTagBuilder(StructureTags.CATS_SPAWN_IN).add(StructureKeys.SWAMP_HUT);
		this.getOrCreateTagBuilder(StructureTags.CATS_SPAWN_AS_BLACK).add(StructureKeys.SWAMP_HUT);
		this.getOrCreateTagBuilder(StructureTags.EYE_OF_ENDER_LOCATED).add(StructureKeys.STRONGHOLD);
		this.getOrCreateTagBuilder(StructureTags.DOLPHIN_LOCATED).addTag(StructureTags.OCEAN_RUIN).addTag(StructureTags.SHIPWRECK);
		this.getOrCreateTagBuilder(StructureTags.ON_WOODLAND_EXPLORER_MAPS).add(StructureKeys.MANSION);
		this.getOrCreateTagBuilder(StructureTags.ON_OCEAN_EXPLORER_MAPS).add(StructureKeys.MONUMENT);
		this.getOrCreateTagBuilder(StructureTags.ON_TREASURE_MAPS).add(StructureKeys.BURIED_TREASURE);
	}
}
