/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface DamageTypes {
    public static final RegistryKey<DamageType> IN_FIRE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("in_fire"));
    public static final RegistryKey<DamageType> LIGHTNING_BOLT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("lightning_bolt"));
    public static final RegistryKey<DamageType> ON_FIRE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("on_fire"));
    public static final RegistryKey<DamageType> LAVA = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("lava"));
    public static final RegistryKey<DamageType> HOT_FLOOR = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("hot_floor"));
    public static final RegistryKey<DamageType> IN_WALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("in_wall"));
    public static final RegistryKey<DamageType> CRAMMING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("cramming"));
    public static final RegistryKey<DamageType> DROWN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("drown"));
    public static final RegistryKey<DamageType> STARVE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("starve"));
    public static final RegistryKey<DamageType> CACTUS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("cactus"));
    public static final RegistryKey<DamageType> FALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("fall"));
    public static final RegistryKey<DamageType> FLY_INTO_WALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("fly_into_wall"));
    public static final RegistryKey<DamageType> OUT_OF_WORLD = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("out_of_world"));
    public static final RegistryKey<DamageType> GENERIC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("generic"));
    public static final RegistryKey<DamageType> MAGIC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("magic"));
    public static final RegistryKey<DamageType> WITHER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("wither"));
    public static final RegistryKey<DamageType> DRAGON_BREATH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dragon_breath"));
    public static final RegistryKey<DamageType> DRY_OUT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dry_out"));
    public static final RegistryKey<DamageType> SWEET_BERRY_BUSH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("sweet_berry_bush"));
    public static final RegistryKey<DamageType> FREEZE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("freeze"));
    public static final RegistryKey<DamageType> STALAGMITE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("stalagmite"));
    public static final RegistryKey<DamageType> FALLING_BLOCK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("falling_block"));
    public static final RegistryKey<DamageType> FALLING_ANIVL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("falling_anvil"));
    public static final RegistryKey<DamageType> FALLING_STALACTITE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("falling_stalactite"));
    public static final RegistryKey<DamageType> STING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("sting"));
    public static final RegistryKey<DamageType> MOB_ATTACK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("mob_attack"));
    public static final RegistryKey<DamageType> MOB_ATTACK_NO_AGGRO = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("mob_attack_no_aggro"));
    public static final RegistryKey<DamageType> PLAYER_ATTACK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("player_attack"));
    public static final RegistryKey<DamageType> ARROW = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("arrow"));
    public static final RegistryKey<DamageType> TRIDENT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("trident"));
    public static final RegistryKey<DamageType> MOB_PROJECTILE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("mob_projectile"));
    public static final RegistryKey<DamageType> FIREWORKS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("fireworks"));
    public static final RegistryKey<DamageType> FIREBALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("fireball"));
    public static final RegistryKey<DamageType> UNATTRIBUTED_FIREBALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("unattributed_fireball"));
    public static final RegistryKey<DamageType> WITHER_SKULL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("wither_skull"));
    public static final RegistryKey<DamageType> THROWN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("thrown"));
    public static final RegistryKey<DamageType> INDIRECT_MAGIC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("indirect_magic"));
    public static final RegistryKey<DamageType> THORNS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("thorns"));
    public static final RegistryKey<DamageType> EXPLOSION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("explosion"));
    public static final RegistryKey<DamageType> PLAYER_EXPLOSION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("player_explosion"));
    public static final RegistryKey<DamageType> SONIC_BOOM = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("sonic_boom"));
    public static final RegistryKey<DamageType> BAD_RESPAWN_POINT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("bad_respawn_point"));

    public static void bootstrap(Registerable<DamageType> damageTypeRegisterable) {
        damageTypeRegisterable.register(IN_FIRE, new DamageType("inFire", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(LIGHTNING_BOLT, new DamageType("lightningBolt", 0.1f));
        damageTypeRegisterable.register(ON_FIRE, new DamageType("onFire", 0.0f, DamageEffects.BURNING));
        damageTypeRegisterable.register(LAVA, new DamageType("lava", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(HOT_FLOOR, new DamageType("hotFloor", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(IN_WALL, new DamageType("inWall", 0.0f));
        damageTypeRegisterable.register(CRAMMING, new DamageType("cramming", 0.0f));
        damageTypeRegisterable.register(DROWN, new DamageType("drown", 0.0f, DamageEffects.DROWNING));
        damageTypeRegisterable.register(STARVE, new DamageType("starve", 0.0f));
        damageTypeRegisterable.register(CACTUS, new DamageType("cactus", 0.1f));
        damageTypeRegisterable.register(FALL, new DamageType("fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0f, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS));
        damageTypeRegisterable.register(FLY_INTO_WALL, new DamageType("flyIntoWall", 0.0f));
        damageTypeRegisterable.register(OUT_OF_WORLD, new DamageType("outOfWorld", 0.0f));
        damageTypeRegisterable.register(GENERIC, new DamageType("generic", 0.0f));
        damageTypeRegisterable.register(MAGIC, new DamageType("magic", 0.0f));
        damageTypeRegisterable.register(WITHER, new DamageType("wither", 0.0f));
        damageTypeRegisterable.register(DRAGON_BREATH, new DamageType("dragonBreath", 0.0f));
        damageTypeRegisterable.register(DRY_OUT, new DamageType("dryout", 0.1f));
        damageTypeRegisterable.register(SWEET_BERRY_BUSH, new DamageType("sweetBerryBush", 0.1f, DamageEffects.POKING));
        damageTypeRegisterable.register(FREEZE, new DamageType("freeze", 0.0f, DamageEffects.FREEZING));
        damageTypeRegisterable.register(STALAGMITE, new DamageType("stalagmite", 0.0f));
        damageTypeRegisterable.register(FALLING_BLOCK, new DamageType("fallingBlock", 0.1f));
        damageTypeRegisterable.register(FALLING_ANIVL, new DamageType("anvil", 0.1f));
        damageTypeRegisterable.register(FALLING_STALACTITE, new DamageType("fallingStalactite", 0.1f));
        damageTypeRegisterable.register(STING, new DamageType("sting", 0.1f));
        damageTypeRegisterable.register(MOB_ATTACK, new DamageType("mob", 0.1f));
        damageTypeRegisterable.register(MOB_ATTACK_NO_AGGRO, new DamageType("mob", 0.1f));
        damageTypeRegisterable.register(PLAYER_ATTACK, new DamageType("player", 0.1f));
        damageTypeRegisterable.register(ARROW, new DamageType("arrow", 0.1f));
        damageTypeRegisterable.register(TRIDENT, new DamageType("trident", 0.1f));
        damageTypeRegisterable.register(MOB_PROJECTILE, new DamageType("mob", 0.1f));
        damageTypeRegisterable.register(FIREWORKS, new DamageType("fireworks", 0.1f));
        damageTypeRegisterable.register(UNATTRIBUTED_FIREBALL, new DamageType("onFire", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(FIREBALL, new DamageType("fireball", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(WITHER_SKULL, new DamageType("witherSkull", 0.1f));
        damageTypeRegisterable.register(THROWN, new DamageType("thrown", 0.1f));
        damageTypeRegisterable.register(INDIRECT_MAGIC, new DamageType("indirectMagic", 0.0f));
        damageTypeRegisterable.register(THORNS, new DamageType("thorns", 0.1f, DamageEffects.THORNS));
        damageTypeRegisterable.register(EXPLOSION, new DamageType("explosion", DamageScaling.ALWAYS, 0.1f));
        damageTypeRegisterable.register(PLAYER_EXPLOSION, new DamageType("explosion.player", DamageScaling.ALWAYS, 0.1f));
        damageTypeRegisterable.register(SONIC_BOOM, new DamageType("sonic_boom", DamageScaling.ALWAYS, 0.0f));
        damageTypeRegisterable.register(BAD_RESPAWN_POINT, new DamageType("badRespawnPoint", DamageScaling.ALWAYS, 0.1f, DamageEffects.HURT, DeathMessageType.INTENTIONAL_GAME_DESIGN));
    }
}

