package net.minecraft.entity.projectile;

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

	public FireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
		super(EntityType.FIREBALL, owner, velocityX, velocityY, velocityZ, world);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.world.isClient) {
			boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
			this.world
				.createExplosion(
					null, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, bl, bl ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE
				);
			this.discard();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		if (!this.world.isClient) {
			Entity entity = entityHitResult.getEntity();
			Entity entity2 = this.getOwner();
			entity.damage(DamageSource.fireball(this, entity2), 6.0F);
			if (entity2 instanceof LivingEntity) {
				this.dealDamage((LivingEntity)entity2, entity);
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(CompoundTag tag) {
		super.writeCustomDataToNbt(tag);
		tag.putInt("ExplosionPower", this.explosionPower);
	}

	@Override
	public void readCustomDataFromNbt(CompoundTag tag) {
		super.readCustomDataFromNbt(tag);
		if (tag.contains("ExplosionPower", 99)) {
			this.explosionPower = tag.getInt("ExplosionPower");
		}
	}
}
