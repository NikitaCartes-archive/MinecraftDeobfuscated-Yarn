package net.minecraft.data.server.tag.onetwentyone;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

public class OneTwentyOneEntityTypeTagProvider extends ValueLookupTagProvider<EntityType<?>> {
	public OneTwentyOneEntityTypeTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.ENTITY_TYPE, registryLookupFuture, entityType -> entityType.getRegistryEntry().registryKey());
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(EntityType.BREEZE);
		this.getOrCreateTagBuilder(EntityTypeTags.DEFLECTS_ARROWS).add(EntityType.BREEZE);
		this.getOrCreateTagBuilder(EntityTypeTags.DEFLECTS_TRIDENTS).add(EntityType.BREEZE);
	}
}
