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
		super(EntityType.field_6066, d, e, f, g, h, i, world);
	}

	public FireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.field_6066, livingEntity, d, e, f, world);
	}

	@Override
	protected void method_7469(HitResult hitResult) {
		if (!this.field_6002.isClient) {
			if (hitResult.getType() == HitResult.Type.field_1331) {
				Entity entity = ((EntityHitResult)hitResult).getEntity();
				entity.damage(DamageSource.explosiveProjectile(this, this.owner), 6.0F);
				this.dealDamage(this.owner, entity);
			}

			boolean bl = this.field_6002.getGameRules().getBoolean(GameRules.field_19388);
			this.field_6002
				.createExplosion(
					null, this.x, this.y, this.z, (float)this.explosionPower, bl, bl ? Explosion.DestructionType.field_18687 : Explosion.DestructionType.field_18685
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
		if (compoundTag.containsKey("ExplosionPower", 99)) {
			this.explosionPower = compoundTag.getInt("ExplosionPower");
		}
	}
}
