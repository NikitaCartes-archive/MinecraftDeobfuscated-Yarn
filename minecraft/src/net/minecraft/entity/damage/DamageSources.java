package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class DamageSources {
	private final Registry<DamageType> registry;
	private final DamageSource inFire;
	private final DamageSource lightningBolt;
	private final DamageSource onFire;
	private final DamageSource lava;
	private final DamageSource hotFloor;
	private final DamageSource inWall;
	private final DamageSource cramming;
	private final DamageSource drown;
	private final DamageSource starve;
	private final DamageSource cactus;
	private final DamageSource fall;
	private final DamageSource flyIntoWall;
	private final DamageSource outOfWorld;
	private final DamageSource generic;
	private final DamageSource magic;
	private final DamageSource wither;
	private final DamageSource dragonBreath;
	private final DamageSource dryOut;
	private final DamageSource sweetBerryBush;
	private final DamageSource freeze;
	private final DamageSource stalagmite;

	public DamageSources(DynamicRegistryManager registryManager) {
		this.registry = registryManager.get(RegistryKeys.DAMAGE_TYPE);
		this.inFire = this.create(DamageTypes.IN_FIRE);
		this.lightningBolt = this.create(DamageTypes.LIGHTNING_BOLT);
		this.onFire = this.create(DamageTypes.ON_FIRE);
		this.lava = this.create(DamageTypes.LAVA);
		this.hotFloor = this.create(DamageTypes.HOT_FLOOR);
		this.inWall = this.create(DamageTypes.IN_WALL);
		this.cramming = this.create(DamageTypes.CRAMMING);
		this.drown = this.create(DamageTypes.DROWN);
		this.starve = this.create(DamageTypes.STARVE);
		this.cactus = this.create(DamageTypes.CACTUS);
		this.fall = this.create(DamageTypes.FALL);
		this.flyIntoWall = this.create(DamageTypes.FLY_INTO_WALL);
		this.outOfWorld = this.create(DamageTypes.OUT_OF_WORLD);
		this.generic = this.create(DamageTypes.GENERIC);
		this.magic = this.create(DamageTypes.MAGIC);
		this.wither = this.create(DamageTypes.WITHER);
		this.dragonBreath = this.create(DamageTypes.DRAGON_BREATH);
		this.dryOut = this.create(DamageTypes.DRY_OUT);
		this.sweetBerryBush = this.create(DamageTypes.SWEET_BERRY_BUSH);
		this.freeze = this.create(DamageTypes.FREEZE);
		this.stalagmite = this.create(DamageTypes.STALAGMITE);
	}

	private DamageSource create(RegistryKey<DamageType> key) {
		return new DamageSource(this.registry.entryOf(key));
	}

	private DamageSource create(RegistryKey<DamageType> key, @Nullable Entity attacker) {
		return new DamageSource(this.registry.entryOf(key), attacker);
	}

	private DamageSource create(RegistryKey<DamageType> key, @Nullable Entity source, @Nullable Entity attacker) {
		return new DamageSource(this.registry.entryOf(key), source, attacker);
	}

	public DamageSource inFire() {
		return this.inFire;
	}

	public DamageSource lightningBolt() {
		return this.lightningBolt;
	}

	public DamageSource onFire() {
		return this.onFire;
	}

	public DamageSource lava() {
		return this.lava;
	}

	public DamageSource hotFloor() {
		return this.hotFloor;
	}

	public DamageSource inWall() {
		return this.inWall;
	}

	public DamageSource cramming() {
		return this.cramming;
	}

	public DamageSource drown() {
		return this.drown;
	}

	public DamageSource starve() {
		return this.starve;
	}

	public DamageSource cactus() {
		return this.cactus;
	}

	public DamageSource fall() {
		return this.fall;
	}

	public DamageSource flyIntoWall() {
		return this.flyIntoWall;
	}

	public DamageSource outOfWorld() {
		return this.outOfWorld;
	}

	public DamageSource generic() {
		return this.generic;
	}

	public DamageSource magic() {
		return this.magic;
	}

	public DamageSource wither() {
		return this.wither;
	}

	public DamageSource dragonBreath() {
		return this.dragonBreath;
	}

	public DamageSource dryOut() {
		return this.dryOut;
	}

	public DamageSource sweetBerryBush() {
		return this.sweetBerryBush;
	}

	public DamageSource freeze() {
		return this.freeze;
	}

	public DamageSource stalagmite() {
		return this.stalagmite;
	}

	public DamageSource fallingBlock(Entity attacker) {
		return this.create(DamageTypes.FALLING_BLOCK, attacker);
	}

	public DamageSource fallingAnvil(Entity attacker) {
		return this.create(DamageTypes.FALLING_ANVIL, attacker);
	}

	public DamageSource fallingStalactite(Entity attacker) {
		return this.create(DamageTypes.FALLING_STALACTITE, attacker);
	}

	public DamageSource sting(LivingEntity attacker) {
		return this.create(DamageTypes.STING, attacker);
	}

	public DamageSource mobAttack(LivingEntity attacker) {
		return this.create(DamageTypes.MOB_ATTACK, attacker);
	}

	public DamageSource mobAttackNoAggro(LivingEntity attacker) {
		return this.create(DamageTypes.MOB_ATTACK_NO_AGGRO, attacker);
	}

	public DamageSource playerAttack(PlayerEntity attacker) {
		return this.create(DamageTypes.PLAYER_ATTACK, attacker);
	}

	public DamageSource arrow(PersistentProjectileEntity source, @Nullable Entity attacker) {
		return this.create(DamageTypes.ARROW, source, attacker);
	}

	public DamageSource trident(Entity source, @Nullable Entity attacker) {
		return this.create(DamageTypes.TRIDENT, source, attacker);
	}

	public DamageSource mobProjectile(Entity source, @Nullable LivingEntity attacker) {
		return this.create(DamageTypes.MOB_PROJECTILE, source, attacker);
	}

	public DamageSource fireworks(FireworkRocketEntity source, @Nullable Entity attacker) {
		return this.create(DamageTypes.FIREWORKS, source, attacker);
	}

	public DamageSource fireball(AbstractFireballEntity source, @Nullable Entity attacker) {
		return attacker == null ? this.create(DamageTypes.UNATTRIBUTED_FIREBALL, source) : this.create(DamageTypes.FIREBALL, source, attacker);
	}

	public DamageSource witherSkull(WitherSkullEntity source, Entity attacker) {
		return this.create(DamageTypes.WITHER_SKULL, source, attacker);
	}

	public DamageSource thrown(Entity source, @Nullable Entity attacker) {
		return this.create(DamageTypes.THROWN, source, attacker);
	}

	public DamageSource indirectMagic(Entity source, @Nullable Entity attacker) {
		return this.create(DamageTypes.INDIRECT_MAGIC, source, attacker);
	}

	public DamageSource thorns(Entity attacker) {
		return this.create(DamageTypes.THORNS, attacker);
	}

	public DamageSource explosion(@Nullable Explosion explosion) {
		return explosion != null ? this.explosion(explosion.getEntity(), explosion.getCausingEntity()) : this.explosion(null, null);
	}

	public DamageSource explosion(@Nullable Entity source, @Nullable Entity attacker) {
		return this.create(attacker != null && source != null ? DamageTypes.PLAYER_EXPLOSION : DamageTypes.EXPLOSION, source, attacker);
	}

	public DamageSource sonicBoom(Entity attacker) {
		return this.create(DamageTypes.SONIC_BOOM, attacker);
	}

	public DamageSource badRespawnPoint(Vec3d position) {
		return new DamageSource(this.registry.entryOf(DamageTypes.BAD_RESPAWN_POINT), position);
	}
}
