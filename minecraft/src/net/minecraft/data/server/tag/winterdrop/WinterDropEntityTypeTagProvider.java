package net.minecraft.data.server.tag.winterdrop;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

public class WinterDropEntityTypeTagProvider extends ValueLookupTagProvider<EntityType<?>> {
	public WinterDropEntityTypeTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, RegistryKeys.ENTITY_TYPE, registriesFuture, entityType -> entityType.getRegistryEntry().registryKey());
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries) {
		this.getOrCreateTagBuilder(EntityTypeTags.BOAT).add(EntityType.PALE_OAK_BOAT);
	}
}
