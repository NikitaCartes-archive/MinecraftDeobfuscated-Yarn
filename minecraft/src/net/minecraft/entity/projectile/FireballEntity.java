package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class FireballEntity extends AbstractFireballEntity {
	public int explosionPower = 1;

	public FireballEntity(EntityType<? extends FireballEntity> entityType, World world) {
		super(entityType, world);
	}

	@Environment(EnvType.CLIENT)
	public FireballEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.FIREBALL, d, e, f, g, h, i, world);
	}

	public FireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.FIREBALL, livingEntity, d, e, f, world);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (!this.world.isClient) {
			if (hitResult.getType() == HitResult.Type.ENTITY) {
				Entity entity = ((EntityHitResult)hitResult).getEntity();
				entity.damage(DamageSource.explosiveProjectile(this, this.owner), 6.0F);
				this.dealDamage(this.owner, entity);
			}

			boolean bl = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
			this.world
				.createExplosion(
					null, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, bl, bl ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE
				);
			this.remove();
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("ExplosionPower", this.explosionPower);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.contains("ExplosionPower", 99)) {
			this.explosionPower = compoundTag.getInt("ExplosionPower");
		}
	}
}
