package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

public class EntityTypeTagProvider extends ValueLookupTagProvider<EntityType<?>> {
	public EntityTypeTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.ENTITY_TYPE, registryLookupFuture, entityType -> entityType.getRegistryEntry().registryKey());
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(EntityTypeTags.SKELETONS).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
		this.getOrCreateTagBuilder(EntityTypeTags.RAIDERS)
			.add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
		this.getOrCreateTagBuilder(EntityTypeTags.BEEHIVE_INHABITORS).add(EntityType.BEE);
		this.getOrCreateTagBuilder(EntityTypeTags.ARROWS).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
		this.getOrCreateTagBuilder(EntityTypeTags.IMPACT_PROJECTILES)
			.addTag(EntityTypeTags.ARROWS)
			.add(
				EntityType.SNOWBALL,
				EntityType.FIREBALL,
				EntityType.SMALL_FIREBALL,
				EntityType.EGG,
				EntityType.TRIDENT,
				EntityType.DRAGON_FIREBALL,
				EntityType.WITHER_SKULL
			);
		this.getOrCreateTagBuilder(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add(EntityType.RABBIT, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.FOX);
		this.getOrCreateTagBuilder(EntityTypeTags.AXOLOTL_HUNT_TARGETS)
			.add(EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.COD, EntityType.SQUID, EntityType.GLOW_SQUID, EntityType.TADPOLE);
		this.getOrCreateTagBuilder(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES).add(EntityType.DROWNED, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);
		this.getOrCreateTagBuilder(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(EntityType.STRAY, EntityType.POLAR_BEAR, EntityType.SNOW_GOLEM, EntityType.WITHER);
		this.getOrCreateTagBuilder(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES).add(EntityType.STRIDER, EntityType.BLAZE, EntityType.MAGMA_CUBE);
		this.getOrCreateTagBuilder(EntityTypeTags.FROG_FOOD).add(EntityType.SLIME, EntityType.MAGMA_CUBE);
	}
}
