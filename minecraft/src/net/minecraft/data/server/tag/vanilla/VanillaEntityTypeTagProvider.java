package net.minecraft.data.server.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

public class VanillaEntityTypeTagProvider extends ValueLookupTagProvider<EntityType<?>> {
	public VanillaEntityTypeTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.ENTITY_TYPE, registryLookupFuture, entityType -> entityType.getRegistryEntry().registryKey());
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(EntityTypeTags.SKELETONS)
			.add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.SKELETON_HORSE, EntityType.BOGGED);
		this.getOrCreateTagBuilder(EntityTypeTags.ZOMBIES)
			.add(
				EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.DROWNED, EntityType.HUSK
			);
		this.getOrCreateTagBuilder(EntityTypeTags.RAIDERS)
			.add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
		this.getOrCreateTagBuilder(EntityTypeTags.UNDEAD)
			.addTag(EntityTypeTags.SKELETONS)
			.addTag(EntityTypeTags.ZOMBIES)
			.add(EntityType.WITHER)
			.add(EntityType.PHANTOM);
		this.getOrCreateTagBuilder(EntityTypeTags.BEEHIVE_INHABITORS).add(EntityType.BEE);
		this.getOrCreateTagBuilder(EntityTypeTags.ARROWS).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
		this.getOrCreateTagBuilder(EntityTypeTags.IMPACT_PROJECTILES)
			.addTag(EntityTypeTags.ARROWS)
			.add(EntityType.FIREWORK_ROCKET)
			.add(
				EntityType.SNOWBALL,
				EntityType.FIREBALL,
				EntityType.SMALL_FIREBALL,
				EntityType.EGG,
				EntityType.TRIDENT,
				EntityType.DRAGON_FIREBALL,
				EntityType.WITHER_SKULL,
				EntityType.WIND_CHARGE,
				EntityType.BREEZE_WIND_CHARGE
			);
		this.getOrCreateTagBuilder(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add(EntityType.RABBIT, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.FOX);
		this.getOrCreateTagBuilder(EntityTypeTags.AXOLOTL_HUNT_TARGETS)
			.add(EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.COD, EntityType.SQUID, EntityType.GLOW_SQUID, EntityType.TADPOLE);
		this.getOrCreateTagBuilder(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES).add(EntityType.DROWNED, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);
		this.getOrCreateTagBuilder(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(EntityType.STRAY, EntityType.POLAR_BEAR, EntityType.SNOW_GOLEM, EntityType.WITHER);
		this.getOrCreateTagBuilder(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES).add(EntityType.STRIDER, EntityType.BLAZE, EntityType.MAGMA_CUBE);
		this.getOrCreateTagBuilder(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
			.addTag(EntityTypeTags.UNDEAD)
			.add(
				EntityType.AXOLOTL,
				EntityType.FROG,
				EntityType.GUARDIAN,
				EntityType.ELDER_GUARDIAN,
				EntityType.TURTLE,
				EntityType.GLOW_SQUID,
				EntityType.COD,
				EntityType.PUFFERFISH,
				EntityType.SALMON,
				EntityType.SQUID,
				EntityType.TROPICAL_FISH,
				EntityType.TADPOLE,
				EntityType.ARMOR_STAND
			);
		this.getOrCreateTagBuilder(EntityTypeTags.FROG_FOOD).add(EntityType.SLIME, EntityType.MAGMA_CUBE);
		this.getOrCreateTagBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE)
			.add(
				EntityType.IRON_GOLEM,
				EntityType.SNOW_GOLEM,
				EntityType.SHULKER,
				EntityType.ALLAY,
				EntityType.BAT,
				EntityType.BEE,
				EntityType.BLAZE,
				EntityType.CAT,
				EntityType.CHICKEN,
				EntityType.GHAST,
				EntityType.PHANTOM,
				EntityType.MAGMA_CUBE,
				EntityType.OCELOT,
				EntityType.PARROT,
				EntityType.WITHER,
				EntityType.BREEZE
			);
		this.getOrCreateTagBuilder(EntityTypeTags.DISMOUNTS_UNDERWATER)
			.add(
				EntityType.CAMEL,
				EntityType.CHICKEN,
				EntityType.DONKEY,
				EntityType.HORSE,
				EntityType.LLAMA,
				EntityType.MULE,
				EntityType.PIG,
				EntityType.RAVAGER,
				EntityType.SPIDER,
				EntityType.STRIDER,
				EntityType.TRADER_LLAMA,
				EntityType.ZOMBIE_HORSE
			);
		this.getOrCreateTagBuilder(EntityTypeTags.NON_CONTROLLING_RIDER).add(EntityType.SLIME, EntityType.MAGMA_CUBE);
		this.getOrCreateTagBuilder(EntityTypeTags.ILLAGER).add(EntityType.EVOKER).add(EntityType.ILLUSIONER).add(EntityType.PILLAGER).add(EntityType.VINDICATOR);
		this.getOrCreateTagBuilder(EntityTypeTags.AQUATIC)
			.add(EntityType.TURTLE)
			.add(EntityType.AXOLOTL)
			.add(EntityType.GUARDIAN)
			.add(EntityType.ELDER_GUARDIAN)
			.add(EntityType.COD)
			.add(EntityType.PUFFERFISH)
			.add(EntityType.SALMON)
			.add(EntityType.TROPICAL_FISH)
			.add(EntityType.DOLPHIN)
			.add(EntityType.SQUID)
			.add(EntityType.GLOW_SQUID)
			.add(EntityType.TADPOLE);
		this.getOrCreateTagBuilder(EntityTypeTags.ARTHROPOD)
			.add(EntityType.BEE)
			.add(EntityType.ENDERMITE)
			.add(EntityType.SILVERFISH)
			.add(EntityType.SPIDER)
			.add(EntityType.CAVE_SPIDER);
		this.getOrCreateTagBuilder(EntityTypeTags.IGNORES_POISON_AND_REGEN).addTag(EntityTypeTags.UNDEAD);
		this.getOrCreateTagBuilder(EntityTypeTags.INVERTED_HEALING_AND_HARM).addTag(EntityTypeTags.UNDEAD);
		this.getOrCreateTagBuilder(EntityTypeTags.WITHER_FRIENDS).addTag(EntityTypeTags.UNDEAD);
		this.getOrCreateTagBuilder(EntityTypeTags.ILLAGER_FRIENDS).addTag(EntityTypeTags.ILLAGER);
		this.getOrCreateTagBuilder(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH)
			.add(EntityType.TURTLE)
			.add(EntityType.GUARDIAN)
			.add(EntityType.ELDER_GUARDIAN)
			.add(EntityType.COD)
			.add(EntityType.PUFFERFISH)
			.add(EntityType.SALMON)
			.add(EntityType.TROPICAL_FISH)
			.add(EntityType.DOLPHIN)
			.add(EntityType.SQUID)
			.add(EntityType.GLOW_SQUID)
			.add(EntityType.TADPOLE);
		this.getOrCreateTagBuilder(EntityTypeTags.SENSITIVE_TO_IMPALING).addTag(EntityTypeTags.AQUATIC);
		this.getOrCreateTagBuilder(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS).addTag(EntityTypeTags.ARTHROPOD);
		this.getOrCreateTagBuilder(EntityTypeTags.SENSITIVE_TO_SMITE).addTag(EntityTypeTags.UNDEAD);
		this.getOrCreateTagBuilder(EntityTypeTags.REDIRECTABLE_PROJECTILE).add(EntityType.FIREBALL, EntityType.WIND_CHARGE, EntityType.BREEZE_WIND_CHARGE);
		this.getOrCreateTagBuilder(EntityTypeTags.DEFLECTS_PROJECTILES).add(EntityType.BREEZE);
		this.getOrCreateTagBuilder(EntityTypeTags.CAN_TURN_IN_BOATS).add(EntityType.BREEZE);
		this.getOrCreateTagBuilder(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE)
			.add(
				EntityType.BREEZE,
				EntityType.SKELETON,
				EntityType.BOGGED,
				EntityType.STRAY,
				EntityType.ZOMBIE,
				EntityType.HUSK,
				EntityType.SPIDER,
				EntityType.CAVE_SPIDER,
				EntityType.SLIME
			);
		this.getOrCreateTagBuilder(EntityTypeTags.IMMUNE_TO_INFESTED).add(EntityType.SILVERFISH);
		this.getOrCreateTagBuilder(EntityTypeTags.IMMUNE_TO_OOZING).add(EntityType.SLIME);
	}
}
