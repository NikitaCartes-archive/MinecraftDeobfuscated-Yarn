package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3855;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class FireballEntity extends class_3855 {
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
	protected void method_7469(HitResult hitResult) {
		if (!this.field_6002.isClient) {
			if (hitResult.getType() == HitResult.Type.ENTITY) {
				Entity entity = ((EntityHitResult)hitResult).getEntity();
				entity.damage(DamageSource.method_5521(this, this.owner), 6.0F);
				this.method_5723(this.owner, entity);
			}

			boolean bl = this.field_6002.getGameRules().getBoolean("mobGriefing");
			this.field_6002
				.createExplosion(null, this.x, this.y, this.z, (float)this.explosionPower, bl, bl ? Explosion.class_4179.field_18687 : Explosion.class_4179.field_18685);
			this.invalidate();
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("ExplosionPower", this.explosionPower);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("ExplosionPower", 99)) {
			this.explosionPower = compoundTag.getInt("ExplosionPower");
		}
	}
}
