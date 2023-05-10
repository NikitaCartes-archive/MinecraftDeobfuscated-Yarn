package net.minecraft.entity.damage;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface DamageTypes {
	RegistryKey<DamageType> IN_FIRE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("in_fire"));
	RegistryKey<DamageType> LIGHTNING_BOLT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("lightning_bolt"));
	RegistryKey<DamageType> ON_FIRE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("on_fire"));
	RegistryKey<DamageType> LAVA = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("lava"));
	RegistryKey<DamageType> HOT_FLOOR = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("hot_floor"));
	RegistryKey<DamageType> IN_WALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("in_wall"));
	RegistryKey<DamageType> CRAMMING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("cramming"));
	RegistryKey<DamageType> DROWN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("drown"));
	RegistryKey<DamageType> STARVE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("starve"));
	RegistryKey<DamageType> CACTUS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("cactus"));
	RegistryKey<DamageType> FALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("fall"));
	RegistryKey<DamageType> FLY_INTO_WALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("fly_into_wall"));
	RegistryKey<DamageType> OUT_OF_WORLD = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("out_of_world"));
	RegistryKey<DamageType> GENERIC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("generic"));
	RegistryKey<DamageType> MAGIC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("magic"));
	RegistryKey<DamageType> WITHER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("wither"));
	RegistryKey<DamageType> DRAGON_BREATH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dragon_breath"));
	RegistryKey<DamageType> DRY_OUT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dry_out"));
	RegistryKey<DamageType> SWEET_BERRY_BUSH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("sweet_berry_bush"));
	RegistryKey<DamageType> FREEZE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("freeze"));
	RegistryKey<DamageType> STALAGMITE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("stalagmite"));
	RegistryKey<DamageType> FALLING_BLOCK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("falling_block"));
	RegistryKey<DamageType> FALLING_ANVIL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("falling_anvil"));
	RegistryKey<DamageType> FALLING_STALACTITE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("falling_stalactite"));
	RegistryKey<DamageType> STING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("sting"));
	RegistryKey<DamageType> MOB_ATTACK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("mob_attack"));
	RegistryKey<DamageType> MOB_ATTACK_NO_AGGRO = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("mob_attack_no_aggro"));
	RegistryKey<DamageType> PLAYER_ATTACK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("player_attack"));
	RegistryKey<DamageType> ARROW = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("arrow"));
	RegistryKey<DamageType> TRIDENT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("trident"));
	RegistryKey<DamageType> MOB_PROJECTILE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("mob_projectile"));
	RegistryKey<DamageType> FIREWORKS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("fireworks"));
	RegistryKey<DamageType> FIREBALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("fireball"));
	RegistryKey<DamageType> UNATTRIBUTED_FIREBALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("unattributed_fireball"));
	RegistryKey<DamageType> WITHER_SKULL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("wither_skull"));
	RegistryKey<DamageType> THROWN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("thrown"));
	RegistryKey<DamageType> INDIRECT_MAGIC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("indirect_magic"));
	RegistryKey<DamageType> THORNS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("thorns"));
	RegistryKey<DamageType> EXPLOSION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("explosion"));
	RegistryKey<DamageType> PLAYER_EXPLOSION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("player_explosion"));
	RegistryKey<DamageType> SONIC_BOOM = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("sonic_boom"));
	RegistryKey<DamageType> BAD_RESPAWN_POINT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("bad_respawn_point"));
	RegistryKey<DamageType> OUTSIDE_BORDER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("outside_border"));
	RegistryKey<DamageType> GENERIC_KILL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("generic_kill"));

	static void bootstrap(Registerable<DamageType> damageTypeRegisterable) {
		damageTypeRegisterable.register(IN_FIRE, new DamageType("inFire", 0.1F, DamageEffects.BURNING));
		damageTypeRegisterable.register(LIGHTNING_BOLT, new DamageType("lightningBolt", 0.1F));
		damageTypeRegisterable.register(ON_FIRE, new DamageType("onFire", 0.0F, DamageEffects.BURNING));
		damageTypeRegisterable.register(LAVA, new DamageType("lava", 0.1F, DamageEffects.BURNING));
		damageTypeRegisterable.register(HOT_FLOOR, new DamageType("hotFloor", 0.1F, DamageEffects.BURNING));
		damageTypeRegisterable.register(IN_WALL, new DamageType("inWall", 0.0F));
		damageTypeRegisterable.register(CRAMMING, new DamageType("cramming", 0.0F));
		damageTypeRegisterable.register(DROWN, new DamageType("drown", 0.0F, DamageEffects.DROWNING));
		damageTypeRegisterable.register(STARVE, new DamageType("starve", 0.0F));
		damageTypeRegisterable.register(CACTUS, new DamageType("cactus", 0.1F));
		damageTypeRegisterable.register(
			FALL, new DamageType("fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS)
		);
		damageTypeRegisterable.register(FLY_INTO_WALL, new DamageType("flyIntoWall", 0.0F));
		damageTypeRegisterable.register(OUT_OF_WORLD, new DamageType("outOfWorld", 0.0F));
		damageTypeRegisterable.register(GENERIC, new DamageType("generic", 0.0F));
		damageTypeRegisterable.register(MAGIC, new DamageType("magic", 0.0F));
		damageTypeRegisterable.register(WITHER, new DamageType("wither", 0.0F));
		damageTypeRegisterable.register(DRAGON_BREATH, new DamageType("dragonBreath", 0.0F));
		damageTypeRegisterable.register(DRY_OUT, new DamageType("dryout", 0.1F));
		damageTypeRegisterable.register(SWEET_BERRY_BUSH, new DamageType("sweetBerryBush", 0.1F, DamageEffects.POKING));
		damageTypeRegisterable.register(FREEZE, new DamageType("freeze", 0.0F, DamageEffects.FREEZING));
		damageTypeRegisterable.register(STALAGMITE, new DamageType("stalagmite", 0.0F));
		damageTypeRegisterable.register(FALLING_BLOCK, new DamageType("fallingBlock", 0.1F));
		damageTypeRegisterable.register(FALLING_ANVIL, new DamageType("anvil", 0.1F));
		damageTypeRegisterable.register(FALLING_STALACTITE, new DamageType("fallingStalactite", 0.1F));
		damageTypeRegisterable.register(STING, new DamageType("sting", 0.1F));
		damageTypeRegisterable.register(MOB_ATTACK, new DamageType("mob", 0.1F));
		damageTypeRegisterable.register(MOB_ATTACK_NO_AGGRO, new DamageType("mob", 0.1F));
		damageTypeRegisterable.register(PLAYER_ATTACK, new DamageType("player", 0.1F));
		damageTypeRegisterable.register(ARROW, new DamageType("arrow", 0.1F));
		damageTypeRegisterable.register(TRIDENT, new DamageType("trident", 0.1F));
		damageTypeRegisterable.register(MOB_PROJECTILE, new DamageType("mob", 0.1F));
		damageTypeRegisterable.register(FIREWORKS, new DamageType("fireworks", 0.1F));
		damageTypeRegisterable.register(UNATTRIBUTED_FIREBALL, new DamageType("onFire", 0.1F, DamageEffects.BURNING));
		damageTypeRegisterable.register(FIREBALL, new DamageType("fireball", 0.1F, DamageEffects.BURNING));
		damageTypeRegisterable.register(WITHER_SKULL, new DamageType("witherSkull", 0.1F));
		damageTypeRegisterable.register(THROWN, new DamageType("thrown", 0.1F));
		damageTypeRegisterable.register(INDIRECT_MAGIC, new DamageType("indirectMagic", 0.0F));
		damageTypeRegisterable.register(THORNS, new DamageType("thorns", 0.1F, DamageEffects.THORNS));
		damageTypeRegisterable.register(EXPLOSION, new DamageType("explosion", DamageScaling.ALWAYS, 0.1F));
		damageTypeRegisterable.register(PLAYER_EXPLOSION, new DamageType("explosion.player", DamageScaling.ALWAYS, 0.1F));
		damageTypeRegisterable.register(SONIC_BOOM, new DamageType("sonic_boom", DamageScaling.ALWAYS, 0.0F));
		damageTypeRegisterable.register(
			BAD_RESPAWN_POINT, new DamageType("badRespawnPoint", DamageScaling.ALWAYS, 0.1F, DamageEffects.HURT, DeathMessageType.INTENTIONAL_GAME_DESIGN)
		);
		damageTypeRegisterable.register(OUTSIDE_BORDER, new DamageType("outsideBorder", 0.0F));
		damageTypeRegisterable.register(GENERIC_KILL, new DamageType("genericKill", 0.0F));
	}
}
