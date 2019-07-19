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
	public FireballEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(EntityType.FIREBALL, x, y, z, velocityX, velocityY, velocityZ, world);
	}

	public FireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
		super(EntityType.FIREBALL, owner, velocityX, velocityY, velocityZ, world);
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
				.createExplosion(null, this.x, this.y, this.z, (float)this.explosionPower, bl, bl ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE);
			this.remove();
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("ExplosionPower", this.explosionPower);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("ExplosionPower", 99)) {
			this.explosionPower = tag.getInt("ExplosionPower");
		}
	}
}
