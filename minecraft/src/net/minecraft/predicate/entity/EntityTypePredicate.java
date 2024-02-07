package net.minecraft.predicate.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public record EntityTypePredicate(RegistryEntryList<EntityType<?>> types) {
	public static final Codec<EntityTypePredicate> CODEC = Codec.either(TagKey.codec(RegistryKeys.ENTITY_TYPE), Registries.ENTITY_TYPE.getEntryCodec())
		.flatComapMap(
			either -> either.map(
					tag -> new EntityTypePredicate(Registries.ENTITY_TYPE.getOrCreateEntryList(tag)), type -> new EntityTypePredicate(RegistryEntryList.of(type))
				),
			predicate -> {
				RegistryEntryList<EntityType<?>> registryEntryList = predicate.types();
				Optional<TagKey<EntityType<?>>> optional = registryEntryList.getTagKey();
				if (optional.isPresent()) {
					return DataResult.success(Either.left((TagKey)optional.get()));
				} else {
					return registryEntryList.size() == 1
						? DataResult.success(Either.right(registryEntryList.get(0)))
						: DataResult.error(() -> "Entity type set must have a single element, but got " + registryEntryList.size());
				}
			}
		);

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
