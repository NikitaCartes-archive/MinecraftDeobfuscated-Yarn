package net.minecraft.data.server.tag.onetwentyone;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

public class OneTwentyOneStructureTagProvider extends TagProvider<Structure> {
	public OneTwentyOneStructureTagProvider(
		DataOutput output,
		CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture,
		CompletableFuture<TagProvider.TagLookup<Structure>> structureTagLookupFuture
	) {
		super(output, RegistryKeys.STRUCTURE, registryLookupFuture, structureTagLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(StructureTags.ON_TRIAL_CHAMBERS_MAPS).add(StructureKeys.TRIAL_CHAMBERS);
	}
}
