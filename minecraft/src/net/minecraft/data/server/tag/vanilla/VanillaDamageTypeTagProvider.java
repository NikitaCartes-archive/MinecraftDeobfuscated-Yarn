package net.minecraft.data.server.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;

public class VanillaDamageTypeTagProvider extends TagProvider<DamageType> {
	public VanillaDamageTypeTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> maxChainedNeighborUpdates) {
		super(output, RegistryKeys.DAMAGE_TYPE, maxChainedNeighborUpdates);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(DamageTypeTags.DAMAGES_HELMET).add(DamageTypes.FALLING_ANVIL, DamageTypes.FALLING_BLOCK, DamageTypes.FALLING_STALACTITE);
		this.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ARMOR)
			.add(
				DamageTypes.ON_FIRE,
				DamageTypes.IN_WALL,
				DamageTypes.CRAMMING,
				DamageTypes.DROWN,
				DamageTypes.FLY_INTO_WALL,
				DamageTypes.GENERIC,
				DamageTypes.WITHER,
				DamageTypes.DRAGON_BREATH,
				DamageTypes.STARVE,
				DamageTypes.FALL,
				DamageTypes.FREEZE,
				DamageTypes.STALAGMITE,
				DamageTypes.MAGIC,
				DamageTypes.INDIRECT_MAGIC,
				DamageTypes.OUT_OF_WORLD,
				DamageTypes.GENERIC_KILL,
				DamageTypes.SONIC_BOOM,
				DamageTypes.OUTSIDE_BORDER
			);
		this.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_SHIELD)
			.addTag(DamageTypeTags.BYPASSES_ARMOR)
			.add(DamageTypes.FALLING_ANVIL, DamageTypes.FALLING_STALACTITE);
		this.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_INVULNERABILITY).add(DamageTypes.OUT_OF_WORLD, DamageTypes.GENERIC_KILL);
		this.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_EFFECTS).add(DamageTypes.STARVE);
		this.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_RESISTANCE).add(DamageTypes.OUT_OF_WORLD, DamageTypes.GENERIC_KILL);
		this.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(DamageTypes.SONIC_BOOM);
		this.getOrCreateTagBuilder(DamageTypeTags.IS_FIRE)
			.add(DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.LAVA, DamageTypes.HOT_FLOOR, DamageTypes.UNATTRIBUTED_FIREBALL, DamageTypes.FIREBALL);
		this.getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE)
			.add(
				DamageTypes.ARROW,
				DamageTypes.TRIDENT,
				DamageTypes.MOB_PROJECTILE,
				DamageTypes.UNATTRIBUTED_FIREBALL,
				DamageTypes.FIREBALL,
				DamageTypes.WITHER_SKULL,
				DamageTypes.THROWN
			);
		this.getOrCreateTagBuilder(DamageTypeTags.WITCH_RESISTANT_TO).add(DamageTypes.MAGIC, DamageTypes.INDIRECT_MAGIC, DamageTypes.SONIC_BOOM, DamageTypes.THORNS);
		this.getOrCreateTagBuilder(DamageTypeTags.IS_EXPLOSION)
			.add(DamageTypes.FIREWORKS, DamageTypes.EXPLOSION, DamageTypes.PLAYER_EXPLOSION, DamageTypes.BAD_RESPAWN_POINT);
		this.getOrCreateTagBuilder(DamageTypeTags.IS_FALL).add(DamageTypes.FALL, DamageTypes.STALAGMITE);
		this.getOrCreateTagBuilder(DamageTypeTags.IS_DROWNING).add(DamageTypes.DROWN);
		this.getOrCreateTagBuilder(DamageTypeTags.IS_FREEZING).add(DamageTypes.FREEZE);
		this.getOrCreateTagBuilder(DamageTypeTags.IS_LIGHTNING).add(DamageTypes.LIGHTNING_BOLT);
		this.getOrCreateTagBuilder(DamageTypeTags.NO_ANGER).add(DamageTypes.MOB_ATTACK_NO_AGGRO);
		this.getOrCreateTagBuilder(DamageTypeTags.NO_IMPACT).add(DamageTypes.DROWN);
		this.getOrCreateTagBuilder(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL).add(DamageTypes.OUT_OF_WORLD);
		this.getOrCreateTagBuilder(DamageTypeTags.WITHER_IMMUNE_TO).add(DamageTypes.DROWN);
		this.getOrCreateTagBuilder(DamageTypeTags.IGNITES_ARMOR_STANDS).add(DamageTypes.IN_FIRE);
		this.getOrCreateTagBuilder(DamageTypeTags.BURNS_ARMOR_STANDS).add(DamageTypes.ON_FIRE);
		this.getOrCreateTagBuilder(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).add(DamageTypes.MAGIC, DamageTypes.THORNS).addTag(DamageTypeTags.IS_EXPLOSION);
		this.getOrCreateTagBuilder(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH).add(DamageTypes.MAGIC);
		this.getOrCreateTagBuilder(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS).addTag(DamageTypeTags.IS_EXPLOSION);
	}
}
