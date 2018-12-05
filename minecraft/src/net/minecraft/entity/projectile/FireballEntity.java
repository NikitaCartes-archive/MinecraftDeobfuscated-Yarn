package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.HitResult;
import net.minecraft.world.World;

public class FireballEntity extends ExplosiveProjectileEntity {
	public int explosionPower = 1;

	public FireballEntity(World world) {
		super(EntityType.FIREBALL, world, 1.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public FireballEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.FIREBALL, d, e, f, g, h, i, world, 1.0F, 1.0F);
	}

	public FireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.FIREBALL, livingEntity, d, e, f, world, 1.0F, 1.0F);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (!this.world.isRemote) {
			if (hitResult.entity != null) {
				hitResult.entity.damage(DamageSource.explosiveProjectile(this, this.owner), 6.0F);
				this.method_5723(this.owner, hitResult.entity);
			}

			boolean bl = this.world.getGameRules().getBoolean("mobGriefing");
			this.world.method_8537(null, this.x, this.y, this.z, (float)this.explosionPower, bl, bl);
			this.invalidate();
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
