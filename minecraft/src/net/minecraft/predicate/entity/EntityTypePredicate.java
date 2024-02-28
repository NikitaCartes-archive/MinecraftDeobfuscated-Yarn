package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public record EntityTypePredicate(RegistryEntryList<EntityType<?>> types) {
	public static final Codec<EntityTypePredicate> CODEC = RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE)
		.xmap(EntityTypePredicate::new, EntityTypePredicate::types);

	public static EntityTypePredicate create(EntityType<?> type) {
		return new EntityTypePredicate(RegistryEntryList.of(type.getRegistryEntry()));
	}

	public static EntityTypePredicate create(TagKey<EntityType<?>> tag) {
		return new EntityTypePredicate(Registries.ENTITY_TYPE.getOrCreateEntryList(tag));
	}

	public boolean matches(EntityType<?> type) {
		return type.isIn(this.types);
	}
}
