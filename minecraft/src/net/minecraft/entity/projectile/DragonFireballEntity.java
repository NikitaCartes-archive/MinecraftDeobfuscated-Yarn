package net.minecraft.entity.projectile;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.EntityHitResult;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DragonFireballEntity extends ExplosiveProjectileEntity {
	public DragonFireballEntity(World world) {
		super(EntityType.DRAGON_FIREBALL, world);
	}

	@Environment(EnvType.CLIENT)
	public DragonFireballEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.DRAGON_FIREBALL, d, e, f, g, h, i, world);
	}

	public DragonFireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.DRAGON_FIREBALL, livingEntity, d, e, f, world);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (hitResult.getType() != HitResult.Type.ENTITY || !((EntityHitResult)hitResult).getEntity().isPartOf(this.owner)) {
			if (!this.world.isClient) {
				List<LivingEntity> list = this.world.getVisibleEntities(LivingEntity.class, this.getBoundingBox().expand(4.0, 2.0, 4.0));
				AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
				areaEffectCloudEntity.setOwner(this.owner);
				areaEffectCloudEntity.setParticleType(ParticleTypes.field_11216);
				areaEffectCloudEntity.setRadius(3.0F);
				areaEffectCloudEntity.setDuration(600);
				areaEffectCloudEntity.setRadiusGrowth((7.0F - areaEffectCloudEntity.getRadius()) / (float)areaEffectCloudEntity.getDuration());
				areaEffectCloudEntity.setPotionEffect(new StatusEffectInstance(StatusEffects.field_5921, 1, 1));
				if (!list.isEmpty()) {
					for (LivingEntity livingEntity : list) {
						double d = this.squaredDistanceTo(livingEntity);
						if (d < 16.0) {
							areaEffectCloudEntity.setPosition(livingEntity.x, livingEntity.y, livingEntity.z);
							break;
						}
					}
				}

				this.world.fireWorldEvent(2006, new BlockPos(this.x, this.y, this.z), 0);
				this.world.spawnEntity(areaEffectCloudEntity);
				this.invalidate();
			}
		}
	}

	@Override
	public boolean doesCollide() {
		return false;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return false;
	}

	@Override
	protected ParticleParameters getParticleType() {
		return ParticleTypes.field_11216;
	}

	@Override
	protected boolean method_7468() {
		return false;
	}
}
