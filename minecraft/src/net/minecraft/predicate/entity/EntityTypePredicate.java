package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public record EntityTypePredicate(RegistryEntryList<EntityType<?>> types) {
	public static final Codec<EntityTypePredicate> CODEC = RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE)
		.xmap(EntityTypePredicate::new, EntityTypePredicate::types);

	public static EntityTypePredicate create(RegistryEntryLookup<EntityType<?>> entityTypeRegistry, EntityType<?> type) {
		return new EntityTypePredicate(RegistryEntryList.of(type.getRegistryEntry()));
	}

	public static EntityTypePredicate create(RegistryEntryLookup<EntityType<?>> entityTypeRegistry, TagKey<EntityType<?>> tag) {
		return new EntityTypePredicate(entityTypeRegistry.getOrThrow(tag));
	}

	public boolean matches(EntityType<?> type) {
		return type.isIn(this.types);
	}
}
