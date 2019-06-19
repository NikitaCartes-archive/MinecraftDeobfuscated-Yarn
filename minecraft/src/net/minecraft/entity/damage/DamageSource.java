package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class DamageSource {
	public static final DamageSource IN_FIRE = new DamageSource("inFire").setFire();
	public static final DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
	public static final DamageSource ON_FIRE = new DamageSource("onFire").setBypassesArmor().setFire();
	public static final DamageSource LAVA = new DamageSource("lava").setFire();
	public static final DamageSource HOT_FLOOR = new DamageSource("hotFloor").setFire();
	public static final DamageSource IN_WALL = new DamageSource("inWall").setBypassesArmor();
	public static final DamageSource CRAMMING = new DamageSource("cramming").setBypassesArmor();
	public static final DamageSource DROWN = new DamageSource("drown").setBypassesArmor();
	public static final DamageSource STARVE = new DamageSource("starve").setBypassesArmor().setUnblockable();
	public static final DamageSource CACTUS = new DamageSource("cactus");
	public static final DamageSource FALL = new DamageSource("fall").setBypassesArmor();
	public static final DamageSource FLY_INTO_WALL = new DamageSource("flyIntoWall").setBypassesArmor();
	public static final DamageSource OUT_OF_WORLD = new DamageSource("outOfWorld").setBypassesArmor().setDamageToCreative();
	public static final DamageSource GENERIC = new DamageSource("generic").setBypassesArmor();
	public static final DamageSource MAGIC = new DamageSource("magic").setBypassesArmor().setUsesMagic();
	public static final DamageSource WITHER = new DamageSource("wither").setBypassesArmor();
	public static final DamageSource ANVIL = new DamageSource("anvil");
	public static final DamageSource FALLING_BLOCK = new DamageSource("fallingBlock");
	public static final DamageSource DRAGON_BREATH = new DamageSource("dragonBreath").setBypassesArmor();
	public static final DamageSource FIREWORKS = new DamageSource("fireworks").setExplosive();
	public static final DamageSource DRYOUT = new DamageSource("dryout");
	public static final DamageSource SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
	private boolean bypassesArmor;
	private boolean damageToCreative;
	private boolean unblockable;
	private float exhaustion = 0.1F;
	private boolean fire;
	private boolean projectile;
	private boolean scaleWithDifficulty;
	private boolean magic;
	private boolean explosive;
	public final String name;

	public static DamageSource mob(LivingEntity livingEntity) {
		return new EntityDamageSource("mob", livingEntity);
	}

	public static DamageSource mobProjectile(Entity entity, LivingEntity livingEntity) {
		return new ProjectileDamageSource("mob", entity, livingEntity);
	}

	public static DamageSource player(PlayerEntity playerEntity) {
		return new EntityDamageSource("player", playerEntity);
	}

	public static DamageSource arrow(ProjectileEntity projectileEntity, @Nullable Entity entity) {
		return new ProjectileDamageSource("arrow", projectileEntity, entity).setProjectile();
	}

	public static DamageSource trident(Entity entity, @Nullable Entity entity2) {
		return new ProjectileDamageSource("trident", entity, entity2).setProjectile();
	}

	public static DamageSource explosiveProjectile(ExplosiveProjectileEntity explosiveProjectileEntity, @Nullable Entity entity) {
		return entity == null
			? new ProjectileDamageSource("onFire", explosiveProjectileEntity, explosiveProjectileEntity).setFire().setProjectile()
			: new ProjectileDamageSource("fireball", explosiveProjectileEntity, entity).setFire().setProjectile();
	}

	public static DamageSource thrownProjectile(Entity entity, @Nullable Entity entity2) {
		return new ProjectileDamageSource("thrown", entity, entity2).setProjectile();
	}

	public static DamageSource magic(Entity entity, @Nullable Entity entity2) {
		return new ProjectileDamageSource("indirectMagic", entity, entity2).setBypassesArmor().setUsesMagic();
	}

	public static DamageSource thorns(Entity entity) {
		return new EntityDamageSource("thorns", entity).method_5550().setUsesMagic();
	}

	public static DamageSource explosion(@Nullable Explosion explosion) {
		return explosion != null && explosion.getCausingEntity() != null
			? new EntityDamageSource("explosion.player", explosion.getCausingEntity()).setScaledWithDifficulty().setExplosive()
			: new DamageSource("explosion").setScaledWithDifficulty().setExplosive();
	}

	public static DamageSource explosion(@Nullable LivingEntity livingEntity) {
		return livingEntity != null
			? new EntityDamageSource("explosion.player", livingEntity).setScaledWithDifficulty().setExplosive()
			: new DamageSource("explosion").setScaledWithDifficulty().setExplosive();
	}

	public static DamageSource netherBed() {
		return new NetherBedDamageSource();
	}

	public boolean isProjectile() {
		return this.projectile;
	}

	public DamageSource setProjectile() {
		this.projectile = true;
		return this;
	}

	public boolean isExplosive() {
		return this.explosive;
	}

	public DamageSource setExplosive() {
		this.explosive = true;
		return this;
	}

	public boolean bypassesArmor() {
		return this.bypassesArmor;
	}

	public float getExhaustion() {
		return this.exhaustion;
	}

	public boolean doesDamageToCreative() {
		return this.damageToCreative;
	}

	public boolean isUnblockable() {
		return this.unblockable;
	}

	protected DamageSource(String string) {
		this.name = string;
	}

	@Nullable
	public Entity getSource() {
		return this.getAttacker();
	}

	@Nullable
	public Entity getAttacker() {
		return null;
	}

	protected DamageSource setBypassesArmor() {
		this.bypassesArmor = true;
		this.exhaustion = 0.0F;
		return this;
	}

	protected DamageSource setDamageToCreative() {
		this.damageToCreative = true;
		return this;
	}

	protected DamageSource setUnblockable() {
		this.unblockable = true;
		this.exhaustion = 0.0F;
		return this;
	}

	protected DamageSource setFire() {
		this.fire = true;
		return this;
	}

	public Text getDeathMessage(LivingEntity livingEntity) {
		LivingEntity livingEntity2 = livingEntity.method_6124();
		String string = "death.attack." + this.name;
		String string2 = string + ".player";
		return livingEntity2 != null
			? new TranslatableText(string2, livingEntity.getDisplayName(), livingEntity2.getDisplayName())
			: new TranslatableText(string, livingEntity.getDisplayName());
	}

	public boolean isFire() {
		return this.fire;
	}

	public String getName() {
		return this.name;
	}

	public DamageSource setScaledWithDifficulty() {
		this.scaleWithDifficulty = true;
		return this;
	}

	public boolean isScaledWithDifficulty() {
		return this.scaleWithDifficulty;
	}

	public boolean getMagic() {
		return this.magic;
	}

	public DamageSource setUsesMagic() {
		this.magic = true;
		return this;
	}

	public boolean isSourceCreativePlayer() {
		Entity entity = this.getAttacker();
		return entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.creativeMode;
	}

	@Nullable
	public Vec3d method_5510() {
		return null;
	}
}
